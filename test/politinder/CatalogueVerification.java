package politinder;

import java.nio.file.*;
import java.sql.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Solo usa el servidor desechable 33307; prueba carga inicial y ampliación desde 20. */
public final class CatalogueVerification {
  static int checks;
  static void check(boolean value, String message) {
    if (!value) throw new AssertionError(message);
    checks++;
    System.out.println("OK · " + message);
  }
  static void script(Connection c, Path file, String schema) throws Exception {
    for (String s : SqlScripts.split(Files.readString(file))) {
      s = s.replace("`politinder`", "`" + schema + "`");
      if (s.equalsIgnoreCase("USE politinder")) s = "USE " + schema;
      try (var st = c.createStatement()) { st.execute(s); }
    }
  }
  public static void main(String[] args) throws Exception {
    var cfg = new AppConfig();
    cfg.host="127.0.0.1"; cfg.port=33307; cfg.user="root"; cfg.password="";
    var db=new Db(cfg);
    try(var c=db.open()) {
      // The earlier suite can add accounts; verify the thirty known demo emails.
      for(var profile:DemoProfiles.PROFILES) {
        var rows=Db.rows(c,"SELECT idUser,password FROM `USER` WHERE mail=?",profile.mail());
        check(rows.size()==1,"Cuenta demo única " + profile.mail());
        int id=((Number)rows.getFirst().get("idUser")).intValue();
        check(Passwords.verify("PoliDemo25!".toCharArray(),(String)rows.getFirst().get("password")),"Clave demo compatible " + profile.id());
        // Gallery editing is tested separately; verify initial DML in a new schema below.
        for(int n=1;n<=profile.photoCount();n++)
          check(ImageIO.read(AppConfig.resolve(profile.photo(n)).toFile())!=null,"Imagen decodificable " + profile.id()+"/"+n);
      }
      String schema="politinder_upgrade_20261114";
      check(Db.number(c,"SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name=?",schema)==0,"Esquema aislado de ampliación libre");
      script(c,AppConfig.ROOT.resolve("sql/01_ddl.sql"),schema);
      script(c,AppConfig.ROOT.resolve("test/fixtures/02_dml_20_users.sql"),schema);
      check(Db.number(c,"SELECT COUNT(*) FROM `USER`")==20,"Punto de partida: 20 usuarios");
      Db.insert(c,"INSERT INTO `USER` (idUser,status,mail,password,name,bornDate,idGender,idMajor) VALUES (21,'ACTIVE','personal@example.test','preservar','Persona propia','2000-01-01',1,1)");
      String before=(String)Db.rows(c,"SELECT password FROM `USER` WHERE idUser=1").getFirst().get("password");
      script(c,AppConfig.ROOT.resolve("sql/04_ampliar_demo.sql"),schema);
      check(Db.number(c,"SELECT COUNT(*) FROM `USER`")==31,"30 demos más cuenta personal, sin colisiones de ID");
      check(Db.number(c,"SELECT COUNT(*) FROM USERPHOTO")==70,"Ampliación produce 70 fotos");
      check(Db.number(c,"SELECT COUNT(*) FROM (SELECT u.idUser FROM `USER` u LEFT JOIN USERPHOTO p ON p.idUser=u.idUser WHERE u.mail LIKE '%@politinder.test' GROUP BY u.idUser HAVING COUNT(p.idUserPhoto)<2 OR SUM(p.isProfilePhoto)<>1) invalid")==0,"Todos los demos con dos fotos mínimo y una principal");
      check(Db.number(c,"SELECT COUNT(*) FROM `USER` WHERE idUser=21 AND mail='personal@example.test' AND password='preservar'")==1,"Cuenta personal preservada");
      check(before.equals(Db.rows(c,"SELECT password FROM `USER` WHERE idUser=1").getFirst().get("password")),"Clave existente preservada");
      check(Db.number(c,"SELECT COUNT(*) FROM MESSAGE")==24 && Db.number(c,"SELECT COUNT(*) FROM POLIMATCH")==6,"Conversaciones y matches preservados");
      script(c,AppConfig.ROOT.resolve("sql/04_ampliar_demo.sql"),schema);
      check(Db.number(c,"SELECT COUNT(*) FROM `USER`")==31 && Db.number(c,"SELECT COUNT(*) FROM USERPHOTO")==70,"Ampliación repetible sin duplicados");
      check(Db.number(c,"SELECT COUNT(*) FROM POLIMATCH pm JOIN `LIKE` a ON a.idLike=pm.idLike1 JOIN `LIKE` b ON b.idLike=pm.idLike2 WHERE a.idUser1<>b.idUser2 OR a.idUser2<>b.idUser1")==0,"Todos los matches tienen likes recíprocos");
      check(Db.number(c,"SELECT COUNT(*) FROM MESSAGE m JOIN CHAT c ON c.idChat=m.idChat JOIN POLIMATCH pm ON pm.idPoliMatch=c.idPoliMatch JOIN `LIKE` l ON l.idLike=pm.idLike1 WHERE m.sender NOT IN(l.idUser1,l.idUser2)")==0,"Emisores pertenecen a sus chats");
      script(c,AppConfig.ROOT.resolve("sql/03_reportes.sql"),schema);
      check(true,"Archivo de nueve reportes ejecutable directamente como SQL");
      try(var st=c.createStatement();var rs=st.executeQuery("SELECT pathPhoto FROM USERPHOTO")) {
        while(rs.next()) if(!Files.isRegularFile(AppConfig.resolve(rs.getString(1)))) throw new AssertionError(rs.getString(1));
      }
      check(true,"Todas las rutas de la base ampliada existen");
    }
    System.out.println("TOTAL CATÁLOGO: " + checks);
  }
}
