package politinder;

import java.nio.file.*;

public final class Installer {
  public static String check(Db db) throws Exception {
    try (var c = db.open(false)) {
      var r = Db.rows(
          c,
          "SELECT VERSION() AS version,@@default_storage_engine AS"
              + " engine,@@character_set_server AS charset")
          .getFirst();
      return "MySQL " + r.get("version") + " · " + r.get("engine") + " · " + r.get("charset");
    }
  }

  public static void initialize(Db db) throws Exception {
    try (var c = db.open(false)) {
      if (Db.number(
          c, "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name='politinder'") != 0)
        throw new IllegalArgumentException(
            "La base politinder ya existe. No se modificó. Usa Conectar o revisa los scripts en"
                + " Workbench.");
      var r = Db.rows(c, "SELECT @@default_storage_engine AS engine,@@character_set_server AS charset")
          .getFirst();
      if (!"InnoDB".equalsIgnoreCase("" + r.get("engine"))
          || !"utf8mb4".equalsIgnoreCase("" + r.get("charset")))
        throw new IllegalArgumentException(
            "Los valores predeterminados no son InnoDB/utf8mb4. Ajusta explícitamente el DDL para"
                + " este servidor antes de cargarlo; no se cambiaron opciones globales.");
      for (String sql : SqlScripts.split(Files.readString(AppConfig.ROOT.resolve("sql/01_ddl.sql"))))
        try (var st = c.createStatement()) {
          st.execute(sql);
        }
    }
    seed(db);
  }

  public static void seed(Db db) throws Exception {
    try (var c = db.open()) {
      // Todas las tablas deben estar vacías: nunca reemplazar datos existentes.
      for (String table : new String[] {
          "FACULTY",
          "MAJOR",
          "GENDER",
          "USER",
          "USERPHOTO",
          "LIKE",
          "POLIMATCH",
          "CHAT",
          "MESSAGE",
          "POLIDATE"
      })
        if (Db.number(c, "SELECT COUNT(*) FROM `" + table + "`") != 0)
          throw new IllegalArgumentException("La carga demo requiere tablas vacías.");
      c.setAutoCommit(false);
      try {
        for (String sql : SqlScripts.split(Files.readString(AppConfig.ROOT.resolve("sql/02_dml.sql")))) {
          if (sql.equalsIgnoreCase("START TRANSACTION") || sql.equalsIgnoreCase("COMMIT"))
            continue;
          try (var st = c.createStatement()) {
            st.execute(sql);
          }
        }
        c.commit();
      } catch (Exception e) {
        c.rollback();
        throw e;
      }
    }
  }
}
