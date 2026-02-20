<script>
    import {onMount} from 'svelte';

    // POLLING
    onMount(() => {
        setInterval(() => {
            callFunction(functionParams);
            if (debug) {
                debug_container.innerHTML = new Date();
            }
        }, interval);


    });

    export let callFunction;
    export let functionParams = {};
    export let interval = 5000;
    export let debug = false;
    let debug_container;

</script>



{#if debug}
    <div style="font-size: x-small">DEBUG MODE ENABLED. LAST POLLING OPERATION DONE AT :</div>
    <div bind:this={debug_container} style="font-size: x-small"></div>
{/if}
<slot></slot>
