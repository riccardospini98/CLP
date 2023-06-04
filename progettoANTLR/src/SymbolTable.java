import java.util.ArrayList;
import java.util.List;

class SymbolTable {
    private final List<String> identifiers;
    private final List<String> references;
    private final List<String> undeclaredIdentifiers;
    private final List<String> duplicateIdentifiers;

    public SymbolTable() {
        this.identifiers = new ArrayList<>();
        this.references = new ArrayList<>();
        this.undeclaredIdentifiers = new ArrayList<>();
        this.duplicateIdentifiers = new ArrayList<>();
    }

    public void addIdentifier(String identifier) {
        identifiers.add(identifier);
    }

    public void addReference(String identifier) {
        if(!references.contains(identifier)) {
            references.add(identifier);
        }
    }

    public void addUndeclaredIdentifier(String identifier) {
        if(!undeclaredIdentifiers.contains(identifier)) {
            undeclaredIdentifiers.add(identifier);
        }
    }

    public void addDuplicateIdentifier(String identifier) {
        duplicateIdentifiers.add(identifier);
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public List<String> getReferences() {
        return references;
    }

    public List<String> getUndeclaredIdentifiers() {

        return undeclaredIdentifiers;
    }

    public List<String> getDuplicateIdentifiers() {
        return duplicateIdentifiers;
    }
}