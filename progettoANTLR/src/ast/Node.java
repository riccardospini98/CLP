package ast;

import ast.Types.Type;
import semanticanalysis.SemanticError;
import semanticanalysis.SymbolTable;

import java.util.ArrayList;

public interface Node {

	ArrayList<SemanticError> checkSemantics(SymbolTable ST, int _nesting);
	Type typeCheck();
	String codeGeneration();

	String toPrint(String s);

}  