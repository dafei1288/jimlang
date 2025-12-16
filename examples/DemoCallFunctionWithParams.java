import com.dafei1288.jimlang.JimLangShell;
import java.util.*;

public class DemoCallFunctionWithParams {
  public static void main(String[] args) {
    Map<String,Object> ctx = new LinkedHashMap<>();

    Map<String,Object> input = new LinkedHashMap<>();
    input.put("name", "Eve");
    input.put("scores", Arrays.asList(2,3,5,7));

    ctx.put("input", input);
    ctx.put("discount", 0.80);

    String script = String.join("\n",
      "function score(it, disc) {",
      "  var s = 0;",
      "  for (var i = 0; i < it.scores.length; i = i + 1) { s = s + it.scores[i]; }",
      "  return s * disc;",
      "}",
      "{ name: input.name, final: score(input, discount) }"
    );

    JimLangShell shell = new JimLangShell();
    @SuppressWarnings("unchecked")
    Map<String,Object> out = (Map<String,Object>) shell.eval(script, "<fn-demo>", ctx);
    System.out.println(out);
  }
}