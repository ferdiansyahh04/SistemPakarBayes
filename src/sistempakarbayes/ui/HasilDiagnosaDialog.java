package sistempakarbayes.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.engine.BayesEngine;
import sistempakarbayes.model.Kerusakan;

/**
 * Dialog hasil diagnosa: ranking kerusakan + persentase + solusi.
 * Menampilkan peringatan bila keyakinan utama di bawah ambang batas.
 */
public class HasilDiagnosaDialog extends JDialog {

    public HasilDiagnosaDialog(JFrame parent, Map<String, Double> hasil) {
        super(parent, "Hasil Diagnosa", Dialog.ModalityType.APPLICATION_MODAL);
        setSize(560, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("=== HASIL DIAGNOSA (Teorema Bayes) ===\n\n");

        boolean pertama = true;
        for (Map.Entry<String, Double> e : hasil.entrySet()) {
            Kerusakan k = BasisPengetahuan.getKerusakan(e.getKey());
            double persen = e.getValue() * 100;
            sb.append(String.format("%-26s : %5.1f %%\n", k.getNama(), persen));

            if (pertama) {
                sb.append("\n--- Diagnosa Utama ---\n");
                sb.append("Kerusakan : ").append(k.getNama()).append('\n');
                sb.append("Keyakinan : ").append(String.format("%.1f", persen)).append(" %\n");
                sb.append("Solusi    : ").append(k.getSolusi()).append('\n');

                if (!BayesEngine.meyakinkan(e.getValue())) {
                    sb.append("\n-----------------------------------------\n");
                    sb.append("[PERINGATAN] Keyakinan diagnosa rendah\n");
                    sb.append("(di bawah ").append((int) (BayesEngine.AMBANG_KEYAKINAN * 100))
                      .append("%). Hasil bersifat saran. Sebaiknya\n");
                    sb.append("konsultasikan langsung ke teknisi.\n");
                    sb.append("-----------------------------------------\n");
                }
                pertama = false;
            }
        }

        area.setText(sb.toString());
        area.setForeground(Color.BLACK);
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}