package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

/** Consola académica libre. Su conexión no se comparte con los servicios. */
public final class ConsoleView extends Ui.Screen {
  private final JTextArea editor =
      new JTextArea("SELECT idUser, name, mail FROM `USER` ORDER BY name;", 10, 24);
  private final JTabbedPane results = new JTabbedPane();
  private final JButton execute, cancel;
  private final JSpinner limit = new JSpinner(new SpinnerNumberModel(1000, 0, 1000000, 100));
  private Connection connection;
  private volatile Statement currentStatement;
  private final AtomicBoolean canceled = new AtomicBoolean();
  private boolean busy;
  private volatile boolean closed;

  private record Outcome(java.util.List<Object> items, long ms) {}

  public ConsoleView(AppFrame app) {
    super(app);
    var top = Ui.column();
    Ui.add(top, Ui.label("Tu espacio para consultar", 22, true));
    Ui.add(
        top,
        Ui.text(
            "Ejecuta una sentencia por vez. Las operaciones de escritura se aplican a esta base de"
                + " datos."));
    execute = Ui.primary("Ejecutar", this::run);
    cancel = Ui.button("Cancelar", this::cancel);
    cancel.setEnabled(false);
    Ui.add(
        top,
        Ui.row(
            execute,
            cancel,
            Ui.button(
                "Limpiar",
                () -> {
                  editor.setText("");
                  results.removeAll();
                })));
    Ui.field(top, "Máximo de filas · 0 = todas", limit);
    add(top, BorderLayout.NORTH);
    editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    editor.setTabSize(4);
    results.addTab(
        "Resultados",
        Ui.scroll(
            Ui.text(
                "Escribe SQL y pulsa Ejecutar o Ctrl+Enter. Aquí aparecerán las tablas o los"
                    + " mensajes de MySQL.")));
    var split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(editor), results);
    split.setResizeWeight(.48);
    split.getTopComponent().setMinimumSize(new Dimension(0, 80));
    split.getBottomComponent().setMinimumSize(new Dimension(0, 80));
    add(split, BorderLayout.CENTER);
    SwingUtilities.invokeLater(() -> split.setDividerLocation(.48));
    editor.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "execute");
    editor
        .getActionMap()
        .put(
            "execute",
            new AbstractAction() {
              public void actionPerformed(java.awt.event.ActionEvent e) {
                run();
              }
            });
  }

  private void run() {
    if (busy || closed) return;
    String sql = editor.getSelectedText();
    if (sql == null || sql.isBlank()) sql = editor.getText();
    try {
      var statements = SqlScripts.split(sql);
      if (statements.size() != 1)
        throw new IllegalArgumentException(
            "Ejecuta una sentencia por vez. Puedes seleccionar solo la sentencia que deseas"
                + " ejecutar.");
      sql = statements.getFirst();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(app, Ui.error(e));
      return;
    }
    final String command = sql;
    int cap = (int) limit.getValue();
    busy = true;
    canceled.set(false);
    execute.setEnabled(false);
    cancel.setEnabled(true);
    status.setText("Ejecutando…");
    new SwingWorker<Outcome, Void>() {
      protected Outcome doInBackground() throws Exception {
        long start = System.nanoTime();
        java.util.List<Object> items = new ArrayList<>();
        if (connection == null || connection.isClosed()) {
          connection = app.db.open();
        }
        try (Statement st = connection.createStatement()) {
          currentStatement = st;
          if (canceled.get()) throw new SQLException("Ejecución cancelada.");
          boolean result = st.execute(command);
          while (true) {
            if (result) {
              try (var rs = st.getResultSet()) {
                items.add(Tables.read(rs, cap));
              }
            } else {
              int count = st.getUpdateCount();
              if (count == -1) break;
              items.add("Operación completada · " + count + " filas afectadas.");
            }
            if (canceled.get()) throw new SQLException("Ejecución cancelada.");
            result = st.getMoreResults();
          }
          if (items.isEmpty()) items.add("Sentencia completada.");
        } finally {
          currentStatement = null;
        }
        return new Outcome(items, (System.nanoTime() - start) / 1_000_000);
      }

      protected void done() {
        busy = false;
        execute.setEnabled(true);
        cancel.setEnabled(false);
        if (closed) {
          disconnect();
          return;
        }
        try {
          var output = get();
          results.removeAll();
          int index = 1;
          for (Object item : output.items()) {
            if (item instanceof TableData data) {
              var table = new JTable();
              Tables.show(table, data);
              var p = new JPanel(new BorderLayout());
              p.add(new JScrollPane(table));
              p.add(
                  Ui.label(
                      data.rows().size()
                          + " filas"
                          + (data.truncated()
                              ? " · resultado limitado; aumenta Máximo de filas"
                              : ""),
                      11,
                      false),
                  BorderLayout.SOUTH);
              results.addTab("Tabla " + index++, p);
            } else {
              JTextArea text = Ui.text(item.toString());
              results.addTab("Mensaje " + index++, new JScrollPane(text));
            }
          }
          status.setText("Completado · " + output.ms() + " ms");
        } catch (Exception e) {
          status.setText(canceled.get() ? "Cancelado" : "Error SQL");
          var text = Ui.text(Ui.error(e));
          results.addTab("Error", new JScrollPane(text));
          results.setSelectedIndex(results.getTabCount() - 1);
        }
      }
    }.execute();
  }

  private void cancel() {
    canceled.set(true);
    Statement st = currentStatement;
    if (st != null)
      new Thread(
              () -> {
                try {
                  st.cancel();
                } catch (SQLException ignored) {
                }
              },
              "cancel-sql")
          .start();
  }

  public void closeSession() {
    closed = true;
    cancel();
    if (!busy) disconnect();
  }

  private void disconnect() {
    new Thread(
            () -> {
              Connection c = connection;
              connection = null;
              if (c != null)
                try (c) {
                  try (var st = c.createStatement()) {
                    st.execute("ROLLBACK");
                  } catch (SQLException ignored) {
                  }
                } catch (SQLException ignored) {
                }
            },
            "close-sql")
        .start();
  }
}
