# Development Environment Setup

This guide will help you set up your development environment for contributing to JimLang.

## Prerequisites

### Required
- **Java Development Kit (JDK) 21 or higher**
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

- **Apache Maven 3.8 or higher**
  - Download from [Maven website](https://maven.apache.org/download.cgi)
  - Verify installation: `mvn -version`

### Recommended IDEs

#### IntelliJ IDEA (Recommended)
- Download: [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- The project already includes `.idea` configuration
- Plugins to install:
  - ANTLR v4 (for `.g4` grammar files)
  - Maven Helper

#### Visual Studio Code
- Download: [VS Code](https://code.visualstudio.com/)
- Open the workspace: `jimlang.code-workspace`
- Install recommended extensions (VS Code will prompt you):
  - Extension Pack for Java
  - Maven for Java
  - ANTLR4 grammar syntax support
  - EditorConfig for VS Code

#### Eclipse
- Download: [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/)
- Import as existing Maven project

## Initial Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd jimlang
```

### 2. Build the Project
```bash
# Clean and build
mvn clean package

# Skip tests for faster build
mvn clean package -DskipTests

# Install to local Maven repository
mvn clean install
```

### 3. Generate ANTLR Parser (if needed)
The ANTLR parser code is generated automatically during Maven build. If you modify the grammar files (`.g4`), run:
```bash
mvn clean compile
```

### 4. Run Tests
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=YourTestClass
```

## Project Structure

```
jimlang/
├── src/
│   ├── main/
│   │   └── java/com/dafei1288/jimlang/
│   │       ├── engine/          # JSR-223 ScriptEngine implementation
│   │       ├── metadata/        # Symbol table and scope management
│   │       ├── parser/          # ANTLR generated parser code
│   │       ├── JimLangShell.java    # Main interpreter shell
│   │       ├── JimLangVistor.java   # AST visitor implementation
│   │       └── Repl.java            # Read-Eval-Print Loop
│   └── test/
│       └── java/                # Unit tests
├── target/                      # Build output (gitignored)
├── doc/                         # Documentation
├── .editorconfig               # Editor configuration
├── .gitignore                  # Git ignore rules
├── pom.xml                     # Maven configuration
└── jimlang.code-workspace      # VS Code workspace settings
```

## Development Workflow

### Making Changes

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow the existing code style
   - The `.editorconfig` file will help maintain consistency

3. **Build and test**
   ```bash
   mvn clean package
   mvn test
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

### Code Style Guidelines

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Maximum 120 characters
- **Encoding**: UTF-8
- **Line endings**: LF (Unix style)
- **Naming conventions**:
  - Classes: PascalCase (e.g., `JimLangVisitor`)
  - Methods: camelCase (e.g., `visitStatement`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_SIZE`)
  - Variables: camelCase (e.g., `symbolTable`)

### Working with ANTLR Grammar

The grammar files define the JimLang syntax. If you modify them:

1. Edit the `.g4` files in `src/main/antlr4/` (if present) or check parser source location
2. Run `mvn clean compile` to regenerate parser code
3. Update the visitor/listener implementations as needed
4. Add tests for new language features

## Testing

### Running the REPL
```java
// Create a test file or run from IDE
public class TestRepl {
    public static void main(String[] args) {
        Repl repl = new Repl();
        repl.start(); // Interactive mode
    }
}
```

### Using JimLangShell
```java
String script = """
    function add(a, b) {
        return a + b;
    }
    var result = add(5, 3);
    println(result);
    """;

JimLangShell shell = new JimLangShell();
Object result = shell.eval(script, null);
```

### Using JSR-223 ScriptEngine
```java
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("jim");
engine.eval("println('Hello from JimLang!')");
```

## Troubleshooting

### Maven Build Issues

**Problem**: ANTLR code generation fails
- **Solution**: Ensure ANTLR dependency version matches your grammar version
- Run: `mvn clean compile -X` for debug output

**Problem**: Java version mismatch
- **Solution**: Verify `JAVA_HOME` points to JDK 21+
  ```bash
  echo $JAVA_HOME  # Unix/Mac
  echo %JAVA_HOME%  # Windows
  ```

### IDE Issues

**IntelliJ IDEA**: Parser classes not recognized
- Right-click on `target/generated-sources` → "Mark Directory as" → "Generated Sources Root"

**VS Code**: Java extension not working
- Open Command Palette (Ctrl+Shift+P) → "Java: Clean Language Server Workspace"

## Adding Maven Wrapper (Optional)

To ensure all developers use the same Maven version:

```bash
# If you have Maven installed locally
mvn wrapper:wrapper -Dmaven=3.9.5

# This creates:
# - mvnw (Unix shell script)
# - mvnw.cmd (Windows batch file)
# - .mvn/wrapper/ (wrapper files)
```

Then use `./mvnw` instead of `mvn` in all commands.

## Continuous Integration

When setting up CI/CD, use:
```bash
mvn clean verify
```

This runs:
- Compilation
- Tests
- Code quality checks (if configured)
- Package creation

## Resources

- [ANTLR Documentation](https://www.antlr.org/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JSR-223 Scripting API](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/package-summary.html)

## Getting Help

- Check existing issues in the repository
- Review the main [README.md](../README.md) for usage examples
- Consult the [doc/](doc/) directory for additional documentation

---

**Happy Coding!** 🚀
