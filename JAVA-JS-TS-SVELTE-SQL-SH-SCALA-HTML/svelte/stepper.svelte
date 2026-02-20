<script>
    import FakeStep from "./fakeStep.svelte";

    export let title;
    export let steps;
    export let currentStep;
    export let currentTask;
    export let done;
    let currentStepIndex;
    let currentTasksList;
    let currentTaskIndex;
    let lastTask;

    $:{
        currentStepIndex = steps.findIndex( (step) =>step.name === currentStep);
        currentTasksList = steps[currentStepIndex].tasks;
        currentTaskIndex = currentTasksList.findIndex( (step) =>step.name === currentTask);
        lastTask = (currentTaskIndex === (currentTasksList.length-1));
    }

</script>


<div class="container">
    <div class="row">
        <div class="col-8">
            <a href={undefined} class="user-anagrafica-auth-area ownerselector">
                <span class="user-data">{title}</span>
            </a>
        </div>
    </div>
    <div class="stepper-navigation">
        <ul class="stepper">
            {#each steps as step, stepIndex}
                <li class="stepper-item"
                    class:active={ (stepIndex === currentStepIndex) && !(lastTask && done)}
                    class:current={(stepIndex === currentStepIndex) && !(lastTask && done)}
                    class:settled={(stepIndex < currentStepIndex) || ( (stepIndex === currentStepIndex) && lastTask && done)} >
                    <a href={undefined} class="step-link clickable">{step.title}</a>
                </li>
            {/each}
        </ul>
    </div>
</div>
<div class="sectiontitle">
    {currentTasksList[currentTaskIndex].subtitle}
</div>

<div class="container container-stepper-circle">
    <div class="circle-stepper-horizontal">
        {#if currentTasksList.length && currentTasksList.length>1}
            {#each currentTasksList as task, taskIndex}
                <FakeStep
                        active={taskIndex <= currentTaskIndex}
                        done={taskIndex < currentTaskIndex || done}
                        index={taskIndex}
                        title={task.title}>
                </FakeStep>
            {/each}
        {/if}
    </div>
</div>
