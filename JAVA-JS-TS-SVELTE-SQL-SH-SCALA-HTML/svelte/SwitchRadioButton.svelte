<script>

    export let value = "Image";

    let optionImage = "Immagini";
    let optionImageId = "optionImageId";
    let optionPDF = "PDF";
    let optionPDFId = "optionPDF";
    let label = "Formato: ";
    let selectedFormat;

    function onChange(e){
        if(e.target.id === optionImageId){
            value = "Image";
        } else if (e.target.id === optionPDFId){
            value = "PDF";
        }
    }


</script>
<div class="col-sm-12 format-radio-selector-container">
    <div class="form-group form-group-lg format-radio-selector no-border">
        <label class="format-label-selector switchRadioLabelTitle">{label}</label>
        <div class="btn-group btn-radio" data-toggle="buttons">
            <label class="btn btn-xs btn-default switchRadioLabel" class:active={value === "Image"} id={optionImageId} on:click={onChange}>
                <input class="switchRadioInput" type="radio" name="optionsswitch" autocomplete="off" data-toggle="radio-collapse" data-target="#bloccoform1">{optionImage}</label>

            <label class="btn btn-xs btn-default switchRadioLabel" class:active={value === "PDF"} id={optionPDFId} on:click={onChange}>
                <input class="switchRadioInput" type="radio" name="optionsswitch" autocomplete="off" data-toggle="radio-collapse" data-target="#bloccoform3">{optionPDF}</label>
        </div>
    </div>
</div>


<style>
    .col-sm-12.format-radio-selector-container {
        margin-top: 5px;
        margin-bottom: 5px;
        padding-left: 0;
        padding-right: 0;
    }


    .form-group.form-group-lg.fp-item.format-radio-selector {
        padding-top: 10px;
        padding-bottom: 0;
    }

    label.format-label-selector {
        padding-bottom: 15px;
        margin-right: 10px;
    }


    .form-group.form-group-lg.fp-item.format-radio-selector.no-border {
        border: 0;
        justify-content: flex-start;
        padding-left: 0;
    }

    .switchRadioInput{
        margin-right: 5px;
        -webkit-appearance: none;
        -moz-appearance: none;
        -ms-appearance: none;
        -o-appearance: none;
        appearance: none;
        display: none;
    }

    label.switchRadioLabel{
        font-size: 11px;
        min-width: 100px;
        line-height: 25px;
        padding: 2px;
    }

    label.switchRadioLabelTitle{
        display: flex;
        justify-content: center;
        align-items: center;
        padding-bottom: 0px;
        font-size: 13px;
    }

    label.switchRadioLabel.active{
        background-color: #eedc00;
    }

</style>