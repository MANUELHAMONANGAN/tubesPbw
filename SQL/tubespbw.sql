DROP TABLE IF EXISTS TransaksiFilm CASCADE;
DROP TABLE IF EXISTS Transaksi CASCADE;
DROP TABLE IF EXISTS Rating CASCADE;
DROP TABLE IF EXISTS FilmAktor CASCADE;
DROP TABLE IF EXISTS FilmGenre CASCADE;
DROP TABLE IF EXISTS Aktor CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TYPE IF EXISTS rent_enum CASCADE;
DROP TYPE IF EXISTS role_enum CASCADE;
DROP TYPE IF EXISTS status_rent CASCADE;
DROP TABLE IF EXISTS Film CASCADE;

CREATE TYPE rent_enum AS ENUM ('Pinjam', 'Pengembalian');
CREATE TYPE status_rent AS ENUM ('ongoing', 'done');
CREATE TYPE role_enum AS ENUM ('Pelanggan', 'Admin');

CREATE TABLE Film (
    idFilm SERIAL PRIMARY KEY,
    judul VARCHAR(60),
    stock int,
    coverFilm VARCHAR(255),
    hargaPerHari int,
	deskripsi TEXT,
	durasi int,
    tahunRilis int,
    averageRating float,
    stokTersedia int
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
    fotoProfil VARCHAR(255)
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
    total int --semua film (termasuk berapa harinya)
    --metodePembayaran ??
);

CREATE TABLE TransaksiFilm (
    idTransaksi int REFERENCES Transaksi(idTransaksi) ON DELETE CASCADE,
    idFilm int REFERENCES Film(idFilm),
    totalHari int,
	jumlah int DEFAULT 1,
    totalHarga int, --1 film berapa hari
	status status_rent DEFAULT 'ongoing',
	batasPengembalian date,  --status sama bataspengembalian per film nya
	PRIMARY KEY (idTransaksi, idFilm)
);

CREATE TABLE Rating (
	idRating SERIAL PRIMARY KEY,
	idFilm int REFERENCES Film(idFilm) ON DELETE CASCADE,
	idUser int REFERENCES Users(idUser) ON DELETE CASCADE,
	rating float CHECK (rating BETWEEN 1 AND 5),
	tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--ON DELETE CASCADE buat kalo table parent dihapus, table yang berkaitan (child) bakal kehapus juga

SELECT *
FROM Users;



--buat hitung averageRating
SELECT idFilm, 
       ROUND(AVG(rating), 2) AS averageRating
FROM Rating
GROUP BY idFilm;


--update averageRating
UPDATE Film
SET averageRating = (
    SELECT ROUND(AVG(rating), 2)
    FROM Rating
    WHERE Rating.idFilm = Film.idFilm
)
WHERE idFilm IN (
    SELECT DISTINCT idFilm
    FROM Rating
);


--buat stokTersedia bisa nambah atau kurang, jadi ambil jumlah dari table TransaksiFilm
--buat ngurangin stok
UPDATE Film
SET stockTersedia = stockTersedia - (
    SELECT jumlah
    FROM TransaksiFilm
    WHERE idTransaksi = 1 AND idFilm = 1
)
WHERE idFilm = 1;

--buat nambahin stok
UPDATE Film
SET stockTersedia = stockTersedia + (
    SELECT jumlah
    FROM TransaksiFilm
    WHERE idTransaksi = 1 AND idFilm = 1 
)
WHERE idFilm = 1;

--trigger buat ngurangin stok
CREATE OR REPLACE FUNCTION kurangi_stok()
RETURNS TRIGGER AS $$
BEGIN
    -- ngurangin stok film berdasarkan jumlah
    UPDATE Film
    SET stockTersedia = stockTersedia - NEW.jumlah
    WHERE idFilm = NEW.idFilm;

    RETURN NEW; -- ngembaliin data baru (baris yang dimasukkan)
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER setelah_peminjaman
AFTER INSERT ON TransaksiFilm -- Trigger berjalan setelah INSERT ke TransaksiFilm
FOR EACH ROW
EXECUTE FUNCTION kurangi_stok();


--triger buat nambah stok
CREATE OR REPLACE FUNCTION tambah_stok()
RETURNS TRIGGER AS $$
BEGIN
    -- nambahin stok film berdasarkan jumlah
    UPDATE Film
    SET stockTersedia = stockTersedia + OLD.jumlah
    WHERE idFilm = OLD.idFilm;

    RETURN OLD; -- ngembaliin data lama (baris yang diperbarui)
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER setelah_pengembalian
AFTER UPDATE OF status ON TransaksiFilm -- Trigger berjalan setelah kolom status diperbarui
FOR EACH ROW
WHEN (NEW.status = 'done') -- Hanya dijalankan pas status 'done'
EXECUTE FUNCTION tambah_stok();

--drop trigger
-- Hapus Trigger
DROP TRIGGER IF EXISTS setelah_peminjaman ON TransaksiFilm;
DROP TRIGGER IF EXISTS setelah_pengembalian ON TransaksiFilm;

-- Hapus Fungsi
DROP FUNCTION IF EXISTS kurangi_stok;
DROP FUNCTION IF EXISTS tambah_stok;




--nyoba triger
INSERT INTO Film (judul, stock, coverFilm, hargaPerHari, deskripsi, durasi, tahunRilis, averageRating, stokTersedia) 
VALUES 
('Inception', 10, 'inception.jpg', 15000, 'Film tentang mimpi berlapis-lapis.', 148, 2010, 4.8, 10);

SELECT * FROM Film;

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (1, 1, 3, 2, 45000, CURRENT_DATE + INTERVAL '3 days');

--stok berkurang setelah transaksifilm


UPDATE TransaksiFilm
SET status = 'done'
WHERE idTransaksi = 1 AND idFilm = 1;

--stok bertambah pas status done
