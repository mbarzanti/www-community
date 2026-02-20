<script>

    export let headerConfig;


</script>

<div class="row product-head-row">
    {#each headerConfig as headerItem, headerIndex}
        <div class="{headerItem.col} config-content" class:last-cc-col={headerIndex === headerConfig.length}>
            <div class="head-label">{headerItem.text}</div>
        </div>
    {/each}
</div>