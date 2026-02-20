package com.amazonaws.services.dynamodbv2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

// import javax.annotation.Generated;

import com.amazonaws.*;
import com.amazonaws.regions.*;

 
import com.amazonaws.services.dynamodbv2.*; 


import java.math.BigDecimal;

class Costanti {
public static final String CIPHER_KEY = "?!_3||4_p3pp4_!?";
private static final byte[] keyValue = 
              new byte[] { 'C', '@', 'c', 'a', '3', 'S', '1', 's', 'E', '@', 'z','/', 'T', '?', ';', 'i' };
	private encdec () {
	  
	  mapFilenameOccurrences.get(++occ); // VIOLAZ la funzione è di tipo String
	  String jsonParam = CipherUtil.decryptAESTextHexProtected(Costanti.CIPHER_KEY, keyCode); // VIOLAZ
	  Key key = new SecretKeySpec(keyValue, ALGO); // VIOLAZ keyValue
	  return CipherUtil.encryptAESTextHexProtected(Costanti.CIPHER_KEY, getTableEnumValue().toString()); // VIOLAZ
	  }
	
	private static String mapFilenameOccurrences (ZipOutputStream zos, AllegatoZipDTO allegatoDTO) throws  IOException, SQLException 
	{
	}

	public String backHomeMiur(HttpSession session, HttpServletRequest request, Model model) throws Exception
	{
		request.getSession().setAttribute("datiUtente", user); // VIOLAZ request è di tipo HttpServletRequest
	}
	
	public List<Mfg1002Anagistscol> getListaScuoleCollegate(HttpSession session, String codScuUt, int annoRif) throws ValidazioneExitException 
	{
	   session.setAttribute("listaScuole", listaScuole); // VIOLAZ session è di tipo HttpSession
	}

}

public class awc_cloud {

	public void lineeGuidaVA( @PathVariable("filename") String fileName,  @PathVariable("ext") String ext, HttpServletResponse response) throws URISyntaxException {
		namedParameterJdbcTemplate.update(sql, paramSource); // VIOLAZ non testa il codice di ritorno
		InputStream resource = this.getClass().getResourceAsStream("/doc/"+fileName+"."+ext); // VIOLAZ filename è untrusted se public function è untrusted

		TrustManager[] trustCerts = new TrustManager[]
		{
			new X509TrustManager() 
			{
				public java.security.cert.X509Certificate[] getAcceptedIssuers() 
				{
					return null;
				}
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				{ 
					return; // VIOLAZ
				}
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) 
				{ // VIOLAZ
				}
			}
		};
	}

		public PrioritaTraguardiRavWsDTO getPriotitaTraguardi(String codScuUt, int annoRif, String codiceSezione, String codiceArea) throws RavWsException{
		   RestTemplate restTemplate = new RestTemplate(factory);
		   ResponseEntity<PrioritaTraguardiRavWsDTO> response = restTemplate.exchange(
								GET_PRIORITA_TRAGUARDI,
								HttpMethod.GET,
								new HttpEntity<Object>(requestHeaders),
								PrioritaTraguardiRavWsDTO.class,
								codScuUt,
								annoRif,
							   codiceSezione,
								codiceArea
		   ); // VIOLAZ se funzione public untrusted, allora sono untrusted anche codScuUt, codiceSezione e codiceArea
		}


		public String paritarieRegione(HttpSession session,HttpServletRequest request,Model model, @PathVariable("reg") String reg,@RequestParam(value = "activeMenu") String activeMenu) throws Exception{
		   
			String header = httpResponse.getHeader("Set-Cookie");
			if (header != null && header.startsWith("JSESSIONID")) {
				header = header + "; SameSite=Lax;";
				httpResponse.setHeader("Set-Cookie", header); // VIOLAZ
			 }

		   model.addAttribute("regione", reg); // VIOLAZ reg untrusted se Public function è untrusted
		   
		}

	}

