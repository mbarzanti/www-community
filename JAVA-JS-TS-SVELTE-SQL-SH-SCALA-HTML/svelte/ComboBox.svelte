<script>

    import {createEventDispatcher, onMount} from 'svelte';
    import {getUniqueId} from '../sveltekit';
    const eventDispatcher = createEventDispatcher();

    export let values;
    // export let label;
    export let value = '';
    export let valid = true;
    export let options;
    export let default_value;

    let id = getUniqueId("combo-");
    let selector = "#" + id;

    onMount(() => {
        if (typeof options != "undefined" && typeof options.initFunction != "undefined") {
            options.initFunction((res) => {
                // populate(res);
                initializeComponent();
            });
        } else {
            initializeComponent();
        }
    });

    function initializeComponent() {
        jQuery(selector).combobox();
        jQuery(selector).on("change", handleSelectChange);
        validate();
    }

    function handleSelectChange(e) {
        value = e.target.value;
        validate();
        eventDispatcher('change', {});
    }

    function validate() {
        if (typeof options != "undefined" && typeof options.required != "undefined" && options.required) {
            valid = typeof value != "undefined" && value.length > 0;
        } else {
            valid = true;
        }
    }

    function isSelected(v,df){
        return v.localeCompare(df) == 0 ? true : undefined;
    }
</script>

<select {id} class="combobox-alt form-control" name="inline" bind:value={value}>
    <option disabled value="">{options.placeholder||'Select value'}</option>
    {#each values as item}
        <option value={item.value} selected={isSelected(item.value,default_value)}>{item.label}</option>
    {/each}
</select>

