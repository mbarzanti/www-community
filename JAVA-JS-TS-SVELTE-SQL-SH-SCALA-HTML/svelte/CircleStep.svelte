<script>

    import {createEventDispatcher} from 'svelte';

    export let step = '';
    export let active = false;
    export let done = false;
    export let t;
    export let title;
    export let taskName;
    export let clickable = false;

    let href = clickable ? "javascript:void(0)" : undefined;

    const circleEventDispatcher = createEventDispatcher();

    function goToTask(e) {
        if ( clickable){
            circleEventDispatcher('click', {"step": step, "t": t});
        }
    }

</script>

<div class="circle-step" class:active={active} class:done={done[taskName]}>
    <a {href} class="step-link" on:click={goToTask}>
        <div class="circle-step-circle">
            <span>{t}</span>
        </div>
    </a>
    <div class="circle-step-title">{title}</div>
    <div class="circle-step-bar-left"></div>
    <div class="circle-step-bar-right"></div>
</div>