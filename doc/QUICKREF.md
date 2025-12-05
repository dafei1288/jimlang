# JimLang Quick Reference

## 闁硅矇鍐ㄧ厬婵炵繝绶ょ槐妾卹eak/continue闁?```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

## 闁轰焦澹嗙划宥嗙▔鎼粹槅鍤犻悹?```jim
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

## 閻庢稒顨堥浣圭▔閹绘帒姣愰柡?- contains(s, sub) -> boolean
- replace(s, target, repl) -> string
- startsWith(s, prefix) / endsWith(s, suffix) -> boolean
- repeat(s, times:int) -> string
- padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
- split(s, sep) -> array
- substring(s, start[, end]) -> string
- indexOf(s, sub) -> number
- toUpperCase/upper, toLowerCase/lower, trim, len

## 闁轰焦澹嗙划宥夊礄閼恒儲娈?
- push(arr, value) -> number
- pop(arr) -> any | null
- shift(arr) -> any | null
- unshift(arr, value) -> number
- join(arr, sep) -> string

## 闁轰焦婢橀鐔煎礄閼恒儲娈?
- pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
- randomRange(min, max) -> number
- max(a,b)/min(a,b)/abs(x)/round(x)/random()

## 闁哄倸娲ｅ▎?I/O
- file_read(path) -> string | null
- file_write(path, content) -> boolean  // UTF-8
- file_exists(path) -> boolean
- file_append(path, content) -> boolean  // UTF-8

## 缂侇偉顕ч悗閿嬬▔鎼淬劍鑲犻柛姘墳缁辨瑩骞嶉埡浣烘綌闁?- typeof(x) -> "number" | "string" | "boolean" | "array" | "object" | "null"
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

var y = yml_encode(o, 2)  // 闂傚洠鍋撻悷?SnakeYAML 闁归潧绉烽崗妯尖偓鐟版湰閺嗭綁寮ㄩ娑樼槷
# var m = yml_decode(y)

// 闁哄倸娲ｅ▎銏㈡嫚鐠囨彃鏅搁柛鏂烘櫆婢?
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")
yml_dump(o, "tmp.yml", 2)
var oy = yml_load("tmp.yml")
```

## CLI 闊浂鍋婇埀顒傚枔閺併倕鈻?- 闁告凹鍨版慨?REPL: `jimlang --cli` 闁?`jimlang -i`
- 闁圭瑳鍡╂斀濞戞挴鍋撻悶? `jimlang --eval "println(1+2)"` 闁?`jimlang -e "..."`
- 濞?STDIN 閻? `echo println(42) | jimlang -`
- 闁告凹鍨抽弫銈囨崉閻斿鍤? `jimlang --trace examples/fibonacci.jim`闁挎稑鐗婇崹銊ф媼閸撗呮瀭闁绘粠鍨伴。銊╁矗濮椻偓閸?`JIM_TRACE=1`闁?
## 濞达絾绮庨弫銈夊春閻曞倻绀勯柛鎴ｅГ閺嗙喐绋夋惔婵嗘暕闁活喕绀佸锟犳晬?闁告垼濮ら弳鐔煎礃閸涚繝绱曢柦?
```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
// 2
// 1
```

闁绘瑯鍓涢悵娑㈠锤濡ゅ懍绱曢柦?
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

濞寸姴楠搁崵閬嶅极閺夊灝鏁堕梺顔哄妺閹便劑寮ㄩ悷閭︽▎闂侇喓鍔岃ぐ澶愭煂?
```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
// 3
```

## Delegates & apply
> 涓嶆帹鑽愶細浠呬繚鐣欏吋瀹圭敤閫斻€備紭鍏堜娇鐢ㄢ€滅涓€绫诲嚱鏁扳€濆啓娉曪紙瑙佷笅锛夈€?```jim
function add(a,b){ return a + b }
var d = delegate("add")
println( apply(d, 2, 3) )        // 5

var inc = partial(d, 1)
println( apply(inc, 41) )        // 42
```

閻犲浄绠掗～? `examples/lib_functional.jim`
## 缂佹鍏涚粩瀵哥尵鐠囨彃姣愰柡浣稿簻缁辨irst-class functions闁?```jim
> 推荐：优先使用“第一类函数”（函数值 + callSuffix 调用），链式调用与存储更直观。
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )        // 5

var inc = partial(add, 1)
println( inc(41) )        // 42


> 濞夘煉绱扮化鑽ょ埠閸戣姤鏆熼崥灞剧壉閺勵垯绔寸粵澶娾偓纭风礉閸欘垵绁寸紒娆忓綁闁插繐鎮楃拫鍐暏閿?
```jim
var p = println
p("ok")
```
var ops = [ add ]
println( ops[0](10, 5) )  // 15
```