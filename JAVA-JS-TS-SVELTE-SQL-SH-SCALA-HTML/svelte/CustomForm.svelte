<script>

    import {createEventDispatcher, onMount, onDestroy} from 'svelte';

    const eventDispatcher = createEventDispatcher();

    export let name;
    // export let label = '';
    export let value = undefined;
    export let options = {};
    export let valid = false;
    export let default_value = "";
    export let ignoreDirty = false;

    let type = "text";
    let test;
    let touched = false;
    let prevValue;
    let maxlength = 50;
    let validationMessage = "";
    let style=undefined;
    let contextSubscribes = [];

    $: if (options.reload) {
          asyncOnMount()
          options.reload = false;
    }


    onMount(() => {
       asyncOnMount();
    });

    onDestroy( () => {
            contextSubscribes.forEach( (unsubscribe) => { unsubscribe(); })
        }
    );
/*
    afterUpdate(() => {
        //handleChange();
        //validate();
    });
 */
    //$ : value, validate();
    function blurHandler() {
        touched = true;
    }

    function executeCustomValidators(){
        if(valid && options.customPostValidators && options.customPostValidators.length > 0){
            let testValid = false;
            let response;
            options.customPostValidators.forEach(item => {
                response = item.validator(value, item.context);
                validationMessage = response.result ? validationMessage : (response.message ? response.message : validationMessage);
                testValid = testValid || response.result;
            });
            setTimeout(()=>{valid = testValid;eventDispatcher('forceValidation', {valid:valid});},100);
        }
    }

    function handleChange() {
        if(checkChange()){
            if(options.exportState && valid ){
                options.exportState.set(value);
            }
            eventDispatcher('change', {});
            prevValue = value;
        }
    }

    function checkChange(){
        if(type === 'number' ){
            return prevValue !== value;
        } else if (prevValue) {
            return prevValue.localeCompare(value) !==0;
        }
        return true
    }


     function asyncOnMount(){
        prevValue = default_value;
        if(typeof value === "undefined" && typeof default_value != "undefined" && default_value != null){
            value = default_value;
            prevValue = default_value;
        } else {
            prevValue = value;
        }
        if ( options && options.validationMessage){
            validationMessage = options.validationMessage;
        }
        if(options.exportState && valid){
            options.exportState.set(value);
        }
        if(options.type){
            type = options.type;
        }
        if(options.maxlength){
            maxlength = options.maxlength;
        }
        if(options.customPostValidators && options.customPostValidators.length > 0){
            options.customPostValidators.forEach(item => {
                if(item.context){
                    Object.keys(item.context).forEach(storage => {
                        if(item.context[storage]){
                        }
                    });
                }
            });
        }
        if(options && options.type === "currency"){
            test = (e) => {
                if((value.split(".")[0]).indexOf("00")>-1){
                    value.replace("00","0");
                } else {
                    value.replace(/[^0-9\.]/g,'');
                }

                if(value.split(".")[2] != null || (value.split(".")[2]).length ){
                    value = value.substring(0, value.lastIndexOf("."));
                }
            }
        }
        if(options && options.width) {
            style =`width:${options.width}!important;`;
        }

     }

</script>

{#if type === 'number'}
    <input class="input-text form-control" type="number" min="0" autocomplete="off" style={style}
        required="{options.required}" {name}
        readonly={options.disabled}
        bind:value={value}
        on:blur={blurHandler}
        class:is-invalid={!valid && (touched || ignoreDirty)}
           class:input-upper-case={options.inputUppercase}
        placeholder={options.placeholder}
        maxlength={maxlength}
           on:input={handleChange}
    >
{:else if type === 'currency'}
    <input class="input-text form-control" type="text" autocomplete="off" style={style}
        required="{options.required}" {name}
        readonly={options.disabled}
        bind:value={value}
        on:blur={blurHandler}
        class:is-invalid={!valid && (touched || ignoreDirty)}
           class:input-upper-case={options.inputUppercase}
        placeholder={options.placeholder}
        maxlength={maxlength}
        on:input={handleChange}
    >
{:else if type === 'email'}
    <input class="input-text form-control" type="text" autocomplete="off" style={style}
        required="{options.required}" {name}
        readonly={options.disabled}
        bind:value={value}
        on:blur={blurHandler}
        class:is-invalid={!valid && (touched || ignoreDirty)}
           class:input-upper-case={options.inputUppercase}
        placeholder={options.placeholder}
        maxlength={maxlength}
        on:input={handleChange}
    >
{:else}
    <input class="input-text form-control" type="text" autocomplete="off" style="{style}"
        required="{options.required}" {name}
        readonly={options.disabled}
        bind:value={value}
        on:blur={blurHandler}
        class:is-invalid={!valid && (touched || ignoreDirty)}
           class:input-upper-case={options.inputUppercase}
        placeholder={options.placeholder}
        maxlength={maxlength}
        on:input={handleChange}
    >
{/if}
<div class="invalid-feedback">
    {validationMessage}
</div>

{#if options.debug}
    <div> value: {value}</div>
    <div> touched: {touched} </div>
    <div> valid: {valid} </div>
    <div> regex: {options.regex}</div>
{/if}


