package sistempakarbayes.ui.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.KonsultasiDAO;
import sistempakarbayes.model.Kerusakan;
import sistempakarbayes.model.Konsultasi;

/**
 * Panel riwayat konsultasi + cetak laporan.
 * Bisa dipakai sebagai tab di AdminFrame, atau dibungkus dialog standalone.
 */
public class RiwayatPanel extends JPanel {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final DefaultTableModel model;
    private final JTable tabel;

    public RiwayatPanel() {
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Riwayat Konsultasi Diagnosa", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nama", "Tanggal", "Hasil", "Keyakinan"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel = new JTable(model);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(220);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(80);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnDetail = new JButton("Lihat Detail");
        JButton btnCetak = new JButton("Cetak Laporan");
        JButton btnHapus = new JButton("Hapus Riwayat");
        JButton btnRefresh = new JButton("Refresh");
        tombol.add(btnDetail);
        tombol.add(btnCetak);
        tombol.add(btnHapus);
        tombol.add(btnRefresh);
        add(tombol, BorderLayout.SOUTH);

        btnDetail.addActionListener(e -> lihatDetail());
        btnCetak.addActionListener(e -> cetakLaporan());
        btnHapus.addActionListener(e -> hapus());
        btnRefresh.addActionListener(e -> muatTabel());

        muatTabel();
    }

    /**
     * Tampilkan panel ini di dalam dialog (dipakai dari menu MainFrame).
     */
    public static void tampilDalamDialog(JFrame parent) {
        JDialog d = new JDialog(parent, "Riwayat Konsultasi", true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setSize(780, 480);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());
        d.add(new RiwayatPanel(), BorderLayout.CENTER);
        d.setVisible(true);
    }

    private void muatTabel() {
        model.setRowCount(0);
        for (Konsultasi k : KonsultasiDAO.loadSemua()) {
            String namaKerusakan = k.getKodeHasil() == null
                    ? "-"
                    : BasisPengetahuan.getKerusakan(k.getKodeHasil()).getNama();
            model.addRow(new Object[]{
                    k.getId(),
                    k.getNamaUser(),
                    k.getTanggal().format(FMT),
                    namaKerusakan,
                    String.format("%.1f%%", k.getNilaiHasil() * 100)});
        }
    }

    private Konsultasi konsultasiTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih salah satu baris terlebih dahulu.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) model.getValueAt(baris, 0);
        for (Konsultasi k : KonsultasiDAO.loadSemua()) {
            if (k.getId() == id) return k;
        }
        return null;
    }

    private void lihatDetail() {
        Konsultasi k = konsultasiTerpilih();
        if (k == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Nama      : ").append(k.getNamaUser()).append('\n');
        sb.append("Tanggal   : ").append(k.getTanggal().format(FMT)).append("\n\n");
        sb.append("Gejala yang dipilih:\n");
        for (String g : k.getKodeGejala()) {
            var gejala = BasisPengetahuan.getSemuaGejala().stream()
                    .filter(x -> x.getKode().equals(g)).findFirst().orElse(null);
            sb.append("  - ").append(gejala != null ? gejala.getNama() : g).append('\n');
        }
        sb.append("\nHasil diagnosa utama:\n");
        Kerusakan hasil = k.getKodeHasil() == null
                ? null : BasisPengetahuan.getKerusakan(k.getKodeHasil());
        if (hasil != null) {
            sb.append("  ").append(hasil.getNama())
              .append(" (").append(String.format("%.1f%%", k.getNilaiHasil() * 100))
              .append(")\n\nSolusi:\n  ").append(hasil.getSolusi());
        } else {
            sb.append("  (tidak ada data kerusakan)");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 320));
        JOptionPane.showMessageDialog(this, scroll, "Detail Konsultasi",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cetakLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("LAPORAN RIWAYAT KONSULTASI\n");
        sb.append("Sistem Pakar Diagnosa Kerusakan Laptop\n");
        sb.append("Metode: Teorema Bayes\n");
        sb.append("========================================\n\n");
        sb.append(String.format("%-4s %-18s %-16s %-26s %-10s%n",
                "ID", "Nama", "Tanggal", "Hasil", "Keyakinan"));
        sb.append("----------------------------------------------------------------------\n");
        for (Konsultasi k : KonsultasiDAO.loadSemua()) {
            String namaKerusakan = k.getKodeHasil() == null
                    ? "-" : BasisPengetahuan.getKerusakan(k.getKodeHasil()).getNama();
            sb.append(String.format("%-4d %-18s %-16s %-26s %-10s%n",
                    k.getId(),
                    k.getNamaUser(),
                    k.getTanggal().format(FMT),
                    namaKerusakan,
                    String.format("%.1f%%", k.getNilaiHasil() * 100)));
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Total konsultasi: ").append(KonsultasiDAO.count()).append('\n');

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 400));

        int pilih = JOptionPane.showConfirmDialog(this, scroll,
                "Pratinjau Laporan", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (pilih == JOptionPane.OK_OPTION) {
            cetakKePrinter(sb.toString());
        }
    }

    private void cetakKePrinter(String teks) {
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics;
            g2.translate(40, 60);
            int y = 0;
            for (String baris : teks.split("\n", -1)) {
                g2.drawString(baris, 0, y);
                y += 14;
            }
            return java.awt.print.Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Gagal mencetak: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapus() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(this,
                    "Pilih riwayat yang akan dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(baris, 0);
        int konfirm = JOptionPane.showConfirmDialog(this,
                "Hapus riwayat konsultasi ID " + id + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirm != JOptionPane.YES_OPTION) return;
        KonsultasiDAO.delete(id);
        muatTabel();
    }
}