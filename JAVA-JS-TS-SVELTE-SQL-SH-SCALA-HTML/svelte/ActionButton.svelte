<script>
    import {createEventDispatcher} from 'svelte';
    import ButtonWrapper from "./ButtonWrapper.svelte";
    const eventDispatcher = createEventDispatcher();

    export let name = 'Button';
    export let modal;
    export let options;
    export let action;
    export let disabled = false;
    export let ordinality = 1;
    export let size ;
    export let standalone = false;
    export let handleClick;

    function innerHandleClick(e) {
        if (typeof handleClick === 'function'){
            return handleClick(action);
        } else {
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
    }

</script>

<ButtonWrapper bind:standaloneButton={standalone}>
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
            disabled="{disabled?disabled:undefined}"
            on:click={innerHandleClick}>
        {name}
    </button>
</ButtonWrapper>
