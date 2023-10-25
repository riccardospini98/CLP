package ast;

import ast.Types.ArrowType;
import ast.Types.ErrorType;
import ast.Types.Type;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
public class CallNode implements Node {
    private String id;
    private STentry entry;
    private ArrayList<Node> parameters;
    private int nesting;

    public CallNode(String _id, ArrayList<Node> _parameters) {
        id = _id;
        parameters = _parameters;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
        nesting = _nesting;
        STentry tmp = ST.lookup(id, false, false);
        if (tmp != null) {
            entry = tmp;
            for (Node par : parameters) {
                errors.addAll(par.checkSemantics(ST, nesting));
            }
        } else {
            errors.add(new SemanticError("ID \"" + id + "\" used but not declared"));
        }
        return errors;
    }

    @Override
    public Type typeCheck() {
        Type _type = entry.getType();
        if (_type instanceof ArrowType) {
            ArrayList<Type> _partype = ((ArrowType) _type).get_inputtype();
            if (_partype.size() != parameters.size()) {
                System.err.println("[X] ERROR: Wrong number of parameters in the invocation of " + id);
                ErrorType err = new ErrorType();
                err.setMessage("[X] ERROR: Wrong number of parameters in the invocation of " + id);
                return err;
            } else {
                boolean ok = true;
                String errorMsg = "";
                for (int i = 0; i < parameters.size(); i++) {
                    Type par_i = (parameters.get(i)).typeCheck();
                    if (!(par_i.getClass().equals(_partype.get(i).getClass()))) {
                        System.err.println("[X] ERROR-TypeError: Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id);
                        errorMsg += "[X] ERROR-TypeError: Wrong type for " + (i + 1) + "-th parameter in the invocation of " + id + "\n";
                        ok = false;
                    }
                }
                if (ok) return ((ArrowType) _type).get_outputtype();
                else {
                    ErrorType err = new ErrorType();
                    err.setMessage(errorMsg);
                    return err;
                }
            }
        } else {
            System.err.println("[X] ERROR-TypeError: Invocation of a non-function " + id);
            ErrorType err = new ErrorType();
            err.setMessage("[X] ERROR-TypeError: Invocation of a non-function " + id);
            return err;
        }
    }

    @Override
    public String codeGeneration() {
        String parCode = "";
        for (int i = 0; i < parameters.size(); i = i + 1)
            parCode += parameters.get(i).codeGeneration() + "pushr A0\n";

        String getAR = "";
        for (int i = 0; i < nesting - entry.getNesting(); i++)
            getAR += "store T1 0(T1) \n";
        // formato AR: control_link + access link + parameters + indirizzo di ritorno + dich_locali

        return "//CallNode\npushr FP \n"            // carico il frame pointer
                + "move SP FP \n"
                + "addi FP 1 \n"    // salvo in FP il puntatore all'indirizzo del frame pointer caricato
                + "move AL T1\n"        // risalgo la catena statica
                + getAR
                + "pushr T1 \n"            // salvo sulla pila l'access link statico
                + parCode                // calcolo i parametri attuali con l'access link del chiamante
                + "move FP AL \n"
                + "subi AL 1 \n"
                + "jsub " + entry.getLabel() + "\n";
    }

    @Override
    public String toPrint(String s) {  //
        String parlstr = "";
        for (Node par : parameters)
            parlstr += par.toPrint(s + "  ");
        return s + "Call:" + id + " at nestlev " + nesting + "\n" + entry.getType().toPrint(s + "  ") + parlstr;
    }
}  