# Agent 编码与换行规范（统一格式，无乱码）

目的：彻底避免 BOM、换行符混乱与由此导致的编译/运行错误；为自动化/Agent 与人工贡献者提供一份必须遵循的操作手册。

## 统一规则
- 编码：统一使用 UTF-8（无 BOM）。
- 换行：
  - 绝大多数文本/源码：LF（\n）。
  - Windows 批处理（*.cmd/*.bat）：CRLF（\r\n）。
- 默认字符集：代码尽量使用 ASCII；中文内容仅用于文档（如本文件、README_ZH.md、QUICKREF/ROADMAP 等）。
- 不进行无意义的整文件换行转换；按 .editorconfig/.gitattributes 约束增量修改。

## 工具配置（已在仓库中）
- .editorconfig（关键节选）
```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.cmd]
end_of_line = crlf
[*.bat]
end_of_line = crlf
```
- .gitattributes（关键节选）
```
* text=auto
*.java text eol=lf
*.md   text eol=lf
*.g4   text eol=lf
*.cmd  text eol=crlf
*.bat  text eol=crlf
*.png *.jpg *.jpeg *.gif *.jar binary
```

## 正确写文件的方式（确保 UTF-8 无 BOM）
- PowerShell（推荐本项目使用此方式写文本文件）
```powershell
$enc = New-Object System.Text.UTF8Encoding($false)  # UTF-8 without BOM
[IO.File]::WriteAllText($path, $content, $enc)
```
- Java
```java
Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
```
- Node.js
```js
fs.writeFileSync(path, content, { encoding: 'utf8' }); // Node 默认无 BOM
```
- Python
```py
open(path, 'w', encoding='utf-8', newline='\n').write(content)
```

## 快速检测/修复
- 扫描仓库是否存在 BOM（U+FEFF）
```powershell
Get-ChildItem -Recurse -File | Where-Object { $_.FullName -notmatch '\\target\\' } | % {
  $b = [IO.File]::OpenRead($_.FullName); try {
    $buf = New-Object byte[] 3; $n = $b.Read($buf,0,3)
    if($n -ge 3 -and $buf[0] -eq 0xEF -and $buf[1] -eq 0xBB -and $buf[2] -eq 0xBF){ $_.FullName }
  } finally { $b.Dispose() }
}
```
- 批量去除 BOM（按扩展名过滤）
```powershell
$enc = New-Object System.Text.UTF8Encoding($false)
Get-ChildItem -Recurse -Include *.java,*.md,*.g4,*.sh,*.cmd | % {
  $s = [IO.File]::ReadAllText($_.FullName)
  [IO.File]::WriteAllText($_.FullName, $s, $enc)
}
```

## 常见报错与处理
- javac: `非法字符: '\ufeff'`（或 `illegal character: '\ufeff'`）
  - 原因：文件头含 BOM。
  - 处理：使用上文“批量去除 BOM”，或编辑器重存为 UTF-8 无 BOM。
- Git 在 Windows 上提示将 CRLF→LF：
  - 说明：由 .gitattributes 控制，文本文件统一 LF；*.cmd/*.bat 例外使用 CRLF；无需手动干预。

## 提交前自检建议
- 快速编译：`mvn -q -DskipTests compile`
- 全量测试：`mvn test`
- BOM 扫描：参考“快速检测/修复”。

## 约定补充
- 仅修改与任务相关的行，避免无关行/文件的大规模改动（尤其是换行/EOL）。
- 如需大规模规范化（如一次性去除 BOM），请独立提交并在消息中注明“格式化/规范化”原因。

（本文件由 Agent 统一维护，存储编码为 UTF-8 无 BOM；若发现乱码或与上述规范不符，请开 Issue 或直接提交修复。）