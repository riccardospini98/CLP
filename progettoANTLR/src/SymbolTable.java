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
        references.add(identifier);
    }

    public void addUndeclaredIdentifier(String identifier) {
        undeclaredIdentifiers.add(identifier);
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
        List<String> result = new ArrayList<>();
        for (String identifier : references) {
            if (!identifiers.contains(identifier)) {
                result.add(identifier);
            }
        }
        return result;
    }

    public List<String> getDuplicateIdentifiers() {
        return duplicateIdentifiers;
    }
}