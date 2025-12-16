import com.dafei1288.jimlang.JimLangShell;
import java.util.*;

public class DemoEvalInjectCtx {
  public static void main(String[] args) {
    Map<String,Object> input = new LinkedHashMap<>();
    input.put("name", "Alice");
    input.put("scores", Arrays.asList(2,3,5,7));

    Map<String,Object> ctx = new LinkedHashMap<>();
    ctx.put("input", input);
    ctx.put("discount", 0.85);
    ctx.put("tags", Arrays.asList("x","y","z"));
    ctx.put("user-id", "u123"); // 非标识符键，脚本里用 ctx["user-id"] 访问

    String script = String.join("\n",
      "function process(){",
      "  var sum = 0;",
      "  for (var i = 0; i < input.scores.length; i = i + 1) { sum = sum + input.scores[i]; }",
      "  var uid = ctx[\"user-id\"];",
      "  return ({ name: input.name, final: sum * discount, tagCount: tags.length, uid: uid });",
      "}",
      "var out = process()",
      "out"
    );

    JimLangShell shell = new JimLangShell();
    Object ret = shell.eval(script, "<ctx-demo>", ctx);
    @SuppressWarnings("unchecked")
    Map<String,Object> out = (Map<String,Object>) ret;
    System.out.println(out);
  }
}