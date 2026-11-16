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
  }

  public static void migrate(Db db) throws Exception {
    try (var c = db.open()) {
      if (Db.number(c, "SELECT GET_LOCK('politinder_schema',10)") != 1)
        throw new IllegalStateException("Otra instancia está actualizando la base.");
      try {
        for (String sql : SqlScripts.split(Files.readString(AppConfig.ROOT.resolve("sql/02_actualizar_base.sql"))))
          try (var st = c.createStatement()) { st.execute(sql); }
      } finally { Db.number(c, "SELECT RELEASE_LOCK('politinder_schema')"); }
    }
  }
}
