grammar MyLang;

prog:  statementList? EOF;

statementList : ( variableDecl | functionDecl |  expressionStatement )* ;

variableDecl : VAR identifier typeAnnotation? ( '=' singleExpression)? ';';
typeAnnotation : ':' typeName;

functionDecl: FUNCTION identifier '(' parameterList ')' functionBody;
functionBody: '{' statementList? '}';
functionCall: identifier '(' parameterList? ')';

expressionStatement: singleExpression ';';
singleExpression: primary (binOP primary)* ;
primary: ID | NUMBER_LITERAL | functionCall | '(' singleExpression ')' ;
binOP : '+'
      | '-'
      | '*'
      | '/'
      | '='
      | '<='
      | '>='
      ;



parameterList: singleExpression(',' singleExpression)*;
identifier: ID;

typeName :  'String'
         |  'Number'
         |  'Boolean'
         ;

VAR: 'var';
FUNCTION: 'function';

STRING_LITERAL: '"'[a-zA-Z0-9!@#$% "]*'"';
NUMBER_LITERAL: [0-9]+(.)?[0-9]?;
ID  :   [a-zA-Z_][a-zA-Z0-9_]*;
WS  :   [ \t\r\n]+ -> skip ;


//FAC :   '!' ;
//POW :   '^' ;
//MUL :   '*' ;
//DIV :   '/' ;
//ADD :   '+' ;
//SUB :   '-' ;
//DOT :   '.' ;
//NUMBER :   [0-9]+ (DOT)? [0-9]*;
//NEWLINE:'\r'? '\n' ;
