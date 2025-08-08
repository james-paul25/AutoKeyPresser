package service;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Presser {
    public void autoKey(){
        try {
            Robot robot = new Robot();

            while (true) {
                robot.keyPress(KeyEvent.VK_3);
                robot.keyRelease(KeyEvent.VK_3);
                Thread.sleep(500); // .5s

                robot.keyPress(KeyEvent.VK_2);
                robot.keyRelease(KeyEvent.VK_2);
                Thread.sleep(500);

                robot.keyPress(KeyEvent.VK_4);
                robot.keyRelease(KeyEvent.VK_4);
                Thread.sleep(500);

                robot.keyPress(KeyEvent.VK_1);
                robot.keyRelease(KeyEvent.VK_1);
                Thread.sleep(3000); // 3s
            }

        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
