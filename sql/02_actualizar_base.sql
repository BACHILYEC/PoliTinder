-- Actualización repetible, conserva cuentas, fotos y conversaciones.
USE politinder;
CREATE TABLE IF NOT EXISTS `SEXUALORIENTATION` (
  `idSexualOrientation` INT PRIMARY KEY AUTO_INCREMENT,
  `type` VARCHAR(80) NOT NULL UNIQUE
);
INSERT IGNORE INTO SEXUALORIENTATION (idSexualOrientation,type) VALUES (1,'Prefiero no indicarlo'),(2,'Heterosexual'),(3,'Homosexual'),(4,'Bisexual'),(5,'Pansexual'),(6,'Asexual');
SET @change = IF((SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='USER' AND column_name='idSexualOrientation')=0,'ALTER TABLE `USER` ADD COLUMN idSexualOrientation INT NOT NULL DEFAULT 1', 'SELECT 1');
PREPARE migration_statement FROM @change;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
SET @change = IF((SELECT COUNT(*) FROM information_schema.key_column_usage WHERE table_schema=DATABASE() AND table_name='USER' AND column_name='idSexualOrientation' AND referenced_table_name='SEXUALORIENTATION')=0,'ALTER TABLE `USER` ADD CONSTRAINT fk_user_orientation FOREIGN KEY (idSexualOrientation) REFERENCES SEXUALORIENTATION(idSexualOrientation)', 'SELECT 1');
PREPARE migration_statement FROM @change;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
