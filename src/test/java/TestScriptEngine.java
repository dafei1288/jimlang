import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestScriptEngine {

    @Test
    public void test01() throws ScriptException {
        String script = """
                function two() { return 2 ; } ;
                function one() { return 1 ; } ;
                var x = one() + two() ;
                println("this message is from jimlang!!!")
                println( x ) ;
                """;
        System.out.println(script);
        System.out.println("--------------------");
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("jim");
        engine.eval(script);
    }
}

