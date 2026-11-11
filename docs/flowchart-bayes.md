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

## Contoh Numerik (Skenario: G02 booting lambat + G03 bunyi klik)

Nilai dari basis pengetahuan:

| Kerusakan              | P(H)  | P(G02|H) | P(G03|H) | skor = P(H)·P(G02)·P(G03) |
|------------------------|-------|----------|----------|---------------------------|
| K01 RAM                | 0.10  | 0.85     | 0.02     | 0.10 × 0.85 × 0.02 = 0.0017 |
| K02 HDD bad sector     | 0.12  | 0.85     | 0.85     | 0.12 × 0.85 × 0.85 = 0.0867 |
| K03 Overheating        | 0.15  | 0.10     | 0.05     | 0.15 × 0.10 × 0.05 = 0.00075 |
| ... (seluruh kerusakan)| ...   | ...      | ...      | ...                       |

Total = Σ skor ≈ 0.0912

Normalisasi (contoh K02):

```
P(K02|E) = 0.0867 / 0.0912 ≈ 0.9508 → 95.08%
```

Diagnosa utama: **K02 Hard disk bad sector (≈95%)** — dominan, meyakinkan.

## Flowchart ASCII

```
        Mulai
          |
          v
   [Input gejala G1..Gn]
          |
          v
   untuk tiap hipotesis Hi:
      skor = P(Hi)
          |
          v
      untuk tiap gejala Gj:
         p = P(Gj | Hi)        <--- baca tabel aturan
         p < eps ? p = eps     <--- Laplace smoothing
         skor = skor * p
          |
          v
   total = jumlah semua skor
          |
          v
   untuk tiap Hi: P(Hi|E) = skor_i / total
          |
          v
   urutkan descending -> H1 = utama
          |
          v
   P(H1|E) >= ambang ?  ---Tidak--> [ + peringatan ]
          |Ya
          v
   tampilkan hasil + solusi + simpan riwayat
          |
          v
        Selesai
```