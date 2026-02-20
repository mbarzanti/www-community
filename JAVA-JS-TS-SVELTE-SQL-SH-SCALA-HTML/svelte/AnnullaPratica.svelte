<script>

    const jQuery = window.$;

    import SetScaduteModal from "./SetScaduteModal.svelte";
    import {createEventDispatcher, onMount} from "svelte";
    import {rejectCession, sendUserEmail} from "../../../api";
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
        showLoadingSpinner.set(true);
        rejectCession(value.id, value.canale).then((resp) => {
            dispatch("exchange");
        }).catch((error) => {
            titleAlertBox = "Errore";
            errorMessage = "Errore durante l'annullamento della pratica";
            showErrorBox = true;
        })
        .finally(() => {
            showLoadingSpinner.set(false);
        });
 }
 function tornaIndietro() { 
    dispatch("back");
 }
   let btnCheck=false;
    function radioCheck(){ btnCheck=true}


        // FORMATTAZIONE DATA

    function getDay(dataAggiornamento) {
        // 2020-11-04 10:42
        const date = new Date(dataAggiornamento.split(" ")[0])
        return date.getDate()
    }

    function getLetteralMonth(dataAggiornamento) {
        const monthNames = ["gen", "feb", "mar", "apr", "mag", "giu",
            "lug", "ago", "set", "ott", "nov", "dic"
        ];
        const date = new Date(dataAggiornamento.split(" ")[0])
        return '' + monthNames[date.getMonth()].toString().toUpperCase();
    }

    function getYear(dataAggiornamento) {
        const date = new Date(dataAggiornamento.split(" ")[0])
        return date.getFullYear()
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
            <div class="panel checkout-step" style="padding: 10px 30px 0px 30px;">
                
                <h3 style="font-weight: bold;">Annulla pratica</h3>
                <p style="margin-bottom: 40px;">Attenzione stai per annullare il contratto di Cessione dei crediti di imposta. Una volta confermata,
                   l'operazione non portà essere più annullata.</p>

                <div id="accordionGroup" class="accordion-group" >
                
               
                <p>Il sottoscritto <strong>{value.nome.toString().toUpperCase()} {value.cognome.toString().toUpperCase()}</strong> in riferimento al contratto di cessione del credito a favore di <strong>Poste Italiane,</strong>
                   stipulato in data <strong>{getDay(value.dataAggiornamento) + " " + getLetteralMonth(value.dataAggiornamento) + " " + getYear(value.dataAggiornamento)}</strong> ed accettato con ID Pratica <strong>{value.funnelId}</strong>, intende avvalersi del diritto di recesso ai sensi dell'art. 5.4 del
                   <strong style="text-decoration: underline;">contratto</strong> stesso.</p>
               
               
                
                </div>
                <p style="margin-top: 20px;">
                <label class="radio-inline" >
                <input on:click={() => radioCheck()} style="position:relative !important;" type="radio" id="radio" value="radio">
                </label>Accetto e confermo di voler recedere il contratto di cessione dei crediti</p>
            </div>

        </div>
        
        <div style="text-align: right;">
        <button on:click={() => tornaIndietro()} class="btn btn-secondary " style="margin-right: 15px;" > ANNULLA </button>
        <button on:click={() => exchangeComponent()} class="btn btn-primary" disabled={btnCheck===false}> CONFERMA </button>
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
