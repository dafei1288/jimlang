package com.dafei1288.jimlang.sys;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Funcall {

  public static Set<String> SYS_FUNCTION_NAMES = new HashSet<>();

  static{
    // existing
    SYS_FUNCTION_NAMES.add("PRINTLN");
    SYS_FUNCTION_NAMES.add("PRINT");
    // strings
    SYS_FUNCTION_NAMES.add("LEN");
    SYS_FUNCTION_NAMES.add("TOUPPERCASE");
    SYS_FUNCTION_NAMES.add("UPPER");
    SYS_FUNCTION_NAMES.add("TOLOWERCASE");
    SYS_FUNCTION_NAMES.add("LOWER");
    SYS_FUNCTION_NAMES.add("SUBSTRING");
    SYS_FUNCTION_NAMES.add("INDEXOF");
    SYS_FUNCTION_NAMES.add("SPLIT");
    SYS_FUNCTION_NAMES.add("TRIM");
    // math
    SYS_FUNCTION_NAMES.add("MAX");
    SYS_FUNCTION_NAMES.add("MIN");
    SYS_FUNCTION_NAMES.add("ABS");
    SYS_FUNCTION_NAMES.add("ROUND");
    SYS_FUNCTION_NAMES.add("RANDOM");
    // file
    SYS_FUNCTION_NAMES.add("FILE_READ");
    SYS_FUNCTION_NAMES.add("FILE_WRITE");
    SYS_FUNCTION_NAMES.add("FILE_EXISTS");
  }

  public static boolean isSysFunction(String functionName){
    if(functionName == null) return false;
    return SYS_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT));
  }

  public static Object exec(String functionName, List<Object> params){
    Funcall f = new Funcall();
    int argc = (params == null) ? 0 : params.size();

    // pick a method with same name and parameter count
    Method method = Arrays.stream(f.getClass().getMethods())
        .filter(m -> m.getName().equals(functionName) && m.getParameterCount() == argc)
        .findFirst().orElse(null);

    if(method == null){
      method = Arrays.stream(f.getClass().getMethods())
          .filter(m -> m.getName().equalsIgnoreCase(functionName) && m.getParameterCount() == argc)
          .findFirst().orElse(null);
    }

    if(method == null){
      throw new RuntimeException("Unknown built-in function: " + functionName + " with " + argc + " args");
    }

    try {
      Object[] args = (params == null) ? new Object[0] : params.toArray();
      return  method.invoke(f,args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  // ------------- string helpers -------------
  private static String asString(Object o){ return (o == null) ? "" : String.valueOf(o); }
  private static int asInt(Object o){
    if(o instanceof Number) return ((Number)o).intValue();
    String s = asString(o).trim();
    if(s.isEmpty()) return 0;
    return Integer.parseInt(s);
  }
  private static double asDouble(Object o){
    if(o instanceof Number) return ((Number)o).doubleValue();
    String s = asString(o).trim();
    if(s.isEmpty()) return 0.0d;
    return Double.parseDouble(s);
  }
  private static boolean hasDot(String s){ return s != null && s.indexOf('.') >= 0; }

  // ------------- built-ins: print -------------
  public void println(Object obj){ System.out.println(obj); }
  public void print(Object obj){ System.out.print(obj); }

  // ------------- built-ins: string -------------
  public Integer len(Object s){ return asString(s).length(); }
  public String toUpperCase(Object s){ return asString(s).toUpperCase(Locale.ROOT); }
  public String upper(Object s){ return toUpperCase(s); }
  public String toLowerCase(Object s){ return asString(s).toLowerCase(Locale.ROOT); }
  public String lower(Object s){ return toLowerCase(s); }
  public String trim(Object s){ return asString(s).trim(); }

  public String substring(Object s, Object start, Object end){
    String str = asString(s);
    int st = asInt(start);
    int en = asInt(end);
    if(st < 0) st = 0;
    if(en > str.length()) en = str.length();
    if(st > en) return "";
    return str.substring(st,en);
  }
  public String substring(Object s, Object start){
    String str = asString(s);
    int st = asInt(start);
    if(st < 0) st = 0;
    if(st > str.length()) st = str.length();
    return str.substring(st);
  }
  public Integer indexOf(Object s, Object sub){ return asString(s).indexOf(asString(sub)); }
  public String split(Object s, Object sep){
    String[] parts = asString(s).split(java.util.regex.Pattern.quote(asString(sep)));
    return "[" + String.join(", ", parts) + "]";
  }

  // ------------- built-ins: math -------------
  public Number max(Object a, Object b){
    String sa = String.valueOf(a); String sb = String.valueOf(b);
    if(a instanceof Double || b instanceof Double || hasDot(sa) || hasDot(sb)){
      return Math.max(asDouble(a), asDouble(b));
    }else{
      return Math.max(asInt(a), asInt(b));
    }
  }
  public Number min(Object a, Object b){
    String sa = String.valueOf(a); String sb = String.valueOf(b);
    if(a instanceof Double || b instanceof Double || hasDot(sa) || hasDot(sb)){
      return Math.min(asDouble(a), asDouble(b));
    }else{
      return Math.min(asInt(a), asInt(b));
    }
  }
  public Number abs(Object x){
    if(x instanceof Double) return Math.abs((Double)x);
    if(x instanceof Number) return Math.abs(((Number)x).intValue());
    return Math.abs(asDouble(x));
  }
  public Number round(Object x){ return Math.round(asDouble(x)); }
  public Double random(){ return Math.random(); }

  // ------------- built-ins: files -------------
  public String file_read(Object path){
    try{
      byte[] bytes = Files.readAllBytes(Paths.get(asString(path)));
      return new String(bytes, StandardCharsets.UTF_8);
    }catch(IOException e){ throw new RuntimeException(e); }
  }
  public Boolean file_write(Object path, Object content){
    try{
      Path p = Paths.get(asString(path));
      Path parent = p.getParent();
      if(parent != null) Files.createDirectories(parent);
      Files.write(p, asString(content).getBytes(StandardCharsets.UTF_8));
      return true;
    }catch(IOException e){ throw new RuntimeException(e); }
  }
  public Boolean file_exists(Object path){ return Files.exists(Paths.get(asString(path))); }
}
