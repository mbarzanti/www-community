<script>
    import {afterUpdate} from 'svelte';
    import {jQuery} from '../libs/jquery';

    export let showModal = false;
    export let progress = 0;
    export let showProgress = false;

    afterUpdate(() => {
        if(showModal) {
            jQuery('#loading-modal').modal('show');
        } else {
            jQuery('#loading-modal').modal('hide');
        }
    });
</script>

<div class="modal fade" id="loading-modal" tabindex="-1" role="dialog" aria-labelledby="loading-modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Attendi...caricamento in corso</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-sm-12">
                        <p>L'operazione potrebbe richiedere qualche minuto.</p>
                        {#if showProgress}
                        <div class="progress progress-striped active spacer-xs-bottom-0">
                            <div class="progress-bar progress-bar-evidence" role="progressbar" aria-valuenow="{progress}" aria-valuemin="{progress}" aria-valuemax="100" style="width: {progress}%;">
                                {progress}%
                            </div>
                        </div>
                        {/if}
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>