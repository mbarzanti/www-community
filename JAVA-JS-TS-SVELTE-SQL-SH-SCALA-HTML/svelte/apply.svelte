<script>
    import SubmitPageDigital from "./../submit/submitPage.svelte";
    import InputSearch from "./../../../components/ui/inputSearch.svelte";
    import Navbar from "./../../../components/ui/navbar.svelte";
    import {getUrlParam} from "./../../../commons/utils";
    import * as constants from "./../../../commons/constants";
    import * as labels from "./../../../commons/labels";
    import * as mapper from "./../../../commons/mapper";
    import * as utils from "./../../../commons/utils";
    import {SEARCH_TAX_CODE_OPTIONS} from "./../../../commons/templates";
    import ApiModal, {openModal, closeModal} from "./../../../components/ui/apiModal.svelte";
    import api from "./../../../api/api";
    import {onMount} from 'svelte';

    export let context;
    export let fail;
    export let complete = false;

    let render = false;
    let taxCode = "";
    let submitted = false;
    let searchRunning = false;
    let modal;
    let modalBodyText = "";
    let dossierInit = false;

    function submitData(data, fail) {
        const request = mapper.populateSubmitRequestFromForm(context, data);

        let onSubmit = (success, data) => {
            if (success) {
                modal = closeModal(".modal-locfinder");
                complete = true;
                submitted = true;

                let state = {};
                state.dossierId = context.dossierId;
                state.dossierStatus = context.dossierStatus;
                state.receipt = context.receipt;
                state.uberTraceId = context.uberTraceId;
                utils.redirectTo(context.dossierId, constants.SUCCESS_PAGE_REF_DIGITAL, state);
            } else {
                fail();
            }
        }
        api.dossier.submit(context, request, onSubmit);
    }

    function close() {
        //console.log("CLOSED");
        if (context.dossier && (modalBodyText === labels.WARNING_TUTORED|| context.dossier.stato === constants.DOSSIER_STATUS_ABORTED_BO || context.dossier.stato === constants.DOSSIER_STATUS_ABORTED_BNL)) {
            dossierInit = true;
        }
    }

    /*     function handleSearch(taxCode) {
            context.taxCode = taxCode;
            searchRunning = true;
            modal = openModal(modal);
            api.dossier.check(context, completeSearch);
        } */

    // SE TUTELATO / AMMINISTRATO => WARNING
    // DIGITAL => BLOCCANTE

    function enableSubmit() {
        if (context.dossier.cliente && (context.dossier.cliente.tutelato || context.dossier.cliente.amministrato)) {
            if (context.channel === constants.CHANNEL_DIGITAL) {
                fail = true;
            } else {
                modalBodyText = labels.WARNING_TUTORED;
            }
        } else {
            setTimeout(() => {
                modal = closeModal(modal);
                dossierInit = true;
            }, 1000);
        }
    }


    function completeSearch(success, data) {
        console.log(data)
        if (success) {
            context.dossier = data;
            context.dossierId = data.id;
            if (data.stato === constants.DOSSIER_STATUS_CREATED) {
                enableSubmit();
            }
        } else if (data.status === constants.CUSTOMER_NOT_ALLOWED) {
            const dossierStatus = (data.responseJSON.info.resultDetails && data.responseJSON.info.resultDetails.length > 0) ? data.responseJSON.info.resultDetails[0] : undefined;
            const responseStatus = data.responseJSON.info.resultCode;
            if ( dossierStatus && (dossierStatus === constants.DOSSIER_STATUS_DONE || dossierStatus === constants.DOSSIER_STATUS_SUBMITTED)) {
                utils.redirectTo(constants.WARNING_PAGE_ID_SUBMITTED, constants.WARNING_PAGE_REF_DIGITAL);

            } else if (dossierStatus && (dossierStatus === constants.DOSSIER_STATUS_ABORTED_BO || dossierStatus === constants.DOSSIER_STATUS_ABORTED_BNL)) {
                modalBodyText = labels.WARNING_ABORTED;
                modal = openModal(modal);
                /*
                UNICO CASO NON BLOCCANTE WARNING_ABORTED
                 */
            } else if (responseStatus === constants.DOSSIER_RESPONSE_STATUS_USER_DEFENDED || responseStatus === constants.DOSSIER_RESPONSE_STATUS_USER_ADMINISTERED) {
                utils.redirectTo(constants.WARNING_PAGE_ID_TUTORED, constants.WARNING_PAGE_REF_DIGITAL);

            } else if (dossierStatus && (dossierStatus === constants.DOSSIER_STATUS_CLOSED)) {
                utils.redirectTo(constants.WARNING_PAGE_ID_CLOSED, constants.WARNING_PAGE_REF_DIGITAL);

            } else {
                utils.redirectTo(constants.ERROR_PAGE_ID_GENERIC_ERROR, constants.ERROR_PAGE_REF_DIGITAL);
            }

        } else if (data.status === constants.INTERNAL_SERVER_ERROR || data.status === constants.MALFORMED_REQUEST) {
            utils.redirectTo(constants.ERROR_PAGE_ID_SERVER_ERROR, constants.ERROR_PAGE_REF_DIGITAL);


        } else if (data.status === constants.NETWORK_ERROR) {
            utils.redirectTo(constants.ERROR_PAGE_ID_ERROR, constants.ERROR_PAGE_REF_DIGITAL);

        } else {
            utils.redirectTo(constants.ERROR_PAGE_ID_GENERIC_ERROR, constants.ERROR_PAGE_REF_DIGITAL);
        }
    }

    onMount(() => {
        api.dossier.check(context, completeSearch);
    });

</script>

<style>

</style>


{#if dossierInit}
    <svelte:component this={SubmitPageDigital} {context} submit={submitData} bind:submitted={submitted}/>
{/if}

<ApiModal
        bind:modal
        modalTitle={'INIZIALIZZAZIONE PRATICA'}
        bind:modalBodyText
        modalLoadingText={'Controllo del Codice Fiscale'}
        bind:apiRunning={searchRunning}
        closeHandler={close}/>
