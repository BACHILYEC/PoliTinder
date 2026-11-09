package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class AppFrame extends JFrame {
  public final AppConfig config = new AppConfig();
  public final Db db = new Db(config);
  public final PeopleService people = new PeopleService(db);
  public final PhotoService photos = new PhotoService(db);
  public final ChatService chats = new ChatService(db);
  public User me;
  public final Set<Integer> skipped = new HashSet<>();
  public int lastDiscoveredUser;
  public int minAge = 18, maxAge = 99, filterGender = 0;
  private final JPanel content = new JPanel(new BorderLayout());
  private Ui.Screen current;
  private ConsoleView console;
  private final JButton menu = new JButton("☰");
  private final JLabel section = Ui.label("CONECTA EN LA POLI", 10, true);

  public AppFrame() {
    super("PoliTinder · EPN");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    var head = new JPanel(new BorderLayout(12, 0));
    head.setBackground(Ui.NAVY);
    head.setBorder(new EmptyBorder(13, 20, 13, 20));
    var mark = Ui.label("PoliTinder", 24, true);
    mark.setForeground(Color.WHITE);
    section.setForeground(new Color(246, 201, 212));
    menu.setBackground(new Color(55, 63, 94));
    menu.setForeground(Color.WHITE);
    menu.putClientProperty("FlatLaf.style",
        "hoverBackground: #48516E; pressedBackground: #555E7D; borderWidth: 0");
    menu.setToolTipText("Abrir menú");
    var brand = new JPanel(new BorderLayout(0, 3));
    brand.setOpaque(false);
    brand.add(mark, BorderLayout.NORTH);
    brand.add(section, BorderLayout.SOUTH);
    head.add(brand, BorderLayout.CENTER);
    head.add(menu, BorderLayout.EAST);
    menu.addActionListener(e -> navigation());
    menu.setEnabled(false);
    add(head, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    Ui.position(this);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            if (current != null) current.onHide();
            if (console != null) console.closeSession();
            dispose();
          }
        });
    // Sin mínima fija: los contenedores desplazan su contenido en pantallas pequeñas.
    var monitorTimer =
        new javax.swing.Timer(
            2500,
            e -> {
              if (!isShowing()) return;
              boolean visible = false;
              Rectangle b = getBounds();
              for (var d : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
                Rectangle screen = d.getDefaultConfiguration().getBounds();
                if (screen.intersects(new Rectangle(b.x, b.y, b.width, 40))) visible = true;
              }
              if (!visible) Ui.position(this);
            });
    monitorTimer.start();
    addWindowListener(
        new WindowAdapter() {
          public void windowClosed(WindowEvent e) {
            monitorTimer.stop();
          }
        });
    show(new AuthViews.ConnectionView(this), "CONEXIÓN LOCAL");
  }

  public void show(Ui.Screen view, String title) {
    if (current != null) current.onHide();
    current = view;
    content.removeAll();
    content.add(view);
    section.setText(title.toUpperCase(Locale.ROOT));
    view.onShow();
    content.revalidate();
    content.repaint();
  }

  public void connected() {
    menu.setEnabled(true);
    show(new AuthViews.LoginView(this), "BIENVENIDO");
  }

  public void signedIn(User user) {
    me = user;
    skipped.clear();
    lastDiscoveredUser = 0;
    show(new DiscoverView(this), "DESCUBRIR");
  }

  public void profile(boolean registration) {
    show(new ProfileView(this, registration), registration ? "CREAR CUENTA" : "MI PERFIL");
  }

  public void discover() {
    show(new DiscoverView(this), "DESCUBRIR");
  }

  public void matches() {
    show(new ChatViews.MatchesView(this), "MIS MATCHES");
  }

  public void chat(Match m) {
    show(new ChatViews.ConversationView(this, m), "CHAT · " + m.name());
  }

  public void dates() {
    show(new ChatViews.DatesView(this), "POLIDATES");
  }

  public void reports() {
    show(new ReportsView(this), "REPORTES SQL");
  }

  public void console() {
    if (console == null) console = new ConsoleView(this);
    show(console, "CONSOLA SQL");
  }

  public void logout() {
    if (console != null) {
      console.closeSession();
      console = null;
    }
    me = null;
    skipped.clear();
    minAge = 18;
    lastDiscoveredUser = 0;
    maxAge = 99;
    filterGender = 0;
    connected();
  }

  private void navigation() {
    var popup = new JPopupMenu();
    if (me != null) {
      item(popup, "Descubrir", this::discover);
      item(popup, "Mis matches", this::matches);
      item(popup, "PoliDates", this::dates);
      item(popup, "Mi perfil", () -> profile(false));
      popup.addSeparator();
    }
    item(popup, "Reportes", this::reports);
    item(popup, "Consola SQL", this::console);
    popup.addSeparator();
    if (me != null) item(popup, "Cerrar sesión", this::logout);
    else item(popup, "Iniciar sesión", this::connected);
    item(
        popup,
        "Conexión MySQL",
        () -> {
          logout();
          show(new AuthViews.ConnectionView(this), "CONEXIÓN LOCAL");
        });
    popup.show(
        menu, Math.min(0, menu.getWidth() - popup.getPreferredSize().width), menu.getHeight());
  }

  private static void item(JPopupMenu menu, String text, Runnable r) {
    var it = new JMenuItem(text);
    it.addActionListener(e -> r.run());
    menu.add(it);
  }
}
