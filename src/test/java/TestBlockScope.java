import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestBlockScope {

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
    public void testShadowingPrints() throws Exception {
        String script = String.join("\n",
                "var x = 1",
                "{ var x = 2; println(x) }",
                "println(x)"
        );
        String out = runAndCapture(script);
        String[] lines = out.split("\n");
        assertEquals("2", lines[0]);
        assertEquals("1", lines[1]);
    }

    @Test
    public void testAssignLocalThenOuter() throws Exception {
        String script = String.join("\n",
                "var x = 1",
                "{ var x = 2; x = 5; println(x) }",
                "println(x)"
        );
        String out = runAndCapture(script);
        String[] lines = out.split("\n");
        assertEquals("5", lines[0]);
        assertEquals("1", lines[1]);
    }

    @Test
    public void testAssignOuterFromBlock() throws Exception {
        String script = String.join("\n",
                "var y = 1",
                "{ y = 4; println(y) }",
                "println(y)"
        );
        String out = runAndCapture(script);
        String[] lines = out.split("\n");
        assertEquals("4", lines[0]);
        assertEquals("4", lines[1]);
    }

    @Test
    public void testBlockVarNotVisibleOutside_shouldError() {
        String script = String.join("\n",
                "{ var t = 9 }",
                "println(t)"
        );
        String msg = evalExpectError(script);
        assertNotNull(msg);
        assertTrue(msg.contains("is not defined"));
        assertTrue("should include caret", msg.contains("^"));
    }
}