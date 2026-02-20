<script>
  import * as validators from '../../../libs/validators'
  import FormGroup from '../../../UiKitLite/forms/FormGroup.svelte'
  import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
  import TextInput from '../../../UiKitLite/forms/TextInput.svelte'
  import CheckboxWrapper from '../../../UiKitLite/forms/CheckboxWrapper.svelte'
  import * as yup from 'yup'
  import { svelteYupForm } from '../../../libs/svelte-yup-form/svelte-yup-form'
  import { createEventDispatcher } from 'svelte'

  export let businessName = '-'
  export let businessTaxCode = '-'
  export let businessType = '-'
  export let businessVatCode = '-'
  export let legalRepresentativeName = '-'
  export let legalRepresentativeSurname = '-'
  export let businessAddressOffice = '-'
  export let businessCityOffice = '-'
  export let phoneNumber = ''
  export let legalRepresentativeCityBirth = '-'
  export let legalRepresentativeDateBirth = '-'
  export let legalRepresentativeTaxCode = '-'
  export let transitionDone = false
  let dispatch = createEventDispatcher()

  let isDataCorrectCheck = false

  $: if (isDataCorrectCheck !== null) {
    doDispatch()
  }

  const {
    values,
    errors,
    touched,
    handleInputChange,
    validateField,
    handleSubmit,
    isValid,
  } = svelteYupForm({
    showFirstErrorOnly: true,
    initialValues: {
      phoneNumber: '+39',
      phoneNumberConfirm: '+39',
    },
    schema: yup.object().shape({
      phoneNumber: validators.yupPhoneNumberValidatorRequired(),
      phoneNumberConfirm: validators.yupMustMatch('phoneNumber'),
    }),
  })

  function validateCellphone() {
    validateField('phoneNumber')
    if ($touched['phoneNumber']) {
      validateField('phoneNumberConfirm')
    }
  }

  function handlePhoneNumberChange() {
    validateCellphone()
    doDispatch()
  }

  function doDispatch() {
    const dataToDispatch = {
      phoneNumber: $isValid ? $values.phoneNumber : null,
      dataChecked: isDataCorrectCheck,
    }
    dispatch('value', dataToDispatch)
  }

  function submitEvent() {
    dispatch('complete')
  }
</script>

<style>
  fieldset {
    margin-bottom: 30px;
    padding: 0px 0px 30px;
  }
  fieldset:last-child {
    padding-bottom: 0px;
  }
</style>

<div class="box-editable-area">
  <fieldset>
    <div class="row">
      <div class="col-xs-12">
        <div class="box-editable-area">
          <p>
            Verifica i tuoi dati anagrafici. Nel caso in cui non fossero
            aggiornati, ti invitiamo a recarti presso il tuo Ufficio Postale di
            riferimento.
          </p>
        </div>
      </div>
      <div class="col-xs-12">
        <div class="box-advice box-summary">
          <div class="box-body">
            <div class="row">
              <div class="col-sm-4 col-xs-12">
                <div class="form-group clearfix">
                  <label class=" control-label">Nome</label>
                  <div class="">
                    <p class="form-control-static">{legalRepresentativeName}</p>
                  </div>
                </div>
              </div>
              <div class="col-sm-4 col-xs-12">
                <div class="form-group clearfix">
                  <label class=" control-label">Cognome</label>
                  <div class="">
                    <p class="form-control-static">
                      {legalRepresentativeSurname}
                    </p>
                  </div>
                </div>
              </div>
              <div class="col-sm-4 col-xs-12">
                <div class="form-group clearfix">
                  <label class=" control-label">Codice Fiscale</label>
                  <div class="">
                    <p class="form-control-static">
                      {legalRepresentativeTaxCode}
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">

              <div class="col-sm-4 col-xs-12">
                <div class="form-group clearfix">
                  <label class=" control-label">Luogo di nascita</label>
                  <div class="">
                    <p class="form-control-static">
                      {legalRepresentativeCityBirth}
                    </p>
                  </div>
                </div>

              </div>
              <div class="col-sm-4 col-xs-12">
                <div class="form-group clearfix">
                  <label class=" control-label">Data di nascita</label>
                  <div class="">
                    <p class="form-control-static">
                      {legalRepresentativeDateBirth}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-12">
            <CheckboxWrapper>
              <label>
                <input
                  
                  type="checkbox"
                  name="contract-checkbox"
                  disabled={transitionDone}
                  bind:checked={isDataCorrectCheck} />
                Confermo che tutti i dati presenti sono corretti
              </label>
            </CheckboxWrapper>
          </div>
        </div>
        <div class="row spacer-xs-top-30">
          <div class="col-sm-12">
            <p
              class="btn-container btn-container-left spacer-xs-bottom-15
              clearfix">
              <button
                type="button"
                class="btn btn-primary pull-right"
                on:click={submitEvent}
                disabled={!isDataCorrectCheck || transitionDone }>
                PROSEGUI
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  </fieldset>
</div>
