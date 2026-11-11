package sistempakarbayes.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.model.Kerusakan;

/**
 * Mesin inferensi Teorema Bayes.
 *
 * Rumus:
 *   P(H|E) = P(E|H) * P(H) / Σ P(E|Hj) * P(Hj)
 *
 * Gejala diasumsikan saling bebas (naive), sehingga:
 *   skor(H) = P(H) * P(G1|H) * P(G2|H) * ... * P(Gn|H)
 *   lalu dinormalisasi agar total semua hipotesis = 1.0 (100%).
 *
 * Penanganan nilai nol (Laplace / additive smoothing):
 *   Nilai P(gejala|kerusakan) yang nol (atau di bawah epsilon) diangkat ke
 *   epsilon, supaya satu gejala dengan nilai 0 tidak otomatis menolkan
 *   seluruh skor suatu hipotesis. Ini mencegah diagnosa "mati semua" saat
 *   admin memasukkan nilai 0 di basis pengetahuan.
 */
public class BayesEngine {

    /** Ambang batas keyakinan diagnosa utama (30%). */
    public static final double AMBANG_KEYAKINAN = 0.30;

    /** Nilai minimum (epsilon) untuk Laplace smoothing. */
    private static final double EPSILON = 0.001;

    /**
     * Hitung probabilitas setiap kerusakan berdasarkan gejala terpilih.
     *
     * @param gejalaTerpilih daftar kode gejala yang dicentang user
     * @return map kodeKerusakan -> probabilitas (0..1), terurut dari terbesar
     */
    public static Map<String, Double> diagnosa(List<String> gejalaTerpilih) {
        Map<String, Double> skor = new LinkedHashMap<>();

        // Hitung skor = prior * hasil kali likelihood tiap gejala (dengan smoothing)
        for (Kerusakan k : BasisPengetahuan.getSemuaKerusakan()) {
            double nilai = k.getPrior();
            for (String g : gejalaTerpilih) {
                nilai *= smooth(BasisPengetahuan.getProbabilitas(g, k.getKode()));
            }
            skor.put(k.getKode(), nilai);
        }

        // Normalisasi: total semua skor = 1.0
        double total = 0;
        for (double v : skor.values()) total += v;

        Map<String, Double> hasil = new LinkedHashMap<>();
        if (total > 0) {
            for (Map.Entry<String, Double> e : skor.entrySet()) {
                hasil.put(e.getKey(), e.getValue() / total);
            }
        }

        // Urutkan dari probabilitas terbesar
        List<Map.Entry<String, Double>> daftar = new ArrayList<>(hasil.entrySet());
        daftar.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        Map<String, Double> terurut = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : daftar) {
            terurut.put(e.getKey(), e.getValue());
        }
        return terurut;
    }

    /**
     * Laplace smoothing: nilai nol (atau mendekati nol) diangkat ke EPSILON
     * agar tidak menolkan seluruh skor hipotesis.
     */
    private static double smooth(double nilai) {
        return nilai < EPSILON ? EPSILON : nilai;
    }

    /**
     * Ambil kode kerusakan dengan probabilitas tertinggi.
     * Mengembalikan null bila hasil kosong.
     */
    public static String kodeUtama(Map<String, Double> hasil) {
        if (hasil == null || hasil.isEmpty()) return null;
        return hasil.entrySet().iterator().next().getKey();
    }

    /**
     * Ambil nilai keyakinan tertinggi.
     * Mengembalikan 0.0 bila hasil kosong.
     */
    public static double nilaiUtama(Map<String, Double> hasil) {
        if (hasil == null || hasil.isEmpty()) return 0.0;
        return hasil.entrySet().iterator().next().getValue();
    }

    /**
     * Apakah diagnosa cukup meyakinkan (di atas ambang batas)?
     */
    public static boolean meyakinkan(double nilai) {
        return nilai >= AMBANG_KEYAKINAN;
    }
}