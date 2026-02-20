<script>
    import AppBasePageLayout from '../AppBasePageLayout.svelte'
    import {companyData, dashboardError, hideDashboard, showLoadingSpinner, disableCessionButton, newMail,
        praticaCorrente,dashboardErrorContent, dataToDisplayIndex} from '../../stores'
    import AlertModal from '../../UiKitLite/common/AlertModal.svelte'
    import {getData,sendMail} from '../../api'

    let tableDataRetrieved = false
    let tableData = []
    const dispatch = createEventDispatcher();

    import * as api from "../../api";
    import moment from 'moment'
    import Spinner from '../../UiKitLite/common/Spinner.svelte';
    import NewTable from './components/NewTable.svelte';
    import {createEventDispatcher, onMount} from "svelte";
    import AppStepper from "../../components/common/AppStepper.svelte";
    import {writable} from "svelte/store";
    import AnnullaPratica from './components/AnnullaPratica.svelte';
    import PraticaConclusa from './components/PraticaConclusa.svelte'
    import UpdateMailComponent from "./components/UpdateMailComponent.svelte";
    // Parameters to handle errors
    let showErrorBox = false
    var exchange = "1";
    // Start loading spinner
    showLoadingSpinner.set(false)

    // Message to be shown to user if errors happen
    let errorMessage

    var taxCode
    var partitaIVA
    var idPratica

    let maxImportoRichiedibile = null;
    let messImportoMassimoRichiedibile = false;

    function performResearch(event) {
        const data = event.detail.data

        if (data.taxCode) {
            taxCode = data.taxCode
        }
        if (data.partitaIVA) {
            partitaIVA = data.partitaIVA
        }
        if (data.idPratica) {
            idPratica = data.idPratica
        }
        fillTable();
    }

    function refreshTable() {
        fillTable();
    }

    function formatMoney(number) {
        return number.toLocaleString('it', {minimumFractionDigits: 2});
    }

    function fillTable() {
        showLoadingSpinner.set(true)
        tableDataRetrieved = false;
        getData(taxCode, partitaIVA, idPratica)
            .then((result) => {
                var filteredData = result.dossier.map(function (item) {
                    return {
                        id: item.id,
                        codiceTributo: item.codiceTributo,
                        dataPratica: moment(item.dataPratica).format('YYYY-MM-DD HH:mm'),
                        descrizioneStato: item.descrizioneStato,
                        funnelId: item.funnelId,
                        stato: item.stato,
                        tipoBonus: item.tipoBonus,
                        canale: item.canale,
                        naturaGiuridica: item.naturaGiuridica,
                        email: item.emailRichiedente,
                        dataAggiornamento: moment(item.dataAggiornamento).format('YYYY-MM-DD HH:mm'),
                        numeroConto: item.numeroConto,
                        CF_Piva: item.fiscalId,
                        flagPrimaCessione: item.primaCessione,
                        annoRiferimento: item.annoRiferimento,
                        importo: item.importo,
                        importoRichiesto: item.importoRichiesto,
                        referente: item.referente,
                        cfReferente: item.cfReferente,
                        denominazione: item.denominazione,
                        idContratto: item.idContrattoQuadro,
                        nome: item.nome,
                        cognome: item.cognome,
                        posteAssicura: item.posteAssicura
                    }
                })
                tableData = []
                tableData = filteredData
                if (result.dossier.length === 0) {
                    dispatch('init')
                    disableCessionButton.set(false)
                } else {
                    if($companyData && $companyData.codiceNdg){
                        api
                        .rischiFinanziariPreLogin($companyData.codiceNdg)
                        .then(resp => {
                            maxImportoRichiedibile = resp.massimoImportoRichiedibile;
                            messImportoMassimoRichiedibile = true;
                            tableDataRetrieved = true;
                        })
                    }
                    else {
                        tableDataRetrieved = true;
                    }
                }
            })
            .catch((error) => {
                errorMessage = 'Errore nel recupero dei dati'
                showErrorBox = true
                dashboardError.set(true);
                hideDashboard.set(true);
            })
            .finally(showLoadingSpinner.set(false))
    }

    onMount(() => {
        api.getCompanyData().then(() => {
        })
        .finally(() => {
            fillTable();
        })
    })

    let value = null;

    function hideTable() {
        if (exchange === "1") {
            value = $praticaCorrente;
            exchange = "2";
        } else if (exchange === "2") {
            showLoadingSpinner.set(true);
            sendMail(value.id, value.canale).then(resp => {
                exchange = "3"
            }).catch((error) => {
                errorMessage = "Errore durante l'invio della mail";
                showErrorBox = true;
            })
                .finally(() => {
                    showLoadingSpinner.set(false);
                });
        }
    }

    function updateMail() {
        value = $praticaCorrente;
        exchange = "4";
    }

    function goBack() {
        fillTable();
        praticaCorrente.set(null);
        exchange = "1"

    }

    function updateData() {
        fillTable();
        newMail.set(null);
        dataToDisplayIndex.set(null);
        praticaCorrente.set(null);
        exchange = "1";

    }

</script>

<Spinner showModal={$showLoadingSpinner}/>

<AppBasePageLayout id="company-data-main" enableMainPills={false} on:init={() => {dispatch('init')}}>

    {#if tableDataRetrieved}
        <div
                class="welcome welcome-simple spacer-xs-top-20 spacer-md-top-0
      spacer-xs-bottom-0 spacer-md-bottom-20">
            <div class="row">
                <div class="col-sm-12 col-md-9">
                    <div class="abstract">
                        <div class="abstract-heading spacer-xs-bottom-25">
                            <h1>Cessione del credito d’imposta</h1>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="checkout spacer-xs-bottom-30">
                <div class="panel checkout-step">
                    <div style="margin-bottom:30px">
                        <h3 style="display: inline-block; padding-right: 10px; margin-top: 0px">
                            Cedi i tuoi crediti
                        </h3>
                    </div>
                    <div class="row">
                        <div class="col-sm-8">
                            <p>La cessione dei crediti d'imposta a Poste Italiane consente di ottenere la liquidit&agrave; in
                            un'unica soluzione sul proprio Conto Corrente BancoPosta.</p>
                            {#if messImportoMassimoRichiedibile}
                            <p>L'importo massimo che puoi cedere ancora <strong>nel 2021</strong> a Poste Italiane è pari a*: {formatMoney(maxImportoRichiedibile)} €</p>
                            {/if}
                            <a href="https://www.poste.it/prodotti/superbonus-altri-bonus-fiscali.html#dettagli">Consulta qui</a> i crediti attualmente disponibili
                            {#if messImportoMassimoRichiedibile}
                            <p style="font-size: 16px;margin-top: 10px;">*Tale indicazione non comporta alcun impegno ad acquisire il credito in futuro da parte di Poste Italiane, che si riserva di modificare e aggiornare tale importo sulla base delle proprie valutazioni.</p>
                            {/if}
                        </div>
                        <div class="col-sm-4">
                            <button class="btn btn-primary" on:click={() => dispatch("init")}>Richiedi cessione</button>
                        </div>
                    </div>
                </div>
            </div>
            <!--  <TableData bind:data={tableData} on:refresh={refreshTable} /> // MODIFICA QUI -->
            
        {#if exchange === "1"}
            <NewTable bind:data={tableData} on:refresh={refreshTable} on:exchange={() => hideTable()} on:update-mail={() => updateMail()}/>
        {:else if exchange === "2"}
            <AnnullaPratica bind:value={value} bind:data={tableData} on:refresh={refreshTable}
                            on:exchange={() => hideTable()} on:back={() => goBack()}/>
        {:else if exchange === "3"}
            <PraticaConclusa bind:value={value} bind:data={tableData} on:refresh={refreshTable}
                             on:exchange={() => hideTable()} on:back={() => goBack()}/>
        {:else if exchange === "4"}
            <UpdateMailComponent bind:value={value} bind:data={tableData} on:updateData={() => updateData()} on:back={() => goBack()}></UpdateMailComponent>
        {/if}


        </div>
    {:else}
        <!--//<showSpinner-->
        <div
                class="welcome welcome-simple spacer-xs-top-20 spacer-md-top-0
      spacer-xs-bottom-0 spacer-md-bottom-20">
            <div class="row">
                <div class="col-sm-12 col-md-9">
                    <div class="abstract">
                        <div class="abstract-heading spacer-xs-bottom-25">
                            <h5>Attendi...caricamento in corso</h5>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    {/if}
</AppBasePageLayout>
{#if showErrorBox}
    <AlertModal
            id="practice-submit-error"
            title="Errore"
            showModal="true"
            on:close={() => (showErrorBox = false)}>
        <p>{errorMessage}</p>
    </AlertModal>
{/if}