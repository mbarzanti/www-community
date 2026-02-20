package com.gtone.dq.customer.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gtone.dq.customer.config.CUSTConfig;
import com.gtone.dq.customer.server.CustomerMasterRun;
import com.gtone.dq.customer.util.CustomerDetail;
import com.gtone.dq.customer.util.CustomerMaster;
import com.gtone.dq.customer.util.Query;

public class SQLQuery {
	public Connection conn = null;
	private static Logger logger = Logger.getLogger(CustomerMasterRun.class);
	String ecfClassName = "";
	String ecfUrl		= "";
	String ecfUserid    = "";
	String ecfPassword  = "";
	String queryStr		= "";

	public SQLQuery(){
		try{
			ecfClassName= CUSTConfig.getConfig("ecfDriver");
			ecfUrl	  	= CUSTConfig.getConfig("ecfUrl");
			ecfUserid   = CUSTConfig.getConfig("ecfUserid");
			ecfPassword = CUSTConfig.getConfig("ecfPassword");		
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	public int CustomerMasterUpdate(Vector v) throws Exception {
		int errCheck		= 0;
		String custID		= "";
		String CTWSendResult= "";
		
		
		ResultSet 	rs		= null;
		Statement   stmt    = null;


		
		try {
			if(v.size()>0 && v != null) {
				
				Class.forName(ecfClassName);			
				conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
				conn.setAutoCommit(false);
				
				for(int i=0;i < v.size();i++) {
					CustomerMaster cust 	= (CustomerMaster) v.elementAt(i);
					
					custID			= cust.getCust_id();
					queryStr 	= " UPDATE TB_CUSTOMER10 "
								 + "  SET BSMN_RGST_NO 	   = '"+cust.getBsmn_rgst_no()+"'"
								 + ", SET CUST_CLSF_CODE   = '"+cust.getCust_clsf_code()+"'"
								 + ", SET CUST_DVCD 	   = '"+cust.getCust_dvcd()+"'"
								 + ", SET CUST_ENGL_NAME   = '"+cust.getCust_engl_name()+"'"
								 + ", SET CUST_NAME		   = '"+cust.getCust_name()+"'"
								 + ", SET CUST_RGST_DATE   = '"+cust.getCust_rgst_date()+"'"
								 + ", SET CUST_STCD		   = '"+cust.getCust_stcd()+"'"
								 + ", SET CUST_TYPE_CODE   = '"+cust.getCust_type_code()+"'"
								 + ", SET INER_CUST_RGYN   = '"+cust.getIner_cust_rgyn()+"'"
								 + ", SET LAST_CHMN_MPNO   = '"+cust.getLast_chmn_mpno()+"'"
								 + ", SET LAST_CHNG_DTTM   = '"+cust.getLast_chng_dttm()+"'"
								 + ", SET MCNT_ADDR_KDCD   = '"+cust.getMcnt_addr_kdcd()+"'"
								 + ", SET OBTA_ROUT_CODE   = '"+cust.getObta_rout_code()+"'"
								 + ", SET PGM_ID 		   = '"+cust.getPgm_id()+"'"
								 + ", SET RSDN_RGST_NO 	   = '"+cust.getRsdn_rgst_no()+"'"
								+ " WHERE CUST_ID ='"+custID+"' ";
							
					stmt = conn.prepareStatement(queryStr);
					stmt.executeQuery(queryStr);
					
					conn.commit();
				}
			}
		} catch (Exception ex) {
			errCheck = 1;
			conn.rollback();
			logger.error("######CustomerMasterUpdate(Vector v) Exception ######"+ex);
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch(SQLException ex) {
				errCheck = 1;
				conn.rollback();
				logger.error("######CustomerMasterUpdate(Vector v) SQLException ######"+ex);	 
			}
		}
		return errCheck;
	}
	public Vector CustomerMasterSelect() throws Exception {
 
		
		Vector		v 		= new Vector();
		ResultSet 	rs		= null;
		Statement   stmt    = null;

		CustomerMaster	cust= null;
 
		try {
					
			Class.forName(ecfClassName);
			conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
			
			//
			queryStr = " SELECT * FROM TB_CUSTOMER10 ";

			stmt = conn.createStatement();
			rs   = stmt.executeQuery(queryStr);
			
			while(rs.next()){
				cust	= new CustomerMaster();
				cust.setCust_id(rs.getString("CUST_ID"));
				cust.setBsmn_rgst_no(rs.getString("BSMN_RGST_NO"));
				cust.setBsmn_rgst_no(rs.getString("BSMN_RGST_NO"));
				cust.setCust_clsf_code(rs.getString("CUST_CLSF_CODE"));
				cust.setCust_dvcd(rs.getString("CUST_DVCD"));
				cust.setCust_engl_name(rs.getString("CUST_ENGL_NAME"));
				cust.setCust_name(rs.getString("CUST_NAME"));
				cust.setCust_rgst_date(rs.getString("CUST_RGST_DATE"));
				cust.setCust_stcd(rs.getString("CUST_STCD"));
				cust.setCust_type_code(rs.getString("CUST_TYPE_CODE"));
				cust.setIner_cust_rgyn(rs.getString("INER_CUST_RGYN"));
				cust.setLast_chmn_mpno(rs.getString("LAST_CHMN_MPNO"));
				cust.setLast_chng_dttm(rs.getString("LAST_CHNG_DTTM"));
				cust.setMcnt_addr_kdcd(rs.getString("MCNT_ADDR_KDCD"));
				cust.setObta_rout_code(rs.getString("OBTA_ROUT_CODE"));
				cust.setPgm_id(rs.getString("PGM_ID"));
				cust.setRsdn_rgst_no(rs.getString("RSDN_RGST_NO"));
				
				v.addElement(cust);
			}
		} catch (Exception ex) {
			logger.error("######CustomerMasterSelect() Exception ######"+ex);			
		} finally {
			try {
				if(rs != null)		rs.close();
				if(stmt != null) 	stmt.close();
				if(conn != null)	conn.close();
			} catch(SQLException sex) {
				logger.error("######CustomerMasterSelect() SQLException ######"+sex);
			}
		}
		
		return v;
	}	
	public Vector CustomerMaserDelete(String CustId) throws Exception {
 
		
		Vector		v 		= new Vector();
		ResultSet 	rs		= null;
		Statement   stmt    = null;

		CustomerMaster	cust= null;
		
		boolean status=false;
 
		
		try {
					
			Class.forName(ecfClassName);
			conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
			
			//
			queryStr = " DELETE FROM TB_CUSTOMER10 WHERE CUST_ID = '"+CustId+"'";

			stmt = conn.createStatement();
			status   = stmt.execute(queryStr);
			
		} catch (Exception ex) {
			logger.error("######CustomerMaserDelete() Exception ######"+ex);			
		} finally {
			try {
				if(rs != null)		rs.close();
				if(stmt != null) 	stmt.close();
				if(conn != null)	conn.close();
			} catch(SQLException sex) {
				logger.error("######CustomerMaserDelete() SQLException ######"+sex);
			}
		}
		
		return v;
	}		
	public int CustomerMaserInsert(Vector v) throws Exception,SQLException {
 

		ResultSet 	rs		= null;
		Statement   stmt    = null;
		
		int         errCheck= 0; 	//
 
		
		try {
			
			if(v.size()>0 && v != null) {
				
				Class.forName(ecfClassName);
				conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
				conn.setAutoCommit(false);
				
				for(int i=0;i < v.size();i++) {
					//Master Insert
					Query tmpQuery = (Query) v.elementAt(i);
					queryStr = tmpQuery.getInsertQuery();
					stmt = conn.prepareStatement(queryStr);
					stmt.executeQuery(queryStr);
					stmt.close();
				} //for_end
			} //if_end
			
			conn.commit();
			
		} catch (Exception ex) {
			errCheck = 1;
			conn.rollback();
			logger.error("######CustomerMaserInsert(Vector v) Exception ######"+ex);
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch(SQLException ex) {
				errCheck = 1;
				conn.rollback();
				logger.error("######CustomerMaserInsert(Vector v) SQLException ######"+ex);	
			}
		}


		return errCheck;
	}
	public Vector CustomerDetailDelete(String CustId) throws Exception {
 
		
		Vector		v 		= new Vector();
		ResultSet 	rs		= null;
		Statement   stmt    = null;
	
		CustomerMaster	cust= null;
		
		boolean status=false;
 
		
		try {
					
			Class.forName(ecfClassName);
			conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
			
			//
			queryStr = " DELETE FROM TB_CUSTOMER20 WHERE CUST_ID = '"+CustId+"'";
	
			stmt = conn.createStatement();
			status   = stmt.execute(queryStr);
			
		} catch (Exception ex) {
			logger.error("######CustomerDetailDelete() Exception ######"+ex);			
		} finally {
			try {
				if(rs != null)		rs.close();
				if(stmt != null) 	stmt.close();
				if(conn != null)	conn.close();
			} catch(SQLException sex) {
				logger.error("######CustomerDetailDelete() SQLException ######"+sex);
			}
		}
		
		return v;
	}
	public int CustomerDetailInsert(Vector v) throws Exception,SQLException {
 

		ResultSet 	rs		= null;
		Statement   stmt    = null;
		
		int         errCheck= 0; 	//
 
		
		try {
			
			if(v.size()>0 && v != null) {
				
				Class.forName(ecfClassName);
				conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
				conn.setAutoCommit(false);
				
				for(int i=0;i < v.size();i++) {
					//Master Insert
					Query tmpQuery = (Query) v.elementAt(i);
					queryStr = tmpQuery.getInsertQuery();
					stmt = conn.prepareStatement(queryStr);
					stmt.executeQuery(queryStr);
					stmt.close();
				} //for_end
			} //if_end
			
			conn.commit();
			
		} catch (Exception ex) {
			errCheck = 1;
			conn.rollback();
			logger.error("######CustomerDetailInsert(Vector v) Exception ######"+ex);
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch(SQLException ex) {
				errCheck = 1;
				conn.rollback();
				logger.error("######CustomerDetailInsert(Vector v) SQLException ######"+ex);	
			}
		}


		return errCheck;
	}
	public Vector CustomerDetailSelect() throws Exception {
		
		Vector		v 		= new Vector();
		ResultSet 	rs		= null;
		Statement   stmt    = null;

		CustomerDetail	cust= null;
 
		
		try {
					
			Class.forName(ecfClassName);
			conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
			
			//
			queryStr = " SELECT * FROM TB_CUSTOMER20 ";// where CUST_ID='"+custId+"'";

			stmt = conn.createStatement();
			rs   = stmt.executeQuery(queryStr);
			
			while(rs.next()){
				cust	= new CustomerDetail();
				
				cust.setBlty_code(rs.getString("BLTY_CODE"));
				cust.setBrth(rs.getString("BRTH"));
				cust.setCar_kind_code(rs.getString("CAR_KIND_CODE"));
				cust.setCar_pose_yn(rs.getString("CAR_POSE_YN"));
				cust.setCust_grad_code(rs.getString("CUST_GRAD_CODE"));
				cust.setCust_id(rs.getString("CUST_ID"));
				cust.setDept_name(rs.getString("DEPT_NAME"));
				cust.setDm_reyn(rs.getString("DM_REYN"));
				cust.setDrve_code(rs.getString("DRVE_CODE"));
				cust.setDwel_type_code(rs.getString("DWEL_TYPE_CODE"));
				cust.setEdbg_code(rs.getString("EDBG_CODE"));
				cust.setEmal_1(rs.getString("EMAL_1"));
				cust.setEmal_1_reyn(rs.getString("EMAL_1_REYN"));
				cust.setEmal_2(rs.getString("EMAL_2"));
				cust.setEmal_2_reyn(rs.getString("EMAL_2_REYN"));
				cust.setEntr_ym(rs.getString("ENTR_YM"));
				cust.setFax_no_brh(rs.getString("FAX_NO_BRH"));
				cust.setFax_no_ddd(rs.getString("FAX_NO_DDD"));
				cust.setFax_no_dtal(rs.getString("FAX_NO_DTAL"));
				cust.setFrnr_rgst_no(rs.getString("FRNR_RGST_NO"));
				cust.setGrdu_area_code(rs.getString("GRDU_AREA_CODE"));
				cust.setHoby_code(rs.getString("HOBY_CODE"));
				cust.setHous_kdcd(rs.getString("HOUS_KDCD"));
				cust.setHp_brh(rs.getString("HP_BRH"));
				cust.setHp_bsmn(rs.getString("HP_BSMN"));
				cust.setHp_dtal(rs.getString("HP_DTAL"));
				cust.setHp_recv_yn(rs.getString("HP_RECV_YN"));
				cust.setJob_pstn_name(rs.getString("JOB_PSTN_NAME"));
				cust.setLast_chmn_mpno(rs.getString("LAST_CHMN_MPNO"));
				cust.setLast_chng_dttm(rs.getString("LAST_CHNG_DTTM"));
				cust.setLusl_dvcd(rs.getString("LUSL_DVCD"));
				cust.setMate_yn(rs.getString("MATE_YN"));
				cust.setNatn_code(rs.getString("NATN_CODE"));
				cust.setNtry_date(rs.getString("NTRY_DATE"));
				cust.setOccp_code(rs.getString("OCCP_CODE"));
				cust.setPgm_id(rs.getString("PGM_ID"));
				cust.setPspt_no(rs.getString("PSPT_NO"));
				cust.setReli_code(rs.getString("RELI_CODE"));
				cust.setRisk_grad_code(rs.getString("RISK_GRAD_CODE"));
				cust.setSchl_name(rs.getString("SCHL_NAME"));
				cust.setSex_dvcd(rs.getString("SEX_DVCD"));
				cust.setSpsk_code(rs.getString("SPSK_CODE"));
				cust.setSpsk_item(rs.getString("SPSK_ITEM"));
				cust.setVisa_end_date(rs.getString("VISA_END_DATE"));
				cust.setWedd_memr_date(rs.getString("WEDD_MEMR_DATE"));
				cust.setWedd_yn(rs.getString("WEDD_YN"));
				cust.setWkpl_name(rs.getString("WKPL_NAME"));
				
				v.addElement(cust);
			}
		} catch (Exception ex) {
			logger.error("######CustomerDetailSelect() Exception ######"+ex);			
		} finally {
			try {
				if(rs != null)		rs.close();
				if(stmt != null) 	stmt.close();
				if(conn != null)	conn.close();
			} catch(SQLException sex) {
				logger.error("######CustomerDetailSelect() SQLException ######"+sex);
			}
		}
		
		return v;
	}	
	public int CustomerDetailUpdate(Vector v) throws Exception {
		int errCheck		= 0;
		String custID		= "";
		String CTWSendResult= "";
		
		
		ResultSet 	rs		= null;
		Statement   stmt    = null;
 
		try {
			if(v.size()>0 && v != null) {
				
				Class.forName(ecfClassName);			
				conn   = DriverManager.getConnection(ecfUrl,ecfUserid,ecfPassword);
				conn.setAutoCommit(false);
				
				for(int i=0;i < v.size();i++) {
					CustomerDetail cust 	= (CustomerDetail) v.elementAt(i);
					
					custID			= cust.getCust_id();
					queryStr 	=  "  UPDATE TB_CUSTOMER20 "
							+" SET LAST_CHMN_MPNO	  ='"+ cust.getLast_chmn_mpno()+ "'"  
							+" , LAST_CHNG_DTTM	  ='"+ cust.getLast_chng_dttm()+ "'"
							+" , PGM_ID	  		    ='"+ cust.getPgm_id()+ "'"
							+" , SEX_DVCD	        ='"+ cust.getSex_dvcd()+ "'"
							+" , LUSL_DVCD	      ='"+ cust.getLusl_dvcd()+ "'"
							+" , BLTY_CODE	      ='"+ cust.getBlty_code()+ "'"
							+" , OCCP_CODE	      ='"+ cust.getOccp_code()+ "'"
							+" , DWEL_TYPE_CODE	  ='"+ cust.getDwel_type_code()+ "'"
							+" , HOUS_KDCD	      ='"+ cust.getHous_kdcd()+ "'"
							+" , RISK_GRAD_CODE   ='"+ cust.getRisk_grad_code()+ "'"
							+" , CAR_POSE_YN		  ='"+ cust.getCar_pose_yn()+ "'"
							+" , CAR_KIND_CODE	  ='"+ cust.getCar_kind_code()+ "'"
							+" , DRVE_CODE	 		  ='"+ cust.getDrve_code()+ "'"
							+" , GRDU_AREA_CODE   ='"+ cust.getGrdu_area_code()+ "'"
							+" , RELI_CODE			  ='"+ cust.getReli_code()+ "'"
							+" , HOBY_CODE			  ='"+ cust.getHoby_code()+ "'"
							+" , SPSK_CODE			  ='"+ cust.getSpsk_code()+ "'"
							+" , EDBG_CODE			  ='"+ cust.getEdbg_code()+ "'"
							+" , NATN_CODE			  ='"+ cust.getNatn_code()+ "'"
							+" , BRTH			        ='"+ cust.getBrth()+ "'"
							+" , WEDD_YN		      ='"+ cust.getWedd_yn()+ "'"
							+" , WEDD_MEMR_DATE   ='"+ cust.getWedd_memr_date()+ "'"
							+" , MATE_YN	        ='"+ cust.getMate_yn()+ "'"
							+" , HP_BSMN	        ='"+ cust.getHp_bsmn()+ "'"
							+" , HP_BRH	          ='"+ cust.getHp_brh()+ "'"
							+" , HP_DTAL	        ='"+ cust.getHp_dtal()+ "'"
							+" , HP_RECV_YN	      ='"+ cust.getHp_recv_yn()+ "'"
							+" , FAX_NO_DDD	      ='"+ cust.getFax_no_ddd()+ "'"
							+" , FAX_NO_BRH	      ='"+ cust.getFax_no_brh()+ "'"
							+" , FAX_NO_DTAL	    ='"+ cust.getFax_no_dtal()+ "'"
							+" , WKPL_NAME	      ='"+ cust.getWkpl_name()+ "'"
							+" , ENTR_YM	        ='"+ cust.getEntr_ym()+ "'"
							+" , JOB_PSTN_NAME	  ='"+ cust.getJob_pstn_name()+ "'"
							+" , DEPT_NAME	      ='"+ cust.getDept_name()+ "'"
							+" , SCHL_NAME	      ='"+ cust.getSchl_name()+ "'"
							+" , PSPT_NO	        ='"+ cust.getPspt_no()+ "'"
							+" , FRNR_RGST_NO	    ='"+ cust.getFrnr_rgst_no()+ "'"
							+" , NTRY_DATE	      ='"+ cust.getNtry_date()+ "'"
							+" , VISA_END_DATE	  ='"+ cust.getVisa_end_date()+ "'"
							+" , SPSK_ITEM	      ='"+ cust.getSpsk_item()+ "'"
							+" , EMAL_1	          ='"+ cust.getEmal_1()+ "'"
							+" , EMAL_1_REYN	    ='"+ cust.getEmal_1_reyn()+ "'"
							+" , EMAL_2	          ='"+ cust.getEmal_2()+ "'"
							+" , EMAL_2_REYN	    ='"+ cust.getEmal_2_reyn()+ "'"
							+" , DM_REYN	        ='"+ cust.getDm_reyn()+ "'"
							+" , CUST_GRAD_CODE   ='"+ cust.getCust_grad_code()+ "'"
							+ " WHERE CUST_ID ='"+custID+"' ";
							
					stmt = conn.prepareStatement(queryStr);
					stmt.executeQuery(queryStr);
					
					conn.commit();
				}
			}
		} catch (Exception ex) {
			errCheck = 1;
			conn.rollback();
			logger.error("######CustomerDetailUpdate(Vector v) Exception ######"+ex);
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(conn!=null) conn.close();
			} catch(SQLException ex) {
				errCheck = 1;
				conn.rollback();
				logger.error("######CustomerDetailUpdate(Vector v) SQLException ######"+ex);	 
			}
		}
		return errCheck;
	}
}
