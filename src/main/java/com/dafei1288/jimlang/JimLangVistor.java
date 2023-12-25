package com.dafei1288.jimlang;

import com.dafei1288.jimlang.metadata.Scope;
import com.dafei1288.jimlang.metadata.Scope.RootScope;
import com.dafei1288.jimlang.metadata.StackFrane;
import com.dafei1288.jimlang.metadata.Symbol;
import com.dafei1288.jimlang.metadata.SymbolFunction;
import com.dafei1288.jimlang.metadata.SymbolType;
import com.dafei1288.jimlang.metadata.SymbolVar;

import com.dafei1288.jimlang.parser.JimLangBaseVisitor;
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
import org.antlr.v4.runtime.TokenStream;

public class JimLangVistor extends JimLangBaseVisitor {
//    Hashtable<String, MyLangParser.FunctionDeclContext> sympoltable = new Hashtable<>();
    Hashtable<String, Symbol> _sympoltable = new Hashtable<>();
    Scope currentScope;

    @Override
    public Object visitProg(ProgContext ctx) {
        currentScope = new RootScope();
        return super.visitProg(ctx);
    }

    @Override
    public Object visitVariableDecl(VariableDeclContext ctx) {
        String varName = ctx.identifier().getText();
        if(_sympoltable.get(varName) == null){

//            System.out.println("define var ==> "+varName);
//            sympoltable.put(ctx.identifier().getText(),ctx);

            SymbolVar symbol = new SymbolVar();
            symbol.setName(varName);
            symbol.setParseTree(ctx);

            if(ctx.typeAnnotation()!=null && ctx.typeAnnotation().typeName()!=null){
                symbol.setTypeName(ctx.typeAnnotation().typeName().getText());
            }

//            if(ctx.singleExpression() != null &&  ctx.singleExpression().primary() != null && ctx.singleExpression().primary().size() > 0){
//                SingleExpressionContext singleExpressionContext = ctx.singleExpression();
//                PrimaryContext primaryContext = singleExpressionContext.primary(0);
//                if(primaryContext.NUMBER_LITERAL() != null){
//                    symbol.setValue(Integer.parseInt(primaryContext.NUMBER_LITERAL().getText().trim()));
//                }else if(primaryContext.STRING_LITERAL() != null){
//                    symbol.setValue(primaryContext.STRING_LITERAL().getText());
//                }else{
//
//                }
//            } /

            for(AssignmentContext assignmentContext : ctx.assignment()){
                 if(assignmentContext.singleExpression() != null &&  assignmentContext.singleExpression().primary() != null && assignmentContext.singleExpression().primary().size() > 0){
                    SingleExpressionContext singleExpressionContext = assignmentContext.singleExpression();
                    PrimaryContext primaryContext = singleExpressionContext.primary(0);
                    if(primaryContext.NUMBER_LITERAL() != null){
                        symbol.setValue(Integer.parseInt(primaryContext.NUMBER_LITERAL().getText().trim()));
                    }else if(primaryContext.STRING_LITERAL() != null){
                        symbol.setValue(primaryContext.STRING_LITERAL().getText());
                    }else{

                    }
                }
            }

            _sympoltable.put(varName,symbol);
        }

        return super.visitVariableDecl(ctx);
    }

    @Override
    public Object visitSingleExpression(SingleExpressionContext ctx) {
        PrimaryContext primaryContext = ctx.primary(0);
        if(_sympoltable.get(primaryContext.getText())!=null){
            return _sympoltable.get(primaryContext.getText().trim()).getValue();
        }
        if(primaryContext.NUMBER_LITERAL() != null){
            return Integer.parseInt(primaryContext.NUMBER_LITERAL().getText().trim());
        }else if(primaryContext.STRING_LITERAL() != null){
            String text = primaryContext.STRING_LITERAL().getText();
            if(text.startsWith("\"")){
                text = text.substring(1,text.length()-1);
            }
            return text;
        }else if(primaryContext.BOOLEAN_LITERAL() != null){
            return Boolean.valueOf(primaryContext.BOOLEAN_LITERAL().getText());
//            return this.visitBooleanType(primaryContext.booleanType());
        }
        return super.visitSingleExpression(ctx);
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
                if(ctx.functionBody().returnStatement() != null){
                    Object o = this.visitReturnStatement(ctx.functionBody().returnStatement());
                    symbol.setValue(o);
                }
            }

//            if(ctx.functionBody().)
            _sympoltable.put(functionName,symbol);
//            return null;
        }
        //return null;
        return super.visitFunctionDecl(ctx);
    }

    @Override
    public Object visitFunctionBody(FunctionBodyContext ctx) {
        Scope scope = new Scope();
        currentScope.setSubScope(scope);


        return super.visitFunctionBody(ctx);
    }

    @Override
    public Object visitReturnStatement(ReturnStatementContext ctx) {
        if(ctx.expressionStatement().singleExpression()!=null){
            return this.visitSingleExpression(ctx.expressionStatement().singleExpression());
        }

        return super.visitReturnStatement(ctx);
    }

    @Override
    public Object visitFunctionCall(FunctionCallContext ctx) {
        String functionName = null;

        if(ctx.parameterList() != null){
            this.visitParameterList(ctx.parameterList());
        }

        if(ctx.sysfunction() != null){
            functionName = ctx.sysfunction().getText();
//            System.out.println(functionName);
            List<Object> all = ctx.parameterList().singleExpression().stream().map(it->{
                return this.visitSingleExpression(it);
                                                                        }).collect(Collectors.toList());
            return Funcall.exec(functionName,all);
        }

        if(ctx.identifier() != null){
            functionName = ctx.identifier().getText();

        }


        SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);
        if(currentSymbol != null){
//            System.out.println("call function ==> "+currentSymbol.getName());
            StackFrane stackFrane = new StackFrane(currentSymbol,functionName);
            return currentSymbol.getValue();
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
