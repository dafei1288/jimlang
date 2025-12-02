import com.dafei1288.jimlang.sys.Funcall;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestStdLib {

    private Object call(String name, Object... args) {
        List<Object> params = Arrays.asList(args);
        return Funcall.exec(name, params);
    }

    @Test
    public void testStringExtras() {
        assertEquals(true, call("contains", "hello", "ell"));
        assertEquals("heo", call("replace", "hello", "ll", ""));
        assertEquals(true, call("startsWith", "hello", "he"));
        assertEquals(true, call("endsWith", "hello", "lo"));
        assertEquals("xxx", call("repeat", "x", 3));
        assertEquals("   a", call("padLeft", "a", 4));
        assertEquals("a***", call("padRight", "a", 4, "*"));
    }

    @Test
    public void testMathExtras() {
        assertEquals(9.0, (Double) call("pow", 3, 2), 1e-9);
        assertEquals(3.0, (Double) call("sqrt", 9), 1e-9);
        assertEquals(3.0, (Double) call("floor", 3.7), 1e-9);
        assertEquals(4.0, (Double) call("ceil", 3.1), 1e-9);
        double d = ((Number) call("randomRange", 1.0, 2.0)).doubleValue();
        assertTrue(d >= 1.0 && d < 2.0);
        int r = ((Number) call("randomRange", 1, 3)).intValue();
        assertTrue(r >= 1 && r <= 3);
    }

    @Test
    public void testFileAppend() throws Exception {
        Path tmp = Path.of("target/test-tmp/stdlib_append.txt");
        Files.createDirectories(tmp.getParent());
        Files.deleteIfExists(tmp);
        assertEquals(true, call("file_write", tmp.toString(), "a"));
        assertEquals(true, call("file_append", tmp.toString(), "b"));
        String s = Files.readString(tmp);
        assertEquals("ab", s);
    }
}