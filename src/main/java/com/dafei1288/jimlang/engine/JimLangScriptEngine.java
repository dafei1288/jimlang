package com.dafei1288.jimlang.engine;

import com.dafei1288.jimlang.JimLangShell;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Hashtable;

public class JimLangScriptEngine extends AbstractScriptEngine
        implements Compilable, Invocable {

    public JimLangScriptEngine(){
        super();
        this.shell = new JimLangShell();
    }

    public JimLangScriptEngine(ScriptEngineFactory scriptEngineFactory){
        super();
        this.shell = new JimLangShell();
        this.scriptEngineFactory = scriptEngineFactory;
    }


    private JimLangShell shell;
    private ScriptEngineFactory scriptEngineFactory;

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return null;
    }

    @Override
    public CompiledScript compile(Reader script) throws ScriptException {
        return null;
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;
    }

    @Override
    public Object eval(String script) throws ScriptException {
        return shell.eval(script,null);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return shell.eval(script,context.getBindings(0));
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        int i = 0;
        char[] cs = new char[1024];
        StringBuffer stringBuffer = new StringBuffer();
        try {
            while((i=reader.read(cs))>0){
                stringBuffer.append(cs,0,i);
            }
            stringBuffer.append(cs,0,i);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return shell.eval(stringBuffer.toString(),context.getBindings(0));
    }

    @Override
    public Bindings createBindings() {
        return null;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return scriptEngineFactory;
    }
}
