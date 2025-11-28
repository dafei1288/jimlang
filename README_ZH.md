# What is JimLang

JimLang鏄熀浜嶫VM鐨勫叿鏈夊畬鍠勮瑷€绯荤粺鐨勭紪绋嬭瑷€锛屽叾涓绘棬鏄府鍔╁ぇ瀹跺叆闂ㄨ瑷€寮€鍙戦鍩熴€?

# 濡備綍浣跨敤

娣诲姞snapshots浠撳簱
```xml
<repositories>
      <repository>
        <id>jim</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      </repository>
</repositories>
```

寮曞叆jdbc渚濊禆
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

鎴栬€呬娇鐢?jsr-233 鏂瑰紡

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

# 鍙備笌寮€鍙?

## 绯荤粺瑕佹眰
1. Java >= 21
2. Maven >= 3.8 (If you want to compile and install IoTDB from source code).

## 浠ｇ爜缂栬瘧

`mvn clean package -DskipTest=true`


# 杩愯 REPL

缂栬瘧椤圭洰鍚庯紝鍙互鍚姩浜や簰寮?REPL锛?

```bash
mvn clean package
java -cp target/jimlang-1.0-SNAPSHOT.jar com.dafei1288.jimlang.Repl
```

鎴栬€呬粠 IDE 涓繍琛?`Repl.java` 鐨?main 鏂规硶銆?

REPL 绀轰緥:
```
JimLang>
var x = 10
JimLang>
var y = 20
JimLang>
println(x + y)
30
JimLang>
function greet(name) { println("Hello, " + name) }
JimLang>
greet("World")
Hello, World
JimLang>
exit()
good bye!
```

# 璇█鐗规€?

## 褰撳墠鏀寔
- 鉁?鍙橀噺澹版槑锛歚var x = 10`
- 鉁?绫诲瀷娉ㄨВ锛歚var name: string = "jim"`
- 鉁?鍑芥暟瀹氫箟锛歚function add(a, b) { return a + b }`
- 鉁?鍩烘湰杩愮畻锛歚+, -, *, /, <, >, ==` 绛?
- 鉁?鍐呯疆鍑芥暟锛歚print()`, `println()`
- 鉁?娉ㄩ噴锛歚//` 鍗曡, `/* */` 澶氳

## 寮€鍙戜腑
- 馃毀 瀹屾暣鐨勪綔鐢ㄥ煙绠＄悊
- 馃毀 鍑芥暟鍙傛暟浼犻€?
- 馃毀 鎺у埗娴侊細if/else, while, for
- 馃毀 鏁版嵁缁撴瀯锛氭暟缁勩€佸璞?
- 馃毀 鏇村鏍囧噯搴撳嚱鏁?

璇﹁ [寮€鍙戣矾绾垮浘](ROADMAP.md) 鍜?[蹇€熻鍒抅(ROADMAP_QUICK.md)

# 椤圭洰鏂囨。

- 馃摉 [README (English)](README.md) - 鑻辨枃璇存槑
- 馃摉 [README_ZH (涓枃)](README_ZH.md) - 鏈枃妗?
- 馃敡 [寮€鍙戠幆澧冭缃甝(DEVELOPMENT.md) - 寮€鍙戣€呮寚鍗?
- 馃搵 [蹇€熷弬鑰僝(QUICKREF.md) - 甯哥敤鍛戒护鍜岀ず渚?
- 馃椇锔?[寮€鍙戣矾绾垮浘 (璇︾粏)](ROADMAP.md) - 瀹屾暣寮€鍙戣鍒?
- 馃椇锔?[寮€鍙戣矾绾垮浘 (蹇€?](ROADMAP_QUICK.md) - 绮剧畝鐗堣鍒?

# 濡備綍璐＄尞

鎴戜滑娆㈣繋鍚勭褰㈠紡鐨勮础鐚紒

1. **鎶ュ憡闂**: 鍙戠幇 bug 鎴栨湁寤鸿锛熻鍒涘缓 Issue
2. **鎻愪氦浠ｇ爜**:
   - Fork 鏈」鐩?
   - 鍒涘缓鐗规€у垎鏀? `git checkout -b feature/my-feature`
   - 鎻愪氦鏇存敼: `git commit -am 'Add my feature'`
   - 鎺ㄩ€佸垎鏀? `git push origin feature/my-feature`
   - 鍒涘缓 Pull Request
3. **瀹屽杽鏂囨。**: 鏀硅繘鏂囨。鍜岀ず渚?
4. **鍒嗕韩缁忛獙**: 鍐欐枃绔犮€佸仛婕旇锛屽垎浜綘浣跨敤 JimLang 鐨勭粡楠?

璇﹁ [ROADMAP_QUICK.md](ROADMAP_QUICK.md) 涓殑杩戞湡浠诲姟鍒楄〃銆?

# 瀛︿範璧勬簮

濡傛灉浣犲璇█寮€鍙戞劅鍏磋叮锛屾帹鑽愪互涓嬭祫婧愶細

- 馃摎 **銆奀rafting Interpreters銆?* - Robert Nystrom (寮虹儓鎺ㄨ崘!)
  - 鍏嶈垂鍦ㄧ嚎闃呰: https://craftinginterpreters.com/
  - 浠庨浂寮€濮嬪疄鐜颁竴闂ㄧ紪绋嬭瑷€
- 馃摎 **銆婄紪绋嬭瑷€瀹炵幇妯″紡銆?* - Terence Parr (ANTLR 浣滆€?
- 馃摎 **銆婄紪璇戝師鐞嗐€?* (榫欎功) - 缁忓吀鏁欐潗
- 馃敆 **ANTLR 瀹樻柟鏂囨。**: https://www.antlr.org/

# 璺嚎鍥?# TODO 鍒楄〃

- 鏇村鍐呯疆鍑芥暟锛坈ontains, replace, startsWith/endsWith, padLeft/padRight, repeat, pow, sqrt, floor, ceil, randomRange, file_append锛?- 鐐硅皟鐢?鍛藉悕绌洪棿璇硶锛堝 s.length(), Math.max(), File.read()锛?- 鏁扮粍涓庡璞＄被鍨嬶紙浣?split 绛夎繑鍥炵粨鏋勫寲鏁版嵁锛?

# 鏍囧噯搴擄紙Fast Path锛?
鏈樁娈垫彁渚涗竴鎵瑰彲鐩存帴璋冪敤鐨勫叏灞€鍐呯疆鍑芥暟锛堝悗缁皢鏀寔鐐硅皟鐢?鍛藉悕绌洪棿锛夛細

- 瀛楃涓?  - `len(s)`锛宍toUpperCase(s)`/`upper(s)`锛宍toLowerCase(s)`/`lower(s)`锛宍trim(s)`
  - `substring(s, start[, end])`锛宍indexOf(s, sub)`锛宍split(s, sep)`
- 鏁板
  - `max(a, b)`锛宍min(a, b)`锛宍abs(x)`锛宍round(x)`锛宍random()`
- 鏂囦欢
  - `file_read(path)`锛宍file_write(path, content)`锛宍file_exists(path)`

绀轰緥锛?```jim
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
```

鏇村鍊欓€夎 ROADMAP Phase 3 - 3.5 鑺傦紙TODO锛夈€?

## 新增示例

### 控制流：break / continue
```jim
for (var i = 0; i < 5; i = i + 1) {
  if (i == 2) { continue }
  if (i == 4) { break }
  println(i)
}
```

### 数组 / 对象
```jim
var arr = [1, 2, 3]
println(arr[0])
arr[1] = 10
println(arr.length)

var person = { name: "Jim", age: 25 }
println(person.name)
person.age = 26
println(person.age)

var nested = { a: { b: 2 } }
println(nested.a.b)
```