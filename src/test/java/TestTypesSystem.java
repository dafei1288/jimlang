import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestTypesSystem {

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
    public void testIntFloatNumberWidening() throws Exception {
        String script = String.join("\n",
                "var i:int = 2",
                "var f:float = i",
                "println(f)",
                "var n:number = f",
                "println(n)"
        );
        String out = runAndCapture(script).replace("\r\n", "\n").trim();
        String[] lines = out.split("\n");
        assertTrue("expect at least 2 lines", lines.length >= 2);
        assertEquals("2.0", lines[0]); // float printed as 2.0
        assertEquals("2.0", lines[1]); // number from float printed as 2.0
    }

    @Test
    public void testFloatLiteralToIntShouldFail_withCaretAndStack() {
        String script = String.join("\n",
                "function f(){",
                "  var a:int = 1.2",
                "}",
                "f()"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("runtime error"));
        assertTrue(msg.contains("Type mismatch"));
        assertTrue(msg.toLowerCase().contains("int"));
        assertTrue("should include caret line", msg.contains("^"));
        assertTrue("should include call stack", msg.contains("Call stack"));
        assertTrue("should include function frame", msg.contains("f()"));
    }

    @Test
    public void testAssignNonIntegerToIntVarShouldFail_withCaretAndStack() {
        String script = String.join("\n",
                "var a:int = 1",
                "function g(){ a = 2.5 }",
                "g()"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("runtime error"));
        assertTrue(msg.contains("Type mismatch"));
        assertTrue(msg.toLowerCase().contains("int"));
        assertTrue("should include caret line", msg.contains("^"));
        assertTrue("should include call stack", msg.contains("Call stack"));
        assertTrue("should include function frame", msg.contains("g()"));
    }
}