grammar MyLang;

prog:   ( functionDecl |  functionCall )* ;
functionDecl: 'function' identifier '(' ')' functionBody;
functionBody: '{' functionCall* '}';
functionCall: identifier '(' parameterList? ')';
parameterList: STRING_LITERAL(',' STRING_LITERAL)*;
identifier: ID;

STRING_LITERAL: '"'[a-zA-Z0-9!@#$% "]*'"';
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
