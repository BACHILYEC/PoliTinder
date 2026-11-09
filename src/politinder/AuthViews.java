package politinder;

import java.awt.*;
import javax.swing.*;

public final class AuthViews {
  public static final class ConnectionView extends Ui.Screen {
    private final JTextField host, port, user;
    private final JPasswordField password;
    private final JButton connect, initialize, test;

    public ConnectionView(AppFrame app) {
      super(app);
      var p = Ui.column();
      Ui.add(p, Ui.label("Tu Poli, nuevas conexiones.", 23, true));
      Ui.add(
          p,
          Ui.text(
              "Conecta con MySQL en este equipo. La contraseña se mantiene solo durante esta"
                  + " ejecución."));
      host = new JTextField(app.config.host);
      port = new JTextField("" + app.config.port);
      user = new JTextField(app.config.user);
      password = new JPasswordField(app.config.password);
      Ui.field(p, "Servidor", host);
      Ui.field(p, "Puerto", port);
      Ui.field(p, "Usuario MySQL", user);
      Ui.field(p, "Contraseña MySQL", password);
      connect = Ui.primary("Conectar", () -> execute("connect"));
      initialize = Ui.button("Crear base + datos demo", () -> execute("initialize"));
      test = Ui.button("Probar servidor", () -> execute("test"));
      Ui.add(p, connect);
      Ui.add(p, test);
      Ui.add(p, initialize);
      Ui.add(
          p,
          Ui.text(
              "La creación inicial solo funciona si no existe la base politinder. También puedes"
                  + " ejecutar los archivos DDL y DML desde Workbench."));
      add(Ui.scroll(p), BorderLayout.CENTER);
      password.addActionListener(e -> execute("connect"));
    }

    private void execute(String mode) {
      try {
        app.config.host = host.getText().strip();
        app.config.port = Integer.parseInt(port.getText().strip());
        app.config.user = user.getText().strip();
        app.config.password = new String(password.getPassword());
        app.config.url(false);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(app, "Revisa el servidor y el puerto.");
        return;
      }
      connect.setEnabled(false);
      initialize.setEnabled(false);
      test.setEnabled(false);
      task(
          () -> {
            String info = Installer.check(app.db);
            if (mode.equals("initialize")) Installer.initialize(app.db);
            if (!mode.equals("test"))
              try (var c = app.db.open()) {
                Db.number(c, "SELECT COUNT(*) FROM `USER`");
              }
            if (!mode.equals("test")) DemoProfiles.refresh(app.db);
            app.config.save();
            return info;
          },
          info -> {
            connect.setEnabled(true);
            initialize.setEnabled(true);
            test.setEnabled(true);
            if (mode.equals("test")) status.setText(info);
            else app.connected();
          },
          e -> {
            connect.setEnabled(true);
            initialize.setEnabled(true);
            test.setEnabled(true);
            JOptionPane.showMessageDialog(
                app, Ui.error(e), "Conexión MySQL", JOptionPane.WARNING_MESSAGE);
          });
    }
  }

  public static final class LoginView extends Ui.Screen {
    public LoginView(AppFrame app) {
      super(app);
      var p = Ui.column();
      Ui.add(p, Ui.label("Conoce a alguien de tu mundo.", 23, true));
      Ui.add(p, Ui.text("Un café, una conversación, una nueva historia en la EPN."));
      var art = new Ui.Portrait();
      art.setPreferredSize(new Dimension(300, 190));
      art.load("photos/demo-real/01/01.png");
      Ui.add(p, art);
      var email = new JTextField();
      var password = new JPasswordField();
      Ui.field(p, "Correo", email);
      Ui.field(p, "Contraseña", password);
      JButton login = new JButton("Entrar");
      login.setBackground(Ui.CORAL);
      login.setForeground(Color.WHITE);
      Runnable enter =
          () -> {
            String mail = email.getText();
            char[] secret = password.getPassword();
            login.setEnabled(false);
            task(
                () -> app.people.login(mail, secret),
                app::signedIn,
                e -> {
                  login.setEnabled(true);
                  JOptionPane.showMessageDialog(app, Ui.error(e));
                });
          };
      login.addActionListener(e -> enter.run());
      password.addActionListener(e -> enter.run());
      Ui.add(p, login);
      Ui.add(p, Ui.button("Crear mi cuenta", () -> app.profile(true)));
      Ui.add(
          p,
          Ui.text(
              "Demo: ana@politinder.test\n"
                  + "Contraseña: PoliDemo25!\n"
                  + "Reportes y consola disponibles en el menú ☰."));
      add(Ui.scroll(p), BorderLayout.CENTER);
    }
  }
}
