package wang.datahub.mylang;

import org.antlr.v4.runtime.tree.ParseTree;

public class Symbol {

    public static enum SymbolType {
        FUNCTION,VAR,
    }

    private String name ;
    private SymbolType type;
    private ParseTree parseTree;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public ParseTree getParseTree() {
        return parseTree;
    }

    public void setParseTree(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", parseTree=" + parseTree +
                '}';
    }
}
