package ast.ExpNodes;

import ast.Node;
import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class AndNode implements Node {
    private Node left;
    private Node right;

    public AndNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        errors.addAll(left.checkSemantics(ST, _nesting));
        errors.addAll(right.checkSemantics(ST, _nesting));
        return errors;
    }

    @Override
    public Type typeCheck() {
        Type leftOp = left.typeCheck();
        Type rightOp = right.typeCheck();
        //Controllo che siano dello stesso entrambi bool e ritorno bool
        if ((leftOp instanceof BoolType) || (rightOp instanceof BoolType))
            return new BoolType();
        else {
            System.err.println("[X] ERROR-TypeError:  incompatible types in op expression");
            ErrorType err = new ErrorType();
            err.setMessage("[X] ERROR-TypeError:  incompatible types in op expression");
            return  err;
        }
    }

    @Override
    public String codeGeneration() {
        String ltrue = SimpLanlib.freshLabel();
        String lend = SimpLanlib.freshLabel();
        String leq = SimpLanlib.freshLabel();
        return "//AndNode\n" +left.codeGeneration() +
                "pushr A0 \n" +
                right.codeGeneration() +
                "popr T1 \n" +
                "beq A0 T1 " + ltrue + "\n" +
                leq + ":\n" +
                "storei A0 0 \n" +
                "b " + lend + "\n" +
                ltrue + ":\n" +
                "storei A0 0 \n" +
                "beq T1 A0 " + leq + "\n" +
                "storei A0 1 \n" +
                lend + ":\n"
                + "//EndAndNode\n";
}

    @Override
    public String toPrint(String s) {
        return s + "Op: && \n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }
}
