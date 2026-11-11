package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistempakarbayes.model.Kerusakan;

/**
 * CRUD data kerusakan (hipotesis).
 */
public final class KerusakanDAO {

    private KerusakanDAO() {
    }

    public static List<Kerusakan> loadSemua() {
        List<Kerusakan> list = new ArrayList<>();
        String sql = "SELECT kode, nama, prior, solusi FROM kerusakan ORDER BY kode";
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
            throw new RuntimeException("Gagal memuat kerusakan.", e);
        }
        return list;
    }

    public static void insert(Kerusakan k) {
        String sql = "INSERT INTO kerusakan (kode, nama, prior, solusi) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k.getKode());
            ps.setString(2, k.getNama());
            ps.setDouble(3, k.getPrior());
            ps.setString(4, k.getSolusi());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah kerusakan.", e);
        }
    }

    public static void update(Kerusakan k) {
        String sql = "UPDATE kerusakan SET nama=?, prior=?, solusi=? WHERE kode=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k.getNama());
            ps.setDouble(2, k.getPrior());
            ps.setString(3, k.getSolusi());
            ps.setString(4, k.getKode());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengubah kerusakan.", e);
        }
    }

    public static void delete(String kode) {
        String sql = "DELETE FROM kerusakan WHERE kode=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus kerusakan.", e);
        }
    }
}