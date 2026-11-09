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

SYSTRAT
- JDK 17 atau lebih baru (JDK 21 juga bisa).
- Tidak perlu library eksternal untuk versi demo ini.
- Tidak perlu MySQL dulu; data pakar berada di BasisPengetahuan.java.

FILE PENTING
- src/sistempakarbayes/Main.java
  Entry point aplikasi.
- src/sistempakarbayes/model/Kerusakan.java
  Model kode kerusakan, prior P(H), dan solusi.
- src/sistempakarbayes/model/Gejala.java
  Model gejala.
- src/sistempakarbayes/data/BasisPengetahuan.java
  Data pakar: 5 kerusakan, 8 gejala, serta P(gejala|kerusakan).
- src/sistempakarbayes/engine/BayesEngine.java
  Perhitungan Teorema Bayes dan normalisasi hasil.
- src/sistempakarbayes/ui/MainFrame.java
  GUI pemilihan gejala.
- src/sistempakarbayes/ui/HasilDiagnosaDialog.java
  GUI hasil diagnosa dan solusi.
- test/sistempakarbayes/UjiBayes.java
  Pengujian perhitungan melalui console.

RUMUS
P(H|E) = P(E|H) x P(H) / jumlah P(E|Hj) x P(Hj)

Dengan asumsi gejala saling bebas:
skor(H) = P(H) x P(G1|H) x P(G2|H) x ... x P(Gn|H)
Posterior = skor(H) / total seluruh skor hipotesis.

PENGEMBANGAN BERIKUTNYA
- Pindahkan basis pengetahuan ke MySQL dan baca menggunakan JDBC.
- Tambahkan form login admin.
- Tambahkan CRUD kerusakan, gejala, nilai probabilitas, dan solusi.
- Tambahkan riwayat konsultasi dan cetak laporan hasil diagnosa.
