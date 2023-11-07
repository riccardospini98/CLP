package semanticanalysis;

import ast.Types.BoolType;
import ast.Types.IntType;
import ast.Types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class SymbolTable {
	private ArrayList<HashMap<String,STentry>>  symbol_table ;
	private ArrayList<Integer> offset;

	private ArrayList<SemanticError> errors;
	
	public SymbolTable(ArrayList<SemanticError> errors) {
		symbol_table = new ArrayList<HashMap<String,STentry>>() ;
		offset = new ArrayList<Integer>() ;
		this.errors = errors;
	}

	public STentry lookup(String id, boolean declaring, boolean initializing) {
		int n = symbol_table.size() - 1 ;
		boolean found = false ;
		STentry T = null ;
		boolean deep = false;
		boolean warn = false;
		while ((n >= 0) && !found) {
			if(deep) {
				warn = true;
			}
			HashMap<String,STentry> H = symbol_table.get(n) ;
			T = H.get(id) ;
			if (T != null) found = true ;
			else n = n-1 ;
			if (!deep) {
				deep = true;
			}
		}

		if (found && initializing) {
			T.setInit(true);
		}

		if (!found && !declaring) {
			SemanticError err = new SemanticError("\t Symbol \""+ id + "\" must be declared before use");
			errors.add(err);
		}

		if (found && !initializing && !declaring && !T.isInitialized()) {
			if(!warn) {
				SemanticError err = new SemanticError("\t Symbol \"" + id + "\" must be initialized before use");
				errors.add(err);
			} else {
				String ANSI_RESET = "\u001B[0m";
				String ANSI_YELLOW = "\u001B[33m";
				System.out.println(ANSI_YELLOW +"WARNING -  Symbol \"" + id + "\" could be not initialized when used"+ ANSI_RESET);
			}
		}
		return T ;
	}

	public void add(HashMap<String,STentry> H) {
		symbol_table.add(H) ;
		offset.add(1) ;		// Prima ci sono FP e AL
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

	public STentry find(String id) {
		int n = symbol_table.size()-1;
		boolean found = false;
		STentry result = null;
		while (n>-1 && !found) {
			HashMap<String, STentry> x = symbol_table.get(n);
			if(x.get(id) != null) {
				found = true;
				result = x.get(id);
			} else n-=1;
		}
		return result;
	}

	public void increaseOffset() {
		int n = offset.size() - 1 ;
		int offs = offset.get(n) ;
		offset.remove(n) ;
		offs = offs + 1 ;
		offset.add(offs) ;	
	}

	public SymbolTable saveSymbolTable() {
		SymbolTable newST = new SymbolTable(errors);
		ArrayList<HashMap<String, STentry>> hashMapNewST = new ArrayList<>();
		ArrayList<Integer> offsetNewST = new ArrayList<>();

		for (HashMap<String, STentry> s : this.symbol_table) {
			HashMap<String, STentry> h = new HashMap<>();
			for (String k : s.keySet()) {
				STentry savedKey = s.get(k);
				STentry savedValue = new STentry(savedKey.getType(), savedKey.getOffset(),
						savedKey.getNesting(), savedKey.getLabel(), savedKey.isInitialized());
				h.put(k, savedValue);
			}
			hashMapNewST.add(h);
		}
		newST.symbol_table = hashMapNewST;

		for (Integer o : this.offset) {
			offsetNewST.add(o);
		}

		newST.offset = offsetNewST;

		return newST;
	}

	private boolean contains(String key, STentry value) {
		for (HashMap<String, STentry> hashMap : this.symbol_table) {
			for (Map.Entry<String, STentry> entry : hashMap.entrySet()) {
				boolean cond1 = entry.getKey().equals(key);
				boolean cond2 = entry.getValue().equals(value);
				if (cond1 && cond2) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<HashMap<String, STentry>> intersectSymbolTables(SymbolTable otherTable) {
		ArrayList<HashMap<String, STentry>> intersection = new ArrayList<>();

		for (HashMap<String, STentry> hashMap : symbol_table) {
			HashMap<String, STentry> intersectionHashMap = new HashMap<>();

			for (Map.Entry<String, STentry> entry : hashMap.entrySet()) {
				String key = entry.getKey();
				STentry value = entry.getValue();

				if (otherTable.contains(key, value)) {
					intersectionHashMap.put(key, value);
				}
			}

			if (!intersectionHashMap.isEmpty()) {
				intersection.add(intersectionHashMap);
			}
		}

		return intersection;
	}

	public void mergeSymbolTable(ArrayList<HashMap<String, STentry>> otherST) {
		for (HashMap<String, STentry> otherHashMap : otherST) {
			for (String key : otherHashMap.keySet()) {
				STentry otherEntry = otherHashMap.get(key);

				STentry toReplace = lookup(key, true, false);
				toReplace.setInit(otherEntry.isInitialized());
			}
		}
	}
}