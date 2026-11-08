package politinder;

import static politinder.Models.*;

import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public final class Tables {
  public static TableData read(ResultSet rs, int limit) throws SQLException {
    var md = rs.getMetaData();
    List<String> cols = new ArrayList<>();
    List<List<Object>> rows = new ArrayList<>();
    for (int i = 1; i <= md.getColumnCount(); i++) cols.add(md.getColumnLabel(i));
    boolean truncated = false;
    while (rs.next()) {
      if (limit > 0 && rows.size() >= limit) {
        truncated = true;
        break;
      }
      List<Object> row = new ArrayList<>();
      for (int i = 1; i <= md.getColumnCount(); i++) {
        Object value = rs.getObject(i);
        if (value instanceof byte[] b) value = HexFormat.of().formatHex(b);
        row.add(value);
      }
      rows.add(row);
    }
    return new TableData(cols, rows, truncated);
  }

  public static void show(JTable table, TableData data) {
    table.setModel(
        new AbstractTableModel() {
          public int getRowCount() {
            return data.rows().size();
          }

          public int getColumnCount() {
            return data.columns().size();
          }

          public String getColumnName(int c) {
            return data.columns().get(c);
          }

          public Object getValueAt(int r, int c) {
            return data.rows().get(r).get(c);
          }

          public boolean isCellEditable(int r, int c) {
            return false;
          }
        });
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setAutoCreateRowSorter(true);
    table.setRowHeight(30);
    for (int c = 0; c < table.getColumnCount(); c++) {
      int width =
          Math.max(
              100, table.getFontMetrics(table.getFont()).stringWidth(table.getColumnName(c)) + 34);
      for (int r = 0; r < Math.min(60, table.getRowCount()); r++)
        width =
            Math.max(
                width,
                table
                        .getFontMetrics(table.getFont())
                        .stringWidth(String.valueOf(table.getValueAt(r, c)))
                    + 28);
      table.getColumnModel().getColumn(c).setPreferredWidth(Math.min(360, width));
    }
    table.setDefaultRenderer(
        Object.class,
        new DefaultTableCellRenderer() {
          @Override
          protected void setValue(Object v) {
            super.setValue(v == null ? "NULL" : v);
          }
        });
  }
}
