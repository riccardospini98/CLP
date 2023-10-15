package semanticanalysis;

import ast.Types.Type;

public class STentry {
	private Type type ;
	private int offset ;
	private int nesting ;
	private String label ;

	private Boolean initialized ;

	public STentry(Type _type, int _offset, int _nesting, String  _label, Boolean _initialized) {
		type = _type ;
		offset = _offset ;
		nesting = _nesting ;
		label = _label ;
		initialized = _initialized ;

	}
	
	public Type getType() {
		return type ;
	}

	public int getOffset() {
		return offset ;
	}
	
	public int getNesting() {
		return nesting ;
	}
	
	public String getLabel() {
		return label ;
	}

	public String toPrint(){
		return "|"+nesting+"|"+offset+"|"+label+"|"+type.toPrint("");
	}
}
