package politinder;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import static politinder.TestSuite.*;

/** Migración sobre un esquema de pruebas separado; nunca conecta al puerto habitual. */
public class SchemaVerification {
 public static void main(String[] args) throws Exception {
  var cfg=new AppConfig();cfg.host="127.0.0.1";cfg.port=33307;cfg.user="root";cfg.password="";
  var db=new Db(cfg);var people=new PeopleService(db);
  Installer.migrate(db);Installer.migrate(db);
  check(people.choices("orientation",0).size()==6,"Catálogo de orientación disponible");
  var u=people.register("Perfil orientación","orientacion@qa.test","Prueba123!".toCharArray(),LocalDate.of(2001,3,7),1,1,4,"Prueba");
  check(u.orientation()==4 && u.orientationName().equals("Bisexual"),"Registro guarda y recupera la FK y su etiqueta");
  u=people.save(u.id(),u.name(),u.mail(),u.bornDate(),u.gender(),u.major(),6,u.biography(),true);
  check(u.orientation()==6,"Edición de orientación persiste");
  check(people.login(u.mail(),"Prueba123!".toCharArray()).orientation()==6,"Login lee el perfil completo");
  try(var c=db.open()) {
   for(String table:List.of("FACULTY","MAJOR","GENDER","SEXUALORIENTATION","USER","USERPHOTO","LIKE","POLIMATCH","CHAT","MESSAGE","POLIDATE"))
    check(Db.number(c,"SELECT COUNT(*) FROM `"+table+"`")>0,"Tabla poblada: "+table);
   for(String col:List.of("bornDate","creationDate","updateDate"))
    check(Db.number(c,"SELECT COUNT(DISTINCT "+col+") FROM `USER` WHERE idUser<=30")==30,"30 fechas diferentes: "+col);
   check(Db.number(c,"SELECT COUNT(DISTINCT DATE(sendDate)) FROM MESSAGE WHERE idMessage<=24")==24,"Mensajes distribuidos en fechas distintas");
   check(Db.number(c,"SELECT COUNT(DISTINCT dateOfPoliDate) FROM POLIDATE WHERE idPoliDate<=8")==8,"8 citas con fecha y hora diferentes");
   rejects(()->Db.update(c,"UPDATE `USER` SET idSexualOrientation=99999 WHERE idUser=1"),"FK rechaza orientación inexistente");
   long users=Db.number(c,"SELECT COUNT(*) FROM `USER`");
   Installer.migrate(db); check(Db.number(c,"SELECT COUNT(*) FROM `USER`")==users,"Migración repetida conserva cuentas");
  }
  try(var c=db.open(false)) {
   String schema="politinder_legacy_v4";
   for(String sql:SqlScripts.split(Files.readString(AppConfig.ROOT.resolve("test/fixtures/legacy_schema.sql")))) {
    sql=sql.replace("`politinder`","`"+schema+"`");if(sql.equalsIgnoreCase("USE politinder"))sql="USE "+schema;
    try(var st=c.createStatement()){st.execute(sql);}
   }
   String hash=(String)Db.rows(c,"SELECT password FROM `USER` WHERE idUser=1").getFirst().get("password");
   for(int pass=0;pass<2;pass++)for(String sql:SqlScripts.split(Files.readString(AppConfig.ROOT.resolve("sql/02_actualizar_base.sql")))) {
    if(sql.equalsIgnoreCase("USE politinder"))sql="USE "+schema;
    try(var st=c.createStatement()){st.execute(sql);}
   }
   check(Db.number(c,"SELECT COUNT(*) FROM `USER`")==30,"Migración antigua conserva 30 perfiles");
   check(Db.number(c,"SELECT COUNT(*) FROM USERPHOTO")==70,"Migración conserva 70 fotos");
   check(Db.number(c,"SELECT COUNT(*) FROM MESSAGE")==24,"Migración conserva conversaciones");
   check(Db.number(c,"SELECT COUNT(*) FROM `USER` WHERE idSexualOrientation=1")==30,"No se infiere orientación de cuentas existentes");
   check(Db.rows(c,"SELECT password FROM `USER` WHERE idUser=1").getFirst().get("password").equals(hash),"Migración conserva hashes");
  }
  System.out.println("TOTAL esquema: "+passed+" verificaciones correctas.");
 }
}
