package visitor.typeAnalyzer;

import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.FloatValue;
import ast.node.expression.values.IntValue;
import ast.node.statement.ImplicationStmt;
import ast.type.NoType;
import ast.type.Type;
import ast.type.primitiveType.BooleanType;
import ast.type.primitiveType.FloatType;
import ast.type.primitiveType.IntType;
import compileError.CompileError;
import compileError.Type.FunctionNotDeclared;
import compileError.Type.UnsupportedOperandType;
import compileError.Type.VarNotDeclared;
import symbolTable.SymbolTable;
import symbolTable.itemException.ItemAlreadyExistsException;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.FunctionItem;
import symbolTable.symbolTableItems.VariableItem;
import visitor.Visitor;

import java.util.ArrayList;

public class ExpressionTypeChecker extends Visitor<Type> {
    public ArrayList<CompileError> typeErrors;
    public ExpressionTypeChecker(ArrayList<CompileError> typeErrors){
        this.typeErrors = typeErrors;
    }

    public boolean sameType(Type el1, Type el2){
        //TODO check the two type are same or not
        return el1.getClass() == el2.getClass();
        // check!
    }

    public boolean isLvalue(Expression expr){
        //TODO check the expr are lvalue or not
        return expr instanceof Variable;
    }


    @Override
    public Type visit(UnaryExpression unaryExpression) {

        Expression uExpr = unaryExpression.getOperand();
        Type expType = uExpr.accept(this);
        UnaryOperator operator = unaryExpression.getUnaryOperator();


        //TODO check errors and return the type
        if(expType instanceof IntType && (operator.equals(UnaryOperator.minus) ||
                operator.equals((UnaryOperator.plus)))) {
            return new IntType();
        }

        else if (expType instanceof BooleanType && (operator.equals(UnaryOperator.not))) {
            return new BooleanType();
        }

        else {
            typeErrors.add(new UnsupportedOperandType(unaryExpression.getLine(), operator.name()));
            return new NoType();
        }
    }

    @Override
    public Type visit(BinaryExpression binaryExpression) {
        Type tl = binaryExpression.getLeft().accept(this);
        Type tr = binaryExpression.getRight().accept(this);
        BinaryOperator operator =  binaryExpression.getBinaryOperator();

        if(operator.equals(BinaryOperator.and) || operator.equals(BinaryOperator.or)){
            if(tl instanceof BooleanType && tr instanceof  BooleanType)
                return new BooleanType();
            else if ((tl instanceof NoType) || (tr instanceof NoType))
                return new NoType();
            else {
                typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
                return new NoType();
            }
        }


        if(operator.equals(BinaryOperator.eq) || operator.equals(BinaryOperator.neq) ||
            operator.equals(BinaryOperator.gt) || operator.equals(BinaryOperator.gte) ||
            operator.equals(BinaryOperator.lt) || operator.equals(BinaryOperator.lte)){
                if( (tl instanceof BooleanType && tr instanceof BooleanType) ||
                    (tl instanceof IntType && tr instanceof IntType) ||
                    (tl instanceof FloatType && tr instanceof FloatType)) {
//                    System.out.println("DOOOOOOne");
                    return new BooleanType();

                }
                else if ((tl instanceof NoType) || (tr instanceof NoType))
                    return new NoType();
                else {
                    typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
                    return new NoType();
                }
        }

        if(operator.equals(BinaryOperator.add) ||
            operator.equals(BinaryOperator.sub) ||
            operator.equals(BinaryOperator.mult) ||
            operator.equals(BinaryOperator.div) ||
            operator.equals(BinaryOperator.mod)){
                if (tl instanceof IntType && tr instanceof IntType)
                    return new IntType();
                else if (tl instanceof FloatType && tr instanceof FloatType) {
                    return new FloatType();
                }
                else if (tl instanceof BooleanType && tr instanceof BooleanType) {
                    return new BooleanType();
                }
                else if ((tl instanceof NoType ) || (tr instanceof NoType)){
                    return new NoType();
                }
                else {
                    typeErrors.add(new UnsupportedOperandType(binaryExpression.getLine(), operator.name()));
                    return new NoType();
                }
        }
        return new NoType();
    }


    @Override
    public Type visit(Identifier identifier) {
        try {
            VariableItem varItem = (VariableItem) SymbolTable.top.get(VariableItem.STARTKEY + identifier.getName());
            return varItem.getType();
        }
        catch (ItemNotFoundException I1) {
            typeErrors.add(new VarNotDeclared(identifier.getLine(), identifier.getName()));
            return new NoType();
        }
    }


    @Override
    public Type visit(FunctionCall functionCall) {
        try{
            FunctionItem funcCall = (FunctionItem) SymbolTable.top.get(FunctionItem.STARTKEY +
                    functionCall.getUFuncName().getName());
            return funcCall.getHandlerDeclaration().getType();

            //            return funcCallName.getType();
        }
        catch (ItemNotFoundException F1){
            typeErrors.add(new FunctionNotDeclared(functionCall.getLine(), functionCall.getUFuncName().toString()));
            return new NoType();
        }
    }


    @Override
    public Type visit(ArrayAccess arrayAccess){
        try {
            VariableItem varItem = (VariableItem) (SymbolTable.top.get(VariableItem.STARTKEY + arrayAccess.getName()));
            var type = arrayAccess.getIndex().accept(this);
            if (type instanceof IntType)
                return varItem.getType();
            else if (type instanceof NoType)
                return  new NoType();
//            typeErrors.add(new UnsupportedOperandType(arrayAccess.getLine(), "[]"));
            return new NoType();
        }
        catch (ItemNotFoundException e){
            typeErrors.add(new VarNotDeclared(arrayAccess.getLine(), arrayAccess.getName()));
//            return new NoType();


            return new NoType();
        }
    }

    @Override
    public Type visit(QueryExpression queryExpression) {
        var query_arg = queryExpression.getVar();
        if (query_arg == null) {
            return new NoType();
        }
        else {
            query_arg.accept(this);
            return new BooleanType();
        }
    }




    @Override
    public Type visit(IntValue value) {
        return new IntType();
    }

    @Override
    public Type visit(FloatValue value) {
        return new FloatType();
    }

    @Override
    public Type visit(BooleanValue value) {
        return new BooleanType();
    }
}













