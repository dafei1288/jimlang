@echo off
java -Dstdout.encoding=UTF-8 -cp "%~dp0..\target\jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar" com.dafei1288.jimlang.catbase.CatBaseCompat %*
