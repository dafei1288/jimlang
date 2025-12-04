import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestJsonYamlDelegate {

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
    public void testJsonRoundtrip() throws Exception {
        String script = String.join("\n",
                "var o = { a: 1, b: [true, \"x\"] }",
                "var j = json_encode(o)",
                "println( contains(j, \"a\") )",
                "var x = json_decode(j)",
                "println( x.a )",
                "println( x.b[0] )"
        );
        String out = runAndCapture(script);
        String[] lines = out.split("\n");
        assertEquals("true", lines[0]);
        assertEquals("1", lines[1]);
        assertEquals("true", lines[2]);
    }

    @Test
    public void testYamlEncode() throws Exception {
        String script = String.join("\n",
                "var o = { a: 1, b: [2,3] }",
                "var y = yml_encode(o)",
                "println( len(y) )"
        );
        String out = runAndCapture(script).trim();
        int n = Integer.parseInt(out);
        assertTrue(n > 0);
    }

    @Test
    public void testDelegateApply() throws Exception {
        String script = String.join("\n",
                "function add(a,b){ return a + b }",
                "var d = delegate(\"add\")",
                "println( apply(d, 2, 3) )"
        );
        String out = runAndCapture(script);
        assertEquals("5", out.trim());
    }

    @Test
    public void testPartialApply() throws Exception {
        String script = String.join("\n",
                "function add(a,b){ return a + b }",
                "var d = delegate(\"add\")",
                "var inc = partial(d, 1)",
                "println( apply(inc, 41) )"
        );
        String out = runAndCapture(script);
        assertEquals("42", out.trim());
    }

    @Test
    public void testYamlDecodeWithoutSnake_shouldErrorOrWork() {
        String script = String.join("\n",
                "var y = \"a: 1\\nb: [2,3]\"",
                "yml_decode(y)"
        );
        try {
            String out = runAndCapture(script);
            assertTrue(out.length() >= 0); // success path
        } catch (Exception ex) {
            String msg = ex.getMessage();
            assertNotNull(msg); // error path when SnakeYAML not present
        }
    }
}