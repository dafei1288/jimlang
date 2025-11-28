# Phase 2 实施计划：控制流

## 📊 当前状态

✅ **Phase 1 已完成**：
- 函数参数传递 ✅
- 完整算术运算 ✅
- 分号可选 ✅
- REPL 改进 ✅
- 浮点数支持 ✅

---

## 🎯 Phase 2 目标

实现程序的基本控制流和数据结构，让 JimLang 成为一个图灵完备的编程语言。

---

## 📋 任务列表（推荐顺序）

### 任务 1: if/else 条件语句 🔴 高优先级
**工作量**: 2-3 小时
**难度**: ⭐⭐

**语法**:
```jim
if (condition) {
    // true branch
} else {
    // false branch
}
```

**实现要点**:
1. 添加 ANTLR 语法规则
2. 实现条件表达式求值
3. 根据条件选择分支执行
4. 支持嵌套 if

**测试用例**:
```jim
var x = 10
if (x > 5) {
    println("x is greater than 5")
} else {
    println("x is less than or equal to 5")
}

// 嵌套 if
if (x > 0) {
    if (x > 10) {
        println("x > 10")
    } else {
        println("0 < x <= 10")
    }
}
```

---

### 任务 2: while 循环 🔴 高优先级
**工作量**: 2-3 小时
**难度**: ⭐⭐⭐

**语法**:
```jim
while (condition) {
    // loop body
}
```

**实现要点**:
1. 添加 while 语法规则
2. 循环条件求值
3. 循环体重复执行
4. 避免无限循环（可选：添加最大迭代次数限制）

**测试用例**:
```jim
var i = 0
while (i < 5) {
    println(i)
    i = i + 1
}

// 嵌套循环
var x = 0
while (x < 3) {
    var y = 0
    while (y < 2) {
        println("x=" + x + ", y=" + y)
        y = y + 1
    }
    x = x + 1
}
```

---

### 任务 3: for 循环 🟡 中优先级
**工作量**: 2-3 小时
**难度**: ⭐⭐⭐

**语法**:
```jim
for (init; condition; increment) {
    // loop body
}
```

**实现要点**:
1. 添加 for 语法规则
2. 初始化表达式
3. 条件判断
4. 增量表达式
5. 作用域管理（循环变量）

**测试用例**:
```jim
for (var i = 0; i < 5; i = i + 1) {
    println(i)
}

// 嵌套 for
for (var x = 0; x < 3; x = x + 1) {
    for (var y = 0; y < 2; y = y + 1) {
        println("x=" + x + ", y=" + y)
    }
}
```

---

### 任务 4: 数组支持 🟡 中优先级
**工作量**: 4-5 小时
**难度**: ⭐⭐⭐⭐

**语法**:
```jim
var arr = [1, 2, 3]
var first = arr[0]
arr[1] = 10
```

**实现要点**:
1. 数组字面量语法 `[...]`
2. 索引访问 `arr[index]`
3. 索引赋值 `arr[index] = value`
4. 内部表示（List 或 ArrayList）
5. 基本方法：`length`, `push`, `pop`

**测试用例**:
```jim
// 数组创建和访问
var arr = [1, 2, 3, 4, 5]
println(arr[0])  // 1
println(arr[4])  // 5

// 数组修改
arr[2] = 100
println(arr[2])  // 100

// 数组长度
println(arr.length)  // 5

// 数组操作
arr.push(6)
println(arr.length)  // 6

var last = arr.pop()
println(last)  // 6
```

---

### 任务 5: 对象支持 🟢 低优先级
**工作量**: 5-6 小时
**难度**: ⭐⭐⭐⭐⭐

**语法**:
```jim
var obj = { name: "Jim", age: 25 }
var n = obj.name
obj.age = 26
```

**实现要点**:
1. 对象字面量语法 `{key: value}`
2. 属性访问 `obj.property`
3. 属性赋值 `obj.property = value`
4. 内部表示（HashMap）
5. 支持嵌套对象

**测试用例**:
```jim
// 对象创建和访问
var person = { name: "Jim", age: 25 }
println(person.name)  // Jim
println(person.age)   // 25

// 对象修改
person.age = 26
println(person.age)   // 26

// 嵌套对象
var user = {
    name: "Alice",
    address: {
        city: "Beijing",
        zip: "100000"
    }
}
println(user.address.city)  // Beijing
```

---

## 🔧 技术实施顺序

### 第一步：if/else + while (第1天)
- 这两个是最基础的控制流
- 实现后可以写出完整的算法
- 测试：写一个冒泡排序或斐波那契数列

### 第二步：for 循环 (第2天)
- 基于 while 的经验
- 主要是语法糖
- 测试：用 for 重写之前的例子

### 第三步：数组 (第3-4天)
- 需要修改类型系统
- 涉及索引访问的语法
- 测试：实现数组排序、搜索

### 第四步：对象 (第5-6天，可选)
- 最复杂的特性
- 可以留到后面
- 测试：实现简单的数据结构

---

## 📝 语法设计草案

### JimLang.g4 新增规则

```antlr
// 在 statement 中添加
statement : variableDecl
          | functionDecl
          | functionCallStmt
          | expressionStatement
          | ifStatement          // 新增
          | whileStatement       // 新增
          | forStatement         // 新增
          ;

// if/else 语句
ifStatement: IF '(' expression ')' block (ELSE block)? ;

// while 循环
whileStatement: WHILE '(' expression ')' block ;

// for 循环
forStatement: FOR '(' forInit ';' expression ';' forUpdate ')' block ;
forInit: variableDecl | expressionStatement | ;
forUpdate: expression | ;

// 代码块
block: '{' statementList? '}' ;

// 数组字面量
arrayLiteral: '[' (expression (',' expression)*)? ']' ;

// 数组访问
arrayAccess: identifier '[' expression ']' ;

// 对象字面量
objectLiteral: '{' (objectProperty (',' objectProperty)*)? '}' ;
objectProperty: identifier ':' expression ;

// 属性访问
propertyAccess: identifier '.' identifier ;

// 关键字
IF: 'if' ;
ELSE: 'else' ;
WHILE: 'while' ;
FOR: 'for' ;
```

---

## 🧪 完整测试脚本

创建 `phase2_test.jim`:

```jim
// ====== Phase 2 综合测试 ======

// 1. if/else 测试
println("=== Test 1: if/else ===")
var score = 85
if (score >= 90) {
    println("Grade: A")
} else {
    if (score >= 80) {
        println("Grade: B")
    } else {
        println("Grade: C")
    }
}

// 2. while 循环测试
println("\n=== Test 2: while loop ===")
var i = 1
var sum = 0
while (i <= 5) {
    sum = sum + i
    i = i + 1
}
println("Sum of 1 to 5: " + sum)

// 3. for 循环测试
println("\n=== Test 3: for loop ===")
for (var j = 0; j < 5; j = j + 1) {
    println("j = " + j)
}

// 4. 数组测试
println("\n=== Test 4: arrays ===")
var numbers = [10, 20, 30, 40, 50]
println("First: " + numbers[0])
println("Last: " + numbers[4])
numbers[2] = 100
println("Modified: " + numbers[2])

// 5. 对象测试
println("\n=== Test 5: objects ===")
var person = { name: "Jim", age: 25 }
println("Name: " + person.name)
println("Age: " + person.age)
person.age = 26
println("New age: " + person.age)

// 6. 综合：冒泡排序
println("\n=== Test 6: Bubble Sort ===")
var arr = [5, 2, 8, 1, 9]
var n = arr.length
for (var i = 0; i < n; i = i + 1) {
    for (var j = 0; j < n - i - 1; j = j + 1) {
        if (arr[j] > arr[j + 1]) {
            var temp = arr[j]
            arr[j] = arr[j + 1]
            arr[j + 1] = temp
        }
    }
}
println("Sorted: " + arr)

println("\n=== All Phase 2 tests completed! ===")
```

---

## 📊 进度追踪

| 任务 | 优先级 | 工作量 | 状态 |
|------|--------|--------|------|
| if/else | 🔴 高 | 2-3h | ⬜ 待开始 |
| while | 🔴 高 | 2-3h | ⬜ 待开始 |
| for | 🟡 中 | 2-3h | ⬜ 待开始 |
| 数组 | 🟡 中 | 4-5h | ⬜ 待开始 |
| 对象 | 🟢 低 | 5-6h | ⬜ 待开始 |

**总工作量**: 15-22 小时
**预计完成**: 3-4 个工作日

---

## 🚀 开始建议

我建议从 **if/else** 开始，因为：

1. ✅ 最简单，容易上手
2. ✅ 立即可用，能写出有用的程序
3. ✅ 为 while/for 打好基础
4. ✅ 2-3 小时就能完成

你想从哪个功能开始？我可以立即开始实现！

---

**创建时间**: 2025-11-27
**Phase**: 2
**下一阶段**: Phase 3 (标准库)
