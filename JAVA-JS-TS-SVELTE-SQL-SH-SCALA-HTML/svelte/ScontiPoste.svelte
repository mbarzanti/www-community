<script>
    import {ENDPOINTS} from '../../endpoints';
    import FormRow from "../../ui/kit/forms/FormRow.svelte";
    import FormItem from "../../ui/kit/mobileForms/FormItem.svelte";
    import Label from "../../ui/kit/forms/Label.svelte";
    import {_} from 'svelte-i18n';
    import Modal from "../../ui/kit/mobileForms/Modal.svelte";
    import {closeModal, openModal} from '../../ui/kit/sveltekit';
    import Loader from "../../ui/kit/elements/Loader.svelte";
    import {downloadFile} from "../../api/download";
    import {findEntityIndexByType} from "../../commons/cdm";
    import {onMount} from "svelte";
    import {EMAIL, LEGAL_FORMS, PHONE} from "../../commons/constants/typos";
    import {mergePhoneNumber} from "../../commons/utils";
    import {CODICE_INFORMATIVA_PRIVACY_SCONTI} from "../../commons/constants/contracts";
    import api from "../../api";

    export let appState;
    export let nextStateEnabled = true;
    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;

    let attivazioneModal;
    let prodottiSconti = false;
    let containsError = false;
    let scontiSrc = ENDPOINTS.CONTEXT + '/images/cig/sconti.png';
    let companySales;
    let phoneNumber;
    let pecIndex;
    let ready = false;

    onMount(()=>{
        companySales = (appState.globalContext.contractor.legalForm.code !== LEGAL_FORMS.LP.code);
        nextStateEnabled = true;
        const mobilePhoneIndex = findEntityIndexByType(appState.globalContext.contractor.phoneNumbers, "type", PHONE.MOBILE);
        pecIndex = findEntityIndexByType(appState.globalContext.contractor.emails, "type", EMAIL.EMAIL_PEC);
        phoneNumber = mergePhoneNumber(appState.globalContext.contractor.phoneNumbers[mobilePhoneIndex]);

        ready = true;
    })




    function acceptScontiPoste() {
        const callback = (success, data) => {
            if (!success) {
                containsError = true;
            } else {
                closeModal(attivazioneModal)
                prodottiSconti = true;
            }
        }
        if (!appState.globalContext.change) {
            appState.globalContext.change = {};
        }
        api.scontiPoste.accept(appState, callback);
    }


    function downloadDocs(documentId) {
        const callback = (data, documentId, success) => {
            if (success) {
                setTimeout(() => {
                    downloadFile(data.CODICE_INFORMATIVA_PRIVACY_SCONTI.url, new URL(data.CODICE_INFORMATIVA_PRIVACY_SCONTI.url).pathname.split('/').pop());
                }, 1300);
            } else {
                //TODO HANDLING
            }
        }
        api.contracts.genDocument(documentId, callback, true);
    }

</script>

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}
{#if ready}
    <div class="webview-scroll-box" id="step-content">
        <img src="{scontiSrc}" alt="sconti image"/>
            <br/>
            {#if !prodottiSconti}
                <div class="success-message format-message">
                    {$_('pages.scontiPoste.text')}
                    <span style="cursor:pointer; color:blue;" on:click={()=>{openModal(attivazioneModal)}}>
                        <a> {$_('pages.scontiPoste.open')}</a>
                    </span>
                    {$_('pages.scontiPoste.continue-description')}
                </div>
            {:else}
                <div class="success-message" style="white-space: pre-wrap;">
                    {$_('pages.scontiPoste.success')}
                </div>
            {/if}
    </div>
{/if}


<Modal bind:modalElement={attivazioneModal} closeButton={false} title="{$_('pages.scontiPoste.modal.title')}">
    {#if ready}
        <div class="mobile-modal-text" style="text-align:initial;overflow-x:hidden!important;">
            {#if containsError}
                <span class="error-message" style="color:red">{$_('pages.scontiPoste.modal.error')}</span>
            {/if}
            <FormRow indent={false} decorated={false}>
                <div class="col-md-6 col-xs-6">
                    <FormItem trasformNone={true} size="lg" label={$_('form.personInfo.firstName')} visible={true}
                              type={"label"}>
                        <Label bind:value={appState.globalContext.contractor.personInfo.firstName}/>
                    </FormItem>
                </div>
                <div class="col-md-6 col-xs-6">
                    <FormItem trasformNone={true} size="lg" label={$_('form.personInfo.lastName')} visible={true}
                              type={"label"}>
                        <Label bind:value={appState.globalContext.contractor.personInfo.lastName}/>
                    </FormItem>
                </div>
            </FormRow>
            <FormRow indent={false} decorated={false}>
                <div class="col-md-6 col-xs-6">
                    <FormItem trasformNone={true} size="lg" label={$_('form.taxData.vatNumber')} visible={true}
                              type={"label"}>
                        <Label bind:value={appState.globalContext.contractor.taxData.vatNumber}/>
                    </FormItem>
                </div>
                <div class="col-md-6 col-xs-6">
                    <FormItem trasformNone={true} size="lg" label={$_('form.contact.pec')} visible={true}
                              type={"label"}>
                        <Label bind:value={appState.globalContext.contractor.emails[pecIndex].email}/>
                    </FormItem>
                </div>
            </FormRow>
            <FormRow indent={false} decorated={false}>
                <div class="col-md-12 col-xs-12">
                    <FormItem trasformNone={true} size="lg" label={$_('form.contact.mobile')} visible={true}
                              type={"label"}>
                        <Label value={phoneNumber}/>
                    </FormItem>
                </div>
            </FormRow>
            <br>
            <div class="success-message" style="white-space: pre-wrap;">
                {$_('pages.scontiPoste.modal.text')}
                <span style="cursor:pointer; color:blue;" on:click={()=>{downloadDocs(CODICE_INFORMATIVA_PRIVACY_SCONTI)}}>
                    <a>{$_('pages.scontiPoste.modal.link')}</a>.
                </span>
            </div>
        </div>
        <div class="mobile-modal-buttons">
            <a href="javascript:void(0)"
                    class="btn btn-secondary mobile-modal-button"
                    on:click={ ()=>{closeModal(attivazioneModal);}}>
                {$_('pages.scontiPoste.modal.cancel')}
            </a>
            <a href="javascript:void(0)"
                    class="btn btn-yellow mobile-modal-button"
                    on:click={ ()=>{acceptScontiPoste();}}>
                {$_('pages.scontiPoste.modal.confirm')}
            </a>
        </div>
    {:else}
        <Loader/>
    {/if}
</Modal>



<style>
    #step-content {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        margin-top: 10vh;
    }


    .error-message {
        font-weight: 500;
        font-size: 1.2rem;
        margin-bottom: 1rem;
        padding-left: 2rem;
        padding-right: 2rem;
        text-align: center;
        white-space: pre-wrap;
    }

    .error-title {
        color: #222427;
        font-size: 30px;
        line-height: 40px;
        text-align: center;
        margin-top: 2rem;
    }

    .error-message {
        width: 324px;
        color: #222427;
        font-size: 17px;
        font-weight: 300;
        line-height: 26px;
        text-align: center;
    }

    .success-title {
        color: #222427;
        font-family: Texta;
        font-size: 24px;
        font-weight: 500;
        line-height: 36px;
        text-align: center;
        white-space: pre;
        padding-bottom: 20px;
    }

    .success-message {
        color: #222427;
        font-family: Texta;
        font-size: 16px;
        font-weight: 300;
        line-height: 23px;
        text-align: center;
        white-space: pre;
    }

    .format-message {
        padding-top: 2rem;
        font-size: 20px;
        white-space: pre-line;
        font-weight: 400;
    }



    .success-bottom-message{
        margin-top: 1rem;
    }

    :global(.mobile-modal-text input.form-control-plaintext){
       border-bottom:none!important;
    }

    @media (max-width: 767px) {
        .success-message {
        }

        .beautify-success-message{
            white-space: unset;
            margin-left: 25vw;
            margin-right: 25vw;
        }

        .success-bottom-message{
            bottom: 0;
            position: fixed;
            margin: auto;
            margin-bottom: 5vh;
            padding-left: 18vw;
            padding-right: 18vw;
        }
    }
</style>






