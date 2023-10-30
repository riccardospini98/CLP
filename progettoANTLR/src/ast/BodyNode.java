package ast;

import ast.Types.Type;
import ast.Types.VoidType;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class BodyNode implements Node {
    private ArrayList<Node> decs; //FIXME va bene node?

    private ArrayList<Node> stms;

    private Node exp;
    private int nesting;

    public BodyNode(ArrayList<Node> _dec, ArrayList<Node> _stm, Node _exp) {
        decs = _dec;
        stms = _stm;
        exp = _exp;
    }
    public BodyNode(ArrayList<Node> _dec, ArrayList<Node> _stm) {
        decs = _dec;
        stms = _stm;
    }

    @Override
    public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
        nesting = _nesting;
        HashMap<String, STentry> H = new HashMap<String, STentry>();
        ST.add(H);

        ArrayList<SemanticError> errors = new ArrayList<SemanticError>();

        if (decs != null) {
            for (Node d : decs) {
                errors.addAll(d.checkSemantics(ST, nesting));
            }
        }

        if (stms != null) {
            for (Node s : stms) {
                errors.addAll(s.checkSemantics(ST, nesting));
            }
        }

        if (exp != null) {
            errors.addAll(exp.checkSemantics(ST, nesting));
        }

        ST.remove();

        return errors;
    }

    @Override
    public Type typeCheck() {
        if (decs != null) {
            for (Node d : decs)
                d.typeCheck();
        }

        if (stms != null) {
            for (Node s : stms)
                s.typeCheck();
        }

        if (exp != null) {
            return exp.typeCheck();
        }

        return new VoidType();
    }

    @Override
    public String codeGeneration() {
        String decCode = "";
        String stmCode = "";
        String expCode = "";

        if (decs != null) {
            for (Node d : decs)
                decCode += d.codeGeneration();
        }

        if (stms != null) {
            for (Node s : stms)
                stmCode += s.codeGeneration();
        }

        if (exp != null) {
            expCode += exp.codeGeneration();
        }

        return "//BodyNode\n"
                //+ "move SP FP  \n"
                //+ "pushr FP \n"
                //+ "move SP AL \n"
                //+ "pushr AL \n"
                + decCode
                + stmCode
                + expCode
                + "addi SP " + (decs != null ? decs.size() : 0) + "\n"
                //+ "pop\n"
                //+ "popr FP\n"
                //+ "move FP AL\n"
                //+ "subi AL 1\n"
                + "//EndBodyNode\n";

    }

    @Override
    public String toPrint(String s) {
        String decStr = "";
        String stmStr = "";
        String expStr = "";

        if (decs != null) {
            for (Node d : decs)
                decStr += d.toPrint(s + "\t");
        }

        if (stms != null) {
            for (Node st : stms)
                stmStr += st.toPrint(s + "\t");
        }

        if (exp != null) {
            expStr += exp.toPrint(s + "\t");
        }

        return s + "FunBody\n" + decStr + stmStr + "\n" + expStr;
    }
}
