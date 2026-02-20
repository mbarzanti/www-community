<script>

    const jQuery = window.$;

    import SetScaduteModal from "./SetScaduteModal.svelte";
    import {createEventDispatcher, onMount} from "svelte";
    import {sendUserEmail} from "../../../api";
    import {
        newMail, showLoadingSpinner, inLavorazione,
        notVisibile,
        presaInCarico,
        liquidata,
        rifiutata,
        scaduta,
        annullata,
        praticaCorrente, dataToDisplayIndex,
    } from "../../../stores";
    import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";
    import {modifyUserEmail} from "../../../api";
    import Details from "./Details.svelte";
    import {marcaComeScadutaStorageUrls} from "../../../api";

    const dispatch = createEventDispatcher();

    export let data = [];
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

    function makeModalVisible(value, flag) {
        if (flag) {
            buttonPressed = true;
            showTable = true;
        } else {
            buttonPressed = false;
        }
        praticaDaModificare = value;
        showInsertMailModal = flag;
    }

    function showConfirmSendingEmail(value, flag) {
        if (flag) {
            buttonPressed = true;
            showTable = true;
        } else {
            buttonPressed = false;
        }
        praticaDaInviare = value;
        showConfirmSendingEmailBox = flag;
    }

    function rowClicked(value) {
        praticaDaMostrareDettagli = value;
        if (!buttonPressed) {
            showTable = false;
        }
    }

    function mostraTabella() {
        showTable = true;
        praticaDaMostrareDettagli = null;
    }

    function showConfirmModifyMailModal(flag) {
        showConfirmModifyEmailBox = flag;
    }

    function modifyMail(event) {
        showLoadingSpinner.set(true);
        let success = false;
        modifyUserEmail($newMail, event.detail.praticaId, event.detail.canale)
            .then((response) => {
                if (response.resultCode !== successCode) {
                    titleAlertBox = "Errore";
                    errorMessage = "Errore durante la modifica della mail";
                    showErrorBox = true;
                } else {
                    success = true;
                }
            })
            .catch((error) => {
                titleAlertBox = "Errore";
                errorMessage = "Errore durante la modifica della mail";
                showErrorBox = true;
            })
            .finally(() => {
                showLoadingSpinner.set(false);
                if (success) {
                    dispatch("refresh");
                }
                newMail.set("");
                showConfirmModifyEmailBox = false;
            });
    }

    function notifyServer(event) {
        showLoadingSpinner.set(true);
        sendUserEmail(event.detail.praticaId, event.detail.canale)
            .then((response) => {
                if (response.resultCode === successCode) {
                    titleAlertBox = "Successo";
                    errorMessage = "L'email è stata inviata correttamente";
                    showErrorBox = true;
                } else {
                    titleAlertBox = "Errore";
                    errorMessage = "Errore durante l'invio della mail";
                    showErrorBox = true;
                }
            })
            .catch((error) => {
                titleAlertBox = "Errore";
                errorMessage = "Errore durante l'invio della mail";
                showErrorBox = true;
            })
            .finally(() => {
                showLoadingSpinner.set(false);
            });
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

    //------------MarcaComeScaduta
    function marcaComeScaduta(value) {
        showLoadingSpinner.set(true);
        let success = false;
        marcaComeScadutaStorageUrls(value.detail.funnelId)
            .then((response) => {
                if (response.resultCode !== successCode) {
                    titleAlertBox = "Errore";
                    errorMessage = "Errore durante la modifica della mail";
                    showErrorBox = true;
                } else {
                    success = true;
                }
            })
            .catch((error) => {
                titleAlertBox = "Errore";
                errorMessage = "Errore durante la modifica della mail";
                showErrorBox = true;
            })
            .finally(() => {
                showLoadingSpinner.set(false);
                if (success) {
                    dispatch("refresh");
                }
            });
    }

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

    /*ArrowFunction*/

    function arrowDownToUp(i) {
        if (document.getElementById('arrowUpDown-' + i).classList.contains('glyphicon-chevron-down')) {
            document.getElementById('arrowUpDown-' + i).classList.remove('glyphicon-chevron-down');
            document.getElementById('arrowUpDown-' + i).classList.add('glyphicon-chevron-up')
        } else {
            document.getElementById('arrowUpDown-' + i).classList.remove('glyphicon-chevron-up');
            document.getElementById('arrowUpDown-' + i).classList.add('glyphicon-chevron-down')
        }

    }

    function splitBySpace(date) {
        let noTimeDate = date.split(' ');
        return noTimeDate[0];
    }

    /*---ArrowFunction*/

    function manageDetailAccordion(index, value) {
        arrowDownToUp(index);
        showTable = !showTable;
    }

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

    function formatMoney(number) {
        return number.toLocaleString('it', {minimumFractionDigits: 2});
    }


    /*BACKGROUND-COLOR-BALLON*/
    let backgroundColorGray = '#e3e3e3';
    let backgroundColorOrange = '#ffa500';
    let backgroundColorGreen = '#66ff00';
    let backgroundColorRed = '#fe0000';

    /*----BACKGROUND-COLOR-BALLON-----*/


    function exchangeComponent(value) {
        praticaCorrente.set(value);
        dispatch("exchange");
    }

    function updateMail(value, i) {
        praticaCorrente.set(value);
        dataToDisplayIndex.set(i)
        dispatch("update-mail");
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
    <div class="col-sm-12 spacer-xs-bottom-30">
        <div>
            <table class="col-sm-12">
                <thead>
                <div style="width:100%">
                    <div class="row">
                        <div>
                            <div id="accordionGroup" class="checkout" style="overflow-y: scroll; height: 500px;">
                                <div class="panel checkout-step">
                                    <div style="margin-bottom:30px">
                                        <h3 style="display: inline-block; padding-right: 10px; margin-top: 0px">
                                            Le tue pratiche
                                        </h3>
                                        <span class="numero-pratiche">{dataToDisplay.length}</span>
                                    </div>
                                    <!--1-->
                                    <div id="accordionGroup" class="accordion-group">
                                        {#each dataToDisplay as value, i}
                                            <div class="checkout">
                                                <div class="checkout-calendar" style="display: inline-block;">
                                                    <p class="checkout-step-number"
                                                       style="margin: 0px;">{getDay(value.dataAggiornamento)}</p>
                                                    <p style="font-weight:bold;margin: 0px;">{getLetteralMonth(value.dataAggiornamento)}</p>
                                                    <p style="font-weight:bold;margin: 0px;">{getYear(value.dataAggiornamento)}</p>
                                                </div>
                                                <div class="box-pratica">
                                                    <!--BALLON-backgroundColorGray-->
                                                    {#if value.stato === 'null'}
                                                        <div style="background-color:{backgroundColorGray}"
                                                             class="checkout-step-title">
                                                            <a style="color:black">{value.stato}</a>
                                                        </div>
                                                        <!--BALLON-backgroundColorOrange-->
                                                    {:else if value.stato.toLowerCase() === $inLavorazione.toLowerCase()
                                                    || value.stato.toLowerCase() === $presaInCarico.toLowerCase()}
                                                        <div style="background-color:{backgroundColorOrange}"
                                                             class="checkout-step-title">
                                                            <a style="color:black">{value.stato}</a>
                                                        </div>
                                                        <!--BALLON-backgroundColorGreen-->
                                                    {:else if value.stato.toLowerCase() === $liquidata.toLowerCase()}
                                                        <div style="background-color:{backgroundColorGreen}"
                                                             class="checkout-step-title">
                                                            <a style="color:black">{value.stato}</a>
                                                        </div>
                                                        <!--BALLON-backgroundColorRed-->
                                                    {:else if value.stato.toLowerCase() === $rifiutata.toLowerCase() ||
                                                    value.stato.toLowerCase() === $scaduta.toLowerCase() ||
                                                    value.stato.toLowerCase() === $annullata.toLowerCase()}
                                                        <div style="background-color:{backgroundColorRed}"
                                                             class="checkout-step-title">
                                                            <a style="color:black">{value.stato}</a>
                                                        </div>
                                                    {/if}
                                                    <p class="idPratica"> ID
                                                        PRATICA {value.funnelId ? value.funnelId : "-----"}
                                                        <i id="arrowUpDown-{i}"
                                                           on:click={() => manageDetailAccordion(i, value)}
                                                           class="arrow glyphicon glyphicon-chevron-down" role="button"
                                                           data-toggle="collapse" data-parent="#accordionGroup"
                                                           href="#collapseOne-{i}"/>
                                                    </p>
                                                    <p style="font-weight:bold;margin-top:5px;font-size: 17px;margin: 0px;">
                                                        {value.descrizioneStato}</p>
                                                </div>
                                            </div>
                                            <div id="collapseOne-{i}" class="collapse on closable"
                                                 data-parent="#accordionGroup">
                                                <div class="checkout-step-body">
                                                    <table class="table" style="margin-bottom: 50px;">
                                                        <div style="margin:30px 0px 60px 0px">
                                                            <strong>DETTAGLIO</strong>

                                                        </div>

                                                        <tbody id="noBorderLine" class="tbody">
                                                        <tr>
                                                            <th scope="row">
                                                                <p>DATA CREAZIONE</p>
                                                                <p style="text-transform: lowercase;">{getDay(value.dataPratica) + " " + getLetteralMonth(value.dataPratica) + " " + getYear(value.dataPratica)}</p>
                                                            </th>

                                                            <th scope="row"><p>DATA AGGIORNAMENTO</p>
                                                                <p style="text-transform: lowercase;">{getDay(value.dataAggiornamento) + " " + getLetteralMonth(value.dataAggiornamento) + " " + getYear(value.dataAggiornamento)}</p>
                                                            </th>
                                                        </tr>
                                                        <tr>
                                                            <th scope="row"><p>CANALE DI RICHIESTA</p>
                                                                <p>{value.canale}</p></th>
                                                            <th scope="row"><p>EMAIL</p>
                                                                <p>{value.email}
                                                                    {#if value.stato.toLowerCase() === $presaInCarico.toLowerCase() ||
                                                                    value.stato.toLowerCase() === $inLavorazione.toLowerCase()}
                                                                        <i
                                                                                class="glyphicon glyphicon-pencil" style="cursor:pointer;"
                                                                                on:click={() => updateMail(value, i) }></i>{/if}
                                                                </p></th>
                                                        </tr>
                                                        <br/>
                                                        {#if value.stato.toLowerCase() === $presaInCarico.toLowerCase()}
                                                        <button on:click={() => exchangeComponent(value)} id="button{i}"
                                                              class="btn btn-primary" style="margin-bottom: 40px;">
                                                          ANNULLA PRATICA
                                                      </button>
                                                        {/if}
                                                        </tbody>
                                                    </table>

                                                    <table class="table table_outer">
                                                        <thead>
                                                        <tr>
                                                            <th class="tableTitle"><strong>Bonus</strong></th>
                                                            <th class="tableTitle"><strong>Codice</strong></th>
                                                            <th class="tableTitle"><strong>Anno</strong></th>
                                                            <th class="tableTitle"><strong>Importo</strong></th>
                                                            <th class="tableTitle"><strong>Importo ceduto</strong></th>
                                                        </tr>
                                                        </thead>

                                                        <tbody id="noBorderLine" class="tbody">
                                                        {#each value.iterator as tipobonus, index}
                                                            {#each value.dettagli[tipobonus].anniBonus as innerValue}

                                                                <tr>
                                                                    <td>{tipobonus}</td>
                                                                    <td>{value.codeIndex[tipobonus]}</td>
                                                                    <td>{innerValue.anno}</td>
                                                                    <td>{formatMoney(innerValue.importo)}€</td>
                                                                    <td>{formatMoney(innerValue.importoRichiesto)}€</td>
                                                                    <!--<FormGroup>
                                                                        <FormLabel inputDescription="Importo Anno {value.anno}"
                                                                                   inputName="anno-{value.anno}"/>
                                                                        <TextInput id="anno-{value.anno}" name="anno-{value.anno}"
                                                                                   placeholder="Importo Anno {value.anno}"
                                                                                   value={value.importo}
                                                                                   readOnly={true}
                                                                                   maxlength={validators.MAX_NAME_LENGTH}/>
                                                                    </FormGroup>-->

                                                                </tr>
                                                            {/each}
                                                        {/each}
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        {/each}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                </thead>
            </table>
        </div>
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
