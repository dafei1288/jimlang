# JimLang 鍛戒护琛屽伐鍏蜂娇鐢ㄦ寚鍗?

## 姒傝堪

JimLang 鐜板湪鏀寔鍛戒护琛屽伐鍏凤紝鍙互鐩存帴鎵ц鑴氭湰鏂囦欢锛?

## 瀹夎鍜屾瀯寤?

### 1. 鏋勫缓椤圭洰

棣栧厛锛岄渶瑕佹瀯寤洪」鐩敓鎴愬彲鎵ц JAR锛?

```bash
cd D:\working\mycode\jimlang
mvn clean package
```

杩欎細鐢熸垚锛?
- `target/jimlang-1.0-SNAPSHOT.jar` - 涓?JAR
- `target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar` - 鍖呭惈鎵€鏈変緷璧栫殑鍙墽琛?JAR

### 2. 鍚姩鑴氭湰浣嶇疆

- **Windows**: `bin/jimlang.cmd`
- **Unix/Linux/Mac**: `bin/jimlang.sh`

## 浣跨敤鏂瑰紡

### 鍩烘湰鍛戒护

```bash
# Windows
bin\jimlang.cmd <script.jim>

# Unix/Linux/Mac
bin/jimlang.sh <script.jim>
```

### 鍛戒护閫夐」

```bash
# 鏌ョ湅鐗堟湰淇℃伅
bin\jimlang.cmd --version
bin\jimlang.cmd -v

# 鏌ョ湅甯姪淇℃伅
bin\jimlang.cmd --help
bin\jimlang.cmd -h

# 鎵ц鑴氭湰鏂囦欢
bin\jimlang.cmd mycode.jim
```

## 绀轰緥鑴氭湰

### 绀轰緥 1: 绠€鍗曟祴璇?(`examples/test.jim`)

```jim
var x = 10
var y = 20
println("Sum:")
println(x + y)

if (x < y) {
    println("x is less than y")
}

for (var i = 0; i < 5; i = i + 1) {
    println(i)
}
```

鎵ц锛?
```bash
bin\jimlang.cmd examples\test.jim
```

杈撳嚭锛?
```
Sum:
30
x is less than y
0
1
2
3
4
```

### 绀轰緥 2: 鏂愭尝閭ｅ鏁板垪 (`examples/fibonacci.jim`)

```jim
function fibonacci(n) {
    var a = 0
    var b = 1
    for (var i = 0; i < n; i = i + 1) {
        var temp = a
        a = b
        b = temp + b
    }
    return a
}

println("Fibonacci sequence:")
for (var i = 0; i < 10; i = i + 1) {
    var result = fibonacci(i)
    println(result)
}
```

鎵ц锛?
```bash
bin\jimlang.cmd examples\fibonacci.jim
```

## 娣诲姞鍒扮郴缁?PATH

### Windows

鍙互灏?`bin` 鐩綍娣诲姞鍒扮郴缁?PATH锛岃繖鏍峰彲浠ュ湪浠讳綍浣嶇疆鐩存帴杩愯锛?

1. 鎵撳紑"绯荤粺灞炴€? 鈫?"鐜鍙橀噺"
2. 鍦?鐢ㄦ埛鍙橀噺"鎴?绯荤粺鍙橀噺"涓壘鍒?`Path`
3. 娣诲姞锛歚D:\working\mycode\jimlang\bin`
4. 閲嶆柊鎵撳紑鍛戒护琛?

鐒跺悗鍙互鐩存帴浣跨敤锛?
```bash
C:\> jimlang mycode.jim
```

### Unix/Linux/Mac

鍦?`~/.bashrc` 鎴?`~/.zshrc` 涓坊鍔狅細

```bash
export PATH="$PATH:/path/to/jimlang/bin"
```

鐒跺悗杩愯锛?
```bash
source ~/.bashrc
jimlang.sh mycode.jim
```

## 鏂囦欢缁撴瀯

```
jimlang/
鈹溾攢鈹€ bin/
鈹?  鈹溾攢鈹€ jimlang.cmd          # Windows 鍚姩鑴氭湰
鈹?  鈹斺攢鈹€ jimlang.sh           # Unix 鍚姩鑴氭湰
鈹溾攢鈹€ examples/
鈹?  鈹溾攢鈹€ test.jim             # 绠€鍗曟祴璇曡剼鏈?
鈹?  鈹斺攢鈹€ fibonacci.jim        # 鏂愭尝閭ｅ鏁板垪绀轰緥
鈹溾攢鈹€ src/
鈹?  鈹斺攢鈹€ main/java/.../Main.java  # 鍛戒护琛屽叆鍙?
鈹溾攢鈹€ target/
鈹?  鈹斺攢鈹€ jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar  # 鍙墽琛?JAR
鈹斺攢鈹€ pom.xml                  # Maven 閰嶇疆
```

## 閿欒澶勭悊

### JAR 鏂囦欢鏈壘鍒?

濡傛灉鐪嬪埌閿欒锛?
```
Error: JimLang JAR file not found!
```

瑙ｅ喅鏂规锛?
```bash
cd D:\working\mycode\jimlang
mvn clean package
```

### Java 鏈畨瑁?

濡傛灉鐪嬪埌閿欒锛?
```
Error: Java is not installed or not in PATH
```

瑙ｅ喅鏂规锛?
1. 瀹夎 Java 21 鎴栨洿楂樼増鏈?
2. 纭繚 `java` 鍛戒护鍦?PATH 涓?

楠岃瘉 Java 瀹夎锛?
```bash
java -version
```

### 鑴氭湰鎵ц閿欒

濡傛灉鑴氭湰鎵ц鍑洪敊锛屾鏌ワ細
1. 鏂囦欢璺緞鏄惁姝ｇ‘
2. 鏂囦欢鏄惁瀛樺湪
3. 璇硶鏄惁姝ｇ‘锛堝弬鑰?`QUICKREF.md`锛?

## JimLang 璇硶蹇€熷弬鑰?

```jim
// 鍙橀噺
var x = 10
var name = "JimLang"

// 鍑芥暟
function add(a, b) {
    return a + b
}

// if/else
if (x > 5) {
    println("Greater")
} else {
    println("Less or equal")
}

// while 寰幆
var i = 0
while (i < 5) {
    println(i)
    i = i + 1
}

// for 寰幆
for (var j = 0; j < 10; j = j + 1) {
    println(j)
}

// 绯荤粺鍑芥暟
println("Hello")  // 杈撳嚭骞舵崲琛?
print("Hi")       // 杈撳嚭涓嶆崲琛?
```

## 鎶€鏈粏鑺?

- **璇█**: JimLang 1.0-SNAPSHOT (Phase 2 Complete)
- **Java 鐗堟湰**: 21
- **瑙ｆ瀽鍣?*: ANTLR 4.13.1
- **鏋勫缓宸ュ叿**: Maven 3.x
- **JAR 澶у皬**: ~8 MB (鍖呭惈鎵€鏈変緷璧?

## 宸茬煡闄愬埗

1. **閾惧紡琛ㄨ揪寮?*: 涓嶆敮鎸?`a + b + c`锛岄渶瑕佸垎瑙ｄ负澶氫釜璇彞
2. **瀛楃涓叉嫾鎺?*: 鏀寔鍗曟鎷兼帴 `"Hello" + name`
3. **break/continue**: 鏆備笉鏀寔锛圥hase 2 鍙€夊姛鑳斤級
4. **鏁扮粍**: 鏆備笉鏀寔
5. **瀵硅薄**: 鏆備笉鏀寔

## 涓嬩竴姝?

- **Phase 2 鍙€夊姛鑳?*: break/continue, 鏁扮粍, 瀵硅薄
- **Phase 3**: 鏍囧噯搴撳嚱鏁帮紙瀛楃涓层€佹暟瀛︺€佹枃浠?I/O锛?
- **Phase 4**: 绫诲拰瀵硅薄銆佹ā鍧楃郴缁熴€佸紓姝ユ敮鎸?

## 鐩稿叧鏂囨。

- `QUICKREF.md` - 璇硶蹇€熷弬鑰?
- `PHASE2_COMPLETED.md` - Phase 2 瀹屾垚鎶ュ憡
- `STACK_MECHANISM_EXPLAINED.md` - 鏍堟満鍒惰瑙?
- `README_ZH.md` - 椤圭洰璇存槑

## 鍙嶉鍜岃础鐚?

濡傛灉鍙戠幇闂鎴栨湁寤鸿锛屾杩庯細
- 鎻愪氦 Issue
- 鍒涘缓 Pull Request
- 鑱旂郴浣滆€咃細dafei1288@sina.com

---

**鐗堟湰**: 1.0-SNAPSHOT
**瀹屾垚鏃堕棿**: 2025-11-27
**鐘舵€?*: 鉁?Phase 2 瀹屾垚 + 鍛戒护琛屽伐鍏?

## CLI extras (new)

- Start REPL: `jimlang --cli` or `jimlang -i`
- Eval one-liner: `jimlang --eval "println(\"hi\")"`
- Read from STDIN:
  - Windows: `type code.jim | jimlang -`
  - Unix: `cat code.jim | jimlang -`