# Las nueve consultas del grupo

El archivo original se conserva byte por byte en sql y en docs/entrega. Reportes ejecuta `sql/03_reportes.sql`, con las mismas nueve preguntas.

| Nº | Consulta | Adaptación |
|---|---|---|
| 1 | Todas las carreras | Nombre de tabla normalizado. |
| 2 | ID y nombre de usuarios | Identificador USER delimitado. |
| 3 | Citas con DAY(fecha) > 9 | Se conserva la condición y el orden por día del mes. |
| 4 | Fotos principales | Misma condición y orden descendente de usuario. |
| 5 | Usuarios de carreras de ingeniería | Mismos JOIN y LIKE; se usa la colación del servidor. |
| 6 | Citas EPN o Alameda con participantes | Misma lógica y orden. |
| 7 | Carreras por facultad | GROUP BY F.idFaculty, F.name explícito. |
| 8 | Edad máxima por carrera y facultad | Misma agregación, JOIN y agrupación. |
| 9 | Matches por carrera | COUNT(DISTINCT pm.idPoliMatch), GROUP BY m.idMajor, m.name. |

La consulta 9 original cuenta participaciones: cuando dos usuarios de la misma carrera tienen un match, cuenta dos filas. Para que «Cantidad de PoliMatch» mida parejas distintas se usa DISTINCT. Un match entre dos carreras sigue apareciendo una vez en cada carrera. Los nombres de tablas se normalizan a mayúsculas y se delimitan para coincidir con el DDL también en sistemas sensibles a mayúsculas.

Las condiciones y columnas del original se conservan excepto esas correcciones documentadas. No se añaden parámetros de edad/carrera inventados: estas nueve consultas tienen filtros literales. Los comentarios @report y @end permiten que la app lea el SQL y sus títulos.
