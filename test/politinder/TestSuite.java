package politinder;

import static politinder.Models.*;

import java.awt.Rectangle;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/** Pruebas sin Maven ni framework. La integración SOLO usa la instancia aislada 33307. */
public class TestSuite {
  static int passed;

  static void check(boolean value, String label) {
    if (!value) throw new AssertionError(label);
    passed++;
    System.out.println("OK · " + label);
  }

  interface Throwing {
    void run() throws Exception;
  }

  static void rejects(Throwing work, String label) throws Exception {
    try {
      work.run();
    } catch (Exception expected) {
      check(true, label);
      return;
    }
    throw new AssertionError("Se aceptó: " + label);
  }

  public static void main(String[] args) throws Exception {
    var statements =
        SqlScripts.split(
            "-- comentario\nSELECT 'a;b'; SELECT \"x;y\"; /* ; */ SELECT `a;b`; SELECT 'a''b';");
    check(statements.size() == 4, "Separación SQL respeta comillas y comentarios");
    rejects(() -> SqlScripts.split("SELECT 'sin cerrar"), "SQL incompleto detectado");
    String hash = Passwords.hash("Contraseña😊25".toCharArray());
    check(Passwords.verify("Contraseña😊25".toCharArray(), hash), "Hash PBKDF2 verifica Unicode");
    check(!Passwords.verify("incorrecta".toCharArray(), hash), "Contraseña incorrecta rechazada");
    rejects(
        () ->
            PeopleService.validate(
                "Ana", "ana@example.com", LocalDate.now(ZONE).minusYears(17), ""),
        "Registro de menor rechazado");
    for (Rectangle r :
        new Rectangle[] {
          new Rectangle(0, 0, 1366, 728),
          new Rectangle(0, 0, 1920, 1040),
          new Rectangle(1920, 0, 2560, 1400),
          new Rectangle(0, 0, 800, 560)
        }) {
      Rectangle w = Ui.bounds(r);
      check(
          r.contains(w)
              && w.height > w.width
              && Math.abs(w.getCenterX() - r.getCenterX()) <= 1
              && Math.abs(w.getCenterY() - r.getCenterY()) <= 1,
          "Ventana vertical centrada " + r.width + "×" + r.height);
    }
    check(Reports.load().size() == 9, "Nueve reportes cargados desde el TXT");
    if (Arrays.asList(args).contains("--db")) integration();
    System.out.println("TOTAL: " + passed + " verificaciones correctas.");
  }

  static void integration() throws Exception {
    var config = new AppConfig();
    config.host = "127.0.0.1";
    config.port = 33307;
    config.user = "root";
    config.password = "";
    var db = new Db(config);
    System.out.println(Installer.check(db));
    try (var c = db.open(false)) {
      if (Db.number(
              c, "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name='politinder'")
          != 0)
        throw new IllegalStateException(
            "La instancia de pruebas debe estar vacía. No se borra ninguna base automáticamente.");
    }
    Installer.initialize(db);
    var people = new PeopleService(db);
    var photos = new PhotoService(db);
    var chat = new ChatService(db);
    try (var c = db.open()) {
      check(
          Db.number(
                  c,
                  "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='politinder'")
              == 10,
          "DDL crea exactamente diez tablas");
      check(Db.number(c, "SELECT COUNT(*) FROM `USER`") == 20, "20 usuarios demo");
      check(Db.number(c, "SELECT COUNT(*) FROM USERPHOTO") == 35, "35 fotos realistas demo");
      check(Db.number(c, "SELECT COUNT(*) FROM `LIKE`") == 16, "16 likes demo");
      check(Db.number(c, "SELECT COUNT(*) FROM MESSAGE") == 24, "24 mensajes demo");
      rejects(
          () -> Db.insert(c, "INSERT INTO `LIKE` (idUser1,idUser2) VALUES (1,1)"),
          "CHECK impide autolike");
      rejects(
          () -> Db.insert(c, "INSERT INTO `LIKE` (idUser1,idUser2) VALUES (1,2)"),
          "UNIQUE impide duplicar like");
      rejects(() -> Db.insert(c, "INSERT INTO CHAT (idPoliMatch) VALUES (1)"), "Un chat por match");
      rejects(
          () ->
              Db.insert(
                  c,
                  "INSERT INTO POLIDATE (place,dateOfPoliDate,idPoliMatch) VALUES"
                      + " ('X',NOW(),999999)"),
          "FK impide cita huérfana");
    }
    var params = new HashMap<String, Object>();
    params.put("edad_min", 18);
    params.put("edad_max", 25);
    params.put("carrera", 1);
    params.put("facultad", 1);
    params.put("corte", Timestamp.valueOf(LocalDateTime.now(ZONE)));
    for (var r : Reports.load()) {
      var t = Reports.run(db, r, params);
      check(!t.rows().isEmpty(), "Reporte " + r.id() + " ejecuta y devuelve datos");
      if (r.id() == 7) check(t.rows().size() == 2, "Reporte 7 conserva empate de mayor edad");
      if (r.id() == 8)
        check(
            t.rows().stream().anyMatch(x -> ((Number) x.get(2)).longValue() == 0),
            "Facultades sin matches aparecen con cero");
      if (r.id() == 9) {
        var row = t.rows().getFirst();
        check(
            ((Number) row.get(0)).intValue() == 6
                && ((Number) row.get(1)).intValue() == 5
                && ((Number) row.get(2)).intValue() == 3
                && ((Number) row.get(3)).doubleValue() == 50,
            "Reporte 9: 6 / 5 / 3 / 50%");
      }
    }
    check(
        people.login("ana@politinder.test", "PoliDemo25!".toCharArray()).id() == 1,
        "Login demo y hash DML compatibles");
    rejects(
        () -> people.login("ana@politinder.test", "incorrecta".toCharArray()),
        "Login inválido rechazado");
    rejects(
        () ->
            people.register(
                "Duplicado",
                "ana@politinder.test",
                "12345678".toCharArray(),
                LocalDate.of(2000, 1, 1),
                1,
                1,
                ""),
        "Correo duplicado rechazado");
    rejects(() -> chat.messages(5, 1), "Usuario ajeno no lee chat");
    rejects(() -> chat.send(5, 1, "Intruso"), "Usuario ajeno no escribe chat");
    rejects(
        () -> chat.schedule(1, 1, "Campus", LocalDateTime.now(ZONE).minusDays(1)),
        "Cita pasada rechazada");
    User a =
        people.register(
            "Prueba A",
            "qa.a@example.test",
            "12345678".toCharArray(),
            LocalDate.of(2000, 1, 1),
            1,
            1,
            "Biografía A");
    User b =
        people.register(
            "Prueba B",
            "qa.b@example.test",
            "12345678".toCharArray(),
            LocalDate.of(2001, 1, 1),
            2,
            2,
            "Biografía B");
    Path picture = AppConfig.ROOT.resolve("photos/demo-real/01/01.png");
    photos.add(a.id(), picture);
    photos.add(b.id(), picture);
    check(!people.like(a.id(), b.id()).match(), "Like unilateral no crea match");
    check(people.matches(a.id()).isEmpty(), "Like unilateral no crea chat");
    try (var workers = Executors.newVirtualThreadPerTaskExecutor()) {
      var x = workers.submit(() -> people.like(a.id(), b.id()));
      var y = workers.submit(() -> people.like(b.id(), a.id()));
      x.get();
      y.get();
    }
    check(
        people.matches(a.id()).size() == 1 && people.matches(b.id()).size() == 1,
        "Likes concurrentes generan un único match y chat");
    check(people.like(a.id(), b.id()).duplicate(), "Like repetido es idempotente");
    Match match = people.matches(a.id()).getFirst();
    chat.send(a.id(), match.chat(), "¡Hola! 😊");
    chat.send(b.id(), match.chat(), "¡Un café! ☕");
    check(
        chat.messages(a.id(), match.chat()).size() == 2,
        "Chat conserva texto con emojis entre dos sesiones");
    chat.schedule(a.id(), match.id(), "Cafetería EPN", LocalDateTime.now(ZONE).plusDays(1));
    check(chat.dates(b.id()).rows().size() == 1, "Cita visible para ambos participantes");
    photos.add(a.id(), picture);
    var images = photos.list(a.id());
    try (var workers = Executors.newVirtualThreadPerTaskExecutor()) {
      var x =
          workers.submit(
              () -> {
                photos.main(a.id(), images.get(0).id());
                return null;
              });
      var y =
          workers.submit(
              () -> {
                photos.main(a.id(), images.get(1).id());
                return null;
              });
      x.get();
      y.get();
    }
    check(
        photos.list(a.id()).stream().filter(Photo::main).count() == 1,
        "Una única portada ante cambios concurrentes");
    people.save(
        a.id(), a.name(), a.mail(), a.bornDate(), a.gender(), a.major(), a.biography(), false);
    check(
        people.discover(12, 18, 99, 0, Set.of()).stream().noneMatch(u -> u.id() == a.id()),
        "Perfil pausado excluido de Descubrir");
    try (var c = db.open();
        var s = c.createStatement()) {
      check(s.execute("SELECT NULL AS N, 'á😊' AS Texto"), "Consola: SELECT dinámico");
      try (var rs = s.getResultSet()) {
        var result = Tables.read(rs, 0);
        check(
            result.columns().size() == 2 && result.rows().getFirst().getFirst() == null,
            "Tabla conserva NULL y columnas dinámicas");
      }
      check(
          !s.execute("CREATE TEMPORARY TABLE qa_console (id INT)"), "Consola: DDL devuelve estado");
      s.execute("INSERT INTO qa_console VALUES (1),(2)");
      check(s.getUpdateCount() == 2, "Consola: cantidad de filas afectadas");
      s.execute("UPDATE qa_console SET id=3 WHERE id=2");
      check(s.getUpdateCount() == 1, "Consola: UPDATE");
      s.execute("DELETE FROM qa_console WHERE id=3");
      check(s.getUpdateCount() == 1, "Consola: DELETE");
      s.execute("START TRANSACTION");
      s.execute("INSERT INTO FACULTY (name) VALUES ('QA rollback')");
      s.execute("ROLLBACK");
      check(
          Db.number(c, "SELECT COUNT(*) FROM FACULTY WHERE name='QA rollback'") == 0,
          "Consola: transacción persistente y rollback");
      rejects(
          () -> s.execute("SELECT columna_inexistente FROM `USER`"),
          "Consola: error SQL recuperable");
      check(s.execute("SELECT 1"), "Consola sigue operativa tras error");
    }
    try (var c = db.open();
        var s = c.createStatement();
        var pool = Executors.newVirtualThreadPerTaskExecutor()) {
      var running =
          pool.submit(
              () -> {
                try {
                  s.execute("SELECT SLEEP(10)");
                  try (var rs = s.getResultSet()) {
                    return rs.next() && rs.getInt(1) == 1;
                  }
                } catch (SQLException expected) {
                  return true;
                }
              });
      Thread.sleep(300);
      s.cancel();
      check(
          running.get(5, TimeUnit.SECONDS), "Consola cancela consulta sin bloquear la aplicación");
    }
    BugRegression.run(db);
    System.out.println(
        "La base de pruebas contiene datos QA añadidos; no se tocó el servidor principal.");
  }
}
