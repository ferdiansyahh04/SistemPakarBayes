package sistempakarbayes.data;

import java.util.*;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.Kerusakan;

/**
 * Basis pengetahuan sistem pakar.
 * Nilai probabilitas P(gejala|kerusakan) diperoleh dari wawancara pakar.
 * Prior P(kerusakan) dari data historis servis.
 *
 * Catatan: versi demo ini data di-hardcode agar bisa langsung jalan
 * tanpa database. Untuk versi final, tabel ini dipindah ke MySQL
 * dan di-load lewat JDBC (lihat proposal/konfigurasi dosen).
 */
public class BasisPengetahuan {

    private static final List<Kerusakan> DAFTAR_KERUSAKAN = new ArrayList<>();
    private static final List<Gejala> DAFTAR_GEJALA = new ArrayList<>();
    private static final Map<String, Map<String, Double>> PROBABILITAS = new HashMap<>();

    static {
        // --- Hipotesis kerusakan + prior + solusi ---
        DAFTAR_KERUSAKAN.add(new Kerusakan("K01", "RAM rusak / longgar", 0.20,
                "Lepaskan RAM, bersihkan pin dengan penghapus, pasang kembali. "
              + "Jika masih BSOD, ganti RAM baru."));
        DAFTAR_KERUSAKAN.add(new Kerusakan("K02", "Hard disk bad sector", 0.25,
                "Backup data segera. Jalankan chkdsk /r. "
              + "Jika bad sector parah, ganti HDD (disarankan upgrade ke SSD)."));
        DAFTAR_KERUSAKAN.add(new Kerusakan("K03", "Overheating (fan berdebu)", 0.20,
                "Bersihkan kipas dan heatsink dari debu, ganti thermal paste. "
              + "Gunakan cooling pad saat pemakaian berat."));
        DAFTAR_KERUSAKAN.add(new Kerusakan("K04", "Baterai drop", 0.15,
                "Kalibrasi baterai. Jika tetap drop, ganti baterai baru "
              + "yang original."));
        DAFTAR_KERUSAKAN.add(new Kerusakan("K05", "VGA/GPU bermasalah", 0.20,
                "Update driver VGA. Jika artefak tetap muncul, kemungkinan "
              + "chip GPU rusak — perlu reball/ganti motherboard di servis center."));

        // --- Gejala yang bisa diamati ---
        DAFTAR_GEJALA.add(new Gejala("G01", "Sering blue screen (BSOD)"));
        DAFTAR_GEJALA.add(new Gejala("G02", "Booting sangat lambat"));
        DAFTAR_GEJALA.add(new Gejala("G03", "Bunyi klik/berdecit dari dalam laptop"));
        DAFTAR_GEJALA.add(new Gejala("G04", "Mati mendadak saat dipakai"));
        DAFTAR_GEJALA.add(new Gejala("G05", "Bodi terasa sangat panas"));
        DAFTAR_GEJALA.add(new Gejala("G06", "Hanya hidup saat charger terpasang"));
        DAFTAR_GEJALA.add(new Gejala("G07", "Layar bergaris / muncul artefak"));
        DAFTAR_GEJALA.add(new Gejala("G08", "Layar blank tapi mesin hidup"));

        // --- Tabel probabilitas P(gejala | kerusakan) dari pakar ---
        tambahProbabilitas("G01", new double[]{0.80, 0.30, 0.20, 0.05, 0.40});
        tambahProbabilitas("G02", new double[]{0.30, 0.85, 0.30, 0.10, 0.10});
        tambahProbabilitas("G03", new double[]{0.05, 0.80, 0.20, 0.05, 0.05});
        tambahProbabilitas("G04", new double[]{0.20, 0.20, 0.85, 0.60, 0.30});
        tambahProbabilitas("G05", new double[]{0.10, 0.20, 0.90, 0.30, 0.50});
        tambahProbabilitas("G06", new double[]{0.02, 0.05, 0.05, 0.90, 0.02});
        tambahProbabilitas("G07", new double[]{0.20, 0.05, 0.20, 0.02, 0.85});
        tambahProbabilitas("G08", new double[]{0.50, 0.10, 0.10, 0.05, 0.70});
    }

    private static void tambahProbabilitas(String kodeGejala, double[] nilai) {
        String[] urutan = {"K01", "K02", "K03", "K04", "K05"};
        Map<String, Double> baris = new HashMap<>();
        for (int i = 0; i < urutan.length; i++) {
            baris.put(urutan[i], nilai[i]);
        }
        PROBABILITAS.put(kodeGejala, baris);
    }

    public static List<Kerusakan> getSemuaKerusakan() {
        return DAFTAR_KERUSAKAN;
    }

    public static List<Gejala> getSemuaGejala() {
        return DAFTAR_GEJALA;
    }

    /** P(gejala | kerusakan). */
    public static double getProbabilitas(String kodeGejala, String kodeKerusakan) {
        return PROBABILITAS.get(kodeGejala).get(kodeKerusakan);
    }

    /** Cari kerusakan berdasarkan kode. */
    public static Kerusakan getKerusakan(String kode) {
        for (Kerusakan k : DAFTAR_KERUSAKAN) {
            if (k.getKode().equals(kode)) return k;
        }
        return null;
    }
}
