import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import politinder.AppConfig;
import politinder.Passwords;

/** Generador opcional de datos ficticios y avatares originales, sin descargas de fotos. */
public class GenerateDemo {
  static String q(String s) {
    return "'" + s.replace("'", "''") + "'";
  }

  static String[] names = {
    "Ana Torres",
    "Luis Andrade",
    "Sofía Molina",
    "Mateo Ruiz",
    "Valentina Paz",
    "Diego Luna",
    "Camila Ríos",
    "Nicolás Vega",
    "Isabella León",
    "Daniel Cueva",
    "Lucía Flores",
    "Emilia Castro",
    "Joaquín Mora",
    "Martina Pérez",
    "Gabriel Silva",
    "Renata Ortiz",
    "Andrés Salas",
    "Elena Vidal",
    "Paula Díaz",
    "Samuel Lara"
  };
  static String[] hobbies = {
    "Café, fotografía y paseos por Quito. ¿Tu lugar favorito del campus?",
    "Programo, juego básquet y siempre tengo una playlist nueva.",
    "Me gustan los libros, los museos y conversar sin mirar el reloj.",
    "Buscando compañía para un café después de clases.",
    "Música en vivo, montañas y proyectos que valgan la pena."
  };

  public static void main(String[] args) throws Exception {
    Path root = AppConfig.ROOT;
    StringBuilder s =
        new StringBuilder(
            "-- PoliTinder · datos ficticios. Ejecutar UNA VEZ sobre las tablas vacías.\n"
                + "-- Contraseña demo de todos los usuarios: PoliDemo25! (hashes con sal"
                + " individual).\n"
                + "USE politinder;\n"
                + "SET time_zone='-05:00';\n"
                + "START TRANSACTION;\n");
    String[] faculties = {
      "Ingeniería de Sistemas",
      "Ciencias",
      "Ciencias Administrativas",
      "Ingeniería Civil y Ambiental",
      "Ingeniería Eléctrica y Electrónica",
      "Geología y Petróleos",
      "Ingeniería Mecánica",
      "Ingeniería Química y Agroindustria",
      "Escuela de Formación de Tecnólogos (ESFOT)"
    };
    for (int i = 0; i < faculties.length; i++)
      s.append("INSERT INTO FACULTY VALUES (" + (i + 1) + "," + q(faculties[i]) + ");\n");
    String[][] majors = {
      {
        "Software",
        "Computación",
        "Sistemas de Información",
        "Ciencia de Datos e Inteligencia Artificial"
      },
      {"Física", "Matemática", "Matemática Aplicada", "Economía"},
      {"Ingeniería de la Producción"},
      {"Ingeniería Civil", "Ingeniería Ambiental"},
      {
        "Electrónica y Automatización",
        "Electricidad",
        "Tecnologías de la Información",
        "Telecomunicaciones"
      },
      {"Geología", "Petróleos"},
      {"Ingeniería Mecánica", "Materiales", "Mecatrónica"},
      {"Agroindustria", "Ingeniería Química"},
      {
        "Tecnología Superior en Agua y Saneamiento Ambiental",
        "Tecnología Superior en Redes y Telecomunicaciones",
        "Tecnología Superior en Desarrollo de Software",
        "Tecnología Superior en Electromecánica",
        "Procesamiento de Alimentos",
        "Procesamiento Industrial de la Madera"
      }
    };
    int major = 1;
    for (int f = 0; f < majors.length; f++)
      for (String m : majors[f])
        s.append("INSERT INTO MAJOR VALUES (" + (major++) + "," + q(m) + "," + (f + 1) + ");\n");
    s.append(
        "INSERT INTO GENDER VALUES (1,'Mujer'),(2,'Hombre'),(3,'No binario'),(4,'Prefiero no"
            + " indicarlo');\n");
    int[] ages = {22, 23, 21, 24, 20, 25, 23, 26, 22, 24, 21, 20, 25, 23, 27, 24, 33, 33, 19, 22};
    int[] career = {1, 1, 5, 9, 2, 10, 13, 16, 18, 21, 22, 1, 4, 7, 14, 20, 22, 24, 1, 2};
    for (int i = 0; i < 20; i++) {
      String mail =
          java.text.Normalizer.normalize(
                      names[i].split(" ")[0].toLowerCase(), java.text.Normalizer.Form.NFD)
                  .replaceAll("\\p{M}", "")
              + "@politinder.test";
      String hash = Passwords.hash("PoliDemo25!".toCharArray());
      s.append(
          "INSERT INTO `USER`"
              + " (idUser,status,mail,password,biography,name,bornDate,idGender,idMajor) VALUES ("
              + (i + 1)
              + ","
              + q(i == 16 || i == 17 ? "PAUSED" : "ACTIVE")
              + ","
              + q(mail)
              + ","
              + q(hash)
              + ","
              + q(hobbies[i % hobbies.length])
              + ","
              + q(names[i])
              + ",DATE_SUB(CURDATE(),INTERVAL "
              + ages[i]
              + " YEAR),"
              + (i % 2 == 0 ? 1 : 2)
              + ","
              + career[i]
              + ");\n");
    }
    Files.createDirectories(root.resolve("photos/demo"));
    for (int i = 1; i <= 24; i++) {
      int user = i <= 18 ? i : i - 18;
      String path = String.format("photos/demo/avatar-%02d.jpg", i);
      avatar(root.resolve(path), i, names[user - 1]);
      s.append(
          "INSERT INTO USERPHOTO VALUES ("
              + i
              + ","
              + q(path)
              + ","
              + (i <= 18 ? 1 : 0)
              + ","
              + user
              + ");\n");
    }
    int[][] pairs = {{1, 2}, {1, 3}, {4, 5}, {6, 7}, {8, 9}, {10, 11}};
    int like = 1;
    for (int[] pair : pairs) {
      s.append(
          "INSERT INTO `LIKE` VALUES ("
              + like
              + ","
              + pair[0]
              + ","
              + pair[1]
              + "),("
              + (like + 1)
              + ","
              + pair[1]
              + ","
              + pair[0]
              + ");\n");
      like += 2;
    }
    s.append("INSERT INTO `LIKE` VALUES (13,12,13),(14,14,15),(15,16,17),(16,2,3);\n");
    for (int i = 1; i <= 6; i++) {
      s.append("INSERT INTO POLIMATCH VALUES (" + i + "," + (i * 2 - 1) + "," + (i * 2) + ");\n");
      s.append("INSERT INTO CHAT VALUES (" + i + "," + i + ");\n");
    }
    String[] messages = {
      "¡Hola! Qué gusto coincidir 😊",
      "¡Hola! ¿Cómo va tu semestre?",
      "Entre clases y proyectos, pero bien. ¿Un café?",
      "¡Me apunto! Podemos ir a la cafetería.",
      "Perfecto, lo agendamos como PoliDate.",
      "¡Nos vemos! ☕"
    };
    int msg = 1;
    for (int chat = 1; chat <= 4; chat++)
      for (int i = 0; i < 6; i++)
        s.append(
            "INSERT INTO MESSAGE (idMessage,sendDate,content,idChat,sender) VALUES ("
                + (msg++)
                + ",DATE_SUB(NOW(),INTERVAL "
                + (120 - i * 5)
                + " MINUTE),"
                + q(messages[i])
                + ","
                + chat
                + ","
                + pairs[chat - 1][i % 2]
                + ");\n");
    int[] dateMatches = {1, 1, 2, 2, 3, 1, 4, 5};
    for (int i = 0; i < 8; i++)
      s.append(
          "INSERT INTO POLIDATE VALUES ("
              + (i + 1)
              + ","
              + q(i % 2 == 0 ? "Cafetería de la EPN" : "Parque La Alameda")
              + ",DATE_"
              + (i < 5 ? "SUB" : "ADD")
              + "(NOW(),INTERVAL "
              + (i + 1)
              + " DAY),"
              + dateMatches[i]
              + ");\n");
    s.append("COMMIT;\n");
    Files.writeString(root.resolve("sql/02_dml.sql"), s.toString());
    System.out.println("DML y 24 avatares generados.");
  }

  static void avatar(Path path, int seed, String name) throws Exception {
    var img = new BufferedImage(600, 760, BufferedImage.TYPE_INT_RGB);
    var g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Color[] colors = {
      new Color(226, 154, 165),
      new Color(124, 163, 184),
      new Color(163, 154, 192),
      new Color(156, 180, 148),
      new Color(227, 178, 126)
    };
    Color c = colors[(seed - 1) % colors.length];
    g.setPaint(new GradientPaint(0, 0, c, 600, 760, new Color(250, 240, 229)));
    g.fillRect(0, 0, 600, 760);
    g.setColor(new Color(255, 255, 255, 65));
    g.fillOval(-100, -100, 450, 450);
    g.fillOval(400, 370, 420, 420);
    g.setColor(new Color(255, 255, 255, 50));
    for (int j = 0; j < 6; j++) g.drawRoundRect(35 + j * 4, 70 + j * 4, 500, 560, 200, 200);
    Color skin = new Color(213 + (seed % 3) * 10, 159 + (seed % 3) * 12, 130 + (seed % 3) * 14);
    Color hair = new Color(47 + (seed % 4) * 12, 37 + (seed % 3) * 7, 40 + (seed % 3) * 7);
    g.setColor(hair);
    g.fillRoundRect(181, 145, 238, 350, 140, 140);
    g.setColor(c.darker());
    g.fill(new Ellipse2D.Double(100, 410, 400, 480));
    g.setColor(skin.darker());
    g.fillRoundRect(264, 335, 72, 150, 30, 30);
    g.setColor(skin);
    g.fillOval(195, 175, 210, 255);
    g.fillOval(181, 279, 35, 61);
    g.fillOval(385, 279, 35, 61);
    g.setColor(hair);
    g.fillArc(187, 155, 230, 178, 0, 180);
    g.fillRoundRect(193, 185, 35, 120, 26, 26);
    g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g.drawArc(235, 282, 36, 15, 10, 160);
    g.drawArc(331, 282, 36, 15, 10, 160);
    g.fillOval(249, 307, 8, 10);
    g.fillOval(344, 307, 8, 10);
    g.setColor(new Color(159, 86, 81));
    g.drawArc(273, 351, 60, 28, 190, 150);
    g.setColor(new Color(255, 255, 255, 190));
    g.setFont(new Font("SansSerif", Font.BOLD, 18));
    g.drawString("POLITINDER  /  EPN", 35, 47);
    g.setPaint(
        new GradientPaint(0, 550, new Color(0, 0, 0, 0), 0, 760, new Color(30, 25, 45, 220)));
    g.fillRect(0, 550, 600, 210);
    g.setColor(Color.WHITE);
    g.setFont(new Font("SansSerif", Font.BOLD, 40));
    g.drawString(name.split(" ")[0], 36, 688);
    g.setFont(new Font("SansSerif", Font.PLAIN, 18));
    g.drawString("Perfil ficticio · demostración", 38, 722);
    g.dispose();
    ImageIO.write(img, "jpg", path.toFile());
  }
}
