-- ============================================================
-- Seed data awal (dari wawancara pakar)
-- Password admin default: admin123 (PBKDF2-SHA256)
-- ============================================================
USE sistem_pakar;

-- Admin / pakar
INSERT INTO user (username, password, nama, role) VALUES
('admin', '10000:a1b2c3d4e5f60718293a4b5c6d7e8f90:bfacd14cb736c70b012d89d766b455864f193d232ed1c3d1b27c472e4ca3f224', 'Administrator', 'admin'),
('pakar', '10000:f0e1d2c3b4a5968778695a4b3c2d1e0f:f06ac3fb34758a444c01a99dfd033321bbcac35fcb9c106f7f0e402d95936deb', 'Pakar Komputer', 'pakar');

-- Kerusakan (hipotesis) + prior P(H)
INSERT INTO kerusakan (kode, nama, prior, solusi) VALUES
('K01', 'RAM rusak / longgar', 0.20, 'Lepaskan RAM, bersihkan pin dengan penghapus, pasang kembali. Jika masih BSOD, ganti RAM baru.'),
('K02', 'Hard disk bad sector', 0.25, 'Backup data segera. Jalankan chkdsk /r. Jika bad sector parah, ganti HDD (disarankan upgrade ke SSD).'),
('K03', 'Overheating (fan berdebu)', 0.20, 'Bersihkan kipas dan heatsink dari debu, ganti thermal paste. Gunakan cooling pad saat pemakaian berat.'),
('K04', 'Baterai drop', 0.15, 'Kalibrasi baterai. Jika tetap drop, ganti baterai baru yang original.'),
('K05', 'VGA/GPU bermasalah', 0.20, 'Update driver VGA. Jika artefak tetap muncul, kemungkinan chip GPU rusak - perlu reball/ganti motherboard di servis center.');

-- Gejala (evidence)
INSERT INTO gejala (kode, nama) VALUES
('G01', 'Sering blue screen (BSOD)'),
('G02', 'Booting sangat lambat'),
('G03', 'Bunyi klik/berdecit dari dalam laptop'),
('G04', 'Mati mendadak saat dipakai'),
('G05', 'Bodi terasa sangat panas'),
('G06', 'Hanya hidup saat charger terpasang'),
('G07', 'Layar bergaris / muncul artefak'),
('G08', 'Layar blank tapi mesin hidup');