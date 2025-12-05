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
    // web
    SYS_FUNCTION_NAMES.add("START_WEBSERVER");
    SYS_FUNCTION_NAMES.add("ROUTE");
    SYS_FUNCTION_NAMES.add("RESPONSE");
    SYS_FUNCTION_NAMES.add("REQ_JSON");
  }

  public static boolean isSysFunction(String functionName){
    if(functionName == null) return false;
    return SYS_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT)) || hasMethod(functionName);
  }

  public static Object exec(String functionName, List<Object> params){
    Funcall f = new Funcall();
    int argc = (params == null) ? 0 : params.size();

    // 1) prefer exact match by name (case sensitive) and parameter count
    Method method = Arrays.stream(f.getClass().getMethods())
        .filter(m -> m.getName().equals(functionName) && m.getParameterCount() == argc)
        .findFirst().orElse(null);

    // 2) fallback: case-insensitive exact parameter count
    if(method == null){
      method = Arrays.stream(f.getClass().getMethods())
          .filter(m -> m.getName().equalsIgnoreCase(functionName) && m.getParameterCount() == argc)
          .findFirst().orElse(null);
    }

    // 3) varargs support: find varargs method and adapt arguments
    if (method == null) {
      Method varMethod = Arrays.stream(f.getClass().getMethods())
          .filter(m -> m.getName().equalsIgnoreCase(functionName) && m.isVarArgs())
          .findFirst().orElse(null);
      if (varMethod != null) {
        try {
          int pcount = varMethod.getParameterCount();
          // varargs requires argc >= pcount - 1 (fixed params)
          int fixed = Math.max(0, pcount - 1);
          if (argc < fixed) {
            throw new RuntimeException("Unknown built-in function: " + functionName + " with " + argc + " args");
          }
          Class<?>[] pt = varMethod.getParameterTypes();
          Class<?> varComp = pt[pcount - 1].getComponentType();
          int varLen = argc - fixed;
          Object varArray = java.lang.reflect.Array.newInstance(varComp, varLen);
          for (int i=0;i<varLen;i++) {
            Object v = params.get(fixed + i);
            java.lang.reflect.Array.set(varArray, i, v);
          }
          Object[] invokeArgs = new Object[pcount];
          for (int i=0;i<fixed;i++) invokeArgs[i] = params.get(i);
          invokeArgs[pcount - 1] = varArray;
          return varMethod.invoke(f, invokeArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
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


    // ------------- built-ins: JSON/YAML and delegates -------------
  private static String jsonEscape(String s){
    StringBuilder sb = new StringBuilder();
    for (int i=0;i<s.length();i++){
      char c = s.charAt(i);
      switch(c){
        case '"': sb.append("\\\""); break;
        case '\\': sb.append("\\\\"); break;
        case '\n': sb.append("\\n"); break;
        case '\r': sb.append("\\r"); break;
        case '\t': sb.append("\\t"); break;
        default:
          if (c < 0x20) { sb.append(String.format("\\u%04x", (int)c)); }
          else sb.append(c);
      }
    }
    return sb.toString();
  }

  // ---------------- web server (simple) ----------------
  private static class Resp {
    final int status;
    final java.util.Map<String,String> headers;
    final byte[] body;
    Resp(int s, java.util.Map<String,String> h, byte[] b){ this.status=s; this.headers=h; this.body=b; }
  }
  // path pattern compiler: supports literals, :param, and trailing * wildcard (prefix)
  private static class PathPattern {
    final String raw;
    final String[] tokens; // without leading empty segment
    PathPattern(String raw){
      if (raw == null || raw.isEmpty()) raw = "/";
      this.raw = raw;
      String norm = raw.startsWith("/") ? raw.substring(1) : raw;
      if (norm.isEmpty()) { this.tokens = new String[0]; }
      else this.tokens = norm.split("/");
    }
    // Returns null if not match; otherwise returns params map. Supports trailing '*' -> captures remainder under key "splat".
    java.util.Map<String,String> match(String path){
      if (path == null) path = "/";
      String norm = path.startsWith("/") ? path.substring(1) : path;
      String[] parts = norm.isEmpty()? new String[0] : norm.split("/");
      java.util.LinkedHashMap<String,String> params = new java.util.LinkedHashMap<>();
      int i=0, j=0;
      while(i < tokens.length && j < parts.length){
        String t = tokens[i];
        if ("*".equals(t)){
          // wildcard consumes remainder
          StringBuilder sb = new StringBuilder();
          for (int k=j;k<parts.length;k++){ if (k>j) sb.append('/'); sb.append(parts[k]); }
          params.put("splat", sb.toString());
          return params;
        }
        if (t.startsWith(":")){
          String name = t.substring(1);
          params.put(name, parts[j]);
        } else {
          if (!t.equals(parts[j])) return null;
        }
        i++; j++;
      }
      // if pattern has remaining tokens
      if (i < tokens.length){
        if (i == tokens.length-1 && "*".equals(tokens[i])){ params.put("splat", ""); return params; }
        return null;
      }
      // pattern consumed; path must also be fully consumed
      if (j < parts.length) return null;
      return params;
    }
  }
  private static class Route {
    final String method; // upper-case, or "ANY"/"*"
    final String path;
    final PathPattern pattern;
    final Object handler; // Delegate or function name
    final com.dafei1288.jimlang.JimLangVistor visitor;
    Route(String m, String p, Object h, com.dafei1288.jimlang.JimLangVistor v){ this.method=m; this.path=p; this.pattern=new PathPattern(p); this.handler=h; this.visitor=v; }
  }
  private static final java.util.Map<Integer, com.sun.net.httpserver.HttpServer> SERVERS = new java.util.concurrent.ConcurrentHashMap<>();
  @SuppressWarnings({"rawtypes","unchecked"})
  public Object route(Object path, Object method, Object handler){
    java.util.LinkedHashMap m = new java.util.LinkedHashMap();
    m.put("path", asString(path));
    m.put("method", asString(method));
    m.put("handler", handler);
    return m;
  }
  @SuppressWarnings({"rawtypes","unchecked"})
  public Object response(Object status, Object headers, Object body){
    int st = asInt(status);
    java.util.Map<String,String> hdr = new java.util.LinkedHashMap<>();
    if (headers instanceof java.util.Map){
      java.util.Map hm = (java.util.Map)headers;
      for (Object k : hm.keySet()){
        hdr.put(String.valueOf(k), (hm.get(k)==null?null:String.valueOf(hm.get(k))));
      }
    }
    String contentType = hdr.getOrDefault("Content-Type", hdr.getOrDefault("content-type", null));
    byte[] bytes;
    if (body == null) {
      bytes = new byte[0];
    } else if (body instanceof String) {
      bytes = ((String)body).getBytes(StandardCharsets.UTF_8);
      if (contentType == null) contentType = "text/plain; charset=utf-8";
    } else if (body instanceof java.util.Map || body instanceof java.util.List) {
      bytes = jsonStringify(body).getBytes(StandardCharsets.UTF_8);
      if (contentType == null) contentType = "application/json; charset=utf-8";
    } else {
      bytes = String.valueOf(body).getBytes(StandardCharsets.UTF_8);
      if (contentType == null) contentType = "text/plain; charset=utf-8";
    }
    if (contentType != null) hdr.put("Content-Type", contentType);
    return new Resp(st, hdr, bytes);
  }
  public Object response(Object status, Object body){
    return response(status, null, body);
  }
  public Object req_json(Object req){
    if (!(req instanceof java.util.Map)) return null;
    Object body = ((java.util.Map)req).get("body");
    if (body == null) return null;
    return json_decode(String.valueOf(body));
  }
  @SuppressWarnings({"rawtypes"})
  public Boolean start_webserver(Object port, Object routes){
    int p = asInt(port);
    if (SERVERS.containsKey(p)) return true;
    com.dafei1288.jimlang.JimLangVistor v = com.dafei1288.jimlang.Host.current();
    if (v == null) throw new RuntimeException("start_webserver must be called from script context");
    try{
      java.net.InetSocketAddress addr = new java.net.InetSocketAddress(p);
      com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(addr, 0);
      java.util.ArrayList<Route> routeList = new java.util.ArrayList<>();
      java.util.List list;
      if (routes instanceof java.util.List){ list = (java.util.List)routes; }
      else if (routes instanceof java.util.Map){ list = java.util.Arrays.asList(routes); }
      else { throw new RuntimeException("routes must be array of route(...) or object"); }
      for (Object it : list){
        if (!(it instanceof java.util.Map)) throw new RuntimeException("route entry must be object from route(path,method,handler)");
        java.util.Map r = (java.util.Map) it;
        String pathStr = asString(r.get("path"));
        String m = asString(r.get("method")); if (m==null || m.isEmpty()) m="GET"; m = m.toUpperCase(java.util.Locale.ROOT);
        Object h = r.get("handler");
        routeList.add(new Route(m, pathStr, h, v));
      }
      // single dispatch context for all paths; match in order
      server.createContext("/", exchange -> {
          try {
            String method = exchange.getRequestMethod();
            if (method == null) method = "GET"; method = method.toUpperCase(java.util.Locale.ROOT);
            String reqPath = exchange.getRequestURI().getPath();
            Route hit = null; java.util.Map<String,String> pathParams = null; boolean pathOnlyMatched = false;
            for (Route r : routeList) {
              java.util.Map<String,String> m = r.pattern.match(reqPath);
              if (m != null) {
                if ("*".equals(r.method) || "ANY".equals(r.method) || r.method.equals(method)) { hit = r; pathParams = m; break; }
                pathOnlyMatched = true; // path ok but method mismatch
              }
            }
            if (hit == null){
              int code = pathOnlyMatched ? 405 : 404;
              String msg = pathOnlyMatched ? "Method Not Allowed" : "Not Found";
              byte[] b = (msg).getBytes(java.nio.charset.StandardCharsets.UTF_8);
              exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
              exchange.sendResponseHeaders(code, b.length);
              try(java.io.OutputStream os = exchange.getResponseBody()){ os.write(b); }
              return;
            }
            // Build request map
            java.util.LinkedHashMap<String,Object> req = new java.util.LinkedHashMap<>();
            java.net.URI uri = exchange.getRequestURI();
            req.put("method", method);
            req.put("path", uri.getPath());
            if (pathParams != null) req.put("params", pathParams);
            // query params
            java.util.LinkedHashMap<String,Object> q = new java.util.LinkedHashMap<>();
            String qq = uri.getRawQuery();
            if (qq != null && !qq.isEmpty()){
              for (String pair : qq.split("&")){
                int i = pair.indexOf('=');
                if (i>=0) q.put(java.net.URLDecoder.decode(pair.substring(0,i), "UTF-8"), java.net.URLDecoder.decode(pair.substring(i+1), "UTF-8"));
                else q.put(java.net.URLDecoder.decode(pair, "UTF-8"), "");
              }
            }
            req.put("query", q);
            // headers
            java.util.LinkedHashMap<String,Object> hs = new java.util.LinkedHashMap<>();
            for (java.util.Map.Entry<String, java.util.List<String>> he : exchange.getRequestHeaders().entrySet()){
              hs.put(he.getKey(), he.getValue()==null?null:String.join(",", he.getValue()));
            }
            req.put("headers", hs);
            // body
            byte[] bodyBytes = new byte[0];
            try(java.io.InputStream is = exchange.getRequestBody()){
              bodyBytes = is.readAllBytes();
            }
            String body = new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8);
            req.put("body", body);
            // parse json body if content-type indicates json
            String ct = null;
            try { ct = exchange.getRequestHeaders().getFirst("Content-Type"); } catch(Exception ignore) {}
            if (ct != null && ct.toLowerCase(java.util.Locale.ROOT).contains("json")){
              try { req.put("json", json_decode(body)); } catch(Exception ignore) {}
            }

            Object result;
            synchronized (hit.visitor){
              java.util.ArrayList<Object> args = new java.util.ArrayList<>();
              args.add(req);
              result = hit.visitor.callFromHost(hit.handler, args);
            }
            int status = 200;
            byte[] out;
            java.util.Map<String,String> outHeaders = new java.util.LinkedHashMap<>();
            outHeaders.put("Content-Type", "text/plain; charset=utf-8");
            if (result instanceof Resp){
              Resp r = (Resp) result;
              status = r.status;
              out = (r.body == null) ? new byte[0] : r.body;
              if (r.headers != null) outHeaders.putAll(r.headers);
            } else if (result == null){
              out = new byte[0];
            } else if (result instanceof String){
              out = ((String)result).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            } else if (result instanceof java.util.Map || result instanceof java.util.List){
              out = jsonStringify(result).getBytes(java.nio.charset.StandardCharsets.UTF_8);
              outHeaders.put("Content-Type", "application/json; charset=utf-8");
            } else {
              out = String.valueOf(result).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }
            for (java.util.Map.Entry<String,String> he : outHeaders.entrySet()){
              if (he.getKey() != null && he.getValue() != null) exchange.getResponseHeaders().set(he.getKey(), he.getValue());
            }
            exchange.sendResponseHeaders(status, out.length);
            try(java.io.OutputStream os = exchange.getResponseBody()){ os.write(out); }
          } catch (Exception ex){
            byte[] b = ("Internal Server Error: " + ex.getMessage()).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(500, b.length);
            try(java.io.OutputStream os = exchange.getResponseBody()){ os.write(b); }
          } finally {
            exchange.close();
          }
        });
      
      server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
      server.start();
      SERVERS.put(p, server);
      return true;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  public Boolean start_webserver(Object port, Object path1, Object method1, Object handler1){
    java.util.ArrayList<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
    list.add((java.util.Map<String,Object>)route(path1, method1, handler1));
    return start_webserver(port, list);
  }
  public Boolean start_webserver(Object port, Object path1, Object method1, Object handler1,
                                 Object path2, Object method2, Object handler2){
    java.util.ArrayList<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
    list.add((java.util.Map<String,Object>)route(path1, method1, handler1));
    list.add((java.util.Map<String,Object>)route(path2, method2, handler2));
    return start_webserver(port, list);
  }
  public Boolean start_webserver(Object port, Object... triples){
    java.util.ArrayList<java.util.Map<String,Object>> list = new java.util.ArrayList<>();
    if (triples != null && triples.length > 0){
      if (triples.length % 3 != 0) throw new RuntimeException("start_webserver expects triples of (path, method, handler)");
      for (int i=0;i<triples.length;i+=3){
        list.add((java.util.Map<String,Object>)route(triples[i], triples[i+1], triples[i+2]));
      }
    }
    return start_webserver(port, list);
  }
  @SuppressWarnings({"rawtypes"})
  private static String jsonStringify(Object v){
    if (v == null) return "null";
    if (v instanceof String) return '"' + jsonEscape((String)v) + '"';
    if (v instanceof Number || v instanceof Boolean) return String.valueOf(v);
    if (v instanceof java.util.Map){
      StringBuilder sb = new StringBuilder(); sb.append('{'); boolean first=true;
      for (Object eObj : ((java.util.Map)v).entrySet()){
        java.util.Map.Entry e = (java.util.Map.Entry)eObj;
        if (!first) sb.append(','); first=false;
        sb.append('"').append(jsonEscape(String.valueOf(e.getKey()))).append('"').append(':');
        sb.append(jsonStringify(e.getValue()));
      }
      sb.append('}'); return sb.toString();
    }
    if (v instanceof java.util.List){
      StringBuilder sb = new StringBuilder(); sb.append('['); boolean first=true;
      java.util.List list = (java.util.List)v;
      for (Object it : list){ if (!first) sb.append(','); first=false; sb.append(jsonStringify(it)); }
      sb.append(']'); return sb.toString();
    }
    // fallback
    return '"' + jsonEscape(String.valueOf(v)) + '"';
  }
  public String json_encode(Object v){ return jsonStringify(v); }

  // JSON pretty printing
  public String json_pretty(Object v){ return json_pretty(v, 2); }
  public String json_pretty(Object v, Object indent){
    int step = 2; try { step = asInt(indent); } catch(Exception ignored) {}
    StringBuilder sb = new StringBuilder();
    prettyJson(v, sb, 0, Math.max(0, step));
    return sb.toString();
  }
  @SuppressWarnings({"rawtypes"})
  private static void prettyJson(Object v, StringBuilder sb, int level, int step){
    String ind = (step<=0? "" : " ".repeat(level*step));
    String ind2 = (step<=0? "" : " ".repeat((level+1)*step));
    if (v == null) { sb.append("null"); return; }
    if (v instanceof String) { sb.append('"').append(jsonEscape((String)v)).append('"'); return; }
    if (v instanceof Number || v instanceof Boolean) { sb.append(String.valueOf(v)); return; }
    if (v instanceof java.util.Map){
      java.util.Map m = (java.util.Map)v;
      sb.append('{'); if (!m.isEmpty()) sb.append('\n');
      boolean first=true;
      for (Object eObj : m.entrySet()){
        java.util.Map.Entry e = (java.util.Map.Entry)eObj;
        if (!first) sb.append(',').append('\n'); first=false;
        sb.append(ind2).append('"').append(jsonEscape(String.valueOf(e.getKey()))).append('"').append(':');
        if (step>0) sb.append(' ');
        prettyJson(e.getValue(), sb, level+1, step);
      }
      if (!m.isEmpty()) { sb.append('\n').append(ind); }
      sb.append('}');
      return;
    }
    if (v instanceof java.util.List){
      java.util.List a = (java.util.List)v;
      sb.append('['); if (!a.isEmpty()) sb.append('\n');
      for (int i=0;i<a.size();i++){
        if (i>0) sb.append(',').append('\n');
        sb.append(ind2);
        prettyJson(a.get(i), sb, level+1, step);
      }
      if (!a.isEmpty()) { sb.append('\n').append(ind); }
      sb.append(']');
      return;
    }
    sb.append('"').append(jsonEscape(String.valueOf(v))).append('"');
  }

  // YAML encode with indentation option (if SnakeYAML present)
  public String yml_encode(Object v, Object indent){
    try{
      Class<?> optClz = Class.forName("org.yaml.snakeyaml.DumperOptions");
      Object opt = optClz.getConstructor().newInstance();
      try { optClz.getMethod("setIndent", int.class).invoke(opt, asInt(indent)); } catch(Throwable ignore) {}
      try { optClz.getMethod("setPrettyFlow", boolean.class).invoke(opt, true); } catch(Throwable ignore) {}
      try {
        Class<?> flowClz = Class.forName("org.yaml.snakeyaml.DumperOptions$FlowStyle");
        Object BLOCK = flowClz.getField("BLOCK").get(null);
        optClz.getMethod("setDefaultFlowStyle", flowClz).invoke(opt, BLOCK);
      } catch(Throwable ignore) {}
      Class<?> yamlClz = Class.forName("org.yaml.snakeyaml.Yaml");
      Object y = yamlClz.getConstructor(optClz).newInstance(opt);
      java.lang.reflect.Method dump = yamlClz.getMethod("dump", Object.class);
      return (String) dump.invoke(y, v);
    }catch(Throwable ignore){
      // fallback simple formatting
      return simpleYaml(v, 0);
    }
  }

  // minimal JSON parser
  private static class J {
    private final String s; private int i=0;
    J(String s){ this.s = s==null?"":s.trim(); }
    private void ws(){ while(i<s.length() && Character.isWhitespace(s.charAt(i))) i++; }
    Object parse(){ ws(); Object v = val(); ws(); return v; }
    private Object val(){ ws(); if (i>=s.length()) return null; char c=s.charAt(i);
      if (c=='"') return str();
      if (c=='{') return obj();
      if (c=='[') return arr();
      if (s.startsWith("true", i)){ i+=4; return Boolean.TRUE; }
      if (s.startsWith("false", i)){ i+=5; return Boolean.FALSE; }
      if (s.startsWith("null", i)){ i+=4; return null; }
      return num(); }
    private String str(){ StringBuilder sb=new StringBuilder(); i++; while(i<s.length()){ char c=s.charAt(i++); if(c=='"') break; if(c=='\\' && i<s.length()){ char e=s.charAt(i++); switch(e){ case '"': sb.append('"'); break; case '\\': sb.append('\\'); break; case 'n': sb.append('\n'); break; case 'r': sb.append('\r'); break; case 't': sb.append('\t'); break; case 'u': String hex=s.substring(i,i+4); sb.append((char)Integer.parseInt(hex,16)); i+=4; break; default: sb.append(e);} } else { sb.append(c);} } return sb.toString(); }
    private Number num(){ int st=i; boolean dot=false; while(i<s.length()){ char c=s.charAt(i); if((c>='0'&&c<='9')||c=='-'||c=='+'||c=='e'||c=='E'){ if(c=='.') dot=true; i++; } else break; } String n=s.substring(st,i); try{ if(dot) return Double.valueOf(n); else return Integer.valueOf(n); }catch(Exception ex){ return Double.valueOf(n); } }
    private java.util.Map obj(){ java.util.LinkedHashMap m = new java.util.LinkedHashMap(); i++; ws(); if (s.charAt(i)=='}'){ i++; return m; } while(true){ ws(); String k=(String)str(); ws(); if(s.charAt(i++)!=':') throw new RuntimeException("Invalid JSON object"); Object v=val(); m.put(k,v); ws(); char c=s.charAt(i++); if(c=='}') break; if(c!=',') throw new RuntimeException("Invalid JSON object"); }
      return m; }
    private java.util.List arr(){ java.util.ArrayList a=new java.util.ArrayList(); i++; ws(); if (s.charAt(i)==']'){ i++; return a; } while(true){ Object v=val(); a.add(v); ws(); char c=s.charAt(i++); if(c==']') break; if(c!=',') throw new RuntimeException("Invalid JSON array"); }
      return a; }
  }
  public Object json_decode(Object s){ return new J(asString(s)).parse(); }

  // YAML (encode always available; decode via SnakeYAML if present)
  public String yml_encode(Object v){
    // try SnakeYAML
    try{
      Class<?> yamlClz = Class.forName("org.yaml.snakeyaml.Yaml");
      Object y = yamlClz.getConstructor().newInstance();
      Method dump = yamlClz.getMethod("dump", Object.class);
      return (String) dump.invoke(y, v);
    }catch(Throwable ignore){
      return simpleYaml(v, 0);
    }
  }

  @SuppressWarnings({"rawtypes"})
  private static String simpleYaml(Object v, int indent){
    String pad = " ".repeat(indent);
    if (v == null) return "null\n";
    if (v instanceof Boolean || v instanceof Number) return pad + String.valueOf(v) + "\n";
    if (v instanceof String) return pad + '"' + ((String)v).replace("\"","\\\"") + '"' + "\n";
    if (v instanceof java.util.Map){ StringBuilder sb=new StringBuilder(); java.util.Map m=(java.util.Map)v; for(Object k : m.keySet()){ sb.append(pad).append(String.valueOf(k)).append(": "); Object val=m.get(k); if (val instanceof java.util.Map || val instanceof java.util.List){ sb.append("\n").append(simpleYaml(val, indent+2)); } else { sb.append(simpleYaml(val, 0)); } } return sb.toString(); }
    if (v instanceof java.util.List){ StringBuilder sb=new StringBuilder(); java.util.List a=(java.util.List)v; for(Object it : a){ sb.append(pad).append("- "); if (it instanceof java.util.Map || it instanceof java.util.List){ sb.append("\n").append(simpleYaml(it, indent+2)); } else { sb.append(simpleYaml(it,0)); } } return sb.toString(); }
    return pad + '"' + String.valueOf(v).replace("\"","\\\"") + '"' + "\n";
  }

  public Object yml_decode(Object s){
    try{
      Class<?> yamlClz = Class.forName("org.yaml.snakeyaml.Yaml");
      Object y = yamlClz.getConstructor().newInstance();
      Method load = yamlClz.getMethod("load", String.class);
      return load.invoke(y, asString(s));
    }catch(Throwable ex){
      throw new RuntimeException("YAML decode requires SnakeYAML on classpath");
    }
  }

  // delegates
  public Object delegate(Object name){ if (name instanceof com.dafei1288.jimlang.Delegate) return name; return new com.dafei1288.jimlang.Delegate(asString(name)); }
  public Object partial(Object del, Object arg){
    if (del instanceof com.dafei1288.jimlang.Delegate){
      return ((com.dafei1288.jimlang.Delegate)del).withBound(arg);
    }
    // treat name as function name
    return new com.dafei1288.jimlang.Delegate(asString(del)).withBound(arg);
  }  
  // ------------- JSON/YAML file helpers -------------
  public Object json_load(Object path){
    String s = file_read(path);
    return json_decode(s);
  }
  public Boolean json_dump(Object v, Object path){
    return file_write(path, json_encode(v));
  }
  public Boolean json_dump(Object v, Object path, Object indent){
    int step = asInt(indent);
    String s = (step > 0) ? json_pretty(v, step) : json_encode(v);
    return file_write(path, s);
  }
  public Object yml_load(Object path){
    String s = file_read(path);
    return yml_decode(s);
  }
  public Boolean yml_dump(Object v, Object path){
    return file_write(path, yml_encode(v));
  }
  public Boolean yml_dump(Object v, Object path, Object indent){
    return file_write(path, yml_encode(v, indent));
  }private static boolean hasMethod(String name){
    if (name == null) return false;
    for (Method m : new Funcall().getClass().getMethods()){
      if (m.getName().equalsIgnoreCase(name)) return true;
    }
    return false;
  }}
