# 用 CatBase 实现一个简易 Agent 框架（领域无关，从 0 到 1 逐步递进）

> 作者：JimLang 项目
> 目标读者：普通开发者。想亲手搭一个“有脑子”的通用 agent 框架：会循环、会识别意图、
> 有记忆、会拼上下文、能调用工具——并且**不绑定任何业务领域**（本示例只挂了一个
> 最小“待办清单”工具用于演示；换成你的业务只需替换一层，框架不动）。
> 系列第 4 篇。配套代码：
> `examples/catbase/agent_llm_demo.cat`（LLM 大脑，已实测）、
> `examples/catbase/agent_demo.cat`（规则大脑脚手架，离线可跑）、
> `examples/catbase/agent/agent_full.cat`（官方 catbasecc 完整版）。

---

## 0. 先讲人话：Agent 到底是什么

把“会干活”的助手拆开，核心只有一句话：

> **循环**：看一句话 → 判断他想干嘛 → 决定怎么办 → 干 → 把结果说出来，然后继续等下一句。

这就是 **agent loop（智能体循环）**——和客服接线员一模一样。让 agent“显得聪明”的是三样东西：

| 概念 | 类比 | 在代码里是什么 |
|---|---|---|
| **意图识别**（intent） | 接线员先归类：咨询/办理/投诉 | 把用户话术归类成已知动作 |
| **记忆**（memory） | 便签本 + 长期档案柜 | 短时=最近几轮对话；长时=沉淀事实 |
| **上下文**（context） | 新员工上岗前的工位卡 | 每轮喂给“大脑”的身份+记忆+本轮结果 |

本框架的设计原则是**领域无关**：agent 的骨架（loop/记忆/上下文/大脑接口）不掺任何业务；
业务能力只通过“行动层（run_tool）+ 工具说明”挂进来。本文用一个最小的“待办清单”当挂件示例，
并演示把它替换成任何领域只需要动哪一处。

---

## 1. 阶段一：先有一个“会循环的壳子”

不管多高级，骨架都是这样：

```catbase
def main(args: list[str]) {
    while true {
        msg: str = input("你：")
        if msg == "退出" { print("再见"); break }
        print("助手：（先随便回一句）你说的是：", msg)
    }
}
```

然后我们把每一轮收敛成一个统一入口，之后所有“升级”都发生在这里面：

```catbase
def run_turn(t: dict, mem: dict, user_msg: str) -> str {
    remember(mem, "user", user_msg)              # 1 先记下
    act: dict = detect_brain(user_msg)           # 2 大脑决策(意图/参数)
    result: str = run_tool(t, act)               # 3 行动(调用工具)
    ctx: str = build_context(mem, act, result)   # 4 组装上下文
    reply: str = generate_reply(act, result)     # 5 生成回复
    remember(mem, "assistant", reply)            # 6 回复记下
    return reply
}
```

注意这个骨架里**没有图书、没有待办、没有业务**——`run_tool` 就是框架与业务之间的唯一插座。

---

## 2. 阶段二：大脑接口与“意图”

“大脑”在本框架里就是一个可替换的函数：输入一句话，输出一个结构化动作（意图 + 参数）。

**规则版**（离线脚手架 `agent_demo.cat` 用）：最直观的分类器，可读、零成本、适合当回归基线：

```catbase
def detect_brain(user_msg: str) -> dict[str, any] {
    act: dict[str, any] = {}
    if user_msg.contains("你好") { act["intent"] = "hello"; act["say"] = "..."; return act }
    if user_msg.contains("再见") { act["intent"] = "bye";   act["say"] = "再见！"; return act }
    ...
}
```

**LLM 版**（`agent_llm_demo.cat` 用）：同一函数签名，内部换成一次大模型调用——模型读“工具说明书”
后直接输出 JSON 动作（这就是 function-calling 的最朴素形态，任何文本模型都能用）：

```
模型输出：{"intent":"todo_add","text":"周三交周报"}
模型输出：{"intent":"todo_done","id":1}
```

> 深入浅出一句：**意图识别 = 把“自由文本”翻译成“结构化动作”**。规则版快、可解释、免 key；
> LLM 版泛化好、免维护关键词。两者接口相同，随时可换——这就是“大脑可插拔”。

---

## 3. 阶段三：让它有记忆

没有记忆的 agent 每轮都像失忆患者。记忆用一本“双层的本子”表示：

```catbase
mem = {
    "facts":   [],   # 长期记忆：跨会话沉淀的事实（档案柜，去重）
    "history": []    # 短期记忆：最近几轮对话（便签本，会翻页/裁剪）
}
```

```catbase
def remember(mem, role, text) {
    append(mem["history"], { "role": role, "text": text })
    if len(mem["history"]) > 6 {   # 短时裁剪：防上下文无限膨胀
        只保留最后 6 条 ...
    }
}
def note_fact(mem, fact) { 去重后 append(mem["facts"], fact) }
```

关键：**短时/长时分离**。短时会长大、必须裁剪；长时是“值得记住的结论”，精简常驻。
记忆由框架管理，业务只需要决定“哪些算值得记的事实”。

---

## 4. 阶段四：给它一张“工位卡”——context 组装

模型/回复器每轮并不知道“你是谁、刚聊过什么、工具查到了什么”，它只看到我们拼给它的文本（context）。

```catbase
def build_context(mem, act, result) -> str {
    ctx = "【系统】你是通用 agent“小助手”。\n"
    ctx += "【长期记忆】\n" + 遍历 mem["facts"]
    ctx += "【最近对话】\n" + 遍历 mem["history"]
    ctx += "【本轮】intent=" + ... + "，工具返回=" + result
    return ctx
}
```

四区块顺序有讲究：**身份 → 长期档案 → 最近对话 → 本轮证据（工具真实返回）**。
“回答长在证据上”而不是让模型凭空编——这是 agent 与纯聊天机器人的分水岭。
脚手架版本每轮把这块上下文原样打印出来，让你亲眼看到“大脑看到了什么”（可观测性）。

---

## 5. 阶段五：把零件装进 run_turn —— 完整框架（两个可运行版本）

```
每轮执行顺序
┌────────────┐   ┌─────────────┐   ┌──────────────┐
│ 1 remember │ → │ 2 detect    │ → │ 3 run_tool   │   ← 业务只在这一层
│  (记下用户) │   │  (大脑/意图) │   │  (调用工具)   │
└────────────┘   └─────────────┘   └──────┬───────┘
                                          ▼
┌────────────┐   ┌─────────────┐   ┌──────────────┐
│ 6 remember │ ← │ 5 generate  │ ← │ 4 build_ctx  │
│ (记下回答)  │   │  (生成回复)   │   │  (组装上下文)  │
└────────────┘   └─────────────┘   └──────────────┘
```

仓库现在提供两个可运行版本（**均领域无关**，只挂了最小“待办清单”示例工具）：

| 文件 | 大脑 | 运行 |
|---|---|---|
| `examples/catbase/agent_demo.cat` | 规则（离线） | `bin/jimlang.cmd examples/catbase/agent_demo.cat` |
| `examples/catbase/agent_llm_demo.cat` | **真实 LLM（ask_llm）** | 先配 key 再 `bin/jimlang.cmd examples/catbase/agent_llm_demo.cat` |
| `examples/catbase/agent/agent_full.cat` | 规则 + input/File 持久化 | 官方 `catbasecc` |
| `examples/catbase/agent_cli.cat` | **LLM，交互式 CLI**（边输入边对话） | `bin/jimlang.cmd examples/catbase/agent_cli.cat` |

LLM 版真实运行记录（DeepSeek deepseek-chat，实测）：

```
========== 小助手 · Agent（LLM 大脑 · 领域无关） ==========
>>> 用户：你好
│ 模型输出：{"intent":"chat","say":"你好！我是小助手，有什么可以帮你的吗？"}
<<< 助手：你好！我是小助手，有什么可以帮你的吗？

>>> 用户：帮我记一条待办：周三交周报
│ 模型输出：{"intent":"todo_add","text":"周三交周报"}
│ 工具返回：已添加待办 #1：周三交周报
<<< 助手：好的，已经帮您记下周三交周报这件事了。

>>> 用户：再看看我的待办      → {"intent":"todo_list"} → 工具列出待办
>>> 用户：把第 1 条完成掉      → {"intent":"todo_done","id":1} → 工具置为完成
>>> 用户：删掉第 2 条          → {"intent":"todo_del","id":2} → 工具返回"没有编号 2 的待办"
>>> 用户：谢谢，再见           → {"intent":"chat","say":"再见，有需要随时找我！"}
```

看懂这段，你就看懂了一个最小但**真由大模型驱动**的 agent：模型负责“听懂并决定干什么”
（输出 JSON 动作），框架负责“把动作变成真实副作用”（增/查/改/删），模型再把结果说成人话。
那句“没有编号 2 的待办”不是模板——是模型读了工具返回值自己转述的（第 2 条不存在，它说得对）。

---

## 6. 大脑是怎么接上 LLM 的（真实接线，非示意）

只有三处关键代码：

```catbase
# (1) 给模型看的“工具说明书 + 上下文”
def brain_prompt(mem, user_msg) -> str {
    p = "你是“小助手”，一个通用 agent。\n"
    p += "可用工具（JSON 动作）：\n"
    p += "- {\"intent\":\"todo_add\",\"text\":\"...\"} 新增待办\n"
    p += "- {\"intent\":\"todo_done\",\"id\":编号} 标记完成\n"
    p += "...\n【长期记忆】...\n【最近对话】...\n【本轮用户】" + user_msg
    p += "\n只输出一行 JSON："
    return p
}

# (2) 调用模型（JimLang 内置 ask_llm，OpenAI 兼容，DeepSeek 默认）
r: str = ask_llm(brain_prompt(mem, user_msg),
                 { model: "deepseek-chat", temperature: 0.1, print: false })

# (3) 把模型输出解析成动作并执行
act: dict[str, any] = json_loads(r)
if str(act.get("intent")) == "todo_done" { run_tool(t, act) }
```

三个要点：

1. **工具以 JSON 协议暴露给模型**，模型只输出 JSON——不依赖厂商专属 function-calling API；
2. **两段式**：模型先“决策”（JSON 动作）→ 框架执行拿到真实结果 → 模型再“话术”（润色转述）。
   “工具返回”那格就是 agent 与聊天机器人的分水岭：答案有据可依；
3. **`ask_llm` 只是这座桥的 JimLang 实现**。官方 `catbasecc` 上用 `http_post` 打
   OpenAI 兼容的 `/v1/chat/completions` 即可，JSON 动作协议不变，只换“桥”
   （`agent_full.cat` 里默认规则大脑，接法见官方 `Response` 文档）。

**想要“边输入边对话”？** 用交互式 CLI：`examples/catbase/agent_cli.cat`。它与上面
LLM 版同一套框架，只是把固定对话脚本换成 `while true { input("你> ") ... }` 循环，
每次你敲一行，它就经历一轮“记忆→决策→工具→回复”，说“再见/退出”或 `Ctrl+C` 结束：

```bash
bin/jimlang.cmd examples/catbase/agent_cli.cat
你> 帮我记一条待办：周三交周报
小助手> 好的，已经帮您记下周三交周报这件事了。
你> 看看我的待办
小助手> 周三需要提交周报。
你> 再见
小助手：再见！
```

key 优先级（易踩坑）：`ask_llm` 按 `LLM_API_KEY → API_KEY → DEEPSEEK_API_KEY` 取 key
（显式覆盖 > `.env` > 系统环境变量）。demo 启动会打印“将使用 …中的 key（尾号 …）”。
配置方式：

```bash
export LLM_API_KEY=sk-...                  # 或项目根目录 .env 写 LLM_API_KEY=sk-...
bin/jimlang.cmd examples/catbase/agent_llm_demo.cat
```

---

## 7. 怎么把它接到你的业务上（领域无关的真正含义）

换领域只动两处，框架零改动：

1. **加一个工具函数**（例如“查询订单”）并塞进 `run_tool` 的分支：
   ```catbase
   def tool_query_order(t, act) -> str { ... 查你的数据/调你的 API ... }
   # run_tool 里加一行：
   if intent == "order_query" { return tool_query_order(t, act) }
   ```
2. **在 `brain_prompt` 的工具说明里加一行 JSON 协议**，让模型知道什么时候用、参数是什么。

本仓库早前的“图书管理系统”文章就是活例子：把它那组 `cmd_*` 函数原样塞进 `run_tool`，
再在工具说明里描述一遍，图书就变成了这个框架的一个工具域——框架代码一行都不用改。

更远一步的方向（都是替换某个函数）：
- 工具注册表：把 `run_tool` 的 if 链换成“名字查表”，让模型从清单里自己挑工具；
- 思考-行动循环：允许多次“决策→执行→再决策”，直到任务完成（ReAct 雏形）；
- 反思：每次行动后追加一轮自评回合写进 history；
- 向量记忆：`facts` 变大后按相关度检索，替换“全量拼接”；
- 多智能体：拆成多个 agent loop + 消息队列互调。

---

## 8. 避坑与小结

**写这类框架时容易踩的坑**
1. CatBase 变量都要类型：`ctx: str = ""` 而非 `ctx = ""`；dict 键加引号、写 `dict[str, any]`；
2. 从 JSON 取回来的值记得显式转换：`int(str(act.get("id", 0)))`；
3. `history` 必须裁剪，否则上下文越长越贵、越长越乱；
4. 工具结果一定要进 context（作为“证据”），只塞进回复等于大脑没看到；
5. 想让框架接得住任何领域，就把业务**只**放在 `run_tool` + 工具说明里。

**一句话总结**
Agent 不神秘：**循环是骨架，意图是耳朵，记忆是档案柜，上下文是工位卡，工具是手**。
本框架刻意与业务解耦——骨架 + 可插拔大脑 + 一层薄薄的工具插座；
挂图书管理是它，挂待办清单是它，挂你的下一个业务也是它。
