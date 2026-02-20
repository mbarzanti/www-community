package dao;

import java.sql.*;
import java.util.*;

public class TB_CUST_PLACE {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select CUST_ID, PLACE_SEQ, PLACE_GB, DDD, TEL1, TEL2, TEL3, POST_NO, ADDR, USE_YN, ADDR_CHG_DATE, ADDR_CHK_YN, INSERT_ID, INSERT_DATE, MODIFY_ID, MODIFY_DATE, REJECT_YN ";
		query += "from TB_CUST_PLACE ";

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
	public boolean getData2()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = "select CODE_ID, LAST_CHMN_MPNO, LAST_CHNG_DTTM, PGM_ID, CODE_NAME, CODE_OUNM, CODE_TYPE, CODE_GRNT_STDD, RELN_CD1, RELN_CD2, INER_STDD_CODE, EXPL_1, EXPL_2, EXPL_3, EXPL_4, EXPL_5, EXPL_6, FRST_RGST_MPNO, STRT_DATE, END_DATE, LAST_CHNG_CHCD, LAST_CHMN_DPCD, DEL_DATE, DOMN_YN  ";
		query += "from TB_CD10 ";

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
