package com.dafei1288.jimlang;

public final class Host {
  private static final ThreadLocal<JimLangVistor> CURRENT = new ThreadLocal<>();
  private Host() {}
  public static void set(JimLangVistor v){ CURRENT.set(v); }
  public static JimLangVistor current(){ return CURRENT.get(); }
  public static void clear(){ CURRENT.remove(); }
}