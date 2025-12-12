import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestLetConstReturn {

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
        return bout.toString(StandardCharsets.UTF_8).replace("\r\n","\n").trim();
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
    public void testConstMustInitialize() {
        String msg = evalExpectError("const x");
        assertNotNull(msg);
        assertTrue(msg.contains("const variable must be initialized"));
    }

    @Test
    public void testConstReassignError() {
        String msg = evalExpectError("const x = 1\n x = 2");
        assertTrue(msg.contains("Cannot reassign const variable 'x'"));
    }

    @Test
    public void testLetBlockScope() throws Exception {
        String script = String.join("\n",
                "{ let a = 1; println(a) }",
                "println(a)"
        );
        String outOrMsg;
        try {
            outOrMsg = runAndCapture(script);
        } catch (Exception ex) {
            outOrMsg = null;
        }
        // First print should succeed, second should error
        String msg = evalExpectError(script);
        assertTrue(msg.contains("is not defined"));
    }

    @Test
    public void testLetShadowing() throws Exception {
        String script = String.join("\n",
                "let a = 1",
                "{ let a = 2; println(a) }",
                "println(a)"
        );
        String out = runAndCapture(script);
        String[] lines = out.split("\n");
        assertEquals("2", lines[0]);
        assertEquals("1", lines[1]);
    }

    @Test
    public void testLetRedeclareSameScopeError() {
        String msg = evalExpectError("let a = 1\nlet a = 2");
        assertTrue(msg.contains("already declared"));
    }

    @Test
    public void testReturnInsideIfBlock() throws Exception {
        String script = String.join("\n",
                "function f(){ if (true) { return 7 } println(999) }",
                "println(f())"
        );
        String out = runAndCapture(script);
        assertEquals("7", out.trim());
    }

    @Test
    public void testReturnNestedWhile() throws Exception {
        String script = String.join("\n",
                "function f(){ while (true) { if (true) { return 5 } } }",
                "println(f())"
        );
        String out = runAndCapture(script);
        assertEquals("5", out.trim());
    }
}