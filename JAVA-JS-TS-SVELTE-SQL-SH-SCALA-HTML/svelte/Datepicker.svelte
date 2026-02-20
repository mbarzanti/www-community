<script>
    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';
    import {getUniqueId} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let value = '';
    export let options = {};
    export let valid = true;
    export let default_value;
    let rawValue = '';

    let date_format = (typeof options.date_format != "undefined" ? options.date_format : "dd/mm/yyyy");
    let placeholder = (typeof options.placeholder != "undefined" ? options.placeholder : "dd/mm/yyyy");
    let id = getUniqueId("datepicker-");
    let touched = false;
    let datepicker;

    onMount(() => {

        let arg = {
            format: 'dd/mm/yyyy',
            maxViewMode: 3,
            autoclose: true,
            clearBtn: true,
        };

        if(options.startDate){
            arg["startDate"]=options.startDate;
        }
        if(options.endDate){
            arg["endDate"]=options.endDate;
        }

        jQuery.fn.datepicker.defaults.language = 'it';
        jQuery("#" + id).datepicker(arg).on('change', handleChange);

        if (typeof value === "undefined" && typeof default_value != "undefined" && default_value != null) {
            value = default_value;
            rawValue = default_value;
            touched = true;
        } else {
            rawValue = value;
        }
    });

    afterUpdate(() => {
        handleChange();
    });

    function handleChange(e) {
        if (e) {
            /* value = jQuery(datepicker).datepicker('getDate').toLocaleDateString('it-IT',{
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            }); */
            rawValue = e.target.value;
            touched = true;
        } else if(jQuery(datepicker).datepicker('getDate')){
            value = jQuery(datepicker).datepicker('getDate').toLocaleDateString('it-IT',{
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            });
        }
        jQuery(this).datepicker('hide');
        validate();
        eventDispatcher('change', {});
    }

    function validate() {
        // Se il valore e' definito procedo ai controlli
        if (typeof value != "undefined" && value != null && value.length > 0) {
            // Se viene richiesto che il campo di default non e' valido
            if (typeof options.default_invalid != "undefined" && options.default_invalid != null && options.default_invalid) {
                // controllo se effettivamente il valore e' uguale al valore di default
                // se' e' uguale torno false
                if (value == default_value) {
                    valid = false;
                }
                // altrimenti torno true
                else {
                    valid = true;
                }
            }
            // altrimenti torno true
            else {
                valid = true;
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

</script>

<input {id} class="datepicker form-control" class:is-invalid="{!valid && touched}" data-date-format="{date_format}"
       {placeholder} bind:this={datepicker}
       bind:value={rawValue}/>
<div class="invalid-feedback">
    {options.validationMessage}
</div>