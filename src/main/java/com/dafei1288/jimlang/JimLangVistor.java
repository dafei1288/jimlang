package com.dafei1288.jimlang;

import com.dafei1288.jimlang.metadata.Scope;
import com.dafei1288.jimlang.metadata.Scope.RootScope;
import com.dafei1288.jimlang.metadata.StackFrane;
import com.dafei1288.jimlang.metadata.Symbol;
import com.dafei1288.jimlang.metadata.SymbolFunction;
import com.dafei1288.jimlang.metadata.SymbolType;
import com.dafei1288.jimlang.metadata.SymbolVar;

import com.dafei1288.jimlang.parser.JimLangBaseVisitor;
import com.dafei1288.jimlang.parser.JimLangParser;
import com.dafei1288.jimlang.parser.JimLangParser.AssignmentContext;
import com.dafei1288.jimlang.parser.JimLangParser.FunctionBodyContext;
import com.dafei1288.jimlang.parser.JimLangParser.FunctionCallContext;
import com.dafei1288.jimlang.parser.JimLangParser.FunctionDeclContext;
import com.dafei1288.jimlang.parser.JimLangParser.PrimaryContext;
import com.dafei1288.jimlang.parser.JimLangParser.ProgContext;
import com.dafei1288.jimlang.parser.JimLangParser.ReturnStatementContext;
import com.dafei1288.jimlang.parser.JimLangParser.SingleExpressionContext;
import com.dafei1288.jimlang.parser.JimLangParser.SysfunctionContext;
import com.dafei1288.jimlang.parser.JimLangParser.VariableDeclContext;
import com.dafei1288.jimlang.sys.Funcall;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Hashtable;

public class JimLangVistor extends JimLangBaseVisitor {    private String sourceName = "<script>";
    private String[] sourceLines = new String[0];
    public void setSourceName(String s){ this.sourceName = (s==null?"<script>":s); }
    public void setSourceText(String txt){ this.sourceLines = (txt==null)? new String[0] : txt.split("\r?\n", -1); }
    private RuntimeException error(String msg, org.antlr.v4.runtime.ParserRuleContext ctx){
        int line=1,col=1;
        if (ctx!=null && ctx.getStart()!=null){ line = ctx.getStart().getLine(); col = ctx.getStart().getCharPositionInLine()+1; }
        StringBuilder sb = new StringBuilder();
        sb.append(sourceName).append(":").append(line).append(":").append(col).append(": runtime error: ").append(msg);
        if (line >= 1 && line <= sourceLines.length) {
            String snippet = sourceLines[line-1];
            sb.append(System.lineSeparator()).append(snippet);
            int caretPos = Math.max(0, Math.min(col-1, 200));
            StringBuilder caret = new StringBuilder();
            for (int i=0;i<caretPos;i++) caret.append(' ');
            caret.append('^');
            sb.append(System.lineSeparator()).append(caret.toString());
        }
        java.util.List<String> st = com.dafei1288.jimlang.Trace.snapshot();
if (st != null && !st.isEmpty()) {
    sb.append(System.lineSeparator()).append("Call stack:");
    for (String f : st) sb.append(System.lineSeparator()).append("  at ").append(f);
}
return new RuntimeException(sb.toString());
    }Hashtable<String, Symbol> _sympoltable = new Hashtable<>();
    private final java.util.ArrayDeque<java.util.Hashtable<String, Symbol>> scopeStack = new java.util.ArrayDeque<>();
    private final java.util.Hashtable<String, Symbol> globals = _sympoltable;
    private java.util.Hashtable<String, Symbol> current(){ return scopeStack.isEmpty()? globals : scopeStack.peek(); }
    private Symbol findSymbol(String name){ if(!scopeStack.isEmpty()){ for (java.util.Hashtable<String, Symbol> m : scopeStack){ Symbol s=m.get(name); if(s!=null) return s; } } return globals.get(name); }
    private void pushScope(){ scopeStack.push(new java.util.Hashtable<>()); }
    private void popScope(){ if(!scopeStack.isEmpty()) scopeStack.pop(); }
    Scope currentScope;

    // loop control flow exceptions
    static class BreakException extends RuntimeException { private static final long serialVersionUID = 1L; }
    static class ContinueException extends RuntimeException { private static final long serialVersionUID = 1L; }

    private static int toInt(Object o){
        if(o instanceof Number) return ((Number)o).intValue();
        if(o == null) return 0;
        try{ return Integer.parseInt(String.valueOf(o).trim()); } catch(Exception e){ throw new RuntimeException("Expect integer index but got: " + o); }
    }

    public Hashtable<String, Symbol> getSymbolTable() { return _sympoltable; }

    @Override
    public Object visitProg(ProgContext ctx) {
        currentScope = new RootScope();
        return super.visitProg(ctx);
    }

    @Override
    public Object visitVariableDecl(VariableDeclContext ctx) {
        String varName = ctx.identifier().getText();
        SymbolVar symbol = (SymbolVar) current().get(varName);
        if(symbol == null){
            symbol = new SymbolVar();
            symbol.setName(varName);
            symbol.setParseTree(ctx);
            if(ctx.typeAnnotation()!=null && ctx.typeAnnotation().typeName()!=null){ String tn = ctx.typeAnnotation().typeName().getText(); symbol.setTypeName(tn); try{ ((com.dafei1288.jimlang.metadata.SymbolVar)symbol).setExpectedType(com.dafei1288.jimlang.metadata.TypeDescriptor.parse(tn)); } catch(Exception ex){ throw error("Unknown type: "+tn, ctx); } }
            current().put(varName,symbol);
        }
        for(AssignmentContext assignmentContext : ctx.assignment()){
            if(assignmentContext.expression() != null ){
                Object o = this.visitExpression(assignmentContext.expression());
                com.dafei1288.jimlang.metadata.TypeDescriptor td = ((com.dafei1288.jimlang.metadata.SymbolVar)symbol).getExpectedType(); if(td!=null && !com.dafei1288.jimlang.metadata.TypeDescriptor.isAssignable(td,o)){ throw error("Type mismatch: expected "+symbol.getTypeName()+" but got "+(o==null?"null":o.getClass().getSimpleName()), ctx); } Object __coerced = com.dafei1288.jimlang.metadata.TypeDescriptor.coerceIfNeeded(td,o); symbol.setValue(__coerced);
            }
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(JimLangParser.AssignmentStatementContext ctx) {
        Object newValue = this.visit(ctx.expression());
        JimLangParser.LvalueContext lvc = ctx.lvalue();
        String baseName = lvc.identifier().getText();
        Symbol baseSymbol = findSymbol(baseName);
        if (baseSymbol == null) {
            throw error("Variable '" + baseName + "' is not defined", ctx);
        }
        java.util.List<JimLangParser.AccessorContext> accs = lvc.accessor();
        if (accs == null || accs.isEmpty()) { if(!(baseSymbol instanceof com.dafei1288.jimlang.metadata.SymbolVar)) { baseSymbol.setValue(newValue); return newValue; } com.dafei1288.jimlang.metadata.SymbolVar sv = (com.dafei1288.jimlang.metadata.SymbolVar)baseSymbol; com.dafei1288.jimlang.metadata.TypeDescriptor td = sv.getExpectedType(); if(td!=null && !com.dafei1288.jimlang.metadata.TypeDescriptor.isAssignable(td,newValue)){ throw error("Type mismatch: expected "+sv.getTypeName()+" but got "+(newValue==null?"null":newValue.getClass().getSimpleName()), ctx); } Object __coerced2 = com.dafei1288.jimlang.metadata.TypeDescriptor.coerceIfNeeded(td,newValue); sv.setValue(__coerced2); return __coerced2; }
        Object holder = baseSymbol.getValue();
        for (int i = 0; i < accs.size() - 1; i++) {
            JimLangParser.AccessorContext ac = accs.get(i);
            if (ac.expression() != null) {
                int idx = toInt(this.visit(ac.expression()));
                if (!(holder instanceof java.util.List)) throw error("Index access on non-array", ctx);
                java.util.List list = (java.util.List) holder;
                if (idx < 0 || idx >= list.size()) throw error("Index out of bounds: " + idx, ctx);
                holder = list.get(idx);
            } else {
                String key = ac.identifier().getText();
                if (holder instanceof java.util.Map) {
                    holder = ((java.util.Map) holder).get(key);
                } else if (holder instanceof java.util.List && "length".equals(key)) {
                    throw error("Cannot assign to 'length'", ctx);
                } else {
                    throw error("Property access on non-object", ctx);
                }
            }
        }
        JimLangParser.AccessorContext last = accs.get(accs.size() - 1);
        if (last.expression() != null) {
            int idx = toInt(this.visit(last.expression()));
            if (!(holder instanceof java.util.List)) throw error("Index access on non-array", ctx);
            java.util.List list = (java.util.List) holder;
            if (idx < 0 || idx >= list.size()) throw error("Index out of bounds: " + idx, ctx);
            list.set(idx, newValue);
        } else {
            String key = last.identifier().getText();
            if (!(holder instanceof java.util.Map)) throw error("Property access on non-object", ctx);
            if ("length".equals(key)) throw error("Cannot assign to 'length'", ctx);
            ((java.util.Map) holder).put(key, newValue);
        }
        return newValue;
    }

    @Override
    public Object visitConstVar(JimLangParser.ConstVarContext ctx) {
        if(ctx.NUMBER_LITERAL() != null){
            String numText = ctx.NUMBER_LITERAL().getText().trim();
            if(numText.contains(".")){
                return Double.parseDouble(numText);
            }else{
                return Integer.parseInt(numText);
            }
        }else if(ctx.STRING_LITERAL() != null){
            String text = ctx.STRING_LITERAL().getText();
            if(text.startsWith("\"")){
                text = text.substring(1,text.length()-1);
            }
            return text;
        }else if(ctx.BOOLEAN_LITERAL() != null){
            return Boolean.valueOf(ctx.BOOLEAN_LITERAL().getText().trim());
        }
        return super.visitConstVar(ctx);
    }

    @Override
    public Object visitSingleExpression(SingleExpressionContext ctx) {
        List<PrimaryContext> primaries = ctx.primary();
        if (primaries != null && primaries.size() > 0) {
            Object value = this.visitPrimary(primaries.get(0));
            int ops = ctx.binOP() == null ? 0 : ctx.binOP().size();
            for (int i = 0; i < ops; i++) {
                String op = ctx.binOP(i).getText().trim();
                Object right = this.visitPrimary(primaries.get(i + 1));
                value = executeBinaryOperation(value, right, op, ctx);
            }
            return value;
        }
        return super.visitSingleExpression(ctx);
    }

    @Override
    public Object visitExpression(JimLangParser.ExpressionContext ctx) {
        return this.visitSingleExpression(ctx.singleExpression());
    }

    @Override
    public Object visitExpressionStatement(JimLangParser.ExpressionStatementContext ctx) {
        return this.visitExpression(ctx.expression());
    }

    @Override
    public Object visitFunctionBody(FunctionBodyContext ctx) {
        Scope scope = new Scope();
        currentScope.setSubScope(scope);
        return super.visitFunctionBody(ctx);
    }

    @Override
    public Object visitIfStatement(JimLangParser.IfStatementContext ctx) {
        Object conditionValue = this.visit(ctx.expression());
        boolean condition = false;
        if (conditionValue instanceof Boolean) {
            condition = (Boolean) conditionValue;
        } else if (conditionValue instanceof Number) {
            condition = ((Number) conditionValue).doubleValue() != 0;
        } else if (conditionValue instanceof String) {
            condition = !((String) conditionValue).isEmpty();
        } else if (conditionValue != null) {
            condition = true;
        }
        if (condition) {
            return this.visit(ctx.block(0));
        } else if (ctx.block().size() > 1) {
            return this.visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(JimLangParser.WhileStatementContext ctx) {
        final int MAX_ITERATIONS = 100000;
        int iterations = 0;
        while (true) {
            if (iterations >= MAX_ITERATIONS) {
                throw new RuntimeException("While loop exceeded maximum iterations (" + MAX_ITERATIONS + "). Possible infinite loop.");
            }
            Object conditionValue = this.visit(ctx.expression());
            boolean condition = false;
            if (conditionValue instanceof Boolean) {
                condition = (Boolean) conditionValue;
            } else if (conditionValue instanceof Number) {
                condition = ((Number) conditionValue).doubleValue() != 0;
            } else if (conditionValue instanceof String) {
                condition = !((String) conditionValue).isEmpty();
            } else if (conditionValue != null) {
                condition = true;
            }
            if (!condition) { break; }
            try {
                this.visit(ctx.block());
            } catch (ContinueException ce) {
                iterations++;
                continue;
            } catch (BreakException be) {
                break;
            }
            iterations++;
        }
        return null;
    }

    @Override
    public Object visitForStatement(JimLangParser.ForStatementContext ctx) {
        final int MAX_ITERATIONS = 100000;
        int iterations = 0;
        if (ctx.forInit() != null) {
            this.visit(ctx.forInit());
        }
        while (true) {
            if (iterations >= MAX_ITERATIONS) {
                throw new RuntimeException("For loop exceeded maximum iterations (" + MAX_ITERATIONS + "). Possible infinite loop.");
            }
            if (ctx.forCondition() != null) {
                Object conditionValue = this.visit(ctx.forCondition());
                boolean condition = false;
                if (conditionValue instanceof Boolean) {
                    condition = (Boolean) conditionValue;
                } else if (conditionValue instanceof Number) {
                    condition = ((Number) conditionValue).doubleValue() != 0;
                } else if (conditionValue instanceof String) {
                    condition = !((String) conditionValue).isEmpty();
                } else if (conditionValue != null) {
                    condition = true;
                }
                if (!condition) { break; }
            }
            boolean doContinue = false;
            try {
                this.visit(ctx.block());
            } catch (ContinueException ce) {
                doContinue = true;
            } catch (BreakException be) {
                break;
            }
            if (ctx.forUpdate() != null) {
                this.visit(ctx.forUpdate());
            }
            iterations++;
            if (doContinue) { continue; }
        }
        return null;
    }

    @Override
    public Object visitBlock(JimLangParser.BlockContext ctx) {
        pushScope();
        try {
            if (ctx.statementList() != null) {
                return this.visit(ctx.statementList());
            }
            return null;
        } finally {
            popScope();
        }
    }

    
    @Override
    public Object visitFunctionDecl(FunctionDeclContext ctx) {
        String functionName = ctx.identifier().getText();
        if(globals.get(functionName) == null){
            SymbolFunction symbol = new SymbolFunction();
            symbol.setName(functionName);
            symbol.setParseTree(ctx);
            if(Funcall.isSysFunction(functionName)){
                symbol.setType(SymbolType.SYSFUNCTION);
            }else{
                symbol.setType(SymbolType.FUNCTION);
            }
            if(ctx.parameterList()!=null && ctx.parameterList().singleExpression()!=null){
                List<String> pl = ctx.parameterList().singleExpression().stream().map(it->it.getText().trim()).collect(Collectors.toList());
                symbol.setParameterList(pl);
            }
            if(ctx.functionBody() != null){
                symbol.setFunctionBody(ctx.functionBody().getText());
            }
            globals.put(functionName,symbol);
        }
        return null;
    }private Object executeBinaryOperation(Object left, Object right, String op, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if ("+".equals(op) && (left instanceof String || right instanceof String)) {
            return String.valueOf(left) + String.valueOf(right);
        }
        if (left instanceof Number && right instanceof Number) {
            if (left instanceof Double || right instanceof Double) {
                double l = ((Number) left).doubleValue();
                double r = ((Number) right).doubleValue();
                switch (op) {
                    case "+": return l + r;
                    case "-": return l - r;
                    case "*": return l * r;
                    case "/":
                        if (r == 0) throw error("Division by zero", ctx);
                        return l / r;
                    case "%": return l % r;
                    case ">": return l > r;
                    case "<": return l < r;
                    case ">=": return l >= r;
                    case "<=": return l <= r;
                    case "==": return l == r;
                    case "!=": return l != r;
                    default: throw error("Unknown operator: " + op, ctx);
                }
            } else {
                int l = ((Number) left).intValue();
                int r = ((Number) right).intValue();
                switch (op) {
                    case "+": return l + r;
                    case "-": return l - r;
                    case "*": return l * r;
                    case "/":
                        if (r == 0) throw error("Division by zero", ctx);
                        return l / r;
                    case "%": return l % r;
                    case ">": return l > r;
                    case "<": return l < r;
                    case ">=": return l >= r;
                    case "<=": return l <= r;
                    case "==": return l == r;
                    case "!=": return l != r;
                    default: throw error("Unknown operator: " + op, ctx);
                }
            }
        }
        if ("==".equals(op)) { return left.equals(right); }
        if ("!=".equals(op)) { return !left.equals(right); }
        throw error("Cannot perform operation " + op + " on types " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName(), ctx);
    }

    @Override
    public Object visitReturnStatement(ReturnStatementContext ctx) {
        return this.visitExpression(ctx.expression());
    }

    @Override
    public Object visitFunctionCall(FunctionCallContext ctx) {
    String functionName = null;
    if (ctx.sysfunction() != null) {
        functionName = ctx.sysfunction().getText();
    } else if (ctx.identifier() != null) {
        functionName = ctx.identifier().getText();
    }

    java.util.List<Object> actuals = new java.util.ArrayList<>();
    if (ctx.parameterList() != null && ctx.parameterList().singleExpression() != null) {
        for (JimLangParser.SingleExpressionContext se : ctx.parameterList().singleExpression()) {
            actuals.add(this.visitSingleExpression(se));
        }
    }

    // special: apply(delegateOrName, ...args)
    if ("apply".equals(functionName)) {
        java.util.List<Object> args = actuals;
        if (args == null || args.isEmpty()) throw error("apply requires at least 1 argument", ctx);
        Object target = args.get(0);
        java.util.List<Object> rest = args.subList(1, args.size());
        String targetName;
        java.util.ArrayList<Object> callArgs = new java.util.ArrayList<>();
        if (target instanceof com.dafei1288.jimlang.Delegate) {
            com.dafei1288.jimlang.Delegate d = (com.dafei1288.jimlang.Delegate) target;
            targetName = d.getName();
            java.util.List<Object> b = d.getBound(); if (b != null) callArgs.addAll(b);
        } else {
            targetName = String.valueOf(target);
        }
        callArgs.addAll(rest);
        if (com.dafei1288.jimlang.sys.Funcall.isSysFunction(targetName)) {
            com.dafei1288.jimlang.Trace.push(targetName+"() via apply");
            try { return com.dafei1288.jimlang.sys.Funcall.exec(targetName, callArgs); }
            finally { com.dafei1288.jimlang.Trace.pop(); }
        }
        SymbolFunction currentSymbol2 = (SymbolFunction) globals.get(targetName);
        if (currentSymbol2 == null) throw error("Unknown function: "+targetName, ctx);
        com.dafei1288.jimlang.Trace.push(targetName+"() via apply");
        com.dafei1288.jimlang.Trace.log("enter "+targetName);
        pushScope();
        try {
            java.util.List<String> formalParams = currentSymbol2.getParameterList();
            if (formalParams != null) {
                if (formalParams.size() != callArgs.size()) {
                    throw error("Function "+targetName+" expects "+formalParams.size()+" arguments but got "+callArgs.size(), ctx);
                }
                for (int i = 0; i < formalParams.size(); i++) {
                    SymbolVar paramVar = new SymbolVar();
                    paramVar.setName(formalParams.get(i));
                    paramVar.setValue(callArgs.get(i));
                    current().put(formalParams.get(i), paramVar);
                }
            }
            Object result = null;
            FunctionDeclContext funcDecl = (FunctionDeclContext) currentSymbol2.getParseTree();
            if (funcDecl != null && funcDecl.functionBody() != null) {
                if (funcDecl.functionBody().statementList() != null) {
                    this.visit(funcDecl.functionBody().statementList());
                }
                if (funcDecl.functionBody().returnStatement() != null) {
                    result = this.visitReturnStatement(funcDecl.functionBody().returnStatement());
                }
            }
            return result;
        } finally {
            popScope();
            com.dafei1288.jimlang.Trace.log("leave "+targetName);
            com.dafei1288.jimlang.Trace.pop();
        }
    }
    // normalize delegate/partial first argument when user passes bare identifier (e.g., delegate(add))
    if (("delegate".equals(functionName) || "partial".equals(functionName)) && actuals != null && !actuals.isEmpty()) {
        if (actuals.get(0) == null && ctx.parameterList() != null && ctx.parameterList().singleExpression() != null && !ctx.parameterList().singleExpression().isEmpty()) {
            // recover raw text of the first argument
            String raw = ctx.parameterList().singleExpression(0).getText();
            if (raw != null) {
                // strip surrounding quotes if any
                if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length()-1) == '"') {
                    raw = raw.substring(1, raw.length()-1);
                }
                actuals.set(0, raw);
            }
        }
    }    // built-in function
    if (com.dafei1288.jimlang.sys.Funcall.isSysFunction(functionName)) {
        com.dafei1288.jimlang.Trace.push(functionName + "()");
        com.dafei1288.jimlang.Trace.log("enter " + functionName);
        try {
            return com.dafei1288.jimlang.sys.Funcall.exec(functionName, actuals);
        } finally {
            com.dafei1288.jimlang.Trace.log("leave " + functionName);
            com.dafei1288.jimlang.Trace.pop();
        }
    }

    // user-defined function
    SymbolFunction currentSymbol = (SymbolFunction) globals.get(functionName);
    if (currentSymbol != null) {
        com.dafei1288.jimlang.Trace.push(functionName + "()");
        com.dafei1288.jimlang.Trace.log("enter " + functionName);
        pushScope();
        try {
            java.util.List<String> formalParams = currentSymbol.getParameterList();
            if (formalParams != null) {
                if (formalParams.size() != actuals.size()) {
                    throw error("Function " + functionName + " expects " + formalParams.size() + " arguments but got " + actuals.size(), ctx);
                }
                for (int i = 0; i < formalParams.size(); i++) {
                    SymbolVar paramVar = new SymbolVar();
                    paramVar.setName(formalParams.get(i));
                    paramVar.setValue(actuals.get(i));
                    current().put(formalParams.get(i), paramVar);
                }
            }
            Object result = null;
            FunctionDeclContext funcDecl = (FunctionDeclContext) currentSymbol.getParseTree();
            if (funcDecl != null && funcDecl.functionBody() != null) {
                if (funcDecl.functionBody().statementList() != null) {
                    this.visit(funcDecl.functionBody().statementList());
                }
                if (funcDecl.functionBody().returnStatement() != null) {
                    result = this.visitReturnStatement(funcDecl.functionBody().returnStatement());
                }
            }
            return result;
        } finally { popScope();
            com.dafei1288.jimlang.Trace.log("leave " + functionName);
            com.dafei1288.jimlang.Trace.pop();
        }
    }

    return super.visitFunctionCall(ctx);
}@Override
    public Object visitSysfunction(SysfunctionContext ctx) {
        String functionName = ctx.getText();
        return super.visitSysfunction(ctx);
    }

    // --- primary/atom/arrays/objects ---
    @Override
    public Object visitPrimary(JimLangParser.PrimaryContext ctx) {
        Object value = this.visitAtom(ctx.atom());
        List<JimLangParser.AccessorContext> accs = ctx.accessor();
        if (accs != null) {
            for (JimLangParser.AccessorContext ac : accs) {
                if (ac.expression() != null) {
                    int idx = toInt(this.visit(ac.expression()));
                    if (!(value instanceof java.util.List)) throw error("Index access on non-array", ctx);
                    java.util.List list = (java.util.List) value;
                    if (idx < 0 || idx >= list.size()) throw error("Index out of bounds: " + idx, ctx);
                    value = list.get(idx);
                } else {
                    String key = ac.identifier().getText();
                    if (value instanceof java.util.Map) {
                        value = ((java.util.Map) value).get(key);
                    } else if (value instanceof java.util.List && "length".equals(key)) {
                        value = ((java.util.List) value).size();
                    } else if (value instanceof String && "length".equals(key)) {
                        value = ((String) value).length();
                    } else {
                        throw error("Property access on non-object", ctx);
                    }
                }
            }
        }
        return value;
    }

    public Object visitAtom(JimLangParser.AtomContext ctx) {
        if (ctx.identifier() != null) {
            String name = ctx.identifier().getText();
            Symbol sym = findSymbol(name);
            if (sym == null) throw error("Variable '" + name + "' is not defined", ctx);
            return sym.getValue();
        }
        if (ctx.constVar() != null) return this.visitConstVar(ctx.constVar());
        if (ctx.functionCall() != null) return this.visitFunctionCall(ctx.functionCall());
        if (ctx.arrayLiteral() != null) return this.visitArrayLiteral(ctx.arrayLiteral());
        if (ctx.objectLiteral() != null) return this.visitObjectLiteral(ctx.objectLiteral());
        if (ctx.expression() != null) return this.visit(ctx.expression());
        return null;
    }

    public Object visitArrayLiteral(JimLangParser.ArrayLiteralContext ctx) {
        ArrayList<Object> arr = new ArrayList<>();
        List<JimLangParser.ExpressionContext> exprs = ctx.expression();
        if (exprs != null) {
            for (JimLangParser.ExpressionContext e : exprs) {
                arr.add(this.visit(e));
            }
        }
        return arr;
    }

    public Object visitObjectLiteral(JimLangParser.ObjectLiteralContext ctx) {
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        List<JimLangParser.PairContext> pairs = ctx.pair();
        if (pairs != null) {
            for (JimLangParser.PairContext p : pairs) {
                String key = p.identifier().getText();
                Object val = this.visit(p.expression());
                map.put(key, val);
            }
        }
        return map;
    }

    // break/continue statements
    public Object visitBreakStatement(JimLangParser.BreakStatementContext ctx){ throw new BreakException(); }
    public Object visitContinueStatement(JimLangParser.ContinueStatementContext ctx){ throw new ContinueException(); }
}