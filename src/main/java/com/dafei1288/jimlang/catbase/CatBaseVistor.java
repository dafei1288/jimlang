package com.dafei1288.jimlang.catbase;

import com.dafei1288.jimlang.parser.JimLangParser;
import com.dafei1288.jimlang.sys.Funcall;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Direct-interpreting CatBase front-end.
 *
 * <p>This is the "second front-end" approach: instead of rewriting CatBase
 * text into JimLang (see {@link CatBaseCompat}), this visitor walks the
 * parse tree produced by {@code CatBase.g4} and evaluates it directly.
 * It shares the JimLang runtime value model (Long/Double/String/Boolean/
 * List/Map/null) and reuses {@link Funcall} for built-in functions
 * (int/float/str/bool conversions, len, math, cat_print, json_* ...).</p>
 *
 * <p>Semantic notes:</p>
 * <ul>
 *   <li>type annotations are parsed but ignored at runtime</li>
 *   <li>functions live in one namespace per enclosing scope; bodies are
 *       lazily executed on call (no first-class closures)</li>
 *   <li>blocks share the enclosing function scope (CatBase-style),
 *       {@code for x in expr} binds the loop variable like Python</li>
 *   <li>{@code break/continue/return} are implemented with control-flow
 *       exceptions, exactly like {@code JimLangVistor}</li>
 *   <li>{@code f"..."} interpolation is parsed on the fly per segment</li>
 * </ul>
 */
public class CatBaseVistor extends CatBaseBaseVisitor<Object> {

    // ------------------------------------------------------------- runtime state

    private static class FuncDef {
        String name;
        List<String> params;
        List<Object> defaults;          // parallel to params; null entry = no default
        CatBaseParser.BlockContext body;
    }

    /** built-in that resolves through Funcall.exec() at call time */
    private static class Builtin {
        final String funcallName;
        Builtin(String funcallName) { this.funcallName = funcallName; }
    }

    private static final class ReturnSignal extends RuntimeException {
        final Object value;
        ReturnSignal(Object v) { super(null, null, false, false); this.value = v; }
    }
    private static final class BreakSignal extends RuntimeException {
        BreakSignal() { super(null, null, false, false); }
    }
    private static final class ContinueSignal extends RuntimeException {
        ContinueSignal() { super(null, null, false, false); }
    }

    private final Hashtable<String, Object> globals = new Hashtable<>();
    private final ArrayDeque<Hashtable<String, Object>> scopeStack = new ArrayDeque<>();
    private boolean mainInvoked = false;
    private String sourceName = "<cat>";

    public void setSourceName(String s) { this.sourceName = (s == null) ? "<cat>" : s; }
    public Hashtable<String, Object> getGlobals() { return globals; }

    private Hashtable<String, Object> current() { return scopeStack.isEmpty() ? globals : scopeStack.peek(); }

    private Object findSymbol(String name) {
        for (Hashtable<String, Object> m : scopeStack) {
            Object v = m.get(name);
            if (v != null || m.containsKey(name)) return v;
        }
        if (globals.containsKey(name)) return globals.get(name);
        return null;
    }

    private void pushScope() { scopeStack.push(new Hashtable<>()); }
    private void popScope() { scopeStack.pop(); }

    private RuntimeException error(String msg, org.antlr.v4.runtime.ParserRuleContext ctx) {
        return new RuntimeException(sourceName + ":" + (ctx == null ? "?" : ctx.getStart().getLine())
                + ": runtime error: " + msg);
    }

    // ------------------------------------------------------------------- entry

    @Override
    public Object visitProg(CatBaseParser.ProgContext ctx) {
        try {
            if (ctx.statementList() != null) visit(ctx.statementList());
            // CatBase entry point: run main([]) once after top-level code.
            if (globals.get("main") instanceof FuncDef && !mainInvoked) {
                FuncDef main = (FuncDef) globals.get("main");
                List<Object> mainArgs = new ArrayList<>();
                mainArgs.add(new ArrayList<>());      // args: list[str]
                invoke(main, mainArgs, new ArrayList<>(), ctx);
            }
            return null;
        } catch (ReturnSignal rs) {
            return rs.value;
        }
    }

    @Override
    public Object visitStatementList(CatBaseParser.StatementListContext ctx) {
        Object last = null;
        if (ctx.statement() != null) {
            for (CatBaseParser.StatementContext s : ctx.statement()) last = visit(s);
        }
        return last;
    }

    // -------------------------------------------------------------- declarations

    @Override
    public Object visitFunctionDef(CatBaseParser.FunctionDefContext ctx) {
        FuncDef f = new FuncDef();
        f.name = ctx.ID().getText();
        f.body = ctx.block();
        f.params = new ArrayList<>();
        f.defaults = new ArrayList<>();
        if (ctx.paramList() != null) {
            for (CatBaseParser.ParamContext p : ctx.paramList().param()) {
                f.params.add(p.ID().getText());
                f.defaults.add(p.expression() == null ? null : visit(p.expression()));
            }
        }
        current().put(f.name, f);
        return null;
    }

    @Override
    public Object visitVariableDecl(CatBaseParser.VariableDeclContext ctx) {
        String name = ctx.ID().getText();
        Hashtable<String, Object> cur = current();
        if (cur.containsKey(name)) {
            throw error("Variable '" + name + "' already declared in this scope", ctx);
        }
        Object value = ctx.expression() == null ? null : visit(ctx.expression());
        cur.put(name, value);
        return value;
    }

    // -------------------------------------------------------------- statements

    @Override
    public Object visitExpressionStatement(CatBaseParser.ExpressionStatementContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitReturnStatement(CatBaseParser.ReturnStatementContext ctx) {
        Object v = ctx.expression() == null ? null : visit(ctx.expression());
        throw new ReturnSignal(v);
    }

    @Override
    public Object visitBreakStatement(CatBaseParser.BreakStatementContext ctx) { throw new BreakSignal(); }

    @Override
    public Object visitContinueStatement(CatBaseParser.ContinueStatementContext ctx) { throw new ContinueSignal(); }

    @Override
    public Object visitIfStatement(CatBaseParser.IfStatementContext ctx) {
        if (truthy(visit(ctx.expression()))) {
            return visit(ctx.block(0));
        }
        List<CatBaseParser.BlockContext> blocks = ctx.block();
        if (blocks.size() >= 2) {
            return visit(blocks.get(1));
        }
        if (ctx.ifStatement() != null) {
            return visit(ctx.ifStatement());
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(CatBaseParser.WhileStatementContext ctx) {
        long guard = 0;
        while (truthy(visit(ctx.expression()))) {
            if (++guard > 100_000_000L) {
                throw error("While loop exceeded maximum iterations (possible infinite loop)", ctx);
            }
            try {
                visit(ctx.block());
            } catch (ContinueSignal c) {
                // continue
            } catch (BreakSignal b) {
                break;
            }
        }
        return null;
    }

    @Override
    public Object visitForStatement(CatBaseParser.ForStatementContext ctx) {
        String loopVar = ctx.ID().getText();
        Object it = visit(ctx.expression());
        if (!(it instanceof List)) {
            throw error("'for " + loopVar + " in ...' expects a list but got "
                    + (it == null ? "None" : it.getClass().getSimpleName()), ctx);
        }
        List<?> items = (List<?>) it;
        for (Object item : items) {
            assignVar(loopVar, item);
            try {
                visit(ctx.block());
            } catch (ContinueSignal c) {
                // continue
            } catch (BreakSignal b) {
                break;
            }
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(CatBaseParser.AssignmentStatementContext ctx) {
        return assign(ctx.lvalue(), visit(ctx.expression()));
    }

    @Override
    public Object visitBlock(CatBaseParser.BlockContext ctx) {
        if (ctx.statementList() == null) return null;
        return visit(ctx.statementList());
    }

    // ------------------------------------------------------------------ lvalue

    private void assignVar(String name, Object value) {
        for (Hashtable<String, Object> m : scopeStack) {
            if (m.containsKey(name)) { m.put(name, value); return; }
        }
        if (globals.containsKey(name)) { globals.put(name, value); return; }
        current().put(name, value);
    }

    private Object assign(CatBaseParser.LvalueContext lv, Object value) {
        String name = lv.ID().getText();
        List<CatBaseParser.AccessorContext> acc = lv.accessor();
        if (acc == null || acc.isEmpty()) {
            if (!scopeHas(name) && !(globals.get(name) instanceof FuncDef)) {
                throw error("Variable '" + name + "' is not defined", lv);
            }
            assignVar(name, value);
            return value;
        }
        Object container = findSymbol(name);
        if (container == null) throw error("Variable '" + name + "' is not defined", lv);
        for (int i = 0; i < acc.size(); i++) {
            boolean last = (i == acc.size() - 1);
            Object key = keyOf(acc.get(i));
            if (last) {
                setAt(container, key, value, lv);
            } else {
                container = getAt(container, key, lv);
            }
        }
        return value;
    }

    private boolean scopeHas(String name) {
        for (Hashtable<String, Object> m : scopeStack) if (m.containsKey(name)) return true;
        return globals.containsKey(name);
    }

    private Object keyOf(CatBaseParser.AccessorContext ac) {
        if (ac.expression() != null) return visit(ac.expression());
        return ac.ID().getText();
    }

    private Object getAt(Object container, Object key, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (container instanceof Map) {
            return ((Map<?, ?>) container).get(key);
        }
        if (container instanceof List) {
            List<?> list = (List<?>) container;
            int i = toIndex(key);
            if (i < 0 || i >= list.size()) throw error("Index out of bounds: " + i, ctx);
            return list.get(i);
        }
        throw error("Index access on non-container: " + (container == null ? "null" : container.getClass().getSimpleName()), ctx);
    }

    private void setAt(Object container, Object key, Object value, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (container instanceof Map) {
            ((Map<Object, Object>) container).put(key, value);
            return;
        }
        if (container instanceof List) {
            List<Object> list = (List<Object>) container;
            int i = toIndex(key);
            if (i < 0 || i >= list.size()) throw error("Index out of bounds: " + i, ctx);
            list.set(i, value);
            return;
        }
        throw error("Cannot assign into non-container: " + container.getClass().getSimpleName(), ctx);
    }

    private static int toIndex(Object key) {
        if (key instanceof Number) return ((Number) key).intValue();
        try { return Integer.parseInt(String.valueOf(key).trim()); }
        catch (Exception e) { throw new RuntimeException("Expect integer index but got: " + key); }
    }

    // ------------------------------------------------------------- expressions

    @Override
    public Object visitBooleanOr(CatBaseParser.BooleanOrContext ctx) {
        Object left = visit(ctx.booleanAnd(0));
        List<CatBaseParser.BooleanAndContext> parts = ctx.booleanAnd();
        for (int i = 1; i < parts.size(); i++) {
            if (truthy(left)) return left;            // short circuit
            left = visit(parts.get(i));
        }
        return left;
    }

    @Override
    public Object visitBooleanAnd(CatBaseParser.BooleanAndContext ctx) {
        Object left = visit(ctx.notExpr(0));
        List<CatBaseParser.NotExprContext> parts = ctx.notExpr();
        for (int i = 1; i < parts.size(); i++) {
            if (!truthy(left)) return left;           // short circuit
            left = visit(parts.get(i));
        }
        return left;
    }

    @Override
    public Object visitNotExpr(CatBaseParser.NotExprContext ctx) {
        if (ctx.notExpr() != null) {
            return !truthy(visit(ctx.notExpr()));
        }
        return visit(ctx.comparison());
    }

    @Override
    public Object visitComparison(CatBaseParser.ComparisonContext ctx) {
        Object left = visit(ctx.additive(0));
        int n = ctx.additive().size();
        if (n <= 1) return left;   // bare operand, no comparison operators
        for (int i = 1; i < n; i++) {
            // chain children are: additive op additive op ... so the operator
            // between parts i-1 and i sits at child index 2*i-1
            String op = ctx.getChild(2 * i - 1).getText();
            Object right = visit(ctx.additive(i));
            if (!compare(left, right, op, ctx)) return Boolean.FALSE;
            left = right;
        }
        return Boolean.TRUE;
    }

    private boolean compare(Object l, Object r, String op, org.antlr.v4.runtime.ParserRuleContext ctx) {
        int c;
        if (l instanceof Number && r instanceof Number) {
            c = Double.compare(((Number) l).doubleValue(), ((Number) r).doubleValue());
        } else if (l instanceof String || r instanceof String) {
            c = String.valueOf(l).compareTo(String.valueOf(r));
        } else if (l == null && r == null) {
            c = 0;
        } else if (l == null) {
            c = -1;
        } else if (r == null) {
            c = 1;
        } else {
            c = String.valueOf(l).compareTo(String.valueOf(r));
        }
        switch (op) {
            case "==": return c == 0;
            case "!=": return c != 0;
            case "<":  return c < 0;
            case "<=": return c <= 0;
            case ">":  return c > 0;
            case ">=": return c >= 0;
            default: throw error("Unknown comparison operator: " + op, ctx);
        }
    }

    @Override
    public Object visitAdditive(CatBaseParser.AdditiveContext ctx) {
        Object left = visit(ctx.multiplicative(0));
        List<CatBaseParser.MultiplicativeContext> parts = ctx.multiplicative();
        for (int i = 1; i < parts.size(); i++) {
            String op = opBetween(ctx, i);
            left = arith(left, visit(parts.get(i)), op, ctx);
        }
        return left;
    }

    @Override
    public Object visitMultiplicative(CatBaseParser.MultiplicativeContext ctx) {
        Object left = visit(ctx.unary(0));
        List<CatBaseParser.UnaryContext> parts = ctx.unary();
        for (int i = 1; i < parts.size(); i++) {
            String op = opBetween(ctx, i);
            left = arith(left, visit(parts.get(i)), op, ctx);
        }
        return left;
    }

    /** operator token sitting between child(i-1) and child(i) of a chain rule */
    private static String opBetween(org.antlr.v4.runtime.ParserRuleContext ctx, int i) {
        return ctx.getChild(2 * i - 1).getText();
    }

    private Object arith(Object l, Object r, String op, org.antlr.v4.runtime.ParserRuleContext ctx) {
        switch (op) {
            case "+": return plus(l, r, ctx);
            case "-": return num(l, r, ctx, false);
            case "*": return num(l, r, ctx, true);
            case "/": return divide(l, r, ctx);
            case "%": {
                double a = toNumber(l, ctx).doubleValue(), b = toNumber(r, ctx).doubleValue();
                if (b == 0) throw error("Division by zero", ctx);
                return integralPair(l, r) ? a % b : Double.valueOf(a % b);
            }
            default: throw error("Unknown operator: " + op, ctx);
        }
    }

    private Object plus(Object l, Object r, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (l instanceof String || r instanceof String) {
            return String.valueOf(l) + String.valueOf(r);
        }
        if (l instanceof Number && r instanceof Number) {
            return addNum((Number) l, (Number) r);
        }
        throw error("Cannot add " + typeName(l) + " and " + typeName(r), ctx);
    }

    private static Number addNum(Number a, Number b) {
        if (a instanceof Long && b instanceof Long) return a.longValue() + b.longValue();
        return a.doubleValue() + b.doubleValue();
    }

    /** * or - for numbers (or string repetition for * ? not in CatBase tier-1) */
    private Object num(Object l, Object r, org.antlr.v4.runtime.ParserRuleContext ctx, boolean mul) {
        if (!(l instanceof Number) || !(r instanceof Number)) {
            throw error("Operator needs numbers but got " + typeName(l) + " and " + typeName(r), ctx);
        }
        double a = ((Number) l).doubleValue(), b = ((Number) r).doubleValue();
        boolean bothLong = l instanceof Long && r instanceof Long;
        double res = mul ? a * b : a - b;
        return bothLong ? (Number) (long) res : (Number) res;
    }

    private Object divide(Object l, Object r, org.antlr.v4.runtime.ParserRuleContext ctx) {
        double b = toNumber(r, ctx).doubleValue();
        if (b == 0) throw error("Division by zero", ctx);
        if (integralPair(l, r)) {
            long a = toNumber(l, ctx).longValue(), bb = (long) b;
            return a / bb;                       // CatBase int/int is integer division
        }
        return toNumber(l, ctx).doubleValue() / b;
    }

    private static boolean integralPair(Object l, Object r) {
        return (l instanceof Long || l instanceof Integer) && (r instanceof Long || r instanceof Integer);
    }

    private static Number toNumber(Object o, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (o instanceof Number) return (Number) o;
        if (o instanceof Boolean) return ((Boolean) o) ? 1L : 0L;
        if (o instanceof String) {
            try { return Long.parseLong(((String) o).trim()); }
            catch (Exception e) { return Double.parseDouble(((String) o).trim()); }
        }
        throw new RuntimeException("Expected a number but got: " + (o == null ? "None" : o));
    }

    @Override
    public Object visitUnary(CatBaseParser.UnaryContext ctx) {
        if (ctx.primary() != null) return visit(ctx.primary());
        Object v = visit(ctx.unary());
        if (ctx.SUB() != null) {
            if (v instanceof Number) {
                if (v instanceof Long || v instanceof Integer) {
                    return Long.valueOf(-((Number) v).longValue());
                }
                return Double.valueOf(-((Number) v).doubleValue());
            }
            throw error("Unary '-' needs a number", ctx);
        }
        return v;
    }

    @Override
    public Object visitPrimary(CatBaseParser.PrimaryContext ctx) {
        Object value = visit(ctx.atom());
        int n = ctx.getChildCount();
        int i = 1;
        while (i < n) {
            org.antlr.v4.runtime.tree.ParseTree ch = ctx.getChild(i);
            if (ch instanceof CatBaseParser.AccessorContext) {
                CatBaseParser.AccessorContext ac = (CatBaseParser.AccessorContext) ch;
                // a .name(...) pair is a method call, not a property read
                boolean methodCall = ac.expression() == null && i + 1 < n
                        && ctx.getChild(i + 1) instanceof CatBaseParser.CallSuffixContext;
                if (ac.expression() != null) {
                    value = getAt(value, visit(ac.expression()), ac);
                } else if (methodCall) {
                    CatBaseParser.CallSuffixContext cs = (CatBaseParser.CallSuffixContext) ctx.getChild(i + 1);
                    List<Object> args = new ArrayList<>();
                    if (cs.argList() != null) {
                        for (CatBaseParser.ArgContext a : cs.argList().arg()) {
                            if (a.namedArg() != null) {
                                throw error("Named arguments are not supported in method calls", a);
                            }
                            args.add(visit(a.expression()));
                        }
                    }
                    value = methodCall(value, ac.ID().getText(), args, ac);
                    i += 2;
                    continue;
                } else {
                    String key = ac.ID().getText();
                    if (value instanceof Map) {
                        value = ((Map<?, ?>) value).get(key);
                    } else {
                        throw error("Property access on non-object", ac);
                    }
                }
            } else if (ch instanceof CatBaseParser.CallSuffixContext) {
                CatBaseParser.CallSuffixContext cs = (CatBaseParser.CallSuffixContext) ch;
                List<Object> pos = new ArrayList<>();
                List<String> named = new ArrayList<>();
                if (cs.argList() != null) {
                    for (CatBaseParser.ArgContext a : cs.argList().arg()) {
                        if (a.namedArg() != null) {
                            named.add(a.namedArg().ID().getText());
                            pos.add(visit(a.namedArg().expression()));
                        } else {
                            named.add(null);
                            pos.add(visit(a.expression()));
                        }
                    }
                }
                value = call(value, pos, named, cs);
            }
            i++;
        }
        return value;
    }

    /** value.method(args...) routed through the Funcall builtin library */
    private Object methodCall(Object base, String method, List<Object> args,
                              org.antlr.v4.runtime.ParserRuleContext ctx) {
        String fn = METHOD_ALIAS.get(method);
        if (fn == null && Funcall.isSysFunction(method)) fn = method;
        if (fn == null) {
            // list/dict helpers not covered by Funcall yet
            throw error("Method '" + method + "' is not supported on " + typeName(base), ctx);
        }
        List<Object> all = new ArrayList<>();
        all.add(base);
        all.addAll(args);
        try {
            return Funcall.exec(fn, all);
        } catch (RuntimeException e) {
            throw error("Method '" + method + "' failed on " + typeName(base) + ": " + e.getMessage(), ctx);
        }
    }

    @Override
    public Object visitAtom(CatBaseParser.AtomContext ctx) {
        if (ctx.ID() != null) {
            String name = ctx.ID().getText();
            Object sym = findSymbol(name);
            if (sym == null) {
                // user function?
                Object f = currentHas(name);
                if (f != null) return f;
                // built-ins (range is handled natively, everything else via Funcall)
                if ("range".equals(name)) return new Builtin("range");
                String mapped = builtinMapping(name);
                                if (mapped != null || Funcall.isSysFunction(name)) {
                    return new Builtin(mapped != null ? mapped : name);
                }
                throw error("Variable or function '" + name + "' is not defined", ctx);
            }
            return sym;
        }
        if (ctx.NUMBER() != null) return parseNumber(ctx.NUMBER().getText());
        if (ctx.STRING() != null) return unescapeString(ctx.STRING().getText());
        if (ctx.FSTRING() != null) return evalFString(ctx.FSTRING().getText());
        if (ctx.TRUE() != null) return Boolean.TRUE;
        if (ctx.FALSE() != null) return Boolean.FALSE;
        if (ctx.NONE() != null) return null;
        if (ctx.arrayLiteral() != null) {
            List<Object> arr = new ArrayList<>();
            if (ctx.arrayLiteral().expression() != null) {
                for (CatBaseParser.ExpressionContext e : ctx.arrayLiteral().expression()) arr.add(visit(e));
            }
            return arr;
        }
        if (ctx.dictLiteral() != null) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            if (ctx.dictLiteral().dictEntry() != null) {
                for (CatBaseParser.DictEntryContext e : ctx.dictLiteral().dictEntry()) {
                    String key = e.ID() != null ? e.ID().getText()
                            : unescapeString(e.STRING().getText());
                    map.put(key, visit(e.expression()));
                }
            }
            return map;
        }
        if (ctx.expression() != null) return visit(ctx.expression());
        return null;
    }

    private Object currentHas(String name) {
        // user functions are stored in the scope where they were declared
        for (Hashtable<String, Object> m : scopeStack) {
            Object v = m.get(name);
            if (v instanceof FuncDef) return v;
        }
        return globals.get(name);
    }

    // ------------------------------------------------------------------- calls

    private Object call(Object target, List<Object> pos, List<String> named, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (target instanceof FuncDef) {
            return invoke((FuncDef) target, pos, named, ctx);
        }
        if (target instanceof Builtin) {
            Builtin b = (Builtin) target;
            if ("range".equals(b.funcallName)) return computeRange(pos, ctx);
            return Funcall.exec(b.funcallName, new ArrayList<>(pos));
        }
        if (target == null) throw error("Call on null", ctx);
        throw error("Cannot call value of type " + typeName(target), ctx);
    }

    private static final Map<String, String> METHOD_ALIAS = new LinkedHashMap<>();
    static {
        METHOD_ALIAS.put("strip", "trim");
        METHOD_ALIAS.put("upper", "toUpperCase");
        METHOD_ALIAS.put("lower", "toLowerCase");
        METHOD_ALIAS.put("startsWith", "startsWith");
        METHOD_ALIAS.put("endsWith", "endsWith");
        METHOD_ALIAS.put("indexOf", "indexOf");
        METHOD_ALIAS.put("contains", "contains");
        METHOD_ALIAS.put("replace", "replace");
        METHOD_ALIAS.put("split", "split");
    }

    /** range(stop) / range(start, stop) / range(start, stop, step) -> List */
    private static List<Object> computeRange(List<Object> pos, org.antlr.v4.runtime.ParserRuleContext ctx) {
        double start = 0, stop = 0, step = 1;
        try {
            if (pos.size() == 1) {
                stop = toNumber(pos.get(0), ctx).doubleValue();
            } else if (pos.size() == 2) {
                start = toNumber(pos.get(0), ctx).doubleValue();
                stop = toNumber(pos.get(1), ctx).doubleValue();
            } else if (pos.size() == 3) {
                start = toNumber(pos.get(0), ctx).doubleValue();
                stop = toNumber(pos.get(1), ctx).doubleValue();
                step = toNumber(pos.get(2), ctx).doubleValue();
            } else {
                throw new RuntimeException("range() expects 1..3 arguments");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("range(): " + e.getMessage());
        }
        if (step == 0) throw new RuntimeException("range() step must not be zero");
        List<Object> out = new ArrayList<>();
        boolean integral = start == Math.floor(start) && stop == Math.floor(stop) && step == Math.floor(step);
        double v = start;
        long guard = 0;
        while ((step > 0 && v < stop) || (step < 0 && v > stop)) {
            if (++guard > 10_000_000L) throw new RuntimeException("range() too large");
            out.add(integral ? (Object) (long) v : (Object) v);
            v += step;
        }
        return out;
    }


    /** CatBase builtin names that map onto Funcall methods. Returns null if the
     *  name should be tried directly against Funcall. */
    private static String builtinMapping(String name) {
        switch (name) {
            case "json_dumps": return "json_encode";
            case "json_loads": return "json_decode";
            case "print":      return "cat_print";
            case "println":    return "cat_print";
            default:           return null;
        }
    }

    private Object invoke(FuncDef f, List<Object> pos, List<String> named, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (f.name.equals("main")) mainInvoked = true;
        pushScope();
        try {
            bindParams(f, pos, named, ctx);
            try {
                if (f.body.statementList() != null) visit(f.body.statementList());
                return null;
            } catch (ReturnSignal rs) {
                return rs.value;
            }
        } finally {
            popScope();
        }
    }

    private void bindParams(FuncDef f, List<Object> pos, List<String> named, org.antlr.v4.runtime.ParserRuleContext ctx) {
        int positional = 0;
        boolean[] consumed = new boolean[pos.size()];
        for (int i = 0; i < f.params.size(); i++) {
            Object value = null;
            boolean filled = false;
            for (int k = 0; k < named.size(); k++) {
                if (!consumed[k] && f.params.get(i).equals(named.get(k))) {
                    value = pos.get(k);
                    consumed[k] = true;
                    filled = true;
                    break;
                }
            }
            if (!filled && positional < pos.size()) {
                value = pos.get(positional);
                consumed[positional] = true;
                positional++;
                filled = true;
            }
            if (!filled) {
                Object d = f.defaults.get(i);
                if (d != null) { value = d; filled = true; }
            }
            if (!filled) {
                throw error("Function " + f.name + " missing argument '" + f.params.get(i) + "'", ctx);
            }
            current().put(f.params.get(i), value);
        }
        for (int k = 0; k < consumed.length; k++) {
            if (!consumed[k]) {
                throw error("Function " + f.name + " got too many arguments", ctx);
            }
        }
    }

    // ---------------------------------------------------------------- literals

    private static Object parseNumber(String text) {
        try {
            if (text.indexOf('.') >= 0) return Double.parseDouble(text);
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return text;   // fall back to raw (should not happen)
        }
    }

    private static String unescapeString(String raw) {
        if (raw.length() < 2) return raw;
        String body = raw.substring(1, raw.length() - 1);
        StringBuilder sb = new StringBuilder(body.length());
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '\\' && i + 1 < body.length()) {
                char e = body.charAt(++i);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    case '0': sb.append('\0'); break;
                    default: sb.append(e); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------ f-strings

    /** f"a={x}, b={y}" -> concatenation; interpolations parsed on the fly. */
    private String evalFString(String raw) {
        // raw starts with f' or f"
        char q = raw.charAt(1);
        String body = raw.substring(2, raw.length() - 1);
        StringBuilder out = new StringBuilder();
        StringBuilder literal = new StringBuilder();
        int i = 0;
        while (i < body.length()) {
            char c = body.charAt(i);
            if (c == '\\' && i + 1 < body.length()) {
                // decode escapes exactly like regular strings
                char e = body.charAt(++i);
                literal.append(decodeEscape(e));
                i++;
                continue;
            }
            if (c == '{') {
                int depth = 1;
                int j = i + 1;
                while (j < body.length() && depth > 0) {
                    char d = body.charAt(j);
                    if (d == '{') depth++;
                    else if (d == '}') depth--;
                    if (depth > 0) j++;
                }
                if (depth != 0) { literal.append(c); i++; continue; }
                out.append(literal);
                literal.setLength(0);
                String exprText = body.substring(i + 1, j).trim();
                out.append(stringify(evalInterp(exprText)));
                i = j + 1;
                continue;
            }
            literal.append(c);
            i++;
        }
        out.append(literal);
        return out.toString();
    }

    private static char decodeEscape(char e) {
        switch (e) {
            case 'n': return '\n';
            case 't': return '\t';
            case 'r': return '\r';
            case 'b': return '\b';
            case 'f': return '\f';
            case '0': return '\0';
            default: return e;
        }
    }

    private Object evalInterp(String exprText) {
        org.antlr.v4.runtime.CharStream cs = org.antlr.v4.runtime.CharStreams.fromString(exprText);
        CatBaseLexer lx = new CatBaseLexer(cs);
        lx.removeErrorListeners();
        org.antlr.v4.runtime.CommonTokenStream ts = new org.antlr.v4.runtime.CommonTokenStream(lx);
        CatBaseParser p = new CatBaseParser(ts);
        p.removeErrorListeners();
        p.setErrorHandler(new org.antlr.v4.runtime.BailErrorStrategy());
        CatBaseParser.ExpressionContext e;
        try {
            e = p.expression();
        } catch (RuntimeException ex) {
            throw new RuntimeException("Bad interpolation expression: '" + exprText + "' -> " + ex.getMessage());
        }
        if (p.getCurrentToken().getType() != CatBaseLexer.EOF) {
            throw new RuntimeException("Bad interpolation expression: '" + exprText + "'");
        }
        return visit(e);
    }

    private static String stringify(Object v) {
        if (v == null) return "None";
        return String.valueOf(v);
    }

    // ------------------------------------------------------------------ utils

    private static boolean truthy(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Number) return ((Number) v).doubleValue() != 0;
        if (v instanceof String) return !((String) v).isEmpty();
        return true;
    }

    private static String typeName(Object o) {
        if (o == null) return "None";
        if (o instanceof Long || o instanceof Integer) return "int";
        if (o instanceof Double || o instanceof Float) return "float";
        if (o instanceof String) return "str";
        if (o instanceof Boolean) return "bool";
        if (o instanceof List) return "list";
        if (o instanceof Map) return "dict";
        return o.getClass().getSimpleName();
    }
}
