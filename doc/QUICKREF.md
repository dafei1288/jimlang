
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

## 甯哥敤鍑芥暟
  - contains(s, sub) -> boolean
  - replace(s, target, repl) -> string
  - startsWith(s, prefix) / endsWith(s, suffix) -> boolean
  - repeat(s, times:int) -> string
  - padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
  - pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
  - randomRange(min, max) ->  [min, max)
  - file_append(path, content) -> boolean,UTF-8

## 鏁扮粍锛堟柊澧烇級

- push(arr, value) -> number锛堣繑鍥炴柊闀垮害锛?- pop(arr) -> any | null锛堢Щ闄ゅ苟杩斿洖鏈€鍚庝竴涓厓绱狅級
- shift(arr) -> any | null锛堢Щ闄ゅ苟杩斿洖绗竴涓厓绱狅級
- unshift(arr, value) -> number锛堣繑鍥炴柊闀垮害锛?- split(s, sep) -> array锛堣繑鍥炲瓧绗︿覆鏁扮粍锛?
## 类型与集合（新增）
- typeof(x) -> "number"|"string"|"boolean"|"array"|"object"|"null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- join(arr, sep) -> string
- parseInt(s) / parseFloat(s) -> number