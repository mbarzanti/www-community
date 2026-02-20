<script>
    export let id;
</script>

<div class="panel-group panel-group-pills" id={id} role="tablist" aria-multiselectable="true">
    <slot/>
</div>