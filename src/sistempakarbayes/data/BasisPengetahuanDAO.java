package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.Kerusakan;

/**
 * Data Access Object untuk membaca basis pengetahuan dari MySQL.
 * Menggantikan data hardcode di BasisPengetahuan (versi lama).
 */
public final class BasisPengetahuanDAO {

    private BasisPengetahuanDAO() {
    }

    /** Ambil semua kerusakan (hipotesis) dari tabel kerusakan. */
    public static List<Kerusakan> loadSemuaKerusakan() {
        List<Kerusakan> list = new ArrayList<>();
        String sql = "SELECT kode, nama, prior, solusi FROM kerusakan";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Kerusakan(
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getDouble("prior"),
                        rs.getString("solusi")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat kerusakan dari database.", e);
        }
        return list;
    }

    /** Ambil semua gejala dari tabel gejala. */
    public static List<Gejala> loadSemuaGejala() {
        List<Gejala> list = new ArrayList<>();
        String sql = "SELECT kode, nama FROM gejala";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Gejala(rs.getString("kode"), rs.getString("nama")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat gejala dari database.", e);
        }
        return list;
    }

    /**
     * Muat seluruh aturan P(gejala|kerusakan) ke dalam struktur
     * Map<kodeGejala, Map<kodeKerusakan, nilai>>.
     */
    public static Map<String, Map<String, Double>> loadAturan() {
        Map<String, Map<String, Double>> prob = new HashMap<>();
        String sql = "SELECT kode_gejala, kode_kerusakan, nilai FROM aturan";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String g = rs.getString("kode_gejala");
                String k = rs.getString("kode_kerusakan");
                double n = rs.getDouble("nilai");
                prob.computeIfAbsent(g, x -> new HashMap<>()).put(k, n);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat aturan dari database.", e);
        }
        return prob;
    }
}