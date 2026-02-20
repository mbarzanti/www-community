<script>

    import {PAGES_SERVICE as labelsUP} from "./../../up/views/labels";
    import {PAGES_SERVICE as labelsDigital} from "./../../digital/views/labels";
    import * as constants from "../../../commons/constants";
    import {onMount} from "svelte";

    import ViewUP from "./../../up/views/SuccessView.svelte"
    import ViewDigital from "./../../digital/views/SuccessView.svelte"


    export let kit = constants.KIT.UP;

    let currentTemplate=undefined;
    let currentTemplateView=undefined;
    export let title;
    export let text;
    export let additionalButton;
    export let props = {};

    function getViewComponent(){
        if(kit === constants.KIT.UP){
            return ViewUP;
        } else if(kit === constants.KIT.DIGITAL){
            return ViewDigital;
        } else {
            return ViewUP;
        }
    }

    function getPageDefaultLabels(){
        if(kit === constants.KIT.UP){
            return labelsUP;
        } else if(kit === constants.KIT.DIGITAL){
            return labelsDigital;
        } else {
            return labelsUP;
        }
    }

    onMount(
        ()=>{
            let labels = getPageDefaultLabels();

            currentTemplate = {
                title: labels.SUCCESS.TITLE,
                text: labels.SUCCESS.TEXT
            }
            currentTemplateView = getViewComponent();
        }
    )


</script>

{#if currentTemplateView}
    <svelte:component this={currentTemplateView}
                      title={currentTemplate.title}
                      text={currentTemplate.text}
                      {additionalButton} {props}>
        <slot/>
    </svelte:component>
{/if}