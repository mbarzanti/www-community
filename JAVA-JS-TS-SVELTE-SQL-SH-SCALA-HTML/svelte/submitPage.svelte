<script>

  import AutoFormPageWizard from "../../../UiKit/forms/AutoFormPageWizard.svelte";
  import SidebarStaticComponent from "../components/StaticSidebar.svelte";
  import FormIntroTaskDescription from "../components/FormIntroTaskDescription.svelte";
  import FormBaseTaskDescription from "../components/FormBaseTaskDescription.svelte";
  import {ENDPOINTS} from './../../../endpoints';

  import * as reducedData from "./default/reducedData";
  import * as constants from "./../../../commons/constants";

  export let context;
  export let submit;
  export let submitted = false;
  export let readOnly = false;

  var wizard_values_map = {};
  var wizard_validation_map = {};
  var wizard_visibility_map = {};

  const BACK_ADDRESS = ENDPOINTS.REF_INDEX;

  const FLOW_RESTRICTED = false;


  if(readOnly){
    setTimeout(
      () => {
        wizard_values_map = context.form
      },
      1100
    );
  } else if( context.dossier.cliente ){
    setTimeout(
      () => {
        // ONLY DIGITAL
        wizard_values_map.data.base.taxCode = context.channelContext.taxCode;
        wizard_values_map.data.base.firstName = context.channelContext.firstName;
        wizard_values_map.data.base.lastName = context.channelContext.lastName;
        // ONLY DIGITAL

        //context.form.data.base.countryCode = dossier.cliente.prefissoInternazionale;
        wizard_values_map.data.base.areaCode = context.dossier.cliente.prefissoTelefonico;
        wizard_values_map.data.base.number = context.dossier.cliente.numeroTelefonico;
        wizard_values_map.data.base.email = context.dossier.cliente.email;

        wizard_values_map.data.base.tutelato = context.dossier.cliente.tutelato;
        wizard_values_map.data.base.amministrato = context.dossier.cliente.amministrato;
      },
      1100
    );
  } else {
    setTimeout(
        () => {
          Object.keys(context.channelContext).forEach(
                  (item)=>{
                    wizard_values_map["data"]["base"][item] = context.channelContext[item];
                  }
          )
        },
        1100
    );
  }

  function checkPrePopulated(){

    return context.dossier.cliente &&
            context.dossier.cliente.ibans &&
            context.dossier.cliente.ibans.length > 0 &&
            Object.keys(context.dossier.cliente.ibans[0]).length>0;
  }

  if( !readOnly && !checkPrePopulated() ){
    context.notPrepopulated = true;
  }


  const submitPath = (context) => {
    let path = [];

    path.push({
      name: "data",
      title: "Richiedente",
      subtitle: "",
      tasks: [
        {
          name: "intro",
          title: "Inizio",
          subtitle: "Prima di iniziare",
          form: null
        },
        {
          name: "base",
          title: "Dati anagrafici",
          subtitle: "Dati Anagrafici del Richiedente",
          form: reducedData.formDescriptor.data(context, readOnly)
        },
        {
          name: "documents",
          title: "Documenti",
          subtitle: "Documenti del Richiedente",
          form: reducedData.formDescriptor.documents(context, readOnly)
        },
        {
          name: "attachments",
          title: "Allegati",
          subtitle: "Allegati della Richiesta",
          form: reducedData.formDescriptor.attachments(context, readOnly)
        }
      ]
    });

    return path;
  };

  function submitHandler(e) {
    submit(getValidJSON(), e.detail.fail)
  }

  function handleComplete() {
    console.log("complete")
  }

  function printMap() {
    console.log(getValidJSON(true));
    console.log(JSON.stringify(wizard_values_map, null, 2));
    console.log(JSON.stringify(wizard_validation_map, null, 2));
    console.log(JSON.stringify(wizard_visibility_map, null, 2));
  }

  function getValidJSON(debug = false) {
    let validJSON = {};
    for (let stepKey in wizard_values_map) {
      if (stepKey.localeCompare("backup") === 0) {
        continue;
      }
      validJSON[stepKey] = {};
      for (let taskKey in wizard_values_map[stepKey]) {
        validJSON[stepKey][taskKey] = {};
        for (let formKey in wizard_values_map[stepKey][taskKey]) {
          if (
            wizard_validation_map[stepKey][taskKey][formKey] &&
            wizard_visibility_map[stepKey][taskKey][formKey]
          ) {
            validJSON[stepKey][taskKey][formKey] =
              wizard_values_map[stepKey][taskKey][formKey];
          }
        }
      }
    }
    if (wizard_values_map["backup"]) {
      validJSON["backup"] = wizard_values_map["backup"];
    }
    if (debug) {
      return JSON.stringify(validJSON, null, 2);
    } else {
      return JSON.stringify(validJSON);
    }
  }
</script>

<style>

</style>

<AutoFormPageWizard
  title={'Richiesta Anticipazione Cassa Integrazione Guadagni'}
  stepTitleComponents={{"intro": FormIntroTaskDescription,  "base": FormBaseTaskDescription}}
  backAddress={BACK_ADDRESS}
  fakeFinalStepTitle={'Fine'}
  flowRestricted={FLOW_RESTRICTED}
  enableEditCtxBtn={false}
  path={submitPath(context)}
  bind:wizard_values_map
  bind:wizard_validation_map
  bind:wizard_visibility_map
  bind:submitted
  on:submit={submitHandler}
  on:complete={handleComplete}
  sidebarComponent={SidebarStaticComponent}
/>
