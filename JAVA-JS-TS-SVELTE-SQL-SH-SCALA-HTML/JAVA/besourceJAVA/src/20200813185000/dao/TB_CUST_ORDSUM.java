package dao;

import java.sql.*;
import java.util.*;

public class TB_CUST_ORDSUM {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select CUST_ID, CUR_ORDER_CNT, CUR_ORDER_AMT, CUR_CANCEL_CNT, CUR_CANCEL_AMT, CUR_RETURN_CNT, CUR_RETURN_AMT, TOT_ORDER_CNT, TOT_ORDER_AMT, TOT_CANCEL_CNT, TOT_CANCEL_AMT, TOT_RETURN_CNT, TOT_RETURN_AMT, FIRST_ORDER_DATE, FIRST_MSALE_YEAR, LAST_MSALE_YEAR, LAST_ORDER_DATE, LAST_CANCEL_DATE, LAST_CLAIM_DATE, INSERT_ID, INSERT_DATE, MODIFY_ID, MODIFY_DATE ";
		query += "from TB_CUST_ORDSUM ";

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
