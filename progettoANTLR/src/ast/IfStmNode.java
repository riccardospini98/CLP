package ast;

import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import ast.Types.VoidType;
import evaluator.SimpLanlib;
import org.stringtemplate.v4.ST;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class IfStmNode implements Node {
    private Node guard;
    private ArrayList<Node> thenBranch;
    private ArrayList<Node> elseBranch;

    public IfStmNode(Node _guard, ArrayList<Node> _thenBranch, ArrayList<Node> _elseBranch) {
        guard = _guard;
        thenBranch = _thenBranch;
        elseBranch = _elseBranch;
    }

    public IfStmNode(Node _guard, ArrayList<Node> _thenBranch) {
        guard = _guard;
        thenBranch = _thenBranch;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<>();
        errors.addAll(guard.checkSemantics(ST, _nesting));

        SymbolTable thenST = ST.saveSymbolTable();
        SymbolTable elseST = ST.saveSymbolTable();
        ArrayList<HashMap<String, STentry>> intersect;
        if (thenBranch != null) {
            for (Node thenN : thenBranch) {
                errors.addAll(thenN.checkSemantics(thenST, _nesting));
            }
        }
        if (elseBranch != null) {
            for (Node elseN : elseBranch) {
                errors.addAll(elseN.checkSemantics(elseST, _nesting));
            }
            intersect = thenST.intersectSymbolTables(elseST);
            // Nel caso in cui l'if non abbia un ramo else e una variabile venga
            // inizializzata solo nle corpo dell'if, lo considero un errore di
            // variabile usata ma non inizializzata (se viene usata ovviamente).
        } else {
            // La intersect prende i valori della ST su cui viene invocata,
            // non di quella passata come parametro.
            intersect = ST.intersectSymbolTables(thenST);
        }
        try {
            ST.mergeSymbolTable(intersect);
        } catch (Exception e) {
            System.out.println("Error Merging ST....");
            throw new RuntimeException(e);
        }

        return errors;
    }

    @Override
    public Type typeCheck() {
        if (guard.typeCheck() instanceof BoolType) {
            for (Node thenB : thenBranch) {
                thenB.typeCheck();
            }
            if (elseBranch != null)
                for (Node elseB : elseBranch) {
                    elseB.typeCheck();
                }
            return new VoidType();
        } else {
            System.out.println("Type Error: non boolean condition in if guard");
            ErrorType err = new ErrorType();
            err.setMessage("Type Error: non boolean condition in if guard");
            return err;
        }
    }

    @Override
    public String codeGeneration() {
        String labelthen = SimpLanlib.freshLabel();
        String labelend = SimpLanlib.freshLabel();
        String thencode = "";
        String elsecode = "";

        for (Node thenC : thenBranch) {
            thencode += thenC.codeGeneration();
        }
        if (elseBranch != null)
            for (Node elseC : elseBranch) {
                elsecode += elseC.codeGeneration();
            }

        return guard.codeGeneration() +
                "storei T1 1 \n" +
                "beq A0 T1 " + labelthen + "\n" +
                elsecode +
                "b " + labelend + "\n" +
                labelthen + ":\n" +
                thencode +
                labelend + ":\n";
    }


    @Override
    public String toPrint(String s) {
        String thenstring = "";
        String elsestring = "";

        for (Node thenS : thenBranch) {
            thenstring += thenS.toPrint(s + "  ");
        }

        for (Node elseS : elseBranch) {
            elsestring += elseS.toPrint(s + "  ");
        }

        return s + "If\n" + guard.toPrint(s + "  ") + thenstring + elsestring;
    }

}  