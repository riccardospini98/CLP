package ast.Types;

import ast.Types.Type;

public class ErrorType extends Type {
	private String message ;
	public String toPrint(String s) {
		return s + "Error\n" ;
	}

	public void setMessage(String _message) {
		this.message = _message;
	}

	public String getMessage() {
		return message;
	}
}
