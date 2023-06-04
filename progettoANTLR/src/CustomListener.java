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
    private List<String> parserErrors;
    private Map<String, Integer> declarationCount;

    public CustomListener(SymbolTable symbolTable, List<String> parserErrors) {
        this.symbolTable = symbolTable;
        this.parserErrors = parserErrors;
        this.declarationCount = new HashMap<>();
    }

    @Override
    public void enterDec(SimpLanPlusParser.DecContext ctx) {
        System.out.println("Declaration: " + ctx.getText());
        if (ctx.exception != null) {
            Token token = ctx.getStart();
            String errorMessage = "Syntax error at line " + token.getLine() + ":" + token.getCharPositionInLine() + " " + ctx.exception.getMessage();
            parserErrors.add(errorMessage);
        }

        else if (ctx.type() == null || ctx.ID() == null) {
            // Errore di sintassi nella dichiarazione
            Token token = ctx.getStart();
            String errorMessage = "Syntax error at line " + token.getLine() + ":" + token.getCharPositionInLine() + " Incomplete or invalid declaration";
            parserErrors.add(errorMessage);

        }
        else {

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
    public void visitErrorNode(ErrorNode node) {
        Token token = node.getSymbol();
        String errorMessage = "Syntax error at line " + token.getLine() + ":" + token.getCharPositionInLine() + " " + node.toString();
        parserErrors.add(errorMessage);
    }
}

