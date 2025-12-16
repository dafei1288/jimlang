package com.dafei1288.jimlang.engine;

import com.dafei1288.jimlang.JimLangShell;

import javax.script.*;
import java.io.IOException;
import java.io.Reader;

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
        final String code = script;
        return new CompiledScript() {
            @Override
            public Object eval(ScriptContext context) throws ScriptException {
                return shell.eval(code, context.getBindings(ScriptContext.ENGINE_SCOPE));
            }

            @Override
            public ScriptEngine getEngine() {
                return JimLangScriptEngine.this;
            }
        };
    }

    @Override
    public CompiledScript compile(Reader reader) throws ScriptException {
        try {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
            return compile(sb.toString());
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        throw new NoSuchMethodException("Invocable is not supported yet");
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        try {
            return this.shell.invokeFunction(name, args);
        } catch (RuntimeException e) {
            throw new ScriptException(e);
        }
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
        return shell.eval(script, getContext().getBindings(ScriptContext.ENGINE_SCOPE));
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return shell.eval(script, context.getBindings(ScriptContext.ENGINE_SCOPE));
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
            return shell.eval(sb.toString(), context.getBindings(ScriptContext.ENGINE_SCOPE));
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return scriptEngineFactory;
    }
}
