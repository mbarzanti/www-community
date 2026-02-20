<script>
    import BoxMessages from "./BoxMessages.svelte";

    export let type;
    export let title;


</script>
<BoxMessages
        little={true}
        {title}
        {type}
/>