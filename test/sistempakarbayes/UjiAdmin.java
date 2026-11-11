package sistempakarbayes;

import sistempakarbayes.data.AturanDAO;
import sistempakarbayes.data.BasisPengetahuan;
import sistempakarbayes.data.GejalaDAO;
import sistempakarbayes.data.KerusakanDAO;
import sistempakarbayes.data.UserDAO;
import sistempakarbayes.model.Gejala;
import sistempakarbayes.model.Kerusakan;
import sistempakarbayes.model.User;
import sistempakarbayes.util.PasswordUtils;

/**
 * Uji integrasi login + CRUD (headless, tanpa GUI).
 * Memverifikasi tahap "MySQL + login + CRUD".
 */
public class UjiAdmin {

    public static void main(String[] args) {
        System.out.println("=== UJI LOGIN ===");
        User admin = UserDAO.login("admin", "admin123");
        User pakar = UserDAO.login("pakar", "pakar123");
        User salah = UserDAO.login("admin", "salahpass");
        cek("login admin berhasil", admin != null && admin.getRole().equals("admin"));
        cek("login pakar berhasil", pakar != null && pakar.getRole().equals("pakar"));
        cek("login salah ditolak", salah == null);

        System.out.println("\n=== UJI HASH PASSWORD (PBKDF2) ===");
        String h = PasswordUtils.hash("rahasia");
        cek("format iterasi:salt:hash (" + h.split(":").length + " bagian)",
                h.split(":").length == 3);
        cek("verifikasi hash benar", PasswordUtils.verifikasi("rahasia", h));
        cek("verifikasi hash salah ditolak", !PasswordUtils.verifikasi("lain", h));

        System.out.println("\n=== UJI CRUD KERUSAKAN ===");
        String kodeUji = "KTEST";
        Kerusakan k = new Kerusakan(kodeUji, "Uji Kerusakan", 0.10, "Solusi uji.");
        try {
            KerusakanDAO.insert(k);
            BasisPengetahuan.refresh();
            Kerusakan kBack = BasisPengetahuan.getKerusakan(kodeUji);
            cek("insert + load kerusakan", kBack != null && kBack.getNama().equals("Uji Kerusakan"));

            k.setNama("Uji Kerusakan (diubah)");
            KerusakanDAO.update(k);
            BasisPengetahuan.refresh();
            cek("update kerusakan", BasisPengetahuan.getKerusakan(kodeUji)
                    .getNama().equals("Uji Kerusakan (diubah)"));

            KerusakanDAO.delete(kodeUji);
            BasisPengetahuan.refresh();
            cek("delete kerusakan", BasisPengetahuan.getKerusakan(kodeUji) == null);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== UJI CRUD GEJALA ===");
        String kodeGejalaUji = "GTEST";
        Gejala g = new Gejala(kodeGejalaUji, "Gejala uji sementara.");
        try {
            GejalaDAO.insert(g);
            BasisPengetahuan.refresh();
            boolean ada = false;
            for (Gejala x : BasisPengetahuan.getSemuaGejala()) {
                if (x.getKode().equals(kodeGejalaUji) && x.getNama().equals("Gejala uji sementara.")) ada = true;
            }
            cek("insert + load gejala", ada);

            g.setNama("Gejala uji (diubah)");
            GejalaDAO.update(g);
            BasisPengetahuan.refresh();
            boolean adaUbah = false;
            for (Gejala x : BasisPengetahuan.getSemuaGejala()) {
                if (x.getKode().equals(kodeGejalaUji) && x.getNama().equals("Gejala uji (diubah)")) adaUbah = true;
            }
            cek("update gejala", adaUbah);

            GejalaDAO.delete(kodeGejalaUji);
            BasisPengetahuan.refresh();
            boolean masihAda = false;
            for (Gejala x : BasisPengetahuan.getSemuaGejala()) {
                if (x.getKode().equals(kodeGejalaUji)) masihAda = true;
            }
            cek("delete gejala", !masihAda);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== UJI UPSERT ATURAN ===");
        try {
            // Simpan nilai uji untuk kombinasi yang tidak mengganggu data utama
            // Gunakan gejala G01 x kerusakan K01 (sudah ada), lalu kembalikan nilainya.
            double awal = AturanDAO.getNilai("G01", "K01");
            AturanDAO.upsert("G01", "K01", 0.123);
            double set = AturanDAO.getNilai("G01", "K01");
            cek("upsert nilai aturan", Math.abs(set - 0.123) < 1e-9);
            AturanDAO.upsert("G01", "K01", awal); // kembalikan
            double balik = AturanDAO.getNilai("G01", "K01");
            cek("kembalikan nilai awal", Math.abs(balik - awal) < 1e-9);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nSELESAI.");
    }

    private static void cek(String nama, boolean kondisi) {
        System.out.println("  " + (kondisi ? "PASS" : "FAIL") + " - " + nama);
        if (!kondisi) throw new AssertionError("Uji gagal: " + nama);
    }
}