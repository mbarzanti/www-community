<script>
    import {createEventDispatcher} from "svelte";
    import { getDocument } from "../../../api";
    import GetTrackingManager from "../../../libs/adobetm";
    import { showLoadingSpinner } from "../../../stores";

    const dispatch = createEventDispatcher();

    export let transitionDone = false;

    let disableStart = true;

    const trackingManager = GetTrackingManager();

    const documentHeaders = {
        adeDoc: "GUIDA_ADE",
        informativa_precontrattuale: "INFORMATIVA_PRECONTRATTUALE"
    };

    const documentHeadersDownload = {
        0: "GUIDA_ADE",
    };

    // Parameter to handle errors
    let errorMessage = "";

    function submitEvent() {
        dispatch("complete");
    }

    function onDocumentDownloadClick(documentHeader, startButtonEnable) {
        showLoadingSpinner.set(true);
        getDocument(documentHeader)
            .then((documentUrl) => {
                window.open(documentUrl, "_blank");
                if (startButtonEnable == true) {
                    disableStart = true;
                }
            })
            .catch((error) => {
                errorMessage = "errore nel recupero del file";
                showErrorBox = true;
                trackingManager.trackErrorMessage(errorMessage);
            })
            .finally(() => showLoadingSpinner.set(false));
    }
</script>

<div class="row">
    <div class="col-sm-12">
        <div class="box-editable-area">
            <p>L’importo massimo cedibile è pari a 250 mila euro nell'anno. Poste Italiane si riserva in ogni caso di
                effettuare verifiche preliminari sull’ammissibilità della richiesta.<br>
                L’importo massimo cedibile per ogni singola pratica è di 150 mila euro.

            </p>
            <p>Per procedere con la richiesta e affinché la cessione del tuo credito vada a buon fine, è necessario:</p>
            <p>
                <img
                        alt="check"
                        src="/risorse_dt/condivise/immagini/generiche/list-check.png"
                        srcset="/risorse_dt/condivise/immagini/generiche/list-check@2x.png 2x"/>
                Essere titolare del credito di imposta che intendi cedere tra quelli indicati
                con riferimento al DL 34/2020, convertito con modifiche nella legge n.77 del 17 luglio
                2020. Consulta <a
                    href="https://www.poste.it/prodotti/superbonus-altri-bonus-fiscali.html#dettagli"
                    target="_blank">qui</a> i crediti cedibili a Poste Italiane
            </p>
            <p>
                <img
                        alt="check"
                        src="/risorse_dt/condivise/immagini/generiche/list-check.png"
                        srcset="/risorse_dt/condivise/immagini/generiche/list-check@2x.png 2x"/>
                Essere il beneficiario originario della detrazione (prima cessione)
            </p>
        </div>
    </div>
    <div class="col-sm-12">
        <hr/>
    </div>
    <div class="col-sm-12">
        <div class="row">
            <div class="col-sm-12">
                <p>La tua cessione del credito verso Poste Italiane prevede i seguenti passaggi:</p>
            </div>
            <div class="col-sm-4">
                <div class="text-xs-center">
                    <img src="assets/ico-wizard-poste.png" alt="Documento"/>
                    <p class="h6 uppercase-text">
                        1. Compila la richiesta e sottoscrivi il contratto di cessione con Poste Italiane
                    </p>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="text-xs-center">
                    <img
                            src="assets/ico-wizard-agenzia-delle-entrate.png"
                            alt="Documento"/>
                    <p class="h6 uppercase-text">
                        2. COMUNICA AD AGENZIA DELLE ENTRATE LA CESSIONE DEL CREDITO 
                    </p>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="text-xs-center">
                    <img src="assets/ico-wizard-email.png" alt="Documento"/>
                    <p class="h6 uppercase-text">
                        3. Attendi l’email di conferma dell’avvenuta liquidazione del credito da parte di Poste Italiane
                    </p>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-12">
        <div class="box-editable-area spacer-xs-top-20">
            Per maggiori informazioni, consulta la <a href="javascript:void(0)" 
            on:click={() => onDocumentDownloadClick(documentHeaders.adeDoc, true)}>Guida per la cessione
            del credito d’imposta</a>.
        </div>
        <!--<div class="box-editable-area spacer-xs-top-20">
            Per proseguire è necessario visualizzare l'<a href="javascript:void(0)"
                                   on:click={() => onDocumentDownloadClick(documentHeaders.informativa_precontrattuale, true)}>informativa pre-contrattuale</a>
        </div>-->
    </div>
    <div class="row">
        <div class="col-sm-12">
            <p class="btn-container btn-container-left  spacer-xs-top-20 spacer-xs-bottom-15 clearfix">
                <button
                        type="button"
                        class="btn btn-primary pull-right"
                        on:click={submitEvent}
                        disabled={transitionDone === false && disableStart === false}>
                    INIZIA
                </button>
            </p>
        </div>
    </div>
</div>
