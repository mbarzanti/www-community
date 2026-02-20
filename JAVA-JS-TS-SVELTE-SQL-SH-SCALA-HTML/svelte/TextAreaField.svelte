<script>

    import {createEventDispatcher, afterUpdate, onMount} from 'svelte';
    import {regexTest} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let name;
    // export let label = '';
    export let value;
    export let options = {};
    export let valid = true;
    export let default_value;

    let touched = false;


    onMount(() => {
        if(typeof value === "undefined" && typeof default_value != "undefined" && default_value != null){
            value = default_value;
        }
        validate();
    });

    afterUpdate(() => {
        handleChange();
    });

    // $ : value, validate();
    function blurHandler() {
        touched = true;
    }

    function validate() {
        // Se il valore e' definito procedo ai controlli
        if (typeof value != "undefined" && value != null && value.length > 0) {
            touched = true;
            // Se viene richiesto che il campo di default non e' valido
            if (typeof options.default_invalid != "undefined" && options.default_invalid != null && options.default_invalid) {
                // controllo se effettivamente il valore e' uguale al valore di default
                // se' e' uguale torno false
                if (value.localeCompare(default_value) == 0) {
                    valid = false;
                }
                // altrimenti controllo la regex
                else {
                    valid = regexTest(value, options.regex);
                }
            }
            // altrimenti controllo la regex
            else {
                valid = regexTest(value, options.regex);
            }
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

    function handleChange() {
        validate();
        eventDispatcher('change', {});
    }

</script>

<style>
    textarea{
        resize: none;
        font-family: Montserrat;
        background-color: transparent;
        color: #3C3C3B;
        text-align: left;
        font-size: 12px;
        font-weight: 400;
        /*max-width: 220px;*/
        display: block;
        width: 100%;
        border: 1px solid #bbb;
        margin: 0;
        border-radius: 0;
        padding: 10px 10px 10px 10px;
    }
</style>

<textarea class="input-text form-control" type="text" autocomplete="off"
          cols="{options.cols}" rows="{options.rows}"
       required="{options.required}" {name}
       bind:value={value}
       on:blur="{blurHandler}"
       class:is-invalid="{!valid && touched}"
       placeholder="{options.placeholder}"
></textarea>
<div class="invalid-feedback">
    {options.validationMessage}
</div>

{#if options.debug}
    <div> value: {value}</div>
    <div> touched: {touched} </div>
    <div> valid: {valid} </div>
    <div> regex: {options.regex}</div>
{/if}

