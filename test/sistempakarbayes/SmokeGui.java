package sistempakarbayes;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import sistempakarbayes.ui.MainFrame;

/**
 * Smoke test GUI: merender MainFrame, lalu menangkap screenshot ke file.
 * Dipakai untuk verifikasi tampilan tanpa perlu interaksi manual.
 */
public class SmokeGui {

    public static void main(String[] args) throws Exception {
        String out = args.length > 0 ? args[0] : "/tmp/mainframe.png";
        // Pastikan ada display (dijalankan di Xvfb)
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("HEADLESS - tidak bisa render GUI");
            return;
        }

        SwingUtilities.invokeAndWait(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });

        Thread.sleep(1500);
        Robot robot = new Robot();
        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        ImageIO.write(robot.createScreenCapture(rect), "png", new File(out));
        System.out.println("Screenshot tersimpan: " + out);
        System.exit(0);
    }
}