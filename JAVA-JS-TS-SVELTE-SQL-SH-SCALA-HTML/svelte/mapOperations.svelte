<script>
    import {_} from 'svelte-i18n';
    import AutoFormPageWizard from "../../../suit/uikit/up/form/wizard/AutoFormPageWizard.svelte";
    import * as general from '../../../suit/uikit/templates/general';
    import Loader from "../../../suit/uikit/up/elements/Loader.svelte";
    import * as contact from '../../../suit/uikit/templates/examples/contact';
    import * as account from '../../../suit/uikit/templates/examples/account';
    import * as MapOperationsHandlers from './mapOperations-handlers';
    import {FormBuilder} from "../../../suit/uikit/templates/FormBuilder";
    import {StepBuilderOrchestrator} from "../../../suit/uikit/stepbuilder/StepBuilderOrchestrator";
    import * as componentType from "../../../suit/uikit/formTypes";
    import * as componentSize from "../../../suit/uikit/formSize";

    import CustomForm from "./CustomForm.svelte";
    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;


    let promise = genReviewUiModel()

    setTimeout(() => {
        nextStateEnabled = true;
    }, 1000);

    async function genReviewUiModel() {
        let step = StepBuilderOrchestrator.newStep("dummy","dummy","dummy");
        let task = StepBuilderOrchestrator.newTask("Task1","Task1","Task1")
            .setLoader(MapOperationsHandlers.operation)
            .addObserver(MapOperationsHandlers.observer)
            .setLegend("Inserire testo legenda");
        let row = StepBuilderOrchestrator.newRow(false);

        let customForm = FormBuilder.newForm(componentType.CUSTOM_FORM, "customForm", "", undefined)
            .setSize(componentSize.default.XXL_12)
            .setComponentClass(CustomForm).build();

        let label = FormBuilder.newForm(componentType.LABEL, "label", "", "")
            .setSize(componentSize.default.XXL_12).build();

        let email = contact.email("email", $_("review.form.email.email"), false, {
            size: "ld"
        }).build();
        let emailPec = contact.email("emailPec", $_("review.form.email.pec"), false, {
            size: "ld"
        }).build();
        let ibanCombo = account.ibanCombo("ibanCombo", "super combo", false, {
                size: "ld"
            }).build();

        let actionText = FormBuilder.newForm(componentType.TEXTACTION, "action", "", "inserisci testa")
            .setActionTextField( (currentStepName, currentTaskName, values_complete_map, value, refresh)=> {
                setTimeout(()=>{values_complete_map[currentStepName][currentTaskName]["label"] = "CROCE! " + value; refresh();},245);
            }).build();

        row = row.addFormItem(email).addFormItem(emailPec).addFormItem(ibanCombo).addFormItem(customForm).addFormItem(label).addFormItem(actionText);
        task = task.addRow(row.build());
        step = step.addTask(task.build());


        return StepBuilderOrchestrator.stepBuilder().addStep(step.build()).buildSteps();
    }

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
    <div style="margin-top: 80px;"></div>



<style>
    :global(input.input-text.form-control.custom-label) {
        border: 0;
        padding-left: 0;
        margin-top: -0.6rem;
        margin-bottom: 0.5rem!important;
    }

    :global(.form-group.title.form-row-decorated) {
        margin-bottom: 10px!important;
    }

    :global(.form-group.subtitle.form-row-decorated) {
        padding-bottom: 5px!important;
        margin-bottom: 5px!important;
    }

    :global(label.label.form-group-label) {
        padding-bottom: 0!important;
        /* margin-bottom: 0px!important; */
        /* border-spacing: 2rem!important; */
        /* margin-bottom: 5px!important; */
    }

</style>
