<style>
    input.form-control-plaintext {
        border: 0;
        padding: 0;
        height: 46px;
    }

</style>
<script>
    export let name = undefined;
    export let value;
</script>


    <input class="form-control-plaintext" type="text"
        {name}
        readonly={true}
        value={value}
    >