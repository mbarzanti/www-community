package dao;

import java.sql.*;
import java.util.*;

public class TB_ZIP_CODE {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select ZIPCODE, SIDO, GUGUN, DONG, RI, BLDG, ST_BUNJI, ED_BUNJI, SEQ ";
		query += "from TB_ZIP_CODE ";

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
