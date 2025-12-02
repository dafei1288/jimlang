import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class TestStdLibLang {

    private String runAndCapture(String script) throws Exception {
        JimLangShell shell = new JimLangShell();
        PrintStream old = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8));
            shell.eval(script, null);
        } finally {
            System.setOut(old);
        }
        return bout.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void testStringAndArray() throws Exception {
        String script = String.join("\n",
                "println( contains(\"hello\", \"ell\") )",
                "println( replace(\"hello\", \"ll\", \"\") )",
                "println( startsWith(\"hello\", \"he\") )",
                "println( endsWith(\"hello\", \"lo\") )",
                "println( repeat(\"x\", 3) )",
                "println( padLeft(\"a\", 4) )",
                "println( padRight(\"a\", 4, \"*\") )",
                "var arr = split(\"a,b,c\", \",\")",
                "println( arr.length )",
                "println( arr[0] )",
                "println( arr[2] )"
        );
        String out = runAndCapture(script);
        String[] lines = out.replace("\r\n", "\n").split("\n");
        assertTrue(lines.length >= 10);
        assertEquals("true", lines[0]);
        assertEquals("heo", lines[1]);
        assertEquals("true", lines[2]);
        assertEquals("true", lines[3]);
        assertEquals("xxx", lines[4]);
        assertEquals("   a", lines[5]);
        assertEquals("a***", lines[6]);
        assertEquals("3", lines[7]);
        assertEquals("a", lines[8]);
        assertEquals("c", lines[9]);
    }

    @Test
    public void testFileAppendLang() throws Exception {
        Path tmp = Path.of("target/test-tmp/lang_append.txt");
        Files.createDirectories(tmp.getParent());
        String script = String.join("\n",
                "var p = \"" + tmp.toString().replace("\\", "\\\\") + "\"",
                "file_write(p, \"a\")",
                "file_append(p, \"b\")",
                "println( file_exists(p) )"
        );
        String out = runAndCapture(script).trim();
        assertTrue(out.endsWith("true"));
        String s = Files.readString(tmp);
        assertEquals("ab", s);
    }
}