# Dokumentasi Perancangan — Sistem Pakar Diagnosa Kerusakan Laptop

Metode: **Teorema Bayes** · Platform: Java Swing (NetBeans) · Database: MySQL/MariaDB

## Daftar Dokumen

| File                     | Isi                                             |
|--------------------------|-------------------------------------------------|
| `use-case-diagram.md`    | Use case diagram + tabel aktor & use case.      |
| `activity-diagram.md`    | Activity diagram alur konsultasi + flowchart.   |
| `erd.md`                 | Entity Relationship Diagram + kardinalitas.     |
| `flowchart-bayes.md`     | Flowchart & contoh numerik perhitungan Bayes.   |

## Ringkasan Arsitektur

```
+-------------+     pilih gejala      +-----------------+
|  MainFrame  | -------------------> |  BayesEngine    |
|  (UI user)  |                      |  (mesin Bayes)  |
+-------------+                      +-----------------+
      |  menu login admin                    |
      v                                      v
+-------------+          +----------------------------------+
| LoginDialog |  valid   |  BasisPengetahuan (facade+cache) |
+-------------+          +----------------------------------+
      | login sukses                    | JDBC
      v                                 v
+-------------+                 +------------------+
| AdminFrame  |  (CRUD)         |  Database (MySQL)|
| + Kerusakan | --------------->|  sistem_pakar    |
| + Gejala    |                 |  - kerusakan     |
| + Aturan    |                 |  - gejala        |
| + Riwayat   |                 |  - aturan        |
+-------------+                 |  - konsultasi    |
                                |  - detail_kon.   |
                                |  - user          |
                                +------------------+
```

## Alur Umum

1. User memilih gejala pada MainFrame.
2. MainFrame meminta nama (untuk riwayat).
3. BayesEngine menghitung probabilitas tiap kerusakan (dengan smoothing).
4. Hasil ditampilkan di HasilDiagnosaDialog (peringatan bila < ambang).
5. Konsultasi tersimpan otomatis ke `konsultasi` + `detail_konsultasi`.
6. Admin login (PBKDF2) → kelola kerusakan/gejala/aturan, lihat riwayat, cetak laporan.

## Metode Teorema Bayes

Rumus dan contoh perhitungan lengkap: lihat `flowchart-bayes.md`.
Penanganan nilai nol (Laplace smoothing) dan ambang keyakinan: lihat
`src/sistempakarbayes/engine/BayesEngine.java`.