package ast;

import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class IfExpNode implements Node {
    private Node guard;
    private ArrayList<Node> thenbranch;
    private ArrayList<Node> elsebranch;
    private Node thenExp;
    private Node elseExp        ;

    public IfExpNode(Node guard, ArrayList<Node> thenbranch, Node thenExpbranch, ArrayList<Node> elsebranch, Node elseExpbranch) {
        this.guard = guard;
        this.thenbranch = thenbranch;
        this.thenExp = thenExpbranch;
        this.elsebranch = elsebranch;
        this.elseExp = elseExpbranch;
    }


    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        errors.addAll(guard.checkSemantics(ST, _nesting));

        for (Node thenB : thenbranch) {
            errors.addAll(thenB.checkSemantics(ST, _nesting));
        }
        errors.addAll(thenExp.checkSemantics(ST, _nesting));

        for (Node elseB : elsebranch) {
            errors.addAll(elseB.checkSemantics(ST, _nesting));
        }
        errors.addAll(elseExp.checkSemantics(ST, _nesting));
        return errors;
    }

    public Type typeCheck() {
        if (guard.typeCheck() instanceof BoolType) {
            if (thenbranch != null) {
                for (Node thenB : thenbranch) {
                    thenB.typeCheck();
                }
            }
            if (elsebranch != null) {
                for (Node elseB : elsebranch) {
                    elseB.typeCheck();
                }
            }

            Type thenExpType = thenExp.typeCheck();
            Type elseExpType = elseExp.typeCheck();
            if (thenExpType.getClass().equals(elseExpType.getClass()))
                return thenExpType;
            else {
                System.out.println("Type Error: incompatible types in then and else branches");
                return new ErrorType();
            }
        } else {
            System.out.println("Type Error: non boolean condition in if");
            return new ErrorType();
        }
    }

    public String codeGeneration() {
        String labelthen = SimpLanlib.freshLabel();
        String labelend = SimpLanlib.freshLabel();
        String thencode = "";
        String elsecode = "";

        for (Node thenC : thenbranch) {
            thencode += thenC.codeGeneration();
        }

        for (Node elseC : elsebranch) {
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

    public String toPrint(String s) {
        String thenstring = "";
        String elsestring = "";

        for (Node thenS : thenbranch) {
            thenstring += thenS.toPrint(s + "  ");
        }

        for (Node elseS : elsebranch) {
            elsestring += elseS.toPrint(s + "  ");
        }

        return s + "If\n" + guard.toPrint(s + "  ") + thenstring + thenExp.toPrint(s + "  ") + elsestring + elseExp.toPrint(s + "  ");
    }
}