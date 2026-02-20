<script>

    import {createEventDispatcher, onMount, onDestroy} from 'svelte';
    import {regexTest} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let name;
    // export let label = '';
    export let value = undefined;
    export let options = {};
    export let valid = false;
    export let default_value = "";
    export let ignoreDirty = false;


    let type = "text";
    let touched = false;
    let prevValue;
    let maxlength = 50;
    let validationMessage = "";
    let contextSubscribes = [];

    onMount(() => {
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
        validate();
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
                            contextSubscribes.push(item.context[storage].subscribe(eventValue => {validate()}));
                        }
                    });
                }
            }); 
        }
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

    function validate() {
        if ( options && options.validationMessage){
            validationMessage = options.validationMessage;
        }
        // Se il valore e' definito procedo ai controlli
        if (typeof value != "undefined" && value != null && value.length > 0) {
            touched = true;
            // Se viene richiesto che il campo di default non e' valido
            if (typeof options.default_invalid != "undefined" && options.default_invalid != null && options.default_invalid === true) {
                // controllo se effettivamente il valore e' uguale al valore di default
                // se' e' uguale torno false
                if (value.localeCompare(default_value) == 0) {
                    valid = false;
                }
                // altrimenti controllo la regex
                else {
                    valid = regexTest(value, options.regex);
                    executeCustomValidators();
                }
            }
            // altrimenti controllo la regex
            else {
                valid = regexTest(value, options.regex);
                executeCustomValidators();
            }
        } else if (typeof value != "undefined" && value != null && type === 'number' ){
            valid = regexTest(value, options.regex);
            executeCustomValidators();
        }
        // se non c'e' un valore
        else {
            
            // controllo se il campo è required
            // se e' required, torno false
            if (typeof options.required != "undefined" && options.required != null && options.required) {
                valid = false;
            }
            // altrimenti non essendo required, ritorno true
            else {
                valid = true;
            }
        }
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

        if( checkChange() ){
            validate();
            if(options.exportState && valid){
                options.exportState.set(value);
            }
            eventDispatcher('change', {});
            if(valid && options.handler){
                eventDispatcher('actionRequest', { handler: options.handler, value: value});
            }
            prevValue = value;
        }
    }

    function checkChange(){
        if(type === 'number'){
            return prevValue !== value;
        } else if (prevValue) {
            return prevValue.localeCompare(value) !==0;
        }
        return true
    }

</script>

{#if type === 'number'}
    <input class="input-text form-control" type="number" min="0" autocomplete="off"
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
    <input class="input-text form-control" type="text" autocomplete="off"
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
    <input class="input-text form-control" type="text" autocomplete="off"
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


