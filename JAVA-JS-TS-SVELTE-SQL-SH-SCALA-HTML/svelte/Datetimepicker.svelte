<script>
    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';
    import moment from 'moment';

    import Flatpickr from 'svelte-flatpickr'
    import 'flatpickr/dist/flatpickr.css'
    import 'flatpickr/dist/themes/light.css'

    const eventDispatcher = createEventDispatcher();

    export let value = '';
    export let options = {};
    export let valid = true;
    export let default_value;

    var element;


    let date_format = (typeof options.date_format != "undefined" ? options.date_format : "dd/mm/yyyy");
    let placeholder = (typeof options.placeholder != "undefined" ? options.placeholder : "dd/mm/yyyy");
    let touched = false;
    let floatingLabel = options.floatingLabel;

    const flatpickrOptions = {
        element: '#my-picker',
        altInput: true,
        dateFormat: "YYYY-MM-DD HH:mm:ss",
        altFormat: "YYYY-MM-DD HH:mm:ss",
        allowInput: true,
        parseDate: (datestr, format) => {
            return moment(datestr, format, true).toDate();
        },
        formatDate: (date, format, locale) => {
            // locale can also be used
            return moment(date).format(format);
        },
        enableTime: true,
        time_24hr: true,
        onChange: handleChange
    }

    onMount(() => {

        let arg = {
            format: options.date_format,
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


        if (typeof default_value != "undefined" && default_value != null && default_value.length>0) {
            value = default_value;
            validate();
            touched = true;
        }
    });

    afterUpdate(() => {
        handleChange();
    });




    function handleChange(selectedDates, dateStr, instance) {
        console.log('Svelte onChange handler: ' + selectedDates)
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


<Flatpickr options="{ flatpickrOptions }"
           bind:value={value}
           placeholder={placeholder}
           on:change={handleChange} element="#my-picker">
    <div class="flatpickr" id="my-picker">
        <input type="text" placeholder="Select Date.." data-input>

        <a class="input-button" title="clear" data-clear>
            <i class="icon-close"></i>
        </a>
    </div>
</Flatpickr>


{#if !valid}
<div class="invalid-feedback">
    {options.validationMessage}
</div>
{/if}
