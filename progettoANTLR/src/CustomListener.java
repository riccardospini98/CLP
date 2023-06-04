import antlr.SimpLanPlusBaseListener;
import antlr.SimpLanPlusParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CustomListener extends SimpLanPlusBaseListener {
    private SymbolTable symbolTable;
    private Map<String, Integer> declarationCount;

    public CustomListener(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.declarationCount = new HashMap<>();
    }

    @Override
    public void enterDec(SimpLanPlusParser.DecContext ctx) {
        System.out.println("Declaration: " + ctx.getText());

        if (!(ctx.type() == null || ctx.ID() == null || ctx.exception != null)) {
            String identifier = ctx.ID().getText();

            if (declarationCount.containsKey(identifier)) {
                // Identificatore dichiarato più volte nello stesso ambiente
                symbolTable.addDuplicateIdentifier(identifier);
            } else {
                declarationCount.put(identifier, 1);
                symbolTable.addIdentifier(identifier);
            }
        }
    }

    @Override
    public void exitExp(SimpLanPlusParser.ExpContext ctx) {
        if (ctx.ID() != null) {
            String identifier = ctx.ID().getText();
            symbolTable.addReference(identifier);
        }
    }

    @Override
    public void enterStm(SimpLanPlusParser.StmContext ctx) {
        if(ctx.ID() != null  && !symbolTable.getIdentifiers().contains(ctx.ID().getText())) {
            System.out.println("Undeclared accessed " + ctx.ID().getText());
            symbolTable.addUndeclaredIdentifier(ctx.ID().getText());
        }
    }

    @Override
    public void enterExp(SimpLanPlusParser.ExpContext ctx) {
        if(ctx.ID() != null  && !symbolTable.getIdentifiers().contains(ctx.ID().getText())) {
            symbolTable.addUndeclaredIdentifier(ctx.ID().getText());
        }
    }
}

