package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class WindowFinder {
    private WindowFinder() {
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static boolean isLinux() {
        return System.getProperty("os.name", "").toLowerCase().contains("linux");
    }

    public static List<WindowInfo> find(String text) throws Exception {
        if (isWindows())
            return WindowsBackend.find(text);
        if (isLinux())
            return findLinux(text);
        throw new UnsupportedOperationException("Targeting a window is only supported on Windows and Linux (X11).");
    }

    public static KeySender senderFor(WindowInfo window) {
        if (window.isGlobal())
            throw new IllegalArgumentException("No target window selected.");
        if (isWindows())
            return WindowsBackend.sender(window);
        return new XdotoolKeySender(window.id());
    }

    private static List<WindowInfo> findLinux(String text) throws Exception {
        List<WindowInfo> result = new ArrayList<>();
        for (String id : run("xdotool", "search", "--onlyvisible", "--name", text)) {
            List<String> names = run("xdotool", "getwindowname", id);
            String title = names.isEmpty() ? "(no title)" : names.get(0);
            result.add(new WindowInfo(id, title));
        }
        return result;
    }

    private static List<String> run(String... cmd) throws Exception {
        Process p;
        try {
            p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        } catch (IOException e) {
            throw new IllegalStateException("xdotool is not installed. Run: sudo apt install xdotool");
        }
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        p.waitFor();
        List<String> lines = new ArrayList<>();
        for (String line : out.split("\\R")) {
            if (!line.isBlank())
                lines.add(line.trim());
        }
        return lines;
    }
}