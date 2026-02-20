package it.citel.postel.bancarizzazioneGUI.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.citel.postel.bancarizzazioneGUI.service.AccountingService;
import it.citel.postel.commonLib.constants.Constants;
import it.citel.postel.commonLib.object.ricerche.Combo;
import it.citel.postel.commonLib.rest.model.AccountingDataPostaDescritta;
import it.citel.postel.commonLib.rest.model.AccountingReqDataTableObj;
import it.citel.postel.commonLib.rest.model.Request;
import it.citel.postel.commonLib.rest.model.RequestAccountingGMIDA;
import it.citel.postel.commonLib.rest.model.RequestAccountingJob;
import it.citel.postel.commonLib.rest.model.RequestAccountingPostaDescritta;
import it.citel.postel.commonLib.rest.model.RequestOperatore;
//import it.citel.postel.commonLib.rest.model.RequestDataTableAccounting;
import it.citel.postel.commonLib.rest.model.Response;
import it.citel.postel.commonLib.rest.model.ResponseAccounting;
import it.citel.postel.commonLib.rest.model.ResponseAccountingJob;
import it.citel.postel.commonLib.rest.model.ResponseAttachment;

@Controller
@RequestMapping("/accounting")
public class AccountingController {
	static final Logger log = LogManager.getLogger(AccountingController.class);
	
	@Autowired
	private AccountingService accountingService ;
	
	@RequestMapping( value = "showAccounting", method = {RequestMethod.GET, RequestMethod.POST} )
	public String showAccounting() {
		log.debug("showAccounting");
		return "ricercaAccounting";
	}
	

	@RequestMapping( value="getListAccountingRequest" , method=RequestMethod.GET )
	public @ResponseBody String getListAccountingRequest() {
		
		log.info( "getListAccountingRequest - start" ) ;

		String strReturn = Constants.NO_ROW_FOUND_TABLE;

		try {
			strReturn = accountingService.getListAccountingRequest();
		} catch( Exception e ) {
			log.error( "Exception: " + e.getMessage() , e ) ;
		}
				
		log.info( "getListAccountingRequest - stop" ) ;

		return strReturn ;
	}
	
	@RequestMapping( value="getLastAccountingRequest" , method=RequestMethod.GET )
	public @ResponseBody Response<?> getLastAccountingRequest() {
		Response<AccountingReqDataTableObj> results = new Response<>() ;
		
		log.info( "getLastAccountingRequest - start" ) ;

		try {
			results.setData( accountingService.getLastAccountingRequest() ) ;
			results.setMessage( "OK" ) ;
			results.setStatus( true ) ;
		}
		catch( Exception e ) {
			log.error( "Exception: " + e.getMessage() , e ) ;
			results.setData( null ) ;
			results.setMessage( e.getMessage() ) ;
			results.setStatus( false ) ;
		}
		
		log.info( "getLastAccountingRequest - stop" ) ;

		return results ;
	}
	
	
	
	@RequestMapping(value = "/AccountingInsert", method =  RequestMethod.POST)
	@ResponseBody
	public Response<?>  AccountingInsert(@RequestBody Request<RequestAccountingGMIDA>  request) {
		log.debug("configurazione: Accounting Insert");
		Response<?>  response = null;
		
		try {
			response = accountingService.AccountingInsert(request); 
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
			response.setStatus(false);
			response.setData(null);
			response.setMessage("KO - "+e.getMessage());		
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			response.setStatus(false);
			response.setData(null);
			response.setMessage("KO - "+e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			response.setStatus(false);
			response.setData(null);
			response.setMessage("KO - "+e.getMessage());
		}
		return response;
	}
	
	
	
	
	
	@RequestMapping( value="/getListDateGmida" , method=RequestMethod.GET )
	public @ResponseBody ResponseAccounting getListDateGmida() {
		ResponseAccounting results = new ResponseAccounting() ;
		
		log.info( "getListDateGmida - start" ) ;
		
		try {
			results.setDate(accountingService.getListDateGmida());
			results.setMessage( "OK" ) ;
			results.setStatus( true ) ;
		}
		catch( Exception e ) {
			log.error( "Exception: " + e.getMessage() , e ) ;
			results.setDate( null ) ;
			results.setMessage( e.getMessage() ) ;
			results.setStatus( false ) ;
		}
		
		log.info( "getListDateGmida - stop" ) ;
		
		return results ;
	}

}
