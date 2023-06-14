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
    private Stack<String> constPool = new Stack<>();


    @Override
    public Void visit(Program program){
        for(var functionDec : program.getFuncs()) {
            functionDec.accept(this);
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
    public Void visit(MainDeclaration mainDecleration){
        for (var stmt : mainDecleration.getMainStatements()) {
            stmt.accept(this);
        }

        return null;
    }

    public Void visit(AssignStmt assignStmt){
        assignStmt.getRValue().accept(this);
        // TODO set istore for left value manually

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
        bytecodes.add((new Istore(slot)).toString());

        return null;
    }


    // a = 3
    // varDeclartion
    // Assingnstatement

//    @Override
//    public String visit(AssignStmt assignStmt){
//        assignStmt.getLValue().accept(this);
//    }
    // a = b + c + a




    @Override
    public String visit(ReturnStmt returnStmt) {
        String command = "";
        command += returnStmt.getExpression().accept(this);
        Type returnType = returnStmt.getExpression().getType();

        if (returnType instanceof VoidType)
            command += (new Return(0)).toString();
        else
            command += (new Return(1)).toString();

        return command;
    }

    @Override
    public String visit(IntValue intValue){
        String command = "";
        command += (new Bipush(intValue.getConstant())).toString();
        return command;
    }

    @Override
    public String visit(BooleanValue booleanValue){
        String command = "";
        if(booleanValue.getConstant())
            command += (new Iconst(1).toString());
        else
            command += (new Iconst(0)).toString();

        return command;
    }


}
