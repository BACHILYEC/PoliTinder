-- Ampliación del catálogo existente: ejecutar con la app cerrada.

-- Repetible: no borra filas, no cambia contraseñas ni IDs existentes.

-- Completa hasta dos fotos para las cuentas demo; conserva galerías de cuatro.

USE politinder;

SET time_zone='-05:00';

START TRANSACTION;

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','mariana@politinder.test','pbkdf2$600000$pn7gYaOuv1tNqx6jDJGUbQ==$SxoSEAhbIdjQ8euDtuGggOeonWqIsPiOZIuYG8/Pmss=','Me encantan los jardines, el chocolate caliente y las conversaciones que empiezan con una buena pregunta.','Mariana Alejandra Rosero',DATE_SUB(CURDATE(),INTERVAL 24 YEAR),1,4
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='mariana@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','sebastian@politinder.test','pbkdf2$600000$n3jRiBSdkxu0z1GbUk9r/g==$GvI21kcTjbafsZUQpK3gCsELBMKzet0lS4EMoGZzhjQ=','Toco el bajo y colecciono rutas para caminar por Quito. Busco compañía para descubrir música y lugares nuevos.','Sebastián Emilio Almeida',DATE_SUB(CURDATE(),INTERVAL 25 YEAR),2,5
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='sebastian@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','daniela@politinder.test','pbkdf2$600000$oyOwBa/PptmYTdUzY8NxyA==$2OI3VzaxhOzUxCbPxsD+5JfppwcVAttdZ6pz6hYxzTI=','Entre libros y proyectos siempre encuentro tiempo para una merienda. Me gustan los museos y el humor tranquilo.','Daniela Carolina Espinosa',DATE_SUB(CURDATE(),INTERVAL 23 YEAR),1,6
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='daniela@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','alejandro@politinder.test','pbkdf2$600000$tGYF6JLNqdHfo0rhQBQk0g==$mRaxbNo+Wkw1041wlSX2xJNwd8WZhkdvZEVlb33BKBw=','El básquet, la cocina y los viajes cortos son mis planes favoritos. Me apunto a un café después de clases.','Alejandro José Burbano',DATE_SUB(CURDATE(),INTERVAL 26 YEAR),2,9
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='alejandro@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','antonella@politinder.test','pbkdf2$600000$9FZQcaoypf72qet6ZkNztg==$vR6nWk3DwY7IxO7kjGWo09MnZh8LG/0sKdx352tUoPo=','Fotografío montañas y aprendo a pintar con acuarela. Un paseo al aire libre siempre me alegra el día.','Antonella Belén Maldonado',DATE_SUB(CURDATE(),INTERVAL 24 YEAR),1,10
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='antonella@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','emilio@politinder.test','pbkdf2$600000$4hirDMo1vYQs9OiVxjTXCg==$vj605hEm7FwF7HErnPLmJWotOB8VukLOLUEVmbeTPs4=','Curioso por la ciencia y fan de los juegos de mesa. Tengo una lista larga de cafeterías por conocer.','Emilio Rafael Carrasco',DATE_SUB(CURDATE(),INTERVAL 27 YEAR),2,12
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='emilio@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','sara@politinder.test','pbkdf2$600000$t0WdQxsyvwbmUeZKR5cuaQ==$Gy7LLil+xfgxD3BXXeswUMfiGFedy0X41gDlg7+A1U0=','Bailo, leo novelas y disfruto cocinar con mis amigos. Valoro las conversaciones honestas y las risas espontáneas.','Sara Gabriela Hidalgo',DATE_SUB(CURDATE(),INTERVAL 25 YEAR),1,16
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='sara@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','julian@politinder.test','pbkdf2$600000$xFVcfSBHqVOx8Zdk3oDKtw==$eZdVOPJB3eFkB7tXiVdqJ6QJa1Ia55VR/Ugkitfx6rQ=','Me gustan la guitarra, el ciclismo y los planes sencillos. Recomiéndame una canción para el camino.','Julián Esteban Paredes',DATE_SUB(CURDATE(),INTERVAL 23 YEAR),2,18
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='julian@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','victoria@politinder.test','pbkdf2$600000$XRvyLQ8fJTDp6XXKACthdw==$U4YrxkjxN4FU2Or0zors7e4D0LzOVjKI1fvVRBbF/n0=','Visito exposiciones y dibujo en cualquier libreta. Me gustaría conocer a alguien para compartir nuevas experiencias.','Victoria Isabel Terán',DATE_SUB(CURDATE(),INTERVAL 26 YEAR),1,21
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='victoria@politinder.test');

INSERT INTO `USER` (status,mail,password,biography,name,bornDate,idGender,idMajor)
SELECT 'ACTIVE','tomas@politinder.test','pbkdf2$600000$zNTbBz7iRTY7ozWVorpakg==$R24JPm0hMxKKgz5Rqk2FtT3GnlfVOJ9wslVu6mlcm0c=','Me entusiasman la tecnología y las caminatas largas. Un helado y una buena conversación son un gran comienzo.','Tomás Alejandro Valverde',DATE_SUB(CURDATE(),INTERVAL 24 YEAR),2,25
WHERE NOT EXISTS (SELECT 1 FROM `USER` WHERE mail='tomas@politinder.test');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/01/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='ana@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/01/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/01/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='ana@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/01/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/02/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='luis@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/02/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/02/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='luis@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/02/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/03/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sofia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/03/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/03/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sofia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/03/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/04/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='mateo@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/04/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/04/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='mateo@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/04/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/05/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='valentina@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/05/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/05/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='valentina@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/05/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/06/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='diego@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/06/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/06/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='diego@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/06/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/07/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='camila@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/07/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/07/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='camila@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/07/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/08/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='nicolas@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/08/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/08/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='nicolas@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/08/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/09/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='isabella@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/09/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/09/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='isabella@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/09/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/10/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='daniel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/10/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/10/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='daniel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/10/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/11/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='lucia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/11/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/11/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='lucia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/11/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/12/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='emilia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/12/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/12/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='emilia@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/12/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/13/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='joaquin@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/13/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/13/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='joaquin@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/13/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/14/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='martina@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/14/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/14/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='martina@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/14/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/15/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='gabriel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/15/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/15/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='gabriel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/15/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/16/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='renata@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/16/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/16/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='renata@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/16/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/17/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='andres@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/17/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/17/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='andres@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/17/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/18/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='elena@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/18/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/18/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='elena@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/18/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/19/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='paula@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/19/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/19/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='paula@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/19/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/20/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='samuel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/20/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/20/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='samuel@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/20/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/21/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='mariana@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/21/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/21/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='mariana@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/21/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/22/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sebastian@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/22/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/22/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sebastian@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/22/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/23/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='daniela@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/23/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/23/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='daniela@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/23/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/24/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='alejandro@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/24/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/24/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='alejandro@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/24/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/25/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='antonella@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/25/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/25/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='antonella@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/25/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/26/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='emilio@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/26/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/26/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='emilio@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/26/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/27/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sara@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/27/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/27/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='sara@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/27/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/28/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='julian@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/28/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/28/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='julian@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/28/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/29/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='victoria@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/29/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/29/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='victoria@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/29/02.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/30/01.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='tomas@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/30/01.png');

INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)
SELECT 'photos/demo-real/30/02.png',NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser AND p.isProfilePhoto=TRUE),u.idUser
FROM `USER` u WHERE u.mail='tomas@politinder.test'
AND (SELECT COUNT(*) FROM USERPHOTO p WHERE p.idUser=u.idUser)<2
AND NOT EXISTS (SELECT 1 FROM USERPHOTO p WHERE p.pathPhoto='photos/demo-real/30/02.png');

COMMIT;

-- Comprobación: deben aparecer al menos 30 usuarios en la demo completa.

SELECT COUNT(*) AS total_usuarios FROM `USER`;

SELECT u.idUser,u.name,COUNT(p.idUserPhoto) AS fotos FROM `USER` u LEFT JOIN USERPHOTO p ON p.idUser=u.idUser GROUP BY u.idUser,u.name ORDER BY u.idUser;
