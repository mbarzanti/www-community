<script>

    export let task

</script>

<div class="content-aside-header">
    <h3 class="content-aside-title mb20">{task.title}</h3>
    {#if task.subtitle.trim().length > 0 }
        <div>
            <p class="pb40"> <!--inline-msg inline-msg--attention-->
                <strong>{task.subtitle}</strong>
            </p>
        </div>
    {/if}
</div>
<style>
    .content-aside-header{
        min-height: 21px;
    }
</style>