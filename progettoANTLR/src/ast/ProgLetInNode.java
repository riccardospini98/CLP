package ast;

import ast.Types.ErrorType;
import ast.Types.Type;
import ast.Types.VoidType;
import evaluator.SimpLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ProgLetInNode implements Node {

    private ArrayList<Node> dec;
    private ArrayList<Node> stm;
    private Node exp;
    private int nesting;

    public ProgLetInNode(ArrayList<Node> _dec, ArrayList<Node> _stm, Node _exp) {
        dec = _dec;
        stm = _stm;
        exp = _exp;
    }

    public ProgLetInNode(ArrayList<Node> _dec, ArrayList<Node> _stm) {
        dec = _dec;
        stm = _stm;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        nesting = _nesting + 1;
        HashMap<String, STentry> H = new HashMap<String, STentry>();
        ST.add(H);

        //declare resulting list
        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

        for (Node d : dec) {
            errors.addAll(d.checkSemantics(ST, nesting)); // nella sintassi non ci sono annidamenti di let
        }

        for (Node s : stm) {
            errors.addAll(s.checkSemantics(ST, nesting));
        }

        if (exp != null)
            errors.addAll(exp.checkSemantics(ST, nesting));

        //clean the scope leaving a let scope
        ST.remove();

        //return the result
        return errors;
    }

    @Override
    public Type typeCheck() {
        ArrayList<Type> types = new ArrayList<>();
        for (Node d : dec)
            types.add(d.typeCheck());
        for (Node s : stm)
            types.add(s.typeCheck());
        for (Type t : types){
            if (t instanceof ErrorType) {
                return t;
            }
        }
        if (exp != null) {
            return exp.typeCheck();
        } else {
            return new VoidType();
        }
    }

    @Override
    public String codeGeneration() {
        String declCode = "";
        String stmCode = "";
        String expCode = "";
        for (Node d: dec)
            declCode += d.codeGeneration();
        if(stm!=null){
            for (Node s: stm)
                stmCode += s.codeGeneration();
        }
        if (exp!=null) expCode = exp.codeGeneration();

        return "//ProgLetIn\nmove SP FP  \n"
                + "pushr FP \n"
                + "move SP AL \n"
                + "pushr AL \n"
                + declCode
                + stmCode
                + expCode
                + "halt\n"
                + SimpLanlib.getCode();
    }

    @Override
    public String toPrint(String s) {
        String declstr = "";
        for (Node d : dec)
            declstr += d.toPrint(s + "\t");
        return s + "ProgLetIn\n" + declstr + "\n" + exp.toPrint(s + "\t");
    }

}  