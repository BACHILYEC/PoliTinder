package politinder;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Renderiza las pantallas reales de Swing, sin modificar la configuración local. */
public class UiSmokeTest {
  static AppFrame app;

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
      error.printStackTrace();
      System.exit(1);
    });
    Files.createDirectories(AppConfig.ROOT.resolve("test-output"));
    SwingUtilities.invokeAndWait(
        () -> {
          Ui.installTheme();
          app = new AppFrame();
          app.setBounds(50, 50, 430, 760);
          app.addNotify();
          app.validate();
        });
    if(args.length>0){
      SwingUtilities.invokeAndWait(()->{app.config.host="127.0.0.1";app.config.port=33307;app.config.user="root";app.config.password="";});
      var demo=app.people.get(1);
      SwingUtilities.invokeAndWait(()->app.signedIn(demo));
      capture("scale-"+args[0]);
      System.out.println("Escala solicitada "+args[0]+"; transformación efectiva "+app.getGraphicsConfiguration().getDefaultTransform());
      SwingUtilities.invokeAndWait(()->app.dispatchEvent(new java.awt.event.WindowEvent(app,java.awt.event.WindowEvent.WINDOW_CLOSING)));
      System.exit(0);
    }
    capture("01-conexion");
    SwingUtilities.invokeAndWait(
        () -> {
          app.config.host = "127.0.0.1";
          app.config.port = 33307;
          app.config.user = "root";
          app.config.password = "";
          app.connected();
        });
    capture("02-acceso");
    var user = app.people.get(1);
    SwingUtilities.invokeAndWait(() -> {
      app.maxAge = 40;
      app.signedIn(user);
    });
    capture("03-descubrir");
    verifyDiscovery();
    SwingUtilities.invokeAndWait(() -> app.profile(false));
    capture("04-perfil");
    SwingUtilities.invokeAndWait(app::reports);
    capture("05-reportes");
    SwingUtilities.invokeAndWait(() -> selectReport(app.getContentPane()));
    Thread.sleep(100);
    SwingUtilities.invokeAndWait(() -> click(app.getContentPane(), "Ejecutar reporte"));
    capture("05b-reporte-final");
    SwingUtilities.invokeAndWait(app::console);
    SwingUtilities.invokeAndWait(() -> click(app.getContentPane(), "Ejecutar"));
    capture("06-consola");
    var match = app.people.matches(1).getFirst();
    SwingUtilities.invokeAndWait(() -> app.chat(match));
    capture("07-chat");
    SwingUtilities.invokeAndWait(app::dates);
    capture("07b-polidates");
    SwingUtilities.invokeAndWait(app::matches);
    capture("07c-matches");
    SwingUtilities.invokeAndWait(
        () -> {
          app.setSize(320, 570);
          app.discover();
        });
    capture("08-pequena");
    SwingUtilities.invokeAndWait(
        () -> {
          app.setSize(850, 950);
          app.reports();
        });
    capture("09-amplia");
    SwingUtilities.invokeAndWait(
        () ->
            app.dispatchEvent(
                new java.awt.event.WindowEvent(app, java.awt.event.WindowEvent.WINDOW_CLOSING)));
    System.out.println("Pantallas renderizadas; tres vueltas reales de Descubrir verificadas.");
    System.exit(0);
  }

  static JButton passButton(Container c) {
    for (Component child : c.getComponents()) {
      if (child instanceof JButton button && button.getText().contains("Pasar")) return button;
      if (child instanceof Container nested) {
        JButton found = passButton(nested);
        if (found != null) return found;
      }
    }
    return null;
  }

  static void verifyDiscovery() throws Exception {
    var expected = new java.util.HashSet<>(app.people.discover(1, 18, 40, 0, java.util.Set.of())
        .stream().map(Models.User::id).toList());
    if (expected.size() < 2) throw new AssertionError("Faltan candidatos para probar el ciclo visual.");
    int previous = 0;
    for (int round = 0; round < 3; round++) {
      var seen = new java.util.HashSet<Integer>();
      for (int n = 0; n < expected.size(); n++) {
        var ready = new java.util.concurrent.atomic.AtomicBoolean();
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(8);
        while (!ready.get() && System.nanoTime() < deadline) {
          SwingUtilities.invokeAndWait(() -> {
            JButton button = passButton(app.getContentPane());
            ready.set(button != null && button.isEnabled());
          });
          if (!ready.get()) Thread.sleep(25);
        }
        if (!ready.get()) throw new AssertionError("Descubrir quedó bloqueado al cambiar de perfil.");
        var current = new java.util.concurrent.atomic.AtomicInteger();
        SwingUtilities.invokeAndWait(() -> current.set(app.lastDiscoveredUser));
        if (!expected.contains(current.get()) || !seen.add(current.get()) || previous == current.get())
          throw new AssertionError("Perfil repetido, match incluido o candidato incorrecto en Descubrir.");
        previous = current.get();
        SwingUtilities.invokeAndWait(() -> passButton(app.getContentPane()).doClick());
      }
      if (!seen.equals(expected)) throw new AssertionError("Una vuelta omitió candidatos.");
    }
    System.out.println("OK · UI: tres vueltas completas, sin repeticiones inmediatas ni matches.");
  }

  static void capture(String name) throws Exception {
    Thread.sleep(1600);
    SwingUtilities.invokeAndWait(
        () -> {
          app.validate();
          var content = app.getContentPane();
          layout(content);
          var img =
              new BufferedImage(
                  content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
          var g = img.createGraphics();
          content.printAll(g);
          g.dispose();
          try {
            ImageIO.write(
                img, "png", AppConfig.ROOT.resolve("test-output/" + name + ".png").toFile());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  static void layout(Container c) {
    c.doLayout();
    for (Component child : c.getComponents()) if (child instanceof Container nested) layout(nested);
  }

  static void click(Container c, String text) {
    for (Component child : c.getComponents()) {
      if (child instanceof JButton b && b.getText().equals(text)) {
        b.doClick();
        return;
      }
      if (child instanceof Container nested) click(nested, text);
    }
  }

  static void selectReport(Container c) {
    for (Component child : c.getComponents()) {
      if (child instanceof JComboBox<?> combo
          && combo.getItemCount() == 9
          && combo.getItemAt(0) instanceof Reports.Report) {
        combo.setSelectedIndex(8);
        return;
      }
      if (child instanceof Container nested) selectReport(nested);
    }
  }
}
