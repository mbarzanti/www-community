<script>
    import {afterUpdate, tick} from 'svelte';
    import BasicModal from "./BasicModal.svelte";
    import {getUniqueId} from "../../utils";
    import {MODAL_ACTIONS, TYPE} from "./modals";

    export let showModal = false;
    export let progress = 0;
    export let showProgress = false;
    export let type;
    export let title;
    export let subtitle;
    export let width;
    export let customModal;
    export let closeButton;
    export let buttons;
    export let imgSrc;
    export let noHeader;
    export let id=`digital-generic-modal-${getUniqueId()}`;


    afterUpdate(() => {
        if (showModal) {
            jQuery(`#${id}`).modal('show');
        } else {
            jQuery(`#${id}`).modal('hide');
        }
    });


    async function onClickModal(action) {
        if (action === MODAL_ACTIONS.CLOSE) {
            showModal = false;
            await tick();
        } else {
            showModal = false;
            await tick();
            action();
        }
    }

</script>

{#if type === TYPE.CUSTOM && customModal}
    <BasicModal closeButton={closeButton} {id} noHeader={noHeader} width={width}>
        <div class="modal-body">
            <svelte:component this={customModal}/>
        </div>
        <div class="modal-footer">
            <div class="col text-center">
            {#if buttons}
                {#each buttons as button, b}
                    <button class="btn btn-{button.color} spacer-xs-right-20" on:click={onClickModal(button.action)}>{button.label}</button>
                {/each}
            {/if}
            </div>
        </div>
    </BasicModal>
{/if}
{#if type === TYPE.DEFAULT}
    <BasicModal closeButton={closeButton} {id} width={width}>
        <div class="modal-body">
            <div class="row">
                <div class="col-sm-12 text-xs-center">
                    <h3 class="spacer-xs-bottom-30">{title}</h3>
                    <p class="spacer-xs-bottom-20">{subtitle}</p>
                    {#if showProgress}
                        <div class="progress progress-striped active spacer-xs-bottom-0">
                            <div class="progress-bar progress-bar-evidence" role="progressbar"
                                 aria-valuenow="{progress}" aria-valuemin="{progress}" aria-valuemax="100"
                                 style="width: {progress}%;">
                                {progress}%
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
        <div class="modal-footer">
            {#if buttons}
                {#each buttons as button, b}
                    <button class="btn btn-{button.color} spacer-xs-right-20" on:click={onClickModal(button.action)}>{button.label}</button>
                {/each}
            {/if}
        </div>
    </BasicModal>
{/if}
{#if type === TYPE.RESULT}
    <BasicModal closeButton={closeButton} {id} width={width}>
        <div class="modal-body">
            <div class="row">
                <div class="col-sm-12 text-xs-center">
                    <h3 class="spacer-xs-bottom-30">{title}</h3>
                    <p class="spacer-xs-bottom-20">{subtitle}</p>
                    <img src={imgSrc}>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            {#if buttons}
                {#each buttons as button, b}
                    <button class="btn btn-{button.color} spacer-xs-right-20" on:click={onClickModal(button.action)}>{button.label}</button>
                {/each}
            {/if}
        </div>
    </BasicModal>
{/if}
{#if TYPE.SPINNER}
    <div aria-labelledby="loading-spinner-modal" class="modal modal-spinner fade" {id}
         role="dialog" data-backdrop="static" data-keyboard={false}
         tabindex="-1">
        <div class="modal-dialog modal-xs" style="margin-top: 168.5px;">
            <div class="modal-content">
                <h3>{title}</h3>
                <img src="/risorse_dt/condivise/immagini/generiche/spinner_bianco.gif">
            </div>
        </div>
    </div>
{/if}
