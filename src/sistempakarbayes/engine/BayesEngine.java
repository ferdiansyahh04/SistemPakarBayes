package sistempakarbayes.engine;

import java.util.*;
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
 */
public class BayesEngine {

    /**
     * Hitung probabilitas setiap kerusakan berdasarkan gejala terpilih.
     *
     * @param gejalaTerpilih daftar kode gejala yang dicentang user
     * @return map kodeKerusakan -> probabilitas (0..1), terurut dari terbesar
     */
    public static Map<String, Double> diagnosa(List<String> gejalaTerpilih) {
        Map<String, Double> skor = new LinkedHashMap<>();

        // Hitung skor = prior * hasil kali likelihood tiap gejala
        for (Kerusakan k : BasisPengetahuan.getSemuaKerusakan()) {
            double nilai = k.getPrior();
            for (String g : gejalaTerpilih) {
                nilai *= BasisPengetahuan.getProbabilitas(g, k.getKode());
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
}
