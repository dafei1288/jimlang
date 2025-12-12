# JimLang 快速参考

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

## 字符串字面量
- 双引号字符串："..."（单行，不包含换行）
- 三引号字符串：''' ... '''（多行，原样保留换行和空白）
- 提示：目前不支持转义序列（如 \n），如需跨行请使用三引号。

```jim
var a = "Hello";
var b = '''
Line 1
Line 2
Line 3
''';
println(a)
println(b)
```
## 字符串函数
- contains(s, sub) -> boolean
- replace(s, target, repl) -> string
- startsWith(s, prefix) / endsWith(s, suffix) -> boolean
- repeat(s, times:int) -> string
- padLeft(s, width:int[, padChar]) / padRight(s, width:int[, padChar]) -> string
- split(s, sep) -> array
- substring(s, start[, end]) -> string
- indexOf(s, sub) -> number
- toUpperCase/upper, toLowerCase/lower, trim, len

## 数组函数
- push(arr, value) -> number
- pop(arr) -> any | null
- shift(arr) -> any | null
- unshift(arr, value) -> number
- join(arr, sep) -> string

## 数学函数
- pow(a, b) / sqrt(x) / floor(x) / ceil(x) -> number
- randomRange(min, max) -> number
- max(a,b)/min(a,b)/abs(x)/round(x)/random()

## 文件 I/O
- file_read(path) -> string | null
- file_write(path, content) -> boolean  // UTF-8
- file_exists(path) -> boolean
- file_append(path, content) -> boolean  // UTF-8

## 类型与集合（扩展）
- typeof(x) -> "number" | "string" | "boolean" | "array" | "object" | "null"
- isArray(x) / isObject(x) -> boolean
- keys(obj) -> array; values(obj) -> array
- parseInt(s) / parseFloat(s) -> number


## 环境变量
- 内置：env_get(name[, default]) / env_all() / load_env(path[, override=false])
- 行为：
  - env_get 优先读取内存覆盖层（由 load_env(..., true) 注入），否则读取进程环境变量
  - env_all 返回覆盖层 + 系统环境变量的合并（覆盖层优先）
  - load_env 解析 .env 文件（支持开头的 export、# 注释、key=value、去首尾引号）；override=true 则写入覆盖层

```jim
// 读取系统环境变量
println( env_get("PATH", "<none>") )

// 加载 .env 到覆盖层，后续 env_get 优先取这里
load_env(".env", true)
println( env_get("DB_URL", "jdbc:sqlite:dev.db") )

// 查看所有环境变量（覆盖层优先）
var all = env_all()
println( typeof(all) )   // object
```## JSON / YAML
```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
println( contains(j, "a") )
var x = json_decode(j)
println( x.a )         // 1

println( json_pretty(o, 2) )

var y = yml_encode(o, 2)  // 需要 SnakeYAML 才能完整支持
# var m = yml_decode(y)

// 文件读写助手
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")
yml_dump(o, "tmp.yml", 2)
var oy = yml_load("tmp.yml")
```

## CLI 快速用法
- 启动 REPL: `jimlang --cli` 或 `jimlang -i`
- 执行一行: `jimlang --eval "println(1+2)"` 或 `jimlang -e "..."`
- 从 STDIN 读: `echo println(42) | jimlang -`
- 启用跟踪: `jimlang --trace examples/fibonacci.jim`（或设置环境变量 `JIM_TRACE=1`）

## 作用域（函数与代码块）
函数内遮蔽:
```jim
var x = 1
function f(){ var x = 2; println(x) }
f()
println(x)
// 2
// 1
```

独立块遮蔽:
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

从函数内部修改外部变量:
```jim
var y = 1
function g(){ y = 3 }
g()
println(y)
// 3
```

## Delegates & apply（兼容）
> 不推荐：仅保留兼容用途。优先使用“第一类函数”写法（见下）。
```jim
function add(a,b){ return a + b }
var d = delegate("add")
println( apply(d, 2, 3) )        // 5

var inc = partial(d, 1)
println( apply(inc, 41) )        // 42
```

详见: `examples/lib_functional.jim`

## 第一类函数（推荐）
> 推荐：优先使用“第一类函数”（函数值 + callSuffix 调用），链式调用与存储更直观。
```jim
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )        // 5

var inc = partial(add, 1)
println( inc(41) )        // 42

var p = println
p("ok")                    // 系统函数作为值

var ops = [ add ]
println( ops[0](10, 5) )  // 15
```
## 内置 Web 服务
```jim
function api(req){ return { ok: true } }
start_webserver(8080,
  "/v1/foo", "GET", api,
  "/v1/bar", "POST", api
)
```
- 路由：两种写法均可
  - 三元组：`start_webserver(port, "path","METHOD", handler, ...)`（不限条数）
  - 构造器：`start_webserver(port, [ route(path, method, handler), ... ])`
- handler 签名：`function h(req){ ... }`，`req` 含以下字段：
  - `method`, `path`, `params`（路径参数，如 `:id`），`splat`（通配余项 `*`）
  - `query`（查询参数 map），`headers`（请求头 map），`body`（原文），`json`（自动解析），`cookies`（解析后的 cookie map）
- 返回值（响应）：
  - `string` → `text/plain`；`map/array` → `application/json`
  - `response(status, headers, body)`：完全自定义
  - 便捷函数：
    - 文本/HTML/JSON：`send_text(body[, status])`、`send_html(html[, status])`、`send_json(obj[, status])`
    - 重定向：`redirect(location)` 或 `redirect(status, location)`
    - 头部：`set_header(respOrBody, name, value)`（可链式）
    - 二进制：`response_bytes(status[, headers], bytes)`
- 文件/下载：
  - `send_file(path[, mime][, status])`
  - `attachment_file(path, filename[, mime][, status])`
  - 低阶：`file_read_bytes(path)` 返回字节数组
- Cookie：
  - `set_cookie(respOrBody, name, value[, opts])`
  - `get_cookie(req, name)`、`clear_cookie(respOrBody, name)`
  - `opts` 支持：`path`、`domain`、`httpOnly`、`secure`、`maxAge`、`expires`、`sameSite`
- 路径匹配：
  - `:param` → `req.params.param`；`/*` → `req.params.splat`
  - 方法匹配：`GET/POST/...` 或 `ANY/*`

示例：参数与通配
```jim
function show(req){ return { id: req.params.id } }
function file(req){ return { rest: req.params.splat } }
start_webserver(8082,
  "/users/:id", "GET", show,
  "/static/*",  "GET", file
)
```
示例项目：examples/web_app（完整 Web 应用演示）- 端口可从环境变量读取：先 load_env(".env", true)；再用 env_get("PORT", 8080)。

## LLM（OpenAI 兼容）
- ask_llm(prompt[, overrides]) -> string
- 默认配置（无 env 时）
  - name=ds, model=deepseek-chat, base_url=https://api.deepseek.com, api_type=openai, temperature=0.2, stream=false, thinking=false
- 覆盖优先级：`overrides` 参数 > 覆盖层/环境变量（LLM_*）> 默认值
  - 支持的 LLM_*：LLM_NAME/LLM_MODEL/LLM_BASE_URL/LLM_API_KEY/LLM_API_TYPE/LLM_TEMPERATURE/LLM_STREAM/LLM_THINKING（也可用 DEEPSEEK_API_KEY）
- 用法：
```jim
// 使用默认（需先设置 LLM_API_KEY 或 DEEPSEEK_API_KEY）
// println( ask_llm("今天天气？") )

// 使用 overrides map 覆盖
// println( ask_llm("简述项目特点", { temperature: 0.3, model: "deepseek-chat" }) )
```
## LLM Streaming 说明
- stream=true 实时输出 token，并在函数返回时提供完整字符串。
- 关闭直接打印：`{ stream: true, print: false }`（仅返回聚合文本）。
- 自定义回调：`{ stream: true, on_token: fn }`，每个 token 到达回调一次：`fn(token)`；可与 print:false 搭配。
- 调试：设置环境变量 `LLM_DEBUG=1` 打印调试信息。
- 实现：优先使用 LangChain4j OpenAiStreamingChatModel（若存在），否则回退 HTTP SSE。

PowerShell --eval 中文/引号：建议通过 STDIN 传入，避免转义：
```powershell
$code = @"
print(ask_llm("用一句话描述春天", { stream: true, temperature: 0.2 }))
"@
$code | bin\jimlang.cmd -
```
- 注意：在 overrides 对象里，'print' 是关键字，推荐使用 stream_print 代替（与 print 等效）。
- 示例：examples/llm_on_token_demo.jim（stream_print:false + on_token 回调演示）

## Database (JDBC)
- 基本函数：
  - db_open(name, { url, user, password, driver, pool, max_pool, min_idle, conn_timeout }) -> true
  - db_close(name) -> true/false
  - db_exec(target, sql[, params]) -> int（更新条数）
  - db_query(target, sql[, params]) -> array<map>
  - db_query_stream(target, sql, { params, on_row: fn, fetch_size }) -> int（行数）
  - with_conn(nameOrCfg, fn(conn))：获取连接传给 fn，结束后自动关闭
  - db_tx(nameOrCfg, fn(conn))：事务内执行 fn，出错自动回滚，结束自动提交并关闭
- 参数绑定：
  - 支持位置参数 `?`（传入数组），支持命名参数 `:name`（传入对象），数组会展开成 (?, ?, ?) 以便 `IN (:ids)`
- 结果映射：每行 map（列名使用 label），null 保持为 null，数值为 number，decimal->string，日期/时间 toString()
- 调试：`DB_DEBUG=1` 打印 sql/params/耗时
- JimSQL 示例：
  - driver: `com.dafei1288.jimsql.jdbc.JqDriver`
  - url: `jdbc:jimsql://localhost:8821/test`
  - 示例脚本：`examples/db_jimsql_demo.jim`
## return（块内返回）
- `return` 可在函数体内的任意嵌套块中使用（如 if/while 内部）
- 一旦返回，函数后续语句不会执行
- 示例：
```jim
function f(){ if (true) { return 42 } println(999) }
println(f())   // 42
```
## 变量（var/let/const）
- 作用域：{ ... } 为块作用域；ar/let/const 均为块级
- const：必须初始化；禁止二次赋值
- 重复声明：同一块作用域内重复声明将报错
- 示例：
`jim
{ let a = 1; const b = 2; println(a); println(b) }
// println(a)   // Error: Variable 'a' is not defined
const x = 1
// x = 2        // Error: Cannot reassign const variable 'x'
`"
=
+