package ast;

import ast.Types.ErrorType;
import ast.Types.IntType;
import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public class DivNode implements Node {
	  private Node left;
	  private Node right;
	  
	  public DivNode (Node _left, Node _right) {
	    left = _left ;
	    right = _right ;
	  }
	  
	  public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
		  ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
		  
		  errors.addAll(left.checkSemantics(ST, _nesting));
		  errors.addAll(right.checkSemantics(ST, _nesting));
		  
		  return errors;
		}
	  
	  public Type typeCheck() {
		  if ((left.typeCheck() instanceof IntType) && (right.typeCheck() instanceof IntType) )
			  return new IntType() ;
		  else {
			  System.out.println("Type Error: Non integers in division") ;
			  ErrorType err = new ErrorType();
			  err.setMessage("Type Error: Non integers in division");
			  return  err;
		  }
	  }  
	  
	  public String codeGeneration() {
			return 		left.codeGeneration()
					   + "pushr A0 \n" 
					   + right.codeGeneration()
					   + "popr T1 \n" 
					   + "div T1 A0 \n" 
					   + "popr A0 \n";
	  }

	  public String toPrint(String s) {
		    return s+"Div\n" + left.toPrint(s+"  ") + right.toPrint(s+"  ") ; 
	  }

}
