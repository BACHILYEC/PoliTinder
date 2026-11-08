-- PoliTinder · MySQL 8.0.16+ · ejecutar UNA VEZ en una base nueva.
-- Los valores predeterminados deben ser InnoDB y utf8mb4; la app los comprueba.
-- No se borran bases ni se ignoran errores. Si ya existe politinder, revisar antes de continuar.
CREATE DATABASE `politinder`;
USE `politinder`;

CREATE TABLE `FACULTY` (
  `idFaculty` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL UNIQUE
);
CREATE TABLE `MAJOR` (
  `idMajor` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  `idFaculty` INT NOT NULL,
  UNIQUE (`idFaculty`, `name`),
  FOREIGN KEY (`idFaculty`) REFERENCES `FACULTY` (`idFaculty`)
);
CREATE TABLE `GENDER` (
  `idGender` INT PRIMARY KEY AUTO_INCREMENT,
  `genderType` VARCHAR(60) NOT NULL UNIQUE
);
CREATE TABLE `USER` (
  `idUser` INT PRIMARY KEY AUTO_INCREMENT,
  `status` VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
  `mail` VARCHAR(254) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `biography` VARCHAR(1000) NOT NULL DEFAULT '',
  `name` VARCHAR(100) NOT NULL,
  `bornDate` DATE NOT NULL,
  `creationDate` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updateDate` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `idGender` INT NOT NULL,
  `idMajor` INT NOT NULL,
  CHECK (`status` IN ('ACTIVE','PAUSED')),
  FOREIGN KEY (`idGender`) REFERENCES `GENDER` (`idGender`),
  FOREIGN KEY (`idMajor`) REFERENCES `MAJOR` (`idMajor`)
);
CREATE TABLE `USERPHOTO` (
  `idUserPhoto` INT PRIMARY KEY AUTO_INCREMENT,
  `pathPhoto` VARCHAR(500) NOT NULL UNIQUE,
  `isProfilePhoto` BOOLEAN NOT NULL DEFAULT FALSE,
  `idUser` INT NOT NULL,
  CHECK (`isProfilePhoto` IN (0,1)),
  FOREIGN KEY (`idUser`) REFERENCES `USER` (`idUser`)
);
CREATE TABLE `LIKE` (
  `idLike` INT PRIMARY KEY AUTO_INCREMENT,
  `idUser1` INT NOT NULL,
  `idUser2` INT NOT NULL,
  CHECK (`idUser1` <> `idUser2`),
  UNIQUE (`idUser1`, `idUser2`),
  FOREIGN KEY (`idUser1`) REFERENCES `USER` (`idUser`),
  FOREIGN KEY (`idUser2`) REFERENCES `USER` (`idUser`)
);
CREATE TABLE `POLIMATCH` (
  `idPoliMatch` INT PRIMARY KEY AUTO_INCREMENT,
  `idLike1` INT NOT NULL,
  `idLike2` INT NOT NULL,
  CHECK (`idLike1` < `idLike2`),
  UNIQUE (`idLike1`, `idLike2`),
  FOREIGN KEY (`idLike1`) REFERENCES `LIKE` (`idLike`),
  FOREIGN KEY (`idLike2`) REFERENCES `LIKE` (`idLike`)
);
CREATE TABLE `CHAT` (
  `idChat` INT PRIMARY KEY AUTO_INCREMENT,
  `idPoliMatch` INT NOT NULL UNIQUE,
  FOREIGN KEY (`idPoliMatch`) REFERENCES `POLIMATCH` (`idPoliMatch`)
);
CREATE TABLE `MESSAGE` (
  `idMessage` INT PRIMARY KEY AUTO_INCREMENT,
  `sendDate` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `content` TEXT NOT NULL,
  `idChat` INT NOT NULL,
  `sender` INT NOT NULL,
  FOREIGN KEY (`idChat`) REFERENCES `CHAT` (`idChat`),
  FOREIGN KEY (`sender`) REFERENCES `USER` (`idUser`),
  INDEX `idx_message_chat_time` (`idChat`, `sendDate`, `idMessage`)
);
CREATE TABLE `POLIDATE` (
  `idPoliDate` INT PRIMARY KEY AUTO_INCREMENT,
  `place` VARCHAR(200) NOT NULL,
  `dateOfPoliDate` DATETIME NOT NULL,
  `idPoliMatch` INT NOT NULL,
  FOREIGN KEY (`idPoliMatch`) REFERENCES `POLIMATCH` (`idPoliMatch`),
  INDEX `idx_date_match_time` (`idPoliMatch`, `dateOfPoliDate`)
);
