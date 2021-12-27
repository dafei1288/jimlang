// Generated from /Volumes/arkamu/study/codespace/mylang/src/main/resources/MyLang.g4 by ANTLR 4.9.2
package wang.datahub.mylang;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MyLangParser}.
 */
public interface MyLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MyLangParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(MyLangParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(MyLangParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link MyLangParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(MyLangParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(MyLangParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MyLangParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(MyLangParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(MyLangParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MyLangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(MyLangParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(MyLangParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MyLangParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(MyLangParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(MyLangParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MyLangParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(MyLangParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MyLangParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(MyLangParser.IdentifierContext ctx);
}