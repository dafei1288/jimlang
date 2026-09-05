package com.dafei1288.jimlang.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.WorkspaceService;

public class JimWorkspaceService implements WorkspaceService {
    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        // no-op
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        // no-op
    }
}