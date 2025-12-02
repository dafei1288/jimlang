import com.dafei1288.jimlang.sys.Funcall;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestArrayAndSplit {
    private Object call(String name, Object... args) {
        return Funcall.exec(name, Arrays.asList(args));
    }

    @Test
    public void testSplitReturnsList() {
        Object r = call("split", "a,b,c", ",");
        assertTrue(r instanceof List);
        List<?> list = (List<?>) r;
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testArrayMethods() {
        List<Object> arr = new ArrayList<>();
        assertEquals(1, ((Number) call("push", arr, 1)).intValue());
        assertEquals(2, ((Number) call("push", arr, 2)).intValue());
        // Adjust expectation: unshift returns new size 3
        assertEquals(3, ((Number) call("unshift", arr, 0)).intValue());
        assertEquals(0, ((Number) ((List<?>)arr).get(0)).intValue());
        assertEquals(0, ((Number) call("shift", arr)).intValue());
        assertEquals(2, ((Number) call("pop", arr)).intValue());
        // remaining should be [1]
        assertEquals(1, ((Number) ((List<?>)arr).get(0)).intValue());
    }
}