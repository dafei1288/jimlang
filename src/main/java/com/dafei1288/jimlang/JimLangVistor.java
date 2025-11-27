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
import java.util.List;
import java.util.stream.Collectors;

import java.util.Hashtable;
import java.util.Collections;
import org.antlr.v4.runtime.TokenStream;

public class JimLangVistor extends JimLangBaseVisitor {
//    Hashtable<String, MyLangParser.FunctionDeclContext> sympoltable = new Hashtable<>();
    Hashtable<String, Symbol> _sympoltable = new Hashtable<>();
    Scope currentScope;

    /**
     * ???????????REPL ????????????
     */
    public Hashtable<String, Symbol> getSymbolTable() {
        return _sympoltable;
    }

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
            // ????????
            symbol = new SymbolVar();
            symbol.setName(varName);
            symbol.setParseTree(ctx);

            if(ctx.typeAnnotation()!=null && ctx.typeAnnotation().typeName()!=null){
                symbol.setTypeName(ctx.typeAnnotation().typeName().getText());
            }

            _sympoltable.put(varName,symbol);
        }

        // ??????????????????????????????
        for(AssignmentContext assignmentContext : ctx.assignment()){
             if(assignmentContext.expression() != null ){
                Object o = this.visitExpression(assignmentContext.expression());
                symbol.setValue(o);
             }
        }

        // ?????super???????????
        return null;
    }

    @Override
    public Object visitAssignmentStatement(JimLangParser.AssignmentStatementContext ctx) {
        String varName = ctx.identifier().getText();

        // ??????????????
        Symbol symbol = _sympoltable.get(varName);
        if (symbol == null) {
            throw new RuntimeException("Variable '" + varName + "' is not defined");
        }

        // ???????
        Object newValue = this.visit(ctx.expression());

        // ????????
        symbol.setValue(newValue);

        return newValue;
    }

    @Override
    public Object visitConstVar(JimLangParser.ConstVarContext ctx) {

        if(ctx.NUMBER_LITERAL() != null){
            String numText = ctx.NUMBER_LITERAL().getText().trim();
            // ??????????????????????????Double?????? Integer
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
        // Check if this is a binary operation
        if (ctx.binOP() != null && ctx.primary().size() > 1) {
            // Binary operation: primary binOP primary
            Object left = this.visitPrimary(ctx.primary(0));
            Object right = this.visitPrimary(ctx.primary(1));
            String op = ctx.binOP().getText().trim();
            return executeBinaryOperation(left, right, op);
        }

        // Single primary
        PrimaryContext primaryContext = ctx.primary(0);
        if(primaryContext.constVar()!=null){
            return this.visitConstVar(primaryContext.constVar());
        }
        if(primaryContext.functionCall() != null){
            return this.visitFunctionCall(primaryContext.functionCall());
        }
        if(_sympoltable.get(primaryContext.getText())!=null){
            return _sympoltable.get(primaryContext.getText().trim()).getValue();
        }

        return super.visitSingleExpression(ctx);
    }

    @Override
    public Object visitPrimary(PrimaryContext ctx) {
        if(ctx.identifier() != null){
            String varName = ctx.identifier().getText();
            Symbol currentSymbol = _sympoltable.get(varName);
            if(currentSymbol != null){
                return currentSymbol.getValue();
            }
        }
        if(ctx.constVar() != null){
            Object o = this.visitConstVar(ctx.constVar());
            return o;
        }
        if(ctx.functionCall() != null){
            // ????????????
            return this.visitFunctionCall(ctx.functionCall());
        }
        return super.visitPrimary(ctx);
    }

    @Override
    public Object visitFunctionDecl(FunctionDeclContext ctx) {
        String functionName = ctx.identifier().getText();
        if(_sympoltable.get(functionName) == null){
//            System.out.println("define function ==> "+functionName);

//            sympoltable.put(ctx.identifier().getText(),ctx);

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
                // ?????????????????????????????????
            }

//            if(ctx.functionBody().)
            _sympoltable.put(functionName,symbol);
//            return null;
        }

        // ?????super.visitFunctionDecl??????????????????
        return null;
    }

    /**
     * ???????????????????????
     */
    private Object executeBinaryOperation(Object left, Object right, String op) {
        // ????????
        if ("+".equals(op) && (left instanceof String || right instanceof String)) {
            return String.valueOf(left) + String.valueOf(right);
        }

        // ???????
        if (left instanceof Number && right instanceof Number) {
            // ???????????? Double?????? Double
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
                // ??????
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

        // ??????
        if ("==".equals(op)) {
            return left.equals(right);
        }
        if ("!=".equals(op)) {
            return !left.equals(right);
        }

        throw new RuntimeException("Cannot perform operation " + op + " on types " +
            left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
    }

    @Override
    public Object visitExpressionStatement(JimLangParser.ExpressionStatementContext ctx) {
        // ?????? expression
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
        // 1. ???????????
        Object conditionValue = this.visit(ctx.expression());

        // 2. ????????????????
        boolean condition = false;
        if (conditionValue instanceof Boolean) {
            condition = (Boolean) conditionValue;
        } else if (conditionValue instanceof Number) {
            // ????? ??false?????? true
            condition = ((Number) conditionValue).doubleValue() != 0;
        } else if (conditionValue instanceof String) {
            // ??????????????false?????? true
            condition = !((String) conditionValue).isEmpty();
        } else if (conditionValue != null) {
            // ?????null ??? true
            condition = true;
        }

        // 3. ???????????????
        if (condition) {
            return this.visit(ctx.block(0));
        } else if (ctx.block().size() > 1) {
            // ??? else ???????????
            return this.visit(ctx.block(1));
        }

        return null;
    }

    @Override
    public Object visitWhileStatement(JimLangParser.WhileStatementContext ctx) {
        // ??????????????????????????
        final int MAX_ITERATIONS = 100000;
        int iterations = 0;

        // ??????????????-> ????????
        while (true) {
            // ?????????????
            if (iterations >= MAX_ITERATIONS) {
                throw new RuntimeException("While loop exceeded maximum iterations (" + MAX_ITERATIONS + "). Possible infinite loop.");
            }

            // 1. ???????????
            Object conditionValue = this.visit(ctx.expression());

            // 2. ????????????????
            boolean condition = false;
            if (conditionValue instanceof Boolean) {
                condition = (Boolean) conditionValue;
            } else if (conditionValue instanceof Number) {
                // ????? ??false?????? true
                condition = ((Number) conditionValue).doubleValue() != 0;
            } else if (conditionValue instanceof String) {
                // ??????????????false?????? true
                condition = !((String) conditionValue).isEmpty();
            } else if (conditionValue != null) {
                // ?????null ??? true
                condition = true;
            }

            // 3. ??????????????????
            if (!condition) {
                break;
            }

            // 4. ????????
            this.visit(ctx.block());

            iterations++;
        }

        return null;
    }

    @Override
    public Object visitForStatement(JimLangParser.ForStatementContext ctx) {
        // ??????????????????????????
        final int MAX_ITERATIONS = 100000;
        int iterations = 0;

        // 1. ?????????????????
        if (ctx.forInit() != null) {
            this.visit(ctx.forInit());
        }

        // 2. ??????
        while (true) {
            // ?????????????
            if (iterations >= MAX_ITERATIONS) {
                throw new RuntimeException("For loop exceeded maximum iterations (" + MAX_ITERATIONS + "). Possible infinite loop.");
            }

            // 3. ????????????????
            if (ctx.forCondition() != null) {
                Object conditionValue = this.visit(ctx.forCondition());

                // ????????????????
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

                // ??????????????????
                if (!condition) {
                    break;
                }
            }

            // 4. ????????
            this.visit(ctx.block());

            // 5. ??????????????????
            if (ctx.forUpdate() != null) {
                this.visit(ctx.forUpdate());
            }

            iterations++;
        }

        return null;
    }

    @Override
    public Object visitBlock(JimLangParser.BlockContext ctx) {
        // ??????????????
        if (ctx.statementList() != null) {
            return this.visit(ctx.statementList());
        }
        return null;
    }

    @Override
    public Object visitExpression(JimLangParser.ExpressionContext ctx) {
        // ???????????expressionStatement ??????????????
        if (ctx.binOP() == null) {
            // ????????
            return this.visitSingleExpression(ctx.singleExpression(0));
        } else {
            // ??????
            String left = ctx.singleExpression(0).getText().trim();
            String right = ctx.singleExpression(1).getText().trim();
            String op = ctx.binOP().getText().trim();

            Object leftObject = null;
            if (_sympoltable.containsKey(left)) {
                leftObject = _sympoltable.get(left).getValue();
            } else {
                leftObject = this.visitSingleExpression(ctx.singleExpression(0));
            }

            Object rightObject = null;
            if (_sympoltable.containsKey(right)) {
                rightObject = _sympoltable.get(right).getValue();
            } else {
                rightObject = this.visitSingleExpression(ctx.singleExpression(1));
            }

            return executeBinaryOperation(leftObject, rightObject, op);
        }
    }

    @Override
    public Object visitReturnStatement(ReturnStatementContext ctx) {
        return this.visitExpression(ctx.expression());
//        return super.visitReturnStatement(ctx);
    }

    @Override
    public Object visitFunctionCall(FunctionCallContext ctx) {
        String functionName = null;

        // ???????????? print, println??
        if(ctx.sysfunction() != null){
            functionName = ctx.sysfunction().getText();
            List<Object> all = ctx.parameterList().singleExpression().stream().map(it->{
                return this.visitSingleExpression(it);
            }).collect(Collectors.toList());
            return Funcall.exec(functionName,all);
        }

        // ?????????????????
        if(ctx.identifier() != null){
            functionName = ctx.identifier().getText();
        }

        // ?????????
        SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);
        // Built-in function invoked as identifier (fast path)
        if(currentSymbol == null && Funcall.isSysFunction(functionName)){
            java.util.List<Object> all = null;
            if(ctx.parameterList() != null && ctx.parameterList().singleExpression() != null){
                all = ctx.parameterList().singleExpression().stream().map(it->{
                    return this.visitSingleExpression(it);
                }).collect(java.util.stream.Collectors.toList());
            }else{
                all = java.util.Collections.emptyList();
            }
            return Funcall.exec(functionName,all);
        }

        if(currentSymbol != null){
            StackFrane stackFrane = new StackFrane(currentSymbol,functionName);

            // 1. ???????????
            List<Object> actualParams = null;
            if(ctx.parameterList() != null && ctx.parameterList().singleExpression() != null){
                actualParams = ctx.parameterList().singleExpression().stream().map(it->{
                    return this.visitSingleExpression(it);
                }).collect(Collectors.toList());
            }

            // 2. ????????????
            List<String> formalParams = currentSymbol.getParameterList();

            // 3. ???????????
            Hashtable<String, Symbol> savedSymbolTable = new Hashtable<>(_sympoltable);

            // 4. ????????????????????????
            if(formalParams != null && actualParams != null){
                if(formalParams.size() != actualParams.size()){
                    throw new RuntimeException("Function " + functionName + " expects " +
                        formalParams.size() + " arguments but got " + actualParams.size());
                }

                for(int i = 0; i < formalParams.size(); i++){
                    SymbolVar paramVar = new SymbolVar();
                    paramVar.setName(formalParams.get(i));
                    paramVar.setValue(actualParams.get(i));
                    _sympoltable.put(formalParams.get(i), paramVar);
                }
            }

            // 5. ????????
            Object result = null;
            FunctionDeclContext funcDecl = (FunctionDeclContext) currentSymbol.getParseTree();
            if(funcDecl != null && funcDecl.functionBody() != null){
                // ????????????????????? for ??????????????
                if(funcDecl.functionBody().statementList() != null){
                    this.visit(funcDecl.functionBody().statementList());
                }

                // ?????return ????????????
                if(funcDecl.functionBody().returnStatement() != null){
                    result = this.visitReturnStatement(funcDecl.functionBody().returnStatement());
                }
            }

            // 6. ????????????????????????
            _sympoltable = savedSymbolTable;

            return result;
        }
        return super.visitFunctionCall(ctx);
    }

    @Override
    public Object visitSysfunction(SysfunctionContext ctx) {
        String functionName = ctx.getText();
        return super.visitSysfunction(ctx);
    }

    public Object visitFunctionCall__(FunctionCallContext ctx) {
        String functionName = ctx.identifier().getText();
        Symbol currentSymbol = _sympoltable.get(functionName);
        StackFrane stackFrane = new StackFrane(currentSymbol,functionName);
        if("print".equals(functionName)){
            String parStr = ctx.parameterList().getText();
            if(parStr.startsWith("\"")){
                parStr = parStr.substring(1,parStr.length()-1);
            }
//            System.out.println(parStr);
            return null;
        }else{
//            System.out.println("run function ==>"+functionName);
            FunctionDeclContext fdc = (FunctionDeclContext) _sympoltable.get(functionName).getParseTree();
                    //sympoltable.get(functionName);
            if(fdc==null){
                throw new RuntimeException("undefine function ...." + functionName);
            }
            super.visitFunctionDecl(fdc);
        }
        return super.visitFunctionCall(ctx);
    }




}






