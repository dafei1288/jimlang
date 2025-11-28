// Generated from com/dafei1288/jimlang/parser/JimLang.g4 by ANTLR 4.13.1
package com.dafei1288.jimlang.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class JimLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, VAR=6, FUNCTION=7, RETURN=8, NEW=9, 
		IF=10, ELSE=11, WHILE=12, FOR=13, BREAK=14, CONTINUE=15, BOOLEAN_LITERAL=16, 
		STRING_LITERAL=17, NUMBER_LITERAL=18, ID=19, WS=20, COMMENT=21, LINE_COMMENT=22, 
		FAC=23, POW=24, MUL=25, DIV=26, ADD=27, SUB=28, DOT=29, LT=30, GT=31, 
		LE=32, GE=33, EQ=34, NE=35, AND=36, OR=37, B_OPEN=38, B_CLOSE=39, S_OPEN=40, 
		S_CLOSE=41, C_OPEN=42, C_CLOSE=43, COMMA=44, SEMI=45, COLON=46, ASSIGN=47;
	public static final int
		RULE_prog = 0, RULE_statementList = 1, RULE_assignment = 2, RULE_assignmentStatement = 3, 
		RULE_lvalue = 4, RULE_returnStatement = 5, RULE_ifStatement = 6, RULE_whileStatement = 7, 
		RULE_forStatement = 8, RULE_forInit = 9, RULE_forCondition = 10, RULE_forUpdate = 11, 
		RULE_breakStatement = 12, RULE_continueStatement = 13, RULE_block = 14, 
		RULE_expression = 15, RULE_singleExpression = 16, RULE_variableDecl = 17, 
		RULE_typeAnnotation = 18, RULE_functionDecl = 19, RULE_functionBody = 20, 
		RULE_functionCall = 21, RULE_expressionStatement = 22, RULE_primary = 23, 
		RULE_atom = 24, RULE_accessor = 25, RULE_arrayLiteral = 26, RULE_objectLiteral = 27, 
		RULE_pair = 28, RULE_constVar = 29, RULE_binOP = 30, RULE_parameterList = 31, 
		RULE_identifier = 32, RULE_sysfunction = 33, RULE_typeName = 34;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "statementList", "assignment", "assignmentStatement", "lvalue", 
			"returnStatement", "ifStatement", "whileStatement", "forStatement", "forInit", 
			"forCondition", "forUpdate", "breakStatement", "continueStatement", "block", 
			"expression", "singleExpression", "variableDecl", "typeAnnotation", "functionDecl", 
			"functionBody", "functionCall", "expressionStatement", "primary", "atom", 
			"accessor", "arrayLiteral", "objectLiteral", "pair", "constVar", "binOP", 
			"parameterList", "identifier", "sysfunction", "typeName"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'print'", "'println'", "'string'", "'number'", "'boolean'", "'var'", 
			"'function'", "'return'", "'new'", "'if'", "'else'", "'while'", "'for'", 
			"'break'", "'continue'", null, null, null, null, null, null, null, "'!'", 
			"'^'", "'*'", "'/'", "'+'", "'-'", "'.'", "'<'", "'>'", "'<='", "'>='", 
			"'=='", "'!='", "'&&'", "'||'", "'('", "')'", "'['", "']'", "'{'", "'}'", 
			"','", "';'", "':'", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "VAR", "FUNCTION", "RETURN", "NEW", 
			"IF", "ELSE", "WHILE", "FOR", "BREAK", "CONTINUE", "BOOLEAN_LITERAL", 
			"STRING_LITERAL", "NUMBER_LITERAL", "ID", "WS", "COMMENT", "LINE_COMMENT", 
			"FAC", "POW", "MUL", "DIV", "ADD", "SUB", "DOT", "LT", "GT", "LE", "GE", 
			"EQ", "NE", "AND", "OR", "B_OPEN", "B_CLOSE", "S_OPEN", "S_CLOSE", "C_OPEN", 
			"C_CLOSE", "COMMA", "SEMI", "COLON", "ASSIGN"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "JimLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JimLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(JimLangParser.EOF, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(70);
				statementList();
				}
				break;
			}
			setState(73);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementListContext extends ParserRuleContext {
		public List<VariableDeclContext> variableDecl() {
			return getRuleContexts(VariableDeclContext.class);
		}
		public VariableDeclContext variableDecl(int i) {
			return getRuleContext(VariableDeclContext.class,i);
		}
		public List<FunctionDeclContext> functionDecl() {
			return getRuleContexts(FunctionDeclContext.class);
		}
		public FunctionDeclContext functionDecl(int i) {
			return getRuleContext(FunctionDeclContext.class,i);
		}
		public List<FunctionCallContext> functionCall() {
			return getRuleContexts(FunctionCallContext.class);
		}
		public FunctionCallContext functionCall(int i) {
			return getRuleContext(FunctionCallContext.class,i);
		}
		public List<ExpressionStatementContext> expressionStatement() {
			return getRuleContexts(ExpressionStatementContext.class);
		}
		public ExpressionStatementContext expressionStatement(int i) {
			return getRuleContext(ExpressionStatementContext.class,i);
		}
		public List<IfStatementContext> ifStatement() {
			return getRuleContexts(IfStatementContext.class);
		}
		public IfStatementContext ifStatement(int i) {
			return getRuleContext(IfStatementContext.class,i);
		}
		public List<WhileStatementContext> whileStatement() {
			return getRuleContexts(WhileStatementContext.class);
		}
		public WhileStatementContext whileStatement(int i) {
			return getRuleContext(WhileStatementContext.class,i);
		}
		public List<ForStatementContext> forStatement() {
			return getRuleContexts(ForStatementContext.class);
		}
		public ForStatementContext forStatement(int i) {
			return getRuleContext(ForStatementContext.class,i);
		}
		public List<AssignmentStatementContext> assignmentStatement() {
			return getRuleContexts(AssignmentStatementContext.class);
		}
		public AssignmentStatementContext assignmentStatement(int i) {
			return getRuleContext(AssignmentStatementContext.class,i);
		}
		public List<BreakStatementContext> breakStatement() {
			return getRuleContexts(BreakStatementContext.class);
		}
		public BreakStatementContext breakStatement(int i) {
			return getRuleContext(BreakStatementContext.class,i);
		}
		public List<ContinueStatementContext> continueStatement() {
			return getRuleContexts(ContinueStatementContext.class);
		}
		public ContinueStatementContext continueStatement(int i) {
			return getRuleContext(ContinueStatementContext.class,i);
		}
		public StatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitStatementList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statementList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5772437091526L) != 0)) {
				{
				setState(85);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(75);
					variableDecl();
					}
					break;
				case 2:
					{
					setState(76);
					functionDecl();
					}
					break;
				case 3:
					{
					setState(77);
					functionCall();
					}
					break;
				case 4:
					{
					setState(78);
					expressionStatement();
					}
					break;
				case 5:
					{
					setState(79);
					ifStatement();
					}
					break;
				case 6:
					{
					setState(80);
					whileStatement();
					}
					break;
				case 7:
					{
					setState(81);
					forStatement();
					}
					break;
				case 8:
					{
					setState(82);
					assignmentStatement();
					}
					break;
				case 9:
					{
					setState(83);
					breakStatement();
					}
					break;
				case 10:
					{
					setState(84);
					continueStatement();
					}
					break;
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(JimLangParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(ASSIGN);
			setState(91);
			expression();
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(92);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentStatementContext extends ParserRuleContext {
		public LvalueContext lvalue() {
			return getRuleContext(LvalueContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(JimLangParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public AssignmentStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitAssignmentStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentStatementContext assignmentStatement() throws RecognitionException {
		AssignmentStatementContext _localctx = new AssignmentStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_assignmentStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			lvalue();
			setState(96);
			match(ASSIGN);
			setState(97);
			expression();
			setState(99);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(98);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LvalueContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public List<AccessorContext> accessor() {
			return getRuleContexts(AccessorContext.class);
		}
		public AccessorContext accessor(int i) {
			return getRuleContext(AccessorContext.class,i);
		}
		public LvalueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvalue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitLvalue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalueContext lvalue() throws RecognitionException {
		LvalueContext _localctx = new LvalueContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_lvalue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			identifier();
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT || _la==S_OPEN) {
				{
				{
				setState(102);
				accessor();
				}
				}
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(JimLangParser.RETURN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(RETURN);
			setState(109);
			expression();
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(110);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfStatementContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(JimLangParser.IF, 0); }
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(JimLangParser.ELSE, 0); }
		public IfStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStatementContext ifStatement() throws RecognitionException {
		IfStatementContext _localctx = new IfStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_ifStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(IF);
			setState(114);
			match(B_OPEN);
			setState(115);
			expression();
			setState(116);
			match(B_CLOSE);
			setState(117);
			block();
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(118);
				match(ELSE);
				setState(119);
				block();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileStatementContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(JimLangParser.WHILE, 0); }
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public WhileStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitWhileStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileStatementContext whileStatement() throws RecognitionException {
		WhileStatementContext _localctx = new WhileStatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_whileStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(WHILE);
			setState(123);
			match(B_OPEN);
			setState(124);
			expression();
			setState(125);
			match(B_CLOSE);
			setState(126);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForStatementContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(JimLangParser.FOR, 0); }
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public List<TerminalNode> SEMI() { return getTokens(JimLangParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(JimLangParser.SEMI, i);
		}
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ForInitContext forInit() {
			return getRuleContext(ForInitContext.class,0);
		}
		public ForConditionContext forCondition() {
			return getRuleContext(ForConditionContext.class,0);
		}
		public ForUpdateContext forUpdate() {
			return getRuleContext(ForUpdateContext.class,0);
		}
		public ForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitForStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForStatementContext forStatement() throws RecognitionException {
		ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_forStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(FOR);
			setState(129);
			match(B_OPEN);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==VAR || _la==ID) {
				{
				setState(130);
				forInit();
				}
			}

			setState(133);
			match(SEMI);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5772437028870L) != 0)) {
				{
				setState(134);
				forCondition();
				}
			}

			setState(137);
			match(SEMI);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(138);
				forUpdate();
				}
			}

			setState(141);
			match(B_CLOSE);
			setState(142);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForInitContext extends ParserRuleContext {
		public VariableDeclContext variableDecl() {
			return getRuleContext(VariableDeclContext.class,0);
		}
		public AssignmentStatementContext assignmentStatement() {
			return getRuleContext(AssignmentStatementContext.class,0);
		}
		public ForInitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forInit; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitForInit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForInitContext forInit() throws RecognitionException {
		ForInitContext _localctx = new ForInitContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_forInit);
		try {
			setState(146);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				variableDecl();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(145);
				assignmentStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForConditionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ForConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forCondition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitForCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForConditionContext forCondition() throws RecognitionException {
		ForConditionContext _localctx = new ForConditionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_forCondition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForUpdateContext extends ParserRuleContext {
		public AssignmentStatementContext assignmentStatement() {
			return getRuleContext(AssignmentStatementContext.class,0);
		}
		public ForUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forUpdate; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitForUpdate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForUpdateContext forUpdate() throws RecognitionException {
		ForUpdateContext _localctx = new ForUpdateContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_forUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			assignmentStatement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(JimLangParser.BREAK, 0); }
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_breakStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			match(BREAK);
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(153);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(JimLangParser.CONTINUE, 0); }
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_continueStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			match(CONTINUE);
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(157);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public TerminalNode C_OPEN() { return getToken(JimLangParser.C_OPEN, 0); }
		public TerminalNode C_CLOSE() { return getToken(JimLangParser.C_CLOSE, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(C_OPEN);
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(161);
				statementList();
				}
				break;
			}
			setState(164);
			match(C_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public SingleExpressionContext singleExpression() {
			return getRuleContext(SingleExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			singleExpression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SingleExpressionContext extends ParserRuleContext {
		public List<PrimaryContext> primary() {
			return getRuleContexts(PrimaryContext.class);
		}
		public PrimaryContext primary(int i) {
			return getRuleContext(PrimaryContext.class,i);
		}
		public List<BinOPContext> binOP() {
			return getRuleContexts(BinOPContext.class);
		}
		public BinOPContext binOP(int i) {
			return getRuleContext(BinOPContext.class,i);
		}
		public SingleExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleExpression; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitSingleExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleExpressionContext singleExpression() throws RecognitionException {
		SingleExpressionContext _localctx = new SingleExpressionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_singleExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			primary();
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68149051392L) != 0)) {
				{
				{
				setState(169);
				binOP();
				setState(170);
				primary();
				}
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(JimLangParser.VAR, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public VariableDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitVariableDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclContext variableDecl() throws RecognitionException {
		VariableDeclContext _localctx = new VariableDeclContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_variableDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			match(VAR);
			setState(178);
			identifier();
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(179);
				typeAnnotation();
				}
			}

			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ASSIGN) {
				{
				{
				setState(182);
				assignment();
				}
				}
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeAnnotationContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(JimLangParser.COLON, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TypeAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAnnotation; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitTypeAnnotation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeAnnotationContext typeAnnotation() throws RecognitionException {
		TypeAnnotationContext _localctx = new TypeAnnotationContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_typeAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(COLON);
			setState(189);
			typeName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionDeclContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(JimLangParser.FUNCTION, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public FunctionBodyContext functionBody() {
			return getRuleContext(FunctionBodyContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public FunctionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDecl; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitFunctionDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclContext functionDecl() throws RecognitionException {
		FunctionDeclContext _localctx = new FunctionDeclContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_functionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(FUNCTION);
			setState(192);
			identifier();
			setState(193);
			match(B_OPEN);
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5772437028870L) != 0)) {
				{
				setState(194);
				parameterList();
				}
			}

			setState(197);
			match(B_CLOSE);
			setState(198);
			functionBody();
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(199);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionBodyContext extends ParserRuleContext {
		public TerminalNode C_OPEN() { return getToken(JimLangParser.C_OPEN, 0); }
		public TerminalNode C_CLOSE() { return getToken(JimLangParser.C_CLOSE, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public FunctionBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionBody; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitFunctionBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionBodyContext functionBody() throws RecognitionException {
		FunctionBodyContext _localctx = new FunctionBodyContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_functionBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(C_OPEN);
			setState(204);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(203);
				statementList();
				}
				break;
			}
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETURN) {
				{
				setState(206);
				returnStatement();
				}
			}

			setState(209);
			match(C_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallContext extends ParserRuleContext {
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public SysfunctionContext sysfunction() {
			return getRuleContext(SysfunctionContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
				{
				setState(211);
				sysfunction();
				}
				break;
			case ID:
				{
				setState(212);
				identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(215);
			match(B_OPEN);
			setState(217);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5772437028870L) != 0)) {
				{
				setState(216);
				parameterList();
				}
			}

			setState(219);
			match(B_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionStatementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JimLangParser.SEMI, 0); }
		public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionStatement; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitExpressionStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionStatementContext expressionStatement() throws RecognitionException {
		ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_expressionStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			expression();
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(222);
				match(SEMI);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends ParserRuleContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<AccessorContext> accessor() {
			return getRuleContexts(AccessorContext.class);
		}
		public AccessorContext accessor(int i) {
			return getRuleContext(AccessorContext.class,i);
		}
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			atom();
			setState(229);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(226);
					accessor();
					}
					} 
				}
				setState(231);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ConstVarContext constVar() {
			return getRuleContext(ConstVarContext.class,0);
		}
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ObjectLiteralContext objectLiteral() {
			return getRuleContext(ObjectLiteralContext.class,0);
		}
		public TerminalNode B_OPEN() { return getToken(JimLangParser.B_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode B_CLOSE() { return getToken(JimLangParser.B_CLOSE, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_atom);
		try {
			setState(241);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(232);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(233);
				constVar();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(234);
				functionCall();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(235);
				arrayLiteral();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(236);
				objectLiteral();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(237);
				match(B_OPEN);
				setState(238);
				expression();
				setState(239);
				match(B_CLOSE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AccessorContext extends ParserRuleContext {
		public TerminalNode S_OPEN() { return getToken(JimLangParser.S_OPEN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode S_CLOSE() { return getToken(JimLangParser.S_CLOSE, 0); }
		public TerminalNode DOT() { return getToken(JimLangParser.DOT, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public AccessorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_accessor; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitAccessor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AccessorContext accessor() throws RecognitionException {
		AccessorContext _localctx = new AccessorContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_accessor);
		try {
			setState(249);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case S_OPEN:
				enterOuterAlt(_localctx, 1);
				{
				setState(243);
				match(S_OPEN);
				setState(244);
				expression();
				setState(245);
				match(S_CLOSE);
				}
				break;
			case DOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
				match(DOT);
				setState(248);
				identifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralContext extends ParserRuleContext {
		public TerminalNode S_OPEN() { return getToken(JimLangParser.S_OPEN, 0); }
		public TerminalNode S_CLOSE() { return getToken(JimLangParser.S_CLOSE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JimLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JimLangParser.COMMA, i);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitArrayLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(S_OPEN);
			setState(260);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 5772437028870L) != 0)) {
				{
				setState(252);
				expression();
				setState(257);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(253);
					match(COMMA);
					setState(254);
					expression();
					}
					}
					setState(259);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(262);
			match(S_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectLiteralContext extends ParserRuleContext {
		public TerminalNode C_OPEN() { return getToken(JimLangParser.C_OPEN, 0); }
		public TerminalNode C_CLOSE() { return getToken(JimLangParser.C_CLOSE, 0); }
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JimLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JimLangParser.COMMA, i);
		}
		public ObjectLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectLiteral; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitObjectLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectLiteralContext objectLiteral() throws RecognitionException {
		ObjectLiteralContext _localctx = new ObjectLiteralContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_objectLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			match(C_OPEN);
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(265);
				pair();
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(266);
					match(COMMA);
					setState(267);
					pair();
					}
					}
					setState(272);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(275);
			match(C_CLOSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PairContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode COLON() { return getToken(JimLangParser.COLON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			identifier();
			setState(278);
			match(COLON);
			setState(279);
			expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstVarContext extends ParserRuleContext {
		public TerminalNode NUMBER_LITERAL() { return getToken(JimLangParser.NUMBER_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(JimLangParser.STRING_LITERAL, 0); }
		public TerminalNode BOOLEAN_LITERAL() { return getToken(JimLangParser.BOOLEAN_LITERAL, 0); }
		public ConstVarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constVar; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitConstVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstVarContext constVar() throws RecognitionException {
		ConstVarContext _localctx = new ConstVarContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_constVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 458752L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BinOPContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(JimLangParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(JimLangParser.SUB, 0); }
		public TerminalNode MUL() { return getToken(JimLangParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(JimLangParser.DIV, 0); }
		public TerminalNode GT() { return getToken(JimLangParser.GT, 0); }
		public TerminalNode GE() { return getToken(JimLangParser.GE, 0); }
		public TerminalNode LT() { return getToken(JimLangParser.LT, 0); }
		public TerminalNode LE() { return getToken(JimLangParser.LE, 0); }
		public TerminalNode EQ() { return getToken(JimLangParser.EQ, 0); }
		public TerminalNode NE() { return getToken(JimLangParser.NE, 0); }
		public BinOPContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binOP; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitBinOP(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinOPContext binOP() throws RecognitionException {
		BinOPContext _localctx = new BinOPContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_binOP);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68149051392L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParameterListContext extends ParserRuleContext {
		public List<SingleExpressionContext> singleExpression() {
			return getRuleContexts(SingleExpressionContext.class);
		}
		public SingleExpressionContext singleExpression(int i) {
			return getRuleContext(SingleExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JimLangParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JimLangParser.COMMA, i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_parameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			singleExpression();
			setState(290);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(286);
				match(COMMA);
				setState(287);
				singleExpression();
				}
				}
				setState(292);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(JimLangParser.ID, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(293);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SysfunctionContext extends ParserRuleContext {
		public SysfunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sysfunction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitSysfunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SysfunctionContext sysfunction() throws RecognitionException {
		SysfunctionContext _localctx = new SysfunctionContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_sysfunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			_la = _input.LA(1);
			if ( !(_la==T__0 || _la==T__1) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeNameContext extends ParserRuleContext {
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JimLangVisitor ) return ((JimLangVisitor<? extends T>)visitor).visitTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_typeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 56L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001/\u012c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0001"+
		"\u0000\u0003\u0000H\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0005\u0001V\b\u0001\n\u0001\f\u0001Y\t"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002^\b\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003d\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0005\u0004h\b\u0004\n\u0004\f\u0004k\t\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0003\u0005p\b\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003"+
		"\u0006y\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0003\b\u0084\b\b\u0001\b\u0001"+
		"\b\u0003\b\u0088\b\b\u0001\b\u0001\b\u0003\b\u008c\b\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0003\t\u0093\b\t\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0003\f\u009b\b\f\u0001\r\u0001\r\u0003\r"+
		"\u009f\b\r\u0001\u000e\u0001\u000e\u0003\u000e\u00a3\b\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0005\u0010\u00ad\b\u0010\n\u0010\f\u0010\u00b0\t\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00b5\b\u0011\u0001\u0011\u0005"+
		"\u0011\u00b8\b\u0011\n\u0011\f\u0011\u00bb\t\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013"+
		"\u00c4\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00c9\b"+
		"\u0013\u0001\u0014\u0001\u0014\u0003\u0014\u00cd\b\u0014\u0001\u0014\u0003"+
		"\u0014\u00d0\b\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0003"+
		"\u0015\u00d6\b\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00da\b\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0003\u0016\u00e0\b\u0016"+
		"\u0001\u0017\u0001\u0017\u0005\u0017\u00e4\b\u0017\n\u0017\f\u0017\u00e7"+
		"\t\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u00f2\b\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003"+
		"\u0019\u00fa\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0005"+
		"\u001a\u0100\b\u001a\n\u001a\f\u001a\u0103\t\u001a\u0003\u001a\u0105\b"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0005\u001b\u010d\b\u001b\n\u001b\f\u001b\u0110\t\u001b\u0003\u001b"+
		"\u0112\b\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001f"+
		"\u0001\u001f\u0001\u001f\u0005\u001f\u0121\b\u001f\n\u001f\f\u001f\u0124"+
		"\t\u001f\u0001 \u0001 \u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0000\u0000"+
		"#\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468:<>@BD\u0000\u0004\u0001\u0000\u0010\u0012\u0002"+
		"\u0000\u0019\u001c\u001e#\u0001\u0000\u0001\u0002\u0001\u0000\u0003\u0005"+
		"\u0135\u0000G\u0001\u0000\u0000\u0000\u0002W\u0001\u0000\u0000\u0000\u0004"+
		"Z\u0001\u0000\u0000\u0000\u0006_\u0001\u0000\u0000\u0000\be\u0001\u0000"+
		"\u0000\u0000\nl\u0001\u0000\u0000\u0000\fq\u0001\u0000\u0000\u0000\u000e"+
		"z\u0001\u0000\u0000\u0000\u0010\u0080\u0001\u0000\u0000\u0000\u0012\u0092"+
		"\u0001\u0000\u0000\u0000\u0014\u0094\u0001\u0000\u0000\u0000\u0016\u0096"+
		"\u0001\u0000\u0000\u0000\u0018\u0098\u0001\u0000\u0000\u0000\u001a\u009c"+
		"\u0001\u0000\u0000\u0000\u001c\u00a0\u0001\u0000\u0000\u0000\u001e\u00a6"+
		"\u0001\u0000\u0000\u0000 \u00a8\u0001\u0000\u0000\u0000\"\u00b1\u0001"+
		"\u0000\u0000\u0000$\u00bc\u0001\u0000\u0000\u0000&\u00bf\u0001\u0000\u0000"+
		"\u0000(\u00ca\u0001\u0000\u0000\u0000*\u00d5\u0001\u0000\u0000\u0000,"+
		"\u00dd\u0001\u0000\u0000\u0000.\u00e1\u0001\u0000\u0000\u00000\u00f1\u0001"+
		"\u0000\u0000\u00002\u00f9\u0001\u0000\u0000\u00004\u00fb\u0001\u0000\u0000"+
		"\u00006\u0108\u0001\u0000\u0000\u00008\u0115\u0001\u0000\u0000\u0000:"+
		"\u0119\u0001\u0000\u0000\u0000<\u011b\u0001\u0000\u0000\u0000>\u011d\u0001"+
		"\u0000\u0000\u0000@\u0125\u0001\u0000\u0000\u0000B\u0127\u0001\u0000\u0000"+
		"\u0000D\u0129\u0001\u0000\u0000\u0000FH\u0003\u0002\u0001\u0000GF\u0001"+
		"\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000"+
		"IJ\u0005\u0000\u0000\u0001J\u0001\u0001\u0000\u0000\u0000KV\u0003\"\u0011"+
		"\u0000LV\u0003&\u0013\u0000MV\u0003*\u0015\u0000NV\u0003,\u0016\u0000"+
		"OV\u0003\f\u0006\u0000PV\u0003\u000e\u0007\u0000QV\u0003\u0010\b\u0000"+
		"RV\u0003\u0006\u0003\u0000SV\u0003\u0018\f\u0000TV\u0003\u001a\r\u0000"+
		"UK\u0001\u0000\u0000\u0000UL\u0001\u0000\u0000\u0000UM\u0001\u0000\u0000"+
		"\u0000UN\u0001\u0000\u0000\u0000UO\u0001\u0000\u0000\u0000UP\u0001\u0000"+
		"\u0000\u0000UQ\u0001\u0000\u0000\u0000UR\u0001\u0000\u0000\u0000US\u0001"+
		"\u0000\u0000\u0000UT\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000"+
		"WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\u0003\u0001\u0000"+
		"\u0000\u0000YW\u0001\u0000\u0000\u0000Z[\u0005/\u0000\u0000[]\u0003\u001e"+
		"\u000f\u0000\\^\u0005-\u0000\u0000]\\\u0001\u0000\u0000\u0000]^\u0001"+
		"\u0000\u0000\u0000^\u0005\u0001\u0000\u0000\u0000_`\u0003\b\u0004\u0000"+
		"`a\u0005/\u0000\u0000ac\u0003\u001e\u000f\u0000bd\u0005-\u0000\u0000c"+
		"b\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000d\u0007\u0001\u0000"+
		"\u0000\u0000ei\u0003@ \u0000fh\u00032\u0019\u0000gf\u0001\u0000\u0000"+
		"\u0000hk\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000"+
		"\u0000\u0000j\t\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lm\u0005"+
		"\b\u0000\u0000mo\u0003\u001e\u000f\u0000np\u0005-\u0000\u0000on\u0001"+
		"\u0000\u0000\u0000op\u0001\u0000\u0000\u0000p\u000b\u0001\u0000\u0000"+
		"\u0000qr\u0005\n\u0000\u0000rs\u0005&\u0000\u0000st\u0003\u001e\u000f"+
		"\u0000tu\u0005\'\u0000\u0000ux\u0003\u001c\u000e\u0000vw\u0005\u000b\u0000"+
		"\u0000wy\u0003\u001c\u000e\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000"+
		"\u0000\u0000y\r\u0001\u0000\u0000\u0000z{\u0005\f\u0000\u0000{|\u0005"+
		"&\u0000\u0000|}\u0003\u001e\u000f\u0000}~\u0005\'\u0000\u0000~\u007f\u0003"+
		"\u001c\u000e\u0000\u007f\u000f\u0001\u0000\u0000\u0000\u0080\u0081\u0005"+
		"\r\u0000\u0000\u0081\u0083\u0005&\u0000\u0000\u0082\u0084\u0003\u0012"+
		"\t\u0000\u0083\u0082\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000"+
		"\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0087\u0005-\u0000\u0000"+
		"\u0086\u0088\u0003\u0014\n\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0087"+
		"\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089"+
		"\u008b\u0005-\u0000\u0000\u008a\u008c\u0003\u0016\u000b\u0000\u008b\u008a"+
		"\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008d"+
		"\u0001\u0000\u0000\u0000\u008d\u008e\u0005\'\u0000\u0000\u008e\u008f\u0003"+
		"\u001c\u000e\u0000\u008f\u0011\u0001\u0000\u0000\u0000\u0090\u0093\u0003"+
		"\"\u0011\u0000\u0091\u0093\u0003\u0006\u0003\u0000\u0092\u0090\u0001\u0000"+
		"\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u0013\u0001\u0000"+
		"\u0000\u0000\u0094\u0095\u0003\u001e\u000f\u0000\u0095\u0015\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0003\u0006\u0003\u0000\u0097\u0017\u0001\u0000"+
		"\u0000\u0000\u0098\u009a\u0005\u000e\u0000\u0000\u0099\u009b\u0005-\u0000"+
		"\u0000\u009a\u0099\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000"+
		"\u0000\u009b\u0019\u0001\u0000\u0000\u0000\u009c\u009e\u0005\u000f\u0000"+
		"\u0000\u009d\u009f\u0005-\u0000\u0000\u009e\u009d\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u001b\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a2\u0005*\u0000\u0000\u00a1\u00a3\u0003\u0002\u0001\u0000\u00a2"+
		"\u00a1\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3"+
		"\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005+\u0000\u0000\u00a5\u001d"+
		"\u0001\u0000\u0000\u0000\u00a6\u00a7\u0003 \u0010\u0000\u00a7\u001f\u0001"+
		"\u0000\u0000\u0000\u00a8\u00ae\u0003.\u0017\u0000\u00a9\u00aa\u0003<\u001e"+
		"\u0000\u00aa\u00ab\u0003.\u0017\u0000\u00ab\u00ad\u0001\u0000\u0000\u0000"+
		"\u00ac\u00a9\u0001\u0000\u0000\u0000\u00ad\u00b0\u0001\u0000\u0000\u0000"+
		"\u00ae\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000"+
		"\u00af!\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b1"+
		"\u00b2\u0005\u0006\u0000\u0000\u00b2\u00b4\u0003@ \u0000\u00b3\u00b5\u0003"+
		"$\u0012\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000"+
		"\u0000\u0000\u00b5\u00b9\u0001\u0000\u0000\u0000\u00b6\u00b8\u0003\u0004"+
		"\u0002\u0000\u00b7\u00b6\u0001\u0000\u0000\u0000\u00b8\u00bb\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001\u0000"+
		"\u0000\u0000\u00ba#\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000"+
		"\u0000\u00bc\u00bd\u0005.\u0000\u0000\u00bd\u00be\u0003D\"\u0000\u00be"+
		"%\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005\u0007\u0000\u0000\u00c0\u00c1"+
		"\u0003@ \u0000\u00c1\u00c3\u0005&\u0000\u0000\u00c2\u00c4\u0003>\u001f"+
		"\u0000\u00c3\u00c2\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000"+
		"\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c6\u0005\'\u0000\u0000"+
		"\u00c6\u00c8\u0003(\u0014\u0000\u00c7\u00c9\u0005-\u0000\u0000\u00c8\u00c7"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\'\u0001"+
		"\u0000\u0000\u0000\u00ca\u00cc\u0005*\u0000\u0000\u00cb\u00cd\u0003\u0002"+
		"\u0001\u0000\u00cc\u00cb\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cd\u00cf\u0001\u0000\u0000\u0000\u00ce\u00d0\u0003\n\u0005"+
		"\u0000\u00cf\u00ce\u0001\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000"+
		"\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2\u0005+\u0000\u0000"+
		"\u00d2)\u0001\u0000\u0000\u0000\u00d3\u00d6\u0003B!\u0000\u00d4\u00d6"+
		"\u0003@ \u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d4\u0001\u0000"+
		"\u0000\u0000\u00d6\u00d7\u0001\u0000\u0000\u0000\u00d7\u00d9\u0005&\u0000"+
		"\u0000\u00d8\u00da\u0003>\u001f\u0000\u00d9\u00d8\u0001\u0000\u0000\u0000"+
		"\u00d9\u00da\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000"+
		"\u00db\u00dc\u0005\'\u0000\u0000\u00dc+\u0001\u0000\u0000\u0000\u00dd"+
		"\u00df\u0003\u001e\u000f\u0000\u00de\u00e0\u0005-\u0000\u0000\u00df\u00de"+
		"\u0001\u0000\u0000\u0000\u00df\u00e0\u0001\u0000\u0000\u0000\u00e0-\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e5\u00030\u0018\u0000\u00e2\u00e4\u00032\u0019"+
		"\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e4\u00e7\u0001\u0000\u0000"+
		"\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001\u0000\u0000"+
		"\u0000\u00e6/\u0001\u0000\u0000\u0000\u00e7\u00e5\u0001\u0000\u0000\u0000"+
		"\u00e8\u00f2\u0003@ \u0000\u00e9\u00f2\u0003:\u001d\u0000\u00ea\u00f2"+
		"\u0003*\u0015\u0000\u00eb\u00f2\u00034\u001a\u0000\u00ec\u00f2\u00036"+
		"\u001b\u0000\u00ed\u00ee\u0005&\u0000\u0000\u00ee\u00ef\u0003\u001e\u000f"+
		"\u0000\u00ef\u00f0\u0005\'\u0000\u0000\u00f0\u00f2\u0001\u0000\u0000\u0000"+
		"\u00f1\u00e8\u0001\u0000\u0000\u0000\u00f1\u00e9\u0001\u0000\u0000\u0000"+
		"\u00f1\u00ea\u0001\u0000\u0000\u0000\u00f1\u00eb\u0001\u0000\u0000\u0000"+
		"\u00f1\u00ec\u0001\u0000\u0000\u0000\u00f1\u00ed\u0001\u0000\u0000\u0000"+
		"\u00f21\u0001\u0000\u0000\u0000\u00f3\u00f4\u0005(\u0000\u0000\u00f4\u00f5"+
		"\u0003\u001e\u000f\u0000\u00f5\u00f6\u0005)\u0000\u0000\u00f6\u00fa\u0001"+
		"\u0000\u0000\u0000\u00f7\u00f8\u0005\u001d\u0000\u0000\u00f8\u00fa\u0003"+
		"@ \u0000\u00f9\u00f3\u0001\u0000\u0000\u0000\u00f9\u00f7\u0001\u0000\u0000"+
		"\u0000\u00fa3\u0001\u0000\u0000\u0000\u00fb\u0104\u0005(\u0000\u0000\u00fc"+
		"\u0101\u0003\u001e\u000f\u0000\u00fd\u00fe\u0005,\u0000\u0000\u00fe\u0100"+
		"\u0003\u001e\u000f\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000\u0100\u0103"+
		"\u0001\u0000\u0000\u0000\u0101\u00ff\u0001\u0000\u0000\u0000\u0101\u0102"+
		"\u0001\u0000\u0000\u0000\u0102\u0105\u0001\u0000\u0000\u0000\u0103\u0101"+
		"\u0001\u0000\u0000\u0000\u0104\u00fc\u0001\u0000\u0000\u0000\u0104\u0105"+
		"\u0001\u0000\u0000\u0000\u0105\u0106\u0001\u0000\u0000\u0000\u0106\u0107"+
		"\u0005)\u0000\u0000\u01075\u0001\u0000\u0000\u0000\u0108\u0111\u0005*"+
		"\u0000\u0000\u0109\u010e\u00038\u001c\u0000\u010a\u010b\u0005,\u0000\u0000"+
		"\u010b\u010d\u00038\u001c\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010d"+
		"\u0110\u0001\u0000\u0000\u0000\u010e\u010c\u0001\u0000\u0000\u0000\u010e"+
		"\u010f\u0001\u0000\u0000\u0000\u010f\u0112\u0001\u0000\u0000\u0000\u0110"+
		"\u010e\u0001\u0000\u0000\u0000\u0111\u0109\u0001\u0000\u0000\u0000\u0111"+
		"\u0112\u0001\u0000\u0000\u0000\u0112\u0113\u0001\u0000\u0000\u0000\u0113"+
		"\u0114\u0005+\u0000\u0000\u01147\u0001\u0000\u0000\u0000\u0115\u0116\u0003"+
		"@ \u0000\u0116\u0117\u0005.\u0000\u0000\u0117\u0118\u0003\u001e\u000f"+
		"\u0000\u01189\u0001\u0000\u0000\u0000\u0119\u011a\u0007\u0000\u0000\u0000"+
		"\u011a;\u0001\u0000\u0000\u0000\u011b\u011c\u0007\u0001\u0000\u0000\u011c"+
		"=\u0001\u0000\u0000\u0000\u011d\u0122\u0003 \u0010\u0000\u011e\u011f\u0005"+
		",\u0000\u0000\u011f\u0121\u0003 \u0010\u0000\u0120\u011e\u0001\u0000\u0000"+
		"\u0000\u0121\u0124\u0001\u0000\u0000\u0000\u0122\u0120\u0001\u0000\u0000"+
		"\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123?\u0001\u0000\u0000\u0000"+
		"\u0124\u0122\u0001\u0000\u0000\u0000\u0125\u0126\u0005\u0013\u0000\u0000"+
		"\u0126A\u0001\u0000\u0000\u0000\u0127\u0128\u0007\u0002\u0000\u0000\u0128"+
		"C\u0001\u0000\u0000\u0000\u0129\u012a\u0007\u0003\u0000\u0000\u012aE\u0001"+
		"\u0000\u0000\u0000!GUW]ciox\u0083\u0087\u008b\u0092\u009a\u009e\u00a2"+
		"\u00ae\u00b4\u00b9\u00c3\u00c8\u00cc\u00cf\u00d5\u00d9\u00df\u00e5\u00f1"+
		"\u00f9\u0101\u0104\u010e\u0111\u0122";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}