-- Nueve consultas aportadas por el Grupo G, adaptadas para MySQL.
USE politinder;

-- @report 1 | Todas las carreras
-- Lista el catálogo completo de carreras.
SELECT * FROM `MAJOR`;
-- @end

-- @report 2 | Identificadores y nombres de usuarios
-- Muestra el identificador y nombre de cada usuario.
SELECT idUser, name FROM `USER`;
-- @end

-- @report 3 | PoliDates cuyo día del mes supera nueve
-- Filtra DAY(dateOfPoliDate) > 9 y ordena por día del mes, no por fecha completa.
SELECT place as "Lugar de Cita", dateOfPolidate as Fecha
FROM `POLIDATE`
WHERE DAY(dateOfPolidate) > 9
ORDER BY DAY(dateOfPolidate) desc;
-- @end

-- @report 4 | Fotografías principales
-- Muestra las fotos principales ordenadas por usuario descendente.
SELECT idUser, pathPhoto as "Ruta de la foto"
FROM `USERPHOTO`
WHERE isprofilephoto = TRUE
ORDER BY idUser desc;
-- @end

-- @report 5 | Usuarios de carreras de ingeniería
-- Relaciona usuario, carrera y género; filtra carreras que comienzan por ingeniería.
select u.name as 'Nombre de Usuario', m.name as Carrera, g.gendertype as 'Género'
from `USER` u
join `MAJOR` m on u.idmajor = m.idmajor
join `GENDER` g on u.idgender = g.idgender
where m.name like 'ingenieria%';
-- @end

-- @report 6 | PoliDates en la EPN o La Alameda
-- Relaciona citas, matches, likes y ambos participantes.
SELECT P.idPoliDate, P.place as "Lugar", U1.name AS Usuario1, U2.name AS Usuario2
FROM `POLIDATE` AS P JOIN `POLIMATCH` AS M ON P.idPoliMatch = M.idPoliMatch
JOIN `LIKE` AS L1 ON M.idLike1 = L1.idLike
JOIN `USER` AS U1 ON L1.idUser1 = U1.idUser
JOIN `USER` AS U2 ON L1.idUser2 = U2.idUser
WHERE P.place like "%EPN%" or P.place like "%Alameda%"
ORDER BY U1.name desc;
-- @end

-- @report 7 | Número de carreras por facultad
-- Agrupa por facultad y cuenta sus carreras.
SELECT F.name AS "Facultad", COUNT(M.name) AS "Número de Carreras"
FROM `FACULTY` AS F
JOIN `MAJOR` AS M ON M.idFaculty = F.idFaculty
GROUP BY F.idFaculty, F.name;
-- @end

-- @report 8 | Edad máxima por carrera y facultad
-- Calcula la mayor edad de los usuarios de cada carrera.
SELECT M.name AS Carrera, F.name AS Facultad, MAX(TIMESTAMPDIFF(YEAR, U.bornDate, CURDATE())) AS 'Edad máxima'
FROM `MAJOR` AS M
JOIN `USER` AS U ON U.idMajor = M.idMajor
JOIN `FACULTY` AS F ON M.idFaculty = F.idFaculty
GROUP BY M.idMajor, M.name, F.name;
-- @end

-- @report 9 | Matches por carrera
-- Cuenta matches distintos que involucran estudiantes de cada carrera. Un match entre carreras aparece en ambas.
select m.name as Carrera, COUNT(DISTINCT pm.idPoliMatch) as "Cantidad de PoliMatch"
from `POLIMATCH` as pm
join `LIKE` as l1 on pm.idlike1 = l1.idlike
join `LIKE` as l2 on pm.idlike2 = l2.idlike
join `USER` as u on u.iduser in (l1.iduser1, l1.iduser2,l2.iduser1,l2.iduser2)
join `MAJOR` as m on u.idmajor = m.idmajor
GROUP BY m.idMajor, m.name;
-- @end
