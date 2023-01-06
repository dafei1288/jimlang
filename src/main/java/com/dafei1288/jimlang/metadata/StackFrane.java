package com.dafei1288.jimlang.metadata;

import java.util.Stack;

public class StackFrane extends Stack<Symbol> {
    private String currentName;
    private Symbol currentSymbol;

    public StackFrane(){

    }

  public StackFrane(Symbol currentSymbol, String currentName) {
    this.currentName = currentName;
    this.currentSymbol = currentSymbol;
  }

  public String getCurrentName() {
    return currentName;
  }

  public void setCurrentName(String currentName) {
    this.currentName = currentName;
  }

  public Symbol getCurrentSymbol() {
    return currentSymbol;
  }

  public void setCurrentSymbol(Symbol currentSymbol) {
    this.currentSymbol = currentSymbol;
  }
}
