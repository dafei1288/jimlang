# JimLang 开发路线图 (Development Roadmap)

## 概览
- 目标：实现一个易于上手、可扩展的脚本语言，具备清晰的语法、可嵌入的运行时与实用标准库。
- 平台：Java 21，ANTLR4 语法与解析，Maven 构建。
- 编码：UTF-8（无 BOM）；.cmd 使用 CRLF，其余使用 LF。

## 阶段目标

### Phase 1（基础语法与执行）
- 语法/解析：ANTLR4 词法/语法，AST Visitor。
- 执行方式：REPL、从文件执行、JSR-223 ScriptEngine（可嵌入）。
- 表达式与运算：数字/字符串/布尔，基本二元运算，字符串拼接，可选分号。
- 函数：函数定义/调用，参数传递与返回值。

### Phase 2（流程控制与容器）
- 条件与循环：if/else、while、for、break、continue。
- 数组与对象字面量：索引/属性访问；内置属性 length。
- 错误信息：更友好的运行时错误提示。

### Phase 3（标准库扩展）
- 字符串：split/join/substring/indexOf/toUpperCase/toLowerCase/trim。
- 数组：push/pop/shift/unshift/length 及相关工具。
- 数学：abs/round/floor/ceil/pow/sqrt/random/randomRange 等。
- I/O：file_read/file_write/file_exists/file_append。
- 其他内置：keys/values/typeof/isArray/isObject/parseInt/parseFloat。

### Phase 4（进阶能力：规划中）
- 异常：try/catch/finally。
- 模块：import/export。
- 函数式：lambda/闭包。

## 当前状态（已完成）
- CLI/REPL：选项优先解析（--cli/-i、--eval/-e、"-" 从 STDIN）；--trace 可用
- 跟踪/错误：caret + 源名:行:列 + 调用栈；Trace enter/leave
- 作用域：函数作用域与块作用域；遮蔽与最近作用域赋值；配套示例与单测
- 语法/执行：块内 return（早返回）已支持
- 类型系统：string/number/boolean/int/float/array/object/Java FQCN；int→float 宽化；类型错误信息包含位置信息
- 标准库：字符串、数组、数学、文件 I/O；新增 join/keys/values/typeof/isArray/isObject/parseInt/parseFloat
- 维护：语法文件去重（以 src/main/antlr4 为准）、Windows 启动脚本注释改为 ASCII

## TODO（优先级高→低）
- CLI 与可用性：
  - 补充集成测试：已覆盖 --eval/STDIN；待补 --trace 与 --eval/STDIN 组合；错误时退出码（非 0）约定及测试
- 类型系统：
  - 更细的类型规则与错误信息；可选的类型推断
- 标准库：
  - JSON/YAML 已具备；如有需要再补充工具函数
- 记录的暂缓项：
  - 函数参数/返回值类型注解（function f(a:int): number）[暂缓]
（本文件编码：UTF-8 无 BOM）
## TODO（优先级高→低）
- Web（内置服务器）：
  - 回归测试集：覆盖 /api/ping、/api/echo、/users/:id、/static/*、下载、Cookie set/show/clear；随机端口；启动/停止生命周期。
  - 细节增强：静态文件路径规范化（防目录穿越）、简易访问日志。
  - 可配置项：从环境变量/命令行读取主机、日志开关（端口已可由脚本通过 env_get 传入）。
  - API：stop_webserver(port) 关闭指定端口服务。