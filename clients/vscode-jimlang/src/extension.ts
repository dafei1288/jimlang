import * as path from "path";
import * as vscode from "vscode";
import * as fs from "fs";
import { LanguageClient, LanguageClientOptions, ServerOptions } from "vscode-languageclient/node";

let client: LanguageClient | undefined;
const out = vscode.window.createOutputChannel("JimLang");
let status: vscode.StatusBarItem | undefined;

function pickJar(context: vscode.ExtensionContext): string | undefined {
  const conf = vscode.workspace.getConfiguration("jimlang");
  let jarPath = conf.get<string>("lsp.jarPath") || "";
  const wf = vscode.workspace.workspaceFolders?.[0]?.uri.fsPath;
  const candidates = [
    context.asAbsolutePath("server/jimlang-lsp.jar"),
    jarPath,
    wf ? path.join(wf, "target", "jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar") : undefined,
    path.join(context.extensionPath, "..", "target", "jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar"),
    path.join(context.extensionPath, "..", "..", "target", "jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar")
  ].filter((p): p is string => !!p);
  const found = candidates.find(p => fs.existsSync(p));
  return found || jarPath || undefined;
}

async function startClient(context: vscode.ExtensionContext) {
  if (!status) {
    status = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 100);
    status.command = "jimlang.restart";
    status.text = "JimLang: Stopped";
    status.tooltip = "Click to restart JimLang Language Server";
    status.show();
    context.subscriptions.push(status);
  }

  const jarPath = pickJar(context);
  out.appendLine(`[jimlang-vscode] workspace: ${vscode.workspace.workspaceFolders?.[0]?.uri.fsPath || '<none>'}`);
  out.appendLine(`[jimlang-vscode] using jar: ${jarPath ?? '<not-found>'}`);
  if (!jarPath || !fs.existsSync(jarPath)) {
    const msg = `JimLang LSP jar not found. Configure setting 'jimlang.lsp.jarPath' or build with Maven.`;
    out.appendLine(`[jimlang-vscode][error] ${msg}`);
    vscode.window.showErrorMessage(msg);
    status!.text = "JimLang: Jar Missing";
    return;
  }

  const serverOptions: ServerOptions = {
    command: "java",
    args: ["-cp", jarPath, "com.dafei1288.jimlang.lsp.LspServerLauncher"],
    options: { cwd: vscode.workspace.workspaceFolders?.[0]?.uri.fsPath }
  };
  out.appendLine(`[jimlang-vscode] launching: java -cp "${jarPath}" com.dafei1288.jimlang.lsp.LspServerLauncher`);

  const clientOptions: LanguageClientOptions = {
    documentSelector: [
      { language: "jimlang" },
      { scheme: "file", pattern: "**/*.{jim,mylang}" },
      { scheme: "untitled", language: "jimlang" }
    ],
    synchronize: { fileEvents: vscode.workspace.createFileSystemWatcher("**/*.{jim,mylang}") }
  };

  client = new LanguageClient("jimlang", "JimLang Language Server", serverOptions, clientOptions);
  status.text = "JimLang: Starting";
  vscode.commands.executeCommand('setContext', 'jimlang.running', true);
  try {
    await client.start();
    out.appendLine('[jimlang-vscode] LSP ready');
    status.text = "JimLang: Running";
  } catch (err: any) {
    out.appendLine(`[jimlang-vscode][error] ${String(err)}`);
    status.text = "JimLang: Error";
  }
}

async function stopClient() {
  if (client) {
    try { await client.stop(); } catch { /* ignore */ }
    client = undefined;
  }
}

export function activate(context: vscode.ExtensionContext) {
  out.appendLine('[jimlang-vscode] activate');
  context.subscriptions.push(out);

  // Commands
  context.subscriptions.push(vscode.commands.registerCommand('jimlang.restart', async () => {
    out.appendLine('[jimlang-vscode] restart requested');
    await stopClient();
    await startClient(context);
  }));

  // Start on activation
  startClient(context);
}

export function deactivate(): Thenable<void> | undefined {
  return client?.stop();
}
