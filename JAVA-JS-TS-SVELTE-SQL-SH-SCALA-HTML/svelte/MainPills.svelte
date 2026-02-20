<script>
    export let id = undefined;
    export let title = "";
    export let text = "";
</script>
<div class="main-pills" {id}>
    <div class="main-pills-wrap">
        <slot name="content">
            {#if (text && text.length > 0) || (title && title.length > 0)}
                <div class="row">
                    <div class="col-sm-12">
                        {#if title && title.length > 0}
                            <h2>{title}</h2>
                        {/if}
                        {#if text && text.length > 0}
                            <p>{text}</p>
                        {/if}
                    </div>
                </div>
            {/if}
        </slot>
        <slot/>
    </div>
</div>