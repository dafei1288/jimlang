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
    public void testOptionalSemicolon() {
        System.out.println("=== Testing Optional Semicolon ===");

        // ???1?????????
        String test1 = """
                var x = 10
                var y = 20
                println(x + y)
                """;
        System.out.println("Test 1: No semicolons");
        runScript(test1);

        // ???2????????
        String test2 = """
                var a = 5 ;
                var b = 3 ;
                println(a * b) ;
                """;
        System.out.println("\nTest 2: With semicolons");
        runScript(test2);

        // ???3????????
        String test3 = """
                var x = 1 ; var y = 2
                var z = x + y
                println(z)
                """;
        System.out.println("\nTest 3: Mixed (same line needs semicolon)");
        runScript(test3);

        // ???4????????????
        String test4 = """
                function multiply(a, b) {
                    return a * b
                }
                var result = multiply(6, 7)
                println(result)
                """;
        System.out.println("\nTest 4: Functions without semicolons");
        runScript(test4);

        System.out.println("\n=== Optional semicolon tests completed! ===");
    }

    @Test
    public void testFloatDebug() {
        System.out.println("=== Testing Float Parsing ===");

        String test = """
                var pi = 3.14159 ;
                println(pi) ;
                """;
        runScript(test);
    }

    @Test
    public void testSimpleFunction() {
        System.out.println("=== Testing Simple Function ===");

        String test = """
                var a = 5 ;
                var b = 3 ;
                function add(x, y){
                    return x + y ;
                }
                println(add(a, b)) ;
                """;
        runScript(test);
    }

    @Test
    public void testPhase1Fixes() throws IOException {
        System.out.println("=== Testing Phase 1 Fixes ===\n");

        // Test 1: ??????????- ????????
        System.out.println("Test 1: Function parameters with literals");
        String test1 = """
                function add(a, b){
                    return a + b ;
                }
                var x = 5 ;
                var y = 3 ;
                var result = add(x, y) ;
                println(result) ;
                """;
        runScript(test1);

        // Test 2: ??????
        System.out.println("\nTest 2: Arithmetic operations");
        String test2 = """
                var x = 10 - 5 ;
                println(x) ;
                var y = 4 * 5 ;
                println(y) ;
                var z = 20 / 4 ;
                println(z) ;
                """;
        runScript(test2);

        // Test 3: ?????
        System.out.println("\nTest 3: Float numbers");
        String test3 = """
                var f1 = 3.14 ;
                println(f1) ;
                var f2 = 10.5 + 2.3 ;
                println(f2) ;
                """;
        runScript(test3);

        // Test 4: ????????
        System.out.println("\nTest 4: String concatenation");
        String test4 = """
                var s1 = "Hello" + " " + "World" ;
                println(s1) ;
                """;
        runScript(test4);

        System.out.println("\n=== All Phase 1 tests completed! ===");
    }

    @Test
    public void testIfElse() {
        System.out.println("=== Testing if/else Statements ===\n");

        // Test 1: ??? if/else
        System.out.println("Test 1: Basic if/else");
        String test1 = """
                var score = 85
                if (score >= 90) {
                    println("Grade: A")
                } else {
                    println("Grade: Not A")
                }
                """;
        runScript(test1);

        // Test 2: if ????????? else??
        System.out.println("\nTest 2: if condition true (no else)");
        String test2 = """
                var x = 10
                if (x > 5) {
                    println("x is greater than 5")
                }
                """;
        runScript(test2);

        // Test 3: if ????????? else??
        System.out.println("\nTest 3: if condition false (no else)");
        String test3 = """
                var y = 3
                if (y > 5) {
                    println("This should not print")
                }
                println("After if statement")
                """;
        runScript(test3);

        // Test 4: ??? if/else
        System.out.println("\nTest 4: Nested if/else");
        String test4 = """
                var grade = 75
                if (grade >= 90) {
                    println("Excellent")
                } else {
                    if (grade >= 70) {
                        println("Fair")
                    } else {
                        println("Need improvement")
                    }
                }
                """;
        runScript(test4);

        // Test 5: ????????
        System.out.println("\nTest 5: Comparison operators");
        String test5 = """
                var a = 5
                var b = 10
                if (a < b) {
                    println("5 < 10: true")
                }
                if (a == 5) {
                    println("5 == 5: true")
                }
                if (b >= 10) {
                    println("10 >= 10: true")
                }
                """;
        runScript(test5);

        // Test 6: ????????
        System.out.println("\nTest 6: Boolean conditions");
        String test6 = """
                var isTrue = true
                var isFalse = false
                if (isTrue) {
                    println("isTrue is true")
                }
                if (isFalse) {
                    println("This should not print")
                } else {
                    println("isFalse is false")
                }
                """;
        runScript(test6);

        // Test 7: ????????if/else ??
        System.out.println("\nTest 7: Function calls in if/else");
        String test7 = """
                function isPositive(n) {
                    return n > 0
                }
                var num = 15
                if (isPositive(num)) {
                    println("15 is positive")
                } else {
                    println("15 is not positive")
                }
                """;
        runScript(test7);

        System.out.println("\n=== All if/else tests completed! ===");
    }

    @Test
    public void testWhileLoop() {
        System.out.println("=== Testing while Loop ===\n");

        // Test 1: ??? while ???
        System.out.println("Test 1: Basic while loop");
        String test1 = """
                var i = 0
                while (i < 5) {
                    println(i)
                    i = i + 1
                }
                """;
        runScript(test1);

        // Test 2: while ??????
        System.out.println("\nTest 2: Sum with while loop");
        String test2 = """
                var i = 1
                var sum = 0
                while (i <= 5) {
                    sum = sum + i
                    i = i + 1
                }
                println("Sum of 1 to 5: " + sum)
                """;
        runScript(test2);

        // Test 3: while ????????false????????
        System.out.println("\nTest 3: while condition false (no execution)");
        String test3 = """
                var x = 10
                while (x < 5) {
                    println("This should not print")
                }
                println("After while loop")
                """;
        runScript(test3);

        // Test 4: ??? while ???
        System.out.println("\nTest 4: Nested while loops");
        String test4 = """
                var x = 0
                while (x < 2) {
                    var y = 0
                    while (y < 2) {
                        println(x)
                        println(y)
                        y = y + 1
                    }
                    x = x + 1
                }
                """;
        runScript(test4);

        // Test 5: while ????????if ???
        System.out.println("\nTest 5: while with if condition");
        String test5 = """
                var i = 0
                while (i < 10) {
                    if (i == 3) {
                        println("Found 3!")
                    }
                    i = i + 1
                }
                """;
        runScript(test5);

        // Test 6: ??????
        System.out.println("\nTest 6: Countdown loop");
        String test6 = """
                var count = 5
                while (count > 0) {
                    println("Countdown: " + count)
                    count = count - 1
                }
                println("Liftoff!")
                """;
        runScript(test6);

        // Test 7: while ????????????
        System.out.println("\nTest 7: Function call in while loop");
        String test7 = """
                var i = 0
                while (i < 4) {
                    println(i)
                    i = i + 2
                }
                """;
        runScript(test7);

        System.out.println("\n=== All while loop tests completed! ===");
    }

    @Test
    public void testForLoop() {
        System.out.println("=== Testing for Loop ===\n");

        // Test 1: ??? for ???
        System.out.println("Test 1: Basic for loop");
        String test1 = """
                for (var i = 0; i < 5; i = i + 1) {
                    println(i)
                }
                """;
        runScript(test1);

        // Test 2: for ??????
        System.out.println("\nTest 2: Sum with for loop");
        String test2 = """
                var sum = 0
                for (var i = 1; i <= 5; i = i + 1) {
                    sum = sum + i
                }
                println("Sum of 1 to 5: " + sum)
                """;
        runScript(test2);

        // Test 3: for ????????var???????????????
        System.out.println("\nTest 3: for loop with existing variable");
        String test3 = """
                var j = 0
                for (j = 0; j < 3; j = j + 1) {
                    println(j)
                }
                """;
        runScript(test3);

        // Test 4: ??? for ???
        System.out.println("\nTest 4: Nested for loops");
        String test4 = """
                for (var x = 0; x < 2; x = x + 1) {
                    for (var y = 0; y < 2; y = y + 1) {
                        println(x)
                        println(y)
                    }
                }
                """;
        runScript(test4);

        // Test 5: for ????????if ???
        System.out.println("\nTest 5: for with if condition");
        String test5 = """
                for (var i = 0; i < 10; i = i + 1) {
                    if (i == 5) {
                        println("Found 5!")
                    }
                }
                """;
        runScript(test5);

        // Test 6: ??? for ???
        System.out.println("\nTest 6: Countdown for loop");
        String test6 = """
                for (var count = 5; count > 0; count = count - 1) {
                    println("Countdown: " + count)
                }
                println("Liftoff!")
                """;
        runScript(test6);

        // Test 7: for ????????2
        System.out.println("\nTest 7: for loop with step 2");
        String test7 = """
                for (var i = 0; i < 10; i = i + 2) {
                    println(i)
                }
                """;
        runScript(test7);

        // Test 8: for ??????????????? = ???????????MAX_ITERATIONS ?????
        System.out.println("\nTest 8: for loop without init");
        String test8 = """
                var k = 0
                for (; k < 3; k = k + 1) {
                    println(k)
                }
                """;
        runScript(test8);

        System.out.println("\n=== All for loop tests completed! ===");
    }

    private void runScript(String script) {
        try {
            CharStream stream = CharStreams.fromString(script);
            JimLangLexer lexer = new JimLangLexer(stream);
            JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
            ParseTree parseTree = parser.prog();
            JimLangVistor visitor = new JimLangVistor();
            visitor.visit(parseTree);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

        List<String> lines = Files.readAllLines(Paths.get("D:\\working\\mycode\\jimlang\\src\\test\\java\\Test02.mylang"));
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




    @Test
    public void testStdLibFastPath() {
        System.out.println("=== Testing StdLib (fast path) ===\n");
        String script = """
                var s = "Hello World"
                println(len(s))             
                println(toUpperCase(s))     
                println(substring(s, 0, 5)) 
                println(indexOf(s, "World")) 
                println(split("a,b,c", ","))
                println(trim("  hi  "))
                println(max(3, 9))
                println(min(3, 9))
                println(abs(0 - 5))
                println(round(3.7))
                println(random())
                var p = "target/tmp/phase3.txt"
                file_write(p, "Hello")
                println(file_exists(p))
                println(file_read(p))
                """;
        runScript(script);
    }
}

