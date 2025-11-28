# JimLang 开发计划 (Development Roadmap)

## 项目现状

### 已实现
- ANTLR 4 语法定义、Lexer/Parser 自动生成
- AST Visitor 解释执行器
- 基础 REPL 交互式环境（保留状态、支持命令）
- JSR-223 ScriptEngine 集成
- Maven 构建系统

### 语言特性
- 变量声明：`var x = 10`
- 可选类型注解：`var name: string = "jim"`
- 函数：定义与调用，参数绑定与返回值
- 基本数据类型：number, string, boolean
- 运算：算术 + - * / %；比较 > < >= <= == !=；字符串拼接
- 控制流：if/else，while，for，break，continue（已完成）
- 赋值增强：支持 `a[i] = v`、`obj.key = v`
- 数据结构：数组/对象 字面量、索引/属性读写，`length` 可读

### 待改进/缺失
- 作用域完善（函数/块作用域链、变量 shadowing）、类型系统（可选）
- 标准库扩展：字符串（contains/replace/...）、数组方法（push/pop/shift/unshift）、Math 扩展（pow/sqrt/...）、文件 I/O 扩展
- 错误处理：更友好的错误提示、行列号、try/catch（规划）
- 模块系统、闭包/Lambda、调试/工具链（规划）

---

## 开发路线图

### Phase 1：核心修复与 REPL 增强（进行中）
- 修复函数参数绑定、完善算术/比较运算
- 分层作用域与符号表
- REPL：持久化状态、命令、错误提示、启动横幅

### Phase 2：控制流与数据结构（已达成）
- if/else、while、for、break/continue
- 链式二元运算（左结合 `a + b + c`）
- 数组/对象字面量与索引/属性访问；`length` 属性
- 待办：数组方法 push/pop/shift/unshift

### Phase 3：标准库扩展（进行中）
- 字符串：len、toUpperCase/toLowerCase、trim、substring、indexOf、split
- 数学：max/min/abs/round/random（已具备），pow/sqrt/floor/ceil/randomRange（待办）
- 文件：file_read/file_write/file_exists（已具备），file_append（待办）

### Phase 4：高级特性（规划）
- 闭包、Lambda、错误处理（try/catch/finally）、模块系统（import/export）

---

## Milestones
- 2025-11-28：v0.3.0 — 完成 Phase 2（控制流 + break/continue + 链式运算 + 基础数组/对象）