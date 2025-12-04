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

## 瀛楃涓插嚱鏁?- contains(s, sub) -> boolean
- replace(s, target, repl) -> string
- startsWith(s, prefix) / endsWith(s, suffix) -> boolean
- repeat(s, times:int) -> string
- padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
- split(s, sep) -> array

## 鏁扮粍鍑芥暟
- push(arr, value) -> number
- pop(arr) -> any | null
- shift(arr) -> any | null
- unshift(arr, value) -> number
- join(arr, sep) -> string

## 鏁板鍑芥暟
- pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
- randomRange(min, max) -> number  // 鍖洪棿 [min, max)

## 鏂囦欢 I/O
- file_read(path) -> string | null
- file_write(path, content) -> boolean  // UTF-8
- file_exists(path) -> boolean
- file_append(path, content) -> boolean  // UTF-8

## 绫诲瀷涓庨泦鍚堬紙鏂板锛?- typeof(x) -> "number" | "string" | "boolean" | "array" | "object" | "null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- parseInt(s) / parseFloat(s) -> number

## CLI Quick Usage
- Start REPL: `jimlang --cli` or `jimlang -i`
- Eval one-liner: `jimlang --eval "println(1+2)"` or `jimlang -e "..."`
- Read from STDIN: `echo println(42) | jimlang -`
- Enable trace: `jimlang --trace examples/fibonacci.jim`锛堟垨璁剧疆鐜鍙橀噺 `JIM_TRACE=1`锛?
## 浣滅敤鍩燂紙鍑芥暟涓庝唬鐮佸潡锛?鍑芥暟鍐呴伄钄斤細
```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
# 2
# 1
```

鐙珛鍧楅伄钄斤細
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

浠庡嚱鏁板唴閮ㄤ慨鏀瑰閮ㄥ彉閲忥細
```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
# 3
```
## JSON / YAML

```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
var x = json_decode(j)
println(x.a)         # 1

var y = yml_encode(o)  # requires SnakeYAML for full features
# var m = yml_decode(y)
```

## Delegates & apply

```jim
function add(a,b){ return a + b }
var d = delegate("add")
println( apply(d, 2, 3) )        # 5

var inc = partial(d, 1)
println( apply(inc, 41) )        # 42
```

See more: `examples/lib_functional.jim` (map/reduce/filter).