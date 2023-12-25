package com.dafei1288.jimlang;


import com.dafei1288.jimlang.parser.JimLangLexer;
import com.dafei1288.jimlang.parser.JimLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;

public class JimLangShell {

    public Object eval(String script, Map<String, Object> context) {
        CharStream stream = CharStreams.fromString(script);
        JimLangLexer lexer = new JimLangLexer(stream);
        JimLangParser parser = new JimLangParser(new CommonTokenStream(lexer));

        ParseTree parseTree = parser.prog();
        JimLangVistor mlvistor = new JimLangVistor();

        Object o = mlvistor.visit(parseTree);
        return o;
    }
}