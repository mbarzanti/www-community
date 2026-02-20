<script>
  import { onMount } from "svelte";

  import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";
  import InitErrorPage from "../../ko/InitErrorPage.svelte";
  import Manager from "../../../libs/adobetm";

  const trackingManager = Manager();

  /**
     * ERRORI CREDITI
     "001" -> "Superato termine ultimo erogazione!"
     "002" -> "Superata soglia massima totale!"
     "003" -> "Superata soglia massima annuale!"
     "004" -> "Esistono crediti PENDING per il bonus!"
     */

  export let creditsErrors;

  onMount(() => {
    let message = "";
    for (const credit of creditsErrors) {
      message += credit.name + " - " + credit.fiscalYear + ": ";
      for (const error of credit.errors) {
        let errorMessage = "";
        switch (error.code) {
          case "001":
            errorMessage =
              "È stato superato il periodo massimo per inserire richieste per l’anno selezionato. Puoi inserire il credito per gli anni successivi. Per maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
            break;
          case "002":
            errorMessage =
              "È stato raggiunto il valore massimo di credito accettabile. Per\n" +
                "            maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
            break;
          case "003":
            errorMessage =
              "È stato raggiunto il valore massimo di credito accettabile per\n" +
                "            l’anno di riferimento. Per maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
            break;
          case "004":
            errorMessage =
              "Non è possibile proseguire con l'operazione. Per maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.";
            break;
        }
        message += errorMessage + " - ";
      }
      trackingManager.trackErrorMessage(message);
    }

  });
</script>

{#if creditsErrors.length > 0}
  <AlertModal
    id="practice-submit-error"
    title="Attenzione"
    showModal="true"
    on:close={() => (creditsErrors = [])}>
    {#each creditsErrors as credit}
      <p>{credit.creditName} - {credit.fiscalYear}:</p>
      {#each credit.errors as error}
        {#if error.code === '001'}

          <p>
            È stato superato il periodo massimo per inserire richieste per
            l’anno selezionato. Puoi inserire il credito per gli anni successivi
          </p>
        {:else if error.code === '002'}
          <p>
            È stato raggiunto il valore massimo di credito accettabile. Per
            maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.
          </p>
        {:else if error.code === '003'}
            <p>
            È stato raggiunto il valore massimo di credito accettabile per
            l’anno di riferimento. Per maggiori informazioni contattare il
            numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.
          </p>
        {:else if error.code === '004'}
          <p>La tipologia di credito risulta già inserita e in lavorazione</p>
        {:else}
          <p>
            Non è possibile proseguire con l'operazione. Per
            maggiori informazioni contattare il numero verde gratuito 800.003.322 attivo dal lunedì al sabato dalle ore 8:00 alle ore 20:00, esclusi i festivi.
          </p>
        {/if}
      {/each}
    {/each}
  </AlertModal>
{/if}
