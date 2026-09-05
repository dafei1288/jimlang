# Agents Guide

本仓库支持通过智能体自动完成“从克隆到可开发”的部署与构建。

## Skills
- dev-setup: 一键部署 JimLang 开发环境并完成首次构建与冒烟校验；支持 Windows/macOS/Linux；可选 jlink 生成精简 JRE；进入 REPL 开发状态。（file: skills/dev-setup/SKILL.md）

## 使用方式
克隆仓库后，对智能体说：
- 帮我部署开发环境
- 首次构建并进入开发
- 初始化项目/开始开发

触发规则：若用户表达与上述等价的意图，使用 dev-setup 技能。

## 编码与换行规范（面向智能体与贡献者）
- 文本统一使用 UTF-8（无 BOM）；默认 LF；Windows 批处理（*.cmd/*.bat）使用 CRLF。以 .editorconfig/.gitattributes 为准。
- 避免无意义的整仓库换行/格式化改动；仅做与任务直接相关的最小修改。
- 写文本文件的推荐方式：
  - PowerShell
    ```powershell
    $enc = New-Object System.Text.UTF8Encoding($false)
    [IO.File]::WriteAllText($path, $content, $enc)
    ```
  - Java
    ```java
    Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
    ```
  - Node.js
    ```js
    fs.writeFileSync(path, content, { encoding: 'utf8' });
    ```
  - Python
    ```py
    open(path, 'w', encoding='utf-8', newline='\n').write(content)
    ```
- 终端编码建议（避免乱码）：
  - Windows PowerShell：`$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::new($false)`
  - cmd：`chcp 65001`
- 快速排查 BOM（PowerShell）：
  ```powershell
  Get-ChildItem -Recurse -File | Where-Object { $_.FullName -notmatch '\\target\\' } | % {
    $b=[IO.File]::OpenRead($_.FullName); try{ $buf=New-Object byte[] 3; $n=$b.Read($buf,0,3);
      if($n -ge 3 -and $buf[0]-eq 0xEF -and $buf[1]-eq 0xBB -and $buf[2]-eq 0xBF){ $_.FullName } } finally{ $b.Dispose() }
  }
  ```