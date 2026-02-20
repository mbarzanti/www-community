<script>
    import SelectField from "../components/SelectField.svelte";
    import {legalForms} from "../rowConfig";
    import {STRINGS} from "../resources";

    export let label = STRINGS.PAGES.LEGAL_FORM.LABEL;
    export let legalForm;
    export let valid = false;
    export let ignoreDirty;
    export let disabled;

    export let values = legalForms;
    let options = {
        placeholder:"Selezionare",
        disabled: disabled
    }
</script>

<div class="row product-conf-row ">
    <div class="col-sm-8 config-content ttn legalFormSelect">
        <label class="label inline-label mw140">{label}</label>
        <div class="form-group col-sm-5">
            <SelectField bind:value={legalForm} bind:valid={valid} {values} {ignoreDirty} {options}/>
        </div>
    </div>
</div>

<style>
    .legalFormSelect{
    display: flex;
    align-items: baseline;
}
</style>