# JimLang 功能开发状态

## Snapshot 2025-11-28
- 控制流：if/else，while，for [完成]；break/continue [完成]
- 表达式：算术 (+,-,*,/,%) [完成]；比较 (>,<,>=,<=,==,!=) [完成]；链式二元运算 [完成]
- 赋值：标识符 / 索引 / 属性（a[i]、obj.key）[完成]
- 数据结构：数组/对象 [完成]；数组方法（push/pop/...）[待办]
- 标准库：字符串（len/upper/lower/trim/substring/indexOf/split）[进行中]；数学（max/min/abs/round/random）[进行中]；文件（read/write/exists）[进行中]
- REPL：可保留状态 + 命令 (:vars, :funcs, :clear, :help, :exit) [完成]；多行输入 [改善中]

## Phase 完成度
- Phase 1：25%
- Phase 2：100%
- 数据结构：60%
- 标准库：30%
- REPL：60%
- 模块/高级特性：0%

## TODO
- 字符串：contains、replace、startsWith/endsWith、padLeft/padRight、repeat
- 数学：pow、sqrt、floor、ceil、randomRange
- 文件：file_append
- 作用域/类型系统/错误定位 与 友好提示