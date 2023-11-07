package ast;

import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class DecNode implements Node {
    private String id;
    private Node type;
    private int nesting;

    public DecNode(String _id, Node _type) {
        id = _id;
        type = _type;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;

        if (ST.top_lookup(id) == true)
            errors.add(new SemanticError("\tVar id " + id + " already declared"));
        else ST.insert(id, (Type) type, nesting, "", false);

        return errors;
    }

    @Override
    public Type typeCheck() {
        return null;
    }

    @Override
    public String codeGeneration() {
        return "//DecNode\npush 0 \n//EndDecNode\n";
    }

    @Override
    public String toPrint(String s) {
        return s + "Var:" + id + ":" + type.toPrint(s) + "\n";
    }

}  