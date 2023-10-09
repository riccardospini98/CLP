package ast;

import ast.Types.ErrorType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class AssignNode implements Node {

    private String id;

    private Type type;
    private Node exp;

    private int nesting;

    public AssignNode(String _id, Node _exp) {
        id = _id;
        exp = _exp;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;

        errors.addAll(exp.checkSemantics(ST, nesting));

        try {
            type = ST.lookup(id).getType();
        } catch (Exception e) {
            errors.add(new SemanticError(e.getMessage()));
        }

        return errors;
    }

    @Override
    public Type typeCheck() {
        if (exp.typeCheck().getClass().equals(type.getClass()))
            return null;
        else {
            System.out.println("Wrong return type for assignment " + id);
            return new ErrorType();
        }
    }

    @Override
    public String codeGeneration() {
        return exp.codeGeneration() +
                "pushr A0 \n"; //TODO: potrebbe mancare load di exp alla variabile
    }

    public String toPrint(String s) {
        return s + "Assignment: " + id + type.toPrint(" ") + exp.toPrint(s + " ");
    }

}
