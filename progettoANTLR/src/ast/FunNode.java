package ast;

import ast.Types.ArrowType;
import ast.Types.ErrorType;
import ast.Types.Type;
import evaluator.SimpLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class FunNode implements Node {
    private String id;
    private Type returntype;
    private ArrayList<ParamNode> parlist;

    private Node body;
    private ArrowType type;
    private int nesting;
    private String flabel;

    public FunNode(String _id, Type _type, ArrayList<ParamNode> _parlist, Node _body) {
        id = _id;
        returntype = _type;
        parlist = _parlist;
        body = _body;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {

        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;

        if (ST.lookup(id, true, false) != null)
            errors.add(new SemanticError("Function identifier " + id + " already declared"));
        else {
            HashMap<String, STentry> HM = new HashMap<String, STentry>();
            ArrayList<Type> partypes = new ArrayList<Type>();

            ST.add(HM);

            for (ParamNode arg : parlist) {
                partypes.add(arg.getType());
                if (ST.top_lookup(arg.getId()))
                    errors.add(new SemanticError("Parameter id " + arg.getId() + " already declared"));
                else ST.insert(arg.getId(), arg.getType(), nesting + 1, "", true);
            }

            type = new ArrowType(partypes, returntype);

            ST.increaseOffset(); // aumentiamo di 1 l'offset per far posto al return value

            errors.addAll(body.checkSemantics(ST, nesting + 1));
            ST.remove();

            flabel = SimpLanlib.freshFunLabel();

            ST.insert(id, type, nesting, flabel, true);
        }
        return errors; // problemi con la generazione di codice!
    }

    @Override
    public Type typeCheck() {

        if ((body.typeCheck()).getClass().equals(returntype.getClass()))
            return null;
        else {
            System.out.println("Wrong return type for function " + id);
            ErrorType err = new ErrorType();
            err.setMessage("Wrong return type for function " + id);
            return err;
        }
    }

    @Override
    public String codeGeneration() {

        SimpLanlib.putCode(flabel + ":\n"
                + "pushr RA \n"
                + body.codeGeneration()
                + "popr RA \n"
                + "addi SP " + parlist.size() + "\n"
                + "pop \n"
                + "store FP 0(FP) \n"
                + "move FP AL \n"
                + "subi AL 1 \n"
                + "pop \n"
                + "rsub RA \n"
        );

        return "push " + flabel + "\n";
    }

    @Override
    public String toPrint(String s) {
        String parlstr = "";
        for (Node par : parlist) {
            parlstr += par.toPrint(s);
        }

        return s + "Fun" + id + "\n"
                + parlstr
                + "\n"
                + body.toPrint(s + " ");
    }

}  