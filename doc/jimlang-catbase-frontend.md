# 给 JimLang 造第二个前端：CatBase.g4 语法与直接求值解释器

> 作者：JimLang 项目
> 关键词：ANTLR4 / CatBase / 第二前端 / 语法驱动 / 解释器
> 目标读者：普通开发者（想看懂“用 ANTLR 给一门语言加方言”是怎么做的）

---

## 1. 缘起：文本转译器到了天花板

在第一篇文章里，我们让 JimLang 能跑 CatBase 程序，做法是 **`CatBaseCompat` 文本转译**：把 `.cat` 源码当作字符串，用“token 区间替换”改写成 `.jim` 再交给现有解释器。它对 Tier-1（函数、类型化变量、`for-in-range`、`if/while`、dict/JSON）工作得很好。

但越往下做，越能感到文本转译的“天花板”：

| 想支持的特性 | 文本转译要付出的代价 |
|---|---|
| f-string `f"value={x}"` | 需要自己再写一套“字符串内表达式解析器”，否则只能要求表达式最简单 |
| 关键字实参 `f(a=1, b=2)` / 默认值 | 要重写调用点、维护形参名到默认值的映射 |
| `cfg["k"] = v` 写入 dict | JimLang 语法层本就不支持，文本层无解，只能“绕” |
| `s.strip().lower()` 方法链 | 方法调用本质是语义，不是字符问题 |
| `else if`、负步长 `range` | 需要“大括号手术”级别的结构性改写 |

**根因**：文本转译只认“字形”，不认“语法结构”。特性一多，规则之间互相打架，补丁摞补丁。

所以就有了第二篇文章要讲的方案 B：**给 CatBase 单独写一份真正的语法（ANTLR4 grammar：`CatBase.g4`），再写一个直接在这棵语法树上求值的解释器（`CatBaseVistor`）**。这让 JimLang 第一次拥有“第二语言前端”，而不是“第二种源码改写方式”。

---

## 2. 架构定位：三件套

```
                    ┌────────────────────────────────────────────┐
 .cat 源码 ───────▶ │ CatBase.g4        (ANTLR4 语法)           │
                    │   └─▶ CatBaseParser（词法+语法解析）        │
                    │ CatBaseVistor     (直接求值解释器)          │
                    │   └─ 值模型: Long/Double/String/Boolean/    │
                    │      List/Map/null                          │
                    │   └─ 内置函数 ──▶ Funcall（与 JimLang 共用） │
                    └────────────────────────────────────────────┘
                                    │
                    顺带保留：CatBaseCompat（cat2jim 离线工具）
```

新增文件：
- `src/main/antlr4/.../catbase/CatBase.g4` —— CatBase 语法（词法 + 语法规则）
- `src/main/java/.../catbase/CatBaseVistor.java` —— 求值器
- `src/main/java/.../catbase/CatBaseRunner.java` —— 入口（解析 + 求值 + 错误包装）

运行路径：`Main` 检测到 `.cat` 扩展名 → `CatBaseRunner` → 直接解释，不再经过 `.jim` 文本。

---

## 3. 语法设计要点（CatBase.g4）

### 3.1 别再制造“隐式关键字”

第一篇文章踩过一个经典的 ANTLR 坑：parser 规则里写了 `'int' | 'string' ...` 字面量，ANTLR 会为它们生成隐式 token，导致 `int`、`string` 这类词在**任何位置**都不能再当普通标识符——`int(x)` 直接语法错误。

`CatBase.g4` 从一开始就避开：**关键字显式声明，类型名一律走 `ID`**。

```antlr
DEF : 'def';  IF : 'if';  FOR : 'for';  IN : 'in';  ...
// 没有 INT/STR 关键字！
ID : [a-zA-Z_][a-zA-Z0-9_]*;
```

类型注解只是“建议”，语法上它就是一个普通标识符 + 可选的泛型括号：

```antlr
typeName : ID ( '[' typeName (',' typeName)* ']' )? ;
```

于是 `x: int = 1` 与 `int("42")` 互不干扰：前者 `int` 出现在冒号后由 `typeName` 消费，后者 `int` 是普通函数名。

### 3.2 换行不敏感，块即结构

CatBase 没有 Python 的缩进负担，代码块由 `{}` 界定。因此词法上**换行直接丢弃**（HIDDEN），语句之间天然不需要分号；多行 list/dict 字面量、跨行条件都是“本来就能解析”，不用像 Python 那样为续行写规则。

```antlr
statement : variableDecl | functionDef | ifStatement | forStatement
          | whileStatement | returnStatement | breakStatement
          | continueStatement | assignmentStatement | expressionStatement
          | block ;
block     : '{' statementList? '}' ;
```

代价：同一行放两条无分号语句会合并（CatBase 代码几乎不这么写，可接受并记录为边界）。

### 3.3 else-if 是语法，不是补丁

`ifStatement` 用递归表达 `else if` 链，求值器不需要做任何“大括号手术”：

```antlr
ifStatement : IF expression block (ELSE (ifStatement | block))? ;
```

### 3.4 f-string 在词法层收下、在求值层展开

f-string 的问题是内容里混着表达式，词法很难干净切分。方案：lexer 把 `f"..."` **整体**收成一个 `FSTRING` token（引号内的 `{}` 都只是字符），由求值器在运行期做两件事：

1. 扫描正文，按 `{...}` 的括号配对切出“字面段 + 表达式段”；
2. 每个表达式段**用同一套 parser 现场再解析**（`parser.expression()` + 复用当前求值器的符号表），算出值后拼接。

这保证了 `f"a={a} sum={x + y}"` 里的表达式拥有与普通代码完全相同的语法与求值语义。

```antlr
FSTRING : 'f' '"' FStringBody '"' | 'f' '\'' FStringBody '\'' ;
fragment FStringBody : ( ~["\\\r\n] | EscapeSeq )* ;
```

---

## 4. 求值器设计要点（CatBaseVistor）

解释器结构与 `JimLangVistor` 同构，但只为自己这份语法服务：

**作用域模型**
- 全局 `globals` + 函数调用时压栈的局部 `Hashtable`；
- CatBase 风格：函数体内部不按块开新作用域，循环变量按“名字是否已存在”决定是**更新**还是**新建**——`for x in range(...)` 的 x 若先前声明过就赋值，否则首次迭代时在函数作用域创建；
- `break/continue/return` 用控制流异常实现（`BreakSignal/ContinueSignal/ReturnSignal`），由最近的循环/函数调用捕获——和 JimLang 的做法完全一致。

**函数**
- `def` 只登记 `FuncDef{参数名, 默认值(声明时求值), 函数体}`，调用时才执行函数体（无闭包/一等函数，CatBase 不需要）；
- 实参绑定支持：**位置实参 / 关键字实参 / 默认值**混用，绑定时逐形参匹配，缺参且无默认值时报错，多出未消费实参时报错；
- `main(args: list[str])` 作为入口，顶层代码执行完后自动调用一次 `main([])`。

**容器与对象**
- dict 用 `LinkedHashMap`，list 用 `ArrayList`，**读写都原生支持**：`d["k"] = v`、`d["k"]`、`m["x"][0]` 链式下标、`d.key` 属性读；
- 字符串/列表方法链 `s.strip().lower()`：语法上识别 `值 . 名字 ( 实参 )` 模式，把方法名映射到 `Funcall` 的内置函数（`strip→trim`、`upper→toUpperCase`…），参数 = 接收者 + 实参，**内置函数库与 JimLang 完全共用**。

**内置函数复用**
- `print(a, b, c)` → 拼接打印（映射到 `cat_print`）；
- `int()/float()/str()/bool()` → `Funcall` 的别名表（上一轮已加）；
- `json_dumps/json_loads` → `json_encode/json_decode`；
- `len` 已多态；`range(a,b,step)` 作为原生内建返回 list（支持负步长、1~3 参数）；
- `abs/max/min/pow/round/...` 等直接落到 `Funcall`。

**数值语义**
- 整数一律 `Long`、小数 `Double`；`int / int` 走**整除**（与 CatBase 一致），`7.0 / 2` 得 `3.5`；比较运算符对数值按 double 比较，字符串按字典序；`+` 遇字符串做拼接；支持一元负号。

---

## 5. 与“文本转译”（方案 A）的分工

| 维度 | CatBaseCompat（文本转译） | CatBase.g4 前端（本方案） |
|---|---|---|
| 认识源码的方式 | token 流 + 区间替换 | 真实语法树 |
| f-string / 关键字实参 / 默认值 | ❌ 不支持 | ✅ 原生 |
| dict 写入 / 方法链 | ❌ / 绕路 | ✅ 原生 |
| else-if / 负步长 range | 大括号手术 / 重命名 | 语法或内建原生 |
| 产物 | 一份可读 `.jim` | 无中间文本，直接执行 |
| 定位 | 保留为离线 `cat2jim` 工具 | **`.cat` 默认运行器** |

两个实现共享同一条“内置函数复用”之路，价值模型、字符串转义语义一致，因此切换运行器后四个 Tier-1 demo 的输出一字不差。

---

## 6. 已验证结果

**Tier-1 demo（examples/catbase/ 四个文件，全部通过）**
```
hello.cat            → Hello world!
typed_functions.cat  → total=123 / n=123 / f=123.0 / b=true / pi=3.14
control_flow.cat     → fruit[0]=apple … count=3 … big … done count=0
dict_json.cat        → {"model":"jim-1",…} / len(messages)=2
```

**新增 Tier-2 能力（examples/catbase/features_frontend.cat）**
```
hi jim x1                     ← 默认参数
hi cat x3                     ← 关键字实参
a=7 a*2=14 neg=-7             ← f-string 内联表达式 + 一元负号
fib(10)=55                    ← 递归
d=6 / d=4 / d=2               ← range 负步长
7/2=3  7.0/2=3.5              ← 整数除法语义
cfg.name=box n=1 ok=true      ← dict 下标写入
hello.catbase | HELLO.CATBASE ← 字符串方法链
range ok / logic ok           ← and / or / not
total2=6                      ← for-in 遍历 list 字面量
```

全量单测：既有 70 项（64 旧 + 6 兼容层）+ 本前端新增用例，全部通过；`.jim` 原有行为零回归。

---

## 7. 边界与预期（下一步）

**已支持**：核心语言全部控制流、函数（默认值/关键字实参/递归）、f-string、list/dict（读写）、字符串方法、类型转换、json、range；`#` 注释、字符串转义。

**尚未支持（清晰的演进路线）**
1. `class/struct + self`：语法可加 `classDef`，语义上需要“带字段的实例 + 方法查找 + self 绑定”。求值器的作用域/调用机制已就位，这是最大的一块；
2. `import / from … import`：模块加载与去重；
3. CatBase 并发与硬件 I/O：`thread`、`Queue`、`mutex`、TCP/UDP/HTTP/WebSocket、串口、音频、`Pointer`/FFI——多数可写成调用 `Funcall`/Java 能力的桥接，真正缺口是 JVM 侧语义对应；
4. 两处小的语法取舍：同一行多语句需 `;` 分隔；f-string 插值内的字符串字面量暂不支持（词法层会在引号处提前结束）。

**预期结果**：语法驱动的前端成为 `.cat` 的默认运行器；每次新增 CatBase 特性 = 在 `CatBase.g4` 加规则 + 在 `CatBaseVistor` 加一个 visit 方法 + 一条单测，不再有“改一处坏三处”的文本补丁问题。

---

## 8. 复现

```bash
mvn -q -DskipTests package
bin/jimlang.cmd examples/catbase/features_frontend.cat   # Windows
bin/jimlang.sh    examples/catbase/features_frontend.cat # Unix

# 旧的 cat2jim 工具仍然可用
java -cp target/classes com.dafei1288.jimlang.catbase.CatBaseCompat a.cat out.jim
```

---

## 9. 小结

- **为什么再来一个前端**：文本转译擅长“改写字形”，不擅长“表达语义”；f-string、关键字实参、dict 写入、方法链这类特性一进来，就必须有真正的语法树。
- **怎么做的**：`CatBase.g4` 提供干净语法（不造隐式关键字、换行无关、`else if` 递归、f-string 整体收词），`CatBaseVistor` 在树上直接求值，值模型与内置函数 100% 复用 JimLang 的 `Funcall`。
- **效果**：Tier-1 行为与旧方案完全一致，Tier-2 的能力（插值、命名参数、容器写操作、方法链、负步长 range）开箱即用；解释器只有 ~700 行，因为“语法归语法、语义归语义、内建归 Funcall”。

给一门语言加新方言的正确姿势，不是写更多的字符串替换，而是给它一张真正的语法表，再让求值器照着表走路。
