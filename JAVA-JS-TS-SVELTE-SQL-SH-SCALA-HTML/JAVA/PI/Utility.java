package com.infogroup.poste.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utility {
	
	private static Logger logger = LogManager.getLogger();

	public static String cryptPassword(final String password) {
        MessageDigest md=null;
        try {
            //we use MD5 Algorithm
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
        	logger.error(ex.getMessage(), ex);
        }
        md.update(password.getBytes());
        byte[] hash = md.digest();
        
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
          if ((0xff & hash[i]) < 0x10) {
            hexString.append("0"+Integer.toHexString((0xFF & hash[i])));
          } else {
            hexString.append(Integer.toHexString(0xFF & hash[i]));
          }
        }
        return hexString.toString();
     }
	
	public static String getIPFromRequestHeaders(HttpServletRequest req) {
		String ip = req.getHeader("X-Forwarded-For");

		if (ip != null) {
			int pos = ip.indexOf(",");
			if (pos != -1) {
				ip = ip.substring(0, pos);
			}

			ip = ip.trim();
		}
		
		logger.debug("X-Forwarded-For: {}", ip);
		
		return ip;
	}

}
