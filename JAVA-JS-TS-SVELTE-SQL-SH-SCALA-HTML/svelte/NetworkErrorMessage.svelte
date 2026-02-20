<script>
    import AlertModal from "../../UiKitLite/common/AlertModal.svelte";

    import {raiseNetworkError} from '../../stores';

    function closeModal() {
        raiseNetworkError.set(false);
    }

</script>

<AlertModal showModal={$raiseNetworkError} id="network-error-modal" title="Errore di connessione" on:close={closeModal}>
    <div class="row">
        <div class="col-lg-16 innerspacer-xs-15">
            Errore di comunicazione, controlla la tua connessione internet e riprova. Se il problema persiste ricarica la pagina.
        </div>
    </div>
</AlertModal>