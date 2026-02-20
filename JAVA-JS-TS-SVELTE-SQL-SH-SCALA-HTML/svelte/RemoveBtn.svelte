<script>
    import {Button, Icon} from 'svelte-materialify';
    import {mdiTableRemove} from '@mdi/js';
    import {createEventDispatcher} from "svelte";

    const dispatcher = createEventDispatcher();

    function handleClick(e) {
        dispatcher('click', {
            detail: e.detail
        })
    }
</script>

<Button on:click={handleClick}>
    <Icon path={mdiTableRemove} />
</Button>