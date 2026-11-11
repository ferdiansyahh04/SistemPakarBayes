package sistempakarbayes.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.GejalaDAO;
import sistempakarbayes.model.Gejala;

/**
 * Panel kelola data gejala (CRUD).
 */
public class GejalaPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable tabel;
    private final JTextField txtKode = new JTextField(8);
    private final JTextField txtNama = new JTextField(30);

    public GejalaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(new Object[]{"Kode", "Nama Gejala"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Form Gejala"));
        form.add(new JLabel("Kode:"));
        form.add(txtKode);
        form.add(new JLabel("Nama Gejala:"));
        form.add(txtNama);
        add(form, BorderLayout.NORTH);

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
        for (Gejala g : GejalaDAO.loadSemua()) {
            model.addRow(new Object[]{g.getKode(), g.getNama()});
        }
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return;
        txtKode.setText(String.valueOf(model.getValueAt(baris, 0)));
        txtNama.setText(String.valueOf(model.getValueAt(baris, 1)));
    }

    private void bersihkanForm() {
        txtKode.setText("");
        txtNama.setText("");
        tabel.clearSelection();
    }

    private Gejala bacaForm() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Kode dan nama gejala wajib diisi.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new Gejala(kode, nama);
    }

    private void tambah() {
        Gejala g = bacaForm();
        if (g == null) return;
        GejalaDAO.insert(g);
        BasisPengetahuan.refresh();
        muatTabel();
        bersihkanForm();
        JOptionPane.showMessageDialog(this, "Gejala berhasil ditambahkan.");
    }

    private void ubah() {
        Gejala g = bacaForm();
        if (g == null) return;
        GejalaDAO.update(g);
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
                "Hapus gejala '" + kode + "'? Aturan terkait ikut terhapus.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;
        GejalaDAO.delete(kode);
        BasisPengetahuan.refresh();
        muatTabel();
        bersihkanForm();
    }
}