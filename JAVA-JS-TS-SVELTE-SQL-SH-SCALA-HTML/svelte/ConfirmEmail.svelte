<script>
  import * as validators from '../../../libs/validators'
  import FormGroup from '../../../UiKitLite/forms/FormGroup.svelte'
  import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
  import TextInput from '../../../UiKitLite/forms/TextInput.svelte'
  import * as yup from 'yup'
  import { svelteYupForm } from '../../../libs/svelte-yup-form/svelte-yup-form'
  import { createEventDispatcher } from 'svelte'

  const dispatch = createEventDispatcher()

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
    initialValues: {},
    schema: yup.object().shape({
      email: validators.yupGenericRequiredEmail(),
      emailConfirm: validators.yupMatchEmail(true, 'email'),
    }),
  })

  function submitEvent() {
    dispatch('complete')
  }

  function validateEmail() {
    validateField('email')
    if ($touched['email']) {
      validateField('emailConfirm')
    }
  }

  function handleEmailChange() {
    validateEmail()
    doDispatch()
  }

  function doDispatch() {
    if ($isValid) {
      dispatch('value', {
        email: $values.email,
      })
    }
  }
</script>

<style>
  .title-card {
    font-size: 34px;
  }
  fieldset {
    margin-bottom: 30px;
    padding: 0px 0px 30px;
  }
  fieldset:last-child {
    padding-bottom: 0px;
  }
</style>

<div class="box-editable-area ">
  <p class="clearfix">
    Conferma la tua email o inserisci un nuovo indirizzo sul quale ricevere la
    documentazione e le comunicazioni operative relative alla tua richiesta di
    cessione del credito
  </p>
  <div class="row">
    <div class="col-sm-12">
      <div class="box-advice box-summary">
        <div class="box-body">
          <div class="row">
            <div class="col-md-6">
              <FormGroup status={$errors.email ? 'error' : null}>
                <FormLabel
                  inputDescription="email"
                  inputName="ce-email"
                  status={$errors.email ? 'error' : null}
                  statusMessage={$errors.email} />
                <TextInput
                  id="ce-email"
                  name="email"
                  placeholder="Inserisci"
                  maxlength={validators.MAX_EMAIL_LENGTH}
                  on:change={handleEmailChange}
                  on:blur={handleEmailChange}
                  bind:value={$values.email} />
              </FormGroup>
            </div>
            <div class="col-md-6">
              <FormGroup status={$errors.emailConfirm ? 'error' : null}>
                <FormLabel
                  inputDescription="Conferma email"
                  inputName="ce-emailConfirm"
                  status={$errors.emailConfirm ? 'error' : null}
                  statusMessage={$errors.emailConfirm} />
                <TextInput
                  id="dc-confirm-email"
                  name="emailConfirm"
                  placeholder="Inserisci"
                  maxlength={validators.MAX_EMAIL_LENGTH}
                  on:change={handleEmailChange}
                  on:blur={handleEmailChange}
                  bind:value={$values.emailConfirm} />
              </FormGroup>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row spacer-xs-top-30">
      <div class="col-sm-12">
        <p
          class="btn-container btn-container-left spacer-xs-bottom-15 clearfix">
          <button
            type="button"
            class="btn btn-primary pull-right"
            on:click={submitEvent}
            disabled={!$isValid}>
            PROSEGUI
          </button>
        </p>
      </div>
    </div>
  </div>
</div>
