# JimLang 是什么

JimLang 是一个基于 JVM 的脚本编程语言，语法简洁，易于嵌入 Java 应用。它希望：
- 帮助开发者快速入门“语言实现”的完整链路（词法/语法、语义、执行、错误处理等）
- 提供可用的 CLI/REPL 与标准库
- 以较小的代码规模展示一门语言从零到可用的过程

---

## 使用（Maven）

添加快照仓库：
```xml
<repositories>
  <repository>
    <id>jim</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```

添加依赖：
```xml
<dependency>
  <groupId>com.dafei1288</groupId>
  <artifactId>jimlang</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### 在 Java 中嵌入执行（JimLangShell）
```java
@Test
public void demo() throws IOException {
  String script = """
      function two() { return 2 ; } ;
      function one() { return 1 ; } ;
      var x = one() + two() ;
      println("this message is from jimlang!!!")
      println( x ) ;
      """;

  System.out.println(script);
  System.out.println("--------------------");
  JimLangShell shell = new JimLangShell();
  Object ret = shell.eval(script, null);
}
```

### 使用 JSR‑223（可选）
```java
@Test
public void demoJsr223() throws ScriptException {
  String script = """
      function two() { return 2 ; } ;
      function one() { return 1 ; } ;
      var x = one() + two() ;
      println("this message is from jimlang!!!")
      println( x ) ;
      """;

  System.out.println(script);
  System.out.println("--------------------");

  ScriptEngineManager manager = new ScriptEngineManager();
  ScriptEngine engine = manager.getEngineByName("jim");
  engine.eval(script);
}
```

> 说明：`ScriptEngineFactory.getParameter(NAME)` 返回的是 `"jim"`，因此可以通过 `getEngineByName("jim")` 获取引擎。

---

## 使用智能体快速部署（推荐）
- 克隆仓库后，在仓库根对智能体（Codex/Claude Code）说：`帮我部署开发环境` 或 `首次构建并进入开发`。
- 智能体将依次：检查 JDK 21+/Maven 3.8+；执行 `mvn -q -DskipTests package`；冒烟验证（Windows：`bin\jimlang.cmd --eval "println(1+2)"` 与 `examples\fibonacci.jim`；Unix：`bin/jimlang.sh --eval "println(1+2)"` 与 `examples/fibonacci.jim`）；启动 REPL（`--cli`）。可选执行 `build_win.bat`（jlink）或 `mkos.sh`（WSL/Linux）。
- 详细步骤与故障排查见 `AGENTS.md`。
## 开发与构建

- 环境要求：
  - Java >= 21
  - Maven >= 3.8

- 从源码构建：
  ```bash
  mvn -q -DskipTests package
  ```
- ide:
    [vs-code 扩展](https://marketplace.visualstudio.com/items?itemName=jimlang.jimlang-vscode)


---

## 命令行（CLI）与 REPL
- 运行脚本：`bin\\jimlang.cmd examples\\fibonacci.jim`
- 启动 REPL：`bin\\jimlang.cmd --cli`（或 `-i`）
- 执行单行：`bin\\jimlang.cmd --eval "println(1+2)"`（或 `-e`）
- 从 STDIN 读取：`echo println(42) | bin\\jimlang.cmd -`
- 开启执行跟踪：`bin\\jimlang.cmd --trace examples\\fibonacci.jim`（或设置环境变量 `JIM_TRACE=1`）

更多命令与示例见：`doc/QUICKREF.md`

---

## 示例（Examples）
- `examples/fibonacci.jim`：斐波那契数列
- `examples/stdlib_phase3.jim`：标准库展示
- 作用域示例：
  - `examples/scope_func.jim`：函数内遮蔽
  - `examples/scope_if.jim`：独立块遮蔽
  - `examples/scope_assign_outer.jim`：在函数内部修改外部变量

---

## JSON / YAML
```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
var x = json_decode(j)
println( json_pretty(o, 2) )

// 文件读写
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")

yml_dump(o, "tmp.yml", 2)
// var oy = yml_load("tmp.yml")  // 需要 SnakeYAML 依赖
```

---

## 字符串
- 支持三引号多行字符串：`''' ... '''`（保留换行与空白）。详见 `doc/QUICKREF.md`。

## 第一类函数（First‑class Functions）
```jim
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )
```
```jim
var p = println
p("ok")
```

---

## 环境变量（Environment）
- 内置：`env_get(name[, default])` / `env_all()` / `load_env(path[, override=false])`
- 行为：
  - `env_get` 先读覆盖层（由 `load_env(..., true)` 注入），否则读取进程环境变量
  - `env_all` 返回 覆盖层 + 进程环境变量 的合并（覆盖层优先）
  - `load_env` 解析 `.env`（支持前缀 `export`、`#` 注释、`key=value`、去首尾引号）。文件不存在时返回 `{}`。

详见：`doc/QUICKREF.md`

---

## 内置 Web 服务
```jim
function api(req){ return { ok: true } }
start_webserver(8080, "/api/ping", "GET", api)
```
- 路由：用三元组 `(path, method, handler)` 或 `route(path, method, handler)` 数组
- 请求：`req` 包含 method/path/params/splat/query/headers/body/json/cookies
- 响应：`send_text` / `send_html` / `send_json` / `redirect` / `set_header` / `response` / `response_bytes`
- 文件与下载：`send_file` / `attachment_file`；字节：`file_read_bytes`

更多内容见：`doc/QUICKREF.md`（Web 小节）
- `examples/web_app`
- `examples/env/web_port.jim`：读取 `.env` 中的 PORT、GREETING，演示完整 Web 应用（路由、静态、Cookie、下载）

---

## 与 Java 互操作：通过 Context 注入

- 在 Java 侧，通过 `JimLangShell.eval(script, sourceName, context)` 注入上下文：
  - 整个 `context` 会暴露为全局变量 `ctx`（Map）。
  - 满足标识符规则的键（`[A-Za-z_][A-Za-z0-9_]*`）会额外注入为同名全局变量（如 `input`、`discount`）。
  - 含特殊字符的键使用下标语法访问：`ctx["user-id"]`（当前只读）。

示例（Java）：
```java
Map<String,Object> input = Map.of(
  "name", "Alice",
  "scores", Arrays.asList(2,3,5,7)
);
Map<String,Object> ctx = new LinkedHashMap<>();
ctx.put("input", input);
ctx.put("discount", 0.85);
ctx.put("user-id", "u123");
String script = String.join("\n",
  "var sum = 0;",
  "for (var i = 0; i < input.scores.length; i = i + 1) { sum = sum + input.scores[i]; }",
  "var uid = ctx[\"user-id\"];",
  "{ name: input.name, final: sum * discount, uid: uid }"
);
Object ret = new JimLangShell().eval(script, "<inject-demo>", ctx);
System.out.println(ret); // -> {name=Alice, final=..., uid=u123}
```

更多示例：
- `examples/DemoEvalInjectCtx.java`
- `examples/DemoEvalRoundTrip.java`
- `examples/DemoCallFunctionWithParams.java`

> 说明：当前 `ctx["..."]` 为只读；后续可加入写入支持（例如 `ctx["k"] = v`）。

---

## Roadmap / 文档
- 语言：已支持在嵌套块（if/while/for）中“提前 return”
- 快速参考：`doc/QUICKREF.md`
- 路线图与当前状态：`doc/ROADMAP.md`
