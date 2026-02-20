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
import com.gtone.dq.customer.util.CustomerDetail;
import com.gtone.dq.customer.util.CustomerMaster;

 

public class CustomerDetailRun extends Thread
{
	public  Connection  conn  = null;
	private static Logger logger = Logger.getLogger(CustomerDetailRun.class);

	public CustomerDetailRun() { //
	}
	
	/*
	 * @@@@param  
	 * @@@@return 
	 * @@@@throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		int errCheck 		= 0;
		int sendDelayTime	= 0;	//
		sendDelayTime	= Integer.parseInt(CUSTConfig.getConfig("sendDelayTime"));
		
		logger.info("######Customer Master START######");
		CustomerDetailRun masterrun = new CustomerDetailRun();
		CustomerDetail data;
		SQLQuery 	sqlQuery= new SQLQuery();
		Vector 		detail  = new Vector();
		
		try { 
			detail 	= sqlQuery.CustomerDetailSelect();
				
				for(int i=0; i < detail.size(); i++){
					data= (CustomerDetail)detail.get(i);
					logger.info(data.toString());
				}
			
		}catch (Exception e){
			logger.error("######main Exception######"+e);
		}	
		logger.info("######Customer Master START   END######");
	}
}


