<script>

    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';
    import Button from "./Button.svelte";
    import Modal from "../elements/Modal.svelte";
    import AutoSubForm from "./AutoSubForm.svelte";
    import CircleStep from "../elements/CircleStep.svelte";
    import {closeModal, openModal} from '../sveltekit';

    import SimplePage from "../layout/SimplePage.svelte";
    import Loader from "../elements/Loader.svelte";

    const formEventDispatcher = createEventDispatcher();

    var currentIdx = {stepIdx: 0, taskIdx: 0};
    var pathLength;
    var formDescriptor;
    var confirmModal;

    var ready;

    $:currentStepTasks = path[currentIdx.stepIdx] === undefined ? [] : path[currentIdx.stepIdx].tasks;
    $:formDescriptor = currentStepTasks[currentIdx.taskIdx].form;
    $:formTitle = currentStepTasks[currentIdx.taskIdx].subtitle;

    var currentStepName;
    var currentTaskName;

    export let title = '';
    export let progress_values_map = {};
    export let progress_validation_map = {};
    export let path = {};
    export let submitted = false;

    let taskCompleteMap = {};
    let stepCompleteMap = {};

    $: if (!ready) {
        setTimeout(() => {
            ready = true;
        }, 1);
    }

    $: if (submitted) {

        closeModal(confirmModal);
    }

    onMount(() => {

        currentIdx.stepIdx = 0;
        currentIdx.taskIdx = 0;
        pathLength = path.length;
        ready = false;

        path.forEach(step => {
            progress_values_map[step.name] = {};
            progress_validation_map[step.name] = {};
            taskCompleteMap[step.name] = {};
            stepCompleteMap[step.name] = false;
            step.tasks.forEach(task => {
                progress_values_map[step.name][task.name] = {};
                progress_validation_map[step.name][task.name] = {};
                taskCompleteMap[step.name][task.name] = false;
            });
        });

        updateStateChartVariables();

    });


    function updateStateChartVariables() {
        currentStepName = path[currentIdx.stepIdx].name;
        currentTaskName = path[currentIdx.stepIdx].tasks[currentIdx.taskIdx].name;
    }


    function forward() {
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
        submit_running = true;
        formEventDispatcher('submit', e.detail);
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

    $:{
        if (typeof currentStepName !== "undefined") {
            let _value = Object.values(taskCompleteMap[currentStepName]).every(item => item);
            stepCompleteMap[currentStepName] = _value;
        }
    }

    let valid;
    let invalid;

    let submit_running = false;

</script>

<style></style>

<SimplePage>

{#if !submitted}
<div class="container">
    <div class="row">
        <div class="col-8">
            <a href="javascript:void(0)" class="user-anagrafica-auth-area ownerselector">
                <span class="user-data">{title}</span>
            </a>
        </div>
    </div>
</div>

<div class="sectiontitle">
    {path[currentIdx.stepIdx].subtitle}
</div>

{#if currentStepTasks.length > 0}
<div class="container container-stepper-circle">
    <div class="circle-stepper-horizontal">
        {#each currentStepTasks as task, t}
            <CircleStep step={currentStepName} active={currentIdx.taskIdx === t || (taskCompleteMap[currentStepName] ? taskCompleteMap[currentStepName][task.name] : false)} bind:done={taskCompleteMap[currentStepName]} taskName={task.name} t={t} title={task.title} on:click={goToTask}>
            </CircleStep>
        {/each}
    </div>
</div>
{/if}

{/if}

{#if ready && !submitted}
<AutoSubForm title={formTitle} {formDescriptor} bind:valid={taskCompleteMap[currentStepName][currentTaskName]} bind:values_map={progress_values_map[currentStepName][currentTaskName]} bind:validation_map={progress_validation_map[currentStepName][currentTaskName]} on:change={handleChange} on:validation={handleNewValidation}/>
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

{#if !submitted}
<div class="bottom-buttons">
    <a href="javascript:void(0)" class="btn btn-default absolute-action-left" on:click={backward} class:disabled={currentIdx.stepIdx === 0 && currentIdx.taskIdx === 0}>indietro</a>
    <a href="javascript:void(0)" class="btn btn-yellow absolute-action" on:click={forward} class:disabled={currentIdx.stepIdx === path.length - 1 && currentIdx.taskIdx === currentStepTasks.length - 1}>prosegui</a>
    <a href="javascript:void(0)" class="btn btn-yellow" on:click={()=>openModal(confirmModal)} >invia</a>
</div>
{/if}

<Modal bind:modalElement={confirmModal} title="Conferma invio dati" closeButton={false}>
    {#if submit_running}
        <div class="modauth-text">Invio dei dati in corso</div>
        <Loader/>
    {/if}
    {#if !submit_running}
        <div class="modauth-text"> Confermi l'invio dei dati?</div>
        <div class="modauth-buttons">
            <Button name={'Sì'} ordinality="2" size="2"
                    on:click={submit}
                    disabled={submit_running}/>
            <Button name={'No'} ordinality="1" size="2"
                    on:click={()=>closeModal(confirmModal)}
                    disabled={submit_running}/>
        </div>
    {/if}
</Modal>