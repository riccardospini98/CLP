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
	private STentry type ;
	private int nesting ;
  
	public IdNode (String _id) {
		id = _id ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
		ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		nesting = _nesting ;
		
		STentry st_type = ST.lookup(id, false, false) ;
		if (st_type == null)
			errors.add(new SemanticError("Id " + id + " not initialized"));
		else type = st_type ;

		return errors;
	}

	@Override
	public Type typeCheck () {
		if (type.getType() instanceof ArrowType) { //
			System.out.println("Wrong usage of function identifier");
			ErrorType err = new ErrorType();
			err.setMessage("Wrong usage of function identifier");
			return  err;
		} else return type.getType() ;
	}

	@Override
	public String codeGeneration() {
		String getAR="";
		for (int i = 0; i < nesting - type.getNesting(); i++)
	    	 getAR += "store T1 0(T1) \n";
	    return 
		       "move AL T1 \n"
		       + getAR  //risalgo la catena statica
		       + "subi T1 " + type.getOffset() +"\n" //metto offset sullo stack
			   + "store A0 0(T1) \n" ; //carico sullo stack il valore all'indirizzo ottenuto
	}

	@Override
	public String toPrint(String s) {
		return s+"Id:" + id + " at nestlev " + type.getNesting() +"\n" ;
	}
  
}  