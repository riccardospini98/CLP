package ast;

import ast.Types.BoolType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class BoolNode implements Node {

	private boolean val;
  
	public BoolNode (boolean _val) {
		val = _val ;
	}

	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
		return new ArrayList<SemanticError>();
 	}

	public Type typeCheck() {
		return new BoolType();
	}    
    
	public String codeGeneration() {
		return "storei A0 "+(val?1:0)+"\n";
	}
    
	public String toPrint(String s) {
		return s + String.valueOf(val) +"\n";  
	}

}  