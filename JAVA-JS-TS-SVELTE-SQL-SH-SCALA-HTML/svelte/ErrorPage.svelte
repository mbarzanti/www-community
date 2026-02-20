<script>
    import {_} from "svelte-i18n";

    export let appState;
    export const nextStateEnabled = undefined;
    export const openTicket = undefined;
    export const runAlternativeFlow = undefined;

    const PREFIX = "error"

</script>
<div class="row mobile-result-view">
    <div class="col-md-12">
        <div class="main-result main-result-error">
            <div class="main-result-wrap">
                <div class="result-heading">
                    <h1>{$_(`${PREFIX}.title`)}</h1>
                </div>
                <div class="result-body">
                    <div class="box-editable-area box-editable-spacing">
                        <p>
                            {#if  appState.errorCode === undefined}
                                {@html $_(`${PREFIX}.GENERIC.text`)}
                            {:else }
                                {@html $_(`${PREFIX}.${appState.errorCode}.text`)}
                            {/if}
                            <br>
                            <br>
                            {@html $_(`${PREFIX}.contact`,{values: {number:$_(`contactNumber`)}})}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<style>

    @media (max-width: 767px){
        .mobile-result-view {
            margin-top: 10vh;
            height: 90vh;
        }
    }
</style>