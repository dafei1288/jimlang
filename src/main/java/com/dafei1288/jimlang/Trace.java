package com.dafei1288.jimlang;

public final class Trace {
  private static final ThreadLocal<java.util.ArrayDeque<String>> STACK = ThreadLocal.withInitial(java.util.ArrayDeque::new);
  private static volatile boolean enabled = System.getProperty("jim.trace") != null || System.getenv("JIM_TRACE") != null;
  private Trace() {}
  public static boolean isEnabled(){ return enabled; }
  public static void setEnabled(boolean v){ enabled = v; }
  public static void log(String s){ if (enabled) System.out.println(s); }
  public static void push(String frame){ STACK.get().push(frame); }
  public static void pop(){ java.util.ArrayDeque<String> d = STACK.get(); if (!d.isEmpty()) d.pop(); }
  public static java.util.List<String> snapshot(){ return new java.util.ArrayList<>(STACK.get()); }
  public static void clear(){ STACK.get().clear(); }
}