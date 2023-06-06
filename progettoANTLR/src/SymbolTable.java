import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class SymbolTable {
    private final List<Symbol> identifiers;
    private final List<Symbol> references;
    private final List<Symbol> undeclaredIdentifiers;
    private final List<Symbol> duplicateIdentifiers;

    public SymbolTable() {
        this.identifiers = new ArrayList<>();
        this.references = new ArrayList<>();
        this.undeclaredIdentifiers = new ArrayList<>();
        this.duplicateIdentifiers = new ArrayList<>();
    }

    public void addIdentifier(Symbol identifier) {
        identifiers.add(identifier);
    }

    public void addReference(Symbol identifier) {
        if(!references.contains(identifier)) {
            references.add(identifier);
        }
    }

    public void addUndeclaredIdentifier(Symbol identifier) {
        if(!undeclaredIdentifiers.contains(identifier)) {
            undeclaredIdentifiers.add(identifier);
        }
    }

    public void addDuplicateIdentifier(Symbol identifier) {
        duplicateIdentifiers.add(identifier);
    }

    public List<Symbol> getIdentifiers() {
        return identifiers;
    }

    public boolean isDeclared(Symbol symbol) {
        for (Symbol i: identifiers) {
            if (symbol.getName().equals(i.getName()) &&
                symbol.getNestingLvl().equals(i.getNestingLvl())) {
                return true;
            }
        }
        return false;
    }
    public List<Symbol> getReferences() {
        return references;
    }

    public List<Symbol> getUndeclaredIdentifiers() {

        return undeclaredIdentifiers;
    }

    public List<Symbol> getDuplicateIdentifiers() {
        return duplicateIdentifiers;
    }
}