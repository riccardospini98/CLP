package ast;

import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import ast.Types.VoidType;
import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

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
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

        errors.addAll(guard.checkSemantics(ST, _nesting));

        SymbolTable thenST = new SymbolTable();
        SymbolTable elseST = new SymbolTable();
        for (Node thenN : thenBranch) {
            errors.addAll(thenN.checkSemantics(thenST, _nesting));
        }
        for (Node elseN : elseBranch) {
            errors.addAll(elseN.checkSemantics(elseST, _nesting));
        }

        //TODO: controllare e gestire
        try {
            ST.mergeSymbolTable(thenST.intersectSymbolTables(elseST));
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
            for (Node elseB : elseBranch) {
                elseB.typeCheck();
            }
            return new VoidType();
        } else {
            System.out.println("Type Error: non boolean condition in if guard");
            ErrorType err = new ErrorType();
            err.setMessage("Type Error: non boolean condition in if guard");
            return  err;
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