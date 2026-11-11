-- ============================================================
-- Seed data LENGKAP (dari wawancara pakar)
-- 10 kerusakan, 16 gejala (diperluas dari versi awal)
-- Password admin default:
--   admin / admin123
--   pakar / pakar123  (PBKDF2-SHA256, format iterasi:salt:hash)
-- ============================================================
USE sistem_pakar;

-- Admin / pakar
INSERT INTO user (username, password, nama, role) VALUES
('admin', '10000:a1b2c3d4e5f60718293a4b5c6d7e8f90:bfacd14cb736c70b012d89d766b455864f193d232ed1c3d1b27c472e4ca3f224', 'Administrator', 'admin'),
('pakar', '10000:f0e1d2c3b4a5968778695a4b3c2d1e0f:f06ac3fb34758a444c01a99dfd033321bbcac35fcb9c106f7f0e402d95936deb', 'Pakar Komputer', 'pakar');

-- Kerusakan (hipotesis) + prior P(H)
INSERT INTO kerusakan (kode, nama, prior, solusi) VALUES
('K01', 'RAM rusak / longgar', 0.10, 'Lepas dan pasang kembali RAM, bersihkan pin dengan penghapus. Jika tetap BSOD, ganti RAM baru.'),
('K02', 'Hard disk bad sector', 0.12, 'Backup data segera. Jalankan chkdsk /r. Ganti HDD bila bad sector parah (disarankan upgrade SSD).'),
('K03', 'Overheating (fan berdebu)', 0.15, 'Bersihkan kipas dan heatsink dari debu, ganti thermal paste. Gunakan cooling pad saat pemakaian berat.'),
('K04', 'Baterai drop', 0.10, 'Kalibrasi baterai. Jika tetap drop, ganti baterai baru original.'),
('K05', 'VGA/GPU bermasalah', 0.10, 'Update/install ulang driver VGA. Jika artefak tetap muncul, perlu reball/repair chip GPU di servis center.'),
('K06', 'Keyboard rusak', 0.08, 'Bersihkan keyboard dari debu/kotoran. Ganti keyboard baru bila beberapa tombol mati.'),
('K07', 'Motherboard / IC power bermasalah', 0.10, 'Bawa ke servis center untuk pemeriksaan motherboard. Kemungkinan perlu penggantian komponen IC.'),
('K08', 'Layar LCD / kabel fleksibel rusak', 0.08, 'Periksa kabel fleksibel LCD. Ganti LCD/kabel bila layar tetap bermasalah.'),
('K09', 'WiFi/Bluetooth card bermasalah', 0.07, 'Update driver WiFi. Ganti kartu WiFi/Bluetooth bila port bermasalah.'),
('K10', 'Sistem operasi / software korup', 0.10, 'Perbaiki bootloader / repair OS. Install ulang sistem operasi bila perlu (backup data dulu).');

-- Gejala (evidence)
INSERT INTO gejala (kode, nama) VALUES
('G01', 'Sering blue screen (BSOD)'),
('G02', 'Booting sangat lambat'),
('G03', 'Bunyi klik/berdecit dari dalam laptop'),
('G04', 'Mati mendadak saat dipakai'),
('G05', 'Bodi terasa sangat panas'),
('G06', 'Hanya hidup saat charger terpasang'),
('G07', 'Layar bergaris / muncul artefak'),
('G08', 'Layar blank tapi mesin hidup'),
('G09', 'Beberapa tombol keyboard tidak berfungsi'),
('G10', 'Kursor melompat / touchpad tidak akurat'),
('G11', 'WiFi sering putus / tidak terdeteksi'),
('G12', 'Baterai cepat habis (daya bocor)'),
('G13', 'Muncul pesan "Operating System not found"'),
('G14', 'Aplikasi sering crash / hang'),
('G15', 'Layar berkedip / flicker'),
('G16', 'Port USB tidak berfungsi');