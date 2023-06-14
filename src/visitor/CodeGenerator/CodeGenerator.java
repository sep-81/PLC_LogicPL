package visitor.CodeGenerator;

import ast.node.Program;
import ast.node.declaration.Declaration;
import ast.node.declaration.FuncDeclaration;
import ast.node.declaration.MainDeclaration;
import ast.node.expression.Expression;
import ast.node.expression.FunctionCall;
import ast.node.expression.Identifier;
import ast.node.expression.operators.BinaryOperator;
import ast.node.expression.values.IntValue;
import ast.node.statement.*;
import ast.type.NoType;
import ast.type.Type;
import com.sun.jdi.VoidType;
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

import java.util.ArrayList;


public class CodeGenerator extends Visitor<String>  {

    private int slotOf(String identifier) {

    }










    @Override
    public String visit(ReturnStmt returnStmt) {
        String command = "";
        command += returnStmt.getExpression().accept(this);
        Type returnType = returnStmt.getExpression().getType();
        if (returnType instanceof VoidType)
            command += "return\n";
        else
            command += "areturn\n";

        return command;
    }


}
