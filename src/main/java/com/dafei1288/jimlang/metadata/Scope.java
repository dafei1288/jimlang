package com.dafei1288.jimlang.metadata;

public class Scope {

  private String name;
  private StatementBlockType statementBlockType;
  private Scope subScope;

  private Scope parentScope;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StatementBlockType getStatementBlockType() {
    return statementBlockType;
  }

  public void setStatementBlockType(StatementBlockType statementBlockType) {
    this.statementBlockType = statementBlockType;
  }

  public Scope getSubScope() {
    return subScope;
  }

  public void setSubScope(Scope subScope) {
    this.subScope = subScope;
  }

  public Scope getParentScope() {
    return parentScope;
  }

  public void setParentScope(Scope parentScope) {
    this.parentScope = parentScope;
  }

  public static class RootScope extends Scope{
    public RootScope(){
      this.setName("ROOT");
      this.setStatementBlockType(StatementBlockType.ROOT_BLOCK);
    }
  }

}
