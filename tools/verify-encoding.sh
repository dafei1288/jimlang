#!/usr/bin/env bash
set -euo pipefail
# Verify repository file encodings: UTF-8 (no BOM). Optional EOL checks via STRICT_EOL=1
root_dir="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root_dir"

if command -v python3 >/dev/null 2>&1; then
python3 - "$@" << 'PY'
import os, sys
from pathlib import Path
STRICT = os.environ.get('STRICT_EOL') == '1'
include_ext = {'.java','.md','.g4','.sh','.ps1','.yml','.yaml','.json','.cmd','.bat','.txt','.xml','.properties'}
exclude_dirs = {'.git','target','.idea','.vscode'}

errors = 0
warns = 0

for root, dirs, files in os.walk('.'):
    dirs[:] = [d for d in dirs if d not in exclude_dirs]
    for name in files:
        p = Path(root)/name
        if p.suffix.lower() not in include_ext: continue
        b = p.read_bytes()
        if b[:3] == b'\xef\xbb\xbf':
            print(f"BOM found: {p}")
            errors += 1
            continue
        try:
            s = b.decode('utf-8')
        except UnicodeDecodeError as e:
            print(f"Invalid UTF-8: {p} -> {e}")
            errors += 1
            continue
        if STRICT:
            if p.suffix.lower() not in {'.cmd','.bat'} and '\r\n' in s:
                print(f"EOL warning (CRLF in text): {p}")
                warns += 1
            if p.suffix.lower() in {'.cmd','.bat'} and ('\n' in s and '\r\n' not in s):
                print(f"EOL warning (LF without CR in .cmd/.bat): {p}")
                warns += 1

if errors:
    print(f"Encoding check FAILED: {errors} error(s).", file=sys.stderr)
    sys.exit(1)
else:
    print(f"Encoding check OK. Warnings: {warns}")
PY
else
  echo "warn: python3 not found; skipping verify-encoding" >&2
fi