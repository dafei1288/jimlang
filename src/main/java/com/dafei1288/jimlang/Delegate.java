package com.dafei1288.jimlang;

import java.util.ArrayList;
import java.util.List;

// Opaque delegate handle: stores target function name and optional bound arguments.
public final class Delegate {
  private final String name;
  private final List<Object> bound; // immutable snapshot

  public Delegate(String name){ this(name, null); }
  public Delegate(String name, List<Object> bound){
    this.name = name == null ? "" : name;
    if (bound == null) this.bound = new ArrayList<>(); else this.bound = new ArrayList<>(bound);
  }
  public String getName(){ return name; }
  public List<Object> getBound(){ return new ArrayList<>(bound); }
  public Delegate withBound(Object arg){ List<Object> b = new ArrayList<>(bound); b.add(arg); return new Delegate(name, b); }
  public Delegate withBound(Object a, Object b){ List<Object> l = new ArrayList<>(bound); l.add(a); l.add(b); return new Delegate(name, l); }
  @Override public String toString(){ return "Delegate("+name+", bound="+bound.size()+")"; }
}