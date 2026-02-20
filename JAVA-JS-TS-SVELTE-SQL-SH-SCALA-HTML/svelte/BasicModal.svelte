<script>

    export let closeButton;
    export let id;
    export let noHeader;
    export let width;


    let src ='./images/contractualization/scontiposte2x.png';

</script>


<div aria-labelledby="loading-modal-custom" class="modal fade" {id} role="dialog" tabindex="-1">
    {#if !noHeader}
        <div class="modal-dialog modal-md {width}">
            <div class="modal-content">
                <div class="modal-header">
                    {#if closeButton}
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                class="close-icon">×</span>
                        </button>
                    {/if}
                    <h4 class="modal-title" id="myModalLabel"><img class="logo-image-pi-small"
                                                                   src="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-small.png"
                                                                   srcset="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-small@2x.png 2x">
                    </h4>
                </div>
                <slot></slot>
            </div>
        </div>
    {:else}
        <div class="modal-dialog modal-md {width}">
            <div class="modal-content">
                <div class="row">
                    <div class="col text-center">
                        <img class="spacer-xs-bottom-40 spacer-xs-top-30" src={src} width="40%" alt="first"/>
                    </div>
                </div>
                <slot></slot>
            </div>
        </div>
    {/if}
</div>

<style>
.largeSizeModal{
    width: 780px!important;
}
</style>