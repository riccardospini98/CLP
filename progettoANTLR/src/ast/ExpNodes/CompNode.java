package ast.ExpNodes;

import ast.Node;
import ast.Types.BoolType;
import ast.Types.ErrorType;
import ast.Types.IntType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class CompNode implements Node {

    private String op;
    private Node left;
    private Node right;

    public CompNode(String op, Node left, Node right) {
        this.op = op;
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
        if ((leftOp instanceof IntType) && (rightOp instanceof IntType)) {
            return new BoolType();
        } else {
            System.err.println("[X] ERROR-TypeError:  incompatible types in left and right operands");
            ErrorType err = new ErrorType();
            err.setMessage("[X] ERROR-TypeError:  incompatible types in left and right operands");
            return  err;
        }
    }

    @Override
    public String codeGeneration() {
        String ltrue = SimpLanlib.freshLabel();
        String lend = SimpLanlib.freshLabel();
        String leq = SimpLanlib.freshLabel();
        String ret = "//ComparisonNode\n";
        switch (op) {
            case ">":
                ret += left.codeGeneration() +
                        "pushr A0 " + " //CompExpNode \n" +
                        right.codeGeneration() +
                        "popr T1 \n" +
                        "bleq A0 T1 " + ltrue + "\n" + // r <= l
                        leq + ":\n" +
                        "storei A0 0 \n" + // se l < r allora no
                        "b " + lend + "\n" +
                        ltrue + ":\n" +
                        "beq T1 A0 " + leq + "\n" + //se sono uguali allora 0
                        "storei A0 1 \n" +
                        lend + ":\n";
                break;
            case "<":
                ret += left.codeGeneration() +
                        "pushr A0 " + " //CompExpNode \n" +
                        right.codeGeneration() +
                        "popr T1 \n" +
                        "bleq T1 A0 " + ltrue + "\n" + // l <= r
                        leq + ":\n" +
                        "storei A0 0 \n" + // se r < l allora no
                        "b " + lend + "\n" +
                        ltrue + ":\n" +
                        "beq T1 A0 " + leq + "\n" + //se sono uguali allora 0
                        "storei A0 1 \n" +
                        lend + ":\n";
                break;
            case ">=": // l >= r
                ret += left.codeGeneration() +
                        "pushr A0 " + " //CompExpNode \n" +
                        right.codeGeneration() +
                        "popr T1 \n" +
                        "bleq A0 T1 " + ltrue + "\n" + //se r <= l
                        "storei A0 0 \n" + //se così non fosse allora 0
                        "b " + lend + "\n" +
                        ltrue + ":\n" + //se vero restituisco 1
                        "storei A0 1 \n" +
                        lend + ":\n";
                break;
            case "<=": // l <= r
                ret += left.codeGeneration() +
                        "pushr A0 " + " //CompExpNode \n" +
                        right.codeGeneration() +
                        "popr T1 \n" +
                        "bleq T1 A0 " + ltrue + "\n" + //se l <= r
                        "storei A0 0 \n" + //se così non fosse allora 0
                        "b " + lend + "\n" +
                        ltrue + ":\n" + //se vero restituisco 1
                        "storei A0 1 \n" +
                        lend + ":\n";
                break;
            case "==":
                ret += left.codeGeneration() +
                        "pushr A0 \n" +
                        right.codeGeneration() +
                        "popr T1 \n" +
                        "beq A0 T1 " + ltrue + "\n" +
                        "storei A0 0\n" +
                        "b " + lend + "\n" +
                        ltrue + ":\n" +
                        "storei A0 1\n" +
                        lend + ":\n";
        }
        return ret + " //EndCompExpNode \n";
    }

    @Override
    public String toPrint(String s) {
        return s + "Op:" + op + "\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }
}
