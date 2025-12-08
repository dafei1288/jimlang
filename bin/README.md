# JimLang 启动脚本 / Launchers

本目录包含跨平台启动脚本：
- Windows: `bin\jimlang.cmd`
- Unix/Linux/Mac: `bin/jimlang.sh`

使用 / Usage
- 运行脚本文件: `jimlang path/to/script.jim`
- 启动 REPL: `jimlang --cli` 或 `jimlang -i`
- 执行一行代码: `jimlang --eval "println(\"hi\")"`
- 从标准输入读取:
  - Windows: `type code.jim | jimlang -`
  - Unix: `cat code.jim | jimlang -`

构建 / Build
- 需要 Java 21+
- 在项目根执行: `mvn clean package`

外部依赖 /ext 目录
- 将第三方 JAR 放入项目根的 `ext/` 目录，启动脚本会自动加入到 `CLASSPATH`。
- 例如：
  - JimSQL 驱动: `ext/your-jimsql-driver.jar`（URL 示例 `jdbc:jimsql://localhost:8821/test`，驱动类 `com.dafei1288.jimsql.jdbc.JqDriver`）
  - 连接池: `ext/HikariCP-*.jar`
  - 其他: `ext/*.jar`

注意事项 / Notes
- 文档与脚本统一采用 UTF-8（无 BOM）。请确保你的编辑器以 UTF-8 保存，以避免出现乱码。
- Windows 下 `.cmd`、Unix 下 `.sh` 已支持 `/ext` 类路径；主程序通过 `-cp` 方式启动（非 `-jar`），以便加载外部 JAR。