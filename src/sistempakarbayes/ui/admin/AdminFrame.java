package sistempakarbayes.ui.admin;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import sistempakarbayes.model.User;

/**
 * Jendela utama admin: kelola kerusakan, gejala, dan nilai aturan.
 */
public class AdminFrame extends JFrame {

    public AdminFrame(User user) {
        setTitle("Panel Admin - Sistem Pakar Diagnosa Kerusakan Laptop");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // Header: identitas user login
        JLabel lblInfo = new JLabel(
                "Login: " + user.getNama() + " (" + user.getRole() + ")",
                JLabel.RIGHT);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(8, 10, 2, 14));
        add(lblInfo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Kerusakan", new KerusakanPanel());
        tabs.addTab("Gejala", new GejalaPanel());
        tabs.addTab("Nilai Aturan (Pakar)", new AturanPanel());
        tabs.addTab("Riwayat", new RiwayatPanel());
        add(tabs, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        JLabel lblCatatan = new JLabel(
                "Perubahan data akan langsung memengaruhi hasil diagnosa pada konsultasi berikutnya.");
        footer.add(lblCatatan);
        add(footer, BorderLayout.SOUTH);
    }
}