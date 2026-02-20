<script>
    import {createEventDispatcher} from 'svelte';

    const eventDispatcher = createEventDispatcher();

    export let name = 'Button';
    export let modal = undefined;
    export let options = {};
    export let action = undefined;
    export let disabled = false;
    export let ordinality = 1;
    export let size = undefined ;

    function handleClick() {
        if (action) {
            eventDispatcher('click', {
                action: action
            });
        } else {
            eventDispatcher('click', {
                action: options && options.action ? options.action : 'foo'
            });
        }
    }

</script>

<div class="buttons">
    <button type="button"
            class="btn"
            class:btn-primary={ordinality==1}
            class:btn-secondary={ordinality==2}
            class:btn-tertiary={ordinality==3}
            class:btn-quaternary={ordinality==4}
            class:btn-small={size==1}
            class:btn-medium={size==2}
            class:btn-large={size==3}
            class:btn-two-rows={size==4}
            class:disabled={disabled}
            data-toggle="{ modal ? 'modal' : undefined}" data-target="{ modal ? '#'+modal : undefined}"
            disabled={disabled?disabled:undefined}
            on:click={handleClick}>{name}</button>
</div>
