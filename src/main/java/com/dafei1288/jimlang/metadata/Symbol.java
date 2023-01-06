package com.dafei1288.jimlang.metadata;

import org.antlr.v4.runtime.tree.ParseTree;

public interface Symbol {
  String getName();

  void setName(String name);

  SymbolType getType();

  void setType(SymbolType type);

  ParseTree getParseTree();

  void setParseTree(ParseTree parseTree);

  String getTypeName() ;

  void setTypeName(String typeName) ;

  Object getValue() ;

  void setValue(Object value);
}
