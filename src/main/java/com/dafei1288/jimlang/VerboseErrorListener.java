package com.dafei1288.jimlang;

import org.antlr.v4.runtime.*;

/**
 * Custom ANTLR error listener to produce friendly messages with source and caret.
 */
public class VerboseErrorListener extends BaseErrorListener {
    private final String sourceName;
    private final String[] lines;

    public VerboseErrorListener(String script, String sourceName) {
        this.sourceName = (sourceName == null ? "<script>" : sourceName);
        this.lines = script == null ? new String[0] : script.split("\r?\n", -1);
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e)
    {
        String prefix = this.sourceName + ":" + line + ":" + (charPositionInLine + 1) + ": ";
        String snippet = (line >= 1 && line <= lines.length) ? lines[line-1] : "";
        StringBuilder caret = new StringBuilder();
        for (int i = 0; i < charPositionInLine && i < 200; i++) caret.append(' ');
        caret.append('^');
        String full = prefix + "syntax error: " + msg + System.lineSeparator()
                + snippet + System.lineSeparator() + caret.toString();
        throw new RuntimeException(full);
    }
}