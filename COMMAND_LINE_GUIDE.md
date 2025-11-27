# JimLang 命令行工具使用指南

## 概述

JimLang 现在支持命令行工具，可以直接执行脚本文件！

## 安装和构建

### 1. 构建项目

首先，需要构建项目生成可执行 JAR：

```bash
cd D:\working\mycode\jimlang
mvn clean package
```

这会生成：
- `target/jimlang-1.0-SNAPSHOT.jar` - 主 JAR
- `target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar` - 包含所有依赖的可执行 JAR

### 2. 启动脚本位置

- **Windows**: `bin/jimlang.cmd`
- **Unix/Linux/Mac**: `bin/jimlang.sh`

## 使用方式

### 基本命令

```bash
# Windows
bin\jimlang.cmd <script.jim>

# Unix/Linux/Mac
bin/jimlang.sh <script.jim>
```

### 命令选项

```bash
# 查看版本信息
bin\jimlang.cmd --version
bin\jimlang.cmd -v

# 查看帮助信息
bin\jimlang.cmd --help
bin\jimlang.cmd -h

# 执行脚本文件
bin\jimlang.cmd mycode.jim
```

## 示例脚本

### 示例 1: 简单测试 (`examples/test.jim`)

```jim
var x = 10
var y = 20
println("Sum:")
println(x + y)

if (x < y) {
    println("x is less than y")
}

for (var i = 0; i < 5; i = i + 1) {
    println(i)
}
```

执行：
```bash
bin\jimlang.cmd examples\test.jim
```

输出：
```
Sum:
30
x is less than y
0
1
2
3
4
```

### 示例 2: 斐波那契数列 (`examples/fibonacci.jim`)

```jim
function fibonacci(n) {
    var a = 0
    var b = 1
    for (var i = 0; i < n; i = i + 1) {
        var temp = a
        a = b
        b = temp + b
    }
    return a
}

println("Fibonacci sequence:")
for (var i = 0; i < 10; i = i + 1) {
    var result = fibonacci(i)
    println(result)
}
```

执行：
```bash
bin\jimlang.cmd examples\fibonacci.jim
```

## 添加到系统 PATH

### Windows

可以将 `bin` 目录添加到系统 PATH，这样可以在任何位置直接运行：

1. 打开"系统属性" → "环境变量"
2. 在"用户变量"或"系统变量"中找到 `Path`
3. 添加：`D:\working\mycode\jimlang\bin`
4. 重新打开命令行

然后可以直接使用：
```bash
C:\> jimlang mycode.jim
```

### Unix/Linux/Mac

在 `~/.bashrc` 或 `~/.zshrc` 中添加：

```bash
export PATH="$PATH:/path/to/jimlang/bin"
```

然后运行：
```bash
source ~/.bashrc
jimlang.sh mycode.jim
```

## 文件结构

```
jimlang/
├── bin/
│   ├── jimlang.cmd          # Windows 启动脚本
│   └── jimlang.sh           # Unix 启动脚本
├── examples/
│   ├── test.jim             # 简单测试脚本
│   └── fibonacci.jim        # 斐波那契数列示例
├── src/
│   └── main/java/.../Main.java  # 命令行入口
├── target/
│   └── jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar  # 可执行 JAR
└── pom.xml                  # Maven 配置
```

## 错误处理

### JAR 文件未找到

如果看到错误：
```
Error: JimLang JAR file not found!
```

解决方案：
```bash
cd D:\working\mycode\jimlang
mvn clean package
```

### Java 未安装

如果看到错误：
```
Error: Java is not installed or not in PATH
```

解决方案：
1. 安装 Java 21 或更高版本
2. 确保 `java` 命令在 PATH 中

验证 Java 安装：
```bash
java -version
```

### 脚本执行错误

如果脚本执行出错，检查：
1. 文件路径是否正确
2. 文件是否存在
3. 语法是否正确（参考 `QUICKREF.md`）

## JimLang 语法快速参考

```jim
// 变量
var x = 10
var name = "JimLang"

// 函数
function add(a, b) {
    return a + b
}

// if/else
if (x > 5) {
    println("Greater")
} else {
    println("Less or equal")
}

// while 循环
var i = 0
while (i < 5) {
    println(i)
    i = i + 1
}

// for 循环
for (var j = 0; j < 10; j = j + 1) {
    println(j)
}

// 系统函数
println("Hello")  // 输出并换行
print("Hi")       // 输出不换行
```

## 技术细节

- **语言**: JimLang 1.0-SNAPSHOT (Phase 2 Complete)
- **Java 版本**: 21
- **解析器**: ANTLR 4.13.1
- **构建工具**: Maven 3.x
- **JAR 大小**: ~8 MB (包含所有依赖)

## 已知限制

1. **链式表达式**: 不支持 `a + b + c`，需要分解为多个语句
2. **字符串拼接**: 支持单次拼接 `"Hello" + name`
3. **break/continue**: 暂不支持（Phase 2 可选功能）
4. **数组**: 暂不支持
5. **对象**: 暂不支持

## 下一步

- **Phase 2 可选功能**: break/continue, 数组, 对象
- **Phase 3**: 标准库函数（字符串、数学、文件 I/O）
- **Phase 4**: 类和对象、模块系统、异步支持

## 相关文档

- `QUICKREF.md` - 语法快速参考
- `PHASE2_COMPLETED.md` - Phase 2 完成报告
- `STACK_MECHANISM_EXPLAINED.md` - 栈机制详解
- `README_ZH.md` - 项目说明

## 反馈和贡献

如果发现问题或有建议，欢迎：
- 提交 Issue
- 创建 Pull Request
- 联系作者：dafei1288@sina.com

---

**版本**: 1.0-SNAPSHOT
**完成时间**: 2025-11-27
**状态**: ✅ Phase 2 完成 + 命令行工具
