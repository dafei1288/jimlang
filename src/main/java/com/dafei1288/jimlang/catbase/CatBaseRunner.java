package com.dafei1288.jimlang.catbase;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Entry point for running CatBase sources through the native front-end
 * ({@code CatBase.g4} + {@link CatBaseVistor}).
 */
public final class CatBaseRunner {

    private CatBaseRunner() { }

    /** Parse and interpret CatBase source. Returns the last top-level value. */
    public static Object run(String src, String sourceName) {
        CatBaseLexer lexer = new CatBaseLexer(CharStreams.fromString(src));
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CatBaseParser parser = new CatBaseParser(tokens);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        CatBaseParser.ProgContext prog;
        try {
            prog = parser.prog();
        } catch (org.antlr.v4.runtime.misc.ParseCancellationException e) {
            int line = parser.getCurrentToken() == null ? 1 : parser.getCurrentToken().getLine();
            throw new RuntimeException((sourceName == null ? "<cat>" : sourceName)
                    + ":" + line + ": syntax error: " + e.getMessage(), e);
        }
        CatBaseVistor v = new CatBaseVistor();
        v.setSourceName(sourceName);
        return v.visit(prog);
    }
}
