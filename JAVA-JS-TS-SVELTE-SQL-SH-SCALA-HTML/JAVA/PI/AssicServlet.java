/*
 * Created on 28-gen-04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.infogroup.poste.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infogroup.ebaasposte.exceptions.EBAASErrorException;
import com.infogroup.ebaasposte.pv.connector.fda1v.ListaRelContraente;
import com.infogroup.ebaasposte.pv.connector.fda1v.Professione;
import com.infogroup.ebaasposte.pv.connector.fda1v.RappLegale;
import com.infogroup.ebaasposte.pv.connector.fda1v.RefreshLogon;
import com.infogroup.ebaasposte.pv.connector.fvsyy.CLEBMVRK_element;
import com.infogroup.poste.bean.FDA1VBean;
import com.infogroup.poste.bean.RelazioneBean;
import com.infogroup.poste.common.CICSConnectionProps;
import com.infogroup.poste.common.EBAASUtility;
import com.infogroup.poste.common.Enumerations;
import com.infogroup.poste.common.HTMLUtils;
import com.infogroup.poste.common.Operatore;
import com.infogroup.poste.common.RolloutConfiguration.ServiceRollInfo;
import com.infogroup.poste.common.SessionUtil;
import com.infogroup.poste.servlet.conf.EbaasConfiguration;

import it.eng.poste.ebaas.commons.persistence.CloseableSessionFactory;
import it.eng.poste.ebaas.exceptions.EBAASAbendException;
import it.eng.poste.ebaas.exceptions.EBAASConnectionException;
import it.eng.poste.ebaas.exceptions.InvalidSessionException;
import it.eng.poste.ebaas.persistence.dao.ComuniDAO;
import it.eng.poste.ebaas.persistence.exceptions.EBAASDAOException;
import it.eng.poste.ebaas.rs.question.QuestionOutputBean;
import it.eng.poste.ebaas.rs.question.QuestionRestClient;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssicServlet extends EBAASGenericServlet {
	private static Logger logger = LogManager.getLogger();

	public void DecessoPrimoAssicServlet(Logger log) {
		super(log);
	}

	public void DecessoPrimoAssicServlet() {
		super(logger);
	}

	protected void doInternalGet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		doInternalPost(req, resp);
	}

	protected void doInternalPost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		HttpSession session = req.getSession(false);

		Operatore operatore = (Operatore) session.getAttribute("operatore");
		// prendo la polizza selezionata
		CLEBMVRK_element polizza = (CLEBMVRK_element) session.getAttribute("polizza");
		if (operatore != null && polizza != null) {

			FDA1VBean bean = (FDA1VBean) session.getAttribute("DecessoPrimoAssicBean");
			if(bean == null){
				bean = new FDA1VBean();
			}
			String jspToCall = "";

			String operazione = req.getParameter("esegui");


			// se l'utente ha variato la provincia, devo ricaricare
			// la pagina con le nuove localita'
			if (operazione != null && operazione.equals("PROVINCIA")) {
				String prov = req.getParameter("lstProv");
				logger.debug("Selezionata provincia '" + prov + "'");
				bean = (FDA1VBean) session.getAttribute("DecessoPrimoAssicBean");
				bean.getOutArea().pr_c = prov;
				bean.getOutArea().ir_c = req.getParameter("TxtIndRecapito");
				bean.getOutArea().nazr_c = req.getParameter("TxtNazioneRecapito");
				bean.getOutArea().capr_c = req.getParameter("TxtCAPRecapito");
				bean.getOutArea().flg_aut_com=req.getParameter("LstDatiPersonali");
				bean.getOutArea().dsc_comune_doc=req.getParameter("TxtComuneRilascioDocumento");
				bean.getOutArea().provd_c=req.getParameter("TxtProvRilascioDocumento");
				req.setAttribute("indexFlag",req.getParameter("indexFlag"));
				req.setAttribute("indexProf",req.getParameter("indexProf"));

				if (session.getAttribute("optionProvince") == null){
					session.setAttribute("optionProvince", EbaasConfiguration.listOPTIONSProvince());
				}

				if (session.getAttribute("optionNazioni") == null)
					session.setAttribute("optionNazioni", EbaasConfiguration.listOPTIONSNazioni());                   

		    	try (CloseableSessionFactory closeableSessionFactory = new CloseableSessionFactory()) {
		    		ComuniDAO comuniDAO = new ComuniDAO(closeableSessionFactory.getActiveTransaction());
		    		session.setAttribute("optionComuni", HTMLUtils.eval(comuniDAO.listByProvincia(prov), "<option value='#filledCodice##cap#'>#comune#</option>"));
				} catch (Exception e) {
					throw new EBAASDAOException("Errore nel recupero dei dati dal DB", e);
				}

				super.callPage(req, resp, "Var_Contraenza_Dec.jsp&ricaricato=si");
				return;
			}                   

			if (session.getAttribute("optionProvince") == null){
				session.setAttribute("optionProvince", EbaasConfiguration.listOPTIONSProvince());
			}
			if (session.getAttribute("optionNazioni") == null)
				session.setAttribute("optionNazioni", EbaasConfiguration.listOPTIONSNazioni()); 

			bean.setInAreaIst(operatore.getIstituto());
			bean.setInAreaN_contrat(polizza.n_contrat);
			bean.setInAreaCod_funz("EDA1");

			bean.setInAreaToken(operatore.getToken());
			bean.setInAreaUserid(operatore.getUserId());
			bean.setInAreaDscPrest(operatore.getDsc_prest());
			bean.setInAreaCod_g(polizza.cod_conv);
			bean.setInAreaCod_conv(polizza.cod_conv);
			bean.setInAreaNumRelaz(polizza.num_relaz);
			bean.setSessione(operatore.getSessioneEBAAS());
			if("".equals(operatore.getToken())||operatore.getToken()==null){
				bean.setInAreaSportello(operatore.getSportello());
			}
			else{
				bean.setInAreaSportello("DIREZ");
			}

			bean.setInAreaCodoperat(operatore.getUserId());



			// VARIAZIONE
			if (operazione != null && operazione.equals("VARIA")) {
				bean.setOperazione("DA1V3");


				String ind = req.getParameter("TxtIndRecapito");
				String prov = req.getParameter("lstProv");
				//IVAN
				//String loc = req.getParameter("lstLoc");
				String loc = req.getParameter("desc_localita");
				//FINE IVAN
				String cap = !"".equals(req.getParameter("varCapRecap")) ? req.getParameter("varCapRecap") : req.getParameter("TxtCAPRecapito");
				String[] pol = req.getParameterValues("lstPol");



				String naz = req.getParameter("TxtNazioneRecapito");

				if (!naz.trim().equals("ITALIA")) {
					cap = "99999";
					prov = "EE";
					loc = req.getParameter("LocEstera");
				}

				bean.setInAreaNdg(req.getParameter("ndg"));
				bean.setInAreaN_sog(req.getParameter("n_sog"));
				bean.setInAreaCodoperat(req.getParameter("codoperat"));
				bean.setInAreaCod_operat_aggior(req.getParameter("cod_operat_aggior"));
				bean.setInAreaCod_prof(req.getParameter("LstProfessione"));
				bean.setInAreaFlg_aut_com(req.getParameter("LstDatiPersonali"));
				bean.setInAreaCapr_c(cap);
				bean.setInAreaPr_c(prov);
				bean.setInAreaNazr_c(naz);
				bean.setInAreaIr_c(ind);
				bean.setInAreaLr_c(loc);

				bean.setInAreaDsc_comune_doc(req.getParameter("TxtComuneRilascioDocumento"));
				bean.setInAreaProvd_c(req.getParameter("TxtProvRilascioDocumento"));
				//CC-C
				//COD-MOD-PAG
				//DSC-RAPPORTO-1

				bean.setInAreaNum_sogg_rappr(req.getParameter("LstRL"));
				bean.setInAreaCod_terminal(operatore.getTermId());

				if (req.getParameter("LstRL")!= null)
					bean.setInAreaNdgrapleg(EBAASUtility.addZero(15,req.getParameter("h_ndg_rappleg")));


				//SETTO LA CLEBREFR NEL CASO UFFICI CENTRALI O POSTALI
				ArrayList listaLogon= new ArrayList();  
				RefreshLogon nuovologon = new RefreshLogon();
				if (operatore.getToken() != null && !"".equals(operatore.getToken())) { 
					nuovologon.profilo_utente = operatore.getProfilo();
					nuovologon.token = operatore.getToken();
					nuovologon.userid = operatore.getUserId();
					bean.setInAreaSportello("DIREZ");
				}
				else {
					nuovologon.profilo_utente = operatore.getProfilo();
					nuovologon.userid = operatore.getUserId();
					bean.setInAreaSportello(operatore.getSportello());
				}
				listaLogon.add(nuovologon); 

				bean.setInCLEBREFR(listaLogon);

				//SETTO LA CLEBRLLS
				ArrayList listaRapLeg= new ArrayList();
				String indexRL = req.getParameter("LstRL");
				if (indexRL != null) {
					int indRLSel = Integer.parseInt(indexRL);
					RappLegale nuovorapleg = new RappLegale();
					nuovorapleg.cf = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).cf;
					nuovorapleg.cod_prov_nasc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).cod_prov_nasc;
					nuovorapleg.cod_tipo_doc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).cod_tipo_doc;
					nuovorapleg.cod_prov_nasc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).cod_prov_nasc;
					nuovorapleg.cogn = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).cogn;
					nuovorapleg.dat_doc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).dat_doc;
					nuovorapleg.dat_nasc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).dat_nasc;
					nuovorapleg.dsc_ente_doc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).dsc_ente_doc;
					nuovorapleg.loc_nsc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).loc_nsc;
					nuovorapleg.ndg = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).ndg;
					nuovorapleg.nome = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).nome;
					nuovorapleg.num_doc = ((RappLegale)bean.getOutCLEBRLLS().get(indRLSel)).num_doc;
	
					listaRapLeg.add(nuovorapleg); 
					bean.setInCLEBRLLS(listaRapLeg);
				}

				//SETTO LA CLEBPROF
				ArrayList listaProf= new ArrayList();  
				Professione nuovoprof = new Professione();
				int indProfSel = Integer.parseInt(req.getParameter("indexProf"));
				nuovoprof.cod_prof = ((Professione)bean.getOutCLEBPROF().get(indProfSel)).cod_prof;
				nuovoprof.dsc_denominazione = ((Professione)bean.getOutCLEBPROF().get(indProfSel)).dsc_denominazione;
				listaProf.add(nuovoprof); 
				bean.setInCLEBPROF(listaProf);


				//bean.setInAreaNumElencoRefr(EBAASUtility.addZero(3,""+bean.getInCLEBREFR().size()));
				//FINE SETTAGGIO CLEBREFR NEL CASO UFFICI CENTRALI O POSTALI


				caricaModPag(bean,req);

				bean.setInAreaDat_Scad_Doc(req.getParameter("TxtDataScadenzaDocumento"));

				//Relazioni
				bean.setInAreaFlg_combo_box1(bean.getOutArea().flg_combo_box1);
				bean.setInAreaCod_rel_contrn_brend(req.getParameter("lstRelContraenteSubentrante"));
				bean.setInAreaDsc_rel_brend(req.getParameter("txtDescLib"));
				bean.setInAreaFlg_combo_box2(bean.getOutArea().flg_combo_box2);
				bean.setInAreaCod_rel_contrn_contro(req.getParameter("lstRelContraenteAssicurato"));
				bean.setInAreaDsc_relcontr(req.getParameter("txtDescLib2"));
				bean.setInAreaFlg_combo_box3(bean.getOutArea().flg_combo_box3);
				bean.setInAreaCod_rel_contrn_assic(req.getParameter("lstRelContraenteBeneficiario"));
				bean.setInAreaDsc_rel_assic(req.getParameter("txtDescLib3"));                


				req.setAttribute("TitoloOK","RICHIESTA DECESSO PRIMO ASSICURATO");
				//jspToCall = "ok.jsp";
				jspToCall = "ok.jsp&functionToCall=" + java.net.URLEncoder.encode("openStampa()");

			}
			//  QUESTIONARIO VALIDATO
			else if (operazione != null && operazione.equals("QUEST")){
				bean.setOperazione("DA1V3");
				String cap = req.getParameter("varCapRecap")!= "" ? req.getParameter("varCapRecap") : req.getParameter("TxtCAPRecapito");
				bean.setInAreaCapr_c(cap);
				req.setAttribute("TitoloOK","RICHIESTA DECESSO PRIMO ASSICURATO");
				jspToCall = "ok.jsp&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
			}
			//	CONSULTAZIONE 
			else {
				bean.setOperazione("DA1V1");

				jspToCall = "Decesso_Primo_Assic.jsp";
			}

			//	Invoco il Servizio
			CICSConnectionProps prop = (CICSConnectionProps) session.getAttribute("cicsProps");
			if (bean.callHOST(prop, operatore.toString())) {
				if (bean.getMgenerr() == 99){
					throw new EBAASAbendException(bean.getMgenerrdsdc());
				}
				//              Richiesta Valerio 24/05/07 per stampare il modello 7B
				if (bean.getMgenerr() == 2){
					session.setAttribute("xmlInput",bean.print);
					session.setAttribute("filexpr", bean.outArea.dsc_modello);
					jspToCall += "&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
				}

				else if (bean.getMgenerr() != 0){
					throw new EBAASErrorException(bean.getError().msgDsc, bean.getError().msgTip);
				}

				// Preparazione Sessione per stampa 

				if (req.getParameter("esegui") != null) {
					session.setAttribute("xmlInput",bean.print);
					session.setAttribute("filexpr", bean.outArea.dsc_modello);
				} else{
					//MiFID
					if (bean.getMgenerr() != 4){
						session.setAttribute("DecessoPrimoAssicBean", bean);
					}

					logger.debug("Oggetto (DecessoPrimoAssicBean) inserito in sessione.");              
				}

				try (CloseableSessionFactory closeableSessionFactory = new CloseableSessionFactory()) {
		    		ComuniDAO comuniDAO = new ComuniDAO(closeableSessionFactory.getActiveTransaction());
		    		session.setAttribute("optionComuni", HTMLUtils.eval(comuniDAO.listByProvincia(bean.outArea.pr_c), "<option value='#filledCodice##cap#'>#comune#</option>"));
					String prov = bean.getOutArea().pr_c;
					String lstLoc = bean.getOutArea().lr_c;
					session.setAttribute("codiceLocalitaRecapito",comuniDAO.getCodiceComuneBy(prov,lstLoc));
					operatore.setSessioneEBAAS(bean.getSessione());
				} catch (Exception e) {
					throw new EBAASDAOException("Errore nel recupero dei dati dal DB", e);
				}

				

				/* INIZIO Modifica branch comuni zonati */
				setSessionCodiceLocalitaRecapito(bean,session);
				/* FINE Modifica branch comuni zonati */



				//               	Popolo le comboBox delle Relazioni

				String flgCombox1 = bean.getOutArea().flg_combo_box1 != null ? bean.getOutArea().flg_combo_box1 : "0";
				String flgCombox2 = bean.getOutArea().flg_combo_box2 != null ? bean.getOutArea().flg_combo_box2 : "0";
				String flgCombox3 = bean.getOutArea().flg_combo_box3 != null ? bean.getOutArea().flg_combo_box3 : "0";

				List listaRelazioni = new ArrayList();
				List listaRelazioniFiltrata = new ArrayList();
				List listaRelazioni1 = new ArrayList();
				List listaRelazioni2 = new ArrayList();
				List listaRelazioni3 = new ArrayList();

				if(bean.getOutCLEBRELC() != null){
					Iterator iteratore = bean.getOutCLEBRELC().iterator();
					while(iteratore.hasNext()){
						ListaRelContraente lrb = (ListaRelContraente) iteratore.next();
						RelazioneBean relazione = new RelazioneBean();
						relazione.setCodiceRelazione(lrb.cod_rel);
						relazione.setCodNaturaGiuridica(lrb.cod_natura_giurid);
						relazione.setDescrizioneRelazione(lrb.dsc_rel);
						listaRelazioni.add(relazione);
						if(!"PG".equalsIgnoreCase(lrb.cod_natura_giurid)){
							listaRelazioniFiltrata.add(relazione);
						}
					}
				}

				if("1".equals(flgCombox1))
					listaRelazioni1.addAll(listaRelazioni);
				else if ("2".equals(flgCombox1))
					listaRelazioni1.addAll(listaRelazioniFiltrata);

				if("1".equals(flgCombox2))
					listaRelazioni2.addAll(listaRelazioni);
				else if ("2".equals(flgCombox2))
					listaRelazioni2.addAll(listaRelazioniFiltrata);

				if("1".equals(flgCombox3))
					listaRelazioni3.addAll(listaRelazioni);
				else if ("2".equals(flgCombox3))
					listaRelazioni3.addAll(listaRelazioniFiltrata);

				session.setAttribute("listaRelazioni1", listaRelazioni1);
				session.setAttribute("listaRelazioni2", listaRelazioni2);
				session.setAttribute("listaRelazioni3", listaRelazioni3);

				session.setAttribute("flgCombox1", flgCombox1);
				session.setAttribute("flgCombox2", flgCombox2);
				session.setAttribute("flgCombox3", flgCombox3);

				session.setAttribute("listaRelazioni", listaRelazioni);



				//              <!-- Includiamo la modalita pagamento solo se arrivano le cleb -->
				String includimodalitaPagamento = "SI";
				if(bean.getOutCLEBMMG2() != null && bean.getOutCLEBMMG2().size() > 0){
					if((bean.getOutCLEBCYLS() != null && bean.getOutCLEBCYLS().size() > 0) || (bean.getOutCLEBLIBR() != null && bean.getOutCLEBLIBR().size() > 0 )){
						includimodalitaPagamento = "SI";
					}
					else{
						includimodalitaPagamento = "NO";
					}
				}
				else{
					includimodalitaPagamento = "NO";
				}
				session.setAttribute("includimodalitaPagamento", includimodalitaPagamento);


				///////////Chiamata questionario

				//Chiama il questionario per l'Antiriciclaggio (Fabio)
				ServiceRollInfo serviceConf = SessionUtil.getServiceRolloutConfiguration(session, Enumerations.SERVIZI.QUESTION.toString());
			boolean isRolled= serviceConf.isRolled();
				
				
				if(!isRolled) {
				if ("1".equals(bean.getOutArea().flg_link_mfi)){
					String linkMiFID = bean.getOutArea().dsc_link_adever ;
					if (linkMiFID != null) {
						if (bean.getOutArea().dsc_param_adever != null)
							linkMiFID += "?" + bean.getOutArea().dsc_param_adever;
						linkMiFID = linkMiFID.replaceAll(" ", "");				
						req.setAttribute("linkMiFID", linkMiFID);
						logger.debug("linkMiFID flg_link_mfi[1]= " + linkMiFID);
					} else 
						throw new EBAASErrorException("LINK MiFID ASSENTE", "12", "WEBERR", null);	
					//jspToCall = "Var_Contraenza_VerificaQuest.jsp?action=ECED&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
					jspToCall = "Decesso_PrimoAssicurato_VerificaQuest.jsp?action=EDA1&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
				}}else {
 					
 					String link= bean.getOutArea().dsc_param_adever;
 					if(link!=null && "1".equals(bean.getOutArea().flg_link_mfi)) {
 					String[] a = link.split("&");
// 					String sottosistema= a[0].split("=")[1].trim();
// 					String userId= a[1].split("=")[1].trim();
 					String ndgTit=a[2].split("=")[1].trim();
// 					String ndgEse=a[3].split("=")[1].trim();
// 					String tipoEse=a[4].split("=")[1].trim();
// 					String ndgAssicurato= a[5].split("=")[1].trim();
// 					String flagTerzoPagatore= a[6].split("=")[1].trim();
// 					String ndgTerzoPagatore= a[7].split("=")[1].trim();
// 					String prodotto= a[8].split("=")[1].trim();
// 					String dipend=a[9].split("=")[1].trim();
// 					String tipoCliente=a[10].split("=")[1].trim();
// 					String tipoOperazione=a[11].split("=")[1].trim();
 					String tipoProdotto=a[12].split("=")[1].trim();
// 					String tipoNatura=a[13].split("=")[1].trim();
// 					String flagBen= a[14].split("=")[1].trim();
// 					String importoTotale=a[15].split("=")[1].trim();
// 					String flagQavFisc=a[16].split("=")[1].trim();
// 					String frazionario= SessionUtil.getFrazionario(session);
// 					String codFiscTit="";
// 					String codFiscEse="";
// 					logger.info("link"+ link);
// 					logger.info("sottosistema"+ sottosistema);
// 					logger.info("userId"+ userId );
// 					logger.info("ndgTit"+ ndgTit );
// 					logger.info("ndgEse"+ ndgEse );
// 					logger.info("tipoEse"+ tipoEse);
// 					logger.info("ndgAssicurato    "+ ndgAssicurato);
// 					logger.info("dipend"+ dipend );
// 					logger.info("tipoCliente "+ tipoCliente );
// 					logger.info("tipoOperazione   "+ tipoOperazione    );
// 					logger.info("tipoProdotto"+ tipoProdotto);
// 					logger.info("tipoNatura  "+ tipoNatura  );
// 					logger.info("importoTotale  "+ importoTotale  );
// 					logger.info("flagQavFisc "+ flagQavFisc );
// 					logger.info("flagTerzoPagatore"+ flagTerzoPagatore );
// 					logger.info("ndgTerzoPagatore "+ ndgTerzoPagatore  );
// 					
 				
 					
 					if("".equals(tipoProdotto)  || tipoProdotto==null) {
						throw new EBAASErrorException("LINK MiFID1 NON FORMATTATO CORRETTAMENTE", "12", "WEBERR", null);
					}
 					QuestionRestClient questionRestClientVerifica = new QuestionRestClient(EbaasConfiguration.getQuestionEndPoint());
 					
 	 				QuestionOutputBean output = questionRestClientVerifica.verifica(tipoProdotto, ndgTit);		
            
                		 if (output.getReturnCode()!=null && ("00".equals(output.getReturnCode())|| "01".equals(output.getReturnCode()))){
         					String linkMiFID = bean.getOutArea().dsc_link_adever ;
         					if (linkMiFID != null) {
         						if (bean.getOutArea().dsc_param_adever != null)
         							linkMiFID += "?" + bean.getOutArea().dsc_param_adever;
         						linkMiFID = linkMiFID.replaceAll(" ", "");				
         						req.setAttribute("linkMiFID", linkMiFID);
         						logger.debug("linkMiFID flg_link_mfi[1]= " + linkMiFID);
         					} else 
         						throw new EBAASErrorException("LINK MiFID ASSENTE", "12", "WEBERR", null);	
         					//jspToCall = "Var_Contraenza_VerificaQuest.jsp?action=ECED&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
         					jspToCall = "Decesso_PrimoAssicurato_VerificaQuest.jsp?action=EDA1&functionToCall=" + java.net.URLEncoder.encode("openStampa()");
         				}else if ("02".equals(output.getReturnCode())) {
         				      
        					bean.setOperazione("QA1V3");
                        	if (bean.callHOST(prop, operatore.toString())) {
                        		if (bean.getMgenerr() == 99)
               					throw new EBAASAbendException(bean.getMgenerrdsdc());

                        		if (bean.getMgenerr() != 0)
                    				throw new EBAASErrorException(bean.getError().msgDsc, bean.getError().msgTip);

                         }else {
                        	logger.error(operatore.toString() + "] Connessione a HOST terminata con errore: " + bean.getHostErrorMessage());
           				throw new EBAASConnectionException("Connessione a HOST terminata con errore", bean.getHostErrorMessage());
                         }
                        	session.setAttribute("xmlInput",bean.print);
        					session.setAttribute("filexpr", bean.outArea.dsc_modello);
                         }
				}
 					
				//fine

				}
				super.callPage(req, resp, jspToCall);
			} else {
				logger.error(operatore.toString()+ "] Connessione a HOST terminata con errore: " + bean.getHostErrorMessage());
				throw new EBAASConnectionException("Connessione a HOST terminata con errore", bean.getHostErrorMessage());
			}
		} else {
			logger.error("Sessione non valida [Operatore/Polizza non in sessione]");
			throw new InvalidSessionException("Sessione non valida");
		}
	}

	protected void caricaModPag(FDA1VBean bean,HttpServletRequest req) {
		// DATI MODALITA' DI PAGAMENTO
		String codModPag = req.getParameter("LstTipiPagamento");
		if (codModPag != null) {
			bean.setInAreaCod_mod_pag(codModPag);

			String cc = req.getParameter("LstCC");
			String libretto = req.getParameter("LstLibretti");
			//caso c/c
			if (codModPag.equals("01") && cc != null) {
				int pos = cc.indexOf('@');
				String intest = cc.substring(pos + 1);
				cc = cc.substring(0, pos);
				bean.setInAreaNumCc_c(cc);
				bean.setInAreaDsc_rapporto_1(intest);
			}
			//caso libretto
			if (codModPag.equals("13") && libretto != null) {
				int pos = libretto.indexOf('@');
				int posFlgDem = libretto.indexOf('#');
				int librettoLungh = libretto.length();
				String intest = libretto.substring(pos + 1, posFlgDem);	

				libretto = libretto.substring(0, pos);
				bean.setInAreaNumCc_c(libretto);
				bean.setInAreaDsc_rapporto_1(intest);
			}

		}

	}

	public String getDscRelazioneByCod(String cod, HttpSession session){
		String descrizione = "";
		List listaRelazioni = new ArrayList();
		if(cod == null)
			return null;
		else{
			listaRelazioni =  (List) session.getAttribute("listaRelazioni");
			Iterator iterator = listaRelazioni.iterator();
			while(iterator.hasNext()){
				RelazioneBean re = (RelazioneBean) iterator.next();
				if(cod.equalsIgnoreCase(re.getCodiceRelazione())){
					descrizione = re.getDescrizioneRelazione();
				}
			}
		}

		return descrizione;
	}

	/* INIZIO Modifica branch comuni zonati */
	private void setSessionCodiceLocalitaRecapito(FDA1VBean bean,HttpSession session) throws EBAASDAOException{
		try (CloseableSessionFactory closeableSessionFactory = new CloseableSessionFactory()) {
			ComuniDAO comuniDAO = new ComuniDAO(closeableSessionFactory.getActiveTransaction());
			String prov = bean.getOutArea().pr_c;
			String lstLoc= bean.getOutArea().lr_c;
			String codLoc = comuniDAO.getCodiceComuneBy(prov,lstLoc);
			session.setAttribute("codiceLocalitaRecapito",codLoc);
		} catch (Exception e) {
			throw new EBAASDAOException("Errore nel recupero dei dati dal DB", e);
		}

	}
	/* FINE Modifica branch comuni zonati */    
}