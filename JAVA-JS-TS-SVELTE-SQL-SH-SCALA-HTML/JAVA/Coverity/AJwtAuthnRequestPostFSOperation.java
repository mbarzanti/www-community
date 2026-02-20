package org.jod.idp.web.jwtoperation;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.EncryptedJWT;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.jod.base.jose.crypto.JodRSASSASigner;
import org.jod.base.keystore.IKeyStoreReader;
import org.jod.base.keystore.IKeyStoreService;
import org.jod.base.log.ILoggerService;
import org.jod.base.message.IMessage;
import org.jod.base.message.PrefixMessage;
import org.jod.base.realm.IRealmService;
import org.jod.base.utils.GenericHolder;
import org.jod.client.redis.IJodRedisClient;
import org.jod.idp.MainApplicationProxyProvider;
import org.jod.idp.MainApplicationServiceLocator;
import org.jod.idp.config.IMainApplicationConfig;
import org.jod.idp.config.IMainFederationConfig;
import org.jod.idp.config.IXMLSigner;
import org.jod.idp.config.ManagedAttribute;
import org.jod.idp.federation.IFederationCertificatesService;
import org.jod.idp.metadata.EntityDescriptorReaderHolder;
import org.jod.idp.metadata.OpenIdConnectClientConfigurationReader;
import org.jod.idp.metadata.ServiceHolder;
import org.jod.idp.metadata.dynamincs.IMetadataLoader;
import org.jod.idp.realm.UserAttributesSearcher;
import org.jod.idp.utils.AntiXSS;
import org.jod.idp.utils.ChallengeStore;
import org.jod.idp.utils.Encoding;
import org.jod.idp.utils.HashCalculator;
import org.jod.idp.utils.IUserTokenConfig;
import org.jod.idp.utils.KeyIdentifierHelper;
import org.jod.idp.utils.TokenAttributeHolder;
import org.jod.idp.utils.Utils;
import org.jod.idp.web.UrlEncoderHelper;
import org.jod.idp.web.UtilIpRemote;
import org.jod.idp.web.jwtoperation.CachedJWTAttributeWriter;
import org.jod.idp.web.jwtoperation.IValueHolder;
import org.jod.idp.web.jwtoperation.JWTAttributeWriter;
import org.jod.idp.web.jwtoperation.JWTTokenIssued;
import org.jod.idp.web.jwtoperation.JwsObjectHolder;
import org.jod.idp.web.jwtoperation.SignSecureHolderData;
import org.jod.idp.web.jwtoperation.StringValueHolder;
import org.jod.idp.web.openidoperation.ITokenStrategyWriter;
import org.jod.idp.web.operation.AFSOperation;
import org.jod.idp.web.operation.IFSOperation;
import org.jod.idp.web.operation.IResponseHolder;
import org.jod.idp.web.securetool.challenge.ParseCfFromX509;
import org.jod.idp.web.securetool.signature.VerifyJWTSignedChallenge;
import org.jod.realm.IAccessorProvider;
import org.jod.realm.IClientIp;
import org.jod.realm.data.AdminUserSearcher;
import org.jod.realm.data.ClientIp;
import org.jod.realm.data.PasswordAuthenticationPolicy;
import org.jod.realm.data.User;
import org.jod.realm.data.UserViewer;
import org.jod.services.userdata.UserAppService;
import org.json.JSONArray;
import org.json.JSONObject;











































































































































public abstract class AJwtAuthnRequestPostFSOperation
  extends AFSOperation
  implements IFSOperation
{
  public static final String BEHALF_OF = "behalf_of";
  public static final String BEHALF_OF_AS_STRING = "behalf_of_as_string";
  public static final String SESSION_LOA0_PRINCIPAL_ANONYMOUS = "anonymous";
  public static final String REQ_OIC_SCOPE_OPENID = "openid";
  public static final String REQ_OIC_SCOPE_OFFLINE_ACCESS = "offline_access";
  public static final String REQUIRED = "_required";
  public static final String INVALID = "invalid_";
  public static final String REQ_OIC_BINDING = "binding";
  public static final String REQ_OIC_BINDING_POST = "http-post";
  public static final String REQ_OIC_BINDING_REDIRECT = "http-redirect";
  public static final String REQ_USERTOKEN = "usertoken";
  public static final String REQ_ENTITYID = "entityid";
  public static final String REQ_SERVICEID = "serviceid";
  public static final String REQ_OIC_RESPONSE_TYPE = "response_type";
  public static final String REQ_OIC_RESPONSE_TYPE_VALUE_NONE = "none";
  public static final String REQ_OIC_SCOPE = "scope";
  public static final String REQ_OIC_CLIENT_ID = "client_id";
  public static final String REQ_OIC_SESSION_INDEX = "sid";
  public static final String REQ_OIC_PSESSION_INDEX = "psid";
  public static final String REQ_OIC_NONCE = "nonce";
  public static final String REQ_OIC_STATE = "state";
  public static final String REQ_OIC_R_STATE = "r_state";
  public static final String REQ_OIC_STATE_HASH = "s_hash";
  public static final String REQ_OIC_FM_HASH = "fm_hash";
  public static final String REQ_OIC_REDIRECT_URI = "redirect_uri";
  public static final String REQ_OIC_CHANGE_PASSWORD_URI = "cp_uri";
  public static final String REQ_OIC_CHANGE_PASSWORD_KEY = "code";
  public static final String REQ_OIC_CLIENT_ASSERTION_TYPE = "client_assertion_type";
  public static final String REQ_OIC_CLIENT_ASSERTION = "client_assertion";
  public static final String REQ_OIC_REQUEST = "request";
  public static final String REQ_OIC_FM = "fm";
  public static final String REQ_OIC_ACT = "act";
  public static final String REQ_OIC_ACR_VALUES = "acr_values";
  public static final String REQ_OIC_ACR = "acr";
  public static final String REQ_OIC_AMR = "amr";
  public static final String REQ_OIC_ACR_TOOL = "acr_tool";
  public static final String REQ_OIC_ACR_TOOL_SHORT = "at";
  public static final String REQ_OIC_DISPLAY = "display";
  public static final String REQ_OIC_DISPLAY_VALUE_POPUP = "popup";
  public static final String REQ_OIC_PROMPT = "prompt";
  public static final String REQ_OIC_GRANT_TYPE = "grant_type";
  public static final String REQ_OIC_RESPONSE_TYPE_VALUE_ID_TOKEN = "id_token";
  public static final String REQ_OIC_RESPONSE_TYPE_VALUE_CODE = "code";
  public static final String REQ_OIC_SCOPE_VALUE = "openid";
  public static final String REQ_OIC_CLIENT_ASSERTION_TYPE_VALUE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
  public static final String REQ_OIC_ID_TOKEN_VALUE = "urn:ietf:params:oauth:id-token:jwt-bearer";
  public static final String REQ_OIC_DISPLAY_VALUE_NONE = "none";
  public static final String REQ_OIC_DISPLAY_VALUE_PAGE = "page";
  public static final String REQ_OIC_PROMPT_VALUE_NONE = "none";
  public static final String REQ_OIC_PROMPT_VALUE_LOGIN = "login";
  public static final String REQ_OIC_PROMPT_VALUE_CONSENT = "consent";
  public static final String REQ_OIC_PROMPT_VALUE_SELECT_ACCOUNT = "select_account";
  public static final String REQ_OIC_PROMPT_VALUE_FORCEAUTH = "forceauth";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_PASSWORD = "password";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_SIGNED_CHALLENGE_ARCOT = "signed_challenge_arcot";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_SIGNED_CHALLENGE = "signed_challenge";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_SIGNED_CHALLENGE_HMAC_JWS = "https://idp-poste.poste.it/grant_type/hmac_jws_signature";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_REG_SIGNED_CHALLENGE = "reg_signed_challenge";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_REFRESH_TOKEN = "refresh_token";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_AUTHORIZATION_CODE = "authorization_code";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_SESSION_EXCHANGE = "session_exchange";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_AMR_RENEWAL = "https://idp-poste.poste.it/amr/amr_renewal";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_DELEGATION_PROTECTED_SUBJECT_ADD = "https://idp-poste.poste.it/delegation/protected_subject/add";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_DELEGATION_PROTECTED_SUBJECT_REMOVE = "https://idp-poste.poste.it/delegation/protected_subject/remove";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_PASSKEY_SIGNED_CHALLENGE = "passkey_signed_challenge";
  public static final String REQ_CUSTOM_ACE_TRACE_ECOBONUS = "trace_ecobonus";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_HTTP_ENRICHMENT_HEADER = "http_enrichment_header";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_REG_APP_ID = "reg_appid_challenge";
  public static final String REQ_OIC_GRANT_TYPE_VALUE_SESSION = "session";
  public static final String REFRESH_TOKEN_HASH = "rt_hash";
  public static final String RES_OIC_CODE = "code";
  public static final String RES_OIC_CODE_AS_HEADER = "X-CODE";
  public static final String RES_OIC_CODE_HASH = "c_hash";
  public static final String RES_OIC_ID_TOKEN = "id_token";
  public static final String RES_OIC_RESPONSE_MODE = "response_mode";
  public static final String RES_OIC_RESPONSE_MODE_QUERY = "query";
  public static final String RES_OIC_RESPONSE_MODE_FORM_POST = "form_post";
  public static final String REQ_OIC_TOKEN = "token";
  public static final String REQ_OIC_TOKEN_HINT = "token_hint";
  public static final String REQ_OIC_TOKEN_HINT_REQUIRED = "token_hint_required";
  public static final String REQ_OIC_TOKEN_TYPE_HINT = "token_type_hint";
  public static final String REQ_OIC_LOGIN_HINT = "login_hint";
  public static final String REQ_OIC_DEFAULT_GROUP = "default_group";
  public static final String REQ_OIC_DEFAULT_GROUP_SID = "default_group_status";
  public static final String REQ_OIC_DEFAULT_ROLE = "default_role";
  public static final String REQ_OIC_CODE_CHALLENGE = "code_challenge";
  public static final String REQ_OIC_CODE_CHALLENGE_METHOD = "code_challenge_method";
  public static final String REQ_OIC_CODE_CHALLENGE_METHOD_VALUE_PLAIN = "plain";
  public static final String REQ_OIC_CODE_CHALLENGE_METHOD_VALUE_S256 = "S256";
  public static final String REQ_OIC_CODE_VERIFIER = "code_verifier";
  public static final String REQ_OIC_CODE_VERIFIER_AS_HEADER = "X-CODE-VERIFIER";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String ID_TOKEN_SIGNED = "id_token_signed";
  public static final String ACCESS_TOKEN_EXPIRE_IN = "expires_in";
  public static final String ACCESS_TOKEN_HASH = "at_hash";
  public static final String ACCESS_TOKEN_DIRECT_KEY = "at_dkey";
  public static final String ACCESS_TOKEN_DIRECT_KEY_TYPE = "at_dkeyty";
  public static final String SCOPE_OPENID = "openid";
  public static final String JWT_ISS = "iss";
  public static final String JWT_SUB = "sub";
  public static final String JWT_AUD = "aud";
  public static final String JWT_AZP = "azp";
  public static final String JWT_EXP = "exp";
  public static final String JWT_IAT = "iat";
  public static final String JWT_NBF = "nbf";
  public static final String JWT_JTI = "jti";
  public static final String JWT_LAT = "lat";
  public static final String JWT_LAT_EXP = "lat_exp";
  public static final String JWT_ACT = "act";
  public static final String JWT_ACT_SUB = "act_sub";
  public static final String JWT_STATUS_RESPONSE = "status";
  public static final String JWT_STATUS_RESPONSE_VALUE_OK = "success";
  public static final String JWT_STATUS_RESPONSE_VALUE_KO = "error";
  public static final String JWT_STATUS_RESPONSE_ERROR_CODE = "error";
  public static final String ERROR = "error";
  public static final String RES_OIC_NOBODY_PRINCIPAL = "nobody";
  public static final String RES_OIC_AUTHENTICATED_PRINCIPAL = "authenticated";
  public static final String REQ_OIC_SUBJECT_TOKEN = "subject_token";
  public static final String REQ_OIC_SUBJECT_TOKEN_TYPE = "subject_token_type";
  public static final String REQ_OIC_SUBJECT_TOKEN_GRANT_TYPE = "urn:ietf:params:oauth:token-type:access_token";
  public static final String REQ_OIC_TOKEN_EXACHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
  public static final String REQ_STS_TOKEN_TYPE_JWT_VALUE = "urn:ietf:params:oauth:token-type:jwt";
  public static final String REQ_PLAIN_CHALLENGE_PUSH = "plain_challenge_push";
  public static final String REQ_OIC_DEBTOR_ACCOUNT_ALIAS = "debtorAccountAlias";
  public static final String KEY_RSA_DATA = "rsa";
/*  363 */   public static Map errorsdescription = new LinkedHashMap<>();


  
  public static final String ERROR_CODE_AUD_REQUIRED = "CODE-0001";


  
  public static final String ERROR_CODE_AUD_VALIDATION_FAILED = "CODE-0002";


  
  public static final String ERROR_CODE_REDIRECT_URL_REQUIRED = "CODE-0003";


  
  public static final String ERROR_CODE_REDIRECT_URL_VALIDATION_FAILED = "CODE-0004";


  
  public static final String ERROR_CODE_GRANT_TYPE_REQUIRED = "CODE-0005";


  
  public static final String ERROR_CODE_GRANT_TYPE_VALIDATION_FAILED = "CODE-0006";

  
  public static final String ERROR_CODE_PROMPT_REQUIRED = "CODE-0007";

  
  public static final String ERROR_CODE_PROMPT_VALIDATION_FAILED = "CODE-0008";

  
  public static final String ERROR_CODE_ACR_VALUES_REQUIRED = "CODE-0009";

  
  public static final String ERROR_CODE_ACR_VALUES_VALIDATION_FAILED = "CODE-0010";

  
  public static final String ERROR_CODE_IAT_VALIDATION_FAILED = "CODE-0011";

  
  public static final String ERROR_CODE_NBF_VALIDATION_FAILED = "CODE-0012";

  
  public static final String ERROR_CODE_EXP_VALIDATION_FAILED = "CODE-0013";

  
  public static final String ERROR_ASSERTION_ACE_VALIDATION_FAILED = "CODE-0014";

  
  public static final String ERROR_CODE_RESPONSE_TYPE_REQUIRED = "CODE-0015";

  
  public static final String ERROR_CODE_RESPONSE_TYPE_VALIDATION_FAILED = "CODE-0016";

  
  public static final String ERROR_CODE_CODE_REQUIRED = "CODE-0017";

  
  public static final String ERROR_CODE_NONCE_REQUIRED = "CODE-0018";

  
  public static final String ERROR_CODE_JTI_REQUIRED = "CODE-0019";

  
  public static final String ERROR_CODE_CLIENT_ASSERTIONTYPE_NOT_VALID = "CODE-0020";

  
  public static final String ERROR_REFRESH_TOKEN_REQUIRED = "CODE-0021";

  
  public static final String ERROR_CODE_ACR_VALUES_NON_VALID = "CODE-1000";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_PASSWORD_LOGIN_FAILED = "CODE-1001";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_PASSWORD_LOGIN_DISABLED = "CODE-1002";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_PASSWORD_LOGIN_ENABLED_CHANGEPASSWORDREQUIRED = "CODE-1003";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_PASSWORD_LOGIN_DISABLED_CHANGEPASSWORDREQUIRED = "CODE-1004";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_PASSWORD_LOGIN_SUPENDED = "CODE-1005";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_REFRESH_TOKEN_SESSION_NOT_FOUND = "CODE-1006";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_REFRESH_TOKEN_NOT_VALID = "CODE-1007";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_REFRESH_TOKEN_NOT_VALID_FOR_LEVEL = "CODE-1008";

  
  public static final String ERROR_CODE_ACCESS_TOKEN_GENERATION = "CODE-1009";

  
  public static final String ERROR_CODE_ACCESS_TOKEN_VALIDATION = "CODE-1010";

  
  public static final String ERROR_CODE_ACCESS_TOKEN_NOT_FOUND_IN_CACHE = "CODE-1011";

  
  public static final String ERROR_CODE_ACCESS_TOKEN_NOT_FOUND = "CODE-1012";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_ACCESS_TOKEN_NOT_VALID = "CODE-1013";

  
  public static final String ERROR_CODE_GRANT_TYPE_VALUE_ACCESS_TOKEN_NOT_VALID_FOR_LEVEL = "CODE-1014";

  
  public static final String ERROR_CODE_ACCESS_TOKEN_GENERATION_SESSION_NOT_VALID = "CODE-1015";

  
  public static final String ERROR_CODE_PARTIAL_LOGOUT = "CODE-1016";

  
  public static final String ERROR_CODE_REFRESH_TOKEN_NOT_VALID = "CODE-1017";

  
  public static final String ERROR_CODE_SYSTEM_REDIR_ERROR_WRITE = "CODE-2000";

  
  public static final String ERROR_CODE_SYSTEM_REQUEST_NOT_VALID = "CODE-2001";

  
  public static final String REQ_STS_RESOURCE = "resource";

  
  public static final String REQ_VALUE_OIC_ACR_TOOL_PV = "PV";

  
  private IMessage message;

  
  private IMainFederationConfig _federationConfig;


  
  protected AJwtAuthnRequestPostFSOperation() {
/*  509 */     this.message = ((ILoggerService)(new MainApplicationProxyProvider()).service("loggerService")).message();
/*  510 */     this._federationConfig = MainApplicationServiceLocator.mainFederationServiceConfig();

    
/*  513 */     errorsdescription.put("CODE-0001", "attributo aud richiesto");
/*  514 */     errorsdescription.put("CODE-0002", "attributo aud non valido");
/*  515 */     errorsdescription.put("CODE-0003", "attributo redirect_url richiesto");
/*  516 */     errorsdescription.put("CODE-0004", "attributo redirect_url non valido");
/*  517 */     errorsdescription.put("CODE-0005", "attributo grant_type non richiesto");
/*  518 */     errorsdescription.put("CODE-0006", "attributo grant_type non valido");
/*  519 */     errorsdescription.put("CODE-0007", "attributo prompt richiesto");
/*  520 */     errorsdescription.put("CODE-0008", "attributo prompt non valido");
/*  521 */     errorsdescription.put("CODE-0009", "attributo acr_values richiesto");
/*  522 */     errorsdescription.put("CODE-0010", "attributo acr_values non valido");
/*  523 */     errorsdescription.put("CODE-0011", "attributo iat non valido o non presente");
/*  524 */     errorsdescription.put("CODE-0012", "attributo nbf non valido o non presente");
/*  525 */     errorsdescription.put("CODE-0013", "attributo exp non valido o non presente");
/*  526 */     errorsdescription.put("CODE-0014", "asserzione di richiesta non valida per le ace configurate");
/*  527 */     errorsdescription.put("CODE-0015", "attributo response_type richiesto");
/*  528 */     errorsdescription.put("CODE-0016", "attributo response_type non valido");
/*  529 */     errorsdescription.put("CODE-0017", "attributo code richiesto");
/*  530 */     errorsdescription.put("CODE-0018", "attributo nonce richiesto");
/*  531 */     errorsdescription.put("CODE-0019", "attributo jti richiesto");
/*  532 */     errorsdescription.put("CODE-0020", "client assertion type non valida o non specificata");
/*  533 */     errorsdescription.put("CODE-0021", "attributo refresh_token richiesto");


    
/*  537 */     errorsdescription
/*  538 */       .put("CODE-1000", "acr_values non valide per il tipo di autenticazione prompt=none ammesso il livello di autenticazione effettuato e' minore di quello richiesto");
    
/*  540 */     errorsdescription.put("CODE-1001", "user authentication failed, grant_type=password");
/*  541 */     errorsdescription.put("CODE-1002", "user disabled, grant_type=password");
/*  542 */     errorsdescription.put("CODE-1003", "user change password required, grant_type=password");
/*  543 */     errorsdescription.put("CODE-1004", "user change password required, grant_type=password");
/*  544 */     errorsdescription.put("CODE-1005", "user supended, grant_type=password");
/*  545 */     errorsdescription.put("CODE-1006", "login richiesto, grant_type=refresh_token");
/*  546 */     errorsdescription.put("CODE-1007", "code not valid, grant_type=refresh_token");
/*  547 */     errorsdescription.put("CODE-1008", "code not valid, grant_type=refresh_token");
/*  548 */     errorsdescription.put("CODE-1009", "impossibile generare access_token");
/*  549 */     errorsdescription.put("CODE-1010", "impossibile validare access_token");
/*  550 */     errorsdescription.put("CODE-1011", "access_token non presente in cache");
/*  551 */     errorsdescription.put("CODE-1012", "access_token richiesto");
    
/*  553 */     errorsdescription.put("CODE-1013", "code required, grant_type=access_token");
/*  554 */     errorsdescription.put("CODE-1014", "code not valid, grant_type=access_token");
    
/*  556 */     errorsdescription.put("CODE-1015", "generazione access_token. sessione non valida");
    
/*  558 */     errorsdescription.put("CODE-1016", "logout parziale sessione non valida");
/*  559 */     errorsdescription.put("CODE-1017", "refresh_token non valido");

    
/*  562 */     errorsdescription.put("CODE-2000", "idp generic error");
/*  563 */     errorsdescription.put("CODE-2001", "request non valida");
  }

  
  public abstract void exec(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, IResponseHolder paramIResponseHolder) throws Exception;

  
  protected void verifySignAndDeCryptClientAssertion(IResponseHolder aPageRedirectHolder, String token, JwsObjectHolder jwsObjectHolder, IMetadataLoader spmetadata, IMessage message) {
/*  571 */     verifySignAndDeCryptClientAssertion(aPageRedirectHolder, token, jwsObjectHolder, spmetadata, message, null);
  }

  
  protected void verifySignAndDeCryptClientAssertion(IResponseHolder aPageRedirectHolder, String token, JwsObjectHolder jwsObjectHolder, IMetadataLoader spmetadata, IMessage message, String extissuer) {
/*  576 */     verifySignAndDeCryptClientAssertion(aPageRedirectHolder, token, jwsObjectHolder, spmetadata, message, extissuer, MainApplicationServiceLocator.mainFederationServiceConfig().isAcceptEncrypted());
  }


  
  public void verifySignAndDeCryptClientAssertion(IResponseHolder aPageRedirectHolder, String token, JwsObjectHolder jwsObjectHolder, IMetadataLoader spmetadata, IMessage message, String extissuer, boolean isEncrypt) {
/*  582 */     IMainFederationConfig federationConfig = MainApplicationServiceLocator.mainFederationServiceConfig();
/*  583 */     IMainApplicationConfig mainConfig = MainApplicationServiceLocator.mainConfig();
    
/*  585 */     if (Utils.isEmpty(token)) {
      
/*  587 */       message.error(getClass().getName() + " client_assertion NULLO ");
/*  588 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  589 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  590 */       aPageRedirectHolder.setMessage("Formato richiesta non corretto - Contattare il gestore del servizio");
/*  591 */       jwsObjectHolder.setStatus(false);
/*  592 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
    } 
    
/*  595 */     IKeyStoreService keyStoreService = MainApplicationServiceLocator.keyStoreService();
/*  596 */     IKeyStoreReader keyreader = keyStoreService.keyStoreReader();
    
/*  598 */     String jwtTokenSigned = "";

    
/*  601 */     if (isEncrypt) {
      try {
/*  603 */         IFederationCertificatesService federationCertificatesService = MainApplicationServiceLocator.federationCertificatesService();
/*  604 */         EncryptedJWT jwt = EncryptedJWT.parse(token);
/*  605 */         federationCertificatesService.decrypt(jwt);
        
/*  607 */         jwtTokenSigned = jwt.getPayload().toString();
/*  608 */         message.debug(getClass().getName() + " JWTAUTHNREQUEST DECIFRATO [" + getClass().getName() + "]");
/*  609 */         message.debug(getClass().getName() + " JWTAUTHNREQUEST DECIFRATO HEADER [" + getClass().getName() + "]");
      }
/*  611 */       catch (Exception x) {
        
/*  613 */         message.error(getClass().getName() + " VERIFICA CIFRATURA JWTAUTHNREQUEST FALLITA ", x);
/*  614 */         jwtTokenSigned = "";
      }
    
    } else {
      
/*  619 */       jwtTokenSigned = token;
    } 


    
/*  624 */     JWSObject jwsObjectSigned = null;



    
    try {
/*  630 */       if (jwtTokenSigned.startsWith("\"") && jwtTokenSigned.endsWith("\"")) {
/*  631 */         message.debug(getClass().getName() + " IL TOKEN FIRMATO [" + getClass().getName() + "] CONTIENE LE VIRGOLETTE LE TOLGO ");
/*  632 */         jwtTokenSigned = jwtTokenSigned.substring(1, jwtTokenSigned.length() - 1);
      } 
      
/*  635 */       jwsObjectHolder.setRequestSigned(jwtTokenSigned);
      
/*  637 */       jwsObjectSigned = JWSObject.parse(jwtTokenSigned);
    
    }
/*  640 */     catch (Exception e) {
/*  641 */       message.error(getClass().getName() + " VERIFICA FIRMA JWTAUTHNREQUEST FALLITA ", e);
/*  642 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  643 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  644 */       aPageRedirectHolder.setMessage("Formato richiesta non corretto - Contattare il gestore del servizio");
/*  645 */       jwsObjectHolder.setStatus(false);
/*  646 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);

      
      return;
    } 
    
/*  652 */     JSONObject headerjson = jwsObjectSigned.getHeader().toJSONObject();
    
/*  654 */     if (!JWSAlgorithm.RS256.getName().equals(headerjson.get("alg")) && !JWSAlgorithm.RS384.getName().equals(headerjson.get("alg")) && 
/*  655 */       !JWSAlgorithm.RS512.getName().equals(headerjson.get("alg"))) {
/*  656 */       message.error(getClass().getName() + " ALGORITMO DI FIRMA DICHIARATO NELL'HEADER NON ACCETTATO  [" + getClass().getName() + "] ACCETTATI [" + (String)headerjson.get("alg") + ", " + JWSAlgorithm.RS256
/*  657 */           .getName() + ", " + JWSAlgorithm.RS384.getName() + "]");
/*  658 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  659 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  660 */       aPageRedirectHolder.setMessage("Formato richiesta non ricevibile - Contattare il gestore del servizio");
/*  661 */       jwsObjectHolder.setStatus(false);
/*  662 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);

      
      return;
    } 
    
/*  668 */     String authnReqISS = null;
/*  669 */     if (Utils.isEmpty(extissuer)) {
/*  670 */       Payload payload = jwsObjectSigned.getPayload();
/*  671 */       JSONObject paylodToJson = payload.toJSONObject();
/*  672 */       message.debug(getClass().getName() + " JWTAUTHNREQUEST FIRMATO [" + getClass().getName() + "]");
      
/*  674 */       if (!paylodToJson.containsKey("iss")) {
/*  675 */         message.error(getClass().getName() + " JWTAUTHNREQUEST FIRMATO ISS NULLO");
/*  676 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  677 */         aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  678 */         jwsObjectHolder.setStatus(false);
/*  679 */         jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
        return;
      } 
/*  682 */       authnReqISS = (String)paylodToJson.getOrDefault("iss", "");
    } else {
/*  684 */       authnReqISS = extissuer;
    } 

    
/*  688 */     message.debug(getClass().getName() + " JWTAUTHNREQUEST FIRMATO ISS [" + getClass().getName() + "]");

    
/*  691 */     JSONObject spEntityDescriptor = spmetadata.clientConfiguration(authnReqISS);
/*  692 */     if (spEntityDescriptor == null) {
/*  693 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] NON TRUSTATO ");
/*  694 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  695 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  696 */       jwsObjectHolder.setStatus(false);
/*  697 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
      
      return;
    } 
/*  701 */     EntityDescriptorReaderHolder entityDescriptorReaderHolder = new EntityDescriptorReaderHolder();
/*  702 */     (new OpenIdConnectClientConfigurationReader(spEntityDescriptor, entityDescriptorReaderHolder, message)).read();
    
/*  704 */     if (!entityDescriptorReaderHolder.isValid()) {
/*  705 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] PARSER DEI METADATI FALLITA ");
/*  706 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  707 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  708 */       jwsObjectHolder.setStatus(false);
/*  709 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
      
      return;
    } 
/*  713 */     String keyid = jwsObjectSigned.getHeader().getKeyID();
    
/*  715 */     if (Utils.isEmpty(keyid)) {
/*  716 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA KEYID NON PRESENTE NELLA RICHIESTA");
/*  717 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  718 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  719 */       jwsObjectHolder.setStatus(false);
/*  720 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
      
      return;
    } 
/*  724 */     if (!entityDescriptorReaderHolder.getPubKeySigning().containsKey(keyid)) {
      
/*  726 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA KEYID DELLA RICHIESTA [" + authnReqISS + "] NON PRESENTE NELL'ELENCO DELLE CHIAVI NEI METADATI");
      
/*  728 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  729 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  730 */       jwsObjectHolder.setStatus(false);
/*  731 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
      
      return;
    } 
/*  735 */     PublicKey pubkey = (PublicKey)entityDescriptorReaderHolder.getPubKeySigning().get(keyid);
    
/*  737 */     message.debug(getClass().getName() + " ENTITYID [" + getClass().getName() + "] KEYID PER VERFICA FIRMA  [" + authnReqISS + "] PUB KEY [" + keyid + "]");
/*  738 */     boolean signverfied = false;
    
/*  740 */     RSASSAVerifier rSASSAVerifier = new RSASSAVerifier((RSAPublicKey)pubkey);
    try {
/*  742 */       signverfied = jwsObjectSigned.verify((JWSVerifier)rSASSAVerifier);
/*  743 */       if (!signverfied) {
/*  744 */         message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA ");
/*  745 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  746 */         aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  747 */         jwsObjectHolder.setStatus(false);
/*  748 */         jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
        
        return;
      } 
/*  752 */     } catch (Exception e) {
/*  753 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA ", e);
/*  754 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  755 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  756 */       jwsObjectHolder.setStatus(false);
/*  757 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);


      
      return;
    } 

    
/*  765 */     if (!entityDescriptorReaderHolder.getCertificateSigning().containsKey(keyid)) {
/*  766 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA NON ESISTE UN CERTIFICATO (x5c) ASSOCIATO AL KEYID [" + authnReqISS + "] UTILIZZATO PER VERFICARE LA FIRMA");
      
/*  768 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  769 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  770 */       jwsObjectHolder.setStatus(false);
/*  771 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);
      
      return;
    } 
/*  775 */     X509Certificate cert = (X509Certificate)entityDescriptorReaderHolder.getCertificateSigning().get(keyid);


    
/*  779 */     message.debug(getClass().getName() + " ENTITYID [" + getClass().getName() + "] CERTIFICATO ASSOCIATO AL KEYID [" + authnReqISS + "]");
/*  780 */     message.debug(cert.toString());
/*  781 */     message.debug(getClass().getName() + " ENTITYID [" + getClass().getName() + "] CERTIFICATO VALIDO");
/*  782 */     boolean certcheckValidity = true;
    try {
/*  784 */       cert.checkValidity();
    }
/*  786 */     catch (Exception x) {
/*  787 */       certcheckValidity = false;
    } 
/*  789 */     message.info(getClass().getName() + " ENTITYID [" + getClass().getName() + "] CERTIFICATO ASSOCIATO AL KEYID [" + authnReqISS + "] VALIDO [" + keyid + "]");

    
/*  792 */     if (!certcheckValidity) {
/*  793 */       message.error(getClass().getName() + " ENTITYID [" + getClass().getName() + "] IMPOSSIBILE VERIFICARE LA FIRMA IL CERTIFICATO (x5c) ASSOCIATO AL KEYID [" + authnReqISS + "] UTILIZZATO PER VERFICARE LA FIRMA E' SCADUTO");
      
/*  795 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/*  796 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/*  797 */       jwsObjectHolder.setStatus(false);
/*  798 */       jwsObjectHolder.setaPageRedirectHolder(aPageRedirectHolder);











      
      return;
    } 










    
/*  824 */     message.debug(getClass().getName() + "  JWTAUTHNREQUEST HEADER  [" + getClass().getName() + "]");
/*  825 */     message.debug(getClass().getName() + "  JWTAUTHNREQUEST PAYLOAD [" + getClass().getName() + "]");
    
/*  827 */     jwsObjectHolder.setStatus(true);
/*  828 */     jwsObjectHolder.setaPageRedirectHolder(null);
/*  829 */     jwsObjectHolder.setJwsObjectSigned(jwsObjectSigned);
    
    try {
/*  832 */       jwsObjectHolder.setCertificateX509((new Base64Encoder()).encode(cert.getEncoded()));
    }
/*  834 */     catch (Exception x) {
/*  835 */       message.error(getClass().getName() + "Encoding del certificato per la verifica della firma");
    } 
  }


  
  protected void jsonResponse(String state, String jwttoken, JSONObject responsejson, String access_token, long expire_in) {
/*  842 */     responsejson.put("id_token", jwttoken);
/*  843 */     if (state != null) {
/*  844 */       responsejson.put("state", state);
    }
    
/*  847 */     if (access_token != null && expire_in != 0L) {
/*  848 */       responsejson.put("token_type", "Bearer");
/*  849 */       responsejson.put("access_token", access_token);
/*  850 */       responsejson.put("expires_in", Long.valueOf(expire_in));
    } 
  }



  
  protected void postResponse(Map params, StringBuffer buffer) {
/*  858 */     buffer.append("<html>");
/*  859 */     buffer.append("<head>");
    
/*  861 */     buffer.append("<meta charset=\"UTF-8\">");
/*  862 */     buffer.append("<title>Poste.it</title>");
    
/*  864 */     buffer.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
/*  865 */     buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
/*  866 */     buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no, user-scalable=0\">");

    
/*  869 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/bootstrap/css/bootstrap.min.css\">");
    
/*  871 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/condivise/stili/trasversali/base.css\">");

    
/*  874 */     buffer.append("<style>");
/*  875 */     buffer.append("html{background-color: #fff !important;}");
/*  876 */     buffer.append("body{background-color: #fff !important;}");
/*  877 */     buffer.append("</style>");
    
/*  879 */     buffer.append("</head>");
    
/*  881 */     buffer.append("<body");
    
/*  883 */     buffer.append(" onLoad=\"javascript:document.xxxForm.submit()\"");
    
/*  885 */     buffer.append(">");
    
/*  887 */     buffer.append("<form action='" + params.get("url") + "'  method='POST' name='xxxForm'>");


    
/*  891 */     if (params.containsKey("state"))
    {
/*  893 */       buffer.append("<input type='hidden' name='state' value='" + 
/*  894 */           urlencode((String)params.get("state")) + "' />");
    }
    
/*  897 */     if (params.containsKey("signature"))
    {
/*  899 */       buffer.append("<input type='hidden' name='signature' value='" + urlencode((String)params.get("signature")) + "' />");
    }





    
/*  907 */     buffer.append("<div class=\"col-md-12\" style=\"position:absolute;top:30%;width:100%;margin:0 auto;\">");
/*  908 */     buffer.append("<div id=\"main\">");
/*  909 */     buffer.append("<div class=\"row\">");
/*  910 */     buffer.append("<div class=\"col-md-12 text-xs-center\">");
/*  911 */     buffer.append("<img alt=\"Poste Italiane\" src=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png\" srcset=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-@2x.png 2x\">");
/*  912 */     buffer.append("<br>");
/*  913 */     buffer.append("<img src=\"/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif\" style=\"width: 40px;\" class=\"loader-spinner spacer-xs-40\">");
/*  914 */     buffer.append("<h3>In attesa di risposta da " + AntiXSS.HtmlEncode((String)params.get("rp")) + "</h3>");
/*  915 */     buffer.append("</div>");
/*  916 */     buffer.append("</div>");
/*  917 */     buffer.append("</div>");
/*  918 */     buffer.append("</div>");
    
/*  920 */     buffer.append("</form>");
    
/*  922 */     buffer.append("</body>");
/*  923 */     buffer.append("</html>");
  }
  
  protected void postErrorResponse(Map params, StringBuffer buffer) {
/*  927 */     buffer.append("<html>");
/*  928 */     buffer.append("<head>");
    
/*  930 */     buffer.append("<meta charset=\"UTF-8\">");
/*  931 */     buffer.append("<title>Poste.it</title>");
    
/*  933 */     buffer.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
/*  934 */     buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
/*  935 */     buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no, user-scalable=0\">");

    
/*  938 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/bootstrap/css/bootstrap.min.css\">");
    
/*  940 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/condivise/stili/trasversali/base.css\">");

    
/*  943 */     buffer.append("<style>");
/*  944 */     buffer.append("html{background-color: #fff !important;}");
/*  945 */     buffer.append("body{background-color: #fff !important;}");
/*  946 */     buffer.append("</style>");
    
/*  948 */     buffer.append("</head>");
    
/*  950 */     buffer.append("<body");
    
/*  952 */     buffer.append(" onLoad=\"javascript:document.xxxForm.submit()\"");
    
/*  954 */     buffer.append(">");

    
/*  957 */     buffer.append("<form action='" + params.get("url") + "'  method='POST' name='xxxForm'>");


    
/*  961 */     if (params.containsKey("error")) {
      
/*  963 */       buffer.append("<input type='hidden' name='error' value='" + urlencode((String)params.get("error")) + "' />");
      
/*  965 */       if (params.containsKey("jti"))
      {
/*  967 */         buffer.append("<input type='hidden' name='jti' value='" + urlencode((String)params.get("jti")) + "' />");
      }


      
/*  972 */       if (params.containsKey("state"))
      {
/*  974 */         buffer.append("<input type='hidden' name='state' value='" + urlencode((String)params.get("state")) + "' />");
      }
    } 

    
/*  979 */     buffer.append("<div class=\"col-md-12\" style=\"position:absolute;top:30%;width:100%;margin:0 auto;\">");
/*  980 */     buffer.append("<div id=\"main\">");
/*  981 */     buffer.append("<div class=\"row\">");
/*  982 */     buffer.append("<div class=\"col-md-12 text-xs-center\">");
/*  983 */     buffer.append("<img alt=\"Poste Italiane\" src=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png\" srcset=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-@2x.png 2x\">");
/*  984 */     buffer.append("<br>");
/*  985 */     buffer.append("<img src=\"/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif\" style=\"width: 40px;\" class=\"loader-spinner spacer-xs-40\">");
/*  986 */     buffer.append("<h3>In attesa di risposta da " + AntiXSS.HtmlEncode((String)params.get("rp")) + "</h3>");
/*  987 */     buffer.append("</div>");
/*  988 */     buffer.append("</div>");
    
/*  990 */     buffer.append("</form>");
    
/*  992 */     buffer.append("</body>");
/*  993 */     buffer.append("</html>");
  }

  
  protected void postResponse(String state, EntityDescriptorReaderHolder entityDescriptorReaderHolder, String jwttoken, ServiceHolder serviceHolder, StringBuffer buffer, String code, boolean hasCode) {
/*  998 */     buffer.append("<html>");
/*  999 */     buffer.append("<head>");
    
/* 1001 */     buffer.append("<meta charset=\"UTF-8\">");
/* 1002 */     buffer.append("<title>Poste.it</title>");
    
/* 1004 */     buffer.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
/* 1005 */     buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
/* 1006 */     buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no, user-scalable=0\">");

    
/* 1009 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/bootstrap/css/bootstrap.min.css\">");
    
/* 1011 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/condivise/stili/trasversali/base.css\">");

    
/* 1014 */     buffer.append("<style>");
/* 1015 */     buffer.append("html{background-color: #fff !important;}");
/* 1016 */     buffer.append("body{background-color: #fff !important;}");
/* 1017 */     buffer.append("</style>");
    
/* 1019 */     buffer.append("</head>");
    
/* 1021 */     buffer.append("<body");
    
/* 1023 */     buffer.append(" onLoad=\"javascript:document.xxxForm.submit()\"");
    
/* 1025 */     buffer.append(">");
    
/* 1027 */     buffer.append("<form action='" + serviceHolder.getLocation() + "'  method='POST' name='xxxForm'>");



    
/* 1032 */     buffer.append("<input type='hidden' name='id_token' value='" + jwttoken + "' />");
/* 1033 */     if (state != null) {
/* 1034 */       buffer.append("<input type='hidden' name='state' value='" + urlencode(state) + "' />");
    }
    
/* 1037 */     if (hasCode) {
/* 1038 */       buffer.append("<input type='hidden' name='code' value='" + urlencode(code) + "' />");
    }

    
/* 1042 */     buffer.append("<input type='hidden' name='status' value='" + 
/* 1043 */         urlencode("success") + "' />");



    
/* 1048 */     buffer.append("<div class=\"col-md-12\" style=\"position:absolute;top:30%;width:100%;margin:0 auto;\">");
/* 1049 */     buffer.append("<div id=\"main\">");
/* 1050 */     buffer.append("<div class=\"row\">");
/* 1051 */     buffer.append("<div class=\"col-md-12 text-xs-center\">");
/* 1052 */     buffer.append("<img alt=\"Poste Italiane\" src=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png\" srcset=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-@2x.png 2x\">");
/* 1053 */     buffer.append("<br>");
/* 1054 */     buffer.append("<img src=\"/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif\" style=\"width: 40px;\" class=\"loader-spinner spacer-xs-40\">");
/* 1055 */     buffer.append("<h3>In attesa di risposta da " + AntiXSS.HtmlEncode(entityDescriptorReaderHolder.getOrganizationDisplayName()) + "</h3>");
/* 1056 */     buffer.append("</div>");
/* 1057 */     buffer.append("</div>");
/* 1058 */     buffer.append("</div>");
/* 1059 */     buffer.append("</div>");

    
/* 1062 */     buffer.append("</form>");
    
/* 1064 */     buffer.append("</body>");
/* 1065 */     buffer.append("</html>");
  }

  
  protected void postErrorResponse(String state, EntityDescriptorReaderHolder entityDescriptorReaderHolder, String jwttoken, ServiceHolder serviceHolder, StringBuffer buffer, Map attributesErrors) {
/* 1070 */     buffer.append("<html>");
/* 1071 */     buffer.append("<head>");
    
/* 1073 */     buffer.append("<meta charset=\"UTF-8\">");
/* 1074 */     buffer.append("<title>Poste.it</title>");
    
/* 1076 */     buffer.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
/* 1077 */     buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
/* 1078 */     buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no, user-scalable=0\">");

    
/* 1081 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/bootstrap/css/bootstrap.min.css\">");
    
/* 1083 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/condivise/stili/trasversali/base.css\">");

    
/* 1086 */     buffer.append("<style>");
/* 1087 */     buffer.append("html{background-color: #fff !important;}");
/* 1088 */     buffer.append("body{background-color: #fff !important;}");
/* 1089 */     buffer.append("</style>");
    
/* 1091 */     buffer.append("</head>");
    
/* 1093 */     buffer.append("<body");
    
/* 1095 */     buffer.append(" onLoad=\"javascript:document.xxxForm.submit()\"");
    
/* 1097 */     buffer.append(">");
    
/* 1099 */     buffer.append("<form action='" + serviceHolder.getLocation() + "'  method='POST' name='xxxForm'>");
    
/* 1101 */     buffer.append("<input type='hidden' name='error' value='" + ((IValueHolder)attributesErrors
/* 1102 */         .get("error")).asString() + "' />");
    
/* 1104 */     if (attributesErrors.containsKey("code")) {
/* 1105 */       buffer.append("<input type='hidden' name='code' value='" + ((IValueHolder)attributesErrors
/* 1106 */           .get("code")).asString() + "' />");
    }
    
/* 1109 */     buffer.append("<input type='hidden' name='status' value='error' />");

    
/* 1112 */     buffer.append("<input type='hidden' name='id_token' value='" + jwttoken + "' />");
    
/* 1114 */     if (!Utils.isEmpty(state)) {
/* 1115 */       buffer.append("<input type='hidden' name='state' value='" + urlencode(state) + "' />");
    }


    
/* 1120 */     buffer.append("<div class=\"col-md-12\" style=\"position:absolute;top:30%;width:100%;margin:0 auto;\">");
/* 1121 */     buffer.append("<div id=\"main\">");
/* 1122 */     buffer.append("<div class=\"row\">");
/* 1123 */     buffer.append("<div class=\"col-md-12 text-xs-center\">");
/* 1124 */     buffer.append("<img alt=\"Poste Italiane\" src=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png\" srcset=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-@2x.png 2x\">");
/* 1125 */     buffer.append("<br>");
/* 1126 */     buffer.append("<img src=\"/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif\" style=\"width: 40px;\" class=\"loader-spinner spacer-xs-40\">");
/* 1127 */     buffer.append("<h3>In attesa di risposta da " + AntiXSS.HtmlEncode(entityDescriptorReaderHolder.getOrganizationDisplayName()) + "</h3>");
/* 1128 */     buffer.append("</div>");
/* 1129 */     buffer.append("</div>");
    
/* 1131 */     buffer.append("</form>");
    
/* 1133 */     buffer.append("</body>");
/* 1134 */     buffer.append("</html>");
  }
  
  protected Map<String, String> splitQuery(URI url) throws UnsupportedEncodingException {
/* 1138 */     Map<String, String> query_pairs = new LinkedHashMap<>();
/* 1139 */     String query = url.getQuery();
/* 1140 */     String[] pairs = query.split("&");
/* 1141 */     for (String pair : pairs) {
/* 1142 */       int idx = pair.indexOf("=");
/* 1143 */       query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
    } 
/* 1145 */     return query_pairs;
  }




  
  protected void logouteResponse(EntityDescriptorReaderHolder entityDescriptorReaderHolder, String serviceHolder, String querystring, StringBuffer buffer, List service, IMessage _message) {
/* 1153 */     buffer.append("<html>");
/* 1154 */     buffer.append("<head>");
    
/* 1156 */     buffer.append("<meta charset=\"UTF-8\">");
/* 1157 */     buffer.append("<title>Poste.it</title>");
    
/* 1159 */     buffer.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
/* 1160 */     buffer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
/* 1161 */     buffer.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no, user-scalable=0\">");

    
/* 1164 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/bootstrap/css/bootstrap.min.css\">");
    
/* 1166 */     buffer.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/risorse_dt/condivise/stili/trasversali/base.css\">");

    
/* 1169 */     buffer.append("<style>");
/* 1170 */     buffer.append("iframe { visibility: hidden; position: absolute; left: 0; top: 0; height:0; width:0; border: none;}");
/* 1171 */     buffer.append("html{background-color: #fff !important;}");
/* 1172 */     buffer.append("body{background-color: #fff !important;}");
/* 1173 */     buffer.append("</style>");
    
/* 1175 */     buffer.append("</head>");
    
/* 1177 */     buffer.append("<body");
    
/* 1179 */     buffer.append(" onLoad=\"javascript:document.xxxForm.submit()\"");
    
/* 1181 */     buffer.append(">");

    
/* 1184 */     Iterator<String> serviceIterator = service.iterator();
/* 1185 */     String url = "";
/* 1186 */     while (serviceIterator.hasNext()) {
/* 1187 */       url = serviceIterator.next();
/* 1188 */       _message.debug("LOGOUT URL: [" + url + "]");
/* 1189 */       buffer.append("<iframe src=\"" + url + "\"></iframe>");
    } 
    
/* 1192 */     buffer.append("<form action=\"" + serviceHolder + "\"  method='GET' name='xxxForm'>");



    
    try {
/* 1198 */       URI uri = new URI(serviceHolder + "?" + serviceHolder);
/* 1199 */       Map<String, String> qq = splitQuery(uri);
      
/* 1201 */       Set<String> keys = qq.keySet();
/* 1202 */       Iterator<String> kiter = keys.iterator();
      
/* 1204 */       String key = "";
/* 1205 */       while (kiter.hasNext())
      {
/* 1207 */         key = kiter.next();
        
/* 1209 */         buffer.append("<input type='hidden' name='" + key + "' value='" + (String)qq.get(key) + "' />");
      }
    
    }
/* 1213 */     catch (Exception x) {
/* 1214 */       _message.error(getClass().getName() + "logoutResponse error ", x);
    } 


    
/* 1219 */     buffer.append("<div class=\"col-md-12\" style=\"position:absolute;top:30%;width:100%;margin:0 auto;\">");
/* 1220 */     buffer.append("<div id=\"main\">");
/* 1221 */     buffer.append("<div class=\"row\">");
/* 1222 */     buffer.append("<div class=\"col-md-12 text-xs-center\">");
/* 1223 */     buffer.append("<img alt=\"Poste Italiane\" src=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png\" srcset=\"/risorse_dt/condivise/immagini/loghi/logo-poste-italiane@2x.png 2x\">");
/* 1224 */     buffer.append("<br>");
/* 1225 */     buffer.append("<img src=\"/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif\" style=\"width: 40px;\" class=\"loader-spinner spacer-xs-40\">");
/* 1226 */     buffer.append("<h3>In attesa di risposta da " + entityDescriptorReaderHolder.getOrganizationDisplayName() + "</h3>");
/* 1227 */     buffer.append("</div>");
/* 1228 */     buffer.append("</div>");
/* 1229 */     buffer.append("</div>");
/* 1230 */     buffer.append("</div>");
    
/* 1232 */     buffer.append("</form>");
    
/* 1234 */     buffer.append("</body>");
/* 1235 */     buffer.append("</html>");
  }
  
  protected void redirectJwtResponse(String state, String jwttoken, ServiceHolder serviceHolder, StringBuffer responseToken) {
/* 1239 */     responseToken.append(serviceHolder.getLocation());
/* 1240 */     responseToken.append("?");
    
/* 1242 */     responseToken.append("token_type=Bearer");
/* 1243 */     responseToken.append("&");
/* 1244 */     responseToken.append("id_token");
/* 1245 */     responseToken.append("=");
/* 1246 */     responseToken.append(urlencode(jwttoken));
/* 1247 */     if (state != null) {
/* 1248 */       responseToken.append("&");
/* 1249 */       responseToken.append("state");
/* 1250 */       responseToken.append("=");
/* 1251 */       responseToken.append(urlencode(state));
    } 
  }



  
  protected void validateAssertionContent(IMainFederationConfig federationConfig, JSONObject authnRequest, EntityDescriptorReaderHolder entityDescriptorReaderHolder, Map<String, StringValueHolder> attributesErrors) {
/* 1259 */     validateBasicAssertionContent(federationConfig, authnRequest, attributesErrors);
/* 1260 */     if (attributesErrors.size() > 0) {
      return;
    }







    
/* 1271 */     if (!authnRequest.containsKey("nonce")) {
/* 1272 */       attributesErrors.put("error", new StringValueHolder("CODE-0018"));
    }


    
/* 1277 */     String scope = (String)authnRequest.getOrDefault("scope", "");
/* 1278 */     if (Utils.isEmpty(scope)) {
/* 1279 */       authnRequest.put("scope", "");
    }

    
/* 1283 */     String prompt = (String)authnRequest.getOrDefault("prompt", "");
    
/* 1285 */     if (Utils.isEmpty(prompt)) {
/* 1286 */       authnRequest.put("prompt", "login");
/* 1287 */       prompt = (String)authnRequest.get("prompt");
    } 
    
/* 1290 */     if (!prompt.contains("none") && 
/* 1291 */       !prompt.contains("login") && 
/* 1292 */       !prompt.contains("consent") && 
/* 1293 */       !prompt.contains("select_account")) {
/* 1294 */       attributesErrors.put("error", new StringValueHolder("CODE-0008"));

      
      return;
    } 
    
/* 1300 */     String acrvalues = (String)authnRequest.get("acr_values");
/* 1301 */     if (Utils.isEmpty(acrvalues)) {
/* 1302 */       attributesErrors.put("error", new StringValueHolder("CODE-0009"));
      
      return;
    } 
/* 1306 */     if (!acrvalues.equals("https://idp.poste.it/L1") && 
/* 1307 */       !acrvalues.equals("https://idp.poste.it/L2") && 
/* 1308 */       !acrvalues.equals("https://idp.poste.it/L3")) {

      
/* 1311 */       attributesErrors.put("error", new StringValueHolder("CODE-0010"));
      
      return;
    } 
    
/* 1316 */     String reqRedirect_uri = (String)authnRequest.get("redirect_uri");
/* 1317 */     if (Utils.isEmpty(reqRedirect_uri)) {
/* 1318 */       attributesErrors.put("error", new StringValueHolder("CODE-0003"));
      
      return;
    } 
/* 1322 */     if (!entityDescriptorReaderHolder.getAssertionConsumerService().containsKey(reqRedirect_uri)) {
/* 1323 */       attributesErrors.put("error", new StringValueHolder("CODE-0004"));
      
      return;
    } 
    
/* 1328 */     String granttype = (String)authnRequest.getOrDefault("grant_type", "refresh_token");
    
/* 1330 */     if (Utils.isEmpty(granttype))
    {
      
/* 1333 */       authnRequest.put("grant_type", "refresh_token");
    }
    
/* 1336 */     if (!"password".equals(granttype) && 
/* 1337 */       !"refresh_token".equals(granttype) && 
/* 1338 */       !"authorization_code".equals(granttype) && 
/* 1339 */       !"access_token".equals(granttype) && 
/* 1340 */       !"signed_challenge".equals(granttype) && 
/* 1341 */       !"reg_signed_challenge".equals(granttype)) {

      
/* 1344 */       attributesErrors.put("error", new StringValueHolder("CODE-0006"));
      
      return;
    } 
/* 1348 */     String jti = (String)authnRequest.getOrDefault("jti", "");
    
/* 1350 */     if (Utils.isEmpty(jti)) {
/* 1351 */       attributesErrors.put("error", new StringValueHolder("CODE-0019"));
      return;
    } 
  }

  
  protected void validateBasicAssertionContent(IMainFederationConfig federationConfig, JSONObject authnRequest, Map<String, StringValueHolder> attributesErrors) {
/* 1358 */     Long now = Long.valueOf((new Date()).getTime());

    
    try {
/* 1362 */       Long iat = Long.valueOf(((Long)authnRequest.get("iat")).longValue() * 1000L);
      
/* 1364 */       if (Math.abs(now.longValue() - iat.longValue()) > 60000L) {
/* 1365 */         attributesErrors.put("error", new StringValueHolder("CODE-0011"));

        
        return;
      } 
/* 1370 */     } catch (Exception x) {
/* 1371 */       attributesErrors.put("error", new StringValueHolder("CODE-0011"));
      
      return;
    } 
    
    try {
/* 1377 */       Long nbf = Long.valueOf(((Long)authnRequest.get("nbf")).longValue() * 1000L);
      
/* 1379 */       if (Math.abs(now.longValue() - nbf.longValue()) > 60000L) {
/* 1380 */         attributesErrors.put("error", new StringValueHolder("CODE-0012"));

        
        return;
      } 
/* 1385 */     } catch (Exception x) {
/* 1386 */       attributesErrors.put("error", new StringValueHolder("CODE-0012"));

      
      return;
    } 
    
    try {
/* 1393 */       Long exp = Long.valueOf(((Long)authnRequest.get("exp")).longValue() * 1000L);
/* 1394 */       if (now.longValue() > exp.longValue()) {
/* 1395 */         attributesErrors.put("error", new StringValueHolder("CODE-0013"));

        
        return;
      } 
/* 1400 */     } catch (Exception x) {
/* 1401 */       attributesErrors.put("error", new StringValueHolder("CODE-0013"));
      
      return;
    } 
    
/* 1406 */     String reqAUD = (String)authnRequest.get("aud");
/* 1407 */     if (Utils.isEmpty(reqAUD)) {
/* 1408 */       attributesErrors.put("error", new StringValueHolder("CODE-0001"));
      
      return;
    } 
/* 1412 */     if (!reqAUD.equals(federationConfig.getEntityid())) {
/* 1413 */       attributesErrors.put("error", new StringValueHolder("CODE-0002"));
      return;
    } 
  }




  
  protected void generateRedirectOpenidError(HttpServletResponse aResponse, IResponseHolder aPageRedirectHolder, String state, JSONObject authnRequest, EntityDescriptorReaderHolder entityDescriptorReaderHolder, Map<String, StringValueHolder> attributesErrors, IUserTokenConfig userTokenCofig) {
/* 1423 */     if (!attributesErrors.containsKey("error")) {
/* 1424 */       StringValueHolder error = new StringValueHolder("CODE-2001");
/* 1425 */       attributesErrors.put("error", error);
    } 
    
/* 1428 */     Certificate certForEncrypt = certificateRandomForEncrypt(entityDescriptorReaderHolder);
    
/* 1430 */     if (!Utils.isEmpty(state)) {
      try {
/* 1432 */         attributesErrors.put("s_hash", new StringValueHolder(HashCalculator.sha256(state.getBytes())));
      }
/* 1434 */       catch (Exception exception) {}
    }

    
/* 1438 */     attributesErrors.put("nonce", new StringValueHolder((String)authnRequest
/* 1439 */           .getOrDefault("nonce", "")));
    
/* 1441 */     attributesErrors.put("status", new StringValueHolder("error"));



    
/* 1446 */     String jwttoken = (new JWTTokenIssued(this.message, "nobody", (String)authnRequest.get("iss"), attributesErrors, certForEncrypt, entityDescriptorReaderHolder, userTokenCofig)).build();
    
/* 1448 */     String prompt = (String)authnRequest.get("prompt");
    
/* 1450 */     if (!prompt.contains("none")) {
/* 1451 */       ServiceHolder serviceHolder = (ServiceHolder)entityDescriptorReaderHolder.getAssertionConsumerService().get(authnRequest
/* 1452 */           .get("redirect_uri"));
/* 1453 */       String binding = (String)authnRequest.getOrDefault("binding", "http-redirect");

      
/* 1456 */       if ("http-redirect".equals(binding))
      {
/* 1458 */         StringBuffer responseToken = new StringBuffer();
        
/* 1460 */         responseToken.append(serviceHolder.getLocation());
/* 1461 */         responseToken.append("?");
/* 1462 */         responseToken.append("error");
/* 1463 */         responseToken.append("=");
/* 1464 */         responseToken.append(urlencode(((IValueHolder)attributesErrors.get("error")).asString()));
        
/* 1466 */         if (attributesErrors.containsKey("code")) {
/* 1467 */           responseToken.append("&");
/* 1468 */           responseToken.append("code");
/* 1469 */           responseToken.append("=");
/* 1470 */           responseToken.append(urlencode(((IValueHolder)attributesErrors.get("code"))
/* 1471 */                 .asString()));
        } 
        
/* 1474 */         responseToken.append("&");
/* 1475 */         responseToken.append("status");
/* 1476 */         responseToken.append("=");
/* 1477 */         responseToken.append(urlencode("error"));
        
/* 1479 */         responseToken.append("&");
/* 1480 */         responseToken.append("id_token");
/* 1481 */         responseToken.append("=");
/* 1482 */         responseToken.append(jwttoken);
        
/* 1484 */         if (!Utils.isEmpty(state)) {
/* 1485 */           responseToken.append("&");
/* 1486 */           responseToken.append("state");
/* 1487 */           responseToken.append("=");
/* 1488 */           responseToken.append(urlencode(state));
        } 
        
/* 1491 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 1492 */         aPageRedirectHolder.setContent(responseToken.toString());
/* 1493 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [urn:ietf:endpoint:oauth:openid-bearer:bindings:redirect_uri] RESPONSE: [" + (String)authnRequest.get("iss") + "]");

      
      }
      else
      {
        
/* 1500 */         aResponse.setContentType("text/html");
/* 1501 */         StringBuffer buffer = new StringBuffer();
/* 1502 */         postErrorResponse(state, entityDescriptorReaderHolder, jwttoken, serviceHolder, buffer, attributesErrors);
        
/* 1504 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1505 */         aPageRedirectHolder.setContent(buffer.toString());
/* 1506 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-POST] RESPONSE: [" + (String)authnRequest.get("iss") + "]");
      
      }
    
    }
    else {
      
/* 1513 */       ServiceHolder serviceHolder = (ServiceHolder)entityDescriptorReaderHolder.getAssertionConsumerService().get(authnRequest
/* 1514 */           .get("redirect_uri"));
/* 1515 */       String binding = (String)authnRequest.getOrDefault("binding", "");
      
/* 1517 */       if ("http-redirect".equals(binding)) {
        
/* 1519 */         StringBuffer responseToken = new StringBuffer();
        
/* 1521 */         responseToken.append(serviceHolder.getLocation());
/* 1522 */         responseToken.append("?");
/* 1523 */         responseToken.append("error");
/* 1524 */         responseToken.append("=");
/* 1525 */         responseToken.append(urlencode(((IValueHolder)attributesErrors.get("error")).asString()));
        
/* 1527 */         if (attributesErrors.containsKey("code")) {
/* 1528 */           responseToken.append("&");
/* 1529 */           responseToken.append("code");
/* 1530 */           responseToken.append("=");
/* 1531 */           responseToken.append(urlencode(((IValueHolder)attributesErrors.get("code"))
/* 1532 */                 .asString()));
        } 
        
/* 1535 */         responseToken.append("&");
/* 1536 */         responseToken.append("status");
/* 1537 */         responseToken.append("=");
/* 1538 */         responseToken.append(urlencode("error"));
        
/* 1540 */         responseToken.append("&");
/* 1541 */         responseToken.append("id_token");
/* 1542 */         responseToken.append("=");
/* 1543 */         responseToken.append(jwttoken);
        
/* 1545 */         if (!Utils.isEmpty(state)) {
/* 1546 */           responseToken.append("&");
/* 1547 */           responseToken.append("state");
/* 1548 */           responseToken.append("=");
/* 1549 */           responseToken.append(urlencode(state));
        } 
        
/* 1552 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 1553 */         aPageRedirectHolder.setContent(responseToken.toString());
/* 1554 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [http-redirect] RESPONSE: [" + (String)authnRequest.get("iss") + "]");

      
      }
/* 1558 */       else if ("http-post".equals(binding)) {

        
/* 1561 */         aResponse.setContentType("text/html");
/* 1562 */         StringBuffer buffer = new StringBuffer();
/* 1563 */         postErrorResponse(state, entityDescriptorReaderHolder, jwttoken, serviceHolder, buffer, attributesErrors);
        
/* 1565 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1566 */         aPageRedirectHolder.setContent(buffer.toString());
/* 1567 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-POST] RESPONSE: [" + (String)authnRequest.get("iss") + "]");
      }
      else {
        
/* 1571 */         aResponse.setContentType("application/json");
/* 1572 */         JSONObject responsejson = new JSONObject();
/* 1573 */         responsejson.put("error", ((IValueHolder)attributesErrors.get("error")).asString());
        
/* 1575 */         if (!Utils.isEmpty(state)) {
/* 1576 */           responsejson.put("state", state);
        }
        
/* 1579 */         responsejson.put("id_token", jwttoken);
/* 1580 */         responsejson.put("status", "error");
        
/* 1582 */         if (attributesErrors.containsKey("code")) {
/* 1583 */           responsejson.put("code", ((IValueHolder)attributesErrors
/* 1584 */               .get("code")).asString());
        }
        
/* 1587 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1588 */         aPageRedirectHolder.setContent(responsejson.toJSONString());
/* 1589 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [application/json] RESPONSE: [" + (String)authnRequest.get("iss") + "]");
      } 
    } 
  }





  
  protected void generateRedirectOpenIdSuccess(IResponseHolder aPageRedirectHolder, String req_state, JSONObject authnRequest, EntityDescriptorReaderHolder entityDescriptorReaderHolder, String code, HttpServletResponse aResponse, boolean hasCode, String idtoken) {
/* 1600 */     String prompt = (String)authnRequest.get("prompt");
    
/* 1602 */     if (!prompt.contains("none")) {
      
/* 1604 */       String binding = (String)authnRequest.getOrDefault("binding", "http-redirect");
      
/* 1606 */       ServiceHolder serviceHolder = (ServiceHolder)entityDescriptorReaderHolder.getAssertionConsumerService().get(authnRequest
/* 1607 */           .get("redirect_uri"));
/* 1608 */       if ("http-redirect".equals(binding))
      {
/* 1610 */         StringBuffer response = generateRedirectOpenidCode(req_state, code, hasCode, idtoken);
/* 1611 */         String redirect = serviceHolder.getLocation() + "?" + serviceHolder.getLocation();
/* 1612 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 1613 */         aPageRedirectHolder.setContent(redirect);
/* 1614 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-REDIRECT] RESPONSE: [" + (String)authnRequest.get("redirect_uri") + "]");
      
      }
      else
      {
        
/* 1620 */         aResponse.setContentType("text/html");
/* 1621 */         StringBuffer buffer = new StringBuffer();
/* 1622 */         postResponse(req_state, entityDescriptorReaderHolder, idtoken, serviceHolder, buffer, code, hasCode);
        
/* 1624 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1625 */         aPageRedirectHolder.setContent(buffer.toString());
/* 1626 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-POST] RESPONSE: [" + (String)authnRequest.get("iss") + "]");



      
      }



    
    }
    else {



      
/* 1642 */       String binding = (String)authnRequest.getOrDefault("binding", "");
/* 1643 */       ServiceHolder serviceHolder = (ServiceHolder)entityDescriptorReaderHolder.getAssertionConsumerService().get(authnRequest
/* 1644 */           .get("redirect_uri"));
/* 1645 */       if ("http-redirect".equals(binding)) {
        
/* 1647 */         StringBuffer response = generateRedirectOpenidCode(req_state, code, hasCode, idtoken);
/* 1648 */         String redirect = serviceHolder.getLocation() + "?" + serviceHolder.getLocation();
/* 1649 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 1650 */         aPageRedirectHolder.setContent(redirect);
/* 1651 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-REDIRECT] RESPONSE: [" + (String)authnRequest.get("redirect_uri") + "]");
      
      }
/* 1654 */       else if ("http-post".equals(binding)) {

        
/* 1657 */         aResponse.setContentType("text/html");
/* 1658 */         StringBuffer buffer = new StringBuffer();
/* 1659 */         postResponse(req_state, entityDescriptorReaderHolder, idtoken, serviceHolder, buffer, code, hasCode);
        
/* 1661 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1662 */         aPageRedirectHolder.setContent(buffer.toString());
/* 1663 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [HTTP-POST] RESPONSE: [" + (String)authnRequest.get("iss") + "]");
      }
      else {
        
/* 1667 */         aResponse.setContentType("application/json");
/* 1668 */         JSONObject responsejson = new JSONObject();
/* 1669 */         if (hasCode) {
/* 1670 */           responsejson.put("code", code);
        }
/* 1672 */         if (!Utils.isEmpty(req_state)) {
/* 1673 */           responsejson.put("state", req_state);
        }
        
/* 1676 */         responsejson.put("id_token", idtoken);
        
/* 1678 */         responsejson.put("status", "success");
        
/* 1680 */         aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
/* 1681 */         aPageRedirectHolder.setContent(responsejson.toJSONString());
/* 1682 */         this.message.debug(getClass().getName() + " [" + getClass().getName() + "]  BINDING: [application/json] RESPONSE: [" + (String)authnRequest.get("iss") + "]");
      } 
    } 
  }










  
  public JSONObject toJson(HttpServletRequest aRequest, JSONObject authnRequest) {
/* 1698 */     Date nowdate = new Date();
    
/* 1700 */     Map<Object, Object> times = new HashMap<>();
/* 1701 */     times.put("iat", "" + nowdate.getTime() / 1000L);
/* 1702 */     times.put("nbf", "" + nowdate.getTime() / 1000L);

    
/* 1705 */     GregorianCalendar c = new GregorianCalendar();
/* 1706 */     c.setTime(nowdate);
/* 1707 */     c.add(13, 30);
    
/* 1709 */     times.put("exp", "" + c.getTime().getTime() / 1000L);


    
/* 1713 */     Enumeration<String> penum = aRequest.getParameterNames();
    
/* 1715 */     String key = "";
/* 1716 */     while (penum.hasMoreElements()) {
/* 1717 */       key = penum.nextElement();
/* 1718 */       if (!key.equals("client_assertion") && 


        
/* 1722 */         !key.equals("username") && !key.equals("password") && 
/* 1723 */         !key.equals("request") && !key.equals("challenge") && 
/* 1724 */         !key.equals("token") && 
/* 1725 */         !key.equals("credentials")) {


        
/* 1729 */         String value = aRequest.getParameter(key);
        
/* 1731 */         if (value != null && !"".equals(value.trim()))
        {
/* 1733 */           if (!authnRequest.containsKey(key)) {
/* 1734 */             if (key.equals("exp") || key.equals("iat") || key.equals("nbf")) {
/* 1735 */               authnRequest.put(key, Long.valueOf(Long.parseLong((String)times.get(key))));
              
              continue;
            } 
/* 1739 */             authnRequest.put(key, value);
          } 
        }
      } 
    } 




    
/* 1749 */     if (!authnRequest.containsKey("exp")) {
/* 1750 */       authnRequest.put("exp", Long.valueOf(Long.parseLong((String)times.get("exp"))));
    }
    
/* 1753 */     if (!authnRequest.containsKey("iat")) {
/* 1754 */       authnRequest.put("iat", Long.valueOf(Long.parseLong((String)times.get("iat"))));
    }
    
/* 1757 */     if (!authnRequest.containsKey("nbf")) {
/* 1758 */       authnRequest.put("nbf", Long.valueOf(Long.parseLong((String)times.get("nbf"))));
    }


    
/* 1763 */     if (!authnRequest.containsKey("jti")) {
/* 1764 */       authnRequest.put("jti", "_ag_" + UUID.randomUUID().toString());
    }
    
/* 1767 */     if (!authnRequest.containsKey("nonce")) {
/* 1768 */       authnRequest.put("nonce", UUID.randomUUID().toString());
    }



    
/* 1774 */     return authnRequest;
  }
  
  protected StringBuffer generateRedirectOpenidCode(String state, String code, boolean hascode, String jwttoken) {
/* 1778 */     StringBuffer response = new StringBuffer();
    
/* 1780 */     response.append("id_token");
/* 1781 */     response.append("=");
/* 1782 */     response.append(jwttoken);
    
/* 1784 */     response.append("&");
/* 1785 */     response.append("status");
/* 1786 */     response.append("=");
/* 1787 */     response.append(urlencode("success"));
    
/* 1789 */     if (hascode) {
/* 1790 */       response.append("&");
/* 1791 */       response.append("code");
/* 1792 */       response.append("=");
/* 1793 */       response.append(urlencode(code));
    } 
/* 1795 */     if (!Utils.isEmpty(state)) {
      
/* 1797 */       response.append("&");
      
/* 1799 */       response.append("state");
/* 1800 */       response.append("=");
/* 1801 */       response.append(urlencode(state));
    } 
    
/* 1804 */     return response;
  }

  
  protected void specialToken(Map aTokenStrategyMap, Map aReturnedAttributes, Map params) {
/* 1809 */     String issuer = (String)((JSONObject)params.get("P_AUTHNRERQUEST")).get("iss");
    
/* 1811 */     List attributes = ((IMetadataLoader)params.get("P_METADATA")).attributes(issuer);
/* 1812 */     ListIterator<String> attributesliter = attributes.listIterator();
/* 1813 */     String attribute = "";
    
/* 1815 */     while (attributesliter.hasNext()) {
      
/* 1817 */       attribute = attributesliter.next();
      
/* 1819 */       if (aTokenStrategyMap.containsKey(attribute)) {
        
/* 1821 */         ITokenStrategyWriter strategy = (ITokenStrategyWriter)aTokenStrategyMap.get(attribute);
/* 1822 */         strategy.write(params, ((JSONObject)params.get("P_AUTHNRERQUEST")).toString(), aReturnedAttributes);
      } 
    } 
  }


  
  protected void generatejsonresponse(HttpServletResponse aResponse, IResponseHolder aPageRedirectHolder, String aJsonResponseContent) {
/* 1830 */     generatejsonresponse(aResponse, aPageRedirectHolder, aJsonResponseContent, false);
  }

  
  protected void generatejsonresponse(HttpServletResponse aResponse, IResponseHolder aPageRedirectHolder, String aJsonResponseContent, boolean isRevoke) {
/* 1835 */     aResponse.setContentType("application/json");
    
/* 1837 */     aPageRedirectHolder.setType("TYPE_RESPONSE_CONTENT");
    
/* 1839 */     if (!isRevoke) {
/* 1840 */       aPageRedirectHolder.setContent(aJsonResponseContent);
    } else {
      
/* 1843 */       aPageRedirectHolder.setContent("");
    } 
    
/* 1846 */     aResponse.setHeader("Cache-Control", "no-store");
/* 1847 */     aResponse.setHeader("Pragma", "no-cache");
/* 1848 */     aResponse.setDateHeader("Expires", 0L);
  }


  
  public void signChallenge(IXMLSigner xmlsigner, IMainFederationConfig federationConfig, JSONObject challenge, Map<String, String> returned, IMessage aMessage) {
    try {
/* 1855 */       IFederationCertificatesService federationCertificatesService = MainApplicationServiceLocator.federationCertificatesService();
/* 1856 */       String signDefaultAlias = federationCertificatesService.getSignDefaultAlias();
/* 1857 */       X509Certificate cert = xmlsigner.certificate(signDefaultAlias);
      
/* 1859 */       returned.put("cert", Encoding.base64urlEncode(cert.getEncoded()));

      
/* 1862 */       JWSHeader jwsSignHeader = (new JWSHeader.Builder(JWSAlgorithm.RS256)).keyID((new KeyIdentifierHelper(cert)).exec()).type(JOSEObjectType.JWT).contentType("JWS").build();
      
/* 1864 */       Map<Object, Object> credential = new HashMap<>();
/* 1865 */       credential.put("_alis_provate_key_", signDefaultAlias);
/* 1866 */       credential.put("_password_provate_key_", federationConfig.attribute("SIGN_PASSWORD"));
/* 1867 */       JodRSASSASigner jodRSASSASigner = new JodRSASSASigner(xmlsigner, aMessage, credential);
      
/* 1869 */       JWSObject jwsObjectSign = new JWSObject(jwsSignHeader, new Payload(challenge.toString()));
      
/* 1871 */       jwsObjectSign.sign((JWSSigner)jodRSASSASigner);
      
/* 1873 */       returned.put("signature", jwsObjectSign.serialize());
    
    }
/* 1876 */     catch (Exception x) {
/* 1877 */       aMessage.error(getClass().getName() + " FIRMA DELLA CHALLENGE DI ACCESSO " + getClass().getName(), x);
/* 1878 */       returned.clear();
    } 
  }

  
  public String signSecureHolderData(IXMLSigner xmlsigner, IMainFederationConfig federationConfig, JSONObject challenge, IMessage aMessage) {
/* 1884 */     return (new SignSecureHolderData(xmlsigner, federationConfig, challenge, aMessage)).exec();
  }


  
  public String parseCf(X509Certificate cert, IMessage message) {
/* 1890 */     return (new ParseCfFromX509(cert, message)).execCF();
  }


  
  public String parseCf(String aCommonName, IMessage message) {
/* 1896 */     return (new ParseCfFromX509(null, message)).execCFfromCN(aCommonName);
  }

  
  protected String getKid(X509Certificate cert) {
/* 1901 */     return (new KeyIdentifierHelper(cert)).exec();
  }

  
  protected String ifstatepresent(String state) {
/* 1906 */     if (Utils.isEmpty(state)) {
/* 1907 */       return "";
    }
/* 1909 */     return "&state=" + UrlEncoderHelper.encode(state);
  }




  
  public String getcf(String principal, TokenAttributeHolder aPrincipalHolder) {
/* 1917 */     aPrincipalHolder.setPrincipal(principal);
    
/* 1919 */     List<ManagedAttribute> resultAttribute = new ArrayList();
    
/* 1921 */     ManagedAttribute m = new ManagedAttribute();
/* 1922 */     m.setName("taxcode");
/* 1923 */     resultAttribute.add(m);
    
/* 1925 */     Map<Object, Object> returnedAttribute = new HashMap<>();
    
/* 1927 */     (new JWTAttributeWriter(resultAttribute, aPrincipalHolder, returnedAttribute, this.message)).write();
    
/* 1929 */     String endUserIdentification = null;
/* 1930 */     if (returnedAttribute.containsKey("claims")) {
/* 1931 */       IValueHolder valueholder = (IValueHolder)returnedAttribute.get("claims");
      
      try {
/* 1934 */         JSONObject userdata = new JSONObject(valueholder.asString());
/* 1935 */         endUserIdentification = userdata.optString("taxcode", "");
      
      }
/* 1938 */       catch (Exception x) {
/* 1939 */         this.message.error(getClass().getName() + " PRINCIPAL [" + getClass().getName() + "]  RECUPERO CF", x);
      } 
    } 

    
/* 1944 */     return endUserIdentification;
  }

  
  public Map getuserdata(String principal, List attributes, IMainApplicationConfig mainConfig, IJodRedisClient redisClientUP) {
/* 1949 */     TokenAttributeHolder aPrincipalHolder = new TokenAttributeHolder();
/* 1950 */     aPrincipalHolder.setPrincipal(principal);
    
/* 1952 */     List<ManagedAttribute> resultAttribute = new ArrayList();
    
/* 1954 */     Iterator<String> liter = attributes.iterator();
    
/* 1956 */     String name = "";
/* 1957 */     while (liter.hasNext()) {
/* 1958 */       name = liter.next();
/* 1959 */       ManagedAttribute m = new ManagedAttribute();
/* 1960 */       m.setName(name);
/* 1961 */       resultAttribute.add(m);
    } 

    
/* 1965 */     Map<Object, Object> result = new HashMap<>();
/* 1966 */     Map<Object, Object> returnedAttribute = new HashMap<>();

    
/* 1969 */     (new CachedJWTAttributeWriter(resultAttribute, aPrincipalHolder, returnedAttribute, this.message, mainConfig, redisClientUP)).write();
    
/* 1971 */     if (returnedAttribute.containsKey("claims")) {
/* 1972 */       IValueHolder valueholder = (IValueHolder)returnedAttribute.get("claims");
      
      try {
/* 1975 */         JSONObject userdata = new JSONObject(valueholder.asString());
        
/* 1977 */         Iterator<String> xliter = userdata.keys();
/* 1978 */         String key = "";
        
/* 1980 */         while (xliter.hasNext()) {
/* 1981 */           key = xliter.next();
/* 1982 */           result.put(key, userdata.optString(key, ""));
        }
      
      }
/* 1986 */       catch (Exception x) {
/* 1987 */         this.message.error(getClass().getName() + " PRINCIPAL [" + getClass().getName() + "]  RECUPERO DATI", x);
      } 
    } 

    
/* 1992 */     return result;
  }
  
  public String buildRedirectQuery(String redirect, Map params) {
/* 1996 */     StringBuffer redirectBuffer = new StringBuffer();
/* 1997 */     if (params.size() > 0) {
/* 1998 */       Set keys = params.keySet();
/* 1999 */       Iterator<String> liter = keys.iterator();
      
/* 2001 */       String xkey = "";
/* 2002 */       while (liter.hasNext()) {
/* 2003 */         xkey = liter.next();
/* 2004 */         redirectBuffer.append(xkey);
/* 2005 */         redirectBuffer.append("=");
/* 2006 */         redirectBuffer.append(UrlEncoderHelper.encode((String)params.get(xkey)));
        
/* 2008 */         if (liter.hasNext()) {
/* 2009 */           redirectBuffer.append("&");
        }
      } 
      
/* 2013 */       redirect = redirect + "?" + redirect;
    } 
/* 2015 */     return redirect;
  }




































































































































  
  protected boolean isValidFormat(String format, String value, IMessage aMessage) {
/* 2151 */     Date date = null;
    try {
/* 2153 */       SimpleDateFormat sdf = new SimpleDateFormat(format);
/* 2154 */       date = sdf.parse(value);
/* 2155 */       if (!value.equals(sdf.format(date))) {
/* 2156 */         date = null;
/* 2157 */         aMessage.error(getClass().getName() + " DATA [" + getClass().getName() + "] NON VALIDA PER IL FORMATO [" + value + "]");
      }
    
/* 2160 */     } catch (Exception ex) {
/* 2161 */       aMessage.error(getClass().getName() + " DATA [" + getClass().getName() + "] NON VALIDA PER IL FORMATO [" + value + "]", ex);
    } 
/* 2163 */     return (date != null);
  }
  
  public boolean isValidFormatAsGMT(String format, String value, IMessage aMessage) {
/* 2167 */     Date date = null;
    try {
/* 2169 */       SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
/* 2170 */       sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 2171 */       date = sdf.parse(value);
/* 2172 */       if (!value.equals(sdf.format(date))) {
/* 2173 */         aMessage.error(getClass().getName() + " DATA [" + getClass().getName() + "] NON VALIDA PER IL FORMATO [" + value + "]");
/* 2174 */         date = null;
      }
    
/* 2177 */     } catch (Exception ex) {
/* 2178 */       aMessage.error(getClass().getName() + " DATA [" + getClass().getName() + "] NON VALIDA PER IL FORMATO [" + value + "]", ex);
    } 
/* 2180 */     return (date != null);
  }
  
  public String formaDateAsGmt(String format, Date value, IMessage aMessage) {
/* 2184 */     String returned = "";
    try {
/* 2186 */       SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
/* 2187 */       sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 2188 */       returned = sdf.format(value);
    }
/* 2190 */     catch (Exception x) {
/* 2191 */       aMessage.error(getClass().getName() + " DATA [" + getClass().getName() + "] NON VALIDA PER IL FORMATO [" + value + "]", x);
    } 
/* 2193 */     return returned;
  }

  
  protected JSONObject evalApp(JSONArray jsonarr, String rapporto, IMessage message, String issuer, String favoriteApp) {
/* 2198 */     int i = 0, l = jsonarr.length();
/* 2199 */     JSONObject app = null;
/* 2200 */     String appname = "";
/* 2201 */     String disabled = "";
/* 2202 */     for (i = 0; i < l; i++) {
      
/* 2204 */       app = jsonarr.getJSONObject(i);
/* 2205 */       appname = app.getString("appName");
/* 2206 */       disabled = app.getString("disabled");
/* 2207 */       message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "]");
/* 2208 */       if ("true".equals(disabled)) {
/* 2209 */         app = null;

      
      }
/* 2213 */       else if (!appname.equals(favoriteApp)) {
/* 2214 */         app = null;
      }
      else {
        
/* 2218 */         JSONObject products = app.getJSONObject("productList");
        
/* 2220 */         JSONArray carte = products.getJSONArray("carte");
        
/* 2222 */         boolean isCarte = forEach(carte, rapporto, message);
/* 2223 */         if (isCarte) {
/* 2224 */           message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "] RAPPORTO DISPOSITIVO TROVATO NELLE CARTE");
/* 2225 */           app.put("rapporto", "carte");
/* 2226 */           return app;
        } 
        
/* 2229 */         JSONArray conti = products.getJSONArray("conti");
        
/* 2231 */         boolean isConti = forEach(conti, rapporto, message);
/* 2232 */         if (isConti) {
/* 2233 */           message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "] RAPPORTO DISPOSITIVO TROVATO NEI CONTI");
/* 2234 */           app.put("rapporto", "conti");
/* 2235 */           return app;
        } 
      } 
    } 
/* 2239 */     return null;
  }


  
  protected JSONObject evalApp(JSONArray jsonarr, String rapporto, IMessage message, String issuer) {
/* 2245 */     int i = 0, l = jsonarr.length();
/* 2246 */     JSONObject app = null;
/* 2247 */     String appname = "";
/* 2248 */     String disabled = "";
/* 2249 */     for (i = 0; i < l; i++) {
      
/* 2251 */       app = jsonarr.getJSONObject(i);
/* 2252 */       appname = app.getString("appName");
/* 2253 */       disabled = app.getString("disabled");
/* 2254 */       message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "]");
/* 2255 */       if ("true".equals(disabled)) {
/* 2256 */         app = null;
      }
      else {
        
/* 2260 */         JSONObject products = app.getJSONObject("productList");
        
/* 2262 */         JSONArray carte = products.getJSONArray("carte");
        
/* 2264 */         boolean isCarte = forEach(carte, rapporto, message);
/* 2265 */         if (isCarte) {
/* 2266 */           message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "] RAPPORTO DISPOSITIVO TROVATO NELLE CARTE");
/* 2267 */           app.put("rapporto", "carte");
/* 2268 */           return app;
        } 
        
/* 2271 */         JSONArray conti = products.getJSONArray("conti");
        
/* 2273 */         boolean isConti = forEach(conti, rapporto, message);
/* 2274 */         if (isConti) {
/* 2275 */           message.info(getClass().getName() + " ISSUER [" + getClass().getName() + "] - APP NAME [" + issuer + "] DISABLED [" + appname + "] RAPPORTO DISPOSITIVO TROVATO NEI CONTI");
/* 2276 */           app.put("rapporto", "conti");
/* 2277 */           return app;
        } 
      } 
    } 
/* 2281 */     return app;
  }
  
  protected boolean forEach(JSONArray conti, String debtoraccount, IMessage aMessage) {
/* 2285 */     return (new UserAppService()).forEach(conti, debtoraccount, (IMessage)new PrefixMessage(aMessage, getClass().getSimpleName()));
  }

  
  public String crypt(String asignature, EntityDescriptorReaderHolder entityDescriptorReaderHolder, IMessage aMessage) {
    try {
/* 2291 */       Certificate certForEncrypt = certificateRandomForEncrypt(entityDescriptorReaderHolder);


      
/* 2295 */       JWEHeader cryptHeader = (new JWEHeader.Builder(entityDescriptorReaderHolder.getRandomEncAlg(), entityDescriptorReaderHolder.getRandomEncMtd())).contentType("JWE").type(JOSEObjectType.JWT).build();
      
/* 2297 */       JWEObject jweObjectCrypt = new JWEObject(cryptHeader, new Payload(asignature));
      
/* 2299 */       RSAEncrypter rSAEncrypter = new RSAEncrypter((RSAPublicKey)certForEncrypt.getPublicKey());
      
/* 2301 */       jweObjectCrypt.encrypt((JWEEncrypter)rSAEncrypter);
      
/* 2303 */       return jweObjectCrypt.serialize();
    }
/* 2305 */     catch (Exception x) {
      
/* 2307 */       aMessage.error(getClass().getName() + " CIFRATURA DELLA FIRMA UTENTE", x);
/* 2308 */       return null;
    } 
  }








  
  protected boolean verifyQRCodeSignature(HttpServletRequest aRequest, IResponseHolder aPageRedirectHolder, IMessage message, IMainApplicationConfig mainConfig, IMainFederationConfig federationConfig, ChallengeStore challengeStore, JSONObject datalogin) {
/* 2321 */     IXMLSigner xmlsigner = mainConfig.getXmlsigner();
/* 2322 */     IRealmService posteItRealmService = MainApplicationServiceLocator.dataRealmService();
/* 2323 */     IAccessorProvider posteItAccessorProvider = posteItRealmService.provider();
    
/* 2325 */     String jti = (String)datalogin.getOrDefault("jti", "not-setted");


    
/* 2329 */     String data = challengeStore.get(jti);
/* 2330 */     if (Utils.isEmpty(data)) {
/* 2331 */       message.error(getClass().getName() + " ID TRANSAZIONE NON VALIDO  ");
/* 2332 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 2333 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2334 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
      
/* 2336 */       return false;
    } 

    
/* 2340 */     JSONObject datax = new JSONObject(data);
/* 2341 */     if ("az_timeout".equals(datax.optString("status", ""))) {
/* 2342 */       message.error(getClass().getName() + " ID TRANSAZIONE SCADUTO  ");





      
/* 2349 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
      
/* 2351 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2352 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");

      
/* 2355 */       return false;
    } 




    
/* 2362 */     JSONObject jdata = new JSONObject(data);
/* 2363 */     String status = jdata.getString("status");
    
/* 2365 */     if (!"az_user_confirmed".equals(status)) {
/* 2366 */       message.error(getClass().getName() + " LA TRANSAZIONE SI TROVA IN UNO STATO NON VALIDO [" + getClass().getName() + "] RICHIESTO [az_user_confirmed] ");
/* 2367 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
      
/* 2369 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2370 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
      
/* 2372 */       return false;
    } 

    
/* 2376 */     String userSignatureTransaction = jdata.optString("user-signature-transaction", "");
/* 2377 */     String usersigtype = jdata.optString("sigtype", "JWS");
    
/* 2379 */     String commonname = "";
/* 2380 */     GenericHolder<String> appnameholder = new GenericHolder();
/* 2381 */     GenericHolder<String> appidholder = new GenericHolder();
/* 2382 */     GenericHolder<String> aztoolholder = new GenericHolder();
/* 2383 */     GenericHolder<String> amrholder = new GenericHolder();
    
/* 2385 */     if ("JWS".equals(usersigtype)) {
      try {
/* 2387 */         commonname = (new VerifyJWTSignedChallenge(jti, userSignatureTransaction, message, federationConfig, xmlsigner)).exec(appnameholder, appidholder, aztoolholder, amrholder);
/* 2388 */       } catch (Exception x) {
/* 2389 */         message.error(getClass().getName() + " VERIFICA FIRMA FALLITA  SIGTYPE [" + getClass().getName() + "]", x);
/* 2390 */         aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 2391 */         aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2392 */         aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
        
/* 2394 */         return false;
      } 
    }
    
/* 2398 */     String cf = parseCf(commonname, message);
    
/* 2400 */     if (Utils.isEmpty(cf)) {
/* 2401 */       message.error(getClass().getName() + " IMPOSSIBILE ESTRARE IL CF DAL CERTIFICATO DI FIRMA  ");
/* 2402 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 2403 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2404 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
      
/* 2406 */       return false;
    } 

    
/* 2410 */     if (!commonname.equals(datalogin.getOrDefault("usertoken", ""))) {
/* 2411 */       message.error(getClass().getName() + " IL CN ARRIVATO DALLA LOGIN  [" + getClass().getName() + "] E DIVERSO DA QUELLO ESTRATTO DALLA FIRMA [" + (String)datalogin.getOrDefault("usertoken", "") + "]");
/* 2412 */       aPageRedirectHolder.setType("TYPE_RESPONSE_PAGE");
/* 2413 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2414 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
      
/* 2416 */       return false;
    } 

    
/* 2420 */     IRealmService service = MainApplicationServiceLocator.dataRealmService();
/* 2421 */     IAccessorProvider ldapAccessorProvider = service.provider();
/* 2422 */     AdminUserSearcher search = new AdminUserSearcher(ldapAccessorProvider);
/* 2423 */     search.put("taxCode", cf);
/* 2424 */     List<UserViewer> r = search.search(2);
/* 2425 */     int size = r.size();
/* 2426 */     if (size > 1) {
/* 2427 */       message.error(getClass().getName() + " TROVATI PIU UTENTI PER IL CF [" + getClass().getName() + "]  FIRMA [" + cf + "] ");
/* 2428 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2429 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
/* 2430 */       return false;
    } 
    
/* 2433 */     if (size == 0) {
/* 2434 */       message.error(getClass().getName() + "  CF [" + getClass().getName() + "] INESISTENTE. FIRMA [" + cf + "] ");
/* 2435 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2436 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
/* 2437 */       return false;
    } 
    
/* 2440 */     UserViewer userViewer = r.get(0);
    
/* 2442 */     User user = new User(posteItAccessorProvider, userViewer.getUserName());
/* 2443 */     PasswordAuthenticationPolicy authenticationPolicy = new PasswordAuthenticationPolicy("", (IClientIp)new ClientIp(UtilIpRemote.getClientIpAddress(aRequest)));
/* 2444 */     authenticationPolicy.setUserDisambiguation(true);
/* 2445 */     user.authenticate(authenticationPolicy);
    
/* 2447 */     if (!authenticationPolicy.authenticated()) {
/* 2448 */       message.error(getClass().getName() + "  CF [" + getClass().getName() + "] UTENTE [ " + cf + "] . FIRMA [" + userViewer.getUserName() + "] NON AUTENTICATO " + usersigtype + "  - " + authenticationPolicy.response().code());
/* 2449 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
      
/* 2451 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
/* 2452 */       if (authenticationPolicy.response().code().equals("24")) {
/* 2453 */         aPageRedirectHolder.setMessage("L'utenza con cui stai tentando di accedere &egrave; bloccata per troppi tentativi errati di autenticazione. Ti invitiamo a riprovare pi&ugrave; tardi.");
      }

      
/* 2457 */       return false;
    } 
    
/* 2460 */     user.load();
/* 2461 */     boolean az = user.getRoleNames().contains("users,portal");
    
/* 2463 */     if (!az) {
/* 2464 */       message.error(getClass().getName() + "  CF [" + getClass().getName() + "] UTENTE [ " + cf + "] . FIRMA [" + userViewer.getUserName() + "] NON AUTORIZZATO");
/* 2465 */       aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2466 */       aPageRedirectHolder.setMessage("Impossibile autorizzare la richiesta");
/* 2467 */       return false;
    } 







    
/* 2477 */     if (datalogin.containsKey("rsa")) {
/* 2478 */       JSONObject rsa = (JSONObject)datalogin.getOrDefault("rsa", new JSONObject());
/* 2479 */       if (rsa.getOrDefault("rsa-action-code", "").equals("DENY")) {
/* 2480 */         message.error(getClass().getName() + "  CF [" + getClass().getName() + "] UTENTE [ " + cf + "] . FIRMA [" + userViewer.getUserName() + "] AUTORIZZAZIONE NEGATA DA RSA");
/* 2481 */         aPageRedirectHolder.setContent((String)mainConfig.getPages().get("PAGE_GENERIC_ERROR"));
/* 2482 */         aPageRedirectHolder.setMessage("Autenticazione non andata a buon fine.  [Codice AAX]");
/* 2483 */         return false;
      } 
    } 



    
/* 2490 */     datalogin.put("usertoken", userViewer.getUserName());
/* 2491 */     datalogin.put("CN", commonname);
/* 2492 */     datalogin.put("CF", cf);
/* 2493 */     datalogin.put("p_derivated", userViewer.getUserName());
/* 2494 */     datalogin.put("p_app_name", appnameholder.value());


    
/* 2498 */     datalogin.put("acr_values", "3");
/* 2499 */     datalogin.put("amr", amrholder.value());
/* 2500 */     return true;
  }
  
  protected String usernameByTaxcode(String taxcode) {
/* 2504 */     List<ManagedAttribute> resultAttribute = new ArrayList();
    
/* 2506 */     ManagedAttribute m = new ManagedAttribute();
/* 2507 */     m.setName("userid");
/* 2508 */     resultAttribute.add(m);
    
/* 2510 */     Map<Object, Object> returnedAttribute = new HashMap<>();
    
/* 2512 */     Map<String, String> searchCriterionMap = new HashMap<>();
/* 2513 */     searchCriterionMap.put("taxCode", taxcode);
    
/* 2515 */     (new UserAttributesSearcher(resultAttribute, searchCriterionMap, returnedAttribute, this.message)).write();
    
/* 2517 */     if (returnedAttribute.size() == 0) {
/* 2518 */       return "";
    }
/* 2520 */     if (!returnedAttribute.containsKey("userid")) {
/* 2521 */       return "";
    }
    
/* 2524 */     StringValueHolder holder = (StringValueHolder)returnedAttribute.get("userid");
    
/* 2526 */     return holder.asString();
  }
  
  public String asString(Set errors) {
/* 2530 */     StringBuilder builder = new StringBuilder();
    
/* 2532 */     Iterator liter = errors.iterator();
/* 2533 */     while (liter.hasNext()) {
      
/* 2535 */       builder.append(liter.next());
/* 2536 */       if (liter.hasNext()) {
/* 2537 */         builder.append(",");
      }
    } 
    
/* 2541 */     return builder.toString();
  }

  
  protected void forEachAdditionData(JSONObject transaction, Map returnedSignedData) {
/* 2546 */     Set keys = returnedSignedData.keySet();
/* 2547 */     Iterator<String> liter = keys.iterator();
/* 2548 */     String key = "";
    
/* 2550 */     while (liter.hasNext()) {
      
/* 2552 */       key = liter.next();
/* 2553 */       if (!transaction.has(key))
      {
/* 2555 */         transaction.put(key, returnedSignedData.get(key));
      }
    } 
  }




  
  protected void getCredentials(String auth, Map<String, String> result) {
    try {
/* 2566 */       String userpassEncoded = auth.substring(6);
/* 2567 */       String userpassDecoded = new String((new Base64Encoder()).decode(userpassEncoded));
      
/* 2569 */       String[] splitted = userpassDecoded.split(":");
/* 2570 */       int len = splitted.length;
      
/* 2572 */       StringBuffer usernamex = new StringBuffer();
/* 2573 */       for (int i = 0; i < len - 1; i++) {
/* 2574 */         usernamex.append(splitted[i]);
/* 2575 */         if (i != len - 2) {
/* 2576 */           usernamex.append(":");
        }
      } 
      
/* 2580 */       result.put("username", usernamex.toString());
/* 2581 */       result.put("password", splitted[len - 1]);
/* 2582 */     } catch (Exception e) {
      
/* 2584 */       result.put("username", "");
/* 2585 */       result.put("password", "");
    } 
  }

  
  protected String removeFM(String aData) {
    try {
/* 2592 */       JSONObject dd = new JSONObject(aData);
/* 2593 */       if (dd.has("fm")) {
/* 2594 */         dd.remove("fm");
      }
      
/* 2597 */       if (dd.has("header")) {
/* 2598 */         dd.remove("header");
      }
      
/* 2601 */       return dd.toString();
    }
/* 2603 */     catch (Exception e) {
      
/* 2605 */       return aData;
    } 
  }

  
  protected JSONArray filterForAmr(EntityDescriptorReaderHolder entityDescriptorReaderHolder, JSONArray jsonArray, IMessage message) {
/* 2611 */     return (new UserAppService()).filterForAmr(entityDescriptorReaderHolder, jsonArray, (IMessage)new PrefixMessage(message, getClass().getSimpleName()));
  }
}


/* Location:              F:\Sorgenti\_Da analizzare\43.2306-2706-2025\RITM2259999 - SBD_4242 - jod-idp-retail 1.309\jod-idp-retail-linux64-1.309.war!\WEB-INF\classes\org\jod\idp\web\jwtoperation\AJwtAuthnRequestPostFSOperation.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */