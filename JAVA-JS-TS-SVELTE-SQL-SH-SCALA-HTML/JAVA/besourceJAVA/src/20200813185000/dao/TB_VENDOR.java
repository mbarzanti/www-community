package dao;

import java.sql.*;
import java.util.*;

public class TB_VENDOR {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select VEND_CODE, VEND_NAME, SIDNO, WORK_TYPE, BNTP_CODE, POST_NO, ADDR, TEL_NO1, TEL_NO2, TEL_NO3, FAX_NO1, FAX_NO2, FAX_NO3, OWNER_NAME, OWNER_RESIDENT_NO, OWNER_PHONE, FIRST_DATE, DISHONOR_NO, GUARANTEE_NAME, FAX_USE_YN, CL_BUSS, NTPR_SCAL_CODE, END_DATE, END_REASON, INSERT_DATE, INSERT_ID, MODIFY_DATE, MODIFY_ID ";
		query += "from TB_VENDOR ";

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
