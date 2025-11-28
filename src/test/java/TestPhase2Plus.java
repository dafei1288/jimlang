import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import com.dafei1288.jimlang.JimLangVistor;

public class TestPhase2Plus {

    private void runScript(String script) {
        try {
            CharStream stream = CharStreams.fromString(script);
            JimLangLexer lexer = new JimLangLexer(stream);
            JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
            ParseTree parseTree = parser.prog();
            JimLangVistor visitor = new JimLangVistor();
            visitor.visit(parseTree);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testChainedOps() {
        String script = """
                var x = 1 + 2 + 3
                var y = "a" + "b" + 1
                println(x)
                println(y)
                """;
        runScript(script);
    }

    @Test
    public void testBreakContinueFor() {
        String script = """
                for (var i = 0; i < 5; i = i + 1) {
                    if (i == 2) { continue }
                    if (i == 4) { break }
                    println(i)
                }
                """;
        runScript(script);
    }

    @Test
    public void testBreakContinueWhile() {
        String script = """
                var i = 0
                while (i < 5) {
                    i = i + 1
                    if (i == 2) { continue }
                    if (i == 4) { break }
                    println(i)
                }
                """;
        runScript(script);
    }

    @Test
    public void testArraysAndObjects() {
        String script = """
                var arr = [1, 2, 3]
                println(arr[0])
                arr[1] = 10
                println(arr.length)

                var person = { name: "Jim", age: 25 }
                println(person.name)
                person.age = 26
                println(person.age)

                var m = { a: { b: 2 } }
                println(m.a.b)
                var a2 = [[1,2],[3,4]]
                println(a2[1][0])
                println("hi".length)
                """;
        runScript(script);
    }

    @Test(expected = RuntimeException.class)
    public void testArrayIndexOutOfBounds() {
        String script = """
                var arr = [1]
                println(arr[2])
                """;
        runScript(script);
    }

    @Test(expected = RuntimeException.class)
    public void testAssignLengthShouldFail() {
        String script = """
                var arr = [1,2]
                arr.length = 10
                """;
        runScript(script);
    }

    @Test(expected = RuntimeException.class)
    public void testPropertyAccessOnNonObject() {
        String script = """
                var x = 1
                println(x.foo)
                """;
        runScript(script);
    }

    @Test(expected = RuntimeException.class)
    public void testIndexOnNonArray() {
        String script = """
                var o = { a: 1 }
                println(o[0])
                """;
        runScript(script);
    }
}
