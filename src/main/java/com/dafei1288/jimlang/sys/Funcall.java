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
    // array
    SYS_FUNCTION_NAMES.add("PUSH");
    SYS_FUNCTION_NAMES.add("POP");
    SYS_FUNCTION_NAMES.add("SHIFT");
    SYS_FUNCTION_NAMES.add("UNSHIFT");
    // extra string
    SYS_FUNCTION_NAMES.add("CONTAINS");
    SYS_FUNCTION_NAMES.add("REPLACE");
    SYS_FUNCTION_NAMES.add("STARTSWITH");
    SYS_FUNCTION_NAMES.add("ENDSWITH");
    SYS_FUNCTION_NAMES.add("REPEAT");
    SYS_FUNCTION_NAMES.add("PADLEFT");
    SYS_FUNCTION_NAMES.add("PADRIGHT");
    // math
    SYS_FUNCTION_NAMES.add("MAX");
    SYS_FUNCTION_NAMES.add("MIN");
    SYS_FUNCTION_NAMES.add("ABS");
    SYS_FUNCTION_NAMES.add("ROUND");
        SYS_FUNCTION_NAMES.add("RANDOM");
    SYS_FUNCTION_NAMES.add("POW");
    SYS_FUNCTION_NAMES.add("SQRT");
    SYS_FUNCTION_NAMES.add("FLOOR");
    SYS_FUNCTION_NAMES.add("CEIL");
    SYS_FUNCTION_NAMES.add("RANDOMRANGE");
    // file
    SYS_FUNCTION_NAMES.add("FILE_READ");
    SYS_FUNCTION_NAMES.add("FILE_WRITE");
        SYS_FUNCTION_NAMES.add("FILE_EXISTS");
    SYS_FUNCTION_NAMES.add("FILE_APPEND");
  }

  public static boolean isSysFunction(String functionName){
    if(functionName == null) return false;
    return SYS_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT)) || hasMethod(functionName);
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
    public java.util.List<String> split(Object s, Object sep){
    String[] parts = asString(s).split(java.util.regex.Pattern.quote(asString(sep)));
    return java.util.Arrays.asList(parts);
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
  // ------------- built-ins: string (extended) -------------
  public Boolean contains(Object s, Object sub){ return asString(s).contains(asString(sub)); }
  public String replace(Object s, Object target, Object repl){
    return asString(s).replace(asString(target), asString(repl));
  }
  public Boolean startsWith(Object s, Object prefix){ return asString(s).startsWith(asString(prefix)); }
  public Boolean endsWith(Object s, Object suffix){ return asString(s).endsWith(asString(suffix)); }
  public String repeat(Object s, Object times){
    int n = asInt(times);
    if (n <= 0) return "";
    String str = asString(s);
    StringBuilder sb = new StringBuilder(str.length() * n);
    for (int i = 0; i < n; i++) sb.append(str);
    return sb.toString();
  }
  private static char padCharOf(Object pad){
    String ps = asString(pad);
    return ps.isEmpty() ? ' ' : ps.charAt(0);
  }
  public String padLeft(Object s, Object width){ return padLeft(s, width, " "); }
  public String padLeft(Object s, Object width, Object pad){
    String str = asString(s);
    int w = asInt(width);
    if (w <= str.length()) return str;
    int count = w - str.length();
    char ch = padCharOf(pad);
    StringBuilder sb = new StringBuilder(w);
    for (int i = 0; i < count; i++) sb.append(ch);
    sb.append(str);
    return sb.toString();
  }
  public String padRight(Object s, Object width){ return padRight(s, width, " "); }
  public String padRight(Object s, Object width, Object pad){
    String str = asString(s);
    int w = asInt(width);
    if (w <= str.length()) return str;
    int count = w - str.length();
    char ch = padCharOf(pad);
    StringBuilder sb = new StringBuilder(w);
    sb.append(str);
    for (int i = 0; i < count; i++) sb.append(ch);
    return sb.toString();
  }

  // ------------- built-ins: math (extended) -------------
  public Number pow(Object a, Object b){ return Math.pow(asDouble(a), asDouble(b)); }
  public Number sqrt(Object x){ return Math.sqrt(asDouble(x)); }
  public Number floor(Object x){ return Math.floor(asDouble(x)); }
  public Number ceil(Object x){ return Math.ceil(asDouble(x)); }
  public Number randomRange(Object min, Object max){
  double a = asDouble(min), b = asDouble(max);
  double lo = Math.min(a,b), hi = Math.max(a,b);
  // Only use integer range if both inputs are integral types (not Double/Float)
  boolean minIntType = (min instanceof Integer) || (min instanceof Long) || (min instanceof Short) || (min instanceof Byte);
  boolean maxIntType = (max instanceof Integer) || (max instanceof Long) || (max instanceof Short) || (max instanceof Byte);
  boolean intLike = minIntType && maxIntType;
  if (intLike) {
    int ilo = (int)lo; int ihi = (int)hi;
    if (ihi < ilo) { int t=ilo; ilo=ihi; ihi=t; }
    int span = (ihi - ilo + 1);
    if (span <= 0) return ilo;
    int r = ilo + (int)Math.floor(Math.random() * span);
    return r;
  } else {
    return lo + Math.random() * (hi - lo);
  }
}public Boolean file_append(Object path, Object content){
    try{
      Path p = Paths.get(asString(path));
      Path parent = p.getParent();
      if(parent != null) Files.createDirectories(parent);
      Files.write(p, asString(content).getBytes(StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
      return true;
    }catch(IOException e){ throw new RuntimeException(e); }
  }

  // ------------- built-ins: array -------------
  @SuppressWarnings({"rawtypes","unchecked"})
  private static java.util.List asListOrThrow(Object a){
    if (a instanceof java.util.List) return (java.util.List)a;
    throw new RuntimeException("Expected array (list) but got: " + (a==null?"null":a.getClass().getSimpleName()));
  }
  @SuppressWarnings({"rawtypes","unchecked"})
  public Number push(Object arr, Object value){
    java.util.List list = asListOrThrow(arr);
    list.add(value);
    return list.size();
  }
  @SuppressWarnings({"rawtypes","unchecked"})
  public Object pop(Object arr){
    java.util.List list = asListOrThrow(arr);
    if (list.isEmpty()) return null;
    return list.remove(list.size()-1);
  }
  @SuppressWarnings({"rawtypes","unchecked"})
  public Object shift(Object arr){
    java.util.List list = asListOrThrow(arr);
    if (list.isEmpty()) return null;
    return list.remove(0);
  }
  @SuppressWarnings({"rawtypes","unchecked"})
  public Number unshift(Object arr, Object value){
    java.util.List list = asListOrThrow(arr);
    list.add(0, value);
    return list.size();
  }
  
  // ------------- built-ins: type/introspection + collections -------------
  @SuppressWarnings({"rawtypes"})
  public String join(Object arr, Object sep){
    java.util.List list = asListOrThrow(arr);
    String s = asString(sep);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++){
      if (i > 0) sb.append(s);
      Object v = list.get(i);
      sb.append(v == null ? "null" : String.valueOf(v));
    }
    return sb.toString();
  }
  
  @SuppressWarnings({"rawtypes","unchecked"})
  public java.util.List keys(Object obj){
    if (obj instanceof java.util.Map){
      return new java.util.ArrayList(((java.util.Map)obj).keySet());
    }
    throw new RuntimeException("Expected object (map) but got: " + (obj==null?"null":obj.getClass().getSimpleName()));
  }
  
  @SuppressWarnings({"rawtypes","unchecked"})
  public java.util.List values(Object obj){
    if (obj instanceof java.util.Map){
      return new java.util.ArrayList(((java.util.Map)obj).values());
    }
    throw new RuntimeException("Expected object (map) but got: " + (obj==null?"null":obj.getClass().getSimpleName()));
  }
  
  public String typeof(Object x){
    if (x == null) return "null";
    if (x instanceof java.util.List) return "array";
    if (x instanceof java.util.Map) return "object";
    if (x instanceof String) return "string";
    if (x instanceof Number) return "number";
    if (x instanceof Boolean) return "boolean";
    return x.getClass().getSimpleName();
  }
  
  public Boolean isArray(Object x){ return (x instanceof java.util.List); }
  public Boolean isObject(Object x){ return (x instanceof java.util.Map); }
  
  public Number parseInt(Object s){
    String str = asString(s).trim();
    if (str.isEmpty()) return 0;
    return Integer.parseInt(str);
  }
  public Number parseFloat(Object s){
    String str = asString(s).trim();
    if (str.isEmpty()) return 0.0d;
    return Double.parseDouble(str);
  }


  private static boolean hasMethod(String name){
    if (name == null) return false;
    for (Method m : new Funcall().getClass().getMethods()){
      if (m.getName().equalsIgnoreCase(name)) return true;
    }
    return false;
  }}
