# PoliTinder

Aplicación Java 25 + Swing + MySQL para ejecutar desde VS Code. Las dependencias ya están en `lib/`; no requiere Maven ni un ejecutable `.exe`.

## Ejecutar

Abre esta carpeta en VS Code y ejecuta `src/politinder/Main.java` con Run Java. La conexión usa por defecto `localhost:3306` y `root`; introduce tu contraseña MySQL en la pantalla de conexión. No se guarda en disco.

- Base nueva: **Crear base + datos demo**, o ejecuta en Workbench `sql/01_ddl.sql` seguido de `sql/02_dml.sql` sobre tablas vacías.
- Base existente: **Ampliar demo a 30 usuarios**, o ejecuta `sql/04_ampliar_demo.sql` con la app cerrada. Este archivo conserva usuarios, claves y chats existentes y puede repetirse sin duplicar datos.
- Después de ampliar, usa las cuentas de `CUENTAS_PRUEBA.txt`. La contraseña inicial demo es `PoliDemo25!`.

El DDL inicial crea exactamente diez tablas. El DML inicial contiene 30 perfiles ficticios y 70 fotos: cinco perfiles con cuatro fotos y veinticinco con dos. En una base existente se añaden las diez cuentas nuevas y se completan hasta dos fotos por cuenta demo; tus cuentas personales conservan sus fotos.

## Entregables de SQL y documentación

| Archivo | Contenido |
|---|---|
| `sql/01_ddl.sql` | Tablas, claves foráneas, índices y restricciones del ER. |
| `sql/02_dml.sql` | Carga inicial: usuarios, fotos, likes, matches, chats, mensajes y citas. |
| `sql/03_reportes.sql` | Nueve reportes; la app lee este mismo archivo. |
| `sql/04_ampliar_demo.sql` | Ampliación para una base ya poblada. |
| `sql/EXPLICACION_DML.txt` | Explicación de cada inserción, claves y funciones utilizadas. |
| `CUENTAS_PRUEBA.txt` | Seis cuentas y ejemplos de pruebas. |
| `docs/ER/PoliTinder_ER_original.jpg` | Imagen original del ER, sin modificaciones. |
| `docs/ER/EXPLICACION_ER.md` | Entidades, relaciones y reglas del modelo. |
| `docs/VALIDACION.txt` | Resultado de las verificaciones y límites de la revisión. |
| `photos/PROMPTS_NUEVAS_FOTOS.txt` | Prompts utilizados para completar las fotografías ficticias. |

## Interfaz

La ventana ocupa el 92% de la altura disponible y mantiene un formato vertical centrado. Es redimensionable, respeta la barra de tareas y desplaza el contenido en pantallas pequeñas. Usa una cabecera degradada violeta-fucsia, botones de acento y transiciones breves al abrir pantallas y cargar fotografías.

Descubrir recorre los candidatos en ciclos aleatorios y excluye los matches existentes. Los chats requieren dos likes recíprocos. La agenda evita reservar dos citas en el mismo minuto para cualquiera de los participantes.

Las fotos están en `photos/`; MySQL guarda sus rutas relativas. Conserva esta carpeta al trasladar el proyecto. Los nombres, biografías e imágenes de demostración son ficticios.

## Reportes en Workbench

Abre `sql/03_reportes.sql`. Las variables del inicio (`@edad_min`, `@edad_max`, `@carrera`, `@facultad`, `@corte`) permiten cambiar filtros. En la app se sustituyen por parámetros JDBC. El archivo antiguo de reportes `.txt` se reemplaza por este `.sql`.

La consola SQL sigue disponible para consultas manuales. Sus sentencias pueden modificar la base y no pasan por las validaciones de negocio de Java.

## Ejercicios SQL

En docs/ejercicios/PoliTinder_Ejercicios_SQL_y_Subconsultas.pdf encontrarás 40 ejercicios sin resolver, incluidos los temas de subconsultas, organizados por dificultad en ocho páginas.

