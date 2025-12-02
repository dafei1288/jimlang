package com.dafei1288.jimlang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * JimLang 命令行入口
 *
 * 使用方式:
 *   jimlang <script.jim>        - 执行脚本文件
 *   jimlang --cli               - 启动 REPL（交互模式）
 */
public class Main {

    public static void main(String[] args) {
        // 无参数 -> 打印帮助并退出
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        // 处理命令行参数
        String command = args[0];

        // --version 或 -v
        if ("--version".equals(command) || "-v".equals(command)) {
            System.out.println("JimLang version 1.0-SNAPSHOT (Phase 2 Complete)");
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.exit(0);
        }

        // --help 或 -h
        if ("--help".equals(command) || "-h".equals(command)) {
            printUsage();
            System.exit(0);
        }

        // --cli: 启动交互式 REPL
        if ("--cli".equals(command)) {
            Repl.main(new String[0]);
            System.exit(0);
        }

        // 执行脚本文件
        String scriptPath = args[0];

        try {
            executeScript(scriptPath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + scriptPath);
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error executing script: " + scriptPath);
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 执行脚本文件
     */
    private static void executeScript(String scriptPath) throws IOException {
        // 检查文件是否存在
        if (!Files.exists(Paths.get(scriptPath))) {
            throw new IOException("File not found: " + scriptPath);
        }

        // 读取脚本内容
        List<String> lines = Files.readAllLines(Paths.get(scriptPath));
        String script = String.join("\n", lines);

        // 执行脚本
        JimLangShell shell = new JimLangShell();
        Object result = shell.eval(script, null);

        // 如果有返回值，打印结果（目前大多数语句通过 println 输出，所以这里可能为 null）
        if (result != null) {
            System.out.println("Result: " + result);
        }
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("JimLang - A Simple Programming Language");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  jimlang --cli           Start interactive REPL");
        System.out.println("  jimlang <script.jim>     Execute a JimLang script file");
        System.out.println("  jimlang --version        Show version information");
        System.out.println("  jimlang --help           Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jimlang mycode.jim       Run mycode.jim");
        System.out.println("  jimlang test.jim         Run test.jim");
        System.out.println();
        System.out.println("For more information, visit: https://github.com/dafei1288/jimlang");
    }
}