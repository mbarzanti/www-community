<script>
    import {ENDPOINTS} from '../../endpoints';
    import { _ } from 'svelte-i18n'
    import {onMount} from "svelte";

    export let appState;
    export let nextStateEnabled;
    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;

    let ready = false;


    let src;
    onMount(()=>{
        src = ENDPOINTS.CONTEXT + '/images/illustrations/PNGx1/illu_contrattox1.png';
        nextStateEnabled = true;
        ready=true;
    })

</script>
{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}
{#if ready}
    <div class="webview-scroll-box" id="step-content">
        <img class="intro-icon" {src} alt="Intro Icon"/>
        <div class="row">
            <div class="col-12 text-center" style="width: 100%;">
                <h3 class="intro-title">{$_('pages.contracts.intro.title')}</h3>
            </div>
            <div class="row mt-2" style="width: 100%;padding-left: 1rem;">
                <div class="col-12 text-center">
                    <p class="intro-message">{$_('pages.contracts.intro.message')}</p>
                </div>
            </div>
        </div>
    </div>
{:else}
    <p>Loading...</p>
{/if}

<style>
    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

    .intro-title {
        color: #222427;
        font-size: 24px;
        font-weight: 600;
        line-height: 36px;
        text-align: center;
    }

    .intro-message {
        color: #222427;
        font-size: 16px;
        font-weight: 500;
        line-height: 23px;
        text-align: center;
        padding-left: .5rem;
        padding-right: .5rem;
    }

    .intro-icon {
        transform: scale(.8);
        margin-bottom: .5rem;
        display: flex;
        margin: auto;
    }

    @media (max-width: 767px) {
        .intro-title {
            color: #222427;
            font-size: 24px;
            font-weight: 600;
            line-height: 36px;
            text-align: center;
            padding-left: 7vw;
            padding-right: 7vw;
        }

        .intro-message {
             color: #222427;
             font-size: 16px;
             font-weight: 500;
             line-height: 23px;
             text-align: center;
            padding-left: 9vw;
            padding-right: 5vw;
         }
    }
</style>




