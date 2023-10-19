package ast;

import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class IfExpNode implements Node {
    private Node guard;
    private ArrayList<Node> thenBranch;
    private ArrayList<Node> elseBranch;
    private Node thenExp;
    private Node elseExp;

    public IfExpNode(Node guard, ArrayList<Node> thenBranch, Node thenExp, ArrayList<Node> elseBranch, Node elseExp) {
        this.guard = guard;
        this.thenBranch = thenBranch;
        this.thenExp = thenExp;
        this.elseBranch = elseBranch;
        this.elseExp = elseExp;
    }


    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        errors.addAll(guard.checkSemantics(ST, _nesting));

        SymbolTable thenST = ST.saveSymbolTable();
        SymbolTable elseST = ST.saveSymbolTable();

        if (thenBranch != null) {
            for (Node thenB : thenBranch) {
                errors.addAll(thenB.checkSemantics(thenST, _nesting));
            }
        }
        errors.addAll(thenExp.checkSemantics(thenST, _nesting));
        if (elseBranch != null) {
            for (Node elseB : elseBranch) {
                errors.addAll(elseB.checkSemantics(elseST, _nesting));
            }
        }
        errors.addAll(elseExp.checkSemantics(elseST, _nesting));

        ArrayList<HashMap<String, STentry>> intersect = thenST.intersectSymbolTables(elseST);
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
            if (thenBranch != null) {
                for (Node thenB : thenBranch) {
                    thenB.typeCheck();
                }
            }
            if (elseBranch != null) {
                for (Node elseB : elseBranch) {
                    elseB.typeCheck();
                }
            }
            Type thenExpType = thenExp.typeCheck();
            Type elseExpType = elseExp.typeCheck();
            if (thenExpType.getClass().equals(elseExpType.getClass()))
                return thenExpType;
            else {
                System.out.println("Type Error: incompatible types in then and else branches");
                ErrorType err = new ErrorType();
                err.setMessage("Type Error: incompatible types in then and else branches");
                return err;
            }
        } else {
            System.out.println("Type Error: non boolean condition in if");
            ErrorType err = new ErrorType();
            err.setMessage("Type Error: non boolean condition in if");
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

        for (Node elseC : elseBranch) {
            elsecode += elseC.codeGeneration();
        }

        return guard.codeGeneration() +
                "storei T1 1 \n" +
                "beq A0 T1 " + labelthen + "\n" +
                elsecode + elseExp.codeGeneration() +
                "b " + labelend + "\n" +
                labelthen + ":\n" +
                thencode + thenExp.codeGeneration() +
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

        return s + "If\n" + guard.toPrint(s + "  ") + thenstring + thenExp.toPrint(s + "  ") + elsestring + elseExp.toPrint(s + "  ");
    }
}