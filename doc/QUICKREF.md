# JimLang Quick Reference

## 控制流（break/continue）
```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

## 数组与对象
```jim
var arr = [1, 2, 3]
println(arr[0])     # 1
arr[1] = 10
println(arr.length) # 3

var person = { name: "Jim", age: 25 }
println(person.name)
person.age = 26
println(person.age)

var nested = { a: { b: 2 } }
println(nested.a.b)  # 2
```

## 字符串函数
- contains(s, sub) -> boolean
- replace(s, target, repl) -> string
- startsWith(s, prefix) / endsWith(s, suffix) -> boolean
- repeat(s, times:int) -> string
- padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
- split(s, sep) -> array

## 数组函数
- push(arr, value) -> number
- pop(arr) -> any | null
- shift(arr) -> any | null
- unshift(arr, value) -> number
- join(arr, sep) -> string

## 数学函数
- pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
- randomRange(min, max) -> number  // 区间 [min, max)

## 文件 I/O
- file_read(path) -> string | null
- file_write(path, content) -> boolean  // UTF-8
- file_exists(path) -> boolean
- file_append(path, content) -> boolean  // UTF-8

## 类型与集合（新增）
- typeof(x) -> "number" | "string" | "boolean" | "array" | "object" | "null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- parseInt(s) / parseFloat(s) -> number

## CLI Quick Usage
- Start REPL: `jimlang --cli` or `jimlang -i`
- Eval one-liner: `jimlang --eval "println(1+2)"` or `jimlang -e "..."`
- Read from STDIN: `echo println(42) | jimlang -`
- Enable trace: `jimlang --trace examples/fibonacci.jim`（或设置环境变量 `JIM_TRACE=1`）

## 作用域（函数与代码块）
函数内遮蔽：
```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
# 2
# 1
```

独立块遮蔽：
```jim
var x = 1
if (true) {
  var x = 2
  println("in:" + x)
}
println("out:" + x)
# in:2
# out:1
```

从函数内部修改外部变量：
```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
# 3
```