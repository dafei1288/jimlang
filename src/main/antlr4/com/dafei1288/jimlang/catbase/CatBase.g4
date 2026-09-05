// CatBase.g4 — ANTLR4 grammar for the CatBase language (Tier-1 dialect).
//
// CatBase (github.com/DoRobotAi/catbase-lang) is a statically typed,
// Python-flavoured language with braces. The official compiler is closed
// source and emits native code via Zig; this grammar is a clean-room,
// runtime-oriented front-end so JimLang can interpret .cat sources
// directly (see com.dafei1288.jimlang.catbase.CatBaseVistor).
//
// Design notes:
//   * Type annotations are parsed but carry no runtime meaning; type words
//     (int/str/list/dict/...) are ordinary IDENTIFIERs here so that
//     int()/str()/bool() keep working as conversion function calls.
//   * Newlines are insignificant (tokens are C/JS-like). Blocks are braces,
//     statements are separated by being well-formed tokens, semicolons are
//     optional. Multi-line list/dict literals therefore "just work".
//   * else-if chains are parsed natively via recursion (no rewriting).
//   * for x in expr iterates a list; range(a,b[,step]) is handled by the
//     interpreter, not by source rewriting.
grammar CatBase;

prog
    : statementList? EOF
    ;

// ------------------------------------------------------------------ statements

statementList
    : statement*
    ;

statement
    : variableDecl SEMI?
    | functionDef
    | ifStatement
    | forStatement
    | whileStatement
    | returnStatement SEMI?
    | breakStatement SEMI?
    | continueStatement SEMI?
    | assignmentStatement SEMI?
    | expressionStatement SEMI?
    | block
    ;

functionDef
    : DEF ID LP paramList? RP (ARROW typeName)? block
    ;

paramList
    : param (COMMA param)*
    ;

param
    : ID (COLON typeName)? (ASSIGN expression)?
    ;

variableDecl
    : ID COLON typeName (ASSIGN expression)?
    ;

assignmentStatement
    : lvalue ASSIGN expression
    ;

lvalue
    : ID accessor*
    ;

ifStatement
    : IF expression block (ELSE (ifStatement | block))?
    ;

forStatement
    : FOR ID IN expression block
    ;

whileStatement
    : WHILE expression block
    ;

returnStatement
    : RETURN expression?
    ;

breakStatement
    : BREAK
    ;

continueStatement
    : CONTINUE
    ;

block
    : LCURLY statementList? RCURLY
    ;

expressionStatement
    : expression
    ;

// ------------------------------------------------------------ expressions

expression
    : booleanOr
    ;

booleanOr
    : booleanAnd (OR booleanAnd)*
    ;

booleanAnd
    : notExpr (AND notExpr)*
    ;

notExpr
    : NOT notExpr
    | comparison
    ;

comparison
    : additive ( (EQ | NE | LT | LE | GT | GE) additive )*
    ;

additive
    : multiplicative ( (ADD | SUB) multiplicative )*
    ;

multiplicative
    : unary ( (MUL | DIV | MOD) unary )*
    ;

unary
    : (ADD | SUB) unary
    | primary
    ;

primary
    : atom (accessor | callSuffix)*
    ;

atom
    : ID
    | NUMBER
    | STRING
    | FSTRING
    | TRUE
    | FALSE
    | NONE
    | arrayLiteral
    | dictLiteral
    | LP expression RP
    ;

callSuffix
    : LP argList? RP
    ;

argList
    : arg (COMMA arg)*
    ;

arg
    : namedArg
    | expression
    ;

namedArg
    : ID ASSIGN expression
    ;

accessor
    : LBRACK expression RBRACK
    | DOT ID
    ;

arrayLiteral
    : LBRACK (expression (COMMA expression)*)? RBRACK
    ;

dictLiteral
    : LCURLY (dictEntry (COMMA dictEntry)*)? RCURLY
    ;

dictEntry
    : (STRING | ID) COLON expression
    ;

// --------------------------------------------------------------- types (advisory)

typeName
    : ID (LBRACK typeName (COMMA typeName)* RBRACK)?
    ;

// ------------------------------------------------------------------- lexer

DEF     : 'def';
IF      : 'if';
ELSE    : 'else';
FOR     : 'for';
IN      : 'in';
WHILE   : 'while';
RETURN  : 'return';
BREAK   : 'break';
CONTINUE: 'continue';
AND     : 'and';
OR      : 'or';
NOT     : 'not';
TRUE    : 'True' | 'true';
FALSE   : 'False' | 'false';
NONE    : 'None' | 'none';

LP    : '(';
RP    : ')';
LCURLY: '{';
RCURLY: '}';
LBRACK: '[';
RBRACK: ']';
COMMA : ',';
COLON : ':';
SEMI  : ';';
DOT   : '.';
ASSIGN: '=';
ARROW : '->';

ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EQ  : '==';
NE  : '!=';
LT  : '<';
LE  : '<=';
GT  : '>';
GE  : '>=';

// f-strings first: f"..." or f'...' (single-quoted content also allowed)
FSTRING
    : 'f' '"' FStringBody '"'
    | 'f' '\'' FStringBody '\''
    ;
fragment FStringBody
    : ( ~["\\\r\n] | EscapeSeq )*            // note: '{'/'}' are plain chars here
    ;

STRING
    : '"' ( ~["\\\r\n] | EscapeSeq )* '"'
    | '\'' ( ~['\\\r\n] | EscapeSeq )* '\''
    ;

fragment EscapeSeq
    : '\\' . ;
fragment Digit : [0-9];

NUMBER
    : Digit+ ('.' Digit+)? 
    | '.' Digit+
    ;

ID
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

// '#' line comments and whitespace (newlines are insignificant)
COMMENT
    : '#' ~[\r\n]* -> channel(HIDDEN)
    ;

WS
    : [ \t\r\n\u000C]+ -> channel(HIDDEN)
    ;
