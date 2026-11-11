package sistempakarbayes.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.KonsultasiDAO;
import sistempakarbayes.engine.BayesEngine;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.User;
import sistempakarbayes.ui.admin.AdminFrame;
import sistempakarbayes.ui.admin.RiwayatPanel;

/**
 * Form utama: user mencentang gejala, lalu menekan tombol Diagnosa.
 */
public class MainFrame extends JFrame {

    private final Map<JCheckBox, String> mapCheckKeKode = new LinkedHashMap<>();

    public MainFrame() {
        setTitle("Sistem Pakar Diagnosa Kerusakan Laptop - Teorema Bayes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Menu bar: akses login admin + riwayat
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Menu");
        JMenuItem miAdmin = new JMenuItem("Login Admin / Kelola Data");
        miAdmin.addActionListener(e -> bukaLoginAdmin());
        JMenuItem miRiwayat = new JMenuItem("Riwayat Konsultasi");
        miRiwayat.addActionListener(e -> RiwayatPanel.tampilDalamDialog(this));
        menuFile.add(miRiwayat);
        menuFile.add(miAdmin);
        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // Header
        JLabel lblJudul = new JLabel("Pilih Gejala yang Dialami Laptop Anda:", JLabel.CENTER);
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblJudul.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
        add(lblJudul, BorderLayout.NORTH);

        // Daftar checkbox gejala
        JPanel panelGejala = new JPanel(new GridLayout(0, 1, 4, 4));
        panelGejala.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        for (Gejala g : BasisPengetahuan.getSemuaGejala()) {
            JCheckBox cb = new JCheckBox(g.getKode() + " - " + g.getNama());
            cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
            mapCheckKeKode.put(cb, g.getKode());
            panelGejala.add(cb);
        }
        add(new JScrollPane(panelGejala), BorderLayout.CENTER);

        // Tombol aksi
        JPanel panelTombol = new JPanel();
        JButton btnDiagnosa = new JButton("Diagnosa");
        JButton btnReset = new JButton("Reset");
        btnDiagnosa.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnReset.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panelTombol.add(btnDiagnosa);
        panelTombol.add(btnReset);
        panelTombol.setBorder(BorderFactory.createEmptyBorder(4, 10, 12, 10));
        add(panelTombol, BorderLayout.SOUTH);

        // Event
        btnDiagnosa.addActionListener(e -> prosesDiagnosa());
        btnReset.addActionListener(e -> {
            for (JCheckBox cb : mapCheckKeKode.keySet()) cb.setSelected(false);
        });
    }

    private void bukaLoginAdmin() {
        LoginDialog dialog = new LoginDialog(this);
        dialog.setVisible(true);
        User user = dialog.getUser();
        if (user != null) {
            new AdminFrame(user).setVisible(true);
        }
    }

    private void prosesDiagnosa() {
        List<String> terpilih = new ArrayList<>();
        for (Map.Entry<JCheckBox, String> e : mapCheckKeKode.entrySet()) {
            if (e.getKey().isSelected()) {
                terpilih.add(e.getValue());
            }
        }

        if (terpilih.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Silakan pilih minimal satu gejala terlebih dahulu.",
                    "Belum Ada Gejala", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Double> hasil = BayesEngine.diagnosa(terpilih);

        // Simpan riwayat konsultasi
        String nama = JOptionPane.showInputDialog(this,
                "Nama Anda (untuk riwayat konsultasi):",
                "Identitas", JOptionPane.QUESTION_MESSAGE);
        if (nama == null) return; // user batal
        nama = nama.trim();
        if (nama.isEmpty()) nama = "Tanpa Nama";

        String kodeUtama = hasil.entrySet().iterator().next().getKey();
        double nilaiUtama = hasil.entrySet().iterator().next().getValue();
        try {
            KonsultasiDAO.simpan(nama, kodeUtama, nilaiUtama, terpilih);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan riwayat: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        new HasilDiagnosaDialog(this, hasil).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
