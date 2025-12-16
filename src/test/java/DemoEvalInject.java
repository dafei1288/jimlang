import com.dafei1288.jimlang.JimLangShell;
import java.util.*;

public class DemoEvalInject {
    public static void main(String[] args) {
        // 1) 准备 context 变量
        Map<String,Object> input = new LinkedHashMap<>();
        input.put("name", "Alice");
        input.put("scores", List.of(2, 3, 5, 7));

        Map<String,Object> config = new LinkedHashMap<>();
        config.put("mode", "fast");

        Map<String,Object> ctx = new LinkedHashMap<>();
        ctx.put("input", input);      // Map -> 脚本里当对象用：input.name / input.scores
        ctx.put("discount", 0.85);    // Number
        ctx.put("tags", List.of("x","y","z")); // List -> 脚本里当数组用：tags.length
        ctx.put("config", config);    // Map

        // 2) 脚本直接使用注入的变量
        String script = String.join("\n",
                "function process(){",
                "  var sum = 0;",
                "  for (var i = 0; i < input.scores.length; i = i + 1) { sum = sum + input.scores[i]; }",
                "  return ({",
                "    name: input.name,",
                "    final: sum * discount,",
                "    mode: config.mode,",
                "    tagCount: tags.length",
                "  });",
                "}",
                "var __out = process()",
                "__out" // 顶层最后一行表达式 -> 作为 eval 返回值
        );

        JimLangShell shell = new JimLangShell();
        Object ret = shell.eval(script, "<ctx-demo>", ctx);

        Map<?,?> out = (Map<?,?>) ret;
        System.out.println("name=" + out.get("name"));       // Alice
        System.out.println("final=" + out.get("final"));     // 17 * 0.85 = 14.45
        System.out.println("mode=" + out.get("mode"));       // fast
        System.out.println("tagCount=" + out.get("tagCount"));// 3
    }
}
