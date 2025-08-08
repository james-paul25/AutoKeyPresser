package service;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Presser {
    private static volatile boolean running = false;

    public static void startAutoKey(String key1, String key2, String key3,
                                    String key4, String key5, int sleepTimeAfterOneKey, int sleepTimeAfterAllKey) {
        running = true;

        new Thread(() -> {
            try {
                Robot robot = new Robot();

                while (running) {
                    pressKey(robot, key1);
                    Thread.sleep(sleepTimeAfterOneKey);

                    pressKey(robot, key2);
                    Thread.sleep(sleepTimeAfterOneKey);

                    pressKey(robot, key3);
                    Thread.sleep(sleepTimeAfterOneKey);

                    pressKey(robot, key4);
                    Thread.sleep(sleepTimeAfterOneKey);

                    pressKey(robot, key5);
                    Thread.sleep(sleepTimeAfterAllKey);
                }

            } catch (AWTException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopAutoKey() {
        running = false;
    }

    private static void pressKey(Robot robot, String key) {
        if (key == null || key.isEmpty()) return;

        char c = key.charAt(0);
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
        if (KeyEvent.CHAR_UNDEFINED == keyCode) {
            System.err.println("Invalid key: " + c);
            return;
        }

        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }
}
