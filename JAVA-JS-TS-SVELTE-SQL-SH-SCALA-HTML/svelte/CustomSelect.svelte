<script>
    /*
        value: state of the component. Must be a string or an object with the field "value"
    */


    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';

    const eventDispatcher = createEventDispatcher();

    export let name;
    export let values = [];
    //export let label;
    export let value;
    export let valid = false;
    export let options;
    export let default_value;
    export let needDisplay;

    let prevValue;
    let select;
    export let startsWithEnabled = true;

    $:if (typeof needDisplay !== "undefined" && needDisplay.status) {

        if (options && options.needDisplayFunction) {
            options.needDisplayFunction(needDisplay.value, (data) => {
                values = data;
                initializeComponent();
            });
        }

        needDisplay["status"] = false;
    }

    function handleChange() {
        if (getInternalValue(prevValue).localeCompare(getInternalValue(value)) !== 0) {
            if(options.exportState){
                options.exportState.set(value);
            }
            validate();
            eventDispatcher('change', {"name": name, "value": value, "invalidate": options.invalidateOnChange});
            prevValue = value;
        }
        refreshComponent();
    }

    afterUpdate(() => {
        handleChange();
        refreshComponent();
        validate();
    });

    onMount(() => {
        if(typeof value === "undefined" && typeof default_value != "undefined" && default_value != null){
            value = default_value;
            prevValue = default_value;
        } else {
            prevValue = value;
        }

        prevValue = default_value;
        if (options && options.initFunction) {
            options.initFunction((data) => {
                values = data;
                initializeComponent();
            });
        } else {
            initializeComponent();
        }
        if(options.exportState){
            options.exportState.set(value);
        }
        if(options.contextMaster && options.handlerView){
            options.contextMaster.subscribe(eventValue => {
                if(Object.keys(eventValue).length === 0){
                    values = [];
                    initializeComponent();
                    value = undefined;
                } else {
                    options.handlerView(eventValue, (data) => {
                        values = data;
                        initializeComponent();
                    })

                }
                valid = false;
                if(options.exportState){
                    options.exportState.set({});
                }
            });
        } else if (options.context && options.handlerView) {
            options.handlerView(options.context, (data) => {
                    values = data;
                    initializeComponent();
                });
        }
    });

    function initializeComponent() {
        refreshComponent();
        validate();
    }

    function validate() {

        if (typeof value !== 'undefined' && value != null && !isSelected(options.placeholder)) {
            if ( !select || select.selectedOptions[0].hidden){
                valid =false;
            } else {
                valid = true;
            }
        }else {
            valid = false;
        }

    }

    function refreshComponent(){
        jQuery(select).selectpicker('refresh');
    }

    function getInternalValue(v){
        let selected = null;

        if(typeof v === "undefined"){
            return "";
        } else if(typeof v === 'string'){
            selected = v;
        } else {
            selected = Object.keys(v).reduce(
                (total, currentValue) => {
                    return total + '$' + getInternalValue(v[currentValue]);
                }, '');
        }
        return selected;
    }

    $: isSelected = (v) => {

        let loaded = default_value;
        if(typeof value != "undefined" && value != null){
            loaded = value;
        }

        if(typeof v != "undefined" && v != null){
            return (getInternalValue(v).localeCompare(getInternalValue(loaded)) === 0) ? true : undefined;
        } else {
            return undefined;
        }


    }


</script>

{#if startsWithEnabled}
<select bind:this={select} class="form-control has-success"
        bind:value={value}
        on:change={handleChange}
        data-live-search={options.liveSearch}
        data-live-search-style={"startsWith"}
        data-dropup-auto="false"
        data-size={options.size ? options.size : 5}
        disabled={options.disabled}>
        <option hidden>{options.placeholder || ''}</option>
    {#each values as item}
        <option value={item.value} selected={isSelected(item.value)}>{item.label}</option>
    {/each}
</select>
{:else}
<select bind:this={select} class="form-control has-success"
        bind:value={value}
        on:change={handleChange}
        data-live-search={options.liveSearch}
        data-dropup-auto="false"
        data-size={options.size ? options.size : 5}
        disabled={options.disabled}>
        <option hidden>{options.placeholder || ''}</option>
    {#each values as item}
        <option value={item.value} selected={isSelected(item.value)}>{item.label}</option>
    {/each}
</select>
{/if}