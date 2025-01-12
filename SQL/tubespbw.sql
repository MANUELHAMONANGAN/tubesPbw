DROP TABLE IF EXISTS Cart CASCADE;
DROP TABLE IF EXISTS TransaksiFilm CASCADE;
DROP TABLE IF EXISTS Transaksi CASCADE;
DROP TABLE IF EXISTS FilmAktor CASCADE;
DROP TABLE IF EXISTS FilmGenre CASCADE;
DROP TABLE IF EXISTS Aktor CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TYPE IF EXISTS methodBayar_enum CASCADE;
DROP TYPE IF EXISTS rent_enum CASCADE;
DROP TYPE IF EXISTS role_enum CASCADE;
DROP TYPE IF EXISTS status_rent CASCADE;
DROP TABLE IF EXISTS Film CASCADE;

--drop trigger
-- Hapus Trigger
DROP TRIGGER IF EXISTS setelah_peminjaman ON TransaksiFilm CASCADE;
DROP TRIGGER IF EXISTS setelah_pengembalian ON TransaksiFilm CASCADE;

-- Hapus Fungsi
DROP FUNCTION IF EXISTS kurangi_stok CASCADE;
DROP FUNCTION IF EXISTS tambah_stok CASCADE;

CREATE TYPE rent_enum AS ENUM ('Pinjam', 'Done');
CREATE TYPE status_rent AS ENUM ('draft','ongoing', 'done');
CREATE TYPE role_enum AS ENUM ('Pelanggan', 'Admin');
CREATE TYPE methodBayar_enum AS ENUM ('Tunai', 'Non_Tunai');

CREATE TABLE Film (
    idFilm SERIAL PRIMARY KEY,
    judul VARCHAR(60),
    stock int,
    coverFilm BYTEA,
    hargaPerHari int,
	deskripsi TEXT,
	durasi int,
    tahunRilis int,
    averageRating float
);

CREATE TABLE Genre (
    idGenre SERIAL PRIMARY KEY,
    nama VARCHAR(30)
);
 

CREATE TABLE Aktor (
    idAktor SERIAL PRIMARY KEY,
    nama VARCHAR(60),
    tanggalLahir date,
    deskripsiDiri TEXT,
    fotoProfil BYTEA
);

CREATE TABLE FilmGenre (
    idFilm int REFERENCES Film (idFilm) ON DELETE CASCADE,
    idGenre int REFERENCES Genre (idGenre) ON DELETE CASCADE,
	PRIMARY KEY (idFilm, idGenre)
);

CREATE TABLE FilmAktor (
    idFilm int REFERENCES Film (idFilm) ON DELETE CASCADE,
    idAktor int REFERENCES Aktor (idAktor) ON DELETE CASCADE,
	PRIMARY KEY (idFilm, idAktor)
);


CREATE TABLE Users (
    idUser SERIAL PRIMARY KEY,
    nama VARCHAR(60),
    nomorTelepon VARCHAR (13) UNIQUE,
    email VARCHAR (60) UNIQUE,
    role role_enum DEFAULT 'Pelanggan',
    password VARCHAR(60)
);


CREATE TABLE Transaksi (
	idTransaksi SERIAL PRIMARY KEY,
    idUser int REFERENCES Users(idUser) ON DELETE CASCADE,
    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipeTransaksi rent_enum,
    total BIGINT, --semua film (termasuk berapa harinya)
    metodePembayaran  methodBayar_enum
);

CREATE TABLE TransaksiFilm (
    idTransaksi int REFERENCES Transaksi(idTransaksi) ON DELETE CASCADE,
    idFilm int REFERENCES Film(idFilm),
    totalHari int,
	jumlah int DEFAULT 1,
    totalHarga int, --1 film berapa hari
	status status_rent DEFAULT 'draft',
	batasPengembalian date,  --status sama bataspengembalian per film nya
	PRIMARY KEY (idTransaksi, idFilm)
);

CREATE TABLE Cart (
    idCart SERIAL PRIMARY KEY,
    idUser INT REFERENCES Users(idUser) ON DELETE CASCADE,
    idFilm INT REFERENCES Film(idFilm) ON DELETE CASCADE,
    jumlahHari INT DEFAULT 1, -- Berapa lama film akan disewa
    jumlah INT DEFAULT 1, -- Jumlah film
	harga INT, --harga per hari
    tanggalDitambahkan TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--ON DELETE CASCADE buat kalo table parent dihapus, table yang berkaitan (child) bakal kehapus juga


-- ngurangin stok pas status berubah menjadi 'ongoing'
CREATE OR REPLACE FUNCTION kurangi_stok()
RETURNS TRIGGER AS $$
BEGIN
    -- kurangi stok jika status berubah dari 'draft' ke 'ongoing'
    IF OLD.status = 'draft' AND NEW.status = 'ongoing' THEN
        UPDATE Film
        SET stock = stock - NEW.jumlah
        WHERE idFilm = NEW.idFilm;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- nambah stok pas status berubah menjadi 'done'
CREATE OR REPLACE FUNCTION tambah_stok()
RETURNS TRIGGER AS $$
BEGIN
    -- tambah stok kalo status berubah dari 'ongoing' ke 'done'
    IF OLD.status = 'ongoing' AND NEW.status = 'done' THEN
        UPDATE Film
        SET stock = stock + NEW.jumlah
        WHERE idFilm = NEW.idFilm;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger buat ngurangin sama nambah stok berdasarkan perubahan status
CREATE TRIGGER trigger_kurangi_stok
AFTER UPDATE OF status ON TransaksiFilm
FOR EACH ROW
WHEN (NEW.status = 'ongoing')
EXECUTE FUNCTION kurangi_stok();

CREATE TRIGGER trigger_tambah_stok
AFTER UPDATE OF status ON TransaksiFilm
FOR EACH ROW
WHEN (NEW.status = 'done')
EXECUTE FUNCTION tambah_stok();