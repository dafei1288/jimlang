# 让 JimLang 运行 CatBase 程序：兼容层设计与实现

> 作者：JimLang 项目
> 关键词：JimLang / CatBase / 跨语言 / 源码翻译 / 解释器
> 目标读者：普通开发者（了解一门脚本语言如何“吃掉”另一门语言的语法）

---

## 1. 问题：为什么想用 JimLang 跑 CatBase？

[CatBase](https://github.com/DoRobotAi/catbase-lang) 是一门语法类似 Python 的静态类型语言，专为“极小运行环境”设计：源码被编译成原生二进制（后端是 Zig），号称可以在小体积环境里替代 C。它的语法清爽、文档齐全，社区里积累了不少 `.cat` 示例程序。

但 CatBase 的官方编译器是**闭源的**（EULA 禁止逆向、禁止修改和再分发），而且只提供 Linux 二进制。这就带来几个现实约束：

- 你没法把它的编译后端“搬”到 JVM 上复用；
- 你在 Windows / 没有 Zig 的环境里想跑 `.cat` 程序，基本没戏；
- 你希望把 CatBase 写好的逻辑集成进 Java 应用、脚本流水线，也很难。

JimLang 则相反：它是纯 Java 实现的解释器，天然跨平台、可嵌入（JSR-223 / `JimLangShell`），语法是 JavaScript 风格的动态类型脚本语言。

于是问题来了：**能不能让 JimLang 直接“运行 CatBase 程序”？**

结论是：**可以，但不是“白嫖” CatBase 的实现，而是把 JimLang 变成 CatBase 源码的第二个解释器**。做法是加一层“翻译器”（transpiler），在 JimLang 解析之前把 `.cat` 语法无损地改写成 JimLang 语法——类型被剥离，结构被等价替换，最终交给 JimLang 自己的解释器执行。本文记录它的开发原理、设计目的、实现方式、预期结果与边界。

---

## 2. 为什么可行：两门语言骨子里很像

翻译器能做，前提是两门语言在**运行时模型**上足够接近。对比一下：

| 维度 | CatBase | JimLang |
|---|---|---|
| 运行模型 | 编译成 Zig → 原生二进制 | Java 上的 AST 解释器 |
| 值类型 | int / float / str / bool / list / dict / any | Integer / Double / String / Boolean / `List` / `Map` / null |
| 代码块 | `{}` 大括号 | `{}` 大括号 |
| 变量赋值 | `x: int = 1` | `var x = 1` |
| 注释 | `# ...` | `// ...` |
| 循环 | `for i in range(a, b)` / `while cond` | C 风格 `for` / `while` |
| 打印 | `print(a, b, c)`（可变参） | `print/println(obj)`（单参） |
| dict 字面量 | `{"key": value}`（字符串键） | 原本只支持 `key: value`（标识符键） |
| 入口 | `def main(args: list[str])` | 顶层语句（无 main 语义） |

关键洞察：

1. **静态类型只影响编译期。** CatBase 强类型是为了编译期检查和生成高效原生代码；对解释器来说，类型注解完全可以剥掉，运行时全部走动态值即可。
2. **两者的“形状”同构。** 函数、块、表达式、容器都是同样的树结构，差异主要在“写法”（词法/语法层面），而不是“语义”。所以做一层**语法翻译**（而不是重新实现语义）就能覆盖大部分代码。
3. **翻译是单向、无状态的。** 翻译器不做任何求值，只是把一种语法糖换成另一种语法糖；翻译不过去的语法会原样留下，JimLang 解析时报错——这让我们可以诚实地划一条“支持边界”。

既然两门语言的差异几乎全在语法面，那么“语法层面重写 + 少量运行时别名”的架构，成本远低于“写第二个解释器”。

---

## 3. 方案对比：为什么选“转译 + 引擎内兼容模式”

实现“让 JimLang 跑 CatBase”共有 4 条候选路线：

| 方案 | 原理 | 优点 | 缺点 | 结论 |
|---|---|---|---|---|
| A. 源码转译器 | `.cat` → `.jim` 文本改写，进现有解析器 | 改动小、见效快、可增量 | 强类型/特殊语法需要逐条翻译 | ✅ 首选 |
| B. 第二套前端 | 新写 CatBase.g4 + 第二个 AST visitor | 保真度高、可支持 class | 成本高、维护双 parser | 演进方向 |
| C. 纯文本预处理 | 只做字符串级替换，不改语法 | 1 小时出 demo | 被语法硬限制卡死 | ❌ 玩具 |
| D. 进程桥接 | `catbasecc` 编译出原生程序，Java 调子进程 | 零成本 | 不是“让 JimLang 执行逻辑”；依赖闭源 Linux 工具链 | ❌ 跑题 |

最终选了 A，但落点不在“离线转换工具”，而在 **“引擎内兼容模式”**：CLI 遇到 `.cat` 文件自动走翻译管道，对使用者来说就是 `jimlang xxx.cat` 直接运行，与运行 `.jim` 无差别。这样未来想支持 `.jim` 上不同的方言、或在 `JimLangShell` 里嵌入“按语言自动识别”，都只需复用同一入口。

同时，为了让翻译结果能真正跑起来，还需要动三处**底座**（见第 4 节）。它们对 JimLang 自身也是净收益，不是只为 CatBase 服务的补丁。

---

## 4. 实现方式

整体管道：

```
xxx.cat ──读取──▶ CatBaseCompat.translate() ──▶ xxx.jim 语法文本 ──▶ ANTLR 解析 ──▶ JimLangVistor 执行
                        │
                        ├─ 词法切分（注释/字符串/标识符/操作符）
                        ├─ 结构化改写（def/变量声明/for-in-range/if-while/else-if 链）
                        ├─ 小词替换（and→&&、True→true、print→cat_print…）
                        ├─ 字符串转义实体化（"\n" → 真实换行）
                        └─ 自动注入 main([])
```

新增的类：`com.dafei1288.jimlang.catbase.CatBaseCompat`（纯文本翻译器，约 600 行，无第三方依赖）。

### 4.1 底座改动一：语法放权（JimLang.g4）

原始语法里，类型注解用**字面量**书写：

```antlr
typeName : 'string' | 'number' | 'boolean' | 'int' | 'float' | 'array' | 'object' | qualifiedName;
```

ANTLR 会为这些字面量生成“隐式关键字 token”，导致 `int`、`float` 这类词**在任何位置都无法再当普通标识符用**——实测 `var n = int(s)` 直接语法报错。而 CatBase 恰恰把 `int()/str()/float()/bool()` 当内置函数用。

既然 JimLang 的运行时并不需要类型注解，就让类型名回归普通标识符：

```antlr
typeName : qualifiedName;   // 注释说明：类型注解仅为建议，不再保留字
```

同时放开 object 字面量，允许**字符串键**（CatBase 的 dict 全是 `{"key": v}`）：

```antlr
pair : (identifier | STRING_LITERAL) COLON expression;
```

配套改动解释器 `JimLangVistor.visitObjectLiteral()`：键可能是标识符也可能是字符串字面量，取文本时做分支处理。这给 JimLang 自己也带来好处——以后直接写 `{ "a": 1 }` 再也不用先 JSON 解析了。

### 4.2 底座改动二：内置函数扩容（Funcall.java）

`Funcall` 是 JimLang 的“内置函数注册表”，按 **方法名 + 参数个数** 反射派发。这里补了三件事：

1. **别名表**：`int → to_int`、`float → to_float`、`str → to_str`、`bool → to_bool`。
   Java 方法不能叫 `int`（关键字），而源码里的调用名又必须保持 `int(...)`，所以在派发入口加一张 `CATBASE_ALIAS` 映射即可。
2. **类型转换 + CatBase 语义的 `cat_print`**：`cat_print(a, b, c)` 把参数**无分隔符拼接**输出且**不追加换行**（CatBase 里换行由调用者显式传 `"\n"`）。
3. **`len` 多态化**：原来 `len` 只算字符串长度，现在 `String → length`、`List/Map → size`——CatBase 的 `len(fruits)` 是数列表元素。

顺带修了一个反射派发的隐患：可变参数方法（`Object...`）不应被“精确 argc”分支抢走，否则 `cat_print("a")` 这种单参调用会以错误的方式反射调用。

### 4.3 翻译器核心规则

翻译器先做**词法切分**（保留每个 token 的行号与字符偏移），再在 token 流上做“区间替换”，一次扫过结构化规则、二次扫过小词替换——因为不改动原文的空白与注释结构，翻译结果天然可读、diff 友好。

核心规则如下（规则名即作用）：

| 规则 | CatBase 源码 | 翻译结果 |
|---|---|---|
| defRewrite | `def add(a: int, b: int) -> int {` | `function add(a, b) {`（剥类型/默认值/返回注解） |
| varDecl | `x: int = 0` | `var x = 0`（含全局变量） |
| varDecl | `msg: list[dict[str, str]] = [` | `var msg = [`（跨行容器原样保留） |
| forRewrite | `for i in range(0, len(fruits)) {` | `for (var __cb0 = 0; __cb0 < len(fruits); __cb0 = __cb0 + 1) {`，且循环体内所有 `i` 重命名为 `__cb0` |
| parenWrap | `if x > 5 {` / `while c {` | `if (x > 5) {` / `while (c) {` |
| elseIfChain | `} else if (n < 0) { ... } else {` | `} else { if (n < 0) { ... } else {`（补配大括号） |
| 词替换 | `and / or / True / False` | `&& / \|\| / true / false` |
| print | `print("total=", total, "\n")` | `cat_print("total=", total, "\n")` |
| json | `json_dumps(x)` / `json_loads(s)` | `json_encode(x)` / `json_decode(s)` |
| 注释 | `# 注释` | 删除（保留换行，行号不错位） |
| 字符串 | `"a\nb"` | `'''a<换行>b'''`（转义在翻译期实体化） |
| main | `def main(args: list[str]) { ... }` | 文件末尾自动追加 `main([])` |

几个值得展开的设计点：

- **`for x in range(a, b)` 的翻译**（forRewrite）：CatBase 的 range 循环是“把值喂给变量”，JimLang 是 C 风格计数循环。做法是**始终引入一个全新计数器**，并把循环体内对 `x` 的所有引用重命名成计数器——这样无论 `x` 之前是否声明过都能正确工作，翻译器不需要维护复杂的“变量是否已声明”作用域信息。步长为负时自动把比较符换成 `>`、增量写成 `(0 - n)`，规避 JimLang 无一元负号的限制。
- **字符串转义实体化**：JimLang 的字符串字面量不做 `\n` 之类的转义处理（字面就是反斜杠 + n），所以翻译器遇到带转义的 CatBase 字符串，会在**翻译期**把 `\n`/`\t` 等解成真实字符，再按内容选择 `"..."` 或 `'''...'''` 定界输出。
- **`else if` 链**：JimLang 语法不支持 `else if`，只支持 `else { ... }`。翻译器把整条链重写为嵌套块：`if A {} else if B {} else {}` → `if A {} else { if B {} else {} }`（在第一个 `else` 后插 `{`，在链尾补等量 `}`）。
- **不改语义**：翻译器从不求值。它只做“语法同构改写”，翻译不过的特性保持原样——随后 JimLang 会给出正常的语法/运行时错误，错误行号与 `.cat` 源文件一致。

### 4.4 入口接线

`Main.executeScript()` 里按扩展名路由：

```java
boolean catMode = scriptPath.toLowerCase().endsWith(".cat");
if (catMode) {
    script = CatBaseCompat.translate(script);
}
```

于是 `bin/jimlang examples/hello.cat` 就能直接跑 CatBase 程序。

---

## 5. 已验证结果

兼容层落地后，我们写了 4 个覆盖 Tier-1 语法的 CatBase 演示程序（`examples/catbase/`），全部真实运行通过：

| 文件 | 覆盖点 | 实测输出（节选） |
|---|---|---|
| `hello.cat` | `def main(args:list[str])`、`print`、`#` 注释、`\n` 转义 | `Hello world!` |
| `typed_functions.cat` | 全局类型变量、带返回注解函数、`int()/float()/str()/bool()` | `total=123` `n=123` `f=123.0` `b=true` `pi=3.14` |
| `control_flow.cat` | `for ... in range`（隐式/预声明变量）、`len(list)`、`if/else`、`while` | `fruit[0]=apple … count=3 … big … done count=0` |
| `dict_json.cat` | 字符串键 dict、嵌套 list[dict]、`json_dumps`、下标访问 | `{"model":"jim-1",…}` `len(messages)=2` |

另外的回归与边界验证：

- **负步长 range**：`for d in range(4, 0, -1)` 正确输出 4,3,2,1；
- **else-if 链 + 行尾注释**：`classify()` 三段分支 + 各种 `# inline comment` 全部通过；
- **对原 JimLang 无回归**：`examples/fibonacci.jim` 等原样通过；`.jim` 里现在也能用 `int("42")`、`{"a": 1}`、`len([1,2,3])`，属于语法放权带来的额外红利。

---

## 6. 边界与预期：支持什么、不支持什么

这套机制按“支持层级”诚实划分（翻译器对未支持的语法不做特殊处理，会得到 JimLang 自身的清晰报错）：

**✅ Tier 1（当前可用）**
- 函数（含类型参数、返回注解、默认值剥离）、全局/局部强类型变量
- 全部控制流：`if/else if/else`、`while`、`for x in range(a, b[, step])`（含负步长）、`break/continue/return`
- list / dict（字符串键、嵌套、下标与属性访问）、多行容器字面量
- 字符串（含 `\n` 等转义）、`#` 注释
- 内置函数：`print`、`len`、`int/float/str/bool`、`abs/max/min/pow/round` 等数学函数、`json_dumps/json_loads`
- 顶层代码 + `def main` 自动入口

**⏳ Tier 2（演进方向）**
- f-string（`f"value={x}"`）——需要表达式插值语法
- `class/struct` + `self`、关键字实参/默认实参、`import`
- CatBase 特有 I/O：`thread`、`Queue`、`mutex`、TCP/UDP/HTTP/WebSocket、串口、音频、`Pointer`/FFI、handle 式文件 API（`file()/read()/write()/close()`）
  - 注意：其中一部分（HTTP 服务端、文件、JSON、DB）JimLang 已有对等能力，接成“别名/桥接”即可，真正的缺口是并发原语与硬件类 API
- 一元负号、`+=` 复合赋值、取模/幂等运算符在 JimLang 语法的缺失（翻译器以 `0 - x` 等方式规避了常见场景）

**预期结果**（按“性价比”排序）
1. 近期：覆盖 CatBase 示例集里的“纯算法/数据处理”类程序，达到开箱即跑；
2. 中期：把 `.cat` 支持做成引擎级“语言模式”（`--lang cat` / 扩展名自动识别 / `JimLangShell` 嵌入），并补齐 f-string 与常用内置别名；
3. 远期：若社区确实需要 class/线程，再考虑 Tier 2 的“第二前端”方案（CatBase.g4 + 独立 visitor），与本文的翻译层共享 `Funcall` 内置函数体系。

---

## 7. 复现与体验

```bash
# 构建（Java 21+ / Maven 3.8+）
mvn -q -DskipTests package

# 直接运行 CatBase 源文件（扩展名 .cat 自动进入兼容模式）
bin/jimlang.cmd examples/catbase/hello.cat          # Windows
bin/jimlang.sh    examples/catbase/hello.cat        # Unix

# 翻译器也可独立使用（产出一个合法 .jim 文件，方便你检查改了什么）
java -cp target/classes com.dafei1288.jimlang.catbase.CatBaseCompat ...
```

想自己加规则：在 `CatBaseCompat.java` 里新增一个 `xxxRewrite(...)`，按“token 区间 + 生成 `Edit`”的套路实现，几十行即可接入现有管道；每个规则都有独立的可测输入（见 `examples/catbase/` 与文中的 torture 样例）。

---

## 8. 小结

- **原理**：CatBase 与 JimLang 运行时模型同构，差异集中在语法层；类型系统在解释器侧可整体剥离。
- **设计目的**：让 JimLang 成为 CatBase 源码的第二个可运行实现，绕开闭源、Linux-only 的官方工具链，同时把语法红利反哺给 JimLang 自己。
- **实现方式**：引擎内兼容模式 = 语法放权（字符串键、类型关键字去保留化）+ 内置函数扩容（别名/多态 `len`/`cat_print`）+ 纯文本结构翻译器（def / var / for-range / if-while / else-if 链 / 转义 / 注释 / main 注入）。
- **预期结果**：Tier-1（核心语言 + 常见标准库）可开箱即用；未覆盖特性保留清晰的 JimLang 报错；Tier-2 按需以“第二前端”演进。

一门语言能“读懂”另一门语言，靠的不是复制它的编译器，而是找到两者运行时模型之间的那座语法桥。
