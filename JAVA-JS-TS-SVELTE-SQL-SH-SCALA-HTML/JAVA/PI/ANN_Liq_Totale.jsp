<%@page import="com.infogroup.poste.bean.*"%>
<%@page import="com.infogroup.ebaasposte.pv.connector.frv2v.*"%>
<%@page import="com.infogroup.poste.common.*"%>
<%@page import="java.util.*"%>

<%!
 private String verifyNull(String value){
 	return (value==null)?"":value;
 }
 
%>
  
<%
if (session.getAttribute("AnnulloBean") != null) {
		 FRV2VBean bean = (FRV2VBean) session.getAttribute("AnnulloBean");
	  	 String appendice = bean.inArea.flg_appendice;
	  	 String dfisc = bean.inArea.flg_dfisc;
%>
<script language="javascript" src="jsp/controlli_recapito.js"></script> 
<script language="javascript">
nomeform="frmCambioContraenza";



function TornaIndietro(){
   location.href="Teleport?funzione=FCHST_ST002";
	   
}

var pressed = false;	
function submitForm(ritornoPopup) {
	
            pressed = true;
            document.frmRiscattoTotale.submit();
            visualizzaClessidra();
        
	}



function openStampa(){

 	str= "STAMPAVAR?n=<%=System.currentTimeMillis()%>";
	mywin=window.open(str, 	'Stampe', 'toolbar=yes, directories=no, location=no, status=no, menubar=no,resizable=yes, scrollbars=yes, width=500, height=400'); 
	//Nuova chiamata della window.open con il secondo parametro Stampe che avrà concatenato un numero di modo che è
	//sempre diverso e così dovrebbe aprirsi sempre una nuova finestra per ogni stampa
	//mywin=window.open(str, 	'Stampe_<%=System.currentTimeMillis()%>', 'toolbar=yes, directories=no, location=no, status=no, menubar=no,resizable=yes, scrollbars=yes, width=500, height=400'); 
	mywin.focus();
}
repositionButtonBar();
</script>

<form name="frmRiscattoTotale" id="form" method="post" action="ANNULLO_OPERAZIONE">
<input type="hidden" name="esegui" id="esegui" value="VARIA">
<input type="hidden" name="ndg" id="ndg" value="<%=verifyNull(bean.getOutArea().ndg)%>">
<input type="hidden" name="n_sog" id="n_sog" value="<%=verifyNull(bean.getOutArea().n_sog)%>">
<input type="hidden" name="flg_dfisc" id="flg_dfisc" value="<%=verifyNull(bean.getOutArea().flg_dfisc)%>">
<input type="hidden" name="codoperat" id="codoperat" value="<%=verifyNull(bean.getOutArea().codoperat)%>">
<input type="hidden" name="imp_pre_lor_gar" id="imp_pre_lor_gar" value="<%=verifyNull(bean.getOutArea().imp_pre_lor_gar)%>">
<input type="hidden" name="flg_motivo" id="flg_motivo" value="0">
<input type="hidden" name="flgammesso" id="flgammesso" value="<%=verifyNull(bean.getOutArea().flg_ammesso)%>">
<input type="hidden" name="LstLiquidazPolizza" id="LstLiquidazPolizza" value="S">
<input type="hidden" name="provenienza" id="provenienza" value="<%=request.getAttribute("provenienza")%>">

<div id="container" class="divContainer">
        <div id="navigation-bar">
            <label class="textLblNav">Post Vendita ></label>
            <label class="textLblNavIn">Annullo Richiesta Liquidazione Per Riscatto Totale</label>
        </div>
        <div id="header">
            <div class="header-title">
                <div>
                    <p>Dati Contraente</p>
                </div>
            </div>

            <div class="divSectionBody">
                <jsp:include page="TestataDatiPolizza.jsp" flush="true"/>
            </div>

        </div>

        <div id="main-content">
        
        	<div class="divSection">
                <table>
                    <tr>
                        <td class="section_col_1">
                            Dati Principali
                        </td>
                    </tr>
                </table>
            </div>

            <div class="divSectionBody" id="datiPrincipali">
                <table class="tableMaschereForm">
                	<tr>
						<td>
							<label class="textLbl">Data Decorrenza Polizza</label>
							<input name="TxtDataDecorrenza" id="TxtDataDecorrenza" class="textControlDisable" value="<%=EBAASUtility.formattaData(verifyNull(bean.getOutArea().dat_decorr))%>" disabled>
						</td>
						<td>
							<label class="textLbl">Totale Premi Pagati</label>
							<input name="TxtTotalePremiPagati" id="TxtTotalePremiPagati" class="textControlDisable" value="<%=(bean.getOutArea().imp_pre_lor_gar != null)?EBAASUtility.toFormattedNumber(verifyNull(bean.getOutArea().imp_pre_lor_gar)):""%>" disabled>						
						</td>
					</tr>
                </table>
        	</div>
        	<div id="datiModPagamento">
				<jsp:include page="ANN_ModalitaPagamento.jsp" flush="true">
					<jsp:param name="beanName" value="AnnulloBean" />
				</jsp:include>
			</div>
			
			<div class="divSection">
                <table>
                    <tr>
                        <td class="section_col_1">
                            Dati Detrazione
                        </td>
                    </tr>
                </table>
            </div>
            
            <div class="divSectionBody" id="datiDetrazione">
                <table class="tableMaschereForm">
                	<TR>
						<TD>
							<label class="textLbl">E' stato usufruito della detraibilità fiscale?</label>
							<select disabled name="LstDetr" id="LstDetr" class="textControl" tabindex="60" >
							<% if ("S".equals(dfisc)) { %> 
									<option value="S">SI</option>
							<%} else {%>
									<option value="N">NO</option>
							<%}%>				
							</select>
						</TD>
					</TR>
                </table>
            </div>
            <% if ("S".equals(bean.getOutArea().flg_dfisc)) { %> 
			<div id='datiDetraibilita' style="display:none">
  			<TABLE border=0 width="100%">
  				<TR>
  					<TD class="text" colspan="3">
						DETRAIBILITA FISCALE
					</TD>
				</TR>
				<TR>
					<TD class="text" width="20%" align="right">Anno</TD>
					<TD class="text" width="40%" align="right">IMPORTO VERSATO</TD>
					<TD class="text" width="40%" align="right">IMPORTO PREMIO DEDOTTO</TD>
				</TR>
				<% if (bean.getOutCLEBDETR() != null) { 
					Iterator iter = bean.getOutCLEBDETR().iterator();
					int tabIndex = 60;
					while (iter.hasNext()) {
						tabIndex++;
						Detrazione d = (Detrazione) iter.next();
				%>
				<TR>
					<TD><input name="TxtAnno" style="HEIGHT: 18px; WIDTH: 100%; TEXT-ALIGN:right;" value="<%=d.anno%>" disabled></TD>
					<TD><input name="TxtImportoVersato" style="HEIGHT: 18px; WIDTH: 100%; TEXT-ALIGN:right;" value="<%=(d.imp_pre_lor_gar != null)?EBAASUtility.toFormattedNumber(verifyNull(d.imp_pre_lor_gar)):""%>" disabled></TD>
					<TD>
						<input tabindex="<%=tabIndex%>" name="TxtImportoDedotto<%=d.anno%>" style="HEIGHT: 18px; WIDTH: 100%; TEXT-ALIGN:right;" value="<%=(d.imp_dedotto!=null)?EBAASUtility.toFormattedNumber(d.imp_dedotto):"0,00"%>">
					</TD>
				</TR>
				<%	} //while
				}// if %>
			</TABLE>	
			</div>
	<%}%>
        	
        	<div class="divSection">
                <table>
                    <tr>
                        <td class="section_col_1">
                            Dati Appendici
                        </td>
                    </tr>
                </table>
            </div>
            
            <div class="divSectionBody" id="datiAppendici">
                <table class="tableMaschereForm">
                	<TR>
						<TD>
							<label class="textLbl">APPENDICI POLIZZA</label>		
							<select disabled name="LstAppendiciPolizza" id="LstAppendiciPolizza" class="textControl" tabindex="70">
								<% if ("S".equals(bean.getOutArea().flg_appendice)) { %> 
									<option value="S">SI</option>
								<% }else{ %>
									<option value="N">NO</option>
								<% } %>
							</select>
						</TD>
					</TR>
                </table>
            </div>
            
            <%
	if (bean.getOutCLEBCOSC() != null) {
	%>
	
			<div class="divSection">
                <table>
                    <tr>
                        <td class="section_col_1">
                            Lista Consulenze
                        </td>
                    </tr>
                </table>
            </div>
            
            <div class="divSectionBody">
            	<%
					boolean listaConsulenza = false; 
					if (bean.getOutCLEBCOSC() != null) {
						Iterator iter1 = bean.getOutCLEBCOSC().iterator();
						while (iter1.hasNext()) {
							ConsulenzaCOS cos = (ConsulenzaCOS)iter1.next();
						    if (!"X".equals(cos.ssel) || !"X".equals(cos.sqta) || "1".equals(cos.cope)) {
						    	continue;
						    }
						    listaConsulenza = true;
						}
					}
					if (listaConsulenza) { 
								//int[] posx = {0, 15, 30, 60, 78, 88, 98, 100};
								int[] posx = {0, 14, 24, 52, 67, 76, 90};
								com.infogroup.poste.common.OptionLabelCreator olc = new com.infogroup.poste.common.OptionLabelCreator(posx);
								String[] values1 = {"", 			"Cod.", 	"Descr.", 		"", 			"Operaz.", "Tipo"};
								String[] values = {"N.Operazione", "Strumento", "Strumento", "  Controvalore", "Eseguita", "Operaz."};
							%>
							<table width="100%" class="datatablePV">
			                    <thead class="paddingTB">
				                    <tr>
				                        <th class="col1"></th>
				                        <th class="colGen8Cols">N.Operazione</th>
				                        <th class="colGen8Cols">Cod. Strumento</th>
				                        <th class="colGen8Cols">Descr. Strumento</th>
				                        <th class="colGen8Cols">Controvalore</th>
				                        <th class="colGen8Cols">Operaz. Eseguita</th>
				                        <th class="colGen8Cols">Tipo Operaz.</th>
				                    </tr>
			                    </thead>
			                </table>
			                <div class="tableList">
			                
			                <table id="TabPolizze" name="TabPolizze" width="100%">
			                <%		
									String datiPolizza = "";
										
									Iterator iter = bean.getOutCLEBCOSC().iterator();
									int pos = 0;
									while (iter.hasNext()) {
										ConsulenzaCOS cos = (ConsulenzaCOS)iter.next();
										String selected = "";
										
									  	values[0] = verifyNull(cos.ncos);
									  	values[1] = verifyNull(cos.cstrfin);
									  	values[2] = verifyNull(cos.xstrfin);
									  	values[3] = EBAASUtility.formatDouble(Double.parseDouble(cos.ictv.substring(0,15) + "." + cos.ictv.substring(15)));
									  	values[3] = EBAASUtility.addBlacks(14, values[3]);
									  	if (cos.sqta != null && "X".equals(cos.sqta))
									  		values[4] = "NO";
									  	else 
									  		values[4] = "SI";
									  	
									  	if (cos.cope != null && "1".equals(cos.cope))
									  		values[5] = "ACQUISTO";
									  	else 
									  		values[5] = "VENDITA";
									  	String selezionabile = "NO";
									  	if (cos.ssel != null && "X".equals(cos.ssel))
									  		selezionabile = "SI";
									    if (("NO" == selezionabile) || ("SI" == values[4]) || ("ACQUISTO" == values[5])) {
									    	continue;
									    }
										
										datiPolizza = pos + "@" + verifyNull(cos.ncos); 
							%>
										
										<tr>
				                            <td class="col1">
				                                <input type="checkbox" name="LstPolizzeReimpiego" id="<%=datiPolizza%>" value="<%=datiPolizza%>"/>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[0]%>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[1]%>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[2]%>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[3]%>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[4]%>
				                            </td>
				                            <td class="colGen8Cols">
				                                <%=values[5]%>
				                            </td>
				                        </tr>					
							<%			pos++;
							
									}  // while  %>
							<%	 
							}%>
			                
			                </table>
			                </div>
            	
            </div>
            
            <%}%>
        </div>
</div>

	<div id="footer" class="divfooter">
        <div style="float: left">
            <INPUT TYPE="button" VALUE="Indietro" align="left" name="Indietro" tabindex=101 ONCLICK="TornaIndietro()" class="buttonNavIndietro">
        </div>

        <div style="text-align: right; float: right;">
            <INPUT TYPE="button" VALUE="Avanti" align="right" name="Avanti" tabindex="100" class="buttonNavProseguiAnnullo block-ui" onclick="submitForm()">
        </div>
    </div>

    <!-- NEW STOP -->
    <jsp:include page="layout/ErrorWarningPopup.jsp" />
</form>
<%
	}
	else {
		throw new it.eng.poste.ebaas.exceptions.InvalidSessionException("Sessione non valida");
	}
%>
