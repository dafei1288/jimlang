package com.dafei1288.jimlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JimLang command line entry.
 */
public class Main {

    public static void main(String[] args) {
        boolean trace = false;
        boolean showHelp = false;
        boolean showVersion = false;
        boolean interactive = false;
        String evalCode = null;
        String scriptPath = null;

        // Pre-scan all args so flags can appear anywhere
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if ("--trace".equals(a)) { trace = true; continue; }
            if ("--help".equals(a) || "-h".equals(a)) { showHelp = true; continue; }
            if ("--version".equals(a) || "-v".equals(a)) { showVersion = true; continue; }
            if ("--cli".equals(a) || "-i".equals(a)) { interactive = true; continue; }
            if ("--eval".equals(a) || "-e".equals(a)) {
                if (i + 1 >= args.length) { System.err.println("Error: --eval requires a code string"); System.exit(2); }
                evalCode = args[++i];
                continue;
            }
            if ("-".equals(a)) { if (scriptPath == null) scriptPath = a; continue; } if (a.startsWith("-")) { continue; }
            if (scriptPath == null) scriptPath = a; // first non-flag argument as script
        }

        if (trace) { com.dafei1288.jimlang.Trace.setEnabled(true); }
        if (showVersion) { System.out.println("JimLang version 1.0-SNAPSHOT"); System.out.println("Java version: " + System.getProperty("java.version")); return; }
        if (showHelp || (args.length == 0 && evalCode == null && scriptPath == null && !interactive)) { printUsage(); return; }
        if (interactive) { Repl.main(new String[0]); return; }
        if (evalCode != null) { JimLangShell shell = new JimLangShell(); shell.eval(evalCode, "<eval>", null); return; }
        if (scriptPath == null) { printUsage(); return; }

        try {
            if ("-".equals(scriptPath)) {
                // read from STDIN
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String ln;
                while ((ln = br.readLine()) != null) { sb.append(ln).append("\n"); }
                JimLangShell shell = new JimLangShell();
                shell.eval(sb.toString(), "<stdin>", null);
                return;
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

    private static void printUsage() {
        System.out.println("JimLang - A Simple Programming Language");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  jimlang --cli | -i       Start interactive REPL");
        System.out.println("  jimlang --eval \"code\"   Execute a one-liner code snippet");
        System.out.println("  jimlang <script.jim>     Execute a JimLang script file");
        System.out.println("  --trace                  Enable function call tracing");
        System.out.println("  --version, -v            Show version information");
        System.out.println("  --help, -h               Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jimlang mycode.jim       Run mycode.jim");
        System.out.println("  type code.jim | jimlang -    Run from STDIN (Windows)");
        System.out.println();
        System.out.println("Flags can appear before or after the script path. e.g. jimlang --trace examples/foo.jim");
    }
}