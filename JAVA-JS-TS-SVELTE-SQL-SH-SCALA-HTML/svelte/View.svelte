<script>
    import {ENDPOINTS} from '../../endpoints';

    import {_} from 'svelte-i18n'
    import {checkIbanThirdParties, getStaticContractDocuments} from "../../commons/utils";
    import {
        MOD_CONTRATTO_CODICE,
        MOD_MANDATO_SEPA_SDD_CORE,
        MOD_QAV
    } from "../../commons/constants/contracts";
    import {PDF_VIEWER_ALT_FLOW_ID} from "../../commons/constants/pageOrchestrator";
    import {onMount} from "svelte";


    export let appState;
    export let nextStateEnabled;
    export let openTicket;
    export let runAlternativeFlow;

    let ticketModal;
    let ready = false;
    let buttonSrc = ENDPOINTS.CONTEXT + '/images/icons/PNGx2/ico-scrivicix2.png';


    onMount(()=>{
        ready = true;
        viewDocuments();
    })

    function viewDocuments() {
        let arg = {};
        arg.title = $_('pages.contracts.view.title');
        arg.actions = {};
        arg.actions.close = (alternativeFlowContext) => {};
        arg.documentsIdList = [];

        const staticDocument = getStaticContractDocuments(appState.globalContext.contractor.legalForm.code);

        arg.documentsIdList.push(MOD_CONTRATTO_CODICE);
        arg.documentsIdList.push(MOD_QAV);
        if (checkIbanThirdParties(appState.globalContext.contractor.bankAccounts[0].ibans[0].iban)) {
            arg.documentsIdList.push(MOD_MANDATO_SEPA_SDD_CORE);
            appState.globalContext.docs[MOD_MANDATO_SEPA_SDD_CORE].title = $_('pages.contracts.view.documents.sepa');
        }
        arg.documentsIdList.push(staticDocument);

        appState.globalContext.docs[MOD_CONTRATTO_CODICE].title = $_('pages.contracts.view.documents.contract');
        appState.globalContext.docs[MOD_QAV].title = $_('pages.contracts.view.documents.qav');

        appState.globalContext.docs[staticDocument].title = $_('pages.contracts.view.documents.privacy');

        arg.documents = appState.globalContext.docs;
        runAlternativeFlow(PDF_VIEWER_ALT_FLOW_ID, arg);
    }



</script>

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}

<style>
    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }
</style>