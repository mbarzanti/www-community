<script>
    import {_} from 'svelte-i18n'
    import {onMount} from "svelte";

    export let appState;
    export let nextStateEnabled;
    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;
    export let onMainRepeatableAction;

    let ready = false;

    onMount(()=>{
        appState.context.otpCode = undefined;

        ready = true;
    })
    $:nextStateEnabled = !!(appState.context.otpCode && appState.context.otpCode.length >= 8);


</script>

{#if ready}
    <div class="container pb20">
        <div class="row">
            <div class="col-12 text-center">
                <h3 class="intro-title">{$_('pages.otp.sign.title')}</h3>
            </div>
        </div>
        {#if appState.fail}
            <div class="error-message text-center">
                {appState.failMessage}
            </div>
        {/if}
        <div class="d-flex justify-content-center">
            <div id="divOuter">
                <div id="divInner">
                    <input id="otpStyle" bind:value={appState.context.otpCode} type="text" maxlength="8" autocomplete="off"
                           onKeyPress="javascript: return (event.keyCode === 8 || event.keyCode === 46 ? true : !isNaN(Number(event.key)) && this.value.length!=8 )"/>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-12 text-center">
                <div class="intro-link" on:click={onMainRepeatableAction}>{$_('pages.otp.sign.resend')}</div>
            </div>
        </div>
    </div>
{/if}

<style>

    .intro-title {
        color: #222427;
        font-size: 24px;
        font-weight: 600;
        line-height: 36px;
        text-align: center;
        max-width: 12.7rem;
        margin: auto;
        margin-top: -1rem;
        margin-bottom: 1rem;
    }

    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

    .intro-link {

        color: #0047BB;
        font-size: 17px;
        font-weight: bold;
        line-height: 30px;
        text-align: center;
        cursor: pointer;
        margin-top: 3rem;
    }
</style>











