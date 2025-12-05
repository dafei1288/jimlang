grammar JimLang;

prog:  statementList? EOF;

statementList : (  variableDecl | functionDecl | expressionStatement | ifStatement | whileStatement | forStatement | assignmentStatement | breakStatement | continueStatement  | block )* ;

assignment: '=' expression ';'? ;

// assignment to identifiers, array index, or object property
assignmentStatement: lvalue '=' expression ';'? ;

lvalue: identifier (accessor)* ;

returnStatement: RETURN expression ';'? ;

// control flow
ifStatement: IF '(' expression ')' block (ELSE block)? ;
whileStatement: WHILE '(' expression ')' block ;
forStatement: FOR '(' forInit? ';' forCondition? ';' forUpdate? ')' block ;
forInit: variableDecl | assignmentStatement ;
forCondition: expression ;
forUpdate: assignmentStatement ;

breakStatement: BREAK ';'? ;
continueStatement: CONTINUE ';'? ;

// blocks
block: '{' statementList? '}' ;

// expressions
expression: singleExpression ;
singleExpression: primary (binOP primary)* ;

variableDecl : VAR identifier typeAnnotation? ( assignment )* ;
typeAnnotation : ':' typeName;

functionDecl: FUNCTION identifier B_OPEN parameterList? B_CLOSE functionBody ';'? ;

functionBody: C_OPEN statementList?  returnStatement? C_CLOSE;
functionCall: sysfunction '(' parameterList? ')';

expressionStatement: expression ';'? ;

// primary with chained accessors
primary: atom (accessor | callSuffix)* ;
atom: functionCall
    | sysfunction
    | identifier
    | constVar
    | arrayLiteral
    | objectLiteral
    | '(' expression ')'
    ;
accessor: '[' expression ']' | '.' identifier ;

callSuffix: '(' parameterList? ')';

// literals
arrayLiteral: S_OPEN (expression (COMMA expression)*)? S_CLOSE ;
objectLiteral: C_OPEN (pair (COMMA pair)*)? C_CLOSE ;
pair: identifier COLON expression ;

constVar : NUMBER_LITERAL | STRING_LITERAL | BOOLEAN_LITERAL ;

binOP : ADD | SUB | MUL | DIV | GT | GE | LT | LE | EQ | NE ;

parameterList: singleExpression (',' singleExpression)*;
identifier: ID;

sysfunction : 'print' | 'println' ;

typeName :  'string' |  'number' |  'boolean' | 'int' | 'float' | 'array' | 'object' | qualifiedName ;
qualifiedName: ID (DOT ID)* ;

VAR                                 : 'var';
FUNCTION                            : 'function';
RETURN                              : 'return';
NEW                                 : 'new';
IF                                  : 'if';
ELSE                                : 'else';
WHILE                               : 'while';
FOR                                 : 'for';
BREAK                               : 'break';
CONTINUE                            : 'continue';
BOOLEAN_LITERAL                     : 'true' | 'false' ;

STRING_LITERAL: '"' (~["\r\n])* '"';
NUMBER_LITERAL: [0-9]+ ('.' [0-9]+)?;

ID  :   [a-zA-Z_][a-zA-Z0-9_]*;

WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

FAC :   '!' ;
POW :   '^' ;
MUL :   '*' ;
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
DOT :   '.' ;
LT  :   '<' ;
GT  :   '>' ;
LE  :   '<=' ;
GE  :   '>=' ;
EQ  :   '==' ;
NE  :   '!=' ;
AND :   '&&' ;
OR  :   '||' ;
B_OPEN : '(' ;
B_CLOSE : ')';
S_OPEN : '[' ;
S_CLOSE : ']';
C_OPEN : '{' ;
C_CLOSE : '}';
COMMA : ',';
SEMI : ';';
COLON : ':';
ASSIGN : '=';
