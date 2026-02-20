<script>
    import {tick} from 'svelte'
    import SelectInput from '../../../UiKitLite/forms/SelectInput.svelte'
    import TextInput from '../../../UiKitLite/forms/TextInput.svelte'
    import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
    import FormGroup from '../../../UiKitLite/forms/FormGroup.svelte'
    import AlertModal from '../../../UiKitLite/common/AlertModal.svelte'
    import {formErrors, formLabels} from '../../../labels'
    import {createEventDispatcher} from 'svelte'
    import numeral from 'numeral'
    import {pickedYearsPerBonusSet, maxTreshold, rischiFinanziariAmountError} from '../../../stores'

    const dispatch = createEventDispatcher()

    export let bonusNotSelected
    export let capPrimaCessione
    export let posteAssicura
    export let years
    export let id = 0
    export let bonusType

    let showErrorBox = false
    let alertBoxMessage = ''

    let amountRegexp = /^\d+$/
    let yearsToBeSoldRegexp = /^(\d)+$/

    $: validData = !yearsToBeSoldError && !yearError && !amountError && yearsToBeSold && yearData && amount

    let yearData = null
    let prevYearData = null;
    let yearError = null

    let yearsToBeSold = null
    let prevYearsToBeSold = null;
    let yearsToBeSoldError = null

    let amount = ''
    let prevAmmount = "";
    let amountError = null

    function resetErrors() {
        yearError = null
        amountError = null
        yearsToBeSoldError = null
    }

    async function checkAndDispatchData() {
        let dataToDispatch = null
        if (validData) {
            var selectedYearIndex = years.findIndex(
                (item) => parseInt(item.label) === parseInt(yearData.label),
            )
            var emittedData = []
            var i;
            for (i = 0; i < yearsToBeSold; i++) {
                dataToDispatch = {
                    fiscalYear: years[selectedYearIndex],
                    amount: numeral(amount).value() / yearsToBeSold,
                }
                selectedYearIndex++;
                emittedData.push(dataToDispatch)
            }
            dispatch('dataSet', {
                id: id,
                data: emittedData,
            })
        } else {
            dispatch('dataSet', {
                id: id,
                data: null,
            })
        }
    }

    async function validateAmount(flag) {

        if (amount.length === 0) {
            amountError = 'Obbligatorio'
        } 
        else if (!amount.match(amountRegexp)) {
            amountError = 'Formato non valido'
        } 
        else if (parseFloat(amount.replace(',', '.')) === 0) {
            amountError = 'Importo non valido'
        }
        else if ($maxTreshold && parseFloat(amount) > $maxTreshold) {
            //showErrorBox = true
            alertBoxMessage =
                $rischiFinanziariAmountError;
            amountError = alertBoxMessage
        }
        else {
            amountError = null
        }
        await tick()
        if (flag) checkAndDispatchData()
    }

    async function validateYearsToBeSold(flag) {
        if (yearsToBeSold === null) {
            yearsToBeSoldError = 'Obbligatorio'
        } else if (capPrimaCessione !== null && yearsToBeSold > capPrimaCessione) {
            yearsToBeSoldError = 'Cap rate superato'
        } else if (yearsToBeSold <= 0 || !Number.isInteger(yearsToBeSold)) {
            yearsToBeSoldError = 'Bisogna inserire un numero intero positivo'
        } else if (yearData !== null) {
            checkYearConsistency()
        } else {
            yearsToBeSoldError = null
        }
        await tick()
        if (flag) checkAndDispatchData()
    }

    function checkYearConsistency() {
        var possibleYearRange =
            parseInt(years[years.length - 1].label) - parseInt(yearData.label) + 1
        if (yearsToBeSold > possibleYearRange) {
            yearError = 'Inconsistenza nei dati'
            yearsToBeSoldError = 'Inconsistenza nei dati'
        } else {
            yearError = null
            yearsToBeSoldError = null
        }
    }

    async function validateYear(event) {
        let data = event.detail
        if (data.value === 'null') {
            yearError = 'Seleziona un valore'
            yearData = null
            onRemoveYearClicked(event, false)
            return
        } else {
            yearData = data.value
            yearError = null
        }

        const index = $pickedYearsPerBonusSet.findIndex(
            (item) =>
                item.id !== id &&
                item.bonusId === bonusType.id &&
                yearData &&
                item.data.label === yearData.label,
        )
        if (index !== -1) {
            yearError = 'Anno già inserito'
        }
        pickedYearsPerBonusSet.update((theArray) => {
            const data = {
                id: id,
                bonusId: bonusType.id,
                data: yearData,
            }

            const index = theArray.findIndex(
                (item) => item.bonusId === data.bonusId && item.id === id,
            )
            if (index !== -1) {
                theArray[index] = data
            } else {
                theArray.push(data)
            }
            return theArray
        })
        validateAmount(false)
        await tick()
        validateYearsToBeSold(false)
        await tick()
        checkAndDispatchData()
    }

    function onRemoveYearClicked(event, remove) {
        event.preventDefault()
        let yearRemoved = null
        pickedYearsPerBonusSet.update((theArray) => {
            const indexToRemove = theArray.findIndex(
                (item) => item.id === id && item.bonusId === bonusType.id,
            )
            if (indexToRemove !== -1) {
                yearRemoved = theArray[indexToRemove].data.label
                theArray = theArray
                    .slice(0, indexToRemove)
                    .concat(theArray.slice(indexToRemove + 1, theArray.length))
            }
            return theArray
        })
        if (remove) {
            dispatch('yearRemoved', {
                id: id,
                year: yearRemoved,
                noErrors: validData,
            })
        } else {
            dispatch('noSelected', {
                id: id,
                year: yearRemoved,
            })
        }
    }

    function manageBlur() {
        let atLeastOneChanged = false;
        if (yearsToBeSold !== null && prevYearsToBeSold !== yearData) {
            atLeastOneChanged = true;
            prevYearsToBeSold = yearsToBeSold;
        }
        if (yearData !== null && prevYearData !== yearData) {
            atLeastOneChanged = true;
            prevYearData = yearData;
        }
        if (amount !== "" && prevAmmount !== amount) {
            atLeastOneChanged = true;
            prevAmmount = amount;
        }
        if (amount !== "" && yearData !== null && yearsToBeSold !==null && atLeastOneChanged == true) {
            dispatch('blurEvent')
        }
    }


    export function reset() {
        amount = ''
        prevAmmount = '';
        prevYearData = null;
        amountError = null
        yearError = null
        yearData = null
        yearsToBeSold = null
        yearsToBeSoldError = null
    }
</script>

<style>

</style>

<div class="row">
    <div class="col-sm-4 cessione-input-allineato" style="display: flex; height: 110px;">
        <FormGroup status={amountError ? 'error' : null}>
            <FormLabel
                    inputDescription="IMPORTO DEL CREDITO DA CEDERE"
                    inputName="amount"
                    status={amountError ? 'error' : null}
                    statusMessage={amountError}
                    popover={{ content: 'Inserire l\'importo complessivo del credito da cedere o in alternativa, l\'ammontare del credito corrispondente alle rate residue non fruite. Per assicurare la corrispondenza con l’importo complessivo da comunicare ad Agenzia delle Entrate, l\'importo inserito deve essere arrotondato all\'unità di euro (per eccesso se la frazione decimale è uguale o superiore a 50 centesimi di euro, per difetto se inferiore).' }}/>
            <div class="input-group text-right">
                <div class="input-group-addon credit-block-input">&euro;</div>
                <input
                        readonly={bonusNotSelected}
                        type="text"
                        class="form-control credit-block-input"
                        name="amount"
                        placeholder="Euro"
                        bind:value={amount}
                        on:input={()=>validateAmount(true)}
                        on:blur={manageBlur}/>
            </div>
        </FormGroup>
    </div>

    <div class="col-sm-4 cessione-input-allineato" style="display: flex; height: 110px;">
        <FormGroup status={yearsToBeSoldError ? 'error' : null}>
            <FormLabel
                    inputDescription="NUMERO RATE RESIDUE DA CEDERE"
                    inputName="amount"
                    status={yearsToBeSoldError ? 'error' : null}
                    statusMessage={yearsToBeSoldError}
                    popover={{ content: 'Inserire il numero totale delle rate da cedere o il numero delle rate residue non ancora utilizzate in detrazione' }}/>
            <div class="input-group text-right">
                <div class="input-group-addon credit-block-input"/>
                <input
                        readonly={bonusNotSelected}
                        type="number"
                        class="form-control credit-block-input"
                        name="yearsToBeSold"
                        bind:value={yearsToBeSold}
                        on:input={()=>validateYearsToBeSold(true)}
                        on:blur={manageBlur} />
            </div>
        </FormGroup>
    </div>

    <div class="col-sm-4 cessione-input-allineato" style="display: flex; height: 110px;">
        <FormGroup status={yearError ? 'error' : null}>
            <FormLabel
                    inputDescription="PRIMO ANNO FISCALE DA CEDERE"
                    inputName="anno-fiscale"
                    statusMessage={yearError}
                    popover={{ content: 'Inserire il primo anno fiscale che si vuole cedere. Ad esempio per lavori effettuati nel 2021, selezionare 2022.' }}/>
            <SelectInput
                    classes="credit-block-input"
                    readOnly={bonusNotSelected}
                    disabled={bonusNotSelected}
                    id="anno-fiscale"
                    name="annoFiscale"
                    keyFieldName="id"
                    labelFieldName="label"
                    nullValueLabel={formLabels.GENERIC_DEFAULT_SELECT}
                    values={years}
                    bind:value={yearData}
                    on:change={validateYear}
                    on:blur={manageBlur}/>
        </FormGroup>
    </div>
</div>

{#if showErrorBox}
    <AlertModal
            id="first-transfer-message"
            title=""
            showModal="true"
            on:close={() => (showErrorBox = false)}>
        <p>{alertBoxMessage}</p>
    </AlertModal>
{/if}
