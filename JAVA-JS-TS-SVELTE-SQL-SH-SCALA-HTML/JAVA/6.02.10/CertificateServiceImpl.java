/**
 * 01/06/2020
 */
package it.poste.fdr.ecomm.be.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.poste.authentication.common.Utils;
import it.poste.fdr.ecomm.be.client.fdr.ICommon;
import it.poste.fdr.ecomm.be.client.fdr.IFdrLayer;
import it.poste.fdr.ecomm.be.client.fdr.IPuk;
import it.poste.fdr.ecomm.be.client.fdr.PukRequestResponse;
import it.poste.fdr.ecomm.be.client.fdr.UtilsClient;
import it.poste.fdr.ecomm.be.common.date.DateHelper;
import it.poste.fdr.ecomm.be.common.transl.TranslatorHelper;
import it.poste.fdr.ecomm.be.constants.Constant;
import it.poste.fdr.ecomm.be.model.BaseResponse;
import it.poste.fdr.ecomm.be.model.ErrorCode;
import it.poste.fdr.ecomm.be.model.Result;
import it.poste.fdr.ecomm.be.model.certificate.CertificateExt.CertificateKind;
import it.poste.fdr.ecomm.be.model.certificate.CertificateListResponse;
import it.poste.fdr.ecomm.be.model.certificate.ChangeAliasRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangeAliasResponse;
import it.poste.fdr.ecomm.be.model.certificate.ChangePhoneCheckRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangePhoneCheckResponse;
import it.poste.fdr.ecomm.be.model.certificate.ChangePhoneCompleteRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangePhoneStartRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangePhoneStartResponse;
import it.poste.fdr.ecomm.be.model.certificate.ChangePinCompleteRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangePinStartRequest;
import it.poste.fdr.ecomm.be.model.certificate.ChangePinStartResponse;
import it.poste.fdr.ecomm.be.model.certificate.PukStartRequest;
import it.poste.fdr.ecomm.be.model.certificate.PukStartResponse;
import it.poste.fdr.ecomm.be.model.certificate.ResetPinCompleteRequest;
import it.poste.fdr.ecomm.be.model.certificate.ResetPinStartRequest;
import it.poste.fdr.ecomm.be.model.certificate.ResetPinStartResponse;
import it.poste.fdr.ecomm.be.model.user.User;
import it.poste.fdr.ecomm.be.service.email.EmailSender;
import it.postecom.fdr.soap.v2.CertificateExt;
import it.postecom.fdr.soap.v2.CertificateResource;
import it.postecom.fdr.soap.v2.CertificateResult;
import it.postecom.fdr.soap.v2.TsInfo;


/**
 * @author TinariL
 *
 */
@Service
public class CertificateServiceImpl implements CertificateService {
	// logger
    private static final Logger log = LogManager.getLogger(CertificateServiceImpl.class);
       
//    @Value("${classpathTel}")
//	private String pathEmailTel;
//	 
//    @Value("${classpathPin}")
//     private String pathEmailPin;
	
	 @Autowired
	EmailSender emailSender;
    
    @Autowired
    ICommon fdrClient;

    @Autowired
    IFdrLayer utilsClient;
    
    @Autowired
    IPuk pukClient;
    
    @Value("${mock.fdr.common.client}")
    private boolean isCommonClientMock;
    
    
	@Override
	public boolean matchCertificatesCodicePratica(User user, String codicePratica) {
		
		// recupero i certificati da FDR
		CertificateListResponse certificateResponse = getCertificates(user);
		if(certificateResponse==null || 
				(Result.SUCCESS.getValue() != certificateResponse.getResult())) 
			return false;

		boolean find = false;
		List<it.poste.fdr.ecomm.be.model.certificate.CertificateExt> list = certificateResponse.getCertificates();
		for(it.poste.fdr.ecomm.be.model.certificate.CertificateExt cert : list) {
			if(codicePratica.equals(cert.getCodicePratica())) {
				find = true;
				break;
			}
		}
		if(!find) {
			log.info("matchCertificatesCodicePratica() - Codice pratica non appartenente all'utente",codicePratica);
			return false;
		}		
		return true;
	}
   
	
    /**
     * getCertificates
     */
	@Override
	public CertificateListResponse getCertificates(User user) {
		CertificateListResponse certificateResponse = new CertificateListResponse();
		certificateResponse.setResult(Result.ERROR.getValue());
		String codiceFiscale = user.getCodiceFiscale();
		if(StringUtils.isEmpty(codiceFiscale) && user.getUserBusiness()!=null) {
			codiceFiscale = user.getUserBusiness().getBusiness_taxcode();
		}
		
		log.info("getCertificates() codiceFiscale={}", Utils.asteriksString(codiceFiscale, 6));
		
		try {
			//
			// 1. chiamata a servizio soap common.getCertificateExtByFiscalCode di FDR
			//
			//by Liviana (22/06/2020)
			//sostituito il servizio getCertificate con il servizio getCertificateExtByFiscalCode
			CertificateResult fdrResponse = null;
			if(isCommonClientMock) {
				fdrResponse = mockGetCertificates(codiceFiscale);
			} else {
				fdrResponse = fdrClient.getCertificateExtByFiscalCode(codiceFiscale);
			}
			if (fdrResponse == null || fdrResponse.getResources().isEmpty()) {
				log.info("getCertificates() fdrResponse==null");
				certificateResponse.setResult(Result.ERROR.getValue());
				certificateResponse.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				certificateResponse.setErrorDescription("No response from FDR");
				return certificateResponse;
			}
			
			//
			// 2. build della response
			//
			ArrayList<it.poste.fdr.ecomm.be.model.certificate.CertificateExt> certificates = new ArrayList<>();
			for (CertificateResource certificateResource: fdrResponse.getResources()) {
				
				CertificateExt fdrCer = certificateResource.getCertificateExt();
				TsInfo tsInfo = certificateResource.getTsInfo();
				
				it.poste.fdr.ecomm.be.model.certificate.CertificateExt certificate = new it.poste.fdr.ecomm.be.model.certificate.CertificateExt();
				
				if (!tsInfo.isAllowTimestamp()) {
					certificate.setCertificateKind(CertificateKind.ONLY_SIGN);
				} else if (tsInfo.isAddedLater()) {
					certificate.setCertificateKind(CertificateKind.ADDED_TIMESTAMP);
				} else {
					certificate.setCertificateKind(CertificateKind.SIGN_AND_TIMESTAMP);
				}
				
				if (tsInfo.getActivationDate() != null) {
					XMLGregorianCalendar activationCalendar = tsInfo.getActivationDate();
					Date activationDate = activationCalendar.toGregorianCalendar().getTime();
					String activationDateFormat = DateHelper.formatDateToString(activationDate, DateHelper.DATE_FORMAT_P);
					certificate.setTimestampActivationDate(activationDateFormat);
				}

				if (fdrCer.getActivationDate()!=null) {
					XMLGregorianCalendar activationCalendar = fdrCer.getActivationDate();
					Date activationDate = activationCalendar.toGregorianCalendar().getTime();
					String activationDateFormat = DateHelper.formatDateToString(activationDate, DateHelper.DATE_FORMAT_P);
					certificate.setActivationDate(activationDateFormat);
				}
				
				if (fdrCer.getExpiryDate()!=null) {
					XMLGregorianCalendar expiryCalendar = fdrCer.getExpiryDate();
					Date expiryDate = expiryCalendar.toGregorianCalendar().getTime();
					String expiryDateFormat = DateHelper.formatDateToString(expiryDate, DateHelper.DATE_FORMAT_P);
					certificate.setExpiryDate(expiryDateFormat);
				}
				
				
				/**
				 * @author TinariL (10/12/2021)
				 * Iniziativa 123145 - Utente Servizi Fiduciari - Fase 1
				 * 
				 * Aggiunti i campi : 
				 * - crlDate: data sospensione/revoca in base allo stato
				 * - pukAlreadySet: mostrare il pulsante "Richiedi Puk" solo se false
				 * - x509: certificato in formato PEM (base64)
				 */
				if (fdrCer.getCrlDate()!=null) {
					XMLGregorianCalendar crlCalendar = fdrCer.getCrlDate();
					Date crlDate = crlCalendar.toGregorianCalendar().getTime();
					String crlDateFormat = DateHelper.formatDateToString(crlDate, DateHelper.DATE_FORMAT_P);
					certificate.setCrlDate(crlDateFormat);
				}
				certificate.setPukAlreadySet(fdrCer.isPukAlreadySet());
				certificate.setX509(fdrCer.getX509());
			
				certificate.setCodicePratica(Long.toString(fdrCer.getCodicePratica()));
				certificate.setEmail(fdrCer.getEmail());
				certificate.setFiscalCode(fdrCer.getFiscalCode());
				if(fdrCer.getSerial()!=null)
					certificate.setSerial((fdrCer.getSerial()));
				certificate.setStatus(fdrCer.getStatus());
				if(fdrCer.getId()!=null)
					certificate.setId(Long.toString(fdrCer.getId()));
				if(fdrCer.getUseLimits()!=null)
					certificate.setUseLimits(fdrCer.getUseLimits());
				certificate.setPhone(fdrCer.getPhone());
				String role = "-";
				if( !StringUtils.isBlank(fdrCer.getRole()) && !"null".equals(fdrCer.getRole()) ) {
					role = fdrCer.getRole();
				}
				certificate.setRole(role);
				certificate.setAlias(fdrCer.getAlias());
			
				certificates.add(certificate);
			}
			
			certificateResponse.setResult(Result.SUCCESS.getValue());
			certificateResponse.setCertificates(certificates);
		} catch (Exception e) {
			log.error("getCertificates() Exception: ", e);
			
			certificateResponse.setResult(Result.ERROR.getValue());
			certificateResponse.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			certificateResponse.setErrorDescription(e.getMessage());
		}finally{
			log.info("getCertificates() Response [{}]  ", Utils.jsonString(certificateResponse));
		}
		
		return certificateResponse;
	}

	/**
	 * changePinComplete
	 */
	@Override
	public BaseResponse changePinComplete(ChangePinCompleteRequest request, User user, String tid) {
		BaseResponse response = new BaseResponse();
		log.info("changePinComplete() START");
		
		try {
			//
			// 1. chiamata a servizio soap utils.changePinComplete di FDR
			//
			String resultCode = utilsClient.changePinComplete(request.getTaskId(), request.getOtp(), request.getOldPin(), request.getNewPin(), tid);
			if (resultCode == null || !resultCode.equalsIgnoreCase(UtilsClient.RESULT_CODE_SUCCESS)) {
				log.info("changePinComplete() tid={} resultCode={}", tid, resultCode);
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("response KO from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			//invio email
			emailSender.sendModificaPinEmail(request.getEmail(), "Modifica PIN Firma Digitale Remota", user.getNome(), user.getCognome());
		} catch (Exception e) {
			log.error("changePinComplete() tid={} - exception: ", tid, e);
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("changePinComplete() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}

	/**
	 * changePinStart
	 */
	@Override
	public ChangePinStartResponse changePinStart(ChangePinStartRequest request, User user, String tid) {
		ChangePinStartResponse response = new ChangePinStartResponse();
		
		try {
			long certSerial = TranslatorHelper.stringToLong(request.getCertSN());
			String codiceFiscale = user.getCodiceFiscale();
			if(StringUtils.isEmpty(codiceFiscale) && user.getUserBusiness()!=null) {
				codiceFiscale = user.getUserBusiness().getBusiness_taxcode();
			}
			//
			// 1. chiamata a servizio soap utils.changePinStart di FDR
			//
			//if(utilsClient2==null) utilsClient2 = new UtilsClient2();
			
			long taskId = utilsClient.changePinStart(request.getCertId(), certSerial, codiceFiscale, tid);
			if (taskId == -1) {
				log.info("changePinStart() fdrResponse==-1");
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("No response from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			response.setTaskId(taskId);
		} catch (Exception e) {
			log.error("changePinStart() tid={} - exception: ", tid, e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info(" changePinStart() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	
	/**
	 * changePhoneStart
	 */
	@Override
	public ChangePhoneStartResponse changePhoneStart(ChangePhoneStartRequest request, User user, String tid) {
		ChangePhoneStartResponse response = new ChangePhoneStartResponse();
		
		try {
			long certSerial = TranslatorHelper.stringToLong(request.getCertSN());
			String codiceFiscale = user.getCodiceFiscale();
			if(StringUtils.isEmpty(codiceFiscale) && user.getUserBusiness()!=null) {
				codiceFiscale = user.getUserBusiness().getBusiness_taxcode();
			}
			//
			// 1. chiamata a servizio soap utils.changePinStart di FDR
			//
			long taskId = utilsClient.changePhoneStart(request.getCertId(), certSerial,codiceFiscale, tid);
			if (taskId == -1) {
				log.info("changePhoneStart() tid={} - fdrResponse==-1", tid);
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("No response from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			response.setTaskId(taskId);
		} catch (Exception e) {
			log.error("changePhoneStart() tid={} - Exception: ", tid, e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("changePhoneStart() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	
	/**
	 * changePhoneCheck
	 */
	@Override
	public ChangePhoneCheckResponse changePhoneCheck(ChangePhoneCheckRequest request, User user, String tid) {
		ChangePhoneCheckResponse response = new ChangePhoneCheckResponse();
		String codiceFiscale = user.getCodiceFiscale();
		if(StringUtils.isEmpty(codiceFiscale) && user.getUserBusiness()!=null) {
			codiceFiscale = user.getUserBusiness().getBusiness_taxcode();
		}
		try {
			//
			// 1. chiamata a servizio soap directEnroll.getCertificateExtByPhoneNumber di FDR
			//
			log.info("changePhoneCheck() check Lista certificati associati a newPhoneNumber={}", request.getNewPhoneNumber());
			CertificateResult certificateResult = fdrClient.getCertificateExtByPhoneNumber( request.getNewPhoneNumber());
			if (!certificateResult.getResources().isEmpty()) {
				log.info("changePhoneCheck() check Lista non vuota associati a newPhoneNumber={}", request.getNewPhoneNumber());
                for (CertificateResource certificateResource: certificateResult.getResources()) {
                	CertificateExt cert = certificateResource.getCertificateExt();
                    if (!cert.getFiscalCode().equalsIgnoreCase(codiceFiscale)) {
        				log.error("changePhoneCheck() numero newPhoneNumber={} associato a CF={} - ", request.getNewPhoneNumber(),cert.getFiscalCode());
        				response.setResult(Result.ERROR.getValue());
        				response.setErrorCode(ErrorCode.ERR_FDR_PHONE_ALREADY_ASSIGNED.getValue());
        				response.setErrorDescription(String.format("Phone number %s already used by other user ",request.getNewPhoneNumber()));
        				return response;                
                    }
                }
			}
			
			//
			// 2. chiamata a servizio soap utils.changePinStart di FDR
			//
			long taskId = utilsClient.changePhoneCheck(request.getTaskId(), request.getPin(), request.getNewPhoneNumber(), request.getOtp(), tid);
			if (taskId == -1) {
				log.info("changePhoneCheck() tid={} - fdrResponse==-1", tid);
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("No response from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			response.setTaskId(taskId);
		} catch (Exception e) {
			log.error("changePhoneCheck() tid={} - Exception: ", tid, e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("changePhoneCheck() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	
	/**
	 * changePhoneComplete
	 */
	@Override
	public BaseResponse changePhoneComplete(ChangePhoneCompleteRequest request, User user, String tid) {
		BaseResponse response = new BaseResponse();
		
		try {
			//
			// 1. chiamata a servizio soap utils.changePhoneComplete di FDR
			//
			String resultCode = utilsClient.changePhoneComplete(request.getTaskId(), request.getOtp(), tid);
			if (resultCode == null || !resultCode.equalsIgnoreCase(UtilsClient.RESULT_CODE_SUCCESS)) {
				log.info("tid={} resultCode={}", tid, resultCode);
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("response KO from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			//invio email
			emailSender.sendModificaNumeroTelefono(request.getEmail(), "Modifica numero di cellulare Firma Digitale Remota", user.getNome(), user.getCognome());
		} catch (Exception e) {
			log.error("tid={} - exception: ", tid, e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info(" changePhoneComplete() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	


	/**
	 * @author TinariL
	 * 
	 * @param request
	 * @param user
	 * @param tid
	 * @return
	 */
	@Override
	public ChangeAliasResponse changeAlias(ChangeAliasRequest request, User user, String accessToken, String sp) {
		ChangeAliasResponse response = new ChangeAliasResponse();
		
		try {

			String alias = request.getAlias();
			long codicePratica = TranslatorHelper.stringToLong(request.getCodicePratica());

			//
			// 1. chiamata a servizio soap utils.changeAlias di FDR Vassoio
			//
			
			int rc = fdrClient.setAliasByCodicePratica(accessToken, sp, codicePratica, alias);

			if(rc==1) {
				response.setResult(Result.SUCCESS.getValue());
			}else {
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			}
		} catch (Exception e) {
			log.error("changeAlias() - exception: ", e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("changeAlias() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
  
	
	/**
	 * @author TinariL
	 * 
	 * @param request
	 * @param user
	 * @param tid
	 * @return
	 */
	@Override
	public ChangeAliasResponse changeAlias(ChangeAliasRequest request, User user) {
		ChangeAliasResponse response = new ChangeAliasResponse();
		
		try {

			String alias = request.getAlias();
			long codicePratica = TranslatorHelper.stringToLong(request.getCodicePratica());

			//
			// 1. chiamata a servizio soap utils.changeAlias di FDR Vassoio
			//
			
			int rc = fdrClient.setAliasByCodicePratica( codicePratica, alias);

			if(rc==1) {
				response.setResult(Result.SUCCESS.getValue());
			}else {
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			}
		} catch (Exception e) {
			log.error("changeAlias() - exception: ", e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("changeAlias() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	
	private CertificateResult mockGetCertificates(String cf) throws DatatypeConfigurationException {
		
		CertificateResult result = new CertificateResult();

		CertificateExt cert = new CertificateExt();
		Instant instant = Instant.now();
		ZonedDateTime dateTime = instant.atZone(ZoneId.of("Europe/Rome"));
	    GregorianCalendar c = GregorianCalendar.from(dateTime);
	    XMLGregorianCalendar activationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	    
	    Date dateExpires = new Date(2022, 12, 31);
	    Instant instanteExpires = dateExpires.toInstant();
	    ZonedDateTime dateTime2 = instanteExpires.atZone(ZoneId.of("Europe/Rome"));
	    GregorianCalendar c2 = GregorianCalendar.from(dateTime2);
	    XMLGregorianCalendar expirationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c2);
		
		cert.setActivationDate(activationDate);
		cert.setExpiryDate(expirationDate);
		cert.setCodicePratica(111l);
		cert.setEmail("test_certificate@poste.it");
		cert.setFiscalCode("MRSMRC80A01H501F");
		cert.setSerial("123456789");
		cert.setStatus("valid");
		cert.setId(10l);
		cert.setUseLimits("false");
		cert.setPhone("061456");
		cert.setRole("admin");
		cert.setAlias("alias");
		
		TsInfo tsInfo = new TsInfo();
		tsInfo.setAllowTimestamp(false);
		
		CertificateResource certificateResource = new CertificateResource();
		certificateResource.setCertificateExt(cert);
		certificateResource.setTsInfo(tsInfo);
		
		result.getResources().add(certificateResource);
		
		return result;
		
	}
	
	/**
	 * @author TinariL
	 * 
	 * resetPinStart
	 */
	@Override
	public ResetPinStartResponse resetPinStart(ResetPinStartRequest request, User user, String tid) {
		ResetPinStartResponse response = new ResetPinStartResponse();
		
		try {
			long certSerial = TranslatorHelper.stringToLong(request.getCertSN());
			String codiceFiscale = user.getCodiceFiscale();
			if(StringUtils.isEmpty(codiceFiscale) && user.getUserBusiness()!=null) {
				codiceFiscale = user.getUserBusiness().getBusiness_taxcode();
			}
			//
			// 1. chiamata a servizio soap utils.resetPinStart di FDR
			//

			long taskId = utilsClient.resetPinStart(request.getCertId(), certSerial, codiceFiscale, tid);
			if (taskId == -1) {
				log.info("resetPinStart() fdrResponse==-1");
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("No response from FDR");
				
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			response.setTaskId(taskId);
		} catch (Exception e) {
			log.error("resetPinStart() tid={} - exception: ", tid, e);
			
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info(" resetPinStart() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}
	
	/**
	 * @author TinariL
	 * 
	 * resetPinComplete
	 */
	@Override
	public BaseResponse resetPinComplete(ResetPinCompleteRequest request, User user, String tid) {
		BaseResponse response = new BaseResponse();
		log.info("resetPinComplete() START");
		
		try {
			//
			// 1. chiamata a servizio soap utils.resetPinComplete di FDR
			//
			String resultCode = utilsClient.resetPinComplete(request.getTaskId(), request.getOtp(), request.getPuk(), request.getNewPin(), tid);
			if (resultCode == null || !resultCode.equalsIgnoreCase(UtilsClient.RESULT_CODE_SUCCESS)) {
				log.info("resetPinComplete() tid={} resultCode={}", tid, resultCode);
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("response KO from FDR");
				
				switch (resultCode) {
				case Constant.PUK_ALREADY_SET:
					response.setErrorCode(ErrorCode.ERR_INVALID_PUK.getValue());
					response.setErrorDescription("FDR: Puk already set");
					break;
				case Constant.WRONG_PUK:
					response.setErrorCode(ErrorCode.ERR_INVALID_PUK.getValue());
					response.setErrorDescription("FDR: Wrong puk");
					break;
				case Constant.OTP_INSERT_ATTEMPTS_LIMIT:
					response.setErrorCode(ErrorCode.ERR_INVALID_OTP.getValue());
					response.setErrorDescription("FDR: Otp insert attempts limit reached");
					break;
				case Constant.WRONG_OTP:
					response.setErrorCode(ErrorCode.ERR_INVALID_OTP.getValue());
					response.setErrorDescription("FDR: Wrong OTP");
					break;
				default:
					break;
				}
			
				return response;
			}
			
			response.setResult(Result.SUCCESS.getValue());
			//invio email
			//emailSender.sendModificaPinEmail(request.getEmail(), "Reset PIN Firma Digitale Remota", user.getNome(), user.getCognome());
		} catch (Exception e) {
			log.error("resetPinComplete() tid={} - exception: ", tid, e);
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("resetPinComplete() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}

	
	/**
	 * @author TinariL
	 * 
	 * requestPuk
	 */
	@Override
	public PukStartResponse requestPuk(PukStartRequest request, User user) {
		PukStartResponse response = new PukStartResponse();
		log.info("requestPuk() START");
		String codiceFiscale = user.getCodiceFiscale();
		
		try {
			//
			// 1. chiamata a servizio soap utils.resetPinComplete di FDR
			//
			//
			long codicePratica = TranslatorHelper.stringToLong(request.getCodicePratica());
			PukRequestResponse pukResponse = pukClient.pukRequest(codiceFiscale, request.getCertId(), request.getCertSN(), codicePratica);
			if (pukResponse == null || (pukResponse.getResult() == Result.ERROR.getValue())) {
				
				log.info("requestPuk() ", Utils.jsonString(pukResponse));
				
				response.setResult(Result.ERROR.getValue());
				response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
				response.setErrorDescription("response KO from FDR");
				
				return response;
			}

			response.setRedirectUrl(pukResponse.getRedirectUrl());
			response.setResult(Result.SUCCESS.getValue());
			
		} catch (Exception e) {
			log.error("requestPuk() - exception: ",  e);
			response.setResult(Result.ERROR.getValue());
			response.setErrorCode(ErrorCode.ERR_EXT_SERVICE.getValue());
			response.setErrorDescription(e.getMessage());
		}finally{
			log.info("requestPuk() :: [FDR] :: Response [{}]  ", Utils.jsonString(response));
		}
		
		return response;
	}

}
