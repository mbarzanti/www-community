<script>

    import {onMount} from 'svelte';
    import CircleStep from "../../elements/CircleStep.svelte";
    import {getValidJSON} from "../../../../commons/utils";
    import TableAccordionContent from "./TableAccordionContent.svelte";

    export let accordionInfo;

    export let id;
    export let index;
    export let wizard_values_map = {};
    export let wizard_validation_map = {};
    export let wizard_visibility_map = {};
    export let path = [];
    export let submit = undefined;
    export let completed = false;

    export let componentsMap = {};

    export let flowRestricted = false;


    let currentIdx = {stepIdx: 0, taskIdx: 0};
    let submitEnabled = false;
    let ready =false;

    let currentStepTasks;

    $:currentStepTasks = path[currentIdx.stepIdx] === undefined ? [] : path[currentIdx.stepIdx].tasks;
    $:formTitle = currentStepTasks[currentIdx.taskIdx].subtitle;
    //$:complete = checkCompleted(stepCompleteMap);
    let ignoreDirty = false;

    let currentStepName;
    let currentTaskName;


    let taskCompleteMap = {};
    let stepCompleteMap = {};
    let formTitle;


    function init(){
        path.forEach(step => {
            wizard_values_map[step.name] = wizard_values_map[step.name] || {};
            wizard_validation_map[step.name] = wizard_validation_map[step.name] || {};
            wizard_visibility_map[step.name] =wizard_visibility_map[step.name] || {};
            taskCompleteMap[step.name] = {};
            stepCompleteMap[step.name] = false;
            componentsMap[step.name] = {};
            step.tasks.forEach(task => {
                wizard_values_map[step.name][task.name] = wizard_values_map[step.name][task.name] || {};
                wizard_validation_map[step.name][task.name] = wizard_validation_map[step.name][task.name] || {};
                wizard_visibility_map[step.name][task.name] = wizard_visibility_map[step.name][task.name] || {};
                taskCompleteMap[step.name][task.name] = false;

                initComponentsMap(step.name, task.name, task.form.values);
            });
        });
    }

    function initComponentsMap(stepName, taskName, taskFormValues){
        componentsMap[stepName][taskName] = {};
        taskFormValues.forEach(row => {
            if(Array.isArray(row) ){
                row.forEach( item => {
                        if(item.name){
                            componentsMap[stepName][taskName][item.name] = item;
                        }
                    }
                )
            } else if(row.name){
                componentsMap[stepName][taskName][row.name] = row;
            }
        });
    }

    onMount(() => {
        currentIdx.stepIdx = 0;
        currentIdx.taskIdx = 0;
        if(submit){
            submitEnabled = true;
        }
        init();
        updateStateChartVariables();

        if(typeof accordionInfo.collapsed !== "undefined" && !accordionInfo.collapsed){
            const tabElement = jQuery(`#${id}-tab`);
            const tabTopButtonElement = jQuery(`${id}-tab-top-button`);
            tabTopButtonElement.removeClass("collapsed");
            tabTopButtonElement.attr("aria-expanded", false);
            tabElement.addClass("show");
        }
        ready = true;

    });

    function goToTask(e) {
        let t = e.detail.t;
        if (currentIdx.taskIdx === t) {
            return;
        }

        currentIdx.taskIdx = t;
        updateStateChartVariables();
    }

    function updateStateChartVariables() {
        currentStepName = path[currentIdx.stepIdx].name;
        currentTaskName = path[currentIdx.stepIdx].tasks[currentIdx.taskIdx].name;
    }

    $:completed = Object.keys(stepCompleteMap).every(
        (stepId) => {
            return Object.keys(taskCompleteMap[stepId]).every(
                (taskId) => {
                    return taskCompleteMap[stepId][taskId];
                }
            )
        }
    );

    async function submitData() {
        return await submit(getValidJSON({
            wizard_values_map: wizard_values_map,
            wizard_validation_map: wizard_validation_map,
            wizard_visibility_map: wizard_visibility_map
        }));
    }
</script>


<div class="accordion  accordion-gestione-dati-aggiuntivi accordion-buoni" {id}>
    <div class="card">
        <div class="card-header">
            <button aria-expanded="true" class="button-acc-rel collapsed" data-target="#{id}-tab" id="{id}-tab-top-button" data-toggle="collapse"
                    type="button">
                <div class="relative-prod-conf">
                    <span class="cta-label">
                        <strong>{accordionInfo.title}</strong>
                        {#if accordionInfo.subTitle && accordionInfo.subTitle.length && typeof accordionInfo.subTitle !== "string"}
                            {#each accordionInfo.subTitle as subTitle}
                                <span>{subTitle}</span>
                            {/each}
                        {:else if accordionInfo.subTitle}
                            <span>{accordionInfo.subTitle}</span>
                        {/if}
                        {#if index}
                            <i class="circle-badge">{index}</i>
                        {/if}
                    </span>
                    <div class="prod-status" class:ok-status={completed} class:warning-status={!completed}></div>
                    <!-- attention-status -->
                </div>
            </button>
        </div>
        <div class="collapse" data-parent="#{id}" id="{id}-tab">
            <div class="card-body">
                {#if ready}
                    <!-- STEPPER -->
                    {#if !accordionInfo.externalStepper && currentStepName && path.length > 1}
                        <div class="container container-stepper-circle">
                            <div class="circle-stepper-horizontal">
                                <!-- STEP LIST -->
                                {#each path as step, t (step.name)}
                                    <CircleStep clickable={!flowRestricted} step={step.name}
                                                active={currentIdx.stepIdx === t || (stepCompleteMap[currentStepName])}
                                                bind:done={stepCompleteMap[currentStepName]} taskName={step.name} t={t}
                                                title={step.title} on:click={goToTask}>
                                    </CircleStep>
                                {/each}
                            </div>
                        </div>
                    {/if}
                    <TableAccordionContent
                            asideTitle={accordionInfo.asideTitle}
                            bind:completedTasks={taskCompleteMap[currentStepName]}
                            bind:form={path[currentIdx.stepIdx]}
                            bind:ignoreDirty={ignoreDirty}
                            bind:table_validation_map={wizard_validation_map[currentStepName]}
                            bind:table_values_map={wizard_values_map[currentStepName]}
                            bind:table_visibility_map={wizard_visibility_map[currentStepName]}
                            bind:componentsMap={componentsMap}
                            {submitData}
                            {submitEnabled}
                            tableId={id}
                            on:change/>
                {/if}
            </div>
        </div>
    </div>
</div>