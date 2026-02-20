<script language="javascript">

	function TornaIndietro(){
	   // serve per tornare alla mappa generale di ricerca
	   location.href="Teleport?funzione=FVSYY";
	}


	var pressed = false;	
	function submitForm() {
		var messaggio="Inserire una password";
		if(document.formOTP.Password.value.trim() != "")
			 {
				if (document.formOTP.Password.value.length!=8){	
						messaggio="Inserire una password corretta.";
						openPopUp(messaggio,4,'E','Password'); 
						document.formOTP.Password.focus();
						return false;
				}
		} else {
				openPopUp(messaggio,4,'E','Password'); 
				document.formOTP.Password.focus();
				return false;		
		}
	
		if (!pressed) {
			pressed = true;
			document.formOTP.submit();	
			visualizzaClessidra();
		}						
	}

</script>

<body>

<form name="formOTP" id="form" method="post" action="<%=session.getAttribute("codFunzione")%>">
<input type="hidden" name="otp" id="otp" value="OTP">
<div id="container" class="divContainer" >
	<div id="navigation-bar" >
		<label class="textLblNav">Post Vendita >  </label>
		<label class="textLblNavIn">Richiesta <%=session.getAttribute("funzione")%></label>
	</div>
		<div id="header">
			<div class="header-title">
						<div>
							<p>Dati Principali</p>
						</div>
			</div>
			<div class="divSectionBody">
				<jsp:include page="TestataDatiPolizza.jsp" flush="true"/>
			</div>
		<div class="divSectionBody" >
			<TABLE border="0" width="100%">
					<TR height="60px">
						<TD colspan="2">
							<label class="textLblBlu">Sblocco operazione</label>
						</TD>
					</TR>
					<TR height="60px">
						<TD colspan="2">
							<label class="textLblNavIn">Inserire la password</label>
							<INPUT id="Password" 
									name="Password"
									value=""
									maxlength=8
									tabindex="2"
									type="password" />
						</TD>
					</TR>
		 		</TABLE>
		</div><!-- divSectionBody -->
	</div><!-- header -->
 </div><!-- container  -->
	<div id="footer" class="divfooter">

				<div style="text-align: right; float: right;">
					<input id="Invia" 
						name="Invia"  
						type="button" 
						onclick="submitForm()"
						class="buttonNavProsegui block-ui" 
						value="Avanti" 
						tabindex="503"
					 />
				</div>
		</div>
			<jsp:include page="layout/ErrorWarningPopup.jsp" />
		</form>
</body>
