# JimLang 寮€鍙戣鍒?- 蹇€熺増

## 馃搳 椤圭洰鐜扮姸

**宸插疄鐜?* 鉁?
- 鍩虹 REPL
- 鍙橀噺鍜屽嚱鏁板畾涔?
- 绠€鍗曡〃杈惧紡璁＄畻
- print/println 杈撳嚭

**瀛樺湪闂** 鈿狅笍
- 鍑芥暟鍙傛暟浼犻€掍笉宸ヤ綔
- 浣滅敤鍩熺鐞嗙己澶?
- 鍙敮鎸佹暣鏁板姞娉?
- REPL 鏃犳硶淇濇寔鐘舵€?

## 馃幆 寮€鍙戦樁娈?

### Phase 1: 鏍稿績淇 (2-3鍛? 馃敶 楂樹紭鍏堢骇
**鐩爣**: 璁╄瑷€鍩烘湰鍔熻兘鍙敤

```
Week 1:
鈻?淇鍑芥暟鍙傛暟浼犻€掓満鍒?
  - 鍑芥暟璋冪敤鏃剁粦瀹氬疄鍙傚埌褰㈠弬
  - 娴嬭瘯: function add(a,b) { return a+b }

鈻?瀹炵幇瀹屾暣绠楁湳杩愮畻
  - 鏀寔: +, -, *, /, %
  - 鏀寔娴偣鏁?
  - 瀛楃涓叉嫾鎺?

Week 2:
鈻?瀹炵幇鍒嗗眰浣滅敤鍩?
  - 灞€閮ㄥ彉閲?vs 鍏ㄥ眬鍙橀噺
  - 鍙橀噺鏌ユ壘閾?

鈻?鏀硅繘 REPL
  - 鐘舵€佹寔涔呭寲
  - 鍛戒护: :vars, :funcs, :help, :exit
  - 鏇村ソ鐨勯敊璇彁绀?
```

**娴嬭瘯鐢ㄤ緥**:
```jim
// 1. 鍑芥暟鍙傛暟
function add(a, b) { return a + b }
println(add(5, 3))  // 搴旇緭鍑? 8

// 2. 瀹屾暣杩愮畻
var result = 10 * 2 + 5 / 2
println(result)  // 搴旇緭鍑? 22

// 3. 浣滅敤鍩?
var x = 10
function test() {
  var x = 20
  println(x)  // 20
}
test()
println(x)    // 10
```

---

### Phase 2: 鎺у埗娴?(3-4鍛? 馃煛 涓紭鍏堢骇
**鐩爣**: 娣诲姞绋嬪簭閫昏緫鎺у埗

```
鈻?if/else 鏉′欢璇彞
if (x > 0) {
  println("positive")
} else {
  println("negative")
}

鈻?while 寰幆
var i = 0
while (i < 5) {
  println(i)
  i = i + 1
}

鈻?for 寰幆
for (var i = 0; i < 5; i = i + 1) {
  println(i)
}

鈻?鏁扮粍
var arr = [1, 2, 3]
println(arr[0])
arr.push(4)

鈻?瀵硅薄
var obj = { name: "Jim", age: 25 }
println(obj.name)
```

---

### Phase 3: 鏍囧噯搴?(2-3鍛? 馃煛 涓紭鍏堢骇
**鐩爣**: 鎻愪緵甯哥敤鍑芥暟

```
鈻?瀛楃涓? length, toUpperCase, substring, split
鈻?鏁扮粍: map, filter, reduce
鈻?鏁板: Math.max, Math.min, Math.random
鈻?鏂囦欢: File.read, File.write
```

- [ ] 更多内置函数（contains, replace, startsWith/endsWith, padLeft/padRight, repeat, pow, sqrt, floor, ceil, randomRange, file_append）

### Phase 4: 楂樼骇鐗规€?(4-5鍛? 馃煝 浣庝紭鍏堢骇
```
鈻?闂寘
鈻?Lambda: (a, b) => a + b
鈻?閿欒澶勭悊: try/catch
鈻?妯″潡绯荤粺: import/export
```

---

## 馃殌 绔嬪嵆寮€濮?(鏈懆浠诲姟)

### 浠诲姟 1: 淇鍑芥暟鍙傛暟 (鏈€閲嶈!)

**鏂囦欢**: `JimLangVistor.java:237-266`

**褰撳墠闂**:
```java
// visitFunctionCall 涓彧鏄幏鍙栧嚱鏁板畾涔夛紝浣嗘病鏈夌粦瀹氬弬鏁?
SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);
return currentSymbol.getValue();  // 鉂?鍙傛暟琚拷鐣ヤ簡
```

**闇€瑕佸仛**:
1. 鑾峰彇瀹炲弬鍊煎垪琛?
2. 鑾峰彇褰㈠弬鍚嶇О鍒楄〃
3. 鍒涘缓鏂扮殑灞€閮ㄤ綔鐢ㄥ煙
4. 灏嗗疄鍙傜粦瀹氬埌褰㈠弬鍚?
5. 鍦ㄨ浣滅敤鍩熶腑鎵ц鍑芥暟浣?
6. 杩斿洖缁撴灉

---

### 浠诲姟 2: 瀹屾暣绠楁湳杩愮畻

**鏂囦欢**: `JimLangVistor.java:168-219`

**褰撳墠闂**:
```java
if("+".equals(op)){
    return (Integer)leftObject + (Integer)rightObject;  // 鉂?鍙疄鐜颁簡鍔犳硶
}
```

**闇€瑕佸仛**:
```java
switch(op) {
    case "+": return add(left, right);
    case "-": return subtract(left, right);
    case "*": return multiply(left, right);
    case "/": return divide(left, right);
    case "%": return mod(left, right);
    // ... 姣旇緝杩愮畻绗?
}
```

鏀寔绫诲瀷:
- Integer + Integer
- Double + Double
- String + anything (鎷兼帴)

---

### 浠诲姟 3: REPL 鐘舵€佹寔涔呭寲

**鏂囦欢**: `Repl.java:23-39`

**褰撳墠闂**:
```java
while (true) {
    // 鉂?姣忔寰幆閮藉垱寤烘柊鐨?visitor锛岀姸鎬佷涪澶?
    JimLangVistor mlvistor = new JimLangVistor();
    Object o = mlvistor.visit(parseTree);
}
```

**闇€瑕佹敼鎴?*:
```java
// 鉁?鍦ㄥ惊鐜鍒涘缓 visitor
JimLangVistor mlvistor = new JimLangVistor();

while (true) {
    String line = reader.readLine();

    // 鐗规畩鍛戒护澶勭悊
    if (line.startsWith(":")) {
        handleCommand(line, mlvistor);
        continue;
    }

    // 瑙ｆ瀽骞舵墽琛岋紙淇濇寔鐘舵€侊級
    Object o = mlvistor.visit(parseTree);
}
```

---

## 馃摑 寮€鍙戞鏌ユ竻鍗?

姣忎釜鏂板姛鑳藉繀椤诲寘鍚?
- [ ] ANTLR 璇硶瀹氫箟鏇存柊
- [ ] Visitor 瀹炵幇
- [ ] 鍗曞厓娴嬭瘯
- [ ] REPL 娴嬭瘯
- [ ] 鏂囨。鏇存柊

## 馃И 娴嬭瘯鏂规硶

```bash
# 杩愯鎵€鏈夋祴璇?
mvn test

# 杩愯 REPL 鎵嬪姩娴嬭瘯
mvn exec:java -Dexec.mainClass="com.dafei1288.jimlang.Repl"
```

## 馃摎 鎺ㄨ崘闃呰

**蹇呰**:
- 銆奀rafting Interpreters銆?- 鏈€閫傚悎杩欎釜椤圭洰!
  - 鍏嶈垂鍦ㄧ嚎: https://craftinginterpreters.com/

**鍙傝€?*:
- ANTLR 4 鏉冨▉鎸囧崡
- 缂栬瘧鍘熺悊锛堥緳涔︼級

## 馃 濡備綍璐＄尞

1. 浠?Phase 1 鐨勪换鍔″紑濮?
2. 鍒涘缓 feature branch: `git checkout -b feature/fix-function-params`
3. 瀹炵幇 + 娴嬭瘯
4. 鎻愪氦 PR

## 鈴憋笍 鏃堕棿浼扮畻

```
Phase 1 (鏍稿績淇):    2-3 鍛? 鈫?褰撳墠涓撴敞
Phase 2 (鎺у埗娴?:      3-4 鍛?
Phase 3 (鏍囧噯搴?:      2-3 鍛?
Phase 4 (楂樼骇鐗规€?:    4-5 鍛?
鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€
鎬昏:                 11-15 鍛?
```

---

**涓嬩竴姝?*: 寮€濮?Phase 1 鐨勪笁涓牳蹇冧换鍔? 馃殌

