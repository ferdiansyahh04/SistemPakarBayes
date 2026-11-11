package sistempakarbayes;

import java.util.Arrays;
import sistempakarbayes.data.KonsultasiDAO;
import sistempakarbayes.model.Konsultasi;

/**
 * Uji integrasi riwayat konsultasi (simpan, baca, hapus).
 */
public class UjiKonsultasi {

    public static void main(String[] args) {
        System.out.println("=== UJI RIWAYAT KONSULTASI ===");

        int sebelum = KonsultasiDAO.count();

        // Simpan satu konsultasi
        int id = KonsultasiDAO.simpan("Budi Santoso", "K02", 0.91,
                Arrays.asList("G02", "G03"));
        cek("simpan konsultasi (id=" + id + ")", id > 0);
        cek("jumlah bertambah", KonsultasiDAO.count() == sebelum + 1);

        // Baca kembali, cek data terbaru
        boolean ditemukan = false;
        for (Konsultasi k : KonsultasiDAO.loadSemua()) {
            if (k.getId() == id) {
                ditemukan = true;
                cek("nama user tersimpan", k.getNamaUser().equals("Budi Santoso"));
                cek("kode hasil tersimpan", k.getKodeHasil().equals("K02"));
                cek("nilai hasil tersimpan", Math.abs(k.getNilaiHasil() - 0.91) < 1e-9);
                cek("detail gejala (2 gejala)", k.getKodeGejala().size() == 2);
                cek("detail gejala benar",
                        k.getKodeGejala().contains("G02")
                        && k.getKodeGejala().contains("G03"));
            }
        }
        cek("konsultasi terbaca kembali", ditemukan);

        // Hapus
        KonsultasiDAO.delete(id);
        cek("konsultasi terhapus", KonsultasiDAO.count() == sebelum);

        System.out.println("\nSELESAI.");
    }

    private static void cek(String nama, boolean kondisi) {
        System.out.println("  " + (kondisi ? "PASS" : "FAIL") + " - " + nama);
        if (!kondisi) throw new AssertionError("Uji gagal: " + nama);
    }
}