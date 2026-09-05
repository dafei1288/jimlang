package com.dafei1288.jimlang.lsp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import com.dafei1288.jimlang.parser.*;
import org.eclipse.lsp4j.*;

public class SymbolIndex {
    public static final class Def {
        public final String name; public final String uri; public final Range range; public final Range selectionRange;
        public Def(String name, String uri, Range range, Range selectionRange) { this.name=name; this.uri=uri; this.range=range; this.selectionRange=selectionRange; }
    }

    private final Map<String, List<Def>> defsByName = new HashMap<>();
    private final Map<String, List<Def>> defsByUri = new HashMap<>();
    private final Map<String, String> texts = new HashMap<>();

    public synchronized void indexDocument(String uri, String text) {
        if (text == null) text = "";
        texts.put(uri, text);
        List<Symbols.Fn> fns = Symbols.findFunctions(text);
        // remove old defs from this uri
        List<Def> old = defsByUri.remove(uri);
        if (old != null) {
            for (Def d : old) {
                List<Def> list = defsByName.get(d.name);
                if (list != null) { list.removeIf(x -> x.uri.equals(uri) && x.selectionRange.getStart().equals(d.selectionRange.getStart())); if (list.isEmpty()) defsByName.remove(d.name); }
            }
        }
        List<Def> fresh = new ArrayList<>();
        for (Symbols.Fn f : fns) {
            Def d = new Def(f.name, uri, f.range, f.selectionRange);
            fresh.add(d);
            defsByName.computeIfAbsent(f.name, k -> new ArrayList<>()).add(d);
        }
        defsByUri.put(uri, fresh);
    }

    public synchronized void removeDocument(String uri) {
        texts.remove(uri);
        List<Def> old = defsByUri.remove(uri);
        if (old != null) {
            for (Def d : old) {
                List<Def> list = defsByName.get(d.name);
                if (list != null) { list.removeIf(x -> x.uri.equals(uri) && x.selectionRange.getStart().equals(d.selectionRange.getStart())); if (list.isEmpty()) defsByName.remove(d.name); }
            }
        }
    }

    public synchronized List<Def> findDefinitions(String name) {
        List<Def> list = defsByName.get(name);
        if (list == null) return java.util.Collections.emptyList();
        return new ArrayList<>(list);
    }

    public synchronized Map<String,String> snapshotTexts() { return new HashMap<>(texts); }

    public void scanWorkspaceFolders(java.util.List<WorkspaceFolder> folders) {
        if (folders == null) return;
        for (WorkspaceFolder wf : folders) {
            try {
                java.net.URI u = new java.net.URI(wf.getUri());
                if (!"file".equalsIgnoreCase(u.getScheme())) continue;
                Path root = Paths.get(u);
                scanRoot(root);
            } catch (Exception ignore) {}
        }
    }

    private void scanRoot(Path root) {
        if (root == null || !Files.isDirectory(root)) return;
        try {
            Files.walk(root).filter(p -> {
                String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                return n.endsWith(".jim") || n.endsWith(".mylang");
            }).forEach(p -> {
                try {
                    String text = Files.readString(p, StandardCharsets.UTF_8);
                    String uri = p.toUri().toString();
                    indexDocument(uri, text);
                } catch (IOException ignore) {}
            });
        } catch (IOException ignore) {}
    }

    public List<Location> references(String name, boolean includeDeclaration) {
        List<Location> out = new ArrayList<>();
        Map<String,String> snap = snapshotTexts();
        for (Map.Entry<String,String> e : snap.entrySet()) {
            String uri = e.getKey(); String text = e.getValue(); if (text == null) continue;
            try {
                CharStream cs = CharStreams.fromString(text);
                JimLangLexer lexer = new JimLangLexer(cs);
                CommonTokenStream stream = new CommonTokenStream(lexer);
                stream.fill();
                List<Token> tokens = stream.getTokens();
                for (int i=0;i<tokens.size();i++) {
                    Token t = tokens.get(i);
                    String sym = JimLangLexer.VOCABULARY.getSymbolicName(t.getType());
                    if (!"ID".equals(sym)) continue;
                    String tx = t.getText(); if (tx == null) continue;
                    if (!tx.equals(name)) continue;
                    Token prev = prevDefault(tokens, i);
                    String prevSym = prev==null? null : JimLangLexer.VOCABULARY.getSymbolicName(prev.getType());
                    boolean isDef = "FUNCTION".equals(prevSym);
                    if (!includeDeclaration && isDef) continue;
                    int line = Math.max(0, t.getLine()-1); int col = Math.max(0, t.getCharPositionInLine());
                    Location loc = new Location();
                    loc.setUri(uri);
                    loc.setRange(new Range(new Position(line, col), new Position(line, col + tx.length())));
                    out.add(loc);
                }
            } catch (Exception ignore) {}
        }
        return out;
    }

    private static Token prevDefault(List<Token> tokens, int idx) {
        for (int k=idx-1;k>=0;k--) { Token tt = tokens.get(k); if (tt.getChannel()==Token.DEFAULT_CHANNEL) return tt; }
        return null;
    }
}