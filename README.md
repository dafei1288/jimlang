# What is JimLang

Jim Lang is a programming language based on JVM with a comprehensive language system, aimed at helping everyone get started in the field of language development.

# Useage

```
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
```

Or use jsr-233 

    @Test
    public void test01() throws ScriptException {
        String script = """
                function two() { return 2 } ;
                print( two() ) ;
                """;

        System.out.println(script);
        System.out.println("--------------------");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("jim");
        engine.eval(script);
    }


# Devlpment

## Prerequisites
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## Build from source

`mvn clean package -DskipTest=true`


# Roadmap