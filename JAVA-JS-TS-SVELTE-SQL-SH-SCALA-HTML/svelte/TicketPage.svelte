<script>
    import {ENDPOINTS} from '../../endpoints';

    import { isLocaleLoaded} from '../../locale/i18n';
    import { _ } from 'svelte-i18n'
    import {onMount} from "svelte";


    export let appState;
    export const nextStateEnabled = undefined;
    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;

    let ready = false;
    let src = ENDPOINTS.CONTEXT + '/images/illustrations/PNGx2/tutorial-complete@2x.png';

    onMount(()=>{
        appState.stepTitle = "";
        ready = true;
    })
</script>

{#if ready}
    <div class="webview-scroll-box mobile-result-view" id="step-content">
        <div class="error-title">
            {$_('error.ticket.title')}
        </div>
        <img src={src} class="end-icon" alt="background image"/>
        <div class="error-message">
            {$_('error.ticket.text')}
        </div>
        <div class="error-message">
            {$_('error.ticket.contact')}
        </div>
    </div>
{/if}

<style>
    #step-content {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        /*margin-top: -5rem;*/
    }

    .error-title {
        color: #222427;
        font-size: 30px;
        line-height: 40px;
        text-align: center;
        margin-bottom: 1rem;
    }

    .error-message {
        width: 324px;
        color: #222427;
        font-size: 17px;
        font-weight: 300;
        line-height: 26px;
        text-align: center;
        margin-top: 1rem;
    }

    .end-icon {
        width: 30%;
    }

    @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
        .end-icon {
            height: 30%;
        }
    }

    @media (max-width: 767px){
        .mobile-result-view {
            height: 94vh;
        }
        .end-icon {
            width: 60%;
        }
    }

</style>





