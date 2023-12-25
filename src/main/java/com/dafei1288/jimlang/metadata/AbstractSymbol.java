package com.dafei1288.jimlang.metadata;

import org.antlr.v4.runtime.tree.ParseTree;

public class AbstractSymbol implements Symbol {


    private String name ;
    private SymbolType type;
    private ParseTree parseTree;

    private String typeName;
    private Object value;

    private Scope scope;


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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "AbstractSymbol{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", parseTree=" + parseTree +
            ", dataType='" + typeName + '\'' +
            ", value=" + value +
            '}';
    }
}
