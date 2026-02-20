<script>
    import {slide} from 'svelte/transition';

</script>

<div transition:slide|local>
    <slot></slot>
</div>