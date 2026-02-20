<script>
    export let status = null;
    export let isDisabled = null;

    $: hasError = status === 'error';
    $: hasSuccess = status === 'success';
    $: hasWarning = status === 'warning';
</script>
<div class="checkbox"
     class:has-error={hasError}
     class:has-warning={hasWarning}
     class:has-success={hasSuccess}
     class:disabled={isDisabled}
>
    <slot />
</div>