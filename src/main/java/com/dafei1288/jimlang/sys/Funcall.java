package com.dafei1288.jimlang.sys;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Funcall {

  public static Set<String> SYS_FUNCTION_NAMES = new HashSet<>();

  static{
    SYS_FUNCTION_NAMES.add("PRINTLN");
    SYS_FUNCTION_NAMES.add("PRINT");
  }



  public static boolean isSysFunction(String functionName){
    boolean tag = false;
    if(SYS_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT))){
      tag = true;
    }
    return tag;
  }

  public static Object exec(String functionName, List<Object> params){
    Funcall f = new Funcall();
    Method method = Arrays.stream(f.getClass().getMethods()).filter(it->it.getName().equals(functionName)).findFirst().get();
    try {
      return  method.invoke(f,params.toArray());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }


  public void println(Object obj){
    System.out.println(obj);
  }
  public void print(Object obj){
    System.out.print(obj);
  }
}
