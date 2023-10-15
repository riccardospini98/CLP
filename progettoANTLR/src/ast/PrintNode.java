package ast;

import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class PrintNode implements Node {
	private Node exp;
	
	public PrintNode (Node _exp) {
		    exp = _exp ;
	}

	@Override
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
	 	  return exp.checkSemantics(ST, _nesting);
	 	}

	@Override
	public Type typeCheck() {
		    return exp.typeCheck();
	}

	@Override
	public String codeGeneration() {
				return exp.codeGeneration()+"print\n";
	}

	@Override
	public String toPrint(String s) {
		return s + "Print\n" +exp.toPrint(s+"  ") ;
	}
  
}  