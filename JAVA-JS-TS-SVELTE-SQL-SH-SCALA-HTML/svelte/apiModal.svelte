<script>
    import Button from "./../../SvelteKit/forms/Button.svelte";
    import Loader from "./../../SvelteKit/elements/Loader.svelte";
    import {createEventDispatcher, onMount} from 'svelte';

    import {getUniqueId} from "./../../SvelteKit/sveltekit";

    export let modal = undefined;
    export let modalTitle = "";
    export let modalBodyText = "";
    export let modalLoadingText = "";
    export let apiRunning = false;
    export let closeHandler = ()=>{};
    export let closeButton = false;

    const eventDispatcher = createEventDispatcher();

    onMount(() => {
        jQuery(modal).on('show.bs.modal', function () {
            // Evento rilanciato in fase di apertura della modale
            eventDispatcher('show');
        });
        jQuery(modal).on('shown.bs.modal', function () {
            // Evento rilanciato una volta che la modale e' aperta
            eventDispatcher('shown');
        });
        jQuery(modal).on('hide.bs.modal', function () {
            // Evento rilanciato in fase di chiusura della modale
            eventDispatcher('hide');
        });
        jQuery(modal).on('hidden.bs.modal', function () {
            // Evento rilanciato una volta che la modale e' chiusa
            eventDispatcher('hidden');
        });
    });

</script>

<script context="module">
    export function openModal(modalArg){
        jQuery(modalArg).modal('show');
        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
    }

    export function closeModal(modalArg){
        jQuery(modalArg).modal('hide');
        setTimeout(()=>{
            jQuery('body').removeClass('modal-open');
            jQuery('.modal-backdrop').remove();
        }, 500);

    }
</script>

<div class="stylemodal-pit modal-locfinder modal fade"
     bind:this={modal} tabindex="-1" role="dialog" aria-labelledby="modalAuthLabel" aria-hidden="true" id={getUniqueId("modal-")}
     data-backdrop="static" data-keyboard={false}>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <div class="modhead">
                    {#if closeButton}
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"></button>
                    {/if}
                    <div class="modhead-title">{modalTitle}</div>
                </div>
                <div class="modal-padd">
                    {#if apiRunning}
                        <div class="modauth-text">{modalLoadingText}</div>
                        <Loader/>
                    {/if}
                    {#if !apiRunning}
                            <div class="modauth-text">{modalBodyText}</div>
                        
                        <div class="modauth-buttons">
                            <Button name={'Ok'} ordinality="1" size="2"
                                    on:click={()=>{closeHandler();closeModal(modal);}}/>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>


<style>
.modauth-text {
    white-space: pre-wrap;
}
</style>