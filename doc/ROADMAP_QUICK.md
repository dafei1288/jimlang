# JimLang 开发计划 - 快速版

## 项目状态
- 已实现：基础 REPL、变量/函数、表达式求值、print/println、JSR‑223
- 控制流：if/else、while、for、break、continue（完成）
- 数据结构：数组/对象 字面量与索引/属性（完成）；数组方法待办
- 标准库：字符串/数学/文件 基础函数（进行中）

## 开发阶段

### Phase 1：核心修复（进行中）
- 函数参数绑定、作用域、REPL 体验

### Phase 2：控制流与数据结构（已达成）
- if/else、while、for、break/continue、链式运算、数组/对象基础

### Phase 3：标准库（进行中）
- 字符串方法、数组方法、Math 扩展、文件 I/O 扩展

### Phase 4：高级特性（规划）
- 闭包/Lambda、try/catch、模块系统

## 示例

### 控制流（break/continue）
```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

### 数组与对象
```jim
var arr = [1, 2, 3]
println(arr[0])
arr[1] = 10
println(arr.length)

var person = { name: "Jim", age: 25 }
println(person.name)
person.age = 26
println(person.age)
```