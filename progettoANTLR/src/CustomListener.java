import antlr.SimpLanPlusBaseListener;
import antlr.SimpLanPlusParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CustomListener extends SimpLanPlusBaseListener {
    private SymbolTable symbolTable;
    private ArrayList<Map<String, Integer>> symbolList;

    private Integer scope;

    public CustomListener(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.symbolList = new ArrayList<>();
    }

    @Override
    public void enterProg(SimpLanPlusParser.ProgContext ctx) {
        super.enterProg(ctx);
        System.out.println("prog");
        Map<String, Integer> programScope = new HashMap<>();
        symbolList.add(programScope);
        scope = 0;
    }

    @Override
    public void enterDec(SimpLanPlusParser.DecContext ctx) {
        //System.out.println("Entering dec " + ctx.getText());

        if(ctx.getRuleIndex() == 1) {
            System.out.println("scope +1 " + ctx.getText() + " now " + scope);
            Map<String, Integer> funcScope = new HashMap<>();
            symbolList.add(funcScope);
            scope = scope + 1;
        }
        if (!(ctx.type() == null || ctx.ID() == null || ctx.exception != null)) {
            String type = ctx.type().getText();
            String identifier = ctx.ID().getText();
            Symbol symbol = new Symbol(type, identifier, scope);

            if (symbolList.get(scope).containsKey(identifier)) {
                // Identificatore dichiarato più volte nello stesso ambiente
                symbolTable.addDuplicateIdentifier(symbol);
            } else {
                // scope salvato nell'hash mapo è useless
                symbolList.get(scope).put(identifier, scope);
                symbolTable.addIdentifier(symbol);
            }
        }
    }

    @Override
    public void enterParam(SimpLanPlusParser.ParamContext ctx) {
        super.enterParam(ctx);
        String type = ctx.type().getText();
        String identifier = ctx.ID().getText();
        Symbol symbol = new Symbol(type, identifier, scope);
        System.out.println("Param" +  ctx.getText());
        if (symbolList.get(scope).containsKey(identifier)) {
            symbolTable.addDuplicateIdentifier(symbol);
        } else {
            symbolTable.addIdentifier(symbol);
        }

    }

    @Override
    public void exitDec(SimpLanPlusParser.DecContext ctx) {
        super.exitDec(ctx);
        if(ctx.getRuleIndex() == 1) {
            //symbolList.remove(symbolList.get(scope));
            scope -=1;
        }
    }

    @Override
    public void exitExp(SimpLanPlusParser.ExpContext ctx) {
        if (ctx.ID() != null) {
            String identifier = ctx.ID().getText();
            Symbol symbol = new Symbol("*", identifier, scope);
            //Se ho un identifier con scope minore o uguale a quello attuale la referenziazione è valida
            if (symbolList.get(scope).containsKey(identifier) &&
                    symbolList.get(scope).get(identifier) <= scope) {
                //System.out.println("Referencing " + ctx.getText());
                symbolTable.addReference(symbol);
            }

        }
    }

    @Override
    public void enterStm(SimpLanPlusParser.StmContext ctx) {
        //System.out.println("Entering stm " + ctx.getText());

        if(ctx.ID() != null) {
            Symbol symbol = new Symbol("*", ctx.ID().getText(), scope);
            if (!symbolTable.isDeclared(symbol)) {
                System.out.println("Undeclared accessed " + ctx.ID().getText());
                symbolTable.addUndeclaredIdentifier(symbol);
            } else {
                symbolTable.addReference(symbol);
            }
        }
    }

    @Override
    public void enterExp(SimpLanPlusParser.ExpContext ctx) {
        //System.out.println("Entering exp " + ctx.getText());
        if(ctx.ID() != null) {
            Symbol symbol = new Symbol(null, ctx.ID().getText(), scope);
            if (!symbolTable.isDeclared(symbol)) {
                symbolTable.addUndeclaredIdentifier(symbol);
            }
        }
    }
}

