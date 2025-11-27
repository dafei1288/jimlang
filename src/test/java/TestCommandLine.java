import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestCommandLine {

    @Test
    public void testSimpleParam() {
        System.out.println("=== Test Simple Param ===");
        String script = """
                function double(n) {
                    return n + n
                }

                var x = double(5)
                println(x)
                """;
        executeScript(script);
    }

    @Test
    public void testFibonacci() {
        System.out.println("=== Test Fibonacci ===");
        String script = """
                function fibonacci(n) {
                    var a = 0
                    var b = 1
                    for (var i = 0; i < n; i = i + 1) {
                        var temp = a
                        a = b
                        b = temp + b
                    }
                    return a
                }

                println("Fibonacci sequence:")
                for (var i = 0; i < 10; i = i + 1) {
                    var result = fibonacci(i)
                    println(result)
                }
                """;
        executeScript(script);
    }

    @Test
    public void testFromFile() throws IOException {
        System.out.println("=== Test From File ===");
        List<String> lines = Files.readAllLines(Paths.get("examples/test_param_return_only.jim"));
        String script = String.join("\n", lines);
        System.out.println("Script content:");
        System.out.println(script);
        System.out.println("---");
        executeScript(script);
    }

    private void executeScript(String script) {
        try {
            JimLangShell shell = new JimLangShell();
            shell.eval(script, null);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
