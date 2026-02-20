<script>

    import {onMount} from 'svelte';

    export let label;
    export let type;
    export let visible = true;
    export let predicate = {};
    export let descriptor;
    export let size;
    let cssClass;

    onMount(() => {
       predicate = descriptor.predicate;
       cssClass = createCssClassForSize(size);
    });

    function createCssClassForSize(size) {
        switch (size) {
            case "xxs":
                return "col-xs-6 col-sm-2";
            case "xs":
                return "col-xs-6 col-sm-3";
            case "ld":
                return "col-xs-6 col-sm-4";
            case "pmd":
                return "col-xs-6 col-sm-5";
            case "md":
                return "col-xs-6 col-sm-6"
            case "hmd":
                return "col-xs-6 col-sm-8";
            case "hd":
                return "col-xs-6 col-sm-9";
            case "lg":
                return "col-xs-6 col-sm-12";
            default:
                if(size){
                    return size;
                } else {
                    return "";
                }
        }
    }

    function insertRequiredMark(){
        if ( descriptor && descriptor.options && descriptor.options.required) {
            return "*"
        } else {
            return ""
        }
    }

</script>

{#if visible}
<!--<div class="form-group">-->
<div class={cssClass}>
    {#if typeof label != "undefined" && type !== "checkbox"}
        <label class="label form-element-label">{label}{insertRequiredMark()}</label>
    {/if}
    <slot></slot>
</div>
{/if}
