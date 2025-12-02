# JimLang 启动脚本

本目录包含 JimLang 的命令行启动脚本。

## 文件说明

- `jimlang.cmd` - Windows 批处理脚本
- `jimlang.sh` - Unix/Linux/Mac Shell 脚本

## 使用前提

1. 已安装 Java 21+
2. 已构建项目：`mvn clean package`

## 基本用法

### Windows

```cmd
REM 执行脚本
bin\jimlang.cmd mycode.jim

REM 查看版本
bin\jimlang.cmd --version

REM 查看帮助
bin\jimlang.cmd --help

REM 启动 REPL
bin\jimlang.cmd --cli
```

### Unix/Linux/Mac

```bash
# 执行脚本
bin/jimlang.sh mycode.jim

# 查看版本
bin/jimlang.sh --version

# 查看帮助
bin/jimlang.sh --help

# 启动 REPL
bin/jimlang.sh --cli
```

## 添加到 PATH（可选）

将 `bin` 目录加入系统 PATH 后，可直接使用 `jimlang` 命令。
详细说明请参考项目根目录的 `COMMAND_LINE_GUIDE.md`。

## 示例脚本

请参考 `examples/` 目录：
- `test.jim` - 基础功能测试
- `fibonacci_simple.jim` - 斐波那契数列

## 故障排除

如果看到 "JAR file not found" 错误：
```bash
cd ..
mvn clean package
```

## 更多信息

- `COMMAND_LINE_GUIDE.md` - 详细使用指南
- `QUICKREF.md` - 语法快速参考
- `README_ZH.md` - 项目介绍