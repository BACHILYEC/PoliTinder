package politinder;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/** Rutas portables: admite Run Java, Run.ps1 y ejecución desde bin. */
public final class AppConfig {
  public static final Path ROOT = locateRoot();
  public String host = "localhost", user = "root", password = "";
  public int port = 3306;

  private static Path locateRoot() {
    Path p = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    for (int i = 0; i < 5 && p != null; i++, p = p.getParent())
      if (Files.isDirectory(p.resolve("sql")) && Files.isDirectory(p.resolve("lib"))) return p;
    try {
      p = Path.of(AppConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      for (int i = 0; i < 5 && p != null; i++, p = p.getParent())
        if (Files.isDirectory(p.resolve("sql"))) return p;
    } catch (Exception ignored) {
    }
    return Path.of(System.getProperty("user.dir")).toAbsolutePath();
  }

  public AppConfig() {
    Properties p = new Properties();
    Path file = ROOT.resolve("config/local.properties");
    if (Files.exists(file))
      try (var in = Files.newInputStream(file)) {
        p.load(in);
        host = p.getProperty("host", host);
        user = p.getProperty("user", user);
        port = Integer.parseInt(p.getProperty("port", "3306"));
      } catch (Exception ignored) {
      }
    password = System.getenv().getOrDefault("POLITINDER_DB_PASSWORD", "");
  }

  public void save() throws IOException {
    Properties p = new Properties();
    p.setProperty("host", host);
    p.setProperty("port", "" + port);
    p.setProperty("user", user);
    Files.createDirectories(ROOT.resolve("config"));
    try (var out = Files.newOutputStream(ROOT.resolve("config/local.properties"))) {
      p.store(out, "La contraseña solo se conserva en memoria.");
    }
  }

  public String url(boolean database) {
    if (!host.matches("[A-Za-z0-9.:-]+") || port < 1 || port > 65535)
      throw new IllegalArgumentException("Servidor o puerto inválido.");
    return "jdbc:mysql://"
        + host
        + ":"
        + port
        + "/"
        + (database ? "politinder" : "")
        + "?connectionTimeZone=-05:00&connectTimeout=5000&socketTimeout=60000";
  }

  public static Path resolve(String relative) {
    Path p = ROOT.resolve(relative).normalize();
    if (!p.startsWith(ROOT)) throw new IllegalArgumentException("Ruta fuera del proyecto.");
    return p;
  }
}
