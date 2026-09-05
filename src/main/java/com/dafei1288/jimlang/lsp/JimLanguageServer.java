package com.dafei1288.jimlang.lsp;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

public class JimLanguageServer implements LanguageServer, LanguageClientAware {
    private LanguageClient client;
    private final DocumentManager docs = new DocumentManager();
    private final SymbolIndex index = new SymbolIndex();
    private final JimTextDocumentService textService = new JimTextDocumentService(docs, index);
    private final JimWorkspaceService workspaceService = new JimWorkspaceService();

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        textService.connect(client);
        Log.attach(client);
        Log.info("client attached");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        Log.info("initialize: rootUri=" + params.getRootUri());
        if (params.getWorkspaceFolders() != null) {
            index.scanWorkspaceFolders(params.getWorkspaceFolders());
            Log.info("workspace indexed: folders=" + params.getWorkspaceFolders().size());
        }
        ServerCapabilities caps = new ServerCapabilities();
        TextDocumentSyncOptions sync = new TextDocumentSyncOptions();
        sync.setOpenClose(true);
        sync.setChange(TextDocumentSyncKind.Full);
        caps.setTextDocumentSync(sync);

        CompletionOptions completion = new CompletionOptions();
        completion.setResolveProvider(false);
        completion.setTriggerCharacters(java.util.Arrays.asList("(", ",", "."));
        caps.setCompletionProvider(completion);
        caps.setHoverProvider(true);
        caps.setDocumentSymbolProvider(true);
        caps.setDefinitionProvider(true);
        caps.setReferencesProvider(true);
        caps.setFoldingRangeProvider(true);

        // Semantic tokens capability (extended with property)
        SemanticTokensLegend legend = new SemanticTokensLegend();
        legend.setTokenTypes(java.util.Arrays.asList(
            "namespace","type","function","variable","property","keyword","comment","string","number","operator"));
        legend.setTokenModifiers(java.util.Arrays.asList("declaration","defaultLibrary"));
        SemanticTokensWithRegistrationOptions sem = new SemanticTokensWithRegistrationOptions();
        sem.setLegend(legend);
        sem.setFull(true);
        caps.setSemanticTokensProvider(sem);

        InitializeResult result = new InitializeResult(caps);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() { return CompletableFuture.completedFuture(null); }

    @Override
    public void exit() { }

    @Override
    public TextDocumentService getTextDocumentService() { return textService; }

    @Override
    public WorkspaceService getWorkspaceService() { return workspaceService; }
}