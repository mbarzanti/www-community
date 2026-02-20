<script>
    import * as yup from 'yup'
    import FirstTransferBlock from './FirstTransferBlock.svelte'
    import {onMount} from 'svelte'
    import {svelteYupForm} from '../../../libs/svelte-yup-form/svelte-yup-form'
    import * as validators from '../../../libs/validators'
    import SelectInput from '../../../UiKitLite/forms/SelectInput.svelte'
    import TextInput from '../../../UiKitLite/forms/TextInput.svelte'
    import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
    import FormGroup from '../../../UiKitLite/forms/FormGroup.svelte'
    import FiscalYearBlock from './FiscalYearBlock.svelte'
    import {formErrors, formLabels} from '../../../labels'
    import {createEventDispatcher} from 'svelte'
    import numeral from 'numeral'
    import {
        pickedBonusDataSet,
        pickedYearsPerBonusSet,
        flags,
        sismaBonusId,
        numberYears
    } from '../../../stores'
    import {
        mapCreditData,
        mapCreditDataFirstTransfer,
    } from '../utils/MappingUtils.js'
    import CheckboxWrapper from '../../../UiKitLite/forms/CheckboxWrapper.svelte'
    import AlertModal from '../../../UiKitLite/common/AlertModal.svelte'
    import Checkbox from '../../../UiKitLite/forms/Checkbox.svelte'
    import RadioButton from '../../../UiKitLite/forms/RadioButton.svelte'
    import RadioButtonWrapper from '../../../UiKitLite/forms/RadioButtonWrapper.svelte'
    import BoxAdviceInfo from '../../../UiKitLite/common/BoxAdviceInfo.svelte'

    const dispatch = createEventDispatcher()

    export let id
    export let removeEnabled = false
    export let realId;

    let bonusSelected = false

    let sBId = null

    // Parameters to manage AlertModal
    let showErrorBox = false
    let alertBoxMessage = ''

    // Boolean to be used to show/don't show first transfer checkbox
    let showFlag = false

    let showPosteAssicura = false
    let posteAssicuraCheck = false;

    // Variable to check if first transfer credit flag is checked
    let firstTransferCreditChecked = false
    let radioButtonSelected = null
    $: yearVisible = radioButtonSelected !== null
    let isNull = true

    //Data to configure the component
    export let bonusData = []

    export let number
    export let title

    let bonusNotSelected = true

    let creditsData = []

    // Variables to define values in select menu
    let bonus = []
    let years = []
    let mapBonusId = []

    let yearsFormData = null
    let additionalYearsFormData = []

    let previous = -1
    let firstYear

    const {
        values,
        errors,
        touched,
        touchedErrors,
        handleInputChange,
        validateField,
        handleSubmit,
        isValid,
    } = svelteYupForm({
        showFirstErrorOnly: true,
        initialValues: {
            bonusTypes: null,
            tributeCode: null,
        },
        schema: yup.object().shape({
            bonusTypes: validators.yupGenericRequiredObject(),
            tributeCode: validators.yupGenericRequiredString(),
        }),
    })

    // Method to handle the change in "first credit transfer" flag
    function firstCreditDisposal() {
        //delete inserted years for this credit
        if (!$errors.bonusType) {
            pickedYearsPerBonusSet.update((theArray) => {
                theArray = theArray.filter((item) => item.bonusId !== previous)
                return theArray
            })
            additionalYearsFormData.forEach(function (item) {
                if (!item.deleted) {
                    numberYears.update((n) => n + -1)
                }
            })
            additionalYearsFormData = []
            yearsFormData = null
            if (firstYear) firstYear.reset()
            dispatch('creditChanged', {
                id: number,
            })
        }
        doDispatch();
    }

    function validateBonusType(event) {
        //delete inserted years for this credit
        if (!$errors.bonusType) {
            pickedYearsPerBonusSet.update((theArray) => {
                theArray = theArray.filter((item) => item.bonusId !== previous)
                return theArray
            })
            additionalYearsFormData.forEach(function (item) {
                if (!item.deleted) {
                    numberYears.update((n) => n + -1)
                }
            })
            additionalYearsFormData = []
            yearsFormData = null
            bonusNotSelected = true
            if (firstYear) firstYear.reset()
            radioButtonSelected = null
            dispatch('creditChanged', {
                id: number,
            })
            // Update store to manage flag
            if (firstTransferCreditChecked) {
                firstTransferCreditChecked = false
            }
        }

        // Clear previous checkbox data

        years = []
        $errors.bonusTypes = null
        if ($values.bonusTypes === 'null') {
            // Remove flag from UI
            showFlag = false
            showPosteAssicura = false
            posteAssicuraCheck = false
            bonusNotSelected = true
            pickedBonusDataSet.update((theArray) => {
                theArray[id] = {...theArray[id], data: null}
                return theArray
            })
            dispatch('valueChanged', {
                id: number,
                value: null,
            })
            $values.bonusTypes = 'null'
            previous = -1
            isNull = true
            return
        } else {
            let selectionIndex = event.detail.value.id

            //check id this bonus is taken already
            let itemIndex = $pickedBonusDataSet.findIndex(
                (item) =>
                    item.id !== id &&
                    item.data !== null &&
                    item.data.id === event.detail.value.id,
            )

            pickedBonusDataSet.update((theArray) => {
                theArray[id] = {id: id, data: event.detail.value}
                return theArray
            })

            if (itemIndex !== -1) {
                bonusNotSelected = true
                dispatch('valueChanged', {
                    id: number,
                    value: null,
                })
                $errors.bonusTypes = 'Errore - Bonus già selezionato'
                $values.bonusTypes = 'null'
                previous = -1
                isNull = true
                return
            }

            previous = selectionIndex

            // Check if the flag has to be shown
            showFlag = event.detail.value.flag
            showPosteAssicura = event.detail.value.flagPostaAssicura

            if (!showFlag && $flags > 0) {
                $errors.bonusTypes = ''
                $values.bonusTypes = 'null'
                alertBoxMessage =
                    'Non è possibile inserire nella stessa richiesta crediti relativi a una prima cessione e crediti relativi a cessioni successive.'
                showErrorBox = true
                isNull = true;
                return
            }
            isNull = false
            const currentTaxCode = bonusData.bonus[event.detail.value.id].taxCode[0]
            if ('' + currentTaxCode === sBId) {
                dispatch('sismaAlert')
            }

            // Enable other checkboxes
            bonusNotSelected = false

            // Calculate data of other checkboxes
            //TRIBUTES
            let taxCodes = bonusData.bonus[selectionIndex].taxCode
            let capPrimaCessione = bonusData.bonus[selectionIndex].capPrimaCessione
            $values.tributeCode = taxCodes[0]
            $values.capPrimaCessione = capPrimaCessione

            // YEARS
            let date = bonusData.bonus[selectionIndex].date
            for (var j = 0; j < date.length; j++) {
                var object = {
                    id: j,
                    label: date[j],
                }
                years.push(object)
            }
        }
        doDispatch()
    }

    function doDispatch() {
        validateField('bonusTypes')
        validateField('tributeCode')
        let returnObject = null
        let yearsData = additionalYearsFormData.filter(
            (item) => !item.deleted && item.data && item.data.fiscalYear,
        )
        if ($isValid) {
            returnObject = mapCreditData(
                mapBonusId,
                $values,
                yearsFormData,
                yearsData,
            )
        }
        dispatch('valueChanged', {
            id: number,
            value: returnObject,
        })
    }

    function doDispatchRemove(year, noErrors) {
        validateField('bonusTypes')
        validateField('tributeCode')
        let returnObject = null
        let yearsData = additionalYearsFormData.filter(
            (item) => !item.deleted && item.data && item.data.fiscalYear,
        )
        if ($isValid) {
            returnObject = mapCreditData(
                mapBonusId,
                $values,
                yearsFormData,
                yearsData,
            )
        }
        if (noErrors) {
            dispatch('removeYear', {
                id: number,
                year: year,
                value: returnObject,
            })
        }
    }

    function onRemoveButtonClick(event) {
        event.preventDefault()
        // Update  store
        if (firstTransferCreditChecked) {
            firstTransferCreditChecked = false
        }
        radioButtonSelected = null;
        pickedYearsPerBonusSet.update((theArray) => {
            theArray = theArray.filter(
                (item) =>
                    $values.bonusTypes !== null && item.bonusId !== $values.bonusTypes.id,
            )
            return theArray
        })
        numberYears.update((n) => n + -1)
        additionalYearsFormData.forEach(function (item) {
            if (!item.deleted) {
                numberYears.update((n) => n + -1)
            }
        })
        dispatch('remove', id)
    }

    function onAddYearButtonClicked(event) {
        event.preventDefault()
        let nextId =
            additionalYearsFormData.length > 0
                ? additionalYearsFormData[additionalYearsFormData.length - 1].id + 1
                : 1
        additionalYearsFormData.push({id: nextId, data: null, deleted: false})
        additionalYearsFormData = additionalYearsFormData
        numberYears.update((n) => n + 1)
    }

    function validateFirstTransferFields(event) {
        yearsFormData = event.detail.data
        if (yearsFormData === null) {
            dispatch('valueChanged', {
                id: number,
                value: null,
                primaCessione: true,
            })
            return
        }
        validateField('bonusTypes')
        validateField('tributeCode')
        let returnObject = null

        if ($isValid) {
            returnObject = mapCreditDataFirstTransfer(
                mapBonusId,
                $values,
                yearsFormData,
                posteAssicuraCheck
            )
        }
        dispatch('valueChanged', {
            id: number,
            value: returnObject,
            primaCessione: true,
        })
    }

    function removeAdditionalYear(event) {
        let indexToRemove = event.detail.id
        additionalYearsFormData[indexToRemove - 1].deleted = true
        numberYears.update((n) => n + -1)
        doDispatchRemove(event.detail.year, event.detail.noErrors)
    }

    function validateAllFields(event) {
        yearsFormData = event.detail.data
        doDispatch()
    }

    function validateAdditionalYears(event) {
        additionalYearsFormData[event.detail.id - 1] = event.detail
        additionalYearsFormData[event.detail.id - 1].deleted = false
        doDispatch()
    }

    function noSelected(event) {
        if (event.detail.id === 0 && yearsFormData && yearsFormData.fiscalYear) {
            yearsFormData.fiscalYear = null
        } else if (event.detail.id !== 0) {
            if (
                additionalYearsFormData[event.detail.id - 1].data &&
                additionalYearsFormData[event.detail.id - 1].data.fiscalYear
            )
                additionalYearsFormData[event.detail.id - 1].data.fiscalYear = null
        }
        doDispatch()
    }

    function changeRadioButton(value) {
        firstTransferCreditChecked = value
        posteAssicuraCheck = false;
        firstCreditDisposal()
    }

    onMount(() => {
        sismaBonusId.subscribe((value) => {
            sBId = value
        })
        // Create data to be shown in form

        for (var index = 0; index < bonusData.bonus.length; index++) {
            let currentItem = {
                id: index,
                label: bonusData.bonus[index].ecobonus,
                flag: bonusData.bonus[index].flagPrimaCessione,
                flagPostaAssicura: bonusData.bonus[index].flagPosteAssicura,
            }
            bonus.push(currentItem)

            // Update a map between bonus name and id
            mapBonusId[bonusData.bonus[index].ecobonus] = bonusData.bonus[index].id
        }
    })

    function managePosteAssicura() {
        //TODO the dirty stuff
        dispatch("updateFlagAssicura", {id: id, posteAssicuraCheck: posteAssicuraCheck });
        firstCreditDisposal();
    }


    function manageBlur1() {
        dispatch('blurEvent1')
    }

</script>

<style>
    .credit-block {
        background-color: #f0f0f0;
        padding: 1em;
        margin-top: 10px;
    }
</style>

{#if showErrorBox}
    <AlertModal
            id="first-transfer-message"
            title=""
            showModal="true"
            on:close={() => (showErrorBox = false)}>
        <p>{alertBoxMessage}</p>
    </AlertModal>
{/if}

<div class="row">
    <div class="col-xs-12">
        <div class="col-xs-12 credit-block">
            <div class="col-xs-12">
                <h5 style="display: inline">
                    {title}
                    {#if removeEnabled == true}
                        <a
                                class="spacer-xs-left-10"
                                href="/"
                                on:click={onRemoveButtonClick}>
                            <img
                                    style="position: relative; top: -3px; height: 20px"
                                    src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-cestino.png"
                                    alt="remove"/>
                        </a>
                    {/if}
                </h5>
            </div>
            <div class="col-sm-12 spacer-xs-top-15">
                <fieldset>
                    <div class="row">
                        <div class="col-sm-6">
                            <FormGroup status={$errors.bonusTypes ? 'error' : null}>

                                <FormLabel
                                        inputDescription="Tipologia di credito"
                                        inputName="tipo-bonus"
                                        statusMessage={$errors.bonusTypes}
                                        popover={{ content: 'Seleziona la tipologia di credito d’imposta che si vuole cedere. Non è possibile selezionare tipologie di credito per le quali è già stata inoltrata una richiesta di cessione' }}/>

                                <SelectInput
                                        classes="credit-block-input"
                                        id="tipo-bonus"
                                        name="bonus"
                                        keyFieldName="id"
                                        labelFieldName="label"
                                        nullValueLabel={formLabels.GENERIC_DEFAULT_SELECT}
                                        bind:values={bonus}
                                        on:change={validateBonusType}
                                        bind:value={$values.bonusTypes}/>
                            </FormGroup>
                        </div>
                        <div class="col-sm-6">
                            <FormGroup status={$errors.tributeCode ? 'error' : null}>
                                <FormLabel
                                        inputDescription="CODICE TRIBUTO"
                                        inputName="codice-tributo"
                                        statusMessage={$errors.tributeCode}
                                        popover={{ content: 'Viene visualizzato in automatico in funzione del credito d’imposta scelto' }}/>
                                <input
                                        class="form-control credit-block-input"
                                        type="text"
                                        name="tribute"
                                        bind:value={$values.tributeCode}
                                        placeholder="-------"
                                        readonly/>

                            </FormGroup>
                        </div>
                    </div>

                    {#if !isNull}
                        <div
                                class="row"
                                style="display:{showFlag ? 'block' : 'none'} !important">
                            <div class="col-xs-12">
                                <FormLabel
                                        inputDescription="È una prima cessione del credito d'imposta"
                                        inputName="tipo-bonus"
                                        popover={{ content: 'Selezionare \"SI\" solo quando il credito viene ceduto per la prima volta dal beneficiario della detrazione' }}/>

                            </div>
                        </div>

                        <div
                                class="row custom-bottom-margin"
                                style="display:{showFlag ? 'block' : 'none'} !important">
                            <div class="col-xs-12 radio">
                                <RadioButton id={"radio"+realId+"yes"} name={"radiogroup"+realId} label="SI" value={1}
                                             bind:group={radioButtonSelected}
                                             on:change={() => changeRadioButton(true)} checked></RadioButton>

                                <RadioButton  id={"radio"+realId+"no"} name={"radiogroup"+realId} label="NO" value={2}
                                             bind:group={radioButtonSelected}
                                             on:change={() => changeRadioButton(false)} ></RadioButton>
                            </div>
                        </div>


                        {#if showPosteAssicura === true && firstTransferCreditChecked === true }
                            <div
                                    class="row custom-bottom-margin"
                                    style="display:{showFlag ? 'block' : 'none'} !important">
                            <div class="col-xs-12" style="display: inline-flex">
                                <input  style="margin-right: 10px;margin-top: 7px;"
                                        type="checkbox"
                                        name="contract-checkbox"
                                        disabled={false}
                                        bind:checked={posteAssicuraCheck}
                                        on:change={() => managePosteAssicura()}/>
                                <FormLabel
                                        inputDescription="Intendo stipulare contestualmente la polizza di Poste assicura che copre il rischio di eventi calamitosi"
                                        inputName="tipo-bonus"
                                        popover={{ content: 'Selezionare \"SI\" solo quando il credito viene ceduto per la prima volta dal beneficiario della detrazione' }}/>

                            </div>
                            </div>
                        {/if}
                    {/if}

                    {#if !firstTransferCreditChecked && (yearVisible || !showFlag) && !isNull}
                        <FiscalYearBlock
                                {bonusNotSelected}
                                {years}
                                bonusType={$values.bonusTypes}
                                on:fiscalYearSet={validateAllFields}
                                this={FiscalYearBlock}
                                bind:this={firstYear}
                                on:noSelected={noSelected}
                                on:blurEvent={manageBlur1}/>
                        {#each additionalYearsFormData as additionalYear, i (additionalYear.id)}
                            {#if !additionalYear.deleted}
                                <FiscalYearBlock
                                        {bonusNotSelected}
                                        {years}
                                        on:fiscalYearSet={validateAdditionalYears}
                                        id={i + 1}
                                        bonusType={$values.bonusTypes}
                                        additional={true}
                                        on:yearRemoved={removeAdditionalYear}
                                        on:noSelected={noSelected}
                                        on:blurEvent={manageBlur1}/>
                            {/if}
                        {/each}
                        <div class="col-sm-6">
                            <FormGroup>

                                <a href="/" on:click={onAddYearButtonClicked}>
                                    <img
                                            src="/risorse_dt/condivise/immagini/icone/icone-tonde-20-blue/ico-aggiungi-blu.png"
                                            alt="plus"/>
                                    Aggiungi anno
                                </a>
                            </FormGroup>
                        </div>
                    {:else if (!(yearVisible || !showFlag) && !isNull)}
                        <BoxAdviceInfo
                                message="Nel caso di cliente retail per proseguire con la richiesta devi essere il beneficiario originario della detrazione 
                                (prima cessione). È in tal caso necessario cedere tutte le quote annuali o le quote residue non ancora utilizzate in detrazione"/>
                        <FirstTransferBlock
                                {bonusNotSelected}
                                {years}
                                capPrimaCessione={$values.capPrimaCessione}
                                bonusType={$values.bonusTypes}
                                posteAssicura={showPosteAssicura}
                                on:dataSet={validateFirstTransferFields}
                                this={FiscalYearBlock}
                                bind:this={firstYear}
                                on:noSelected={noSelected}
                                on:blurEvent={manageBlur1}/>
                    {/if}

                </fieldset>
            </div>
        </div>

    </div>
</div>
