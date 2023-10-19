package semanticanalysis;

import ast.Types.Type;

import java.util.Objects;

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

	public boolean equals(STentry other) {
		return Objects.equals(this.label, other.label) &&
				this.offset == other.offset &&
				this.initialized == other.initialized &&
				this.type == other.type &&
				this.nesting == other.nesting;
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

	public Boolean isInitialized() { return initialized ; }

	public void setInit(boolean init) { initialized = init; }
	public String toPrint(){
		return "|"+nesting+"|"+offset+"|"+label+"|"+type.toPrint("");
	}
}
