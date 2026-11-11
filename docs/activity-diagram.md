# Activity Diagram — Alur Diagnosa Teorema Bayes

## Deskripsi

Diagram aktivitas menggambarkan alur konsultasi dari user memilih gejala hingga sistem menampilkan hasil akhir. Langkah perhitungan Bayes diperlihatkan sebagai sub-aktivitas.

## Diagram ASCII

```
  [User buka aplikasi]
          |
          v
  +---------------------+
  | Pilih gejala dari   |
  | daftar checkbox     |<--- (Reset: kosongkan)
  +---------------------+
          |
          v
     /------------\
    < Ada dipilih? >-- Tidak --> [Tampilkan peringatan]
     \------------/
          | Ya
          v
  +---------------------+
  | Minta nama user     |<--- (untuk riwayat konsultasi)
  +---------------------+
          |
          v
  +=====================+
  = HITUNG TEOREMA BAYES=
  +=====================+
          |
          v
  +---------------------------+
  | Untuk setiap kerusakan H: |
  |   skor = P(H)             |
  |   Untuk setiap gejala G:  |
  |     P_E_H = P(G|H)        |
  |     jika P_E_H < epsilon: |
  |       P_E_H = epsilon     |   <-- Laplace smoothing
  |     skor *= P_E_H         |
  |   simpan skor(H)          |
  +---------------------------+
          |
          v
  +---------------------------+
  | Normalisasi:              |
  |   total = Σ skor(H)       |
  |   P(H|E) = skor(H)/total  |
  +---------------------------+
          |
          v
  +---------------------------+
  | Urutkan dari terbesar     |
  | Ambil H1 = utama          |
  +---------------------------+
          |
          v
     /----------------\
    < P(H1|E) >= 30%? >----- Tidak ---> [Tampilkan hasil + peringatan]
     \----------------/                   "Keyakinan rendah"
          | Ya
          v
  +---------------------------+
  | Tampilkan hasil:          |
  | - Ranking kerusakan (%)   |
  | - Diagnosa utama + solusi |
  +---------------------------+
          |
          v
  +---------------------------+
  | Simpan ke tabel           |
  | konsultasi + detail       |
  | gejala (otomatis)         |
  +---------------------------+
          |
          v
        [SELESAI]
```

## Flowchart Ringkas (untuk BAB III)

```
Mulai
  │
  ▼
Input: user memilih gejala G1..Gn
  │
  ▼
Untuk setiap hipotesis Hi:
  skor_i = P(Hi)                    ← prior
  Untuk setiap gejala Gj:
    nilai = P(Gj | Hi)              ← dari tabel aturan DB
    jika nilai < ε maka nilai = ε   ← Laplace smoothing
    skor_i = skor_i × nilai
  │
  ▼
Normalisasi: P(Hi|Evidence) = skor_i / Σ skor_k
  │
  ▼
Urutkan dari P terbesar → hasil[0] = diagnosa utama
  │
  ▼
P(utama) ≥ 30%? → Ya: tampilkan + solusi
                 → Tidak: tampilkan + peringatan
  │
  ▼
Simpan riwayat konsultasi
  │
  ▼
Selesai
```