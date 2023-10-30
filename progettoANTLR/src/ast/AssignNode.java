package ast;

import ast.Types.ErrorType;
import ast.Types.Type;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class AssignNode implements Node {

    private String id;
    private Type type;
    private Node exp;
    private int nesting;
    private int offset;
    private int nestingNode;

    public AssignNode(String _id, Node _exp) {
        id = _id;
        exp = _exp;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;
        nestingNode = ST.getNesting(id);
        offset = ST.find(id).getOffset();

        errors.addAll(exp.checkSemantics(ST, nesting));

        try {
            STentry entry = ST.lookup(id, false, true);
            if(entry == null)
                errors.add(new SemanticError("ID \"" + id + "\" used but not declared"));
            else
                type = entry.getType();
            } catch (Exception e) {
            errors.add(new SemanticError(e.getMessage()));
        }

        return errors;
    }

    @Override
    public Type typeCheck() {
        if (exp.typeCheck().getClass().equals(type.getClass())) {
            return null;
        } else {
            System.err.println("[X] ERROR-TypeError: Wrong type for assignment of " + id);
            ErrorType err = new ErrorType();
            err.setMessage("[X] ERROR-TypeError: Wrong type for assignment of " + id);
            return  err;
        }
    }

    @Override
    public String codeGeneration() {
        String getAR="";
        for (int i = 0; i < nesting - nestingNode; i++)
            getAR += "store T1 0(T1) \n";

        return "//AssignNode\n"
                + exp.codeGeneration()
                + "move AL T1 \n"
                + getAR  //risalgo la catena statica
                + "subi T1 " + offset + "\n" //metto offset sullo stack
                + "load A0 0(T1) \n"
                + "//EndAssignNode\n";
    }

    public String toPrint(String s) {
        return s + "Assignment: " + id + type.toPrint(" ") + exp.toPrint(s + " ");
    }

}
