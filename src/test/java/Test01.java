import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import wang.datahub.mylang.MLVistor;
import wang.datahub.mylang.MyLangLexer;
import wang.datahub.mylang.MyLangParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Test01 {
    @Test
    public void T1() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("/Volumes/arkamu/study/codespace/mylang/src/test/java/Test01.mylang"));
        String expr = lines.stream().collect(Collectors.joining("\n"));
        System.out.println(expr);
        System.out.println("--------------------");
        CharStream stream= CharStreams.fromString(expr);
        MyLangLexer lexer=new MyLangLexer(stream);
        MyLangParser parser = new MyLangParser(new CommonTokenStream(lexer));

        ParseTree parseTree = parser.prog();
        MLVistor mlvistor = new MLVistor();

        mlvistor.visit(parseTree);
    }
}
