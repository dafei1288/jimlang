package com.dafei1288.jimlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * JimLang command line entry.
 *
 * Usage:
 *   jimlang <script.jim>        - execute a script file
 *   jimlang --cli | -i          - start interactive REPL
 *   jimlang --eval "code"        - execute a one-liner snippet
 */
public class Main {

    public static void main(String[] args) {
        // No args => print help
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        // Parse first argument as command
        for (String a : args) { if ("--trace".equals(a)) { com.dafei1288.jimlang.Trace.setEnabled(true); } }
        String command = args[0];

        // --version or -v
        if ("--version".equals(command) || "-v".equals(command)) {
            System.out.println("JimLang version 1.0-SNAPSHOT (Phase 2 Complete)");
            System.out.println("Java version: " + System.getProperty("java.version"));
            System.exit(0);
        }

        // --help or -h
        if ("--help".equals(command) || "-h".equals(command)) {
            printUsage();
            System.exit(0);
        }

        // --cli or -i => REPL
        if ("--cli".equals(command) || "-i".equals(command)) {
            Repl.main(new String[0]);
            System.exit(0);
        }

        // --eval or -e => run a snippet
        if ("--eval".equals(command) || "-e".equals(command)) {
            if (args.length < 2) {
                System.err.println("Error: --eval requires a code string");
                System.exit(2);
            }
            String code = args[1];
            JimLangShell shell = new JimLangShell();
            shell.eval(code, "<eval>", null);
            System.exit(0);
        }

        // Script path or '-' for STDIN
        String scriptPath = args[0];

        try {
            if ("-".equals(scriptPath)) {
                // read from STDIN
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String ln;
                while ((ln = br.readLine()) != null) { sb.append(ln).append("\n"); }
                JimLangShell shell = new JimLangShell();
                shell.eval(sb.toString(), "<stdin>", null);
                System.exit(0);
            }

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

    // Execute a script file
    private static void executeScript(String scriptPath) throws IOException {
        if (!Files.exists(Paths.get(scriptPath))) {
            throw new IOException("File not found: " + scriptPath);
        }

        List<String> lines = Files.readAllLines(Paths.get(scriptPath));
        String script = String.join("\n", lines);

        JimLangShell shell = new JimLangShell();
        Object result = shell.eval(script, scriptPath, null);

        if (result != null) {
            System.out.println("Result: " + result);
        }
    }

    // Print usage
    private static void printUsage() {
        System.out.println("JimLang - A Simple Programming Language");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  jimlang --cli | -i       Start interactive REPL");
        System.out.println("  jimlang --eval \"code\"   Execute a one-liner code snippet");
        System.out.println("  jimlang <script.jim>     Execute a JimLang script file");
        System.out.println("  jimlang --trace         Enable function call tracing");
        System.out.println("  jimlang --version        Show version information");
        System.out.println("  jimlang --help           Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jimlang mycode.jim       Run mycode.jim");
        System.out.println("  type code.jim | jimlang -    Run from STDIN (Windows)");
        System.out.println();
        System.out.println("For more information, visit: https://github.com/dafei1288/jimlang");
    }
}