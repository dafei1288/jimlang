package com.dafei1288.jimlang;


import com.dafei1288.jimlang.parser.JimLangLexer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import com.dafei1288.jimlang.parser.JimLangParser;

public class Repl {
  private final static String SYMPOL_LINE = "\nJimLang>";

  public static void main(String[] args) {


    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));   //从终端获取输入

    String scriptText = "";
    System.out.print(SYMPOL_LINE);   //提示符
    JimLangVistor mlvistor = new JimLangVistor();
    while (true) {             //无限循环
      try {
        String line = reader.readLine().trim(); //读入一行
        if (line.equals("exit();")) {   //硬编码退出条件
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

          System.out.print(SYMPOL_LINE);   //显示一个提示符

          scriptText = "";
        }else{
          scriptText = scriptText+line.trim().substring(0,line.length()-1);
//          System.out.print(SYMPOL_LINE);
        }

      } catch (Exception e) { //如果发现语法错误，报错，然后可以继续执行
        e.printStackTrace();
        System.out.println(e.getLocalizedMessage());
        System.out.print(SYMPOL_LINE);   //提示符
        scriptText = "";
      }
    }
  }

}
