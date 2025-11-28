
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
