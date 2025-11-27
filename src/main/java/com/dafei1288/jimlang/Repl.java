package com.dafei1288.jimlang;


import com.dafei1288.jimlang.metadata.Symbol;
import com.dafei1288.jimlang.metadata.SymbolFunction;
import com.dafei1288.jimlang.metadata.SymbolVar;
import com.dafei1288.jimlang.parser.JimLangLexer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import com.dafei1288.jimlang.parser.JimLangParser;

public class Repl {
  private final static String SYMPOL_LINE = "\nJimLang>";
  private final static String VERSION = "0.1.0";

  public static void main(String[] args) {

    System.out.println("JimLang REPL v" + VERSION);
    System.out.println("Type :help for help, :exit to quit");

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    String scriptText = "";
    System.out.print(SYMPOL_LINE);

    // 在循环外创建 visitor，保持状态
    JimLangVistor mlvistor = new JimLangVistor();

    while (true) {
      try {
        String line = reader.readLine();
        if (line == null) break;

        line = line.trim();

        // 处理特殊命令
        if (line.startsWith(":")) {
          handleCommand(line, mlvistor);
          System.out.print(SYMPOL_LINE);
          continue;
        }

        // 兼容旧的退出方式
        if (line.equals("exit();")) {
          System.out.println("good bye!");
          break;
        }

        if (!line.endsWith("\\")) {
          scriptText += line + "\n";
          CharStream stream= CharStreams.fromString(scriptText);
          JimLangLexer lexer=new JimLangLexer(stream);
          JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
          ParseTree parseTree = parser.prog();

          Object o = mlvistor.visit(parseTree);

          // 如果有返回值，显示它
          if (o != null) {
            System.out.println("=> " + o);
          }

          System.out.print(SYMPOL_LINE);

          scriptText = "";
        }else{
          scriptText = scriptText+line.trim().substring(0,line.length()-1);
        }

      } catch (Exception e) {
        // 改善错误提示
        System.err.println("Error: " + e.getMessage());
        if (System.getenv("DEBUG") != null) {
          e.printStackTrace();
        }
        System.out.print(SYMPOL_LINE);
        scriptText = "";
      }
    }
  }

  /**
   * 处理特殊命令
   */
  private static void handleCommand(String command, JimLangVistor visitor) {
    String cmd = command.toLowerCase().trim();

    if (cmd.equals(":help") || cmd.equals(":h")) {
      printHelp();
    } else if (cmd.equals(":exit") || cmd.equals(":quit") || cmd.equals(":q")) {
      System.out.println("good bye!");
      System.exit(0);
    } else if (cmd.equals(":vars") || cmd.equals(":v")) {
      showVariables(visitor);
    } else if (cmd.equals(":funcs") || cmd.equals(":f")) {
      showFunctions(visitor);
    } else if (cmd.equals(":clear") || cmd.equals(":c")) {
      // 清空符号表
      visitor.getSymbolTable().clear();
      System.out.println("Symbol table cleared");
    } else {
      System.out.println("Unknown command: " + command);
      System.out.println("Type :help for available commands");
    }
  }

  /**
   * 显示帮助信息
   */
  private static void printHelp() {
    System.out.println("\nJimLang REPL Commands:");
    System.out.println("  :help, :h       - Show this help message");
    System.out.println("  :exit, :quit, :q - Exit the REPL");
    System.out.println("  :vars, :v       - Show all variables");
    System.out.println("  :funcs, :f      - Show all functions");
    System.out.println("  :clear, :c      - Clear all variables and functions");
    System.out.println("\nExamples:");
    System.out.println("  var x = 10");
    System.out.println("  function add(a, b) { return a + b }");
    System.out.println("  println(add(5, 3))");
    System.out.println();
  }

  /**
   * 显示所有变量
   */
  private static void showVariables(JimLangVistor visitor) {
    System.out.println("\nVariables:");
    boolean hasVars = false;
    for (Map.Entry<String, Symbol> entry : visitor.getSymbolTable().entrySet()) {
      if (entry.getValue() instanceof SymbolVar) {
        SymbolVar var = (SymbolVar) entry.getValue();
        System.out.println("  " + var.getName() + " = " + var.getValue() +
                         (var.getTypeName() != null ? " : " + var.getTypeName() : ""));
        hasVars = true;
      }
    }
    if (!hasVars) {
      System.out.println("  (no variables defined)");
    }
    System.out.println();
  }

  /**
   * 显示所有函数
   */
  private static void showFunctions(JimLangVistor visitor) {
    System.out.println("\nFunctions:");
    boolean hasFuncs = false;
    for (Map.Entry<String, Symbol> entry : visitor.getSymbolTable().entrySet()) {
      if (entry.getValue() instanceof SymbolFunction) {
        SymbolFunction func = (SymbolFunction) entry.getValue();
        String params = func.getParameterList() != null ?
                       String.join(", ", func.getParameterList()) : "";
        System.out.println("  " + func.getName() + "(" + params + ")");
        hasFuncs = true;
      }
    }
    if (!hasFuncs) {
      System.out.println("  (no functions defined)");
    }
    System.out.println();
  }

}
