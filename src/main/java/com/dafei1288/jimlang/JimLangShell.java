package com.dafei1288.jimlang;

import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Arrays;

public class JimLangShell {
    private JimLangVistor visitor; // persistent runtime (globals/functions)

    private JimLangVistor ensureVisitor(){
        if (visitor == null) visitor = new JimLangVistor();
        return visitor;
    }

    public synchronized Object eval(String script, String sourceName, java.util.Map<String, Object> context) {
        String src = (sourceName == null ? "<script>" : sourceName);
        CharStream stream = CharStreams.fromString(script, src);
        JimLangLexer lexer = new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        lexer.removeErrorListeners();
        VerboseErrorListener vel = new VerboseErrorListener(script, src);
        parser.addErrorListener(vel);
        lexer.addErrorListener(vel);

        ParseTree parseTree = parser.prog();
        JimLangVistor v = ensureVisitor();
        v.setSourceName(src);
        v.setSourceText(script);
        try {
            // preload context variables into globals and as `ctx`
            if (context != null && !context.isEmpty()) {
                java.util.Hashtable<String, com.dafei1288.jimlang.metadata.Symbol> tbl = v.getSymbolTable();
                com.dafei1288.jimlang.metadata.SymbolVar svCtx = new com.dafei1288.jimlang.metadata.SymbolVar();
                svCtx.setName("ctx");
                svCtx.setType(com.dafei1288.jimlang.metadata.SymbolType.VAR);
                svCtx.setValue(new java.util.LinkedHashMap<>(context));
                svCtx.setAssigned(true);
                tbl.put("ctx", svCtx);
                for (java.util.Map.Entry<String,Object> e : context.entrySet()) {
                    String k = e.getKey();
                    if (k == null) continue;
                    if (!k.matches("[A-Za-z_][A-Za-z0-9_]*")) continue;
                    com.dafei1288.jimlang.metadata.SymbolVar sv = new com.dafei1288.jimlang.metadata.SymbolVar();
                    sv.setName(k);
                    sv.setType(com.dafei1288.jimlang.metadata.SymbolType.VAR);
                    sv.setValue(e.getValue());
                    sv.setAssigned(true);
                    tbl.put(k, sv);
                }
            }
            return v.visit(parseTree);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            StringBuilder sb = new StringBuilder();
            sb.append(msg == null ? "" : msg);
            java.util.List<String> st = com.dafei1288.jimlang.Trace.snapshot();
            if (st != null && !st.isEmpty() && (msg == null || msg.indexOf("Call stack:") < 0)) {
                sb.append(System.lineSeparator()).append("Call stack:");
                for (String f : st) sb.append(System.lineSeparator()).append("  at ").append(f);
            }
            com.dafei1288.jimlang.Trace.clear();
            throw new RuntimeException(sb.toString(), ex);
        }
    }

    public synchronized Object eval(String script, java.util.Map<String, Object> context) {
        return eval(script, "<script>", context);
    }

    // For JSR-223 Invocable: call a named function in the persistent runtime
    public synchronized Object invokeFunction(String name, Object... args) {
        JimLangVistor v = ensureVisitor();
        java.util.List<Object> list = (args == null) ? java.util.Collections.emptyList() : Arrays.asList(args);
        return v.callFromHost(name, list);
    }
}
