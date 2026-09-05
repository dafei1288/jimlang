#!/usr/bin/env bash
set -euo pipefail

# Config (override with env)
JAR_SRC=${JAR_SRC:-/mnt/d/working/mycode/jimlang/target/jimlang-1.0-SNAPSHOT-jar-with-dependencies.jar}
JRE_OUT=${JRE_OUT:-$PWD/out/jre-jim-linux}
ROOT=${ROOT:-$PWD/out/rootfs}
OUT=${OUT:-$PWD/out}
KERNEL=${KERNEL:-/boot/vmlinuz-$(uname -r)}

[ "${DEBUG:-}" = "1" ] && set -x

need() { command -v "$1" >/dev/null || { echo "[ERR] missing tool: $1"; exit 1; }; }
need java
need cpio
need gzip
need qemu-system-x86_64
need ldd
if [ ! -x /bin/busybox ] && [ ! -x /usr/bin/busybox ]; then
  echo "[ERR] busybox not installed"; exit 1;
fi

# 1) Build Linux JRE via jlink
if [ ! -x "$JRE_OUT/bin/java" ]; then
  echo "[mkos] Building Linux JRE via jlink..."
  if [ "${MINIMAL:-}" = "1" ]; then
    MODS="java.base"
  else
    MODS=$(jdeps --ignore-missing-deps --multi-release 21 --print-module-deps "$JAR_SRC" 2>/dev/null || true)
    [ -z "$MODS" ] && MODS="java.base"
    [ "${WITH_WEB:-}" = "1" ] && MODS="$MODS,jdk.httpserver"
    [ "${WITH_SQL:-}" = "1" ] && MODS="$MODS,java.sql"
    [ -n "${EXTRA_MODULES:-}" ] && MODS="$MODS,${EXTRA_MODULES}"
    MODS=$(echo "$MODS" | tr -d " ")
  fi
  echo "[mkos] Modules: $MODS"
  rm -rf "$JRE_OUT"
  jlink --no-header-files --no-man-pages --strip-debug --compress=2 --add-modules "$MODS" --output "$JRE_OUT"
fi

# 2) Assemble rootfs
echo "[mkos] Assembling rootfs..."
sudo rm -rf "$ROOT"
sudo mkdir -p "$ROOT"/{bin,sbin,proc,sys,dev,jim}
if [ -x /bin/busybox ]; then
  sudo cp /bin/busybox "$ROOT/bin"
else
  sudo cp /usr/bin/busybox "$ROOT/bin"
fi
sudo ln -sf busybox "$ROOT/bin/sh"
sudo ln -sf busybox "$ROOT/bin/mount"
sudo ln -sf busybox "$ROOT/bin/poweroff"
sudo ln -sf busybox "$ROOT/bin/sleep"

sudo cp -r "$JRE_OUT" "$ROOT/jim/jre"
sudo cp "$JAR_SRC" "$ROOT/jim/jimlang.jar"

# 2.1) Copy dynamic libs required by java/libjvm into rootfs (glibc, loader, etc.)
collect_libs() {
  ldd "$1" | awk '{for(i=1;i<=NF;i++){ if($i=="=>"){ if($(i+1) ~ /^\//) print $(i+1)} else if($i ~ /^\//) print $i}}' | sort -u
}
LIBS=""
for b in "$JRE_OUT/bin/java" "$JRE_OUT/lib/server/libjvm.so"; do
  [ -e "$b" ] && LIBS="$LIBS
$(collect_libs "$b")"
done
LIBS=$(echo "$LIBS" | sed '/^$/d' | sort -u || true)
if [ -n "$LIBS" ]; then
  echo "[mkos] Copying runtime libs:"
  while IFS= read -r f; do
    [ -z "$f" ] && continue
    dest="$ROOT$(dirname "$f")"
    echo "  + $f -> $dest"
    sudo mkdir -p "$dest"
    sudo cp -L "$f" "$dest/" || true
  done <<EOF
$LIBS
EOF
fi

# 3) /init (no set -e/||, absolute busybox paths)
cat <<'EOT' | sudo tee "$ROOT/init" >/dev/null
#!/bin/sh
/bin/busybox mount -t proc  proc /proc
/bin/busybox mount -t sysfs sys  /sys
/bin/busybox mount -t devtmpfs dev /dev
echo "[init] Booting JimLang REPL..."
/jim/jre/bin/java -Xms32m -Xmx128m -jar /jim/jimlang.jar --cli
echo "[init] REPL exited; powering off"
exec /bin/busybox poweroff -f
EOT
sudo chmod +x "$ROOT/init"

# 4) Pack initramfs
mkdir -p "$OUT"
( cd "$ROOT" && sudo find . -print0 | cpio --null -ov --format=newc | gzip -9 > "$OUT/rootfs.cpio.gz" )
echo "[mkos] Initramfs: $OUT/rootfs.cpio.gz"

# 5) Kernel path (copy unreadable to local)
if [ ! -f "$KERNEL" ]; then
  CAND=$(ls -1 /boot/vmlinuz-* 2>/dev/null | tail -n1 || true)
  if [ -z "$CAND" ]; then
    echo "[mkos] Kernel not found at $KERNEL";
    echo "       set KERNEL=/path/to/bzImage and re-run.";
    exit 1
  fi
  KERNEL="$CAND"
  echo "[mkos] Auto-selected kernel: $KERNEL"
fi
if [ ! -r "$KERNEL" ]; then
  mkdir -p "$OUT/kernel"
  sudo cp "$KERNEL" "$OUT/kernel/vmlinuz"
  sudo chown "$(id -u)":"$(id -g)" "$OUT/kernel/vmlinuz"
  chmod 644 "$OUT/kernel/vmlinuz"
  KERNEL="$OUT/kernel/vmlinuz"
  echo "[mkos] Copied unreadable kernel to $KERNEL"
fi

# 6) Boot
exec qemu-system-x86_64 -m 512M -cpu max -nographic -kernel "$KERNEL" -initrd "$OUT/rootfs.cpio.gz" -append "console=ttyS0"