package com.dafei1288.jimlang.metadata;

public class SymbolVar extends AbstractSymbol {
  private TypeDescriptor expectedType;
  private boolean constant;
  private boolean assigned;

  public TypeDescriptor getExpectedType(){ return expectedType; }
  public void setExpectedType(TypeDescriptor t){ this.expectedType = t; }

  public boolean isConst(){ return constant; }
  public void setConst(boolean c){ this.constant = c; }

  public boolean isAssigned(){ return assigned; }
  public void setAssigned(boolean a){ this.assigned = a; }
}