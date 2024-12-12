DROP TABLE IF EXISTS TransaksiFilm CASCADE;
DROP TABLE IF EXISTS Transaksi CASCADE;
DROP TABLE IF EXISTS Reviews CASCADE;
DROP TABLE IF EXISTS FilmAktor CASCADE;
DROP TABLE IF EXISTS FilmGenre CASCADE;
DROP TABLE IF EXISTS Aktor CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Users CASCADE;
DROP TYPE IF EXISTS rent_enum CASCADE;
DROP TYPE IF EXISTS role_enum CASCADE;
DROP TABLE IF EXISTS Film CASCADE;

CREATE TYPE rent_enum AS ENUM ('Pinjam', 'Pengembalian');
CREATE TYPE status_rent AS ENUM ('ongoing', 'done');

CREATE TABLE Film (
    idFilm SERIAL PRIMARY KEY,
    judul VARCHAR(60),
    stock int,
    coverFilm VARCHAR(255),
    hargaPerHari int,
	deskripsi TEXT,
	durasi int
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

CREATE TABLE Pelanggan (
    idPelanggan SERIAL PRIMARY KEY,
    nama VARCHAR(60),
    nomorTelepon VARCHAR (13) UNIQUE,
    email VARCHAR (60) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE Admin (
    idAdmin SERIAL PRIMARY KEY,
    nama VARCHAR(60),
    nomorTelepon VARCHAR (13) UNIQUE,
    email VARCHAR (60) UNIQUE,
    password VARCHAR(255)
);

CREATE TABLE Transaksi (
	idTransaksi SERIAL PRIMARY KEY,
    idUser int REFERENCES Users(idUser) ON DELETE CASCADE,
    tanggal TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipeTransaksi rent_enum,
    total int --semua film (termasuk berapa harinya)
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