package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CRUD nilai aturan P(gejala | kerusakan).
 * Menggunakan UPSERT: bila kombinasi gejala-kerusakan sudah ada, nilai di-update.
 */
public final class AturanDAO {

    private AturanDAO() {
    }

    /** Ambil nilai P(gejala|kerusakan); 0.0 bila tidak ada aturan. */
    public static double getNilai(String kodeGejala, String kodeKerusakan) {
        String sql = "SELECT nilai FROM aturan WHERE kode_gejala=? AND kode_kerusakan=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            ps.setString(2, kodeKerusakan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("nilai") : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal membaca nilai aturan.", e);
        }
    }

    /** Simpan atau perbarui nilai P(gejala|kerusakan). */
    public static void upsert(String kodeGejala, String kodeKerusakan, double nilai) {
        String sql = "INSERT INTO aturan (kode_gejala, kode_kerusakan, nilai) VALUES (?,?,?) "
                   + "ON DUPLICATE KEY UPDATE nilai = VALUES(nilai)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            ps.setString(2, kodeKerusakan);
            ps.setDouble(3, nilai);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan nilai aturan.", e);
        }
    }

    /** Hapus semua aturan untuk satu gejala (dipakai saat hapus gejala/matrix). */
    public static void deleteByGejala(String kodeGejala) {
        String sql = "DELETE FROM aturan WHERE kode_gejala=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus aturan gejala.", e);
        }
    }

    /** Hapus satu aturan spesifik. */
    public static void delete(String kodeGejala, String kodeKerusakan) {
        String sql = "DELETE FROM aturan WHERE kode_gejala=? AND kode_kerusakan=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kodeGejala);
            ps.setString(2, kodeKerusakan);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus aturan.", e);
        }
    }
}