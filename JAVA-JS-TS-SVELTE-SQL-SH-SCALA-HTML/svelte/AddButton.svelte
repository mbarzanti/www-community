<script>
    import {BUTTON_TYPE} from "../../../commons/typos/definitions";

    export let objectSelectId;
    export let objectSelectType="modal";
    export let type;
    export let onClick;
    export let label = "Aggiungi figura";


</script>

<div class="quantity-chooser">
    <a class="abs-action btn-add" data-target="#{objectSelectId}" data-toggle="{objectSelectType}" class:btn-minus={type === BUTTON_TYPE.REMOVE}
       href="javascript:void(0)" tabindex="0" on:click={onClick}>
        <span class="label">{label}</span>
    </a>
</div>