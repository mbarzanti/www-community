<script>
    import {createEventDispatcher, afterUpdate, onMount, tick} from 'svelte';
    import {getUniqueId} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let value = {};
    export let values;
    // export let label = "";
    export let valid = true;
    //export let name;
    export let ignoreDirty = false;
    export let options;
    export let default_value;
    let touched = false;
    let limit = 0;
    let limitWarning = false;

    let inputMap = {};
    let validationMessage = "Campo non selezionato";

    function genLimitWarningMessage(){
        return `È possibile selezionare solo fino a ${limit} opzioni!`
    }

    onMount(async () => {
        if(typeof value === "undefined"){
            value = {};
        }
        if(options.validationMessage){
            validationMessage = options.validationMessage;
        }
        if(options.limit){
            limit = options.limit;
        }
        if(Object.keys(value).length === 0 && typeof default_value !== "undefined" && default_value !== null){
            value[default_value] = true;
        }
        if (options && options.context && options.handlerView) {
            options.handlerView(options.context, (data) => {
                    values = data;
                })
        }
        await tick();
        validate();
    });

    function validate() {
        if (Object.keys(value).some(elem=>value[elem])){
            valid = true;
        } else if (Object.keys(value).length === 0 && options && !options.required){
            valid = true;
        } else {
            valid = false;
        }
    }

    function handleClick(e) {
        touched = true;
        limitWarning = false;
        if(e){
            const selectedEntries = Object.keys(value).reduce((previousValue, item) => previousValue + (value[item] ? 1 : 0),0);
            if(e.target.checked){
                if(limit !== 0 && selectedEntries === limit){
                    //limit reached
                    limitWarning = true;
                } else {
                    value[e.target.id] = true;
                }
                if(options.defaultZeroValue){
                    // if default value is zero value
                    if(default_value === e.target.id){
                        value = {[default_value]:true};
                    } else {
                        value[default_value] = false;
                    }
                }
            } else {
                value[e.target.id] = false;
                if(selectedEntries === 1 && options.defaultZeroValue){
                    value[default_value] = true;
                }
            }
        }
        validate();
        eventDispatcher('change', {});
    }

    function isChecked(v, dv) {
        return v.localeCompare(dv) === 0 ? "checked" : undefined;
    }

    

</script>

<style>
    .checkbox_custom {
        display: inline-block;
        vertical-align: middle;
        margin: 0;
        padding: 0;
        margin-right: 0.3rem;
        width: 20px;
        height: 20px;
        border: none;
    }

    .checkbox_item{
        display: flex;
    }
     .invalid-feedback-visible{
         display: block;
     }
    .form-check-label.label{
        white-space: pre;
    }

</style>


{#each values as v, i}
    <span class="checkbox_item">
        <input id={v.value} type="checkbox" class="checkbox_custom"
           bind:checked={value[v.value]}
           on:click={handleClick}/>
        <label class="form-check-label label" for="{v.value}">{v.label}</label>
    </span>
{/each}
<div class="invalid-feedback" class:invalid-feedback-visible={limitWarning || (!valid && (touched || ignoreDirty))}>
    {limitWarning ? genLimitWarningMessage() : validationMessage}
</div>
