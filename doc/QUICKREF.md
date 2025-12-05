# JimLang Quick Reference

## 鎺у埗娴侊紙break/continue锛?```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

## 鏁扮粍涓庡璞?```jim
var arr = [1, 2, 3]
println(arr[0])     // 1
arr[1] = 10
println(arr.length) // 3

var person = { name: "Jim", age: 25 }
println(person.name)
person.age = 26
println(person.age)

var nested = { a: { b: 2 } }
println(nested.a.b)  // 2
```

## 瀛楃涓插嚱鏁?- contains(s, sub) -> boolean
- replace(s, target, repl) -> string
- startsWith(s, prefix) / endsWith(s, suffix) -> boolean
- repeat(s, times:int) -> string
- padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
- split(s, sep) -> array
- substring(s, start[, end]) -> string
- indexOf(s, sub) -> number
- toUpperCase/upper, toLowerCase/lower, trim, len

## 鏁扮粍鍑芥暟
- push(arr, value) -> number
- pop(arr) -> any | null
- shift(arr) -> any | null
- unshift(arr, value) -> number
- join(arr, sep) -> string

## 鏁板鍑芥暟
- pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
- randomRange(min, max) -> number
- max(a,b)/min(a,b)/abs(x)/round(x)/random()

## 鏂囦欢 I/O
- file_read(path) -> string | null
- file_write(path, content) -> boolean  // UTF-8
- file_exists(path) -> boolean
- file_append(path, content) -> boolean  // UTF-8

## 绫诲瀷涓庨泦鍚堬紙鎵╁睍锛?- typeof(x) -> "number" | "string" | "boolean" | "array" | "object" | "null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- parseInt(s) / parseFloat(s) -> number

## JSON / YAML
```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
println( contains(j, "a") )
var x = json_decode(j)
println( x.a )         // 1

println( json_pretty(o, 2) )

var y = yml_encode(o, 2)  // 闇€瑕?SnakeYAML 鎵嶈兘瀹屾暣鏀寔
# var m = yml_decode(y)

// 鏂囦欢璇诲啓鍔╂墜
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")
yml_dump(o, "tmp.yml", 2)
var oy = yml_load("tmp.yml")
```

## CLI 蹇€熺敤娉?- 鍚姩 REPL: `jimlang --cli` 鎴?`jimlang -i`
- 鎵ц涓€琛? `jimlang --eval "println(1+2)"` 鎴?`jimlang -e "..."`
- 浠?STDIN 璇? `echo println(42) | jimlang -`
- 鍚敤璺熻釜: `jimlang --trace examples/fibonacci.jim`锛堟垨璁剧疆鐜鍙橀噺 `JIM_TRACE=1`锛?
## 浣滅敤鍩燂紙鍑芥暟涓庝唬鐮佸潡锛?鍑芥暟鍐呴伄钄?
```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
// 2
// 1
```

鐙珛鍧楅伄钄?
```jim
var x = 1
if (true) {
  var x = 2
  println("in:" + x)
}
println("out:" + x)
// in:2
// out:1
```

浠庡嚱鏁板唴閮ㄤ慨鏀瑰閮ㄥ彉閲?
```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
// 3
```

## Delegates & apply
```jim
function add(a,b){ return a + b }
var d = delegate("add")
println( apply(d, 2, 3) )        // 5

var inc = partial(d, 1)
println( apply(inc, 41) )        // 42
```

璇﹁: `examples/lib_functional.jim`
## 绗竴绫诲嚱鏁帮紙First-class functions锛?```jim
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )        // 5

var inc = partial(add, 1)
println( inc(41) )        // 42


> 注：系统函数同样是一等值，可赋给变量后调用：
```jim
var p = println
p("ok")
```
var ops = [ add ]
println( ops[0](10, 5) )  // 15
```