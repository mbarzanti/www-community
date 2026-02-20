<script>

    export let active = false;
    export let done = false;
    export let index;
    export let title;


</script>

<div class="circle-step" class:active={active} class:done={done}>
    <a href={undefined} class="step-link clickable">
        <div class="circle-step-circle">
            <span>{index}</span>
        </div>
    </a>
    <div class="circle-step-title">{title}</div>
    <div class="circle-step-bar-left"></div>
    <div class="circle-step-bar-right"></div>
</div>