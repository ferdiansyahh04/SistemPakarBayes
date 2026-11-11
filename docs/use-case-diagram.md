# Use Case Diagram — Sistem Pakar Diagnosa Kerusakan Laptop

## Aktor

| Aktor   | Deskripsi                                                              |
|---------|------------------------------------------------------------------------|
| User    | Pengguna umum yang berkonsultasi (memilih gejala dan melihat diagnosa).|
| Admin   | Mengelola seluruh basis pengetahuan (kerusakan, gejala, nilai aturan). |
| Pakar   | Validator data; login untuk meninjau/menyesuaikan nilai probabilitas.  |

## Use Case

| ID   | Nama Use Case              | Aktor        | Deskripsi                                                                 |
|------|----------------------------|--------------|---------------------------------------------------------------------------|
| UC01 | Konsultasi Diagnosa        | User         | Memilih gejala, sistem menghitung Teorema Bayes, menampilkan hasil+solusi.|
| UC02 | Login                       | Admin, Pakar | Otentikasi username & password (PBKDF2).                                  |
| UC03 | Kelola Data Kerusakan      | Admin, Pakar | Tambah/ubah/hapus data kerusakan (prior & solusi).                        |
| UC04 | Kelola Data Gejala         | Admin, Pakar | Tambah/ubah/hapus data gejala.                                            |
| UC05 | Kelola Nilai Aturan        | Admin, Pakar | Isi/ubah nilai P(gejala|kerusakan) pada matriks.                          |
| UC06 | Lihat Riwayat Konsultasi   | Admin, Pakar | Menampilkan daftar konsultasi yang tersimpan.                             |
| UC07 | Cetak Laporan              | Admin, Pakar | Mencetak laporan riwayat konsultasi.                                      |
| UC08 | Simpan Riwayat Konsultasi  | Sistem       | Otomatis menyimpan diagnosa ke database setelah konsultasi.               |

## Diagram ASCII

```
                     +---------------------------+
                     |      Sistem Pakar         |
                     +---------------------------+
                                    ^
        (UC01 Konsultasi)            |            (UC02 Login)
                                    |                 |
+------+            +---------------+                 +---------------+
| User |----------->|                                               |
+------+            |               +---------------------------+  |
                    |               |                           |  |
        (UC02 Login)|               |    +----------------+     |  |
+-------+           |               |    |  UC03 Kerusakan |     |  |
| Admin |-----------+-------------->|    +----------------+     |<--+
+-------+                           |    |  UC04 Gejala    |     |
        |                           |    +----------------+     |
+-------+ (UC02 Login)              |    |  UC05 Aturan    |     |
| Pakar |--------------------------->    +----------------+     |
+-------+                           |    |  UC06 Riwayat   |     |
                                    |    +----------------+     |
                                    |    |  UC07 Cetak     |     |
                                    |    +----------------+     |
                                    +---------------------------+
```