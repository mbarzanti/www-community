<script>
    import CheckboxWrapper from "../../../UiKitLite/forms/CheckboxWrapper.svelte";
    import FormGroup from "../../../UiKitLite/forms/FormGroup.svelte";
    import FormLabel from "../../../UiKitLite/forms/FormLabel.svelte";
    import TextInput from "../../../UiKitLite/forms/TextInput.svelte";
    import * as validators from '../../../libs/validators'
    import {createEventDispatcher} from 'svelte'
    import * as api from "../../../api";
    import {
        companyData,
        conventionType,
        conventionalCodeSelected,
        aliasSelected,
        showLoadingSpinner, listino,
    } from "../../../stores";
    import {onMount} from 'svelte';
    import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";

    const dispatch = createEventDispatcher()

    // C = convenzionato
    // D = dipendente
    // P = pensionato
    // NC = non convenzionato

    let tipoUtente = 'NC';
    let possessoreCodiceConvenzione = false;
    let codiceConvenzione = "";
    let alias = "";
    let erroreCodice = false;
    let erroreAlias = false;
    let codiceMessage = "";
    let aliasMessage = "";
    let eightAlphanumericCharacters = "^[a-zA-Z0-9]{1,8}$";
    let sixAlphanumericCharacters = "^[a-zA-Z0-9]{6,6}$";
    let showAlias = false;
    let prosegui = true;
    let aliasDaAbilitare = false;
    let showErrorBox = false;
    let errorMessage = "";

    onMount(() => {
        if ($conventionType === "PREMIUM") {
            tipoUtente = "C";
        } else if ($conventionType === "DIPENDENTE") {
            tipoUtente = "D";
        } else if ($conventionType === "PENSIONATO") {
            tipoUtente = "P";
        } else {
            tipoUtente = "NC"
        }
    });

    function submitEvent() {
        if (aliasDaAbilitare) {
            aliasDaAbilitare = false;
            showAlias = true;
            prosegui = true;
            alias = "";
            erroreAlias = false;
            aliasMessage = "";
        } else {
            if (alias || codiceConvenzione) {
                getBonus();
            } else {
                dispatch('complete')
            }
        }
    }

    function possessoreChange() {
        alias = "";
        erroreAlias = false;
        codiceConvenzione = "";
        erroreCodice = false;
        codiceMessage = "";
        aliasMessage = "";
        aliasDaAbilitare = false;
        showAlias = false;
        if (possessoreCodiceConvenzione) {
            prosegui = false;
        } else {
            prosegui = true;
            getBonus();
        }
    }

    function handleCodiceChange() {
        if (!codiceConvenzione.match(sixAlphanumericCharacters)) {
            erroreCodice = true;
            codiceMessage = "Inserire codice a 6 cifre"
            prosegui = false;
            showAlias = false;
            aliasDaAbilitare = false;
        } else {
            erroreCodice = false;
            aliasDaAbilitare = false;
            api.getExistAlias(codiceConvenzione).then((result) => {
                // RESULT E' FALSO = LA CONVENZIONE NON ESITE
                if (result.data.result.result === false) {
                    erroreCodice = true;
                    codiceMessage = "Convenzione non valida"
                    prosegui = false;
                    showAlias = false;
                    aliasDaAbilitare = false;
                } else {
                    // RESULT E' TRUE = LA CONVENZIONE ESITE
                    // ERROR MESSAGE E 001 = SERVE L'ALIAS
                    if (result.data.result.errorMessage === "001") {
                        codiceMessage = "Codice accettato"
                        prosegui = true;
                        aliasDaAbilitare = true;
                    }
                    // ERROR MESSAGE E 002 = NON C'E' ALIAS PROCEDI CON LA GET ECOBONUS
                    else if (result.data.result.errorMessage === "002") {
                        prosegui = true;
                        codiceMessage = "Codice accettato"
                    }
                }
            }).catch((err) => {
                errorMessage = "Errore durante il controllo di esistenza dell'alias"
                showErrorBox = true

            })
        }
    }

    function handleAliasChange() {
        if (alias.match(eightAlphanumericCharacters)) {
            erroreAlias = false;
            aliasMessage = "Codice accettato"
            prosegui = true;
        } else {
            erroreAlias = true;
            aliasMessage = "Inserire codice di massimo 8 cifre"
            prosegui = false;
        }
    }

    function getBonus(){
        showLoadingSpinner.set(true)
        api.getCreditsData($companyData.legalRepresentativeTaxCode, $companyData.codiceNdg, codiceConvenzione, alias)
            .then((response) => {
                let erroreResponse = response.data.result.errore;
                if (erroreResponse && erroreResponse.codice !== "005") {
                    erroreCodice = true;
                    prosegui = false;
                    showAlias = false;
                    aliasDaAbilitare = false;
                    codiceMessage = erroreResponse.descrizione;
                } else if (erroreResponse && erroreResponse.codice === "005") {
                    errorMessage = "Non è possibile verificare il codice convenzione, riprova oppure prosegui senza convenzione";
                    showErrorBox = true
                } else {
                    conventionalCodeSelected.set(codiceConvenzione);
                    aliasSelected.set(alias);
                    if(codiceConvenzione !== "") {
                        dispatch('complete')
                    }
                }
                showLoadingSpinner.set(false)
            })
            .catch((err) => {
                showLoadingSpinner.set(false)
                errorMessage = "Errore durante l'inserimento dei campi"
                showErrorBox = true
            });
    }
</script>

<style>
    .alignImg {
        display: flex;
        align-items: center;
        margin-top: 10px;
    }
</style>

<div class="box-editable-area ">

    {#if tipoUtente === "C" &&  $listino.toLowerCase() === "premium"}
        <p class="clearfix">

            Ti confermiamo che ti verranno applicate le condizioni previste dalla convenzione attivata.
        </p>
    {:else if ((tipoUtente === "D"  &&  $listino.toLowerCase() === "dipendente") || (tipoUtente === "P"  &&  $listino.toLowerCase() === "pensionato" ))}
        <p class="clearfix">
            Ti confermiamo che ti verranno applicate le condizioni previste dalla convenzione attivata.
        </p>
    {:else}
        <div class="row">
            <div class="col-xs-12">
                <CheckboxWrapper>
                    <label>
                        <input

                                type="checkbox"
                                name="contract-checkbox"
                                bind:checked={possessoreCodiceConvenzione}
                                on:change={possessoreChange}
                        />
                        Sì, ho un codice convenzione
                    </label>
                </CheckboxWrapper>
            </div>
        </div>
        {#if possessoreCodiceConvenzione}
            <div class="row spacer-xs-top-30">
                <div class="col-sm-12">
                    <div class="box-advice box-summary">
                        <div class="box-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <FormGroup>
                                        <FormLabel
                                                inputDescription="codice convenzione"
                                                inputName="ce-codice-convenzione"/>
                                        <div class="input-group text-right">
                                            <input
                                                    type="text"
                                                    class="form-control credit-block-input"
                                                    id="ce-codice-convenzione"
                                                    name="codice convenzione"
                                                    placeholder="Inserisci"
                                                    maxlength={validators.MAX_EMAIL_LENGTH}
                                                    on:blur={handleCodiceChange}
                                                    bind:value={codiceConvenzione}/>
                                            <div class="input-group-addon credit-block-input">
                                                {#if codiceMessage && erroreCodice}
                                                    <img alt="check"
                                                         src="/risorse_dt/condivise/immagini/icone/ico-messages-warning.png"
                                                         srcset="/risorse_dt/condivise/immagini/icone/ico-messages-warning@2x.png 2x"
                                                         width="20"/>
                                                {:else if codiceMessage && !erroreCodice}
                                                    <img alt="check"
                                                         src="/risorse_dt/condivise/immagini/icone/ico-messages-success.png"
                                                         srcset="/risorse_dt/condivise/immagini/icone/ico-messages-success@2x.png 2x"
                                                         width="20"/>
                                                {/if}
                                            </div>
                                        </div>

                                        {#if codiceMessage && erroreCodice}
                                            <p class="alignImg">
                                                <img alt="check"
                                                     src="/risorse_dt/condivise/immagini/icone/ico-messages-warning.png"
                                                     srcset="/risorse_dt/condivise/immagini/icone/ico-messages-warning@2x.png 2x"
                                                     width="20" style="margin-right: 5px;"/>
                                                {codiceMessage}
                                            </p>
                                        {:else if codiceMessage && !erroreCodice}
                                            <p class="alignImg">
                                                <img alt="check"
                                                     src="/risorse_dt/condivise/immagini/icone/ico-messages-success.png"
                                                     srcset="/risorse_dt/condivise/immagini/icone/ico-messages-success@2x.png 2x"
                                                     width="20" style="margin-right: 5px;"/>
                                                {codiceMessage}
                                            </p>
                                        {/if}
                                    </FormGroup>
                                </div>

                                {#if showAlias}
                                    <div class="col-md-6">
                                        <FormGroup>
                                            <FormLabel
                                                    inputDescription="alias"
                                                    inputName="ce-alias"/>

                                            <div class="input-group text-right">
                                                <input
                                                        type="text"
                                                        class="form-control credit-block-input"
                                                        id="dc-alias"
                                                        name="alias"
                                                        placeholder="Inserisci"
                                                        maxlength={validators.MAX_EMAIL_LENGTH}
                                                        on:change={handleAliasChange}
                                                        on:blur={handleAliasChange}
                                                        bind:value={alias}/>
                                                <div class="input-group-addon credit-block-input">
                                                    {#if aliasMessage && erroreAlias && alias}
                                                        <img alt="check"
                                                             src="/risorse_dt/condivise/immagini/icone/ico-messages-warning.png"
                                                             srcset="/risorse_dt/condivise/immagini/icone/ico-messages-warning@2x.png 2x"
                                                             width="20"/>
                                                    {:else if aliasMessage && !erroreAlias && alias}
                                                        <img alt="check"
                                                             src="/risorse_dt/condivise/immagini/icone/ico-messages-success.png"
                                                             srcset="/risorse_dt/condivise/immagini/icone/ico-messages-success@2x.png 2x"
                                                             width="20"/>
                                                    {/if}
                                                </div>
                                            </div>

                                            {#if aliasMessage && erroreAlias && alias}
                                                <p class="alignImg">
                                                    <img alt="check"
                                                         src="/risorse_dt/condivise/immagini/icone/ico-messages-warning.png"
                                                         srcset="/risorse_dt/condivise/immagini/icone/ico-messages-warning@2x.png 2x"
                                                         width="20" style="margin-right: 5px;"/>
                                                    {aliasMessage}
                                                </p>
                                            {:else if aliasMessage && !erroreAlias && alias}
                                                <p class="alignImg">
                                                    <img alt="check"
                                                         src="/risorse_dt/condivise/immagini/icone/ico-messages-success.png"
                                                         srcset="/risorse_dt/condivise/immagini/icone/ico-messages-success@2x.png 2x"
                                                         width="20" style="margin-right: 5px;"/>
                                                    {aliasMessage}
                                                </p>
                                            {/if}
                                        </FormGroup>
                                    </div>
                                {/if}

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        {/if}
    {/if}
    <div class="row spacer-xs-top-30">
        <div class="col-sm-12">
            <p
                    class="btn-container btn-container-left spacer-xs-bottom-15 clearfix">
                <button
                        type="button"
                        class="btn btn-primary pull-right"
                        on:click={submitEvent}
                        disabled={!prosegui}>
                    PROSEGUI
                </button>
            </p>
        </div>
    </div>
</div>

{#if showErrorBox}
    <AlertModal
            id="submit-error"
            title="Errore"
            showModal="true"
            on:close={() => {
        showErrorBox = false
      }}>
        <p>{errorMessage}</p>
    </AlertModal>
{/if}