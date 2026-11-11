package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sistempakarbayes.model.User;
import sistempakarbayes.util.PasswordUtils;

/**
 * Akses data pengguna + otentikasi login.
 */
public final class UserDAO {

    private UserDAO() {
    }

    /**
     * Validasi login. Mengembalikan objek User bila username & password
     * cocok, atau null bila gagal.
     */
    public static User login(String username, String password) {
        String sql = "SELECT id, username, password, nama, role FROM user WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashTersimpan = rs.getString("password");
                    if (PasswordUtils.verifikasi(password, hashTersimpan)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                hashTersimpan,
                                rs.getString("nama"),
                                rs.getString("role"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal melakukan login.", e);
        }
        return null;
    }
}