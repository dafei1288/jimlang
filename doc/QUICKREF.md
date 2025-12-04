
### Control Flow (break/continue)
```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

### Arrays & Objects
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

## 鐢摜鏁ら崙鑺ユ殶
  - contains(s, sub) -> boolean
  - replace(s, target, repl) -> string
  - startsWith(s, prefix) / endsWith(s, suffix) -> boolean
  - repeat(s, times:int) -> string
  - padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
  - pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
  - randomRange(min, max) ->  [min, max)
  - file_append(path, content) -> boolean,UTF-8

## 閺佹壆绮嶉敍鍫熸煀婢х儑绱?
- push(arr, value) -> number閿涘牐绻戦崶鐐存煀闂€鍨閿?- pop(arr) -> any | null閿涘牏些闂勩倕鑻熸潻鏂挎礀閺堚偓閸氬簼绔存稉顏勫帗缁辩媴绱?- shift(arr) -> any | null閿涘牏些闂勩倕鑻熸潻鏂挎礀缁楊兛绔存稉顏勫帗缁辩媴绱?- unshift(arr, value) -> number閿涘牐绻戦崶鐐存煀闂€鍨閿?- split(s, sep) -> array閿涘牐绻戦崶鐐茬摟缁楋缚瑕嗛弫鎵矋閿?
## 绫诲瀷涓庨泦鍚堬紙鏂板锛?- typeof(x) -> "number"|"string"|"boolean"|"array"|"object"|"null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- join(arr, sep) -> string
- parseInt(s) / parseFloat(s) -> number
## CLI Quick Usage

- Start REPL: `jimlang --cli` or `jimlang -i`
- Eval one-liner: `jimlang --eval "println(1+2)"` or `jimlang -e "..."`
- Read from STDIN: `echo println(42) | jimlang -`
- Enable trace: `jimlang --trace examples/fibonacci.jim` (or `set JIM_TRACE=1`)

## Scoping (Function & Block)

Function-local shadowing:

```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
# Output:
# 2
# 1
```

Independent block scope (shadowing inside block):

```jim
var x = 1
if (true) {
  var x = 2
  println("in:" + x)
}
println("out:" + x)
# Output:
# in:2
# out:1
```

Assign to outer variable from function:

```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
# Output:
# 3
```