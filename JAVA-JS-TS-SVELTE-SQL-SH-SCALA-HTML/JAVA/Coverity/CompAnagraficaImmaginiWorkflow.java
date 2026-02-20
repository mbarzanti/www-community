package it.poste.gotoapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import it.gform.client.GFormLog;
import it.gform.client.GFormUtility;
import it.gform.client.annotation.GFormTemplate;
import it.gform.client.annotation.IsGWidget;
import it.gform.client.dto.FieldMapDTO;
import it.gform.client.dto.QueryDTO;
import it.gform.client.dto.ResponseDTO;
import it.gform.client.dto.ResponseResultSetDTO;
import it.gform.client.dto.RowDTO;
import it.gform.client.form.DialogFactory;
import it.gform.client.form.GButton;
import it.gform.client.form.GDataTable;
import it.gform.client.form.GDataTable.RowRenderListener;
import it.gform.client.form.GDialogWidget;
import it.gform.client.form.GEvent;
import it.gform.client.form.GForm;
import it.gform.client.form.GHPanel;
import it.gform.client.form.GHtml;
import it.gform.client.form.GVPanel;
import it.gform.client.form.LoadingIndicator;
import it.gform.client.parser.RenderModeEnum;
import it.gform.client.service.GFormAsyncSuccessCallback;

@GFormTemplate(filename = "CompAnagraficaImmaginiWorkflow.gform.xml", renderMode = RenderModeEnum.onDemand)
public class CompAnagraficaImmaginiWorkflow extends GForm implements FormRefreshable {
	final FormPanel formPanel = new FormPanel();
	final FileUpload fileUpload = new FileUpload();	
	@IsGWidget 
	GButton btnEsegui;
	@IsGWidget 
	GVPanel panelUpload;	
	@IsGWidget 
	GVPanel panelGrid;
	@IsGWidget
	GDataTable dtImmagini;
	@IsGWidget
	GDialogWidget dlgImmagini,dlgWorkFlow;
	@IsGWidget
	GHtml lblWorkFlowImmagine;

	
	
	
	private String idImmagine;
	
	final ListBox fuStato=new ListBox();
	final ListBox fuStatoOTE=new ListBox();
	final ListBox fuTipoIniziativa=new ListBox();
	
	/***
	 * Gestore degli eventi della form
	 */
	public void onEvent(GEvent event) {
		GFormLog.writeEvent(event);
		if (event.isRenderComplete()) {
//			componiElenco();
			dtImmagini.setRowRenderListener(new RowRenderListener() {
				public void afterRowRender(RowDTO row, final int currentRowIndex,final FlexTable flexTable) {
					final String idImmagine=row.get(4)+"";
					GHtml export = new GHtml();
					export.setHTML("Visualizza");
					export.setTitle("Visualizza");	
					export.addClickHandler(new ClickHandler() {							
						public void onClick(ClickEvent event) {
							final FieldMapDTO fieldMapDTO = new FieldMapDTO();
							fieldMapDTO.addField("fldNomeImmagine", idImmagine);	
							serviceCaller.executeAction("VisualizzaAnagraficaImmagineWorkflow", fieldMapDTO, new GFormAsyncSuccessCallback<ResponseDTO>() {
								protected void onSuccessInternal(ResponseDTO responseDTO) {
									if (!responseDTO.hasError() && !responseDTO.hasWarning()) {
										GFormUtility.openDocumentNative(GWT.getModuleBaseURL() + "DownloadFile?filename=" + URL.encodeQueryString(responseDTO.getParam("filename")));
									} else if (!responseDTO.hasError()) {
										DialogFactory.dialogAlert(responseDTO.getWarningList().get(0));
									}				
								}
							});
						}
					});					
					flexTable.setWidget(currentRowIndex, 4, export);	
				}
			});
			creaFileUpload();		
		} 
		else if (event.isClickOn("btnEsegui")) {
			validateForm();
		} 
		else if (event.isValidateSuccess()) {
			LoadingIndicator.show();
			formPanel.submit();					
		}
		else if (event.isClickOn("btnBack")) {
			HomePage.getNavBar().gotoBack();
		}
		else if (event.isClickOn("btnChiudiImmagine")) {
			dlgImmagini.hide();	
		} else if (event.isClickOn("btnSalvaImmagine")) {
			salvaImmagineIniziativa();
		}else if (event.isClickOn("dtImmagini")) {
			idImmagine = event.getArgs()[0];
		} else if (event.isClickOn("actbarImmagini")) {
			if (event.isArgEquals(0, "actAdd")){
				formPanel.reset();
				dlgImmagini.showAtCenter();
			} else if (event.isArgEquals(0, "actDel")){
				if (idImmagine!=null){
					String msg = "Sicuro di voler eliminare questa Immagine? ";
					DialogFactory.dialogYesOrNo2(msg, new Command() {
						public void execute() {
							FieldMapDTO fieldMapDTO=new FieldMapDTO();
							fieldMapDTO.addField("idImmagine", idImmagine);
							serviceCaller.executeAction("EliminaImmagineAnagraficaWorkflow", fieldMapDTO, new GFormAsyncSuccessCallback<ResponseDTO>() {
								protected void onSuccessInternal(ResponseDTO responseDTO) {
									if (!responseDTO.hasError() && !responseDTO.hasWarning()) {
										idImmagine=null;			
										dtImmagini.refreshData();
									} else if (responseDTO.hasWarning()) {
										DialogFactory.dialogAlert(responseDTO.getWarningList().get(0));
									}				
								}
							});
							
						}
					}, null);								
				} else {
					DialogFactory.dialogAlert("Selezionare il Documento che si desidera eliminare!");
				}
			} 
		} 
	}
	
	
	
	private void creaFileUpload() {
		GVPanel panel=new GVPanel();
		panel.setWidth("450px");
		panel.setSpacing(5);
		
		GHPanel riga1=new GHPanel();
		final Label lblStato=new Label();
		lblStato.setText("Stato");
		lblStato.setWidth("150px");
		lblStato.setStylePrimaryName("GForm-FieldLabel");
		lblStato.setHorizontalAlignment(ALIGN_RIGHT);
		riga1.add(lblStato);
		fuStato.clear();
		fuStato.setName("fuTipoStato");
		
		
		QueryDTO queryDTO=new QueryDTO("QryStatoIniziativaImmaginiWorkflow");
		serviceCaller.executeQuery(queryDTO, new GFormAsyncSuccessCallback<ResponseResultSetDTO>() {
			protected void onSuccessInternal(ResponseResultSetDTO responseDTO) {
				for (final RowDTO row : responseDTO.getRows()) {	
					fuStato.addItem(row.get(1), row.get(0));
				}
			}
		});
		riga1.add(fuStato);
		
		panel.add(riga1);
		
		GHPanel riga2=new GHPanel();
		final Label lblStatoOTE=new Label();
		lblStatoOTE.setText("Stato OTE");
		lblStatoOTE.setWidth("150px");
		lblStatoOTE.setStylePrimaryName("GForm-FieldLabel");
		lblStatoOTE.setHorizontalAlignment(ALIGN_RIGHT);
		riga2.add(lblStatoOTE);
		fuStatoOTE.clear();
		fuStatoOTE.setName("fuTipoStatoOTE");
		fuStatoOTE.addItem("Nessuno", "0");
		
		QueryDTO queryDTOOTE=new QueryDTO("QryStatoOTEIniziativaImmaginiWorkflow");
		serviceCaller.executeQuery(queryDTOOTE, new GFormAsyncSuccessCallback<ResponseResultSetDTO>() {
			protected void onSuccessInternal(ResponseResultSetDTO responseDTO) {
				for (final RowDTO row : responseDTO.getRows()) {	
					fuStatoOTE.addItem(row.get(1), row.get(0));
				}
			}
		});
		
		riga2.add(fuStatoOTE);
		
		panel.add(riga2);
		
		GHPanel riga3=new GHPanel();
		final Label lblTipo=new Label();
		lblTipo.setText("Tipo");
		lblTipo.setWidth("150px");
		lblTipo.setStylePrimaryName("GForm-FieldLabel");
		lblTipo.setHorizontalAlignment(ALIGN_RIGHT);
		riga3.add(lblTipo);
		fuTipoIniziativa.clear();
		fuTipoIniziativa.addItem("Non Valorizzato","0");
		fuTipoIniziativa.setName("fuTipoIniziativa");
		
		
		
		riga3.add(fuTipoIniziativa);
		queryDTO=new QueryDTO("QryTipoIniziativaImmaginiWorkflow");
		serviceCaller.executeQuery(queryDTO, new GFormAsyncSuccessCallback<ResponseResultSetDTO>() {
			protected void onSuccessInternal(ResponseResultSetDTO responseDTO) {
				for (final RowDTO row : responseDTO.getRows()) {	
					fuTipoIniziativa.addItem(row.get(1), row.get(0));
				}
			}
		});
		panel.add(riga3);
		
		GHPanel riga4=new GHPanel();
		final Label lblDoc=new Label();
		lblDoc.setText("Immagine");
		lblDoc.setWidth("150px");
		lblDoc.setStylePrimaryName("GForm-FieldLabel");
		lblDoc.setHorizontalAlignment(ALIGN_RIGHT);
		riga4.add(lblDoc);
		fileUpload.setName("theFile");
		fileUpload.setWidth("300px");
		fileUpload.addStyleName("GForm-FileUpload-Field");
		riga4.add(fileUpload);
		panel.add(riga4);
		
		formPanel.setWidget(panel);
		panelUpload.add(formPanel);
		
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String jsonResponse = event.getResults();
				if (jsonResponse.isEmpty() || jsonResponse.equals("<pre></pre>")) {
					dlgImmagini.hide();
					dtImmagini.refreshData();
				} else if (jsonResponse.contains("INFO|")){
					DialogFactory.dialogAlert(jsonResponse.substring(jsonResponse.indexOf("INFO|")+5));
				} else {
					jsonResponse=jsonResponse.replace("<pre>", "");
					jsonResponse=jsonResponse.replace("</pre>", "");
					DialogFactory.dialogAlert(jsonResponse);
				}
			}
		});	
	}		
			
	private void validateForm() {
		boolean chk=true;
		if(null==fileUpload.getFilename() || fileUpload.getFilename().isEmpty()){
			chk=false;
			DialogFactory.dialogAlert("Selezionare il file che si desidera importare!");			
		}
		
		if(chk)
			validateAllField();
	}

	@Override
	public void refresh() {
	}
	
	
	private void salvaImmagineIniziativa(){
		String url="SalvaImmagineWorkflow";
		formPanel.setAction(GWT.getModuleBaseURL() + url);
		formPanel.submit();	
				
	}
	
}
