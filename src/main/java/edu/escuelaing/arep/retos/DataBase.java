package edu.escuelaing.arep.retos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * DataBase
 */
public class DataBase {

    public static void main(String[] args){
        
        System.out.println(getData());
    }

  public static String getData() {
    String res = "";
    try {
      Class.forName("org.postgresql.Driver");
      String host = "ec2-3-234-109-123.compute-1.amazonaws.com";
      String db = "deq208d0vecg75";
      String port = "5432";
      String user = "smtktknjrcgusb";
      String passwd = "0acd1b131fe78d50cfafc73df0d67303ebe829faecfa9055d89597c61ebf83a6";
      Connection con = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + db, user, passwd);
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("select * from Reto2");
      int i = 0;
      while (rs.next()){
        res += "Line" + i + ": " + rs.getInt(1) + "  " + rs.getString(2) + "</br>";
        i++;
      }
      con.close();
    } catch (Exception e) {
      res = "Can't Connect to database";
      System.out.println(e);
    }
    return res;
  }
}