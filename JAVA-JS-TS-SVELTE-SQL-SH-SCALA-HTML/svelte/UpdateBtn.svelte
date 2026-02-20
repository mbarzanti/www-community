<script>
    import {Button, Icon} from 'svelte-materialify';
    import {mdiTableEdit} from '@mdi/js';
    import {createEventDispatcher} from 'svelte';

    const dispatcher = createEventDispatcher();

    function handleClick(e) {
        dispatcher('click', {
            detail: e.detail
        })
    }
</script>

<Button on:click={handleClick}>
    <Icon path={mdiTableEdit} />
</Button>