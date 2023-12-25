import com.dafei1288.jimlang.JimLangShell;
import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import com.dafei1288.jimlang.JimLangVistor;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Test01 {


    @Test
    public void T3() throws IOException{

        String script = """
                function two() { return 2 } ;
                print( two() ) ;
                """;

        System.out.println(script);
        System.out.println("--------------------");
        JimLangShell shell = new JimLangShell();
        Object ret = shell.eval(script,null);
    }

    @Test
    public void T2() throws IOException {

        String script = """
                    var i = 11 ;
                    
                    function aa(i){
                      return i;
                    }
                    
                    print(aa(i))
                """;
        script = """
                function two() { return 2 } ;
                print( two() ) ;
                """;
//        script = "1 + 1 + 1 ; ";
        System.out.println(script);
        System.out.println("--------------------");
        CharStream stream = CharStreams.fromString(script);
        JimLangLexer lexer = new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));

        ParseTree parseTree = parser.prog();
        JimLangVistor mlvistor = new JimLangVistor();

        Object o = mlvistor.visit(parseTree);
//        System.out.println(o);
    }


    @Test
    public void T1() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("D:\\working\\opensource\\mylang\\src\\test\\java\\Test02.mylang"));
        String expr = lines.stream().collect(Collectors.joining("\n"));
        System.out.println(expr);
        System.out.println("--------------------");
        CharStream stream = CharStreams.fromString(expr);
        JimLangLexer lexer = new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));

        ParseTree parseTree = parser.prog();
        JimLangVistor mlvistor = new JimLangVistor();

        Object o = mlvistor.visit(parseTree);
//        System.out.println(o);
    }





}