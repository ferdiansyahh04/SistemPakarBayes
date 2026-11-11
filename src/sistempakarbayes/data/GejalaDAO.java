package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistempakarbayes.model.Gejala;

/**
 * CRUD data gejala (evidence).
 */
public final class GejalaDAO {

    private GejalaDAO() {
    }

    public static List<Gejala> loadSemua() {
        List<Gejala> list = new ArrayList<>();
        String sql = "SELECT kode, nama FROM gejala ORDER BY kode";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Gejala(rs.getString("kode"), rs.getString("nama")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal memuat gejala.", e);
        }
        return list;
    }

    public static void insert(Gejala g) {
        String sql = "INSERT INTO gejala (kode, nama) VALUES (?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getKode());
            ps.setString(2, g.getNama());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah gejala.", e);
        }
    }

    public static void update(Gejala g) {
        String sql = "UPDATE gejala SET nama=? WHERE kode=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getNama());
            ps.setString(2, g.getKode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengubah gejala.", e);
        }
    }

    public static void delete(String kode) {
        String sql = "DELETE FROM gejala WHERE kode=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus gejala.", e);
        }
    }
}