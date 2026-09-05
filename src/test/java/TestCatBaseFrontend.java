import com.dafei1288.jimlang.catbase.CatBaseRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Tests for the native CatBase front-end (CatBase.g4 + CatBaseVistor).
 * These run .cat sources directly through the interpreter and assert output,
 * covering features that the text translator (CatBaseCompat) does not have:
 * f-strings, keyword/default arguments, dict writes, method chains,
 * negative-step range, integer division, recursion.
 */
public class TestCatBaseFrontend {

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

    private String runCat(String catSource) {
        out.reset();
        CatBaseRunner.run(catSource, "<test-cat>");
        return out.toString();
    }

    @Test
    public void testHelloAndComments() {
        String src = "# a comment\ndef main(args: list[str]) {\n    print(\"Hello world!\\n\")\n}\n";
        assertEquals("Hello world!\n", runCat(src));
    }

    @Test
    public void testTypedFunctionsAndConversions() {
        String src = "" +
            "def add(a: int, b: int) -> int {\n    return a + b\n}\n" +
            "def main(args: list[str]) {\n" +
            "    total: int = add(40, 2)\n" +
            "    s: str = \"123\"\n" +
            "    n: int = int(s)\n" +
            "    print(\"t=\", total, \" n=\", n, \" s=\", str(n), \"\\n\")\n" +
            "    print(\"f=\", float(n), \" b=\", bool(n), \"\\n\")\n" +
            "}\n";
        assertEquals("t=42 n=123 s=123\nf=123.0 b=true\n", runCat(src));
    }

    @Test
    public void testControlFlowElseIfRange() {
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    fruits: list[str] = [\"a\", \"b\"]\n" +
            "    count: int = 0\n" +
            "    for i in range(0, len(fruits)) {\n" +
            "        count = count + 1\n" +
            "        print(\"f[\", i, \"]=\", fruits[i], \"\\n\")\n" +
            "    }\n" +
            "    if count > 5 {\n        print(\"big\\n\")\n" +
            "    } else if count > 1 {\n        print(\"mid\\n\")\n" +
            "    } else {\n        print(\"small\\n\")\n    }\n" +
            "    for d in range(4, 0, -1) {\n        print(\"d=\", d, \"\\n\")\n    }\n" +
            "}\n";
        assertEquals("f[0]=a\nf[1]=b\nmid\nd=4\nd=3\nd=2\nd=1\n", runCat(src));
    }

    @Test
    public void testDictStringKeysWriteAndJson() {
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    data: dict[str, any] = {\n" +
            "        \"model\": \"jim\",\n" +
            "        \"n\": 0,\n" +
            "        \"ok\": True\n" +
            "    }\n" +
            "    data[\"n\"] = data[\"n\"] + 41\n" +
            "    print(\"model=\", data[\"model\"], \" n=\", data[\"n\"], \" ok=\", data[\"ok\"], \"\\n\")\n" +
            "    s: str = json_dumps(data)\n" +
            "    print(s, \"\\n\")\n" +
            "}\n";
        assertEquals("model=jim n=41 ok=true\n{\"model\":\"jim\",\"n\":41,\"ok\":true}\n", runCat(src));
    }

    @Test
    public void testFStringKwargsDefaultsRecursion() {
        String src = "" +
            "def greet(name: str, times: int = 1) -> str {\n" +
            "    return f\"hi {name} x{times}\"\n}\n" +
            "def fib(n: int) -> int {\n" +
            "    if n < 2 {\n        return n\n    }\n" +
            "    return fib(n - 1) + fib(n - 2)\n}\n" +
            "def main(args: list[str]) {\n" +
            "    print(greet(\"jim\"), \"\\n\")\n" +
            "    print(greet(name=\"cat\", times=3), \"\\n\")\n" +
            "    a: int = 7\n" +
            "    print(f\"a={a} a2={a * 2} neg={-a}\\n\")\n" +
            "    print(\"fib(10)=\", fib(10), \"\\n\")\n" +
            "}\n";
        assertEquals("hi jim x1\nhi cat x3\na=7 a2=14 neg=-7\nfib(10)=55\n", runCat(src));
    }

    @Test
    public void testMethodChainIntegerDivisionLogic() {
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    s: str = \"  Hello.Cat  \"\n" +
            "    print(s.strip().lower(), \"|\", s.upper(), \"\\n\")\n" +
            "    print(\"7/2=\", 7 / 2, \" 7.0/2=\", 7.0 / 2, \"\\n\")\n" +
            "    x: int = 10\n" +
            "    if x > 0 and x < 20 {\n        print(\"range ok\\n\")\n    }\n" +
            "    if not (x == 99) {\n        print(\"not ok\\n\")\n    }\n" +
            "    total: int = 0\n" +
            "    for v in [1, 2, 3] {\n        total = total + v\n    }\n" +
            "    print(\"total=\", total, \"\\n\")\n" +
            "}\n";
        assertEquals("hello.cat|  HELLO.CAT  \n7/2=3 7.0/2=3.5\nrange ok\nnot ok\ntotal=6\n", runCat(src));
    }

    @Test
    public void testDictJsonRoundTripViaFuncallShared() {
        // json_loads -> json_decode is shared with the JimLang Funcall layer
        String src = "" +
            "def main(args: list[str]) {\n" +
            "    s: str = \"{\\\"a\\\": 1, \\\"b\\\": [1, 2]}\"\n" +
            "    d: dict[str, any] = json_loads(s)\n" +
            "    print(\"a=\", d[\"a\"], \" b1=\", d[\"b\"][1], \"\\n\")\n" +
            "}\n";
        assertEquals("a=1 b1=2\n", runCat(src));
    }
}
