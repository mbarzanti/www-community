package dao;

import java.sql.*;
import java.util.*;

public class TB_CD10 {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = "select *  ";
		query += "from TB_CD10 a, TB_CD20 b where a.code_id = b.code_id ";

		  Connection con = null;
		  Statement stmt = null;
		  ResultSet rs = null;

		  try{
		   con = cp.getConnection();
		   stmt = con.createStatement();
		   rs = stmt.executeQuery(query);

		   result = rs.next();

		  } catch(Exception e) {
		   e.printStackTrace();
		  } finally {
		   try{
		    rs.close();
		    stmt.close();
		   } catch(Exception e) {
		    e.printStackTrace();
		   }
		  }
		  return result;

		}
	
}
