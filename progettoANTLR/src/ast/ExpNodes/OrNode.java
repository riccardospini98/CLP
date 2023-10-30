package ast.ExpNodes;

import ast.Node;
import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class OrNode implements Node {
    private Node left;
    private Node right;

    public OrNode(Node left, Node right) {
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
        //both must be booleans
        if ((leftOp instanceof BoolType) && (rightOp instanceof BoolType))
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
        return "//OrNode\n" +
                left.codeGeneration() +
                "pushr A0 \n" +
                right.codeGeneration() +
                "popr T1 \n" +
                "beq A0 T1 " + ltrue + "\n" +
                leq + ":\n" +
                "storei A0 1 \n" +
                "b " + lend + "\n" +
                ltrue + ":\n" +
                "storei A0 1 \n" +
                "beq T1 A0 " + leq + "\n" +
                "storei A0 0 \n" +
                lend + ":\n" +
                "//EndOrNode\n";
    }

    @Override
    public String toPrint(String s) {
        return s + "Op: || \n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }
}
