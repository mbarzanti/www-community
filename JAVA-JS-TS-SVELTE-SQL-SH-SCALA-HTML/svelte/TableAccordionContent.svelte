<script>

    import TableAccordionAsideMenu from './TableAccordionAsideMenu.svelte';
    import TableAccordionTabContentTitle from "./TableAccordionTabContentTitle.svelte";
    import TableAccordionTabContent from "./TableAccordionTabContent.svelte";
    import jquery from 'jquery';
    import {onMount} from "svelte";
    import Button from "../../button/Button.svelte";
    import Modal from "../../elements/Modal.svelte";
    import {openModal,closeModal} from "../../../utils";
    import Loader from "../../elements/Loader.svelte";
    import {getValidJSON} from "../../../../commons/utils";
    import {ENTRIES, STRINGS} from '../../resources';

    export let form;
    export let asideTitle;
    export let table_visibility_map = {};
    export let table_validation_map = {};
    export let table_values_map = {};
    export let completedTasks = {};
    export let ignoreDirty = false;
    export let tableId;
    export let submitData;
    export let submitEnabled=false;

    export let componentsMap;
    export let options = {};

    let RES = Object.assign({}, STRINGS.TABLE);
    let confirmModal;
    let actionError = false;
    let actionWarningTitle;
    let actionWarningText;
    const modalStates = {
        "confirm": 0,
        "running": 1,
        "error": 2,
        "success": 3
    }
    let modalState = modalStates["confirm"];

    function validateData(){
        if(Object.keys(completedTasks).every((taskId)=>completedTasks[taskId])){
            actionError = false;
            modalState = modalStates["confirm"];
        } else {
            actionError = true;
            actionWarningTitle = RES[ENTRIES.TABLE.MODAL.VALIDATION.TITLE];
            actionWarningText = RES[ENTRIES.TABLE.MODAL.VALIDATION.TEXT];
            ignoreDirty = true;
        }
        openModal(confirmModal);
    }
    async function submit(){
        modalState = modalStates["running"];
        let success = false;
        try{
            success = await submitData();
        } catch (e){
            modalState = modalStates["error"];
            console.log(e);
        }
        if(success){
            modalState = modalStates["success"];
        } else {
            modalState = modalStates["error"];
        }
    }

    // avoid collapse active panel
    jquery("document").ready(function () {
        jquery("#" + tableId + " .asidetabsmenu-ncb .label-link").on('click', function (e) {
            if (jquery(this).attr("aria-expanded") === "true") {
                let targetId = e.currentTarget.dataset.target;
                let count = jQuery("#" + tableId + "-tasks .collapse").length;
                for (let i = 0; i < count; i++) {
                    const tabId = `${tableId}-${i}`;
                    let element = jQuery("div#" + tabId + ".collapse");
                    element.removeClass("show")
                    jQuery("a.label-link").addClass("collapsed")
                    if (targetId === tabId) {
                        element.addClass("show")
                    }
                }
                e.preventDefault();
                e.stopPropagation();
                jquery(this).removeClass("collapsed")
                jquery(this).addClass("current-view");
            }

        })
    })
    onMount(() => {
        if (options.resources && Object.keys(options.resources).length >= 0) {
            Object.keys(options.resources).forEach(
                (item) => {
                    RES[item] = options.resources[item];
                }
            )
        }
        jQuery(`div#${tableId}-0.collapse`).addClass("show");
    })
</script>


<div class="product-configurator">
    <div class="row product-conf-row product-conf-row-full">
        <div class=" config-content">
            <div class="padd-conf-tab aside-tab aside-tab-full ">

                <TableAccordionAsideMenu form={form} {tableId} title={asideTitle} valid_map={completedTasks}/>

                <div class="content-aside content-aside--fullw" id="{tableId}-tasks">
                    {#each form.tasks as task, t (task.name)}
                        <div id="{tableId}-{t}" class="collapse" data-parent="#{tableId}-tasks">
                            <TableAccordionTabContentTitle task={task}/>
                            <hr class="forced-fullw-row mb40">
                            <TableAccordionTabContent
                                    bind:currentStepName={form.name}
                                    bind:currentTaskName={task.name}
                                    bind:module_values_map={table_values_map[task.name]}
                                    bind:module_validation_map={table_validation_map[task.name]}
                                    bind:module_visibility_map={table_visibility_map[task.name]}
                                    bind:table_values_map={table_values_map}
                                    bind:componentsMap={componentsMap}
                                    task={task.form}
                                    bind:valid={completedTasks[task.name]}
                                    bind:ignoreDirty={ignoreDirty}
                                    on:change/>
                        </div>
                    {/each}
                    {#if submitEnabled}
                        <div class="accordion-buttons">
                            <Button on:click={validateData} name={RES[ENTRIES.TABLE.BUTTONS.SAVE]} label={RES[ENTRIES.TABLE.BUTTONS.SAVE]} />
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>
{#if submitEnabled}
    <Modal bind:modalElement={confirmModal} title={actionError ? actionWarningTitle : RES[ENTRIES.TABLE.MODAL.SEND.TITLE]} closeButton={false}>
        {#if actionError}
            <div class="modauth-text">{actionWarningText}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} label={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} ordinality="1" size="2"
                        on:click={()=>{closeModal(confirmModal);}}/>
            </div>
        {:else if modalState === modalStates["running"]}
            <div class="modauth-text">{RES[ENTRIES.TABLE.MODAL.SEND.LOADING]}</div>
            <Loader/>
        {:else if modalState === modalStates["confirm"]}
            <div class="modauth-text">{RES[ENTRIES.TABLE.MODAL.SEND.CONFIRM]}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.CANCEL]} label={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.CANCEL]} ordinality="2" size="2"
                        on:click={()=>closeModal(confirmModal)}
                        disabled={modalState !== modalStates["confirm"]}/>
                <Button name={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.CONFIRM]} label={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.CONFIRM]} ordinality="1" size="2"
                        on:click={submit}
                        disabled={modalState !== modalStates["confirm"]}/>
            </div>
        {:else if modalState === modalStates["error"]}
            <div class="modauth-text">{RES[ENTRIES.TABLE.MODAL.SEND.ERROR]}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} label={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} ordinality="1" size="2"
                        on:click={()=>closeModal(confirmModal)}
                        disabled={modalState !== modalStates["error"]}/>
            </div>
        {:else if modalState === modalStates["success"]}
            <div class="modauth-text">{RES[ENTRIES.TABLE.MODAL.SEND.SUCCESS]}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} label={RES[ENTRIES.TABLE.MODAL.SEND.BUTTONS.OK]} ordinality="1" size="2"
                        on:click={()=>closeModal(confirmModal)}
                        disabled={modalState !== modalStates["success"]}/>
            </div>
        {/if}
    </Modal>
{/if}



<style>
    .accordion-buttons {
        margin-bottom: -60px;
        text-align: right;
        position: relative;
        bottom: 80px;
        right: 30px;
    }
</style>