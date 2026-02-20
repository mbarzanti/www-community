<script>
  import { onMount } from "svelte";
  import GetTrackingManager from "../../libs/adobetm";

  import GenericFunnelResult from "../../UiKitLite/layouts/GenericFunnelResult.svelte";
  import AppBasePageLayout from "../AppBasePageLayout.svelte";
  const trackingManager = GetTrackingManager();

  const errorMessage =
    "Utente non abilitato al servizio. Per maggiori informazioni chiama il numero gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
  onMount(() => {
    trackingManager.trackErrorMessage(errorMessage);
    trackingManager.sendErrorEvent();
  });
</script>

<AppBasePageLayout>
  <GenericFunnelResult
    resultType="warning"
    resultTitle="Attenzione"
    resultText={errorMessage} />
</AppBasePageLayout>
