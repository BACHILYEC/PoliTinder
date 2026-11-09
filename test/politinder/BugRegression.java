package politinder;

import static politinder.Models.*;
import static politinder.TestSuite.*;

import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

public final class BugRegression {
  public static void main(String[] args) throws Exception {
    var config = new AppConfig();
    config.host = "127.0.0.1";
    config.port = 33307;
    config.user = "root";
    config.password = "";
    run(new Db(config));
    System.out.println("TOTAL regresión: " + TestSuite.passed + " verificaciones correctas.");
  }

  public static void run(Db db) throws Exception {
    var people = new PeopleService(db);
    var clock = Clock.fixed(Instant.parse("2035-01-10T12:00:00Z"), ZONE);
    var chat = new ChatService(db, clock);
    User[] users = new User[5];
    for (int i = 0; i < users.length; i++) {
      users[i] = people.register("QA Agenda " + i, UUID.randomUUID() + "@example.test",
          "Pruebas25!".toCharArray(), LocalDate.now(ZONE).minusYears(67), 4, 1, "Prueba aislada");
      new PhotoService(db).add(users[i].id(), AppConfig.resolve("photos/demo-real/01/01.png"));
    }
    int a = users[0].id(), b = users[1].id(), c = users[2].id(), d = users[3].id(), e = users[4].id();
    Set<Integer> expected = Set.of(b, c, d, e);
    for (int round = 0; round < 3; round++) {
      Set<Integer> visited = new HashSet<>();
      for (int n = 0; n < expected.size(); n++) {
        var candidates = people.discover(a, 67, 67, 4, visited);
        int next = candidates.getFirst().id();
        check(visited.add(next), "Descubrir: sin repeticiones en vuelta " + (round + 1));
      }
      check(visited.equals(expected), "Descubrir: todos los candidatos aparecen en la vuelta");
      check(ids(people.discover(a, 67, 67, 4, visited)).equals(expected),
          "Descubrir: vuelve a empezar automáticamente al agotar la lista");
    }
    Set<List<Integer>> orders = new HashSet<>();
    for (int i = 0; i < 10; i++)
      orders.add(people.discover(a, 67, 67, 4, Set.of()).stream().map(User::id).toList());
    check(orders.size() > 1, "Descubrir: el orden cambia entre consultas");
    people.like(a, b);
    check(ids(people.discover(a, 67, 67, 4, Set.of())).contains(b),
        "Descubrir: el like unilateral sigue siendo candidato");
    people.like(b, a);
    check(!ids(people.discover(a, 67, 67, 4, Set.of())).contains(b)
            && !ids(people.discover(b, 67, 67, 4, Set.of())).contains(a),
        "Descubrir: el match desaparece para ambos participantes");
    check(!ids(people.discover(a, 67, 67, 4, expected)).contains(b),
        "Descubrir: reiniciar el ciclo no reintroduce matches");
    check(people.discover(a, 110, 120, 4, Set.of()).isEmpty(),
        "Descubrir: filtros sin candidatos devuelven vacío, sin bucle bloqueante");
    people.save(e, users[4].name(), users[4].mail(), users[4].bornDate(), 4, 1, "", false);
    check(!ids(people.discover(a, 67, 67, 4, Set.of())).contains(e),
        "Descubrir: respeta perfiles pausados");
    try (var connection = db.open()) {
      Db.update(connection, "DELETE FROM USERPHOTO WHERE idUser=?", d);
    }
    check(ids(people.discover(a, 67, 67, 4, Set.of())).equals(Set.of(c)),
        "Descubrir: un único candidato sigue disponible, excluye perfiles sin foto");
    check(ids(people.discover(a, 67, 67, 4, Set.of(c))).equals(Set.of(c)),
        "Descubrir: un candidato puede reiniciar el ciclo");
    new PhotoService(db).add(d, AppConfig.resolve("photos/demo-real/04/01.png"));
    people.like(a, c); people.like(c, a);
    people.like(c, d); people.like(d, c);
    int ab = match(db, a, b), ac = match(db, a, c), cd = match(db, c, d);
    LocalDateTime slot = LocalDateTime.of(2035, 1, 12, 16, 30);
    chat.schedule(a, ab, "Cafetería", slot);
    rejects(() -> chat.schedule(b, ab, "Biblioteca", slot), "Cita duplicada en el mismo match rechazada");
    rejects(() -> chat.schedule(c, ac, "Parque", slot), "Conflicto de la otra persona en otro match");
    rejects(() -> chat.schedule(a, ac, "Parque", slot.plusSeconds(45)), "Segundos distintos no evaden el mismo minuto");
    chat.schedule(c, cd, "Otro lugar", slot);
    check(true, "Parejas independientes pueden reservar el mismo horario");
    chat.schedule(a, ac, "Otro día", slot.plusDays(1));
    check(true, "La misma hora en otro día está permitida");
    rejects(() -> chat.schedule(d, ab, "Intruso", slot.plusDays(2)), "Usuario ajeno al match no agenda");

    LocalDateTime concurrent = slot.plusDays(3);
    try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
      var gate = new CountDownLatch(1);
      var first = pool.submit(() -> attempt(chat, a, ab, concurrent, gate));
      var second = pool.submit(() -> attempt(chat, c, ac, concurrent, gate));
      gate.countDown();
      check(first.get(10, TimeUnit.SECONDS) + second.get(10, TimeUnit.SECONDS) == 1,
          "Reservas simultáneas con un participante común: solo una se confirma");
    }
    try (var connection = db.open()) {
      check(Db.number(connection, "SELECT COUNT(*) FROM POLIDATE WHERE dateOfPoliDate=?",
          Timestamp.valueOf(concurrent)) == 1, "Una única reserva concurrente quedó guardada");
    }

    // Simular un catálogo anterior y una personalización antes de ejecutar la actualización.
    var firstProfile = DemoProfiles.PROFILES.getFirst();
    try (var connection = db.open()) {
      Db.update(connection, "UPDATE \u0060USER\u0060 SET name=?,biography=?,idGender=? WHERE mail=?",
          firstProfile.oldName(), firstProfile.oldBio(), firstProfile.oldGender(), firstProfile.mail());
      Db.update(connection, "DELETE FROM USERPHOTO WHERE idUser=1");
      Db.insert(connection, "INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)"
          + " VALUES ('photos/demo/avatar-01.jpg',TRUE,1)");
      Db.update(connection, "UPDATE \u0060USER\u0060 SET name='Mi nombre personalizado',"
          + "biography='Mi biografía personal' WHERE idUser=2");
      Db.update(connection, "DELETE FROM USERPHOTO WHERE idUser=2");
      Db.insert(connection, "INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser)"
          + " VALUES ('testImages/persona_02_Mateo/01_perfil.png',TRUE,2)");
    }
    DemoProfiles.refresh(db);
    var photoService = new PhotoService(db);
    check(people.get(1).name().equals(firstProfile.name())
            && people.get(1).biography().equals(firstProfile.bio()),
        "Catálogo existente: nombres y biografías originales actualizados");
    check(photoService.list(1).size() == 4
            && photoService.list(1).stream().noneMatch(p -> p.path().contains("avatar")),
        "Catálogo existente: avatar sustituido por cuatro fotos coherentes");
    check(people.get(2).name().equals("Mi nombre personalizado")
            && people.get(2).biography().equals("Mi biografía personal")
            && photoService.list(2).getFirst().path().startsWith("testImages/"),
        "Actualización respeta nombre, biografía y fotos personalizados");
    List<Photo> before = photoService.list(1);
    DemoProfiles.refresh(db);
    check(photoService.list(1).equals(before), "Actualizar nuevamente no duplica fotografías");
    try (var connection = db.open()) {
      Db.update(connection, "DELETE FROM USERPHOTO WHERE idUser=1 AND isProfilePhoto=FALSE");
    }
    DemoProfiles.refresh(db);
    check(photoService.list(1).size() == 1, "Una foto eliminada por el usuario no se repone al reconectar");
    try (var connection = db.open()) {
      check(Db.number(connection, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND pathPhoto LIKE ?",
          a, "photos/users/" + a + "/%") == 1, "Catálogo no modifica las cuentas fuera de la demo");
    }
    for (var profile : DemoProfiles.PROFILES) {
      for (int n = 1; n <= profile.photoCount(); n++) {
        var path = AppConfig.resolve(profile.photo(n));
        check(Files.size(path) < 5 * 1024 * 1024 && ImageIO.read(path.toFile()) != null,
            "Foto realista legible: " + profile.id() + "/" + n);
      }
    }
  }

  private static Set<Integer> ids(List<User> list) {
    return new HashSet<>(list.stream().map(User::id).toList());
  }

  private static int match(Db db, int a, int b) throws Exception {
    try (var c = db.open()) {
      return (int) Db.number(c, "SELECT pm.idPoliMatch FROM POLIMATCH pm JOIN \u0060LIKE\u0060 l"
          + " ON l.idLike=pm.idLike1 WHERE (l.idUser1=? AND l.idUser2=?)"
          + " OR (l.idUser1=? AND l.idUser2=?)", a, b, b, a);
    }
  }

  private static int attempt(ChatService chat, int me, int match, LocalDateTime slot,
      CountDownLatch gate) throws Exception {
    gate.await();
    try {
      chat.schedule(me, match, "Reserva concurrente", slot);
      return 1;
    } catch (IllegalArgumentException expected) {
      if (!expected.getMessage().contains("ya tienen una PoliDate")) throw expected;
      return 0;
    }
  }
}

