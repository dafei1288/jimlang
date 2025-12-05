# JimLang 是什么

JimLang 是一个基于 JVM 的脚本/编程语言，语法简洁，便于嵌入到 Java 应用中。它的目标是：
- 让初学者快速进入“语言开发/实现”的世界；
- 提供可用的 CLI/REPL 和标准库；
- 在较小的实现规模下，展现解析、语义、执行、错误处理等完整链路。

## 环境要求
- Java >= 21
- Maven >= 3.8

## 从源码构建
```bash
mvn -q -DskipTests package
```

## CLI 快速上手
- 运行脚本：`bin\\jimlang.cmd examples\\fibonacci.jim`
- 启动 REPL：`bin\\jimlang.cmd --cli`（或 `-i`）
- 执行单行：`bin\\jimlang.cmd --eval "println(1+2)"`（或 `-e`）
- 从 STDIN 读取：`echo println(42) | bin\\jimlang.cmd -`
- 启用调用跟踪：`bin\\jimlang.cmd --trace examples\\fibonacci.jim`（或设置环境变量 `JIM_TRACE=1`）

更多命令与示例见：`doc/QUICKREF.md`

## 在 Java 中嵌入执行（JimLangShell）
```java
@Test
public void demo() throws IOException {
    String script = """
            function two() { return 2 ; }
            function one() { return 1 ; }
            var x = one() + two() ;
            println("this message is from jimlang!!!")
            println(x)
            """;
    JimLangShell shell = new JimLangShell();
    Object ret = shell.eval(script, null);
}
```

## 使用 JSR-223（可选）
```java
@Test
public void demoJsr223() throws ScriptException {
    String script = """
            function two() { return 2 ; }
            function one() { return 1 ; }
            var x = one() + two() ;
            println("this message is from jimlang!!!")
            println(x)
            """;
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("jim");
    engine.eval(script);
}
```

## 示例（Examples）
- `examples/fibonacci.jim`：斐波那契数列
- `examples/stdlib_phase3.jim`：标准库展示
- 作用域示例：
  - `examples/scope_func.jim`：函数内遮蔽
  - `examples/scope_if.jim`：独立块遮蔽
  - `examples/scope_assign_outer.jim`：在函数内部修改外部变量

## 文档
- 快速参考：`doc/QUICKREF.md`
- 路线图与当前状态：`doc/ROADMAP.md`
- 编码/换行规范：参见根目录 `agent.md`（操作指引）与 `doc/ROADMAP.md` 中的“编码/换行规范（摘要）”章节

## JSON/YAML
```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
var x = json_decode(j)
println( json_pretty(o, 2) )

// 文件读写
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")

yml_dump(o, "tmp.yml", 2)
// var oy = yml_load("tmp.yml")  // 需要 SnakeYAML
```
## 第一类函数
```jim
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )
```
```jim
var p = println
p("ok")
```
## 内置 Web 服务
```jim
function api(req){ return { ok: true } }
start_webserver(8080, "/api/ping", "GET", api)
```
- 路由：三元组 `(path, method, handler)` 或 `route(...)` 数组
- 请求：`req` 包含 method/path/params/splat/query/headers/body/json/cookies
- 响应：`send_text/send_html/send_json/redirect/set_header/response/response_bytes`
- 文件与下载：`send_file/attachment_file`；字节：`file_read_bytes`

详见：`doc/QUICKREF.md`（Web）
- examples/web_app：完整 Web 应用（路由/静态/Cookie/下载）