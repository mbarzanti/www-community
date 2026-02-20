import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApplicationStatusType } from '@enums/application-status-type.enum';
import { HttpMethod } from '@enums/http-method.enum';
import { ApprovazioneEnnupleRequest } from '@interfaces/approvazione-ennuple.request';
import { ApprovazioneEnnupleResponse } from '@interfaces/approvazione-ennuple.response';
import { AsincApiResponse } from '@interfaces/asinc/asinc-api-response.interface';
import { ResponseCheckCompensationSupplement } from '@interfaces/check-compensation-supplement.response';
import { compensationCalculationRequest } from '@interfaces/compensationCalculation';
import { DettaglioToolReadRequest, DettaglioToolReadResponse } from '@interfaces/dettaglio-tool-read.interface';
import {
  isEnabledCompensationCalculationRequest,
  isEnabledCompensationCalculationResponse,
} from '@interfaces/isEnabledCompensationCalculation';
import { ProfiloGruppoEmailResponse } from '@interfaces/profilo-gruppo-email.interface';
import { DownloadDelegaPdfResponse } from '@interfaces/prove-di-consegna/download-delega-pdf-response.type';
import { GetDelegaResponse } from '@interfaces/prove-di-consegna/get-delega-response.type';
import { AddressesModel } from '@interfaces/recupera-indirizzo-modificato-input';
import { AddressesResponseWrapper } from '@interfaces/recupera-indirizzo-modificato-output';
import { RequestUpdateAuthorization, ResponseUpdateAuthorization } from '@interfaces/update-authorization.interface';
import { Store } from '@ngrx/store';
import { ApiCacheService } from '@services/api-cache.service';
import { CleanFacadeService } from '@services/clean-facade/clean-facade.service';
import { CookieService } from 'ngx-cookie-service';
import { Observable, of, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { OrderManagementResponse } from 'src/app/components/pages/dashboard/ticket-detail/create-escalation/utils';
import {
  GetPagamentiInboxResponse,
  PagamentiInboxRow,
} from 'src/app/components/pages/inbox/components/inbox-pagamenti/types/get-pagamenti-inbox-reponse.interface';
import { GetPagamentiInboxRequest } from 'src/app/components/pages/inbox/components/inbox-pagamenti/types/get-pagamenti-inbox-request.interface';
import { UpdatePagamTicketResponse } from 'src/app/components/pages/inbox/components/inbox-pagamenti/types/update-pagam-ticket.type';
import { ResponseMailAssistanceBPM } from 'src/app/components/pages/nfea-cj/components/product-assistance/funnel-detail-smart-booklet/utils';
import { SendMassiveDocumentResponse } from 'src/app/components/pages/pix-cj/components/archivio-documentale-massivo/utils';
import { FacadeBiscottieraService } from 'src/app/components/pages/pix-cj/services/facade-biscottiera/facade-biscottiera.service';
import { GetDettaglioFeedbackRequest } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-dettaglio-feedback.request';
import { GetDettaglioFeedbackResponse } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-dettaglio-feedback.response';
import { GetFeedbackTemplateRequest } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-feedback-template.request';
import { GetFeedbackTemplateResponse } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-feedback-template.response';
import { GetListaTemplateRequest } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-lista-template.request';
import { GetListaTemplateResponse } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/get-lista-template.response';
import { HelpSearchTemplateResponse } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/help-search-template.response';
import { UpdateFeedbackResponse } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/update-feedback-response';
import { UpdateFeedbackRequest } from 'src/app/components/pages/settings/cruscotto-ai-write/interfaces/update-feedback.request';
import { ResponseGetCommunicationsByUser } from 'src/app/components/shared/aiknow-header/utils';
import { EnnuplaLevelEnum } from 'src/app/enums/ennupla-level.enum';
import { Pix } from 'src/app/enums/pix-generic.enum';
import { RequestFields } from 'src/app/enums/poste-vita';
import { ResponseGetDatiFiliali } from 'src/app/interface/ResponseGetDatiFiliali';
import { ResponseSearchDocuments } from 'src/app/interface/ResponseSearchDocuments';
import { DocumentInfoById } from 'src/app/interface/adapter-web-download-main-content';
import { RequestAggiornamentoTransazione } from 'src/app/interface/aggiornamento-transazione-request';
import { ResponseAggiornamentoTransazione } from 'src/app/interface/aggiornamento-transazione-response';
import {
  RequestAltreInfoContoCliente,
  ResponseAltreInfoContoCliente,
} from 'src/app/interface/altre-info-conto-cliente';
import { Any2Feu317Request } from 'src/app/interface/any-2-feu-317-request';
import { Any2Feu317Response } from 'src/app/interface/any-2-feu-317-response';
import { ApiUrls } from 'src/app/interface/api-urls';
import {
  GetSubgroupsProductResponse,
  GetSubgroupsRequest,
  LoadSubGroupGETOutputResponse,
  RequestCompaniesDetails,
  RequestGetAppBusinessTerminals,
  RequestMerchantDetails,
  RequestShopAndTerminal,
  ResponseCompaniesDetails,
  ResponseGetAppBusinessTerminals,
  ResponseMerchantDetails,
  ResponseShopAndTerminal,
} from 'src/app/interface/app-business';
import { RequestClientMovements } from 'src/app/interface/balance-list-movements/request-client-movements';
import { ResponseClientMovements } from 'src/app/interface/balance-list-movements/response-client-movements';
import { ResponseMovementsDetail } from 'src/app/interface/balance-list-movements/response-movements-detail';
import { BaseUrl, CjEnnuple, Visibility } from 'src/app/interface/base-url';
import { Bfp } from 'src/app/interface/bfp-list';
import { CalculateRedemptionValueRequest } from 'src/app/interface/calculate-redemption-value-request';
import { CalculateRedemptionValueResponse } from 'src/app/interface/calculate-redemption-value-response';
import { RequestCancelOrderCardSim } from 'src/app/interface/cardReitero/request-cancel-order';
import { RequestCardReitero } from 'src/app/interface/cardReitero/request-card-reitero';
import { RequestVolppReitero } from 'src/app/interface/cardReitero/request-volpp-reitero';
import { ResponseCancelOrderSim } from 'src/app/interface/cardReitero/response-cancel-order';
import { ResponseCardReitero } from 'src/app/interface/cardReitero/response-card-reitero';
import { ResponseVolppReitero } from 'src/app/interface/cardReitero/response-volpp-reitero';
import { ResponseGetSolrPacchiContrassegno } from 'src/app/interface/cashOnDelivery/responseGetSolrPacchiContrassegno.interface';
import { CercaUtenteRequest } from 'src/app/interface/cerca-utente-request';
import { CercaUtenteResponse } from 'src/app/interface/cerca-utente-response';
import { RequestCheckCodOggFatt, ResponseCheckCodOggFatt } from 'src/app/interface/check-cod-ogg-fatt';
import { RequestCheckTicketFigli, ResponseCheckTiketFigli } from 'src/app/interface/check-ticket-figli.interface';
import {
  RequestLeggiLiquidazioni,
  ResponseBeneficiari,
  ResponseLeggiLiquidazioni,
} from 'src/app/interface/closeout-interfaces';
import { ResponseConvertDtsToEuro } from 'src/app/interface/convert-dts-to-euro.interface';
import { CreateDocumentPagerRequest } from 'src/app/interface/create-document-pager-request';
import { CreateDocumentPagerResponse } from 'src/app/interface/create-document-pager-response';
import { RequestCreateTicketFigli, ResponseCreateTicketFigli } from 'src/app/interface/create-ticket-figli.interface';
import { DeliveryProofResponse, GetImageLdvResponse } from 'src/app/interface/delivery-proof-response';
import { DettaglioMailRequest } from 'src/app/interface/dettaglio-mail-request';
import { DettaglioMailResponse } from 'src/app/interface/dettaglio-mail-response';
import { DigitalMembershipStatus } from 'src/app/interface/digital-membership-status';
import { DocumentFromAu } from 'src/app/interface/document-from-au';
import { RequestReprintPin } from 'src/app/interface/duplicatoPin/request-reprint-pin';
import { ResponseReprintPin } from 'src/app/interface/duplicatoPin/response-reprint-pin';
import { RequestElencoMateriali, ResponseElencoMateriali } from 'src/app/interface/elenco-materiali';
import { ElencoServiziBpiolRequest, ElencoServiziBpiolResponse } from 'src/app/interface/elenco-servizi-bpiol';
import { EnableUserAndProspectChangePasswordRequest } from 'src/app/interface/enable-user-and-prospect-change-password-request';
import { EnableUserAndProspectChangePasswordResponse } from 'src/app/interface/enable-user-and-prospect-change-password-response';
import { EnableUserAndSendResetPassordResponse } from 'src/app/interface/enable-user-and-send-reset-password-response';
import { RequestEnableUser } from 'src/app/interface/enableUser';
import { EneableUserAndSendResetPasswordRequest } from 'src/app/interface/eneable-user-and-send-reset-password-request';
import {
  FAQAddTypesResponse,
  FAQSearchRequest,
  FAQSearchResponse,
  FAQTypesRequest,
  FAQTypesResponse,
  FAQUpdateRequest,
  FAQUpdateResponse,
} from 'src/app/interface/faq';
import { RequestFindEnnuple, ResponseFindEnnuple } from 'src/app/interface/find-ennuple';
import { ResponseRecuperaInfoStato } from 'src/app/interface/forms/cj/poste-plus.interface';
import { GenerateDeactivationCodeRequest } from 'src/app/interface/generate-deactivation-code-request';
import { GenerateDeactivationCodeResponse } from 'src/app/interface/generate-deactivation-code-response';
import { GetActorsResponse } from 'src/app/interface/get-actors-response';
import { GetAssetHeaderInteropResponse } from 'src/app/interface/get-asset-header-interop-response';
import { GetAssetsByRoleOrchResponse } from 'src/app/interface/get-assets-by-role-orch-response';
import { RequestCollegamenti, ResponseCollegamenti } from 'src/app/interface/get-collegamenti-soggetto';
import {
  RequestGetComunicazioneSolleciti,
  ResponseGetComunicazioneSolleciti,
} from 'src/app/interface/get-comunicazione-solleciti';
import { GetContrassegnoRequest, GetContrassegnoResponse } from 'src/app/interface/get-contrassegno';
import { GetCookiesResponse } from 'src/app/interface/get-cookies.interface';
import { ResponseGetCountSchCli } from 'src/app/interface/get-count-sch-cli';
import { GetCreditProductSupport } from 'src/app/interface/get-credit-product-support-response';
import { ResponseGetCruscAssDed } from 'src/app/interface/get-crusc-ded-ass-response';
import {
  RequestGetDettStatCausStat,
  ResponseGetDettStatCausStat,
} from 'src/app/interface/get-dett-stat-caus-stat.interface';
import { GetDocumentPaRequest } from 'src/app/interface/get-document-pa-request';
import { GetDocumentPaResponse } from 'src/app/interface/get-document-pa-response';
import { ResponseGetDossierInterop } from 'src/app/interface/get-dossier-interop';
import { GetDossierInteropOrch } from 'src/app/interface/get-dossier-interop-orch-response';
import { GetExcelClientiLargeResponse } from 'src/app/interface/get-excel-clienti-large.response';
import { GetIbanRequest } from 'src/app/interface/get-iban-request';
import { GetIbanResponse } from 'src/app/interface/get-iban-response';
import { GetImageLdvRequest } from 'src/app/interface/get-image-ldv-request';
import { GetKpiRichOfflinRequest } from 'src/app/interface/get-kpi-ric-offlin-request';
import { GetKpiRichOfflinResponse } from 'src/app/interface/get-kpi-ric-offlin-response';
import { GetLinkEvolutoRequest } from 'src/app/interface/get-link-evoluto-request';
import { GetLinkEvolutoResponse } from 'src/app/interface/get-link-evoluto-response';
import { GetListaRapportiFiglioV2Response } from 'src/app/interface/get-lista-rapporti-figlio-v2-response';
import { RequestGetMailCliOpt, ResponseGetMailCliOpt } from 'src/app/interface/get-mail-cli-opt.interface';
import { RequestGetStagFormOl, ResponseGetStagFormOl } from 'src/app/interface/get-stag-form-ol';
import {
  RequestGetTicketInbox,
  RequestGetTicketInboxContatoriSla,
  ResponseGetTicketInbox,
  ResponseGetTicketInboxAsync,
} from 'src/app/interface/get-ticket-inbox';
import { RequestGetTitoli, ResponseGetTitoli } from 'src/app/interface/get-titoli';
import {
  RequestGetValoriCondizioni,
  ResponseGetValoriCondizioni,
} from 'src/app/interface/get-valori-condizioni-interface';
import { RequestGetValoriPerRapporto, ResponseGetValoriPerRapporto } from 'src/app/interface/get-valori-per-rapporto';
import { RequestGetVariantiInbox, ResponseGetVariantiInbox } from 'src/app/interface/get-varianti-inbox';
import { getSessionDecisionRequest } from 'src/app/interface/getSessionDecisionRequest';
import { RequestGiacenze, ResponseGiacenze } from 'src/app/interface/giacenze';
import { InvestmentRequest, InvestmentResponse } from 'src/app/interface/investments';
import { RequestLavorazioneTicket } from 'src/app/interface/lavorazione-ticket.interface';
import { LeadBusinessRequest } from 'src/app/interface/lead-business';
import { LeggiDettaglioSinistroRequest } from 'src/app/interface/leggi-dettaglio-sinistro-request';
import { LeggiDettaglioSinistroResponse } from 'src/app/interface/leggi-dettaglio-sinistro-response';
import { LeggiDiarioSinistroRequest } from 'src/app/interface/leggi-diario-sinistro-request';
import { LeggiDiarioSinistroResponse } from 'src/app/interface/leggi-diario-sinistro-response';
import { LeggiSinistriRequest } from 'src/app/interface/leggi-sinistri-request';
import { LeggiSinistriResponse } from 'src/app/interface/leggi-sinistri-response';
import { ListaPecRequest } from 'src/app/interface/lista-pec-request';
import { ListaPecResponse } from 'src/app/interface/lista-pec-response';
import { RequestListaStepTandem, ResponseListaStepTandem } from 'src/app/interface/lista-step-tandem';
import {
  ListaBlocchiCompleteRequest,
  ListaBlocchiParams,
  ListaBlocchiResponse,
} from 'src/app/interface/listaBlocchi/lista-blocchi.interface';
import { RequestModifyContacts } from 'src/app/interface/modifyContacts';
import { MultipleOrderManagementRequest } from 'src/app/interface/multiple-order-management-request';
import { MultipleOrderManagementResponse } from 'src/app/interface/multiple-order-management-response';
import { NextCallPvPaResponse } from 'src/app/interface/next-call-pv-pa';
import { OfferteStoricoOrchRequest } from 'src/app/interface/offerte-storico-orch-request';
import { OfferteStoricoOrchResponse } from 'src/app/interface/offerte-storico-orch-response';
import { RequestPacchiAnagrafica, ResponsePacchiAnagrafica } from 'src/app/interface/pacchi-anagrafica';
import { RequestPacchiConsumo, ResponsePacchiConsumo } from 'src/app/interface/pacchi-consumo';
import { RequestPackagesCashOnDelivery } from 'src/app/interface/package-cash-on-delivery';
import {
  RequestPickupCustomerSearch,
  RequestPrenotaByLdv,
  RequestVerifyContracts,
  ResponseLDVList,
  ResponsePickupCustomerSearch,
  ResponsePrenotaByLdv,
  ResponseVerifyContracts,
} from 'src/app/interface/parcel-pickup';
import {
  GetExcelPIvaAssistanceResponse,
  PartitaIvaRequest,
  PartitaIvaResponse,
  PIvaAssistanceRequest,
} from 'src/app/interface/piva-assistance-request';
import { RequestPrendiInCarico, ResponsePrendiInCarico } from 'src/app/interface/prendi-in-carico';
import { ReadContractSpidRequest } from 'src/app/interface/read-contract-spid-request';
import { ReadContractSpidResponse } from 'src/app/interface/read-contract-spid-response';
import { RequestReadPVPAPrivacy, ResponseReadPVPAPrivacy } from 'src/app/interface/read-pvpa-privacy';
import { ReadWarrantiesRequest } from 'src/app/interface/read-warranties-request';
import { ReadWarrantiesResponse } from 'src/app/interface/read-warranties-response';
import { RecuperaBonificiRequest } from 'src/app/interface/recupera-bonifici-request';
import { RecuperaBonificiResponse } from 'src/app/interface/recupera-bonifici-response';
import { RecuperaDettaglioLdvResponse } from 'src/app/interface/recupera-dettaglio-ldv.interface';
import { RequestRecuperaIndirizzoModificato } from 'src/app/interface/recupera-indirizzo-modificato.interface';
import { RequestInfoAccountCliente, ResponseInfoAccountCliente } from 'src/app/interface/recupera-info-cliente';
import { RequestRecuperoCredenzialiUtente } from 'src/app/interface/recupero-credenziali-utente-request';
import { ResponseRecuperoCredenzialiUtente } from 'src/app/interface/recupero-credenziali-utente-response';
import {
  RequestRecuperoGruppoAssFromEnnupla,
  ResponseRecuperoGruppoAssFromEnnupla,
} from 'src/app/interface/recupero-gruppo-ass-from-ennupla.interface';
import { RefreshTokenResponse } from 'src/app/interface/refresh-token-response';
import { RequestReportMateriali, ResponseReportMateriali } from 'src/app/interface/report-materiali';
import { RequestReportRitiro, ResponseReportRitiri } from 'src/app/interface/report-ritiro';
import { RequestAcceptanceClientWs } from 'src/app/interface/request-acceptance-client-ws';
import { RequestAccountStatus } from 'src/app/interface/request-account-status';
import { ActivationCard } from 'src/app/interface/request-activation-card';
import { RequestAttribTickInbox } from 'src/app/interface/request-attrib-tick-inbox';
import { RequestBankAccountList } from 'src/app/interface/request-bank-account-list';
import { RequestBfp } from 'src/app/interface/request-bfp';
import { RequestBlockBankAccount } from 'src/app/interface/request-block-bank-account';
import { RequestCallMeBackAdd } from 'src/app/interface/request-call-me-back-add';
import { RequestCallMeBackTimeSlot } from 'src/app/interface/request-call-me-back-time-slot';
import { RequestCancelPractice } from 'src/app/interface/request-cancel-practice';
import { RequestCardBlocking } from 'src/app/interface/request-card-blocking';
import { RequestCardRenewal } from 'src/app/interface/request-card-renewal';
import { RequestCmbResults } from 'src/app/interface/request-cmb-results';
import { RequestConsistency } from 'src/app/interface/request-consistency';
import {
  RequestConsultazioneStatoMorosita,
  RequestStatoMorosita,
} from 'src/app/interface/request-consultazione-stato-morosita';
import { RequestCreateAttachment } from 'src/app/interface/request-create-attachment';
import { RequestCreateCustomer } from 'src/app/interface/request-create-customer';
import { RequestCreateUpdateProfiling } from 'src/app/interface/request-create-update-profiling';
import { RequestCustomerAsset } from 'src/app/interface/request-customer-asset';
import { RequestCustomerChat } from 'src/app/interface/request-customer-chat';
import { RequestCustomerGeopost } from 'src/app/interface/request-customer-geopost';
import { RequestDeleteAllPosteIdCertificate } from 'src/app/interface/request-delete-all-poste-id-certificate';
import { RequestDeletePosteIdCertificate } from 'src/app/interface/request-delete-poste-id-certificate';
import { RequestDeleteTicketAttachment } from 'src/app/interface/request-delete-ticket-attachment';
import { RequestDisableUser } from 'src/app/interface/request-disable-user';
import { RequestDoLdvRelease } from 'src/app/interface/request-do-ldv-release';
import { RequestDomiciliationDetails } from 'src/app/interface/request-domiciliation-details';
import { RequestDomiciliationList } from 'src/app/interface/request-domiciliation-list';
import { RequestDossier, RequestDossierByPod } from 'src/app/interface/request-dossier';
import { RequestDownloadAttachment } from 'src/app/interface/request-download-attachment';
import { RequestDuplicateAcceptanceCreateDocumentPager } from 'src/app/interface/request-duplicate-acceptance-create-document-pager';
import { RequestDuplicateAcceptanceGetDocument } from 'src/app/interface/request-duplicate-acceptance-get-document';
import { RequestEditPaymentMethodFeu218 } from 'src/app/interface/request-edit-payment-method-feu-218';
import { RequestExpiringCardList } from 'src/app/interface/request-expiring-card-list';
import { RequestFeuCart } from 'src/app/interface/request-feu-cart';
import { RequestFibraBilling } from 'src/app/interface/request-fibra-billing';
import { RequestFinalTransaction } from 'src/app/interface/request-final-transaction';
import { RequestGestioneCruscottoAttivazioneCampagna } from 'src/app/interface/request-gestione-cruscotto-attivazione-campagna';
import { RequestGestioneCruscottoConfigurazioneCampagna } from 'src/app/interface/request-gestione-cruscotto-configurazione-campagna';
import { RequestGestioneCruscottoDeleteContatti } from 'src/app/interface/request-gestione-cruscotto-delete-contatti';
import { RequestGestioneCruscottoDettagliCampagna } from 'src/app/interface/request-gestione-cruscotto-dettagli-campagna';
import { RequestGetActivitiesRsa } from 'src/app/interface/request-get-activities-rsa';
import { RequestGetAllGroups } from 'src/app/interface/request-get-all-groups';
import { RequestGetAllReadings } from 'src/app/interface/request-get-all-readings';
import { RequestGetAnagraficaClienteContratti } from 'src/app/interface/request-get-anagrafica-cliente-contratti';
import { RequestGetAsset } from 'src/app/interface/request-get-asset';
import { RequestGetBlockScaDetail } from 'src/app/interface/request-get-block-sca-detail';
import { RequestBooked } from 'src/app/interface/request-get-booked-transactions';
import { RequestGetBusinessAgreements } from 'src/app/interface/request-get-business-agreements';
import { RequestGetCap } from 'src/app/interface/request-get-cap';
import { RequestGetClosingRequest } from 'src/app/interface/request-get-closingRequest';
import { RequestGetCockpit } from 'src/app/interface/request-get-cockpit';
import { RequestGetComune } from 'src/app/interface/request-get-comune';
import { RequestGetConfAttrNrg } from 'src/app/interface/request-get-conf-attr-nrg';
import { RequestGetConsensi } from 'src/app/interface/request-get-consensi';
import { RequestGetContactChannel } from 'src/app/interface/request-get-contactChannel';
import { RequestGetContract360 } from 'src/app/interface/request-get-contract-360';
import { RequestGetContracts } from 'src/app/interface/request-get-contracts';
import { RequestGetCruscAssDed } from 'src/app/interface/request-get-crusc-ass-ded';
import { RequestCruscottoBlacklist } from 'src/app/interface/request-get-cruscotto-blacklist';
import { RequestCruscottoCustomerLarge } from 'src/app/interface/request-get-cruscotto-customer-large';
import { RequestGetCustomerDetails } from 'src/app/interface/request-get-customer-details';
import { RequestGetCustomerSalesPointAsset } from 'src/app/interface/request-get-customer-sales-point-asset';
import { RequestGetCustomers } from 'src/app/interface/request-get-customers';
import { RequestGetDatiFiliali } from 'src/app/interface/request-get-dati-filiali';
import { RequestGetDatiInvioPinFromValidation } from 'src/app/interface/request-get-dati-invio-pin-from-validation';
import { RequestGetDatiLdv } from 'src/app/interface/request-get-dati-ldv';
import { RequestGetDatiTipologiche } from 'src/app/interface/request-get-dati-tipologiche';
import { RequestDettaglioBonificoStati } from 'src/app/interface/request-get-dettaglio-bonifico-stati';
import { RequestGetDocumentSd } from 'src/app/interface/request-get-document-sd';
import { RequestGetEmailRecipients } from 'src/app/interface/request-get-email-recipients';
import { RequestGetEmailTemplateDetail } from 'src/app/interface/request-get-email-template-detail';
import { RequestGetEmailTemplates } from 'src/app/interface/request-get-email-templates';
import { RequestGetEnergyMandates } from 'src/app/interface/request-get-energy-mandates';
import { RequestTransaction } from 'src/app/interface/request-get-ex-transactions';
import { RequestGetFullVitalStatisticsByCf } from 'src/app/interface/request-get-full-vital-statistics-by-cf';
import { RequestGetHeaderPreventivo } from 'src/app/interface/request-get-header-preventivo';
import { RequestGetHistory } from 'src/app/interface/request-get-history';
import { RequestGetIdentificationCode } from 'src/app/interface/request-get-identification-code';
import { RequestGetIndicator } from 'src/app/interface/request-get-indicator';
import { RequestGetInfoAnagrafiche } from 'src/app/interface/request-get-info-anagrafiche';
import { RequestInquiryBonificiPostagiro } from 'src/app/interface/request-get-inquiry-bonifici-postagiro';
import { RequestInquiryUpdateCard, RequestTemporaryBlock } from 'src/app/interface/request-get-inquiry-update-card';
import { RequestGetInvoice360 } from 'src/app/interface/request-get-invoice-360';
import { RequestGetInvoiceAttachment } from 'src/app/interface/request-get-invoice-attachment';
import { RequestGetKoCard } from 'src/app/interface/request-get-ko-card';
import { RequestGetMailSenders } from 'src/app/interface/request-get-mail-senders';
import { RequestGetOneView } from 'src/app/interface/request-get-one-view';
import { RequestGetOrderDetails } from 'src/app/interface/request-get-order-details';
import { RequestGetOrderObjFact } from 'src/app/interface/request-get-order-obj-fact';
import { RequestGetOrderRicOp } from 'src/app/interface/request-get-order-ric-op';
import { RequestGetOrderRicOpAsinc } from 'src/app/interface/request-get-order-ric-op-asinc';
import { RequestGetPrefix } from 'src/app/interface/request-get-prefix';
import { RequestGetPresignedUrl } from 'src/app/interface/request-get-presigned-url';
import { RequestGetProvincia } from 'src/app/interface/request-get-provincia';
import { RequestGetRapporti } from 'src/app/interface/request-get-rapporti';
import { RequestGetRicOffline } from 'src/app/interface/request-get-ric-offline';
import { RequestSimTracking } from 'src/app/interface/request-get-sim-tracking';
import { RequestGetStructTerr } from 'src/app/interface/request-get-struct-terr';
import { RequestGetTicketChangeHistory } from 'src/app/interface/request-get-ticket-change-history';
import { RequestGetTicketClaims } from 'src/app/interface/request-get-ticket-claims';
import { RequestGetTicketRequests } from 'src/app/interface/request-get-ticket-requests';
import { RequestGetUserDeviceData } from 'src/app/interface/request-get-user-device-data';
import { RequestGetUserRolesByCf } from 'src/app/interface/request-get-user-roles-by-cf';
import { RequestGetVirtualGroups } from 'src/app/interface/request-get-virtual-groups';
import { RequestHandleIStTransfer } from 'src/app/interface/request-handle-ist-transfers';
import { RequestInfoPolicyDetailPa } from 'src/app/interface/request-info-policy-detail-pa';
import { RequestInfoPolicyDetailPv } from 'src/app/interface/request-info-policy-detail-pv';
import { RequestInstallmentConcession } from 'src/app/interface/request-installment-concession';
import { RequestInviaPin } from 'src/app/interface/request-invia-pin';
import { RequestInvoiceDetail } from 'src/app/interface/request-invoice-detail';
import { RequestLdvReleaseData } from 'src/app/interface/request-ldv-release-data';
import { RequestLeggiPuntiVendita } from 'src/app/interface/request-leggi-punti-vendita';
import { RequestListaAziendeBpiol } from 'src/app/interface/request-lista-aziende-bpiol';
import { RequestListaAziendeCreditrici } from 'src/app/interface/request-lista-aziende-creditrici';
import { RequestNfeaRelease } from 'src/app/interface/request-nfea-release';
import { RequestOrder } from 'src/app/interface/request-order';
import { RequestOrderCustomer } from 'src/app/interface/request-order-customer';
import { RequestPassbookMovements } from 'src/app/interface/request-passbook-movements';
import { RequestPostel } from 'src/app/interface/request-postel';
import { RequestPracticeDetailEnergy } from 'src/app/interface/request-practice-detail-energy';
import { RequestPracticeDetailFeu41 } from 'src/app/interface/request-practice-detail-feu-41';
import { RequestPracticeDetailRecovery } from 'src/app/interface/request-practice-detail-recovery';
import { RequestPracticesRecovery } from 'src/app/interface/request-practices-recovery';
import { RequestPresaInCarico } from 'src/app/interface/request-presa-in-carico';
import { RequestProductsSearch } from 'src/app/interface/request-products-search';
import { RequestProfileDetail } from 'src/app/interface/request-profile-detail';
import { RequestProfiling } from 'src/app/interface/request-profiling';
import { RequestProspectChangePasswordRequest } from 'src/app/interface/request-prospect-change-password';
import { RequestPukReissue } from 'src/app/interface/request-puk-reissue';
import { RequestReadAddresses } from 'src/app/interface/request-read-addresses';
import { RequestReadCategory } from 'src/app/interface/request-read-category';
import { RequestReadPayments } from 'src/app/interface/request-read-payments';
import { RequestReadPolicyList } from 'src/app/interface/request-read-policy-list';
import { RequestReadTitles } from 'src/app/interface/request-read-titles';
import { RequestReadings } from 'src/app/interface/request-readings';
import { RequestReinvioNotificaNfeaFromValidation } from 'src/app/interface/request-reinvio-notifica-nfea-from-validation';
import { RequestResponsibleEmployee } from 'src/app/interface/request-responsible-employee';
import { RequestRetrieveEcbpwebDocument } from 'src/app/interface/request-retrieve-ecbpweb-document';
import { RequestRetrieveStockInfo } from 'src/app/interface/request-retrieve-stock-info';
import { RequestRetrieveUserid } from 'src/app/interface/request-retrieve-userid';
import { RequestRigenerazionePinBpiol } from 'src/app/interface/request-rigenerazione-pin-bpiol';
import { RequestSaleabilityCoverage } from 'src/app/interface/request-saleability-coverage';
import { RequestSdaDeliveryDocument } from 'src/app/interface/request-sda-delivery-document';
import { RequestSearchDocument } from 'src/app/interface/request-search-document-archivio-unico';
import { RequestSearchEcbpwebData } from 'src/app/interface/request-search-ecbpweb-data';
import { RequestSearchPickup } from 'src/app/interface/request-search-pickup';
import { RequestSearchShipments } from 'src/app/interface/request-search-shipments';
import { RequestSearchUserWallet } from 'src/app/interface/request-search-user-wallet';
import { RequestSecuredNumberBusiness } from 'src/app/interface/request-secured-number-business';
import { RequestSelectOperations } from 'src/app/interface/request-select-operations';
import { RequestSend23l } from 'src/app/interface/request-send-23l';
import { RequestSendEmail } from 'src/app/interface/request-send-email';
import { RequestSendGeneratedIsee } from 'src/app/interface/request-send-generated-isee';
import { RequestSendNotification } from 'src/app/interface/request-send-notification';
import { SendResetPasswordRequest } from 'src/app/interface/request-send-reset-password';
import { SendSmsRequest } from 'src/app/interface/request-send-sms';
import { RequestStrongAuthDoc, RequestStrongAuthSecuredNumber } from 'src/app/interface/request-strong-auth';
import { RequestSupplyDetail } from 'src/app/interface/request-supply-detail';
import { RequestTakeActionByScenario } from 'src/app/interface/request-take-action-by-scenario';
import { RequestTakeMoreActionByScenario } from 'src/app/interface/request-take-more-action-by-scenario';
import { RequestTicketGetNotifiche } from 'src/app/interface/request-ticket-get-notifiche';
import { RequestTransfer } from 'src/app/interface/request-transfer';
import { RequestUpdtCruscAssDed } from 'src/app/interface/request-updt-crusc-ass-ded';
import { RequestUploadAttachment } from 'src/app/interface/request-upload-attachment';
import { RequestUserRevoke } from 'src/app/interface/request-user-revoke';
import { RequestValidateForeignVat } from 'src/app/interface/request-validate-foreign-vat';
import { RequestVerifyEnabledIstTransfer } from 'src/app/interface/request-verify-enabled-ist-transfer';
import { RequestVerifyIban } from 'src/app/interface/request-verify-iban';
import { RequestVerifyShipment } from 'src/app/interface/request-verify-shipment';
import { RequestVerifyWallet } from 'src/app/interface/request-verifyWallet';
import { RequestViewArchivedDocs } from 'src/app/interface/request-view-archived-docs';
import { RequestZcaRecessoInesitate } from 'src/app/interface/request-zca-recesso-inesitate';
import { RequestRequiredFields, ResponseRequiredFields } from 'src/app/interface/required-fields';
import { ResetPasswordUserRequest } from 'src/app/interface/reset-password-user-request';
import { ResetPasswordUserResponse } from 'src/app/interface/reset-password-user-response';
import { ResponseVerifyWallet } from 'src/app/interface/responce-verifyWallet';
import { ResponseAcceptanceClientWs } from 'src/app/interface/response-acceptance-client-ws';
import { ResponseAccountConnection } from 'src/app/interface/response-account-connection';
import { ResponseAccountDeleteCredential } from 'src/app/interface/response-account-delete-credential';
import { ResponseAccountStatus } from 'src/app/interface/response-account-status';
import { ResponseAccountTransitionHist } from 'src/app/interface/response-account-transition-hist';
import { ResponseAddress } from 'src/app/interface/response-address';
import { ResponseAttrTickInbox } from 'src/app/interface/response-attr-tick-inbox';
import { ResponseBankAccountList } from 'src/app/interface/response-bank-account-list';
import { ResponseBfp } from 'src/app/interface/response-bfp-calc';
import { ResponseTypesBfp } from 'src/app/interface/response-bfp-types-list';
import { ResponseBankAccount } from 'src/app/interface/response-block-bank-account';
import {
  RequestClienteRidotto,
  ResponseBlockingCard,
  ResponseOneRapporto,
} from 'src/app/interface/response-blocking-card';
import { ResponseBookedPassbookBalanceList } from 'src/app/interface/response-booked-passbook-balance-list';
import { ResponseCallMeBackAdd } from 'src/app/interface/response-call-me-back-add';
import { ResponseCallMeBackTimeSlot } from 'src/app/interface/response-call-me-back-time-slot';
import { ResponseCancelPractice } from 'src/app/interface/response-cancel-practice';
import { ResponseCardListByCf } from 'src/app/interface/response-card-list-by-cf';
import { ResponseCardRenewal } from 'src/app/interface/response-card-renewal';
import { ResponseCardReplacement, ResponseVerifyCardReplacement } from 'src/app/interface/response-card-replacement';
import { ResponseCashback } from 'src/app/interface/response-cashback';
import { ResponseCmbChannels } from 'src/app/interface/response-cmb-channels';
import { ResponseCmbResults } from 'src/app/interface/response-cmb-results';
import { ResponseCmbServices } from 'src/app/interface/response-cmb-services';
import { ResponseConfigurationStatus } from 'src/app/interface/response-configuration-status';
import { ResponseConnectionList } from 'src/app/interface/response-connection-list';
import { ResponseConsistency } from 'src/app/interface/response-consistency';
import {
  ResponseConsultazioneStatoMorosita,
  ResponseStatoMorosita,
} from 'src/app/interface/response-consultazione-stato-morosita';
import { ResponseCreateAttachment } from 'src/app/interface/response-create-attachment';
import { ResponseCreateCustomer } from 'src/app/interface/response-create-customer';
import { ResponseCreateRequestID } from 'src/app/interface/response-create-request-id';
import { ResponseCreateUpdateProfiling } from 'src/app/interface/response-create-update-profiling';
import { ResponseCustomerAssets } from 'src/app/interface/response-customer-assets';
import { ResponseCustomerChat } from 'src/app/interface/response-customer-chat';
import { ResponseCustomerGeopost } from 'src/app/interface/response-customer-geopost';
import { ResponseCruscottoBlackList } from 'src/app/interface/response-delete-cruscotto-blacklist';
import { ResponseDeleteProfiling } from 'src/app/interface/response-delete-profiling';
import { ResponseDeleteTicketAttachment } from 'src/app/interface/response-delete-ticket-attachment';
import { ResponseDeleteUser } from 'src/app/interface/response-delete-user';
import { ResponseDettaglioBonificoStati } from 'src/app/interface/response-dettaglio-bonifico-stati';
import { ResponseDisableUser } from 'src/app/interface/response-disable-user';
import { ResponseDoLdvRelease } from 'src/app/interface/response-do-ldv-release';
import { ResponseDomiciliationDetails } from 'src/app/interface/response-domiciliation-details';
import { ResponseDomiciliationList } from 'src/app/interface/response-domiciliation-list';
import { ResponseDossier } from 'src/app/interface/response-dossier';
import { ResponseDownloadAttachment } from 'src/app/interface/response-download-attachment';
import { ResponseDownloadBillingDocument } from 'src/app/interface/response-download-billing-document';
import { ResponseDuplicateAcceptanceCreateDocumentPager } from 'src/app/interface/response-duplicate-acceptance-create-document-pager';
import { ResponseDuplicateAcceptanceGetDocument } from 'src/app/interface/response-duplicate-acceptance-get-document';
import { ResponseEditPaymentMethodFeu218 } from 'src/app/interface/response-edit-payment-method-feu-218';
import { ResponseExpiringCardList } from 'src/app/interface/response-expiring-card-list';
import { ResponseFeuCart } from 'src/app/interface/response-feu-cart';
import { ResponseFibraBilling } from 'src/app/interface/response-fibra-billing';
import { ResponseFinalTransaction } from 'src/app/interface/response-final-transaction';
import { ResponseFindsByCF } from 'src/app/interface/response-finds-by-cf';
import { ResponseSendGeneratedIsee } from 'src/app/interface/response-generated-isee';
import { ResponseGestioneCruscottoAttivazioneCampagna } from 'src/app/interface/response-gestione-cruscotto-attivazione-campagna';
import { ResponseGestioneCruscottoCampagne } from 'src/app/interface/response-gestione-cruscotto-campagne';
import { ResponseGestioneCruscottoConfigurazioneCampagna } from 'src/app/interface/response-gestione-cruscotto-configurazione-campagna';
import { ResponseGestioneCruscottoDeleteContatti } from 'src/app/interface/response-gestione-cruscotto-delete-contatti';
import { ResponseGestioneCruscottoDettagliContatti } from 'src/app/interface/response-gestione-cruscotto-dettagli-contatti';
import { ResponseGetActionRelease } from 'src/app/interface/response-get-action-release';
import { ResponseGetActivitiesRsa } from 'src/app/interface/response-get-activities-rsa';
import { ResponseGetAllGroups } from 'src/app/interface/response-get-all-groups';
import { ResponseGetAllReadings } from 'src/app/interface/response-get-all-readings';
import { ResponseGetAnagraficaClienteContratti } from 'src/app/interface/response-get-anagrafica-cliente-contratti';
import { ResponseGetAsset } from 'src/app/interface/response-get-asset';
import { ResponseGetAssetFull } from 'src/app/interface/response-get-asset-full';
import { ResponseGetAssetHeader } from 'src/app/interface/response-get-asset-header';
import { ResponseGetAssociatedProducts } from 'src/app/interface/response-get-associated-products';
import { ResponseGetBlockScaDetail } from 'src/app/interface/response-get-block-sca-detail';
import { ResponseBooked } from 'src/app/interface/response-get-booked-transactions';
import { ResponseGetBusinessAgreements } from 'src/app/interface/response-get-business-agreements';
import { ResponseGetBusinessInvoicings } from 'src/app/interface/response-get-business-invoicings';
import { GetByAlias } from 'src/app/interface/response-get-by-alias';
import { Categories } from 'src/app/interface/response-get-category';
import { ResponseGetClosingRequest } from 'src/app/interface/response-get-closingRequest';
import { ResponseGetCockpit } from 'src/app/interface/response-get-cockpit';
import { ResponseGetConfAttrNrg } from 'src/app/interface/response-get-conf-attr-nrg';
import { ResponseGetConsensi } from 'src/app/interface/response-get-consensi';
import { ResponseGetContactChannel } from 'src/app/interface/response-get-contactChannel';
import { ResponseGetContract360 } from 'src/app/interface/response-get-contract-360';
import { ResponseGetContracts } from 'src/app/interface/response-get-contracts';
import { ResponseGetContractsFullData } from 'src/app/interface/response-get-contracts-full-data';
import { ResponseGetContractsUserRoles } from 'src/app/interface/response-get-contracts-user-roles';
import { ResponseSearchCruscottoBlackList } from 'src/app/interface/response-get-cruscotto-blacklist';
import { ResponseSearchCruscottoCustomerLarge } from 'src/app/interface/response-get-cruscotto-customer-large';
import { ResponseGetCustomerBusinessAgreements } from 'src/app/interface/response-get-customer-business-agreements';
import { ResponseGetCustomerContractFull } from 'src/app/interface/response-get-customer-contract-full';
import { ResponseGetCustomerDetails } from 'src/app/interface/response-get-customer-details';
import { ResponseGetCustomerSalesPointAsset } from 'src/app/interface/response-get-customer-sales-point-asset';
import { ResponseGetCustomers } from 'src/app/interface/response-get-customers';
import { ResponseGetDatiInvioPinFromValidation } from 'src/app/interface/response-get-dati-invio-pin-from-validation';
import { ResponseGetDatiLdv } from 'src/app/interface/response-get-dati-ldv';
import { ResponseGetDatiTipologiche } from 'src/app/interface/response-get-dati-tipologiche';
import { ResponseGetDocumentBase64 } from 'src/app/interface/response-get-document-base-64';
import { ResponseGetDocumentSd } from 'src/app/interface/response-get-document-sd';
import { ResponseGetEmailTemplateDetail } from 'src/app/interface/response-get-email-detail';
import { ResponseGetEmailRecipients } from 'src/app/interface/response-get-email-recipients';
import { ResponseGetEmailTemplates } from 'src/app/interface/response-get-email-templates';
import { ResponseGetEnergyMandates } from 'src/app/interface/response-get-energy-mandates';
import { ResponseGetEntityUser } from 'src/app/interface/response-get-entity-user';
import { AccountedTransitionResponse } from 'src/app/interface/response-get-ex-transactions';
import { ResponseGetExport } from 'src/app/interface/response-get-export.interface';
import { ResponseGetFullVitalStatisticsByCFResponse } from 'src/app/interface/response-get-full-vital-statistics-by-cf';
import { ResponseGetFunnelDetail } from 'src/app/interface/response-get-funnel-detail';
import { ResponseGetFunnels } from 'src/app/interface/response-get-funnels';
import { ResponseGetHeaderPreventivo } from 'src/app/interface/response-get-header-preventivo';
import { ResponseGetHistory } from 'src/app/interface/response-get-history';
import { ResponseGetHourTimeSpans } from 'src/app/interface/response-get-hour-time-spans';
import { ResponseGetIbanByTicket } from 'src/app/interface/response-get-iban-by-ticket';
import { ResponseGetIdentificationCode } from 'src/app/interface/response-get-identification-code';
import { ResponseGetIndicator, ResponsePoliciesProactivity } from 'src/app/interface/response-get-indicator';
import { ResponseGetInfoAnagrafiche } from 'src/app/interface/response-get-info-anagrafiche';
import { ResponseInquiryBonificiPostagiro } from 'src/app/interface/response-get-inquiry-bonifici-postagiro';
import { ResponseGetInvoice360 } from 'src/app/interface/response-get-invoice-360';
import { ResponseGetInvoiceAttachment } from 'src/app/interface/response-get-invoice-attachment';
import { ResponseIstTransfer } from 'src/app/interface/response-get-ist-transfers';
import { RequestGetJweToken, ResponseGetJweToken } from 'src/app/interface/response-get-jwt-token';
import { ResponseGetKoCard } from 'src/app/interface/response-get-ko-card';
import { ResponseGetMailSenders } from 'src/app/interface/response-get-mail-senders';
import { ResponseGetMandatesDetail } from 'src/app/interface/response-get-mandates-detail';
import { ResponseGetOneView } from 'src/app/interface/response-get-one-view';
import { ResponseGetOrderDetails } from 'src/app/interface/response-get-order-details';
import { ResponseGetOrderObjFact } from 'src/app/interface/response-get-order-obj-fact';
import { ResponseGetOrderRicOp } from 'src/app/interface/response-get-order-ric-op';
import { ResponseGetPosidoniaDetail } from 'src/app/interface/response-get-posidonia-detail';
import { ResponseGetPreferences } from 'src/app/interface/response-get-preferences';
import { ResponseGetPrefix } from 'src/app/interface/response-get-prefix';
import { ResponseGetPresignedUrl } from 'src/app/interface/response-get-presigned-url';
import { ResponseGetPresignedUrlAutomaticSend } from 'src/app/interface/response-get-presigned-url-automatic-send';
import { Products } from 'src/app/interface/response-get-products';
import { ResponseGetPukReissue } from 'src/app/interface/response-get-puk-reissue';
import { ResponseGetQuotes } from 'src/app/interface/response-get-quotes';
import { ResponseGetRapporti } from 'src/app/interface/response-get-rapporti';
import { ResponseGetRicOffline } from 'src/app/interface/response-get-ric-offline';
import { ResponseSimTracking } from 'src/app/interface/response-get-sim-tracking';
import { ResponseGetStructTerr } from 'src/app/interface/response-get-struct-terr';
import { ResponseGetTicketChangeHistory } from 'src/app/interface/response-get-ticket-change-history';
import { ResponseGetTicketClaims } from 'src/app/interface/response-get-ticket-claims';
import { ResponseGetTicketRequests } from 'src/app/interface/response-get-ticket-requests';
import { ResponseGetUpdateCard } from 'src/app/interface/response-get-update-card-interface';
import { ResponseGetUserDeviceData } from 'src/app/interface/response-get-user-device-data';
import { ResponseGetUserEvents } from 'src/app/interface/response-get-user-events';
import { ResponseGetUserInbox } from 'src/app/interface/response-get-user-inbox';
import { ResponseGetUserRolesByCf } from 'src/app/interface/response-get-user-roles-by-cf';
import { ResponseGetVirtualGroups } from 'src/app/interface/response-get-virtual-groups';
import { ResponseGetWorkDate } from 'src/app/interface/response-get-work-date';
import { ResponseHandleIStTransfer } from 'src/app/interface/response-handle-ist-transfer';
import { ResponseHubDistribution } from 'src/app/interface/response-hub-distribution';
import { ResponseI23lGetDocument } from 'src/app/interface/response-i23l-get-document';
import { InfoAccountDetail, RequestInfoAccountDetail } from 'src/app/interface/response-info-account-detail';
import { InfoRicarica, StoriaDisponibile } from 'src/app/interface/response-info-card-detail';
import { ResponseInfoPolicyDetailPa } from 'src/app/interface/response-info-policy-detail-pa';
import { ResponseInfoPolicyDetailPv } from 'src/app/interface/response-info-policy-detail-pv';
import { ResponseInquiryOrch } from 'src/app/interface/response-inquiry-orch';
import { ResponseInquiryByAlias } from 'src/app/interface/response-inquiry-phone-by-alias';
import { ResponseInquiryPhoneByCfReport } from 'src/app/interface/response-inquiry-phone-by-cf-report';
import { ResponseInstallmentConcession } from 'src/app/interface/response-installment-concession';
import { ResponseInviaPin } from 'src/app/interface/response-invia-pin';
import { ResponseInvoiceDetail } from 'src/app/interface/response-invoice-detail';
import { ResponseLavorazioneTicket } from 'src/app/interface/response-lavorazione-ticket.interface';
import { ResponseLdvReleaseData } from 'src/app/interface/response-ldv-release-data';
import { ResponseLeggiPuntiVendita } from 'src/app/interface/response-leggi-punti-vendita';
import { ResponseListaAziendeBpiol } from 'src/app/interface/response-lista-aziende-bpiol';
import { ResponseListaAziendeCreditrici } from 'src/app/interface/response-lista-aziende-creditrici';
import { ResponseLogisticOrder, ResponseLogisticOrderOrchestrator } from 'src/app/interface/response-logistic-order';
import { ResponseMoneybox } from 'src/app/interface/response-moneybox-status';
import { ResponseNfeaRelease } from 'src/app/interface/response-nfea-release';
import { ResponseOrder } from 'src/app/interface/response-order';
import { ResponseOrderCustomer } from 'src/app/interface/response-order-customer';
import { ResponsePassbookMovements } from 'src/app/interface/response-passbook-movements';
import { ResponsePayments } from 'src/app/interface/response-payments';
import { ResponsePostel } from 'src/app/interface/response-postel';
import { DossierCrmu, ResponsePracticeDetailEnergy } from 'src/app/interface/response-practice-detail-energy';
import { ResponsePracticeDetailFeu41 } from 'src/app/interface/response-practice-detail-feu-41';
import { ResponsePracticeDetailRecovery } from 'src/app/interface/response-practice-detail-recovery';
import { ResponsePracticesRecovery } from 'src/app/interface/response-practices-recovery';
import { ResponsePresaInCarico } from 'src/app/interface/response-presa-in-carico';
import { ResponseProductToActive } from 'src/app/interface/response-product-to-active';
import { ResponseProfileDetail } from 'src/app/interface/response-profile-detail';
import { Data2, ResponseProfiling } from 'src/app/interface/response-profiling';
import { ResponseProspectChangePasswordResponse } from 'src/app/interface/response-prospect-change-password';
import { ResponsePtGetDocument } from 'src/app/interface/response-pt-get-document';
import { ResponsePukReissue } from 'src/app/interface/response-puk-reissue';
import { ResponsePukReissueRequestList } from 'src/app/interface/response-puk-reissue-request-list';
import { ResponseReadAddresses } from 'src/app/interface/response-read-addresses';
import { ResponseReadCategory } from 'src/app/interface/response-read-category';
import { ResponseReadPayments } from 'src/app/interface/response-read-payments';
import { ResponseReadPolicyList } from 'src/app/interface/response-read-policy-list';
import { ResponseReadTitles } from 'src/app/interface/response-read-titles';
import { ResponseReadings } from 'src/app/interface/response-readings';
import { ResponseRecuperaInfoPacchi } from 'src/app/interface/response-recupero-info-pacchi';
import { ResponseReinvioNotificaNfeaFromValidation } from 'src/app/interface/response-reinvio-notifica-nfea-from-validation';
import { ResponseResetPasswordSys } from 'src/app/interface/response-reset-password-sys';
import { ResponseResponsibleEmployee } from 'src/app/interface/response-responsible-employee';
import { ResponseRetrieveecbpwebDocument } from 'src/app/interface/response-retrieve-ecbpweb-document';
import { ResponseRetrieveStockInfo } from 'src/app/interface/response-retrieve-stock-info';
import { ResponseRetrieveUserid } from 'src/app/interface/response-retrieve-userid';
import { ResponseRevokePostePlus } from 'src/app/interface/response-revoke-posteplus';
import { ResponseRigenerazionePinBpiol } from 'src/app/interface/response-rigenerazione-pin-bpiol';
import { ResponseSaleabilityCoverage } from 'src/app/interface/response-saleability-coverage';
import { ResponseSdaDeliveryDocument } from 'src/app/interface/response-sda-delivery-document';
import { ResponseSearchBillingDocument } from 'src/app/interface/response-search-billing-document';
import { ResponseSearchEcbpwebData } from 'src/app/interface/response-search-ecbpweb-data';
import { ResponseSearchPickup } from 'src/app/interface/response-search-pickup';
import { ResponseSearchShipment } from 'src/app/interface/response-search-shipment';
import { ResponseSearchShipments } from 'src/app/interface/response-search-shipments';
import { ResponseSearchUserWallet } from 'src/app/interface/response-search-user-wallet';
import { ResponseSecuredNumberBusiness } from 'src/app/interface/response-secured-number-business';
import { ResponseSend23l } from 'src/app/interface/response-send-23l';
import { ResponseSendEmail } from 'src/app/interface/response-send-email';
import { ResponseSendNotification } from 'src/app/interface/response-send-notification';
import { ResponseSendResetPasswordResponse } from 'src/app/interface/response-send-reset-password';
import { ResponseSendSmsResponse } from 'src/app/interface/response-send-sms';
import { ResponseSetAddresses } from 'src/app/interface/response-set-addresses';
import { ResponseSupplyDetail } from 'src/app/interface/response-supply-detail';
import { ResponseTakeActionByScenario } from 'src/app/interface/response-take-action-by-scenario';
import { ResponseTakeMoreActionByScenario } from 'src/app/interface/response-take-more-action-by-scenario';
import { ResponseTicketGetNotifiche } from 'src/app/interface/response-ticket-get-notifiche';
import { ResponseTransfer } from 'src/app/interface/response-transfer';
import { ResponseUpdateRevokeInfo } from 'src/app/interface/response-update-revoke-info';
import { ResponseUpdtCruscAssDed } from 'src/app/interface/response-updt-crusc-ass-ded';
import { ResponseUploadAttachment } from 'src/app/interface/response-upload-attachment';
import { ResponseUserRevoke } from 'src/app/interface/response-user-revoke';
import { ResponseUsersCompanyByTaxcode } from 'src/app/interface/response-users-company-by-taxcode';
import { ResponseValidateForeignVat } from 'src/app/interface/response-validate-foreign-vat';
import { ResponseVerifyEnabledIstTransfer } from 'src/app/interface/response-verify-enabled-ist-transfer';
import { ResponseVerifyIban } from 'src/app/interface/response-verify-iban';
import { ResponseVerifyShipment } from 'src/app/interface/response-verify-shipment';
import { ResponseViewArchivedDocs } from 'src/app/interface/response-view-archived-docs';
import { ResponseViewInstallmentDetail } from 'src/app/interface/response-view-installment-detail';
import { ResponseZcaRecessoInesitate } from 'src/app/interface/response-zca-recesso-inesitate';
import { RequestRetrieveCard, ResponseRetrieveCard } from 'src/app/interface/retrieve-card';
import { RetrieveOperationsSelect } from 'src/app/interface/retrieve-operations-select';
import { RetrieveTransactionPortRequest } from 'src/app/interface/retrieve-transaction-port-request';
import { RetrieveTransactionPortResponse } from 'src/app/interface/retrieve-transaction-port-response';
import { RetrievesUserCredentialsRequest } from 'src/app/interface/retrieves-user-credentials-request';
import { RetrievesUserCredentialsResponse } from 'src/app/interface/retrieves-user-credentials-response';
import { RequestRicalcoliOrch, ResponseRicalcoliOrch } from 'src/app/interface/ricalcoli-orch';
import { ResponseSaldoListaCollegamenti } from 'src/app/interface/saldo-lista-collegament-response';
import { RequestSaldoListaCollegamenti } from 'src/app/interface/saldo-lista-collegamenti';
import { SaveFeedbackRequest } from 'src/app/interface/save-feeback/save-feedback-request.interface';
import { SaveFeedbackResponse } from 'src/app/interface/save-feeback/save-feedback-response.interface';
import { ScriviProattivitaRequest } from 'src/app/interface/scrivi-proattivita-request.interface';
import { SdaAnagldvRequest } from 'src/app/interface/sda-anagldv-request';
import { SdaAnagldvResponse } from 'src/app/interface/sda-anagldv-response';
import { SearchUserAndProfileV2Request } from 'src/app/interface/search-user-and-profile-v2-request';
import { SearchUserAndProfileV2Response } from 'src/app/interface/search-user-and-profile-v2-response';
import {
  SearchUserSpidProfileRequest,
  SearchUserSpidProfileResponse,
} from 'src/app/interface/searchUserSpidAndProfile';
import { SendDossierMailRequest } from 'src/app/interface/send-dossier-mail-request';
import { SendDossierMailResponse } from 'src/app/interface/send-dossier-mail-response';
import { ErrorSaveSMS } from 'src/app/interface/sendSMS/save/error-save-sms.interface';
import { RequestSaveSMS } from 'src/app/interface/sendSMS/save/request-save-sms.interface';
import { ResponseSaveSMS } from 'src/app/interface/sendSMS/save/response-save-sms.interface';
import { ErrorSendSMS } from 'src/app/interface/sendSMS/send/error-send-sms.interface';
import { RequestSendSMS } from 'src/app/interface/sendSMS/send/request-send-sms.interface';
import { ResponseSendSMS } from 'src/app/interface/sendSMS/send/response-send-sms.interface';
import { requestSMSTemplatesError } from 'src/app/interface/sendSMS/templates/error-sms-templates.interface';
import { RequestSMSTemplates } from 'src/app/interface/sendSMS/templates/request-sms-templates.interface';
import { ResponseSMSTemplates } from 'src/app/interface/sendSMS/templates/response-sms-templates.interface';
import { SimulatoreProdottoByCfResponse } from 'src/app/interface/simulatore-prodotto-by-cf-response';
import { SimulatoreProdottoByIdResponse } from 'src/app/interface/simulatore-prodotto-by-id-response.interface';
import { DocData, ResponseStrongAuth } from 'src/app/interface/strong-auth';
import { submitPersonIdRequest } from 'src/app/interface/submitPersonIdRequest';
import { GestioneTemplateMailRequest } from 'src/app/interface/template-mail/gestione-template-mail-request.type';
import { GestioneTemplateMailResponse } from 'src/app/interface/template-mail/gestione-template-mail-response.type';
import { TemplateMailCompleteRequest } from 'src/app/interface/template-mail/template-mail-complete-request.interface';
import { TemplateMailRequest } from 'src/app/interface/template-mail/template-mail-request.interface';
import {
  TemplateMailMongoResponse,
  TemplateMailResponse,
} from 'src/app/interface/template-mail/template-mail-response.interface';
import { UndoRevokeResponse } from 'src/app/interface/undo-revoke-response';
import { UpdateUserHistoryRequest } from 'src/app/interface/update-user-history-request';
import { UpdateUserHistoryResponse } from 'src/app/interface/update-user-history-response';
import { UserHistoryRequest } from 'src/app/interface/user-history-request';
import { UserHistoryResponse } from 'src/app/interface/user-history-response';
import { RequestOperativitaTelsec, ResponseOperativitaTelsec } from 'src/app/interface/verifica-operativita-and-telsec';
import {
  VerificaStatoByAliasCompleteRequest,
  VerificaStatoByAliasRequest,
  VerificaStatoByAliasResponse,
} from 'src/app/interface/verifica-stato-by-alias.interface';
import {
  getVoiceBiometricStatus,
  getVoiceBiometricStatusPayload,
  updateVoiceBiometricStatus,
  updateVoiceBiometricStatusPayload,
} from 'src/app/interface/voiceBiometricStatus';
import {
  ChangeMerchantStateCompleteRequest,
  ChangeMerchantStateRequest,
  ChangeMerchantStateResponse,
} from 'src/app/interface/whitelist/change-merchant-state.interface';
import {
  OpenWhitelistAccordionCompleteRequest,
  OpenWhitelistAccordionRequest,
  OpenWhitelistAccordionResponse,
} from 'src/app/interface/whitelist/whitelist.interface.ts';
import { RequestInsertDatiCliente } from 'src/app/models/request-insert-dati-cliente';
import { State } from 'src/app/redux';
import { setVoteReasonSelects } from 'src/app/redux/actions/settings.action';
import { FacadeService } from 'src/app/services/facade/facade.service';
import { MomentService } from 'src/app/services/moment/moment.service';
import { aliasLength, getUniqueId } from 'src/app/utils/general-functions';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl: string;
  private apiUrls: ApiUrls;
  private clientKey: string;
  private isComingFromNFEA = false;
  private isPostevita = false;
  private urlProxy: string;
  private baseUrlPath: string;
  private sendSmsParams: Record<string, any>;
  public urlRaccoltaAzioniPoste = '';
  public urlCommunicationNavigate = '';
  public aiknowConfig: object;
  private kmBaseUrl: string;
  private spBaseUrl: string;
  private companyPIN: string;
  private isClientLarge: boolean;

  constructor(
    private http: HttpClient,
    private facade: FacadeService,
    private facadeBiscottiera: FacadeBiscottieraService,
    private cleanFacade: CleanFacadeService,
    private cookieService: CookieService,
    private store$: Store<State>,
    private readonly moment: MomentService,
    private apiCacheService: ApiCacheService,
  ) {
    this.facade.apiData.getVoiceBotClientKey$
      .pipe(first(Boolean))
      .subscribe((clientKey: string) => (this.clientKey = clientKey));
    this.cleanFacade.getIsPosteVita$.subscribe((isPostevita: boolean) => (this.isPostevita = isPostevita));
  }

  sendMassiveDocumentation(request: any): Observable<SendMassiveDocumentResponse> {
    return this.http.post<SendMassiveDocumentResponse>(
      `${this.baseUrlPath}${this.apiUrls.sendMassiveDocumentation.uri}`,
      request,
    );
  }

  getBaseUrl(): Observable<BaseUrl> {
    return this.http.get<BaseUrl>('config/base-url.json').pipe(
      tap((url: BaseUrl) => {
        this.apiUrls = url.apiUrls;
        this.baseUrl = url.baseUrl;
        this.facade.setBaseUrl(url);
        this.urlProxy = url.environmentUrl;
        this.facade.apiData.getIsComingFromNfea$.subscribe((isComingFromNFEA: boolean) => {
          this.isComingFromNFEA = isComingFromNFEA;
          this.baseUrlPath = isComingFromNFEA ? this.urlProxy : this.baseUrl;
        });
        this.facade.visibility = url.cjConfig.visibility as Record<string, Visibility>;
        this.sendSmsParams = url.sendSmsParams;
        this.urlRaccoltaAzioniPoste = url.urlRaccoltaOrdiniPoste;
        this.urlCommunicationNavigate = url.communicationNavigateTo;
        this.facade.setSms(url.sendSmsParams);
        this.facade.setAdapterWebReceiptValues(url.adapterWebReceiptValues);
        this.facade.setIsEnabled132627(url.enabled132627);
        this.kmBaseUrl = url.kmBaseUrl;
        this.spBaseUrl = url.spBaseUrl;
        this.store$.dispatch(setVoteReasonSelects({ feedbackMotivationSelect: url.feedbackMotivationSelect }));
      }),
    );
  }

  getDefaultStatusList(
    request: RequestConsultazioneStatoMorosita,
    interactionId: string,
    timestamp: string,
  ): Observable<ResponseConsultazioneStatoMorosita> {
    return this.http.post<ResponseConsultazioneStatoMorosita>(
      `${this.baseUrlPath}${this.apiUrls.getDefaultStatusList.uri}`,
      request,
      {
        headers: {
          'Content-Encoding': 'identity',
          'PI-Correlation-Id': interactionId,
          'PI-Request-Id': interactionId,
          'PI-Source': 'PIX',
          'PI-Channel': 'CC',
          'PI-Timestamp': timestamp,
          'PI-BusinessObject-Id': timestamp,
          'PI-Target': 'ISU',
          'PI-CompanyName': 'PI',
        },
      },
    );
  }

  getAiknowScriptConfig(): Observable<{ pix_intent_url: string; pix_km_proxy_url: string; user: string }> {
    return this.facade.apiData.getBaseUrl$.pipe(
      filter(res => !!res),
      first(),
      map(_ => ({
        pix_intent_url: `${this.apiUrls.getAiknowIntentUrl.uri}`,
        pix_km_proxy_url: `${this.apiUrls.getAiknowProxyUrl.uri}`,
        user: `${this.cookieService.get('USERID')}@posteitaliane.it`,
        pix_bff_token_url: `${this.baseUrlPath}${this.apiUrls.aiknowTokenUrl.uri}`,
        km_base_url: this.kmBaseUrl,
        sp_base_url: this.spBaseUrl,
      })),
    );
  }

  getDefaultStatusListOrch(
    request: RequestStatoMorosita,
    interactionId: string,
    timestamp: string,
  ): Observable<ResponseStatoMorosita> {
    return this.http.post<ResponseStatoMorosita>(
      `${this.baseUrlPath}${this.apiUrls.getDefaultStatusListOrch.uri}`,
      request,
      {
        headers: {
          'Content-Encoding': 'identity',
          'PI-Correlation-Id': interactionId,
          'PI-Request-Id': interactionId,
          'PI-Source': 'PIX',
          'PI-Channel': 'CC',
          'PI-Timestamp': timestamp,
          'PI-BusinessObject-Id': timestamp,
          'PI-Target': 'ISU',
          'PI-CompanyName': 'PI',
        },
      },
    );
  }

  getProfiling(request?: RequestProfiling): Observable<ResponseProfiling | Data2> {
    const headers = request
      ? {
          profili: request.profili,
          ...(request.gruppoCreazione ? { gruppoCreazione: request.gruppoCreazione } : {}),
          ...(request.tipoCliente ? { tipoCliente: request.tipoCliente } : {}),
        }
      : {};
    return this.http
      .get<{
        data: ResponseProfiling;
      }>(`${this.baseUrlPath}${this.apiUrls.getProfiling.uri}`, { headers })
      .pipe(map(response => response.data));
  }

  userRevokeAll(request: RequestUserRevoke): Observable<ResponseUserRevoke> {
    return this.http.post<ResponseUserRevoke>(`${this.baseUrlPath}${this.apiUrls.userRevokeAll.uri}`, request);
  }

  userRevokeSchemasAuth(request: RequestUserRevoke): Observable<ResponseUserRevoke> {
    return this.http.post<ResponseUserRevoke>(`${this.baseUrlPath}${this.apiUrls.userRevokeSchemasAuth.uri}`, request);
  }

  presaInCarico(request: RequestPresaInCarico): Observable<ResponsePresaInCarico> {
    return this.http.post<ResponsePresaInCarico>(`${this.baseUrlPath}${this.apiUrls.presaInCarico.uri}`, request);
  }

  updateRevokeInfo(codiceFiscale: string, codiceRevoca: string): Observable<ResponseUpdateRevokeInfo> {
    return this.http.post<ResponseUpdateRevokeInfo>(`${this.baseUrlPath}${this.apiUrls.updateRevokeInfo.uri}`, {
      codiceFiscale,
      codiceRevoca,
    });
  }

  undoRevoke(revokeId: string): Observable<UndoRevokeResponse> {
    return this.http.post<UndoRevokeResponse>(`${this.baseUrlPath}${this.apiUrls.undoRevoke.uri}`, {
      revokeId,
    });
  }

  deleteProfiling(request: RequestProfiling): Observable<ResponseDeleteProfiling> {
    return this.http.delete<ResponseDeleteProfiling>(`${this.baseUrlPath}${this.apiUrls.getProfiling.uri}`, {
      headers: {
        profili: request.profili,
      },
    });
  }

  createProfiling(request: RequestCreateUpdateProfiling): Observable<ResponseCreateUpdateProfiling> {
    return this.http.put<ResponseCreateUpdateProfiling>(`${this.baseUrlPath}${this.apiUrls.getProfiling.uri}`, request);
  }

  updateProfiling(
    request: RequestCreateUpdateProfiling,
    oldName: string = null,
  ): Observable<ResponseCreateUpdateProfiling> {
    if (!oldName) {
      return this.http.post<ResponseCreateUpdateProfiling>(
        `${this.baseUrlPath}${this.apiUrls.getProfiling.uri}`,
        request,
      );
    } else {
      return this.deleteProfiling({ profili: oldName }).pipe(
        switchMap(({ data: response }: ResponseDeleteProfiling) => {
          if (response.deletedCount > 0) {
            return this.createProfiling(request);
          }
        }),
      );
    }
  }

  getReadAddresses(request: RequestReadAddresses): Observable<ResponseReadAddresses> {
    return this.http.post<ResponseReadAddresses>(`${this.baseUrlPath}${this.apiUrls.readAddresses.uri}`, request);
  }

  getReadPolicyList(request: RequestReadPolicyList): Observable<ResponseReadPolicyList> {
    return this.http.post<ResponseReadPolicyList>(`${this.baseUrlPath}${this.apiUrls.readPolicyList.uri}`, request);
  }

  getReadPVPAPrivacy(request: RequestReadPVPAPrivacy): Observable<ResponseReadPVPAPrivacy> {
    return this.http.post<ResponseReadPVPAPrivacy>(`${this.baseUrlPath}${this.apiUrls.readPVPAPrivacy.uri}`, request);
  }

  getUserInfo(mobilePhone?: string, fiscalCode?: string, buGroup?: string): Observable<ResponseGetCustomers> {
    const body: RequestGetCustomers = {
      searchCriteria: [
        ...(mobilePhone
          ? [
              {
                attrName: 'SAP_TELCONTATTO',
                attrValue: mobilePhone,
              },
            ]
          : []),
        ...(fiscalCode
          ? [
              {
                attrName: 'SAP_CODFIS',
                attrValue: fiscalCode,
              },
            ]
          : []),
      ],
      buGroup,
    };

    return this.getCustomers(body);
  }

  order(order: RequestOrder): Observable<ResponseOrder> {
    if (this.isPostevita) {
      order = {
        ...order,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseOrder>(`${this.baseUrlPath}${this.apiUrls.createOrder.uri}`, {
      order,
    });
  }

  getCommunicationsByUser(queryParams: string): Observable<ResponseGetCommunicationsByUser> {
    return this.http.get<ResponseGetCommunicationsByUser>(
      `${this.baseUrlPath}${this.apiUrls.getCommunicationsByUser.uri}?user=${queryParams}`,
    );
  }

  createEscalationOrder(order: any): Observable<any> {
    return this.http.post<ResponseOrder>(`${this.baseUrlPath}${this.apiUrls.createOrder.uri}`, {
      order,
    });
  }

  ennupleRetrieve(): Observable<{ data: CjEnnuple }> {
    return this.http.get<{ data: CjEnnuple }>(`${this.baseUrlPath}${this.apiUrls.ennupleRetrieve.uri}`);
  }

  orderCustomer(orderCustomers: RequestOrderCustomer): Observable<ResponseOrderCustomer> {
    return this.http.post<ResponseOrderCustomer>(`${this.baseUrlPath}${this.apiUrls.getCustomerOrders.uri}`, {
      orderCustomers,
    });
  }

  getJweToken(request: RequestGetJweToken): Observable<ResponseGetJweToken> {
    return this.http.post<ResponseGetJweToken>(`${this.baseUrlPath}${this.apiUrls.getJweToken.uri}`, request);
  }

  getCustomerChat(customerChatRequest: RequestCustomerChat): Observable<ResponseCustomerChat> {
    const headers = new HttpHeaders({
      ClientKey: this.clientKey,
    });

    return this.http.post<ResponseCustomerChat>(
      `${this.baseUrlPath}${this.apiUrls.getCustomerChat.uri}`,
      customerChatRequest,
      {
        headers,
      },
    );
  }

  getCustomers(getCustomersRequest: RequestGetCustomers): Observable<ResponseGetCustomers> {
    if (this.isPostevita) {
      getCustomersRequest = {
        ...getCustomersRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseGetCustomers>(
      `${this.baseUrlPath}${this.apiUrls.getCustomerV2.uri}`,
      getCustomersRequest,
    );
  }

  getInfoAnagrafiche(getCustomersRequest: RequestGetInfoAnagrafiche): Observable<ResponseGetInfoAnagrafiche> {
    return this.http.post<ResponseGetInfoAnagrafiche>(
      `${this.baseUrlPath}${this.apiUrls.getInfoAnagrafiche.uri}`,
      getCustomersRequest,
    );
  }

  getHistory(getHistoryRequest: RequestGetHistory): Observable<ResponseGetHistory> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetHistory>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getHistory.uri : this.apiUrls.getHistory.uri + queryParam
      }`,
      getHistoryRequest,
    );
  }

  getCustomerDetails(getCustomerDetailsRequest: RequestGetCustomerDetails): Observable<ResponseGetCustomerDetails> {
    if (this.isPostevita) {
      getCustomerDetailsRequest = {
        ...getCustomerDetailsRequest,
        mandante: RequestFields.mandante,
      };
      if (getCustomerDetailsRequest.bp && getCustomerDetailsRequest.bp === '-' && this.isPostevita) {
        delete getCustomerDetailsRequest.bp;
      }
    }
    return this.http.post<ResponseGetCustomerDetails>(
      `${this.baseUrlPath}${this.apiUrls.getCustomerV2Detail.uri}`,
      getCustomerDetailsRequest,
    );
  }

  uploadAttachment(
    uploadAttachmentRequest: RequestUploadAttachment,
    isFromTicketDetail?: boolean,
  ): Observable<ResponseUploadAttachment> {
    if (this.isPostevita) {
      uploadAttachmentRequest = {
        ...uploadAttachmentRequest,
        mandante: RequestFields.mandante,
      };
    }
    const queryParamsString = this.isPostevita ? `?sap-client=200` : '';

    return this.http.post<ResponseUploadAttachment>(
      `${this.baseUrlPath}${this.apiUrls.uploadAttachment.uri}${queryParamsString}`,
      uploadAttachmentRequest,
    );
  }

  createCustomer(createCustomerRequest: RequestCreateCustomer): Observable<ResponseCreateCustomer> {
    if (!this.isPostevita) {
      delete createCustomerRequest.mandante;
    }
    return this.http.post<ResponseCreateCustomer>(
      `${this.baseUrlPath}${this.apiUrls.createCustomer.uri}`,
      createCustomerRequest,
    );
  }

  getConsistency(type: string, consistency: RequestConsistency): Observable<ResponseConsistency> {
    return this.http.post<ResponseConsistency>(
      `${this.baseUrlPath}${this.apiUrls.consistency.uri}${type}`,
      consistency,
    );
  }

  sendEmail(sendEmailRequest: RequestSendEmail): Observable<ResponseSendEmail> {
    if (this.isPostevita) {
      sendEmailRequest = {
        ...sendEmailRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseSendEmail>(`${this.baseUrlPath}${this.apiUrls.sendEmail.uri}`, sendEmailRequest);
  }

  getTicketChangesRegistry(
    getTicketChangesRequest: RequestGetTicketChangeHistory,
  ): Observable<ResponseGetTicketChangeHistory> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetTicketChangeHistory>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getTicketHistory.uri : this.apiUrls.getTicketHistory.uri + queryParam
      }`,
      getTicketChangesRequest,
    );
  }

  getNazione(): Observable<ResponseAddress> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.get<ResponseAddress>(
      `${this.baseUrlPath}${!this.isPostevita ? this.apiUrls.getNation.uri : this.apiUrls.getNation.uri + queryParam}`,
    );
  }

  getComune(getComuneRequest: RequestGetComune): Observable<ResponseAddress> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseAddress>(
      `${this.baseUrlPath}${!this.isPostevita ? this.apiUrls.getCity.uri : this.apiUrls.getCity.uri + queryParam}`,
      getComuneRequest,
    );
  }

  getProvincia(getProvinciaRequest: RequestGetProvincia): Observable<ResponseAddress> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseAddress>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getProvince.uri : this.apiUrls.getProvince.uri + queryParam
      }`,
      getProvinciaRequest,
    );
  }

  getCap(getCapRequest: RequestGetCap): Observable<ResponseAddress> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseAddress>(
      `${this.baseUrlPath}${!this.isPostevita ? this.apiUrls.getCap.uri : this.apiUrls.getCap.uri + queryParam}`,
      getCapRequest,
    );
  }

  getContracts(getContractsRequest: RequestGetContracts): Observable<ResponseGetContracts> {
    return this.http.post<ResponseGetContracts>(
      `${this.baseUrlPath}${this.apiUrls.getContracts.uri}`,
      getContractsRequest,
    );
  }

  verifyWallet(verifyWalletRequest: RequestVerifyWallet, headers: HttpHeaders): Observable<ResponseVerifyWallet> {
    return this.http.post<ResponseVerifyWallet>(
      `${this.baseUrlPath}${this.apiUrls.verifyWallet.uri}`,
      verifyWalletRequest,
      {
        headers,
      },
    );
  }

  getEmailTemplates(getEmailRequest: RequestGetEmailTemplates): Observable<ResponseGetEmailTemplates> {
    if (this.isPostevita) {
      getEmailRequest = {
        ...getEmailRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseGetEmailTemplates>(
      `${this.baseUrlPath}${this.apiUrls.getEmailTemplates.uri}`,
      getEmailRequest,
    );
  }

  getEmailTemplateDetail(
    getEmailDetailRequest: RequestGetEmailTemplateDetail,
  ): Observable<ResponseGetEmailTemplateDetail> {
    if (this.isPostevita) {
      getEmailDetailRequest = {
        ...getEmailDetailRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseGetEmailTemplateDetail>(
      `${this.baseUrlPath}${this.apiUrls.getEmailTemplateDetail.uri}`,
      getEmailDetailRequest,
    );
  }

  getMailSenders(getMailSendersRequest: RequestGetMailSenders): Observable<ResponseGetMailSenders> {
    if (this.isPostevita) {
      getMailSendersRequest = {
        ...getMailSendersRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseGetMailSenders>(
      `${this.baseUrlPath}${this.apiUrls.getMailSenders.uri}`,
      getMailSendersRequest,
    );
  }

  getOrderDetails(getOrderRequest: RequestGetOrderDetails): Observable<ResponseGetOrderDetails> {
    if (this.isPostevita) {
      getOrderRequest = {
        ...getOrderRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseGetOrderDetails>(
      `${this.baseUrlPath}${this.apiUrls.getOrderDetails.uri}`,
      getOrderRequest,
    );
  }

  getTicketClaims(getTicketClaimsRequest: RequestGetTicketClaims): Observable<ResponseGetTicketClaims> {
    return this.http.post<ResponseGetTicketClaims>(
      `${this.baseUrlPath}${this.apiUrls.getReclami.uri}`,
      getTicketClaimsRequest,
    );
  }

  getTicketRequests(getTicketRequestsRequest: RequestGetTicketRequests): Observable<ResponseGetTicketRequests> {
    return this.http.post<ResponseGetTicketRequests>(
      `${this.baseUrlPath}${this.apiUrls.getOrderFromCustomer.uri}`,
      getTicketRequestsRequest,
    );
  }

  getReadCategory(request: Partial<RequestReadCategory>): Observable<ResponseReadCategory> {
    let headers;
    if (this.isPostevita) {
      headers = new HttpHeaders({
        mandante: RequestFields.mandante,
      });
    }
    return this.http.post<ResponseReadCategory>(
      `${this.baseUrlPath}${this.apiUrls.getLevelsEnnupla.uri}`,
      request,
      this.isPostevita ? { headers } : {},
    );
  }

  getTicketCommunication(getTicketCommunication: RequestTicketGetNotifiche): Observable<ResponseTicketGetNotifiche> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseTicketGetNotifiche>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getNotifiche.uri : this.apiUrls.getNotifiche.uri + queryParam
      }`,
      getTicketCommunication,
    );
  }

  getVirtualGroups(getVirtualGroupsRequest: RequestGetVirtualGroups): Observable<ResponseGetVirtualGroups> {
    return this.http.post<ResponseGetVirtualGroups>(
      `${this.baseUrlPath}${this.apiUrls.getVirtualGroups.uri}`,
      getVirtualGroupsRequest,
    );
  }

  getPrefix(getPrefixRequest: RequestGetPrefix): Observable<ResponseGetPrefix> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetPrefix>(
      `${this.baseUrlPath}${!this.isPostevita ? this.apiUrls.getPrefix.uri : this.apiUrls.getPrefix.uri + queryParam}`,
      getPrefixRequest,
    );
  }

  getClosingReasons(getReasonClosingRequest: RequestGetClosingRequest): Observable<ResponseGetClosingRequest> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetClosingRequest>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getClosingReason.uri : this.apiUrls.getClosingReason.uri + queryParam
      }`,
      getReasonClosingRequest,
    );
  }

  deleteTicketAttachment(
    deteteAttachmentRequest: RequestDeleteTicketAttachment,
  ): Observable<ResponseDeleteTicketAttachment> {
    return this.http.post<ResponseDeleteTicketAttachment>(
      `${this.baseUrlPath}${this.apiUrls.deleteAttachment.uri}`,
      deteteAttachmentRequest,
    );
  }

  getContactChannel(getContactChannel: RequestGetContactChannel): Observable<ResponseGetContactChannel> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetContactChannel>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getContactChannel.uri : this.apiUrls.getContactChannel.uri + queryParam
      }`,
      getContactChannel,
    );
  }

  downloadAttachment(downloadAttachmentRequest: RequestDownloadAttachment): Observable<ResponseDownloadAttachment> {
    if (this.isPostevita) {
      downloadAttachmentRequest = {
        ...downloadAttachmentRequest,
        mandante: RequestFields.mandante,
      };
    }
    return this.http.post<ResponseDownloadAttachment>(
      `${this.baseUrlPath}${this.apiUrls.downloadAttachment.uri}`,
      downloadAttachmentRequest,
    );
  }

  getIdentificationCode(
    getIdentificationCode: RequestGetIdentificationCode,
  ): Observable<ResponseGetIdentificationCode> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetIdentificationCode>(
      `${this.baseUrlPath}${
        !this.isPostevita
          ? this.apiUrls.getCodiceIdentificativo.uri
          : this.apiUrls.getCodiceIdentificativo.uri + queryParam
      }`,
      getIdentificationCode,
    );
  }

  getEmailRecipients(getEmailRecipients: RequestGetEmailRecipients): Observable<ResponseGetEmailRecipients> {
    return this.http.post<ResponseGetEmailRecipients>(
      `${this.baseUrlPath}${this.apiUrls.getEmailRecipients.uri}`,
      getEmailRecipients,
    );
  }

  finalTransaction(getIdentificationCode: Partial<RequestFinalTransaction>): Observable<ResponseFinalTransaction> {
    return this.http.post<ResponseFinalTransaction>(
      `${this.baseUrlPath}${this.apiUrls.createRequest.uri}`,
      getIdentificationCode,
    );
  }

  createRequestID(): Observable<ResponseCreateRequestID> {
    return this.http.post<ResponseCreateRequestID>(`${this.baseUrlPath}${this.apiUrls.createRequestID.uri}`, null);
  }

  getCruscottoBlacklist(
    getCruscottoBlacklist: RequestCruscottoBlacklist,
  ): Observable<ResponseSearchCruscottoBlackList> {
    return this.http.post<ResponseSearchCruscottoBlackList>(
      `${this.baseUrlPath}${this.apiUrls.getBlockers.uri}`,
      getCruscottoBlacklist,
    );
  }

  getCruscottoCustomerLarge(
    getCruscottoCustomerLarge: RequestCruscottoCustomerLarge,
    headers: HttpHeaders,
  ): Observable<ResponseSearchCruscottoCustomerLarge> {
    return this.http.post<ResponseSearchCruscottoCustomerLarge>(
      `${this.baseUrlPath}${this.apiUrls.getContractCustomerLarge.uri}`,
      getCruscottoCustomerLarge,
      {
        headers,
      },
    );
  }

  deleteCruscottoBlacklist(
    deleteCruscottoBlacklist: RequestCruscottoBlacklist,
  ): Observable<ResponseCruscottoBlackList> {
    return this.http.post<ResponseCruscottoBlackList>(
      `${this.baseUrlPath}${this.apiUrls.removeBlocker.uri}`,
      deleteCruscottoBlacklist,
    );
  }

  addCruscottoBlacklist(
    addCruscottoBlacklist: RequestCruscottoBlacklist | RequestCruscottoBlacklist[],
  ): Observable<ResponseCruscottoBlackList> {
    return this.http.post<ResponseCruscottoBlackList>(
      `${this.baseUrlPath}${this.apiUrls.insertBlocker.uri}`,
      addCruscottoBlacklist,
    );
  }

  getSecuredNumber(
    getSecuredNumber: RequestStrongAuthSecuredNumber,
    headers: HttpHeaders,
  ): Observable<ResponseStrongAuth> {
    return this.http.post<ResponseStrongAuth>(
      `${this.baseUrlPath}${this.apiUrls.getSecuredNumber.uri}`,
      getSecuredNumber,
      {
        headers,
      },
    );
  }

  blockCard(blockCard: RequestCardBlocking): Observable<ResponseBlockingCard> {
    return this.http.post<ResponseBlockingCard>(`${this.baseUrlPath}${this.apiUrls.blockDebitCard.uri}`, blockCard);
  }

  getStrongAuthDocs(doc: RequestStrongAuthDoc): Observable<DocData> {
    return this.http.post<DocData>(`${this.baseUrlPath}${this.apiUrls.clientDocumentIdentification.uri}`, doc);
  }

  getAccountedTransactions(accounted: RequestTransaction, headers): Observable<AccountedTransitionResponse> {
    return this.http.post<AccountedTransitionResponse>(
      `${this.baseUrlPath}${this.apiUrls.exTransactionMovementsList.uri}`,
      accounted,
      { headers },
    );
  }

  getBookedTransactions(booked: RequestBooked, headers?): Observable<ResponseBooked> {
    return this.http.post<ResponseBooked>(
      `${this.baseUrlPath}${this.apiUrls.bookedTransactionsList.uri}`,
      booked,
      headers ? { headers } : {},
    );
  }

  getSimTracking(tracking: RequestSimTracking, headers: HttpHeaders): Observable<ResponseSimTracking> {
    return this.http.post<ResponseSimTracking>(`${this.baseUrlPath}${this.apiUrls.simTracking.uri}`, tracking, {
      headers,
    });
  }

  blockBankAccount(account: RequestBlockBankAccount): Observable<ResponseBankAccount> {
    return this.http.post<ResponseBankAccount>(`${this.baseUrlPath}${this.apiUrls.blockAccountCode.uri}`, account);
  }

  getAccountConnection(headers: HttpHeaders): Observable<ResponseAccountConnection> {
    return this.http.get<ResponseAccountConnection>(`${this.baseUrlPath}${this.apiUrls.accountConnection.uri}`, {
      headers,
    });
  }

  getAccountTransitionHist(keys: string, headers: HttpHeaders): Observable<{ data: ResponseAccountTransitionHist[] }> {
    return this.http.get<{
      data: ResponseAccountTransitionHist[];
    }>(`${this.baseUrlPath}${this.apiUrls.accountTransactionHist.uri}${keys}`, {
      headers,
    });
  }

  deleteCredential(id: string, headers: HttpHeaders): Observable<ResponseAccountDeleteCredential> {
    return this.http.delete<ResponseAccountDeleteCredential>(
      `${this.baseUrlPath}${this.apiUrls.accountDeleteConnection.uri}/${id}`,
      {
        headers,
      },
    );
  }

  deleteUser(headers: HttpHeaders): Observable<ResponseDeleteUser> {
    return this.http.delete<ResponseDeleteUser>(`${this.baseUrlPath}${this.apiUrls.deleteUser.uri}`, {
      headers,
    });
  }

  getConnectionList(keys: string, headers: HttpHeaders): Observable<{ data: ResponseConnectionList }> {
    return this.http.get<{
      data: ResponseConnectionList;
    }>(`${this.baseUrlPath}${this.apiUrls.connectionList.uri}${keys}`, {
      headers,
    });
  }

  inquiryTelefonoByCfRapporto(request: RequestConsistency): Observable<ResponseInquiryPhoneByCfReport> {
    return this.http.post<ResponseInquiryPhoneByCfReport>(
      `${this.baseUrlPath}${this.apiUrls.inquiryTelefonoByCfRapporto.uri}`,
      request,
    );
  }

  inquiryTelefonoByAlias(request: RequestConsistency): Observable<ResponseInquiryByAlias> {
    return this.http.post<ResponseInquiryByAlias>(
      `${this.baseUrlPath}${this.apiUrls.verifyStatusByAlias.uri}`,
      request,
    );
  }

  getAccountStatus(request: RequestAccountStatus): Observable<ResponseAccountStatus> {
    return this.http.post<ResponseAccountStatus>(`${this.baseUrlPath}${this.apiUrls.accountStatus.uri}`, request);
  }

  getTargetsMoneybox(headers: HttpHeaders, cf: string, flags: string): Observable<ResponseMoneybox> {
    return this.http.get<ResponseMoneybox>(`${this.baseUrlPath}${this.apiUrls.moneyboxStatus.uri}${cf}&${flags}`, {
      headers,
    });
  }

  bankAccountList(request: RequestBankAccountList): Observable<ResponseBankAccountList> {
    return this.http.post<ResponseBankAccountList>(`${this.baseUrlPath}${this.apiUrls.bankAccountList.uri}`, request);
  }

  bfpList(request, headers): Observable<Bfp> {
    return this.http.post<Bfp>(`${this.baseUrlPath}${this.apiUrls.bfpList.uri}`, request, { headers });
  }

  consistencyList(request: string, headers: HttpHeaders): Observable<ResponseFindsByCF> {
    return this.http.get<ResponseFindsByCF>(
      `${this.baseUrlPath}${this.apiUrls.findsByCf.uri}?codFisc=${request}&numeroCarte=100`,
      {
        headers,
      },
    );
  }

  bfpDetailList(request: RequestBfp): Observable<ResponseBfp> {
    return this.http.post<ResponseBfp>(`${this.baseUrlPath}${this.apiUrls.bfpDetail.uri}`, request);
  }

  getBpCardList(request: { cf: string; numeroCC: string }): Observable<ResponseCardListByCf> {
    return this.http.post<ResponseCardListByCf>(`${this.baseUrlPath}${this.apiUrls.cardListBp.uri}`, request);
  }

  getDossier(request: RequestDossier): Observable<ResponseDossier> {
    return this.http.post<ResponseDossier>(`${this.baseUrlPath}${this.apiUrls.getDossier.uri}`, request);
  }

  getDossier360(request: RequestDossier | RequestDossierByPod): Observable<ResponseDossier> {
    return this.http.post<ResponseDossier>(`${this.baseUrlPath}${this.apiUrls.getDossier360.uri}`, request);
  }

  getAsset(request: RequestGetAsset): Observable<ResponseGetAsset> {
    return this.http.post<ResponseGetAsset>(`${this.baseUrlPath}${this.apiUrls.getAsset.uri}`, request);
  }

  readWarranties(request: ReadWarrantiesRequest, headers?): Observable<ReadWarrantiesResponse> {
    return this.http.post<ReadWarrantiesResponse>(
      `${this.baseUrlPath}${this.apiUrls.getWarranties.uri}`,
      request,
      headers ? { headers } : {},
    );
  }

  getAsset360(request: RequestGetAsset): Observable<ResponseGetAsset> {
    return this.http.post<ResponseGetAsset>(`${this.baseUrlPath}${this.apiUrls.getAsset360.uri}`, request);
  }

  getAssetFull(id: string, headers = null, nemesi?): Observable<{ data: ResponseGetAssetFull }> {
    return this.http.get<{
      data: ResponseGetAssetFull;
    }>(`${this.baseUrlPath}${this.apiUrls.getAssetFull.uri}/${id}`, {
      headers: {
        ...(this.isComingFromNFEA && Boolean(headers) ? headers : {}),
        'PI-user-id': 'NO_USERID',
        ...nemesi,
      },
    });
  }

  getPostel(request: RequestPostel): Observable<ResponsePostel> {
    return this.http.post<ResponsePostel>(`${this.baseUrlPath}${this.apiUrls.getPostel.uri}`, request);
  }

  getPostelDoc(request: string, headers: HttpHeaders): Observable<{ data: { base64: string; type: string } }> {
    return this.http.get<{
      data: { base64: string; type: string };
    }>(`${this.baseUrlPath}${this.apiUrls.getPostelDoc.uri}?r_object_id=${request}`, {
      headers,
    });
  }

  getTransfer(request: RequestTransfer): Observable<ResponseTransfer> {
    return this.http.post<ResponseTransfer>(`${this.baseUrlPath}${this.apiUrls.getTransfer.uri}`, request);
  }

  getDetailsCardActivation(request): Observable<any> {
    return this.http.get<any>(`${this.baseUrlPath}${this.apiUrls.getCardDetail.uri}`, request);
  }

  getCardListToActive(request: RequestConsistency): Observable<ResponseProductToActive> {
    return this.http.post<ResponseProductToActive>(`${this.baseUrlPath}${this.apiUrls.cardListToActive.uri}`, request);
  }

  getPaymentRequest(keys: string, headers: HttpHeaders): Observable<ResponsePayments> {
    return this.http.get<ResponsePayments>(`${this.baseUrlPath}${this.apiUrls.getPayments.uri}${keys}`, {
      headers,
    });
  }

  getCategory(headers: HttpHeaders): Observable<Categories> {
    return this.http.get<Categories>(`${this.baseUrlPath}${this.apiUrls.getCategory.uri}`, {
      headers,
    });
  }

  getProduct(headers: HttpHeaders, requestBody: RequestProductsSearch): Observable<Products> {
    return this.http.post<Products>(`${this.baseUrlPath}${this.apiUrls.getProducts.uri}`, requestBody, {
      headers,
    });
  }

  deletePsd2(keys: string, headers: HttpHeaders): Observable<any> {
    return this.http.delete<any>(`${this.baseUrlPath}${this.apiUrls.deleteProduct.uri}${keys}`, {
      headers,
    });
  }

  adapterWebDownloadDocumentInfoById(keys: string, isComingFromNfea: boolean): Observable<DocumentInfoById> {
    const baseUrl = isComingFromNfea ? this.urlProxy : this.baseUrl;
    return this.http.get<DocumentInfoById>(`${baseUrl}${this.apiUrls.adapterWebDownloadDocumentInfoById.uri}${keys}`);
  }

  adapterWebDownloadDocumentInfoByAu(keys: string, isComingFromNfea: boolean): Observable<DocumentFromAu> {
    return this.http.get<DocumentFromAu>(
      `${this.baseUrlPath}${this.apiUrls.adapterWebDownloadDocumentInfoByAu.uri.replace('%idAu%', keys)}`,
    );
  }

  activateCard(request: ActivationCard): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.activateCard.uri}`, request);
  }

  getCashback(headers: HttpHeaders): Observable<ResponseCashback> {
    return this.http.get<ResponseCashback>(`${this.baseUrlPath}${this.apiUrls.getCashback.uri}`, { headers });
  }

  deleteCashback(headers: HttpHeaders): Observable<ResponseCashback> {
    return this.http.delete<ResponseCashback>(`${this.baseUrlPath}${this.apiUrls.deleteCashback.uri}`, { headers });
  }

  getFunnels(queryParams: string, request): Observable<ResponseGetFunnels> {
    return this.http.post<ResponseGetFunnels>(
      `${this.baseUrlPath}${this.apiUrls.getFunnels.uri}?${queryParams}`,
      request,
    );
  }

  getMailAssistenzaBPM(idFdi: string, headers?: HttpHeaders): Observable<ResponseMailAssistanceBPM> {
    return this.http.post<ResponseMailAssistanceBPM>(
      `${this.baseUrlPath}${this.apiUrls.getMailAssistanceBPM.uri}`,
      { idFdi },
      { headers },
    );
  }

  annullableFunnel(fid: string, queryParams: string): Observable<{}> {
    return this.http.get<{}>(
      `${this.baseUrlPath}${this.apiUrls.annullableFunnel.uri.replace('%fid%', fid)}?${queryParams}`,
    );
  }

  deleteFunnel(fid: string, queryParams: string): Observable<{}> {
    return this.http.delete<{}>(`${this.baseUrlPath}${this.apiUrls.removeFunnel.uri}/${fid}?${queryParams}`);
  }

  fibraBilling(data: RequestFibraBilling): Observable<ResponseFibraBilling> {
    return this.http.post<ResponseFibraBilling>(`${this.baseUrlPath}${this.apiUrls.fibraBilling.uri}`, data);
  }

  searchBillingDocument(request: any): Observable<ResponseSearchBillingDocument> {
    return this.http.post<ResponseSearchBillingDocument>(
      `${this.baseUrlPath}${this.apiUrls.searchBillingDocument.uri}`,
      request,
    );
  }

  downloadBillingDocument(request): Observable<ResponseDownloadBillingDocument> {
    return this.http.post<ResponseDownloadBillingDocument>(
      `${this.baseUrlPath}${this.apiUrls.downloadBillingDocument.uri}`,
      request,
    );
  }

  logisticOrderByFunnelId(
    dossierId: string,
    headers: HttpHeaders,
    isMaintainance = false,
  ): Observable<ResponseLogisticOrder> {
    return this.http.post<ResponseLogisticOrder>(
      `${this.baseUrlPath}${this.apiUrls.logisticOrderByFunnelId.uri}?dossierId=${dossierId}${
        isMaintainance ? '&flagDetail=true' : ''
      }`,
      {},
      {
        headers,
      },
    );
  }

  logisticOrderByFunnelIdList(
    dossierId: string[],
    headers: HttpHeaders,
    isMaintainance = true,
  ): Observable<ResponseLogisticOrderOrchestrator> {
    return this.http.post<ResponseLogisticOrderOrchestrator>(
      `${this.baseUrlPath}${this.apiUrls.logisticOrderByFunnelIdList.uri}${isMaintainance ? '?flagDetail=false' : ''}`,
      {
        dossierId,
      },
      {
        headers,
      },
    );
  }

  getDigitalMembershipStatus(request: string): Observable<{ data: DigitalMembershipStatus }> {
    return this.http.get<{
      data: DigitalMembershipStatus;
    }>(`${this.baseUrlPath}${this.apiUrls.digitalMembershipStatus.uri}${request}`);
  }

  searchUserWallet(request: RequestSearchUserWallet): Observable<ResponseSearchUserWallet> {
    return this.http.post<ResponseSearchUserWallet>(`${this.baseUrlPath}${this.apiUrls.searchUserWallet.uri}`, request);
  }

  getUserEvent(request: RequestSearchUserWallet): Observable<ResponseGetUserEvents> {
    return this.http.post<ResponseGetUserEvents>(`${this.baseUrlPath}${this.apiUrls.getUserEvent.uri}`, request);
  }

  deleteWallet(request: RequestSearchUserWallet): Observable<{
    data: { 'command-success': boolean; 'command-error-message': string };
  }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteWallet.uri}`, request);
  }

  deleteDevice(request: RequestSearchUserWallet): Observable<{ data: any }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteDevice.uri}`, request);
  }

  deleteAllDevice(request: RequestSearchUserWallet): Observable<{ data: any }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteAllDevice.uri}`, request);
  }

  findBfpList({ dataSottoscrizione }): Observable<ResponseTypesBfp> {
    return this.http.post<ResponseTypesBfp>(`${this.baseUrlPath}${this.apiUrls.bfpTypesList.uri}`, {
      dataSottoscrizione,
    });
  }

  userDeviceData(request: RequestGetUserDeviceData): Observable<ResponseGetUserDeviceData> {
    return this.http.post<ResponseGetUserDeviceData>(`${this.baseUrlPath}${this.apiUrls.userDeviceData.uri}`, request);
  }

  getPassbookMovements(request: RequestPassbookMovements, headers): Observable<ResponsePassbookMovements> {
    return this.http.post<ResponsePassbookMovements>(
      `${this.baseUrlPath}${this.apiUrls.getPassbookMovements.uri}`,
      request,
      { headers },
    );
  }

  listaMovimentiPrenotate(request: string, headers: HttpHeaders): Observable<ResponseBookedPassbookBalanceList> {
    return this.http.get<ResponseBookedPassbookBalanceList>(
      `${this.baseUrlPath}${this.apiUrls.listaMovimentiPrenotate.uri}?${request}`,
      {
        headers,
      },
    );
  }

  deleteSinglePosteId(request: RequestDeletePosteIdCertificate): Observable<{ data: any }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteSinglePosteId.uri}`, request);
  }

  deleteAllPosteId(request: RequestDeleteAllPosteIdCertificate): Observable<{ data: any }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteAllPosteId.uri}`, request);
  }

  deleteUserCompany(request: RequestGetUserDeviceData): Observable<{ data: any }> {
    return this.http.post<{
      data: {
        'command-success': boolean;
        'command-error-message': string;
      };
    }>(`${this.baseUrlPath}${this.apiUrls.deleteUserCompany.uri}`, request);
  }

  infoCardDetail(request: string, headers: HttpHeaders): Observable<GetByAlias> {
    return this.http.get<GetByAlias>(`${this.baseUrlPath}${this.apiUrls.getByAlias.uri}/${request}`, {
      headers,
    });
  }

  saleabilityCoverage(request: RequestSaleabilityCoverage): Observable<ResponseSaleabilityCoverage> {
    return this.http.post<ResponseSaleabilityCoverage>(
      `${this.baseUrlPath}${this.apiUrls.saleabilityCoverage.uri}`,
      request,
    );
  }

  topUpInfo(headers: HttpHeaders, info): Observable<InfoRicarica> {
       return this.http.get<InfoRicarica>(
      `${this.baseUrlPath}${this.apiUrls.topUpInfo.uri}?canale=${info.canale}&utente=${info.utente}&codiceDispositivo=${info.codiceDispositivo}&dataOraSaldo=${info.dataOraSaldo}`,
      {
        headers,
      },
    );
  }

  historyList(headers: HttpHeaders, info): Observable<StoriaDisponibile> {
    if (info.dataDa) {
           return this.http.get<StoriaDisponibile>(
        `${this.baseUrlPath}${this.apiUrls.balanceInfo.uri}?aliasCarta=${info.aliasCarta}&canale=${info.canale}&utente=${info.utente}&numOperazioni=${info.numOperazioni}&dataDa=${info.dataDa}&dataA=${info.dataA}`,
        {
          headers,
        },
      );
    }
       return this.http.get<StoriaDisponibile>(
      `${this.baseUrlPath}${this.apiUrls.balanceInfo.uri}?aliasCarta=${info.aliasCarta}&canale=${info.canale}&utente=${info.utente}&numOperazioni=${info.numOperazioni}`,
      {
        headers,
      },
    );
  }

  cardReplacement(request, headers): Observable<ResponseCardReplacement> {
    return this.http.post<ResponseCardReplacement>(`${this.baseUrlPath}${this.apiUrls.cardReplacement.uri}`, request, {
      headers,
    });
  }

  verifyCardReplacement(
    headers: any,
    utente: string,
    codiceDispositivo: string,
  ): Observable<ResponseVerifyCardReplacement> {
    return this.http.get<ResponseVerifyCardReplacement>(
      `${this.baseUrlPath}${this.apiUrls.verifyCardReplacement.uri}?canale=PIX&utente=${utente}&codiceDispositivo=${codiceDispositivo}`,
      {
        headers,
      },
    );
  }

  regeneratePin(request, headers: HttpHeaders): Observable<ResponseCardReplacement> {
    return this.http.post<ResponseCardReplacement>(`${this.baseUrlPath}${this.apiUrls.regeneratePin.uri}`, request, {
      headers,
    });
  }

  cardRenewal(request: RequestCardRenewal): Observable<ResponseCardRenewal> {
    return this.http.post<ResponseCardRenewal>(`${this.baseUrlPath}${this.apiUrls.cardRenewal.uri}`, request);
  }

  expiringCardList(request: RequestExpiringCardList): Observable<ResponseExpiringCardList> {
    return this.http.post<ResponseExpiringCardList>(`${this.baseUrlPath}${this.apiUrls.expiringCardList.uri}`, request);
  }

  getSaldoListaMovimenti(request: RequestInfoAccountDetail, headers?): Observable<InfoAccountDetail> {
    return this.http.post<InfoAccountDetail>(
      `${this.baseUrlPath}${this.apiUrls.getSaldoListaMovimenti.uri}`,
      request,
      headers ? { headers } : {},
    );
  }

  getSaldoListaCollegamenti(
    request: RequestSaldoListaCollegamenti,
    header,
  ): Observable<ResponseSaldoListaCollegamenti> {
    const headers = {
      requestId: 'PIX',
      correlationId: 'PIX',
      ...header,
    };
    return this.http.post<ResponseSaldoListaCollegamenti>(
      `${this.baseUrlPath}${this.apiUrls.getSaldoListaCollegamenti.uri}`,
      request,
      { headers },
    );
  }

  elencoDomiciliazioni(request: RequestDomiciliationList, headers: HttpHeaders): Observable<ResponseDomiciliationList> {
    return this.http.post<ResponseDomiciliationList>(
      `${this.baseUrlPath}${this.apiUrls.elencoDomiciliazioni.uri}`,
      request,
      {
        headers,
      },
    );
  }

  dettaglioDomiciliazione(
    request: RequestDomiciliationDetails,
    headers: HttpHeaders,
  ): Observable<ResponseDomiciliationDetails> {
    return this.http.post<ResponseDomiciliationDetails>(
      `${this.baseUrlPath}${this.apiUrls.dettaglioDomiciliazione.uri}`,
      request,
      {
        headers,
      },
    );
  }

  getUsersCompanyTaxcode(request: string): Observable<ResponseUsersCompanyByTaxcode> {
    return this.http.get<ResponseUsersCompanyByTaxcode>(
      `${this.baseUrlPath}${this.apiUrls.getUsersCompanyTaxcode.uri}${request}`,
    );
  }

  listaAziendeCreditrici(
    request: RequestListaAziendeCreditrici,
    headers: HttpHeaders,
  ): Observable<ResponseListaAziendeCreditrici> {
    return this.http.post<ResponseListaAziendeCreditrici>(
      `${this.baseUrlPath}${this.apiUrls.listaAziendeCreditrici.uri}`,
      request,
      {
        headers,
      },
    );
  }

  retrieveUserId(request: RequestRetrieveUserid): Observable<ResponseRetrieveUserid> {
    return this.http.post<ResponseRetrieveUserid>(`${this.baseUrlPath}${this.apiUrls.retrieveUserId.uri}`, request);
  }

  getActivitiesRsa(request: RequestGetActivitiesRsa): Observable<ResponseGetActivitiesRsa> {
    return this.http.post<ResponseGetActivitiesRsa>(`${this.baseUrlPath}${this.apiUrls.getActivitiesRsa.uri}`, request);
  }

  getFullVitalStatisticsByCf(
    request: RequestGetFullVitalStatisticsByCf,
  ): Observable<ResponseGetFullVitalStatisticsByCFResponse> {
    return this.http.post<ResponseGetFullVitalStatisticsByCFResponse>(
      `${this.baseUrlPath}${this.apiUrls.getFullVitalStatisticsByCf.uri}`,
      request,
    );
  }

  sendGeneratedIsee(request: RequestSendGeneratedIsee): Observable<ResponseSendGeneratedIsee> {
    const moduleType = `?riferimentoIsee=${request.moduleType}`;
    const referenceYears = `&riferimentoAnno=${request.referenceYears}`;
    const fiscalCode = `&codiceFiscale=${request.fiscalCode}`;
    const email = `&destinatario=${request.email}`;
    return this.http.get<ResponseSendGeneratedIsee>(
      `${this.baseUrlPath}${this.apiUrls.sendGeneratedIsee.uri}${moduleType}${referenceYears}${fiscalCode}${email}`,
    );
  }

  pacchiAnagrafica(request: RequestPacchiAnagrafica): Observable<ResponsePacchiAnagrafica> {
    return this.http.post<ResponsePacchiAnagrafica>(`${this.baseUrlPath}${this.apiUrls.pacchiAnagrafica.uri}`, request);
  }

  giacenze(request: RequestGiacenze): Observable<ResponseGiacenze> {
    return this.http.post<ResponseGiacenze>(`${this.baseUrlPath}${this.apiUrls.giacenze.uri}`, request);
  }

  reportRitiro(request: RequestReportRitiro): Observable<ResponseReportRitiri> {
    return this.http.post<ResponseReportRitiri>(`${this.baseUrlPath}${this.apiUrls.reportRitiro.uri}`, request);
  }

  elencoMateriali(request: RequestElencoMateriali): Observable<ResponseElencoMateriali> {
    return this.http.post<ResponseElencoMateriali>(`${this.baseUrlPath}${this.apiUrls.elencoMateriali.uri}`, request);
  }

  reportMateriali(request: RequestReportMateriali): Observable<ResponseReportMateriali> {
    return this.http.post<ResponseReportMateriali>(`${this.baseUrlPath}${this.apiUrls.reportMateriali.uri}`, request);
  }

  pacchiConsumo(request: RequestPacchiConsumo): Observable<ResponsePacchiConsumo> {
    return this.http.post<ResponsePacchiConsumo>(`${this.baseUrlPath}${this.apiUrls.pacchiConsumo.uri}`, request);
  }

  searchUserSpidProfile(request: SearchUserSpidProfileRequest): Observable<SearchUserSpidProfileResponse> {
    return this.http.post<SearchUserSpidProfileResponse>(
      `${this.baseUrlPath}${this.apiUrls.searchUserSpidProfile.uri}`,
      request,
    );
  }

  // rigenerazionePinWs1
  listaAziendeBpiol(request: RequestListaAziendeBpiol): Observable<ResponseListaAziendeBpiol> {
    return this.http.post<ResponseListaAziendeBpiol>(
      `${this.baseUrlPath}${this.apiUrls.listaAziendeBpiol.uri}`,
      request,
    );
  }

  // rigenerazionePinWs2
  rigenerazionePinBpiol(request: RequestRigenerazionePinBpiol): Observable<ResponseRigenerazionePinBpiol> {
    return this.http.post<ResponseRigenerazionePinBpiol>(
      `${this.baseUrlPath}${this.apiUrls.rigenerazionePinBpiol.uri}`,
      request,
    );
  }

  getCmbChannels(): Observable<ResponseCmbChannels> {
    return this.http.get<ResponseCmbChannels>(`${this.baseUrlPath}${this.apiUrls.getCmbChannels.uri}`);
  }

  getCmbServices(): Observable<ResponseCmbServices> {
    return this.http.get<ResponseCmbServices>(`${this.baseUrlPath}${this.apiUrls.getCmbServices.uri}`);
  }

  getCmbResults(request: RequestCmbResults): Observable<ResponseCmbResults> {
    return this.http.post<ResponseCmbResults>(`${this.baseUrlPath}${this.apiUrls.getCmbResults.uri}`, request);
  }

  getStructTerr(request: RequestGetStructTerr): Observable<ResponseGetStructTerr> {
    return this.http.post<ResponseGetStructTerr>(`${this.baseUrlPath}${this.apiUrls.getStructTerr.uri}`, request);
  }

  getDatiLdv(request: RequestGetDatiLdv): Observable<ResponseGetDatiLdv> {
    return this.http.post<ResponseGetDatiLdv>(`${this.baseUrlPath}${this.apiUrls.getDatiLdv.uri}`, request);
  }

  getAllGroups(request: RequestGetAllGroups): Observable<ResponseGetAllGroups> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetAllGroups>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getAllGroups.uri : this.apiUrls.getAllGroups.uri + queryParam
      }`,
      request,
    );
  }

  getDatiTipologiche(request: RequestGetDatiTipologiche): Observable<ResponseGetDatiTipologiche> {
    return this.http.post<ResponseGetDatiTipologiche>(
      `${this.baseUrlPath}${this.apiUrls.getDatiTipologiche.uri}`,
      request,
    );
  }

  getDatiFiliali(request: RequestGetDatiFiliali): Observable<ResponseGetDatiFiliali> {
    return this.http.post<ResponseGetDatiFiliali>(`${this.baseUrlPath}${this.apiUrls.getDatiFiliali.uri}`, request);
  }

  segnalazioniGroup(request: LeadBusinessRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.segnalazioniGroup.uri}`, request);
  }

  dettaglioPatrimonio(request: InvestmentRequest): Observable<InvestmentResponse> {
    const headers = {
      source: 'PIX',
      requestID: getUniqueId(),
    };
    return this.http.post<InvestmentResponse>(
      `${this.baseUrlPath}${this.apiUrls.dettaglioPatrimonio.uri}`,
      request.PatrimonioClienteInput,
      { headers },
    );
  }

  callMeBackTimeslot(request: RequestCallMeBackTimeSlot): Observable<ResponseCallMeBackTimeSlot> {
    return this.http.post<ResponseCallMeBackTimeSlot>(
      `${this.baseUrlPath}${this.apiUrls.callMeBackTimeSlot.uri}`,
      request,
    );
  }

  callMeBackAdd(request: RequestCallMeBackAdd): Observable<ResponseCallMeBackAdd> {
    return this.http.post<ResponseCallMeBackAdd>(`${this.baseUrlPath}${this.apiUrls.callMeBackAdd.uri}`, request);
  }

  getOrderObjFact(request: RequestGetOrderObjFact): Observable<ResponseGetOrderObjFact> {
    return this.http
      .post<ResponseGetOrderObjFact>(`${this.baseUrlPath}${this.apiUrls.getOrderOggFatt.uri}`, request)
      .pipe(
        switchMap(res => {
          const error = res?.errors?.[0];
          if (res?.errors?.length && error?.code) {
            return throwError(new Error(`${error.code} - ${error.title}`));
          }
          return of(res);
        }),
      );
  }

  getStagFormOl(request: RequestGetStagFormOl): Observable<ResponseGetStagFormOl> {
    return this.http.post<ResponseGetStagFormOl>(`${this.baseUrlPath}${this.apiUrls.getStagFormOl.uri}`, request);
  }

  checkCodOggFatt(request: RequestCheckCodOggFatt): Observable<ResponseCheckCodOggFatt> {
    return this.http.post<ResponseCheckCodOggFatt>(`${this.baseUrlPath}${this.apiUrls.checkCodOggFatt.uri}`, request);
  }

  getHourTimeSpans(): Observable<ResponseGetHourTimeSpans> {
    return this.http.get<ResponseGetHourTimeSpans>(`${this.baseUrlPath}${this.apiUrls.getHourTimeSpan.uri}`);
  }

  getOrderRicOp(request: RequestGetOrderRicOp): Observable<ResponseGetOrderRicOp> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetOrderRicOp>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getOrderRicOp.uri : this.apiUrls.getOrderRicOp.uri + queryParam
      }`,
      request,
    );
  }

  getOrderRicOpAsinc(request: RequestGetOrderRicOpAsinc): Observable<AsincApiResponse> {
    if (this.isPostevita) {
      request.ambito = 'PV';
    }
    return this.http.post<AsincApiResponse>(
      `${this.baseUrlPath}${this.apiUrls.getOrderRicOpAsinc.uri}${
        this.isPostevita ? RequestFields.sap_client_header : ''
      }`,
      request,
    );
  }

  getSelectOperations(request: RequestSelectOperations): Observable<RetrieveOperationsSelect> {
    const queryParam = RequestFields.sap_client_header;
    const url = `${this.apiUrls.getSelectOperations.uri}${this.isPostevita ? queryParam : ''}`;
    return this.http.post<RetrieveOperationsSelect>(`${this.baseUrlPath}${url}`, request);
  }

  findEnnuple(request: RequestFindEnnuple): Observable<ResponseFindEnnuple> {
    return this.http.post<ResponseFindEnnuple>(`${this.baseUrlPath}${this.apiUrls.findEnnuple.uri}`, request);
  }

  getResponsibleEmployee(request: RequestResponsibleEmployee): Observable<ResponseResponsibleEmployee> {
    return this.http.post<ResponseResponsibleEmployee>(
      `${this.baseUrlPath}${this.apiUrls.getResponsibleEmployee.uri}`,
      request,
    );
  }

  practicesRecovery(request: RequestPracticesRecovery): Observable<ResponsePracticesRecovery> {
    return this.http.post<ResponsePracticesRecovery>(
      `${this.baseUrlPath}${this.apiUrls.practicesRecovery.uri}`,
      request,
    );
  }

  practiceDetailRecovery(request: RequestPracticeDetailRecovery): Observable<ResponsePracticeDetailRecovery> {
    return this.http.post<ResponsePracticeDetailRecovery>(
      `${this.baseUrlPath}${this.apiUrls.practiceDetailRecovery.uri}`,
      request,
    );
  }

  getDocumentSd(request: RequestGetDocumentSd): Observable<ResponseGetDocumentSd> {
    return this.http.post<ResponseGetDocumentSd>(`${this.baseUrlPath}${this.apiUrls.getDocumentSd.uri}`, request);
  }

  getAnagraficaClienteContratti(
    request: RequestGetAnagraficaClienteContratti,
  ): Observable<ResponseGetAnagraficaClienteContratti> {
    return this.http.post<ResponseGetAnagraficaClienteContratti>(
      `${this.baseUrlPath}${this.apiUrls.getAnagraficaClienteContratti.uri}`,
      request,
    );
  }

  getConfAttrNrg(request: RequestGetConfAttrNrg): Observable<ResponseGetConfAttrNrg> {
    return this.http.post<ResponseGetConfAttrNrg>(`${this.baseUrlPath}${this.apiUrls.getConfAttrNrg.uri}`, request);
  }

  getFunnelDetail(id: string): Observable<ResponseGetFunnelDetail> {
    return this.http.get<ResponseGetFunnelDetail>(`${this.baseUrlPath}${this.apiUrls.getFunnelDetail.uri}/${id}`);
  }

  customerAsset(request: RequestCustomerAsset, headers?): Observable<ResponseCustomerAssets> {
    return this.http.post<ResponseCustomerAssets>(`${this.baseUrlPath}${this.apiUrls.customerAsset.uri}`, request, {
      headers,
    });
  }

  getPreferences(request, headers): Observable<ResponseGetPreferences> {
    return this.http.post<ResponseGetPreferences>(`${this.baseUrlPath}${this.apiUrls.getPreferences.uri}`, {
      headers,
    });
  }

  revokePostePlus(headers): Observable<ResponseRevokePostePlus> {
    return this.http.delete<ResponseRevokePostePlus>(`${this.baseUrlPath}${this.apiUrls.revokePostePlus.uri}`, {
      headers,
    });
  }

  recuperaInfoPacchi(request, headers): Observable<ResponseRecuperaInfoPacchi> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.recuperaInfoPacchi.uri}`, request, { headers });
  }

  saveAddressUpdated(request: Partial<AddressesModel>, headers): Observable<AddressesResponseWrapper> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.recuperaIndirizzoModificato.uri}`, request, {
      headers,
    });
  }

  setAddresses(request, headers): Observable<ResponseSetAddresses> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.setAddresses.uri}`, request, { headers });
  }

  zcaRecessoInesitate(request: RequestZcaRecessoInesitate): Observable<ResponseZcaRecessoInesitate> {
    return this.http.post<ResponseZcaRecessoInesitate>(
      `${this.baseUrlPath}${this.apiUrls.zcaRecessoInesitate.uri}`,
      request,
    );
  }

  findsByPartitaIva(data: string, headers: HttpHeaders): Observable<ResponseFindsByCF> {
    return this.http.get<ResponseFindsByCF>(
      `${this.baseUrlPath}${this.apiUrls.findsByPartitaIva.uri}?${data}&numeroCarte=100`,
      {
        headers,
      },
    );
  }

  requestPukReissue(cf: string, request: RequestPukReissue): Observable<ResponsePukReissue> {
    return this.http.post<ResponsePukReissue>(
      `${this.baseUrlPath}${this.apiUrls.requestPukReissue.uri}/${cf}`,
      request,
    );
  }

  getPukReissue(fiscalCode: string): Observable<ResponseGetPukReissue> {
    return this.http.post<ResponseGetPukReissue>(
      `${this.baseUrlPath}${this.apiUrls.getInfoPukReissue.uri}/${fiscalCode}`,
      {},
    );
  }

  getPukReissueRequestList(cf: string): Observable<ResponsePukReissueRequestList> {
    return this.http.get<ResponsePukReissueRequestList>(
      `${this.baseUrlPath}${this.apiUrls.getPukReissueRequestList.uri}/${cf}`,
    );
  }

  getIncomingIStTransfers(keys: string, headers?): Observable<ResponseIstTransfer> {
    return this.http.get<ResponseIstTransfer>(
      `${this.baseUrlPath}${this.apiUrls.getIncomingIStTransfers.uri}${keys}`,
      headers ? { headers } : {},
    );
  }

  getVerifyEnabledIstTransfer(request: RequestVerifyEnabledIstTransfer): Observable<ResponseVerifyEnabledIstTransfer> {
    return this.http.post<ResponseVerifyEnabledIstTransfer>(
      `${this.baseUrlPath}${this.apiUrls.getVerifyEnabledIstTransfer.uri}`,
      request,
    );
  }

  getHandleIStTransfer(request: RequestHandleIStTransfer, headers: HttpHeaders): Observable<ResponseHandleIStTransfer> {
    return this.http.post<ResponseHandleIStTransfer>(
      `${this.baseUrlPath}${this.apiUrls.getHandleIStTransfer.uri}`,
      request,
      {
        headers,
      },
    );
  }

  getVerifyIban(request: RequestVerifyIban): Observable<ResponseVerifyIban> {
    return this.http.post<ResponseVerifyIban>(`${this.baseUrlPath}${this.apiUrls.getVerifyIban.uri}`, request);
  }

  retrieveCard(request: RequestRetrieveCard, headers?): Observable<ResponseRetrieveCard> {
    request = {
      ...request,
      accountId: aliasLength(request.accountId),
    };
    return this.http.post<ResponseRetrieveCard>(
      `${this.baseUrlPath}${this.apiUrls.retrieveCard.uri}`,
      request,
      headers ? { headers } : {},
    );
  }

   practiceDetail(
    request: RequestPracticeDetailEnergy,
    frazionario: string = null,
    operatorId: string = null,
  ): Observable<ResponsePracticeDetailEnergy> {
    const headers = this.isComingFromNFEA
      ? new HttpHeaders({
          FRAZIONARIO: frazionario,
          USERID: operatorId,
        })
      : {};
    return this.http.post<ResponsePracticeDetailEnergy>(
      `${this.baseUrlPath}${this.apiUrls.practiceDetail.uri}`,
      request,
      { headers },
    );
  }

  createAttachment(
    request: RequestCreateAttachment,
    frazionario: string = null,
    operatorId: string = null,
  ): Observable<ResponseCreateAttachment> {
    const headers = this.isComingFromNFEA
      ? new HttpHeaders({
          FRAZIONARIO: frazionario,
          USERID: operatorId,
        })
      : {};
    if (Boolean(request.attachmentData.id) && request.attachmentData.id !== '-') {
      return this.http.post<ResponseCreateAttachment>(
        `${this.baseUrlPath}${this.apiUrls.createAttachment.uri}`,
        request,
        { headers },
      );
    } else {
      return of(null);
    }
  }

  // fornitureDetail(request: RequestFornitureDetail, frazionario: string = null, operatorId: string = null)

  searchDocumentsArchivioUnico(
    request: RequestSearchDocument,
    hasMetadata = false,
  ): Observable<ResponseSearchDocuments> {
    const headers = new HttpHeaders({
      requester: 'PIX',
    });
    return this.http.post<ResponseSearchDocuments>(
      `${this.baseUrlPath}${this.apiUrls.searchDocumentsArchivioUnico.uri}${
        hasMetadata ? '?metadata=true&index=true ' : ''
      }`,
      request,
      { headers },
    );
  }

  elencoServiziBpiol(request: ElencoServiziBpiolRequest): Observable<ElencoServiziBpiolResponse> {
    return this.http.post<ElencoServiziBpiolResponse>(
      `${this.baseUrlPath}${this.apiUrls.elencoServiziBpiol.uri}`,
      request,
    );
  }

  getBusinessAgreements(request: RequestGetBusinessAgreements): Observable<ResponseGetBusinessAgreements> {
    return this.http.post<ResponseGetBusinessAgreements>(
      `${this.baseUrlPath}${this.apiUrls.getBusinessAgreements.uri}`,
      request,
    );
  }

  getEnergyMandates(request: RequestGetEnergyMandates): Observable<ResponseGetEnergyMandates> {
    return this.http.post<ResponseGetEnergyMandates>(
      `${this.baseUrlPath}${this.apiUrls.getEnergyMandates.uri}`,
      request,
    );
  }

  getCustomerBusinessAgreements(
    customerIdAUC: string,
    businessAgreementId: string,
    frazionario: string = null,
    operatorId: string = null,
  ): Observable<ResponseGetCustomerBusinessAgreements> {
    let headers;
    if (!this.isComingFromNFEA) {
      headers = {
        'PI-user-id': 'NO_USERID',
      };
    } else {
      headers = new HttpHeaders({
        'PI-user-id': 'NO_USERID',
        FRAZIONARIO: frazionario,
        USERID: operatorId,
      });
    }
    return this.http.get<ResponseGetCustomerBusinessAgreements>(
      `${this.baseUrlPath}${this.apiUrls.getCustomerBusinessAgreements.uri.replace(
        '%idAuc%',
        customerIdAUC,
      )}?businessAgreementId=${businessAgreementId}`,
      { headers },
    );
  }

  getOneView(request: RequestGetOneView): Observable<ResponseGetOneView> {
    return this.http.post<ResponseGetOneView>(`${this.baseUrlPath}${this.apiUrls.getOneView.uri}`, request);
  }

  getCustomerContractFull(customerId: string, contractId: string): Observable<ResponseGetCustomerContractFull> {
    const uri = this.apiUrls.getCustomerContractFull.uri
      .replace(':customerId', customerId)
      .replace(':contractId', contractId);
    return this.http.get<ResponseGetCustomerContractFull>(`${this.baseUrlPath}${uri}`);
  }

  hubElectDistribution(dstCdGestRete: string, dstCap: string): Observable<{ data: ResponseHubDistribution }> {
       return this.http.post<{
      data: ResponseHubDistribution;
    }>(`${this.baseUrlPath}${this.apiUrls.hubElecDistribution.uri}`, { dstCdGestRete, dstCap });
  }

  hubGasDistribution(dstCdGestRete: string, dstCap: string): Observable<{ data: ResponseHubDistribution }> {
       return this.http.post<{
      data: ResponseHubDistribution;
    }>(`${this.baseUrlPath}${this.apiUrls.hubGasDistribution.uri}`, { dstCdGestRete, dstCap });
  }

  getQuotes(idAuc: string): Observable<ResponseGetQuotes> {
    return this.http.post<ResponseGetQuotes>(`${this.baseUrlPath}${this.apiUrls.quotes.uri}`, { customer: { idAuc } });
  }

  getHeaderPreventivo(
    request: RequestGetHeaderPreventivo,
    timestamp,
    interactionId,
  ): Observable<ResponseGetHeaderPreventivo> {
    const headers = {
      'Content-Encoding': 'identity',
      Accept: 'application/json',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'Contact Center',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'FEU',
      'PI-CompanyName': 'PI',
      'PI-ProcessCode': 'ATT01',
    };
    return this.http.post<ResponseGetHeaderPreventivo>(
      `${this.baseUrlPath}${this.apiUrls.getHeaderPreventivo.uri}`,
      request,
      { headers },
    );
  }

  getMandatesDetail(
    code: string,
    frazionario: string = null,
    operatorId: string = null,
  ): Observable<ResponseGetMandatesDetail> {
    const headers = this.isComingFromNFEA
      ? new HttpHeaders({
          FRAZIONARIO: frazionario,
          USERID: operatorId,
        })
      : {};
    return this.http.get<ResponseGetMandatesDetail>(
      `${this.baseUrlPath}${this.apiUrls.getMandatesDetail.uri}/${code}`,
      { headers },
    );
  }

  getContractsFullData(headers: HttpHeaders, params: string): Observable<ResponseGetContractsFullData> {
    return this.http.get<ResponseGetContractsFullData>(
      `${this.baseUrlPath}${this.apiUrls.getContractsFullData.uri}${params}`,
      {
        headers,
      },
    );
  }

  getContractsUserRoles(headers: HttpHeaders, params: string): Observable<ResponseGetContractsUserRoles> {
    return this.http.get<ResponseGetContractsUserRoles>(
      `${this.baseUrlPath}${this.apiUrls.getContractsUserRoles.uri}${params}`,
      {
        headers,
      },
    );
  }

  getInfoAccountCliente(request: RequestInfoAccountCliente, headers?): Observable<ResponseInfoAccountCliente> {
    return this.http.post<ResponseInfoAccountCliente>(
      `${this.baseUrlPath}${this.apiUrls.getInfoAccountCliente.uri}`,
      request,
      headers ? { headers } : {},
    );
  }

  gestioneCruscottoCampagne(): Observable<ResponseGestioneCruscottoCampagne> {
    return this.http.get<ResponseGestioneCruscottoCampagne>(
      `${this.baseUrlPath}${this.apiUrls.gestioneCruscottoCampagne.uri}`,
    );
  }

  gestioneCruscottoConfigurazioneCampagna(
    params: RequestGestioneCruscottoConfigurazioneCampagna,
  ): Observable<ResponseGestioneCruscottoConfigurazioneCampagna> {
    return this.http.post<ResponseGestioneCruscottoConfigurazioneCampagna>(
      `${this.baseUrlPath}${this.apiUrls.gestioneCruscottoConfigurazioneCampagna.uri}`,
      params,
    );
  }

  gestioneCruscottoAttivazioneCampagna(
    params: RequestGestioneCruscottoAttivazioneCampagna,
  ): Observable<ResponseGestioneCruscottoAttivazioneCampagna> {
    return this.http.post<ResponseGestioneCruscottoAttivazioneCampagna>(
      `${this.baseUrlPath}${this.apiUrls.gestioneCruscottoAttivazioneCampagna.uri}`,
      params,
    );
  }

  gestioneCruscottoDettagliContatti(
    params: RequestGestioneCruscottoDettagliCampagna,
  ): Observable<ResponseGestioneCruscottoDettagliContatti> {
    return this.http.post<ResponseGestioneCruscottoDettagliContatti>(
      `${this.baseUrlPath}${this.apiUrls.gestioneCruscottoDettagliContatti.uri}`,
      params,
    );
  }

  getInfoPolicyDetailPA(request: RequestInfoPolicyDetailPa): Observable<ResponseInfoPolicyDetailPa> {
    return this.http.post<ResponseInfoPolicyDetailPa>(
      `${this.baseUrlPath}${this.apiUrls.infoPolicyDetailPA.uri}`,
      request,
    );
  }

  getInfoPolicyDetailPV(request: RequestInfoPolicyDetailPv): Observable<ResponseInfoPolicyDetailPv> {
    return this.http.post<ResponseInfoPolicyDetailPv>(
      `${this.baseUrlPath}${this.apiUrls.infoPolicyDetailPV.uri}`,
      request,
    );
  }

  gestioneCruscottoDeleteContatti(
    params: RequestGestioneCruscottoDeleteContatti,
  ): Observable<ResponseGestioneCruscottoDeleteContatti> {
    return this.http.post<ResponseGestioneCruscottoDeleteContatti>(
      `${this.baseUrlPath}${this.apiUrls.gestioneCruscottoDeleteContatti.uri}`,
      params,
    );
  }

  getPosidoniaDetail(funnelId: number, origin: string): Observable<ResponseGetPosidoniaDetail> {
    return this.http.get<ResponseGetPosidoniaDetail>(
      `${this.baseUrlPath}${this.apiUrls.getPosidoniaDetail.uri}/${funnelId}?origin=${origin}`,
    );
  }

  getFunnelsFid(id: number, origin?: string, headers?): Observable<any> {
    const originParam: string = origin ? origin : 'on-prem';
    return this.http.get<any>(
      `${this.baseUrlPath}${this.apiUrls.getFunnelsFid.uri}/${id}?origin=${originParam}`,
      headers ? { headers } : {},
    );
  }

  getContract360(request: RequestGetContract360): Observable<ResponseGetContract360> {
    return this.http.post<ResponseGetContract360>(`${this.baseUrlPath}${this.apiUrls.getContract360.uri}`, request);
  }

  getUserRolesByCf(request: RequestGetUserRolesByCf): Observable<ResponseGetUserRolesByCf> {
    return this.http.post<ResponseGetUserRolesByCf>(`${this.baseUrlPath}${this.apiUrls.getUserRolesByCf.uri}`, request);
  }

  verificaOperativitaOlAndTelSec(request: RequestOperativitaTelsec): Observable<ResponseOperativitaTelsec> {
    return this.http.post<ResponseOperativitaTelsec>(
      `${this.baseUrlPath}${this.apiUrls.verificaOperativitaOlAndTelSec.uri}`,
      request,
    );
  }

  recuperaAltreInfoCliente(
    request: RequestAltreInfoContoCliente,
    headers: HttpHeaders,
  ): Observable<ResponseAltreInfoContoCliente> {
    return this.http.post<ResponseAltreInfoContoCliente>(
      `${this.baseUrlPath}${this.apiUrls.recuperaAltreInfoContoCliente.uri}`,
      request,
      {
        headers,
      },
    );
  }

  getPracticeDetailFeu41(
    request: RequestPracticeDetailFeu41,
    headers: HttpHeaders,
  ): Observable<ResponsePracticeDetailFeu41> {
    return this.http.post<ResponsePracticeDetailFeu41>(
      `${this.baseUrlPath}${this.apiUrls.recuperaDettaglioPratica.uri}`,
      request,
      {
        headers,
      },
    );
  }

   getEditPaymentMethodFeu218(
    request: RequestEditPaymentMethodFeu218,
    headers: HttpHeaders,
    isInitPractices = false,
  ): Observable<ResponseEditPaymentMethodFeu218> {
    const apiUrls = isInitPractices
      ? this.apiUrls.anyToFeuInitPratiche
      : this.apiUrls.recuperaVariazioneMetodoDiPagamento;
    return this.http.post<ResponseEditPaymentMethodFeu218>(`${this.baseUrlPath}${apiUrls.uri}`, request, {
      headers,
    });
  }

  supplyDetail(
    request: RequestSupplyDetail,
    fractional: string = null,
    operatorId: string = null,
  ): Observable<ResponseSupplyDetail> {
    const headers = this.isComingFromNFEA
      ? new HttpHeaders({
          FRAZIONARIO: fractional,
          USERID: operatorId,
        })
      : {};
    return this.http.post<ResponseSupplyDetail>(`${this.baseUrlPath}${this.apiUrls.supplyDetail.uri}`, request, {
      headers,
    });
  }

  fileLoader(request, headers): Observable<any> {
    return this.http.post<ResponseCallMeBackTimeSlot>(`${this.baseUrlPath}${this.apiUrls.fileLoader.uri}`, request, {
      headers,
    });
  }

  getInvoice360(request: RequestGetInvoice360): Observable<ResponseGetInvoice360> {
    return this.http.post<ResponseGetInvoice360>(`${this.baseUrlPath}${this.apiUrls.getInvoice360.uri}`, request);
  }

  getInvoiceDetail(request: RequestInvoiceDetail, interactionId, timestamp): Observable<ResponseInvoiceDetail> {
    const headers = {
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseInvoiceDetail>(`${this.baseUrlPath}${this.apiUrls.getInvocieDetail.uri}`, request, {
      headers,
    });
  }

  retrievesUserCredentials(request: RetrievesUserCredentialsRequest): Observable<RetrievesUserCredentialsResponse> {
    return this.http.post<RetrievesUserCredentialsResponse>(
      `${this.baseUrlPath}${this.apiUrls.retrievesUserCredentials.uri}`,
      request,
    );
  }

  resetPasswordUser(request: ResetPasswordUserRequest): Observable<ResetPasswordUserResponse> {
    return this.http.post<ResetPasswordUserResponse>(
      `${this.baseUrlPath}${this.apiUrls.resetPasswordUser.uri}`,
      request,
    );
  }

  sendDossierMail(request: SendDossierMailRequest): Observable<SendDossierMailResponse> {
    return this.http.post<SendDossierMailResponse>(`${this.baseUrlPath}${this.apiUrls.sendDossierMail.uri}`, request);
  }

  searchUserSpidAndProfileV2(request: SearchUserAndProfileV2Request): Observable<SearchUserAndProfileV2Response> {
    return this.http.post<SearchUserAndProfileV2Response>(
      `${this.baseUrlPath}${this.apiUrls.searchUserSpidAndProfileV2.uri}`,
      request,
    );
  }

  readContractSpid(request: ReadContractSpidRequest): Observable<ReadContractSpidResponse> {
    return this.http.post<ReadContractSpidResponse>(`${this.baseUrlPath}${this.apiUrls.readContractSpid.uri}`, request);
  }

  generateDeactivationCode(request: GenerateDeactivationCodeRequest): Observable<GenerateDeactivationCodeResponse> {
    return this.http.post<GenerateDeactivationCodeResponse>(
      `${this.baseUrlPath}${this.apiUrls.generateDeactivationCode.uri}`,
      request,
    );
  }

  userHistory(request: UserHistoryRequest): Observable<UserHistoryResponse> {
    return this.http.post<UserHistoryResponse>(`${this.baseUrlPath}${this.apiUrls.userHistory.uri}`, request);
  }

  updateUserHistory(request: UpdateUserHistoryRequest): Observable<UpdateUserHistoryResponse> {
    return this.http.post<UpdateUserHistoryResponse>(
      `${this.baseUrlPath}${this.apiUrls.updateUserHistory.uri}`,
      request,
    );
  }

  recuperoCredenzialiUtente(request: RequestRecuperoCredenzialiUtente): Observable<ResponseRecuperoCredenzialiUtente> {
    return this.http.post<ResponseRecuperoCredenzialiUtente>(
      `${this.baseUrlPath}${this.apiUrls.recuperoCredenzialiUtente.uri}`,
      request,
    );
  }

  sendSms(request: SendSmsRequest): Observable<ResponseSendSmsResponse> {
    return this.http.post<ResponseSendSmsResponse>(`${this.baseUrlPath}${this.apiUrls.sendSms.uri}`, request);
  }

  sendResetPassword(request: SendResetPasswordRequest): Observable<ResponseSendResetPasswordResponse> {
    return this.http.post<ResponseSendResetPasswordResponse>(
      `${this.baseUrlPath}${this.apiUrls.sendResetPassword.uri}`,
      request,
    );
  }

  prospectChangePassword(
    request: RequestProspectChangePasswordRequest,
  ): Observable<ResponseProspectChangePasswordResponse> {
    return this.http.post<ResponseProspectChangePasswordResponse>(
      `${this.baseUrlPath}${this.apiUrls.prospectChangePassword.uri}`,
      request,
    );
  }

  enableUser(request: RequestEnableUser): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.enableUser.uri}`, request);
  }

  modifyContacts(request: RequestModifyContacts): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.modifyContacts.uri}`, request);
  }

  getDocumentBase64(url: string, isComingFromNFEA = false): Observable<ResponseGetDocumentBase64> {
    const baseUrl = isComingFromNFEA ? this.urlProxy : this.baseUrl;
    return this.http.post<ResponseGetDocumentBase64>(`${baseUrl}${this.apiUrls.getDocumentBase64.uri}`, { url });
  }

  getDossierFull(dossierId: string, headers = null): Observable<{ data: DossierCrmu }> {
    if (this.isComingFromNFEA && !headers) {
      this.facadeBiscottiera.energy.getCookies$.subscribe(res => {
        headers = {
          FRAZIONARIO: res.frazionario,
          USERID: res.operatorId,
        };
      });
    }
    return this.http.post<{
      data: DossierCrmu;
    }>(
      `${this.baseUrlPath}${this.apiUrls.getDossierFull.uri}${dossierId}`,
      {},
      {
        ...(this.isComingFromNFEA && Boolean(headers) ? { headers } : {}),
      },
    );
  }

  getAssetHeader(queryParams: string, headers = null): Observable<ResponseGetAssetHeader> {
    return this.http.get<ResponseGetAssetHeader>(
      `${this.baseUrlPath}${this.apiUrls.getAssetHeader.uri}${queryParams}`,
      {
        headers,
      },
    );
  }

  getConsensi(request: RequestGetConsensi): Observable<ResponseGetConsensi> {
    return this.http.post<ResponseGetConsensi>(`${this.baseUrlPath}${this.apiUrls.getConsensi.uri}`, request);
  }

  getCockpit(request: RequestGetCockpit): Observable<ResponseGetCockpit> {
    return this.http.post<ResponseGetCockpit>(`${this.baseUrlPath}${this.apiUrls.getCockpit.uri}`, request);
  }

  getCollegamenti(request: Partial<RequestCollegamenti>): Observable<ResponseCollegamenti> {
    return this.http.post<ResponseCollegamenti>(`${this.baseUrlPath}${this.apiUrls.getCollegamenti.uri}`, request);
  }

  listaStepTandem(request: RequestListaStepTandem): Observable<ResponseListaStepTandem> {
    return this.http.post<ResponseListaStepTandem>(`${this.baseUrlPath}${this.apiUrls.listaStepTandem.uri}`, request);
  }

  getReadings(request: RequestReadings, interactionId, timestamp): Observable<ResponseReadings> {
    const headers = {
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseReadings>(`${this.baseUrlPath}${this.apiUrls.getLetture.uri}`, request, { headers });
  }

   getListaRapportiFiglioV2(
    fiscalCode: string,
    id: string,
    operatorId: string,
    timestamp: string,
  ): Observable<GetListaRapportiFiglioV2Response> {
    const headers = {
      requestId: `PIX-${id}`,
      requestTime: timestamp,
      channelId: 'RPOL',
      userId: operatorId,
      stationId: 'contact center',
      ip: '0.0.0.0',
    };
    return this.http.get<GetListaRapportiFiglioV2Response>(
      `${this.baseUrlPath}${this.apiUrls.getListaRapportiFiglioV2.uri}/${fiscalCode}`,
      { headers },
    );
  }

  getIban(request: GetIbanRequest, timeStamp): Observable<GetIbanResponse> {
    const headers = {
      request_id_CBI_rest: `WEB_GESTIBAN_${timeStamp}`,
      request_id_LEGACY: getUniqueId(),
      correlation_id_POSTE_rest: getUniqueId(),
    };

    return this.http.post<GetIbanResponse>(`${this.baseUrlPath}${this.apiUrls.getIban.uri}`, request, { headers });
  }

  getAllReadings(request: RequestGetAllReadings, interactionId, timestamp): Observable<ResponseGetAllReadings> {
    const headers = {
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseGetAllReadings>(`${this.baseUrlPath}${this.apiUrls.getAllLetture.uri}`, request, {
      headers,
    });
  }

  infoContoOffloading(accountNumber: string, header): any {
    const headers = {
      correlationId: getUniqueId(),
      requestId: getUniqueId(),
      ...header,
    };
    return this.http.get<any>(`${this.baseUrlPath}${this.apiUrls.infoContoOffloading.uri}${accountNumber}`, {
      headers,
    });
  }

  getTitoli(request: RequestGetTitoli): Observable<ResponseGetTitoli> {
    return this.http.post<ResponseGetTitoli>(`${this.baseUrlPath}${this.apiUrls.getTitoli.uri}`, request);
  }

  getValoriPerRapporto(request: RequestGetValoriPerRapporto): Observable<ResponseGetValoriPerRapporto> {
    const headers = {
      correlationId: getUniqueId(),
      requestId: getUniqueId(),
    };
    return this.http.post<ResponseGetValoriPerRapporto>(
      `${this.baseUrlPath}${this.apiUrls.condizioniere.uri}`,
      request,
      { headers },
    );
  }

  getAppBusinessTerminals(request: RequestGetAppBusinessTerminals): Observable<ResponseGetAppBusinessTerminals> {
    return this.http.post<ResponseGetAppBusinessTerminals>(
      `${this.baseUrlPath}${this.apiUrls.getAppBusinessTerminals.uri}`,
      request,
    );
  }

  getAppBusinessCompaniesDetails(request: RequestCompaniesDetails): Observable<ResponseCompaniesDetails> {
    return this.http.post<ResponseCompaniesDetails>(
      `${this.baseUrlPath}${this.apiUrls.getAppBusinessCompaniesDetails.uri}`,
      request,
    );
  }

  getAppBusinessSubgroup(request: GetSubgroupsRequest): Observable<GetSubgroupsProductResponse> {
    return this.http.post<GetSubgroupsProductResponse>(
      `${this.baseUrlPath}${this.apiUrls.getAppBusinessSubgroup.uri}`,
      request,
    );
  }

  getAppBusinessMerchantDetails(request: RequestMerchantDetails): Observable<ResponseMerchantDetails> {
    return this.http.post<ResponseMerchantDetails>(
      `${this.baseUrlPath}${this.apiUrls.getAppBusinessMerchantDetails.uri}`,
      request,
    );
  }

  getAppBusinessShopAndTerminal(request: RequestShopAndTerminal): Observable<ResponseShopAndTerminal> {
    return this.http.post<ResponseShopAndTerminal>(
      `${this.baseUrlPath}${this.apiUrls.getAppBusinessShopAndTerminal.uri}`,
      request,
    );
  }

  getAppBusinessSalePoints(paginationURI: string): Observable<LoadSubGroupGETOutputResponse> {
    return this.http.get<LoadSubGroupGETOutputResponse>(`${paginationURI}`);
  }

  paginationUpdate<T>(paginationURI: string): Observable<T> {
    return this.http.get<T>(`${paginationURI}`);
  }

  getCustomerSalesPointAsset(
    request: RequestGetCustomerSalesPointAsset,
  ): Observable<ResponseGetCustomerSalesPointAsset> {
    return this.http.post<ResponseGetCustomerSalesPointAsset>(
      `${this.baseUrlPath}${this.apiUrls.getCustomerSalesPointAsset.uri}`,
      request,
    );
  }

  configurationStatus(termiId: string): Observable<ResponseConfigurationStatus> {
    return this.http.get<ResponseConfigurationStatus>(
      `${this.baseUrlPath}${this.apiUrls.configurationStatus.uri}/${termiId}`,
      {},
    );
  }

  getFlagGruppi(request): Observable<any> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.post<ResponseGetTicketChangeHistory>(
      `${this.baseUrlPath}${
        !this.isPostevita ? this.apiUrls.getFlagGruppi.uri : this.apiUrls.getFlagGruppi.uri + queryParam
      }`,
      request,
    );
  }

  getCreditProductSupport(taxcode: string, vatCode?: string): Observable<GetCreditProductSupport> {
    const vatCodePresent = vatCode ? `?vatCode=${vatCode}` : '';
    const headers = {
      'x-requestId': getUniqueId(),
    };
    return this.http.get<GetCreditProductSupport>(
      `${this.baseUrlPath}${this.apiUrls.getCreditProductSupport.uri}/${taxcode}${vatCodePresent}`,
      { headers },
    );
  }

  getCalculateRedemptionValue(request: CalculateRedemptionValueRequest): Observable<CalculateRedemptionValueResponse> {
    return this.http.post<CalculateRedemptionValueResponse>(
      `${this.baseUrlPath}${this.apiUrls.calculateRedemptionValue.uri}`,
      request,
    );
  }

  getAssociatedProducts(assetFullId: string, assetId: string, header): Observable<ResponseGetAssociatedProducts> {
    const headers = {
      ...header,
      requester: 'PIX',
    };
    return this.http.get<ResponseGetAssociatedProducts>(
      `${this.baseUrlPath}${this.apiUrls.getAssociatedProducts.uri}/${assetFullId}/${assetId}?size=100`,
      { headers },
    );
  }

  customerGeopost(request: RequestCustomerGeopost): Observable<ResponseCustomerGeopost> {
    return this.http.post<ResponseCustomerGeopost>(`${this.baseUrlPath}${this.apiUrls.customerGeopost.uri}`, request);
  }

  searchShipments(request: RequestSearchShipments): Observable<ResponseSearchShipments> {
    return this.http.post<ResponseSearchShipments>(`${this.baseUrlPath}${this.apiUrls.searchShipments.uri}`, request);
  }

  acceptanceClientWs(request: RequestAcceptanceClientWs): Observable<ResponseAcceptanceClientWs> {
    return this.http.post<ResponseAcceptanceClientWs>(
      `${this.baseUrlPath}${this.apiUrls.acceptanceClientWs.uri}`,
      request,
    );
  }

  sdaDeliveryDocument(request: RequestSdaDeliveryDocument): Observable<ResponseSdaDeliveryDocument> {
    return this.http.post<ResponseSdaDeliveryDocument>(
      `${this.baseUrlPath}${this.apiUrls.sdaDeliveryDocument.uri}`,
      request,
    );
  }

  getBlockScaDetail(request: RequestGetBlockScaDetail): Observable<ResponseGetBlockScaDetail> {
    return this.http.post<ResponseGetBlockScaDetail>(
      `${this.baseUrlPath}${this.apiUrls.getBlockScaDetail.uri}`,
      request,
    );
  }

  getComunicazioneSolleciti(
    request: RequestGetComunicazioneSolleciti,
    interactionId: string,
    timestamp: string,
  ): Observable<ResponseGetComunicazioneSolleciti> {
    const headers = {
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseGetComunicazioneSolleciti>(
      `${this.baseUrlPath}${this.apiUrls.getComunicazioneSolleciti.uri}`,
      request,
      { headers },
    );
  }

  getAnyToFeu317(request: Any2Feu317Request, headers: HttpHeaders): Observable<Any2Feu317Response> {
    return this.http.post<Any2Feu317Response>(`${this.baseUrlPath}${this.apiUrls.anyToFeu317.uri}`, request, {
      headers,
    });
  }

  createDocumentPager(request: CreateDocumentPagerRequest): Observable<CreateDocumentPagerResponse | any> {
    return this.http.post<CreateDocumentPagerResponse | any>(
      `${this.baseUrlPath}${this.apiUrls.createDocumentPager.uri}`,
      request,
      {
        observe: 'response',
      },
    );
  }

  createDocumentPagerPv(request: CreateDocumentPagerRequest): Observable<CreateDocumentPagerResponse | any> {
    return this.http.post<CreateDocumentPagerResponse | any>(
      `${this.baseUrlPath}${this.apiUrls.createDocumentPagerPv.uri}`,
      request,
      {
        observe: 'response',
      },
    );
  }

  duplicateAcceptanceGetDocument(
    request: RequestDuplicateAcceptanceGetDocument,
  ): Observable<ResponseDuplicateAcceptanceGetDocument> {
    return this.http.post<ResponseDuplicateAcceptanceGetDocument>(
      `${this.baseUrlPath}${this.apiUrls.duplicateAcceptanceGetDocument.uri}`,
      request,
    );
  }

  duplicateAcceptanceCreateDocumentPager(
    request: RequestDuplicateAcceptanceCreateDocumentPager,
  ): Observable<ResponseDuplicateAcceptanceCreateDocumentPager | any> {
    return this.http.post<ResponseDuplicateAcceptanceCreateDocumentPager | any>(
      `${this.baseUrlPath}${this.apiUrls.duplicateAcceptanceCreateDocumentPager.uri}`,
      request,
      {
        observe: 'response',
      },
    );
  }

  nextPa(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.nextPa.uri}`, { headers });
  }

  nextPv(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.nextPv.uri}`, { headers });
  }

  lastPa(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.lastPa.uri}`, { headers });
  }

  lastPv(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.lastPv.uri}`, { headers });
  }

  prevPa(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.prevPa.uri}`, { headers });
  }

  prevPv(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.prevPv.uri}`, { headers });
  }

  firstPa(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.firstPa.uri}`, { headers });
  }

  firstPv(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.firstPv.uri}`, { headers });
  }

  nextDu(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.nextDu.uri}`, { headers });
  }

  prevDu(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.prevDu.uri}`, { headers });
  }

  lastDu(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.lastDu.uri}`, { headers });
  }

  getDocumentPa(request: GetDocumentPaRequest): Observable<GetDocumentPaResponse> {
    return this.http.post<GetDocumentPaResponse>(`${this.baseUrlPath}${this.apiUrls.getDocumentPa.uri}`, request);
  }

  getDocumentPv(request: GetDocumentPaRequest): Observable<GetDocumentPaResponse> {
    return this.http.post<GetDocumentPaResponse>(`${this.baseUrlPath}${this.apiUrls.getDocumentPv.uri}`, request);
  }

  retrieveStockInfo(request: RequestRetrieveStockInfo): Observable<ResponseRetrieveStockInfo> {
    return this.http.post<ResponseRetrieveStockInfo>(
      `${this.baseUrlPath}${this.apiUrls.retrieveStockInfo.uri}`,
      request,
    );
  }

  getLdvReleaseData(request: RequestLdvReleaseData): Observable<ResponseLdvReleaseData> {
    return this.http.post<ResponseLdvReleaseData>(`${this.baseUrlPath}${this.apiUrls.getLdvReleaseData.uri}`, request);
  }

  doLdvRelease(request: RequestDoLdvRelease): Observable<ResponseDoLdvRelease> {
    return this.http.post<ResponseDoLdvRelease>(`${this.baseUrlPath}${this.apiUrls.doLdvRelease.uri}`, request);
  }

  nfeaRelease(request: RequestNfeaRelease): Observable<ResponseNfeaRelease> {
    return this.http.post<ResponseNfeaRelease>(`${this.baseUrlPath}${this.apiUrls.nfeaRelease.uri}`, request);
  }

  getActionRelease(barcode: string, idcliente: string): Observable<ResponseGetActionRelease> {
    return this.http.post<ResponseGetActionRelease>(`${this.baseUrlPath}${this.apiUrls.getActionRelease.uri}`, {
      barcode,
      ...(idcliente ? { idcliente } : {}),
    });
  }

  getBusinessInvoicings(paramName: string, paramvalue: string): Observable<ResponseGetBusinessInvoicings> {
    const queryparams = `?${paramName}=${paramvalue}`;
    return this.http.get<ResponseGetBusinessInvoicings>(
      `${this.baseUrlPath}${this.apiUrls.getBusinessInvoicings.uri}${queryparams}`,
      {},
    );
  }

  permanentLock(request: {}): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.permanentLock.uri}`, request);
  }

  getSendersProfiled(request: {}): Observable<ProfiloGruppoEmailResponse> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.getEmailProfile.uri}`, request);
  }

  validateForeignVat(request: RequestValidateForeignVat, headers: HttpHeaders): Observable<ResponseValidateForeignVat> {
    return this.http.post<ResponseValidateForeignVat>(
      `${this.baseUrlPath}${this.apiUrls.validateForeignVat.uri}`,
      request,
      { headers },
    );
  }

  getWorkDate(date: string): Observable<ResponseGetWorkDate> {
    return this.http.post<ResponseGetWorkDate>(`${this.baseUrlPath}${this.apiUrls.getWorkDate.uri}`, {
      date,
      calendarId: 'IT',
    });
  }

  verifyShipment(request: RequestVerifyShipment): Observable<ResponseVerifyShipment> {
    return this.http.post<ResponseVerifyShipment>(`${this.baseUrlPath}${this.apiUrls.verifyShipment.uri}`, request);
  }

  searchShipment(request: { id: string }): Observable<ResponseSearchShipment> {
    return this.http.post<ResponseSearchShipment>(`${this.baseUrlPath}${this.apiUrls.searchShipment.uri}`, request);
  }

  sendNotification(
    request: RequestSendNotification,
    timestamp: string,
    interactionId: string,
  ): Observable<ResponseSendNotification> {
    const headers = {
      'Content-Encoding': 'identity',
      Accept: 'application/json',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'FEU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseSendNotification>(
      `${this.baseUrlPath}${this.apiUrls.sendNotification.uri}`,
      request,
      { headers },
    );
  }

  getSecuredNumberBusiness(
    request: RequestSecuredNumberBusiness,
    interactionId,
  ): Observable<ResponseSecuredNumberBusiness> {
    const headers = {
      correlationId: interactionId,
      requestId: interactionId,
      abi: '00000',
      requestId2: ' ',
    };
    return this.http.post<ResponseSecuredNumberBusiness>(
      `${this.baseUrlPath}${this.apiUrls.securedNumberBusiness.uri}`,
      request,
      { headers },
    );
  }

  getRequiredFields(request: RequestRequiredFields): Observable<ResponseRequiredFields> {
    return this.http.post<ResponseRequiredFields>(
      `${this.baseUrlPath}${this.apiUrls.getRequiredFields.uri}${this.isPostevita ? '?sap-client=200' : ''}`,
      request,
    );
  }

  recuperaBonifici(request: RecuperaBonificiRequest): Observable<RecuperaBonificiResponse> {
    const headers = {
      'Content-Type': 'application/json',
    };
    return this.http.post<RecuperaBonificiResponse>(
      `${this.baseUrlPath}${this.apiUrls.recuperaBonifici.uri}`,
      request,
      { headers },
    );
  }

  disableUser(request: RequestDisableUser): Observable<ResponseDisableUser> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.disableUser.uri}`, request);
  }

  getFAQTypes(request: FAQTypesRequest): Observable<FAQTypesResponse> {
    return this.http.post<FAQTypesResponse>(`${this.baseUrlPath}${this.apiUrls.getFAQTypes.uri}`, request);
  }

  getFAQInfo(request: FAQSearchRequest): Observable<FAQSearchResponse> {
    return this.http.post<FAQSearchResponse>(`${this.baseUrlPath}${this.apiUrls.getFAQInfo.uri}`, request);
  }

  updateFAQInfo(request: FAQUpdateRequest): Observable<FAQUpdateResponse> {
    return this.http.post<FAQUpdateResponse>(`${this.baseUrlPath}${this.apiUrls.updateFAQInfo.uri}`, request);
  }

  addFAQTypes(request: FAQTypesRequest): Observable<FAQAddTypesResponse> {
    return this.http.post<FAQAddTypesResponse>(`${this.baseUrlPath}${this.apiUrls.addFAQTypes.uri}`, request);
  }

  reinvioNotificaNfeaFromValidation(
    request: RequestReinvioNotificaNfeaFromValidation,
  ): Observable<ResponseReinvioNotificaNfeaFromValidation> {
    return this.http.post<ResponseReinvioNotificaNfeaFromValidation>(
      `${this.baseUrlPath}${this.apiUrls.reinvioNotificaNfeaFromValidation.uri}`,
      request,
    );
  }

  getDatiInvioPinFromValidation(
    request: RequestGetDatiInvioPinFromValidation,
  ): Observable<ResponseGetDatiInvioPinFromValidation> {
    return this.http.post<ResponseGetDatiInvioPinFromValidation>(
      `${this.baseUrlPath}${this.apiUrls.getDatiInvioPinFromValidation.uri}`,
      request,
    );
  }

  inviaPin(request: RequestInviaPin): Observable<ResponseInviaPin> {
    return this.http.post<ResponseInviaPin>(`${this.baseUrlPath}${this.apiUrls.inviaPin.uri}`, request);
  }

  getDeliveryProofs(objectCode: string): Observable<DeliveryProofResponse> {
    return this.http.post<DeliveryProofResponse>(`${this.baseUrlPath}${this.apiUrls.proveConsegna.uri}`, {
      codiceOggetto: objectCode,
    });
  }

  retrieveTransactionPort(request: RetrieveTransactionPortRequest): Observable<RetrieveTransactionPortResponse> {
    return this.http.post<RetrieveTransactionPortResponse>(
      `${this.baseUrlPath}${this.apiUrls.recuperaTransazioniPort.uri}`,
      request,
    );
  }

  recuperaTransazione(request: RetrieveTransactionPortRequest): Observable<RetrieveTransactionPortResponse> {
    return this.http.post<RetrieveTransactionPortResponse>(
      `${this.baseUrlPath}${this.apiUrls.recuperaTransazione.uri}`,
      request,
    );
  }

  ptGetDocument(objectId: string): Observable<ResponsePtGetDocument> {
    return this.http.post<ResponsePtGetDocument>(`${this.baseUrlPath}${this.apiUrls.ptGetDocument.uri}`, {
      objectId,
    });
  }

  i23LGetDocument(objectId: string): Observable<ResponseI23lGetDocument> {
    return this.http.post<ResponseI23lGetDocument>(`${this.baseUrlPath}${this.apiUrls.i23lGetDocument.uri}`, {
      objectId,
    });
  }

  send23L(request: RequestSend23l): Observable<ResponseSend23l> {
    return this.http.post<ResponseSend23l>(`${this.baseUrlPath}${this.apiUrls.send23L.uri}`, request);
  }

  retrieveEcbpwebDocument(request: RequestRetrieveEcbpwebDocument): Observable<ResponseRetrieveecbpwebDocument> {
    return this.http.post<ResponseRetrieveecbpwebDocument>(
      `${this.baseUrlPath}${this.apiUrls.retrieveEcbpwebDocument.uri}`,
      request,
    );
  }

  searchEcbpwebData(request: RequestSearchEcbpwebData): Observable<ResponseSearchEcbpwebData> {
    return this.http.post<ResponseSearchEcbpwebData>(
      `${this.baseUrlPath}${this.apiUrls.searchEcbpwebData.uri}`,
      request,
    );
  }

  getInvoiceAttachment(request: RequestGetInvoiceAttachment): Observable<ResponseGetInvoiceAttachment> {
    return this.http.post<ResponseGetInvoiceAttachment>(
      `${this.baseUrlPath}${this.apiUrls.getInvoiceAttachment.uri}`,
      request,
    );
  }

  getRapporti(request: RequestGetRapporti): Observable<ResponseGetRapporti> {
    const headers = {
      correlationId: 'PIX',
      requestId: 'PIX',
    };
    return this.http.post<ResponseGetRapporti>(`${this.baseUrlPath}${this.apiUrls.getRapporti.uri}`, request, {
      headers,
    });
  }

  getLDVShipments(paramName?: string, paramvalue?: string): Observable<ResponseLDVList> {
    const queryparams = `?${paramName}=${paramvalue}`;

    return this.http.get<ResponseLDVList>(`${this.baseUrlPath}${this.apiUrls.getLDVShipments.uri}${queryparams}`);
  }

  takeActionByScenario(request: RequestTakeActionByScenario): Observable<ResponseTakeActionByScenario> {
    return this.http.post<ResponseTakeActionByScenario>(
      `${this.baseUrlPath}${this.apiUrls.takeActionByScenario.uri}`,
      request,
    );
  }

  dettaglioMail(request: DettaglioMailRequest): Observable<DettaglioMailResponse> {
    return this.http.post<DettaglioMailResponse>(`${this.baseUrlPath}${this.apiUrls.dettaglioMail.uri}`, request);
  }

  searchPickup(request: RequestSearchPickup): Observable<ResponseSearchPickup> {
    return this.http.post<ResponseSearchPickup>(`${this.baseUrlPath}${this.apiUrls.searchPickup.uri}`, request);
  }

  prenotaRitiriByLdv(request: RequestPrenotaByLdv): Observable<ResponsePrenotaByLdv> {
    return this.http.post<ResponsePrenotaByLdv>(`${this.baseUrlPath}${this.apiUrls.prenotaRitiriByLdv.uri}`, request);
  }

  takeMoreActionByScenario(request: RequestTakeMoreActionByScenario): Observable<ResponseTakeMoreActionByScenario> {
    return this.http.post<ResponseTakeMoreActionByScenario>(
      `${this.baseUrlPath}${this.apiUrls.takeMoreActionByScenario.uri}`,
      request,
    );
  }

  verifyContractsAndRetrieveAddress(request: RequestVerifyContracts): Observable<ResponseVerifyContracts> {
    return this.http.post<ResponseVerifyContracts>(
      `${this.baseUrlPath}${this.apiUrls.verifyContractsAndRetrieveAddress.uri}`,
      request,
    );
  }

  pickupCustomerSearch(request: RequestPickupCustomerSearch): Observable<ResponsePickupCustomerSearch> {
    return this.http.post<ResponsePickupCustomerSearch>(
      `${this.baseUrlPath}${this.apiUrls.pickupCustomerSearch.uri}`,
      request,
    );
  }

  ennupleFindLevel(uri: EnnuplaLevelEnum): Observable<ResponseFindEnnuple> {
    const queryParam = RequestFields.sap_client_header;
    return this.http.get<ResponseFindEnnuple>(`${this.baseUrlPath}${this.isPostevita ? uri + queryParam : uri}`);
  }

  getIbanByTicket(bp: string): Observable<ResponseGetIbanByTicket> {
    const queryParam = RequestFields.sap_client_header;
    const uri = this.apiUrls.getIbanByTicket.uri;
    return this.http.post<ResponseGetIbanByTicket>(`${this.baseUrlPath}${this.isPostevita ? uri + queryParam : uri}`, {
      bp,
    });
  }

  installmentConcession(
    request: RequestInstallmentConcession,
    interactionId: string,
    timestamp: string,
  ): Observable<ResponseInstallmentConcession> {
    const headers = {
      Accept: 'application/json',
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseInstallmentConcession>(
      `${this.baseUrlPath}${this.apiUrls.installmentConcession.uri}`,
      request,
      { headers },
    );
  }

  viewInstallmentDetail(
    X_RPNUM: string,
    interactionId: string,
    timestamp: string,
  ): Observable<ResponseViewInstallmentDetail> {
    const headers = {
      Accept: 'application/json',
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
    };
    return this.http.post<ResponseViewInstallmentDetail>(
      `${this.baseUrlPath}${this.apiUrls.viewInstallmentDetail.uri}`,
      { X_RPNUM },
      { headers },
    );
  }

  sdaAnagLdv(request: SdaAnagldvRequest, { start }: { start: number }): Observable<SdaAnagldvResponse> {
    const queryparams = `?wt=json&indent=true&rows=10&start=${start}`;
    return this.http.post<SdaAnagldvResponse>(
      `${this.baseUrlPath}${this.apiUrls.sdaAnagLdv.uri}${queryparams}`,
      request,
    );
  }

  resendLinkEshopper(request): Observable<ResponseReinvioNotificaNfeaFromValidation> {
    const headers = {
      'Content-Type': 'application/json',
    };
    return this.http.post<ResponseReinvioNotificaNfeaFromValidation>(
      `${this.baseUrlPath}${this.apiUrls.resendLinkEshopper.uri}`,
      request,
      { headers },
    );
  }

  listaPec(request: ListaPecRequest): Observable<ListaPecResponse> {
    return this.http.post<ListaPecResponse>(`${this.baseUrlPath}${this.apiUrls.listaPec.uri}`, request);
  }

  cercaUtente(request: CercaUtenteRequest): Observable<CercaUtenteResponse> {
    return this.http.post<CercaUtenteResponse>(`${this.baseUrlPath}${this.apiUrls.cercaUtente.uri}`, request);
  }

  getImageLdv(request: GetImageLdvRequest): Observable<GetImageLdvResponse> {
    return this.http.post<GetImageLdvResponse>(`${this.baseUrlPath}${this.apiUrls.getImageLdv.uri}`, request);
  }

  ricalcoliOrch(
    request: RequestRicalcoliOrch,
    interactionId: string,
    timestamp: string,
    frazionario: string = null,
    operatorId: string = null,
  ): Observable<ResponseRicalcoliOrch> {
    const headers: HttpHeaders = new HttpHeaders({
      'Content-Encoding': 'identity',
      'PI-Correlation-Id': interactionId,
      'PI-Request-Id': interactionId,
      'PI-Source': 'PIX',
      'PI-Channel': 'CC',
      'PI-Timestamp': timestamp,
      'PI-BusinessObject-Id': timestamp,
      'PI-Target': 'ISU',
      'PI-CompanyName': 'PI',
      ...(request.dossierId
        ? {
            FRAZIONARIO: frazionario ?? '',
            USERID: operatorId ?? '',
          }
        : {}),
    });
    return this.http.post<ResponseRicalcoliOrch>(`${this.baseUrlPath}${this.apiUrls.ricalcoliOrch.uri}`, request, {
      headers,
    });
  }

  getReadPaymentsList(request: RequestReadPayments): Observable<ResponseReadPayments> {
    return this.http.post<ResponseReadPayments>(`${this.baseUrlPath}${this.apiUrls.leggiVersamenti.uri}`, request);
  }

  getReadTitles(request: RequestReadTitles): Observable<ResponseReadTitles> {
    return this.http.post<ResponseReadTitles>(`${this.baseUrlPath}${this.apiUrls.leggiTitoli.uri}`, request);
  }

  readCloseout(request: RequestLeggiLiquidazioni): Observable<ResponseLeggiLiquidazioni> {
    return this.http.post<ResponseLeggiLiquidazioni>(
      `${this.baseUrlPath}${this.apiUrls.leggiLiquidazioni.uri}`,
      request,
    );
  }

  readBeneficiaries(request: RequestLeggiLiquidazioni): Observable<ResponseBeneficiari> {
    return this.http.post<ResponseBeneficiari>(`${this.baseUrlPath}${this.apiUrls.leggiBeneficiari.uri}`, request);
  }

  getInquiryAbilitazioneCarta(
    tipoRapporto: string,
    alias: string,
    cf: string,
    operatorId: string,
  ): Observable<ResponseInquiryOrch> {
    const headers: HttpHeaders = new HttpHeaders({
      'user-id': operatorId,
    });
    return this.http.post<ResponseInquiryOrch>(
      `${this.baseUrlPath}${this.apiUrls.inquiryAbilitazioneCarta.uri}/${cf}/${alias}`,
      { tipoRapporto },
      { headers },
    );
  }

  getInquiryUpdateInfoCard(
    request: RequestInquiryUpdateCard | RequestTemporaryBlock,
    alias: string,
    cf: string,
    operatorId: string,
  ): Observable<ResponseGetUpdateCard> {
    const headers: HttpHeaders = new HttpHeaders({
      'user-id': operatorId,
    });
    return this.http.post<ResponseGetUpdateCard>(
      `${this.baseUrlPath}${this.apiUrls.inquiryUpdateInfoCarta.uri}/${cf}/${alias}`,
      request,
      { headers },
    );
  }

  getInquiryBonificiPostagiro(request: RequestInquiryBonificiPostagiro): Observable<ResponseInquiryBonificiPostagiro> {
    return this.http.post<ResponseInquiryBonificiPostagiro>(
      `${this.baseUrlPath}${this.apiUrls.inquiryBonificiPostagiro.uri}`,
      request,
    );
  }

  getDettaglioBonificoStati(request: RequestDettaglioBonificoStati): Observable<ResponseDettaglioBonificoStati> {
    return this.http.post<ResponseDettaglioBonificoStati>(
      `${this.baseUrlPath}${this.apiUrls.dettaglioBonificoStati.uri}`,
      request,
    );
  }

  leggiDettaglioSinistro(request: LeggiDettaglioSinistroRequest): Observable<LeggiDettaglioSinistroResponse> {
    return this.http.post<LeggiDettaglioSinistroResponse>(
      `${this.baseUrlPath}${this.apiUrls.leggiDettaglioSinistro.uri}`,
      request,
    );
  }

  leggiDiarioSinistro(request: LeggiDiarioSinistroRequest): Observable<LeggiDiarioSinistroResponse> {
    return this.http.post<LeggiDiarioSinistroResponse>(
      `${this.baseUrlPath}${this.apiUrls.leggiDiarioSinistro.uri}`,
      request,
    );
  }

  leggiSinistri(request: LeggiSinistriRequest): Observable<LeggiSinistriResponse> {
    return this.http.post<LeggiSinistriResponse>(`${this.baseUrlPath}${this.apiUrls.leggiSinistri.uri}`, request);
  }

  createAttachmentPager(request: CreateDocumentPagerRequest): Observable<CreateDocumentPagerResponse | any> {
    return this.http.post<CreateDocumentPagerResponse | any>(
      `${this.baseUrlPath}${this.apiUrls.createAttachmentPager.uri}`,
      request,
      {
        observe: 'response',
      },
    );
  }

  attachmentFirst(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.attachmentFirst.uri}`, { headers });
  }

  attachmentLast(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.attachmentLast.uri}`, { headers });
  }

  attachmentNext(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.attachmentNext.uri}`, { headers });
  }

  attachmentPrev(headers: HttpHeaders): Observable<NextCallPvPaResponse> {
    return this.http.get<NextCallPvPaResponse>(`${this.baseUrlPath}${this.apiUrls.attachmentPrev.uri}`, { headers });
  }

  attachedFileToDownload(request: GetDocumentPaRequest): Observable<GetDocumentPaResponse> {
    return this.http.post<GetDocumentPaResponse>(
      `${this.baseUrlPath}${this.apiUrls.attachedFileToDownload.uri}`,
      request,
    );
  }

  getEntityUser(identifier: string): Observable<ResponseGetEntityUser> {
    return this.http.get<ResponseGetEntityUser>(`${this.baseUrlPath}${this.apiUrls.getEntityUser.uri}/${identifier}`);
  }

  resetPasswordSys(identifier: string): Observable<ResponseResetPasswordSys> {
    return this.http.put<ResponseResetPasswordSys>(
      `${this.baseUrlPath}${this.apiUrls.resetPasswordSys.uri}/${identifier}`,
      {},
    );
  }

  getDossierInteropOrch(queryParams: string): Observable<GetDossierInteropOrch> {
    return this.http.get<GetDossierInteropOrch>(
      `${this.baseUrlPath}${this.apiUrls.getDossierInteropOrch.uri}${queryParams}`,
      {},
    );
  }

  getAssetHeaderInterop(queryParams: string): Observable<GetAssetHeaderInteropResponse> {
    return this.http.get<GetAssetHeaderInteropResponse>(
      `${this.baseUrlPath}${this.apiUrls.getAssetHeader.uri}${queryParams}`,
      {},
    );
  }

  getDossierInterop(id: string): Observable<ResponseGetDossierInterop> {
    return this.http.get<ResponseGetDossierInterop>(
      `${this.baseUrlPath}${this.apiUrls.getDossierInterop.uri}/${id}`,
      {},
    );
  }

  viewArchivedDocs(request: RequestViewArchivedDocs): Observable<ResponseViewArchivedDocs> {
    return this.http.post<ResponseViewArchivedDocs>(`${this.baseUrlPath}${this.apiUrls.viewArchivedDocs.uri}`, request);
  }

  getPresignedUrl(request: RequestGetPresignedUrl): Observable<ResponseGetPresignedUrl> {
    return this.http.post<ResponseGetPresignedUrl>(`${this.baseUrlPath}${this.apiUrls.getPresignedUrl.uri}`, request);
  }

  getPresignedUrlAutomaticSend(idRedis: string, recipient: string): Observable<ResponseGetPresignedUrlAutomaticSend> {
    return this.http.post<ResponseGetPresignedUrlAutomaticSend>(
      `${this.baseUrlPath}${this.apiUrls.getPresignedUrlAutomaticSend.uri}`,
      {
        idRedis,
        destinatario: recipient,
      },
    );
  }

  getAssetsByRoleOrch(
    queryParams: string,
    request: {
      clientType: string;
    },
    headers = null,
  ): Observable<GetAssetsByRoleOrchResponse> {
    return this.http.post<GetAssetsByRoleOrchResponse>(
      `${this.baseUrlPath}${this.apiUrls.getAssetsByRoleOrch.uri}${queryParams}`,
      request,
      {
        headers,
      },
    );
  }

  offerteStoricoOrch(
    request: OfferteStoricoOrchRequest,
    request_id_CBI_rest: string,
    ndg,
  ): Observable<OfferteStoricoOrchResponse> {
    const headers: HttpHeaders = new HttpHeaders({
      request_id_CBI_rest: request_id_CBI_rest,
      canale: 'NFEU',
      ndg,
    });

    return this.http.post<OfferteStoricoOrchResponse>(
      `${this.baseUrlPath}${this.apiUrls.offerteStoricoOrch.uri}`,
      request,
      { headers },
    );
  }

  getOneRapporto(request: RequestClienteRidotto): Observable<ResponseOneRapporto> {
    const headers: HttpHeaders = new HttpHeaders({
      correlationId: 'PIX',
      requestId: 'PIX',
    });
    return this.http.post<ResponseOneRapporto>(`${this.baseUrlPath}${this.apiUrls.getOneRapporto.uri}`, request, {
      headers,
    });
  }

  enableUserAndSendResetPassword(
    request: EneableUserAndSendResetPasswordRequest,
  ): Observable<EnableUserAndSendResetPassordResponse> {
    return this.http.post<EnableUserAndSendResetPassordResponse>(
      `${this.baseUrlPath}${this.apiUrls.enableUserAndSendResetPassword.uri}`,
      request,
    );
  }

  enableUserAndProspectChangePassword(
    request: EnableUserAndProspectChangePasswordRequest,
  ): Observable<EnableUserAndProspectChangePasswordResponse> {
    return this.http.post<EnableUserAndProspectChangePasswordResponse>(
      `${this.baseUrlPath}${this.apiUrls.enableUserAndProspectChangePassword.uri}`,
      request,
    );
  }

  getLinkEvoluto(request: GetLinkEvolutoRequest): Observable<GetLinkEvolutoResponse> {
    return this.http.post<GetLinkEvolutoResponse>(`${this.baseUrlPath}${this.apiUrls.getLinkEvoluto.uri}`, request);
  }

  insertDatiCliente(request: RequestInsertDatiCliente): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.insertDatiCliente.uri}`, request);
  }

  getActors(actorId: string, connId: string): Observable<GetActorsResponse> {
    const queryParam = `?fullview=Y`;
    const headers: HttpHeaders = new HttpHeaders({
      'Request-id': connId,
      'X-Origin': 'PIX',
    });
    return this.http.get<GetActorsResponse>(
      `${this.baseUrlPath}${this.apiUrls.getActors.uri}/${actorId}${queryParam}`,
      { headers },
    );
  }

  getUserInbox(orgunit: string): Observable<ResponseGetUserInbox> {
    const queryParamsString = this.isPostevita ? `?sap-client=200` : '';
    const orgunitObj = { orgunit: orgunit };
    return this.http.post<ResponseGetUserInbox>(
      `${this.baseUrlPath}${this.apiUrls.getUserInbox.uri}${queryParamsString}`,
      orgunitObj,
    );
  }

  attribTickInbox(request: RequestAttribTickInbox): Observable<ResponseAttrTickInbox> {
    const queryParamsString = this.isPostevita ? `?sap-client=200` : '';
    return this.http.post<ResponseAttrTickInbox>(
      `${this.baseUrlPath}${this.apiUrls.attribTickInbox.uri}${queryParamsString}`,
      request,
      this.isPostevita ? { headers: { mandante: RequestFields.mandante } } : {},
    );
  }

  getRicOffline(request: RequestGetRicOffline): Observable<ResponseGetRicOffline> {
    const queryParamsString = this.isPostevita ? `?sap-client=200` : '';
    return this.http.post<ResponseGetRicOffline>(
      `${this.baseUrlPath}${this.apiUrls.getRicOffline.uri}${queryParamsString}`,
      request,
    );
  }

  getProfileDetail(request: RequestProfileDetail): Observable<ResponseProfileDetail> {
    return this.http.post<ResponseProfileDetail>(
      `${this.baseUrlPath}${this.apiUrls.getProfileDetail.uri}`,
      request,
      this.isPostevita ? { headers: { mandante: RequestFields.mandante } } : {},
    );
  }

  getLdv(queryParam: any[]): Observable<ResponseLDVList> {
    return this.http.get<ResponseLDVList>(`${this.baseUrlPath}${this.apiUrls.getLDVShipments.uri}?${queryParam}`);
  }

  getExport(queryParam: any): Observable<ResponseGetExport> {
    return this.http.get<ResponseGetExport>(`${this.baseUrlPath}${this.apiUrls.getExport.uri}?${queryParam}`);
  }

  getKpiRicOfflin(request: GetKpiRichOfflinRequest): Observable<GetKpiRichOfflinResponse> {
    const queryParam = this.isPostevita ? RequestFields.sap_client_header : '';
    return this.http.post<GetKpiRichOfflinResponse>(
      `${this.baseUrlPath}${this.apiUrls.getKpiRicOfflin.uri}${queryParam}`,
      request,
    );
  }

  getMultipleOrderManagement(request: MultipleOrderManagementRequest): Observable<MultipleOrderManagementResponse> {
    return this.http.post<MultipleOrderManagementResponse>(
      `${this.baseUrlPath}${this.apiUrls.multipleOrderManagement.uri}`,
      request,
    );
  }

  getOrderManagent(request: any): Observable<OrderManagementResponse> {
    return this.http.post<OrderManagementResponse>(`${this.baseUrlPath}${this.apiUrls.orderManagement.uri}`, request);
  }

  getAggiornamentoTransazione(
    request: RequestAggiornamentoTransazione,
    headers?: {
      'sap-contextid': string;
    },
    accessToken?: string,
  ): Observable<ResponseAggiornamentoTransazione> {
    const queryParam = accessToken ? '?sap-session_access_token=' + accessToken : '';
    return this.http.post<ResponseAggiornamentoTransazione>(
      `${this.baseUrlPath}${this.apiUrls.aggiornamentoTransazione.uri}${queryParam}`,
      request,
      { headers: headers || {} },
    );
  }

  getKoCard(request: RequestGetKoCard, header): Observable<ResponseGetKoCard> {
    const headers = {
      Accept: 'application/json',
      ...header,
    };
    return this.http.post<ResponseGetKoCard>(`${this.baseUrlPath}${this.apiUrls.getKoCard.uri}`, request, { headers });
  }

  getCruscAssDed(request: RequestGetCruscAssDed): Observable<ResponseGetCruscAssDed> {
    return this.http.post<ResponseGetCruscAssDed>(`${this.baseUrlPath}${this.apiUrls.getCruscAssDed.uri}`, request);
  }

  updtCruscAssDed(request: RequestUpdtCruscAssDed): Observable<ResponseUpdtCruscAssDed> {
    return this.http.post<ResponseUpdtCruscAssDed>(`${this.baseUrlPath}${this.apiUrls.updtCruscAssDed.uri}`, request);
  }

  leggiPuntiVendita(request: RequestLeggiPuntiVendita): Observable<ResponseLeggiPuntiVendita> {
    return this.http.post<ResponseLeggiPuntiVendita>(
      `${this.baseUrlPath}${this.apiUrls.leggiPuntiVendita.uri}`,
      request,
    );
  }

  getValoriCondizioni(request: RequestGetValoriCondizioni): Observable<ResponseGetValoriCondizioni> {
    return this.http.post<ResponseGetValoriCondizioni>(
      `${this.baseUrlPath}${this.apiUrls.getValoriCondizioni.uri}`,
      request,
    );
  }

  getVariantiInbox(request: RequestGetVariantiInbox): Observable<ResponseGetVariantiInbox> {
    const queryParam = this.isPostevita ? RequestFields.sap_client_header : '';
    return this.http.post<ResponseGetVariantiInbox>(
      `${this.baseUrlPath}${this.apiUrls.getVariantiInbox.uri}${queryParam}`,
      request,
    );
  }

  getTicketInbox(request: RequestGetTicketInbox): Observable<ResponseGetTicketInbox> {
    const queryParam = this.isPostevita ? RequestFields.sap_client_header : '';
    const headers = {
      'Content-Type': 'application/json',
    };
    return this.http.post<ResponseGetTicketInbox>(
      `${this.baseUrlPath}${this.apiUrls.getTicketInbox.uri}${queryParam}`,
      request,
      { headers },
    );
  }

  simulatoreProdottoByCf(codiceFiscale: string): Observable<SimulatoreProdottoByCfResponse> {
    return this.http.get<SimulatoreProdottoByCfResponse>(
      `${this.baseUrlPath}${this.apiUrls.simulatoreProdottoByCf.uri}/${codiceFiscale}`,
      {},
    );
  }

  simulatoreProdottoById(id: string): Observable<SimulatoreProdottoByIdResponse> {
    return this.http.get<SimulatoreProdottoByIdResponse>(
      `${this.baseUrlPath}${this.apiUrls.simulatoreProdottoById.uri}/${id}`,
      {},
    );
  }

  refreshToken(): Observable<RefreshTokenResponse> {
    return this.http.get<RefreshTokenResponse>(`${this.baseUrlPath}${this.apiUrls.getRefreshToken.uri}`);
  }

  prendiInCarico(request: RequestPrendiInCarico): Observable<ResponsePrendiInCarico> {
    const queryParams: string = this.isPostevita ? '?sap-client=200' : '';
    return this.http.post<ResponsePrendiInCarico>(
      `${this.baseUrlPath}${this.apiUrls.prendiInCarico.uri}${queryParams}`,
      request,
    );
  }

  recuperaInfoStato(request, headers): Observable<ResponseRecuperaInfoStato> {
    return this.http.post<ResponseRecuperaInfoStato>(
      `${this.baseUrlPath}${this.apiUrls.recuperaInfoStato.uri}`,
      request,
      { headers },
    );
  }

  recuperaIndirizzoModificato(headers, request: RequestRecuperaIndirizzoModificato): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.recuperaIndirizzoModificato.uri}`, request, {
      headers,
    });
  }

  getCountSchCli(request: { partner: string }): Observable<ResponseGetCountSchCli> {
    const queryParams: string = this.isPostevita ? '?sap-client=200' : '';
    return this.http.post<ResponseGetCountSchCli>(
      `${this.baseUrlPath}${this.apiUrls.getCountSchCli.uri}${queryParams}`,
      request,
    );
  }

  getLavorazioneTicket(
    request: Partial<RequestLavorazioneTicket>,
    headers?: {
      'sap-contextid': string;
    },
    accessToken?: string,
  ): Observable<ResponseLavorazioneTicket> {
    const queryParam = this.isPostevita
      ? accessToken
        ? `?sap-client=200&sap-session_access_token=${accessToken}`
        : '?sap-client=200'
      : accessToken
        ? `?sap-session_access_token=${accessToken}`
        : '';
    return this.http.post<ResponseLavorazioneTicket>(
      `${this.baseUrlPath}${this.apiUrls.lavorazioneTicket.uri}${queryParam}`,
      request,
      { headers: headers || {} },
    );
  }

  recuperoGruppoAssFromEnnupla(
    request: RequestRecuperoGruppoAssFromEnnupla,
  ): Observable<ResponseRecuperoGruppoAssFromEnnupla> {
    const headers = { mandante: '200' };
    return this.http.post<ResponseRecuperoGruppoAssFromEnnupla>(
      `${this.baseUrlPath}${this.apiUrls.recuperoGruppoAssFromEnnupla.uri}`,
      request,
      this.isPostevita ? { headers } : {},
    );
  }

  getDetStatCausStat(request: RequestGetDettStatCausStat): Observable<ResponseGetDettStatCausStat> {
    return this.http.post<ResponseGetDettStatCausStat>(
      `${this.baseUrlPath}${this.apiUrls.getDetStatCausStat.uri}`,
      request,
    );
  }

  indicatori(request: RequestGetIndicator, connId: string): Observable<ResponseGetIndicator> {
    const headers = {
      requestId: connId,
    };
    return this.http.post<ResponseGetIndicator>(`${this.baseUrlPath}${this.apiUrls.indicatori.uri}`, request, {
      headers,
    });
  }

  policiesProactivity(request: RequestGetIndicator, connId: string): Observable<ResponsePoliciesProactivity> {
    const headers = { requestId: connId };
    return this.http.post<ResponsePoliciesProactivity>(
      `${this.baseUrlPath}${this.apiUrls.policiesProactivity.uri}`,
      request,
      { headers },
    );
  }

  convertDtsToEuro(request: { annoValuta: string; importoDts: string }): Observable<ResponseConvertDtsToEuro> {
    return this.http.post<ResponseConvertDtsToEuro>(`${this.baseUrlPath}${this.apiUrls.convertDtsToEuro.uri}`, request);
  }

  getMailCliOpt(request: RequestGetMailCliOpt): Observable<ResponseGetMailCliOpt> {
    const queryParamsString = this.isPostevita ? `?sap-client=200` : '';
    return this.http.post<ResponseGetMailCliOpt>(
      `${this.baseUrlPath}${this.apiUrls.getMailCliOpt.uri}${queryParamsString}`,
      request,
    );
  }

  createTicketFigli(request: RequestCreateTicketFigli): Observable<ResponseCreateTicketFigli> {
    return this.http.post<ResponseCreateTicketFigli>(
      `${this.baseUrlPath}${this.apiUrls.createTicketFigli.uri}`,
      request,
    );
  }

  checkTicketFigli(request: RequestCheckTicketFigli): Observable<ResponseCheckTiketFigli> {
    return this.http.post<ResponseCheckTiketFigli>(`${this.baseUrlPath}${this.apiUrls.checkTicketFigli.uri}`, request);
  }

  getPacchiContrassegno(request: RequestPackagesCashOnDelivery): Observable<ResponseGetSolrPacchiContrassegno> {
    const headers = new HttpHeaders({
      requester: Pix.PIX,
    });
    return this.http.post<ResponseGetSolrPacchiContrassegno>(
      `${this.baseUrlPath}${this.apiUrls.getPacchiContrassegno.uri}`,
      request,
      { headers },
    );
  }

  getVoiceBiometricStatus(request: getVoiceBiometricStatusPayload): Observable<getVoiceBiometricStatus> {
    const headers = {
      ContentType: 'application/json',
    };

    return this.http.post<getVoiceBiometricStatus>(
      `${this.baseUrlPath}${this.apiUrls.getVoiceBiometricStatus.uri}`,
      request,
      { headers },
    );
  }

  updateVoiceBiometricStatus(request: updateVoiceBiometricStatusPayload): Observable<updateVoiceBiometricStatus> {
    const headers = {
      ContentType: 'application/json',
    };

    return this.http.post<updateVoiceBiometricStatus>(
      `${this.baseUrlPath}${this.apiUrls.updateVoiceBiometricStatus.uri}`,
      request,
      { headers },
    );
  }

  updateVoiceBiometric(request: updateVoiceBiometricStatusPayload) {
    const headers = {
      ContentType: 'application/json',
    };

    return this.http.post<updateVoiceBiometricStatus>(
      `${this.baseUrlPath}${this.apiUrls.updateVoiceBiometricStatus.uri}`,
      request,
      { headers },
    );
  }

  endIVRSession(request: getSessionDecisionRequest) {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.endIVRSession.uri}`, request);
  }

  getContrassegno(request: GetContrassegnoRequest): Observable<GetContrassegnoResponse> {
    return this.http.post<GetContrassegnoResponse>(`${this.baseUrlPath}${this.apiUrls.getContrassegno.uri}`, request);
  }

  cardReitero(request: RequestCardReitero): Observable<ResponseCardReitero> {
    return this.http.post<ResponseCardReitero>(`${this.baseUrlPath}${this.apiUrls.cardReitero.uri}`, request);
  }

  cancelOrder(request: RequestCancelOrderCardSim, headers): Observable<ResponseCancelOrderSim> {
    return this.http.put<ResponseCancelOrderSim>(`${this.baseUrlPath}${this.apiUrls.cancelOrder.uri}`, request, {
      headers,
    });
  }

  volppReitero(request: RequestVolppReitero): Observable<ResponseVolppReitero> {
    return this.http.post<ResponseVolppReitero>(`${this.baseUrlPath}${this.apiUrls.volppReitero.uri}`, request);
  }

  reprintPin(request: RequestReprintPin): Observable<ResponseReprintPin> {
    return this.http.post<ResponseReprintPin>(`${this.baseUrlPath}${this.apiUrls.reprintPin.uri}`, request);
  }

  clientMovements(
    request: RequestClientMovements,
    params: string[],
    correlationId: string,
    requestId: string,
    naturaGiuridica: 'r' | 'b',
    pivaOrCodiceFiscale: string,
  ): Observable<ResponseClientMovements> {
    let headers = {};
    const commonHeaders = {
      correlationId,
      requestId,
    };
    if (naturaGiuridica === 'r') {
      headers = {
        ...commonHeaders,
        naturaGiuridica,
        codiceFiscale: pivaOrCodiceFiscale,
      };
    } else if (naturaGiuridica === 'b') {
      headers = {
        ...commonHeaders,
        naturaGiuridica,
        partitaIva: pivaOrCodiceFiscale,
      };
    }
    return this.http.post<ResponseClientMovements>(
      `${this.baseUrlPath}${this.apiUrls.clientMovements.uri}?utente=${params[0]}&codiceDispositivo=${params[1]}&tipoMovimenti=${params[2]}&canale=PIX`,
      request,
      { headers },
    );
  }

  movementsDetail(params: string[], correlationId: string, requestId: string): Observable<ResponseMovementsDetail> {
    const headers = {
      correlationId,
      requestId,
    };
    return this.http.get<ResponseMovementsDetail>(
      `${this.baseUrlPath}${this.apiUrls.movementsDetail.uri}?canale=${params[0]}&timestampMovimento=${params[1]}&utente=${params[2]}&aliasCarta=${params[3]}&tipoOperazione=${params[4]}`,
      { headers },
    );
  }

  getSMSTemplates(request: RequestSMSTemplates): Observable<ResponseSMSTemplates | requestSMSTemplatesError> {
    return this.http
      .post<
        ResponseSMSTemplates | requestSMSTemplatesError
      >(`${this.baseUrlPath}${this.apiUrls.getSMSTemplates.uri}`, request)
      .pipe(catchError(error => of(error)));
  }

  saveSMS(request: RequestSaveSMS): Observable<ResponseSaveSMS | ErrorSaveSMS> {
    return this.http
      .post<ResponseSaveSMS | ErrorSaveSMS>(`${this.baseUrlPath}${this.apiUrls.saveSMS.uri}`, {
        ...request,

        account: this.sendSmsParams.account,
        serviceId: this.sendSmsParams.serviceID,
      })
      .pipe(catchError(error => of(error)));
  }

  sendSMS(request: RequestSendSMS): Observable<ResponseSendSMS | ErrorSendSMS> {
    return this.http
      .post<ResponseSendSMS | ErrorSendSMS>(`${this.baseUrlPath}${this.apiUrls.sendSMS.uri}`, {
        ...request,

        account: this.sendSmsParams.account,
        serviceId: this.sendSmsParams.serviceID,
      })
      .pipe(catchError(error => of(error)));
  }

  getFeuCart(request: RequestFeuCart): Observable<ResponseFeuCart> {
    const headers: HttpHeaders = new HttpHeaders({
      taxCode: request.taxCode,
    });
    return this.http.get<ResponseFeuCart>(`${this.baseUrlPath}${this.apiUrls.feuCart.uri}`, {
      headers,
    });
  }

  cancelPractice(request: RequestCancelPractice): Observable<ResponseCancelPractice> {
    const headers = new HttpHeaders({
      ID_TOKEN: request.header.ID_TOKEN,
    });

    return this.http.post<ResponseCancelPractice>(
      `${this.baseUrlPath}${this.apiUrls.cancelPractice.uri}`,
      request.body,
      {
        headers,
      },
    );
  }

  getProactivity(request: ScriviProattivitaRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.getProactivity.uri}`, request);
  }

  getTicketInboxAsync(
    request: RequestGetTicketInbox,
    fileId: string | undefined,
  ): Observable<ResponseGetTicketInboxAsync> {
    const queryParam = this.isPostevita ? RequestFields.sap_client_header : '';
    const headers = {
      'Content-Type': 'application/json',
      requester: Pix.PIX,
    };
    return this.http.post<ResponseGetTicketInboxAsync>(
      `${this.baseUrlPath}${this.apiUrls.getTicketInboxAsync.uri}${queryParam}`,
      fileId ? { id: fileId } : request,
      { headers },
    );
  }

  getTicketInboxContatoriSla(request: RequestGetTicketInboxContatoriSla): Observable<ResponseGetTicketInbox> {
    const headers = {
      'Content-Type': 'application/json',
      requester: Pix.PIX,
    };
    return this.http.post<ResponseGetTicketInbox>(`${this.baseUrlPath}${this.apiUrls.getTicketInbox.uri}`, request, {
      headers,
    });
  }

  getSessionDecision(request: getSessionDecisionRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.getSessionDecision.uri}`, request);
  }

  submitPersonId(request: submitPersonIdRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.submitPersonId.uri}`, request);
  }

  recuperaDettaglioLdv(id: string): Observable<RecuperaDettaglioLdvResponse> {
    return this.http.post<RecuperaDettaglioLdvResponse>(`${this.baseUrlPath}${this.apiUrls.recuperaDettaglioLdv.uri}`, {
      id,
    });
  }

  getCookies() {
    return this.http.get<GetCookiesResponse>(`${this.baseUrlPath}${this.apiUrls.getCookies.uri}`);
  }

  recuperaTemplateDescr(body: TemplateMailRequest): Observable<TemplateMailResponse> {
    const completeBody: TemplateMailCompleteRequest = {
      ...body,
      recuperaDescrizioni: 'X',
    };
    return this.http.post<TemplateMailResponse>(
      `${this.baseUrlPath}${this.apiUrls.recuperaTemplateDescr.uri}`,
      completeBody,
    );
  }

  gestioneTemplateDescr(
    body: GestioneTemplateMailRequest | TemplateMailMongoResponse,
  ): Observable<GestioneTemplateMailResponse> {
    return this.http.post<GestioneTemplateMailResponse>(
      `${this.baseUrlPath}${this.apiUrls.gestioneTemplateDescr.uri}`,
      body,
    );
  }

  saveFeedback(body: SaveFeedbackRequest): Observable<SaveFeedbackResponse> {
    return this.http.post<SaveFeedbackResponse>(`${this.baseUrlPath}${this.apiUrls.saveFeedback.uri}`, body);
  }

  getInvestments(fiscalCode: string) {
    const request = {
      listaRapportiInput: {
        acf550DatiInput: {
          acf550ITipoRich: '2',
          acf550ICf: fiscalCode,
          acf550IPiva: '',
        },
      },
      elencoBuoniInput: {
        ci004AreaInput: {
          ci004Cf: fiscalCode,
          ci004Piva: '',
          ci004NumRappRegol: '',
          ci004DataRimbDa: '0',
          ci004DataRimbA: '99991231',
          ci004DataElab: this.moment.today('YYYYMMGG'),
          ci004DataNasc: '',
          ci004TipoConto: '',
          ci004StatoBuono: '9',
          ci004KeyRipartenza: {
            ci004FrazEmissKey: '',
            ci004DataEmissKey: '',
            ci004NumBuonoKey: '',
            ci004DivisaKey: '',
            ci004TaglioKey: '1',
            ci004CodProdKey: '',
          },
        },
      },
    };

    return this.http
      .post<ResponseBankAccountList>(`${this.baseUrlPath}${this.apiUrls.bankAccountList.uri}`, request)
      .pipe(
        first(Boolean),
        map((res: ResponseBankAccountList) => res?.data?.listaRapportiOutput?.acf550DatiOutput?.acf550ORapporti),
      );
  }

  openWhitelistAccordion(
    body: OpenWhitelistAccordionRequest,
    requestId: string,
  ): Observable<OpenWhitelistAccordionResponse> {
    const fullBody: OpenWhitelistAccordionCompleteRequest = {
      ...body,
      canale: 'PIX',
    };
    return this.http.post<OpenWhitelistAccordionResponse>(
      `${this.baseUrlPath}${this.apiUrls.whitelist.uri}`,
      fullBody,
      {
        headers: {
          requestId,
          trackingId: requestId,
          systemId: 'PIX',
          canaleId: 'PIX',
        },
      },
    );
  }

  changeMerchantState(body: ChangeMerchantStateRequest, requestId: string): Observable<ChangeMerchantStateResponse> {
    const fullBody: ChangeMerchantStateCompleteRequest = {
      ...body,
      canale: 'PIX',
      flgAbilitazione: null,
    };

    return this.http.post<ChangeMerchantStateResponse>(
      `${this.baseUrlPath}${this.apiUrls.changeMerchantState.uri}`,
      fullBody,
      {
        headers: {
          requestId: requestId,
          trackingId: requestId,
          systemId: 'PIX',
          canaleId: 'PIX',
        },
      },
    );
  }

  statoByAlias(body: VerificaStatoByAliasRequest): Observable<VerificaStatoByAliasResponse> {
    const fullBody: VerificaStatoByAliasCompleteRequest = {
      ...body,
      idBanca: '07601',
      channel: 'PIX',
    };

    return this.http.post<VerificaStatoByAliasResponse>(
      `${this.baseUrlPath}${this.apiUrls.statoByAlias.uri}`,
      fullBody,
      {
        headers: {
          requestId: getUniqueId(),
          trackingId: getUniqueId(),
          systemId: 'NFEA',
          canaleId: 'PIX',
        },
      },
    );
  }

  getListaBlocchi(params: ListaBlocchiParams): Observable<ListaBlocchiResponse> {
    return this.http.get<ListaBlocchiResponse>(`${this.baseUrlPath}${this.apiUrls.listaBlocchi.uri}`, {
      params: {
        ...params,
        canale: 'PIX',
        abi: '07601',
        offsetBlocchi: '0',
      } satisfies ListaBlocchiCompleteRequest,
      headers: {
        requestId: getUniqueId(),
        correlationId: getUniqueId(),
      },
    });
  }

  //Lettura record
  gestionePartiteIvaRead(getPIvaAssistance: PartitaIvaRequest, headers: HttpHeaders): Observable<PartitaIvaResponse> {
    return this.http
      .post<PartitaIvaResponse>(`${this.baseUrlPath}${this.apiUrls.gestionePartiteIvaRead.uri}`, getPIvaAssistance, {
        headers,
      })
      .pipe(
        map(response => {
          if (response.data && Array.isArray(response.data.result)) {
            response.data.result = response.data.result.map(item => {
              // Se presente active/inactive dal servizio, converte in Attivo/Non Attivo
              for (const key in item) {
                if (Object.prototype.hasOwnProperty.call(item, key) && typeof item[key] === 'string') {
                  const lowerValue = item[key].toLowerCase();
                  if (lowerValue === 'active') {
                    item[key] = 'Attivo';
                  } else if (lowerValue === 'inactive') {
                    item[key] = 'Non Attivo';
                  }
                }
              }
              return item;
            });
          }
          return response;
        }),
      );
  }

  //Inserimento nuovo record
  gestionePartiteIvaOrch(getPIvaAssistance: PartitaIvaRequest, headers: HttpHeaders): Observable<PartitaIvaResponse> {
    return this.http.post<PartitaIvaResponse>(
      `${this.baseUrlPath}${this.apiUrls.gestionePartiteIvaOrch.uri}`,
      getPIvaAssistance,
      {
        headers,
      },
    );
  }

  //Download custom xls
  gestionePartiteIvaXls(getPIvaAssistance: PartitaIvaRequest): Observable<GetExcelPIvaAssistanceResponse> {
    return this.http.post<GetExcelPIvaAssistanceResponse>(
      `${this.baseUrlPath}${this.apiUrls.gestionePartiteIvaXls.uri}`,
      getPIvaAssistance,
    );
  }

  //Modifica record o elimina record
  gestionePartiteIvaWrite(getPIvaAssistance: PIvaAssistanceRequest): Observable<PartitaIvaResponse> {
    return this.http.post<PartitaIvaResponse>(
      `${this.baseUrlPath}${this.apiUrls.gestionePartiteIvaWrite.uri}`,
      getPIvaAssistance,
    );
  }

  // gestionePartitaIvaWrite(request: RequestGestionePartitaIvaWrite): Observable<ResponseGestionePartitaIvaWrite> {
  //   return this.http.post<ResponseGestionePartitaIvaWrite>(`${ this.baseUrlPath }${ this.apiUrls.gestionePartiteIvaWrite.uri }`, request);
  // }

  setCompanyPIN(pin: string, clientLarge: boolean) {
    this.companyPIN = pin;
    this.isClientLarge = clientLarge;
  }

  getCompanyPIN() {
    return {
      companyPIN: this.companyPIN,
      isClientLarge: this.isClientLarge,
    };
  }
  getExcelLarge(
    partitaIva: string,
    codiceFiscale: string,
    ragioneSociale: string,
    headers: HttpHeaders,
  ): Observable<GetExcelClientiLargeResponse> {
    return this.http.post<GetExcelClientiLargeResponse>(
      `${this.baseUrlPath}${this.apiUrls.getExcelLarge.uri}`,
      { partitaIva, codiceFiscale, ragioneSociale },
      { headers },
    );
  }

  // API CRUSCOTTO AI WRITE
  getHelpSearchTemplate(emailAssistenza: string[]): Observable<HelpSearchTemplateResponse> {
    return this.http.post<HelpSearchTemplateResponse>(`${this.baseUrlPath}${this.apiUrls.helpSearchTemplate.uri}`, {
      emailAssistenza,
    });
  }
  getListaTemplate(request: GetListaTemplateRequest): Observable<GetListaTemplateResponse> {
    return this.http.post<GetListaTemplateResponse>(`${this.baseUrlPath}${this.apiUrls.getListaTemplate.uri}`, request);
  }
  getFeedbackTemplate(request: GetFeedbackTemplateRequest): Observable<GetFeedbackTemplateResponse> {
    return this.http.post<GetFeedbackTemplateResponse>(
      `${this.baseUrlPath}${this.apiUrls.getFeedbackTemplate.uri}`,
      request,
    );
  }
  updateFeedback(request: UpdateFeedbackRequest): Observable<UpdateFeedbackResponse> {
    return this.http.post<any>(`${this.baseUrlPath}${this.apiUrls.updateFeedback.uri}`, request);
  }
  getDettaglioFeedback(request: GetDettaglioFeedbackRequest): Observable<GetDettaglioFeedbackResponse> {
    return this.http.post<GetDettaglioFeedbackResponse>(
      `${this.baseUrlPath}${this.apiUrls.dettaglioFeedback.uri}`,
      request,
    );
  }

  getDelega(uid: string): Observable<GetDelegaResponse> {
    const headers = new HttpHeaders({ uid });
    return this.http.get<GetDelegaResponse>(`${this.baseUrlPath}${this.apiUrls.getDelega.uri}`, {
      headers,
    });
  }

  getDelegaPdf(uid: string): Observable<DownloadDelegaPdfResponse> {
    const headers = new HttpHeaders({ uid });
    return this.http.get<DownloadDelegaPdfResponse>(`${this.baseUrlPath}${this.apiUrls.getDelegaPdf.uri}`, {
      headers,
    });
  }

  dettaglioToolRead(request: DettaglioToolReadRequest): Observable<DettaglioToolReadResponse> {
    return this.http.post<DettaglioToolReadResponse>(
      `${this.baseUrlPath}${this.apiUrls.dettaglioToolRead.uri}`,
      request,
    );
  }

  getPagamentiInbox(request: GetPagamentiInboxRequest): Observable<PagamentiInboxRow[]> {
    return this.http
      .post<GetPagamentiInboxResponse[]>(`${this.baseUrlPath}${this.apiUrls.getPagamentiInbox.uri}`, request)
      .pipe(
        map(rows =>
          rows.map<PagamentiInboxRow>(row => ({
            ...row,
            selected: false,
            isCheckboxEnabled: true,
          })),
        ),
      );
  }

  getPagamentiInboxAsinc(request: GetPagamentiInboxRequest & { id?: string }): Observable<AsincApiResponse> {
    return this.http.post<AsincApiResponse>(`${this.baseUrlPath}${this.apiUrls.getPagamentiInboxAsinc.uri}`, request);
  }

  /** TODO: replace any */
  updatePagamTick(request: any): Observable<UpdatePagamTicketResponse> {
    return this.http.post<UpdatePagamTicketResponse>(`${this.baseUrlPath}${this.apiUrls.updatePagamTick.uri}`, request);
  }

  updateAuthorization(request: RequestUpdateAuthorization): Observable<ResponseUpdateAuthorization> {
    return this.http.post<ResponseUpdateAuthorization>(
      `${this.baseUrlPath}${this.apiUrls.updateAuthorization.uri}`,
      request,
    );
  }
  isEnabledCompensationCalculation(
    request: isEnabledCompensationCalculationRequest,
  ): Observable<isEnabledCompensationCalculationResponse> {
    const headers = new HttpHeaders({
      mandante: this.isPostevita ? ApplicationStatusType.INCOMING_CALL : ApplicationStatusType.AGENT_NOT_CONNECTED,
    });
    return this.http.post<isEnabledCompensationCalculationResponse>(
      `${this.baseUrlPath}${this.apiUrls.isEnabledCompensationCalculation.uri}`,
      request,
      { headers },
    );
  }

  compensationCalculation(
    request: compensationCalculationRequest,
  ): Observable<isEnabledCompensationCalculationResponse> {
    const headers = new HttpHeaders({
      mandante: this.isPostevita ? ApplicationStatusType.INCOMING_CALL : ApplicationStatusType.AGENT_NOT_CONNECTED,
    });
    return this.http.post<isEnabledCompensationCalculationResponse>(
      `${this.baseUrlPath}${this.apiUrls.compensationCalculation.uri}`,
      request,
      { headers },
    );
  }

  checkCompensationSupplement(codiceOggetto: string[]): Observable<ResponseCheckCompensationSupplement> {
    const url = `${this.baseUrlPath}${this.apiUrls.checkCompensationSupplement?.uri || ''}`;
    const apiCall$ = this.http.post<ResponseCheckCompensationSupplement>(url, { codiceOggetto });
    const cachedResponse$ = this.apiCacheService.getFromCache<ResponseCheckCompensationSupplement>(
      HttpMethod.POST,
      url,
      { codiceOggetto },
    );
    if (cachedResponse$) {
      return cachedResponse$;
    }
    return this.apiCacheService.cacheApiCall(apiCall$, HttpMethod.POST, url, { codiceOggetto });
  }

  approvazioneEnnuple(request: ApprovazioneEnnupleRequest): Observable<ApprovazioneEnnupleResponse> {
    const url = `${this.baseUrlPath}${this.apiUrls.approvazioneEnnuple?.uri || ''}`;
    return this.http.post<ApprovazioneEnnupleResponse>(url, request);
  }
}
