package dao;

import java.sql.*;

public class DAO {

	private static Connection conn;
	 
	 public static synchronized Connection getConnection(){
	  try{
	   Class.forName("oracle.jdbc.driver.OracleDriver");
	  }catch(ClassNotFoundException e){
	   e.printStackTrace();
	  }
	  
	  String db_url = "jdbc:oracle:thin:@localhost:1521:orcl";
	  String db_user = "Scott";
	  String db_pwd = "Tiger";
	  
	  try{
	   conn = DriverManager.getConnection(db_url, db_user, db_pwd);
	  }catch(SQLException e){
	   e.printStackTrace();
	   return null;
	  }
	  return conn;
	 }
}
