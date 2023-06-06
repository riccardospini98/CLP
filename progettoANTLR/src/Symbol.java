public class Symbol {
    private String name;
    private Integer nestingLvl;

    private String type;

    public Symbol(String type, String name, Integer nestingLvl) {
        this.type = type;
        this.name = name;
        this.nestingLvl = nestingLvl;
    }

    private void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNestingLvl(Integer nestingLvl) {
        this.nestingLvl = nestingLvl;
    }

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public Integer getNestingLvl() {
        return nestingLvl;
    }

    @Override
    public String toString() {
        return type + " " + name + " - nesting level: " + nestingLvl;
    }
}
