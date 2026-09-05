package com.dafei1288.jimlang.lsp;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import com.dafei1288.jimlang.parser.*;

public final class Symbols {
    private Symbols() {}

    public static final class Fn {
        public final String name;
        public final Range range;
        public final Range selectionRange;
        public Fn(String name, Range range, Range selectionRange) {
            this.name = name; this.range = range; this.selectionRange = selectionRange;
        }
    }

    public static List<Fn> findFunctions(String text) {
        List<Fn> out = new ArrayList<>();
        if (text == null) return out;
        JimLangParser.ProgContext tree = parse(text);
        if (tree == null) return out;
        new JimLangBaseVisitor<Void>() {
            @Override public Void visitFunctionDecl(JimLangParser.FunctionDeclContext ctx) {
                String name = ctx.identifier().getText();
                Token start = ctx.getStart();
                Token stop = ctx.getStop();
                Range r = range(start, stop);
                Token idTok = ctx.identifier().start;
                Range sel = range(idTok, idTok);
                out.add(new Fn(name, r, sel));
                return super.visitFunctionDecl(ctx);
            }
        }.visit(tree);
        return out;
    }

    public static List<Either<SymbolInformation, DocumentSymbol>> documentSymbols(String text) {
        List<Fn> fns = findFunctions(text);
        List<Either<SymbolInformation, DocumentSymbol>> list = new ArrayList<>();
        for (Fn f : fns) {
            DocumentSymbol ds = new DocumentSymbol();
            ds.setName(f.name);
            ds.setKind(SymbolKind.Function);
            ds.setRange(f.range);
            ds.setSelectionRange(f.selectionRange);
            list.add(Either.forRight(ds));
        }
        return list;
    }

    public static Optional<String> wordAt(String text, int line0, int ch0) {
        if (text == null) return Optional.empty();
        String[] lines = text.split("\r?\n", -1);
        if (line0 < 0 || line0 >= lines.length) return Optional.empty();
        String line = lines[line0];
        if (ch0 < 0) ch0 = 0; if (ch0 > line.length()) ch0 = line.length();
        int l = ch0, r = ch0;
        while (l > 0 && isId(line.charAt(l-1))) l--;
        while (r < line.length() && isId(line.charAt(r))) r++;
        if (l == r) return Optional.empty();
        return Optional.of(line.substring(l, r));
    }

    private static boolean isId(char c) { return Character.isLetterOrDigit(c) || c == '_' || c == '$'; }

    public static Range range(Token start, Token stop) {
        int sl = Math.max(0, start.getLine()-1);
        int sc = Math.max(0, start.getCharPositionInLine());
        int el = Math.max(0, stop.getLine()-1);
        int ec = Math.max(0, stop.getCharPositionInLine());
        String text = stop.getText();
        if (text != null) ec += text.length();
        return new Range(new Position(sl, sc), new Position(el, ec));
    }

    private static JimLangParser.ProgContext parse(String text) {
        try {
            CharStream cs = CharStreams.fromString(text);
            JimLangLexer lexer = new JimLangLexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JimLangParser parser = new JimLangParser(tokens);
            parser.removeErrorListeners();
            lexer.removeErrorListeners();
            return parser.prog();
        } catch (Exception e) {
            return null;
        }
    }
}