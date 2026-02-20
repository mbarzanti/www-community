<script>
    import GenericFunnelResultPage from "../components/GenericFunnelResultPage.svelte";
    import * as labels from './../../../commons/labels';
    import * as constants from './../../../commons/constants';
    import ReturnToHomeContent from "../components/ReturnToHomeContent.svelte";


    export let id;

    const template = {
        [constants.WARNING_PAGE_ID_TUTORED]: {
            title: labels.WARNING_PAGE_TITLE,
            text: labels.WARNING_TUTORED_DIGITAL
        },
        [constants.WARNING_PAGE_ID_SUBMITTED]: {
            title: labels.WARNING_PAGE_TITLE,
            text: labels.WARNING_SUBMITTED
        },
        [constants.WARNING_PAGE_ID_CLOSED]: {
            title: labels.WARNING_PAGE_TITLE,
            text: labels.WARNING_CLOSED
        }
    }

    if(!template[id]){
        window.location.hash = "/error/" + constants.ERROR_PAGE_ID_GENERIC_ERROR;
    }

</script>

<GenericFunnelResultPage
        resultTitle={template[id].title}
        resultText={template[id].text}
        resultType="warning"
        innerContentComponent={ReturnToHomeContent}
/>
