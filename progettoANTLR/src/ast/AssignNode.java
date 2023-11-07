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

    private STentry entry;

    public AssignNode(String _id, Node _exp) {
        id = _id;
        exp = _exp;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;
        STentry tmpEntry = ST.find(id);
        if (tmpEntry == null) {
            errors.add(new SemanticError("\tSymbol \""+ id + "\n must be declared before use"));
            return errors;
        }

        errors.addAll(exp.checkSemantics(ST, nesting));

        try {
            STentry entry = ST.lookup(id, false, true);
            if(entry == null)
                errors.add(new SemanticError("\tID \"" + id + "\" used but not declared"));
            else {
                type = entry.getType();
                this.entry = entry;
            }
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
        for (int i = 0; i < nesting - entry.getNesting(); i++)
            getAR += "store T1 0(T1) \n";

        return "//AssignNode\n"
                + exp.codeGeneration()
                + "move AL T1 \n"
                + getAR  //risalgo la catena statica
                + "subi T1 " + entry.getOffset()+ "\n" //metto offset sullo stack
                + "load A0 0(T1) \n"
                + "//EndAssignNode\n";
    }

    public String toPrint(String s) {
        return s + "Assignment: " + id + type.toPrint(" ") + exp.toPrint(s + " ");
    }

}
