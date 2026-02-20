package com.gtone.dq.customer.util;

public class CustomerDetail {
	private String cust_id			     ;
	private String last_chmn_mpno		;
	private String last_chng_dttm	 ;
	private String pgm_id	  		   ;
	private String sex_dvcd	       ;
	private String lusl_dvcd	       ;
	private String blty_code	       ;
	private String occp_code	       ;
	private String dwel_type_code		;
	private String hous_kdcd	     	 ;
	private String risk_grad_code   ;
	private String car_pose_yn		   ;
	private String car_kind_code	   ;
	private String drve_code	 		   ;
	private String grdu_area_code   ;
	private String reli_code			   ;
	private String hoby_code			   ;
	private String spsk_code			   ;
	private String edbg_code			   ;
	private String natn_code			   ;
	private String brth			       ;
	private String wedd_yn		       ;
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
	public CustomerDetail(){
		
	}
	public CustomerDetail(String blty_code, String brth, String car_kind_code,
			String car_pose_yn, String cust_grad_code, String cust_id,
			String dept_name, String dm_reyn, String drve_code,
			String dwel_type_code, String edbg_code, String emal_1,
			String emal_1_reyn, String emal_2, String emal_2_reyn,
			String entr_ym, String fax_no_brh, String fax_no_ddd,
			String fax_no_dtal, String frnr_rgst_no, String grdu_area_code,
			String hoby_code, String hous_kdcd, String hp_brh, String hp_bsmn,
			String hp_dtal, String hp_recv_yn, String job_pstn_name,
			String last_chmn_mpno, String last_chng_dttm, String lusl_dvcd,
			String mate_yn, String natn_code, String ntry_date,
			String occp_code, String pgm_id, String pspt_no, String reli_code,
			String risk_grad_code, String schl_name, String sex_dvcd,
			String spsk_code, String spsk_item, String visa_end_date,
			String wedd_memr_date, String wedd_yn, String wkpl_name) {
		super();
		this.blty_code = blty_code;
		this.brth = brth;
		this.car_kind_code = car_kind_code;
		this.car_pose_yn = car_pose_yn;
		this.cust_grad_code = cust_grad_code;
		this.cust_id = cust_id;
		this.dept_name = dept_name;
		this.dm_reyn = dm_reyn;
		this.drve_code = drve_code;
		this.dwel_type_code = dwel_type_code;
		this.edbg_code = edbg_code;
		this.emal_1 = emal_1;
		this.emal_1_reyn = emal_1_reyn;
		this.emal_2 = emal_2;
		this.emal_2_reyn = emal_2_reyn;
		this.entr_ym = entr_ym;
		this.fax_no_brh = fax_no_brh;
		this.fax_no_ddd = fax_no_ddd;
		this.fax_no_dtal = fax_no_dtal;
		this.frnr_rgst_no = frnr_rgst_no;
		this.grdu_area_code = grdu_area_code;
		this.hoby_code = hoby_code;
		this.hous_kdcd = hous_kdcd;
		this.hp_brh = hp_brh;
		this.hp_bsmn = hp_bsmn;
		this.hp_dtal = hp_dtal;
		this.hp_recv_yn = hp_recv_yn;
		this.job_pstn_name = job_pstn_name;
		this.last_chmn_mpno = last_chmn_mpno;
		this.last_chng_dttm = last_chng_dttm;
		this.lusl_dvcd = lusl_dvcd;
		this.mate_yn = mate_yn;
		this.natn_code = natn_code;
		this.ntry_date = ntry_date;
		this.occp_code = occp_code;
		this.pgm_id = pgm_id;
		this.pspt_no = pspt_no;
		this.reli_code = reli_code;
		this.risk_grad_code = risk_grad_code;
		this.schl_name = schl_name;
		this.sex_dvcd = sex_dvcd;
		this.spsk_code = spsk_code;
		this.spsk_item = spsk_item;
		this.visa_end_date = visa_end_date;
		this.wedd_memr_date = wedd_memr_date;
		this.wedd_yn = wedd_yn;
		this.wkpl_name = wkpl_name;
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
	public String getSex_dvcd() {
		return sex_dvcd;
	}
	public void setSex_dvcd(String sex_dvcd) {
		this.sex_dvcd = sex_dvcd;
	}
	public String getLusl_dvcd() {
		return lusl_dvcd;
	}
	public void setLusl_dvcd(String lusl_dvcd) {
		this.lusl_dvcd = lusl_dvcd;
	}
	public String getBlty_code() {
		return blty_code;
	}
	public void setBlty_code(String blty_code) {
		this.blty_code = blty_code;
	}
	public String getOccp_code() {
		return occp_code;
	}
	public void setOccp_code(String occp_code) {
		this.occp_code = occp_code;
	}
	public String getDwel_type_code() {
		return dwel_type_code;
	}
	public void setDwel_type_code(String dwel_type_code) {
		this.dwel_type_code = dwel_type_code;
	}
	public String getHous_kdcd() {
		return hous_kdcd;
	}
	public void setHous_kdcd(String hous_kdcd) {
		this.hous_kdcd = hous_kdcd;
	}
	public String getRisk_grad_code() {
		return risk_grad_code;
	}
	public void setRisk_grad_code(String risk_grad_code) {
		this.risk_grad_code = risk_grad_code;
	}
	public String getCar_pose_yn() {
		return car_pose_yn;
	}
	public void setCar_pose_yn(String car_pose_yn) {
		this.car_pose_yn = car_pose_yn;
	}
	public String getCar_kind_code() {
		return car_kind_code;
	}
	public void setCar_kind_code(String car_kind_code) {
		this.car_kind_code = car_kind_code;
	}
	public String getDrve_code() {
		return drve_code;
	}
	public void setDrve_code(String drve_code) {
		this.drve_code = drve_code;
	}
	public String getGrdu_area_code() {
		return grdu_area_code;
	}
	public void setGrdu_area_code(String grdu_area_code) {
		this.grdu_area_code = grdu_area_code;
	}
	public String getReli_code() {
		return reli_code;
	}
	public void setReli_code(String reli_code) {
		this.reli_code = reli_code;
	}
	public String getHoby_code() {
		return hoby_code;
	}
	public void setHoby_code(String hoby_code) {
		this.hoby_code = hoby_code;
	}
	public String getSpsk_code() {
		return spsk_code;
	}
	public void setSpsk_code(String spsk_code) {
		this.spsk_code = spsk_code;
	}
	public String getEdbg_code() {
		return edbg_code;
	}
	public void setEdbg_code(String edbg_code) {
		this.edbg_code = edbg_code;
	}
	public String getNatn_code() {
		return natn_code;
	}
	public void setNatn_code(String natn_code) {
		this.natn_code = natn_code;
	}
	public String getBrth() {
		return brth;
	}
	public void setBrth(String brth) {
		this.brth = brth;
	}
	public String getWedd_yn() {
		return wedd_yn;
	}
	public void setWedd_yn(String wedd_yn) {
		this.wedd_yn = wedd_yn;
	}
	public String getWedd_memr_date() {
		return wedd_memr_date;
	}
	public void setWedd_memr_date(String wedd_memr_date) {
		this.wedd_memr_date = wedd_memr_date;
	}
	public String getMate_yn() {
		return mate_yn;
	}
	public void setMate_yn(String mate_yn) {
		this.mate_yn = mate_yn;
	}
	public String getHp_bsmn() {
		return hp_bsmn;
	}
	public void setHp_bsmn(String hp_bsmn) {
		this.hp_bsmn = hp_bsmn;
	}
	public String getHp_brh() {
		return hp_brh;
	}
	public void setHp_brh(String hp_brh) {
		this.hp_brh = hp_brh;
	}
	public String getHp_dtal() {
		return hp_dtal;
	}
	public void setHp_dtal(String hp_dtal) {
		this.hp_dtal = hp_dtal;
	}
	public String getHp_recv_yn() {
		return hp_recv_yn;
	}
	public void setHp_recv_yn(String hp_recv_yn) {
		this.hp_recv_yn = hp_recv_yn;
	}
	public String getFax_no_ddd() {
		return fax_no_ddd;
	}
	public void setFax_no_ddd(String fax_no_ddd) {
		this.fax_no_ddd = fax_no_ddd;
	}
	public String getFax_no_brh() {
		return fax_no_brh;
	}
	public void setFax_no_brh(String fax_no_brh) {
		this.fax_no_brh = fax_no_brh;
	}
	public String getFax_no_dtal() {
		return fax_no_dtal;
	}
	public void setFax_no_dtal(String fax_no_dtal) {
		this.fax_no_dtal = fax_no_dtal;
	}
	public String getWkpl_name() {
		return wkpl_name;
	}
	public void setWkpl_name(String wkpl_name) {
		this.wkpl_name = wkpl_name;
	}
	public String getEntr_ym() {
		return entr_ym;
	}
	public void setEntr_ym(String entr_ym) {
		this.entr_ym = entr_ym;
	}
	public String getJob_pstn_name() {
		return job_pstn_name;
	}
	public void setJob_pstn_name(String job_pstn_name) {
		this.job_pstn_name = job_pstn_name;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	public String getSchl_name() {
		return schl_name;
	}
	public void setSchl_name(String schl_name) {
		this.schl_name = schl_name;
	}
	public String getPspt_no() {
		return pspt_no;
	}
	public void setPspt_no(String pspt_no) {
		this.pspt_no = pspt_no;
	}
	public String getFrnr_rgst_no() {
		return frnr_rgst_no;
	}
	public void setFrnr_rgst_no(String frnr_rgst_no) {
		this.frnr_rgst_no = frnr_rgst_no;
	}
	public String getNtry_date() {
		return ntry_date;
	}
	public void setNtry_date(String ntry_date) {
		this.ntry_date = ntry_date;
	}
	public String getVisa_end_date() {
		return visa_end_date;
	}
	public void setVisa_end_date(String visa_end_date) {
		this.visa_end_date = visa_end_date;
	}
	public String getSpsk_item() {
		return spsk_item;
	}
	public void setSpsk_item(String spsk_item) {
		this.spsk_item = spsk_item;
	}
	public String getEmal_1() {
		return emal_1;
	}
	public void setEmal_1(String emal_1) {
		this.emal_1 = emal_1;
	}
	public String getEmal_1_reyn() {
		return emal_1_reyn;
	}
	public void setEmal_1_reyn(String emal_1_reyn) {
		this.emal_1_reyn = emal_1_reyn;
	}
	public String getEmal_2() {
		return emal_2;
	}
	public void setEmal_2(String emal_2) {
		this.emal_2 = emal_2;
	}
	public String getEmal_2_reyn() {
		return emal_2_reyn;
	}
	public void setEmal_2_reyn(String emal_2_reyn) {
		this.emal_2_reyn = emal_2_reyn;
	}
	public String getDm_reyn() {
		return dm_reyn;
	}
	public void setDm_reyn(String dm_reyn) {
		this.dm_reyn = dm_reyn;
	}
	public String getCust_grad_code() {
		return cust_grad_code;
	}
	public void setCust_grad_code(String cust_grad_code) {
		this.cust_grad_code = cust_grad_code;
	}
	private String wedd_memr_date   ;
	private String mate_yn	         ;
	private String hp_bsmn	         ;
	private String hp_brh	         ;
	private String hp_dtal	         ;
	private String hp_recv_yn	     ;
	private String fax_no_ddd	     ;
	private String fax_no_brh	     ;
	private String fax_no_dtal	     ;
	private String wkpl_name	       ;
	private String entr_ym	         ;
	private String job_pstn_name	   ;
	private String dept_name	       ;
	private String schl_name	       ;
	private String pspt_no	         ;
	private String frnr_rgst_no	   ;
	private String ntry_date	       ;
	private String visa_end_date	   ;
	private String spsk_item	       ;
	private String emal_1	         ;
	private String emal_1_reyn	     ;
	private String emal_2	         ;
	private String emal_2_reyn	     ;
	private String dm_reyn	         ;
	private String cust_grad_code   ;
	public String toString(){
		StringBuffer ret=new StringBuffer();
		
		ret.append(" CUST_ID=["+getCust_id()+"] ");
		ret.append(" LAST_CHMN_MPNO=["+getLast_chmn_mpno()+"] ");
		ret.append(" LAST_CHNG_DTTM=["+getLast_chng_dttm()+"] ");
		ret.append(" PGM_ID=["+getPgm_id()+"] ");
		ret.append(" SEX_DVCD=["+getSex_dvcd()+"] ");
		ret.append(" LUSL_DVCD=["+getLusl_dvcd()+"] ");
		ret.append(" BLTY_CODE=["+getBlty_code()+"] ");
		ret.append(" OCCP_CODE=["+getOccp_code()+"] ");
 		return ret.toString();
	}
}
