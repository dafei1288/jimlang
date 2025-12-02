import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * CLI integration tests for Main entry.
 * These tests spawn a separate java process because Main calls System.exit().
 */
public class TestMainCli {

    private static class ProcResult {
        final int exitCode;
        final String stdout;
        ProcResult(int exitCode, String stdout) { this.exitCode = exitCode; this.stdout = stdout; }
    }

    private ProcResult runMainWithOutput(Duration timeout, String... args) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(getJavaCmd());
        cmd.add("-cp");
        cmd.add(System.getProperty("java.class.path"));
        cmd.add("com.dafei1288.jimlang.Main");
        for (String a : args) cmd.add(a);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Thread t = new Thread(() -> {
            try (InputStream in = p.getInputStream()) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = in.read(buf)) != -1) {
                    bout.write(buf, 0, n);
                }
            } catch (IOException ignored) {}
        });
        t.setDaemon(true);
        t.start();

        int ec = waitFor(p, timeout);
        t.join(2000);
        String out = bout.toString(StandardCharsets.UTF_8);
        return new ProcResult(ec, out);
    }

    private static String getJavaCmd() {
        String javaHome = System.getProperty("java.home");
        File bin = new File(javaHome, "bin");
        File javaExe = new File(bin, isWindows() ? "java.exe" : "java");
        if (javaExe.exists()) return javaExe.getAbsolutePath();
        return "java"; // fallback to PATH
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win");
    }

    private static int waitFor(Process p, Duration timeout) throws InterruptedException {
        Instant end = Instant.now().plus(timeout);
        while (Instant.now().isBefore(end)) {
            try {
                return p.exitValue();
            } catch (IllegalThreadStateException ignore) {
                Thread.sleep(50);
            }
        }
        p.destroyForcibly();
        return -1;
    }

    @Test
    public void helpShowsCliFlag() throws Exception {
        ProcResult r = runMainWithOutput(Duration.ofSeconds(10), "--help");
        assertEquals(0, r.exitCode);
        assertTrue("usage should be printed", r.stdout.toLowerCase().contains("usage"));
        assertTrue("--cli should be documented", r.stdout.contains("--cli"));
        assertTrue("should mention REPL", r.stdout.toLowerCase().contains("repl"));
    }

    @Test
    public void cliStartsAndExits() throws Exception {
        // Start REPL and immediately exit via :exit
        List<String> cmd = new ArrayList<>();
        cmd.add(getJavaCmd());
        cmd.add("-cp");
        cmd.add(System.getProperty("java.class.path"));
        cmd.add("com.dafei1288.jimlang.Main");
        cmd.add("--cli");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        // Hook stdout reader
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Thread t = new Thread(() -> {
            try (InputStream in = p.getInputStream()) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = in.read(buf)) != -1) {
                    bout.write(buf, 0, n);
                }
            } catch (IOException ignored) {}
        });
        t.setDaemon(true);
        t.start();

        // Wait for prompt banner (best-effort), then send :exit
        OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8);
        // Give the REPL some time to start
        Thread.sleep(300);
        w.write(":exit\n");
        w.flush();

        int ec = waitFor(p, Duration.ofSeconds(10));
        t.join(2000);
        String out = bout.toString(StandardCharsets.UTF_8);

        assertEquals("process should exit cleanly", 0, ec);
        assertTrue("should print REPL banner", out.toLowerCase().contains("repl"));
        // It may also contain prompt or goodbye
        assertTrue("should show prompt or goodbye",
                out.contains("JimLang>") || out.toLowerCase().contains("good bye"));
    }
}