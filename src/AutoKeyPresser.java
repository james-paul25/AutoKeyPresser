import java.awt.Robot;
import java.awt.event.KeyEvent;

public class AutoKeyPresser {

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();

        while (true) {
            robot.keyPress(KeyEvent.VK_3);
            robot.keyRelease(KeyEvent.VK_3);
            Thread.sleep(500); // wait 0.5 secon3241ds

            robot.keyPress(KeyEvent.VK_2);
            robot.keyRelease(KeyEvent.VK_2);
            Thread.sleep(500);

            robot.keyPress(KeyEvent.VK_4);
            robot.keyRelease(KeyEvent.VK_4);
            Thread.sleep(500);

            robot.keyPress(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_1);
            Thread.sleep(3000); // wait 3 seconds
        }
    }
}
