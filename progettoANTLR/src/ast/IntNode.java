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
	
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
 	 	  return new ArrayList<SemanticError>();
	}
 	  
	public Type typeCheck(){
		return new IntType();
	} 
  
	public String codeGeneration() {
		return "storei A0 "+val+"\n";
	}

	public String toPrint(String s) {
	    return s + Integer.toString(val) +"\n";  
	}
}  