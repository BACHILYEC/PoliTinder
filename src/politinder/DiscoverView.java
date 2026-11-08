package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import politinder.Models.Choice;

public final class DiscoverView extends Ui.Screen {
  private final JPanel body = Ui.column();
  private final JScrollPane bodyScroll = Ui.scroll(body);
  private final Ui.Portrait portrait = new Ui.Portrait();
  private final JTextArea heading = Ui.heading("Buscando conexiones…", 23), career = Ui.text(" ");
  private final JLabel counter = Ui.label(" ", 12, false);
  private final JTextArea biography = Ui.text("");
  private final JButton pass, like;
  private final JSpinner min = new JSpinner(new SpinnerNumberModel(18, 18, 120, 1)),
      max = new JSpinner(new SpinnerNumberModel(99, 18, 120, 1));
  private final JComboBox<Choice> gender = new JComboBox<>();
  private User candidate;
  private List<Photo> photos = List.of();
  private int photoIndex;

  public DiscoverView(AppFrame app) {
    super(app);
    var top = Ui.column();
    Ui.add(top, Ui.heading("Descubre tu próxima historia", 21));
    min.setValue(app.minAge);
    max.setValue(app.maxAge);
    gender.addItem(new Choice(0, "Todos los géneros"));
    var filter = Ui.column();
    Ui.field(filter, "Desde / hasta (años)", Ui.row(min, max));
    Ui.field(filter, "Mostrar", gender);
    Ui.add(
        filter,
        Ui.button(
            "Aplicar filtros",
            () -> {
              app.minAge = (int) min.getValue();
              app.maxAge = (int) max.getValue();
              app.filterGender = ((Choice) gender.getSelectedItem()).id();
              load();
            }));
    var showFilters = new JToggleButton("Filtros");
    filter.setVisible(false);
    showFilters.addActionListener(
        e -> {
          filter.setVisible(showFilters.isSelected());
          revalidate();
        });
    Ui.add(top, showFilters);
    top.add(filter);
    add(top, BorderLayout.NORTH);
    portrait.setPreferredSize(new Dimension(300, 310));
    Ui.add(body, portrait);
    Ui.add(
        body,
        Ui.row(
            Ui.button("‹ Foto", () -> picture(-1)),
            counter,
            Ui.button("Foto ›", () -> picture(1))));
    Ui.add(body, heading);
    Ui.add(body, career);
    Ui.add(body, biography);
    pass = Ui.button("✕  Pasar", () -> decision(false));
    like = Ui.primary("♥  Me gusta", () -> decision(true));
    var actions = new JPanel(new BorderLayout());
    actions.setOpaque(false);
    actions.add(Ui.row(pass, like), BorderLayout.CENTER);
    actions.add(status, BorderLayout.SOUTH);
    add(actions, BorderLayout.SOUTH);
    add(bodyScroll, BorderLayout.CENTER);
    MouseAdapter swipe =
        new MouseAdapter() {
          int x;

          public void mousePressed(MouseEvent e) {
            x = e.getX();
          }

          public void mouseReleased(MouseEvent e) {
            int dx = e.getX() - x;
            if (Math.abs(dx) > portrait.getWidth() * .25) decision(dx > 0);
          }
        };
    portrait.addMouseListener(swipe);
    addComponentListener(
        new ComponentAdapter() {
          public void componentResized(ComponentEvent e) {
            portrait.setPreferredSize(new Dimension(300, Math.max(120, (int) (getHeight() * .46))));
            portrait.setMinimumSize(new Dimension(0, portrait.getPreferredSize().height));
            portrait.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, portrait.getPreferredSize().height));
            body.revalidate();
          }
        });
    task(
        () -> app.people.choices("gender", 0),
        choices -> {
          choices.forEach(gender::addItem);
          ProfileView.select(gender, app.filterGender);
          load();
        });
  }

  private void load() {
    candidate = null;
    pass.setEnabled(false);
    like.setEnabled(false);
    int me = app.me.id();
    var skipped = Set.copyOf(app.skipped);
    int lo = app.minAge, hi = app.maxAge, g = app.filterGender;
    task(
        () -> {
          User own = app.people.get(me);
          if (!own.status().equals("ACTIVE"))
            throw new IllegalArgumentException("Tu perfil está pausado. Actívalo desde Mi perfil.");
          if (app.photos.list(me).isEmpty())
            throw new IllegalArgumentException("Añade una fotografía en Mi perfil para empezar.");
          var people = app.people.discover(me, lo, hi, g, skipped);
          if (people.isEmpty()) return null;
          User u = people.getFirst();
          return new Object[] {u, app.photos.list(u.id())};
        },
        this::display);
  }

  @SuppressWarnings("unchecked")
  private void display(Object[] data) {
    if (data == null) {
      heading.setText("Por ahora, eso es todo");
      career.setText("Prueba otros filtros o vuelve más tarde.");
      biography.setText("Los perfiles que pasaste volverán a aparecer en una nueva sesión.");
      counter.setText("");
      portrait.load(null);
      return;
    }
    candidate = (User) data[0];
    photos = (List<Photo>) data[1];
    photoIndex = 0;
    picture(0);
    heading.setText(candidate.name() + ", " + candidate.age());
    career.setText(candidate.majorName());
    career.setToolTipText(candidate.faculty());
    biography.setText(candidate.biography());
    pass.setEnabled(true);
    like.setEnabled(true);
    SwingUtilities.invokeLater(() -> bodyScroll.getViewport().setViewPosition(new Point(0, 0)));
  }

  private void picture(int move) {
    if (photos.isEmpty()) {
      portrait.load(null);
      return;
    }
    photoIndex = Math.floorMod(photoIndex + move, photos.size());
    portrait.load(photos.get(photoIndex).path());
    counter.setText((photoIndex + 1) + " / " + photos.size());
  }

  private void decision(boolean positive) {
    if (candidate == null || !like.isEnabled()) return;
    int other = candidate.id();
    String name = candidate.name();
    if (!positive) {
      app.skipped.add(other);
      load();
      return;
    }
    pass.setEnabled(false);
    like.setEnabled(false);
    int me = app.me.id();
    task(
        () -> app.people.like(me, other),
        result -> {
          if (result.match())
            JOptionPane.showMessageDialog(
                app,
                "¡Es un match con " + name + "!\nYa pueden conversar en Mis matches.",
                "♥ ¡Conectaron!",
                JOptionPane.INFORMATION_MESSAGE);
          load();
        },
        e -> {
          pass.setEnabled(true);
          like.setEnabled(true);
          JOptionPane.showMessageDialog(app, Ui.error(e));
        });
  }
}
