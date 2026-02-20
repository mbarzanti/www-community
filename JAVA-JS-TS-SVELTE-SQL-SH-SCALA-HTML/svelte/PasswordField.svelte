<script>

    import Icon from 'svelte-materialify/src/components/Icon';
    import TextField from 'svelte-materialify/src/components/TextField';
    import {createEventDispatcher, afterUpdate, onMount} from 'svelte';
    import {regexTest} from '../../js/utils';
    import {mdiEye, mdiEyeOff} from '@mdi/js';
    const eventDispatcher = createEventDispatcher();

    export let label;
    export let required;
    export let name;
    export let value = null;
    export let options = {};
    export let valid = false;
    export let default_value = null;
    export let resetCounter = 0;

    let touched = false;

    $ : if(resetCounter){
        value = default_value;
        validate();
    }

    onMount(() => {
        if(!value && default_value){
            value = default_value;
        }
        validate();
        if(options.exportState && valid){
            options.exportState.set(value);
        }
    });

    afterUpdate(() => {
        handleChange();
    });

    function blurHandler() {
        touched = true;
    }

    function validate() {
        // Se il valore e' definito procedo ai controlli
        if (value && value.length > 0) {
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
            if (required) {
                valid = false;
            }
            // altrimenti non essendo required, ritorno true
            else {
                valid = true;
            }
        }

        return valid;
    }

    var validationRules = [(v) => (validate() && options.validationFormula(v)) || options.validationMessage];

    function handleChange() {
        validate();
        if(options.exportState && valid){
            options.exportState.set(value);
        }
        eventDispatcher('change', {});
    }

    let show = false;
    $:showIcon = show ? mdiEyeOff : mdiEye;
</script>


<TextField dense placeholder={options.placeholder} rules={validationRules} bind:value={value} on:blur={blurHandler} type={show ? 'text' : 'password'}>
    {#if required}
        <span style="">*&nbsp;</span>
    {/if}
    {label}
    <div slot="append" on:click={() => {show = !show; }}>
        <Icon path={showIcon} />
    </div>
</TextField>

{#if options.debug}
    <code> value: {value}</code>
    <code> touched: {touched} </code>
    <code> valid: {valid} </code>
    <code> required: {required} </code>
    <code> regex: {options.regex}</code>
{/if}
