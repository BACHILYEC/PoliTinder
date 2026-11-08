package politinder;

import static politinder.Models.*;

import java.time.*;
import java.util.*;

/**
 * Reglas de perfiles, likes y reciprocidad. Nunca se invoca en el hilo de
 * Swing.
 */
public final class PeopleService {
  public final Db db;

  public PeopleService(Db db) {
    this.db = db;
  }

  private static final String PROFILE = "SELECT u.*, m.name AS majorName, f.name AS faculty, (SELECT p.pathPhoto FROM USERPHOTO p"
      + " WHERE p.idUser=u.idUser ORDER BY p.isProfilePhoto DESC,p.idUserPhoto LIMIT 1) AS"
      + " photo FROM `USER` u JOIN MAJOR m ON m.idMajor=u.idMajor JOIN FACULTY f ON"
      + " f.idFaculty=m.idFaculty ";

  private static User user(Map<String, Object> r) {
    return new User(
        ((Number) r.get("idUser")).intValue(),
        (String) r.get("name"),
        (String) r.get("mail"),
        (String) r.get("biography"),
        ((java.sql.Date) r.get("bornDate")).toLocalDate(),
        ((Number) r.get("idGender")).intValue(),
        ((Number) r.get("idMajor")).intValue(),
        (String) r.get("majorName"),
        (String) r.get("faculty"),
        (String) r.get("status"),
        (String) r.get("photo"));
  }

  public User get(int id) throws Exception {
    try (var c = db.open()) {
      var r = Db.rows(c, PROFILE + "WHERE u.idUser=?", id);
      if (r.isEmpty())
        throw new IllegalArgumentException("El usuario ya no existe.");
      return user(r.getFirst());
    }
  }

  public User login(String mail, char[] password) throws Exception {
    try {
      try (var c = db.open()) {
        var r = Db.rows(c, PROFILE + "WHERE u.mail=?", mail.trim().toLowerCase(Locale.ROOT));
        if (r.isEmpty() || !Passwords.verify(password, (String) r.getFirst().get("password")))
          throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        return user(r.getFirst());
      }
    } finally {
      Arrays.fill(password, '\0');
    }
  }

  public List<Choice> choices(String kind, int parent) throws Exception {
    String sql = switch (kind) {
      case "faculty" -> "SELECT idFaculty AS id,name FROM FACULTY ORDER BY name";
      case "major" -> "SELECT idMajor AS id,name FROM MAJOR WHERE idFaculty=? ORDER BY name";
      case "allMajor" -> "SELECT idMajor AS id,name FROM MAJOR ORDER BY name";
      case "gender" -> "SELECT idGender AS id,genderType AS name FROM GENDER ORDER BY idGender";
      default -> throw new IllegalArgumentException("Catálogo desconocido");
    };
    try (var c = db.open()) {
      return Db.rows(c, sql, kind.equals("major") ? new Object[] { parent } : new Object[] {})
          .stream()
          .map(r -> new Choice(((Number) r.get("id")).intValue(), (String) r.get("name")))
          .toList();
    }
  }

  public static void validate(String name, String mail, LocalDate birth, String bio) {
    if (name.trim().length() < 2 || name.trim().length() > 100)
      throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres.");
    if (mail.length() > 254 || !mail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Escribe un correo válido.");
    if (birth == null
        || birth.isAfter(LocalDate.now(ZONE).minusYears(18))
        || birth.isBefore(LocalDate.now(ZONE).minusYears(120)))
      throw new IllegalArgumentException("Debes tener entre 18 y 120 años.");
    if (bio.length() > 1000)
      throw new IllegalArgumentException("La biografía admite hasta 1000 caracteres.");
  }

  public User register(
      String name, String mail, char[] password, LocalDate birth, int gender, int major, String bio)
      throws Exception {
    validate(name, mail, birth, bio);
    String hash;
    try {
      if (password.length < 8)
        throw new IllegalArgumentException("Usa una contraseña de al menos 8 caracteres.");
      hash = Passwords.hash(password);
    } finally {
      Arrays.fill(password, '\0');
    }
    int id = db.tx(
        c -> Db.insert(
            c,
            "INSERT INTO `USER` (name,mail,password,bornDate,idGender,idMajor,biography)"
                + " VALUES (?,?,?,?,?,?,?)",
            name.trim(),
            mail.trim().toLowerCase(Locale.ROOT),
            hash,
            java.sql.Date.valueOf(birth),
            gender,
            major,
            bio.trim()));
    return get(id);
  }

  public User save(
      int id,
      String name,
      String mail,
      LocalDate birth,
      int gender,
      int major,
      String bio,
      boolean active)
      throws Exception {
    validate(name, mail, birth, bio);
    db.tx(
        c -> {
          Db.update(
              c,
              "UPDATE `USER` SET name=?,mail=?,bornDate=?,idGender=?,idMajor=?,biography=?,status=?"
                  + " WHERE idUser=?",
              name.trim(),
              mail.trim().toLowerCase(Locale.ROOT),
              java.sql.Date.valueOf(birth),
              gender,
              major,
              bio.trim(),
              active ? "ACTIVE" : "PAUSED",
              id);
          return null;
        });
    return get(id);
  }

  public List<User> discover(int me, int min, int max, int gender, Set<Integer> skipped)
      throws Exception {
    if (min < 18 || max < min || max > 120)
      throw new IllegalArgumentException("Revisa el intervalo de edad.");
    try (var c = db.open()) {
      var list = Db.rows(
          c,
          PROFILE
              + "WHERE u.idUser<>? AND u.status='ACTIVE' AND"
              + " TIMESTAMPDIFF(YEAR,u.bornDate,CURDATE()) BETWEEN ? AND ? AND (?=0 OR"
              + " u.idGender=?) AND EXISTS(SELECT 1 FROM USERPHOTO p WHERE p.idUser=u.idUser)"
              + " AND NOT EXISTS(SELECT 1 FROM `LIKE` l WHERE l.idUser1=? AND"
              + " l.idUser2=u.idUser) ORDER BY u.idUser",
          me,
          min,
          max,
          gender,
          gender,
          me);
      return list.stream().map(PeopleService::user).filter(u -> !skipped.contains(u.id())).toList();
    }
  }

  public LikeResult like(int me, int other) throws Exception {
    if (me == other)
      throw new IllegalArgumentException("No puedes darte like a ti mismo.");
    return db.tx(
        c -> {
          for (int id : new int[] { Math.min(me, other), Math.max(me, other) }) {
            var rows = Db.rows(c, "SELECT status FROM `USER` WHERE idUser=? FOR UPDATE", id);
            if (rows.isEmpty() || !rows.getFirst().get("status").equals("ACTIVE"))
              throw new IllegalArgumentException("Ambos perfiles deben estar activos.");
          }
          if (Db.number(
              c,
              "SELECT COUNT(DISTINCT idUser) FROM USERPHOTO WHERE idUser IN (?,?)",
              me,
              other) != 2)
            throw new IllegalArgumentException("Ambos perfiles necesitan una fotografía.");
          int forward = (int) Db.number(
              c, "SELECT idLike FROM `LIKE` WHERE idUser1=? AND idUser2=?", me, other);
          boolean duplicate = forward != 0;
          if (forward == 0)
            forward = Db.insert(c, "INSERT INTO `LIKE` (idUser1,idUser2) VALUES (?,?)", me, other);
          int reverse = (int) Db.number(
              c, "SELECT idLike FROM `LIKE` WHERE idUser1=? AND idUser2=?", other, me);
          if (reverse == 0)
            return new LikeResult(false, duplicate);
          int low = Math.min(forward, reverse), high = Math.max(forward, reverse);
          int match = (int) Db.number(
              c,
              "SELECT idPoliMatch FROM POLIMATCH WHERE idLike1=? AND idLike2=?",
              low,
              high);
          if (match == 0)
            match = Db.insert(c, "INSERT INTO POLIMATCH (idLike1,idLike2) VALUES (?,?)", low, high);
          if (Db.number(c, "SELECT COUNT(*) FROM CHAT WHERE idPoliMatch=?", match) == 0)
            Db.insert(c, "INSERT INTO CHAT (idPoliMatch) VALUES (?)", match);
          return new LikeResult(true, duplicate);
        });
  }

  public List<Match> matches(int me) throws Exception {
    try (var c = db.open()) {
      var rows = Db.rows(
          c,
          "SELECT pm.idPoliMatch,ch.idChat,u.idUser,u.name,m.name AS career,(SELECT pathPhoto"
              + " FROM USERPHOTO p WHERE p.idUser=u.idUser ORDER BY isProfilePhoto"
              + " DESC,idUserPhoto LIMIT 1) AS photo FROM POLIMATCH pm JOIN `LIKE` l ON"
              + " l.idLike=pm.idLike1 JOIN `LIKE` r ON r.idLike=pm.idLike2 JOIN CHAT ch ON"
              + " ch.idPoliMatch=pm.idPoliMatch JOIN `USER` u ON u.idUser=CASE WHEN l.idUser1=?"
              + " THEN l.idUser2 ELSE l.idUser1 END JOIN MAJOR m ON m.idMajor=u.idMajor WHERE ?"
              + " IN(l.idUser1,l.idUser2) AND l.idUser1=r.idUser2 AND l.idUser2=r.idUser1 ORDER"
              + " BY pm.idPoliMatch DESC",
          me,
          me);
      return rows.stream()
          .map(
              r -> new Match(
                  ((Number) r.get("idPoliMatch")).intValue(),
                  ((Number) r.get("idChat")).intValue(),
                  ((Number) r.get("idUser")).intValue(),
                  (String) r.get("name"),
                  (String) r.get("career"),
                  (String) r.get("photo")))
          .toList();
    }
  }
}
