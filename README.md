# What is JimLang

Jim Lang is a programming language based on JVM with a comprehensive language system, aimed at helping everyone get started in the field of language development.

# Usage

add snapshots repository
```xml
<repositories>
    <repository>
        <id>jim</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

set dependency
```xml
<dependency>
    <groupId>com.dafei1288</groupId>
    <artifactId>jimlang</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

```
    @Test
    public void T3() throws IOException{

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
        Object ret = shell.eval(script,null);
    }
```

or use jsr-223

```
    @Test
    public void test01() throws ScriptException {
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

# Development

## Prerequisites
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## Build from source

`mvn clean package -DskipTests=true`


# Roadmap

- Language: supports early return from nested blocks (if/while/for)
- Build: `mvn -q -DskipTests package`
- Run a script: `bin\\jimlang.cmd examples\\fibonacci.jim`
- Start REPL: `bin\\jimlang.cmd --cli` (or `-i`)
- Eval one-liner: `bin\\jimlang.cmd --eval "println(1+2)"` (or `-e`)
- Read from STDIN: `echo println(42) | bin\\jimlang.cmd -`
- Enable trace: `bin\\jimlang.cmd --trace examples\\fibonacci.jim` (or set `JIM_TRACE=1`)

See also:
- `doc/QUICKREF.md` language snippets, stdlib, CLI
- `doc/ROADMAP.md` phases, current status, TODO

## Examples
- `examples/fibonacci.jim` Fibonacci sequence
- `examples/stdlib_phase3.jim` stdlib showcase
- Scoping demos:
  - `examples/scope_func.jim` function-local shadowing
  - `examples/scope_if.jim` block scope shadowing
  - `examples/scope_assign_outer.jim` assign to outer var inside function
## JSON/YAML helpers

```jim
var o = { a: 1, b: [2,3] }
var j = json_encode(o)
var x = json_decode(j)
println(json_pretty(o, 2))

// file IO
json_dump(o, "tmp.json", 2)
var ox = json_load("tmp.json")

yml_dump(o, "tmp.yml", 2)
// var oy = yml_load("tmp.yml")  // requires SnakeYAML
```

## Strings
- Triple-quoted multi-line strings: ''' ... ''' (preserves newlines/whitespace). See doc/QUICKREF.md.\n\n## First-class functions

> Note: sysfunctions are also first-class values. You can assign and call them:
```jim
var p = println
p("ok")
```
```jim
function add(a,b){ return a + b }
var d = add
println( d(2, 3) )
```


## Environment
- Built-ins: env_get(name[, default]) / env_all() / load_env(path[, override=false])
- Behavior:
  - env_get reads from an in-memory overlay (populated via load_env(..., true)) before System.getenv
  - env_all returns merged overlay + process envs (overlay wins)
  - load_env parses .env (supports leading export, # comments, key=value, strip quotes). If file missing, returns {}.
See doc/QUICKREF.md.## Web Server (built-in)
```jim
function api(req){ return { ok: true } }
start_webserver(8080, "/api/ping", "GET", api)
```
- Routes: use triples `(path, method, handler)` or `route(path, method, handler)` array
- Handler receives `req`: method, path, params/splat, query, headers, body, json, cookies
- Response helpers: `send_text`, `send_html`, `send_json`, `redirect`, `set_header`, `response`, `response_bytes`
- Files: `send_file`, `attachment_file`; bytes: `file_read_bytes`

See more: `doc/QUICKREF.md` (Web section)
- examples/web_app
- examples/env/web_port.jim read .env for PORT and GREETING 锟?complete web app (routes, static, cookies, download)
## Java Interop via Context (read-only ctx["锟斤拷"]) 

- Use `JimLangShell.eval(script, sourceName, context)` to inject variables:
  - The entire map is exposed as `ctx` (Map).
  - Identifier-friendly keys (`[A-Za-z_][A-Za-z0-9_]*`) are also injected as globals (e.g., `input`, `discount`).
  - Keys with special characters are accessed by bracket syntax: `ctx["user-id"]` (currently read-only).

Example (Java):

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
System.out.println(ret);
```

- More examples:
  - `examples/DemoEvalInjectCtx.java`
  - `examples/DemoEvalRoundTrip.java`
  - `examples/DemoCallFunctionWithParams.java`

Note: `ctx["锟斤拷"]` is read-only for now; write support (e.g. `ctx["k"] = v`) can be added later.