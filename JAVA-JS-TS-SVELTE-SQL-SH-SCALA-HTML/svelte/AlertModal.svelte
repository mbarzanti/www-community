<script>
    import {afterUpdate} from 'svelte';
    import {tick} from "svelte";


    import {getUniqueId} from "../../suit/uikit/utils";

    export let showModal = false;
    export let text = "Caricamento in corso";
    export let title = "Errore";
    export let id=getUniqueId();
    export let closeButton = true;
    export let onClick = undefined;

    afterUpdate(() => {
        if (showModal) {
            jQuery("#" + id).modal('show');
        } else {
            jQuery("#" + id).modal('hide');
        }
    });

    function close(){
        showModal = false;
    }
</script>

<div class="stylemodal-pit modal-locfinder modal fade"
     id="{id}" tabindex="-1" role="dialog" aria-labelledby="modalAuthLabel" aria-hidden="true"
     data-backdrop="static" data-keyboard={false}>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <div class="modhead">
                    {#if closeButton}
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" on:click={close}></button>
                    {/if}
                    <div class="modhead-title">{title}</div>
                </div>
                <div class="modal-padd">
                    <div class="modauth-text">
                        {@html text}
                    </div>
                    <div class="modauth-buttons">
                        {#if onClick}
                            <a href="javascript:void(0)" class="btn btn-yellow" data-dismiss="modal" aria-label="Close" on:click={async ()=>{close(); await tick(); await onClick()}}>ok</a>
                        {:else }
                            <a href="javascript:void(0)" class="btn btn-yellow" data-dismiss="modal" aria-label="Close" on:click={close}>ok</a>
                        {/if}
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>