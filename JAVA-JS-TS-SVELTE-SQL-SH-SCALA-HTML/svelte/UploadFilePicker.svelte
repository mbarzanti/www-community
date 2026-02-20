<style>
    .btn-file {
        position: relative;
        overflow: hidden;
    }

    .btn-file input[type="button"] {
        position: absolute;
        top: 0;
        right: 0;
        min-width: 100%;
        min-height: 100%;
        font-size: 100px;
        text-align: right;
        filter: alpha(opacity=0);
        opacity: 0;
        background: red;
        cursor: pointer;
        display: block;
    }

    .fp-label {
        color: #222427;
        line-height: 14px;
    }

    .fp-side {
        font-size: 12px;
        font-weight: 500;
        flex-grow: 6;
        align-self: center;
        flex-basis: 2rem;
        margin-bottom: 0;
    }

    .fp-load-btn {
        width: 21px;
        height: 16px;
        transform: scale(1.1);
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }


    .fp-action {
        font-size: 12px;
        font-weight: 500;
        flex-grow: 3;
        align-self: center;
        margin-bottom: 0;
        text-align: right;
        padding-right: 0.7rem;
    }

    .fp-item {
        box-sizing: border-box;
        display: flex;
        padding: 1rem;
        width: 25rem;
        flex-wrap: nowrap;
        flex-direction: row;
        border: 1px solid #DEDEDE;
        /*border-radius: 8px;*/
        background-color: #FFFFFF;
    }

    .fp-uploaded-btn {
        width: auto;
        align-self: flex-end;
        -webkit-transform-origin-x: left;
        transform: scale(1.2) !important;
        margin-left: 0 !important;
    }

    .input-group-fp {
        flex-direction: column;
        align-items: flex-start;
    }

    .circle-stepper-horizontal {
        align-self: center;
        margin-left: 0.5rem;
        transform: scale(1.5);
    }

    .circle-stepper-horizontal .circle-step {
        padding: 0;
    }

    .fp-container {
        display: flex;
        flex-direction: row;
        margin-bottom: .7rem;
        align-items: center;
    }

    .fp-load-btn {
        background: url(/risorse_dt/condivise/immagini/icone/icone-default-blue/ico-back-to-the-top.png);
        width: 25px;
        height: 24px;
        margin-bottom: 3px;
    }

    .invalid-feedback-visible {
        display: block;
    }
</style>


<script>
    import {createEventDispatcher, onMount} from "svelte";
    import Button from "./Button.svelte";
    import Modal from "./../elements/Modal.svelte";
    import {closeModal, openModal} from '../sveltekit';
    import Loader from "../elements/Loader.svelte";

    const eventDispatcher = createEventDispatcher();

    export let name;
    export let value;

    let uploadModal;
    let confirmModal;
    let tooltip;
    let tooltips = {};
    let validationMessage = "Caricamento file obbligatorio";
    export let ignoreDirty = false;

    export let valid = false;

    export let options = {};

    let internalState = {
        FRONTE: {
            address: "",
            fileName: "FRONTE",
            fileSelected: false,
            files: []
        }
    };
    let fileName = "";
    let address = "";
    let files = [];
    let feedback = false;
    let labels = ["FRONTE"];
    let uploadRunning = false;
    let allSelected = false;


    let modalTitle;
    let modalLoadingText;
    let modalSuccessText;
    let modalFailureText;
    let modalErrorIntent = false;

    let confirmLabel;
    let retryEnabled = false;

    const acceptedExtensions = ["pdf"];


    const DEFAULT_MODAL_TITLE_SUCCESS = "Invio dei dati";
    const DEFAULT_MODAL_SUCCESS_TEXT = "Caricamento completato";
    const DEFAULT_MODAL_FAILURE_TEXT = "Caricamento fallito";
    const DEFAULT_MODAL_LOADING_TEXT = "Invio dei dati in corso";

    //const GECT_MODAL_FAILURE_TITLE = "Errore chiamata GECT";
    //const GECT_MODAL_FAILURE_TEXT = "Chiamata a Gect in errore";

    const UPLOADED_TEXT = "CARICATO";
    const UPLOAD_TEXT = "ACQUISISCI";

    let resultGectCall = false; //in caso di gect KO: resultGectCall===true -  apro la modale con errore e permetto di ripremere

    let arg = {};


    onMount(() => {
        if (options.labels) {
            labels = options.labels;
        }
        if (typeof value === "undefined" || value === "") {
            value = {};
            labels.forEach(element => {
                value[element] = {};
            });
        }
        labels.forEach(element => {
            internalState[element] = {};
            internalState[element].id = getIdFromLabel(element);
            internalState[element].address = "";
            internalState[element].fileName = "";
            internalState[element].fileSelected = false;
            internalState[element].error = false;
            internalState[element].files = [];
            internalState[element].completed = false;
            internalState[element].touched = false;
        });
        if (options && options.placeholder) {
            labels.forEach(element => {
                internalState[element].fileName = options.placeholder;
            });
        }
        if (typeof options.required === "undefined" || options.required === null || !options.required) {
            valid = true;
        }

        if (typeof value !== "undefined" && value !== null) {

            labels.forEach(element => {
                internalState[element].completed = typeof value[element] !== "undefined" && value[element] !== null && Object.keys(value[element]).length > 0;
                if (value[element] && value[element].fileName) {
                    internalState[element].fileName = value[element].fileName;
                }
            });
        }
        allSelected = checkAllSelected();
    });

    function checkAllSelected() {
        return true;
    }

    function getLabelFromId(id) {
        return id.substring(name.length + 1);
    }

    function getIdFromLabel(label) {
        return `${name}_${label}`;
    }

    function clear(label) {
        internalState[label] = {};
        internalState[label].id = getIdFromLabel(label);
        internalState[label].address = "";
        internalState[label].fileName = "";
        internalState[label].fileSelected = false;
        internalState[label].error = false;
        internalState[label].files = [];
        internalState[label].completed = false;
        value[label] = {};
    }

    function handleChange(event) {
        const labelId = getLabelFromId(event.target.id);
        if (options.confirmMessage) {
            confirmLabel = labelId;
            openModal(confirmModal);
        } else {
            options.getFile(options.context, labelId, getFileSuccess, getFileFail);
        }
    }

    function getFileSuccess(label, files) {
        resultGectCall = false;
        handleUpload(label, files);
    }

    function getFileFail(label) {
        resultGectCall = true;
        handleUpload(label);
    }


    function uploadSuccess(fileRemoteLinks) {
        uploadRunning = false;
        feedback = false;
        retryEnabled = false;
        //value = fileRemoteLinks; // stessa struttura file picker
        Object.keys(fileRemoteLinks).forEach(element => {
            internalState[element].error = false;
            value[element] = {};
            value[element].fileNameOnServer = fileRemoteLinks[element].fileName;
            value[element].fileName = fileRemoteLinks[element].fileName;
            value[element].type = fileRemoteLinks[element].type;
            value[element].error = false;
            internalState[element].completed = true;
            internalState[element].touched = true;
            //clear(element)
        });
        valid = labels.every((label) => internalState[label].completed);
        eventDispatcher("upload-done", {});
    }

    function uploadFail(labelId) {
        uploadRunning = false;
        if (typeof options.required != "undefined" && options.required != null && options.required) {
            valid = false;
        }
        clear(labelId);
        feedback = true;
        internalState[labelId].touched = true;
        eventDispatcher("upload-failed", {});
    }

    function handleUpload(label, file) {
        retryEnabled = false;
        //TO DO: integro chiamata alla gect e recupero del file, in caso di true apro la modale con errore else running
        //CALL GECT con errore:  resultGectCall===true
        if (resultGectCall) {
            //openFPModal({title: GECT_MODAL_FAILURE_TITLE, failureText: GECT_MODAL_FAILURE_TEXT, errorIntent: true});
            //uploadFail();
            return;
        } else {
            internalState[label].completed = false;
            uploadRunning = true;
            openFPModal({
                title: DEFAULT_MODAL_TITLE_SUCCESS,
                loadingText: DEFAULT_MODAL_LOADING_TEXT,
                successText: DEFAULT_MODAL_SUCCESS_TEXT,
                failureText: DEFAULT_MODAL_FAILURE_TEXT
            });
            eventDispatcher("upload-start", {});

            //struttura da mandare all'upload handlers:

            let files = {};
            files[label] = {
                "ext": "pdf",
                "file": file
            };
            options.handler(files, options.context, uploadSuccess, uploadFail); //chiamata uploadFile vera e propria, passare files
        }
    }


    function openFPModal(arg) {

        modalTitle = arg.title;
        modalLoadingText = arg.loadingText;
        modalSuccessText = arg.successText;
        modalFailureText = arg.failureText;
        modalErrorIntent = arg.errorIntent;
        openModal(uploadModal);
    }

    function confirmClose() {
        clear(confirmLabel);
        closeModal(confirmModal);
    }

    function confirmOK() {
        closeModal(confirmModal);
        options.getFile(options.context, confirmLabel, getFileSuccess, getFileFail);
    }

</script>
<div class="input-group input-group-fp">
    {#each labels as labelValue}
        <div class="fp-container">
            <div class="fp-item">
                <label class="fp-label fp-side">{labelValue}</label>
                <label class="fp-label fp-action">
                    {(internalState[labelValue].completed ? UPLOADED_TEXT : UPLOAD_TEXT)}
                </label>
                {#if options.handler}
                    {#if false}
                        <span class="circle-stepper-horizontal fp-uploaded-btn">
            <div class="circle-step" class:active={true} class:done={true}>
              <div class="circle-step-circle"/>
            </div>
          </span>
                    {:else}
                        <span class="fp-load-btn btn-file">
            <input
                    id={internalState[labelValue].id}
                    type="button"
                    {name}
                    on:click={handleChange}/>
          </span>
                    {/if}
                {/if}
            </div>
        </div>
        <div class="invalid-feedback"
             class:invalid-feedback-visible={!(internalState[labelValue].completed)&& options.required && (internalState[labelValue].touched || ignoreDirty) }>
            {validationMessage}
        </div>
    {/each}
</div>


<Modal bind:modalElement={uploadModal} title={modalTitle} closeButton={false}>
    {#if modalErrorIntent}
        <div class="mobile-modal-text">{modalFailureText}</div>
        <div class="mobile-modal-buttons">
            <a
                    href={undefined}
                    class="btn btn-yellow mobile-modal-button"
                    on:click={ ()=>{
                    closeModal(uploadModal);
                    }}>
                {"OK"}
            </a>
        </div>
    {:else}
        {#if uploadRunning}
            <div class="mobile-modal-text">{modalLoadingText}</div>
            <Loader/>
        {/if}
        {#if !uploadRunning}
            {#if !feedback}
                <div class="mobile-modal-text">{modalSuccessText}</div>
            {:else}
                <div class="mobile-modal-text">{modalFailureText}</div>
            {/if}
            <div class="mobile-modal-buttons">
                <a
                        href={undefined}
                        class="btn btn-yellow mobile-modal-button"
                        on:click={ ()=>{
                        closeModal(uploadModal);
                        }}>
                    {"OK"}
                </a>
            </div>
        {/if}
    {/if}
</Modal>

{#if options.confirmMessage}
    <!--// CONFIRM MODAL-->
    <Modal bind:modalElement={confirmModal} title={"Conferma"} closeButton={false}>
        <div class="modauth-text">{options.confirmMessage[confirmLabel]}</div>
        <div class="modauth-buttons">
            <Button name={'ANNULLA'} ordinality="2" size="2"
                    on:click={()=>confirmClose()}/>
            <Button name={'CONFERMA'} ordinality="1" size="2"
                    on:click={()=>confirmOK()}/>
        </div>
    </Modal>
{/if}

{#if options.debug}
    {#each labels as label}
        <div>{label} Complete: {internalState[label].completed}</div>
    {/each}
    <div>confirmMessage: {options.confirmMessage}</div>
    <div>valid: {valid}</div>
    <div>value: {JSON.stringify(value, null, 2)}</div>
{/if}
