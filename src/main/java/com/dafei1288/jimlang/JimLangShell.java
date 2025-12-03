package com.dafei1288.jimlang;

import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class JimLangShell {
    public Object eval(String script, String sourceName, java.util.Map<String, Object> context) {
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
        JimLangVistor visitor = new JimLangVistor();
        visitor.setSourceName(src);
        visitor.setSourceText(script);
        try {
            return visitor.visit(parseTree);
        } catch (RuntimeException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(src).append(": runtime error: ").append(ex.getMessage() == null ? "" : ex.getMessage());
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