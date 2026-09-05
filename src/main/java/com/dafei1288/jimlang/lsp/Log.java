package com.dafei1288.jimlang.lsp;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.services.LanguageClient;

public final class Log {
    private static volatile LanguageClient client;
    private Log() {}
    public static void attach(LanguageClient c) { client = c; }
    public static void info(String msg) { publish(MessageType.Info, msg); }
    public static void warn(String msg) { publish(MessageType.Warning, msg); }
    public static void error(String msg) { publish(MessageType.Error, msg); }
    private static void publish(MessageType t, String msg) {
        String full = "[jimlang-lsp] " + msg;
        LanguageClient c = client;
        if (c != null) {
            try { c.logMessage(new MessageParams(t, full)); } catch (Exception ignore) {}
        }
        System.err.println(full);
    }
}