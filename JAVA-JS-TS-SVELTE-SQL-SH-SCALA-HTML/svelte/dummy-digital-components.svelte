<script>
    import {_} from 'svelte-i18n';
    import {writable} from 'svelte/store';
    import {onMount} from 'svelte';
    import Loader from "../../../suit/uikit/digital/elements/Loader.svelte";
    import AutoFormPageWizard from "../../../suit/uikit/digital/form/wizard/AutoFormPageWizard.svelte";

    import {FormBuilder} from "../../../suit/uikit/templates/FormBuilder";
    import {StepBuilderOrchestrator} from "../../../suit/uikit/stepbuilder/StepBuilderOrchestrator";
    import api from "../../api/api";
    import * as componentType from "../../../suit/uikit/formTypes";
    import Typo from "../../../suit/cdm/typos/Typo";
    import {heading} from "../../../suit/uikit/templates/general";
    import BoxAdvice from "../../../suit/uikit/digital/ui/Messages/FieldErrorBoxMessages.svelte";
    import FieldErrorBoxMessages from "../../../suit/uikit/digital/ui/Messages/FieldErrorBoxMessages.svelte";
    import {FIELD_MESSAGES_TYPE} from "../../../suit/uikit/digital/ui/Messages/utils";

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

            let city = new writable({});
            let province = new writable({});

            let citySelect = FormBuilder.newForm("select", "city", "Città", new Typo("B239", "BUCCIANO"), "Test_ID_Select")
                .setExportState(city)
                .setContextMaster(province)
                .setLiveSearch(true)
                .setHandlerView(api.typos.cities.bind(api.typos))
                .setPlaceholder("Seleziona")
                .setSize("ld")
                .build();
            let provinceSelect = FormBuilder.newForm("select", "province", "Provincia", new Typo("BN", "BENEVENTO"), "Test_ID_Select")
                .setExportState(province)
                .setLiveSearch(true)
                .setInitFunction(api.typos.provinces.bind(api.typos))
                .setPlaceholder("Seleziona")
                .setSize("ld")
                .setPopOver("Testiamo", "Ma con calma", "content")
                .build();
            let capSelect = FormBuilder.newForm("select", "cap", "CAP", new Typo("82010", "82010"), "Test_ID_Select")
                .setContextMaster(city)
                .setLiveSearch(true)
                .setHandlerView(api.typos.zipCodes.bind(api.typos))
                .setPlaceholder("Seleziona")
                .setSize("ld")
                .build();


            ateco.code = "466999";
            ateco.description = "Commercio ingrosso macchine ed attrezz. per industria, commercio e navigazione";
            let placeholderText = "Non è morto ciò che in eterno può attendere, E con il passare di strani eoni anche la morte può morire";
            let button = FormBuilder.newForm("button", "ID_button", "Cliccami", "", "TestClass_Button").setOption("ordinality",2).setSize("ld").build();
            let datePicker = FormBuilder.newForm("datepicker", "Name_Date_Picker", "Scegli una data", "", "Test_ID_DatePicker").setDateFormat("dd/mm/yyyy").setStyle("background-color: white !important").setValidationMessage("Campo non valido").setStartDate("+4d").build();
            let radios = FormBuilder.newForm("radio", "ID_Radio_Button", "Scegli un'opzione", "2", "TestClass_Radio")
                .setValues(values)
                .setSize("ld")
                .setInline(false)
                .setStyle("background-color: white !important")
                .setPopOver("Testiamo", "Ma con calma")
                .build();


            let downloadComponent = FormBuilder.newForm(componentType.DOWNLOAD, "ID_Download", "Test Download", "")
                .setPopOver("Cliccami", "Serve per scaricare")
                .setSize("lg")
                .setFileType("img")
                .setHandler(api.test.mockDownload)
                .setLabels(["FRONTE", "RETRO"])
                .build();

            let filePickerComponent = FormBuilder.newForm(componentType.FILEPICKER, "ID_Filepicker", "Test Upload", "")
                .setPopOver("Cliccami", "Serve per fare upload di documenti")
                .setSize("lg")
                .setHandler(api.test.mockUpload)
                .setPreviewHandler(api.test.mockPreview)
                .setLabels(["FRONTE", "RETRO"])
                .build();

            let select = FormBuilder.newForm("select", "Name_Select", "Seleziona un'opzione'", "4", "Test_ID_Select").setValues(values).setSize("lg").setStyle("background-color: white !important").build();
            let checkbox = FormBuilder.newForm("checkbox", "ID_Checkbox", "Scegli un'opzione", "", "TestClass_Checkbox").setValues(values).setSize("pmd").setStyle("background-color: white !important").setPopOver("Con attenzione", "Potrebbe essere la tua ultima", "label", {trigger: "click"}).build();
            let label = FormBuilder.newForm("label", "Name_Label", "Questa è una semplice etichetta'", "Oppure no !?!?", "Test_ID_Label").setSize("lg").setStyle("color: blue !important").build();
            let textaction = FormBuilder.newForm("textaction", "ID_Textaction", "Scrivi qualcosa con delle conseguenze", "", "TestClass_Textaction").setPopOver("La conseguenza", "Per tua fortuna nessuna").setSize("lg").setStyle("background-color: white !important").build();
            let text = FormBuilder.newForm("text", "Name_Text", "Scrivi qualcosa senza conseguenze'", "", "Test_Text").setPopOver("L'autore della citazione", "H.P. Lovecraft").setSize("lg").setStyle("color: blue !important").setPlaceholder(placeholderText).build();
            let multicheckbox = FormBuilder.newForm("multicheckbox", "ID_Multicheckbox", "Scegli più opzioni", "3", "TestClass_Multicheckbox").setInline(false).setSelectionLimit(3).setValues(values).setSize("lg").setStyle("background-color: white !important").setPredicate({"Name_Select": "6"}).build();
            let row = StepBuilderOrchestrator.newRow(false).addFormItem(text).build();
            let row_2_2 = StepBuilderOrchestrator.newRow(false).setTextDescription("una volta tornato al suo villaggio nativo di Que-shu, Riverwind passò poi il magico bastone alla sua compagna Goldmoon").build();
            let row_2 = StepBuilderOrchestrator.newRow(false).addFormItem(checkbox).addFormItem(button).build();
            let row_3 = StepBuilderOrchestrator.newRow(true).addFormItem(label).addFormItem(select).addFormItem(multicheckbox).build();
            let row_3_1 = StepBuilderOrchestrator.newRow(false).addFormItem(downloadComponent).build();
            let row_3_2 = StepBuilderOrchestrator.newRow(true).addFormItem(filePickerComponent).build();
            let row_4 = StepBuilderOrchestrator.newRow(true).addFormItem(radios).build();
            let row_5 = StepBuilderOrchestrator.newRow(false).addFormItem(textaction).build();
            let row_6 = StepBuilderOrchestrator.newRow(false).addFormItem(datePicker).build();
            let row_address = StepBuilderOrchestrator.newRow(true).addFormItem(provinceSelect).addFormItem(citySelect).addFormItem(capSelect).build();
            let task = StepBuilderOrchestrator.newTask("Test_Task", "Title_Test_Task", "SubTitle_Test_Task")
                .addRow(row).addRow(row_2).addRow(row_2_2)
                .addRow(heading("Heading Generico")).addRow(row_3).addRow(row_3_1).addRow(row_3_2)
                .addRow(row_4).addRow(row_5).addRow(row_6).addRow(row_address).build();
            let filepicker = FormBuilder.newForm("filepicker", "Name_Filepicker", "Prendi un file'", "", "Test_ID_Filepicker").setSize("ld").setStyle("background-color: white !important").build();
            let download = FormBuilder.newForm("download", "ID_Download", "Scarica un file", "", "TestClass_Download").setSize("ld").setStyle("background-color: white !important").build();
            let gect = FormBuilder.newForm("gect", "Name_Gect", "Gect un file'", "", "Test_Gect").setSize("ld").setStyle("background-color: white !important").build();
            let row_1_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(filepicker).build();
            let row_2_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(download).build();
            let row_3_task_2 = StepBuilderOrchestrator.newRow(false).addFormItem(gect).build();
            let task_2 = StepBuilderOrchestrator.newTask("Test_File_Task", "Title_File_Test_Task_2", "SubTitle_File_Test_Task_2").addRow(row_2_task_2).addRow(row_1_task_2, 0).addRow(row_3_task_2).build();
            let step_2 = StepBuilderOrchestrator.newStep("Test_File_Step", "Title_File_Test_Step_2", "SubTitle_File_Test_Step_2").addTask(task_2).build();
            let step = StepBuilderOrchestrator.newStep("Step_Test", "Title_Test_Step", "SubTitle_Test_Step").addTask(task).addTask(task_2).build();
            formComplete = StepBuilderOrchestrator.stepBuilder().addStep(step).addStep(step_2).buildSteps();
        } catch (e) {
            console.error("Error encountered parsing customer data", e);
        }
        console.log(formComplete);

        return formComplete;
    }


    const messages = [
        {
            type: FIELD_MESSAGES_TYPE.FIELD_VALIDATION,
            field: {
                id: "ID_Textaction",
                label: "Scrivi qualcosa con delle conseguenze"
            },
            prefix: "Il campo",
            suffix: "contiene caratteri non validi"
        },
        {
            type: FIELD_MESSAGES_TYPE.FIELD_REQUIRED,
            field: {
                id: "ID_Multicheckbox",
                label: "Scegli più opzioni"
            },
            prefix: "Il campo",
            suffix: "è obbligatorio"
        },
        {
            type: FIELD_MESSAGES_TYPE.TEXT,
            text: "La speranza è vana"
        }
    ]



</script>

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
