import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestFirstClassFunctions {

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

    @Test
    public void testAssignAndCall() throws Exception {
        String script = String.join("\n",
                "function add(a,b){ return a + b }",
                "var d = add",
                "println( d(2, 3) )"
        );
        String out = runAndCapture(script);
        assertEquals("5", out);
    }

    @Test
    public void testPartialThenCall() throws Exception {
        String script = String.join("\n",
                "function add(a,b){ return a + b }",
                "var inc = partial(add, 1)",
                "println( inc(41) )"
        );
        String out = runAndCapture(script);
        assertEquals("42", out);
    }

    @Test
    public void testArrayStoredDelegateCall() throws Exception {
        String script = String.join("\n",
                "function add(a,b){ return a + b }",
                "var arr = [ add ]",
                "println( arr[0](5, 7) )"
        );
        String out = runAndCapture(script);
        assertEquals("12", out);
    }

    @Test
    public void testSysFunctionAsValue() throws Exception {
        String script = String.join("\n",
                "var p = println",
                "p(\"ok\")"
        );
        String out = runAndCapture(script);
        assertEquals("ok", out);
    }
}