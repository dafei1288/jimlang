import com.dafei1288.jimlang.JimLangShell;
import com.dafei1288.jimlang.catbase.CatBaseCompat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * CatBase compatibility layer tests:
 *  - translation rewrites CatBase syntax into runnable JimLang
 *  - translated programs execute with expected output
 *  - grammar relaxations (string dict keys, int()/str(), polymorphic len)
 *    are available to plain JimLang scripts too
 */
public class TestCatBaseCompat {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream old = System.out;

    @Before
    public void captureStdout() {
        System.setOut(new PrintStream(out, true));
    }

    @After
    public void restoreStdout() {
        System.setOut(old);
    }

    private String run(String code) {
        out.reset();
        Object ret = new JimLangShell().eval(code, "<test-cat>", null);
        return out.toString();
    }

    private String runCat(String catSource) {
        return run(CatBaseCompat.translate(catSource));
    }

    @Test
    public void testTranslateBasics() {
        String src = "# comment\ndef main(args: list[str]) {\n    print(\"Hello world!\\n\")\n}\n";
        String out = CatBaseCompat.translate(src);
        assertTrue(out.contains("function main(args)"));
        assertFalse(out.contains("#"));
        assertTrue(out.contains("cat_print"));
        assertTrue(out.contains("main([])"));
    }

    @Test
    public void testHelloRuns() {
        String src = "# comment\ndef main(args: list[str]) {\n    print(\"Hello world!\\n\")\n}\n";
        assertEquals("Hello world!\n", runCat(src));
    }

    @Test
    public void testTypedFunctionsAndConversions() {
        String src = "" +
            "def add(a: int, b: int) -> int {\n" +
            "    return a + b\n" +
            "}\n" +
            "def main(args: list[str]) {\n" +
            "    total: int = add(40, 2)\n" +
            "    print(\"total=\", total, \"\\n\")\n" +
            "    s: str = \"123\"\n" +
            "    n: int = int(s)\n" +
            "    f: float = float(n)\n" +
            "    b: bool = bool(n)\n" +
            "    print(\"n=\", n, \" f=\", f, \" b=\", b, \"\\n\")\n" +
            "}\n";
        assertEquals("total=42\nn=123 f=123.0 b=true\n", runCat(src));
    }

    @Test
    public void testControlFlowTranslation() {
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    fruits: list[str] = [\"a\", \"b\"]\n" +
            "    count: int = 0\n" +
            "    for i in range(0, len(fruits)) {\n" +
            "        count = count + 1\n" +
            "        print(\"f[\", i, \"]=\", fruits[i], \"\\n\")\n" +
            "    }\n" +
            "    if count > 5 {\n" +
            "        print(\"big\\n\")\n" +
            "    } else if count > 1 {\n" +
            "        print(\"mid\\n\")\n" +
            "    } else {\n" +
            "        print(\"small\\n\")\n" +
            "    }\n" +
            "}\n";
        assertEquals("f[0]=a\nf[1]=b\nmid\n", runCat(src));
    }

    @Test
    public void testDictStringKeysAndJson() {
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    data: dict[str, any] = {\n" +
            "        \"model\": \"jim\",\n" +
            "        \"n\": 3,\n" +
            "        \"ok\": True\n" +
            "    }\n" +
            "    print(\"m=\", data[\"model\"], \" n=\", data[\"n\"], \"\\n\")\n" +
            "    s: str = json_dumps(data)\n" +
            "    print(s, \"\\n\")\n" +
            "}\n";
        assertEquals("m=jim n=3\n{\"model\":\"jim\",\"n\":3,\"ok\":true}\n", runCat(src));
    }

    @Test
    public void testPlainJimLangGains() {
        // these used to be syntax errors in JimLang; now they work
        assertTrue(run("println(int(\"42\") + 1)").contains("43"));
        assertEquals("x", run("var o = { \"a\": \"x\" }\nprintln(o[\"a\"])").trim());
        assertTrue(run("println(len([1,2,3]))").trim().contains("3"));
        // polymorphic len on dicts too
        assertEquals("2", run("var d = {\"k\": 1, \"j\": 2}\nprintln(len(d))").trim());
        // cat_print: no separator, no trailing newline
        assertEquals("ab", run("cat_print(\"a\", \"b\")"));
        assertEquals("ab", run("cat_print(\"a\", \"b\")\nprintln(\"c\")").substring(0, 2));
    }
}
