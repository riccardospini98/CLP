package ast;

import ast.Types.BoolType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class BoolNode implements Node {

    private boolean val;

    public BoolNode(boolean _val) {
        val = _val;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        return new ArrayList<SemanticError>();
    }

    @Override
    public Type typeCheck() {
        return new BoolType();
    }

    @Override
    public String codeGeneration() {
        return "//BoolNode\nstorei A0 " + (val ? 1 : 0) + "\n";
    }

    @Override
    public String toPrint(String s) {
        return s + String.valueOf(val) + "\n";
    }

}  