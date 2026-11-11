package sistempakarbayes.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sistempakarbayes.data.AturanDAO;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.GejalaDAO;
import sistempakarbayes.data.KerusakanDAO;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.Kerusakan;

/**
 * Panel matriks aturan P(gejala | kerusakan).
 * Baris = gejala, Kolom = kerusakan. Admin mengedit nilai di sel.
 */
public class AturanPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable tabel;
    private final JComboBox<String> cmbGejala = new JComboBox<>();
    private final JComboBox<String> cmbKerusakan = new JComboBox<>();
    private final JTextField txtNilai = new JTextField(6);
    private final List<Gejala> daftarGejala;
    private final List<Kerusakan> daftarKerusakan;

    public AturanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        daftarGejala = GejalaDAO.loadSemua();
        daftarKerusakan = KerusakanDAO.loadSemua();

        // Kolom: Gejala | K01 | K02 | K03 | ...
        String[] kolom = new String[1 + daftarKerusakan.size()];
        kolom[0] = "Gejala";
        for (int i = 0; i < daftarKerusakan.size(); i++) {
            kolom[i + 1] = daftarKerusakan.get(i).getKode();
        }

        model = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c > 0; }
        };
        tabel = new JTable(model);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(250);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // Form edit cepat
        JPanel form = new JPanel(new GridLayout(2, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Edit Nilai P(gejala | kerusakan)"));
        form.add(new JLabel("Gejala:"));
        form.add(cmbGejala);
        form.add(new JLabel("Kerusakan:"));
        form.add(cmbKerusakan);
        form.add(new JLabel("Nilai (0..1):"));
        form.add(txtNilai);
        JButton btnSimpan = new JButton("Simpan");
        JButton btnRefresh = new JButton("Refresh Matrix");
        btnSimpan.setFont(new Font("SansSerif", Font.BOLD, 12));
        form.add(btnSimpan);
        form.add(btnRefresh);
        add(form, BorderLayout.NORTH);

        // Isi combo
        for (Gejala g : daftarGejala) cmbGejala.addItem(g.getKode() + " - " + g.getNama());
        for (Kerusakan k : daftarKerusakan) cmbKerusakan.addItem(k.getKode() + " - " + k.getNama());

        btnSimpan.addActionListener(e -> simpanSatu());
        btnRefresh.addActionListener(e -> muatTabel());

        // Pilih sel = isi form
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariSel();
        });

        muatTabel();
    }

    private String kodeDariCombo(JComboBox<String> cmb) {
        String val = String.valueOf(cmb.getSelectedItem());
        return val.split(" ")[0]; // "K01 - ..." -> "K01"
    }

    private void muatTabel() {
        model.setRowCount(0);
        for (Gejala g : daftarGejala) {
            Object[] baris = new Object[1 + daftarKerusakan.size()];
            baris[0] = g.getKode() + " - " + g.getNama();
            for (int j = 0; j < daftarKerusakan.size(); j++) {
                baris[j + 1] = AturanDAO.getNilai(g.getKode(), daftarKerusakan.get(j).getKode());
            }
            model.addRow(baris);
        }
    }

    private void isiFormDariSel() {
        int baris = tabel.getSelectedRow();
        int kol = tabel.getSelectedColumn();
        if (baris < 0 || kol <= 0) return;
        // Set combo ke gejala baris
        for (int i = 0; i < cmbGejala.getItemCount(); i++) {
            if (cmbGejala.getItemAt(i).startsWith(daftarGejala.get(baris).getKode())) {
                cmbGejala.setSelectedIndex(i);
                break;
            }
        }
        // Set combo ke kerusakan kolom
        for (int i = 0; i < cmbKerusakan.getItemCount(); i++) {
            if (cmbKerusakan.getItemAt(i).startsWith(daftarKerusakan.get(kol - 1).getKode())) {
                cmbKerusakan.setSelectedIndex(i);
                break;
            }
        }
        txtNilai.setText(String.valueOf(model.getValueAt(baris, kol)));
    }

    private void simpanSatu() {
        String kodeG = kodeDariCombo(cmbGejala);
        String kodeK = kodeDariCombo(cmbKerusakan);
        double nilai;
        try {
            nilai = Double.parseDouble(txtNilai.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Nilai harus angka (contoh: 0.85).", "Peringatan",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nilai < 0 || nilai > 1) {
            JOptionPane.showMessageDialog(this,
                    "Nilai harus antara 0 dan 1.", "Peringatan",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        AturanDAO.upsert(kodeG, kodeK, nilai);
        BasisPengetahuan.refresh();
        muatTabel();
    }
}