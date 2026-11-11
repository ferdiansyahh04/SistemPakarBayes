package sistempakarbayes.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.KerusakanDAO;
import sistempakarbayes.model.Kerusakan;

/**
 * Panel kelola data kerusakan (CRUD): tambah, ubah, hapus.
 */
public class KerusakanPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable tabel;

    private final JTextField txtKode = new JTextField(8);
    private final JTextField txtNama = new JTextField(25);
    private final JTextField txtPrior = new JTextField(8);
    private final JTextArea txtSolusi = new JTextArea(3, 25);

    public KerusakanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabel
        model = new DefaultTableModel(
                new Object[]{"Kode", "Nama", "Prior P(H)", "Solusi"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(320);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // Form input
        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Form Kerusakan"));
        form.add(new JLabel("Kode:"));
        form.add(txtKode);
        form.add(new JLabel("Nama Kerusakan:"));
        form.add(txtNama);
        form.add(new JLabel("Prior P(H) (0..1):"));
        form.add(txtPrior);
        form.add(new JLabel("Solusi:"));
        form.add(new JScrollPane(txtSolusi));
        add(form, BorderLayout.NORTH);

        // Tombol
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Simpan Perubahan");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBatal = new JButton("Bersihkan");
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBatal);
        add(tombol, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBatal.addActionListener(e -> bersihkanForm());

        muatTabel();
    }

    private void muatTabel() {
        model.setRowCount(0);
        List<Kerusakan> list = KerusakanDAO.loadSemua();
        for (Kerusakan k : list) {
            model.addRow(new Object[]{k.getKode(), k.getNama(), k.getPrior(), k.getSolusi()});
        }
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return;
        txtKode.setText(String.valueOf(model.getValueAt(baris, 0)));
        txtNama.setText(String.valueOf(model.getValueAt(baris, 1)));
        txtPrior.setText(String.valueOf(model.getValueAt(baris, 2)));
        txtSolusi.setText(String.valueOf(model.getValueAt(baris, 3)));
    }

    private void bersihkanForm() {
        txtKode.setText("");
        txtNama.setText("");
        txtPrior.setText("");
        txtSolusi.setText("");
        tabel.clearSelection();
    }

    private Kerusakan bacaForm() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String priorStr = txtPrior.getText().trim();
        String solusi = txtSolusi.getText().trim();
        if (kode.isEmpty() || nama.isEmpty() || priorStr.isEmpty() || solusi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua kolom wajib diisi.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double prior;
        try {
            prior = Double.parseDouble(priorStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Prior harus angka (contoh: 0.25).", "Peringatan",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (prior < 0 || prior > 1) {
            JOptionPane.showMessageDialog(this,
                    "Prior harus antara 0 dan 1.", "Peringatan",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Kerusakan(kode, nama, prior, solusi);
    }

    private void tambah() {
        Kerusakan k = bacaForm();
        if (k == null) return;
        KerusakanDAO.insert(k);
        BasisPengetahuan.refresh();
        muatTabel();
        bersihkanForm();
        JOptionPane.showMessageDialog(this, "Kerusakan berhasil ditambahkan.");
    }

    private void ubah() {
        Kerusakan k = bacaForm();
        if (k == null) return;
        KerusakanDAO.update(k);
        BasisPengetahuan.refresh();
        muatTabel();
        bersihkanForm();
        JOptionPane.showMessageDialog(this, "Perubahan disimpan.");
    }

    private void hapus() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih baris yang akan dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String kode = String.valueOf(model.getValueAt(baris, 0));
        int konfirm = JOptionPane.showConfirmDialog(this,
                "Hapus kerusakan '" + kode + "'? Aturan terkait ikut terhapus.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;
        KerusakanDAO.delete(kode);
        BasisPengetahuan.refresh();
        muatTabel();
        bersihkanForm();
    }
}