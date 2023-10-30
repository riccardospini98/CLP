package ast;

import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class ParamNode implements Node {

    private String id;
    private Type type;
    private int nesting;

    public ParamNode(String _id, Type _type) {
        id = _id;
        type = _type;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;
        if (ST.top_lookup(id)) //controllo se esiste già un parametro con questo nome
            errors.add(new SemanticError("Parameter id " + id + " already declared")); //se si errore
        else
            ST.insert(id, type, nesting, "", true);
        return errors;
    }

    //non utilizzato
    @Override
    public Type typeCheck() {
        return null;
    }

    //non utilizzato
    @Override
    public String codeGeneration() {
        return "";
    }

    @Override
    public String toPrint(String s) {
        return s + "Par " + id + ":" + type.toPrint(s);
    }

}  