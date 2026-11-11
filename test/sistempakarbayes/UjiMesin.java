package sistempakarbayes;

import java.util.Arrays;
import java.util.Map;
import sistempakarbayes.data.AturanDAO;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.engine.BayesEngine;

/**
 * Uji mesin inferensi: Laplace smoothing + confidence threshold.
 */
public class UjiMesin {

    public static void main(String[] args) {
        System.out.println("=== UJI DIAGNOSA DASAR ===");
        Map<String, Double> h1 = BayesEngine.diagnosa(Arrays.asList("G02", "G03"));
        cek("skenario 1 utama = K02 (HDD)", "K02".equals(BayesEngine.kodeUtama(h1)),
                "utama=" + BayesEngine.kodeUtama(h1));
        double utama1 = BayesEngine.nilaiUtama(h1);
        cek("keyakinan utama > 80%", utama1 > 0.80, "nilai=" + utama1);
        cek("total normalisasi 1.0", Math.abs(total(h1) - 1.0) < 1e-9);

        System.out.println("\n=== UJI CONFIDENCE THRESHOLD (30%) ===");
        cek("0.91 meyakinkan", BayesEngine.meyakinkan(0.91));
        cek("0.68 meyakinkan", BayesEngine.meyakinkan(0.68));
        cek("0.25 TIDAK meyakinkan", !BayesEngine.meyakinkan(0.25));
        cek("0.05 TIDAK meyakinkan", !BayesEngine.meyakinkan(0.05));
        cek("ambang = 0.30", Math.abs(BayesEngine.AMBANG_KEYAKINAN - 0.30) < 1e-9);

        System.out.println("\n=== UJI LAPLACE SMOOTHING (nilai nol) ===");
        // Simulasikan: set P(G01|K01) = 0, lalu jalankan diagnosa dengan G01.
        // Tanpa smoothing, skor K01 akan jadi 0 total; dengan smoothing tetap > 0.
        double awal = AturanDAO.getNilai("G01", "K01");
        try {
            AturanDAO.upsert("G01", "K01", 0.0);
            BasisPengetahuan.refresh();

            Map<String, Double> h = BayesEngine.diagnosa(Arrays.asList("G01"));
            double nilaiK01 = h.getOrDefault("K01", 0.0);
            cek("K01 tidak nol total (ada smoothing)", nilaiK01 > 0.0,
                    "nilai K01=" + nilaiK01);
            cek("tidak ada hasil NaN", !adaNaN(h));
            cek("total masih 1.0", Math.abs(total(h) - 1.0) < 1e-9);

            double u = BayesEngine.nilaiUtama(h);
            cek("masih ada diagnosa utama valid", u > 0.0 && u <= 1.0);
        } finally {
            // Kembalikan nilai awal
            AturanDAO.upsert("G01", "K01", awal);
            BasisPengetahuan.refresh();
        }
        double balik = AturanDAO.getNilai("G01", "K01");
        cek("nilai awal dikembalikan", Math.abs(balik - awal) < 1e-9);

        System.out.println("\nSELESAI.");
    }

    private static double total(Map<String, Double> m) {
        double t = 0;
        for (double v : m.values()) t += v;
        return t;
    }

    private static boolean adaNaN(Map<String, Double> m) {
        for (double v : m.values()) if (Double.isNaN(v)) return true;
        return false;
    }

    private static void cek(String nama, boolean kondisi) {
        cek(nama, kondisi, "");
    }

    private static void cek(String nama, boolean kondisi, String info) {
        System.out.println("  " + (kondisi ? "PASS" : "FAIL") + " - " + nama
                + (info.isEmpty() ? "" : "  [" + info + "]"));
        if (!kondisi) throw new AssertionError("Uji gagal: " + nama);
    }
}