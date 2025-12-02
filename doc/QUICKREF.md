
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

## 常用函数
  - contains(s, sub) -> boolean
  - replace(s, target, repl) -> string
  - startsWith(s, prefix) / endsWith(s, suffix) -> boolean
  - repeat(s, times:int) -> string
  - padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
  - pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
  - randomRange(min, max) ->  [min, max)
  - file_append(path, content) -> boolean,UTF-8

## 数组（新增）

- push(arr, value) -> number（返回新长度）
- pop(arr) -> any | null（移除并返回最后一个元素）
- shift(arr) -> any | null（移除并返回第一个元素）
- unshift(arr, value) -> number（返回新长度）
- split(s, sep) -> array（返回字符串数组）
