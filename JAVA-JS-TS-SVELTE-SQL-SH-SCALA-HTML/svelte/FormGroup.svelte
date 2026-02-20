<script>

    import {onMount} from 'svelte';

    export let visible = true;
    export let predicate = {};
    export let descriptor;

    let title;
    let name;
    let options = {};

    onMount(() => {
       name = descriptor.name;
       title = descriptor.title;
       predicate = descriptor.predicate;
       options = descriptor.options || options;
    });

</script>
{#if visible === undefined || visible}
<div class="form-group" style={options.style} class:title={options.title} class:subtitle={options.subtitle} class:form-row-decorated={options.decorated}>
    {#if typeof title != "undefined"}
        <label class="label form-group-label">{title}</label>
    {/if}
    <slot></slot>
</div>
{/if}
