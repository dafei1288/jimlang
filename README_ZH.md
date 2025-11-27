# What is JimLang

JimLang是基于JVM的具有完善语言系统的编程语言，其主旨是帮助大家入门语言开发领域。

# 如何使用

添加snapshots仓库
```xml
<repositories>
      <repository>
        <id>jim</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      </repository>
</repositories>
```

引入jdbc依赖
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

或者使用 jsr-233 方式

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

# 参与开发

## 系统要求
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## 代码编译

`mvn clean package -DskipTest=true`


# 运行 REPL

编译项目后，可以启动交互式 REPL：

```bash
mvn clean package
java -cp target/jimlang-1.0-SNAPSHOT.jar com.dafei1288.jimlang.Repl
```

或者从 IDE 中运行 `Repl.java` 的 main 方法。

REPL 示例:
```
JimLang>
var x = 10
JimLang>
var y = 20
JimLang>
println(x + y)
30
JimLang>
function greet(name) { println("Hello, " + name) }
JimLang>
greet("World")
Hello, World
JimLang>
exit()
good bye!
```

# 语言特性

## 当前支持
- ✅ 变量声明：`var x = 10`
- ✅ 类型注解：`var name: string = "jim"`
- ✅ 函数定义：`function add(a, b) { return a + b }`
- ✅ 基本运算：`+, -, *, /, <, >, ==` 等
- ✅ 内置函数：`print()`, `println()`
- ✅ 注释：`//` 单行, `/* */` 多行

## 开发中
- 🚧 完整的作用域管理
- 🚧 函数参数传递
- 🚧 控制流：if/else, while, for
- 🚧 数据结构：数组、对象
- 🚧 更多标准库函数

详见 [开发路线图](ROADMAP.md) 和 [快速计划](ROADMAP_QUICK.md)

# 项目文档

- 📖 [README (English)](README.md) - 英文说明
- 📖 [README_ZH (中文)](README_ZH.md) - 本文档
- 🔧 [开发环境设置](DEVELOPMENT.md) - 开发者指南
- 📋 [快速参考](QUICKREF.md) - 常用命令和示例
- 🗺️ [开发路线图 (详细)](ROADMAP.md) - 完整开发计划
- 🗺️ [开发路线图 (快速)](ROADMAP_QUICK.md) - 精简版计划

# 如何贡献

我们欢迎各种形式的贡献！

1. **报告问题**: 发现 bug 或有建议？请创建 Issue
2. **提交代码**:
   - Fork 本项目
   - 创建特性分支: `git checkout -b feature/my-feature`
   - 提交更改: `git commit -am 'Add my feature'`
   - 推送分支: `git push origin feature/my-feature`
   - 创建 Pull Request
3. **完善文档**: 改进文档和示例
4. **分享经验**: 写文章、做演讲，分享你使用 JimLang 的经验

详见 [ROADMAP_QUICK.md](ROADMAP_QUICK.md) 中的近期任务列表。

# 学习资源

如果你对语言开发感兴趣，推荐以下资源：

- 📚 **《Crafting Interpreters》** - Robert Nystrom (强烈推荐!)
  - 免费在线阅读: https://craftinginterpreters.com/
  - 从零开始实现一门编程语言
- 📚 **《编程语言实现模式》** - Terence Parr (ANTLR 作者)
- 📚 **《编译原理》** (龙书) - 经典教材
- 🔗 **ANTLR 官方文档**: https://www.antlr.org/

# 路线图
# TODO 列表

- 更多内置函数（contains, replace, startsWith/endsWith, padLeft/padRight, repeat, pow, sqrt, floor, ceil, randomRange, file_append）
- 点调用/命名空间语法（如 s.length(), Math.max(), File.read()）
- 数组与对象类型（使 split 等返回结构化数据）

# 标准库（Fast Path）

本阶段提供一批可直接调用的全局内置函数（后续将支持点调用/命名空间）：

- 字符串
  - `len(s)`，`toUpperCase(s)`/`upper(s)`，`toLowerCase(s)`/`lower(s)`，`trim(s)`
  - `substring(s, start[, end])`，`indexOf(s, sub)`，`split(s, sep)`
- 数学
  - `max(a, b)`，`min(a, b)`，`abs(x)`，`round(x)`，`random()`
- 文件
  - `file_read(path)`，`file_write(path, content)`，`file_exists(path)`

示例：
```jim
var s = "Hello World"
println(len(s))
println(toUpperCase(s))
println(substring(s, 0, 5))
println(indexOf(s, "World"))
println(split("a,b,c", ","))
println(trim("  hi  "))

println(max(3, 9))
println(min(3, 9))
println(abs(0 - 5))
println(round(3.7))
println(random())

var p = "target/tmp/phase3.txt"
file_write(p, "Hello")
println(file_exists(p))
println(file_read(p))
```

更多候选见 ROADMAP Phase 3 - 3.5 节（TODO）。
