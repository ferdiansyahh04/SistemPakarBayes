# Flowchart Perhitungan Teorema Bayes

## Rumus

```
                    P(E|H) × P(H)
P(H|E) = ----------------------------------------
          Σ [ P(E|Hj) × P(Hj) ]   untuk semua j
```

Dengan asumsi gejala saling bebas (naive):

```
P(E|H) = P(G1|H) × P(G2|H) × ... × P(Gn|H)
```

## Langkah Perhitungan

1. Ambil prior P(H) tiap kerusakan dari tabel `kerusakan`.
2. Untuk tiap gejala terpilih, ambil P(gejala|kerusakan) dari tabel `aturan`.
3. Kalikan semua likelihood → skor(H) = P(H) × Π P(Gi|H).
4. Terapkan Laplace smoothing: nilai ≤ ε diangkat ke ε (default ε = 0.001).
5. Normalisasi: bagi tiap skor dengan total seluruh skor → P(H|E).
6. Urutkan dari terbesar, ambil H1 sebagai diagnosa utama.
7. Bandingkan dengan ambang keyakinan (default 30%).

## Flowchart Perhitungan

```mermaid
flowchart TD
    A[Mulai] --> B[Ambil prior P&#40;H&#41; dari tabel kerusakan]
    B --> C[Untuk tiap gejala terpilih Gi]
    C --> D[Ambil P&#40;Gi|H&#41; dari tabel aturan]
    D --> E{ nilai &lt; epsilon ?}
    E -->|ya| F[nilai = epsilon]
    E -->|tidak| G6[nilai tetap]
    F --> G[kalikan skor = skor x nilai]
    G6 --> G
    G --> H{Semua gejala diproses?}
    H -->|tidak| C
    H -->|ya| I[total = jumlah semua skor]
    I --> J[P&#40;H|E&#41; = skor / total untuk tiap H]
    J --> K[Urutkan descending]
    K --> L[H1 = kerusakan utama]
    L --> M{ P&#40;H1|E&#41; &gt;= 30% ?}
    M -->|ya| N[Tampilkan hasil + solusi]
    M -->|tidak| O[Tampilkan hasil + peringatan keyakinan rendah]
    N --> P[Simpan riwayat konsultasi]
    O --> P
    P --> Q[Selesai]
```

## Contoh Numerik (Skenario: G02 booting lambat + G03 bunyi klik)

Nilai dari basis pengetahuan (10 kerusakan, hanya 4 teratas ditampilkan):

| Kerusakan        | P(H)  | P(G02\|H) | P(G03\|H) | skor = P(H)·P(G02)·P(G03) |
|------------------|-------|-----------|-----------|----------------------------|
| K01 RAM          | 0.10  | 0.20      | 0.02      | 0.10 × 0.20 × 0.02 = 0.0004 |
| K02 HDD bad sec. | 0.12  | 0.85      | 0.85      | 0.12 × 0.85 × 0.85 = 0.0867 |
| K03 Overheating  | 0.15  | 0.10      | 0.05      | 0.15 × 0.10 × 0.05 = 0.00075 |
| K10 OS korup     | 0.10  | 0.40      | 0.05      | 0.10 × 0.40 × 0.05 = 0.002   |

Total seluruh skor (10 kerusakan) = **0.091184**

Normalisasi (contoh K02):

```
P(K02|E) = 0.0867 / 0.091184 ≈ 0.9508 → 95.08%
```

Diagnosa utama: **K02 Hard disk bad sector (≈95%)** — dominan, meyakinkan.
(Nilai ini sesuai hasil uji otomatis `UjiMesin`, yang menghasilkan 0.950824...)

## Catatan Laplace Smoothing

Jika admin mengisi nilai P(gejala|kerusakan) = 0, tanpa smoothing skor
hipotesis itu menjadi 0 total (tidak pernah terpilih). Dengan smoothing,
nilai 0 diangkat ke epsilon (0.001), sehingga tetap memberi kontribusi kecil
dan hasil diagnosa tidak "mati". Lihat `BayesEngine.smooth()`.