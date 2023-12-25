grammar JimLang;

//@header {
//package com.dafei1288.jimlang.parser;
//}

prog:  statementList? EOF;

statementList : ( variableDecl | functionDecl | functionCall | expressionStatement  )* ;

assignment: '=' singleExpression ;

returnStatement: RETURN  expressionStatement;

variableDecl : VAR identifier typeAnnotation? ( assignment )* ';';
typeAnnotation : ':' typeName;

functionDecl: FUNCTION identifier '(' parameterList? ')' functionBody ';'? ;
functionBody: '{' statementList?  returnStatement? '}';
functionCall: (sysfunction |  identifier) '(' parameterList? ')';

expressionStatement: singleExpression | singleExpression ';';
singleExpression: primary (binOP primary)* ;
primary: identifier |  constVar |  functionCall | '(' singleExpression ')' ;

constVar :
           NUMBER_LITERAL
         | STRING_LITERAL
         | BOOLEAN_LITERAL
         ;


binOP : '+'
      | '-'
      | '*'
      | '/'
      | '='
      | '<='
      | '>='
      ;

//booleanType: TRUE | FALSE ;

parameterList: singleExpression (',' singleExpression)*;
identifier: ID;

sysfunction : 'print'
            | 'println'
;

typeName :  'string'
         |  'number'
         |  'boolean'
         ;



//TRUE: 'true';
//FALSE: 'false';
VAR                                 : 'var';
FUNCTION                            : 'function';
RETURN                              : 'return';
NEW                                 : 'new';
BOOLEAN_LITERAL                     : 'true' | 'false' ;

STRING_LITERAL: '"'[a-zA-Z0-9!@#$% "]*'"';
NUMBER_LITERAL: [0-9]+(.)?[0-9]?;


ID  :   [a-zA-Z_][a-zA-Z0-9_]*;
//WS  :   [ \t\r\n]+ -> skip ;

WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

//FAC :   '!' ;
//POW :   '^' ;
//MUL :   '*' ;
//DIV :   '/' ;
//ADD :   '+' ;
//SUB :   '-' ;
//DOT :   '.' ;
//NUMBER :   [0-9]+ (DOT)? [0-9]*;
//NEWLINE:'\r'? '\n' ;
