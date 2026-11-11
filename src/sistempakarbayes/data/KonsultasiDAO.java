package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sistempakarbayes.model.Konsultasi;

/**
 * DAO untuk riwayat konsultasi (simpan, baca, hapus).
 */
public final class KonsultasiDAO {

    private KonsultasiDAO() {
    }

    /**
     * Simpan satu konsultasi beserta detail gejala, mengembalikan id baru.
     */
    public static int simpan(String namaUser, String kodeHasil, double nilaiHasil,
                             List<String> kodeGejala) {
        Connection c = null;
        try {
            c = Database.getConnection();
            c.setAutoCommit(false);

            String sqlK = "INSERT INTO konsultasi (nama_user, hasil_kode, hasil_nilai) VALUES (?,?,?)";
            int id;
            try (PreparedStatement ps = c.prepareStatement(sqlK, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, namaUser);
                ps.setString(2, kodeHasil);
                ps.setDouble(3, nilaiHasil);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    id = rs.getInt(1);
                }
            }

            String sqlD = "INSERT INTO detail_konsultasi (id_konsultasi, kode_gejala) VALUES (?,?)";
            try (PreparedStatement ps = c.prepareStatement(sqlD)) {
                for (String g : kodeGejala) {
                    ps.setInt(1, id);
                    ps.setString(2, g);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            return id;
        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); } catch (SQLException ignore) { }
            }
            throw new RuntimeException("Gagal menyimpan konsultasi.", e);
        } finally {
            if (c != null) {
                try { c.close(); } catch (SQLException ignore) { }
            }
        }
    }

    /** Ambil semua riwayat konsultasi (terbaru dulu). */
    public static List<Konsultasi> loadSemua() {
        List<Konsultasi> list = new ArrayList<>();
        String sql = "SELECT id, nama_user, tanggal, hasil_kode, hasil_nilai FROM konsultasi ORDER BY tanggal DESC, id DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                List<String> gejala = loadDetail(id);
                LocalDateTime t = rs.getTimestamp("tanggal").toLocalDateTime();
                list.add(new Konsultasi(
                        id,
                        rs.getString("nama_user"),
                        t,
                        rs.getString("hasil_kode"),
                        rs.getDouble("hasil_nilai"),
                        gejala));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat riwayat konsultasi.", e);
        }
        return list;
    }

    private static List<String> loadDetail(int idKonsultasi) {
        List<String> gejala = new ArrayList<>();
        String sql = "SELECT kode_gejala FROM detail_konsultasi WHERE id_konsultasi=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKonsultasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) gejala.add(rs.getString("kode_gejala"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat detail konsultasi.", e);
        }
        return gejala;
    }

    /** Hapus satu riwayat (cascade detail). */
    public static void delete(int id) {
        String sql = "DELETE FROM konsultasi WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus konsultasi.", e);
        }
    }

    /** Jumlah total konsultasi (untuk statistik). */
    public static int count() {
        String sql = "SELECT COUNT(*) FROM konsultasi";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghitung konsultasi.", e);
        }
    }
}