package visitor.nameAnalyzer;

import ast.node.Program;
import ast.node.declaration.*;
import ast.node.statement.*;
import compileError.*;
import compileError.Name.*;
import symbolTable.SymbolTable;
import symbolTable.symbolTableItems.*;
import symbolTable.itemException.ItemAlreadyExistsException;
import symbolTable.symbolTableItems.VariableItem;
import visitor.Visitor;

import java.util.ArrayList;

public class NameAnalyzer extends Visitor<Void> {

    public ArrayList<CompileError> nameErrors = new ArrayList<>();

    @Override
    public Void visit(Program program) {
        SymbolTable.root = new SymbolTable();
        SymbolTable.push(SymbolTable.root);

        for (FuncDeclaration functionDeclaration : program.getFuncs()) {
            functionDeclaration.accept(this);
        }

        for (var stmt : program.getMain().getMainStatements()) {
                stmt.accept(this);
        }

        return null;
    }


    @Override
    public Void visit(FuncDeclaration funcDeclaration) {
        var functionItem = new FunctionItem(funcDeclaration);
        var functionSymbolTable = new SymbolTable(SymbolTable.top, funcDeclaration.getName().getName());
        functionItem.setFunctionSymbolTable(functionSymbolTable);

        // ToDo
        FunctionRedefinition funcRedifError = null;
        while(true){
            try{
                SymbolTable.top.put(functionItem);
                SymbolTable.push(functionSymbolTable);
                break;
            }
            catch (ItemAlreadyExistsException Err){
                funcRedifError = new FunctionRedefinition(funcDeclaration.getLine(), funcDeclaration.getName().getName());
                functionItem.setName(funcDeclaration.getName().getName() + '$');
            }
        }

        if (funcRedifError != null)
            this.nameErrors.add(funcRedifError);

        for (ArgDeclaration varDeclaration : funcDeclaration.getArgs()) {
            varDeclaration.accept(this);
        }

        for (var stmt : funcDeclaration.getStatements()) {
                stmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(VarDecStmt varDeclaration) {
        var variableItem = new VariableItem(varDeclaration.getIdentifier().getName(), varDeclaration.getType());
        variableItem.setVarDeclaration(varDeclaration);
        String varName = varDeclaration.getIdentifier().getName();

        // ToDo
        VariableRedefinition varRedifError = null;
        while(true){
            try{
                SymbolTable.top.put(variableItem);
                break;
            }
            catch (ItemAlreadyExistsException Err){
                varRedifError = new VariableRedefinition(varDeclaration.getLine(), varName);
                variableItem.setName(varDeclaration.getIdentifier().getName() + "$");
            }
        }

        if (varRedifError != null)
            this.nameErrors.add(varRedifError);

        return null;
    }

    @Override
    public  Void visit (ArgDeclaration argDeclaration) {
        var argVariableItem = new VariableItem(argDeclaration.getIdentifier().getName(), argDeclaration.getType());
        argVariableItem.setVarDeclaration(new VarDecStmt(argDeclaration.getIdentifier(), argDeclaration.getIdentifier().getType()));
        String argName = argDeclaration.getIdentifier().getName();
        // ToDo
        VariableRedefinition varRedifError = null;
        while(true){
            try{
                SymbolTable.top.put(argVariableItem);
                break;
            }
            catch (ItemAlreadyExistsException Err){
                varRedifError = new VariableRedefinition(argDeclaration.getLine(), argName);
                argVariableItem.setName(argDeclaration.getIdentifier().getName() + "$");
            }
        }

        if (varRedifError != null)
            this.nameErrors.add(varRedifError);

        return null;
    }

    @Override
    public  Void visit (ArrayDecStmt arrDeclaration) {
        var arrItem = new ArrayItem(arrDeclaration.getIdentifier().getName(), arrDeclaration.getType());
        arrItem.setArrayDeclaration(arrDeclaration);
        String arrName = arrDeclaration.getIdentifier().getName();

        // ToDo
        VariableRedefinition arrRedifError = null;
        while(true){
            try{
                SymbolTable.top.put(arrItem);
                break;
            }
            catch (ItemAlreadyExistsException Err){
                arrRedifError = new VariableRedefinition(arrDeclaration.getLine(), arrName);
                arrItem.setName(arrDeclaration.getIdentifier().getName() + "$");
            }
        }

        if (arrRedifError != null)
            this.nameErrors.add(arrRedifError);

        return null;
    }

    @Override
    public Void visit(ForloopStmt forLoop) {
        var forSymbolTable = new SymbolTable(SymbolTable.top, "for loop");
        var iteratorItem = new VariableItem(forLoop.getIterator().getName(), forLoop.getIterator().getType());
        // ToDO
        try {
            forSymbolTable.put(iteratorItem);
        } catch (ItemAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        SymbolTable.push(forSymbolTable);
        for(var forStmt : forLoop.getStatements()) {
            forStmt.accept(this);
        }

        SymbolTable.pop();
        return null;


    }
    // have problem

    public Void visit(ImplicationStmt implStmt) {
        var implSymbolTable = new SymbolTable(SymbolTable.top, "implication statement");
        SymbolTable.push(implSymbolTable);
        // ToDO
        for (var implicStmt : implStmt.getStatements()) {
            implicStmt.accept(this);
        }

        SymbolTable.pop();
        return null;
    }

    }


// for , implication , argdeclration , arraydeclration
