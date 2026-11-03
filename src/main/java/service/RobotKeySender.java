package service;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

// old behavior
public class RobotKeySender implements KeySender {
    private final Robot robot;

    public RobotKeySender() throws AWTException {
        robot = new Robot();
    }

    @Override
    public void press(char c) {
        int code = KeyEvent.getExtendedKeyCodeForChar(c);
        if (code == KeyEvent.CHAR_UNDEFINED) {
            throw new IllegalArgumentException("Unsupported key: " + c);
        }
        robot.keyPress(code);
        robot.delay(30);
        robot.keyRelease(code);
    }
}