param(
  [switch]$StrictEol
)
# Verify repository file encodings: UTF-8 (no BOM). Optionally check EOLs.
# Excludes: .git, target, build artifacts.

$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Resolve-Path (Join-Path $here '..')
Set-Location $root

$enc = New-Object System.Text.UTF8Encoding($false, $true) # no BOM, throw on invalid

$includeExt = @(
  '.java','.md','.g4','.sh','.ps1','.yml','.yaml','.json','.cmd','.bat','.txt','.xml','.properties'
)
$excludeDirs = @('\.git\','\btarget\b','\b\.idea\b','\b\.vscode\b')

$files = Get-ChildItem -Recurse -File | Where-Object {
  $p = $_.FullName
  -not ($excludeDirs | Where-Object { $p -match $_ }) -and ($includeExt -contains ([IO.Path]::GetExtension($p).ToLowerInvariant()))
}

$errors = 0
$warns = 0

function Test-Bom {
  param([byte[]]$Bytes)
  if ($Bytes.Length -ge 3) {
    return ($Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF)
  }
  return $false
}

foreach($f in $files){
  try {
    $bytes = [IO.File]::ReadAllBytes($f.FullName)
    $hasBom = Test-Bom -Bytes $bytes
    if ($hasBom) {
      Write-Host "BOM found: $($f.FullName)" -ForegroundColor Red
      $errors++
      continue
    }
    # decode to ensure valid UTF-8
    [void]$enc.GetString($bytes)

    # EOL checks (warnings by default)
    $ext = [IO.Path]::GetExtension($f.Name).ToLowerInvariant()
    if ($StrictEol) {
      $content = [System.Text.Encoding]::UTF8.GetString($bytes)
      if ($ext -ne '.cmd' -and $ext -ne '.bat'){
        if ($content -match "\r\n"){
          Write-Host "EOL warning (CRLF in text): $($f.FullName)" -ForegroundColor Yellow
          $warns++
        }
      } else {
        if ($content -match "(?<!\r)\n"){
          Write-Host "EOL warning (LF without CR in .cmd/.bat): $($f.FullName)" -ForegroundColor Yellow
          $warns++
        }
      }
    }
  } catch {
    Write-Host "Invalid UTF-8: $($f.FullName) -> $($_.Exception.GetType().Name)" -ForegroundColor Red
    $errors++
  }
}

if ($errors -gt 0){
  Write-Host "Encoding check FAILED: $errors error(s)." -ForegroundColor Red
  exit 1
} else {
  Write-Host "Encoding check OK. Warnings: $warns" -ForegroundColor Green
}