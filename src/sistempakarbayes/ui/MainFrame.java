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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.engine.BayesEngine;
import sistempakarbayes.model.Gejala;

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
        new HasilDiagnosaDialog(this, hasil).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
