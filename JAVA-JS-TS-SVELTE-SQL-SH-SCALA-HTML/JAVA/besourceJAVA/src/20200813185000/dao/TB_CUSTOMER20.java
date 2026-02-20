package dao;

import java.sql.*;
import java.util.*;

public class TB_CUSTOMER20 {

	public boolean getData()
	{
		DAO cp = new DAO();
		boolean result = false;
		
		
		String query = " select CUST_ID, LAST_CHMN_MPNO, LAST_CHNG_DTTM, PGM_ID, SEX_DVCD, LUSL_DVCD, BLTY_CODE, OCCP_CODE, DWEL_TYPE_CODE, HOUS_KDCD, RISK_GRAD_CODE, CAR_POSE_YN, CAR_KIND_CODE, DRVE_CODE, GRDU_AREA_CODE, RELI_CODE, HOBY_CODE, SPSK_CODE, EDBG_CODE, NATN_CODE, BRTH, WEDD_YN, WEDD_MEMR_DATE, MATE_YN, HP_BSMN, HP_BRH, HP_DTAL, HP_RECV_YN, FAX_NO_DDD, FAX_NO_BRH, FAX_NO_DTAL, WKPL_NAME, ENTR_YM, JOB_PSTN_NAME, DEPT_NAME, SCHL_NAME, PSPT_NO, FRNR_RGST_NO, NTRY_DATE, VISA_END_DATE, SPSK_ITEM, EMAL_1, EMAL_1_REYN, EMAL_2, EMAL_2_REYN, DM_REYN, CUST_GRAD_CODE ";
		query += "from TB_CUSTOMER20 ";

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
	

public int insertData()
	{
		DAO cp = new DAO();
		int result = -1;
		
		
		String query = "INSERT INTO CUST_ID, LAST_CHMN_MPNO, LAST_CHNG_DTTM, PGM_ID  ";
		query += "VALUE('1', '1', '1', '1', '1') ";
		query += "from TB_CUSTOMER20 ";

		  Connection con = null;
		  Statement stmt = null;
		  ResultSet rs = null;

		  try{
		   con = cp.getConnection();
		   stmt = con.createStatement();
		   result = stmt.executeUpdate(query);

		   

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

	public int updateData()
	{
		DAO cp = new DAO();
		int result = -1;
		
		
		String query = "UPDATE TB_CUSTOMER20 SET LAST_CHMN_MPNO = '1', LAST_CHNG_DTTM = '1', PGM_ID = '1'  WHERE CUST_ID = '1' ";


		  Connection con = null;
		  Statement stmt = null;
		  ResultSet rs = null;

		  try{
		   con = cp.getConnection();
		   stmt = con.createStatement();
		   result = stmt.executeUpdate(query);

		   

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


	public int deleteData()
	{
		DAO cp = new DAO();
		int result = -1;
		
		
		String query = "DELETE FROM TB_CUSTOMER20 WHERE CUST_ID = '1' ";


		  Connection con = null;
		  Statement stmt = null;
		  ResultSet rs = null;

		  try{
		   con = cp.getConnection();
		   stmt = con.createStatement();
		   result = stmt.executeUpdate(query);

		   

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
