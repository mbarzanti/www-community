<style>
    .success-container {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        padding-bottom: 5rem;
        height: auto;
        min-height: 60vh;
        padding-top: 3rem;
    }

    .success-image {
        padding-top: 1rem;
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

    .back-home{
        position: absolute;
        bottom: 50px;
        right: 50px;
    }

</style>
<script>

    import * as labels from './../../commons/labels';
    import * as constants from './../../commons/constants';
    import Button from "../../SvelteKit/forms/Button.svelte";
    import InputSearch from "./../../components/ui/inputSearch.svelte";
    import * as utils from '../../commons/utils';
    import {onMount, tick} from 'svelte';
    import * as pageOrchestratorConstants from './../../components/orchestrator/constants';
    import {SEARCH_TAX_CODE_OPTIONS} from "../../commons/optionsTemplates";
    import getFormattedMessage from "../../commons/error/Formatter";

    export let params = {};
    export let appState = undefined;
    export const runAlternativeFlow = undefined;
    export let runAction;
    export const nextStateEnabled = false;
    let taxCode = "";
    let ready = false;
    let id;

    let text;
    let title;

    const template = {
        [constants.ERROR_PAGE_ID_BPM_ERROR]:{
            title: labels.ERROR_PAGE_TITLE,
            text: labels.WARNING_BPM_ERROR
        },
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
        [constants.ERROR_PAGE_ID_SEARCH_VALIDATION]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.DEFAULT_SEARCH_VALIDATION_ERROR
        },
        [constants.ERROR_PAGE_ID_PSI_ERROR]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.PSI_ERROR_MESSAGE
        },
        [constants.ERROR_PAGE_ID_CHANGES_UI_GEN]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.WARNING_GENERIC_ERROR
        },
        [constants.ERROR_PAGE_ID_PARTNER_ERROR]: {
            title: labels.ERROR_PAGE_TITLE,
            text: labels.PARTNER_ERROR
        }
    }
    onMount( async ()=>{
        let fromFailCode = false;
        if(params.id) {
            id = params.id;
        } else if( appState && appState.failCode){
            id = appState.failCode;
            fromFailCode = true;
        } else {
            id = params.id;
        }

        if(!template[id]){
            id = constants.ERROR_PAGE_ID_ERROR;
            fromFailCode = false;
        }
        title = template[id].title;
        if(fromFailCode && appState.failMessage && appState.failMessage.name){
            text = getFormattedMessage(appState.failMessage);
        } else {
            text = template[id].text;
        }


        await tick();
        ready = true;
    })


    function handleSearch(taxCode) {
        console.log(taxCode)
        if(appState){
            appState.globalContext.taxCode = taxCode;
            runAction(pageOrchestratorConstants.SHIFT_TO_STATE_ACTION_ID, {next:constants.SEARCH_PAGE_ID});
        } else {
            utils.redirectToHome({taxCode:taxCode});
        }
    }


    export let homeAddress;

    function comeBackHome() {

        utils.redirectToWithState(
            homeAddress, {}
        );
    }
</script>

<!--<svelte:component
        this={InputSearch}
        handler={handleSearch}
        options={SEARCH_TAX_CODE_OPTIONS}
        bind:value={taxCode}/>-->
{#if ready}
<div class="container width960">
    <div class="card success-container">
        <div class="row">
            <div class="col-12 text-center success-title">
                <h4>{title}</h4>
            </div>
        </div>
        <img class="success-image" src={"/feu-after-sales/images/cig/ico-result-error@2x.png"} alt/>
        <div class="row mt-10">
            <div class="col-12 text-center success-message" style="white-space: pre-line">
                <p>{text}</p>
            </div>
        </div>
        {#if homeAddress}
            <div class="back-home">
                <Button name={utils.getString("default.buttons.comeBackHome")} on:click={comeBackHome}/>
            </div>
        {/if}
    </div>
</div>
{/if}