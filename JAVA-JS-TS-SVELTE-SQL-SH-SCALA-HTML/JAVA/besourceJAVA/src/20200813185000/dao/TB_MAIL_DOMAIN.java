package dao;

import java.sql.*;
import java.util.*;

public class TB_MAIL_DOMAIN {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select ORDER_NO, ORGITEM_CODE, VEND_CODE, CUST_ID, ORDER_DATE, PROC_STATE, ALLOT_MONTH, SALE_PRICE, ORDER_QTY, SYSCANCEL, ORDER_AMT, DCRATE, DCAMT, RSALE_AMT, DELY_GB, FIRST_PLAN_DATE, OUT_PLAN_DATE, DELY_HOPE_DATE, DELY_HOPE_YN, MSG_NOTE, HAPPY_CARD_YN, SAVEAMT, CANCEL_DATE, SAVEAMT_GB, LAST_PROC_DATE, WEB_SAVEAMT, LAST_PROC_DAY ";
		query += "from TB_ORDER_DETAIL ";

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
