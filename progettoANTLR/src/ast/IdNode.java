package ast;

import ast.Types.ArrowType;
import ast.Types.ErrorType;
import ast.Types.Type;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class IdNode implements Node {
	private String id ;
	private STentry entry ;
	private int nesting ;
  
	public IdNode (String _id) {
		id = _id ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		
		STentry sTentry = ST.lookup(id, false, false) ;
		if (sTentry == null)
			errors.add(new SemanticError("\tId " + id + " not initialized"));
		else entry = sTentry ;

		return errors;
	}

	@Override
	public Type typeCheck () {
		if (entry.getType() instanceof ArrowType) { //
			System.err.println("[X] ERROR-TypeError:  Wrong usage of function identifier");
			ErrorType err = new ErrorType();
			err.setMessage("[X] ERROR-TypeError:  Wrong usage of function identifier");
			return  err;
		} else return entry.getType() ;
	}

	@Override
	public String codeGeneration() {

		String getAR="";
		for (int i = 0; i < nesting - entry.getNesting(); i++)
	    	 getAR += "store T1 0(T1) //searching " + id+" at nesting "+nesting+"-"+entry.getNesting()+"\n";
	    return 
		       "//IDNode\nmove AL T1 \n"
		       + getAR  //risalgo la catena statica
		       + "subi T1 " + entry.getOffset() +"\n" //metto offset sullo stack
			   + "store A0 0(T1) \n"  //carico sullo stack il valore all'indirizzo ottenuto
				+ "//EndIDNode\n";
	}

	@Override
	public String toPrint(String s) {
		return s+"Id:" + id + " at nesting lvl " + entry.getNesting() +"\n" ;
	}
  
}  