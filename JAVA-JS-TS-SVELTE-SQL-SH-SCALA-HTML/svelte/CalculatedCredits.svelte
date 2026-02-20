<script>
  import FormLabel from '../../../UiKitLite/forms/FormLabel.svelte'
  import { listino } from '../../../stores';
  import { convention } from '../../../stores';
  import numeral from "numeral";
  export let result

  let currencyFormat = "0,0.00";
  $: totalAmount = splitAmountValue(result.computedBonus);
  $: totalRequested = formatValueData(result.bonusDetail.reduce((total, next) => { return { creditRequested: total.creditRequested + next.creditRequested }}).creditRequested);

  $: resultData = result.bonusDetail.map(item => {
    return {
      name: item.name,
      year: item.year,
      creditRequested: formatValueData(item.creditRequested),
      creditAmount: formatValueData(item.creditAmount)
    };

  })


  function splitAmountValue(value) {
    return {
      value: formatValueData(value).split(',')[0],
      subValue: formatValueData(value).split(',')[1]
    }
  }

  function formatValueData(value) {
    return numeral(value).format(currencyFormat);
  }
</script>

<style>
  .credit-row {
    border: 1px solid #f0f0f0;
    padding: 1em;
  }

  .credit-heading {
    display: none;
    border: 0px;
  }

  .middle {
    vertical-align: middle;
  }

  /* Responsive design for the desktop devices  */
  @media (min-width: 769px) {
    .heading {
      display: none;
    }

    .small-heading {
      font-size: 0.9em;
    }

    .credit-row {
      width: 100%;
      display:table;
      margin: 0 auto;
    }

    .side-bar {
      border-right: 1px solid #f0f0f0;
    }

    .credit-cell-block {
      display: table-cell;
      width: 30%;
    }

    .credit-first-cell-block {
      padding-left: 1em;
      padding-right: 1em;
      width: 40%;
    }

    .credit-heading {
      width: 100%;
    }

    .currency {
      text-align: right;
    }
  }

</style>

  <div class="row">
    <div class="col-xs-12 col-sm-9">
      {#if $listino !== null && $listino !== undefined && $listino !== "" && $listino !== "standard" &&
      $convention !== null && $convention !== undefined && $convention !== ""}
        <span>in convenzione <b style = "text-transform:uppercase;">{$convention}</b></span>
      {:else} <span>&nbsp;</span>
      {/if}
    </div>  
    <div class="col-xs-12 col-sm-3 text-right">
        <span class="price-detail price-detail-xs pull-sm-left pull-sm-right">
          <span style="font-size:20px" class="price-currency">€</span>
          <span style="font-size:30px" class="price-value"><strong>{totalAmount.value}</strong></span>
          <span style="font-size:20px" class="price-subvalue">,{totalAmount.subValue}</span>
        </span>
  </div>
  </div>

<div class="row" id="credits-value">
  <div class="col-xs-12">
    <div class="credit-heading credit-row">
      <div class="credit-cell-block credit-first-cell-block small-heading">TIPOLOGIA</div>
      <div class="credit-cell-block text-right small-heading">CREDITO CEDUTO</div>
      <div class="credit-cell-block text-right small-heading">IMPORTO DA LIQUIDARE</div>
    </div>

    {#each resultData as row}


    <div class="credit-row">
      <div class="credit-cell-block credit-first-cell-block side-bar">
        <span class="heading"><strong>TIPOLOGIA:</strong></span>
        <p>{row.name} <br> Anno di riferimento: {row.year}</p>
      </div>
      <div class="credit-cell-block middle currency">
        <span class="heading">
          <strong>CREDITO CEDUTO:</strong>
        </span>
        &euro;{row.creditRequested}
      </div>
      <div class="credit-cell-block middle currency">
        <span class="heading">
          <strong>
            IMPORTO DA LIQUIDARE:
          </strong>
        </span>
        &euro;{row.creditAmount}
      </div>
    </div>

    {/each}

    <div class="credit-row">
      <div class="credit-cell-block credit-first-cell-block side-bar">
        <p><strong>TOTALE</strong></p>
      </div>
      <div class="credit-cell-block middle currency">
        <span class="heading">
          <strong>CREDITO CEDUTO:</strong>
        </span>
        <strong>&euro;{totalRequested}</strong>
      </div>
      <div class="credit-cell-block middle currency">
        <span class="heading">
          <strong>
            IMPORTO DA LIQUIDARE:
          </strong>
        </span>
        <strong>
          &euro;{totalAmount.value},{totalAmount.subValue}
        </strong>
      </div>
    </div>
  </div>
</div>
