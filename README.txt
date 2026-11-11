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

FILE PENTING
- src/sistempakarbayes/Main.java
  Entry point aplikasi.
- src/sistempakarbayes/model/Kerusakan.java
  Model kode kerusakan, prior P(H), dan solusi.
- src/sistempakarbayes/model/Gejala.java
  Model gejala.
- src/sistempakarbayes/data/Database.java
  Konfigurasi koneksi JDBC (host, user, password).
- src/sistempakarbayes/data/BasisPengetahuanDAO.java
  Akses basis pengetahuan dari MySQL.
- src/sistempakarbayes/data/BasisPengetahuan.java
  Facade + cache data pakar (5 kerusakan, 8 gejala, aturan).
- src/sistempakarbayes/engine/BayesEngine.java
  Perhitungan Teorema Bayes dan normalisasi hasil.
- src/sistempakarbayes/ui/MainFrame.java
  GUI pemilihan gejala.
- src/sistempakarbayes/ui/HasilDiagnosaDialog.java
  GUI hasil diagnosa dan solusi.
- test/sistempakarbayes/UjiBayes.java
  Pengujian perhitungan melalui console (membaca dari MySQL).
- sql/schema.sql
  Skema database (tabel user, kerusakan, gejala, aturan, konsultasi).
- sql/seed_data.sql & sql/seed_aturan.sql
  Data awal pakar.

RUMUS
P(H|E) = P(E|H) x P(H) / jumlah P(E|Hj) x P(Hj)

Dengan asumsi gejala saling bebas:
skor(H) = P(H) x P(G1|H) x P(G2|H) x ... x P(Gn|H)
Posterior = skor(H) / total seluruh skor hipotesis.

PENGEMBANGAN BERIKUTNYA
- Tambahkan form login admin.
- Tambahkan CRUD kerusakan, gejala, nilai probabilitas, dan solusi.
- Tambahkan riwayat konsultasi dan cetak laporan hasil diagnosa.
