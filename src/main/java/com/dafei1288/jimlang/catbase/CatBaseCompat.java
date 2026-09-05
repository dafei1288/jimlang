package com.dafei1288.jimlang.catbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CatBase -&gt; JimLang source translator.
 *
 * <p>CatBase (github.com/DoRobotAi/catbase-lang) is a statically typed,
 * Python-flavoured language whose official compiler is closed-source and
 * compiles to native binaries via Zig. JimLang cannot reuse that pipeline,
 * so this class re-implements a dynamic subset of CatBase on top of the
 * JimLang interpreter:</p>
 *
 * <ul>
 *   <li>{@code def f(a: int, b: str = "x") -> int { ... }} -&gt;
 *       {@code function f(a, b) { ... }}  (types/defaults/returns stripped,
 *       CatBase is strongly typed but the JVM runtime does not need the
 *       annotations)</li>
 *   <li>{@code x: int = 0} / {@code name: str = "CatBase"} -&gt;
 *       {@code var x = 0}</li>
 *   <li>{@code # comment} -&gt; {@code // comment}</li>
 *   <li>{@code if cond { } / while cond { }} -&gt; {@code if (cond) { }}</li>
 *   <li>{@code for i in range(a, b[, step]) { }} -&gt; a JimLang C-style
 *       {@code for} loop with a fresh counter; all uses of {@code i} inside
 *       the loop body are renamed to the counter</li>
 *   <li>string escapes ({@code \n \t \" \\} ...) are materialised at
 *       translation time (JimLang strings carry raw bytes)</li>
 *   <li>{@code print(a, b, c, ...)} -&gt; {@code cat_print(a, b, c, ...)}
 *       (concatenated, no separator, no trailing newline)</li>
 *   <li>{@code int()/float()/str()/bool()} stay as-is and are dispatched by
 *       the engine alias table to {@code to_int} etc.</li>
 *   <li>{@code json_dumps()/json_loads()} -&gt; {@code json_encode()/json_decode()}</li>
 *   <li>logical words {@code and/or} -&gt; {@code &&/||}, {@code True/False}
 *       -&gt; {@code true/false}</li>
 *   <li>a {@code def main(...)} entry point is invoked automatically by
 *       appending {@code main([])}</li>
 * </ul>
 *
 * <p>Translation is syntax-level only: it never evaluates CatBase semantics
 * itself, and unsupported constructs simply remain and surface as ordinary
 * JimLang parse/runtime errors. Known unsupported (Tier 2+): f-strings,
 * class/struct with {@code self}, keyword/default arguments, import,
 * multi-return, threads/queues/mutex/socket/serial/audio/Pointer/FFI and the
 * handle-based file API.</p>
 */
public final class CatBaseCompat {

    private CatBaseCompat() { }

    /** Standalone helper: cat2jim <in.cat> [out.jim]. Without an output path
     *  the translated source is printed to stdout. */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: CatBaseCompat <in.cat> [out.jim]");
            System.exit(2);
        }
        String src = java.nio.file.Files.readString(java.nio.file.Path.of(args[0]));
        String out = translate(src);
        if (args.length >= 2) {
            java.nio.file.Files.writeString(java.nio.file.Path.of(args[1]), out);
        } else {
            System.out.print(out);
        }
    }

    // ---------------------------------------------------------------- lexer

    private static final int W = 0;   // word / identifier / keyword
    private static final int N = 1;   // number literal
    private static final int S = 2;   // string literal (raw, incl. quotes)
    private static final int O = 3;   // operator / punctuation
    private static final int C = 4;   // '#' comment (excl. trailing newline)

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "def", "if", "elif", "else", "for", "while", "return", "class", "struct",
        "import", "from", "print", "range", "in", "not", "and", "or", "break",
        "continue", "true", "false", "True", "False", "None", "none", "self",
        "var", "function", "new", "pass", "global", "thread", "try", "except",
        "finally", "raise", "with", "lambda", "as", "is"
    ));

    private static final class Tok {
        int kind; String text; int start, end; int line;
        Tok(int kind, String text, int start, int end, int line) {
            this.kind = kind; this.text = text; this.start = start; this.end = end; this.line = line;
        }
    }

    private static final class Edit {
        int start, end; String repl;
        Edit(int start, int end, String repl) { this.start = start; this.end = end; this.repl = repl; }
    }

    /** Tokenise CatBase source. Whitespace is dropped, but line numbers and
     *  char offsets are kept on every token. */
    private static List<Tok> lex(String src) {
        List<Tok> out = new ArrayList<>();
        int n = src.length();
        int i = 0, line = 1;
        while (i < n) {
            char c = src.charAt(i);
            if (c == '\n') { line++; i++; continue; }
            if (Character.isWhitespace(c)) { i++; continue; }
            // comment to end of line
            if (c == '#') {
                int s = i;
                while (i < n && src.charAt(i) != '\n') i++;
                out.add(new Tok(C, src.substring(s, i), s, i, line));
                continue;
            }
            // strings (double, single, or ''' triple)
            if (c == '"' || c == '\'') {
                char q = c;
                int s = i;
                if (c == '\'' && i + 2 < n && src.charAt(i + 1) == '\'' && src.charAt(i + 2) == '\'') {
                    i += 3;
                    while (i + 2 < n && !(src.charAt(i) == '\'' && src.charAt(i + 1) == '\'' && src.charAt(i + 2) == '\'')) {
                        if (src.charAt(i) == '\n') line++;
                        i++;
                    }
                    i = Math.min(i + 3, n);
                } else {
                    i++;
                    while (i < n) {
                        char d = src.charAt(i);
                        if (d == '\\' && i + 1 < n) { i += 2; continue; }
                        if (d == q) { i++; break; }
                        if (d == '\n') { line++; break; }   // unterminated: stop at EOL
                        i++;
                    }
                }
                out.add(new Tok(S, src.substring(s, i), s, i, line));
                continue;
            }
            // numbers
            if (c >= '0' && c <= '9') {
                int s = i;
                while (i < n && ((src.charAt(i) >= '0' && src.charAt(i) <= '9') || src.charAt(i) == '.')) {
                    if (src.charAt(i) == '\n') line++;
                    i++;
                }
                out.add(new Tok(N, src.substring(s, i), s, i, line));
                continue;
            }
            // words
            if (Character.isLetter(c) || c == '_') {
                int s = i;
                while (i < n) {
                    char d = src.charAt(i);
                    if (Character.isLetterOrDigit(d) || d == '_') i++; else break;
                }
                out.add(new Tok(W, src.substring(s, i), s, i, line));
                continue;
            }
            // operators: longest match first
            String two = (i + 1 < n) ? src.substring(i, i + 2) : "";
            String three = (i + 2 < n) ? src.substring(i, i + 3) : "";
            int len = 1;
            String op = String.valueOf(c);
            if (three.equals("...")) { len = 3; op = "..."; }
            else if (two.equals("==") || two.equals("!=") || two.equals("<=") || two.equals(">=")
                  || two.equals("&&") || two.equals("||") || two.equals("**") || two.equals("//")
                  || two.equals("+=") || two.equals("-=") || two.equals("*=") || two.equals("/=")
                  || two.equals("->") || two.equals(":=")) { len = 2; op = two; }
            out.add(new Tok(O, op, i, i + len, line));
            i += len;
        }
        return out;
    }

    // ------------------------------------------------------------ utilities

    private static boolean isWord(Tok t) { return t != null && t.kind == W; }
    private static boolean isSym(Tok t, String s) { return t != null && t.kind == O && t.text.equals(s); }
    private static boolean isWord(Tok t, String s) { return t != null && t.kind == W && t.text.equals(s); }

    private static String slice(String src, Tok a, Tok b) {
        if (a == null || b == null) return "";
        return src.substring(a.start, b.end).trim();
    }

    /** structural edits that already own a text region (their spans win) */    private static final class Intervals {
        final List<int[]> spans = new ArrayList<>();
        boolean inside(int pos) {
            for (int[] sp : spans) if (pos >= sp[0] && pos < sp[1]) return true;
            return false;
        }
        void add(int s, int e) { spans.add(new int[]{s, e}); }
    }

    private static boolean stmtStart(List<Tok> sig, int i) {
        if (i <= 0) return true;
        Tok p = sig.get(i - 1);
        return isSym(p, "{") || isSym(p, ";") || isSym(p, "}") || p.line != sig.get(i).line;
    }

    // ------------------------------------------------------------ rewriting

    /**
     * Translate CatBase source text into JimLang source text.
     * The result is meant to be fed to the regular JimLang parser/interpreter.
     */
    public static String translate(String src) {
        List<Tok> toks = lex(src);
        List<Tok> sig = new ArrayList<>();
        for (Tok t : toks) if (t.kind != C) sig.add(t);
        List<Edit> edits = new ArrayList<>();
        Intervals owned = new Intervals();

        // fresh counter names share one namespace across the whole file
        int[] counterBox = { 0 };

        for (int i = 0; i < sig.size(); i++) {
            Tok t = sig.get(i);
            if (t.kind != W) continue;
            boolean ss = stmtStart(sig, i);
            if (t.text.equals("def") && ss) { defRewrite(src, sig, i, edits, owned); }
            else if (t.text.equals("for") && ss) { forRewrite(src, sig, i, edits, owned, counterBox); }
            else if (t.text.equals("else") && ss && i + 1 < sig.size() && isWord(sig.get(i + 1), "if")) {
                int last = elseIfChain(src, sig, i, edits, owned);
                if (last > i) i = last;
            }
            else if ((t.text.equals("if") || t.text.equals("while"))
                     && (ss || (i > 0 && isWord(sig.get(i - 1), "else")))) { parenWrap(src, sig, i, edits, owned); }
            else if (ss && i + 1 < sig.size() && isSym(sig.get(i + 1), ":") && !KEYWORDS.contains(t.text)) {
                varDecl(src, sig, i, edits, owned);
            }
        }

        // second pass: small per-token edits (never inside an owned region)
        for (int i = 0; i < sig.size(); i++) {
            Tok t = sig.get(i);
            if (t.kind != W) continue;
            if (owned.inside(t.start)) continue;
            // logical words / booleans
            String map = null;
            switch (t.text) {
                case "and":  map = "&&"; break;
                case "or":   map = "||"; break;
                case "True": map = "true"; break;
                case "False": map = "false"; break;
                default: break;
            }
            if (map != null) edits.add(new Edit(t.start, t.end, map));
            // builtin renames (call sites only)
            boolean call = i + 1 < sig.size() && isSym(sig.get(i + 1), "(");
            boolean notMember = i == 0 || !isSym(sig.get(i - 1), ".");
            String bn = null;
            if (call && notMember) {
                if (t.text.equals("print")) bn = "cat_print";
                else if (t.text.equals("println")) bn = "cat_print";
                else if (t.text.equals("json_dumps")) bn = "json_encode";
                else if (t.text.equals("json_loads")) bn = "json_decode";
                else if (t.text.equals("len")) bn = null;      // already polymorphic
            }
            if (bn != null) edits.add(new Edit(t.start, t.end, bn));
        }

        // comments -> remove; strings with escapes -> rebuild
        for (Tok t : toks) {
            if (owned.inside(t.start)) continue;
            if (t.kind == C) {
                edits.add(new Edit(t.start, t.end, ""));
            } else if (t.kind == S && needsRebuild(t.text)) {
                String rebuilt = rebuildString(t.text);
                if (rebuilt != null) edits.add(new Edit(t.start, t.end, rebuilt));
            }
        }

        // auto-invoke main([]) when a top-level "def main(...)" exists and no
        // explicit top-level call to main is present
        boolean hasMain = false, callsMain = false;
        for (int i = 0; i < sig.size(); i++) {
            Tok t = sig.get(i);
            if (isWord(t, "main")) {
                if (i > 0 && isWord(sig.get(i - 1), "def")) hasMain = true;
                if (i + 1 < sig.size() && isSym(sig.get(i + 1), "(") && stmtStart(sig, i)) callsMain = true;
            }
        }
        if (hasMain && !callsMain) {
            edits.add(new Edit(src.length(), src.length(), "\nmain([])\n"));
        }

        return applyEdits(src, edits);
    }

    // ------------------------------------------------------ structural rules

    /** def name(p1: T1 = d1, p2: T2) -> Ret { ... } -> function name(p1, p2) { ... } */
    private static void defRewrite(String src, List<Tok> sig, int i,
                                   List<Edit> edits, Intervals owned) {
        if (i + 2 >= sig.size()) return;
        Tok name = sig.get(i + 1);
        if (name.kind != W) return;
        int open = i + 2;
        if (!isSym(sig.get(open), "(")) return;
        int close = match(sig, open, "(", ")");
        if (close < 0) return;
        // header: "def name(" -> "function name("
        edits.add(new Edit(sig.get(i).start, sig.get(open).start, "function " + name.text));
        owned.add(sig.get(i).start, sig.get(open).start);
        // param list inner region
        List<String> params = new ArrayList<>();
        if (close - open > 1) {
            int depth = 0, segStart = open + 1;
            for (int k = open + 1; k < close; k++) {
                Tok t = sig.get(k);
                if (isSym(t, "(") || isSym(t, "[") || isSym(t, "{")) depth++;
                else if (isSym(t, ")") || isSym(t, "]") || isSym(t, "}")) depth--;
                else if (isSym(t, ",") && depth == 0) {
                    addParam(sig, segStart, k, params);
                    segStart = k + 1;
                }
            }
            addParam(sig, segStart, close, params);
        }
        int innerS = sig.get(open).end, innerE = sig.get(close).start;
        String paramText = String.join(", ", params);
        boolean innerHasContent = src.substring(innerS, innerE).trim().length() > 0;
        if (!params.isEmpty() || innerHasContent) {
            edits.add(new Edit(innerS, innerE, paramText));
            owned.add(innerS, innerE);
        }
        // optional "-> Ret" before the body brace
        int brace = -1;
        for (int k = close + 1; k < sig.size(); k++) {
            Tok t = sig.get(k);
            if (isSym(t, "{")) { brace = k; break; }
            if (isSym(t, "->")) { continue; }
            if (t.line != sig.get(close).line && t.kind != W) break; // don't run away
            // annotation type words are fine to skip; stop only at '{'
        }
        if (brace > 0) {
            Tok arrow = null;
            for (int k = close + 1; k < brace; k++) if (isSym(sig.get(k), "->")) { arrow = sig.get(k); break; }
            if (arrow != null) {
                edits.add(new Edit(arrow.start, sig.get(brace).start, " "));
                owned.add(arrow.start, sig.get(brace).start);
            }
        }
    }

    /** extract the plain parameter name from a comma-separated segment */
    private static void addParam(List<Tok> sig, int from, int to, List<String> out) {
        for (int k = from; k < to; k++) {
            Tok t = sig.get(k);
            if (t.kind == W) {
                String nm = t.text;
                if (!nm.equals("self") && !KEYWORDS.contains(nm)) { out.add(nm); return; }
            }
        }
        // no usable identifier -> keep a placeholder so arity is preserved
        out.add("_");
    }

    /** for v in range(a, b[, s]) { body } -> for (var __cbN = a; ... ) with body
     *  references to v renamed to __cbN. */
    private static void forRewrite(String src, List<Tok> sig, int i,
                                   List<Edit> edits, Intervals owned, int[] counterBox) {
        if (i + 4 >= sig.size()) return;
        Tok var = sig.get(i + 1);
        if (var.kind != W || KEYWORDS.contains(var.text)) return;
        if (!isWord(sig.get(i + 2), "in")) return;
        if (!isWord(sig.get(i + 3), "range")) return;
        int open = i + 4;
        if (!isSym(sig.get(open), "(")) return;
        int close = match(sig, open, "(", ")");
        if (close < 0) return;
        // brace that opens the loop body must be the next significant token
        int brace = close + 1;
        if (brace >= sig.size() || !isSym(sig.get(brace), "{")) return;
        int endBrace = match(sig, brace, "{", "}");
        if (endBrace < 0) return;

        // parse range arguments (top-level commas)
        List<int[]> args = new ArrayList<>(); // {startTokIdx, endTokIdx}
        if (close - open > 1) {
            int depth = 0, segStart = open + 1;
            for (int k = open + 1; k < close; k++) {
                Tok t = sig.get(k);
                if (isSym(t, "(") || isSym(t, "[") || isSym(t, "{")) depth++;
                else if (isSym(t, ")") || isSym(t, "]") || isSym(t, "}")) depth--;
                else if (isSym(t, ",") && depth == 0) { args.add(new int[]{segStart, k - 1}); segStart = k + 1; }
            }
            if (segStart <= close - 1) args.add(new int[]{segStart, close - 1});
        }
        String A, B, step;
        if (args.isEmpty()) return;
        if (args.size() == 1) {
            int[] ab = args.get(0);
            A = "0";
            B = slice(src, sig.get(ab[0]), sig.get(ab[1]));
            step = "1";
        } else {
            int[] ab = args.get(0), bb = args.get(1);
            A = slice(src, sig.get(ab[0]), sig.get(ab[1]));
            B = slice(src, sig.get(bb[0]), sig.get(bb[1]));
            step = args.size() >= 3 ? slice(src, sig.get(args.get(2)[0]), sig.get(args.get(2)[1])) : "1";
        }
        boolean neg = step.startsWith("-");
        String cnt = "__cb" + (counterBox[0]++);
        String cond = cnt + (neg ? " > " : " < ") + B;
        String inc = cnt + " = " + cnt + " + " + (neg ? ("(0 " + step.replaceFirst("^-", "- ") + ")") : step);
        String header = "for (var " + cnt + " = " + A + "; " + cond + "; " + inc + ")";
        edits.add(new Edit(sig.get(i).start, sig.get(brace).start, header));
        owned.add(sig.get(i).start, sig.get(brace).start);
        // rename uses of the loop variable inside the body
        for (int k = brace + 1; k < endBrace; k++) {
            Tok t = sig.get(k);
            if (t.kind == W && t.text.equals(var.text) && !owned.inside(t.start)) {
                edits.add(new Edit(t.start, t.end, cnt));
            }
        }
    }

    /** if cond { ... } / while cond { ... } -> if (cond) { ... } */
    private static void parenWrap(String src, List<Tok> sig, int i,
                                  List<Edit> edits, Intervals owned) {
        int open = findCondBlock(sig, i);
        if (open < 0) return;
        wrapCond(src, sig, i, open, edits, owned);
    }

    /** index of the '{' that opens the body block after an if/while keyword */
    private static int findCondBlock(List<Tok> sig, int kw) {
        int depth = 0;
        for (int k = kw + 1; k < sig.size(); k++) {
            Tok t = sig.get(k);
            if (isSym(t, "(") || isSym(t, "[")) depth++;
            else if (isSym(t, ")") || isSym(t, "]")) depth--;
            else if (isSym(t, "{") && depth == 0) return k;
            else if ((isSym(t, "}") || isSym(t, ";")) && depth == 0) return -1;
        }
        return -1;
    }

    /** parenthesize the condition text between an if/while and its body brace */
    private static void wrapCond(String src, List<Tok> sig, int kw, int open,
                                 List<Edit> edits, Intervals owned) {
        int last = open - 1;
        if (last > kw) {
            Tok a = sig.get(kw + 1), b = sig.get(last);
            String cond = src.substring(a.start, b.end).trim();
            if (!cond.isEmpty()) {
                edits.add(new Edit(a.start, b.end, "(" + cond + ")"));
                owned.add(a.start, b.end);
            }
        }
    }

    /**
     * JimLang has no "else if". Rewrite a whole else-if chain into nested
     * blocks: {@code else if C1 {B1} else {Be}} -&gt;
     * {@code else { if C1 {B1} else {Be} }}.
     * Returns the token index just past the chain (for the scan loop).
     */
    private static int elseIfChain(String src, List<Tok> sig, int start,
                                   List<Edit> edits, Intervals owned) {
        int cur = start;
        int lastClose = -1;
        int openCount = 0;
        while (cur < sig.size() && isWord(sig.get(cur), "else")
               && cur + 1 < sig.size() && isWord(sig.get(cur + 1), "if")) {
            // insert the opening brace of the enclosing else block
            edits.add(new Edit(sig.get(cur).end, sig.get(cur).end, "{"));
            owned.add(sig.get(cur).end, sig.get(cur).end);
            openCount++;
            int ifIdx = cur + 1;
            int open = findCondBlock(sig, ifIdx);
            if (open < 0) break;
            wrapCond(src, sig, ifIdx, open, edits, owned);
            int close = match(sig, open, "{", "}");
            if (close < 0) break;
            lastClose = close;
            int nxt = close + 1;
            if (nxt < sig.size() && isWord(sig.get(nxt), "else")) {
                if (nxt + 1 < sig.size() && isWord(sig.get(nxt + 1), "if")) { cur = nxt; continue; }
                // final plain else clause
                int eClose = match(sig, nxt + 1, "{", "}");
                if (eClose < 0) break;
                lastClose = eClose;
                break;
            }
            break;   // chain ends after this else-if
        }
        if (openCount == 0) return start;
        if (lastClose >= 0) {
            StringBuilder tail = new StringBuilder();
            for (int k = 0; k < openCount; k++) tail.append('}');
            edits.add(new Edit(sig.get(lastClose).end, sig.get(lastClose).end, tail.toString()));
        }
        return lastClose;
    }

    /** x: int = v / msg: list[dict[str, str]] = [ ... ] -> var x = v */
    private static void varDecl(String src, List<Tok> sig, int i,
                                List<Edit> edits, Intervals owned) {
        Tok name = sig.get(i);
        if (name.kind != W || KEYWORDS.contains(name.text)) return;
        int depth = 0;
        for (int k = i + 2; k < sig.size(); k++) {
            Tok t = sig.get(k);
            if (isSym(t, "(") || isSym(t, "[") || isSym(t, "{")) depth++;
            else if (isSym(t, ")") || isSym(t, "]") || isSym(t, "}")) { if (depth == 0) return; depth--; }
            else if (isSym(t, "=") && depth == 0) {
                edits.add(new Edit(name.start, t.start, "var " + name.text + " "));
                owned.add(name.start, t.start);
                return;
            }
        }
    }

    // ----------------------------------------------------------- string utils

    /** A string needs rebuilding when single-quoted or when it contains escapes. */
    private static boolean needsRebuild(String raw) {
        if (raw.length() >= 2 && raw.charAt(0) == '\'' && !raw.startsWith("'''")) return true;
        if (!raw.startsWith("\"")) return false;
        return raw.indexOf('\\') >= 0;
    }

    private static String unescape(String content) {
        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '\\' && i + 1 < content.length()) {
                char e = content.charAt(++i);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    case '0': sb.append('\0'); break;
                    default: sb.append(e); break;   // unknown escape -> literal
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Rebuild a string literal so JimLang sees the final characters.
     *  Prefers double quotes; falls back to '''...''' when the content has
     *  quotes or real newlines. Returns null when it cannot be represented. */
    private static String rebuildString(String raw) {
        char q = raw.charAt(0);
        String content;
        if (q == '\'') content = unescape(raw.substring(1, raw.length() - 1));
        else content = unescape(raw.substring(1, raw.length() - 1));
        boolean multi = content.indexOf('\n') >= 0 || content.indexOf('\r') >= 0;
        boolean hasDq = content.indexOf('"') >= 0;
        boolean hasTrip = content.contains("'''");
        if (!multi && !hasDq) return "\"" + content + "\"";
        if (!hasTrip) return "'''" + content + "'''";
        return null;   // cannot represent safely -> leave original (will error)
    }

    // ------------------------------------------------------------- plumbing

    private static int match(List<Tok> sig, int open, String ob, String cb) {
        int depth = 0;
        for (int k = open; k < sig.size(); k++) {
            Tok t = sig.get(k);
            if (isSym(t, ob)) depth++;
            else if (isSym(t, cb)) { depth--; if (depth == 0) return k; }
            // tokens of other bracket kinds are simply ignored, so nesting of
            // different delimiter types inside the searched pair is harmless
        }
        return -1;
    }

    private static String applyEdits(String src, List<Edit> edits) {
        edits.sort((a, b) -> a.start != b.start ? Integer.compare(a.start, b.start) : Integer.compare(a.end, b.end));
        List<Edit> merged = new ArrayList<>();
        for (Edit e : edits) {
            if (merged.isEmpty()) { merged.add(e); continue; }
            Edit last = merged.get(merged.size() - 1);
            if (e.start >= last.end) { merged.add(e); continue; }
            if (e.start == last.start) { merged.set(merged.size() - 1, e); continue; } // later wins
            // nested overlap -> the bigger edit owns the region
            if (e.end > last.end) merged.set(merged.size() - 1, e);
        }
        StringBuilder sb = new StringBuilder(src);
        for (int k = merged.size() - 1; k >= 0; k--) {
            Edit e = merged.get(k);
            sb.replace(e.start, e.end, e.repl);
        }
        return sb.toString();
    }
}
