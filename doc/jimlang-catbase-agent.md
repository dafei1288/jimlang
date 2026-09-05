# 用 CatBase 实现一个简易 Agent 框架（从 0 到 1，逐步递进）

> 作者：JimLang 项目
> 目标读者：普通开发者。想亲手搭一个“有脑子”的小助手：会循环、会识别意图、
> 有记忆、会拼上下文、还能调用工具。
> 系列第 4 篇。配套代码：`examples/catbase/agent/agent_full.cat`（官方 catbasecc）
> 与 `examples/catbase/agent_demo.cat`（可在 JimLang 直接运行的子集版）。

---

## 0. 先说人话：Agent 到底是什么

把 ChatGPT 那种“会干活”的程序拆开，核心就是一句话：

> **循环**：看一句话 → 判断他想干嘛 → 决定怎么办 → 干 → 把结果说出来，然后继续等下一句。

这就是 **agent loop（智能体循环）**。它本身不神秘，和客服接线员的流程一模一样。真正让 agent“显得聪明”的是三样东西：

| 概念 | 类比 | 在代码里是什么 |
|---|---|---|
| **意图识别**（intent） | 接线员先归类：投诉/咨询/办理 | 把用户话术分类成几个已知动作 |
| **记忆**（memory） | 便签本 + 长期档案柜 | 短时=最近几轮对话；长时=沉淀下来的事实 |
| **上下文**（context） | 新员工上岗前的工位资料 | 每一轮喂给“大脑”的系统说明+记忆+本轮结果 |

本文就用 **CatBase** 把这三样从零搭出来。为什么用 CatBase？它语法接近 Python、强制类型让数据结构必须清楚、自带 JSON 与文件 API——非常适合讲“框架”；而且我们第 3 篇刚做过图书管理系统，正好拿来当 agent 的“工具箱”。

---

## 1. 阶段一：先有一个“会循环的壳子”（agent loop 最小版）

不管多高级的 agent，骨架都长这样：

```catbase
def main(args: list[str]) {
    while true {
        msg: str = input("你：")
        if msg == "退出" {
            print("再见")
            break
        }
        print("助手：（先随便回一句）你说的是：", msg)
    }
}
```

就这 10 行，已经是一个 agent loop：**读入 → 处理 → 输出 → 再读入**。它什么都不会，但循环骨架对了。

接下来的每一阶段，都是往这个循环里“塞脑子”。我们把它收敛成一个统一的入口，之后每轮只调用它：

```catbase
def run_turn(s: dict[str, any], mem: dict[str, any], user_msg: str) -> str {
    remember(mem, "user", user_msg)            # 1 先把话记下
    intent: dict[str, any] = detect_intent(user_msg)   # 2 判断意图
    result: str = run_tool(s, intent)          # 3 调用工具(行动)
    ctx: str = build_context(mem, intent, result)      # 4 组装上下文
    reply: str = generate_reply(intent, result)        # 5 生成回复
    remember(mem, "assistant", reply)          # 6 把回复记下
    return reply
}
```

这一条 `run_turn` 就是本框架的“心脏”。下面每个阶段都在换它的内部器官。

---

## 2. 阶段二：学会听懂话——意图识别

“你说的是……”不算回答。我们要把用户的话**分类**成已知动作：查书、借书、还书、统计、退出。

规则分类器（关键词打分）是最容易理解的一版——它不需要任何模型：

```catbase
def detect_intent(msg: str) -> dict[str, any] {
    rules: list[any] = [
        { "name": "list",   "keys": ["有哪些书", "列出", "都有什么书", "列表"] },
        { "name": "find",   "keys": ["找", "查一下", "有没有", "搜索"] },
        { "name": "borrow", "keys": ["借", "借阅"] },
        { "name": "ret",    "keys": ["还", "归还"] },
        { "name": "stats",  "keys": ["统计", "多少本", "馆藏情况"] },
        { "name": "quit",   "keys": ["退出", "再见", "拜拜"] }
    ]
    best: str = "help"        # 默认意图
    bestScore: int = 0
    for i in range(0, len(rules)) {
        r: dict[str, any] = rules[i]
        ks: list[any] = r["keys"]
        score: int = 0
        for j in range(0, len(ks)) {
            if 命中关键词(msg, str(ks[j])) { score = score + 1 }
        }
        if score > bestScore { bestScore = score; best = str(r["name"]) }
    }
    intent: dict[str, any] = { "name": best, ... }
    return intent
}
```

- “有哪些书”命中 `list`（2 分）→ 归类 `list`；“帮我借一下”命中 `borrow` → 归类 `borrow`；一句话里两个关键词都出现时，谁分高听谁的。
- 意图不只是名字，还要**抽取参数**：借书要知道借几号。抽取函数很直白：把消息里的数字抠出来当编号，书名则用 `《》` 之间的内容（`sub_between`）。

> 深入浅出一句：**意图识别 = 从“自由文本”到“结构化动作”的翻译**。规则版可读、可控、零成本；等语料多了再换 LLM/分类器，接口不变——这就是把 `detect_intent` 单独抽成函数的意义。

---

## 3. 阶段三：让它有记忆——memory

没有记忆的 agent 每轮都像失忆患者。我们给它两本“本子”：

```catbase
mem = {
    "facts":   [],   # 长期记忆：跨会话沉淀的事实（档案柜）
    "history": []    # 短期记忆：最近几轮对话（便签本，会翻页）
}
```

写与读分别封装成 `remember()` 与 `note_fact()`：

```catbase
def remember(mem: dict[str, any], role: str, text: str) {
    turn: dict[str, any] = { "role": role, "text": text }
    mem["history"].append(turn)
    # 短时记忆会“翻页”：只保留最近 6 条，防止上下文无限膨胀
    if len(mem["history"]) > 6 {
        keep: list[any] = []
        h: list[any] = mem["history"]
        start: int = len(h) - 6
        for i in range(start, len(h)) { keep.append(h[i]) }
        mem["history"] = keep
    }
}

def note_fact(mem: dict[str, any], fact: str) {
    # 长时记忆：去重后写入（只有借书成功这类“值得记住”的事才写）
    if 不存在该事实 {
        mem["facts"].append(fact)
    }
}
```

关键设计：
- **短时/长时分离**。短时会无限增长、必须裁剪（不然每次请求都塞满旧对话）；长时是沉淀下来的结论（如“最近借了《三体》”），精简且长期有效。
- **记忆是“框架层”行为，不是业务行为**。借书动作由工具完成，而“要不要把这件事记进长期档案”由 agent 自己判断——这正是 agent 与普通 if-else 程序的差别：它会对自己的行为做元层面记录。

CatBase 的好处在这里体现：`dict` + `list` 直接描述记忆结构，`json_dumps(mem)` 一行就能落盘/展示；完整版里 `memory.json` 就是它的持久化档案。

---

## 4. 阶段四：给它一张“工位卡”——context 组装

模型/回复器每一轮并不知道“你是谁、刚才聊过什么、刚才工具查到了什么”。它只看到我们拼给它的**那一块文本**，也就是 context。所以 context 的组装质量 = agent 的回答质量。

```catbase
def build_context(mem: dict[str, any], intent: dict[str, any], tool_result: str) -> str {
    ctx: str = "【系统】你是图书馆助手，请根据工具结果友好回答。\n"
    ctx = ctx + "【长期记忆】\n"
    ... 遍历 mem["facts"]，每行加 "  - ..."
    ctx = ctx + "【最近对话】\n"
    ... 遍历 mem["history"]，每行加 "  role: text"
    ctx = ctx + "【本轮】意图=...，工具返回=..." 
    return ctx
}
```

四个区块的顺序是有讲究的（先身份、再背景、再上下文、最后才是本轮事件）：
1. **系统说明**：你是什么、用什么语气、必须依据工具结果回答——防止“幻觉式瞎编”；
2. **长期记忆**：已经认定的事实；
3. **最近对话**：最近的问答，让回答有连贯性（用户刚问过什么，别答非所问）；
4. **本轮**：当前意图 + 工具真实返回——这是“证据”，回答要长在证据上。

> 类比：把每次回答看作新员工刚上岗。工位卡上先写岗位职责（系统），再写客户档案（长期记忆），然后是刚才聊到哪（最近对话），最后是“刚刚查到的数据”（本轮工具结果）。没有这张卡，他只能信口开河。

子集演示版为了教学，会把每一轮的 context 原样打印出来（`── 上下文 ──` 那一大段），让你亲眼看到“大脑看到的东西”——这正是框架可观测性（observability）的雏形：**把决策依据摊开给人看**。

---

## 5. 阶段五：把零件装进 run_turn —— 完整框架

把前面所有函数收进 `run_turn`，框架就成了：

```
每轮执行顺序
┌────────────┐    ┌─────────────┐    ┌──────────────┐
│ 1 remember │ → │ 2 detect    │ → │ 3 run_tool   │
│  (记下用户) │    │  (意图+参数) │    │  (调用工具)   │
└────────────┘    └─────────────┘    └──────┬───────┘
                                             ▼
┌────────────┐    ┌─────────────┐    ┌──────────────┐
│ 6 remember │ ← │ 5 generate  │ ← │ 4 build_ctx  │
│ (记下回答)  │    │  (生成回复)   │    │  (组装上下文)  │
└────────────┘    └─────────────┘    └──────────────┘
```

这个框架的可替换点（也就是将来“升级大脑”的插槽）：
- `detect_intent` / `run_tool` —— 想加能力就加工具与规则；
- `generate_reply`（大脑）—— 可以是规则、也可以是**真实 LLM**（下节就是真·LLM 版本）。

仓库现在提供**两个可运行的 agent**：

| 文件 | 大脑 | 用途 |
|---|---|---|
| `examples/catbase/agent_demo.cat` | 规则（离线确定） | 脚手架：没网也能看管道；每轮打印“大脑看到的上下文” |
| `examples/catbase/agent_llm_demo.cat` | **真实 LLM（ask_llm）** | 正式版：每轮由模型决策动作并组织话术（**已实测**） |

运行 LLM 版前先准备 key（选一即可），再运行：

```bash
export LLM_API_KEY=sk-...          # 或 DEEPSEEK_API_KEY，或用 load_env(".env")
bin/jimlang.cmd examples/catbase/agent_llm_demo.cat
```

**LLM 大脑版真实运行记录（DeepSeek deepseek-chat，节选）：**

```
========== 图书助手 · LLM 大脑版 ==========
>>> 用户：你好呀
┌─ 思考中(ask_llm) ─────────────────────
│ 模型动作原始输出：{"intent":"help","say":"你好呀！我是图书助手，可以帮你查书、借书、还书或了解馆藏统计。请问有什么需要帮助的吗？"}
│ 决策：intent=help
└─────────────────────────────────────────
<<< 助手：你好呀！我是图书助手，可以帮你查书、借书、还书或了解馆藏统计。请问有什么需要帮助的吗？

>>> 用户：有哪些书？
│ 模型动作原始输出：{"intent":"list"}
│ 决策：intent=list
│ 工具返回：#1 《三体》 刘慈欣 可借2 / #2 《深入理解计算机系统》 Bryant 可借1 / #3 《小王子》 ...
<<< 助手：查询到三本相关书籍：《三体》有2本可借，《深入理解计算机系统》有1本可借，《小王子》有3本可借。

>>> 用户：我想借《小王子》      → {"intent":"borrow","kw":"小王子"} → 借出成功：《小王子》
<<< 助手：好的，《小王子》已经借出成功了。
>>> 用户：还一下《小王子》      → {"intent":"ret","kw":"小王子"}   → 还书成功：《小王子》
>>> 用户：统计一下馆藏情况      → {"intent":"stats"} → 馆藏种类=3 总册数=6 借出=0
<<< 助手：馆内共有3种、6册藏书，目前全部在架，没有借出的记录。
>>> 用户：谢谢，再见 → {"intent":"help","say":"不客气，再见！有需要随时找我。"}
========== 记忆快照 ==========
【长期事实】 - 最近借了《小王子》
【最近对话】 user: 我想借《小王子》 / assistant: 好的... / ...
```

看懂这段记录，你就看懂了一个最小但**真·由大模型驱动**的 agent：模型负责“听懂并决定干什么”（输出 JSON 动作），框架负责“把动作变成真实副作用”（借书/还书/统计），模型再负责“把结果说成人话”。那句“目前全部在架”不是模板，是模型读了工具返回值自己组织的——因为还书后 `借出=0`，它说得对。

---

## 6. 大脑是怎么接上 LLM 的（真实接线，非示意）

关键只有三处代码：

```catbase
# (1) 给模型看的“工具说明书 + 上下文”：
def brain_prompt(mem, user_msg) -> str {
    p: str = "你是“图书助手”智能体。可用工具（JSON 动作）：
"
    p = p + "{\"intent\":\"list\"} 列馆藏 ... "
    p = p + "
【长期记忆】..."   # 拼 mem["facts"]
    p = p + "
【最近对话】..."   # 拼 mem["history"]
    p = p + "【本轮用户】" + user_msg
    p = p + "
只输出一行 JSON："
    return p
}

# (2) 调用模型（JimLang 内置 ask_llm，OpenAI 兼容，DeepSeek 默认）：
r: str = ask_llm(brain_prompt(mem, user_msg),
                 { model: "deepseek-chat", temperature: 0.1, print: false })

# (3) 把模型输出当 JSON 解析成动作：
act: dict[str, any] = json_loads(r)
if str(act.get("intent")) == "borrow" { ...执行工具... }
```

三个设计点：

1. **“工具”以 JSON 协议暴露给模型**（intent + 参数），模型只输出 JSON——这就是“函数调用(function calling)”的最朴素形态，任何支持文本生成的模型都能用，不依赖厂商专属 API；
2. **两段式**：模型先“决策”（出 JSON 动作）→ 框架执行工具拿到真实结果 → 模型再“话术”（把结果润色成自然中文）。中间那格“工具返回”就是 agent 与纯聊天机器人的分水岭：回答长在证据上，而不是模型凭空编；
3. **`ask_llm` 只是这座桥的 JimLang 实现**。官方 catbasecc 上同样的 JSON 动作协议用 `http_post` 打 OpenAI 兼容的 `/v1/chat/completions` 即可（按官方 `Response` 文档取内容），协议不变、只换“桥”。`agent_demo.cat`（规则大脑）则可以继续当“无网也能跑”的脚手架与回归基线。

> 再深一步：意图识别也可以整个交给模型——让它在 JSON 里直接回 `{"intent":"borrow","kw":"小王子"}`（本版就是这么做的），规则分类器就退居为“备用/兜底”。取舍始终是：规则 = 快、可解释、零成本；模型 = 泛化、少维护关键词表。

## 7. 更远一步：这个框架能长成什么样

| 方向 | 做法（思路） |
|---|---|
| **工具注册表** | 把 `run_tool` 的 if 链改成 `tools: list[dict]`（名字/说明/处理函数字符串），用“名字查表”分派；将来可以让 LLM 从工具清单里自己挑 |
| **思考-行动循环** | 在 `run_turn` 里加“计划”：先让大脑决定下一步调哪个工具，把结果喂回去再决定，直到任务完成（ReAct 的雏形） |
| **流式输出** | LLM 接口开 `stream: true`，边收边打印（CatBase 官方支持 WebSocket/流式，JimLang 侧见 `examples/llm_stream_demo.jim`） |
| **反思（reflection）** | 每次行动后追加一轮“刚才做对了吗”的自评回合，写进 history 再继续 |
| **向量记忆** | `facts` 多了以后按相关度检索（本地可用关键词倒排，正式用向量库），替换 `build_context` 的“全量拼接” |
| **多智能体** | 拆成“图书员/统计员/外联员”，各自一个 loop + 一个消息队列，互相调用 |

每个方向都对应本框架里一个可以替换的函数——这也是把框架写“薄”（只负责编排，不掺业务）的最大红利。

---

## 8. 避坑与小结

**写 CatBase Agent 时容易踩的坑**
1. 变量都要类型：`ctx: str = ""` 而不是 `ctx = ""`；
2. dict 键加引号、类型写 `dict[str, any]`；从 JSON 回来的值记得 `int(str(x))` 显式转换；
3. `history` 不裁剪会无限增长——每个真实项目都死于“上下文爆掉”；
4. 工具结果要进 context 而不是只进回复——否则“证据”没有被大脑看到；
5. 想加关键词命中，先用“切片 + 窗口比对”自己写个 `sub_pos`，别依赖没验证过的方法名（官方示例见 `s[i:j]` 切片）。

**一句话总结**
Agent 不神秘：**循环是骨架，意图是耳朵，记忆是档案柜，上下文是工位卡，工具是手**。用 CatBase 搭这套，10 行能跑 loop，几十行能听懂话、记得住事，一百来行就有一个看得见、存得下、可插拔的框架——而把它换成真实 LLM，只差一次 HTTP 调用。

代码位置：
- LLM 大脑版（JimLang，已实测）：`examples/catbase/agent_llm_demo.cat`（先 `export LLM_API_KEY`）
- 规则脚手架（JimLang，离线可跑）：`examples/catbase/agent_demo.cat`
- 官方 catbasecc 完整版（File/JSON 落盘 + input 交互）：`examples/catbase/agent/agent_full.cat`
