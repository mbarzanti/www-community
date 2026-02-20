<script>
  import { onMount } from "svelte";
  import GetTrackingManager from "../../libs/adobetm";
  import GenericFunnelResult from "../../UiKitLite/layouts/GenericFunnelResult.svelte";
  import AppBasePageLayout from "../AppBasePageLayout.svelte";
  const trackingManager = GetTrackingManager();
  const errorMessage =
    "Errore durante l'inizializzazione dell'applicazione, prova a ricaricare la pagina";

  export let error;
  onMount(() => {
    console.error(error);
    trackingManager.trackErrorMessage(errorMessage);
    trackingManager.sendErrorEvent();
  });
</script>

<AppBasePageLayout>
  <GenericFunnelResult
    resultType="error"
    resultTitle="Errore"
    resultText={errorMessage} />
</AppBasePageLayout>
