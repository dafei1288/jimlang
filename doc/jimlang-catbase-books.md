# 用 CatBase 写一个图书管理系统

> 作者：JimLang 项目
> 目标读者：普通开发者（想快速上手 CatBase，做一个小而完整的项目）
> 系列第 3 篇：前两篇讲“怎么让 JimLang 跑 CatBase”，这篇直接用 CatBase 写一个真实小程序，
> 顺便展示：同一套代码的核心子集，现在也能用 JimLang 直接解释运行。

---

## 1. 为什么是“图书管理系统”？

CRUD（增删查改）+ 持久化 + 简单的交互菜单，几乎覆盖一门语言的“标配知识点”：

- 数据结构：一本书长什么样 → `dict`（或 `class`）
- 容器操作：一个书库 → `list`，增删、遍历、统计
- 控制流：菜单循环、按条件分支、`break` 退出
- 字符串与转换：`int()`/`str()`、关键词匹配
- 持久化：写文件、读文件、JSON 序列化/反序列化
- 模块化：把功能拆成一个个 `def` 函数

CatBase 特别适合拿来做这个练习：它语法像 Python 却用大括号，**强类型**让你被迫把数据结构想清楚，内置 JSON/文件 API 让持久化只需几行。

如果你只想要代码：完整版在 `examples/catbase/library/library_full.cat`，子集演示版在 `examples/catbase/library_demo.cat`。

---

## 2. CatBase 速写（写代码前先记这几点）

1. **强类型、大小写敏感**：`x: int = 1`、`name: str = "三体"`、容器 `list[str]` / `dict[str, any]`；类型注解是声明，不是注释。
2. **用 `{}` 而不是缩进**：`if x > 5 { ... } else { ... }`。
3. **dict 的字面量键要加引号**：`{"title": "三体"}`，像 JSON 一样。
4. **函数**：`def add(a: int, b: int) -> int { return a + b }`；`def main(args: list[str])` 是程序入口。
5. **内置利器**：`print(a, b)`（可变参拼接）、`len()`、`range(a, b, step)`、`int()/str()/float()/bool()`、`json_dumps()/json_loads()`、`file()` 文件句柄、`f"...{x}..."` 字符串插值。
6. **错误处理**：`try { ... } except { ... }`（也有 `except e` 拿到错误对象）。

> 更多差异（对照 Python）见文末“避坑清单”。

---

## 3. 系统设计与数据模型

功能清单（经典“菜单式”控制台程序）：

| 编号 | 功能 | 说明 |
|---|---|---|
| 0 | 保存并退出 | 把书库写成 `books.json` |
| 1 | 添加图书 | 书名/作者/出版年/馆藏册数，自动编号 |
| 2 | 图书列表 | 表格形式列出全部，含“可借 = 馆藏 − 借出” |
| 3 | 关键词查询 | 按书名或作者模糊匹配 |
| 4 | 借书 | 有可借副本才成功，借出 +1 |
| 5 | 还书 | 借出 −1 |
| 6 | 删除图书 | 按编号删除 |
| 7 | 统计 | 种类数、总册数、借出数、可借数 |

数据模型采用 CatBase 世界里最“顺手”的形状——**dict + JSON**（官方自己的图数据管理器就是这么写的）：

```catbase
# 一本书
book = {
    "id": 1,
    "title": "三体",
    "author": "刘慈欣",
    "year": 2008,
    "copies": 3,     # 馆藏册数
    "borrowed": 1    # 当前借出数
}

# 整个书库
store = {
    "next_id": 2,        # 下一个可用编号
    "books": [book, ...]
}
```

所有改动都发生在内存里的 `store`，需要落盘时 `json_dumps(store)` 写文件，启动时读文件 `json_loads` 回来——天然可存档、可迁移、可查看。

---

## 4. 逐块实现讲解

### 4.1 常量与“工厂”

```catbase
DATA_FILE: str = "books.json"

def make_store() -> dict[str, any] {
    return {"next_id": 1, "books": []}
}
```

`dict[str, any]` 表示“键是字符串、值是任意类型”的字典——图书对象里混着 `int` 和 `str`，用 `any` 最省心。

### 4.2 读档 / 存档（File + JSON）

```catbase
def load_store(path: str) -> dict[str, any] {
    content: str = ""
    raw: dict[str, any] = {}
    f: File = file(path, "r")
    content = f.read()
    f.close()
    if content == "" {
        return make_store()
    }
    raw = json_loads(content)
    try { _t: any = raw["books"] } except { raw["books"] = [] }
    try { _t2: any = raw["next_id"] } except { raw["next_id"] = 1 }
    return raw
}

def save_store(st: dict[str, any], path: str) {
    text: str = json_dumps(st)
    fw: File = file(path, "w")
    fw.write(text)
    fw.close()
    print("已保存到 ", path, "\n")
}
```

要点：
- `file(path, "r"/"w")` 返回 `File` 句柄，用 `f.read()` / `f.write(...)` / `f.close()`；
- `json_loads(content)` 把整段 JSON 还原成 `dict[str, any]`；
- **`try { ... } except { ... }` 在这里当“存在性探测”**：老存档缺 `books` 或 `next_id` 字段时补默认值，文件不存在时 `main` 里会兜底。

### 4.3 添加与列表

```catbase
def cmd_add(st: dict[str, any], title: str, author: str, year: int, copies: int) -> int {
    nid: int = st["next_id"]
    book: dict[str, any] = {}
    book["id"] = nid
    book["title"] = title
    book["author"] = author
    book["year"] = year
    book["copies"] = copies
    book["borrowed"] = 0
    st["books"].append(book)      # list.append 直接可用
    st["next_id"] = nid + 1
    return nid
}

def cmd_list(st: dict[str, any]) {
    books: list[any] = st["books"]
    for i in range(0, len(books)) {
        b: dict[str, any] = books[i]
        avail: int = int(str(b.get("copies", 0))) - int(str(b.get("borrowed", 0)))
        print(b.get("id", 0), "|", b.get("title", ""), "...", avail, "\n")
    }
}
```

要点：
- `st["books"].append(book)`：从 `dict[str,any]` 里取出的 list 仍可原地追加；
- 从 JSON 回来的值都是“弱类型”，官方惯用法是 **`int(str(b.get("copies", 0)))`** 这样的显式转换；`dict.get(key, 默认值)` 避免取不存在的键。

### 4.4 借书 / 还书 / 删除（都是“先找索引，再改字段”）

```catbase
def find_index(st: dict[str, any], nid: int) -> int {
    books: list[any] = st["books"]
    for i in range(0, len(books)) {
        b: dict[str, any] = books[i]
        if int(str(b.get("id", 0))) == nid {
            return i
        }
    }
    return -1
}

def cmd_borrow(st: dict[str, any], nid: int) {
    idx: int = find_index(st, nid)
    if idx < 0 { print("没有编号 ", nid, " 的图书\n"); return }
    b: dict[str, any] = st["books"][idx]
    borrowed: int = int(str(b.get("borrowed", 0)))
    copies: int = int(str(b.get("copies", 0)))
    if borrowed >= copies { print("无可借副本\n"); return }
    b["borrowed"] = borrowed + 1
    print("借出成功：", b.get("title", ""), "\n")
}
```

还书是 `borrowed - 1`（不小于 0），删除用 `pop(st["books"], idx)`，模式完全一致。

### 4.5 模糊查询（用切片实现 contains）

CatBase 没有内置 `str.contains`（官方示例里见过字符串切片 `s[i:j]`），于是我们顺手写一个：

```catbase
def str_contains(s: str, kw: str) -> bool {
    n: int = len(s); k: int = len(kw)
    if k == 0 { return true }
    if k > n { return false }
    for i in range(0, n - k + 1) {
        sub: str = s[i:i + k]
        if sub == kw { return true }
    }
    return false
}
```

### 4.6 主菜单

```catbase
def main(args: list[str]) {
    st: dict[str, any] = {}
    try {
        st = load_store(DATA_FILE)
        print("已加载存档（", len(st["books"]), " 种图书）\n")
    } except {
        st = make_store()
    }
    while true {
        show_menu()
        opt: int = int(input("请选择功能："))
        if opt == 0 { save_store(st, DATA_FILE); print("再见！\n"); break }
        if opt == 1 { ... cmd_add(...) }
        else { if opt == 2 { cmd_list(st) } ... }
    }
}
```

要点：`input("提示语")` 读一行字符串，`int(...)` 转数字；`while true { ... break }` 做菜单循环。

> 如果希望优雅地“面向对象”，CatBase 同样支持 `class`。官方示例的写法是：字段在类里声明类型，`__init__(self, ...)` 显式接收 `self`，普通方法不用写 `self` 形参但体内可用 `self`：

```catbase
class Book {
    id: int
    title: str
    author: str
    copies: int
    borrowed: int

    def __init__(self, id: int, title: str, author: str, copies: int) {
        self.id = id
        self.title = title
        self.author = author
        self.copies = copies
        self.borrowed = 0
    }
    def available() -> int {
        return self.copies - self.borrowed
    }
}
# b1: Book = Book(1, "三体", "刘慈欣", 3)
# b2: Book = Book { id: 2, title: "...", ... }   # 结构体字面量写法
```

完整版为了贴合“JSON 一键存档”，统一用 dict 模型；把 `class Book` 融进去（加 `to_dict()`）是很好的课后练习。

---

## 5. 完整源码与运行

- 完整版（官方 `catbasecc` 编译运行，含 File/input/class 之外的全部特性）：
  **`examples/catbase/library/library_full.cat`**（~270 行）

```bash
./bin/catbasecc examples/catbase/library/library_full.cat
./library_full                       # 按菜单交互，退出时生成 books.json
```

一次典型的控制台会话（预期效果）：

```
已加载存档（ 0 种图书）
================ 图书管理系统 ================
 1. 添加图书    2. 图书列表    3. 按关键词查询
 4. 借书        5. 还书        6. 删除图书
 7. 统计        0. 保存并退出
===============================================
请选择功能：1
书名：三体
作者：刘慈欣
出版年份：2008
馆藏册数：3
已添加 #1  三体
...
请选择功能：0
已保存到  books.json
再见！
```

退出后 `books.json` 内容形如：

```json
{"next_id":4,"books":[{"id":1,"title":"三体","author":"刘慈欣","year":2008,"copies":3,"borrowed":0}, ...]}
```

---

## 6. 用 JimLang 直接体验子集版（已实测）

本仓库（JimLang）给 CatBase 实现了兼容前端（见本系列前两篇）。由于那套前端目前覆盖“核心子集”（无 `File`/`input`/`class`），我们把同一个系统改写成了**无交互的内存版**：种子数据直接入库，跑一遍“借书→查询→还书→删除→统计”，最后把书库 JSON 打出来。它**已经在本机跑通**：

```bash
bin/jimlang.cmd examples/catbase/library_demo.cat     # Windows
bin/jimlang.sh    examples/catbase/library_demo.cat   # Unix
```

真实输出（一字未改）：

```
=== 初始馆藏 ===
id | 书名 | 作者 | 年份 | 馆藏 | 借出 | 可借
1|三体|刘慈欣|2008|3|0|3
2|深入理解计算机系统|Bryant|2016|2|0|2
3|Java 编程思想|Bruce Eckel|2006|1|0|1
共 3 本
馆藏种类=3 总册数=6 借出=0 可借=6

=== 借书 id=1、id=3 ===
借出成功：三体
借出成功：Java 编程思想
=== 查询「计算」 ===
命中：2深入理解计算机系统/Bryant
=== 还书 id=1 ===
还书成功：三体
=== 删除 id=2 ===
已删除：深入理解计算机系统
=== 最终馆藏 ===
id | 书名 | 作者 | 年份 | 馆藏 | 借出 | 可借
1|三体|刘慈欣|2008|3|0|3
3|Java 编程思想|Bruce Eckel|2006|1|1|0
共 2 本
=== 存档 JSON ===
{"next_id":4,"books":[{"id":1,"title":"三体","author":"刘慈欣","year":2008,"copies":3,"borrowed":0},{"id":3,"title":"Java 编程思想","author":"Bruce Eckel","year":2006,"copies":1,"borrowed":1}]}
```

> 有意思的对照：完整版退出时把 `store` 写成 `books.json`；子集版把同一份 `store` 用 `json_dumps` 打到屏幕。**数据模型完全一致**——将来 JimLang 前端补齐 `File`/`input` 后，两个版本可以直接合并。

---

## 7. 避坑清单（CatBase 常见坑）

| 场景 | 注意 |
|---|---|
| 变量声明 | 类型不能省：`books = []` 不行，要 `books: list[any] = []` |
| dict 键 | 字面量键加引号 `{"title": ...}`；类型名 `dict[str, any]` |
| 从 JSON/any 取数 | 官方惯用 `int(str(x))` / `str(x)` 显式转换，别直接做算术 |
| 越界/缺键 | 多用 `dict.get(key, 默认值)` 和 `try/except` 探测 |
| 布尔 | `true` / `false`（大小写不敏感于 `True`，官方两者都收） |
| 字符串匹配 | 官方示例见切片 `s[i:j]`；可自行封装 `str_contains` |
| 编译目标 | 官方编译器目前是 Linux 的 `catbasecc`（闭源 EULA）；本仓库的 JimLang 前端支持其核心子集 |
| 模块化 | 支持 `import "./x.cat" as gm` + `gm.fn()`（官方示例 `test_get_use.cat`） |

---

## 8. 还能怎么扩展

- **换成 `class Book` 模型**：加 `to_dict()`/`from_json()`，把第 4.6 节的类用起来；
- **读者与借阅记录**：加 `readers: list[dict]` 与 `records: list[dict]`，借书时同时写记录；
- **分类统计/报表**：按 `category` 分组计数，练习 `dict[str, list[any]]`；
- **改造成 HTTP 服务**：官方 CatBase 自带 TCP/UDP/HTTP/WebSocket，把 `cmd_*` 包一层路由就是“图书 API”；
- **多存档**：`DATA_FILE` 变成参数，支持 `--db=xxx.json`（练习命令行参数 `args`）。

---

## 9. 小结

用 CatBase 写图书管理系统，本质是在练三件事：**想清楚强类型的数据结构、用好 dict+list 的组合、打通 “内存 → JSON → 文件” 的持久化链路**。做完这个项目，你会自然掌握 `def`/类型注解/容器/控制流/File/JSON/异常探测这些 CatBase 日常 80% 的语法面。

而 JimLang 兼容前端的出现，让同一份“核心子集”代码多了一个零安装、可嵌入的落点——系统逻辑在哪儿跑不重要，重要的是它终于能跑起来给你看。
