<script>
    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';

    export let name;
    // export let label = '';
    export let value = '';
    export let options = {};
    export let valid = true;
    export let default_value;

    let touched = false;
    const eventDispatcher = createEventDispatcher();

    onMount(() => {
        if (typeof value === "undefined" && typeof default_value != "undefined" && default_value != null) {
            value = default_value;
        }
        validate();
    });

    afterUpdate(() => {
        handleChange();
    });

    function blurHandler() {
        touched = true;
    }

    function handleChange() {
        validate();
        eventDispatcher('change', {});
    }

    function validate() {
        // Se il valore e' definito procedo ai controlli
        if (typeof value != "undefined" && value != null) {
            touched = true;
            // Se viene richiesto che il campo di default non e' valido
            if (typeof options.default_invalid != "undefined" && options.default_invalid != null && options.default_invalid) {
                // controllo se effettivamente il valore e' uguale al valore di default
                // se' e' uguale torno false
                if (value == default_value) {
                    valid = false;
                }
                // altrimenti controllo i limiti
                else {
                    valid = limitsTest(value, {min: options.min, max: options.max});
                }
            }
            // altrimenti controllo i limiti
            else {
                valid = limitsTest(value, {min: options.min, max: options.max});
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

    function limitsTest(v, opt) {
        // Controllo sui limiti
        // Se ci sono entrambi, controllo se il valore è compreso o uguale
        if (typeof opt.min != "undefined" && opt.min != null &&
                typeof opt.max != "undefined" && opt.max != null) {
            if (v >= opt.min && v <= opt.max) {
                return true;
            } else {
                return false;
            }

        }
        // C'è solo il min
        else if (typeof opt.min != "undefined" && opt.min != null &&
                typeof opt.max == "undefined") {
            return v > opt.min;
        }
        // c'è solo il max
        else if (typeof opt.min == "undefined" &&
                typeof opt.max != "undefined" && opt.max != null) {
            return v < opt.max;
        }
        // non ci sono limiti
        else {
            return true;
        }
    }

</script>

<input class="form-control form-control"
       type="number"
       autocomplete="off"
       required="{options.required}"
       {name}
       bind:value={value}
       on:blur="{blurHandler}"
       class:is-invalid="{!valid && touched}"
>
<div class="invalid-feedback">
    {options.validationMessage}
</div>

{#if options.debug}
    <div> value: {value}</div>
    <div> touched: {touched} </div>
    <div> valid: {valid} </div>
    <div> regex: {options.regex}</div>
{/if}
