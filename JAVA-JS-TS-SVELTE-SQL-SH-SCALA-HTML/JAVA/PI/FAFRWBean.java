package com.infogroup.poste.bean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.infogroup.ebaasposte.pv.connector.fafrw.Error;
import com.infogroup.ebaasposte.pv.connector.fafrw.FAFRW;
import com.infogroup.ebaasposte.pv.connector.fafrw.InArea;
import com.infogroup.ebaasposte.pv.connector.fafrw.Oper;
import com.infogroup.ebaasposte.pv.connector.fafrw.OutArea;


public class FAFRWBean extends EBAASGenericBean {

	private static final long serialVersionUID = 2082338850665109660L;
	transient private static Logger logger = LogManager.getLogger();
	private FAFRW bean;

	private String inAreaIst;
	private String inAreaCodFunz;
	private String inAreaCodG;
	private String inAreaCodConv;
	private String inAreaNContrat;
	private String inAreaNumRelaz;
	private String inAreaCf;
	private String inAreaCogn;
	private String inAreaNome;
	private String inAreaNdg;
	private String inAreaDscConv;
	private String inAreaToken;
	private String inAreaUserId;
	private String inAreaDscPrest;
	private String inAreaSportello;
	private String inAreaCodOperat;
	
	private String inAreaFlagHub;
	private String inAreaNCOB;
	private String inAreaNCOS;
	
	public String print;
	
	private String progetto;
	private int canaleMgen;
	private String operazione;
	private int tpinst;
	private String canaleAppl;
	private Oper oper;
	private Error error;

	private InArea inArea;
	private java.util.ArrayList inCLEWCOMU;
	public OutArea outArea;

	private java.util.ArrayList outCLEWCOMU;

	// Wrapper per l'errore 
	private String sessione;
	private int mgenerr;
	private String mgenerrprg;
	private String mgenerrdsdc;
	
	public FAFRWBean() {
		super(logger);
		beanDescription = "Vista Light EMPTY";
		bean = new FAFRW();
	}

	public void callInternalHOST() throws Exception {
		// inizializzo i parametri di input
		bean.inArea = new InArea();

		inArea = bean.inArea;
		inArea.ist = this.inAreaIst;
		inArea.cod_funz = this.inAreaCodFunz;
		inArea.cod_g = this.inAreaCodG;
		inArea.cod_conv = this.inAreaCodConv;
		inArea.n_contrat = this.inAreaNContrat;
		inArea.num_relaz = this.inAreaNumRelaz;
		inArea.cf = this.inAreaCf;
		inArea.cogn = this.inAreaCogn;
		inArea.nome = this.inAreaNome;
		inArea.ndg = this.inAreaNdg;
		inArea.dsc_conv = this.inAreaDscConv;
		inArea.token = this.inAreaToken;
		inArea.userid = this.inAreaUserId;
		inArea.dsc_prest = this.inAreaDscPrest;
		inArea.sportello = this.inAreaSportello;
		inArea.codoperat = this.inAreaCodOperat;
		
		inArea.flg_hub = this.inAreaFlagHub;
		inArea.ncob = this.inAreaNCOB;
		inArea.ncos = this.inAreaNCOS;
		
        bean.canaleMgen = Integer.parseInt(com.infogroup.poste.common.EBAASConf.getCanaleMgen());
		bean.tpinst = Integer.parseInt(com.infogroup.poste.common.EBAASConf.getTpinst());
		bean.canaleAppl = com.infogroup.poste.common.EBAASConf.getCanaleAppl();
		bean.progetto = com.infogroup.poste.common.EBAASConf.getProgetto();
		bean.sessione = this.getSessione();
        bean.operazione = operazione;
        bean.inCLEWCOMU = inCLEWCOMU;
        
		setRouting(bean.getRouting());
        this.invoke(bean);

		setMgenerr(bean.oper.mgenerr);
		setSessione(bean.oper.sessione);
		setMgenerrdsdc(bean.oper.mgenerrdsdc);
		setMgenerrprg(bean.oper.mgenerrprg);

		this.print = bean.print;
		this.outArea = bean.outArea;
		this.outCLEWCOMU = bean.outCLEWCOMU;
		
		this.error = bean.error;
	}

	public FAFRW getBean() {
		return bean;
	}

	public void setBean(FAFRW bean) {
		this.bean = bean;
	}

	public String getInAreaIst() {
		return inAreaIst;
	}

	public void setInAreaIst(String inAreaIst) {
		this.inAreaIst = inAreaIst;
	}

	public String getInAreaCodFunz() {
		return inAreaCodFunz;
	}

	public void setInAreaCodFunz(String inAreaCodFunz) {
		this.inAreaCodFunz = inAreaCodFunz;
	}

	public String getInAreaCodG() {
		return inAreaCodG;
	}

	public void setInAreaCodG(String inAreaCodG) {
		this.inAreaCodG = inAreaCodG;
	}

	public String getInAreaCodConv() {
		return inAreaCodConv;
	}

	public void setInAreaCodConv(String inAreaCodConv) {
		this.inAreaCodConv = inAreaCodConv;
	}

	public String getInAreaNContrat() {
		return inAreaNContrat;
	}

	public void setInAreaNContrat(String inAreaNContrat) {
		this.inAreaNContrat = inAreaNContrat;
	}

	public String getInAreaNumRelaz() {
		return inAreaNumRelaz;
	}

	public void setInAreaNumRelaz(String inAreaNumRelaz) {
		this.inAreaNumRelaz = inAreaNumRelaz;
	}

	public String getInAreaCf() {
		return inAreaCf;
	}

	public void setInAreaCf(String inAreaCf) {
		this.inAreaCf = inAreaCf;
	}

	public String getInAreaCogn() {
		return inAreaCogn;
	}

	public void setInAreaCogn(String inAreaCogn) {
		this.inAreaCogn = inAreaCogn;
	}

	public String getInAreaNome() {
		return inAreaNome;
	}

	public void setInAreaNome(String inAreaNome) {
		this.inAreaNome = inAreaNome;
	}

	public String getInAreaNdg() {
		return inAreaNdg;
	}

	public void setInAreaNdg(String inAreaNdg) {
		this.inAreaNdg = inAreaNdg;
	}

	public String getInAreaDscConv() {
		return inAreaDscConv;
	}

	public void setInAreaDscConv(String inAreaDscConv) {
		this.inAreaDscConv = inAreaDscConv;
	}

	public String getInAreaToken() {
		return inAreaToken;
	}

	public void setInAreaToken(String inAreaToken) {
		this.inAreaToken = inAreaToken;
	}

	public String getInAreaUserId() {
		return inAreaUserId;
	}

	public void setInAreaUserId(String inAreaUserId) {
		this.inAreaUserId = inAreaUserId;
	}

	public String getInAreaDscPrest() {
		return inAreaDscPrest;
	}

	public void setInAreaDscPrest(String inAreaDscPrest) {
		this.inAreaDscPrest = inAreaDscPrest;
	}

	public String getInAreaSportello() {
		return inAreaSportello;
	}

	public void setInAreaSportello(String inAreaSportello) {
		this.inAreaSportello = inAreaSportello;
	}

	public String getInAreaCodOperat() {
		return inAreaCodOperat;
	}

	public void setInAreaCodOperat(String inAreaCodOperat) {
		this.inAreaCodOperat = inAreaCodOperat;
	}

	public String getProgetto() {
		return progetto;
	}

	public void setProgetto(String progetto) {
		this.progetto = progetto;
	}

	public int getCanaleMgen() {
		return canaleMgen;
	}

	public void setCanaleMgen(int canaleMgen) {
		this.canaleMgen = canaleMgen;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public int getTpinst() {
		return tpinst;
	}

	public void setTpinst(int tpinst) {
		this.tpinst = tpinst;
	}

	public String getCanaleAppl() {
		return canaleAppl;
	}

	public void setCanaleAppl(String canaleAppl) {
		this.canaleAppl = canaleAppl;
	}

	public Oper getOper() {
		return oper;
	}

	public void setOper(Oper oper) {
		this.oper = oper;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public InArea getInArea() {
		return inArea;
	}

	public void setInArea(InArea inArea) {
		this.inArea = inArea;
	}

	public java.util.ArrayList getInCLEWCOMU() {
		return inCLEWCOMU;
	}

	public void setInCLEWCOMU(java.util.ArrayList inCLEWCOMU) {
		this.inCLEWCOMU = inCLEWCOMU;
	}

	public OutArea getOutArea() {
		return outArea;
	}

	public void setOutArea(OutArea outArea) {
		this.outArea = outArea;
	}

	public java.util.ArrayList getOutCLEWCOMU() {
		return outCLEWCOMU;
	}

	public void setOutCLEWCOMU(java.util.ArrayList outCLEWCOMU) {
		this.outCLEWCOMU = outCLEWCOMU;
	}

	public String getSessione() {
		return sessione;
	}

	public void setSessione(String sessione) {
		this.sessione = sessione;
	}

	public int getMgenerr() {
		return mgenerr;
	}

	public void setMgenerr(int mgenerr) {
		this.mgenerr = mgenerr;
	}

	public String getMgenerrprg() {
		return mgenerrprg;
	}

	public void setMgenerrprg(String mgenerrprg) {
		this.mgenerrprg = mgenerrprg;
	}

	public String getMgenerrdsdc() {
		return mgenerrdsdc;
	}

	public void setMgenerrdsdc(String mgenerrdsdc) {
		this.mgenerrdsdc = mgenerrdsdc;
	}

	public String getInAreaFlagHub() {
		return inAreaFlagHub;
	}

	public void setInAreaFlagHub(String inAreaFlagHub) {
		this.inAreaFlagHub = inAreaFlagHub;
	}

	public String getInAreaNCOB() {
		return inAreaNCOB;
	}

	public void setInAreaNCOB(String inAreaNCOB) {
		this.inAreaNCOB = inAreaNCOB;
	}

	public String getInAreaNCOS() {
		return inAreaNCOS;
	}

	public void setInAreaNCOS(String inAreaNCOS) {
		this.inAreaNCOS = inAreaNCOS;
	}

	public String getPrint() {
		return print;
	}

	public void setPrint(String print) {
		this.print = print;
	}
}