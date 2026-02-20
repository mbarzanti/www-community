<script>
    export let indent = false;
    export let row = true;
    export let decorated = true;
</script>

<div class="form-group" class:form-row={row} class:form-row-decorated={decorated} class:p-lg-5={indent}>
    <slot></slot>
</div>
