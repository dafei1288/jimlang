# JimLang 寮€鍙戣矾绾垮浘 (Development Roadmap)

## 姒傝
- 鐩爣锛氬疄鐜颁竴涓槗浜庝笂鎵嬨€佸彲鎵╁睍鐨勮剼鏈瑷€锛屽叿澶囨竻鏅扮殑璇硶銆佸彲宓屽叆鐨勮繍琛屾椂涓庡疄鐢ㄦ爣鍑嗗簱銆?- 骞冲彴锛欽ava 21锛孉NTLR4 璇硶涓庤В鏋愶紝Maven 鏋勫缓銆?- 缂栫爜锛歎TF-8锛堟棤 BOM锛夛紱.cmd 浣跨敤 CRLF锛屽叾浣欎娇鐢?LF锛堢敱 .editorconfig/.gitattributes 绠℃帶锛夈€?
## 闃舵鐩爣

### Phase 1锛堝熀纭€璇硶涓庢墽琛岋級
- 璇硶/瑙ｆ瀽锛欰NTLR4 璇嶆硶/璇硶锛孉ST Visitor銆?- 鎵ц鏂瑰紡锛歊EPL銆佷粠鏂囦欢鎵ц銆丣SR-223 ScriptEngine锛堝彲宓屽叆锛夈€?- 琛ㄨ揪寮忎笌杩愮畻锛氭暟瀛?瀛楃涓?甯冨皵锛屽熀鏈簩鍏冭繍绠楋紝瀛楃涓叉嫾鎺ワ紝鍙€夊垎鍙枫€?- 鍑芥暟锛氬嚱鏁板畾涔?璋冪敤锛屽弬鏁颁紶閫掍笌杩斿洖鍊笺€?
### Phase 2锛堟祦绋嬫帶鍒朵笌瀹瑰櫒锛?- 鏉′欢涓庡惊鐜細if/else銆亀hile銆乫or銆乥reak銆乧ontinue銆?- 鏁扮粍涓庡璞″瓧闈㈤噺锛氱储寮?灞炴€ц闂紱鍐呯疆灞炴€?length銆?- 閿欒淇℃伅锛氭洿鍙嬪ソ鐨勮繍琛屾椂閿欒鎻愮ず銆?
### Phase 3锛堟爣鍑嗗簱鎵╁睍锛?- 瀛楃涓诧細split/join/substring/indexOf/toUpperCase/toLowerCase/trim銆?- 鏁扮粍锛歱ush/pop/shift/unshift/length 鍙婄浉鍏冲伐鍏枫€?- 鏁板锛歛bs/round/floor/ceil/pow/sqrt/random/randomRange 绛夈€?- I/O锛歠ile_read/file_write/file_exists/file_append銆?- 鍏朵粬鍐呯疆锛歬eys/values/typeof/isArray/isObject/parseInt/parseFloat銆?
### Phase 4锛堣繘闃惰兘鍔涳細瑙勫垝涓級
- 寮傚父锛歵ry/catch/finally銆?- 妯″潡锛歩mport/export銆?- 鍑芥暟寮忥細lambda/闂寘銆?
## 褰撳墠鐘舵€侊紙宸插畬鎴愶級
- CLI/REPL锛?  - --cli/-i 杩涘叆浜や簰寮?REPL锛?-eval/-e 鎵ц鍗曡锛?-" 浠庢爣鍑嗚緭鍏ヨ鍙栵紱閫夐」浼樺厛瑙ｆ瀽銆?  - --trace 鍚敤璋冪敤璺熻釜锛堜篃鏀寔鐜鍙橀噺 JIM_TRACE=1锛夈€?- 杩愯鏃惰窡韪笌閿欒锛?  - Trace.push/pop锛涘綋鍚敤璺熻釜鏃舵墦鍗?enter/leave銆?  - 杩愯鏃堕敊璇寘鍚?婧愬悕:琛?鍒?+ caret 鎸囩ず + 璋冪敤鏍堬紙Call stack锛夈€?- 浣滅敤鍩燂細
  - 鍑芥暟浣滅敤鍩熶笌鍧椾綔鐢ㄥ煙鍧囧凡瀹炵幇锛涘彉閲忛伄钄界敓鏁堬紱绠€鍗曡祴鍊兼洿鏂版渶杩戜綔鐢ㄥ煙涓殑宸叉湁鍙橀噺銆?- 绫诲瀷绯荤粺锛?  - 鏀寔绫诲瀷鏍囨敞锛歴tring/number/boolean/int/float/array/object 浠ュ強 Java FQCN锛?  - 瀹藉寲瑙勫垯锛歩nt 鍙祴缁?float锛沜oerce 琛屼负涓庣被鍨嬫鏌ラ泦鎴愪簬鍙橀噺澹版槑/璧嬪€笺€?- 鏍囧噯搴擄細
  - 瀛楃涓层€佹暟缁勩€佹暟瀛︺€佹枃浠?I/O 绛夊父鐢ㄥ嚱鏁帮紱鏂板 join/keys/values/typeof/isArray/isObject/parseInt/parseFloat銆?
## TODO锛堜紭鍏堢骇楂樷啋浣庯級
- 浣滅敤鍩?鍑芥暟锛?  - 闂寘涓庢崟鑾峰閮ㄥ彉閲忥紱鍑芥暟鍊间綔涓轰竴绛夊叕姘戙€?  - let/const 璇箟涓庝笉鍙彉缁戝畾锛堣璁′笌瀹炵幇锛夈€?- 绫诲瀷绯荤粺锛?  - 鏇寸粏鐨勭被鍨嬭鍒欎笌閿欒淇℃伅锛涘彲閫夌殑绫诲瀷鎺ㄦ柇锛涙暟缁?瀵硅薄鐨勫厓绱犵被鍨嬬害鏉燂紙娉涘瀷鍖栵級銆?- CLI 涓庡彲鐢ㄦ€э細
  - 閫€鍑虹爜瑙勮寖锛?-trace 涓?--eval 缁勫悎鍦烘櫙鐨勬洿澶氭祴璇曪紱甯姪淇℃伅涓庣ず渚嬪畬鍠勩€?- 杩愯鏃讹細
  - return 鍦ㄤ换鎰忓祵濂楄鍙ュ唴鐨勬棭杩斿洖澶勭悊鏇村姞鍋ュ．銆?- 鏂囨。锛?  - 蹇€熶笂鎵嬩笌绀轰緥瀹屽杽锛涢敊璇俊鎭牸寮忚鏄庯紱缂栫爜涓庢崲琛岃鑼冩暣鍚堬紙agent.md锛夈€?
## 缂栫爜/鎹㈣瑙勮寖锛堟憳瑕侊級
- 鎵€鏈夋簮鐮?鏂囨。浣跨敤 UTF-8 鏃?BOM锛?- .cmd/.bat 浣跨敤 CRLF锛屽叾浣欐枃鏈娇鐢?LF锛?- 浣跨敤 .editorconfig/.gitattributes 缁熶竴椋庢牸锛汸owerShell 鍐欐枃鏈缓璁細
  - `$enc = New-Object System.Text.UTF8Encoding($false)`
  - `[IO.File]::WriteAllText(path, content, $enc)`