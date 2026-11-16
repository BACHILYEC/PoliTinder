# PoliTinder

Proyecto del Grupo G para la Escuela Politécnica Nacional. Java 25, Swing, FlatLaf y MySQL mediante JDBC. Ejecutar `src/politinder/Main.java` desde VS Code; las bibliotecas están en `lib/`, sin Maven.

## Instalar o actualizar

- **Base nueva:** botón «Crear base + datos demo» o ejecutar `sql/01_ddl.sql` una sola vez. Este archivo unifica DDL y DML: once tablas, 30 usuarios, 70 fotos, 16 likes, 6 matches, 6 chats, 24 mensajes y 8 citas.
- **Base existente:** usar «Actualizar esquema existente», conectar normalmente con la app actualizada o ejecutar `sql/02_actualizar_base.sql` desde Workbench. Añade SEXUALORIENTATION y su FK sin borrar perfiles, claves ni conversaciones. Las cuentas existentes quedan en «Prefiero no indicarlo» y pueden elegir otro valor en su perfil.
- La contraseña MySQL se introduce en la app, permanece en memoria y no se guarda en Git. No ejecutar el instalador inicial sobre la base existente.
- `CUENTAS_PRUEBA.txt` contiene los accesos ficticios para demostrar la app.

La migración no modifica fechas históricas ni vuelve a insertar la demo. La variedad de fechas se incluye en la instalación nueva. El esquema utiliza los valores predeterminados InnoDB y utf8mb4; el instalador los comprueba.

## Reportes

`sql/03_reportes.sql` contiene las nueve consultas aportadas por el grupo, con títulos y explicaciones para la app. «Recargar consultas» vuelve a leer el archivo. Los filtros se muestran según los parámetros presentes; estas nueve consultas tienen condiciones literales y no necesitan filtros adicionales.

El archivo original, sin alterar, está en `sql/Queries a presentar.sql` y `docs/entrega/Queries a presentar.sql`. Las diferencias están en `docs/ADAPTACION_QUERIES.md`: identificadores normalizados, GROUP BY explícito y matches distintos por carrera. Los resultados se muestran con las columnas devueltas por SQL.

## Modelo y uso

`docs/ER/ERjpeg.jpeg` es el esquema físico vigente. Los otros dos diagramas son el MER V1 y el MER mejorado. `docs/ER/EXPLICACION_ER.md` documenta las once tablas y cómo participa cada una en la app. Preferencias, suscripciones y maxMatch aparecen en diseños conceptuales, pero no en el ER físico actual.

Registro y perfil permiten elegir facultad/carrera, género y orientación. Descubrir mantiene ciclos aleatorios y excluye matches. Dos likes recíprocos habilitan el chat. La agenda evita citas simultáneas para un participante. Las fotos se conservan en archivos, con rutas en USERPHOTO. La consola SQL permite ejecutar instrucciones manuales que pueden eludir las reglas de Java.

## Entrega y exposición

- `docs/presentacion/ENLACE_CANVA.txt`: enlace al Canva editable.
- `docs/GUION_EXPOSICION_GRUPO_G.md`: explicación por diapositiva y demostración de las consultas.
- `docs/entrega/PoliTinder_Scripts_SQL.zip`: instalación, migración opcional, reportes y SQL original.
- `sql/EXPLICACION_DML.txt`: inserciones, fechas y funciones utilizadas.
- `docs/AUDITORIA_Y_PRUEBAS.md`: cobertura y depuración.
- `docs/ejercicios/`: material de práctica conservado.

## Pruebas

Las pruebas de integración usan exclusivamente localhost:33307 con una instancia MySQL desechable. `TestSuite --db` exige una base vacía y prueba instalación y reglas. `SchemaVerification` prueba el nuevo catálogo y la migración desde el esquema anterior. `UiSmokeTest` recorre las pantallas sin guardar configuración de conexión; sus imágenes de verificación quedan en `test-output/`, fuera de docs y de Git. No ejecutar estas pruebas contra el servidor habitual.

Se conservan `photos/users/`, `testImages/`, bibliotecas y licencias. `test/fixtures/legacy_schema.sql` es intencional: reproduce la versión antigua para comprobar la migración, no es un script de instalación vigente.
