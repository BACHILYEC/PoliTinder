package politinder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class Ui {
  public static final Color CORAL = new Color(217, 37, 85),
      INK = new Color(32, 38, 62),
      MUTED = new Color(97, 105, 124),
      BG = new Color(245, 246, 250),
      SURFACE = Color.WHITE,
      LINE = new Color(221, 226, 236),
      SOFT = new Color(252, 232, 237),
      NAVY = new Color(37, 43, 72),
      TEAL = new Color(20, 110, 104);
  public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public static void installTheme() {
    com.formdev.flatlaf.FlatLightLaf.setup();
    UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    UIManager.put("Component.arc", 16);
    UIManager.put("Button.arc", 22);
    UIManager.put("TextComponent.arc", 12);
    UIManager.put("Component.focusColor", CORAL);
    UIManager.put("Component.focusedBorderColor", CORAL);
    UIManager.put("Component.borderColor", LINE);
    UIManager.put("Panel.background", BG);
    UIManager.put("Label.foreground", INK);
    UIManager.put("Button.background", SURFACE);
    UIManager.put("Button.foreground", INK);
    UIManager.put("Button.hoverBackground", SOFT);
    UIManager.put("Button.pressedBackground", new Color(248, 212, 223));
    UIManager.put("Button.margin", new Insets(8, 14, 8, 14));
    UIManager.put("ToggleButton.selectedBackground", SOFT);
    UIManager.put("ToggleButton.selectedForeground", CORAL);
    UIManager.put("TextField.foreground", INK);
    UIManager.put("TextArea.foreground", INK);
    UIManager.put("TextField.selectionBackground", SOFT);
    UIManager.put("TextField.selectionForeground", INK);
    UIManager.put("List.selectionBackground", SOFT);
    UIManager.put("List.selectionForeground", INK);
    UIManager.put("Table.selectionBackground", SOFT);
    UIManager.put("Table.selectionForeground", INK);
    UIManager.put("Table.alternateRowColor", new Color(249, 247, 251));
    UIManager.put("TableHeader.background", NAVY);
    UIManager.put("TableHeader.foreground", Color.WHITE);
    UIManager.put("MenuItem.selectionBackground", SOFT);
    UIManager.put("MenuItem.selectionForeground", INK);
    UIManager.put("ScrollPane.smoothScrolling", true);
  }

  private static final class Stack extends JPanel implements Scrollable {
    Stack() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setOpaque(false);
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
      return 18;
    }

    public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
      return Math.max(18, r.height - 36);
    }

    public boolean getScrollableTracksViewportWidth() {
      return true;
    }

    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  public static JPanel column() {
    return new Stack();
  }

  public static JPanel row(Component... cs) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
    p.setOpaque(false);
    for (Component c : cs)
      p.add(c);
    return p;
  }

  public static JLabel label(String text, int size, boolean bold) {
    var l = new JLabel(text);
    l.setFont(l.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, (float) size));
    l.setForeground(INK);
    return l;
  }

  private static final class WrappedText extends JTextArea {
    WrappedText(String value) {
      super(value, 0, 24);
      setLineWrap(true);
      setWrapStyleWord(true);
      setEditable(false);
      setOpaque(false);
      setForeground(MUTED);
      setBorder(new EmptyBorder(4, 0, 8, 0));
      setCaret(
          new javax.swing.text.DefaultCaret() {
            @Override
            protected void adjustVisibility(Rectangle location) {
            }
          });
      setFocusable(false);
      ((javax.swing.text.DefaultCaret) getCaret())
          .setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
    }

    @Override
    public Dimension getPreferredSize() {
      if (getFont() == null)
        return super.getPreferredSize();
      int width = getParent() != null && getParent().getWidth() > 0 ? getParent().getWidth() : 300;
      int chars = Math.max(12, width / Math.max(1, getFontMetrics(getFont()).charWidth('n')));
      int lines = 0;
      for (String line : getText().split("\n", -1))
        lines += Math.max(1, (line.length() + chars - 1) / chars);
      return new Dimension(width, Math.max(1, lines) * getFontMetrics(getFont()).getHeight() + 16);
    }

    @Override
    public Dimension getMaximumSize() {
      return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    public Dimension getMinimumSize() {
      return new Dimension(0, getPreferredSize().height);
    }
  }

  public static JTextArea text(String value) {
    return new WrappedText(value);
  }

  public static JTextArea heading(String value, int size) {
    var t = text(value);
    t.setFont(t.getFont().deriveFont(Font.BOLD, (float) size));
    t.setForeground(INK);
    return t;
  }

  public static JButton button(String text, Runnable action) {
    var b = new JButton(text);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.addActionListener(e -> action.run());
    return b;
  }

  public static JButton primary(String text, Runnable action) {
    var b = button(text, action);
    b.setBackground(CORAL);
    b.setForeground(Color.WHITE);
    b.putClientProperty("JButton.buttonType", "roundRect");
    b.putClientProperty("FlatLaf.style",
        "hoverBackground: #C41E4A; pressedBackground: #A91C41; focusedBackground: #D92555");
    return b;
  }

  public static void add(JPanel p, Component c) {
    if (c instanceof JComponent jc) {
      jc.setAlignmentX(Component.LEFT_ALIGNMENT);
      jc.setMinimumSize(new Dimension(0, jc.getPreferredSize().height));
      jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, jc.getPreferredSize().height));
    }
    p.add(c);
    p.add(Box.createVerticalStrut(9));
  }

  public static void field(JPanel p, String name, JComponent input) {
    add(p, label(name, 12, true));
    add(p, input);
  }

  public static JScrollPane scroll(Component c) {
    var s = new JScrollPane(c);
    s.setBorder(null);
    s.getViewport().setBackground(BG);
    s.getVerticalScrollBar().setUnitIncrement(18);
    s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    return s;
  }

  public static String error(Throwable ex) {
    while (ex.getCause() != null
        && (ex instanceof java.util.concurrent.ExecutionException
            || ex instanceof java.lang.reflect.InvocationTargetException))
      ex = ex.getCause();
    if (ex instanceof java.sql.SQLException se) {
      if (se.getErrorCode() == 1062)
        return "Ese dato ya existe (correo, like o relación duplicada).";
      return "MySQL " + se.getErrorCode() + " [" + se.getSQLState() + "]: " + se.getMessage();
    }
    return ex.getMessage() == null ? ex.toString() : ex.getMessage();
  }

  public static Rectangle bounds(Rectangle available) {
    int h = (int) (available.height * .85);
    int w = Math.min((int) (h * .65), (int) (available.width * .9));
    return new Rectangle(
        available.x + (available.width - w) / 2, available.y + (available.height - h) / 2, w, h);
  }

  public static void position(Window window) {
    Point mouse = MouseInfo.getPointerInfo() == null ? new Point() : MouseInfo.getPointerInfo().getLocation();
    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .getDefaultConfiguration();
    for (var d : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
      if (d.getDefaultConfiguration().getBounds().contains(mouse))
        gc = d.getDefaultConfiguration();
    Rectangle r = new Rectangle(gc.getBounds());
    Insets in = Toolkit.getDefaultToolkit().getScreenInsets(gc);
    r.x += in.left;
    r.y += in.top;
    r.width -= in.left + in.right;
    r.height -= in.top + in.bottom;
    window.setBounds(bounds(r));
  }

  public static final class Portrait extends JComponent {
    private BufferedImage picture;
    private int generation;
    private boolean loading;

    public Portrait() {
      setPreferredSize(new Dimension(300, 330));
    }

    public void load(String path) {
      picture = null;
      loading = path != null;
      repaint();
      int ticket = ++generation;
      if (path == null)
        return;
      new SwingWorker<BufferedImage, Void>() {
        protected BufferedImage doInBackground() throws Exception {
          return ImageIO.read(AppConfig.resolve(path).toFile());
        }

        protected void done() {
          try {
            if (generation == ticket) {
              picture = get();
              loading = false;
              repaint();
            }
          } catch (Exception ignored) {
            if (generation == ticket) {
              loading = false;
              repaint();
            }
          }
        }
      }.execute();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
      var g = (Graphics2D) graphics.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setPaint(
          new GradientPaint(
              0, 0, SOFT, getWidth(), getHeight(), new Color(230, 234, 246)));
      g.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
      g.clip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
      if (picture != null) {
        double r = Math.max(
            (double) getWidth() / picture.getWidth(),
            (double) getHeight() / picture.getHeight());
        int w = (int) (picture.getWidth() * r), h = (int) (picture.getHeight() * r);
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(picture, (getWidth() - w) / 2, (int) ((getHeight() - h) * .20), w, h, null);
      } else {
        g.setColor(MUTED);
        g.setFont(UIManager.getFont("Label.font"));
        String text = loading ? "Cargando foto…" : "Sin fotografía";
        if (getWidth() >= 130)
          g.drawString(text, (getWidth() - g.getFontMetrics().stringWidth(text)) / 2,
              getHeight() / 2);
      }
      g.dispose();
    }
  }

  public abstract static class Screen extends JPanel {
    protected final AppFrame app;
    protected final JLabel status = new JLabel(" ");
    protected boolean active = true;
    private int epoch = 0;

    public Screen(AppFrame app) {
      super(new BorderLayout(0, 10));
      this.app = app;
      setBackground(BG);
      setBorder(new EmptyBorder(16, 20, 12, 20));
      status.setForeground(MUTED);
      add(status, BorderLayout.SOUTH);
    }

    public void onShow() {
      active = true;
    }

    public void onHide() {
      active = false;
      epoch++;
    }

    public <T> void task(Callable<T> job, Consumer<T> success) {
      task(job, success, null);
    }

    public <T> void task(Callable<T> job, Consumer<T> success, Consumer<Throwable> failure) {
      int ticket = epoch;
      status.setText("Un momento…");
      new SwingWorker<T, Void>() {
        protected T doInBackground() throws Exception {
          return job.call();
        }

        protected void done() {
          if (!active || epoch != ticket)
            return;
          try {
            T value = get();
            status.setText(" ");
            success.accept(value);
          } catch (Exception e) {
            status.setText("No se pudo completar");
            if (failure != null)
              failure.accept(e);
            else
              JOptionPane.showMessageDialog(
                  app, Ui.error(e), "PoliTinder", JOptionPane.WARNING_MESSAGE);
          }
        }
      }.execute();
    }
  }
}
