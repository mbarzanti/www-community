<script>
    export let markCompleted;
    export let stepIndex;
</script>
<span class="panel-raise bold spacer-xs-right-15">
    {#if markCompleted}
        <img class="vam"
             src="/risorse_dt/condivise/immagini/icone/ico-applicative-success.png"
             srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-success@2x.png 2x"
             alt="Step completato"/>
    {:else if stepIndex}
        {stepIndex}
    {:else}
        &nbsp;
    {/if}
</span>
<slot/>