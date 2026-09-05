---
name: dev-setup
description: |
  在一个新克隆的 JimLang 仓库中，自动完成开发环境部署与首次构建，
  并进行冒烟验证与进入 REPL（开发状态）。支持 Windows/macOS/Linux；
  可选使用 jlink 生成精简 JRE。触发词示例：
  “帮我部署开发环境/首次构建/进入开发状态/初始化项目/开始开发/构建项目”。
---

目标
- 检查工具链（JDK 21+、Maven 3.8+）
- 完成 mvn package（默认跳过测试）
- 冒烟执行（eval 一行代码 + 跑一个示例脚本）
- 启动 REPL，进入开发状态
- 可选（Windows）：build_win.bat jlink 精简 JRE
- 可选（Linux/WSL）：mkos.sh 生成最小系统并在 QEMU 运行

权限与审批
- 需要写入：target/、build/、out/（构建与打包）
- 需要联网：首次下载 Maven 依赖
- 需要 sudo（仅 Linux/WSL 做 mkos 时）
- 如受沙箱/网络限制导致失败，请向用户请求批准后再继续；不要私自更改仓库文件

前置检查（所有平台）
1) 仓库根目录存在 pom.xml；bin/jimlang.*；ext/*.jar（包含 jdbc-1.0.0-SNAPSHOT.jar）
2) 版本：
   - java -version 需要 >= 21
   - mvn -v 需要 >= 3.8
3) 若缺失：提示安装（Windows: winget/choco；macOS: brew；Linux: apt/yum），征用户批准后执行

构建（默认）
- 在仓库根执行：
  - mvn -q -DskipTests package
- 失败排查：
  - 依赖下载失败：检查网络/代理（~/.m2/settings.xml）
  - 语法生成失败：mvn -X package 输出关键错误段落并等待用户处理

冒烟验证
- Windows:
  - bin\jimlang.cmd --eval "println(1+2)"
  - bin\jimlang.cmd examples\fibonacci.jim
- macOS/Linux:
  - bin/jimlang.sh --eval "println(1+2)"
  - bin/jimlang.sh examples/fibonacci.jim
- 期望：输出 3 与 Fibonacci 结果

进入开发（REPL）
- Windows: bin\jimlang.cmd --cli
- macOS/Linux: bin/jimlang.sh --cli
- 退出：Ctrl+C

可选步骤
- Windows jlink：
  - 需要 JAVA_HOME 指向 JDK 21（包含 jdeps/jlink）
  - 执行 build_win.bat
  - 支持环境变量：WITH_WEB=1、WITH_SQL=1、EXTRA_MODULES=foo,bar
- Linux/WSL mkos：
  - 依赖 busybox、qemu-system-x86_64、cpio、gzip、ldd
  - 执行 bash ./mkos.sh（可能需要 sudo）

IDE 集成
- VS Code：打开 jimlang.code-workspace，安装 Extension Pack for Java，JDK 选择 21；运行/调试 Main 或测试
- IntelliJ IDEA：Open pom.xml；Project SDK 选 21；运行 Main 或添加 --cli 参数

编码与换行
- 写控制台日志使用 UTF-8；避免 BOM
- Windows 终端建议：
  - PowerShell 会话设置：$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)
  - cmd：chcp 65001
- 本技能本身不修改仓库源文件，仅产出构建产物目录

成功判定（向用户汇报）
- Java/Maven 版本满足要求
- 生成 target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar
- 两项冒烟通过
- 已进入 REPL 或提示进入方式
- 可选项（jlink/mkos）按需完成