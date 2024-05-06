grammar LogicPL;

@header{
import ast.node.*;
import ast.node.expression.*;
import ast.node.statement.*;
import ast.node.declaration.*;
import ast.node.expression.values.*;
import ast.node.expression.operators.*;
import ast.type.primitiveType.*;
import ast.type.*;
}

program returns[Program p]:
    {$p = new Program(); $p.setLine(0);}
    (f = functionDec {$p.addFunc($f.functionDeclaration);})*
    main = mainBlock {$p.setMain($main.main) ;}
    ;

functionDec returns[FuncDeclaration functionDeclaration]:
    {ArrayList<ArgDeclaration> args = new ArrayList<>();
     ArrayList<Statement> statements = new ArrayList<>();}
    FUNCTION name = identifier
    LPAR (arg1 = functionVarDec {args.add($arg1.argDeclaration);} (COMMA arg = functionVarDec {args.add($arg.argDeclaration);})*)? RPAR COLON returnType = type
    LBRACE ((stmt = statement {statements.add($stmt.return_statement);})+) RBRACE
    {$functionDeclaration = new FuncDeclaration($name.return_identifier, $returnType.return_type, args, statements); $functionDeclaration.setLine($name.return_identifier.getLine());}
    ;

functionVarDec returns [ArgDeclaration argDeclaration]:
    t = type arg_iden = identifier {$argDeclaration = new ArgDeclaration($arg_iden.return_identifier, $t.return_type); $argDeclaration.setLine($arg_iden.return_identifier.getLine());}
    ;

mainBlock returns [MainDeclaration main]:
    {ArrayList<Statement> mainStmts = new ArrayList<>();}
    m = MAIN LBRACE (s = statement {mainStmts.add($s.return_statement);})+ RBRACE
    {$main = new MainDeclaration(mainStmts); $main.setLine($m.getLine());}
    ;

statement returns [Statement return_statement]:
    s1 = assignSmt {$return_statement = $s1.return_assignment;}| (s2 = predicate SEMICOLON {$return_statement = $s2.return_predicate;})
    | s3 = implication {$return_statement = $s3.return_implication;} | s4 = returnSmt {$return_statement = $s4.return_ret;}
    | s5 = printSmt {$return_statement = $s5.return_print_statement;}| s6 = forLoop {$return_statement = $s6.return_loop;}| s7 = localVarDeclaration {$return_statement = $s7.localreturn_var_dec;}
    ;

assignSmt returns [AssignStmt return_assignment]:
    lv = variable line = ASSIGN rv = expression SEMICOLON
    {$return_assignment = new AssignStmt($lv.v,  $rv.e); $return_assignment.setLine($line.getLine());}
    ;

variable returns [Variable v]:
    i = identifier {$v = $i.return_identifier;} | name = identifier LBRACKET exp = expression RBRACKET {$v = new ArrayAccess($name.return_identifier.getName(), $exp.e); $v.setLine($name.return_identifier.getLine()); }
    ;

localVarDeclaration returns [Statement localreturn_var_dec]:
    s1 = varDeclaration {$localreturn_var_dec = $s1.return_var_dec;}
    | s2 = arrayDeclaration {$localreturn_var_dec = $s2.return_array_dec;}
    ;

varDeclaration returns [VarDecStmt return_var_dec]:
    t = type var_iden = identifier {$return_var_dec = new VarDecStmt($var_iden.return_identifier, $t.return_type); $return_var_dec.setLine($var_iden.return_identifier.getLine());} (ASSIGN e = expression {$return_var_dec.setInitialExpression($e.e);} )? SEMICOLON
    ;

arrayDeclaration returns [ArrayDecStmt return_array_dec]:
    t = type LBRACKET INT_NUMBER RBRACKET var_iden = identifier {$return_array_dec = new ArrayDecStmt($var_iden.return_identifier, $t.return_type, $INT_NUMBER.int); $return_array_dec.setLine($var_iden.return_identifier.getLine());}
    (initial = arrayInitialValue {$return_array_dec.setInitialValues($initial.initialValues);})? SEMICOLON
    ;

arrayInitialValue returns [ArrayList<Expression> initialValues]:
    ASSIGN arrList = arrayList {$initialValues = $arrList.values;}
    ;

arrayList returns [ArrayList<Expression> values]:
    {$values = new ArrayList<Expression>();}
    LBRACKET (v = value {$values.add($v.return_value);}| id = identifier {$values.add($id.return_identifier);}) (COMMA (v2 = value {$values.add($v2.return_value);}| id2 = identifier {$values.add($id.return_identifier);}))* RBRACKET
    ;

printSmt returns [PrintStmt return_print_statement]:
    p = PRINT LPAR arg = printExpr RPAR SEMICOLON
    {$return_print_statement = new PrintStmt($arg.return_printExpr); $return_print_statement.setLine($p.getLine());}
    ;

printExpr returns [Expression return_printExpr]:
    var = variable {$return_printExpr = $var.v;}
    | q = query {$return_printExpr = $q.return_query;}
    ;

query returns [QueryExpression return_query]:
     q1 = queryType1 {$return_query = $q1.return_query1;}
     | q2 = queryType2 {$return_query = $q2.return_query2;}
    ;

queryType1 returns [QueryExpression return_query1]:
    LBRACKET line = QUARYMARK id = predicateIdentifier LPAR var = variable RPAR RBRACKET
    {$return_query1 = new QueryExpression($id.return_predicate_identifier); $return_query1.setLine($line.getLine()); $return_query1.setVar($var.v);}
    ;

queryType2 returns [QueryExpression return_query2]:
    LBRACKET id = predicateIdentifier LPAR line = QUARYMARK RPAR RBRACKET
    {$return_query2 = new QueryExpression($id.return_predicate_identifier); $return_query2.setLine($line.getLine()); }
    ;

returnSmt returns [ReturnStmt return_ret]:
    RETURN (v = value {$return_ret = new ReturnStmt($v.return_value);} | iden = identifier {$return_ret = new ReturnStmt($iden.return_identifier);})? SEMICOLON {if($return_ret == null){$return_ret = new ReturnStmt(null);}}
    {$return_ret.setLine($RETURN.getLine());}
    ;

forLoop returns [ForloopStmt return_loop]:
    {ArrayList<Statement> bodyStmts = new ArrayList<>();}
    line = FOR LPAR iterator = identifier COLON arrayName = identifier RPAR
    LBRACE ((stmt =statement {bodyStmts.add($stmt.return_statement);})*) RBRACE
    {$return_loop = new ForloopStmt($iterator.return_identifier, $arrayName.return_identifier, bodyStmts); $return_loop.setLine($line.getLine());}
    ;

predicate returns [PredicateStmt return_predicate]:
    id = predicateIdentifier LPAR v = variable RPAR
    {$return_predicate = new PredicateStmt($id.return_predicate_identifier, $v.v);
     $return_predicate.setLine($id.return_predicate_identifier.getLine());}
    ;

implication returns [ImplicationStmt return_implication]:
    {ArrayList<Statement> results = new ArrayList<Statement>();}
    LPAR e = expression RPAR a = ARROW LPAR ((s = statement {results.add($s.return_statement);})+) RPAR
    {$return_implication = new ImplicationStmt($e.e, results);
     $return_implication.setLine($a.getLine());}
    ;

expression returns [Expression e]:
    left = andExpr right = expression2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

expression2 returns [BinaryExpression e] locals [BinaryExpression ee]:
    OR left = andExpr right = expression2
    {if($right.e != null) {$ee = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $ee.setLine($right.e.getLine()); $e = new BinaryExpression(null, $ee, BinaryOperator.or);} else{$e = new BinaryExpression(null, $left.e, BinaryOperator.or);}}
    {$e.setLine($OR.getLine());}
    |
    {$e = null;}
    ;

andExpr returns [Expression e]:
    left = eqExpr right = andExpr2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

andExpr2 returns [BinaryExpression e] locals [BinaryExpression ee]:
    AND left = eqExpr right = andExpr2
    {if($right.e != null) {$ee = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $ee.setLine($right.e.getLine()); $e = new BinaryExpression(null, $ee, BinaryOperator.and);} else{$e = new BinaryExpression(null, $left.e, BinaryOperator.and);}}
    {$e.setLine($AND.getLine());}
    |
    {$e = null;}
    ;

eqExpr returns [Expression e]:
    left = compExpr right = eqExpr2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

eqExpr2 returns [BinaryExpression e] locals [BinaryOperator opt, BinaryExpression ee]:
    (op = EQ {$opt = BinaryOperator.eq;}| op = NEQ {$opt = BinaryOperator.neq;}) left = compExpr right = eqExpr2
    {if($right.e != null) {$ee = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $ee.setLine($right.e.getLine()); $e = new BinaryExpression(null, $ee, $opt);} else{$e = new BinaryExpression(null, $left.e, $opt);}}{$e.setLine($op.getLine());}
    |
    {$e = null;}
    ;

compExpr returns [Expression e]:
    left = additive right = compExpr2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

compExpr2 returns [BinaryExpression e] locals [BinaryOperator opt, BinaryExpression ee]:
    (op = LT {$opt = BinaryOperator.lt;}| op = LTE {$opt = BinaryOperator.lte;}| op = GT {$opt = BinaryOperator.gt;}| op = GTE{$opt = BinaryOperator.gte;}) l = additive r = compExpr2
    {if($r.e != null) {$ee = new BinaryExpression($l.e, $r.e.getRight(), $r.e.getBinaryOperator()); $ee.setLine($r.e.getLine()); $e = new BinaryExpression(null, $ee, $opt);} else{$e = new BinaryExpression(null, $l.e, $opt);}}{$e.setLine($op.getLine());}
    |
    {$e = null;}
    ;

additive returns [Expression e]:
    left = multicative right = additive2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

additive2 returns [BinaryExpression e] locals [BinaryOperator opt, BinaryExpression ee]:
    (op = PLUS {$opt = BinaryOperator.add;} | op = MINUS {$opt = BinaryOperator.sub;}) left = multicative right = additive2
    {if($right.e != null) {$ee = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $ee.setLine($right.e.getLine()); $e = new BinaryExpression(null, $ee, $opt);} else{$e = new BinaryExpression(null, $left.e, $opt);}}
    {$e.setLine($op.getLine());}
    |
    {$e = null;}
    ;

multicative returns [Expression e]:
    left =  unary right = multicative2
    {if($right.e != null) {$e = new BinaryExpression($left.e, $right.e.getRight(), $right.e.getBinaryOperator()); $e.setLine($right.e.getLine());} else {$e = $left.e;}}
    ;

multicative2 returns [BinaryExpression e] locals [BinaryOperator opt, BinaryExpression ee]:
    (op = MULT {$opt = BinaryOperator.mult;} | op = MOD {$opt = BinaryOperator.mod;}| op = DIV {$opt = BinaryOperator.div;}) l = unary r = multicative2
    {if($r.e != null) {$ee = new BinaryExpression($l.e, $r.e.getRight(), $r.e.getBinaryOperator()); $ee.setLine($r.e.getLine()); $e = new BinaryExpression(null, $ee, $opt);} else{$e = new BinaryExpression(null, $l.e, $opt);}}{$e.setLine($op.getLine());}
    |
    {$e = null;}
    ;

unary returns [Expression e] locals [UnaryOperator opt]:
    otherRet = other {$e = $otherRet.e;}
    |
     (op = PLUS {$opt = UnaryOperator.plus;} | op = MINUS {$opt = UnaryOperator.minus;} | op = NOT {$opt = UnaryOperator.not;}) expr = other
     {$e = new UnaryExpression($opt, $expr.e); $e.setLine($op.getLine());}
    ;

other returns [Expression e]:
    LPAR expr = expression RPAR {$e = $expr.e;}| var = variable {$e = $var.v;} | val = value {$e = $val.return_value;}
    | q1 = queryType1 {$e = $q1.return_query1;} | funcCall = functionCall {$e = $funcCall.return_function_call;}
    ;

functionCall returns [FunctionCall return_function_call]:
    {ArrayList<Expression> args = new ArrayList<Expression>();}
    name = identifier LPAR (arg1 = expression {args.add($arg1.e);} (COMMA newArg = expression {args.add($newArg.e);})*)? RPAR
    {$return_function_call = new FunctionCall(args, $name.return_identifier); $return_function_call.setLine($name.return_identifier.getLine());}
    ;

value returns [Value return_value]:
    v = numericValue {$return_value = $v.v;}
    | t = TRUE {$return_value = new BooleanValue(true);} {$return_value.setLine($t.getLine());}
    | f = FALSE {$return_value = new BooleanValue(false);} {$return_value.setLine($f.getLine());}
    | MINUS v2 = numericValue {$v2.v.negateConstant(); $return_value = $v2.v;}
    ;

numericValue returns [Value v]:
    i = INT_NUMBER {$v = new IntValue($i.int);}{$v.setLine($i.getLine());}
    | f = FLOAT_NUMBER {$v = new FloatValue(Float.parseFloat($f.text));}{$v.setLine($f.getLine());}
    ;

identifier returns [Identifier return_identifier]:
    idnfr = IDENTIFIER {$return_identifier = new Identifier($idnfr.text); $return_identifier.setLine($idnfr.getLine());}
    ;

predicateIdentifier returns [Identifier return_predicate_identifier]:
    predicate_idnfr = PREDICATE_IDENTIFIER {$return_predicate_identifier = new Identifier($predicate_idnfr.text); $return_predicate_identifier.setLine($predicate_idnfr.getLine());}
    ;

type returns [Type return_type]:
    BOOLEAN {$return_type = new BooleanType();}
    | INT {$return_type = new IntType();}
    | FLOAT {$return_type = new FloatType();}
    ;




FUNCTION : 'function';
BOOLEAN : 'boolean';
INT : 'int';
FLOAT: 'float';
MAIN: 'main';
PRINT: 'print';
RETURN: 'return';
FOR: 'for';
TRUE: 'true';
FALSE: 'false';

LPAR: '(';
RPAR: ')';
COLON: ':';
COMMA: ',';
LBRACE: '{';
RBRACE: '}';
SEMICOLON: ';';
ASSIGN: '=';
LBRACKET: '[';
RBRACKET: ']';
QUARYMARK: '?';
ARROW: '=>';
OR: '||';
AND: '&&';
EQ: '==';
GT: '>';
LT: '<';
GTE: '>=';
LTE: '<=';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
MOD: '%';
NEQ: '!=';
NOT: '!';


WS : [ \t\r\n]+ -> skip ;
COMMENT : '#' ~[\r\n]* -> skip ;

IDENTIFIER : [a-z][a-zA-Z0-9_]* ;
PREDICATE_IDENTIFIER : [A-Z][a-zA-Z0-9]* ;
INT_NUMBER : [0-9]+;
FLOAT_NUMBER: ([0-9]*[.])?[0-9]+;