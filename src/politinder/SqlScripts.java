package politinder;

import java.util.*;

/** Divide scripts simples respetando cadenas, identificadores y comentarios SQL. */
public final class SqlScripts {
  public static List<String> split(String text) {
    List<String> out = new ArrayList<>();
    StringBuilder part = new StringBuilder();
    char quote = 0;
    boolean line = false, block = false;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i), next = i + 1 < text.length() ? text.charAt(i + 1) : '\0';
      if (line) {
        if (c == '\n') {
          line = false;
          part.append('\n');
        }
        continue;
      }
      if (block) {
        if (c == '*' && next == '/') {
          block = false;
          i++;
          part.append(' ');
        }
        continue;
      }
      if (quote != 0) {
        part.append(c);
        if (c == '\\' && quote != '`' && i + 1 < text.length()) {
          part.append(text.charAt(++i));
          continue;
        }
        if (c == quote) {
          if (next == quote) {
            part.append(next);
            i++;
          } else quote = 0;
        }
        continue;
      }
      if (c == '#'
          || (c == '-'
              && next == '-'
              && (i + 2 == text.length() || Character.isWhitespace(text.charAt(i + 2))))) {
        line = true;
        if (c == '-') i++;
        continue;
      }
      if (c == '/' && next == '*') {
        block = true;
        i++;
        continue;
      }
      if (c == '\'' || c == '"' || c == '`') {
        quote = c;
        part.append(c);
        continue;
      }
      if (c == ';') {
        if (!part.toString().isBlank()) out.add(part.toString().strip());
        part.setLength(0);
      } else part.append(c);
    }
    if (quote != 0 || block)
      throw new IllegalArgumentException("Hay una cadena o comentario SQL sin cerrar.");
    if (!part.toString().isBlank()) out.add(part.toString().strip());
    return out;
  }
}
