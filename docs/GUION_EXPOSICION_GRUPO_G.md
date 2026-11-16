# Guion de exposición · PoliTinder · Grupo G

Integrantes: Ismael Heredia, Adrián Muñoz, Samuel Hinojosa y Stephanie Lozada.

Duración sugerida: 8–10 minutos más la demostración SQL. Este guion complementa EXPLICACION_ER.md y no lo reemplaza.

## Diapositiva 1. PoliTinder

Presentamos PoliTinder, una organización ficticia orientada a facilitar encuentros entre estudiantes de la Escuela Politécnica Nacional. El grupo G está formado por Ismael Heredia, Adrián Muñoz, Samuel Hinojosa y Stephanie Lozada. El foco de la exposición es cómo la base organiza perfiles e interacciones; no es un servicio oficial de la EPN.

## Diapositiva 2. Caso de estudio

Primero explicamos el proceso: una persona crea su perfil, expresa interés por otra y, solo si el interés es mutuo, se genera un match. La pareja puede conversar y programar citas. Esta diapositiva justifica las tablas que veremos después: el sistema necesita conservar cada paso y relacionarlo con sus participantes.

## Diapositiva 3. Las diez entidades de la base

Las diez tablas se agrupan en catálogos, perfiles e interacciones. FACULTY, MAJOR y GENDER evitan repetir valores de referencia. USER guarda la cuenta y USERPHOTO permite múltiples fotos. LIKE registra un interés dirigido; POLIMATCH reúne dos likes opuestos. CHAT contiene la conversación, MESSAGE sus mensajes y POLIDATE las citas de la pareja.

## Diapositiva 4. Entidades, atributos y claves

Una entidad representa un concepto, los atributos describen sus propiedades y las relaciones conectan conceptos. En USER, idUser es la clave primaria; idMajor e idGender son foráneas. La clave foránea debe apuntar a una fila existente. El correo es único. La edad se calcula desde bornDate, y las fotos se almacenan en archivos con rutas en USERPHOTO. Este ejemplo no enumera todos los atributos; el diagrama completo está en la diapositiva 6.

## Diapositiva 5. MER V1: primer diseño

Esta imagen es el primer MER creado. Usa entidades, atributos y relaciones para representar la idea inicial. Incluye preferencias y suscripciones. Todavía no representa con entidades separadas los likes, matches, chats, mensajes y fotos. La carrera aparece como atributo major y también como entidad, por lo que el diseño final explicita la relación por clave foránea. Es una versión de análisis inicial, no el esquema que ejecuta la app.

## Diapositiva 6. MER mejorado: problemas solventados

Este es el diagrama mejorado utilizado como referencia del DDL. Se distinguen LIKE y POLIMATCH, se añaden CHAT y MESSAGE, las fotos tienen su tabla USERPHOTO y POLIDATE pertenece a un match. USER referencia MAJOR, y MAJOR referencia FACULTY. Son mejoras para representar claramente las interacciones. Preferencias y suscripciones quedaron fuera del alcance actual, no son tablas de esta implementación. ER y MER nombran el modelo entidad-relación; esta imagen muestra su representación lógica con tablas y claves.

## Diapositiva 7. Relaciones y reglas del modelo

Explicamos 1:N con facultades y carreras. USER y USER se relacionan N:N mediante LIKE, con roles de emisor y receptor. Un match referencia exactamente dos likes; la reciprocidad es una validación de negocio. El DDL permite cero o un chat por match, pero la app crea match y chat en la misma transacción. Un chat puede tener muchos mensajes. El emisor también referencia USER. Una pareja puede tener múltiples citas.

## Diapositiva 8. De dos likes a un match

Usamos IDs ilustrativos. La fila 11 es un like de A a B y la 12 de B a A. Java comprueba las direcciones opuestas y crea POLIMATCH y CHAT dentro de una transacción. Las claves foráneas prueban que las filas existen, pero no prueban por sí mismas la reciprocidad. Por eso distinguimos integridad de la base y reglas de la aplicación.

## Diapositiva 9. Conexión y validaciones

La aplicación usa Java 25, Swing y FlatLaf, con bibliotecas en lib y sin Maven. JDBC Connector/J conecta al MySQL local usando la configuración de la pantalla de conexión. InnoDB permite transacciones y claves foráneas; utf8mb4 representa Unicode. MySQL valida integridad estructural. Java valida reciprocidad, participantes y conflictos de horario. La consola SQL manual puede saltarse reglas que solo están en Java. No se muestran contraseñas durante la exposición.

## Diapositiva 10. Demostración de consultas

Esta diapositiva es el punto para cambiar a Workbench o a la sección Reportes y mostrar los scripts definitivos. Antes de ejecutar, formulamos la pregunta, identificamos las tablas y explicamos JOIN, WHERE o agregaciones que correspondan. Después interpretamos las filas y columnas. Las queries nuevas no han sido recibidas, por lo que no se atribuyen todavía preguntas ni resultados definitivos. No se inventa un enlace público al archivo local.

## Diapositiva 11. Bibliografía y uso de IA

Referencia: Silberschatz, A., Korth, H. F., y Sudarshan, S. (2019). Database System Concepts (7.ª ed.). McGraw-Hill. Fuente oficial: https://db-book.com/. Se declara el uso de ChatGPT de OpenAI como herramienta de apoyo para la presentación, las imágenes ficticias de usuarios y el DDL. Esta declaración no sustituye la referencia académica del libro.

## Preguntas que conviene saber responder

- **¿Por qué dos likes?** Uno expresa interés unilateral; el otro confirma reciprocidad.
- **¿Dónde están las fotos?** En archivos; USERPHOTO guarda sus rutas y el usuario propietario.
- **¿Una cita pasada se concretó?** El modelo no registra asistencia. Solo podemos afirmar que su fecha pasó.
- **¿Por qué separar tablas?** Cada una representa un concepto y se conecta mediante claves, evitando repetir datos de catálogos.
- **¿Qué cambió entre versiones?** Se hicieron explícitas las relaciones e interacciones; los planes y preferencias quedaron fuera del alcance.
- **¿Cuál es la diferencia entre ER y MER?** MER significa modelo entidad-relación. Aquí comparamos la vista conceptual inicial con el esquema lógico final de tablas.

## Demostración pendiente

Las queries definitivas serán aportadas por el grupo. Cuando lleguen, se actualizará el archivo de reportes y esta sección con sus preguntas y resultados. No presentar el espacio reservado como una consulta implementada.
