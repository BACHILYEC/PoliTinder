package politinder;

import static politinder.Models.*;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** Una sola fuente SQL para los reportes y el archivo .sql de exposición. */
public final class Reports {
  public record Report(int id, String title, String explanation, String sql) {
    public String toString() {
      return id + ". " + title;
    }
  }

  public static List<Report> load() throws Exception {
    return load(AppConfig.ROOT.resolve("sql/03_reportes.sql"));
  }

  public static List<Report> load(Path path) throws Exception {
    List<Report> result = new ArrayList<>();
    Set<Integer> ids = new HashSet<>();
    int id = 0;
    String title = "";
    var sql = new StringBuilder();
    var notes = new StringBuilder();
    for (String line : Files.readAllLines(path)) {
      if (line.startsWith("-- @report ")) {
        if (id != 0) throw new IllegalArgumentException("Falta -- @end antes del siguiente reporte.");
        var parts = line.substring(11).split("\\|", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Formato: -- @report ID | Título");
        id = Integer.parseInt(parts[0].strip());
        if (id <= 0 || !ids.add(id)) throw new IllegalArgumentException("ID inválido o repetido: " + id);
        title = parts[1].strip();
        if (title.isBlank()) throw new IllegalArgumentException("El reporte necesita un título.");
        sql.setLength(0);
        notes.setLength(0);
      } else if (line.equals("-- @end")) {
        if (id == 0 || sql.toString().isBlank()) throw new IllegalArgumentException("Reporte vacío o cierre sin apertura.");
        result.add(new Report(id, title, notes.toString().strip(), sql.toString().strip()));
        id = 0;
      } else if (id != 0) {
        if (line.startsWith("--"))
          notes.append(line.substring(2).strip()).append(' ');
        else
          sql.append(line).append('\n');
      }
    }
    if (id != 0) throw new IllegalArgumentException("Falta -- @end al final del reporte.");
    return result;
  }

  public static TableData run(Db db, Report report, Map<String, Object> parameters)
      throws Exception {
    Matcher m = Pattern.compile("@(edad_min|edad_max|carrera|facultad|corte)").matcher(report.sql());
    List<Object> args = new ArrayList<>();
    StringBuilder sql = new StringBuilder();
    while (m.find()) {
      Object value = parameters.get(m.group(1));
      if (value == null)
        throw new IllegalArgumentException("Falta parámetro: " + m.group(1));
      args.add(value);
      m.appendReplacement(sql, "?");
    }
    m.appendTail(sql);
    try (var c = db.open();
        var st = Db.statement(c, sql.toString(), args.toArray());
        var rs = st.executeQuery()) {
      return Tables.read(rs, 0);
    }
  }
}
