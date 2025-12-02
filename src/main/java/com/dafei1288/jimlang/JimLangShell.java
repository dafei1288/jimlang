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
        // remove default console error listener to avoid noisy stderr
        parser.removeErrorListeners();
        lexer.removeErrorListeners();
        VerboseErrorListener vel = new VerboseErrorListener(script, sourceName);
        parser.addErrorListener(vel);
        lexer.addErrorListener(vel);

        ParseTree parseTree = parser.prog();
        JimLangVistor mlvistor = new JimLangVistor();
        try { return mlvistor.visit(parseTree); } catch (RuntimeException ex) { String sn = (sourceName==null?"<script>":sourceName); String msg = ex.getMessage(); if (msg==null || !msg.startsWith(sn+":")) { throw new RuntimeException(sn+": runtime error: "+msg, ex); } throw ex; }
    }

        public Object eval(String script, java.util.Map<String, Object> context) {
        return eval(script, "<script>", context);
    }
}