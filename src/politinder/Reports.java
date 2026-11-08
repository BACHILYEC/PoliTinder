package politinder;

import static politinder.Models.*;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** Una sola fuente SQL para los reportes y el TXT de exposición. */
public final class Reports {
  public record Report(int id, String title, String explanation, String sql) {
    public String toString() {
      return id + ". " + title;
    }
  }

  public static List<Report> load() throws Exception {
    List<Report> result = new ArrayList<>();
    int id = 0;
    String title = "";
    var sql = new StringBuilder();
    var notes = new StringBuilder();
    for (String line : Files.readAllLines(AppConfig.ROOT.resolve("sql/03_consultas_exposicion.txt"))) {
      if (line.startsWith("-- @report ")) {
        var parts = line.substring(11).split("\\|", 2);
        id = Integer.parseInt(parts[0].strip());
        title = parts[1].strip();
        sql.setLength(0);
        notes.setLength(0);
      } else if (line.equals("-- @end")) {
        result.add(new Report(id, title, notes.toString().strip(), sql.toString().strip()));
        id = 0;
      } else if (id != 0) {
        if (line.startsWith("--"))
          notes.append(line.substring(2).strip()).append(' ');
        else
          sql.append(line).append('\n');
      }
    }
    if (result.size() != 9)
      throw new IllegalStateException("El TXT debe contener nueve reportes.");
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
