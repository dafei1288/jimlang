# JimLang 瀵偓閸欐垼顓搁崚?- 韫囶偊鈧喓澧?

## 棣冩惓 妞ゅ湱娲伴悳鎵Ц

**瀹告彃鐤勯悳?* 閴?
- 閸╄櫣顢?REPL
- 閸欐﹢鍣洪崪灞藉毐閺佹澘鐣炬稊?
- 缁犫偓閸楁洝銆冩潏鎯х础鐠侊紕鐣?
- print/println 鏉堟挸鍤?

**鐎涙ê婀梻顕€顣?* 閳跨媴绗?
- 閸戣姤鏆熼崣鍌涙殶娴肩娀鈧帊绗夊銉ょ稊
- 娴ｆ粎鏁ら崺鐔侯吀閻炲棛宸辨径?
- 閸欘亝鏁幐浣规殻閺佹澘濮炲▔?
- REPL 閺冪姵纭舵穱婵囧瘮閻樿埖鈧?

## 棣冨箚 瀵偓閸欐垿妯佸▓?

### Phase 1: 閺嶇绺炬穱顔碱槻 (2-3閸? 棣冩暥 妤傛ü绱崗鍫㈤獓
**閻╊喗鐖?*: 鐠佲晞顕㈢懛鈧崺鐑樻拱閸旂喕鍏橀崣顖滄暏

```
Week 1:
閳?娣囶喖顦查崙鑺ユ殶閸欏倹鏆熸导鐘烩偓鎺撴簚閸?
  - 閸戣姤鏆熺拫鍐暏閺冨墎绮︾€规艾鐤勯崣鍌氬煂瑜般垹寮?
  - 濞村鐦? function add(a,b) { return a+b }

閳?鐎圭偟骞囩€瑰本鏆ｇ粻妤佹钩鏉╂劗鐣?
  - 閺€顖涘瘮: +, -, *, /, %
  - 閺€顖涘瘮濞搭喚鍋ｉ弫?
  - 鐎涙顑佹稉鍙夊閹?

Week 2:
閳?鐎圭偟骞囬崚鍡楃湴娴ｆ粎鏁ら崺?
  - 鐏炩偓闁劌褰夐柌?vs 閸忋劌鐪崣姗€鍣?
  - 閸欐﹢鍣洪弻銉﹀闁?

閳?閺€纭呯箻 REPL
  - 閻樿埖鈧焦瀵旀稊鍛
  - 閸涙垝鎶? :vars, :funcs, :help, :exit
  - 閺囨潙銈介惃鍕晩鐠囶垱褰佺粈?
```

**濞村鐦悽銊ょ伐**:
```jim
// 1. 閸戣姤鏆熼崣鍌涙殶
function add(a, b) { return a + b }
println(add(5, 3))  // 鎼存棁绶崙? 8

// 2. 鐎瑰本鏆ｆ潻鎰暬
var result = 10 * 2 + 5 / 2
println(result)  // 鎼存棁绶崙? 22

// 3. 娴ｆ粎鏁ら崺?
var x = 10
function test() {
  var x = 20
  println(x)  // 20
}
test()
println(x)    // 10
```

---

### Phase 2: 閹貉冨煑濞?(3-4閸? 棣冪厸 娑擃厺绱崗鍫㈤獓
**閻╊喗鐖?*: 濞ｈ濮炵粙瀣碍闁槒绶幒褍鍩?

```
閳?if/else 閺夆€叉鐠囶厼褰?
if (x > 0) {
  println("positive")
} else {
  println("negative")
}

閳?while 瀵邦亞骞?
var i = 0
while (i < 5) {
  println(i)
  i = i + 1
}

閳?for 瀵邦亞骞?
for (var i = 0; i < 5; i = i + 1) {
  println(i)
}

閳?閺佹壆绮?
var arr = [1, 2, 3]
println(arr[0])
arr.push(4)

閳?鐎电钖?
var obj = { name: "Jim", age: 25 }
println(obj.name)
```

---

### Phase 3: 閺嶅洤鍣惔?(2-3閸? 棣冪厸 娑擃厺绱崗鍫㈤獓
**閻╊喗鐖?*: 閹绘劒绶电敮鍝ユ暏閸戣姤鏆?

```
閳?鐎涙顑佹稉? length, toUpperCase, substring, split
閳?閺佹壆绮? map, filter, reduce
閳?閺佹澘顒? Math.max, Math.min, Math.random
閳?閺傚洣娆? File.read, File.write
```

- [ ] 鏇村鍐呯疆鍑芥暟锛坈ontains, replace, startsWith/endsWith, padLeft/padRight, repeat, pow, sqrt, floor, ceil, randomRange, file_append锛?

### Phase 4: 妤傛楠囬悧瑙勨偓?(4-5閸? 棣冪厺 娴ｅ簼绱崗鍫㈤獓
```
閳?闂傤厼瀵?
閳?Lambda: (a, b) => a + b
閳?闁挎瑨顕ゆ径鍕倞: try/catch
閳?濡€虫健缁崵绮? import/export
```

---

## 棣冩畬 缁斿宓嗗鈧慨?(閺堫剙鎳嗘禒璇插)

### 娴犺濮?1: 娣囶喖顦查崙鑺ユ殶閸欏倹鏆?(閺堚偓闁插秷顩?)

**閺傚洣娆?*: `JimLangVistor.java:237-266`

**瑜版挸澧犻梻顕€顣?*:
```java
// visitFunctionCall 娑擃厼褰ч弰顖濆箯閸欐牕鍤遍弫鏉跨暰娑斿绱濇担鍡樼梾閺堝绮︾€规艾寮弫?
SymbolFunction currentSymbol = (SymbolFunction) _sympoltable.get(functionName);
return currentSymbol.getValue();  // 閴?閸欏倹鏆熺悮顐㈡嫹閻ｃ儰绨?
```

**闂団偓鐟曚礁浠?*:
1. 閼惧嘲褰囩€圭偛寮崐鐓庡灙鐞?
2. 閼惧嘲褰囪ぐ銏犲棘閸氬秶袨閸掓銆?
3. 閸掓稑缂撻弬鎵畱鐏炩偓闁劋缍旈悽銊ョ厵
4. 鐏忓棗鐤勯崣鍌滅拨鐎规艾鍩岃ぐ銏犲棘閸?
5. 閸︺劏顕氭担婊呮暏閸╃喍鑵戦幍褑顢戦崙鑺ユ殶娴?
6. 鏉╂柨娲栫紒鎾寸亯

---

### 娴犺濮?2: 鐎瑰本鏆ｇ粻妤佹钩鏉╂劗鐣?

**閺傚洣娆?*: `JimLangVistor.java:168-219`

**瑜版挸澧犻梻顕€顣?*:
```java
if("+".equals(op)){
    return (Integer)leftObject + (Integer)rightObject;  // 閴?閸欘亜鐤勯悳棰佺啊閸旂姵纭?
}
```

**闂団偓鐟曚礁浠?*:
```java
switch(op) {
    case "+": return add(left, right);
    case "-": return subtract(left, right);
    case "*": return multiply(left, right);
    case "/": return divide(left, right);
    case "%": return mod(left, right);
    // ... 濮ｆ棁绶濇潻鎰暬缁?
}
```

閺€顖涘瘮缁鐎?
- Integer + Integer
- Double + Double
- String + anything (閹峰吋甯?

---

### 娴犺濮?3: REPL 閻樿埖鈧焦瀵旀稊鍛

**閺傚洣娆?*: `Repl.java:23-39`

**瑜版挸澧犻梻顕€顣?*:
```java
while (true) {
    // 閴?濮ｅ繑顐煎顏嗗箚闁棄鍨卞鐑樻煀閻?visitor閿涘瞼濮搁幀浣锋丢婢?
    JimLangVistor mlvistor = new JimLangVistor();
    Object o = mlvistor.visit(parseTree);
}
```

**闂団偓鐟曚焦鏁奸幋?*:
```java
// 閴?閸︺劌鎯婇悳顖氼樆閸掓稑缂?visitor
JimLangVistor mlvistor = new JimLangVistor();

while (true) {
    String line = reader.readLine();

    // 閻楄鐣╅崨鎴掓姢婢跺嫮鎮?
    if (line.startsWith(":")) {
        handleCommand(line, mlvistor);
        continue;
    }

    // 鐟欙絾鐎介獮鑸靛⒔鐞涘矉绱欐穱婵囧瘮閻樿埖鈧緤绱?
    Object o = mlvistor.visit(parseTree);
}
```

---

## 棣冩憫 瀵偓閸欐垶顥呴弻銉︾閸?

濮ｅ繋閲滈弬鏉垮閼宠棄绻€妞よ瀵橀崥?
- [ ] ANTLR 鐠囶厽纭剁€规矮绠熼弴瀛樻煀
- [ ] Visitor 鐎圭偟骞?
- [ ] 閸楁洖鍘撳ù瀣槸
- [ ] REPL 濞村鐦?
- [ ] 閺傚洦銆傞弴瀛樻煀

## 棣冃?濞村鐦弬瑙勭《

```bash
# 鏉╂劘顢戦幍鈧張澶嬬ゴ鐠?
mvn test

# 鏉╂劘顢?REPL 閹靛濮╁ù瀣槸
mvn exec:java -Dexec.mainClass="com.dafei1288.jimlang.Repl"
```

## 棣冩憥 閹恒劏宕橀梼鍛邦嚢

**韫囧懓顕?*:
- 閵嗗rafting Interpreters閵?- 閺堚偓闁倸鎮庢潻娆庨嚋妞ゅ湱娲?
  - 閸忓秷鍨傞崷銊у殠: https://craftinginterpreters.com/

**閸欏倽鈧?*:
- ANTLR 4 閺夊啫鈻夐幐鍥у础
- 缂傛牞鐦ч崢鐔烘倞閿涘牓绶虫稊锔肩礆

## 棣冾檪 婵″倷缍嶇拹锛勫盀

1. 娴?Phase 1 閻ㄥ嫪鎹㈤崝鈥崇磻婵?
2. 閸掓稑缂?feature branch: `git checkout -b feature/fix-function-params`
3. 鐎圭偟骞?+ 濞村鐦?
4. 閹绘劒姘?PR

## 閳存唻绗?閺冨爼妫挎导鎵暬

```
Phase 1 (閺嶇绺炬穱顔碱槻):    2-3 閸? 閳?瑜版挸澧犳稉鎾存暈
Phase 2 (閹貉冨煑濞?:      3-4 閸?
Phase 3 (閺嶅洤鍣惔?:      2-3 閸?
Phase 4 (妤傛楠囬悧瑙勨偓?:    4-5 閸?
閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓閳光偓
閹槒顓?                 11-15 閸?
```

---

**娑撳绔村?*: 瀵偓婵?Phase 1 閻ㄥ嫪绗佹稉顏呯壋韫囧啩鎹㈤崝? 棣冩畬


## Milestones
- 2025-11-28: Phase 2 瀹屾垚锛堟帶鍒舵祦 + break/continue + 閾惧紡浜屽厓杩愮畻 + 鍩虹鏁扮粍/瀵硅薄锛?鈫?閲岀▼纰?v0.3.0
