package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class ChatViews {
  public static final class MatchesView extends Ui.Screen {
    private final DefaultListModel<Match> model = new DefaultListModel<>();
    private final JList<Match> list = new JList<>(model);
    private final javax.swing.Timer timer;
    private boolean loading;

    public MatchesView(AppFrame app) {
      super(app);
      var p = Ui.column();
      Ui.add(p, Ui.label("El interés fue mutuo ♥", 22, true));
      Ui.add(p, Ui.text("Cada conexión empieza con dos likes. Elige a alguien para conversar."));
      add(p, BorderLayout.NORTH);
      list.setFixedCellHeight(64);
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setCellRenderer(
          new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                JList<?> l, Object v, int i, boolean s, boolean f) {
              JLabel label = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
              label.setBorder(new EmptyBorder(8, 10, 8, 10));
              return label;
            }
          });
      var center = new JPanel(new BorderLayout(0, 10));
      center.add(new JScrollPane(list));
      center.add(
          Ui.primary(
              "Abrir conversación",
              () -> {
                if (list.getSelectedValue() != null) app.chat(list.getSelectedValue());
              }),
          BorderLayout.SOUTH);
      add(center, BorderLayout.CENTER);
      list.addMouseListener(
          new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
              if (e.getClickCount() == 2 && list.getSelectedValue() != null)
                app.chat(list.getSelectedValue());
            }
          });
      timer = new javax.swing.Timer(2000, e -> refresh());
      timer.start();
      refresh();
    }

    private void refresh() {
      if (loading) return;
      loading = true;
      int me = app.me.id();
      task(
          () -> app.people.matches(me),
          items -> {
            loading = false;
            if (!items.equals(java.util.Collections.list(model.elements()))) {
              Match selected = list.getSelectedValue();
              model.clear();
              items.forEach(model::addElement);
              if (selected != null) list.setSelectedValue(selected, true);
              if (list.getSelectedIndex() < 0 && !items.isEmpty()) list.setSelectedIndex(0);
            }
            status.setText(
                items.isEmpty()
                    ? "Todavía no tienes matches. Sigue descubriendo."
                    : items.size() + " conexiones");
          },
          e -> {
            loading = false;
            timer.stop();
            status.setText("Sin conexión. Vuelve a abrir Mis matches.");
          });
    }

    @Override
    public void onHide() {
      super.onHide();
      timer.stop();
    }
  }

  public static final class ConversationView extends Ui.Screen {
    private final Match match;
    private final JPanel messages = Ui.column();
    private final JScrollPane scroll = Ui.scroll(messages);
    private final JTextArea input = new JTextArea(3, 20);
    private final JButton send;
    private final javax.swing.Timer timer;
    private boolean loading;
    private int last = -1;

    public ConversationView(AppFrame app, Match match) {
      super(app);
      this.match = match;
      var top = Ui.column();
      Ui.add(top, Ui.label(match.name(), 23, true));
      Ui.add(top, Ui.text(match.career()));
      Ui.add(top, Ui.button("Agendar una PoliDate", () -> schedule(app, match, () -> {})));
      add(top, BorderLayout.NORTH);
      add(scroll, BorderLayout.CENTER);
      var bottom = new JPanel(new BorderLayout(6, 6));
      input.setLineWrap(true);
      input.setWrapStyleWord(true);
      bottom.add(new JScrollPane(input), BorderLayout.CENTER);
      send = Ui.primary("Enviar", this::send);
      bottom.add(send, BorderLayout.EAST);
      bottom.add(status, BorderLayout.SOUTH);
      add(bottom, BorderLayout.SOUTH);
      input.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "send");
      input
          .getActionMap()
          .put(
              "send",
              new AbstractAction() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                  send();
                }
              });
      timer = new javax.swing.Timer(2000, e -> refresh());
      timer.start();
      refresh();
    }

    private void refresh() {
      if (loading) return;
      loading = true;
      int me = app.me.id();
      task(
          () -> app.chats.messages(me, match.chat()),
          rows -> {
            loading = false;
            int latest = rows.isEmpty() ? 0 : rows.getLast().id();
            if (latest == last) return;
            last = latest;
            boolean atBottom =
                scroll.getVerticalScrollBar().getValue()
                        + scroll.getVerticalScrollBar().getVisibleAmount()
                    >= scroll.getVerticalScrollBar().getMaximum() - 50;
            messages.removeAll();
            if (rows.isEmpty())
              Ui.add(
                  messages,
                  Ui.text("Un hola puede ser el inicio de algo. Escribe el primer mensaje."));
            for (Message msg : rows) {
              var bubble = Ui.column();
              bubble.setOpaque(true);
              bubble.setBackground(msg.sender() == me ? Ui.SOFT : Ui.SURFACE);
              bubble.setBorder(new EmptyBorder(10, 12, 10, 12));
              var text = new JTextArea(msg.content());
              text.setEditable(false);
              text.setLineWrap(true);
              text.setWrapStyleWord(true);
              text.setOpaque(false);
              text.setColumns(22);
              bubble.add(text);
              bubble.add(
                  Ui.label(
                      msg.date().format(DateTimeFormatter.ofPattern("dd MMM · HH:mm")), 10, false));
              var wrapper = new JPanel(new BorderLayout());
              wrapper.setOpaque(false);
              wrapper.setBorder(
                  new EmptyBorder(4, msg.sender() == me ? 35 : 0, 4, msg.sender() == me ? 0 : 35));
              wrapper.add(bubble);
              messages.add(wrapper);
            }
            messages.revalidate();
            messages.repaint();
            if (atBottom || rows.size() < 2)
              SwingUtilities.invokeLater(
                  () ->
                      scroll
                          .getVerticalScrollBar()
                          .setValue(scroll.getVerticalScrollBar().getMaximum()));
          },
          e -> {
            loading = false;
            timer.stop();
            status.setText("No se pudo actualizar. Vuelve a abrir el chat.");
          });
    }

    private void send() {
      if (!send.isEnabled()) return;
      String text = input.getText();
      int me = app.me.id();
      send.setEnabled(false);
      task(
          () -> {
            app.chats.send(me, match.chat(), text);
            return null;
          },
          v -> {
            input.setText("");
            send.setEnabled(true);
            timer.start();
            refresh();
          },
          e -> {
            send.setEnabled(true);
            JOptionPane.showMessageDialog(app, Ui.error(e));
          });
    }

    @Override
    public void onHide() {
      super.onHide();
      timer.stop();
    }
  }

  public static final class DatesView extends Ui.Screen {
    private final JTable table = new JTable();
    private final JComboBox<Match> matches = new JComboBox<>();

    public DatesView(AppFrame app) {
      super(app);
      var top = Ui.column();
      Ui.add(top, Ui.label("Un plan para encontrarse", 22, true));
      Ui.add(
          top,
          Ui.text(
              "Encuentros compartidos con tus matches. Una fecha pasada no confirma asistencia."));
      Ui.field(top, "Con quién", matches);
      Ui.add(
          top,
          Ui.button(
              "Agendar PoliDate",
              () -> {
                var m = (Match) matches.getSelectedItem();
                if (m != null) schedule(app, m, this::refresh);
              }));
      Ui.add(top, Ui.button("Actualizar", this::refresh));
      add(top, BorderLayout.NORTH);
      add(new JScrollPane(table), BorderLayout.CENTER);
      int me = app.me.id();
      task(() -> app.people.matches(me), items -> items.forEach(matches::addItem));
      refresh();
    }

    private void refresh() {
      int me = app.me.id();
      task(
          () -> app.chats.dates(me),
          r -> {
            Tables.show(table, r);
            status.setText(r.rows().size() + " citas");
          });
    }
  }

  private static void schedule(AppFrame app, Match match, Runnable completed) {
    var p = Ui.column();
    var place = new JTextField("Cafetería de la EPN");
    var date = new JTextField(LocalDateTime.now(ZONE).plusDays(1).withSecond(0).format(Ui.DATE));
    Ui.field(p, "Lugar", place);
    Ui.field(p, "Fecha y hora · AAAA-MM-DD HH:mm", date);
    Ui.add(p, Ui.text("El horario debe estar libre para ambos participantes."));
    if (JOptionPane.showConfirmDialog(
            app, p, "PoliDate con " + match.name(), JOptionPane.OK_CANCEL_OPTION)
        != JOptionPane.OK_OPTION) return;
    try {
      LocalDateTime when = LocalDateTime.parse(date.getText().strip(), Ui.DATE);
      String where = place.getText();
      int me = app.me.id();
      new SwingWorker<Void, Void>() {
        protected Void doInBackground() throws Exception {
          app.chats.schedule(me, match.id(), where, when);
          return null;
        }

        protected void done() {
          try {
            get();
            JOptionPane.showMessageDialog(
                app, "PoliDate agendada. Ambos pueden verla en PoliDates.");
            completed.run();
          } catch (Exception e) {
            JOptionPane.showMessageDialog(app, Ui.error(e));
          }
        }
      }.execute();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(app, "Revisa el formato de fecha: AAAA-MM-DD HH:mm");
    }
  }
}
