package politinder;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/** Actualiza únicamente los valores originales del catálogo ficticio de demostración. */
public final class DemoProfiles {
  public record Profile(int id, String mail, String oldName, String name, String oldBio,
      String bio, int oldGender, int gender, int photoCount) {
    public String photo(int number) {
      return "photos/demo-real/%02d/%02d.png".formatted(id, number);
    }
    boolean legacyPhoto(String path) {
      return (id <= 18 && path.equals("photos/demo/avatar-%02d.jpg".formatted(id)))
          || (id <= 6 && path.equals("photos/demo/avatar-%02d.jpg".formatted(id + 18)));
    }
  }

  public static final List<Profile> PROFILES = List.of(
      new Profile(1, "ana@politinder.test", "Ana Torres", "Ana Lucía Cárdenas", "Café, fotografía y paseos por Quito. ¿Tu lugar favorito del campus?", "Me gusta guardar los días en fotos y descubrir cafeterías pequeñas de Quito. Si tu plan incluye caminar sin prisa y una buena conversación, me apunto.", 1, 1, 4),
      new Profile(2, "luis@politinder.test", "Luis Andrade", "Luis Andrés Benavides", "Programo, juego básquet y siempre tengo una playlist nueva.", "Entre clases, una partida de básquet y proyectos que casi siempre terminan con café. Recomiéndame una canción y te comparto mi playlist para programar.", 2, 2, 1),
      new Profile(3, "sofia@politinder.test", "Sofía Molina", "Sofía Isabel Montalvo", "Me gustan los libros, los museos y conversar sin mirar el reloj.", "Leo de todo un poco y siempre llevo una libreta para dibujar. Mi plan ideal: museo, helado y una conversación que haga olvidar la hora.", 1, 1, 1),
      new Profile(4, "mateo@politinder.test", "Mateo Ruiz", "Mateo Alejandro Jaramillo", "Buscando compañía para un café después de clases.", "Los fines de semana cambio los apuntes por una caminata. Estoy aprendiendo a cocinar; mi especialidad, por ahora, es invitar a probar el resultado.", 2, 2, 4),
      new Profile(5, "valentina@politinder.test", "Valentina Paz", "Valentina Paz Cevallos", "Música en vivo, montañas y proyectos que valgan la pena.", "Fan de la música en vivo y de los atardeceres desde Guápulo. Busco alguien con curiosidad, sentido del humor y ganas de conocer rincones nuevos.", 1, 1, 4),
      new Profile(6, "diego@politinder.test", "Diego Luna", "Diego Sebastián Aguirre", "Café, fotografía y paseos por Quito. ¿Tu lugar favorito del campus?", "Me gustan las bicicletas, los documentales y arreglar cosas que todavía tienen solución. Un paseo por el parque y un café suenan como un buen comienzo.", 2, 2, 1),
      new Profile(7, "camila@politinder.test", "Camila Ríos", "Camila Fernanda Caicedo", "Programo, juego básquet y siempre tengo una playlist nueva.", "Me río fácil, cocino con música y nunca digo que no a un paseo improvisado. Si conoces un buen lugar para desayunar después de clases, tenemos plan.", 1, 1, 4),
      new Profile(8, "nicolas@politinder.test", "Nicolás Vega", "Nicolás Esteban Chiriboga", "Me gustan los libros, los museos y conversar sin mirar el reloj.", "Disfruto las películas que dejan tema de conversación y las escapadas a la montaña. También soy feliz con un juego de mesa y buena compañía.", 2, 2, 1),
      new Profile(9, "isabella@politinder.test", "Isabella León", "Isabella María Naranjo", "Buscando compañía para un café después de clases.", "Me encanta bailar, probar recetas y encontrar pequeñas librerías. Estoy aquí para conocer a alguien con quien los planes sencillos se vuelvan memorables.", 1, 1, 1),
      new Profile(10, "daniel@politinder.test", "Daniel Cueva", "Daniel Andrés Endara", "Música en vivo, montañas y proyectos que valgan la pena.", "Entre libros, fotografía urbana y proyectos de la Poli. Prefiero una charla tranquila a los mensajes eternos; podemos empezar por un café.", 2, 2, 4),
      new Profile(11, "lucia@politinder.test", "Lucía Flores", "Lucía Belén Santillán", "Café, fotografía y paseos por Quito. ¿Tu lugar favorito del campus?", "Los domingos son para caminar, sacar fotos y buscar algo rico que comer. Me gusta la gente que escucha y se entusiasma contando lo que le apasiona.", 1, 1, 1),
      new Profile(12, "emilia@politinder.test", "Emilia Castro", "Emilia Josefina Viteri", "Programo, juego básquet y siempre tengo una playlist nueva.", "Colecciono canciones, plantas pequeñas y ganas de viajar. Siempre tengo una recomendación de película y espacio para descubrir una nueva contigo.", 2, 1, 1),
      new Profile(13, "joaquin@politinder.test", "Joaquín Mora", "Joaquín Andrés Cordero", "Me gustan los libros, los museos y conversar sin mirar el reloj.", "Juego fútbol, improviso en la guitarra y disfruto los paseos largos. Me gustan las conversaciones honestas y quienes no se toman demasiado en serio.", 1, 2, 1),
      new Profile(14, "martina@politinder.test", "Martina Pérez", "Martina Isabel Salazar", "Buscando compañía para un café después de clases.", "Me pierdo feliz entre libros y exposiciones. Aprendo a hacer cerámica y busco compañía para descubrir sitios bonitos cerca de la universidad.", 2, 1, 1),
      new Profile(15, "gabriel@politinder.test", "Gabriel Silva", "Gabriel Alejandro Villamar", "Música en vivo, montañas y proyectos que valgan la pena.", "Me recarga nadar, escuchar jazz y pasar tiempo al aire libre. Me interesa conocer a alguien con quien compartir metas, risas y una buena sobremesa.", 1, 2, 1),
      new Profile(16, "renata@politinder.test", "Renata Ortiz", "Renata Victoria Palacios", "Café, fotografía y paseos por Quito. ¿Tu lugar favorito del campus?", "Me gusta bailar salsa, cocinar para mis amigos y ver la ciudad desde otro ángulo. Hagamos un plan sencillo y dejemos que la conversación lo mejore.", 2, 1, 1),
      new Profile(17, "andres@politinder.test", "Andrés Salas", "Andrés Felipe Alvarado", "Programo, juego básquet y siempre tengo una playlist nueva.", "Curioso por naturaleza: tecnología, senderos y recetas nuevas. Valoro la calma, el humor y la gente que cumple lo que promete.", 1, 2, 1),
      new Profile(18, "elena@politinder.test", "Elena Vidal", "Elena Cristina Echeverría", "Me gustan los libros, los museos y conversar sin mirar el reloj.", "Disfruto la fotografía, el cine latinoamericano y las caminatas de domingo. Un encuentro con buena conversación vale más que un plan perfecto.", 2, 1, 1),
      new Profile(19, "paula@politinder.test", "Paula Díaz", "Paula Daniela Ponce", "Buscando compañía para un café después de clases.", "Estoy aprendiendo guitarra y buscando mi cafetería favorita de Quito. Me gustan los perros, las películas de misterio y los planes espontáneos.", 1, 1, 1),
      new Profile(20, "samuel@politinder.test", "Samuel Lara", "Samuel David Zambrano", "Música en vivo, montañas y proyectos que valgan la pena.", "Fan del básquet, los videojuegos cooperativos y los viajes cortos. Si te ríes de los chistes malos, probablemente nos llevemos muy bien.", 2, 2, 1)
  );

  private DemoProfiles() {}

  public static void refresh(Db db) throws Exception {
    // Cada perfil se actualiza en una transacción breve. No se recrean cuentas eliminadas.
    for (Profile profile : PROFILES) {
      for (int n = 1; n <= profile.photoCount(); n++)
        if (!Files.isRegularFile(AppConfig.resolve(profile.photo(n))))
          throw new IllegalStateException("Falta una foto de demostración: " + profile.photo(n));
      db.tx(c -> {
        var users = Db.rows(c, "SELECT idUser,name,biography,idGender FROM `USER`"
            + " WHERE mail=? FOR UPDATE", profile.mail());
        if (users.isEmpty()) return null;
        var user = users.getFirst();
        int id = ((Number) user.get("idUser")).intValue();
        boolean originalName = profile.oldName().equals(user.get("name"));
        String name = originalName ? profile.name() : (String) user.get("name");
        String bio = profile.oldBio().equals(user.get("biography"))
            ? profile.bio() : (String) user.get("biography");
        int originalGender = ((Number) user.get("idGender")).intValue();
        int gender = originalName && originalGender == profile.oldGender()
            ? profile.gender() : originalGender;
        if (!Objects.equals(name, user.get("name"))
            || !Objects.equals(bio, user.get("biography")) || gender != originalGender)
          Db.update(c, "UPDATE `USER` SET name=?,biography=?,idGender=? WHERE idUser=?",
              name, bio, gender, id);

        var photos = Db.rows(c, "SELECT * FROM USERPHOTO WHERE idUser=? ORDER BY idUserPhoto", id);
        boolean legacy = photos.stream().anyMatch(p -> profile.legacyPhoto((String) p.get("pathPhoto")));
        // Una galería editada o eliminada por el usuario no vuelve a poblarse al reconectar.
        if (!legacy && !(photos.isEmpty() && originalName)) return null;
        for (var photo : photos) {
          String path = (String) photo.get("pathPhoto");
          if (!profile.legacyPhoto(path)) continue;
          boolean extra = path.equals("photos/demo/avatar-%02d.jpg".formatted(profile.id() + 18));
          int number = extra && profile.photoCount() > 1 ? 2 : 1;
          String replacement = profile.photo(number);
          if (Db.number(c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND pathPhoto=?",
              id, replacement) == 0)
            Db.update(c, "UPDATE USERPHOTO SET pathPhoto=? WHERE idUserPhoto=?",
                replacement, photo.get("idUserPhoto"));
          else
            Db.update(c, "DELETE FROM USERPHOTO WHERE idUserPhoto=?", photo.get("idUserPhoto"));
        }
        for (int n = 1; n <= profile.photoCount(); n++) {
          if (Db.number(c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND pathPhoto=?",
              id, profile.photo(n)) != 0) continue;
          if (Db.number(c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=?", id) >= 6) break;
          boolean main = Db.number(c,
              "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND isProfilePhoto=TRUE", id) == 0;
          Db.insert(c, "INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser) VALUES (?,?,?)",
              profile.photo(n), main, id);
        }
        if (Db.number(c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND isProfilePhoto=TRUE", id) == 0)
          Db.update(c, "UPDATE USERPHOTO SET isProfilePhoto=TRUE WHERE idUser=?"
              + " ORDER BY idUserPhoto LIMIT 1", id);
        return null;
      });
    }
  }
}
