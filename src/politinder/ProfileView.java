package politinder;

import static politinder.Models.*;

import java.awt.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import politinder.Models.Choice;

public final class ProfileView extends Ui.Screen {
  private final boolean registration;
  private final JTextField name = new JTextField(),
      mail = new JTextField(),
      born = new JTextField();
  private final JPasswordField password = new JPasswordField();
  private final JTextArea bio = new JTextArea(4, 20);
  private final JComboBox<Choice> gender = new JComboBox<>(),
      faculty = new JComboBox<>(),
      major = new JComboBox<>();
  private final JCheckBox visible = new JCheckBox("Mostrar mi perfil en Descubrir", true);
  private final JButton save;
  private final DefaultListModel<Photo> photoModel = new DefaultListModel<>();
  private final JList<Photo> photoList = new JList<>(photoModel);
  private final Ui.Portrait preview = new Ui.Portrait();
  private List<Choice> allMajors = List.of();
  private Map<Integer, Integer> majorFaculty = Map.of();
  private boolean populating;

  public ProfileView(AppFrame app, boolean registration) {
    super(app);
    this.registration = registration;
    var p = Ui.column();
    Ui.add(
        p, Ui.label(registration ? "Tu historia empieza aquí" : "Así te ven en la Poli", 22, true));
    Ui.field(p, "Nombre", name);
    Ui.field(p, "Correo", mail);
    if (registration) Ui.field(p, "Contraseña · mínimo 8 caracteres", password);
    Ui.field(p, "Nacimiento · AAAA-MM-DD", born);
    Ui.field(p, "Género", gender);
    Ui.field(p, "Facultad / escuela", faculty);
    Ui.field(p, "Carrera", major);
    bio.setLineWrap(true);
    bio.setWrapStyleWord(true);
    Ui.field(p, "Sobre ti · hasta 1000 caracteres", new JScrollPane(bio));
    if (!registration) Ui.add(p, visible);
    save = Ui.primary(registration ? "Crear cuenta" : "Guardar perfil", this::save);
    save.setEnabled(false);
    Ui.add(p, save);
    faculty.addActionListener(
        e -> {
          if (!populating) filterMajors(0);
        });
    if (!registration) {
      Ui.add(p, Ui.label("Mis fotografías", 20, true));
      Ui.add(p, Ui.text("Sube de 1 a 6 fotos. Elige una portada para aparecer en Descubrir."));
      preview.setPreferredSize(new Dimension(300, 220));
      Ui.add(p, preview);
      photoList.setVisibleRowCount(3);
      photoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      Ui.add(p, new JScrollPane(photoList));
      photoList.addListSelectionListener(
          e -> {
            var selected = photoList.getSelectedValue();
            if (selected != null) preview.load(selected.path());
          });
      Ui.add(p, Ui.button("Añadir foto", this::upload));
      Ui.add(p, Ui.button("Usar como portada", () -> photoAction(false)));
      Ui.add(p, Ui.button("Eliminar foto", () -> photoAction(true)));
      Ui.add(p, Ui.primary("Ir a Descubrir", app::discover));
    } else
      Ui.add(
          p,
          Ui.text(
              "Después de crear tu cuenta podrás añadir fotografías. Tu perfil será visible al"
                  + " tener al menos una."));
    add(Ui.scroll(p), BorderLayout.CENTER);
    task(
        () -> {
          List<Choice> genders = app.people.choices("gender", 0),
              faculties = app.people.choices("faculty", 0),
              majors = app.people.choices("allMajor", 0);
          Map<Integer, Integer> mapping = new HashMap<>();
          try (var c = app.db.open()) {
            for (var r : Db.rows(c, "SELECT idMajor,idFaculty FROM MAJOR"))
              mapping.put(
                  ((Number) r.get("idMajor")).intValue(), ((Number) r.get("idFaculty")).intValue());
          }
          return new Object[] {
            genders, faculties, majors, mapping, registration ? null : app.people.get(app.me.id())
          };
        },
        this::loaded);
  }

  @SuppressWarnings("unchecked")
  private void loaded(Object[] data) {
    populating = true;
    for (var x : (List<Choice>) data[0]) gender.addItem(x);
    for (var x : (List<Choice>) data[1]) faculty.addItem(x);
    allMajors = (List<Choice>) data[2];
    majorFaculty = (Map<Integer, Integer>) data[3];
    if (!registration) {
      User u = (User) data[4];
      app.me = u;
      name.setText(u.name());
      mail.setText(u.mail());
      born.setText(u.bornDate().toString());
      bio.setText(u.biography());
      visible.setSelected(u.status().equals("ACTIVE"));
      select(gender, u.gender());
      select(faculty, majorFaculty.getOrDefault(u.major(), 0));
      filterMajors(u.major());
      reloadPhotos();
    } else {
      born.setText(LocalDate.now(ZONE).minusYears(20).toString());
      filterMajors(0);
    }
    populating = false;
    save.setEnabled(true);
  }

  public static void select(JComboBox<Choice> combo, int id) {
    for (int i = 0; i < combo.getItemCount(); i++)
      if (combo.getItemAt(i).id() == id) {
        combo.setSelectedIndex(i);
        return;
      }
  }

  private void filterMajors(int selected) {
    major.removeAllItems();
    Choice f = (Choice) faculty.getSelectedItem();
    if (f != null)
      for (var m : allMajors)
        if (Objects.equals(majorFaculty.get(m.id()), f.id())) major.addItem(m);
    select(major, selected);
  }

  private void save() {
    try {
      String n = name.getText().strip(), email = mail.getText().strip(), b = bio.getText();
      LocalDate birth = LocalDate.parse(born.getText().strip());
      Choice g = (Choice) gender.getSelectedItem(), m = (Choice) major.getSelectedItem();
      if (g == null || m == null)
        throw new IllegalArgumentException("Selecciona género y carrera.");
      boolean activeProfile = visible.isSelected();
      char[] secret = password.getPassword();
      int userId = registration ? 0 : app.me.id();
      save.setEnabled(false);
      task(
          () ->
              registration
                  ? app.people.register(n, email, secret, birth, g.id(), m.id(), b)
                  : app.people.save(userId, n, email, birth, g.id(), m.id(), b, activeProfile),
          u -> {
            app.me = u;
            if (registration) app.profile(false);
            else {
              save.setEnabled(true);
              status.setText("Perfil guardado");
            }
          },
          e -> {
            save.setEnabled(true);
            JOptionPane.showMessageDialog(app, Ui.error(e));
          });
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          app, "Revisa la fecha (AAAA-MM-DD), el género y la carrera.\n" + Ui.error(e));
    }
  }

  private void reloadPhotos() {
    task(
        () -> app.photos.list(app.me.id()),
        photos -> {
          photoModel.clear();
          photos.forEach(photoModel::addElement);
          if (!photos.isEmpty()) photoList.setSelectedIndex(0);
          else preview.load(null);
        });
  }

  private void upload() {
    var chooser = new JFileChooser();
    chooser.setFileFilter(
        new FileNameExtensionFilter("Fotografías JPEG o PNG", "jpg", "jpeg", "png"));
    if (chooser.showOpenDialog(app) != JFileChooser.APPROVE_OPTION) return;
    Path selected = chooser.getSelectedFile().toPath();
    int me = app.me.id();
    task(
        () -> {
          app.photos.add(me, selected);
          return null;
        },
        v -> reloadPhotos());
  }

  private void photoAction(boolean delete) {
    Photo p = photoList.getSelectedValue();
    if (p == null) return;
    int me = app.me.id();
    task(
        () -> {
          if (delete) app.photos.delete(me, p.id());
          else app.photos.main(me, p.id());
          return null;
        },
        v -> reloadPhotos());
  }
}
