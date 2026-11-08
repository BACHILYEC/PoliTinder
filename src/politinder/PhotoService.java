package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public final class PhotoService {
  private final Db db;

  public PhotoService(Db db) {
    this.db = db;
  }

  public List<Photo> list(int user) throws Exception {
    try (var c = db.open()) {
      return Db.rows(
              c,
              "SELECT * FROM USERPHOTO WHERE idUser=? ORDER BY isProfilePhoto DESC,idUserPhoto",
              user)
          .stream()
          .map(
              r ->
                  new Photo(
                      ((Number) r.get("idUserPhoto")).intValue(),
                      (String) r.get("pathPhoto"),
                      Boolean.TRUE.equals(r.get("isProfilePhoto"))
                          || "1".equals("" + r.get("isProfilePhoto"))))
          .toList();
    }
  }

  public void add(int user, Path file) throws Exception {
    if (Files.size(file) > 5 * 1024 * 1024)
      throw new IllegalArgumentException("La imagen supera 5 MB.");
    String filename = file.getFileName().toString().toLowerCase(Locale.ROOT);
    if (!filename.matches(".*\\.(jpg|jpeg|png)$"))
      throw new IllegalArgumentException("Selecciona una imagen JPEG o PNG.");
    BufferedImage source;
    try (var input = ImageIO.createImageInputStream(file.toFile())) {
      var readers = ImageIO.getImageReaders(input);
      if (!readers.hasNext()) throw new IllegalArgumentException("La imagen no se puede leer.");
      var reader = readers.next();
      try {
        reader.setInput(input);
        if ((long) reader.getWidth(0) * reader.getHeight(0) > 40_000_000)
          throw new IllegalArgumentException("La imagen tiene demasiados píxeles (máximo 40 MP).");
        source = reader.read(0);
      } finally {
        reader.dispose();
      }
    }
    String rel = "photos/users/" + user + "/" + UUID.randomUUID() + ".jpg";
    Path dest = AppConfig.resolve(rel), thumb = thumb(dest);
    Files.createDirectories(dest.getParent());
    try {
      ImageIO.write(scale(source, 1400), "jpg", dest.toFile());
      ImageIO.write(scale(source, 400), "jpg", thumb.toFile());
      db.tx(
          c -> {
            Db.rows(c, "SELECT idUser FROM `USER` WHERE idUser=? FOR UPDATE", user);
            long n = Db.number(c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=?", user);
            if (n >= 6) throw new IllegalArgumentException("Puedes tener hasta 6 fotografías.");
            boolean main =
                n == 0
                    || Db.number(
                            c,
                            "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND isProfilePhoto=TRUE",
                            user)
                        == 0;
            Db.insert(
                c,
                "INSERT INTO USERPHOTO (pathPhoto,isProfilePhoto,idUser) VALUES (?,?,?)",
                rel,
                main,
                user);
            return null;
          });
    } catch (Exception e) {
      Files.deleteIfExists(dest);
      Files.deleteIfExists(thumb);
      throw e;
    }
  }

  public void main(int user, int photo) throws Exception {
    db.tx(
        c -> {
          Db.rows(c, "SELECT idUser FROM `USER` WHERE idUser=? FOR UPDATE", user);
          if (Db.number(
                  c, "SELECT COUNT(*) FROM USERPHOTO WHERE idUserPhoto=? AND idUser=?", photo, user)
              != 1) throw new IllegalArgumentException("Fotografía no encontrada.");
          Db.update(c, "UPDATE USERPHOTO SET isProfilePhoto=FALSE WHERE idUser=?", user);
          Db.update(
              c,
              "UPDATE USERPHOTO SET isProfilePhoto=TRUE WHERE idUserPhoto=? AND idUser=?",
              photo,
              user);
          return null;
        });
  }

  public void delete(int user, int photo) throws Exception {
    String rel =
        db.tx(
            c -> {
              Db.rows(c, "SELECT idUser FROM `USER` WHERE idUser=? FOR UPDATE", user);
              var rows =
                  Db.rows(
                      c,
                      "SELECT pathPhoto FROM USERPHOTO WHERE idUserPhoto=? AND idUser=?",
                      photo,
                      user);
              if (rows.isEmpty()) return null;
              Db.update(c, "DELETE FROM USERPHOTO WHERE idUserPhoto=? AND idUser=?", photo, user);
              if (Db.number(
                      c,
                      "SELECT COUNT(*) FROM USERPHOTO WHERE idUser=? AND isProfilePhoto=TRUE",
                      user)
                  == 0) {
                int next =
                    (int)
                        Db.number(c, "SELECT MIN(idUserPhoto) FROM USERPHOTO WHERE idUser=?", user);
                if (next != 0)
                  Db.update(
                      c, "UPDATE USERPHOTO SET isProfilePhoto=TRUE WHERE idUserPhoto=?", next);
              }
              return (String) rows.getFirst().get("pathPhoto");
            });
    // Los recursos demo son compartibles y se conservan; solo se eliminan subidas propias.
    if (rel != null && rel.startsWith("photos/users/")) {
      try {
        var p = AppConfig.resolve(rel);
        Files.deleteIfExists(p);
        Files.deleteIfExists(thumb(p));
      } catch (Exception ignored) {
      }
    }
  }

  public static Path thumb(Path p) {
    return p.resolveSibling(p.getFileName() + ".thumb.jpg");
  }

  public static BufferedImage scale(BufferedImage source, int max) {
    double ratio = Math.min(1.0, (double) max / Math.max(source.getWidth(), source.getHeight()));
    var img =
        new BufferedImage(
            Math.max(1, (int) (source.getWidth() * ratio)),
            Math.max(1, (int) (source.getHeight() * ratio)),
            BufferedImage.TYPE_INT_RGB);
    var g = img.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, img.getWidth(), img.getHeight());
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.drawImage(source, 0, 0, img.getWidth(), img.getHeight(), null);
    g.dispose();
    return img;
  }
}
