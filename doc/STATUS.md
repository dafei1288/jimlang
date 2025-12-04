# JimLang 项目状态（Snapshot）

日期：2025-12-04

## 已完成
- 语法/解析：ANTLR4，支持块作为独立语句、数组/对象字面量、访问器（. 与 []）
- 执行：REPL、脚本、STDIN；CLI 选项优先解析（--cli/-i、--eval/-e、--trace）
- 错误与跟踪：运行时错误包含 源名:行:列 + caret + 调用栈；Trace push/pop + enter/leave（可用 --trace 或 JIM_TRACE 开启）
- 作用域：函数作用域与块作用域；遮蔽与最近作用域赋值
- 类型系统（基础）：string/number/boolean/int/float/array/object/Java FQCN；int→float 宽化；错误信息含位置信息
- 标准库：字符串、数组、数学、文件 I/O；新增 join/keys/values/typeof/isArray/isObject/parseInt/parseFloat
- 文档：QUICKREF/ROADMAP/README/README_ZH 编码统一（UTF-8 无 BOM），CLI/作用域示例完善

## 进行中/近期计划
- 回归测试补充：
  - 独立块遮蔽的单测（与 examples/scope_if.jim 同步）
  - CLI --trace/--eval/STDIN 组合用例

## TODO（暂缓的两项，记录）
- 函数参数/返回值类型注解（function f(a:int): number）及测试（暂不实现，待排期）
- let/const（块作用域 + const 禁止重赋值）及测试（暂不实现，待排期）

## 建议的下一步（不涉及上面两项）
1) 语法文件同步：移除或同步 `src/main/resources/JimLang.g4`（避免与 `src/main/antlr4/.../JimLang.g4` 重复/不一致）
2) 补充块作用域单测：覆盖 `{ var x=2; }` 遮蔽后外部恢复
3) 清理 Windows 启动脚本注释的乱码：`bin/jimlang.cmd` 注释改为英文/ASCII，保持 CRLF

（本文件编码：UTF-8 无 BOM）