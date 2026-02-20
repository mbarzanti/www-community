<script>
    import {afterUpdate} from 'svelte';
    import {jQuery} from '../libs/jquery';
    export let showModal;

    afterUpdate(() => {
        if(showModal) {
            jQuery('#loading-spinner-modal').modal('show');
        } else {
            jQuery('#loading-spinner-modal').modal('hide');
        }
    });
</script>
<div class="modal modal-spinner fade" id="loading-spinner-modal" tabindex="-1" role="dialog" aria-labelledby="loading-spinner-modal">
    <div class="modal-dialog modal-xs" style="margin-top: 168.5px;">
        <div class="modal-content">
            <h3>Caricamento...</h3>
            <img src="/risorse_dt/condivise/immagini/generiche/spinner_bianco.gif">
        </div>
    </div>
</div>