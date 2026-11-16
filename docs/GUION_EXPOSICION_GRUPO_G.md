# Guion de exposición · PoliTinder · Grupo G

Ismael Heredia, Adrián Muñoz, Samuel Hinojosa y Stephanie Lozada.

Exposición sugerida: 8–10 minutos más la demostración. Seguir el orden del Canva.

## 1. PoliTinder

Presentamos PoliTinder, una organización ficticia que conecta estudiantes adultos de la EPN. Grupo G: Ismael Heredia, Adrián Muñoz, Samuel Hinojosa y Stephanie Lozada. La exposición se centra en el modelo de datos que sostiene los encuentros. No es un servicio oficial de la EPN.

## 2. El caso de PoliTinder

Desde la perspectiva de Ana: crea su cuenta, elige los datos de su perfil y carga fotos. Descubre personas y expresa interés. Un like unilateral no crea chat; cuando Luis corresponde, la app registra el match y abre la conversación. Ambos pueden agendar una cita. MySQL conserva los datos entre sesiones. Descubrir excluye matches actuales y la agenda comprueba conflictos por participante.

## 3. Entidades que sostienen la experiencia

Los símbolos ayudan a agrupar conceptos. FACULTY y MAJOR ubican académicamente al usuario. GENDER y SEXUALORIENTATION son catálogos distintos y elegidos por la persona. USER almacena su cuenta; USERPHOTO sus rutas de fotos. LIKE, POLIMATCH, CHAT, MESSAGE y POLIDATE describen la interacción. Las once tablas se consultan o modifican a través de formularios, servicios, galerías, chats, agenda y reportes.

## 4. Del perfil a MySQL

Una fila USER se identifica con idUser. Las claves foráneas apuntan a catálogos, evitando repetir sus descripciones. La edad se calcula desde bornDate. Las contraseñas son hashes PBKDF2. La app usa Java 25, Swing, FlatLaf y JDBC Connector/J. MySQL con InnoDB conserva relaciones y transacciones; utf8mb4 admite Unicode. La orientación se guarda al registrarse o editar el perfil, nunca se infiere de la imagen o nombre.

## 5. Relaciones entre entidades

La tabla sigue las relaciones de la imagen proporcionada, ordenando correctamente el lado uno y el lado muchos: una facultad tiene carreras y un match tiene citas. Se precisan dos casos: el modelo físico tiene dos referencias a LIKE, no una relación global 1:1 con LIKE; y CHAT es único por match en SQL, aunque la app crea match y chat juntos. Las dos referencias de LIKE a USER tienen roles diferentes. No se cambian las imágenes originales para ocultar estas diferencias.

## 6. MER V1

Primera versión: usuarios, carrera, facultad, género y citas, además de preferencias y planes. Todavía no detalla todas las interacciones actuales. Las preferencias y planes quedaron fuera del alcance; no deben presentarse como funcionalidades implementadas.

## 7. MER mejorado

El MER mejorado incorpora orientación sexual, fotos, likes, matches, chats y mensajes. Comparar con la versión inicial. La relación match-like se implementa físicamente con dos claves foráneas, como se ve en el ER siguiente. maxMatch aparece como atributo en esta imagen conceptual, pero no en el ER físico suministrado ni en la app actual; no se inventa un límite de matches.

## 8. ER: esquema de la base implementada

Este ER es la referencia para la implementación. SEXUALORIENTATION y USER.idSexualOrientation son la incorporación nueva. Todas las tablas del diagrama están representadas en MySQL y participan en la app. La facultad se obtiene a través de MAJOR. El match conserva idLike1 e idLike2. Las fotos tienen su propio propietario y los mensajes su chat y emisor.

## 9. Scripts SQL

Transición para explicar los scripts. Abrir 01_ddl.sql: crea las once tablas y carga la demo ampliada en un solo archivo. Distinguir que CREATE es DDL e INSERT es DML aunque estén juntos. Para una base existente se utiliza 02_actualizar_base.sql, nunca se vuelve a ejecutar la instalación completa. Luego abrir 03_reportes.sql y el original Queries a presentar.sql. El original se conserva sin modificar.

## 10. Prueba de scripts SQL

Transición para ejecutar los nueve SELECT. 1: carreras completas. 2: IDs y nombres. 3: citas cuyo día del mes es mayor que nueve, no significa posteriores al día nueve de una fecha concreta. 4: fotos principales. 5: usuarios de carreras que empiezan por ingeniería, con género. 6: citas de EPN o Alameda y participantes. 7: cantidad de carreras por facultad. 8: edad máxima por carrera y facultad. 9: matches por carrera. En la versión de app se normalizan identificadores, se agrupa explícitamente por facultad y se usa COUNT(DISTINCT pm.idPoliMatch) para evitar duplicar parejas en una misma carrera. La imagen y el SQL original se conservan. Explicar pregunta, tablas y resultado antes de pasar al siguiente SELECT.

## 11. Bibliografía y uso de IA

Referencia bibliográfica: Silberschatz, A., Korth, H. F., y Sudarshan, S. (2019). Database System Concepts (7.ª ed.). McGraw-Hill. https://db-book.com/. Se declara el uso de ChatGPT de OpenAI para preparar la presentación, generar imágenes ficticias y apoyar el DDL. No se atribuyen las imágenes de los diagramas a ChatGPT: son las aportadas por el grupo.

## Antes de presentar

Abrir Workbench, VS Code y la app. La instalación completa se ejecuta una sola vez sobre una base nueva. La migración conserva las cuentas. Probar el acceso con las cuentas demo. Las fechas de nacimiento, creación, actualización, mensajes y citas tienen variedad en la carga nueva. Las fechas históricas de cuentas existentes se conservan al migrar.

## Preguntas frecuentes

- **¿Dónde están las fotos?** En archivos; USERPHOTO guarda rutas y propietario.
- **¿Por qué dos likes?** Uno expresa interés unilateral; el inverso confirma reciprocidad.
- **¿Puede una cita pasada considerarse realizada?** No: falta un atributo de asistencia.
- **¿Por qué no hay preferencias o planes?** Están en el MER V1 histórico, no en el ER implementado.
- **¿Por qué no hay maxMatch?** Es un atributo del MER conceptual mejorado que no aparece en el ER físico ni tiene una regla de negocio definida.
- **¿El total de matches por carrera suma el total global?** No necesariamente: un match entre dos carreras aparece en ambas. Dentro de cada carrera se cuenta una sola vez.
