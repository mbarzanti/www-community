<script>
    import {afterUpdate, tick} from 'svelte';
    import * as validators from '../../../libs/validators'
    import FormGroup from '../../../UiKitLite/forms/FormGroup.svelte'
    import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
    import TextInput from '../../../UiKitLite/forms/TextInput.svelte'
    import * as yup from 'yup'
    import {svelteYupForm} from '../../../libs/svelte-yup-form/svelte-yup-form'
    import { newMail, showLoadingSpinner } from '../../../stores'

    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

    export let id;
    export let title;
    export let showModal;
    export let pratica;

    showLoadingSpinner.set(false)

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
            email: ''
        },
        schema: yup.object().shape({
            email: validators.yupGenericRequiredEmail(),
            emailConfirm: validators.yupMatchEmail(true, 'email'),
        }),
    })

    let errorMessage = "" ;

    afterUpdate(async () => {
        if(showModal) {
            window.$('#' + id).modal('show');
        } else {
            window.$('#' + id).modal('hide');
            await tick()
            dispatch('close');
        }
    });

    async function closeModal() {
        showModal = false;
        await tick()
        dispatch('close');
    }

    function validateEmail() {
        validateField('email')
        if ($touched['email']) {
            validateField('emailConfirm')
        }
    }

    function handleEmailChange() {
        validateEmail()
    }

    function sendMail(){
        newMail.set($values.email);
        let parameters={
            praticaId: pratica.id,
            canale: pratica.canale,
        }
        closeModal()
        dispatch('modifymail', parameters)
    }
</script>

<div data-backdrop="static" class="modal fade" {id} tabindex="-1" role="dialog" aria-labelledby={id}>
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">{title}</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="box-advice box-summary">
                            <div class="box-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <FormGroup status={$errors.email ? 'error' : null}>
                                            <FormLabel
                                                    inputDescription="NUOVA EMAIL"
                                                    inputName="ce-email"
                                                    status={$errors.email ? 'error' : null}
                                                    statusMessage={$errors.email}/>
                                            <TextInput
                                                    id="ce-email"
                                                    name="email"
                                                    placeholder="Inserisci"
                                                    maxlength={validators.MAX_EMAIL_LENGTH}
                                                    on:change={handleEmailChange}
                                                    on:blur={handleEmailChange}
                                                    on:keyup={handleEmailChange}
                                                    bind:value={$values.email}/>
                                        </FormGroup>
                                    </div>
                                    <div class="col-md-6">
                                        <FormGroup status={$errors.emailConfirm ? 'error' : null}>
                                            <FormLabel
                                                    inputDescription="CONFERMA NUOVA EMAIL"
                                                    inputName="ce-emailConfirm"
                                                    status={$errors.emailConfirm ? 'error' : null}
                                                    statusMessage={$errors.emailConfirm}/>
                                            <TextInput
                                                    id="dc-confirm-email"
                                                    name="emailConfirm"
                                                    placeholder="Inserisci"
                                                    maxlength={validators.MAX_EMAIL_LENGTH}
                                                    on:change={handleEmailChange}
                                                    on:blur={handleEmailChange}
                                                    on:keyup={handleEmailChange}
                                                    bind:value={$values.emailConfirm}/>
                                        </FormGroup>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" disabled={!$isValid} on:click={sendMail}>Salva</button>
                <button class="btn" on:click={closeModal}>Chiudi</button>
            </div>
        </div>
    </div>
</div>