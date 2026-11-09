package sistempakarbayes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.engine.BayesEngine;

/**
 * Pengujian mesin Bayes lewat console (tanpa GUI).
 * Dua skenario uji yang sama dengan perhitungan manual:
 *  1. G02 + G03 -> diharapkan Hard disk bad sector tertinggi (~91%)
 *  2. G04 + G05 -> diharapkan Overheating tertinggi (~68%)
 */
public class UjiBayes {

    public static void main(String[] args) {
        uji("Skenario 1: Booting lambat + bunyi klik", Arrays.asList("G02", "G03"));
        uji("Skenario 2: Mati mendadak + bodi panas", Arrays.asList("G04", "G05"));
    }

    private static void uji(String judul, List<String> gejala) {
        System.out.println(judul);
        Map<String, Double> hasil = BayesEngine.diagnosa(gejala);

        double total = 0;
        for (Map.Entry<String, Double> e : hasil.entrySet()) {
            String nama = BasisPengetahuan.getKerusakan(e.getKey()).getNama();
            System.out.printf("  %-28s %5.1f%%%n", nama, e.getValue() * 100);
            total += e.getValue();
        }
        System.out.printf("  Total probabilitas = %.6f%n%n", total);

        if (Math.abs(total - 1.0) > 0.000001) {
            throw new AssertionError("Normalisasi salah: total bukan 1.0");
        }
    }
}
