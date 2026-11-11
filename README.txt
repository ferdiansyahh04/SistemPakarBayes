SISTEM PAKAR DIAGNOSA KERUSAKAN LAPTOP
Metode: Teorema Bayes
Platform: Java Swing - Apache NetBeans

CARA MEMBUKA DI NETBEANS
1. Extract ZIP ini.
2. Buka NetBeans -> File -> Open Project.
3. Pilih folder SistemPakarBayes (yang ada ikon project/cangkir).
4. Klik Open Project.
5. Tekan F6 (Run Project). Jika ditanya main class, pilih sistempakarbayes.Main.

Alternatif jika project tidak langsung dikenali:
- File -> New Project -> Java with Ant -> Java Project with Existing Sources.
- Project Folder: pilih folder SistemPakarBayes.
- Source Package Folder: pilih folder src.
- Test Package Folder: pilih folder test.
- Set Main Class: klik kanan project -> Properties -> Run -> Main Class:
  sistempakarbayes.Main

SYARAT (SYSTRAT)
- JDK 17 atau lebih baru (JDK 21 juga bisa).
- MySQL/MariaDB 8+ terpasang dan berjalan.
- Library MySQL Connector/J (sudah disertakan di folder lib/).

PERSIAPAN DATABASE (sebelum pertama kali run)
1. Pastikan MySQL/MariaDB aktif.
2. Login root, lalu jalankan 3 file SQL secara berurutan:
     source sql/schema.sql
     source sql/seed_data.sql
     source sql/seed_aturan.sql
   Atau dari baris perintah:
     mysql -uroot < sql/schema.sql
     mysql -uroot < sql/seed_data.sql
     mysql -uroot < sql/seed_aturan.sql
3. Buat user aplikasi (bila belum):
     CREATE USER 'pakarbayes'@'localhost' IDENTIFIED BY 'bayes12345';
     CREATE USER 'pakarbayes'@'127.0.0.1' IDENTIFIED BY 'bayes12345';
     GRANT ALL PRIVILEGES ON sistem_pakar.* TO 'pakarbayes'@'localhost';
     GRANT ALL PRIVILEGES ON sistem_pakar.* TO 'pakarbayes'@'127.0.0.1';
     FLUSH PRIVILEGES;
4. Sesuaikan kredensial di src/sistempakarbayes/data/Database.java
   bila host/user/password berbeda.

LOGIN ADMIN (untuk kelola data)
- Buka aplikasi, klik menu "Menu" -> "Login Admin / Kelola Data".
- Username: admin   Password: admin123  (role: admin)
- Username: pakar   Password: pakar123  (role: pakar)

FILE PENTING
- src/sistempakarbayes/Main.java
  Entry point aplikasi.
- src/sistempakarbayes/model/Kerusakan.java
  Model kode kerusakan, prior P(H), dan solusi.
- src/sistempakarbayes/model/Gejala.java
  Model gejala.
- src/sistempakarbayes/model/User.java
  Model pengguna (admin/pakar).
- src/sistempakarbayes/data/Database.java
  Konfigurasi koneksi JDBC (host, user, password).
- src/sistempakarbayes/data/BasisPengetahuanDAO.java
  Akses basis pengetahuan dari MySQL.
- src/sistempakarbayes/data/BasisPengetahuan.java
  Facade + cache data pakar (5 kerusakan, 8 gejala, aturan).
- src/sistempakarbayes/data/KerusakanDAO.java
  CRUD kerusakan.
- src/sistempakarbayes/data/GejalaDAO.java
  CRUD gejala.
- src/sistempakarbayes/data/AturanDAO.java
  CRUD nilai aturan P(gejala|kerusakan).
- src/sistempakarbayes/data/KonsultasiDAO.java
  Simpan/baca/hapus riwayat konsultasi.
- src/sistempakarbayes/util/PasswordUtils.java
  Hashing password PBKDF2-SHA256.
- src/sistempakarbayes/engine/BayesEngine.java
  Teorema Bayes + Laplace smoothing + confidence threshold.
- src/sistempakarbayes/ui/MainFrame.java
  GUI pemilihan gejala (menu login admin di menu bar).
- src/sistempakarbayes/ui/LoginDialog.java
  Dialog login admin/pakar.
- src/sistempakarbayes/ui/HasilDiagnosaDialog.java
  GUI hasil diagnosa dan solusi.
- src/sistempakarbayes/ui/admin/AdminFrame.java
  Jendela admin (tab kerusakan, gejala, nilai aturan).
- test/sistempakarbayes/UjiBayes.java
  Pengujian perhitungan (membaca dari MySQL).
- test/sistempakarbayes/UjiAdmin.java
  Pengujian login + CRUD.
- test/sistempakarbayes/UjiMesin.java
  Pengujian Laplace smoothing + confidence threshold.
- test/sistempakarbayes/UjiKonsultasi.java
  Pengujian riwayat konsultasi.
- test/sistempakarbayes/UjiAkurasi.java
  Pengujian akurasi diagnosa vs pakar (15 kasus, akurasi 100%).
- test/sistempakarbayes/SmokeGui.java & SmokeAdmin.java
  Smoke test GUI (menangkap screenshot).
- sql/schema.sql
  Skema database (tabel user, kerusakan, gejala, aturan, konsultasi).
- sql/seed_data.sql & sql/seed_aturan.sql
  Data pakar: 10 kerusakan, 16 gejala, 160 aturan.
- scripts/generate_seed_aturan.py
  Generator matriks aturan P(gejala|kerusakan).

RUMUS
P(H|E) = P(E|H) x P(H) / jumlah P(E|Hj) x P(Hj)

Dengan asumsi gejala saling bebas:
skor(H) = P(H) x P(G1|H) x P(G2|H) x ... x P(Gn|H)
Posterior = skor(H) / total seluruh skor hipotesis.

PENGEMBANGAN BERIKUTNYA
- Sinkronisasi basis pengetahuan ke sumber data eksternal/API.
- Laporan hasil diagnosa dalam format PDF.
- Multi-user dengan pembatasan hak akses per role yang lebih granular.
