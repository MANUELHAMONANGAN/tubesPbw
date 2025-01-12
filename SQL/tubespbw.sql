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
    coverFilm BYTEA,
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
    total BIGINT --semua film (termasuk berapa harinya) #Kalo pengembalian total 0 atau null ?
    --metodePembayaran ??
);

CREATE TABLE TransaksiFilm (
    idTransaksi int REFERENCES Transaksi(idTransaksi) ON DELETE CASCADE,
    idFilm int REFERENCES Film(idFilm),
    totalHari int, --Banyak hari dipinjem
	jumlah int DEFAULT 1, --Banyak DVD yg dipinjem
    totalHarga int, --Sub total buat DVD itu -> harga di tabel film * total hari * jumlah
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

INSERT INTO Film (judul, stock, coverFilm, hargaPerHari, deskripsi, durasi, tahunRilis, averageRating, stokTersedia) 
VALUES 
('Cars', 10, 'cars.jpg', 15000, 'Balapan mobil', 148, 2010, 4.8, 10);

INSERT INTO Film (judul, stock, coverFilm, hargaPerHari, deskripsi, durasi, tahunRilis, averageRating, stokTersedia) 
VALUES 
('Home alone', 10, 'homeAlone.jpg', 15000, 'Jailin penjahat.', 148, 2010, 4.8, 10);

INSERT INTO Film (judul, stock, coverFilm, hargaPerHari, deskripsi, durasi, tahunRilis, averageRating, stokTersedia) 
VALUES 
('Toys story', 10, 'toysStory.jpg', 15000, 'Boneka hidup.', 148, 2010, 4.8, 10);

SELECT * FROM Film;

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (1, 1, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (1, 2, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (1, 3, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (2, 1, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (2, 2, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian)
VALUES (2, 3, 3, 2, 90000, CURRENT_DATE + INTERVAL '3 days');

SELECT * FROM users;

INSERT INTO Genre (nama) VALUES ('Action'); --1
INSERT INTO Genre (nama) VALUES ('Adventure'); --2
INSERT INTO Genre (nama) VALUES ('Sci-Fi'); --3
INSERT INTO Genre (nama) VALUES ('Animation'); --4
INSERT INTO Genre (nama) VALUES ('Comedy'); --5
INSERT INTO Genre (nama) VALUES ('Family'); --6
INSERT INTO Genre (nama) VALUES ('Romance')

INSERT INTO FilmGenre (idFilm, idGenre) VALUES (1, 1);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (1, 2);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (1, 3);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (2, 2);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (2, 4);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (2, 5);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (3, 5);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (3, 6);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (4, 2);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (4, 4);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (4, 5);
INSERT INTO FilmGenre (idFilm, idGenre) VALUES (4, 6);

--stok berkurang setelah transaksifilm


UPDATE TransaksiFilm
SET status = 'done'
WHERE idTransaksi = 1 AND idFilm = 1;

--stok bertambah pas status done

INSERT INTO Users (nama, nomorTelepon, email, password) VALUES ('Nikolas', '082117127921', 'nikolas@gmail.com', 'niko123');
INSERT INTO Users (nama, nomorTelepon, email, password) VALUES ('Christ', '082117127922', 'christ@gmail.com', 'christ123');
INSERT INTO Users (nama, nomorTelepon, email, password, role) VALUES ('Nikolas', '082117127923', 'nikolas1@gmail.com', 'niko123', 'Admin');

INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-01 19:10:25', 'Pinjam', 10000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (2,'2024-11-01 19:20:25', 'Pinjam', 5000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-01', 'Pinjam', 20000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (2,'2024-11-01', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-01', 'Pinjam', 2000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (2,'2024-11-01', 'Pinjam', 30000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-01', 'Pinjam', 50000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (2,'2024-11-01', 'Pinjam', 75000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-01', 'Pinjam', 20000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (2,'2024-11-01', 'Pinjam', 2000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-02', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-03', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-03', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-03', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-04', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-04', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-05', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-05', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-05', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-06', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-06', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-06', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-07', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-07', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-07', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-07', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-07', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-08', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-08', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-09', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-09', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-09', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-10', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-10', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-10', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-11', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-12', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-13', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-13', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-14', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-15', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-15', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-16', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-16', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-16', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-16', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-16', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-17', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-17', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-17', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-17', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-17', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-18', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-18', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-19', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-20', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-20', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-20', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-21', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-22', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-23', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-23', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-23', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-23', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-23', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-24', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-25', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-26', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-27', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-28', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-29', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);
INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total) VALUES (1,'2024-11-30', 'Pinjam', 25000);

INSERT INTO TransaksiFilm ()

--LAPORAN BULANAN HOME (BULAN INI)
--GRAPH
SELECT DATE(tanggal) AS tanggal, SUM(total) AS total
FROM Transaksi
WHERE tipeTransaksi = 'Pinjam'
  	AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
GROUP BY DATE(tanggal)
ORDER BY tanggal;

--WEEKLY SALES
SELECT COALESCE(SUM(total) / (EXTRACT(WEEK FROM CURRENT_DATE) - EXTRACT(WEEK FROM DATE_TRUNC('month', CURRENT_DATE)) + 1), 0) as WeeklySales
FROM Transaksi
WHERE tipeTransaksi = 'Pinjam'
	AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)

--JUMLAH FILM DISEWA
SELECT COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
FROM Transaksi
INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi
WHERE tipeTransaksi = 'Pinjam'
	AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)

--TOP GENRE
SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
FROM
	(SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
	INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
	WHERE
		EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
	  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
	GROUP BY Genre.nama
	ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
ORDER BY JumlahPenyewaan DESC, Genre.nama
LIMIT 1;

--INCEPTION -> Action, Adventure, Sci-Fi
--CARS -> Adventure, Animation, Comedy
--HOME ALONE -> Comedy, Family

--JUMLAH
--Action = 2
--Adventure = 4
--Sci-Fi = 2
--Animation = 2
--Comedy = 4
--Family = 2

SELECT * FROM TransaksiFilm

--TOP 5 Film Paling Laku
SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
FROM
	(SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
	WHERE
		EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
	  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
	GROUP BY Film.judul
	ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
ORDER BY JumlahPenyewaan DESC, Film.judul
LIMIT 5;

--TOP 5 Film Paling Ga Laku
SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
FROM
	(SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
	WHERE
		EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
	  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
	GROUP BY Film.judul
	ORDER BY JumlahPenyewaan ASC, Film.judul) as PenjualanFilm
RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
ORDER BY JumlahPenyewaan ASC, Film.judul
LIMIT 5;

--List Out Of Stock
SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
FROM
	(SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
	WHERE
		EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
	  	AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
	GROUP BY Film.judul
	ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
WHERE Film.stock = 0
ORDER BY JumlahPenyewaan DESC, Film.judul;

--TOP 5 GENRE PALING BANYAK DIPINJAM
SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
FROM
	(SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
	INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
	WHERE
		EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
  		AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
	GROUP BY Genre.nama
	ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
ORDER BY JumlahPenyewaan DESC, Genre.nama
LIMIT 5;

------------------------------------------------------------------------------------------------
--LAPORAN BULANAN FILTER TANGGAL
--GRAPH
SELECT DATE(tanggal) AS tanggal, SUM(total) AS total
FROM Transaksi
WHERE tipeTransaksi = 'Pinjam'
  	AND tanggal >= '2024-11-01'
  	AND tanggal <= '2024-11-30'
GROUP BY DATE(tanggal)
ORDER BY tanggal;

--WEEKLY SALES
SELECT COALESCE(SUM(total) / 4, 0) as WeeklySales
FROM Transaksi
WHERE tipeTransaksi = 'Pinjam'
	AND tanggal >= '2024-11-01'
  	AND tanggal <= '2024-11-30'

--JUMLAH FILM DISEWA
SELECT COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
FROM Transaksi
INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi
WHERE tipeTransaksi = 'Pinjam'
	AND tanggal >= '2024-11-01'
  	AND tanggal <= '2024-11-30'

--TOP GENRE
SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
FROM
	(SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
	INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
	WHERE
		tanggal >= '2024-11-01'
	  	AND tanggal <= '2024-11-30'
	GROUP BY Genre.nama
	ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
ORDER BY JumlahPenyewaan DESC, Genre.nama
LIMIT 1;

--TOP 5 Film Paling Laku
SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
FROM
	(SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
	WHERE
		tanggal >= '2024-11-01'
	  	AND tanggal <= '2024-11-30'
	GROUP BY Film.judul
	ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
ORDER BY JumlahPenyewaan DESC, Film.judul
LIMIT 5;

--TOP 5 Film Paling Ga Laku
SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
FROM
	(SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
	WHERE
		tanggal >= '2024-11-01'
	  	AND tanggal <= '2024-11-30'
	GROUP BY Film.judul
	ORDER BY JumlahPenyewaan ASC, Film.judul) as PenjualanFilm
RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
ORDER BY JumlahPenyewaan ASC, Film.judul
LIMIT 5;

--TOP 5 GENRE PALING BANYAK DIPINJAM
SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
FROM
	(SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
	FROM Transaksi
	INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
	INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
	INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
	WHERE
		tanggal >= '2024-11-01'
	  	AND tanggal <= '2024-11-30'
	GROUP BY Genre.nama
	ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
ORDER BY JumlahPenyewaan DESC, Genre.nama
LIMIT 5;





