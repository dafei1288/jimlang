// Generated from /Volumes/arkamu/study/codespace/mylang/src/main/resources/MyLang.g4 by ANTLR 4.9.2
package wang.datahub.mylang.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MyLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MyLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MyLangParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(MyLangParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MyLangParser#functionDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDecl(MyLangParser.FunctionDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MyLangParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(MyLangParser.FunctionBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MyLangParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(MyLangParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MyLangParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(MyLangParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MyLangParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(MyLangParser.IdentifierContext ctx);
}