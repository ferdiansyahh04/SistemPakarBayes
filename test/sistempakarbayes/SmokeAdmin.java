package sistempakarbayes;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import sistempakarbayes.model.User;
import sistempakarbayes.ui.admin.AdminFrame;

/**
 * Smoke test GUI panel admin: render AdminFrame + screenshot.
 */
public class SmokeAdmin {

    public static void main(String[] args) throws Exception {
        String out = args.length > 0 ? args[0] : "/tmp/adminframe.png";
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("HEADLESS");
            return;
        }
        User u = new User(1, "admin", "", "Administrator", "admin");
        SwingUtilities.invokeAndWait(() -> new AdminFrame(u).setVisible(true));
        Thread.sleep(1500);
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        ImageIO.write(robot.createScreenCapture(rect), "png", new File(out));
        System.out.println("Screenshot tersimpan: " + out);
        System.exit(0);
    }
}