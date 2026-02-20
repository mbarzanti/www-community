<script>
  import AppBasePageLayout from "../AppBasePageLayout.svelte";
  import HeadBanner from "../../UiKitLite/common/HeadBanner.svelte";
  import GenericFunnelResult from "../../UiKitLite/layouts/GenericFunnelResult.svelte";
  import { onMount } from "svelte";
  import GetTrackingManager from "../../libs/adobetm";

  const trackingManager = GetTrackingManager();
  const errorMessage =
    "Sono già presenti a sistema pratiche in lavorazione. Non è possibile inserirne di nuove fino all’espletamento. Per maggiori informazioni chiama il numero gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
  onMount(() => {
    trackingManager.trackErrorMessage(errorMessage);
    trackingManager.sendErrorEvent();
  });
</script>

<style>
  span {
    color: red;
  }
</style>

<!--
<HeadBanner>
    <div class="head-banner">
        <h2>Ecobonus e Sismabonusu</h2>
    </div>
</HeadBanner>
-->
<AppBasePageLayout>
  <div slot="default">
    <div class="row">
      <div class="col-sm-12">
        <div class="box-editable-area box-editable-spacing">
          <GenericFunnelResult
            resultType="warning"
            resultTitle="Attenzione"
            resultText="Non puoi sottoscrivere questo servizio." />
          Sono già presenti a sistema pratiche in lavorazione. Non è possibile
          inserirne di nuove fino all’espletamento. Per maggiori informazioni chiama il numero gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.
        </div>
      </div>
    </div>
  </div>
</AppBasePageLayout>
