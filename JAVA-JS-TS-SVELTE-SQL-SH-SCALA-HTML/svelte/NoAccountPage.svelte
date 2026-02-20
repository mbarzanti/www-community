<script>
  import AppBasePageLayout from "../AppBasePageLayout.svelte";
  import HeadBanner from "../../UiKitLite/common/HeadBanner.svelte";
  import { onMount } from "svelte";
  import GetTrackingManager from "../../libs/adobetm";

  const trackingManager = GetTrackingManager();
  const errorMessage =
    "Il servizio di cessione del credito d’imposta è rivolto verso i titolari di un conto corrente BancoPosta. Al fine di proseguire con la richiesta ti invitiamo ad aprire un conto";

  onMount(() => {
    trackingManager.trackErrorMessage(errorMessage);
    trackingManager.sendErrorEvent();
  });
</script>

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
        <h2>Come sottoscrivere il servizio</h2>
      </div>
    </div>
    <div class="row row-bottom-border">
      <div class="col-sm-12">
        <div class="box-editable-area box-editable-spacing">
          Il servizio di cessione del credito d’imposta è rivolto verso i
          titolari di un conto corrente BancoPosta. Al fine di proseguire con la
          richiesta ti invitiamo ad aprire un conto
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-12">
        <h3>Come aprire il conto</h3>
        <div
          class="wizard-card"
          style="background-image:
          url(/risorse_dt/condivise/immagini/icone/icone-wizard-2x/ico-wizard-prenota-appuntamento@2x.png);">
          <div class="wizard-card-inner">
            <p class="spacer-xs-bottom-0"><strong>Clientela Privati</strong></p>
            <p>
              Apri il conto tramite APP BancoPosta oppure in uno degli oltre 12
              mila Uffici Postali in tutta Italia
            </p>
          </div>
        </div>
        <div
          class="wizard-card"
          style="background-image:
          url(/risorse_dt/condivise/immagini/icone/icone-wizard-2x/ico-wizard-attiva-sottoscrivi@2x.png);">
          <div class="wizard-card-inner">
            <p class="spacer-xs-bottom-0">
              <strong>Clientela Business</strong>
            </p>
            <p>
              Apri il conto presso uno degli oltre 12 mila Uffici Postali in
              tutta Italia
            </p>
          </div>
        </div>
      </div>
    </div>
    <div class="row" />
    <div class="row" />
  </div>
  <div slot="bottom-content">
    <div class="col-sm-12">
      <p class="btn-container btn-container-left spacer-xs-bottom-15 clearfix">
        <button type="button" class="btn btn-primary">
          prendi appuntamento
        </button>
      </p>
    </div>
  </div>
</AppBasePageLayout>
