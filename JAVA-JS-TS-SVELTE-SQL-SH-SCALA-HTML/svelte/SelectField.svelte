<script>
    /*
        value: state of the component. Must be a string or an object with the field "value"
    */


    import {createEventDispatcher, onMount, afterUpdate, onDestroy, tick} from 'svelte';
    import {getUniqueId} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let name;
    export let values = [];
    //export let label;
    export let value = undefined;
    export let valid = false;
    export let options;
    export let default_value;
    export const needDisplay = undefined;
    export let ignoreDirty = false;

    let validationMessage = "Campo non selezionato";
    let prevValue;
    let select;
    let touched = false;
    let customStyle = undefined;
    export let startsWithEnabled = true;
    let refreshRunning = false;
    let waitingUpdate = false;
    let firstUpdate = false;


    let contextMasterUnsubscribe = undefined;

    function handleChange() {
        if (!isSelected(prevValue)) {
            if (options.exportState) {
                options.exportState.set(value);
            }
            if (prevValue != undefined) {
                touched = true;
            }
            validate();
            eventDispatcher("change", {
                name: name,
                value: value,
                valid: valid,
                invalidate: options.invalidateOnChange
            });
            prevValue = value;
        }
    }

        afterUpdate( async () => {
            if (refreshRunning) {
                refreshRunning = false;
                refreshComponent();
                await tick();
                handleChange();
                jQuery(select).selectpicker("refresh");
            }
        });

        onMount(() => {
            firstUpdate = true;
            refreshComponent();
            if (value == undefined && default_value != undefined) {
                value = default_value;
                prevValue = default_value;
            } else {
                prevValue = value;
            }
            if(options.validationMessage){
                validationMessage = options.validationMessage;
            }
            if (options && options.initFunction) {
                waitingUpdate = true;
                options.initFunction((data) => {
                    waitingUpdate = false;
                    values = data;
                    initializeComponent();
                });
            } else {
                initializeComponent();
            }
            if (options.exportState) {
                options.exportState.set(value);
            }
            if (options.contextMaster && options.handlerView) {
                contextMasterUnsubscribe = options.contextMaster.subscribe(eventValue => {
                    if (eventValue !== null && Object.keys(eventValue).length === 0) {
                        waitingUpdate = true;
                        values = [];
                        initializeComponent();
                    } else {
                        options.handlerView(eventValue, (data) => {
                            values = data;
                            waitingUpdate = false;
                            initializeComponent();
                            if(firstUpdate && options.exportState){
                                firstUpdate = false;
                                options.exportState.set(value);
                            }
                        })
                    }
                    if(!firstUpdate){
                        value = {};
                    }
                    valid = false;
                    if (options.exportState) {
                        options.exportState.set({});
                    }
                });
            } else if (options.context && options.handlerView) {
                options.handlerView(options.context, (data) => {
                    values = data;
                    initializeComponent();
                });
            }
            if (options && options.customWidth) {
                customStyle = `width: ${options.customWidth}!important;`;

            }
        });

        onDestroy(() => {
            if (contextMasterUnsubscribe) {
                contextMasterUnsubscribe();
            }
        });

        function initializeComponent() {
            //refreshComponent();
            //validate();
            refreshRunning = true;
        }

        function validate() {
            const checkSelectedOptions = select.selectedOptions && select.selectedOptions[0];
            const isSelectedPlaceholder = !select
                || (checkSelectedOptions && select.selectedOptions[0].hidden)
                || isSelected(options.placeholder)
                || (checkSelectedOptions && select.selectedOptions[0].value === "");
            if (waitingUpdate) {
                valid = false;
                console.log("Validation:In attesa di update",valid);
            } else if (!value || isSelectedPlaceholder) {
                valid = !options.required;
                console.log("Validation: nessun value o isSelectedPlaceholder",valid);
            } else {
                valid = true;
                console.log("Validation: Tutto Apposto",valid);
            }
        }

        function refreshComponent() {
            jQuery(select).selectpicker("refresh");
            validate();
        }

        function getInternalValue(v) {
            let selected = null;

            if (typeof v === "undefined" || v === null) {
                return "";
            } else if (typeof v === 'string') {
                selected = v;
            } else {
                selected = Object.keys(v).reduce(
                        (total, currentValue) => {
                            return total + '$' + getInternalValue(v[currentValue]);
                        }, '');
            }
            return selected;
        }

        function isSelected(v) {

            let loaded = default_value;
            if (typeof value != "undefined" && value != null) {
                loaded = value;
            }

            if (typeof v != "undefined" && v != null) {
                return (getInternalValue(v).localeCompare(getInternalValue(loaded)) === 0) ? true : undefined;
            } else {
                return undefined;
            }
        }


</script>
<div class="custom-select-wrapper" class:is-invalid={!valid && (touched || ignoreDirty)}>
    {#if startsWithEnabled}
        <select bind:this={select}
                class={"selectpicker- hometendina- show-menu-arrow"}
                bind:value={value} data-size={options.size ? options.size : 5} data-dropup-auto="false"
                on:change={handleChange}
                style={customStyle}
                class:is-invalid={!valid && (touched || ignoreDirty)}
                disabled={options.disabled}
                data-live-search={options.liveSearch}
                data-live-search-style={"startsWith"}
                title={(value && value.length > 0 )? undefined : options.placeholder || ''}>
            {#each values as item}
                <option value={item.value} selected={isSelected(item.value)}>{item.label}</option>
            {/each}
        </select>
    {:else}
        <select bind:this={select}
                class={"selectpicker- hometendina- show-menu-arrow"}
                bind:value={value} data-size={options.size ? options.size : 5} data-dropup-auto="false"
                on:change={handleChange}
                class:is-invalid={!valid && (touched || ignoreDirty)}
                style={customStyle}
                disabled={options.disabled}
                data-live-search={options.liveSearch}
                title={(value && value.length > 0 )? undefined : options.placeholder || ''}>
            {#each values as item}
                <option value={item.value} selected={isSelected(item.value)}>{item.label}</option>
            {/each}
        </select>
    {/if}
    <div class="invalid-feedback" class:invalid-feedback-visible={!valid && (touched || ignoreDirty)}>
        {validationMessage}
    </div>
</div>

<style>
    .invalid-feedback-visible{
        display: block;
    }


    .custom-select-wrapper{
        min-height: calc(35px + 2.25rem);
    }

    .custom-select-wrapper.is-invalid{
        min-height: 35px;
    }

    /*
    .extended-width:not(.is-invalid) + button.btn.dropdown-toggle.btn-light{
        margin-bottom: 2.25rem;
    }*/


    /*.extended-width.is-invalid + button.btn.dropdown-toggle.bs-placeholder.btn-light{
        border-color: #dc3545;
    }*/
</style>
