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

or use jsr-233

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

`mvn clean package -DskipTest=true`


# Roadmap
## CLI Quick Start

- Build: `mvn -q -DskipTests package`
- Run a script: `bin\\jimlang.cmd examples\\fibonacci.jim`
- Start REPL: `bin\\jimlang.cmd --cli` (or `-i`)
- Eval one-liner: `bin\\jimlang.cmd --eval "println(1+2)"` (or `-e`)
- Read from STDIN: `echo println(42) | bin\\jimlang.cmd -`
- Enable trace: `bin\\jimlang.cmd --trace examples\\fibonacci.jim` (or set `JIM_TRACE=1`)

See also:
- `doc/QUICKREF.md` – language snippets, stdlib, CLI
- `doc/ROADMAP.md` – phases, current status, TODO

## Examples
- `examples/fibonacci.jim` – Fibonacci sequence
- `examples/stdlib_phase3.jim` – stdlib showcase
- Scoping demos:
  - `examples/scope_func.jim` – function-local shadowing
  - `examples/scope_if.jim` – block scope shadowing
  - `examples/scope_assign_outer.jim` – assign to outer var inside function