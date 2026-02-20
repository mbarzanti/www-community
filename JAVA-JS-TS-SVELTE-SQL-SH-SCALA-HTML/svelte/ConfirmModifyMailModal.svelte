<script>
    import {afterUpdate, tick} from 'svelte';

    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    export let id;
    export let title;
    export let showModal;

    afterUpdate(async () => {
        if(showModal) {
            window.$('#' + id).modal('show');
        } else {
            window.$('#' + id).modal('hide');
            await tick()
            dispatch('close');
        }
    });

    async function closeModal() {
        showModal = false;
        await tick()
        dispatch('close');
    }

    function confirm(){
        closeModal()
        dispatch('modify')
    }

</script>

<div data-backdrop="static" class="modal fade" {id} tabindex="-1" role="dialog" aria-labelledby={id}>
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">{title}</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-lg-12">
                        <p>Sicuro di voler modificare l'email?</p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" on:click={confirm}>Prosegui</button>
                <button class="btn" on:click={closeModal}>Chiudi</button>
            </div>
        </div>
    </div>
</div>