<script>

    const jQuery = window.$;

    import SetScaduteModal from "./SetScaduteModal.svelte";
    import {createEventDispatcher, onMount} from "svelte";
    import {getRejectDocument, sendUserEmail} from "../../../api";
    import {newMail, showLoadingSpinner,} from "../../../stores";
    import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";
    import {modifyUserEmail} from "../../../api";
    import Details from "./Details.svelte";
    import {marcaComeScadutaStorageUrls} from "../../../api";

    const dispatch = createEventDispatcher();

    export let data = [];
    export let value = null;
    let showInsertFunnelId = false;
    let funnelIdDaModificare = null;
    let showInsertMailModal = false;
    let praticaDaModificare = null;
    let praticaDaInviare = null;
    let showErrorBox = false;
    let showConfirmSendingEmailBox = false;
    let showConfirmModifyEmailBox = false;
    let errorMessage = "";
    let titleAlertBox = "";
    let popovers = [];
    let successCode = "PI_MS_0000";
    let dataToDisplay = [];
    let showTable = false;
    let buttonPressed = false;
    let praticaDaMostrareDettagli = null;
    let rowValue = null;
    showLoadingSpinner.set(false);

    function showMarcaComeScaduta(value, flag) {
        if (flag) {
            buttonPressed = true;
            showTable = true;
        } else {
            buttonPressed = false;
        }
        rowValue = value;
        showInsertFunnelId = flag;
    }

    function insertTipoBonus(item) {
        let index = -1;
        for (let i = 0; i < dataToDisplay.length; i++) {
            if (dataToDisplay[i].id === item.id) {
                index = i;
                break;
            }
        }
        if (index === -1) {
            dataToDisplay.push({
                id: item.id,
                dataPratica: item.dataPratica,
                descrizioneStato: item.descrizioneStato,
                funnelId: item.funnelId,
                stato: item.stato,
                canale: item.canale,
                naturaGiuridica: item.naturaGiuridica,
                email: item.email,
                dataAggiornamento: item.dataAggiornamento,
                numeroConto: item.numeroConto,
                CF_Piva: item.CF_Piva,
                //dettagli: item.dettagli,
                flagPrimaCessione: item.flagPrimaCessione,
                referente: item.referente,
                cfReferente: item.cfReferente,
                denominazione: item.denominazione,
                idContratto: item.idContratto,
                nome: item.nome,
                cognome: item.cognome,
                posteAssicura: item.posteAssicura,
                tipoBonus: [],
                dettagli: {},
                iterator: [],
                codeIndex: {},
            });
            dataToDisplay = dataToDisplay;
            dataToDisplay[dataToDisplay.length - 1].tipoBonus[0] =
                item.tipoBonus + " - (" + item.codiceTributo + ")";
            if (
                dataToDisplay[dataToDisplay.length - 1].iterator.indexOf(
                    item.tipoBonus
                ) === -1
            ) {
                dataToDisplay[dataToDisplay.length - 1].iterator.push(item.tipoBonus);
                dataToDisplay[dataToDisplay.length - 1].codeIndex[item.tipoBonus] =
                    item.codiceTributo;
            }
            dataToDisplay[dataToDisplay.length - 1].dettagli[item.tipoBonus] = {
                anniBonus: [],
            };
            dataToDisplay[dataToDisplay.length - 1].dettagli[
                item.tipoBonus
                ].anniBonus[0] = {
                anno: item.annoRiferimento,
                importo: item.importo,
                importoRichiesto: item.importoRichiesto,
            };
        } else {
            if (
                dataToDisplay[index].tipoBonus.indexOf(
                    "" + item.tipoBonus + " - (" + item.codiceTributo + ")"
                ) === -1
            ) {
                dataToDisplay[index].tipoBonus.push(
                    item.tipoBonus + " - (" + item.codiceTributo + ")"
                );
            }
            if (dataToDisplay[index].dettagli[item.tipoBonus] === undefined) {
                dataToDisplay[index].iterator.push(item.tipoBonus);
                dataToDisplay[index].codeIndex[item.tipoBonus] = item.codiceTributo;
                dataToDisplay[index].dettagli[item.tipoBonus] = {anniBonus: []};
            }
            dataToDisplay[index].dettagli[item.tipoBonus].anniBonus.push({
                anno: item.annoRiferimento,
                importo: item.importo,
                importoRichiesto: item.importoRichiesto,
            });
        }
    }

    onMount(() => {
        for (let i = 0; i < data.length; i++) {
            insertTipoBonus(data[i]);
        }
        for (let i = 0; i < dataToDisplay.length; i++) {
            let popoverProps = {
                "data-toggle": "tooltip",
                "data-placement": "bottom",
                title: dataToDisplay[i].email,
            };
            popovers.push(popoverProps);
            popovers = popovers;
        }
        //rowClicked()
    });


    function middleware(rowValue) {
        showLoadingSpinner.set(true);
        marcaComeScadutaStorageUrls(rowValue)
            .then((response) => {
                dispatch("refresh");
            })
            .catch((error) => {
                titleAlertBox = "Errore";
                errorMessage = "Errore durante la modifica della pratica";
                showErrorBox = true;
            })
            .finally(() => {
                showLoadingSpinner.set(false);
            });
    }

    function exchangeComponent() {
        dispatch("exchange");
    }

    function tornaIndietro() {
        dispatch("back");
    }

    function downloadDoc() {
        getRejectDocument(value.id, value.canale).then((response) => {
            window.open(response, "_blank");
        })
            .catch((error) => {
                titleAlertBox = "Errore";
                errorMessage = "Errore durante il download del contratto";
                showErrorBox = true;
            })
            .finally(() => {
                showLoadingSpinner.set(false);
            });

    }

</script>

{#if showInsertFunnelId}
    <SetScaduteModal
            id="set-as-scaduta"
            title="Conferma come scaduta"
            showModal="true"
            pratica={praticaDaModificare}
            on:close={() => showMarcaComeScaduta(null, false)}
            on:setAsScaduta={() => middleware(rowValue)}
    />
{/if}

{#if showInsertMailModal}
    <!--<InsertMailModal
              id="modifica-email"
              title="Modifica Email"
              showModal="true"
              pratica={praticaDaModificare}
              on:close={() => makeModalVisible(praticaDaModificare, false)}
              on:modifymail={() => showConfirmModifyMailModal(true)}
      />-->
{/if}

{#if showConfirmSendingEmailBox}
    <!--<ConfirmMailModal
              id="conferma-invio"
              title="Invio Email"
              showModal="true"
              pratica={praticaDaInviare}
              on:close={() => showConfirmSendingEmail(null, false)}
              on:confirm={notifyServer}
      />-->
{/if}

{#if showConfirmModifyEmailBox}
    <!--<ConfirmModifyMailModal
      id="conferma-modifica-email"
      title="Conferma modifica Email"
      showModal="true"
      pratica={praticaDaModificare}
      on:close={() => {
        showConfirmModifyMailModal(false);
        praticaDaModificare = null;
      }}
      on:modify={modifyMail}
    />-->
{/if}

<div class="row">
    <div class="checkout spacer-xs-bottom-30">
        <div class="panel checkout-step" style="padding: 10px 30px 0px 30px; text-align: center;">

            <h1 style="font-weight:bold; font-size: 35px;margin-bottom: 30px; color:#303030">Operazione conclusa con
                successo!</h1>

            <div id="accordionGroup" class="accordion-group">


                <p>Ti abbiamo inviato un'email a <strong style="color:#727273">{value.email}</strong><br> con la copia
                    del recesso del contratto.</p>

                <p style="margin-top: 40px;"><strong>Non hai ancora ricevuto l'email? Scarica la documentazione da
                    qui:</strong></p>

                <div class="documentazioneCreditiImposta" on:click={() => downloadDoc()}>
                    <p style="width:100%; text-align: start;"><span class="glyphicon glyphicon-list-alt"
                                                                    aria-hidden="true"></span>
                        <strong style="margin-left:13px;">Recesso contratto cessioni dei crediti di imposta<i
                                class="download glyphicon glyphicon-cloud-download" aria-hidden="true"></i></strong>
                    </p>
                </div>

            </div>

        </div>

    </div>

    <div style="text-align: right;">
        <button on:click={() => tornaIndietro()} class="btn btn-secondary " style="margin-right: 15px;"> TORNA ALLA
            GESTIONE
        </button>
    </div>

</div>

{#if showErrorBox}
    <AlertModal
            id="practice-submit-error"
            title={titleAlertBox}
            showModal="true"
            on:close={() => (showErrorBox = false)}
    >
        <p>{errorMessage}</p>
    </AlertModal>
{/if}
