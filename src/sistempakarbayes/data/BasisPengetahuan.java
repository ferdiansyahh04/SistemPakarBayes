package sistempakarbayes.data;

import java.util.List;
import java.util.Map;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.Kerusakan;

/**
 * Facade basis pengetahuan yang dibaca dari database (MySQL).
 *
 * Data di-cache sekali saat pertama diakses (lazy) agar cepat,
 * dan dimuat ulang lewat {@link #refresh()} setelah admin mengubah data.
 */
public class BasisPengetahuan {

    private static List<Kerusakan> daftarKerusakan;
    private static List<Gejala> daftarGejala;
    private static Map<String, Map<String, Double>> probabilitas;

    private BasisPengetahuan() {
    }

    /** Muat (atau muat ulang) seluruh data dari database. */
    public static void refresh() {
        daftarKerusakan = BasisPengetahuanDAO.loadSemuaKerusakan();
        daftarGejala = BasisPengetahuanDAO.loadSemuaGejala();
        probabilitas = BasisPengetahuanDAO.loadAturan();
    }

    private static void pastikanTermuat() {
        if (daftarKerusakan == null || daftarGejala == null || probabilitas == null) {
            refresh();
        }
    }

    public static List<Kerusakan> getSemuaKerusakan() {
        pastikanTermuat();
        return daftarKerusakan;
    }

    public static List<Gejala> getSemuaGejala() {
        pastikanTermuat();
        return daftarGejala;
    }

    /** P(gejala | kerusakan). */
    public static double getProbabilitas(String kodeGejala, String kodeKerusakan) {
        pastikanTermuat();
        Map<String, Double> baris = probabilitas.get(kodeGejala);
        if (baris == null) {
            throw new IllegalArgumentException("Gejala tidak dikenal: " + kodeGejala);
        }
        Double nilai = baris.get(kodeKerusakan);
        if (nilai == null) {
            return 0.0; // aturan tidak ada -> kontribusi nol
        }
        return nilai;
    }

    /** Cari kerusakan berdasarkan kode. */
    public static Kerusakan getKerusakan(String kode) {
        pastikanTermuat();
        for (Kerusakan k : daftarKerusakan) {
            if (k.getKode().equals(kode)) return k;
        }
        return null;
    }
}