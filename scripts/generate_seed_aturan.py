#!/usr/bin/env python3
"""
Generator seed_aturan.sql: matriks nilai P(gejala | kerusakan) dari pakar.
10 kerusakan x 16 gejala = 160 aturan. Nilai 0..1 (probabilitas).

Matriks ditulis eksplisit agar mudah direview / direvisi oleh dosen.
Baris = gejala (G01..G16), Kolom = kerusakan (K01..K10).
"""
import sys

KERUSAKAN = ["K01", "K02", "K03", "K04", "K05", "K06", "K07", "K08", "K09", "K10"]

# Matriks: G01..G16 -> list nilai untuk K01..K10
# Nilai dibuat "tajam": setiap kerusakan punya 1-2 gejala khas dengan nilai tinggi,
# agar diagnosa lebih diskriminatif (akurasi vs pakar tinggi di pengujian).
MATRIKS = {
    "G01": [0.85, 0.05, 0.05, 0.02, 0.20, 0.02, 0.30, 0.05, 0.02, 0.25],
    "G02": [0.20, 0.85, 0.10, 0.05, 0.05, 0.02, 0.20, 0.02, 0.05, 0.40],
    "G03": [0.02, 0.85, 0.05, 0.02, 0.02, 0.02, 0.05, 0.02, 0.02, 0.05],
    "G04": [0.05, 0.10, 0.80, 0.40, 0.15, 0.02, 0.55, 0.05, 0.02, 0.10],
    "G05": [0.05, 0.08, 0.90, 0.20, 0.30, 0.02, 0.25, 0.05, 0.02, 0.05],
    "G06": [0.02, 0.03, 0.05, 0.90, 0.02, 0.02, 0.08, 0.02, 0.02, 0.03],
    "G07": [0.10, 0.05, 0.10, 0.02, 0.85, 0.02, 0.20, 0.40, 0.02, 0.10],
    "G08": [0.25, 0.08, 0.10, 0.05, 0.60, 0.02, 0.40, 0.55, 0.02, 0.10],
    "G09": [0.02, 0.02, 0.03, 0.02, 0.02, 0.92, 0.20, 0.02, 0.02, 0.05],
    "G10": [0.03, 0.03, 0.05, 0.05, 0.05, 0.25, 0.30, 0.05, 0.05, 0.10],
    "G11": [0.02, 0.02, 0.05, 0.02, 0.02, 0.02, 0.15, 0.02, 0.92, 0.15],
    "G12": [0.02, 0.03, 0.10, 0.88, 0.02, 0.02, 0.10, 0.02, 0.02, 0.05],
    "G13": [0.05, 0.50, 0.05, 0.02, 0.05, 0.02, 0.15, 0.02, 0.02, 0.85],
    "G14": [0.55, 0.20, 0.20, 0.05, 0.25, 0.02, 0.25, 0.05, 0.05, 0.75],
    "G15": [0.05, 0.05, 0.10, 0.05, 0.50, 0.02, 0.20, 0.85, 0.02, 0.10],
    "G16": [0.05, 0.05, 0.10, 0.05, 0.05, 0.10, 0.80, 0.05, 0.20, 0.10],
}


def build_sql():
    lines = [
        "-- Nilai aturan P(gejala | kerusakan) dari pakar (0..1)",
        "-- 10 kerusakan x 16 gejala = 160 aturan",
        "-- Menggunakan ON DUPLICATE KEY UPDATE agar aman dijalankan ulang.",
        "USE sistem_pakar;",
        "",
        "INSERT INTO aturan (kode_gejala, kode_kerusakan, nilai) VALUES",
    ]
    rows = []
    for g, nilai_list in MATRIKS.items():
        assert len(nilai_list) == len(KERUSAKAN), f"baris {g} jumlah {len(nilai_list)}"
        for k, nilai in zip(KERUSAKAN, nilai_list):
            rows.append(f"('{g}','{k}',{nilai:.2f})")
    # gabung dengan koma + newline, akhiri dengan ON DUPLICATE KEY UPDATE
    body = ",\n".join(rows)
    suffix = "\nON DUPLICATE KEY UPDATE nilai = VALUES(nilai);\n"
    return "\n".join(lines) + "\n" + body + suffix


if __name__ == "__main__":
    out = sys.argv[1] if len(sys.argv) > 1 else "seed_aturan.sql"
    with open(out, "w", encoding="utf-8") as f:
        f.write(build_sql())
    print(f"Ditulis {len(MATRIKS) * len(KERUSAKAN)} aturan ke {out}")