package ast;

import ast.Types.IntType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class IntNode implements Node {
	private Integer val;
  
	public IntNode (Integer _val) {
		val = _val ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
 	 	  return new ArrayList<SemanticError>();
	}

	@Override
	public Type typeCheck(){
		return new IntType();
	}

	@Override
	public String codeGeneration() {
		return "//IntNode\nstorei A0 "+val+"\n";
	}

	@Override
	public String toPrint(String s) {
	    return s + Integer.toString(val) +"\n";  
	}
}  