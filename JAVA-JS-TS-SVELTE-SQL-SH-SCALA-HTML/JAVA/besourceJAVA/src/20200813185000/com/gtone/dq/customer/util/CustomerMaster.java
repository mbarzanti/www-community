package com.gtone.dq.customer.util;

public class CustomerMaster {

	public CustomerMaster(){
		
	}
	public CustomerMaster(String bsmn_rgst_no, String cust_clsf_code,
			String cust_dvcd, String cust_engl_name, String cust_id,
			String cust_name, String cust_rgst_date, String cust_stcd,
			String cust_type_code, String iner_cust_rgyn,
			String last_chmn_mpno, String last_chng_dttm,
			String mcnt_addr_kdcd, String obta_rout_code, String pgm_id,
			String rsdn_rgst_no) {
		super();
		this.bsmn_rgst_no = bsmn_rgst_no;
		this.cust_clsf_code = cust_clsf_code;
		this.cust_dvcd = cust_dvcd;
		this.cust_engl_name = cust_engl_name;
		this.cust_id = cust_id;
		this.cust_name = cust_name;
		this.cust_rgst_date = cust_rgst_date;
		this.cust_stcd = cust_stcd;
		this.cust_type_code = cust_type_code;
		this.iner_cust_rgyn = iner_cust_rgyn;
		this.last_chmn_mpno = last_chmn_mpno;
		this.last_chng_dttm = last_chng_dttm;
		this.mcnt_addr_kdcd = mcnt_addr_kdcd;
		this.obta_rout_code = obta_rout_code;
		this.pgm_id = pgm_id;
		this.rsdn_rgst_no = rsdn_rgst_no;
	}
	private String cust_id;
	private String last_chmn_mpno;
	private String last_chng_dttm;
	private String pgm_id;
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getLast_chmn_mpno() {
		return last_chmn_mpno;
	}
	public void setLast_chmn_mpno(String last_chmn_mpno) {
		this.last_chmn_mpno = last_chmn_mpno;
	}
	public String getLast_chng_dttm() {
		return last_chng_dttm;
	}
	public void setLast_chng_dttm(String last_chng_dttm) {
		this.last_chng_dttm = last_chng_dttm;
	}
	public String getPgm_id() {
		return pgm_id;
	}
	public void setPgm_id(String pgm_id) {
		this.pgm_id = pgm_id;
	}
	public String getCust_dvcd() {
		return cust_dvcd;
	}
	public void setCust_dvcd(String cust_dvcd) {
		this.cust_dvcd = cust_dvcd;
	}
	public String getCust_clsf_code() {
		return cust_clsf_code;
	}
	public void setCust_clsf_code(String cust_clsf_code) {
		this.cust_clsf_code = cust_clsf_code;
	}
	public String getCust_type_code() {
		return cust_type_code;
	}
	public void setCust_type_code(String cust_type_code) {
		this.cust_type_code = cust_type_code;
	}
	public String getRsdn_rgst_no() {
		return rsdn_rgst_no;
	}
	public void setRsdn_rgst_no(String rsdn_rgst_no) {
		this.rsdn_rgst_no = rsdn_rgst_no;
	}
	public String getBsmn_rgst_no() {
		return bsmn_rgst_no;
	}
	public void setBsmn_rgst_no(String bsmn_rgst_no) {
		this.bsmn_rgst_no = bsmn_rgst_no;
	}
	public String getCust_stcd() {
		return cust_stcd;
	}
	public void setCust_stcd(String cust_stcd) {
		this.cust_stcd = cust_stcd;
	}
	public String getCust_rgst_date() {
		return cust_rgst_date;
	}
	public void setCust_rgst_date(String cust_rgst_date) {
		this.cust_rgst_date = cust_rgst_date;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getCust_engl_name() {
		return cust_engl_name;
	}
	public void setCust_engl_name(String cust_engl_name) {
		this.cust_engl_name = cust_engl_name;
	}
	public String getMcnt_addr_kdcd() {
		return mcnt_addr_kdcd;
	}
	public void setMcnt_addr_kdcd(String mcnt_addr_kdcd) {
		this.mcnt_addr_kdcd = mcnt_addr_kdcd;
	}
	public String getObta_rout_code() {
		return obta_rout_code;
	}
	public void setObta_rout_code(String obta_rout_code) {
		this.obta_rout_code = obta_rout_code;
	}
	public String getIner_cust_rgyn() {
		return iner_cust_rgyn;
	}
	public void setIner_cust_rgyn(String iner_cust_rgyn) {
		this.iner_cust_rgyn = iner_cust_rgyn;
	}
	private String cust_dvcd;
	private String cust_clsf_code;
	private String cust_type_code;
	private String rsdn_rgst_no;
	private String bsmn_rgst_no;
	private String cust_stcd;
	private String cust_rgst_date;
	private String cust_name;
	private String cust_engl_name;
	private String mcnt_addr_kdcd;
	private String obta_rout_code;
	private String iner_cust_rgyn;
	
	public String toString(){
		StringBuffer ret=new StringBuffer();
		
		ret.append(" CUST_ID=["+getCust_id()+"] ");
		ret.append(" LAST_CHMN_MPNO=["+getLast_chmn_mpno()+"] ");
		ret.append(" LAST_CHNG_DTTM=["+getLast_chng_dttm()+"] ");
		ret.append(" PGM_ID=["+getPgm_id()+"] ");
 		return ret.toString();
	}
}
