package service;

import java.util.List;
import java.util.function.Consumer;

public class Presser {

    private static volatile boolean running = false;
    private static Thread worker;

    public static synchronized boolean isRunning() {
        return running;
    }

    public static synchronized void startAutoKey(List<String> keys,
            int delayAfterKey,
            int delayAfterAll,
            int startDelay,
            KeySender sender,
            Runnable onStopped,
            Consumer<String> onError) {
        if (running)
            return;
        running = true;

        worker = new Thread(() -> {
            boolean failed = false;
            try {
                Thread.sleep(startDelay);

                while (running) {
                    for (int i = 0; i < keys.size() && running; i++) {
                        sender.press(keys.get(i).charAt(0));
                        if (i < keys.size() - 1)
                            Thread.sleep(delayAfterKey);
                    }
                    Thread.sleep(delayAfterAll);
                }
            } catch (InterruptedException ignored) {
                // stopped by user
            } catch (Exception e) {
                failed = true;
                onError.accept(e.getMessage() == null ? e.toString() : e.getMessage());
            } finally {
                running = false;
                if (!failed)
                    onStopped.run();
            }
        }, "auto-key-presser");

        worker.setDaemon(true);
        worker.start();
    }

    public static synchronized void stopAutoKey() {
        running = false;
        if (worker != null)
            worker.interrupt();
    }
}

// package service;

// import java.awt.AWTException;
// import java.awt.Robot;
// import java.awt.event.KeyEvent;
// import java.util.List;
// import java.util.function.Consumer;

// public class Presser {

// private static final int KEY_HOLD_MS = 30;

// private static volatile boolean running = false;
// private static Thread worker;

// public static synchronized boolean isRunning() {
// return running;
// }

// public static synchronized void startAutoKey(List<String> keys,
// int delayAfterKey,
// int delayAfterAll,
// int startDelay,
// Runnable onStopped,
// Consumer<String> onError) {
// if (running)
// return;
// running = true;

// worker = new Thread(() -> {
// boolean failed = false;
// try {
// Robot robot = new Robot();
// Thread.sleep(startDelay);

// while (running) {
// for (int i = 0; i < keys.size() && running; i++) {
// pressKey(robot, keys.get(i));
// if (i < keys.size() - 1)
// Thread.sleep(delayAfterKey);
// }
// Thread.sleep(delayAfterAll);
// }
// } catch (AWTException | SecurityException e) {
// failed = true;
// onError.accept("Can't control the keyboard: " + e.getMessage());
// } catch (InterruptedException ignored) {
// // stopped by user
// } finally {
// running = false;
// if (!failed)
// onStopped.run();
// }
// }, "auto-key-presser");

// worker.setDaemon(true);
// worker.start();
// }

// public static synchronized void stopAutoKey() {
// running = false;
// if (worker != null)
// worker.interrupt();
// }

// private static void pressKey(Robot robot, String key) {
// if (key == null || key.isEmpty())
// return;

// char c = key.charAt(0);
// int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
// if (keyCode == KeyEvent.CHAR_UNDEFINED) {
// System.err.println("Invalid key: " + c);
// return;
// }

// try {
// robot.keyPress(keyCode);
// robot.delay(KEY_HOLD_MS);
// robot.keyRelease(keyCode);
// } catch (IllegalArgumentException e) {
// System.err.println("Unsupported key: " + c);
// }
// }
// }