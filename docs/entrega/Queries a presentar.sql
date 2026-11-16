USE politinder;

-- Sentencias simples sin WHERE

SELECT * FROM major;

SELECT idUser, name FROM USER;

-- Dos selects de una o más tablas con condiciones (where) y order by

SELECT place as "Lugar de Cita", dateOfPolidate as Fecha
FROM Polidate
WHERE DAY(dateOfPolidate) > 9
ORDER BY DAY(dateOfPolidate) desc;

SELECT idUser, pathPhoto as "Ruta de la foto"
FROM UserPhoto
WHERE isprofilephoto = TRUE
ORDER BY idUser desc;

-- Dos selects con dos o más tablas, aplicar join y where

select u.name as 'Nombre de Usuario', m.name as Carrera, g.gendertype as 'Género'
from user u
join major m on u.idmajor = m.idmajor
join gender g on u.idgender = g.idgender
where m.name like 'ingenieria%';

SELECT P.idPoliDate, P.place as "Lugar", U1.name AS Usuario1, U2.name AS Usuario2
FROM PoliDate AS P JOIN PoliMatch AS M ON P.idPoliMatch = M.idPoliMatch
JOIN `Like` AS L1 ON M.idLike1 = L1.idLike
JOIN `User` AS U1 ON L1.idUser1 = U1.idUser
JOIN `User` AS U2 ON L1.idUser2 = U2.idUser
WHERE P.place like "%EPN%" or P.place like "%Alameda%"
ORDER BY U1.name desc;

-- Dos selects con dos o más tablas, aplicar funciones de agregación y join

SELECT F.name AS "Facultad", COUNT(M.name) AS "Número de Carreras"
FROM Faculty AS F
JOIN Major AS M ON M.idFaculty = F.idFaculty
GROUP BY M.idFaculty;

SELECT M.name AS Carrera, F.name AS Facultad, MAX(TIMESTAMPDIFF(YEAR, U.bornDate, CURDATE())) AS 'Edad máxima'
FROM Major AS M
JOIN `User` AS U ON U.idMajor = M.idMajor
JOIN Faculty AS F ON M.idFaculty = F.idFaculty
GROUP BY M.idMajor, M.name, F.name;


-- Un select que incluya join, where y/o funciones de agregación que permita consultar 
-- algo relevante de su organización ficticia. Presentar la pregunta y el select.
-- Que el resultado sea filas y columnas, como si se tratara de un reporte.

select m.name as Carrera, count(pm.idpolimatch) as "Cantidad de PoliMatch"
from polimatch as pm
join `like` as l1 on pm.idlike1 = l1.idlike
join `like` as l2 on pm.idlike2 = l2.idlike
join user as u on u.iduser in (l1.iduser1, l1.iduser2,l2.iduser1,l2.iduser2)
join major as m on u.idmajor = m.idmajor
group by m.name;



