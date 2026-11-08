package politinder;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;

public final class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          FlatLightLaf.setup();
          UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
          UIManager.put("Component.arc", 14);
          UIManager.put("Button.arc", 18);
          UIManager.put("TextComponent.arc", 12);
          UIManager.put("Component.focusColor", Ui.CORAL);
          UIManager.put("Panel.background", Ui.BG);
          UIManager.put("ScrollPane.smoothScrolling", true);
          new AppFrame().setVisible(true);
        });
  }
}
