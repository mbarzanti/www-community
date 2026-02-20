<script>
    import Filepicker from "../../../../../suit/uikit/up/file/Filepicker.svelte";
    import PdfFilepicker from "../../../../../suit/uikit/up/file/PdfFilepicker.svelte";
    import SwitchRadioButton from "./SwitchRadioButton.svelte";
    import {createEventDispatcher, onMount} from "svelte";

    export let document;
    export let label;
    export let form;
    export let formPdf;
    export let example = true;
    export let options;

    export let valid;
    export let uploadComplete;
    export let value;

    export let format;
    export let ignoreDirty = false;

    let selectedFormat;
    let ready = false;

    const eventDispatcher = createEventDispatcher();

    function handleChange(e) {
        eventDispatcher('change', e.detail);
    }

    let optionsFPImage = options.optionsFPImage;
    let optionsFPPdf = options.optionsFPPdf;



    onMount(
        ()=>{
            if(value && value.DOCUMENTO){
                selectedFormat = "PDF"
            } else {
                selectedFormat = "Image"
            }
            ready = true;
        }
    )


</script>

<slot/>
    <SwitchRadioButton bind:value={selectedFormat}/>

    {#if ready}
        {#if selectedFormat === "Image"}
            <!--/<Filepicker {...form} bind:valid bind:uploadComplete/>-->
            <Filepicker name={optionsFPImage.name} options={optionsFPImage.options} bind:valid={valid} {ignoreDirty} style={optionsFPImage.style}
                        on:change={handleChange} bind:value={value} bind:uploadComplete
            />
        {:else if selectedFormat === "PDF"}
            <p style="margin-left: 8px;font-style: italic;font-size: 13px;margin-bottom: 28px;">
                {"Assicurarsi che il \"fronte\" e il \"retro\" del documento siano inseriti rispettivamente nella prima e nella seconda pagina del file."}
            </p>
            <PdfFilepicker name={optionsFPPdf.name} options={optionsFPPdf.options} bind:valid={valid} {ignoreDirty} style={optionsFPPdf.style}
            on:change={handleChange} bind:value={value} bind:uploadComplete
                           />
            <!--<PdfFilepicker {...formPdf} bind:valid bind:uploadComplete/>-->
        {/if}
    {/if}