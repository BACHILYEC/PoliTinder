package politinder;

import java.sql.*;
import java.util.*;

public final class Db {
  public final AppConfig config;

  public Db(AppConfig config) {
    this.config = config;
  }

  public Connection open() throws SQLException {
    return open(true);
  }

  public Connection open(boolean database) throws SQLException {
    Connection c = DriverManager.getConnection(config.url(database), config.user, config.password);
    try (var st = c.createStatement()) {
      st.execute("SET time_zone='-05:00'");
    } catch (SQLException e) {
      c.close();
      throw e;
    }
    return c;
  }

  @FunctionalInterface
  public interface Work<T> {
    T run(Connection c) throws Exception;
  }

  public <T> T tx(Work<T> work) throws Exception {
    try (Connection c = open()) {
      c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      c.setAutoCommit(false);
      try {
        T result = work.run(c);
        c.commit();
        return result;
      } catch (Exception e) {
        c.rollback();
        throw e;
      }
    }
  }

  public static PreparedStatement statement(Connection c, String sql, Object... args)
      throws SQLException {
    PreparedStatement st = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    for (int i = 0; i < args.length; i++) st.setObject(i + 1, args[i]);
    return st;
  }

  public static int update(Connection c, String sql, Object... args) throws SQLException {
    try (var st = statement(c, sql, args)) {
      return st.executeUpdate();
    }
  }

  public static int insert(Connection c, String sql, Object... args) throws SQLException {
    try (var st = statement(c, sql, args)) {
      st.executeUpdate();
      try (var rs = st.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    throw new SQLException("No se obtuvo el identificador creado.");
  }

  public static List<Map<String, Object>> rows(Connection c, String sql, Object... args)
      throws SQLException {
    try (var st = statement(c, sql, args);
        var rs = st.executeQuery()) {
      List<Map<String, Object>> list = new ArrayList<>();
      var md = rs.getMetaData();
      while (rs.next()) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= md.getColumnCount(); i++)
          row.put(md.getColumnLabel(i), rs.getObject(i));
        list.add(row);
      }
      return list;
    }
  }

  public static long number(Connection c, String sql, Object... args) throws SQLException {
    try (var st = statement(c, sql, args);
        var rs = st.executeQuery()) {
      return rs.next() ? rs.getLong(1) : 0;
    }
  }
}
