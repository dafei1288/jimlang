package com.dafei1288.jimlang.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import java.util.concurrent.Future;

public class LspServerLauncher {
    public static void main(String[] args) throws Exception {
        Log.info("starting LSP server (stdio)");
        JimLanguageServer server = new JimLanguageServer();
        var launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);
        Future<?> start = launcher.startListening();
        Log.info("listening...");
        start.get();
        Log.info("server stopped");
    }
}