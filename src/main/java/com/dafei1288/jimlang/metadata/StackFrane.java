package com.dafei1288.jimlang.metadata;

import java.util.List;
import java.util.Stack;

/**
 * 返回值：一般放在最顶上，这样它的地址是固定的。foo 函数返回以后，它的调用者可以到这里来取到返回值。在实际情况中，ABI 会规定优先通过寄存器来传递返回值，比通过内存传递性能更高。
 *
 * 参数：在调用 foo 函数时，我们把它所需要一个整型参数写到栈帧的这个位置。同样，我们也可以通过寄存器来传递参数，而不是通过内存。
 *
 * 控制链接：就是上一级栈帧（也就是 main 函数的栈帧）的地址。如果该函数用到了上一级作用域中的变量，那么就可以顺着这个链接找到上一级作用域的栈帧，并找到变量的值。
 *
 * 返回地址： foo 函数执行完毕以后，继续执行哪条指令。同样，我们可以用寄存器来保存这个信息。
 *
 * 本地变量： foo 函数的本地变量 b 的存储空间。
 *
 * 寄存器信息：我们还经常在栈帧里保存寄存器的数据。如果在 foo 函数里要使用某个寄存器，可能需要先把它的值保存下来，防止破坏了别的代码保存在这里的数据。这种约定叫做被调用者责任，也就是使用寄存器的函数要保护好寄存器里原有的信息。某个函数如果使用了某个寄存器，但它又要调用别的函数，为了防止别的函数把自己放在寄存器中的数据覆盖掉，这个函数就要自己把寄存器信息保存在栈帧中。这种约定叫做调用者责任。
 * */

public class StackFrane extends Stack<Symbol> {
    private String currentName;
    private Symbol currentSymbol;

    private Object returnValue;

    private List<SymbolVar> symbolVars;

    private StackFrane parent;

    private SymbolFunction next;

    private List<SymbolVar> localVars;

    private Object register;

    public StackFrane(){

    }

  public StackFrane(Symbol currentSymbol, String currentName) {
    this.currentName = currentName;
    this.currentSymbol = currentSymbol;
  }

  public String getCurrentName() {
    return currentName;
  }

  public void setCurrentName(String currentName) {
    this.currentName = currentName;
  }

  public Symbol getCurrentSymbol() {
    return currentSymbol;
  }

  public void setCurrentSymbol(Symbol currentSymbol) {
    this.currentSymbol = currentSymbol;
  }

  public Object getReturnValue() {
    return returnValue;
  }

  public void setReturnValue(Object returnValue) {
    this.returnValue = returnValue;
  }

  public List<SymbolVar> getSymbolVars() {
    return symbolVars;
  }

  public void setSymbolVars(List<SymbolVar> symbolVars) {
    this.symbolVars = symbolVars;
  }

  public StackFrane getParent() {
    return parent;
  }

  public void setParent(StackFrane parent) {
    this.parent = parent;
  }

  public SymbolFunction getNext() {
    return next;
  }

  public void setNext(SymbolFunction next) {
    this.next = next;
  }

  public List<SymbolVar> getLocalVars() {
    return localVars;
  }

  public void setLocalVars(List<SymbolVar> localVars) {
    this.localVars = localVars;
  }

  public Object getRegister() {
    return register;
  }

  public void setRegister(Object register) {
    this.register = register;
  }

  @Override
  public String toString() {
    return "StackFrane{" +
        "currentName='" + currentName + '\'' +
        ", currentSymbol=" + currentSymbol +
        ", returnValue=" + returnValue +
        ", symbolVars=" + symbolVars +
        ", parent=" + parent +
        ", next=" + next +
        ", localVars=" + localVars +
        ", register=" + register +
        '}';
  }
}
