<script>
    import AutoSubForm from "../wizard/AutoSubForm.svelte";

    //export let title;
    export let currentStepName;
    export let currentTaskName;
    export let task;
    export let module_values_map = {};
    export let module_validation_map = {};
    export let module_visibility_map = {};
    export let table_values_map = {};
    export let ignoreDirty = true;
    export let valid = false;

    export let componentsMap;

    let levels = 1;

    let ready;
    let readOnly = false;

    $: if (!ready) {
        setTimeout(() => {
            ready = true;
        }, 1);
    }

    function handleNewValidation(event) {
        valid = event.detail;
    }

</script>


<!--<h3 class="content-aside-title content-aside-title--secondary mb20">Input text</h3>-->
<div class="form-fild forced-full-grid">
    <div class="row-form">
        {#if ready}
            <AutoSubForm title="" formDescriptor={task} bind:ignoreDirty={ignoreDirty}
                         bind:valid={valid}
                         bind:values_map={module_values_map}
                         bind:values_complete_map={table_values_map}
                         bind:visibility_map={module_visibility_map}
                         bind:validation_map={module_validation_map}
                         {levels}
                         bind:componentsMap={componentsMap}
                         bind:currentStepName={currentStepName}
                         bind:currentTaskName={currentTaskName}
                         readOnly={readOnly}
                         on:change
                         on:validation={handleNewValidation}
            />
        {/if}
    </div>
</div>


