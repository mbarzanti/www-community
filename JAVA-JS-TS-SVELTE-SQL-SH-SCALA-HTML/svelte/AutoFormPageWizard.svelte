<script>

    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';
    import Button from "./Button.svelte";
    import Modal from "./../elements/Modal.svelte";
    import AutoSubForm from "./AutoSubForm.svelte";
    import CircleStep from "./../elements/CircleStep.svelte";
    import {closeModal, openModal} from './../sveltekit';

    import SimplePage from "./../layout/SimplePage.svelte";
    import Loader from "../elements/Loader.svelte";
    import api from "./../../api/api";

    const formEventDispatcher = createEventDispatcher();

    export let title = '';
    export let enableEditCtxBtn = false;
    export let editCtxBtnLbl = '';
    export let wizard_values_map = {};
    export let wizard_validation_map = {};
    export let wizard_visibility_map = {};
    export let path = {};
    export let submitted = false;
    export let flowRestricted = false;
    export let readOnly = false;
    export let externalStepper=false;
    export let externalButtons=false;

    export let buttons = {}
    export let externalConfirm=false;
    export let options={};
    import {ENTRIES, STRINGS} from './resources';

    var currentIdx = {stepIdx: 0, taskIdx: 0};
    var pathLength;
    var formDescriptor;
    var confirmModal;
    let complete = false;
    var ready;

    let currentStepTasks;
    let RES = Object.assign({}, STRINGS.WIZARD);

    $:currentStepTasks = path[currentIdx.stepIdx] === undefined ? [] : path[currentIdx.stepIdx].tasks;
    $:formDescriptor = currentStepTasks[currentIdx.taskIdx].form;
    $:formTitle = currentStepTasks[currentIdx.taskIdx].subtitle;
    //$:complete = checkCompleted(stepCompleteMap);
    let ignoreDirty = false;

    var currentStepName;
    var currentTaskName;




    let taskCompleteMap = {};
    let stepCompleteMap = {};
    let actionError = false;
    let actionWarningTitle;
    let actionWarningText;
    let formTitle;


    const modalStates = {
        "confirm":0,
        "running":1,
        "error":2
    }

    let modalState = modalStates["confirm"];


    $: if (!ready) {
        setTimeout(() => {
            ready = true;
        }, 1);
    }

    $: if (submitted && modalState === modalStates["running"]) {
        modalState = modalStates["confirm"];
        closeModal(confirmModal);
    }

    onMount(() => {
        currentIdx.stepIdx = 0;
        currentIdx.taskIdx = 0;
        pathLength = path.length;
        ready = false;
        if (options.resources && Object.keys(options.resources).length >= 0) {
            Object.keys(options.resources).forEach(
                (item) => {
                    RES[item] = options.resources[item];
                }
            )
        }

        path.forEach(step => {
            wizard_values_map[step.name] = {};
            wizard_validation_map[step.name] = {};
            wizard_visibility_map[step.name] = {};
            taskCompleteMap[step.name] = {};
            stepCompleteMap[step.name] = false;
            step.tasks.forEach(task => {
                wizard_values_map[step.name][task.name] = {};
                wizard_validation_map[step.name][task.name] = {};
                wizard_visibility_map[step.name][task.name] = {};
                taskCompleteMap[step.name][task.name] = false;
            });
        });

        /*setTimeout(() => {
            api.customerSearch.prePopulationHandler(currentStepName, currentTaskName, wizard_values_map, undefined, refresh);
            ready = true;
        }, 500);*/
        updateStateChartVariables();
    });


    function updateStateChartVariables() {
        currentStepName = path[currentIdx.stepIdx].name;
        currentTaskName = path[currentIdx.stepIdx].tasks[currentIdx.taskIdx].name;
    }

    function checkCurrentStepValidations() {
        //console.debug(wizard_validation_map[currentStepName][currentTaskName]);
        //console.debug(wizard_visibility_map[currentStepName][currentTaskName]);
        let taskValidations = wizard_validation_map[currentStepName][currentTaskName];
        let taskVisibility = wizard_visibility_map[currentStepName][currentTaskName];
        for (const key of Object.keys(wizard_validation_map[currentStepName][currentTaskName])) {

            let validationValue = taskValidations[key];
            let visibilityValue = taskVisibility[key];

            if (visibilityValue) {
                if (typeof validationValue !== 'undefined' && validationValue !== null && !validationValue) {
                    return false;
                }
            }
        }

        return true;
    }


    function checkSubmit() {
        if(!checkCurrentStepValidations()) {
            jQuery([document.documentElement, document.body]).animate({
                scrollTop: jQuery("body").offset().top
            }, 500);
            ignoreDirty = true;
            return;
        } else {
            ignoreDirty = false;
        }
        modalState = modalStates["confirm"];
        if(!externalConfirm){
            openModal(confirmModal);
        } else {
            submit({detail:{}});
        }
    }

    function forward() {
        if(!checkCurrentStepValidations() && flowRestricted) {
            jQuery([document.documentElement, document.body]).animate({
                scrollTop: jQuery("body").offset().top
            }, 500);
            ignoreDirty = true;
            return;
        } else {
            ignoreDirty = false;
        }

        if (currentIdx.taskIdx < currentStepTasks.length - 1) {
            currentIdx.taskIdx = (currentIdx.taskIdx < currentStepTasks.length - 1) ? currentIdx.taskIdx + 1 : currentIdx.taskIdx;
        } else if (currentIdx.stepIdx < pathLength - 1) {
            currentIdx.stepIdx = (currentIdx.stepIdx < pathLength - 1) ? currentIdx.stepIdx + 1 : currentIdx.stepIdx;
            currentIdx.taskIdx = 0;
        }

        updateStateChartVariables();
        ready = false;
    }

    function backward() {
        ignoreDirty = false;
        if (currentIdx.taskIdx > 0) {
            currentIdx.taskIdx = (currentIdx.taskIdx > 0) ? currentIdx.taskIdx - 1 : currentIdx.taskIdx;
        } else if (currentIdx.stepIdx > 0) {
            currentIdx.stepIdx = (currentIdx.stepIdx > 0) ? currentIdx.stepIdx - 1 : currentIdx.stepIdx;
            currentIdx.taskIdx = path[currentIdx.stepIdx].tasks.length - 1;
        }

        updateStateChartVariables();
        ready = false;
    }

    function submit(e) {
        modalState = modalStates["running"];
        formEventDispatcher('submit', {detail: e.detail, fail: submitFailed});
    }

    function submitFailed(){
        modalState = modalStates["error"];
    }

    function goToStep(s) {
        if (currentIdx.stepIdx === s) {
            return;
        }
        currentIdx.stepIdx = s;
        currentIdx.taskIdx = 0;

        updateStateChartVariables();

        ready = false;
    }

    function goToTask(e) {
        let t = e.detail.t;
        if (currentIdx.taskIdx === t) {
            return;
        }

        currentIdx.taskIdx = t;
        updateStateChartVariables();

        ready = false;
    }

    function handleChange(e) {
        formEventDispatcher('change', {});
    }
    
    function handleNewValidation(e) {
        taskCompleteMap[currentStepName][currentTaskName] = e.detail;
    }
    

    function dispatchEditCtx() {
        formEventDispatcher('editCtx', {});
    }

    function handleComplete() {
        formEventDispatcher('complete', {});
    }

    function handleActionRequest(e) {
        //formEventDispatcher('actionRequest', e.detail);
        if( e.detail.handler ){
            e.detail.handler(currentStepName, currentTaskName, wizard_values_map, e.detail.value, refresh)
        }
    }

    function refresh(success=true, actionWarningTitleArg=undefined, actionWarningTextArg=undefined){
        if(!success){
            // MODAL VIEW
            actionWarningTitle = actionWarningTitleArg;
            actionWarningText = actionWarningTextArg;
            actionError = true;
            openModal(confirmModal);
        }
        ready = false;
    }

    $:{
        if (typeof currentStepName !== "undefined") {
            let _value = Object.values(taskCompleteMap[currentStepName]).every(item => item);
            stepCompleteMap[currentStepName] = _value;
        }
    }

    function checkCompleted(stepCompleteMap){
        if( typeof stepCompleteMap === 'undefined' ){
            return false;
        }
        return Object.keys(stepCompleteMap).every(
            (stepName) => {
                return stepCompleteMap[stepName];
            }
        )

    }

    let valid;
    let invalid;

</script>

<style></style>

<SimplePage ignore={externalStepper}>

{#if !submitted}
    {#if !externalStepper}
        <div class="container">
            <div class="row">
                <div class="col-8">
                    <a href={undefined} class="user-anagrafica-auth-area ownerselector">
                        <span class="user-data">{title}</span>
                    </a>
                </div>
                {#if enableEditCtxBtn}
                    <div class="col-4">
                        <a href={undefined} class="btn btn-primary btn-small" on:click={dispatchEditCtx}>{editCtxBtnLbl}</a>
                    </div>
                {/if}
            </div>
            <div class="stepper-navigation">
                <ul class="stepper">
                    {#each path as step, s}
                        <li class="stepper-item" class:active={!(stepCompleteMap[step.name] ? stepCompleteMap[step.name] : false) && currentIdx.stepIdx === s} class:current={!(stepCompleteMap[step.name] ? stepCompleteMap[step.name] : false) && currentIdx.stepIdx === s} class:settled={stepCompleteMap[step.name] ? stepCompleteMap[step.name] : false} >
                            <a href={!flowRestricted ? "javascript:void(0)" : undefined} class="step-link" on:click={!flowRestricted ? () => goToStep(s) : undefined}>{step.title}</a>
                        </li>
                    {/each}
                </ul>
            </div>
        </div>

        <div class="sectiontitle">
            {path[currentIdx.stepIdx].subtitle}
        </div>

        {#if currentStepName && currentStepTasks.length > 1}
            <div class="container container-stepper-circle">
                <div class="circle-stepper-horizontal">
                    {#each currentStepTasks as task, t}
                        <CircleStep clickable={!flowRestricted} step={currentStepName} active={currentIdx.taskIdx === t || (taskCompleteMap[currentStepName] ? taskCompleteMap[currentStepName][task.name] : false)} bind:done={taskCompleteMap[currentStepName]} taskName={task.name} t={t} title={task.title} on:click={goToTask}>
                        </CircleStep>
                    {/each}
                </div>
            </div>
        {/if}
    {/if}
{/if}

{#if ready && !submitted}
<AutoSubForm title={formTitle} {formDescriptor} bind:ignoreDirty={ignoreDirty}
    bind:valid={taskCompleteMap[currentStepName][currentTaskName]}
    bind:values_map={wizard_values_map[currentStepName][currentTaskName]}
    bind:values_complete_map={wizard_values_map}
    bind:currentStepName={currentStepName}
    bind:currentTaskName={currentTaskName}
    bind:visibility_map={wizard_visibility_map[currentStepName][currentTaskName]}
    bind:validation_map={wizard_validation_map[currentStepName][currentTaskName]}
             readOnly={readOnly}
    on:change={handleChange}
    on:validation={handleNewValidation}
    on:actionRequest={handleActionRequest}
/>
{/if}
<!--
{#if submitted}
    <div class="container width960 container-stepper-circle">
        <div class="padd-conf-tab attivazione-page sm-activation-recap tac">
            <h1 class="sm-title-success">L'operazione conclusa<br> con successo</h1>
            <p class="sm-act-notice">Complimenti! <br>L'identificazione del cliente avvenuta con successo</p>
            <div class="modauth-buttons mb20">
                <a href="javascript:void(0)" class="btn " on:click={handleComplete}>Prosegui</a>
            </div>
        </div>

    </div>
{/if}
-->
</SimplePage>
{#if !externalButtons}
    {#if !submitted}
    <div class="bottom-buttons fixed fixed-bottom"  style="position:fixed;z-index: 999;">
        <slot name="buttons"/>
        {#if currentIdx.stepIdx === 0 && currentIdx.taskIdx > 0 || currentIdx.stepIdx > 0}
            <a href={undefined} class="btn btn-default absolute-action-left" on:click={backward} class:disabled={currentIdx.stepIdx === 0 && currentIdx.taskIdx === 0}>{(buttons.back && buttons.back.label) ? buttons.back.label : RES[ENTRIES.WIZARD.BUTTONS.BACKWARD]}</a>
        {/if}
        {#if !(currentIdx.stepIdx === path.length - 1 && currentIdx.taskIdx === currentStepTasks.length - 1)}
            <a href={undefined} class="btn btn-yellow absolute-action" on:click={forward} class:disabled={(currentIdx.stepIdx === path.length - 1 && currentIdx.taskIdx === currentStepTasks.length - 1)}>{(buttons.continue && buttons.continue.label) ? buttons.continue.label : RES[ENTRIES.WIZARD.BUTTONS.CONTINUE]}</a>
        {/if}
        {#if !readOnly && currentIdx.stepIdx === path.length - 1 && currentIdx.taskIdx === currentStepTasks.length - 1}
            <a href={undefined} class="btn btn-yellow" on:click={checkSubmit}>{(buttons.submit && buttons.submit.label) ? buttons.submit.label : RES[ENTRIES.WIZARD.BUTTONS.SEND]}</a>
        {/if}
    </div>
    {/if}

    <Modal bind:modalElement={confirmModal} title={actionError ? actionWarningTitle : RES[ENTRIES.WIZARD.MODAL.SEND.TITLE]} closeButton={false}>
        {#if actionError}
            <div class="modauth-text">{actionWarningText}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.WIZARD.MODAL.SEND.BUTTONS.OK]} ordinality="1" size="2"
                        on:click={()=>{closeModal(confirmModal);actionError = false;}}/>
            </div>
        {:else if modalState === modalStates["running"]}
            <div class="modauth-text">{RES[ENTRIES.WIZARD.MODAL.SEND.LOADING]}</div>
            <Loader/>
        {:else if modalState === modalStates["confirm"]}
            <div class="modauth-text">{RES[ENTRIES.WIZARD.MODAL.SEND.CONFIRM]}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.WIZARD.MODAL.SEND.BUTTONS.CANCEL]} ordinality="2" size="2"
                        on:click={()=>closeModal(confirmModal)}
                        disabled={modalState !== modalStates["confirm"]}/>
                <Button name={RES[ENTRIES.WIZARD.MODAL.SEND.BUTTONS.CONFIRM]} ordinality="1" size="2"
                        on:click={submit}
                        disabled={modalState !== modalStates["confirm"]}/>
            </div>
        {:else if modalState === modalStates["error"]}
            <div class="modauth-text">{RES[ENTRIES.WIZARD.MODAL.SEND.ERROR]}</div>
            <div class="modauth-buttons">
                <Button name={RES[ENTRIES.WIZARD.MODAL.SEND.BUTTONS.OK]} ordinality="1" size="2"
                        on:click={()=>closeModal(confirmModal)}
                        disabled={modalState !== modalStates["error"]}/>
            </div>
        {/if}
    </Modal>
{/if}