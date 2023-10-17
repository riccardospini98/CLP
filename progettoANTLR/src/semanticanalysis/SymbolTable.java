package semanticanalysis;

import ast.Types.BoolType;
import ast.Types.IntType;
import ast.Types.Type;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
	private ArrayList<HashMap<String,STentry>>  symbol_table ;
	private ArrayList<Integer> offset;
	
	public SymbolTable() {
		symbol_table = new ArrayList<HashMap<String,STentry>>() ;
		offset = new ArrayList<Integer>() ;
	}
	
	public Integer nesting() {
		return symbol_table.size() -1 ;
	}

	//TODO: setInit(id) setta id nella symbol table (magari specificando lvl nesting) con init=true;

	public STentry lookup(String id, boolean declaring, boolean initializing) {
		int n = symbol_table.size() - 1 ;
		boolean found = false ;
		STentry T = null ;
		while ((n >= 0) && !found) {
			HashMap<String,STentry> H = symbol_table.get(n) ;
			T = H.get(id) ;
			if (T != null) found = true ;
			else n = n-1 ;
		}
		if (found && initializing) {
			T.setInit();
		}
		if (found && !initializing && !declaring && !T.isInitialized()) {
			System.out.println("[X] ERROR: Symbol "+ id + " must be initialized before use");
		}
		return T ;
	}

	public void add(HashMap<String,STentry> H) {
		symbol_table.add(H) ;
		offset.add(1) ;		// si inizia da 2 perche` prima ci sonop FP e AL
	}
	
	public void remove() {
		int x = symbol_table.size() ;
		symbol_table.remove(x-1) ;
		offset.remove(x-1) ;
	}
	
	public boolean top_lookup(String id) {
		int n = symbol_table.size() - 1 ;
		STentry T = null ;
		HashMap<String,STentry> H = symbol_table.get(n) ;
		T = H.get(id) ;
		return (T != null) ;
	}
	
	public void insert(String id, Type type, int _nesting, String _label, Boolean _init) {
		int n = symbol_table.size() - 1 ;
		HashMap<String,STentry> H = symbol_table.get(n) ;
		symbol_table.remove(n) ;
		int offs = offset.get(n) ;
		offset.remove(n) ;
		STentry idType = new STentry(type, offs, _nesting, _label, _init) ;
		H.put(id,idType) ;
		symbol_table.add(H) ;
		if (type.getClass().equals((new BoolType()).getClass()))
			offs = offs + 1 ; // we always increment the offset by 1 otherwise we need ad-hoc
							  // bytecode operations
		else if (type.getClass().equals((new IntType()).getClass()))
			offs = offs + 1 ;
		else offs = offs + 1 ;
		offset.add(offs) ;	
	}

	public void increaseoffset() {
		int n = offset.size() - 1 ;
		int offs = offset.get(n) ;
		offset.remove(n) ;
		offs = offs + 1 ;
		offset.add(offs) ;	
	}

	public void toPrint(String fun, int nesting){
		System.out.println("--------------------------------");
		System.out.println("ST"+nesting+": "+fun);
		for(HashMap<String,STentry> h : symbol_table){
			if(h.size()>0){
				System.out.println("[");
				h.forEach((s,st) -> {
					System.out.println(s+st.toPrint());
				});
				System.out.println("]");
 		}
			else System.out.println("[]");
		}
	}

}
