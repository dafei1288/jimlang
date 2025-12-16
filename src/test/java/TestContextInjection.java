import com.dafei1288.jimlang.JimLangShell;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class TestContextInjection {

  @Test
  public void injectsVariablesAndReturnsValue() {
    Map<String,Object> input = new LinkedHashMap<>();
    input.put("name", "Alice");
    input.put("scores", Arrays.asList(2,3,5,7));

    Map<String,Object> ctx = new LinkedHashMap<>();
    ctx.put("input", input);
    ctx.put("discount", 0.85);
    ctx.put("tags", Arrays.asList("x","y","z"));

    String script = String.join("\n",
        "var sum = 0;",
        "for (var i = 0; i < input.scores.length; i = i + 1) { sum = sum + input.scores[i]; }",
        "var out = { name: input.name, final: sum * discount, tagCount: tags.length };",
        "out"
    );

    JimLangShell shell = new JimLangShell();
    Object ret = shell.eval(script, "<ctx-test>", ctx);
    assertNotNull(ret);
    @SuppressWarnings("unchecked")
    Map<String,Object> out = (Map<String,Object>) ret;
    assertEquals("Alice", out.get("name"));
    assertTrue(out.get("final") instanceof Number);
    double fin = ((Number)out.get("final")).doubleValue();
    assertEquals(14.45, fin, 1e-6);
    assertEquals(3, ((Number)out.get("tagCount")).intValue());
  }

  @Test
  public void ctxAllowsArbitraryKeysViaBracket() {
    Map<String,Object> ctx = new LinkedHashMap<>();
    ctx.put("user-id", "u123");
    ctx.put("weird.key", 42);
    ctx.put("k space", true);

    String script = String.join("\n",
        "var a = ctx[\"user-id\"];",
        "var b = ctx[\"weird.key\"];",
        "var c = ctx[\"k space\"];",
        "{ a: a, b: b, c: c }"
    );

    JimLangShell shell = new JimLangShell();
    Object ret = shell.eval(script, "<ctx-bracket>", ctx);
    assertNotNull(ret);
    @SuppressWarnings("unchecked")
    Map<String,Object> out = (Map<String,Object>) ret;
    assertEquals("u123", out.get("a"));
    assertEquals(42, ((Number)out.get("b")).intValue());
    assertEquals(Boolean.TRUE, out.get("c"));
  }
}