# JimLang 栈机制和栈帧结构详解

## 当前实现概述

### ⚠️ 重要发现：栈机制"有设计但未充分使用"

当前 JimLang 的实现中：
- ✅ **有完整的栈帧类定义** (`StackFrane`)
- ✅ **有作用域类定义** (`Scope`, `RootScope`)
- ❌ **但实际运行时使用的是全局符号表** (`Hashtable<String, Symbol>`)
- ⚠️ **栈帧只在函数调用时部分使用**

---

## 1. 栈帧结构 (StackFrane)

### 类定义

**文件**: `src/main/java/com/dafei1288/jimlang/metadata/StackFrane.java`

```java
public class StackFrane extends Stack<Symbol> {
    private String currentName;          // 当前函数名
    private Symbol currentSymbol;        // 当前符号（函数对象）
    private Object returnValue;          // 返回值
    private List<SymbolVar> symbolVars;  // 符号变量列表
    private StackFrane parent;           // 父栈帧（控制链接）
    private SymbolFunction next;         // 下一个函数
    private List<SymbolVar> localVars;   // 局部变量
    private Object register;             // 寄存器信息
}
```

### 栈帧字段详解

根据代码中的注释，栈帧设计参考了标准的调用栈结构：

| 字段 | 作用 | 标准栈帧对应 |
|------|------|------------|
| `returnValue` | 存储函数返回值 | **返回值区** - 存放函数执行结果 |
| `symbolVars` / `localVars` | 存储局部变量 | **本地变量区** - 函数的局部变量空间 |
| `parent` | 指向上一级栈帧 | **控制链接** - 用于访问外层作用域变量 |
| `currentSymbol` | 当前函数信息 | **函数元信息** |
| `register` | 保存寄存器状态 | **寄存器保存区** - 保护现场 |
| *(缺失)* | 返回地址 | **返回地址** - 函数返回后的执行位置 |

**继承关系**: `StackFrane extends Stack<Symbol>`
- 继承了 Java 的 `Stack<Symbol>` 类
- 可以使用 `push()`, `pop()`, `peek()` 等栈操作

---

## 2. 作用域结构 (Scope)

### 类定义

**文件**: `src/main/java/com/dafei1288/jimlang/metadata/Scope.java`

```java
public class Scope {
    private String name;                      // 作用域名称
    private StatementBlockType statementBlockType;  // 块类型
    private Scope subScope;                   // 子作用域
    private Scope parentScope;                // 父作用域

    // 内部类：根作用域
    public static class RootScope extends Scope {
        public RootScope() {
            this.setName("ROOT");
            this.setStatementBlockType(StatementBlockType.ROOT_BLOCK);
        }
    }
}
```

### 作用域设计

```
┌─────────────────────────────┐
│      RootScope (全局)        │  <- currentScope (JimLangVistor.java:33)
│  name: "ROOT"               │
│  type: ROOT_BLOCK           │
└──────────┬──────────────────┘
           │ subScope
           ▼
    ┌──────────────┐
    │ Function     │
    │ Scope        │
    └──────────────┘
```

---

## 3. 当前实际实现（符号表方式）

### 核心数据结构

**文件**: `src/main/java/com/dafei1288/jimlang/JimLangVistor.java`

```java
public class JimLangVistor extends JimLangBaseVisitor {
    // 全局符号表 - 实际使用的变量存储
    Hashtable<String, Symbol> _sympoltable = new Hashtable<>();

    // 当前作用域 - 定义了但很少使用
    Scope currentScope;

    @Override
    public Object visitProg(ProgContext ctx) {
        currentScope = new RootScope();  // 初始化根作用域
        return super.visitProg(ctx);
    }
}
```

### 实际运行机制

#### 变量存储：全局 Hashtable

所有变量都存储在 `_sympoltable` 这个全局哈希表中：

```java
// 变量声明
public Object visitVariableDecl(VariableDeclContext ctx) {
    String varName = ctx.identifier().getText();
    SymbolVar symbol = (SymbolVar) _sympoltable.get(varName);

    if(symbol == null){
        symbol = new SymbolVar();
        _sympoltable.put(varName, symbol);  // 直接放入全局表
    }
    // ...
}

// 变量查找
public Object visitPrimary(PrimaryContext ctx) {
    if(ctx.identifier() != null){
        String varName = ctx.identifier().getText();
        Symbol currentSymbol = _sympoltable.get(varName);  // 从全局表查找
        if(currentSymbol != null){
            return currentSymbol.getValue();
        }
    }
    // ...
}
```

**特点**:
- ✅ **简单直接** - O(1) 查找效率
- ❌ **没有真正的作用域隔离** - 所有变量在同一个命名空间
- ❌ **变量污染风险** - 函数内变量可能覆盖全局变量

---

## 4. 函数调用中的"伪栈帧"实现

### 函数调用处理

**文件**: `JimLangVistor.java:452-515`

```java
@Override
public Object visitFunctionCall(FunctionCallContext ctx) {
    SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);

    if(currentSymbol != null){
        // ⚠️ 创建栈帧对象，但只用于记录，不真正管理作用域
        StackFrane stackFrane = new StackFrane(currentSymbol, functionName);

        // 1. 获取实参值列表
        List<Object> actualParams = /* ... */;

        // 2. 获取形参名称列表
        List<String> formalParams = currentSymbol.getParameterList();

        // 3. ⭐ 关键：保存当前符号表（快照）
        Hashtable<String, Symbol> savedSymbolTable = new Hashtable<>(_sympoltable);

        // 4. 将实参绑定到形参（直接修改全局符号表）
        for(int i = 0; i < formalParams.size(); i++){
            SymbolVar paramVar = new SymbolVar();
            paramVar.setName(formalParams.get(i));
            paramVar.setValue(actualParams.get(i));
            _sympoltable.put(formalParams.get(i), paramVar);  // 污染全局表
        }

        // 5. 执行函数体
        Object result = /* 执行函数体 */;

        // 6. ⭐ 关键：恢复原符号表
        _sympoltable = savedSymbolTable;  // 整个替换回去

        return result;
    }
}
```

### 工作原理图解

```
调用前状态：
┌────────────────────────────┐
│  _sympoltable (全局)        │
│  { x: 10, y: 20, ...}      │
└────────────────────────────┘

调用 add(5, 3) 时：

Step 1: 保存快照
savedSymbolTable = new Hashtable<>(_sympoltable)
┌────────────────────────────┐
│  savedSymbolTable          │  <- 副本
│  { x: 10, y: 20, ...}      │
└────────────────────────────┘

Step 2: 添加参数到全局表
┌────────────────────────────┐
│  _sympoltable (被污染)      │
│  { x: 10, y: 20,           │
│    a: 5, b: 3, ... }       │  <- 参数 a, b 覆盖或添加
└────────────────────────────┘

Step 3: 执行函数体
function add(a, b) {
    return a + b  // 从全局表读取 a=5, b=3
}

Step 4: 恢复快照
_sympoltable = savedSymbolTable
┌────────────────────────────┐
│  _sympoltable (恢复)        │
│  { x: 10, y: 20, ...}      │  <- a, b 消失了
└────────────────────────────┘
```

---

## 5. 优缺点分析

### 当前实现的优点 ✅

1. **实现简单**
   - 单一全局符号表，逻辑清晰
   - 不需要复杂的作用域链查找

2. **性能高效**
   - Hashtable O(1) 查找
   - 没有作用域链遍历开销

3. **内存效率高**
   - 不需要为每个作用域维护独立的符号表
   - 快照复制只在函数调用时发生

### 当前实现的缺点 ❌

1. **没有真正的块级作用域**
   ```jim
   var x = 10
   if (true) {
       var x = 20  // 实际上覆盖了外层的 x
   }
   println(x)  // 输出 20，而不是 10
   ```

2. **变量生命周期不正确**
   ```jim
   var x = 1
   {
       var y = 2
   }
   println(y)  // 应该报错，但实际可以访问
   ```

3. **栈帧设计未被充分利用**
   - `StackFrane` 类有完整的设计，但只在函数调用时创建一下就丢弃了
   - `parent` 链接、`localVars` 等字段都没有实际使用

4. **符号表快照开销**
   - 每次函数调用都要复制整个 Hashtable
   - 递归调用时开销很大：`O(n * depth)` 其中 n 是符号表大小

5. **嵌套函数和闭包无法实现**
   - 无法访问外层函数的变量
   - 无法实现 JavaScript 风格的闭包

---

## 6. 与标准栈帧的对比

### 标准栈式虚拟机（如 JVM）

```
┌──────────────────────────┐  <- Stack Pointer (SP)
│  Local Variables (局部)   │
├──────────────────────────┤
│  Operand Stack (操作数栈) │
├──────────────────────────┤
│  Frame Data (帧数据)      │
│  - Return Address        │
│  - Dynamic Link          │
│  - Return Value          │
└──────────────────────────┘
         │
         ▼
┌──────────────────────────┐
│  Caller's Frame          │
└──────────────────────────┘
```

**标准实现特点**:
- 每个函数调用创建一个新栈帧
- 栈帧包含局部变量表、操作数栈
- 通过栈指针管理当前帧
- 返回时弹出栈帧，自动清理

### JimLang 当前实现

```
┌──────────────────────────┐
│  Global Symbol Table     │  <- 全局唯一
│  Hashtable<String,Symbol>│
│  { x: 10, y: 20, ...}    │
└──────────────────────────┘
         │
         │ 函数调用时
         ▼
┌──────────────────────────┐
│  Snapshot (快照)         │
│  savedSymbolTable        │
└──────────────────────────┘
         │
         │ 函数返回时
         ▼
┌──────────────────────────┐
│  Restore (恢复)          │
│  _sympoltable = saved    │
└──────────────────────────┘
```

**JimLang 特点**:
- 单一全局符号表 + 快照/恢复机制
- 没有真正的栈结构
- `StackFrane` 对象创建但未使用

---

## 7. 改进建议

### 方案 1: 真正的栈式作用域（推荐）

```java
public class JimLangVistor {
    // 当前栈帧
    private StackFrane currentFrame;

    // 栈帧栈
    private Stack<StackFrane> frameStack = new Stack<>();

    public Object visitFunctionCall(FunctionCallContext ctx) {
        // 1. 创建新栈帧
        StackFrane newFrame = new StackFrane();
        newFrame.setParent(currentFrame);  // 链接父帧

        // 2. 绑定参数到新栈帧
        for(int i = 0; i < params.size(); i++){
            newFrame.put(paramName, paramValue);  // 使用继承的 Stack 方法
        }

        // 3. 压栈
        frameStack.push(currentFrame);
        currentFrame = newFrame;

        // 4. 执行函数体
        Object result = executeBody();

        // 5. 弹栈
        currentFrame = frameStack.pop();

        return result;
    }

    // 变量查找：沿作用域链向上查找
    private Symbol lookupVariable(String name) {
        StackFrane frame = currentFrame;
        while(frame != null){
            if(frame.containsKey(name)){
                return frame.get(name);
            }
            frame = frame.getParent();  // 向上查找
        }
        return null;  // 未找到
    }
}
```

### 方案 2: 作用域链（类似 JavaScript）

```java
public class JimLangVistor {
    // 作用域栈
    private Stack<Scope> scopeStack = new Stack<>();

    // 当前作用域
    private Scope currentScope;

    public Object visitBlock(BlockContext ctx) {
        // 进入新作用域
        Scope newScope = new Scope();
        newScope.setParent(currentScope);
        scopeStack.push(currentScope);
        currentScope = newScope;

        // 执行块内语句
        Object result = visit(ctx.statementList());

        // 退出作用域
        currentScope = scopeStack.pop();

        return result;
    }
}
```

---

## 8. 总结

### 当前状态 📊

| 组件 | 设计状态 | 实现状态 | 使用状态 |
|------|---------|---------|---------|
| StackFrane 类 | ✅ 完整 | ✅ 完整 | ❌ 未充分使用 |
| Scope 类 | ✅ 完整 | ✅ 完整 | ⚠️ 部分使用 |
| 全局符号表 | - | ✅ 完整 | ✅ 主要机制 |
| 快照/恢复 | - | ✅ 实现 | ✅ 函数调用 |

### 核心机制 🔑

**JimLang 使用的是"全局符号表 + 快照恢复"模式**:

1. **平时**: 所有变量在全局 `Hashtable<String, Symbol>` 中
2. **函数调用时**:
   - 保存快照 → 添加参数 → 执行 → 恢复快照
3. **栈帧对象**: 创建了但基本上只是一个标记

### 这种设计的合理性 🤔

**优点**:
- 对于**简单的脚本语言**，这种实现足够用
- 性能好，实现简单
- 适合原型开发和快速迭代

**局限**:
- 无法支持真正的词法作用域
- 无法实现闭包
- 递归深度受符号表大小影响

### 未来演进路径 🚀

1. **短期**: 保持当前设计（够用）
2. **中期**: 实现真正的栈帧机制
3. **长期**: 添加闭包、词法作用域支持

---

**文档时间**: 2025-11-27
**JimLang 版本**: Phase 2 Complete
