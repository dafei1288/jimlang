package com.dafei1288.jimlang;

import com.dafei1288.jimlang.metadata.Symbol;
import com.dafei1288.jimlang.metadata.SymbolFunction;
import com.dafei1288.jimlang.metadata.SymbolVar;
import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Repl {
  private final static String SYMPOL_LINE = "\nJimLang>";
  private final static String VERSION = "0.1.0";

  public static void main(String[] args) {
    System.out.println("JimLang REPL v" + VERSION);
    System.out.println("Type :help for help, :exit to quit");

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    String scriptText = "";
    int paren = 0, brace = 0, bracket = 0;
    System.out.print(SYMPOL_LINE);

    // keep one visitor for stateful session
    JimLangVistor mlvistor = new JimLangVistor();

    while (true) {
      try {
        String line = reader.readLine();
        if (line == null) break;

        String raw = line;
        line = line.trim();

        // commands
        if (line.startsWith(":")) {
          handleCommand(raw, mlvistor);
          System.out.print(SYMPOL_LINE);
          continue;
        }

        // legacy exit
        if (line.equals("exit();")) {
          System.out.println("good bye!");
          break;
        }

        boolean contSlash = line.endsWith("\\");
        String toAdd = contSlash ? line.substring(0, Math.max(0, line.length()-1)) : line;
        scriptText += toAdd + "\n";

        // update counters
        for (char ch : toAdd.toCharArray()) {
          if (ch == '(') paren++; else if (ch == ')') paren--;
          else if (ch == '{') brace++; else if (ch == '}') brace--;
          else if (ch == '[') bracket++; else if (ch == ']') bracket--;
        }

        if (paren > 0 || brace > 0 || bracket > 0 || contSlash) {
          // wait for more lines
          continue;
        }

        CharStream stream= CharStreams.fromString(scriptText);
        JimLangLexer lexer=new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
        ParseTree parseTree = parser.prog();

        Object o = mlvistor.visit(parseTree);
        if (o != null) {
          System.out.println("=> " + o);
        }

        System.out.print(SYMPOL_LINE);
        scriptText = "";
        paren = brace = bracket = 0;

      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        if (System.getenv("DEBUG") != null) {
          e.printStackTrace();
        }
        System.out.print(SYMPOL_LINE);
        scriptText = "";
        paren = brace = bracket = 0;
      }
    }
  }

  private static void handleCommand(String command, JimLangVistor visitor) {
    String cmd = command.trim();

    if (cmd.equals(":help") || cmd.equals(":h")) {
      printHelp();
    } else if (cmd.equals(":exit") || cmd.equals(":quit") || cmd.equals(":q")) {
      System.out.println("good bye!");
      System.exit(0);
    } else if (cmd.equals(":vars") || cmd.equals(":v")) {
      showVariables(visitor);
    } else if (cmd.equals(":funcs") || cmd.equals(":f")) {
      showFunctions(visitor);
    } else if (cmd.equals(":clear") || cmd.equals(":c") || cmd.equals(":reset")) {
      visitor.getSymbolTable().clear();
      System.out.println("Symbol table cleared");
    } else if (cmd.startsWith(":load ")) {
      String path = command.substring(command.indexOf(' ') + 1).trim();
      try {
        java.util.List<String> lines = Files.readAllLines(Paths.get(path));
        String sc = String.join("\n", lines);
        CharStream stream= CharStreams.fromString(sc);
        JimLangLexer lexer=new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
        ParseTree parseTree = parser.prog();
        Object o = visitor.visit(parseTree);
        if (o != null) { System.out.println("=> " + o); }
        System.out.println("Loaded: " + path);
      } catch (Exception ex) {
        System.out.println("Failed to load: " + path + " - " + ex.getMessage());
      }
    } else {
      System.out.println("Unknown command: " + command);
      System.out.println("Type :help for available commands");
    }
  }

  private static void printHelp() {
    System.out.println("\nJimLang REPL Commands:");
    System.out.println("  :help, :h       - Show this help message");
    System.out.println("  :exit, :quit, :q - Exit the REPL");
    System.out.println("  :vars, :v       - Show all variables");
    System.out.println("  :funcs, :f      - Show all functions");
    System.out.println("  :clear, :c      - Clear all variables and functions");
    System.out.println("  :reset          - Alias of :clear");
    System.out.println("  :load <file>    - Load and execute a file");
    System.out.println();
  }

  private static void showVariables(JimLangVistor visitor) {
    System.out.println("\nVariables:");
    boolean hasVars = false;
    for (Map.Entry<String, Symbol> entry : visitor.getSymbolTable().entrySet()) {
      if (entry.getValue() instanceof SymbolVar) {
        SymbolVar var = (SymbolVar) entry.getValue();
        System.out.println("  " + var.getName() + " = " + var.getValue()
                         + (var.getTypeName() != null ? " : " + var.getTypeName() : ""));
        hasVars = true;
      }
    }
    if (!hasVars) {
      System.out.println("  (no variables defined)");
    }
    System.out.println();
  }

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