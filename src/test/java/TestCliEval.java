import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestCliEval {
    private static String getJavaCmd() {
        String javaHome = System.getProperty("java.home");
        File bin = new File(javaHome, "bin");
        File javaExe = new File(bin, isWindows() ? "java.exe" : "java");
        if (javaExe.exists()) return javaExe.getAbsolutePath();
        return "java";
    }
    private static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win");
    }

    private static class ProcResult { final int code; final String out; ProcResult(int c, String o){code=c;out=o;} }

    private ProcResult run(String... args) throws Exception {
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
                byte[] buf = new byte[4096]; int n; while ((n=in.read(buf))!=-1) bout.write(buf,0,n);
            } catch(IOException ignored){}
        });
        t.setDaemon(true); t.start();
        int code = p.waitFor(); t.join(2000);
        return new ProcResult(code, bout.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testEvalOption() throws Exception {
        ProcResult r = run("--eval", "println(\"OK\")");
        assertTrue(r.out.contains("OK"));
    }

    @Test
    public void testStdinDash() throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(getJavaCmd());
        cmd.add("-cp");
        cmd.add(System.getProperty("java.class.path"));
        cmd.add("com.dafei1288.jimlang.Main");
        cmd.add("-");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (OutputStreamWriter w = new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)) {
            w.write("println(42)\n");
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (InputStream in = p.getInputStream()) {
            byte[] buf = new byte[4096]; int n; while ((n=in.read(buf))!=-1) bout.write(buf,0,n);
        }
        int code = p.waitFor();
        String out = bout.toString(StandardCharsets.UTF_8);
        assertEquals(0, code);
        assertTrue(out.contains("42"));
    }
}