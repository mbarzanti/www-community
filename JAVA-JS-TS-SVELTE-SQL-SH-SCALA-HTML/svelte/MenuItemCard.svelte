<script>
    import {
        Card,
        CardTitle,
        CardSubtitle,
        Ripple
    } from 'svelte-materialify/src';

    import { createEventDispatcher } from 'svelte';

    export let title;
    export let subtitle;
    export let image;

    const dispatch = createEventDispatcher();

    function handleClickEvent(e) {
        dispatch('click', {
            detail: e.detail
        });
    }

</script>

<Card >
    <div use:Ripple={{ centered: true }} on:click={handleClickEvent}>
        <CardTitle>{title}</CardTitle>
        <CardSubtitle>{subtitle}</CardSubtitle>
        <img src={image} style="max-height: 100px;align-content: center;padding: 10px;"/>
    </div>
</Card>