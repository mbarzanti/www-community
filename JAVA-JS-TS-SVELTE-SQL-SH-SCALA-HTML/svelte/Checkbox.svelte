<script>
    import {createEventDispatcher, onMount} from 'svelte';
    import {getUniqueId} from '../sveltekit';

    let id = getUniqueId("checkbox-");
    let selector = "#" + id;
    export const name = "";
    export let value = false;
    export let label = "";
    export let valid = true;
    // export let name;
    export let options;
    export let default_value;

    const eventDispatcher = createEventDispatcher();

    onMount(() => {
        if (typeof value === "undefined" && typeof default_value != "undefined" && default_value != null) {
            value = default_value;
        }
        validate();

    });

    function validate() {
        if (typeof options != "undefined" && typeof options.required != "undefined" && options.required == true) {
            valid = value;
        } else {
            valid = true;
        }
    }

    function handleClick() {
        value = jQuery(selector)[0].checked;
        validate();
        eventDispatcher('change', {});
    }

</script>

<style>
    .checkbox_custom {
        display: inline-block;
        vertical-align: middle;
        margin: 0;
        padding: 0;
        width: 20px;
        height: 20px;
        border: none;
    }
</style>

<div class="form-check">
    <input {id} type="checkbox" class="checkbox_custom"
           bind:checked={value}
           on:click={handleClick}
           disabled={options && options.disabled}/>
    <label class="form-check-label label inline-label label-check" for="{id}">{label}</label>
</div>
