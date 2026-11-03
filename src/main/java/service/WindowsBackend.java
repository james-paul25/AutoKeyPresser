package service;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows: posts key messages directly to one window handle, without focusing
 * it.
 */
final class WindowsBackend {
    private WindowsBackend() {
    }

    private static final int WM_KEYDOWN = 0x0100;
    private static final int WM_KEYUP = 0x0101;
    private static final int WM_CHAR = 0x0102;

    static List<WindowInfo> find(String text) {
        List<WindowInfo> out = new ArrayList<>();
        String needle = text.toLowerCase();
        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            if (User32.INSTANCE.IsWindowVisible(hwnd)) {
                char[] buf = new char[512];
                User32.INSTANCE.GetWindowText(hwnd, buf, buf.length);
                String title = Native.toString(buf);
                if (!title.isBlank() && title.toLowerCase().contains(needle)) {
                    out.add(new WindowInfo(String.valueOf(Pointer.nativeValue(hwnd.getPointer())), title));
                }
            }
            return true;
        }, null);
        return out;
    }

    static KeySender sender(WindowInfo window) {
        HWND hwnd = new HWND(Pointer.createConstant(Long.parseLong(window.id())));
        return c -> {
            User32 user32 = User32.INSTANCE;
            if (!user32.IsWindow(hwnd)) {
                throw new IllegalStateException("Target window was closed.");
            }
            int vk = KeyEvent.getExtendedKeyCodeForChar(c);
            if (vk == KeyEvent.CHAR_UNDEFINED) {
                throw new IllegalArgumentException("Unsupported key: " + c);
            }
            user32.PostMessage(hwnd, WM_KEYDOWN, new WPARAM(vk), new LPARAM(0x00000001L));
            user32.PostMessage(hwnd, WM_CHAR, new WPARAM(c), new LPARAM(0x00000001L));
            user32.PostMessage(hwnd, WM_KEYUP, new WPARAM(vk), new LPARAM(0xC0000001L));
        };
    }
}