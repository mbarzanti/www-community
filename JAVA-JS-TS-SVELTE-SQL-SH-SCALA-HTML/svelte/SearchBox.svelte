<script>

    import {createEventDispatcher, onDestroy, onMount} from 'svelte';
    import {regexTest} from "../../../../suit/uikit/utils";

    const eventDispatcher = createEventDispatcher();

    export let name;
    export let value = undefined;
    export let options = {};
    export let valid = false;
    export let default_value = "";
    export let ignoreDirty = false;
    export let style = options.style || "";
    export let styleClass = options.styleClass || "";


    let typeDef = {type: "text"};
    let touched = false;
    let prevValue;
    let maxlength = 50;
    let validationMessage = "";
    let contextSubscribes = [];

    $: if (options && options.reload) {
        asyncOnMount();
        options.reload = false;
    }

    let copy = false;
    let i = 0;
    let ctrlKeyFlag = false;

    onMount(() => {
        asyncOnMount();
    });

    onDestroy(() => {
            contextSubscribes.forEach((unsubscribe) => {
                unsubscribe();
            })
        }
    );

    function blurHandler() {
        touched = true;
    }

    function validate() {
        if (options && options.validationMessage) {
            validationMessage = options.validationMessage;
        }
        // Se il valore e' definito procedo ai controlli
        if (typeof value != "undefined" && value != null && value.length > 0) {
            touched = true;
            // Se viene richiesto che il campo di default non e' valido
            if (typeof options.default_invalid != "undefined" && options.default_invalid != null && options.default_invalid === true) {
                // controllo se effettivamente il valore e' uguale al valore di default
                // se' e' uguale torno false
                if (value.localeCompare(default_value) === 0) {
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
        } else if (typeof value != "undefined" && value != null && typeDef.type === 'number') {
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

    function executeCustomValidators() {
        if (valid && options.customPostValidators && options.customPostValidators.length > 0) {
            let testValid = false;
            let response;
            options.customPostValidators.forEach(item => {
                response = item.validator(value, item.context);
                validationMessage = response.result ? validationMessage : (response.message ? response.message : validationMessage);
                testValid = testValid || response.result;
            });
            setTimeout(() => {
                valid = testValid;
                eventDispatcher('forceValidation', {valid: valid});
            }, 100);
        }
    }
    let fired = false;
    let displayError=false;

    function handleSearch(e) {
            if(e.keyCode !== 17) {
                if(!e.ctrlKey) {
                    if(ctrlKeyFlag === false || (ctrlKeyFlag === true && e.key.toLowerCase() === "v")) {
                        search(e);
                    }
                    if(ctrlKeyFlag === true){
                        ctrlKeyFlag = false;
                    }
                }else if( e.key.toLowerCase() === "v" ){
                    search(e);
                }
            }else{
                ctrlKeyFlag = true;
            }
    }

    function search(e){
        validate();
        if (valid && !fired) {
            eventDispatcher('search', {value: e.target.value, id: name, valid: valid});
            fired = true;
            setTimeout(() => {
                fired = false
            }, 500);
        } else {
            displayError = true;
        }
        if (checkChange()) {
            if (options.exportState && valid) {
                options.exportState.set(value);
            }
            prevValue = value;
        }
    }

    function onInputHandler(e){
        value=e.target.value.toUpperCase();
        displayError=false;
    }

    function checkChange() {
        if (typeDef.type === 'number') {
            return prevValue !== value;
        } else if (prevValue) {
            return prevValue.localeCompare(value) !== 0;
        }
        return true
    }


    function asyncOnMount() {
        prevValue = default_value;
        if (typeof value === "undefined" && typeof default_value != "undefined" && default_value != null) {
            value = default_value;
            prevValue = default_value;
        } else {
            prevValue = value;
        }
        if (options && options.validationMessage) {
            validationMessage = options.validationMessage;
        }
        validate();
        if (options.exportState && valid) {
            options.exportState.set(value);
        }
        switch (options.type) {
            case "number":
                typeDef = {type: "number", min: "0"};
                break;
            default:
                typeDef = {type: "text"};
                break;

        }
        if (options.maxlength) {
            maxlength = options.maxlength;
        }
        if (options.customPostValidators && options.customPostValidators.length > 0) {
            options.customPostValidators.forEach(item => {
                if (item.context) {
                    Object.keys(item.context).forEach(storage => {
                        if (item.context[storage]) {
                            contextSubscribes.push(item.context[storage].subscribe(eventValue => {
                                validate()
                            }));
                        }
                    });
                }
            });
        }
    }

</script>
<div class="cfpiva-search cfpiva-search-ncb autocomplete">
    <div class="autocomplete">
        <input type="search" autocomplete="off" bind:value={value} class="input-text form-control {styleClass}" class:input-upper-case={options.inputUppercase}
               class:is-invalid={!valid && (touched || ignoreDirty)} id="{name}"
               disabled={options.disabled}
               maxlength={maxlength}
               {name}
               on:blur={blurHandler}
               on:keyup={handleSearch}
               on:input={onInputHandler}
               placeholder={options.placeholder}
               readonly="{options.disabled}"
               required="{options.required}"
               style="{style}">

        <div class="invalid-feedback">
            {validationMessage}
        </div>
    </div>
</div>


{#if options.debug}
    <div> value: {value}</div>
    <div> touched: {touched} </div>
    <div> valid: {valid} </div>
    <div> regex: {options.regex}</div>
{/if}

<style>
    .feedback-invisible{
        display: none;
    }
</style>

