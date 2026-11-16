# Auditoría de la actualización

Fecha: 15 de noviembre de 2026.

## Modelo y aplicación

Se comparó el ER aportado con el código y el DDL. Sus once tablas están representadas: FACULTY, MAJOR, GENDER, SEXUALORIENTATION, USER, USERPHOTO, LIKE, POLIMATCH, CHAT, MESSAGE y POLIDATE. La matriz de uso está en ER/EXPLICACION_ER.md. La incorporación es SEXUALORIENTATION y su FK en USER; el registro y la edición del perfil leen y guardan este catálogo. No se deduce orientación a partir de nombres o fotografías.

## Verificación

- Java 25: compilación de producción y pruebas.
- TestSuite --db: 166 comprobaciones correctas, incluyendo instalación, reportes, autenticación, fotos, likes recíprocos, chats, citas y concurrencia.
- SchemaVerification: 27 comprobaciones correctas; catálogo, registro y edición de orientación, integridad referencial, fechas variadas y migración repetida sin pérdida de cuentas, hashes, fotos ni mensajes.
- UiSmokeTest: pantallas reales de Swing renderizadas, tres ciclos de Descubrir y reporte final. Revisión visual del perfil con orientación y tabla del reporte.
- Nueve consultas ejecutadas contra MySQL; original conservado sin modificaciones. Consulta 9 usa COUNT(DISTINCT) para no duplicar matches dentro de una carrera.
- ZIP SQL validado y copias del SQL original comparadas byte por byte.
- Presentación Canva revisada: once diapositivas, tres diagramas separados, bibliografía al final. No se generó ni exportó PDF.

Las pruebas de integración se hicieron en una instancia aislada en el puerto 33307, no en el MySQL habitual del usuario. La base habitual recibirá la migración al pulsar Conectar o Actualizar esquema existente. No se reemplazan sus datos ni sus fechas históricas. La carga nueva contiene 30 usuarios, 70 fotografías y fechas variadas.

## Depuración

Se retiran los scripts de inserción/ampliación sustituidos por el instalador unificado, el generador antiguo de avatares, su actualización automática, las pruebas antiguas sustituidas, el plan inicial y las copias locales desactualizadas del Canva y diagramas. Se conserva una fixture del esquema anterior exclusivamente para probar la migración.

Se conservan fotos de usuarios, fotografías demo, testImages, bibliotecas, licencias y ejercicios. Las fotos no se borran basándose solo en referencias del código: una base existente puede conservar sus rutas. La configuración privada y los binarios compilados permanecen fuera de Git. Las imágenes de verificación no se incorporan a docs ni a la presentación.

## Límites del modelo

Las reglas de reciprocidad de likes, pertenencia del emisor al chat y choque de citas se validan en los servicios Java; una escritura manual en la consola puede eludirlas. Una fecha pasada no acredita que una cita se haya realizado. maxMatch aparece en el MER conceptual, pero no en el ER físico vigente. Los planes y preferencias del MER V1 son históricos, no funciones implementadas.
