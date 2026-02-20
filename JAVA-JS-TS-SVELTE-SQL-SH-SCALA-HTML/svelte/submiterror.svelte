<script>
    import GenericFunnelResultPage from "../components/GenericFunnelResultPage.svelte";
    import * as labels from './../../../commons/labels';
    import * as constants from './../../../commons/constants';
    import ReturnToHomeContent from "../components/ReturnToHomeContent.svelte";

    export let id;

    const template = {
        [constants.ERROR_PAGE_ID_GENERIC_ERROR]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.WARNING_GENERIC_ERROR
        },
        [constants.ERROR_PAGE_ID_NOT_ALLOWED]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.WARNING_NOT_ALLOWED
        },
        [constants.ERROR_PAGE_ID_SERVER_ERROR]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.WARNING_SERVER_ERROR
        },
        [constants.ERROR_PAGE_ID_ERROR]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.NETWORK_ERROR
        },
        [constants.ERROR_PAGE_ID_NO_DOSSIER]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.NO_DOSSIER_ERROR
        }
    }

    if(!template[id]){
        id = constants.ERROR_PAGE_ID_ERROR;
    }

</script>

<GenericFunnelResultPage
        resultTitle={template[id].title}
        resultText={template[id].text}
        innerContentComponent={ReturnToHomeContent}
/>
