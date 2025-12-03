# JimLang 寮€鍙戣鍒?(Development Roadmap)

## 椤圭洰鐜扮姸

### 宸插疄鐜?- ANTLR 4 璇硶瀹氫箟銆丩exer/Parser 鑷姩鐢熸垚
- AST Visitor 瑙ｉ噴鎵ц鍣?- 鍩虹 REPL 浜や簰寮忕幆澧冿紙淇濈暀鐘舵€併€佹敮鎸佸懡浠わ級
- JSR-223 ScriptEngine 闆嗘垚
- Maven 鏋勫缓绯荤粺

### 璇█鐗规€?- 鍙橀噺澹版槑锛歚var x = 10`
- 鍙€夌被鍨嬫敞瑙ｏ細`var name: string = "jim"`
- 鍑芥暟锛氬畾涔変笌璋冪敤锛屽弬鏁扮粦瀹氫笌杩斿洖鍊?- 鍩烘湰鏁版嵁绫诲瀷锛歯umber, string, boolean
- 杩愮畻锛氱畻鏈?+ - * / %锛涙瘮杈?> < >= <= == !=锛涘瓧绗︿覆鎷兼帴
- 鎺у埗娴侊細if/else锛寃hile锛宖or锛宐reak锛宑ontinue锛堝凡瀹屾垚锛?- 璧嬪€煎寮猴細鏀寔 `a[i] = v`銆乣obj.key = v`
- 鏁版嵁缁撴瀯锛氭暟缁?瀵硅薄 瀛楅潰閲忋€佺储寮?灞炴€ц鍐欙紝`length` 鍙

### 寰呮敼杩?缂哄け
- 浣滅敤鍩熷畬鍠勶紙鍑芥暟/鍧椾綔鐢ㄥ煙閾俱€佸彉閲?shadowing锛夈€佺被鍨嬬郴缁燂紙鍙€夛級
- 鏍囧噯搴撴墿灞曪細瀛楃涓诧紙contains/replace/...锛夈€佹暟缁勬柟娉曪紙push/pop/shift/unshift锛夈€丮ath 鎵╁睍锛坧ow/sqrt/...锛夈€佹枃浠?I/O 鎵╁睍
- 閿欒澶勭悊锛氭洿鍙嬪ソ鐨勯敊璇彁绀恒€佽鍒楀彿銆乼ry/catch锛堣鍒掞級
- 妯″潡绯荤粺銆侀棴鍖?Lambda銆佽皟璇?宸ュ叿閾撅紙瑙勫垝锛?
---

## 寮€鍙戣矾绾垮浘

### Phase 1锛氭牳蹇冧慨澶嶄笌 REPL 澧炲己锛堣繘琛屼腑锛?- 淇鍑芥暟鍙傛暟缁戝畾銆佸畬鍠勭畻鏈?姣旇緝杩愮畻
- 鍒嗗眰浣滅敤鍩熶笌绗﹀彿琛?- REPL锛氭寔涔呭寲鐘舵€併€佸懡浠ゃ€侀敊璇彁绀恒€佸惎鍔ㄦí骞?
### Phase 2锛氭帶鍒舵祦涓庢暟鎹粨鏋勶紙宸茶揪鎴愶級
- if/else銆亀hile銆乫or銆乥reak/continue
- 閾惧紡浜屽厓杩愮畻锛堝乏缁撳悎 `a + b + c`锛?- 鏁扮粍/瀵硅薄瀛楅潰閲忎笌绱㈠紩/灞炴€ц闂紱`length` 灞炴€?- 寰呭姙锛氭暟缁勬柟娉?push/pop/shift/unshift

### Phase 3锛氭爣鍑嗗簱鎵╁睍锛堣繘琛屼腑锛?- 瀛楃涓诧細len銆乼oUpperCase/toLowerCase銆乼rim銆乻ubstring銆乮ndexOf銆乻plit
- 鏁板锛歮ax/min/abs/round/random锛堝凡鍏峰锛夛紝pow/sqrt/floor/ceil/randomRange锛堝緟鍔烇級
- 鏂囦欢锛歠ile_read/file_write/file_exists锛堝凡鍏峰锛夛紝file_append锛堝緟鍔烇級

### Phase 4锛氶珮绾х壒鎬э紙瑙勫垝锛?- 闂寘銆丩ambda銆侀敊璇鐞嗭紙try/catch/finally锛夈€佹ā鍧楃郴缁燂紙import/export锛?
---

## Milestones
- 2025-11-28锛歷0.3.0 鈥?瀹屾垚 Phase 2锛堟帶鍒舵祦 + break/continue + 閾惧紡杩愮畻 + 鍩虹鏁扮粍/瀵硅薄锛