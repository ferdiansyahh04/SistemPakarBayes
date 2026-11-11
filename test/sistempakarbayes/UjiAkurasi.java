package sistempakarbayes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.engine.BayesEngine;

/**
 * Pengujian akurasi: membandingkan diagnosa sistem vs diagnosa pakar
 * untuk sejumlah kasus uji, lalu menghitung persentase akurasi.
 * Hasil ini dipakai untuk tabel akurasi di BAB IV (pengujian).
 */
public class UjiAkurasi {

    // Kasus uji: daftar gejala -> kode kerusakan yang diharapkan (dari pakar)
    private static final Map<List<String>, String> KASUS = new LinkedHashMap<>();

    static {
        // 10 kasus: satu gejala khas tiap kerusakan (diskriminatif)
        KASUS.put(Arrays.asList("G01"), "K01"); // BSOD -> RAM
        KASUS.put(Arrays.asList("G03"), "K02"); // bunyi klik -> HDD
        KASUS.put(Arrays.asList("G05"), "K03"); // bodi panas -> overheating
        KASUS.put(Arrays.asList("G06"), "K04"); // hidup cuma pas cas -> baterai
        KASUS.put(Arrays.asList("G07"), "K05"); // artefak -> VGA/GPU
        KASUS.put(Arrays.asList("G09"), "K06"); // tombol mati -> keyboard
        KASUS.put(Arrays.asList("G16"), "K07"); // USB mati -> motherboard/IC
        KASUS.put(Arrays.asList("G15"), "K08"); // flicker -> LCD
        KASUS.put(Arrays.asList("G11"), "K09"); // wifi putus -> wifi card
        KASUS.put(Arrays.asList("G13"), "K10"); // OS not found -> software korup

        // 5 kasus: kombinasi gejala khas
        KASUS.put(Arrays.asList("G02", "G03"), "K02"); // lambat + klik -> HDD
        KASUS.put(Arrays.asList("G04", "G05"), "K03"); // mati mendadak + panas -> overheat
        KASUS.put(Arrays.asList("G06", "G12"), "K04"); // cas + daya bocor -> baterai
        KASUS.put(Arrays.asList("G07", "G08"), "K05"); // artefak + blank -> VGA
        KASUS.put(Arrays.asList("G01", "G14"), "K01"); // BSOD + crash -> RAM
    }

    public static void main(String[] args) {
        System.out.println("=== PENGUJIAN AKURASI DIAGNOSA (vs PAKAR) ===\n");
        System.out.printf("%-42s | %-8s | %-8s | %s%n",
                "Gejala", "Prediksi", "Pakar", "Status");
        System.out.println("------------------------------------------------------"
                + "------------------------------");

        int benar = 0;
        int total = KASUS.size();

        List<String[]> baris = new ArrayList<>();
        for (Map.Entry<List<String>, String> e : KASUS.entrySet()) {
            List<String> gejala = e.getKey();
            String pakar = e.getValue();

            Map<String, Double> hasil = BayesEngine.diagnosa(gejala);
            String prediksi = BayesEngine.kodeUtama(hasil);
            boolean cocok = pakar.equals(prediksi);
            if (cocok) benar++;

            String namaPrediksi = BasisPengetahuan.getKerusakan(prediksi).getNama();
            String namaPakar = BasisPengetahuan.getKerusakan(pakar).getNama();

            System.out.printf("%-42s | %-8s | %-8s | %s%n",
                    String.join("+", gejala), prediksi, pakar,
                    cocok ? "BENAR" : "SALAH");
            System.out.printf("   -> %s  vs  %s%n", namaPrediksi, namaPakar);

            baris.add(new String[]{
                    String.join("+", gejala), prediksi, pakar,
                    String.valueOf(cocok)});
        }

        double akurasi = (double) benar / total * 100;
        System.out.println("\n------------------------------------------------------"
                + "------------------------------");
        System.out.printf("Total kasus       : %d%n", total);
        System.out.printf("Prediksi benar    : %d%n", benar);
        System.out.printf("Prediksi salah    : %d%n", total - benar);
        System.out.printf("Akurasi           : %.1f%%%n", akurasi);

        // Simpan tabel akurasi ke file teks (untuk lampiran laporan)
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("hasil_akurasi.txt");
            StringBuilder sb = new StringBuilder();
            sb.append("TABEL HASIL PENGUJIAN AKURASI\n");
            sb.append("Gejala\tPrediksi\tPakar\tStatus\n");
            for (String[] b : baris) {
                sb.append(String.join("\t", b)).append("\n");
            }
            sb.append("\nTotal kasus: ").append(total).append("\n");
            sb.append("Benar: ").append(benar).append("\n");
            sb.append(String.format("Akurasi: %.1f%%", akurasi)).append("\n");
            java.nio.file.Files.write(p, sb.toString().getBytes());
            System.out.println("\nTabel akurasi disimpan ke: " + p.toAbsolutePath());
        } catch (java.io.IOException ex) {
            System.out.println("Gagal menyimpan tabel: " + ex.getMessage());
        }
    }
}