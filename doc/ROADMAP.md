# JimLang 开发路线图 (Development Roadmap)

## 概览
- 目标：实现一个易于上手、可扩展的脚本语言，具备清晰的语法、可嵌入的运行时与实用标准库。
- 平台：Java 21，ANTLR4 语法与解析，Maven 构建。
- 编码：UTF-8（无 BOM）；.cmd 使用 CRLF，其余使用 LF（由 .editorconfig/.gitattributes 管控）。

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
- CLI/REPL：
  - --cli/-i 进入交互式 REPL；--eval/-e 执行单行；"-" 从标准输入读取；选项优先解析。
  - --trace 启用调用跟踪（也支持环境变量 JIM_TRACE=1）。
- 运行时跟踪与错误：
  - Trace.push/pop；当启用跟踪时打印 enter/leave。
  - 运行时错误包含 源名:行:列 + caret 指示 + 调用栈（Call stack）。
- 作用域：
  - 函数作用域与块作用域均已实现；变量遮蔽生效；简单赋值更新最近作用域中的已有变量。
- 类型系统：
  - 支持类型标注：string/number/boolean/int/float/array/object 以及 Java FQCN；
  - 宽化规则：int 可赋给 float；coerce 行为与类型检查集成于变量声明/赋值。
- 标准库：
  - 字符串、数组、数学、文件 I/O 等常用函数；新增 join/keys/values/typeof/isArray/isObject/parseInt/parseFloat。

## TODO（优先级高→低）
- 作用域/函数：
  - 闭包与捕获外部变量；函数值作为一等公民。
  - let/const 语义与不可变绑定（设计与实现）。
- 类型系统：
  - 更细的类型规则与错误信息；可选的类型推断；数组/对象的元素类型约束（泛型化）。
- CLI 与可用性：
  - 退出码规范；--trace 与 --eval 组合场景的更多测试；帮助信息与示例完善。
- 运行时：
  - return 在任意嵌套语句内的早返回处理更加健壮。
- 文档：
  - 快速上手与示例完善；错误信息格式说明；编码与换行规范整合（agent.md）。

## 编码/换行规范（摘要）
- 所有源码/文档使用 UTF-8 无 BOM；
- .cmd/.bat 使用 CRLF，其余文本使用 LF；
- 使用 .editorconfig/.gitattributes 统一风格；PowerShell 写文本建议：
  - `$enc = New-Object System.Text.UTF8Encoding($false)`
  - `[IO.File]::WriteAllText(path, content, $enc)`