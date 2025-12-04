import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestJavaFqcnTypes {

    private String runAndCapture(String script) throws Exception {
        JimLangShell shell = new JimLangShell();
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8));
            shell.eval(script, "<test>", null);
        } finally {
            System.setOut(old);
        }
        return bout.toString(StandardCharsets.UTF_8);
    }

    private String evalExpectError(String script) {
        JimLangShell shell = new JimLangShell();
        try {
            shell.eval(script, "<test>", null);
            fail("Expected RuntimeException");
            return null; // unreachable
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    @Test
    public void testJavaListAndMapAssignable() throws Exception {
        String script = String.join("\n",
                "var l:java.util.List = [1,2,3]",
                "println(join(l, \",\"))",
                "var m:java.util.Map = { a: 1 }",
                "println(keys(m).length)"
        );
        String out = runAndCapture(script).replace("\r\n", "\n").trim();
        String[] lines = out.split("\n");
        assertTrue(lines.length >= 2);
        assertEquals("1,2,3", lines[0]);
        assertEquals("1", lines[1]);
    }

    @Test
    public void testJavaStringTypeMismatch_withCaretAndStack() {
        String script = String.join("\n",
                "function f(){",
                "  var s:java.lang.String = 42",
                "}",
                "f()"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("runtime error"));
        assertTrue(msg.contains("Type mismatch"));
        assertTrue(msg.contains("java.lang.String"));
        assertTrue("should include caret line", msg.contains("^"));
        assertTrue("should include call stack", msg.contains("Call stack"));
        assertTrue("should include function frame", msg.contains("f()"));
    }

    @Test
    public void testUnknownJavaType_withCaretAndStack() {
        String script = String.join("\n",
                "function f(){ var x:com.example.DoesNotExist = 1 }",
                "f()"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("runtime error"));
        assertTrue(msg.contains("Unknown type"));
        assertTrue(msg.contains("com.example.DoesNotExist"));
        assertTrue("should include caret line", msg.contains("^"));
        assertTrue("should include call stack", msg.contains("Call stack"));
        assertTrue("should include function frame", msg.contains("f()"));
    }

    @Test
    public void testReassignIncompatible_withCaretAndStack() {
        String script = String.join("\n",
                "function g(){",
                "  var l:java.util.List = [1,2]",
                "  l = { a: 1 }",
                "}",
                "g()"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("runtime error"));
        assertTrue(msg.contains("Type mismatch"));
        assertTrue(msg.contains("java.util.List"));
        assertTrue("should include caret line", msg.contains("^"));
        assertTrue("should include call stack", msg.contains("Call stack"));
        assertTrue("should include function frame", msg.contains("g()"));
    }
}