// Generated from com/dafei1288/jimlang/parser/JimLang.g4 by ANTLR 4.13.1
package com.dafei1288.jimlang.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JimLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JimLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JimLangParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(JimLangParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(JimLangParser.StatementListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(JimLangParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(JimLangParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(JimLangParser.LvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(JimLangParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(JimLangParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(JimLangParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(JimLangParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(JimLangParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#forCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForCondition(JimLangParser.ForConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(JimLangParser.ForUpdateContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(JimLangParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(JimLangParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(JimLangParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(JimLangParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#singleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleExpression(JimLangParser.SingleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#variableDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDecl(JimLangParser.VariableDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#typeAnnotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeAnnotation(JimLangParser.TypeAnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#functionDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDecl(JimLangParser.FunctionDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(JimLangParser.FunctionBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(JimLangParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(JimLangParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(JimLangParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(JimLangParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#accessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccessor(JimLangParser.AccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#callSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallSuffix(JimLangParser.CallSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(JimLangParser.ArrayLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#objectLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectLiteral(JimLangParser.ObjectLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(JimLangParser.PairContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#constVar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstVar(JimLangParser.ConstVarContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#binOP}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinOP(JimLangParser.BinOPContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(JimLangParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(JimLangParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#sysfunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSysfunction(JimLangParser.SysfunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeName(JimLangParser.TypeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JimLangParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(JimLangParser.QualifiedNameContext ctx);
}