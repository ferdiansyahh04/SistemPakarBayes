package sistempakarbayes.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Satu catatan konsultasi (riwayat diagnosa).
 */
public class Konsultasi {
    private int id;
    private String namaUser;
    private LocalDateTime tanggal;
    private String kodeHasil;   // kode kerusakan hasil utama (boleh null)
    private double nilaiHasil;  // persentase keyakinan 0..1
    private List<String> kodeGejala; // gejala yang dipilih user

    public Konsultasi(int id, String namaUser, LocalDateTime tanggal,
                      String kodeHasil, double nilaiHasil, List<String> kodeGejala) {
        this.id = id;
        this.namaUser = namaUser;
        this.tanggal = tanggal;
        this.kodeHasil = kodeHasil;
        this.nilaiHasil = nilaiHasil;
        this.kodeGejala = kodeGejala;
    }

    public int getId()             { return id; }
    public String getNamaUser()    { return namaUser; }
    public LocalDateTime getTanggal() { return tanggal; }
    public String getKodeHasil()   { return kodeHasil; }
    public double getNilaiHasil()  { return nilaiHasil; }
    public List<String> getKodeGejala() { return kodeGejala; }
}