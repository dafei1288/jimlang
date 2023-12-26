# What is JimLang

JimLang是基于JVM的具有完善语言系统的编程语言，其主旨是帮助大家入门语言开发领域。

# 如何使用

添加snapshots仓库
```xml
<repositories>
      <repository>
        <id>jim</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      </repository>
</repositories>
```

引入jdbc依赖
```xml
<dependency>
    <groupId>com.dafei1288</groupId>
    <artifactId>jimlang</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

```
    @Test
    public void T3() throws IOException{

        String script = """
                function two() { return 2 ; } ;
                function one() { return 1 ; } ;
                var x = one() + two() ; 
                println("this message is from jimlang!!!")
                println( x ) ;
                """;

        System.out.println(script);
        System.out.println("--------------------");
        JimLangShell shell = new JimLangShell();
        Object ret = shell.eval(script,null);
    }
```

或者使用 jsr-233 方式

```
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
```

# 参与开发

## 系统要求
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## 代码编译

`mvn clean package -DskipTest=true`


# Roadmap