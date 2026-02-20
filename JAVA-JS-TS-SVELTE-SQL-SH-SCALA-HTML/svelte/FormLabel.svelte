<script>
    import {onMount} from 'svelte';
    import {initPopovers} from '../libs/bootstrap';

    export let inputName = null;
    export let inputDescription;
    export let hasPopover = null;
    export let statusMessage = null;
    export let status = null;
    export let popover = null;
    export let id = null;

    let popoverProps = {};

    onMount(() => {
        if (popover) {
            popoverProps = {
                "data-toggle": "popover",
                "data-placement": "right",
                "data-trigger": "focus",
                "title": popover.title,
                "data-content": popover.content,
                "data-container": "body",
                "data-html": popover.html ? "true" : "false"
            }

            initPopovers(id ? "#"+id : null);
        }
    })
</script>

<label class="control-label" for={inputName}>
    {#if status === 'error'}
    <img class="spacer-xs-right-05"
         src="/risorse_dt/condivise/immagini/icone/ico-applicative-alert.png"
         srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-alert@2x.png 2x"
         alt={statusMessage}>
    {:else if status === 'warning'}
        <img class="spacer-xs-right-05"
             src="/risorse_dt/condivise/immagini/icone/ico-applicative-warning.png"
             srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-warning@2x.png 2x"
             alt={statusMessage}>
    {:else if status === 'success'}
        <img class="spacer-xs-right-05"
             src="/risorse_dt/condivise/immagini/icone/ico-applicative-success.png"
             srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-success@2x.png 2x"
             alt={statusMessage}>
    {/if}
    {inputDescription}{#if statusMessage} <span>&nbsp;- {statusMessage}</span>{/if}
    {#if hasPopover || popover}
        {#if popover}
        <a href="javascript:void(0)"  {...popoverProps}>
            <img src="/risorse_dt/condivise/immagini/generiche/informazioni.png"
                 srcset="/risorse_dt/condivise/immagini/generiche/informazioni@2x.png 2x"
                 class="popover-info" alt="maggiori info disponibili">
        </a>
        {:else}
            <img src="/risorse_dt/condivise/immagini/generiche/informazioni.png"
                 srcset="/risorse_dt/condivise/immagini/generiche/informazioni@2x.png 2x"
                 class="popover-info" alt="maggiori info disponibili">
        {/if}
    {/if}
</label>