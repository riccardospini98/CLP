package ast.ExpNodes;

import ast.Node;
import ast.Types.ErrorType;
import ast.Types.IntType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class DivNode implements Node {
    private Node left;
    private Node right;

    public DivNode(Node _left, Node _right) {
        left = _left;
        right = _right;
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
        if ((left.typeCheck() instanceof IntType) && (right.typeCheck() instanceof IntType))
            return new IntType();
        else {
            System.err.println("[X] ERROR-TypeError:  Non integers in division");
            ErrorType err = new ErrorType();
            err.setMessage("[X] ERROR-TypeError:  Non integers in division");
            return err;
        }
    }

    @Override
    public String codeGeneration() {
        return "//DivNode\n"+left.codeGeneration()
                + "pushr A0 \n"
                + right.codeGeneration()
                + "popr T1 \n"
                + "div T1 A0 \n"
                + "popr A0 \n"
                + "//EndDivNode\n";
    }

    @Override
    public String toPrint(String s) {
        return s + "Div\n" + left.toPrint(s + "  ") + right.toPrint(s + "  ");
    }

}
