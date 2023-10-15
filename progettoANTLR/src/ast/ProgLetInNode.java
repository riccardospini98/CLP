package ast;

import ast.Types.Type;
import ast.Types.VoidType;
import evaluator.SimpLanlib;
import semanticanalysis.STentry;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgLetInNode implements Node {
	
	private ArrayList<Node> dec ;
	private ArrayList<Node> stm ;
	private Node exp ;
	private int nesting ;
  
	public ProgLetInNode (ArrayList<Node> _dec, ArrayList<Node> _stm, Node _exp) {
		dec = _dec ;
		stm = _stm ;
		exp = _exp ;
	}
	public ProgLetInNode (ArrayList<Node> _dec, ArrayList<Node> _stm) {
		dec = _dec ;
		stm = _stm;
	}
  
	public ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting) {
		  nesting = _nesting + 1 ;
	      HashMap<String,STentry> H = new HashMap<String, STentry>();
	      ST.add(H);

	      //declare resulting list
	      ArrayList<SemanticError> errors = new ArrayList<SemanticError>();
	      
	      for (Node d : dec) {
	    	  	errors.addAll(d.checkSemantics(ST, nesting)) ; // nella sintassi non ci sono annidamenti di let
	      }

	      for (Node s : stm) {
			  errors.addAll(s.checkSemantics(ST, nesting));
		  }

	      if(exp != null)
			  errors.addAll(exp.checkSemantics(ST, nesting)) ;
	      
	      //clean the scope leaving a let scope
	      ST.remove();
	      
	      //return the result
	      return errors;
	}
	public Type typeCheck () {
		for (Node d: dec)
		    d.typeCheck();
		if(exp != null) {
			return exp.typeCheck();
		}
		else {
			return new VoidType();
		}
	}
		  
	public String codeGeneration() {
		String declCode="";
		for (Node d: dec)
				    declCode += d.codeGeneration();
		return  "move SP FP  \n"
				+ "pushr FP \n"
				+ "move SP AL \n"
				+ "pushr AL \n"
				+ declCode 
				+ exp.codeGeneration() 
				+ "halt\n" +
				SimpLanlib.getCode();
	} 
		  
	public String toPrint(String s) {
		String declstr="";
		for (Node d : dec)
			declstr += d.toPrint(s+"\t");
		return s+"ProgLetIn\n" + declstr + "\n" + exp.toPrint(s+"\t") ;
	}
     
}  