param(
  [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\.."),
  [string]$ExtDir = "$ProjectRoot/clients/vscode-jimlang",
  [string]$Jar = "$ProjectRoot/target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar",
  [string]$Out = "$ProjectRoot/target/jimlang-vscode.vsix"
)

$ErrorActionPreference = 'Stop'
$enc = [Text.UTF8Encoding]::new($false)

function EnsureJar {
  if (-not (Test-Path $Jar)) { throw "Jar not found: $Jar. Run 'mvn -q -DskipTests package' first." }
}

function CopyJar {
  $server = Join-Path $ExtDir 'server'
  if (-not (Test-Path $server)) { New-Item -ItemType Directory -Force -Path $server | Out-Null }
  Copy-Item -Force $Jar (Join-Path $server 'jimlang-lsp.jar')
}

function BuildClient {
  Push-Location $ExtDir
  try {
    if (Get-Command pnpm -ErrorAction SilentlyContinue) {
      pnpm install
      pnpm run compile
    } elseif (Get-Command npm -ErrorAction SilentlyContinue) {
      npm install
      npm run compile
    } else {
      throw 'Neither pnpm nor npm is available on PATH.'
    }
  } finally { Pop-Location }
}

function ResolveVsceCommand {
  $localCmd = Join-Path $ExtDir 'node_modules/.bin/vsce.cmd'
  if (Test-Path $localCmd) { return @{ cmd = $localCmd; args = @('package','--no-dependencies','-o',$Out) } }
  $localCmd = Join-Path $ExtDir 'node_modules/.bin/vsce'
  if (Test-Path $localCmd) { return @{ cmd = $localCmd; args = @('package','--no-dependencies','-o',$Out) } }
  if (Get-Command vsce -ErrorAction SilentlyContinue) { return @{ cmd = 'vsce'; args = @('package','--no-dependencies','-o',$Out) } }
  if (Get-Command pnpm -ErrorAction SilentlyContinue) { return @{ cmd = 'pnpm'; args = @('dlx','@vscode/vsce','package','--no-dependencies','-o',$Out) } }
  if (Get-Command npx -ErrorAction SilentlyContinue) { return @{ cmd = 'npx'; args = @('@vscode/vsce','package','--no-dependencies','-o',$Out) } }
  if (Get-Command npm -ErrorAction SilentlyContinue) { return @{ cmd = 'npm'; args = @('exec','--','vsce','package','--no-dependencies','-o',$Out) } }
  throw 'vsce is not available. Install it (npm i -g @vscode/vsce) or add it as a devDependency.'
}

function PackVsix {
  if (Test-Path $Out) { Remove-Item -Force $Out }
  Push-Location $ExtDir
  try {
    $r = ResolveVsceCommand
    & $r.cmd @($r.args)
    if ($LASTEXITCODE -ne 0) { throw "vsce package failed with exit code $LASTEXITCODE" }
  } finally { Pop-Location }
}

EnsureJar
CopyJar
BuildClient
PackVsix
Write-Host "VSIX: $Out"
