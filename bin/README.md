# JimLang 鍚姩鑴氭湰

鏈洰褰曞寘鍚?JimLang 鐨勫懡浠よ鍚姩鑴氭湰銆?
## 鏂囦欢璇存槑

- `jimlang.cmd` - Windows 鎵瑰鐞嗚剼鏈?- `jimlang.sh` - Unix/Linux/Mac Shell 鑴氭湰

## 浣跨敤鍓嶆彁

1. 宸插畨瑁?Java 21+
2. 宸叉瀯寤洪」鐩細`mvn clean package`

## 鍩烘湰鐢ㄦ硶

### Windows

```cmd
REM 鎵ц鑴氭湰
bin\jimlang.cmd mycode.jim

REM 鏌ョ湅鐗堟湰
bin\jimlang.cmd --version

REM 鏌ョ湅甯姪
bin\jimlang.cmd --help

REM 鍚姩 REPL
bin\jimlang.cmd --cli
```

### Unix/Linux/Mac

```bash
# 鎵ц鑴氭湰
bin/jimlang.sh mycode.jim

# 鏌ョ湅鐗堟湰
bin/jimlang.sh --version

# 鏌ョ湅甯姪
bin/jimlang.sh --help

# 鍚姩 REPL
bin/jimlang.sh --cli
```

## 娣诲姞鍒?PATH锛堝彲閫夛級

灏?`bin` 鐩綍鍔犲叆绯荤粺 PATH 鍚庯紝鍙洿鎺ヤ娇鐢?`jimlang` 鍛戒护銆?璇︾粏璇存槑璇峰弬鑰冮」鐩牴鐩綍鐨?`COMMAND_LINE_GUIDE.md`銆?
## 绀轰緥鑴氭湰

璇峰弬鑰?`examples/` 鐩綍锛?- `test.jim` - 鍩虹鍔熻兘娴嬭瘯
- `fibonacci_simple.jim` - 鏂愭尝閭ｅ鏁板垪

## 鏁呴殰鎺掗櫎

濡傛灉鐪嬪埌 "JAR file not found" 閿欒锛?```bash
cd ..
mvn clean package
```

## 鏇村淇℃伅

- `COMMAND_LINE_GUIDE.md` - 璇︾粏浣跨敤鎸囧崡
- `QUICKREF.md` - 璇硶蹇€熷弬鑰?- `README_ZH.md` - 椤圭洰浠嬬粛

# 新增 CLI 用法

- 启动 REPL：`jimlang --cli` 或 `jimlang -i`
- 执行一行代码：`jimlang --eval "println(\"hi\")"`
- 从 STDIN 读取：
  - Windows：`type code.jim | jimlang -`
  - Unix：`cat code.jim | jimlang -`