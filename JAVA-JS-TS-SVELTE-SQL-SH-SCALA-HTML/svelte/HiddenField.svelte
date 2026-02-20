<script>
    import {onMount} from 'svelte';

    export let name;
    export let value = '';
    export let options = {};
    export const valid = true;
    export let default_value;

    onMount(() => {
        if (value === undefined && typeof default_value != "undefined" && default_value != null) {
            value = default_value;
        }
    });
</script>

<input class="form-control" type="hidden" autocomplete="off"
       required="{options.required}" {name}
       bind:value={value}/>
