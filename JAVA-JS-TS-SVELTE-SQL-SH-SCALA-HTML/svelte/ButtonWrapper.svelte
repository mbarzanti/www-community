<script>
    export let standaloneButton = false;
    export let stylingClass = "buttons";
</script>

{#if !standaloneButton }
<div class={stylingClass}>
    <slot></slot>
</div>
{:else}
    <slot></slot>
{/if}
