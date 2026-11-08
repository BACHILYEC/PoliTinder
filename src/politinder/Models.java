package politinder;

import java.time.*;
import java.util.*;

public final class Models {
  public static final ZoneId ZONE = ZoneId.of("America/Guayaquil");

  public record Choice(int id, String name) {
    @Override
    public String toString() {
      return name;
    }
  }

  public record User(
      int id,
      String name,
      String mail,
      String biography,
      LocalDate bornDate,
      int gender,
      int major,
      String majorName,
      String faculty,
      String status,
      String photo) {
    public int age() {
      return Period.between(bornDate, LocalDate.now(ZONE)).getYears();
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public record Photo(int id, String path, boolean main) {
    @Override
    public String toString() {
      return (main ? "★ Portada · " : "") + "Foto " + id;
    }
  }

  public record Match(int id, int chat, int otherId, String name, String career, String photo) {
    @Override
    public String toString() {
      return name + " · " + career;
    }
  }

  public record Message(int id, int sender, String name, String content, LocalDateTime date) {}

  public record LikeResult(boolean match, boolean duplicate) {}

  public record TableData(List<String> columns, List<List<Object>> rows, boolean truncated) {}
}
