package symbolTable.symbolTableItems;

import ast.node.declaration.ArgDeclaration;
import ast.node.declaration.FuncDeclaration;
import ast.node.statement.ForloopStmt;
import ast.type.Type;
import symbolTable.SymbolTable;

import java.util.ArrayList;

public class ForLoopItem extends SymbolTableItem{
    protected SymbolTable ForLoopSymbolTable;
    protected ForloopStmt forloopStmt;
    public static final String STARTKEY = "ForLoop_";
    private static int counter = 0;
    private int id;

//    public ForLoopItem(String name) {
//        this.name = name;
//    }

    public ForLoopItem(ForloopStmt forloopStmt)
    {
        this.id = counter++;
        forloopStmt.set_For_loop_Id(id);
        this.forloopStmt = forloopStmt;
        this.name = forloopStmt.toString();
    }

    public SymbolTable getForLoopSymbolTable()
    {
        return this.ForLoopSymbolTable;
    }

    @Override
    public String getKey() {
        return ForLoopItem.STARTKEY + this.name + this.id;
    }

    public void setForLoopSymbolTable(SymbolTable symbolTable) {
        this.ForLoopSymbolTable = symbolTable;
    }
}
