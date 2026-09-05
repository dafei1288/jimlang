// Generated from com/dafei1288/jimlang/catbase/CatBase.g4 by ANTLR 4.13.1
package com.dafei1288.jimlang.catbase;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CatBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CatBaseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(CatBaseParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#statementList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementList(CatBaseParser.StatementListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(CatBaseParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#functionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDef(CatBaseParser.FunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(CatBaseParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(CatBaseParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#variableDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDecl(CatBaseParser.VariableDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(CatBaseParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(CatBaseParser.LvalueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(CatBaseParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(CatBaseParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(CatBaseParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(CatBaseParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#breakStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(CatBaseParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#continueStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(CatBaseParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(CatBaseParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#expressionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(CatBaseParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(CatBaseParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#booleanOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanOr(CatBaseParser.BooleanOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#booleanAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAnd(CatBaseParser.BooleanAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#notExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(CatBaseParser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(CatBaseParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#additive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditive(CatBaseParser.AdditiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#multiplicative}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicative(CatBaseParser.MultiplicativeContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(CatBaseParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(CatBaseParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(CatBaseParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#callSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallSuffix(CatBaseParser.CallSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(CatBaseParser.ArgListContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(CatBaseParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#namedArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedArg(CatBaseParser.NamedArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#accessor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccessor(CatBaseParser.AccessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(CatBaseParser.ArrayLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#dictLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictLiteral(CatBaseParser.DictLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#dictEntry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDictEntry(CatBaseParser.DictEntryContext ctx);
	/**
	 * Visit a parse tree produced by {@link CatBaseParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeName(CatBaseParser.TypeNameContext ctx);
}