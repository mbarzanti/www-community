package com.gtone.dq.customer.server;

/***************************************************************************
*                                                                           

*
**************************************************************************/
import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gtone.dq.customer.config.CUSTConfig;
import com.gtone.dq.customer.query.SQLQuery;
import com.gtone.dq.customer.util.CustomerMaster;

public class CustomerMasterRun extends Thread
{
	public  Connection  conn  = null;
	private static Logger logger = Logger.getLogger(CustomerMasterRun.class);

	public CustomerMasterRun() { //
	}

	/*****
 
	 * @@@@param  
	 * @@@@return 
	 * @@@@throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		logger.info("######Customer Master START######");
		CustomerMasterRun masterrun = new CustomerMasterRun();
		CustomerMaster data;
		SQLQuery 	sqlQuery= new SQLQuery();
		Vector 		master 	= new Vector();
 
		
		try { 
				master 	= sqlQuery.CustomerMasterSelect();
				
				for(int i=0; i < master.size(); i++){
					data= (CustomerMaster)master.get(i);
					logger.info(data.toString());
				}
			
		}catch (Exception e){
			logger.error("######main Exception######"+e);
		}	
		logger.info("######Customer Master START   END######");
	}
}


