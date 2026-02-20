<script>
  import { tick } from "svelte";
  import SelectInput from "../../../UiKitLite/forms/SelectInput.svelte";
  import TextInput from "../../../UiKitLite/forms/TextInput.svelte";
  import FormLabel from "../../../UiKitLite/forms/FormLabel.svelte";
  import FormGroup from "../../../UiKitLite/forms/FormGroup.svelte";
  import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";
  import { formErrors, formLabels } from "../../../labels";
  import { createEventDispatcher } from "svelte";
  import numeral from "numeral";
  import { pickedYearsPerBonusSet, maxTreshold, rischiFinanziariAmountError } from "../../../stores";


  const dispatch = createEventDispatcher();

  export let bonusNotSelected;
  export let years;
  export let additional = false;
  export let id = 0;
  export let bonusType;

  let showErrorBox = false;
  let alertBoxMessage = "";

  let amountRegexp = /^\d+(\,){0,1}\d{0,2}$/;

  $: validData = !yearError && !amountError;

  let yearData = null;
  let prevYearData = null;
  let yearError = null;

  let amount = "";
  let prevAmmount = "";
  let amountError = null;

  function resetErrors() {
    yearError = null;
    amountError = null;
  }

  async function checkAndDispatchData() {
    let dataToDispatch = null;

    if (validData) {
      dataToDispatch = {
        fiscalYear: yearData,
        amount: numeral(amount).value(),
      };
    }
    dispatch("fiscalYearSet", {
      id: id,
      data: dataToDispatch,
    });
  }

  async function validateAmount() {
    if (amount.length === 0) {
      amountError = "Obbligatorio";
    }
    else if (!amount.match(amountRegexp)) {
      amountError = "Formato non valido";
    }
    else if (parseFloat(amount.replace(',', '.')) === 0) {
      amountError = 'Importo non valido'
    }
    else if ($maxTreshold && parseFloat(amount) > $maxTreshold) {
      //showErrorBox = true;
      //alertBoxMessage = "Il valore di credito non può superare € " + $maxTreshold;
      amountError = $rischiFinanziariAmountError;
    }
    else {
      amountError = null;
    }
    await tick();
    checkAndDispatchData();
  }

  async function validateYear(event) {
    let data = event.detail;
    if (data.value === "null") {
      yearError = "Seleziona un valore";
      yearData = null;
      onRemoveYearClicked(event, false);
      return;
    } else {
      yearData = data.value;
      yearError = null;
    }

    const index = $pickedYearsPerBonusSet.findIndex(
      (item) =>
        item.id !== id &&
        item.bonusId === bonusType.id &&
        yearData &&
        item.data.label === yearData.label
    );
    if (index !== -1) {
      yearError = "Anno già inserito";
    }
    pickedYearsPerBonusSet.update((theArray) => {
      const data = {
        id: id,
        bonusId: bonusType.id,
        data: yearData,
      };

      const index = theArray.findIndex(
        (item) => item.bonusId === data.bonusId && item.id === id
      );
      if (index !== -1) {
        theArray[index] = data;
      } else {
        theArray.push(data);
      }
      return theArray;
    });
    validateAmount();
    await tick();
  }

  function onRemoveYearClicked(event, remove) {
    event.preventDefault();
    let yearRemoved = null;
    pickedYearsPerBonusSet.update((theArray) => {
      const indexToRemove = theArray.findIndex(
        (item) => item.id === id && item.bonusId === bonusType.id
      );
      if (indexToRemove !== -1) {
        yearRemoved = theArray[indexToRemove].data.label;
        theArray = theArray
          .slice(0, indexToRemove)
          .concat(theArray.slice(indexToRemove + 1, theArray.length));
      }
      return theArray;
    });
    if (remove) {
      dispatch("yearRemoved", {
        id: id,
        year: yearRemoved,
        noErrors: validData,
      });
    } else {
      dispatch("noSelected", {
        id: id,
        year: yearRemoved,
      });
    }
  }

  export function reset() {
    amount = "";
    prevAmmount = "";
    amountError = null;
    amountError = null;
    yearError = null;
    yearData = null;
  }

  function manageBlur() {
    let atLeastOneChanged = false;
    if (yearData !== null && prevYearData !== yearData) {
      atLeastOneChanged = true;
      prevYearData = yearData;
    }
    if (amount !== "" && prevAmmount !== amount) {
      atLeastOneChanged = true;
      prevAmmount = amount;
    }
    if (amount !== "" && yearData !== null && atLeastOneChanged == true) {
      dispatch('blurEvent')
    }
  }
</script>

<style>
  .additional-block-input {
    width: 80%;
  }
</style>

<div class="row">
  <div class="col-sm-6">
    <FormGroup status={yearError ? 'error' : null}>
      <FormLabel
        inputDescription="ANNO FISCALE DI RIFERIMENTO"
        inputName="anno-fiscale"
        statusMessage={yearError}
        popover={{ content: 'Inserire il primo anno fiscale che si vuole cedere\n' }} />
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
        on:blur={manageBlur} />
    </FormGroup>
  </div>

  <div class="col-sm-6">
    <FormGroup status={amountError ? 'error' : null}>
      <FormLabel
        inputDescription="IMPORTO"
        inputName="amount"
        status={amountError ? 'error' : null}
        statusMessage={amountError}
        popover={{ content: 'Inserisci l’importo del credito d’imposta maturato. In caso di crediti d’imposta ripartiti in più quote annuali (es. Superbonus 110%) va inserita la singola quota che si vuole cedere relativa ad ogni anno fiscale di riferimento (per aggiungere più di una quota utilizzare “aggiungi credito”) e non l’importo complessivo del credito maturato. \n' + 'Per ciascun anno fiscale puoi inserire l’importo parziale o totale della singola quota.\n' }} />
      <div class="input-group text-right">
        <div class="input-group-addon credit-block-input">&euro;</div>
        <input
          readonly={bonusNotSelected}
          type="text"
          class="form-control credit-block-input {additional ? 'additional-block-input' : ''}"
          name="amount"
          placeholder="Euro"
          bind:value={amount}
          on:input={validateAmount}
          on:blur={manageBlur} />
        {#if additional}
          <a
            href="/"
            on:click={(event) => {
              onRemoveYearClicked(event, true);
            }}>
            <img
              style="position: relative; right: 10px; top: 10px"
              src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-cestino.png"
              width="20"
              alt="delete-year" />
          </a>
        {/if}
      </div>
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
