grammar LogicPL;


logicPL: (function_def)* main;

main: {System.out.println("MainBody");} MAIN LBRACE body RBRACE  ;

function_def: FUNCTION id=IDENTIFIER {System.out.println("FunctionDec: " + $id.getText());}
    function_args COLON PRIMARY_TYPE LBRACE body RBRACE;

function_args: LPAR (function_arg COMMA)*
    function_arg RPAR | LPAR RPAR;

function_arg: PRIMARY_TYPE arg=IDENTIFIER {System.out.println("ArgumentDec: " + $arg.getText());};

function_call: IDENTIFIER function_call_args; // {System.out.println("FunctionCall");}

function_call_args: LPAR ((IDENTIFIER | expression) COMMA)*
            (IDENTIFIER | expression) RPAR | LPAR RPAR;

body: (statement)+ ;

statement: implication | loop | (declaration | function_call |
     predicate | expression | array_def | print | query_type2 | return) SEMICOLON;

implication: {System.out.println("Implication");} (expression) ARROW LPAR (statement)+ RPAR;

return: {System.out.println("Return");} RETURN (expression)?;

declaration: PRIMARY_TYPE var_name (ASSIGN (expression | IDENTIFIER))?;

var_name: var=IDENTIFIER {System.out.println("VarDec: " + $var.getText());};

predicate: predicate_name = PREDICATE_IDENTIFIER LPAR (predicate_variable) RPAR {System.out.println("Predicate: " + $predicate_name.getText());};

query_type1: LBRACK QUESTION_MARK (predicate) RBRACK;

query_type2: LBRACK predicate_name = PREDICATE_IDENTIFIER LPAR QUESTION_MARK RPAR RBRACK {System.out.println("Predicate: " + $predicate_name.getText());};

loop: FOR LPAR IDENTIFIER COLON IDENTIFIER RPAR LBRACE
      {System.out.println("Loop: for");} (statement)* RBRACE;

print: {System.out.println("Built-in: print");} PRINT LPAR (expression | query_type2) RPAR;

array_def: PRIMARY_TYPE LBRACK INT RBRACK arr_name = IDENTIFIER {System.out.println("VarDec: " + $arr_name.getText());}
            (ASSIGN array_rvalue)?;

array_rvalue: LBRACK ((IDENTIFIER | expression) COMMA)* (IDENTIFIER | expression) RBRACK;

expression: or_expression ASSIGN expression | or_expression;

or_expression: and_expression (op = OR) or_expression {System.out.println("Operator: " + $op.getText());}| and_expression ;

and_expression: equal_expression (op = AND) and_expression {System.out.println("Operator: " + $op.getText());}| equal_expression;

equal_expression: compare_expression ((op = EQUAL | op = NOT_EQUAL) equal_expression) {System.out.println("Operator: " + $op.getText());} | compare_expression;

compare_expression: sum_minus_expression ((op = GREATER_THAN | op = GREATER_THAN_OR_EQUAL | op = LESS_THAN | op = LESS_THAN_OR_EQUAL) compare_expression)
                    {System.out.println("Operator: " + $op.getText());} | sum_minus_expression;

sum_minus_expression: mult_divide_mode_expression ((op = PLUS | op = MINUS) sum_minus_expression) {System.out.println("Operator: " + $op.getText());} | mult_divide_mode_expression;

mult_divide_mode_expression: unary_expression ((op = MULT | op = DIVIDE | op = MODE) mult_divide_mode_expression)  {System.out.println("Operator: " + $op.getText());} | unary_expression;

unary_expression: ((op = PLUS | op = MINUS | op = NOT) unary_expression) {System.out.println("Operator: " + $op.getText());} | array_expression;

array_expression: factor LBRACK expression RBRACK | parantheses_expression;

parantheses_expression: LPAR expression RPAR | base_values;

factor: IDENTIFIER | function_call | parantheses_expression;

base_values: function_call | INT | BOOLEAN | FLOAT | IDENTIFIER | PREDICATE_IDENTIFIER | query_type1;

predicate_variable: IDENTIFIER (LBRACK expression RBRACK)?;

// ********** TOKENs ********** //

// Keywords
MAIN: 'main';
FUNCTION: 'function';
FOR: 'for';
RETURN: 'return';
PRINT: 'print';


// Operators //
SHARP: '#';
LPAR: '(';
RPAR: ')';
LBRACK: '[';
RBRACK: ']';
LBRACE: '{';
RBRACE: '}';
ARROW: '=>';

//UNARY_PLUS: '+';
//UNARY_MINUS: '-';
NOT: '!';

PLUS: '+';
MINUS: '-';
MULT: '*';
DIVIDE: '/';
MODE: '%';
INC: '++';
DEC: '--';
EQUAL: '==';
NOT_EQUAL: '!=';
GREATER_THAN: '>';
LESS_THAN: '<';
GREATER_THAN_OR_EQUAL: '>=';
LESS_THAN_OR_EQUAL: '<=';

AND: '&&';
OR: '||';
ASSIGN: '=';

// SYMBOLS //
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
QUESTION_MARK: '?';

WS: [ \t\r\n]+ -> skip;
COMMENT: '#' ~[\r\n]* -> skip;
Whitespace: [ \t]+ -> skip;

// Variable Types Tokens
PRIMARY_TYPE: 'int' | 'boolean' | 'float';
INT: [1-9][0-9]* | '0';
//POS_INT: [1-9][0-9]*;
BOOLEAN: 'true' | 'false';
FLOAT: INT'.'[0-9][0-9]*;


// Identifiers
IDENTIFIER: ([a-z][a-zA-Z0-9_]*);
PREDICATE_IDENTIFIER: [A-Z] ( [a-zA-Z] | [0-9])*;
// UPPERCASE_CHAR (ALL_CHARS | DIGIT)*;

// Fragments
//fragment
//DIGIT: [0-9];
//
//fragment
//NON_DIGIT: [a-zA-z_];
//
//fragment
//LOWERCASE_CHAR: [a-z];
//
//fragment
//UPPERCASE_CHAR: [A-Z];
//
//fragment
//ALL_CHARS: [a-zA-Z];