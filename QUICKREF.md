# JimLang - Quick Reference

## Common Commands

### Building
```bash
# Full clean build
mvn clean package

# Build without tests (faster)
mvn clean package -DskipTests

# Install to local Maven repository
mvn clean install

# Compile only (regenerates ANTLR parser if grammar changed)
mvn clean compile
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run specific test method
mvn test -Dtest=ClassName#methodName

# Run tests with debug output
mvn test -X
```

### Cleaning
```bash
# Remove build artifacts
mvn clean

# Clean and remove IDE files
mvn clean && rm -rf .idea/*.iml
```

### Development
```bash
# Check for dependency updates
mvn versions:display-dependency-updates

# Check for plugin updates
mvn versions:display-plugin-updates

# Validate project structure
mvn validate

# Run complete verification
mvn verify
```

## JimLang Quick Examples

### Hello World
```jim
println("Hello from JimLang!")
```

### Variables
```jim
var x = 10
var name = "JimLang"
var result = x * 2
println(result)  # Outputs: 20
```

### Functions
```jim
function add(a, b) {
    return a + b
}

function greet(name) {
    println("Hello, " + name + "!")
}

var sum = add(5, 3)
greet("Developer")
```

### Combined Example
```jim
function factorial(n) {
    if (n <= 1) {
        return 1
    }
    return n * factorial(n - 1)
}

var result = factorial(5)
println("5! = " + result)  # Outputs: 5! = 120
```

## Using JimLang from Java

### Method 1: JimLangShell (Native API)
```java
import com.dafei1288.jimlang.JimLangShell;

public class Example {
    public static void main(String[] args) {
        JimLangShell shell = new JimLangShell();

        String script = """
            function square(x) { return x * x; }
            var result = square(5);
            println(result);
            """;

        Object result = shell.eval(script, null);
    }
}
```

### Method 2: JSR-223 ScriptEngine
```java
import javax.script.*;

public class Example {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("jim");

        if (engine == null) {
            System.err.println("JimLang engine not found!");
            return;
        }

        engine.eval("println('Hello from JSR-223!')");
    }
}
```

### Adding JimLang to Your Project

**Add to pom.xml:**
```xml
<repositories>
    <repository>
        <id>jim</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.dafei1288</groupId>
        <artifactId>jimlang</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Project Structure Quick Map

```
src/main/java/com/dafei1288/jimlang/
├── JimLangShell.java          # Main interpreter entry point
├── JimLangVistor.java         # AST visitor (executes code)
├── Repl.java                  # Interactive REPL
├── engine/                    # JSR-223 ScriptEngine
│   ├── JimLangScriptEngine.java
│   └── JimLangScriptEngineFactory.java
├── metadata/                  # Symbol table & scope
│   ├── Symbol.java
│   ├── SymbolFunction.java
│   ├── SymbolVar.java
│   └── Scope.java
└── parser/                    # ANTLR generated files
    ├── JimLangLexer.java
    ├── JimLangParser.java
    └── JimLangVisitor.java
```

## Debugging Tips

### Enable Maven Debug Output
```bash
mvn -X <command>
```

### Enable Java Debug Logging
```bash
java -Djava.util.logging.level=FINE -jar target/jimlang-1.0-SNAPSHOT.jar
```

### IntelliJ IDEA: Debug Tests
1. Right-click on test class/method
2. Select "Debug 'TestName'"
3. Set breakpoints in visitor or shell code

### Common Issues

**Problem**: "Cannot find symbol" errors
- **Solution**: Run `mvn clean compile` to regenerate parser

**Problem**: Changes to grammar not reflected
- **Solution**: Delete `target/` and rebuild: `mvn clean compile`

**Problem**: REPL not responding
- **Solution**: Check for infinite loops or missing return statements

## Language Features Status

- ✅ Variables (`var`)
- ✅ Functions with parameters
- ✅ Function calls
- ✅ Arithmetic operations
- ✅ String concatenation
- ✅ Built-in `println` function
- ⏳ Control flow (if/else, loops) - Check current implementation
- ⏳ Data structures (arrays, objects) - Check current implementation
- ⏳ Standard library - In development

## Contributing

See [DEVELOPMENT.md](DEVELOPMENT.md) for detailed setup instructions.

Quick contribution workflow:
```bash
git checkout -b feature/my-feature
# Make changes
mvn clean package
mvn test
git commit -am "Add my feature"
git push origin feature/my-feature
# Create pull request
```

## Resources

- 📖 Main README: [README.md](README.md)
- 🔧 Development Setup: [DEVELOPMENT.md](DEVELOPMENT.md)
- 📚 Documentation: [doc/](doc/)
- 🌐 ANTLR: https://www.antlr.org/
- ☕ Java 21 Docs: https://docs.oracle.com/en/java/javase/21/

## 内置函数速查（Fast Path）

当前已支持以下全局内置函数（无需点调用）：

- 字符串
  - `len(s)`
  - `toUpperCase(s)` / `upper(s)`
  - `toLowerCase(s)` / `lower(s)`
  - `trim(s)`
  - `substring(s, start[, end])`
  - `indexOf(s, sub)`
  - `split(s, sep)`  // 返回形如 "[a, b, c]" 的字符串
- 数学
  - `max(a, b)`, `min(a, b)`
  - `abs(x)`  // 语法暂不支持一元负号，写作 `abs(0 - 5)`
  - `round(x)`
  - `random()`
- 文件
  - `file_read(path)`
  - `file_write(path, content)`  // 返回 true/false
  - `file_exists(path)`

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

注意：下一步将支持点调用/命名空间（如 `s.length()`, `Math.max()`, `File.read()`），并引入数组/对象类型，使 `split` 等返回结构化数据。
