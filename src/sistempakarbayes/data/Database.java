package sistempakarbayes.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Pengelola koneksi database JDBC (MySQL/MariaDB).
 * Konfigurasi default disimpan di sini agar mudah diubah.
 */
public final class Database {

    private static final String URL      = "jdbc:mysql://127.0.0.1:3306/sistem_pakar";
    private static final String USERNAME = "pakarbayes";
    private static final String PASSWORD = "bayes12345";

    private Database() {
        // utility class
    }

    /**
     * Membuka koneksi baru ke database.
     * Driver com.mysql.cj.jdbc.Driver dimuat otomatis via ServiceLoader (JDBC 4).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}