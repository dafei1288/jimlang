# Agent 开发/自动化约定（编码与换行）

为避免 UTF‑8 BOM 与换行符导致的构建/运行问题，统一如下规则与脚本约定。

## 规范
- 编码：统一使用 UTF‑8（无 BOM）。
- 默认字符集：代码文件尽量使用 ASCII；文档可使用 UTF‑8 中文等，但仍需无 BOM。
- 换行：保持与现有文件一致（不要无意义地整体转换）。新文件优先 `LF`（Unix），Windows 批处理脚本 `.cmd` 使用系统默认 `CRLF` 即可。

## 在脚本/工具里如何写文件（必须显式无 BOM）

- PowerShell（推荐在本项目中一律用这个方式写入文本文件）：
```powershell
$enc = New-Object System.Text.UTF8Encoding($false)  # UTF-8 without BOM
[IO.File]::WriteAllText($path, $content, $enc)
```

- Java：
```java
Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
```

- Node.js：
```js
fs.writeFileSync(path, content, { encoding: 'utf8' }); // Node 默认无 BOM
```

- Python：
```py
open(path, 'w', encoding='utf-8', newline='\n').write(content)
```

## 快速排查/去除 BOM

- 单文件（PowerShell）：
```powershell
$enc = New-Object System.Text.UTF8Encoding($false)
$s = [IO.File]::ReadAllText($f)
[IO.File]::WriteAllText($f, $s, $enc)
```

- 批量去除所有 .java/.md/.sh/.cmd 的 BOM（在仓库根目录执行）：
```powershell
$enc = New-Object System.Text.UTF8Encoding($false)
Get-ChildItem -Recurse -Include *.java,*.md,*.sh,*.cmd |
  ForEach-Object {
    $s = [IO.File]::ReadAllText($_.FullName)
    [IO.File]::WriteAllText($_.FullName, $s, $enc)
  }
```

- 识别 BOM（首字符 U+FEFF）：
```powershell
Get-ChildItem -Recurse -Include *.java,*.md | % {
  $bytes = [IO.File]::ReadAllBytes($_.FullName)
  if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
    Write-Output $_.FullName
  }
}
```

## Maven/编译提示
- 项目已设置 `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`，但 `javac` 遇到 BOM 仍可能报错：
  - `非法字符: '\ufeff'`（或 `illegal character: '\ufeff'`）
- 一旦出现上述错误，按“快速排查/去除 BOM”处理。

## 推荐的编辑器配置（可选）
- `.editorconfig`（建议后续添加到仓库）：
```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.cmd]
end_of_line = crlf
```
- `.gitattributes`（建议后续添加到仓库，避免跨平台换行抖动）：
```
* text=auto
*.sh  text eol=lf
*.java text eol=lf
*.md  text eol=lf
*.cmd text eol=crlf
```

## 提交前自检（建议）
- 快速编译（跳过测试）：`mvn -q -DskipTests compile`
- 全量测试：`mvn test`
- 若在 Windows 上批量创建/修改了文件，建议运行一次“批量去除 BOM”。

—— 本文档面向自动化/Agent 与贡献者，严格遵循可避免再次出现 BOM/换行相关问题。