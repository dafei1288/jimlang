package com.dafei1288.jimlang;

public final class Trace {
  private static volatile boolean enabled =
      System.getProperty("jim.trace") != null || System.getenv("JIM_TRACE") != null;
  private Trace() {}
  public static boolean isEnabled(){ return enabled; }
  public static void setEnabled(boolean v){ enabled = v; }
  public static void log(String s){ if (enabled) System.out.println(s); }
}