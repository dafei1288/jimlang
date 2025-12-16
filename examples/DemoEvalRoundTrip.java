import com.dafei1288.jimlang.JimLangShell;
import java.util.*;

public class DemoEvalRoundTrip {
  public static void main(String[] args) {
    Map<String,Object> ctx = new LinkedHashMap<>();

    Map<String,Object> input = new LinkedHashMap<>();
    input.put("name", "Bob");
    input.put("scores", Arrays.asList(1,4,9));

    ctx.put("input", input);
    ctx.put("discount", 0.90);
    ctx.put("user-id", "u999");     // ctx["user-id"]

    String script1 = String.join("\n",
      "var sum = 0;",
      "for (var i = 0; i < input.scores.length; i = i + 1) { sum = sum + input.scores[i]; }",
      "var uid = ctx[\"user-id\"];",
      "{ name: input.name, subtotal: sum, uid: uid }"
    );

    JimLangShell shell = new JimLangShell();
    @SuppressWarnings("unchecked")
    Map<String,Object> out1 = (Map<String,Object>) shell.eval(script1, "<rt-1>", ctx);
    System.out.println("out1=" + out1);

    // Java
    ctx.put("firstOut", out1);

    String script2 = String.join("\n",
      "var boosted = firstOut.subtotal * discount;",
      "{ who: firstOut.name, boosted: boosted, uid: firstOut.uid }"
    );

    @SuppressWarnings("unchecked")
    Map<String,Object> out2 = (Map<String,Object>) shell.eval(script2, "<rt-2>", ctx);
    System.out.println("out2=" + out2);
  }
}
