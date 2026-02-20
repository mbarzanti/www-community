package com.infogroup.ebaasposte.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeplerUtils {
	private static Logger logger = LogManager.getLogger();
	
	private static Logger loggerAS01 = LogManager.getLogger("analisiLogAS01");
	private static Logger loggerAS02 = LogManager.getLogger("analisiLogAS02");
	private static Logger loggerAS03 = LogManager.getLogger("analisiLogAS03");
	private static Logger loggerAS04 = LogManager.getLogger("analisiLogAS04");
	private static Logger loggerAS05 = LogManager.getLogger("analisiLogAS05");
	private static Logger loggerAS06 = LogManager.getLogger("analisiLogAS06");
	
	
	private static String nodeId = ""; 
	
	public static String getIP() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("Impossibile recuperare l'IP della macchina");
		}

		return result;
	}
		
	public static void setNodeId(String nodeId) {
		KeplerUtils.nodeId = nodeId;
	}

	public static Logger getLogger() {
		switch (nodeId) {
		case "AS01":
			return loggerAS01;
		case "AS02":
			return loggerAS02;
		case "AS03":
			return loggerAS03;
		case "AS04":
			return loggerAS04;
		case "AS05":
			return loggerAS05;
		case "AS06":
			return loggerAS06;
			
		default:
			return logger;
		}
	}
}
