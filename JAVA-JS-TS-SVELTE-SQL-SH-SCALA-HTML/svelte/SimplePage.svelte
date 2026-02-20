<script>
    export let ignore=false;

</script>

{#if !ignore}
<div class="customer-top-area customer-top-area-nfid inside-header">
    <div class="container width960">
        <slot></slot>
    </div>
</div>
{:else}
    <slot></slot>
{/if}