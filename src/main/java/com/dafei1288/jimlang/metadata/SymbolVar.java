package com.dafei1288.jimlang.metadata;

public class SymbolVar extends AbstractSymbol {
  private TypeDescriptor expectedType;
  public TypeDescriptor getExpectedType(){ return expectedType; }
  public void setExpectedType(TypeDescriptor t){ this.expectedType = t; }
}