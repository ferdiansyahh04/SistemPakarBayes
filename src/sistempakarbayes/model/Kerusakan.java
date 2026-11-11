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

    public void setKode(String kode)     { this.kode = kode; }
    public void setNama(String nama)     { this.nama = nama; }
    public void setPrior(double prior)   { this.prior = prior; }
    public void setSolusi(String solusi) { this.solusi = solusi; }
}
