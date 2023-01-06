package com.dafei1288.jimlang.metadata;

import java.util.List;

public class SymbolFunction extends AbstractSymbol {

  private List<String> parameterList;
  private String functionBody;


  public List<String> getParameterList() {
    return parameterList;
  }

  public void setParameterList(List<String> parameterList) {
    this.parameterList = parameterList;
  }

  public String getFunctionBody() {
    return functionBody;
  }

  public void setFunctionBody(String functionBody) {
    this.functionBody = functionBody;
  }
}
