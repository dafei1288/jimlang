package com.dafei1288.jimlang;


import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ConsoleErrorListener;

import java.util.Map;

public class JimLangShell {

    public Object eval(String script, String sourceName, java.util.Map<String, Object> context) {
    // Attach custom error listener for better diagnostics
    CharStream stream = CharStreams.fromString(script, sourceName == null ? "<script>" : sourceName);
    JimLangLexer lexer = new JimLangLexer(stream);
    JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    lexer.removeErrorListeners();
    VerboseErrorListener vel = new VerboseErrorListener(script, sourceName);
    parser.addErrorListener(vel);
    lexer.addErrorListener(vel);

    ParseTree parseTree = parser.prog();
    JimLangVistor mlvistor = new JimLangVistor();
    try {
        return mlvistor.visit(parseTree);
    } catch (RuntimeException ex) {
        String sn = (sourceName==null?"<script>":sourceName);
        String msg = ex.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append(sn).append(": runtime error: ").append(msg==null?"":msg);
        java.util.List<String> st = com.dafei1288.jimlang.Trace.snapshot();
        if (st != null && !st.isEmpty()) {
            sb.append(System.lineSeparator()).append("Call stack:");
            for (String f : st) sb.append(System.lineSeparator()).append("  at ").append(f);
        }
        com.dafei1288.jimlang.Trace.clear();
        throw new RuntimeException(sb.toString(), ex);
    }
}

        public Object eval(String script, java.util.Map<String, Object> context) {
        return eval(script, "<script>", context);
    }
}