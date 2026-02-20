<script>
    import InputSearch from "./../../components/ui/inputSearch.svelte";
    import * as constants from "./../../commons/constants";
    import * as fluxConstants from './../../components/orchestrator/constants';
    import * as utils from './utils';

    import api from "../../api/api";

    import {onMount} from 'svelte'
    import ApiModal, {openModal, closeModal} from "../../components/ui/apiModal.svelte";
    import {SEARCH_TAX_CODE_OPTIONS} from "../../commons/optionsTemplates";
    import { _ } from 'svelte-i18n';
    import {REDIRECT_STATE_KEY} from "./../../commons/constants";
    import {popRedirectState} from "../../commons/utils";
    import { partner_campi_readonly, partnerNome, partnershipAttiva, partner_tax_code, partner_vat_number, tipoDiRicerca, partnerTecnologico, pagamentoCommissioni, ibanDebitoPartner, ibanCreditoPartner, intestazioneIbanCreditoPartner, intestazioneIbanDebitoPartner, welcome, azioneCessazioneRapporto, partnerId, nomiPartnersTecnologici, onboardingTecnicoMace, swiftDebitoPartner, swiftCreditoPartner, partnerTecnologicoUnivoco, codice_bruciatura, partnerTipologiaCodice } from "../change-operations/store";
    import { get } from 'svelte/store';



    export let appState;
    export let runAction;
    export let nextStateEnabled=undefined;
    export let runAlternativeFlow=undefined;

    // Remove Warnings
    nextStateEnabled;
    runAlternativeFlow;

    let searchProgress = 0;
    let taxCode = "";
    let searchRunning = false;
    let modal;
    let modalBodyText = "";
    let searchRoles;


    function close() {}

    function handleSearch(taxCode) {
        appState.context.taxCode = taxCode.toUpperCase();
        searchRunning = true;
        searchRoles = undefined;
        searchProgress = 0;
        openModal(modal);
        if(get(tipoDiRicerca) === "cliente") {
            api.customer.get({taxCode:taxCode}, nextCustomerSearch);
        } else {
            api.crmu.partner.get(taxCode, nextCustomerSearch);
        }
    }

    function nextstep() {
        setTimeout(()=>{
            closeModal(modal);
            runAction(fluxConstants.SHIFT_TO_STATE_ACTION_ID, { next: 1});},1000
        );
    }

    function nextCustomerSearch(success, data) {
        if( success){
            if(get(tipoDiRicerca) === "cliente") {
                if(!searchRoles){
                    if(data.customer.legalForm.code === "PF") {
                        searchRoles = utils.SEARCH_ROLES_RETAIL;
                    } else if(data.customer.legalForm.code === "LP"){
                        searchRoles = utils.SEARCH_ROLES_FREELANCE;
                    } else if(data.customer.legalForm.code === "DI"){
                        searchRoles = utils.SEARCH_ROLES_SOLE_TRADER;
                    } else {
                        searchRoles = utils.SEARCH_ROLES_BUSINESS;
                    }
                }
                appState.globalContext.customers = appState.globalContext.customers || {};
                appState.globalContext.customers[searchRoles[searchProgress].role] = data.customer;
                searchProgress++;
                if(searchRoles[searchProgress]){
                    const searchCustomerId = searchRoles[searchProgress].getSearchCustomerId(data.customer);
                    api.customer.get({taxCode:searchCustomerId, customerType: searchRoles[searchProgress].oneViewCustomerType}, nextCustomerSearch);
                } else {
                    completeSearch(success, data, searchRoles[searchProgress-1].role);
                }
            } else {
                partner_vat_number.set(data.PARTNER_PIVA);
                partnerNome.set(data.PARTNER_NOME);
                partner_tax_code.set(data.PARTNER_CF);
                console.log("PRIMA DEL SET", data.PARTNER_PARTNERSHIP_ATTIVA);
                partnershipAttiva.set(data.PARTNER_PARTNERSHIP_ATTIVA);
                partnerTecnologico.set(data.PARTNER_TECNOLOGICO);
                partnerTecnologicoUnivoco.set(data.PARTNER_TECNOLOGICO_UNIVOCO);
                pagamentoCommissioni.set(data.PARTNER_PAGAMENTO_COMMISSIONI);
                ibanDebitoPartner.set(data.PARTNER_IBAN_ADDEBITO);
                ibanCreditoPartner.set(data.PARTNER_IBAN_ACCREDITO);
                intestazioneIbanCreditoPartner.set(data.PARTNER_INTESTAZIONE_ACCR);
                intestazioneIbanDebitoPartner.set(data.PARTNER_INTESTAZIONE_ADD);
                welcome.set(data.PARTNER_TEMPLATE_EMAIL_WELCOME);
                azioneCessazioneRapporto.set(data.PARTNER_AZIONE_CESSAZIONE_RAPPORTO);
                partner_campi_readonly.set(data.PARTNER_CAMPI_READONLY);
                partnerId.set(data.PARTNER_ID);
                nomiPartnersTecnologici.set(data.PARTNER_NOMI_PARTNERS_TECNOLOGICI);
                onboardingTecnicoMace.set(data.PARTNER_ONBOARDING_TECNICO_MACE);
                swiftDebitoPartner.set(data.PARTNER_SWIFT_ADDEBITO);
                swiftCreditoPartner.set(data.PARTNER_SWIFT_ACCREDITO);
                codice_bruciatura.set(data.PARTNER_TEMPLATE_EMAIL_COD_BRUCIATURA);
                partnerTipologiaCodice.set(data.PARTNER_TIPOLOGIA_CODICE);
                nextstep();
            }
        } else {
            completeSearch(success, data, searchRoles ? searchRoles[searchProgress].role :  utils.SEARCH_ROLES_BUSINESS[0].role);
        }
    }


    function completeSearch(success, data, role) {
        if(success){
            nextstep();
        } else {
            if (data.errorInfos && data.errorInfos[0] && data.errorInfos[0].code === "001"){
                modalBodyText = $_(`search.modal.errors.customer.${role}`) + $_("search.modal.errors.customer.notFound");

            } else if (data.errorInfos && data.errorInfos[0] && data.errorInfos[0].code === "002"){
                modalBodyText = $_(`search.modal.errors.customer.${role}`) + $_("search.modal.errors.customer.notElegible");

            } else if (data.status === constants.INTERNAL_SERVER_ERROR || data.status === constants.MALFORMED_REQUEST) {
                modalBodyText = $_("search.modal.errors.server");

            } else if (data.status === constants.NETWORK_ERROR) {
                modalBodyText = $_("search.modal.errors.network");

            } else if (data.status === constants.NOT_FOUND_ERROR) {
                modalBodyText = $_("search.modal.errors.notFound");

            } else {
                modalBodyText = $_("search.modal.errors.generic");
            }
            setTimeout(() => {
                searchRunning = false;
                taxCode = "";
            }, 1200);
        }
    }
    onMount(()=>{

        const state = popRedirectState(REDIRECT_STATE_KEY);
        if(state && state.vatNumber){
            appState.globalContext.redirectState = state;
            handleSearch(state.vatNumber);
        } else if(appState.globalContext.taxCode){
            handleSearch(appState.globalContext.taxCode)
        }
    })
</script>

<svelte:component
        this={InputSearch}
        handler={handleSearch}
        options={SEARCH_TAX_CODE_OPTIONS($_("search.bar.placeholder"))}
        bind:value={taxCode}/>
<ApiModal
        bind:modal = {modal}
        modalTitle={$_("search.modal.title")}
        bind:modalBodyText = {modalBodyText}
        modalLoadingText={$_("search.modal.title")}
        bind:apiRunning={searchRunning}
        closeHandler={close}/>