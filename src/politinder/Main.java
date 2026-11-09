package politinder;

import javax.swing.*;

public final class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          Ui.installTheme();
          new AppFrame().setVisible(true);
        });
  }
}
