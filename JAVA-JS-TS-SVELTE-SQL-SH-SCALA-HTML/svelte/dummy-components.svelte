<script>
    import {_} from 'svelte-i18n';
    import {onMount} from 'svelte';
    import Loader from "../../../suit/uikit/up/elements/Loader.svelte";
    import Accordion from "../../../suit/uikit/up/elements/Accordion.svelte";
    import AutoFormPageWizard from "../../../suit/uikit/up/form/wizard/AutoFormPageWizard.svelte";
    import {FormBuilder} from "../../../suit/uikit/templates/FormBuilder";
    import {StepBuilderOrchestrator} from "../../../suit/uikit/stepbuilder/StepBuilderOrchestrator";
    import * as componentType from "../../../suit/uikit/formTypes";
    import api from "../../api/api";
    import * as constants from "../../../suit/uikit/formTypes";

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;

    let promise = genReviewUiModel();
    let type = "number";
    let type_e = "email";
    let options = {
        "inputUppercase": true,
        "required": false,
        "placeholder": "placeholder",
        "validationMessage": "validationMessageString",
        "size": "ld",
        "action": "function myFunction() { document.getElementById(\"\"demo\"\").style.color = \"\"red\"\"; }"
    };
    let name = "Button";
    let ordinality = 2;
    let size = 4;
    let modal = "modale";
    let action = "function myFunction() { document.getElementById(\"\"demo\"\").style.color = \"\"red\"\"; }"
    runAlternativeFlow;
    runAction;


    onMount(() => {
    });
    async function genReviewUiModel() {
        let path = [];
        let items = [];
        let values = [];
        let formComplete;
        let objChange = {}
        try {
            for (let i = 0; i < 10; i++) {
                objChange = {
                    "label": "test_" + i,
                    "value": "" + i
                }
                values.push(objChange);
            }
            console.log(values);
            let defaultValue = ["2", "4", "6", "7"];
            let ateco = {};
            ateco.code = "466999";
            ateco.description = "Commercio ingrosso macchine ed attrezz. per industria, commercio e navigazione";
            let placeholderText = "Non è morto ciò che in eterno può attendere, E con il passare di strani eoni anche la morte può morire";
            let button = FormBuilder.newForm("button", "ID_button", "Button", "", "TestClass_Button").setSize("ld").setButtonSize(1).setStyle("background-color: white !important");
            let datePicker = FormBuilder.newForm("datepicker", "Name_Date_Picker", "Scegli una data", "", "Test_ID_DatePicker").setDateFormat("dd-mm-yyyy").setStyle("background-color: white !important");
            let radios = FormBuilder.newForm("radio", "ID_Radio_Button", "Scegli un'opzione", "", "TestClass_Radio").setValues(values).setSize("ld").setInline(true).setStyle("background-color: white !important");
            let select = FormBuilder.newForm("select", "Name_Select", "Seleziona un'opzione'", "", "Test_ID_Select").setValues(values).setSize("lg").setStyle("background-color: white !important");
            let checkbox = FormBuilder.newForm("checkbox", "ID_Checkbox", "Scegli un'opzione", "", "TestClass_Checkbox").setValues(values).setSize("pmd").setStyle("background-color: white !important");
            let label = FormBuilder.newForm("label", "Name_Label", "Questa è una semplice etichetta'", "", "Test_ID_Label").setSize("lg").setStyle("color: blue !important");
            let textaction = FormBuilder.newForm("textaction", "ID_Textaction", "Scrivi qualcosa con delle conseguenze", "", "TestClass_Textaction").setPopOver("La conseguenza", "Per tua fortuna nessuna").setSize("lg").setStyle("background-color: white !important");
            let text = FormBuilder.newForm("text", "Name_Text", "Scrivi qualcosa senza conseguenze'", "", "Test_Text").setPopOver("L'autore della citazione", "H.P. Lovecraft").setSize("lg").setStyle("color: blue !important").setPlaceholder(placeholderText);
            let multicheckbox = FormBuilder.newForm("multicheckbox", "ID_Multicheckbox", "Scegli più opzioni", "", "TestClass_Multicheckbox").setValues(values).setSize("pmd").setStyle("background-color: white !important");
            let accordion = FormBuilder.newForm(constants.CUSTOM_FORM, "id_accordion", "Prova Label Accordion").setComponentClass(Accordion);

            let downloadComponent = FormBuilder.newForm(componentType.DOWNLOAD, "ID_Download", "Test Download", "")
                .setPopOver("Cliccami", "Serve per scaricare")
                .setSize("lg")
                .setFileType("img")
                .setHandler(api.test.mockDownload).setPredicate({"ID_Checkbox": true})
                ;

            let filePickerComponent = FormBuilder.newForm(componentType.FILEPICKER, "ID_Filepicker", "Test Upload", "")
                .setPopOver("Cliccami", "Serve per fare upload di documenti")
                .setSize("lg")
                .setHandler(api.test.mockUpload)
                .setPreviewHandler(api.test.mockPreview)
                .setLabels(["FRONTE","RETRO"])
                ;

            let row = StepBuilderOrchestrator.newRow(false).addFormItem(text).addFormItem(accordion);
            let row_1 = StepBuilderOrchestrator.newRow(true).addFormItem(downloadComponent).addFormItem(filePickerComponent);
            let row_2 = StepBuilderOrchestrator.newRow(true).addFormItem(datePicker).addFormItem(checkbox).addFormItem(button);
            let row_3 = StepBuilderOrchestrator.newRow(true).addFormItem(label).addFormItem(select).addFormItem(multicheckbox);
            let row_4 = StepBuilderOrchestrator.newRow(true).addFormItem(radios);
            let row_5 = StepBuilderOrchestrator.newRow(true).addFormItem(textaction);
            let task = StepBuilderOrchestrator.newTask("Test_Task", "Title_Test_Task", "SubTitle_Test_Task")
                .addRow(row)
                .addRow(row_1)
                .addRow(row_2).addRow(row_3).addRow(row_4).addRow(row_5);
            let filepicker = FormBuilder.newForm("filepicker", "Name_Filepicker", "Prendi un file'", "", "Test_ID_Filepicker").setSize("ld").setStyle("background-color: white !important");
            let download = FormBuilder.newForm("download", "ID_Download", "Scarica un file", "", "TestClass_Download").setSize("ld").setStyle("background-color: white !important");
            let gect = FormBuilder.newForm("gect", "Name_Gect", "Gect un file'", "", "Test_Gect").setSize("ld").setStyle("background-color: white !important");
            let row_1_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(filepicker);
            let row_2_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(download);
            let row_3_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(gect);
            let task_2 = StepBuilderOrchestrator.newTask("Test_File_Task", "Title_File_Test_Task_2", "SubTitle_File_Test_Task_2").addRow(row_2_task_2).addRow(row_1_task_2, 0).addRow(row_3_task_2);
            let step_2 = StepBuilderOrchestrator.newStep("Test_File_Step", "Title_File_Test_Step_2", "SubTitle_File_Test_Step_2").addTask(task_2);
            let step = StepBuilderOrchestrator.newStep("Step_Test", "Title_Test_Step", "SubTitle_Test_Step").addTask(task).addTask(task_2);
            formComplete = StepBuilderOrchestrator.stepBuilder().addStep(step).addStep(step_2).buildSteps();
        } catch (e) {
            console.error("Error encountered parsing customer data", e);
        }
        console.log(formComplete);

        return formComplete;
    }


</script>

<div class='container'>
    {#await promise}
        <div class="center-loader">
            <div>
                <Loader/>
            </div>
        </div>

    {:then reviewUiModel}
        <AutoFormPageWizard
                title={'Richiesta Anticipazione Cassa Integrazione Guadagni'}
                flowRestricted={true}
                enableEditCtxBtn={false}
                externalButtons={false}
                externalStepper={false}
                path={reviewUiModel}
                submitted={false}
                readOnly={true}
                on:submit={undefined}
                on:complete={undefined} />
    {/await}
    <div style="margin-top: 80px;"></div>
</div>

<style>

    .container {
        min-height: 200px;
        height: calc(100vh - 15em);
    }

    h2{
        margin-top: 40px;
        text-align: center;
    }

</style>
