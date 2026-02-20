<script>
    import { createEventDispatcher } from "svelte";
    import { get } from "svelte/store";
    import api from "../../api/api";
    import Loader from "../../SvelteKit/elements/Loader.svelte";
    import FormItem from "../../SvelteKit/forms/FormItem.svelte";
    import FormRow from "../../SvelteKit/forms/FormRow.svelte";
    import SelectField from "../../SvelteKit/forms/SelectField.svelte";
    import MultiSelectButton from "../../SvelteKit/forms/MultiSelectButton.svelte";
    import CustomDatepicker from "./custom-datepicker.svelte";
    import MessageToast from "./MessageToast.svelte";
    import { partner_tax_code, tipoDiRicerca } from "./store";
    import Tab from "./Tab.svelte";
    import TabList from "./TabList.svelte";
    import TabPanel from "./TabPanel.svelte";
    import Tabs from "./Tabs.svelte";

    const eventDispatcher = createEventDispatcher();

    export let salesPointFilename;
    export let appState;

    let input;
    export let visible = false;
    export let successMessage;
    export let textMessage;
    let files = null;
    let valid;
    let validDateFrom;
    let validDateTo;
    let dateFrom;
    let dateTo = new Date().toLocaleDateString("it-IT", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    });
    let stato;
    export let isLoading = false;
    let isHistoryLoading = false;
    let variazioni = [];
    export let tipoVariazione;

    const onFileSelected = (e) => {
        isLoading = true;
        let ext = e.target.files[0].name.split(".").pop();
        if (ext === "csv") {
            files = e.target.files[0];
            api.ms.funnel.uploadCSV(
                appState.globalContext.FUNNEL_INSTANCE_ID,
                get(tipoDiRicerca) === "cliente"
                    ? appState.globalContext.customers.contractor.taxData
                          .taxCode
                    : get(partner_tax_code),
                files,
                tipoVariazione,
                (success, data) => {
                    if (success) {
                        isLoading = false;
                        salesPointFilename = data.filename;
                        successMessage = true;
                        textMessage = "Il file è stato caricato correttamente.";
                        visible = true;
                        hideToast();
                    } else {
                        isLoading = false;
                        files = null;
                        successMessage = false;
                        textMessage = data.parsingMessage
                            ? data.parsingMessage
                            : "Si è verificato un errore durante il caricamento del file, si prega di riprovare più tardi.";
                        visible = true;
                        hideToast();
                    }
                }
            );
        } else {
            isLoading = false;
            files = null;
            successMessage = false;
            textMessage = "Formato file non valido";
            visible = true;
            hideToast();
        }
        removeFile();
    };

    const deleteFile = () => {
        files = null;
        removeFile();
    };

    function hideToast() {
        setTimeout(() => {
            visible = false;
        }, 3500);
    }

    function avviaProcesso(e) {
        files = null;
        removeFile();
        eventDispatcher("submit", { detail: e.detail });
    }

    function removeFile() {
        jQuery(input).val("");
    }

    function loadHistory() {
        isHistoryLoading = true;
        api.history.history.getStorico(
            get(tipoDiRicerca) === "cliente"
                ? appState.globalContext.customers.contractor.taxData.taxCode
                : get(partner_tax_code),
            formatDate(dateFrom, true),
            formatDate(dateTo, false),
            tipoVariazione,
            stato === "All" ? "" : stato,
            (success, data) => {
                if (success) {
                    variazioni = data.historyList;
                    isHistoryLoading = false;
                } else {
                    isHistoryLoading = false;
                    successMessage = false;
                    textMessage =
                        "Si è verificato un errore durante il caricamento dei dati, riprovare più tardi.";
                    visible = true;
                    hideToast();
                }
            }
        );
    }

    function downloadFile(filename) {
        api.history.history.getFile(
            filename,
            tipoVariazione,
            (success, data, status) => {
                if (success) {
                    const url = window.URL.createObjectURL(new Blob([data]));
                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute("download", filename);
                    document.body.appendChild(link);
                    link.click();
                } else {
                    successMessage = false;
                    textMessage =
                        status === 404
                            ? "File di esito non disponibile"
                            : "Si è verificato un errore durante il download del file, riprovare più tardi.";
                    visible = true;
                    hideToast();
                }
            }
        );
    }

    function formatDate(date, isFrom) {
        let formattedDate = "";
        const splittedDate = date.split("/");
        formattedDate =
            addPadding(parseInt(splittedDate[0])) +
            "-" +
            addPadding(parseInt(splittedDate[1])) +
            "-" +
            splittedDate[2] +
            "T";
        formattedDate += isFrom ? "00:00:00.000+0000" : "23:59:59.000+0000";
        return formattedDate;
    }

    function addPadding(n) {
        return (n < 10 ? "0" : "") + n;
    }
</script>

<div class="container pt20 pb20 mb40">
    <Tabs>
        <TabList>
            <Tab>
                <h5>Nuova</h5>
            </Tab>
            <Tab>
                <h5>Storico</h5>
            </Tab>
        </TabList>
        <TabPanel>
            {#if isLoading}
                <div class="center-loader" style="margin-top: 150px;">
                    <div>
                        <Loader />
                    </div>
                </div>
            {:else}
                <div>
                    <div>
                        <input
                            style="display:none"
                            type="file"
                            name="file"
                            id="file"
                            accept=".csv"
                            bind:this={input}
                            on:change={(e) => onFileSelected(e)}
                        />
                    </div>
                    <p />
                    <h2>Inserisci file</h2>
                    <p />
                    <h6>
                        Inserisci un file .csv contentente i punti vendita e in
                        seguito premi sul pulsante "Avvia processo di
                        variazione" per avviare la procedura di caricamento.
                    </h6>
                    <MessageToast
                        {visible}
                        success={successMessage}
                        text={textMessage}
                    />
                    {#if files !== undefined && files !== null}
                        <div class="file-box">
                            <svg
                                width="40"
                                height="40"
                                xmlns="http://www.w3.org/2000/svg"
                                ><g
                                    id="ICONS"
                                    stroke="none"
                                    stroke-width="1"
                                    fill="none"
                                    fill-rule="evenodd"
                                    ><g id="Allegato"
                                        ><g
                                            id="ico-allegato@2x"
                                            stroke-width="1"
                                            transform="matrix(0 -1 -1 0 31 30)"
                                            stroke="#0047BB"
                                            stroke-linecap="round"
                                            stroke-linejoin="round"
                                            ><path
                                                d="M19.978 8.306L8.276 20.048c-2.06 2.041-4.057 2.041-5.994 0-1.937-2.04-1.937-4.123 0-6.248L14.526 1.481c1.07-1.09 2.318-.892 3.742.594 1.423 1.486 1.583 2.917.478 4.291L8.276 16.918c-.845.69-1.646.69-2.404 0-.757-.69-.757-1.494 0-2.412l6.98-6.994"
                                                id="Path-11"
                                            /></g
                                        ></g
                                    ></g
                                ></svg
                            >
                            {files.name}
                        </div>
                    {/if}
                    <p
                        style="display: flex; justify-content: space-between; margin-top: 30px"
                    >
                        <a
                            on:click={deleteFile}
                            href={undefined}
                            class={files
                                ? "btn btn-default"
                                : "btn btn-default disabled"}
                        >
                            Annulla
                        </a>
                        <a
                            href={undefined}
                            class="btn btn-yellow"
                            on:click={() => {
                                input.click();
                            }}
                        >
                            Seleziona file
                        </a>
                        <a
                            class={!files
                                ? "btn btn-yellow disabled"
                                : "btn btn-yellow"}
                            href={undefined}
                            on:click={avviaProcesso}
                        >
                            Avvia processo di variazione
                        </a>
                    </p>
                </div>
            {/if}
        </TabPanel>
        <TabPanel>
            {#if isHistoryLoading}
                <div class="center-loader" style="margin-top: 150px;">
                    <div>
                        <Loader />
                    </div>
                </div>
            {:else}
                <p />
                <h2>Ricerca</h2>
                <MessageToast
                    {visible}
                    success={successMessage}
                    text={textMessage}
                />
                <FormRow>
                    <FormItem
                        label={"Data da:"}
                        type={"date"}
                        size={"ld"}
                        descriptor={{ options: { required: true } }}
                    >
                        <CustomDatepicker
                            bind:valid={validDateFrom}
                            bind:value={dateFrom}
                            default_value=""
                            options={{
                                default_invalid: true,
                                required: true,
                                endDate: new Date(),
                            }}
                        />
                    </FormItem>
                    <FormItem
                        label={"Data a:"}
                        type={"date"}
                        size={"ld"}
                        descriptor={{ options: { required: true } }}
                    >
                        <CustomDatepicker
                            bind:valid={validDateTo}
                            bind:value={dateTo}
                            default_value=""
                            options={{
                                default_invalid: true,
                                required: true,
                                endDate: new Date(),
                            }}
                        />
                    </FormItem>
                    <FormItem
                        label={"Stato:"}
                        type={"select"}
                        size={"ld"}
                        descriptor={{ options: { required: false } }}
                    >
                        <SelectField
                            name={"Stato"}
                            options={{
                                required: true,
                            }}
                            default_value={{
                                value: "",
                                label: "Tutti gli stati",
                            }}
                            bind:value={stato}
                            values={[
                                {
                                    value: "All",
                                    label: "Tutti gli stati",
                                },
                                {
                                    value: "In corso",
                                    label: "In corso",
                                },
                                {
                                    value: "Terminato con errori",
                                    label: "Terminato con errori",
                                },
                                {
                                    value: "Terminato senza errori",
                                    label: "Terminato senza errori",
                                },
                            ]}
                        />
                    </FormItem>
                </FormRow>
                <p style="display: flex; justify-content: flex-end;">
                    <a
                        class={!validDateTo || !validDateFrom
                            ? "btn btn-yellow disabled"
                            : "btn btn-yellow"}
                        href={undefined}
                        on:click={loadHistory}
                    >
                        Cerca
                    </a>
                </p>
                <div class="card-body">
                    <div class="table-verifica-ruoli">
                        {#if variazioni.length > 0}
                            <table class=" results  projalloc">
                                <thead>
                                    <tr>
                                        <th>Data</th>
                                        <th>ID Funnel</th>
                                        <th>Operatore</th>
                                        <th>Stato</th>
                                        <th>Dettaglio elaborazione</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {#each variazioni as v}
                                        <tr>
                                            <td
                                                >{v.dataCreazioneInput
                                                    ? new Date(
                                                          v.dataCreazioneInput
                                                      ).toLocaleDateString(
                                                          "it-IT",
                                                          {
                                                              day: "2-digit",
                                                              month: "2-digit",
                                                              year: "numeric",
                                                          }
                                                      )
                                                    : ""}
                                            </td>
                                            <td
                                                >{v.funnelId
                                                    ? v.funnelId
                                                    : ""}</td
                                            >
                                            <td
                                                >{v.operatorId
                                                    ? v.operatorId
                                                    : ""}</td
                                            >
                                            <td>{v.stato ? v.stato : ""}</td>
                                            <td>
                                                {#if v.filenameEsito}
                                                    <a
                                                        style="cursor: pointer;"
                                                        href={undefined}
                                                        on:click={() =>
                                                            downloadFile(
                                                                v.filenameEsito
                                                            )}>Scarica dati</a
                                                    >
                                                {/if}
                                            </td>
                                        </tr>
                                    {/each}
                                </tbody>
                            </table>
                        {/if}
                    </div>
                </div>
            {/if}
        </TabPanel>
    </Tabs>
    <div
        class="bottom-buttons fixed fixed-bottom"
        style="position:fixed;z-index: 999;"
    >
        <slot name="buttons" />
    </div>
</div>

<style>
    .file-box {
        border: 1px solid #0047bb;
        color: #0047bb;
        width: 50%;
        margin-top: 20px;
        margin-bottom: 20px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
</style>
