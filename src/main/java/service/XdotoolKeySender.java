package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// for linux
public class XdotoolKeySender implements KeySender {
    private final String windowId;

    public XdotoolKeySender(String windowId) {
        this.windowId = windowId;
    }

    @Override
    public void press(char c) throws Exception {
        String keysym = (c == ' ') ? "space" : String.valueOf(c);
        Process p = new ProcessBuilder("xdotool", "key", "--window", windowId, keysym)
                .redirectErrorStream(true)
                .start();
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        if (p.waitFor() != 0) {
            throw new IOException("Target window is gone or rejected the key. " + out);
        }
    }
}