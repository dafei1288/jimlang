# JimLang Project Status (Snapshot)

Date: 2025-12-04

## Completed
- Syntax/Parsing: ANTLR4; blocks as statements; arrays/objects; accessors (dot and index)
- Execution: REPL, scripts, STDIN; CLI options parsed before script path (--cli/-i, --eval/-e, --trace)
- Errors & Trace: runtime errors include source:line:col + caret + call stack; Trace push/pop with enter/leave; enabled by --trace or JIM_TRACE
- Scoping: function scope and block scope; shadowing; nearest-scope assignment semantics
- Types (baseline): string/number/boolean/int/float/array/object/Java FQCN; widening int -> float; errors include location info
- Stdlib: strings, arrays, math, file I/O; added join/keys/values/typeof/isArray/isObject/parseInt/parseFloat
- Docs: QUICKREF/ROADMAP/README/README_ZH unified to UTF-8 (no BOM); examples for scoping

## In Progress / Near Term
- Tests: block scope shadowing; CLI combos for --trace/--eval/STDIN

## Deferred (record only)
- Function param/return type annotations (function f(a:int): number) + tests
- let/const (block scope; const not reassignable) + tests

## Next (safe chores)
- Grammar file sync: remove or sync src/main/resources/JimLang.g4 with src/main/antlr4 version
- Add JUnit for block shadowing
- Clean bin/jimlang.cmd comments to ASCII (keep CRLF)

(Encoding: UTF-8 without BOM; ASCII-only content to avoid garbled text.)