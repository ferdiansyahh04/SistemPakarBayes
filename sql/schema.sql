-- ============================================================
-- Sistem Pakar Diagnosa Kerusakan Laptop - Teorema Bayes
-- Skema Database MySQL/MariaDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS sistem_pakar
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE sistem_pakar;

-- ------------------------------------------------------------
-- Tabel pengguna (admin)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,          -- format: iterasi:salt_hex:hash_hex (PBKDF2-SHA256)
  nama        VARCHAR(100) NOT NULL,
  role        ENUM('admin','pakar') NOT NULL DEFAULT 'admin',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabel kerusakan (hipotesis)
-- nilai prior = P(H), probabilitas awal tiap kerusakan
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kerusakan (
  kode        VARCHAR(10)  PRIMARY KEY,
  nama        VARCHAR(150) NOT NULL,
  prior       DOUBLE       NOT NULL,
  solusi      TEXT         NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabel gejala (evidence yang diamati user)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS gejala (
  kode        VARCHAR(10)  PRIMARY KEY,
  nama        VARCHAR(200) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabel aturan / rule: nilai P(gejala | kerusakan) dari pakar
-- relasi M:N antara gejala dan kerusakan
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS aturan (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  kode_gejala    VARCHAR(10) NOT NULL,
  kode_kerusakan VARCHAR(10) NOT NULL,
  nilai           DOUBLE      NOT NULL,       -- P(gejala|kerusakan), 0..1
  UNIQUE KEY uq_gejala_kerusakan (kode_gejala, kode_kerusakan),
  CONSTRAINT fk_aturan_gejala    FOREIGN KEY (kode_gejala)
    REFERENCES gejala(kode)    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_aturan_kerusakan FOREIGN KEY (kode_kerusakan)
    REFERENCES kerusakan(kode) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabel konsultasi (riwayat diagnosa)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS konsultasi (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  nama_user     VARCHAR(100) NOT NULL,
  tanggal       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hasil_kode    VARCHAR(10)  NULL,            -- kode kerusakan hasil utama
  hasil_nilai   DOUBLE       NULL,            -- persentase keyakinan (0..1)
  CONSTRAINT fk_konsultasi_hasil FOREIGN KEY (hasil_kode)
    REFERENCES kerusakan(kode) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Tabel detail konsultasi: gejala yang dipilih user
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detail_konsultasi (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  id_konsultasi   INT         NOT NULL,
  kode_gejala     VARCHAR(10) NOT NULL,
  CONSTRAINT fk_detail_konsultasi FOREIGN KEY (id_konsultasi)
    REFERENCES konsultasi(id) ON DELETE CASCADE,
  CONSTRAINT fk_detail_gejala FOREIGN KEY (kode_gejala)
    REFERENCES gejala(kode) ON DELETE CASCADE
) ENGINE=InnoDB;