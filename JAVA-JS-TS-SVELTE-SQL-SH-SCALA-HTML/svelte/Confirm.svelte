<script>
    import {onMount} from 'svelte';
    import Modal from "../../ui/kit/mobileForms/Modal.svelte";
    import {_} from 'svelte-i18n'
    import {closeModal, openModal} from '../../ui/kit/sveltekit';
    import {MOD_MANDATO_SEPA_SDD_CORE} from "../../commons/constants/contracts";

    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;
    export let appState;
    export let nextStateEnabled;

    let sepaPresence;
    let sepaFlag = false;
    let attivazioneModal;
    let dichiarazioneModal;
    let attivazioneFlag = false;
    let dichiarazioneFlag = false;

    let ready = false;

    onMount(() => {
        appState.continueMessage = $_('pages.general.continue');
        nextStateEnabled = false;
        sepaPresence = !!appState.globalContext.docs[MOD_MANDATO_SEPA_SDD_CORE];
        ready = true;
    });


    $:if( ready && ( !sepaPresence || sepaFlag) && attivazioneFlag && dichiarazioneFlag){
        nextStateEnabled = true;
    }

    function checkAttivazione(){
       attivazioneFlag = true;
    }

    function checkDichiarazione(){
        dichiarazioneFlag = true;
    }

</script>

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}
{#if ready}
    <div class="webview-scroll-box" id="step-content">
        <div class="col-sm-12">
            <div class="context-abstract">
                <h3 class="area-heading">{$_('pages.contracts.confirm.title')}</h3>
            </div>
        </div>
        <div class="conditions">
            <div class="form-group form-group-lg form-row">
                <div class="col-xs-12 col-sm-12">
                    <div class="form-control-plaintext">{$_('pages.contracts.confirm.activation.check')}</div>
                </div>
            </div>
            <div class="form-group form-group-lg form-row">
                <div class="col-11 text-center intro-link" on:click={()=>{openModal(attivazioneModal)}}>
                    {$_('pages.contracts.confirm.read')}
                </div>
                {#if attivazioneFlag}
                    <div class="col-1 review-checkbox">
                        <input id="checkboxAttivazione" style="margin-top:0;" type="checkbox" disabled
                               bind:checked={attivazioneFlag}>
                        <label for="checkboxAttivazione" style="margin-top:0;"></label>
                    </div>
                {/if}
            </div>
            <hr>
            <div class="form-group form-group-lg form-row">
                <div class="col-xs-12 col-sm-12">
                    <div class="form-control-plaintext"> {$_('pages.contracts.confirm.declaration.check')}</div>
                </div>
            </div>
            <div class="form-group form-group-lg form-row">
                <div class="col-11 text-center intro-link" on:click={()=>{openModal(dichiarazioneModal)}}>
                    {$_('pages.contracts.confirm.read')}
                </div>
                {#if dichiarazioneFlag}
                    <div class="col-1 review-checkbox">
                        <input id="checkboxDichiarazione" style="margin-top:0;" type="checkbox" disabled
                               bind:checked={dichiarazioneFlag}>
                        <label for="checkboxDichiarazione" style="margin-top:0;"></label>
                    </div>
                {/if}
            </div>


            {#if sepaPresence}
                <hr>
                <div class="review-checkbox-item" style="border-radius: 5px;">
                    <div class="review-checkbox">
                        <input id="checkbox" type="checkbox" bind:checked={sepaFlag}>
                        <label for="checkbox" style="border-radius: 40%;"></label>
                    </div>
                    <div class="review-checkbox-text">
                        {$_('pages.contracts.confirm.sepa.check')}
                    </div>
                </div>
            {/if}
        </div>

    </div>
{/if}

<Modal bind:modalElement={attivazioneModal}
       closeButton={true} title={$_('pages.contracts.confirm.activation.modal.title')}>
    <div class="mobile-modal-text legal-notes">
        <div class="mobile-modal-title">
            {$_('pages.contracts.confirm.activation.modal.subtitle')}
        </div>
        {$_('pages.contracts.confirm.activation.modal.text')}
    </div>
    <div class="mobile-modal-buttons">
        <a href="javascript:void(0)"
           class="btn btn-yellow mobile-modal-button"
           on:click={ ()=>{
                closeModal(attivazioneModal),checkAttivazione();
                }}>
            {$_('pages.contracts.confirm.activation.modal.ok')}
        </a>
    </div>
</Modal>

<Modal bind:modalElement={dichiarazioneModal}
       closeButton={true} title={$_('pages.contracts.confirm.declaration.modal.title')}>
    <div class="mobile-modal-text legal-notes">
        <div class="mobile-modal-title">
            {$_('pages.contracts.confirm.declaration.modal.subtitle')}
        </div>
        {$_('pages.contracts.confirm.declaration.modal.text')}
    </div>
    <div class="mobile-modal-buttons">
        <a href="javascript:void(0)"
           class="btn btn-yellow mobile-modal-button"
           on:click={ ()=>{
                closeModal(dichiarazioneModal),checkDichiarazione();
                }}>
            {$_('pages.contracts.confirm.declaration.modal.ok')}
        </a>
    </div>
</Modal>


<style>

    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

    .form-control-plaintext{
        font-style: italic;
    }

    .intro-link {
        color: #0047BB;
        font-size: 16px;
        font-weight: bold;
        line-height: 25px;
        text-align: right;
        cursor: pointer;
        display: flex;
        justify-content: left;
        align-items: center;
        margin-top: 0!important;
    }

    hr{
        border-top: 1px solid #afafaf;
    }

    .legal-notes{
        font-style: italic;
        white-space: pre-wrap!important;
    }

    .mobile-modal-text{
        text-align: left;
    }

    .mobile-modal-title{
        text-align: left;
    }
    .conditions {
        padding-left: 20px;
        padding-right: 50px;
    }

    @media (max-width: 767px) {
        .intro-link {

            font-size: 14px;
        }

        .conditions {
            padding-left: 5vw;
            padding-right: 8vw;
        }

        .form-control-plaintext {
            color: #222427;
            font-size: 16px;
            font-style: italic;
            letter-spacing: 0;
            line-height: 24px;
        }
        h3.area-heading{
            color: #222427;
            font-size: 25px;
            font-weight: 500;
            letter-spacing: 0;
            line-height: 32px;
            text-align: center;
            padding-left: 5vw;
            padding-right: 5vw;
        }
    }

</style>