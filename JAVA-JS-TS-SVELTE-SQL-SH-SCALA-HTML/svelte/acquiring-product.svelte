<script>
  import { onMount, tick } from "svelte";
  import { _ } from "svelte-i18n";

  import Loader from "../../../suit/uikit/up/elements/Loader.svelte";
  import TableAccordion from "../../../suit/uikit/up/form/tableAccordion/TableAccordion.svelte";
  import Modal from "../../../suit/uikit/up/elements/Modal.svelte"
  import SaveErrorsModal from "../../../suit/uikit/up/elements/modals/SaveErrorsModal.svelte"
  import NormalizerModal from "../../../suit/uikit/up/elements/modals/NormalizerModal.svelte"
  import { StepBuilderOrchestrator } from "../../../suit/uikit/stepbuilder/StepBuilderOrchestrator";

  import api from "../../api/api";
  import * as BaseGenerator from "./acquiring-product-sections/base-generator";
  import * as MultiFormGenerator from "./acquiring-product-sections/multi-form-generator";
  import * as RecapGenerator from "./acquiring-product-sections/recap-generator";
  import globalFunctions from "../../commons/globalFunctions";
  import extraFunctions from "../../commons/extraFunctions";
  import recapDataReady from './stores/recap-data-ready'
  
  export let nextStateEnabled = false;

  let dataLoaded = false;
  let error;
  let products = [];
  let formModel = [];
  let accordionInformations = {};
  let valuesMap = {};
  let visibilityMap = {};
  let validationMap = {};
  let formObservers = {};
  let productJson = {};
  let completedMap = {};
  let extraDataMap = {};
  let relatedFields = {};
  let saveErrors = [];
  let saveErrorsKind;

  $: nextStateEnabled = Object.values(completedMap).filter(el => !el).length === 0;

  function parseData(data) {
    let allFieldsMap = {}; // map {fieldId: fieldDefaultValue} for check visibility at start
    const dataParsed = [];

    // we assume that each subcontainer has only one element
    const subContainers = data.container.subContainers;
    relatedFields = data.container.prepopulateFields.values
    // manage fields
    for (const sC of subContainers) {
      const scCopy = { ...sC, sections: [] };
      // each sub container has an array of sections
      for (const section of sC.sections) {
        const sectionCopy = { ...section, subSections: [] };
        // each section has an array of subsections
        for (const subSection of section.subSections) {
          // populate all fields map
          allFieldsMap = subSection.fields.reduce((obj, field) => {
            obj[field.id] = field.default;
            return obj;
          }, allFieldsMap);
          // define new sub section object
          let subSectionCopy = {};
          // check if section is a multi form
          if (subSection.template) {
            // check if multi form with same id exists
            const multiFormSection = sectionCopy.subSections.find(
              (sb) => sb.id === subSection.templateCode
            );
            if (multiFormSection) {
              // add sub section to section fields
              multiFormSection.fields.push(subSection);
            } else {
              // create new section
              // into fields we push subsection object, without multiform property
              subSectionCopy = {
                id: subSection.templateCode,
                title: subSection.templateDescription,
                fields: [],
                template: true,
                unreplicable: subSection.unreplicable
              };
              subSectionCopy.fields.push(subSection);
              sectionCopy.subSections.push(subSectionCopy);
            }
          } else {
            subSectionCopy = { ...subSection };
            sectionCopy.subSections.push(subSectionCopy);
          }
        }
        scCopy.sections.push(sectionCopy);
      }
      dataParsed.push(scCopy);
    }

    return [dataParsed, allFieldsMap];
  }

  function initForm(productsList, allFieldsMap) {
    // for each product define relative form
    productsList.forEach((product, index) => {
      // init values map
      valuesMap[product.id] = {};
      // init visibility map
      visibilityMap[product.id] = {};
      // init validation map
      validationMap[product.id] = {};
      // init completed map
      completedMap[product.id] = {};
      // init extra data map
      extraDataMap[product.id] = {};
      // init form observers
      formObservers[product.id] = product.globalFunctions;
      // set accordion infos
      accordionInformations[product.id] = {
        title: `Prodotto ${index + 1} di ${productsList.length}`,
        subTitle: product.intestazione.short,
        asideTitle: "Tutti i servizi",
        externalStepper: false,
      };

      // define steps
      const confStep = StepBuilderOrchestrator.newStep(
        "conf_" + product.id,
        "Configurazione prodotto",
        undefined
      );
      const otherStep = StepBuilderOrchestrator.newStep(
        "other_" + product.id,
        "Altri dati",
        undefined
      );
      // define tasks for configuration step
      for (const section of product.sections) {
        const task = StepBuilderOrchestrator.newTask(
          section.id,
          section.title,
          section.description
        );

        // set extra function
        if (section.extraFunctions) {
          const selectedFunctions = section.extraFunctions.map(extraFunction => {
            return { invoke: extraFunctions[extraFunction.name], params: extraFunction.params }
          });
          selectedFunctions.forEach(selectedFunction => {
            selectedFunction.invoke(selectedFunction.params, task)
          })
        }

        // populate extra data map
        extraDataMap[product.id][section.id] = {};

        // define rows
        // each section has sub sections
        if (section.subSections) {
          for (const subSection of section.subSections) {
            // populate extra data map
            extraDataMap[product.id][section.id][subSection.id] = {};
            
            let rows;
            if (subSection.template) {
              // multi form case
              rows = MultiFormGenerator.gen(subSection, allFieldsMap, extraDataMap[product.id][section.id][subSection.id]);
            } else {
              // default case
              rows = BaseGenerator.gen(subSection, allFieldsMap, extraDataMap[product.id][section.id][subSection.id]);
            }
            // add row to task
            if (rows) {
              task.addRows(rows);
            }
          }
        }

        // add tasks to step
        confStep.addTask(task.build());
      }

      // define task for recap step
      const recapTask = StepBuilderOrchestrator.newTask("RECAP_" + product.id, "Riepilogo costi", "");
      // generate row
      const recapRow = RecapGenerator.gen(product.id);
      // add row to task
      if (recapRow) {
        recapTask.addRow(recapRow);
      }
      // add task to step
      otherStep.addTask(recapTask.build());

      // add step to form
      formModel.push({
        id: product.id,
        form: StepBuilderOrchestrator.stepBuilder()
          .addStep(confStep.build())
          .addStep(otherStep.build())
          .buildSteps(),
      });
    });
  }

  // call be to retrieve product information
  onMount(() => {
    api.acquiringProductConfiguration.configurations.getProductsConfiguration((resp) => {
      if (resp) {
        productJson = resp;
        let allFieldsMap;
        [products, allFieldsMap] = parseData(resp);
        // define the form object
        initForm(products, allFieldsMap);
        dataLoaded = true;
      }
    });
  });

  function updateForm(formModelCopy) {
    formModel.forEach((product) => {
      product.form.find((step) => step.name.startsWith('conf')).tasks.forEach((task) => {
        let productIndex = formModel.indexOf(product)
        let productTasks = formModelCopy[productIndex].form.find((step) => step.name.startsWith('conf')).tasks
        let taskIndex = product.form.find((step) => step.name.startsWith('conf')).tasks.indexOf(task)
        productTasks[taskIndex].form.observers = task.form.observers

        task.form.values.forEach((value) => {
          if (value.length) {
            value.forEach((field) => {
              if (hasStoreOptions(field)) {
                let taskValues = productTasks[taskIndex].form.values
                let valueIndex = task.form.values.indexOf(value)
                let newField = taskValues[valueIndex][value.indexOf(field)]
                
                newField.options = field.options
              }
            })
          } else {
            let field = value
            if (hasStoreOptions(field) && !field.name.includes('MULTI')) {
              let taskValues = productTasks[taskIndex].form.values
              let valueIndex = task.form.values.indexOf(value)
              let newField = taskValues[valueIndex][value.indexOf(field)]

              newField.options = field.options
            } else if (field.name.includes('MULTI') && field.templates) {
              field.templates.forEach((template) => {
                template.forEach((fieldsGroup) => {
                  fieldsGroup.forEach((groupField) => {
                    if (hasStoreOptions(groupField)) {
                      let taskValues = productTasks.find((taskValue) => taskValue.name === task.name).form.values

                      taskValues.filter((taskValue) => taskValue.name && taskValue.name.startsWith(value.name)).forEach((taskValue) => {
                        taskValue.values.forEach((templateInstance) => {
                          let templateInstanceRow = templateInstance.find((templateRow) => {
                            return templateRow.find((fieldsDescriptor) => {
                              return fieldsDescriptor.name && sameNameRoot(fieldsDescriptor, groupField)
                            })
                          })
                          let templateInstanceField = templateInstanceRow.find((fieldsDescriptor) => fieldsDescriptor.name && sameNameRoot(fieldsDescriptor, groupField))

                          templateInstanceField.options = groupField.options
                        })


                        let newTemplateDescriptorField = taskValue.templates.find((templateDescriptorContainer) => {
                          return templateDescriptorContainer.find((templateDescriptorFieldsGroup) => {
                            return templateDescriptorFieldsGroup.find((templateDescriptorField) => {
                              return templateDescriptorField.name && sameNameRoot(templateDescriptorField, groupField)
                            })
                          })
                        }).find((templateDescriptorFieldsGroup) => {
                          return templateDescriptorFieldsGroup.find((templateDescriptorField) => {
                            return templateDescriptorField.name && sameNameRoot(templateDescriptorField, groupField)
                          })
                        }).find((templateDescriptorField) => {
                          return templateDescriptorField.name && sameNameRoot(templateDescriptorField, groupField)
                        })

                        newTemplateDescriptorField.options = groupField.options
                      })
                    }
                  })
                })
              })
            }
          }
        })
      })
    })
    formModel = formModelCopy;
  }

  function handleChanges(productId, event, isInitOrDestroy) {
    let forceUpdate = false;
    const formModelCopy = JSON.parse(JSON.stringify(formModel))
    // check if there are observer registered
    if (formObservers[productId]) {
      // get form descriptor
      const descriptor = formModelCopy.find((f) => f.id === productId);
      // loop over observer and call them
      for (const observer of formObservers[productId]) {
        const globalFunction = globalFunctions[observer.name];
        if (globalFunction && (!isInitOrDestroy || (isInitOrDestroy && observer.runOnInitAndDestroy))) {
          forceUpdate = globalFunction(
            {
              event,
              productId: productId,
              formDescriptor: descriptor.form,
              formStructureJson: products.find((p) => p.id === productId),
              relatedFields: relatedFields,
              formMaps: { valuesMap: valuesMap[productId], allValuesMap: valuesMap, visibilityMap: visibilityMap[productId],
                validationMap: validationMap[productId], extraDataMap: extraDataMap[productId]}
            },
            observer.params
          ) || forceUpdate;
        }
      }
    }

    if (forceUpdate) {
      tick().then(() => {
        updateForm(formModelCopy);
      });
    }
  }

  function hasStoreOptions(field) {
    let storeNames = ["exportState", "handlerView", "contextMaster", "maxStore", "minStore", "customPostValidators", "initFunction", "typoType"]

    return field.options && !storeNames.map((storeName) => field.options[storeName]).every((store) => !store)
  }

  function sameNameRoot(field1, field2) {
    return field1.name.split('__')[0].split('-_')[0].split('-%%ID%%')[0] === field2.name.split('__')[0].split('-_')[0].split('-%%ID%%')[0]
  }

  function setValuesForTemplate(templateKey, valueMap, visualMap, field) {
    let fieldValues = null;
    // get value and visual maps
    const templateValueMap = valueMap[templateKey];
    const templateVisualMap = visualMap[templateKey];
    // first check if entire template is visible
    if (templateVisualMap.array) {
      // init field values array
      fieldValues = [];
      let nullValues = 0;
      // check if single field is visible
      // get the field id without dynamic part %%ID%%
      const fieldIdRoot = field.id.replace("%%ID%%", "");
      // each element in template value map is a copy of the template and in each copy there are the values of the all fields
      templateValueMap.forEach((templateValue, index) => {
        const fieldsId = Object.keys(templateValue);
        // find the id of the field that has the same id root of the current field
        const fieldId = fieldsId.find((f) => f.startsWith(fieldIdRoot));
        // check if we have found the field id and if it is visible
        if (fieldId && templateVisualMap.items[index][fieldId]) {
          fieldValues.push(templateValue[fieldId]);
        } else {
          nullValues++;
          fieldValues.push(null);
        }
      });

      // if fieldValues is an array with all null values, put it equals to null
      if (fieldValues.length === nullValues) {
        fieldValues = null;
      }
    }
    return fieldValues;
  }

  function onSubmit() {
    // check if all steps are completed
    if (nextStateEnabled) {
      // copy product json
      const productJsonCopy = JSON.parse(JSON.stringify(productJson));
      // loop over each product
      for (const product of productJsonCopy.container.subContainers) {
        // get map for single product. we need the map of the values and the map of visualization
        // data we need are in conf_(product.id) key
        const valueMap = valuesMap[product.id]["conf_" + product.id];
        const visualMap = visibilityMap[product.id]["conf_" + product.id];
        // loop over sections
        for (const section of product.sections) {
          // loop over subsections
          for (const subSection of section.subSections) {
            // loop over fields
            for (const field of subSection.fields) {
              // template case
              if (subSection.template) {
                if (subSection.templateReplicable) {
                  // replicas case
                  // for the same template we can have multiple replicas
                  // example: multi pos section can be replicated n-times for each sale point
                  const templateValueMapKey = Object.keys(
                    valueMap[section.id]
                  ).filter((k) => k.startsWith(subSection.templateCode));
                  // init field values array
                  field.fieldValues = [];
                  // loop over all replicas
                  for (const templateKey of templateValueMapKey) {
                    field.fieldValues.push(
                      setValuesForTemplate(
                        templateKey,
                        valueMap[section.id],
                        visualMap[section.id],
                        field
                      )
                    );
                  }
                  // if fieldValues is an array with all null values, put it equals to null
                  if (
                    field.fieldValues.length ===
                    field.fieldValues.filter((f) => !f).length
                  ) {
                    field.fieldValues = null;
                  }
                } else {
                  // default case
                  field.fieldValues = setValuesForTemplate(
                    subSection.templateCode,
                    valueMap[section.id],
                    visualMap[section.id],
                    field
                  );
                }
              } else {
                // default case
                // get value from value map if the field is visible
                field.fieldValue = visualMap[section.id][field.id]
                  ? valueMap[section.id][field.id]
                  : null;
              }
            }
          }
        }
      }

      // call be
      api.acquiringProductConfiguration.configurations.sendProductsConfiguration(productJsonCopy, saveUnsuccessfulCallback);
    }
  }

  function saveUnsuccessfulCallback(error) {
    saveErrors = error.response.data.errors
    saveErrorsKind = error.response.data.kind

    jQuery('#save-errors').modal('show')
  }

  function getRecapData(productId, event) {
    if (event.detail.step.startsWith('other_')) {
      const product = enrichTemporarily(productJson.container.subContainers.find(p => p.id === productId));
      if (product) {
        // call be
        api.acquiringProductConfiguration.configurations.getRecapData(product, data => {
          // get form descriptor
          const descriptor = formModel.find((f) => f.id === productId);
          globalFunctions.populateRecapTableFromBe(productId, descriptor.form, data);
          tick().then(() => {
            formModel = formModel;
          });
          recapDataReady.set(true)
        });
      }
    }
  }

  function enrichTemporarily(product) {
    // get map for single product. we need the map of the values and the map of visualization
    // data we need are in conf_(product.id) key
    const valueMap = { ...valuesMap[product.id]["conf_" + product.id] };
    const visualMap = { ...visibilityMap[product.id]["conf_" + product.id] };
    // loop over sections
    for (const section of product.sections) {
      // loop over subsections
      for (const subSection of section.subSections) {
        // loop over fields
        for (const field of subSection.fields) {
          // template case
          if (subSection.template) {
            if (subSection.templateReplicable) {
              // replicas case
              // for the same template we can have multiple replicas
              // example: multi pos section can be replicated n-times for each sale point
              const templateValueMapKey = Object.keys(
                valueMap[section.id]
              ).filter((k) => k.startsWith(subSection.templateCode));
              // init field values array
              field.fieldValues = [];
              // loop over all replicas
              for (const templateKey of templateValueMapKey) {
                field.fieldValues.push(
                  setValuesForTemplate(
                    templateKey,
                    valueMap[section.id],
                    visualMap[section.id],
                    field
                  )
                );
              }
              // if fieldValues is an array with all null values, put it equals to null
              if (
                field.fieldValues.length ===
                field.fieldValues.filter((f) => !f).length
              ) {
                field.fieldValues = null;
              }
            } else {
              // default case
              field.fieldValues = setValuesForTemplate(
                subSection.templateCode,
                valueMap[section.id],
                visualMap[section.id],
                field
              );
            }
          } else {
            // default case
            // get value from value map if the field is visible
            field.fieldValue = visualMap[section.id][field.id]
              ? valueMap[section.id][field.id]
              : null;
          }
        }
      }
    }
    console.log(product);

    return product;
  }
</script>

<style>
  #page-container {
    height: calc(100vh - 130px);
    padding: 20px;
  }

  #page-container .error-container {
    text-align: center;
  }

  #page-container .error-container .error {
    width: 33%;
    border: 1px solid red;
    border-radius: 5px;
    margin: 0 auto;
  }

  #page-container .error-container .error .header {
    background-color: #ff6565;
    color: white;
    padding: 10px;
  }

  #page-container .error-container .error .text {
    padding: 10px;
  }

  #page-container :global(.form-element-label.empty) {
    height: 22px !important;
  }
</style>

{#if !dataLoaded}
  <Loader />
{:else}
  <div id="page-container">
    {#if error}
      <div class="error-container">
        <div class="error">
          <div class="header">Error!</div>
          <div class="text">{error}</div>
        </div>
      </div>
    {:else}
      <NormalizerModal title="Normalizzazione indirizzi" bind:valuesMap="{valuesMap}" bind:extraDataMap="{extraDataMap}" bind:formModel={formModel} />
      <Modal title="Si sono verificati alcuni errori" customId="save-errors"><SaveErrorsModal bind:errors={saveErrors} bind:kind={saveErrorsKind} bind:productJson bind:valuesMap bind:visibilityMap></SaveErrorsModal></Modal>
      {#each formModel as model, index (model.id)}
        <div class="container">
          <div class="content-padd pt10">
            <TableAccordion
              id={'table-' + model.id}
              accordionInfo={accordionInformations[model.id]}
              path={model.form}
              collapsed="{index != 0}"
              bind:wizard_values_map={valuesMap[model.id]}
              wizard_visibility_map={visibilityMap[model.id]}
              wizard_validation_map={validationMap[model.id]}
              bind:completed={completedMap[model.id]}
              on:change={(event) => handleChanges(model.id, event)}
              on:init={(event) => handleChanges(model.id, event, true)}
              on:destroy={(event) => handleChanges(model.id, event, true)}
              on:stepChanged={(event) => getRecapData(model.id, event)}/>
          </div>
        </div>
      {/each}
      <div
        class="bottom-buttons fixed fixed-bottom"
        style="position:fixed;z-index: 999;">
        <a
          href={undefined}
          class="btn btn-yellow absolute-action fixed-bottom-button"
          on:click={onSubmit}
          class:disabled={!nextStateEnabled}>
          {$_('default.buttons.continue')}
        </a>
      </div>
    {/if}
  </div>
{/if}
