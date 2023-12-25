# What is JimLang

JimLang是基于JVM的具有完善语言系统的编程语言，其主旨是帮助大家入门语言开发领域。

# 如何使用

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

或者使用 jsr-233 方式

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



# 参与开发

## 系统要求
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## 代码编译

`mvn clean package -DskipTest=true`


# Roadmap