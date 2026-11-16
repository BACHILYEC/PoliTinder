package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;
import politinder.Models.Choice;

public final class ReportsView extends Ui.Screen {
  private final JComboBox<Reports.Report> report = new JComboBox<>();
  private final JComboBox<Choice> majors = new JComboBox<>(), faculties = new JComboBox<>();
  private final JSpinner min = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1)),
      max = new JSpinner(new SpinnerNumberModel(25, 0, 120, 1));
  private final JTextField cutoff = new JTextField(LocalDateTime.now(ZONE).format(Ui.DATE));
  private final JTextArea explanation = Ui.text(""), sql = new JTextArea();
  private final JTable table = new JTable();
  private final JPanel ages = Ui.column(),
      majorFilter = Ui.column(),
      facultyFilter = Ui.column(),
      timeFilter = Ui.column();
  private final JButton run;

  public ReportsView(AppFrame app) {
    super(app);
    var top = Ui.column();
    Ui.add(top, Ui.label("Los datos cuentan historias", 21, true));
    Ui.field(top, "Reporte", report);
    Ui.add(top, explanation);
    Ui.field(ages, "Edad mínima / máxima", Ui.row(min, max));
    Ui.field(majorFilter, "Carrera", majors);
    Ui.field(facultyFilter, "Facultad", faculties);
    Ui.field(timeFilter, "Corte · AAAA-MM-DD HH:mm", cutoff);
    top.add(ages);
    top.add(majorFilter);
    top.add(facultyFilter);
    top.add(timeFilter);
    run = Ui.primary("Ejecutar reporte", this::execute);
    run.setEnabled(false);
    Ui.add(top, run);
    Ui.add(top, Ui.button("Recargar consultas", this::reloadReports));
    sql.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    sql.setEditable(false);
    sql.setRows(7);
    var code = new JPanel(new BorderLayout(0, 5));
    code.add(Ui.label("SQL · parámetros visibles en los filtros", 11, true), BorderLayout.NORTH);
    code.add(new JScrollPane(sql));
    var lower = new JSplitPane(JSplitPane.VERTICAL_SPLIT, code, new JScrollPane(table));
    lower.setResizeWeight(.35);
    lower.setDividerLocation(.35);
    code.setMinimumSize(new Dimension(0, 50));
    lower.getBottomComponent().setMinimumSize(new Dimension(0, 80));
    var split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Ui.scroll(top), lower);
    split.setResizeWeight(.43);
    split.setDividerLocation(.43);
    split.getTopComponent().setMinimumSize(new Dimension(0, 80));
    split.getBottomComponent().setMinimumSize(new Dimension(0, 100));
    add(split, BorderLayout.CENTER);
    report.addActionListener(e -> selected());
    task(
        () ->
            new Object[] {
              Reports.load(), app.people.choices("allMajor", 0), app.people.choices("faculty", 0)
            },
        this::loaded);
    SwingUtilities.invokeLater(
        () -> {
          split.setDividerLocation(.43);
          lower.setDividerLocation(.38);
        });
  }

  @SuppressWarnings("unchecked")
  private void loaded(Object[] data) {
    for (var m : (java.util.List<Choice>) data[1]) majors.addItem(m);
    for (var f : (java.util.List<Choice>) data[2]) faculties.addItem(f);
    ProfileView.select(majors, 1);
    ProfileView.select(faculties, 1);
    for (var r : (java.util.List<Reports.Report>) data[0]) report.addItem(r);
    run.setEnabled(report.getItemCount() > 0);
    selected();
  }

  private void reloadReports() {
    task(Reports::load, items -> {
      report.removeAllItems();
      for (var item : items) report.addItem(item);
      run.setEnabled(!items.isEmpty());
      selected();
    });
  }

  private void selected() {
    var r = (Reports.Report) report.getSelectedItem();
    if (r == null) {
      explanation.setText("Consultas pendientes: se incorporarán las nueve queries definitivas del grupo.");
      sql.setText("");
      ages.setVisible(false); majorFilter.setVisible(false);
      facultyFilter.setVisible(false); timeFilter.setVisible(false);
      table.setModel(new javax.swing.table.DefaultTableModel());
      run.setEnabled(false);
      status.setText("Sin reportes configurados");
      revalidate();
      return;
    }
    explanation.setText(r.explanation());
    sql.setText(r.sql());
    sql.setCaretPosition(0);
    ages.setVisible(r.sql().contains("@edad_min") || r.sql().contains("@edad_max"));
    majorFilter.setVisible(r.sql().contains("@carrera"));
    facultyFilter.setVisible(r.sql().contains("@facultad"));
    timeFilter.setVisible(r.sql().contains("@corte"));
    revalidate();
  }

  private void execute() {
    var r = (Reports.Report) report.getSelectedItem();
    if (r == null) return;
    try {
      if (ages.isVisible() && (int) min.getValue() > (int) max.getValue())
        throw new IllegalArgumentException("La edad mínima no puede superar la máxima.");
      Map<String, Object> args = new HashMap<>();
      args.put("edad_min", min.getValue());
      args.put("edad_max", max.getValue());
      if (majors.getSelectedItem() != null)
        args.put("carrera", ((Choice) majors.getSelectedItem()).id());
      if (faculties.getSelectedItem() != null)
        args.put("facultad", ((Choice) faculties.getSelectedItem()).id());
      if (timeFilter.isVisible()) args.put("corte", Timestamp.valueOf(LocalDateTime.parse(cutoff.getText().strip(), Ui.DATE)));
      run.setEnabled(false);
      report.setEnabled(false);
      long started = System.nanoTime();
      task(
          () -> Reports.run(app.db, r, args),
          result -> {
            Tables.show(table, result);
            run.setEnabled(true);
            report.setEnabled(true);
            status.setText(
                result.rows().size()
                    + " filas · "
                    + ((System.nanoTime() - started) / 1_000_000)
                    + " ms");
          },
          e -> {
            run.setEnabled(true);
            report.setEnabled(true);
            JOptionPane.showMessageDialog(app, Ui.error(e));
          });
    } catch (Exception e) {
      JOptionPane.showMessageDialog(app, Ui.error(e));
    }
  }
}
