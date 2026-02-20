<script>
    import {createEventDispatcher, onMount} from "svelte";
    import Modal from "./Modal.svelte";
    import {closeModal, openModal} from '../sveltekit';
    import Loader from "../elements/Loader.svelte";
    import * as PhotoSwipeHelper from "../../photoSwipeHelper";
    import {downloadFile} from "../../../api/download";
    import api from "../../../api";

    const eventDispatcher = createEventDispatcher();

    export let name;
    export let value;

    let uploadModal;
    let tooltip;
    let tooltips = {};
    export let uploadComplete = false;

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

    let retryEnabled = false;
    let lastEventForm;

    const DEFAULT_ACCEPTED_EXTENSIONS = ["pdf"];

    const DEFAULT_FILE_MAX_SIZE_MB = 2.4414;

    const DEFAULT_MODAL_TITLE_SUCCESS = "Invio dei dati";
    const DEFAULT_MODAL_SUCCESS_TEXT = "Caricamento completato";
    const DEFAULT_MODAL_FAILURE_TEXT = "Caricamento fallito";
    const DEFAULT_MODAL_LOADING_TEXT = "Invio dei dati in corso";


    const DEFAULT_MODAL_TITLE_SIZE_ERROR = "File troppo grande";
    const DEFAULT_MODAL_ERROR_SIZE_TEXT = `Dimensione massima dei file: ${defaultFileMaxSizeMb}MB`;
    const DEFAULT_MODAL_TITLE_EXT_ERROR = "Estensione non valida";
    const DEFAULT_MODAL_ERROR_EXT_TEXT = `Estensioni valide: ${acceptedExtensions}`;

    const DEFAULT_MODAL_TITLE_PREVIEW_ERROR = "Errore imprevisto";
    const DEFAULT_MODAL_ERROR_PREVIEW_TEXT = "Scaricamento del documento fallito";


    const UPLOADED_TEXT = "CARICATO".toLowerCase();
    const SELECTED_TEXT = "SELEZIONATO".toLowerCase();
    const UPLOAD_TEXT = "CARICA".toLowerCase();
    const RETRY_TEXT = "RIPROVA".toLowerCase();
    const REUPLOAD_TEXT = "AVVIA".toLowerCase();
    const DEFAULT_NOTE = `Il file non deve superare i ${DEFAULT_FILE_MAX_SIZE_MB}MB di peso.
Sono consentiti i formati .pdf`;

    let note = DEFAULT_NOTE;
    let defaultFileMaxSizeMb = DEFAULT_FILE_MAX_SIZE_MB;
    let acceptedExtensions = DEFAULT_ACCEPTED_EXTENSIONS;
    let fileInputAcceptField = getAccepted();

    let dynamicElementSource = [];
    onMount(() => {

        if (options && options.defaultFileMaxSizeMb) {
            defaultFileMaxSizeMb = options.defaultFileMaxSizeMb;
        }
        if (options && options.acceptedExtensions) {
            acceptedExtensions = options.acceptedExtensions;
        }
        fileInputAcceptField = getAccepted();
        note = getNote();
        if (options.labels) {
            labels = options.labels;
        }
        if (typeof value === "undefined" || value === "") {
            value = {};
            labels.forEach(element => {
                value[element] = "";
            });
        }
        labels.forEach(element => {
            internalState[element] = {};
            internalState[element].address = "";
            internalState[element].fileName = "";
            internalState[element].fileSelected = false;
            internalState[element].error = false;
            internalState[element].files = [];
        });
        if (typeof options.required === "undefined" || options.required === null || !options.required) {
            valid = true;
        }
        allSelected = checkAllSelected();
        setTimeout(() => {
            labels.forEach(element => {
                jQuery(tooltips[element]).tooltip({
                    trigger: 'hover', sanitize: false, sanitizeFn: content => content
                });
            });
        }, 400)
    });

    function checkAllSelected() {
        let test = true;
        labels.forEach(element => {
            test = test && internalState[element].fileSelected && !internalState[element].error;
        });
        return test;
    }

    function clear(label) {
        internalState[label] = {};
        //internalState[label].id = getIdFromLabel(label);
        internalState[label].address = "";
        internalState[label].fileName = "";
        internalState[label].fileSelected = false;
        internalState[label].error = false;
        internalState[label].files = [];
        //internalState[label].completed = false;
        value[label] = {};
        //jQuery(getIdFromLabel(label)).reset();
        try {
            let pointer = jQuery(lastEventForm);
            pointer.wrap('<form>').closest('form').get(0).reset();
            pointer.unwrap();
        } catch (e) {
        }
    }

    function handleChange(event) {
        const id = event.target.id;

        lastEventForm = event.target;
        console.log(internalState[id].files)
        if (internalState[id].files.length === 0) {
            internalState[id].fileSelected = false;
            return;
        }
        if (!checkFileSize(id)) {
            openFPModal({
                title: DEFAULT_MODAL_TITLE_SIZE_ERROR,
                failureText: getErrorMessageSize(),
                errorIntent: true
            });
            clear(id);
        } else if (!checkFileExtensions(id)) {
            openFPModal({
                title: DEFAULT_MODAL_TITLE_EXT_ERROR,
                failureText: getErrorMessageExt(),
                errorIntent: true
            });
            clear(id);
        } else {
            if (uploadComplete) {
                uploadComplete = false;
                if (allSelected) {
                    retryEnabled = true;
                    labels.forEach(element => {
                        internalState[element].fileSelected = false;
                    });
                } else {
                    labels.forEach(element => {
                        internalState[element].fileName = element;
                    });
                }
            }
            if (typeof options.required != "undefined" && options.required != null && options.required) {
                valid = false;
            }
            internalState[id].fileSelected = true;
            internalState[id].error = false;
            internalState[id].fileName = internalState[id].address
                .replace(/\\/g, "/")
                .replace(/.*\//, "");

            allSelected = checkAllSelected();
            eventDispatcher("change", {});

            if (allSelected) {
                handleUpload()
            }
        }
    }

    function uploadSuccess(fileRemoteLinks) {
        valid = true;
        uploadRunning = false;
        uploadComplete = true;
        feedback = false;
        retryEnabled = false;
        value = fileRemoteLinks; // stessa struttura file picker
        labels.forEach(element => {
            internalState[element].error = false;
            try{
                value[element].fileName = internalState[element].fileName;
            } catch (e) {}
        });
        eventDispatcher("upload-done", {});
    }

    function uploadFail() {
        uploadRunning = false;
        if (typeof options.required != "undefined" && options.required != null && options.required) {
            valid = false;
        }
        feedback = true;
        uploadComplete = false;
        labels.forEach(element => {
            internalState[element].error = true;
        });
        eventDispatcher("upload-failed", {});
    }

    function handleUpload() {
        retryEnabled = false;
        if (options && options.handler) {
            if (typeof options.required != "undefined" && options.required != null && options.required) {
                valid = false;
            }
            feedback = false;
            uploadComplete = false;
            let arg = {};
            labels.forEach(element => {
                arg[element] = {};
                arg[element].file = internalState[element].files[0];
                const splitted = internalState[element].fileName.split(".")
                arg[element].ext = splitted.length > 1 ? splitted.pop() : "";
            });

            uploadRunning = true;
            openFPModal({
                title: DEFAULT_MODAL_TITLE_SUCCESS,
                loadingText: DEFAULT_MODAL_LOADING_TEXT,
                successText: DEFAULT_MODAL_SUCCESS_TEXT,
                failureText: DEFAULT_MODAL_FAILURE_TEXT
            });
            eventDispatcher("upload-start", {});
            options.handler(arg, options.context, uploadSuccess, uploadFail);

        }
    }

    function checkFileSize(labelId) {
        return internalState[labelId].files[0].size <= defaultFileMaxSizeMb * 1024 * 1024;
    }

    function checkFileExtensions(labelId) {
        const splitted = internalState[labelId].address
            .replace(/\\/g, "/")
            .replace(/.*\//, "").split(".");
        const ext = splitted.length > 1 ? splitted.pop() : "";
        return (acceptedExtensions.indexOf(ext.toLowerCase()) !== -1);
    }

    function failureCallback() {
        openFPModal({
            title: DEFAULT_MODAL_TITLE_PREVIEW_ERROR,
            failureText: DEFAULT_MODAL_ERROR_PREVIEW_TEXT,
            errorIntent: true
        });
    }

    function openImage(event, label) {
        if (options.previewHandler) {
            const successCallback = (links, fileName = "") => {
                if(canPreviewImage()){
                    let items = [
                        {
                            src: links.front.url,
                            title: "Fronte",
                            w: 0,
                            h: 0
                        },
                        {
                            src: links.back.url,
                            title: "Retro",
                            w: 0,
                            h: 0
                        }];
                    setTimeout(() => {
                        try {
                            PhotoSwipeHelper.show(items)
                        } catch (e) {
                            console.error(e)
                            failureCallback();
                        }
                    }, 50)
                } else {
                    api.downloadV2.downloadFile(links.front.url, fileName || internalState[label].fileName || options.filename, {notAttachment: true});
                }
            }
            options.previewHandler(label, options.context, successCallback, failureCallback)
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

    function getNote() {
        return `Dimensione massima allegati ${defaultFileMaxSizeMb}MB
           Estensioni ammesse ${getAccepted(true)}`;
    }

    function getErrorMessageSize() {
        return `Dimensione massima dei file: ${defaultFileMaxSizeMb}MB`
    }

    function getErrorMessageExt() {
        return `Estensioni valide: ${getAccepted(true)}`
    }

    function getAccepted(pretty = false) {
        let accepted = "";
        acceptedExtensions.forEach(
            (value, index) => {
                if (index === 0) {
                    accepted = `${pretty ? " " : "."}${value}`;
                } else {
                    accepted = accepted + `,${pretty ? " " : "."}${value}`;
                }
            })
        return accepted
    }

    function canPreviewImage(){
        let defaultPreviewEnabled = false;
        let valueIndex = Object.keys(value).findIndex(element =>
            value[element] && value[element]["minio-url"] && value[element]["minio-url"].length > 0
        )
        if(valueIndex >= 0 ){
            let valueItem = value[Object.keys(value)[valueIndex]];
            if(valueItem && valueItem["minio-url"]){
                const ext = valueItem["minio-url"].split(".").slice(-1)[0];
                defaultPreviewEnabled = ["png", "jpg", "jpeg", "tiff"].includes(ext);
            }
        }
        return defaultPreviewEnabled;
    }
</script>

<label class="fp-label fp-side fp-note">{note}</label>
<div class="input-group-fp">
    {#each labels as label}
        <div class="fp-container">
            <div class="fp-item">
                <label class="fp-label fp-side">{internalState[label].fileName.length > 0 ? internalState[label].fileName : label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase()}</label>

                <label class="fp-label fp-action" class:fp-retry={internalState[label].error}
                       class:fp-re-upload={retryEnabled && !internalState[label].fileSelected}
                       on:click={(internalState[label].error || (retryEnabled && !internalState[label].fileSelected)) ? handleUpload : undefined}>
                    {internalState[label].error ? RETRY_TEXT : (uploadComplete ? UPLOADED_TEXT : (internalState[label].fileSelected ? SELECTED_TEXT : (retryEnabled ? REUPLOAD_TEXT : UPLOAD_TEXT)))}
                </label>
                {#if uploadComplete && options.previewHandler}
                    <a href={dynamicElementSource} class="btn-file {canPreviewImage() ? 'fp-view-btn' : 'fp-download-btn'}"
                       on:click|preventDefault={ (event)=>{ openImage(event, label) }}>
                    </a>
                {/if}
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
                    id={label}
                    type="file"
                    accept="{fileInputAcceptField}"
                    {name}
                    on:change={handleChange}
                    bind:value={internalState[label].address}
                    bind:files={internalState[label].files}/>
          </span>
                    {/if}
                {/if}
            </div>
            <!--<span bind:this={tooltips[label]} type="button" class="fp-note-tooltip" data-toggle="tooltip" data-placement="right" title={DEFAULT_NOTE}/>-->
        </div>
    {/each}
</div>


<Modal bind:modalElement={uploadModal} title={modalTitle} closeButton={false}>
    <div class="mobile-modal-title">{modalTitle}</div>
    {#if modalErrorIntent}
        <div class="mobile-modal-text">{modalFailureText}</div>
        <div class="mobile-modal-buttons">
            <a
                    href="javascript:void(0)"
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
                        href="javascript:void(0)"
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

{#if options.debug}
    <div>value: {value}</div>
    <div>address: {address}</div>
    <div>fileName: {fileName}</div>
    <div>files: {files}</div>
    <div>valid: {valid}</div>
    <div>feedback: {feedback}</div>
{/if}
