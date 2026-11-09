package sistempakarbayes.model;

/**
 * Model hipotesis kerusakan laptop.
 */
public class Kerusakan {
    private String kode;
    private String nama;
    private double prior;   // P(H) — probabilitas awal dari pakar/data historis
    private String solusi;

    public Kerusakan(String kode, String nama, double prior, String solusi) {
        this.kode = kode;
        this.nama = nama;
        this.prior = prior;
        this.solusi = solusi;
    }

    public String getKode()   { return kode; }
    public String getNama()   { return nama; }
    public double getPrior()  { return prior; }
    public String getSolusi() { return solusi; }
}
