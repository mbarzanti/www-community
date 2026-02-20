package dao;

import java.sql.*;
import java.util.*;

public class TB_CD20 {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = "select CODE_ID, CODE_VAL, LAST_CHMN_MPNO, LAST_CHNG_DTTM, PGM_ID, CODE_NAME, OUTP_ORDR, RELN_CD1, RELN_CD2, INER_STDD_CODE, EXPL_1, EXPL_2, EXPL_3, EXPL_4, EXPL_5, EXPL_6, FRST_RGST_MPNO, STRT_DATE, END_DATE, LAST_CHNG_CHCD, LAST_CHMN_DPCD, DEL_DATE  ";
		query += "from TB_CD20 ";

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
