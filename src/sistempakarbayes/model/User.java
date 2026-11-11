package sistempakarbayes.model;

/**
 * Pengguna aplikasi (admin / pakar).
 */
public class User {
    private int id;
    private String username;
    private String password; // stored hash: iterasi:salt:hash
    private String nama;
    private String role;

    public User(int id, String username, String password, String nama, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nama = nama;
        this.role = role;
    }

    public int getId()        { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNama()      { return nama; }
    public String getRole()      { return role; }
}