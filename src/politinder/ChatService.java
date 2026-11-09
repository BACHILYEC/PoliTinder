package politinder;

import static politinder.Models.*;

import java.sql.*;
import java.time.*;
import java.util.*;

public final class ChatService {
  private final Db db;
  private final Clock clock;

  public ChatService(Db db) {
    this(db, Clock.system(ZONE));
  }

  public ChatService(Db db, Clock clock) {
    this.db = db;
    this.clock = clock;
  }

  private void member(Connection c, int me, int match) throws SQLException {
    if (Db.number(
            c,
            "SELECT COUNT(*) FROM POLIMATCH pm JOIN `LIKE` l ON l.idLike=pm.idLike1 JOIN `LIKE` r"
                + " ON r.idLike=pm.idLike2 WHERE pm.idPoliMatch=? AND ? IN(l.idUser1,l.idUser2) AND"
                + " l.idUser1=r.idUser2 AND l.idUser2=r.idUser1",
            match,
            me)
        != 1)
      throw new IllegalArgumentException(
          "No perteneces a este match o sus likes no son recíprocos.");
  }

  private int chatMatch(Connection c, int chat) throws SQLException {
    int id = (int) Db.number(c, "SELECT idPoliMatch FROM CHAT WHERE idChat=?", chat);
    if (id == 0) throw new IllegalArgumentException("El chat no existe.");
    return id;
  }

  public List<Message> messages(int me, int chat) throws Exception {
    try (var c = db.open()) {
      member(c, me, chatMatch(c, chat));
      return Db.rows(
              c,
              "SELECT m.*,u.name FROM MESSAGE m JOIN `USER` u ON u.idUser=m.sender WHERE m.idChat=?"
                  + " ORDER BY m.sendDate,m.idMessage",
              chat)
          .stream()
          .map(
              r ->
                  new Message(
                      ((Number) r.get("idMessage")).intValue(),
                      ((Number) r.get("sender")).intValue(),
                      (String) r.get("name"),
                      (String) r.get("content"),
                      date(r.get("sendDate"))))
          .toList();
    }
  }

  private static LocalDateTime date(Object value) {
    return value instanceof LocalDateTime d ? d : ((Timestamp) value).toLocalDateTime();
  }

  public void send(int me, int chat, String text) throws Exception {
    if (text.isBlank() || text.length() > 4000)
      throw new IllegalArgumentException("Escribe entre 1 y 4000 caracteres.");
    db.tx(
        c -> {
          member(c, me, chatMatch(c, chat));
          Db.insert(
              c,
              "INSERT INTO MESSAGE (idChat,sender,content) VALUES (?,?,?)",
              chat,
              me,
              text.strip());
          return null;
        });
  }

  public void schedule(int me, int match, String place, LocalDateTime date) throws Exception {
    if (place.isBlank() || place.length() > 200)
      throw new IllegalArgumentException("Indica un lugar de hasta 200 caracteres.");
    if (date == null)
      throw new IllegalArgumentException("Indica la fecha y hora de la cita.");
    LocalDateTime slot = date.truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
    if (!slot.isAfter(LocalDateTime.now(clock)))
      throw new IllegalArgumentException("La cita debe tener fecha y hora futuras.");
    db.tx(
        c -> {
          member(c, me, match);
          var pair = Db.rows(c,
              "SELECT l.idUser1,l.idUser2 FROM POLIMATCH pm JOIN `LIKE` l"
                  + " ON l.idLike=pm.idLike1 WHERE pm.idPoliMatch=?", match).getFirst();
          int first = ((Number) pair.get("idUser1")).intValue();
          int second = ((Number) pair.get("idUser2")).intValue();
          // Bloquear ambos usuarios evita dos reservas simultáneas en matches diferentes.
          // El orden fijo evita interbloqueos entre parejas que comparten participantes.
          for (int participant : new int[] {Math.min(first, second), Math.max(first, second)})
            Db.rows(c, "SELECT idUser FROM `USER` WHERE idUser=? FOR UPDATE", participant);
          if (Db.number(c,
                  "SELECT COUNT(*) FROM POLIDATE d JOIN POLIMATCH pm"
                      + " ON pm.idPoliMatch=d.idPoliMatch JOIN `LIKE` l ON l.idLike=pm.idLike1"
                      + " WHERE d.dateOfPoliDate>=? AND d.dateOfPoliDate<?"
                      + " AND (l.idUser1 IN (?,?) OR l.idUser2 IN (?,?))",
                  Timestamp.valueOf(slot), Timestamp.valueOf(slot.plusMinutes(1)),
                  first, second, first, second) > 0)
            throw new IllegalArgumentException(
                "Tú o la otra persona ya tienen una PoliDate en esa fecha y hora. Elige otro horario.");
          Db.insert(
              c,
              "INSERT INTO POLIDATE (place,dateOfPoliDate,idPoliMatch) VALUES (?,?,?)",
              place.strip(),
              Timestamp.valueOf(slot),
              match);
          return null;
        });
  }

  public TableData dates(int me) throws Exception {
    try (var c = db.open();
        var st =
            Db.statement(
                c,
                "SELECT d.idPoliDate AS ID,u.name AS Persona,d.place AS Lugar,d.dateOfPoliDate AS"
                    + " Fecha, CASE WHEN d.dateOfPoliDate>NOW() THEN 'Próxima' ELSE 'Fecha pasada'"
                    + " END AS Situación FROM POLIDATE d JOIN POLIMATCH pm ON"
                    + " pm.idPoliMatch=d.idPoliMatch JOIN `LIKE` l ON l.idLike=pm.idLike1 JOIN"
                    + " `USER` u ON u.idUser=CASE WHEN l.idUser1=? THEN l.idUser2 ELSE l.idUser1"
                    + " END WHERE ? IN(l.idUser1,l.idUser2) ORDER BY d.dateOfPoliDate DESC",
                me,
                me);
        var rs = st.executeQuery()) {
      return Tables.read(rs, 0);
    }
  }
}
