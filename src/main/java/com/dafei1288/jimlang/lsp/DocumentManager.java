package com.dafei1288.jimlang.lsp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.antlr.v4.runtime.*;
import org.eclipse.lsp4j.*;

import com.dafei1288.jimlang.parser.*;

/**
 * Maintains document texts and runs parse to produce diagnostics.
 */
public class DocumentManager {
    private final Map<String, String> docs = new ConcurrentHashMap<>();

    public void open(String uri, String text) {
        if (text == null) text = "";
        docs.put(uri, text);
    }

    public void changeFull(String uri, String text) {
        if (text == null) text = "";
        docs.put(uri, text);
    }

    public void close(String uri) {
        docs.remove(uri);
    }

    public String getText(String uri) {
        return docs.getOrDefault(uri, "");
    }

    public java.util.List<Diagnostic> analyze(String uri) {
        Log.info("analyze start: " + uri);
        String text = getText(uri);
        java.util.List<Diagnostic> diags = new java.util.ArrayList<>();
        try {
            CharStream cs = CharStreams.fromString(text);
            JimLangLexer lexer = new JimLangLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JimLangParser parser = new JimLangParser(tokens);

            BaseErrorListener listener = new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    int startLine = Math.max(0, line - 1);
                    int startChar = Math.max(0, charPositionInLine);
                    int endChar = startChar + 1;
                    if (offendingSymbol instanceof Token) {
                        Token t = (Token) offendingSymbol;
                        if (t.getText() != null && !t.getText().isEmpty()) {
                            endChar = startChar + t.getText().length();
                        }
                    }
                    Range range = new Range(new Position(startLine, startChar), new Position(startLine, endChar));
                    Diagnostic d = new Diagnostic();
                    d.setRange(range);
                    d.setSeverity(DiagnosticSeverity.Error);
                    d.setSource("jimlang");
                    d.setMessage("syntax error: " + msg);
                    diags.add(d);
                }
            };
            lexer.removeErrorListeners();
            parser.removeErrorListeners();
            lexer.addErrorListener(listener);
            parser.addErrorListener(listener);

            parser.prog();
        } catch (Exception ex) {
            Diagnostic d = new Diagnostic();
            d.setRange(new Range(new Position(0, 0), new Position(0, 1)));
            d.setSeverity(DiagnosticSeverity.Error);
            d.setSource("jimlang");
            d.setMessage("internal error: " + ex.getMessage());
            diags.add(d);
        }
        Log.info("analyze done: " + uri + " diagnostics=" + diags.size());
        return diags;
    }
}