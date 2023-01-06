// Generated from java-escape by ANTLR 4.11.1
package com.dafei1288.jimlang.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JimLangParser}.
 */
public interface JimLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JimLangParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(JimLangParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(JimLangParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(JimLangParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(JimLangParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(JimLangParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(JimLangParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(JimLangParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(JimLangParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void enterVariableDecl(JimLangParser.VariableDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void exitVariableDecl(JimLangParser.VariableDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#typeAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterTypeAnnotation(JimLangParser.TypeAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#typeAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitTypeAnnotation(JimLangParser.TypeAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(JimLangParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(JimLangParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(JimLangParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(JimLangParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(JimLangParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(JimLangParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(JimLangParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(JimLangParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSingleExpression(JimLangParser.SingleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#singleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSingleExpression(JimLangParser.SingleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(JimLangParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(JimLangParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#binOP}.
	 * @param ctx the parse tree
	 */
	void enterBinOP(JimLangParser.BinOPContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#binOP}.
	 * @param ctx the parse tree
	 */
	void exitBinOP(JimLangParser.BinOPContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(JimLangParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(JimLangParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(JimLangParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(JimLangParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#sysfunction}.
	 * @param ctx the parse tree
	 */
	void enterSysfunction(JimLangParser.SysfunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#sysfunction}.
	 * @param ctx the parse tree
	 */
	void exitSysfunction(JimLangParser.SysfunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JimLangParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(JimLangParser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JimLangParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(JimLangParser.TypeNameContext ctx);
}