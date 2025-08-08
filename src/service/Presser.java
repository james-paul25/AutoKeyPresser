package service;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Presser {
    public static void autoKey(String key1, String key2, String key3,
                               String key4, String key5, int sleepTimeAfterOneKey, int sleepTimeAfterAllKey) {
        try {
            Robot robot = new Robot();

            while (true) {
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
    }

    private static void pressKey(Robot robot, String key) {
        if (key == null || key.isEmpty()) return;

        char c = key.charAt(0);
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
        if (KeyEvent.CHAR_UNDEFINED == keyCode) {
            System.err.println("Key code not found for character: " + c);
            return;
        }

        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }
}
