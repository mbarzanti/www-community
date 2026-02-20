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
    <div class="form-group" style={options.style} class:title={options.title}
         class:form-row-decorated={options.decorated}>
        {#if typeof title != "undefined"}
            <div class="col-xs-12 col-sm-12" style="padding-left: 0;">
                <h3 class="area-heading" class:subtitle={options.subtitle} class:title={options.title} style={options.textStyle} >{title}</h3>
            </div>
        {/if}
    </div>
{/if}
<style>
    .subtitle{
        margin-top: 0;
    }
    .title{
        font-size: 30px;
    }
</style>