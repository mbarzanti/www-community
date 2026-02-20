<script>
    import Label from "../../ui/kit/forms/Label.svelte";
    import FormRow from "../../ui/kit/forms/FormRow.svelte";
    import FormItem from "../../ui/kit/mobileForms/FormItem.svelte";
    import {PAGE_STATUS_FAIL, PAGE_STATUS_OCR_FAIL, PAGE_STATUS_OK} from "../../commons/constants/general";
    import {DOCUMENT_CATEGORY, FORMAT, identityDocumentMap} from "../../commons/constants/documents";
    import {_} from 'svelte-i18n'
    import {onMount} from "svelte";
    import ScanDocumentGuidelines from "../../components/ScanDocumentGuidelines.svelte";
    import ScanDocumentForm from "../../components/ScanDocumentForm.svelte";

    export let appState;
    export let nextStateEnabled;
    export let openTicket;
    export let runAlternativeFlow = undefined;
    export let onMainRepeatableAction = undefined;

    let ready = false;
    let customerId;
    let roleTitle;
    let taxCode;
    let documents = {};
    let values = {};

    $:nextStateEnabled = documents && !!Object.keys(documents).every((documentItem) => documents[documentItem].uploaded);
    $:{
        if (ready && appState.context.reload) {
            appState.context.reload = false;
            ready = false;
            onPropsChange();
        }
    }

    onMount(() => {
        appState.context.reload = false;
        onPropsChange();
    });

    function onPropsChange() {
        customerId = appState.context.customerId;
        appState.stepTitle = $_('pages.upload.title');
        appState.continueMessage = $_('pages.general.continue');

        roleTitle = appState.context.roleTitle;
        taxCode = appState.context.taxCode;
        documents = appState.context.documents;
        if(!appState.context.values){
            appState.context.values = {};
            Object.keys(documents).forEach(docId=>{
                if(!appState.context.values[docId]){
                    appState.context.values[docId] = documents[docId].default_value;
                }
            });
        }
        values = appState.context.values;
        //Object.keys(documents).forEach((item)=>{values[item] = {}})
        setTimeout(()=>{ready = true;},1);

    }

</script>

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}



{#if ready && (appState.failCode === PAGE_STATUS_OCR_FAIL || appState.failCode === PAGE_STATUS_OK || appState.failCode === PAGE_STATUS_FAIL)}
    <div class="container">
        <form class="generic-form bordered-elements">
            <!-- DOCUMENTS GUIDELINES -->
            <ScanDocumentGuidelines/>
            <!-- ROLE TITLE -->
            <FormItem trasformNone={true} label={roleTitle} visible={true} title={true} type={"label"}/>
            <!-- CUSTOMER TAXCODE -->
            <div class="col-xs-12 col-sm-12" style="padding-left:0.2rem;">
                <FormRow indent={false} decorated={false}>
                    <FormItem size="lg" label={$_('form.taxData.taxCode')} visible={true} type={"label"}>
                        <Label value={taxCode}/>
                    </FormItem>
                </FormRow>
            </div>
            <!-- CUSTOMER IDENTITY DOCUMENT -->
            <ScanDocumentForm document={documents[DOCUMENT_CATEGORY.IDENTITY].document}
                              form={documents[DOCUMENT_CATEGORY.IDENTITY].form}
                              formPdf={documents[DOCUMENT_CATEGORY.IDENTITY].formPdf}
                              label={$_("form.document.identity")}
                              identityDocument={true}
                              default_value={documents[DOCUMENT_CATEGORY.IDENTITY].default_value}
                              bind:value={values[DOCUMENT_CATEGORY.IDENTITY]}
                              bind:valid={documents[DOCUMENT_CATEGORY.IDENTITY].valid}
                              bind:uploadComplete={documents[DOCUMENT_CATEGORY.IDENTITY].uploaded}
                              format={FORMAT.AUTO}>
                <FormRow indent={false} decorated={false}>
                    <div class="col-md-6 col-xs-6">
                        <FormItem label={$_('form.document.type')} visible={true} type={"label"}>
                            <Label value={identityDocumentMap[documents[DOCUMENT_CATEGORY.IDENTITY].document.type.code]}/>
                        </FormItem>
                    </div>
                    <div class="col-md-6 col-xs-6">
                        <FormItem label={$_('form.document.number')} visible={true} type={"label"}>
                            <Label value={documents[DOCUMENT_CATEGORY.IDENTITY].document.number}/>
                        </FormItem>
                    </div>
                </FormRow>
            </ScanDocumentForm>
            <!-- CUSTOMER TAXCODE DOCUMENT -->
            <ScanDocumentForm document={documents[DOCUMENT_CATEGORY.TAXCODE].document}
                              form={documents[DOCUMENT_CATEGORY.TAXCODE].form}
                              formPdf={documents[DOCUMENT_CATEGORY.TAXCODE].formPdf}
                              label={$_("form.document.taxCode")}
                              identityDocument={true}
                              default_value={documents[DOCUMENT_CATEGORY.TAXCODE].default_value}
                              bind:value={values[DOCUMENT_CATEGORY.TAXCODE]}
                              bind:valid={documents[DOCUMENT_CATEGORY.TAXCODE].valid}
                              bind:uploadComplete={documents[DOCUMENT_CATEGORY.TAXCODE].uploaded}
                              format={FORMAT.AUTO}/>
            <!-- CUSTOMER VAT REGISTRATION DOCUMENT -->
            {#if documents[DOCUMENT_CATEGORY.VAT_REGISTRATION]}
                <ScanDocumentForm document={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].document}
                                  form={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].form}
                                  formPdf={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].formPdf}
                                  label={$_("form.document.vatRegistration")}
                                  example={false}
                                  default_value={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].default_value}
                                  bind:value={values[DOCUMENT_CATEGORY.VAT_REGISTRATION]}
                                  bind:valid={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].valid}
                                  bind:uploadComplete={documents[DOCUMENT_CATEGORY.VAT_REGISTRATION].uploaded}
                                  format={FORMAT.IMAGE}/>
            {/if}
            <!-- CUSTOMER REGISTER DOCUMENT -->
            {#if documents[DOCUMENT_CATEGORY.REGISTER]}
                <ScanDocumentForm document={documents[DOCUMENT_CATEGORY.REGISTER].document}
                                  form={documents[DOCUMENT_CATEGORY.REGISTER].form}
                                  formPdf={documents[DOCUMENT_CATEGORY.REGISTER].formPdf}
                                  label={$_("form.document.register")}
                                  example={false}
                                  default_value={documents[DOCUMENT_CATEGORY.REGISTER].default_value}
                                  bind:value={values[DOCUMENT_CATEGORY.REGISTER]}
                                  bind:valid={documents[DOCUMENT_CATEGORY.REGISTER].valid}
                                  bind:uploadComplete={documents[DOCUMENT_CATEGORY.REGISTER].uploaded}
                                  format={FORMAT.IMAGE}/>
            {/if}
        </form>
    </div>
{/if}


<style>
    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

</style>
 
