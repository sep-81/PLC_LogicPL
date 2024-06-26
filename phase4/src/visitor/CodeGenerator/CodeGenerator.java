package visitor.CodeGenerator;

import ast.node.Program;
import ast.node.declaration.Declaration;
import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.expression.*;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.operators.UnaryOperator;
import ast.node.expression.values.BooleanValue;
import ast.node.expression.values.FloatValue;
import ast.node.expression.values.IntValue;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.Type;
import com.sun.jdi.VoidType;
import com.sun.tools.javac.Main;
import compileError.CompileError;
import compileError.Type.FunctionNotDeclared;
import compileError.Type.LeftSideNotLValue;
import compileError.Type.UnsupportedOperandType;
import compileError.Type.ConditionTypeNotBool;
import symbolTable.SymbolTable;
import symbolTable.itemException.ItemNotFoundException;
import symbolTable.symbolTableItems.ForLoopItem;
import symbolTable.symbolTableItems.FunctionItem;
import symbolTable.symbolTableItems.MainItem;
import visitor.Visitor;
import ByteCodes.*;

import java.util.ArrayList;
import java.util.Stack;


public class CodeGenerator extends Visitor<Void>  {

    public ArrayList<String> bytecodes = new ArrayList<>();
    private ArrayList<String> localVarArray = new ArrayList<>();

    private int slotOf(String name) {
        int slot = localVarArray.indexOf(name);
        if (slot == -1) {
            localVarArray.add(name);
            slot = localVarArray.size() - 1;
        }
        return slot;
    }
    @Override
    public Void visit(Program program){

        bytecodes.add(".class public Main");
        bytecodes.add(".super java/lang/Object");

        bytecodes.add(".method public <init>()V");
        bytecodes.add("aload_0");
        bytecodes.add("invokenonvirtual java/lang/Object/<init>()V");
        bytecodes.add("return");
        bytecodes.add(".end method");

        for(var functionDec : program.getFuncs()) {
            bytecodes.add(".method static " + functionDec.getName().getName()+"()I");
            bytecodes.add(".limit stack 16");
            bytecodes.add(".limit locals 10");
            functionDec.accept(this);
            bytecodes.add(".end method");

            localVarArray.clear();
        }

        program.getMain().accept(this);

        return null;
    }

    @Override
    public Void visit(FuncDeclaration funcDeclaration){
        for(var stmt : funcDeclaration.getStatements()) {
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(MainDeclaration mainDeclaration){
        bytecodes.add(".method public static main([Ljava/lang/String;)V");
        bytecodes.add(".limit stack 16");
        bytecodes.add(".limit locals 3");
        for (var stmt : mainDeclaration.getMainStatements()) {
            stmt.accept(this);
        }
        bytecodes.add("return");
        bytecodes.add(".end method");
        return null;
    }


    public Void visit(AssignStmt assignStmt){
        assignStmt.getRValue().accept(this);
        // TODO set istore for left value manually
        int slot = slotOf(((Variable)assignStmt.getLValue()).getName());
        bytecodes.add((new Istore(slot)).toString());

        return null;
    }

    public Void visit(BinaryExpression binaryExpression){
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);

        BinaryOperator operator = binaryExpression.getBinaryOperator();

        if(operator == BinaryOperator.add)
            bytecodes.add((new Iadd()).toString());
        else if(operator == BinaryOperator.sub)
            bytecodes.add((new Isub()).toString());
        else if(operator == BinaryOperator.mult)
            bytecodes.add((new Imul()).toString());
        else if(operator == BinaryOperator.div)
            bytecodes.add((new Idiv()).toString());
        else if(operator == BinaryOperator.mod)
            bytecodes.add((new Irem()).toString());

        return null;
    }

    public Void visit(UnaryExpression unaryExpression){
        unaryExpression.getOperand().accept(this);
        UnaryOperator operator = unaryExpression.getUnaryOperator();

        if(operator == UnaryOperator.minus)
            bytecodes.add((new Ineg()).toString());

        return null;
    }

    public Void visit(Identifier identifier){
        int slot = slotOf(identifier.getName());
        bytecodes.add((new Iload(slot)).toString());

        return null;
    }

    @Override
    public Void visit(VarDecStmt varDecStmt){
        varDecStmt.getInitialExpression().accept(this);
        int slot = slotOf(varDecStmt.getIdentifier().getName());
        bytecodes.add((new Istore(slot)).toString());

        return null;
    }

    @Override
    public Void visit(FunctionCall functionCall){
        for (Expression arg: functionCall.getArgs()){
            arg.accept(this);
        }
        String path = "Main/" + functionCall.getUFuncName().getName() + "()I";
        bytecodes.add((new Invokestatic(path)).toString());
        bytecodes.add("pop");
        return null;
    }


    @Override
    public Void visit(ReturnStmt returnStmt) {
        returnStmt.getExpression().accept(this);
        Type returnType = returnStmt.getExpression().getType();

        if (returnType instanceof VoidType)
            bytecodes.add((new Return(0)).toString());
        else
            bytecodes.add((new Return(1)).toString());


        return null;
    }

    @Override
    public Void visit(IntValue intValue){
        bytecodes.add((new Bipush(intValue.getConstant())).toString());
        return null;
    }

    @Override
    public Void visit(BooleanValue booleanValue){
        if(booleanValue.getConstant())
            bytecodes.add((new Iconst(1)).toString());
        else
            bytecodes.add((new Iconst(0)).toString());

        return null;
    }

}
