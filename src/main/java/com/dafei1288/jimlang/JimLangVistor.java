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

public class JimLangVistor extends JimLangBaseVisitor {
    Hashtable<String, Symbol> _sympoltable = new Hashtable<>();
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
        SymbolVar symbol = (SymbolVar) _sympoltable.get(varName);
        if(symbol == null){
            symbol = new SymbolVar();
            symbol.setName(varName);
            symbol.setParseTree(ctx);
            if(ctx.typeAnnotation()!=null && ctx.typeAnnotation().typeName()!=null){
                symbol.setTypeName(ctx.typeAnnotation().typeName().getText());
            }
            _sympoltable.put(varName,symbol);
        }
        for(AssignmentContext assignmentContext : ctx.assignment()){
            if(assignmentContext.expression() != null ){
                Object o = this.visitExpression(assignmentContext.expression());
                symbol.setValue(o);
            }
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(JimLangParser.AssignmentStatementContext ctx) {
        Object newValue = this.visit(ctx.expression());
        JimLangParser.LvalueContext lvc = ctx.lvalue();
        String baseName = lvc.identifier().getText();
        Symbol baseSymbol = _sympoltable.get(baseName);
        if (baseSymbol == null) {
            throw new RuntimeException("Variable '" + baseName + "' is not defined");
        }
        java.util.List<JimLangParser.AccessorContext> accs = lvc.accessor();
        if (accs == null || accs.isEmpty()) {
            baseSymbol.setValue(newValue);
            return newValue;
        }
        Object holder = baseSymbol.getValue();
        for (int i = 0; i < accs.size() - 1; i++) {
            JimLangParser.AccessorContext ac = accs.get(i);
            if (ac.expression() != null) {
                int idx = toInt(this.visit(ac.expression()));
                if (!(holder instanceof java.util.List)) throw new RuntimeException("Index access on non-array");
                java.util.List list = (java.util.List) holder;
                if (idx < 0 || idx >= list.size()) throw new RuntimeException("Index out of bounds: " + idx);
                holder = list.get(idx);
            } else {
                String key = ac.identifier().getText();
                if (holder instanceof java.util.Map) {
                    holder = ((java.util.Map) holder).get(key);
                } else if (holder instanceof java.util.List && "length".equals(key)) {
                    throw new RuntimeException("Cannot assign to 'length'");
                } else {
                    throw new RuntimeException("Property access on non-object");
                }
            }
        }
        JimLangParser.AccessorContext last = accs.get(accs.size() - 1);
        if (last.expression() != null) {
            int idx = toInt(this.visit(last.expression()));
            if (!(holder instanceof java.util.List)) throw new RuntimeException("Index access on non-array");
            java.util.List list = (java.util.List) holder;
            if (idx < 0 || idx >= list.size()) throw new RuntimeException("Index out of bounds: " + idx);
            list.set(idx, newValue);
        } else {
            String key = last.identifier().getText();
            if (!(holder instanceof java.util.Map)) throw new RuntimeException("Property access on non-object");
            if ("length".equals(key)) throw new RuntimeException("Cannot assign to 'length'");
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
                value = executeBinaryOperation(value, right, op);
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
        if (ctx.statementList() != null) {
            return this.visit(ctx.statementList());
        }
        return null;
    }

    
    @Override
    public Object visitFunctionDecl(FunctionDeclContext ctx) {
        String functionName = ctx.identifier().getText();
        if(_sympoltable.get(functionName) == null){
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
            _sympoltable.put(functionName,symbol);
        }
        return null;
    }private Object executeBinaryOperation(Object left, Object right, String op) {
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
                        if (r == 0) throw new RuntimeException("Division by zero");
                        return l / r;
                    case "%": return l % r;
                    case ">": return l > r;
                    case "<": return l < r;
                    case ">=": return l >= r;
                    case "<=": return l <= r;
                    case "==": return l == r;
                    case "!=": return l != r;
                    default: throw new RuntimeException("Unknown operator: " + op);
                }
            } else {
                int l = ((Number) left).intValue();
                int r = ((Number) right).intValue();
                switch (op) {
                    case "+": return l + r;
                    case "-": return l - r;
                    case "*": return l * r;
                    case "/":
                        if (r == 0) throw new RuntimeException("Division by zero");
                        return l / r;
                    case "%": return l % r;
                    case ">": return l > r;
                    case "<": return l < r;
                    case ">=": return l >= r;
                    case "<=": return l <= r;
                    case "==": return l == r;
                    case "!=": return l != r;
                    default: throw new RuntimeException("Unknown operator: " + op);
                }
            }
        }
        if ("==".equals(op)) { return left.equals(right); }
        if ("!=".equals(op)) { return !left.equals(right); }
        throw new RuntimeException("Cannot perform operation " + op + " on types " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
    }

    @Override
    public Object visitReturnStatement(ReturnStatementContext ctx) {
        return this.visitExpression(ctx.expression());
    }

    @Override
    public Object visitFunctionCall(FunctionCallContext ctx) {
        String functionName = null;
        if(ctx.sysfunction() != null){
            functionName = ctx.sysfunction().getText();
            List<Object> all = ctx.parameterList().singleExpression().stream().map(it->{
                return this.visitSingleExpression(it);
            }).collect(Collectors.toList());
            { com.dafei1288.jimlang.Trace.log("enter " + functionName); Object __ret = Funcall.exec(functionName,all); com.dafei1288.jimlang.Trace.log("leave " + functionName); return __ret; }
        }
        if(ctx.identifier() != null){ functionName = ctx.identifier().getText(); }
        SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);
        if(currentSymbol == null && Funcall.isSysFunction(functionName)){
            java.util.List<Object> all = null;
            if(ctx.parameterList() != null && ctx.parameterList().singleExpression() != null){
                all = ctx.parameterList().singleExpression().stream().map(it->{
                    return this.visitSingleExpression(it);
                }).collect(java.util.stream.Collectors.toList());
            }else{
                all = java.util.Collections.emptyList();
            }
            { com.dafei1288.jimlang.Trace.log("enter " + functionName); Object __ret = Funcall.exec(functionName,all); com.dafei1288.jimlang.Trace.log("leave " + functionName); return __ret; }
        }
        if(currentSymbol != null){ com.dafei1288.jimlang.Trace.log("enter " + functionName); try {
            StackFrane stackFrane = new StackFrane(currentSymbol,functionName);
            List<Object> actualParams = null;
            if(ctx.parameterList() != null && ctx.parameterList().singleExpression() != null){
                actualParams = ctx.parameterList().singleExpression().stream().map(it->{
                    return this.visitSingleExpression(it);
                }).collect(Collectors.toList());
            }
            List<String> formalParams = currentSymbol.getParameterList();
            Hashtable<String, Symbol> savedSymbolTable = new Hashtable<>(_sympoltable);
            if(formalParams != null && actualParams != null){
                if(formalParams.size() != actualParams.size()){
                    throw new RuntimeException("Function " + functionName + " expects " + formalParams.size() + " arguments but got " + actualParams.size());
                }
                for(int i = 0; i < formalParams.size(); i++){
                    SymbolVar paramVar = new SymbolVar();
                    paramVar.setName(formalParams.get(i));
                    paramVar.setValue(actualParams.get(i));
                    _sympoltable.put(formalParams.get(i), paramVar);
                }
            }
            Object result = null;
            FunctionDeclContext funcDecl = (FunctionDeclContext) currentSymbol.getParseTree();
            if(funcDecl != null && funcDecl.functionBody() != null){
                if(funcDecl.functionBody().statementList() != null){
                    this.visit(funcDecl.functionBody().statementList());
                }
                if(funcDecl.functionBody().returnStatement() != null){
                    result = this.visitReturnStatement(funcDecl.functionBody().returnStatement());
                }
            }            _sympoltable = savedSymbolTable; return result; } finally { com.dafei1288.jimlang.Trace.log("leave " + functionName); } }
        return super.visitFunctionCall(ctx);
    }

    @Override
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
                    if (!(value instanceof java.util.List)) throw new RuntimeException("Index access on non-array");
                    java.util.List list = (java.util.List) value;
                    if (idx < 0 || idx >= list.size()) throw new RuntimeException("Index out of bounds: " + idx);
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
                        throw new RuntimeException("Property access on non-object");
                    }
                }
            }
        }
        return value;
    }

    public Object visitAtom(JimLangParser.AtomContext ctx) {
        if (ctx.identifier() != null) {
            String name = ctx.identifier().getText();
            Symbol sym = _sympoltable.get(name);
            if (sym == null) throw new RuntimeException("Variable '" + name + "' is not defined");
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

