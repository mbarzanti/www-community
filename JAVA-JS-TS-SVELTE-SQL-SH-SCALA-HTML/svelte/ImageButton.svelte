<script>
    import {createEventDispatcher} from 'svelte';

    const eventDispatcher = createEventDispatcher();

    export let text;
    export let modal;
    export let options;
    export let action;
    export let disabled = false;
    export let ordinality = 1;
    export let size ;
    export let src ;

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

<style>

button.btn.custom-btn-image {
    width: fit-content;
    min-width: 0;
    padding: 0;
    display: flex;
    align-content: center;
    height: fit-content;
    border-radius: 40px;
    min-width: 0;
    margin:auto;
    /* justify-content: center; */
    /* place-content: center; */
}
.custom-btn-text {
    margin: auto;
    padding-left: 1rem;
}
img.custom-image {
    transform: scale(0.6);
}

</style>

    <button type="button"
            class="btn custom-btn-image"
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
            on:click={handleClick}><div class="custom-btn-text">{text}</div>
            <img class="custom-image" {src} alt="background image"/></button>

