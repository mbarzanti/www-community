<style>
    .success-container {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        padding-bottom: 5rem;
        height: 60vh;
        padding-top: 3rem;
    }

    .success-image {
        width: fit-content;
    }

    .success-title {
        color: #222427;
        font-size: 24px;
        font-weight: 500;
        line-height: 36px;
        text-align: center;
        white-space: pre;
    }

    .success-message {
        margin-top: 5rem;
        margin-bottom: 6rem;
        color: #222427;
        font-size: 1.3rem;
        font-weight: 300;
        line-height: 30px;
        text-align: center;
        white-space: pre;
    }

    .container {
        margin-top: 2rem;
    }

    .row {
        width: 100%;
    }

</style>
<script>

    import * as labels from './../../commons/labels';
    import * as constants from './../../commons/constants';
    import InputSearch from "./../../components/ui/inputSearch.svelte";
    import * as utils from '../../commons/utils';
    import {SEARCH_TAX_CODE_OPTIONS} from "../../commons/templates";

    export let context;
    export let id;
    let taxCode = "";

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
        },
        [constants.ERROR_PAGE_ID_NO_DOSSIER_FOUND]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.NO_DOSSIER_FOUND_ERROR
        }
    }

    if(!template[id]){
        id = constants.ERROR_PAGE_ID_ERROR;
    }

    function handleSearch(taxCode) {
        context.dossier = undefined;
        utils.redirectToHome({taxCode:taxCode});
    }
</script>

<svelte:component
        this={InputSearch}
        handler={handleSearch}
        options={SEARCH_TAX_CODE_OPTIONS}
        bind:value={taxCode}/>
<div class="container width960">
    <div class="card success-container">
        <div class="row">
            <div class="col-12 text-center success-title">
                <h4>{template[id].title}</h4>
            </div>
        </div>
        <img class="success-image" src={"/static/images/cig/ico-result-error@2x.png"} alt="background image"/>
        <div class="row mt-10">
            <div class="col-12 text-center success-message">
                <p>{template[id].text}</p>
            </div>
        </div>
    </div>
</div>