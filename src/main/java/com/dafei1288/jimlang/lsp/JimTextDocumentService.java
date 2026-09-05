package com.dafei1288.jimlang.lsp;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.antlr.v4.runtime.*;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

public class JimTextDocumentService implements TextDocumentService {
    private final DocumentManager docs;
    private final SymbolIndex index;
    private org.eclipse.lsp4j.services.LanguageClient client;

    // Legend used by server; keep in sync with JimLanguageServer
    private static final java.util.List<String> SEM_TYPES = java.util.Arrays.asList(
        "namespace","type","function","variable","property","keyword","comment","string","number","operator");
    private static final java.util.List<String> SEM_MODS = java.util.Arrays.asList("declaration","defaultLibrary");

    public JimTextDocumentService(DocumentManager docs, SymbolIndex index) { this.docs = docs; this.index = index; }
    public void connect(org.eclipse.lsp4j.services.LanguageClient client) { this.client = client; }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        docs.open(uri, text);
        index.indexDocument(uri, text);
        publish(uri);
        Log.info("didOpen: " + uri + " len=" + (text==null?0:text.length()));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        java.util.List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
        String text = docs.getText(uri);
        if (changes != null && !changes.isEmpty()) {
            TextDocumentContentChangeEvent last = changes.get(changes.size() - 1);
            if (last.getText() != null) text = last.getText();
        }
        docs.changeFull(uri, text);
        index.indexDocument(uri, text);
        publish(uri);
        Log.info("didChange: " + uri + " changes=" + (changes==null?0:changes.size()));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        docs.close(uri);
        // keep index text to enable cross-file refs even when closed
        if (client != null) client.publishDiagnostics(new PublishDiagnosticsParams(uri, java.util.Collections.emptyList()));
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) { /* no-op */ }

    private void publish(String uri) {
        if (client == null) return;
        java.util.List<Diagnostic> diags = docs.analyze(uri);
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diags));
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        Log.info("completion at " + params.getTextDocument().getUri() + " pos=" + params.getPosition().getLine() + ":" + params.getPosition().getCharacter());
        java.util.List<CompletionItem> items = new java.util.ArrayList<>();
        String[] kws = new String[] {"function","var","let","const","return","if","else","while","for","break","continue","print","println","true","false"};
        for (String kw : kws) { CompletionItem ci = new CompletionItem(kw); ci.setKind(CompletionItemKind.Keyword); items.add(ci); }
        CompletionList list = new CompletionList(false, items);
        return java.util.concurrent.CompletableFuture.completedFuture(Either.forRight(list));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        int line = params.getPosition().getLine();
        int ch = params.getPosition().getCharacter();
        Optional<String> w = Symbols.wordAt(text, line, ch);
        if (w.isEmpty()) return CompletableFuture.completedFuture(null);
        String word = w.get();
        String info;
        switch (word) {
            case "function": case "var": case "let": case "const": case "return":
            case "if": case "else": case "while": case "for": case "break": case "continue": case "new":
                info = "keyword: " + word; break;
            case "true": case "false": info = "boolean literal: " + word; break;
            case "print": case "println": info = "builtin function: " + word; break;
            default:
                boolean match = Symbols.findFunctions(text).stream().anyMatch(f -> f.name.equals(word));
                info = match ? ("function " + word) : word;
        }
        MarkupContent mc = new MarkupContent(); mc.setKind(MarkupKind.PLAINTEXT); mc.setValue(info);
        Hover h = new Hover(mc);
        int start = Math.max(0, ch - word.length());
        h.setRange(new Range(new Position(line, start), new Position(line, ch)));
        return CompletableFuture.completedFuture(h);
    }

    @Override
    public CompletableFuture<java.util.List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        return CompletableFuture.completedFuture(Symbols.documentSymbols(text));
    }

    @Override
    public java.util.concurrent.CompletableFuture<org.eclipse.lsp4j.jsonrpc.messages.Either<java.util.List<? extends Location>, java.util.List<? extends LocationLink>>> definition(DefinitionParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        Position pos = params.getPosition();
        java.util.Optional<String> w = Symbols.wordAt(text, pos.getLine(), pos.getCharacter());
        if (w.isEmpty()) {
            return java.util.concurrent.CompletableFuture.completedFuture(org.eclipse.lsp4j.jsonrpc.messages.Either.forLeft(java.util.Collections.emptyList()));
        }
        String word = w.get();
        java.util.List<SymbolIndex.Def> defs = index.findDefinitions(word);
        java.util.List<Location> locs = new java.util.ArrayList<>();
        if (defs.isEmpty()) {
            for (Symbols.Fn f : Symbols.findFunctions(text)) {
                if (f.name.equals(word)) {
                    Location l = new Location(); l.setUri(uri); l.setRange(f.selectionRange); locs.add(l);
                }
            }
        } else {
            for (SymbolIndex.Def d : defs) { Location l = new Location(); l.setUri(d.uri); l.setRange(d.selectionRange); locs.add(l);}        
        }
        return java.util.concurrent.CompletableFuture.completedFuture(org.eclipse.lsp4j.jsonrpc.messages.Either.forLeft((java.util.List<? extends Location>) locs));
    }

    @Override
    public java.util.concurrent.CompletableFuture<java.util.List<? extends Location>> references(ReferenceParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        Position pos = params.getPosition();
        java.util.Optional<String> w = Symbols.wordAt(text, pos.getLine(), pos.getCharacter());
        if (w.isEmpty()) return java.util.concurrent.CompletableFuture.completedFuture(java.util.Collections.emptyList());
        String word = w.get();
        boolean includeDecl = params.getContext() != null && Boolean.TRUE.equals(params.getContext().isIncludeDeclaration());
        java.util.List<Location> all = index.references(word, includeDecl);
        return java.util.concurrent.CompletableFuture.completedFuture(all);
    }

    @Override
    public java.util.concurrent.CompletableFuture<java.util.List<FoldingRange>> foldingRange(FoldingRangeRequestParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        java.util.List<FoldingRange> out = new java.util.ArrayList<>();
        if (text == null) return java.util.concurrent.CompletableFuture.completedFuture(out);
        String[] lines = text.split("\r?\n", -1);
        class Entry { char ch; int line; Entry(char c,int l){ch=c;line=l;} }
        java.util.Deque<Entry> stack = new java.util.ArrayDeque<>();
        for (int i=0;i<lines.length;i++) {
            String line = lines[i];
            for (int j=0;j<line.length();j++) {
                char c = line.charAt(j);
                if (c=='{'||c=='['||c=='(') {
                    stack.push(new Entry(c,i));
                } else if (c=='}'||c==']'||c==')') {
                    if (!stack.isEmpty()) {
                        Entry e = stack.pop();
                        if ((e.ch=='{'&&c=='}')||(e.ch=='['&&c==']')||(e.ch=='('&&c==')')) {
                            if (e.line < i) {
                                FoldingRange fr = new FoldingRange();
                                fr.setStartLine(e.line);
                                fr.setEndLine(i);
                                out.add(fr);
                            }
                        }
                    }
                }
            }
        }
        return java.util.concurrent.CompletableFuture.completedFuture(out);
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String text = docs.getText(uri);
        if (text == null) text = "";
        java.util.List<int[]> raw = new java.util.ArrayList<>();

        try {
            CharStream cs = CharStreams.fromString(text);
            com.dafei1288.jimlang.parser.JimLangLexer lexer = new com.dafei1288.jimlang.parser.JimLangLexer(cs);
            CommonTokenStream stream = new CommonTokenStream(lexer);
            stream.fill();
            java.util.List<Token> tokens = stream.getTokens();

            class ST { int line; int col; int len; int type; int mods; ST(int l,int c,int n,int t,int m){line=l;col=c;len=n;type=t;mods=m;} }
            java.util.List<ST> st = new java.util.ArrayList<>();

            java.util.Set<String> keywordSyms = new java.util.HashSet<>(java.util.Arrays.asList(
                "VAR","LET","CONST","FUNCTION","RETURN","IF","ELSE","WHILE","FOR","BREAK","CONTINUE","NEW","BOOLEAN_LITERAL"));
            int kwIdx = SEM_TYPES.indexOf("keyword");
            int strIdx = SEM_TYPES.indexOf("string");
            int numIdx = SEM_TYPES.indexOf("number");
            int comIdx = SEM_TYPES.indexOf("comment");
            int opIdx = SEM_TYPES.indexOf("operator");
            int funIdx = SEM_TYPES.indexOf("function");
            int typeIdx = SEM_TYPES.indexOf("type");
            int varIdx = SEM_TYPES.indexOf("variable");
            int propIdx = SEM_TYPES.indexOf("property");
            int modDecl = 1 << SEM_MODS.indexOf("declaration");
            int modLib = 1 << SEM_MODS.indexOf("defaultLibrary");

            for (int i=0;i<tokens.size();i++) {
                Token t = tokens.get(i);
                String sym = com.dafei1288.jimlang.parser.JimLangLexer.VOCABULARY.getSymbolicName(t.getType());
                if (sym == null) continue;
                int line = Math.max(0, t.getLine()-1);
                int col = Math.max(0, t.getCharPositionInLine());
                String tx = t.getText(); if (tx == null) tx = "";
                if (sym.equals("WS")) continue;
                if (keywordSyms.contains(sym)) { st.add(new ST(line, col, tx.length(), kwIdx, 0)); continue; }
                if (sym.equals("STRING_LITERAL") || sym.equals("ML_STRING_LITERAL")) {
                    String[] parts = tx.split("\\r?\\n", -1);
                    for (int k=0;k<parts.length;k++) {
                        String part = parts[k]; int l = line + k; int c = (k==0? col : 0);
                        if (part.length()>0) st.add(new ST(l, c, part.length(), strIdx, 0));
                    }
                    continue;
                }
                if (sym.equals("NUMBER_LITERAL")) { st.add(new ST(line, col, tx.length(), numIdx, 0)); continue; }
                if (sym.equals("COMMENT") || sym.equals("LINE_COMMENT")) {
                    String[] parts = tx.split("\\r?\\n", -1);
                    for (int k=0;k<parts.length;k++) {
                        String part = parts[k]; int l = line + k; int c = (k==0? col : 0);
                        if (part.length()>0) st.add(new ST(l, c, part.length(), comIdx, 0));
                    }
                    continue;
                }
                if (java.util.Arrays.asList("FAC","POW","MUL","DIV","ADD","SUB","LT","GT","LE","GE","EQ","NE","AND","OR","ASSIGN","DOT").contains(sym)) { st.add(new ST(line, col, tx.length(), opIdx, 0)); continue; }
            }

            // Function declarations
            for (Symbols.Fn f : Symbols.findFunctions(text)) {
                int sl = f.selectionRange.getStart().getLine(); int sc = f.selectionRange.getStart().getCharacter();
                int el = f.selectionRange.getEnd().getLine(); int ec = f.selectionRange.getEnd().getCharacter();
                if (sl == el && ec > sc) st.add(new ST(sl, sc, ec - sc, funIdx, modDecl));
            }

            // IDs: function calls, types, properties, variables
            for (int i=0;i<tokens.size();i++) {
                Token t = tokens.get(i);
                String sym = com.dafei1288.jimlang.parser.JimLangLexer.VOCABULARY.getSymbolicName(t.getType());
                if (!"ID".equals(sym)) continue;
                String tx = t.getText(); if (tx == null) continue;
                int line = Math.max(0, t.getLine()-1); int col = Math.max(0, t.getCharPositionInLine());
                Token next = nextDefault(tokens, i);
                Token prev = prevDefault(tokens, i);
                String nextSym = next==null? null : com.dafei1288.jimlang.parser.JimLangLexer.VOCABULARY.getSymbolicName(next.getType());
                String prevSym = prev==null? null : com.dafei1288.jimlang.parser.JimLangLexer.VOCABULARY.getSymbolicName(prev.getType());
                if (java.util.Arrays.asList("string","number","boolean","int","float","array","object").contains(tx)) { st.add(new ST(line, col, tx.length(), typeIdx, 0)); continue; }
                if ("DOT".equals(prevSym)) { st.add(new ST(line, col, tx.length(), propIdx, 0)); continue; }
                if ("VAR".equals(prevSym) || "LET".equals(prevSym) || "CONST".equals(prevSym)) { st.add(new ST(line, col, tx.length(), varIdx, modDecl)); continue; }
                if ("B_OPEN".equals(nextSym) && !"FUNCTION".equals(prevSym)) { // call
                    int mods = (tx.equals("print") || tx.equals("println")) ? modLib : 0;
                    st.add(new ST(line, col, tx.length(), funIdx, mods));
                    continue;
                }
                // default: variable usage
                st.add(new ST(line, col, tx.length(), varIdx, 0));
            }

            // Sort and encode
            st.sort(java.util.Comparator.<ST>comparingInt(a -> a.line).thenComparingInt(a -> a.col));
            int lastLine = 0; int lastCol = 0; boolean first = true;
            for (ST e : st) {
                if (e.len <= 0 || e.type < 0) continue;
                int dl = first ? e.line : (e.line - lastLine);
                int ds = first || dl != 0 ? e.col : (e.col - lastCol);
                raw.add(new int[]{ dl, ds, e.len, e.type, e.mods });
                lastLine = e.line; lastCol = e.col; first = false;
            }
        } catch (Exception ex) {
            Log.warn("semanticTokens error: " + ex.getMessage());
        }

        SemanticTokens result = new SemanticTokens();
        java.util.List<Integer> data = new java.util.ArrayList<>();
        for (int[] a : raw) { for (int v : a) data.add(v); }
        result.setData(data);
        return CompletableFuture.completedFuture(result);
    }

    private static Token nextDefault(java.util.List<Token> tokens, int idx) {
        for (int k=idx+1;k<tokens.size();k++) { Token tt = tokens.get(k); if (tt.getChannel()==Token.DEFAULT_CHANNEL) return tt; }
        return null;
    }
    private static Token prevDefault(java.util.List<Token> tokens, int idx) {
        for (int k=idx-1;k>=0;k--) { Token tt = tokens.get(k); if (tt.getChannel()==Token.DEFAULT_CHANNEL) return tt; }
        return null;
    }
}