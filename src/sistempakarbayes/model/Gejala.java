package sistempakarbayes.model;

/**
 * Model gejala yang dapat diamati user.
 */
public class Gejala {
    private String kode;
    private String nama;

    public Gejala(String kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }

    public void setKode(String kode) { this.kode = kode; }
    public void setNama(String nama) { this.nama = nama; }
}
