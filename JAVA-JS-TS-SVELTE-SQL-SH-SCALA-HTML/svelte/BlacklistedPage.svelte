<script>
  import { onMount } from "svelte";
  import GetTrackingManager from "../../libs/adobetm";

  import GenericFunnelResult from "../../UiKitLite/layouts/GenericFunnelResult.svelte";
  import AppBasePageLayout from "../AppBasePageLayout.svelte";

  const trackingManager = GetTrackingManager();

  let defaultMessage = "Utente non abilitato al servizio. Per maggiori informazioni chiama il numero gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
  export let errorMessage;
  let message = "";
  onMount(() => {
    //trackingManager.trackErrorMessage(errorMessage);
    //trackingManager.sendErrorEvent();
    switch(errorMessage) {
      case "User in blacklist":
        message = "Sulla base delle valutazioni preliminari di Poste Italiane il Cliente non può proseguire con la richiesta di cessione del credito.";
        break;
      case "CAI block":
        message = "Spiacenti, non è possibile proseguire con la richiesta di cessione del credito d’imposta a Poste Italiane.";
        break;
      default:
        message = defaultMessage;
    }
  });
</script>

<AppBasePageLayout>
  <GenericFunnelResult
    resultType="warning"
    resultTitle="Attenzione"
    resultText={message} />
</AppBasePageLayout>
