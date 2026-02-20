<script>
    import * as constants from './../../commons/constants';
    import {_} from 'svelte-i18n';
    import {label} from "../../components/templates/general";
    import * as localUtils from './utils';
    import * as pageOrchestratorConstants from './../../components/orchestrator/constants'
    import Loader from "../../SvelteKit/elements/Loader.svelte";
    import * as general from './../../components/templates/general';
    import * as backwardUtils from "../../components/orchestrator/backward/utils.js";
    import jsonPath from 'jsonpath';
    import AutoFormPageWizard from "../../SvelteKit/forms/AutoFormPageWizard.svelte";
    import api from '../../api/api';
    import * as utils from './../../commons/utils';
    import {formConfig} from './change-form-generator';
    import {closeModal} from "./../../components/ui/apiModal.svelte";
    import TaskPage from '../../components/ui/taskPage.svelte';
    import * as stepsConfig from './../stepperConfig';
    import {SHIFT_TO_STATE_ACTION_ID} from "../../components/orchestrator/constants";
    import {ENTRIES} from "../../SvelteKit/forms/resources";
    import {REDIRECT_STATE_KEY} from "./../../commons/constants";
import NumberField from '../../SvelteKit/forms/NumberField.svelte';
    import UploadFileComponent from './upload-file-component.svelte';
    import axios from 'axios';
    import { get } from 'svelte/store';
    import { welcome, codice_bruciatura, partner_cod_type, partner_company_name, partner_tax_code, partner_vat_number, pagamentoCommissioni, partnerTecnologico, partnerNome, partnerTecnologicoUnivoco, partnerTipologiaCodice, onboardingTecnicoMace, partnerId, swiftCreditoPartner, ibanCreditoPartner, swiftDebitoPartner, ibanDebitoPartner, partner_campi_readonly } from './store';
    
    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;
    runAlternativeFlow;
    runAction;

    const varType = appState.globalContext.change.selectedChange;

    let formData = {};
    let submitted = false;
    let steps = stepsConfig.steps;

    let change = utils.getChange(varType, appState.globalContext.currentChanges);
    let shortTitle = "shortLabel." + change.label;
    let shortSubTitle = "shortLabel." + change.label;
    let confirmMessage = "confirmMessages." + change.label;

    let visible = false;
    let successMessage;
    let textMessage;
    let isLoading = false;

    let templates = {
        variation: utils.getString("confirmMessages.templates.variation"),
        add: utils.getString("confirmMessages.templates.add")
    };

    const currentStepIndex = steps.findIndex((item)=>item.name===constants.CHANGE_OP_STEP_ID);

    steps[currentStepIndex].tasks[0].title = $_(shortTitle);
    steps[currentStepIndex].tasks[0].subtitle = $_(shortSubTitle);

    let formOptions ={
        resources:{
            [ENTRIES.WIZARD.MODAL.SEND.CONFIRM]: $_(confirmMessage, {values: templates})
        }
    }

    function submitData(e) {
        isLoading = true;
        const uiModel = utils.getValidJSON(formData);

        let asset;
        let arg;

        if(varType !== "VAR_RECESSO_MASSIVO") {
            asset= jsonPath.query(appState.globalContext.asset.assets, '$[?(@.id == "' + appState.globalContext.asset.idAsset.idAss + '")]');
            // FIXME
            arg = {
                customers: appState.globalContext.customers,
                asset: asset[0],
                change: {}
            }
            if(appState.globalContext.salesPoint){
                arg.change.id_sales_point = appState.globalContext.salesPoint.id_sales_point;
            }
        }

        let extraPayload;
        if(varType !== "MASSIVE_SP_LOAD" && varType !== "VAR_RECESSO_MASSIVO") {
            extraPayload = formConfig[varType].applyChanges(arg, uiModel);
        }
        let onSubmit = (success, data) => {
            if (success) {
                closeModal(".modal-locfinder");
                submitted = true;
                api.ms.funnel.complete(appState.globalContext,
                        (success, data)=>{
                            if(success){
                                    console.log(data);
                                    if(varType !== "MASSIVE_SP_LOAD" && varType !== "VAR_RECESSO_MASSIVO") {
                                        utils.redirectToWithState(
                                            data.completeFunnelStepResp.nextStepUrl,
                                            {
                                                state: {
                                                    key: REDIRECT_STATE_KEY,
                                                    content: {
                                                        vatNumber: appState.globalContext.customers.contractor.taxData.vatNumber,
                                                        idAsset: appState.globalContext.asset.idAsset,
                                                        productCode: appState.globalContext.asset.productCode
                                                    }
                                                },
                                                hash: appState.globalContext.FUNNEL_INSTANCE_ID
                                            }
                                        );
                                    } else {
                                        startBPMLoadingProcedure(appState.globalContext.FUNNEL_INSTANCE_ID, data.completeFunnelStepResp.nextStepUrl);
                                    }
                            } else {
                                utils.redirectTo(constants.ERROR_PAGE_REF_UP, constants.ERROR_PAGE_ID_GENERIC_ERROR);
                            }
                        }
                )
            } else {
                e.detail.fail();
            }
        }

        let request = {};
        request.customers = {};
        request.customers.contractor = varType !== "VAR_RECESSO_MASSIVO" ? appState.globalContext.customers.contractor : null;
        request.FUNNEL_INSTANCE_ID = appState.globalContext.FUNNEL_INSTANCE_ID;
        request.asset = varType !== "VAR_RECESSO_MASSIVO" ? appState.globalContext.asset.assets.find((item)=>appState.globalContext.asset.idAsset.idAss === item.id) : null;
        request.change = varType !== "VAR_RECESSO_MASSIVO" ? appState.globalContext.change.selectedChange : "VAR_RECESSO_MASSIVO";
        request.extraPayload = extraPayload;
        if(appState.globalContext.salesPoint){
            request.id_sales_point = appState.globalContext.salesPoint.id_sales_point;
        }
        if(appState.globalContext.dossier){
            request.dossier = appState.globalContext.dossier;
            request.dossierFractionalCode  = appState.globalContext.dossierFractionalCode;
        }
        if(appState.globalContext.salesPointFilename) {
            if(varType === "VAR_RECESSO_MASSIVO") {
                request.recessoFilename = appState.globalContext.salesPointFilename;
            } else {
                request.salesPointFilename = appState.globalContext.salesPointFilename;
            }
        } 
        request.emailTemplates = {};
        request.emailTemplates.welcome = get(welcome);
        request.emailTemplates.codBruciatura = get(codice_bruciatura);
        request.partner = {};
        if(varType !== "VAR_RECESSO_MASSIVO") {
            request.partner.codType = get(partner_cod_type);
            request.partner.taxCode = get(partner_tax_code);
            request.partner.vatNumber = get(partner_vat_number);
            request.partner.companyName = get(partner_company_name);
        } else {
            request.partner.taxdata = {};
            request.partner.taxdata.taxCode = get(partner_tax_code);
            request.partner.taxdata.taxResidences = [];
            request.partner.taxdata.vatNumber = get(partner_vat_number); 

            request.partner.legalInfo = {};
            request.partner.legalInfo.companyName = get(partnerNome);
            request.partner.legalInfo.subscriptions = [];
            request.partner.legalInfo.businessActivities = [];
            request.partner.legalInfo.salesPoints = [];

            request.partner.customData = {};
            request.partner.customData.isTechPartner = get(partnerTecnologico);
            request.partner.customData.isTechPartnerUnique = get(partnerTecnologicoUnivoco);
            request.partner.customData.techPartnerName = {};
            request.partner.customData.techPartnerName.items = []; //TODO
            request.partner.customData.codType = get(partnerTipologiaCodice);
            request.partner.customData.mace = get(onboardingTecnicoMace);
            request.partner.customData.closeContractMotivation = null;
            request.partner.customData.activePartnership = true;
            request.partner.customData.partnerId = get(partnerId);

        }

        request.paymentType = get(pagamentoCommissioni);

        request.bankAccounts = [];
        request.bankAccounts.push({
                addresses: [],
                delegates: [],
                ibans: [
                    {
                        code: get(swiftCreditoPartner),
                        iban: get(ibanCreditoPartner)
                    }
                ],
                operations: [],
                accountHolder: get(partnerNome),
                transactionMode: "Credit"
        });

        request.bankAccounts.push({
                addresses: [],
                delegates: [],
                ibans: [
                    {
                        code: get(swiftDebitoPartner),
                        iban: get(ibanDebitoPartner)
                    }
                ],
                operations: [],
                accountHolder: get(partnerNome),
                transactionMode: "Debit"
        });
        

        api.ms.funnel.update(request, onSubmit);
    }

    function backward(){
        let currentChange = appState.globalContext.currentChanges ? appState.globalContext.currentChanges.find((change)=>change.id===appState.globalContext.change.selectedChange) : null;
        let next = -3;
        if (currentChange && currentChange.additionalSelection && currentChange.additionalSelection.indexOf("salesPoint") > -1) {
            next = -2;
        }
        runAction(SHIFT_TO_STATE_ACTION_ID, {next:next});
    }

    function startBPMLoadingProcedure(fid, nextStepUrl) {
        isLoading = true;
        axios({
            url: `${varType !== 'VAR_RECESSO_MASSIVO' ? nextStepUrl : '/codice-bpm-manager-after-sales'}/api/v1/funnels/${fid}/steps${varType !== 'VAR_RECESSO_MASSIVO' ? nextStepUrl : '/codice-bpm-manager-after-sales'}`,
            method: 'post',
            headers: {"groupid": "admin"},
        }).then((res) => {
            if(res.data.task.status === "error") {
                isLoading = false;
                successMessage = false;
                textMessage ='Si è verificato un errore, riprovare più tardi.';
                visible = true;
                hideToast();
            } else {
                isLoading = false;
                successMessage = true;
                textMessage ='Variazione massiva avviata, verifica più tardi l\'esito in "Storico"';
                visible = true;
                hideToast();
            }
        }).catch((error) => {
            isLoading = false;
            successMessage = false;
            textMessage ='Si è verificato un errore, riprovare più tardi.';
            visible = true;
            hideToast();
        });
    }

    function hideToast() {
        setTimeout(() => {
            visible = false;
        }, 3500);
    }
</script>

<style>
</style>
<TaskPage
    bind:steps={steps}
    currentTask={constants.CHANGE_OPERATION_PAGE_ID}
    currentStep={constants.CHANGE_OP_STEP_ID}
    bind:nextStateEnabled={nextStateEnabled} >
    {#if varType === "MASSIVE_SP_LOAD" || varType === "VAR_RECESSO_MASSIVO"}
        <UploadFileComponent bind:salesPointFilename={appState.globalContext.salesPointFilename} {appState} on:submit={submitData} {visible} {successMessage} {textMessage} {isLoading} tipoVariazione={varType}>
            <a href={undefined} slot="buttons"
                class="btn btn-default absolute-action-left"
                on:click={backward}>
                {"indietro"}
            </a>
        </UploadFileComponent>   
    {:else}
     <AutoFormPageWizard
             title={'Richiesta Anticipazione Cassa Integrazione Guadagni'}
             flowRestricted={true}
             enableEditCtxBtn={false}
             externalButtons={false}
             externalStepper={true}
             bind:wizard_values_map={formData.wizard_values_map}
             bind:wizard_validation_map={formData.wizard_validation_map}
             bind:wizard_visibility_map={formData.wizard_visibility_map}
             path={localUtils.dummyFormDescriptor(appState.globalContext.formDescriptor)}
             submitted={submitted}
             readOnly={varType === "VAR_CON_1" && get(partner_campi_readonly) === "ALL" ? true : false}
             options={formOptions}
             on:submit={submitData}
             on:complete={()=>false}>
         <a href={undefined} slot="buttons"
            class="btn btn-default absolute-action-left"
            on:click={backward}>
             {"indietro"}
         </a>
     </AutoFormPageWizard>
     {/if}
    <div style="margin-top: 80px;"></div>
</TaskPage>
