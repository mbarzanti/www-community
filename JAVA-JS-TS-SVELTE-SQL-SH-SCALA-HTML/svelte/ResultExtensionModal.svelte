<script>
    import { afterUpdate, tick, onMount } from "svelte";
    import { jQuery } from "../../../UiKitLite/libs/jquery";
    import { createEventDispatcher } from "svelte";
    
    const dispatch = createEventDispatcher();

    export let id;
    export let showModal;
    export let stato;
    export let error = false;
    export let email;
    export let sogliaReale;
    export let importoFuoriSoglia;

    let resultIcon = "";
    let resultTitle = "";
    let resultMessage = "";
    let resultSubtitle = ""
    let closeButton = "Chiudi";
    let eventToDispatch = '';

    const succesIcon = "assets/\ico-result-success@2x.png"; 
    const errorIcon = "assets/\ico-result-error@2x.png";
    const warningIcon = "assets/\ico-reclami.png";

    afterUpdate(async () => {
        if (showModal) {
        jQuery("#" + id).modal("show");
        disableBack = false;
        } else {
        jQuery("#" + id).modal("hide");
        await tick();
        dispatch("close");
        disableBack = true;
        }
    });

    async function closeModal() {
        showModal = false;
        await tick();
    }

    onMount(() => {
      if(!error){
        switch(stato.toString().toUpperCase()){
            case "NOT_EXIST":
                resultIcon = succesIcon;
                resultTitle = "Richiesta di valutazione inviata";
                resultMessage = "Ti abbiamo inviato un'email all'indirizzo di posta <strong>"+ email +"</strong> con le informazioni<br> necessarie per poter gestire la tua richiesta";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "NOT_EXIST_AND_BLOCK":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Hai superato il massimale a tua disposizione, non puoi procedere con la cessione dei crediti.";
                resultSubtitle = "";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "PENDING":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Abbiamo ricevuto la tua richiesta di valutazione per l'ampliamento della soglia di cedibilità annua e ti preghiamo di attendere l'esito che ti verrà comunicato all'indirizzo e-mail fornito.";
                resultSubtitle = "";
                closeButton = "Ok, ho capito"
                eventToDispatch = "back"
                break;
            case "ESITO_NEUTRO":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Puoi procedere con la cessione dei crediti residui.";
                resultSubtitle = "Ci risulta che la richiesta di estensione è già stata inviata e non è stata accettata.";
                closeButton = "Ok, ho capito";
                eventToDispatch = "accepted"
                break;
            case "ESITO_RIFIUTATO":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Sulla base delle valutazioni preliminari di Poste Italiane il Cliente non può proseguire con la richiesta di cessione del credito.";
                resultSubtitle = "Ci dispiace, è stata raggiunta la soglia massima cedibile per anno.";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "ESITO_ACCETTATO":
                resultIcon = succesIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Puoi procedere con la cessione dei crediti residui.";
                resultSubtitle = "Ci risulta che la richiesta di estensione è già stata inviata ed è stata accettata.";
                closeButton = "Ok, ho capito";
                eventToDispatch = "accepted"
                break;
            case "NOT_EXIST_AND_BLOCK_RISCHI_FINANZIARI":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "L'importo massimo per Lei attualmente cedibile per anno è " + sogliaReale + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di " + importoFuoriSoglia + " €.";
                resultSubtitle = "";
                closeButton = "Modifica Importi";
                eventToDispatch = "close"
                break;
            case "ESITO_NEUTRO_RISCHI_FINANZIARI":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "L'importo massimo per Lei attualmente cedibile per anno è " + sogliaReale + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di " + importoFuoriSoglia + " €.";
                resultSubtitle = "";
                closeButton = "Modifica Importi";
                eventToDispatch = "close"
                break;
            case "ESITO_ACCETTATO_RISCHI_FINANZIARI":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Con l'importo immesso supereresti il massimale a tua disposizione, per procedere modifica gli importi.";
                resultSubtitle = "Ci risulta che la richiesta di estensione è già stata inviata ed è stata accettata.";
                closeButton = "Modifica Importi";
                eventToDispatch = "close"
                break;
            case "ESITO_NEUTRO_BLOCCANTE":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Hai superato il massimale a tua disposizione, non puoi procedere con la cessione dei crediti.";
                resultSubtitle = "Ci risulta che una richiesta di estensione è già stata inviata.";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "ESITO_ACCETTATO_BLOCCANTE":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Hai superato il massimale a tua disposizione, non puoi procedere con la cessione dei crediti.";
                resultSubtitle = "Ci risulta che una richiesta di estensione è già stata inviata.";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "NOT_EXIST_BLOCCANTE":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Hai superato il massimale a tua disposizione, non puoi procedere con la cessione dei crediti.";
                resultSubtitle = "";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            case "NOT_EXIST_RISCHI_FINANZIARI":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "L'importo massimo per Lei attualmente cedibile per anno è " + sogliaReale + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di " + importoFuoriSoglia + " €.";
                resultSubtitle = "";
                closeButton = "Modifica Importi";
                eventToDispatch = "close"
                break;
            case "PENDING_RISCHI_FINANZIARI":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "L'importo massimo per Lei attualmente cedibile per anno è " + sogliaReale + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di " + importoFuoriSoglia + " €.";
                resultSubtitle = "";
                closeButton = "Modifica Importi";
                eventToDispatch = "close"
                break;
            case "PENDING_BLOCCANTE":
                resultIcon = warningIcon;
                resultTitle = "Richiedi Cessione";
                resultMessage = "Hai superato il massimale a tua disposizione, non puoi procedere con la cessione dei crediti.";
                resultSubtitle = "Ci risulta che una richiesta di estensione è già stata inviata.";
                closeButton = "Chiudi";
                eventToDispatch = "back"
                break;
            default:
                resultIcon = errorIcon;
                resultTitle = "Errore durante la richiesta";
                resultMessage = "La richiesta non è andata a buon fine. Riprovare più tardi.";
                eventToDispatch = "back"
        }
      }
      else {
        resultIcon = errorIcon;
        resultTitle = "Errore durante la richiesta";
        resultMessage = "La richiesta non è andata a buon fine. Riprovare più tardi.";
        eventToDispatch = "back"
      }
    })

    async function handleChiudiPressed() {
      showModal = false;
      await tick();
      dispatch(eventToDispatch);
    }
</script>


<div data-backdrop="static" class="modal fade" {id} tabindex="-1"  role="dialog" aria-labelledby={id}>
    <div class="modal-dialog modal-md modal-dialog-custom" style="width: 50%;">
      <div class="modal-content" style="padding-top: 20px;">
        <div class="modal-body">
          <div class="row">
            <div class="col-lg-12" style="text-align:center">
                <img class="logo-image-pi-medium"  src={resultIcon} srcset={resultIcon} alt="Poste Italiane" style="width:45px; margin:50px 0px 0px 0px"/>
                <h1 style="font-size:40px;font-weight: bold;">{resultTitle}</h1>
                {#if resultSubtitle}
                    <p>{resultSubtitle}</p>
                    <br />
                {/if}
                <p>{@html resultMessage}</p><br>
            </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-sm btn-primary" on:click={handleChiudiPressed}>{closeButton}</button>
      </div>
    </div>
  </div>
</div>
