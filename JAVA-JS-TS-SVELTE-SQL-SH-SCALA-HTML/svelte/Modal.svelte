<script>
    import {createEventDispatcher, onMount} from 'svelte';

    // Oggetto che si occupa di rilanciare gli eventi
    const eventDispatcher = createEventDispatcher();

    // Id HTML della modale
    export let modalElement = undefined;
    // Titolo della modale
    export let title;

    export let closeButton = true;

    onMount(() => {
        jQuery(modalElement).on('show.bs.modal', function () {
            // Evento rilanciato in fase di apertura della modale
            eventDispatcher('show');
        });
        jQuery(modalElement).on('shown.bs.modal', function () {
            // Evento rilanciato una volta che la modale e' aperta
            eventDispatcher('shown');
        });
        jQuery(modalElement).on('hide.bs.modal', function () {
            // Evento rilanciato in fase di chiusura della modale
            eventDispatcher('hide');
        });
        jQuery(modalElement).on('hidden.bs.modal', function () {
            // Evento rilanciato una volta che la modale e' chiusa
            eventDispatcher('hidden');
        });
    });
</script>

<div class="stylemodal-pit modal-locfinder modal fade"
     bind:this={modalElement} tabindex="-1" role="dialog" aria-labelledby="modalAuthLabel" aria-hidden="true"
     data-backdrop="static" data-keyboard={false}>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <div class="modhead">
                    {#if closeButton}
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"></button>
                    {/if}
                    <div class="modhead-title">{title}</div>
                </div>
                <div class="modal-padd">
                    <slot></slot>
                </div>
            </div>
        </div>
    </div>
</div>