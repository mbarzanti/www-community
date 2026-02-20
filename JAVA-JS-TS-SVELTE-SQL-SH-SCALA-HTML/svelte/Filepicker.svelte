
<style>
  .btn-file {
    position: relative;
    overflow: hidden;
  }

  .btn-file input[type="file"] {
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

  .fp-side{
    font-size: 12px;
    font-weight: 500;
    flex-grow: 6;
    align-self: center;
    flex-basis: 2rem;
    margin-bottom: 0;
  }

  .fp-load-btn{
    width: 21px;
    height: 16px;
    transform: scale(1.1);
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .fp-action{
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
    width:auto;
    align-self: flex-end;
    -webkit-transform-origin-x: left;
    transform: scale(1.2) !important;
    margin-left: 0 !important;
  }

  .fp-re-upload{
    color: #0047bb;
    cursor: pointer;
  }



  .input-group-fp{
      flex-direction: column;
      align-items: flex-start;
  }

  .fp-retry{
    color: red;
    cursor: pointer;
  }
/*
  .fp-note{
    font-size: small;
    color: darkgray;
    font-style: italic;
    margin-bottom: 0.6rem;
    margin-left: 0.5rem;
    white-space: pre;
  }
*/
  .fp-note-tooltip{
    display: flex;
    flex-direction: row;
  }

  .circle-stepper-horizontal {
    align-self: center;
    margin-left: 0.5rem;
    transform: scale(1.5);
  }
    
  .circle-stepper-horizontal .circle-step {
    padding: 0;
  }

  .fp-container{
    display: flex;
    flex-direction: row;
    margin-bottom: .7rem;
    align-items: center;
  }
</style>


<script>
  import { createEventDispatcher, afterUpdate, onMount } from "svelte";
  import Button from "./Button.svelte";
  import Modal from "./../elements/Modal.svelte";
  import {closeModal, openModal} from './../sveltekit';
  import Loader from "../elements/Loader.svelte";
  const eventDispatcher = createEventDispatcher();

  export let name;
  export let value;
  
  let uploadModal;
  let tooltip;
  let tooltips = {};
  let uploadComplete = false;
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

  let retryEnabled = false;

  const DEFAULT_ACCEPTED_EXTENSIONS = ["pdf"];
  const DEFAULT_FILE_MAX_SIZE_MB = 4;

  const DEFAULT_MODAL_TITLE_SUCCESS = "Invio dei dati";
  const DEFAULT_MODAL_SUCCESS_TEXT = "Caricamento completato";
  const DEFAULT_MODAL_FAILURE_TEXT = "Caricamento fallito";
  const DEFAULT_MODAL_LOADING_TEXT = "Invio dei dati in corso";


  const DEFAULT_MODAL_TITLE_SIZE_ERROR = "File troppo grande";
  const DEFAULT_MODAL_ERROR_SIZE_TEXT = `Dimensione massima dei file: ${defaultFileMaxSizeMb}MB`;
  const DEFAULT_MODAL_TITLE_EXT_ERROR = "Estensione non valida";
  const DEFAULT_MODAL_ERROR_EXT_TEXT = `Estensioni valide: ${acceptedExtensions}`;


  const UPLOADED_TEXT = "CARICATO";
  const SELECTED_TEXT = "SELEZIONATO";
  const UPLOAD_TEXT = "CARICA";
  const RETRY_TEXT = "RIPROVA";
  const REUPLOAD_TEXT = "AVVIA";
  const DEFAULT_NOTE = `Dimensione massima allegati ${defaultFileMaxSizeMb}MB
           Estensioni ammesse ${acceptedExtensions}`;

  let note = DEFAULT_NOTE;
  let defaultFileMaxSizeMb = DEFAULT_FILE_MAX_SIZE_MB;
  let acceptedExtensions = DEFAULT_ACCEPTED_EXTENSIONS;
  let fileInputAcceptField = getAccepted();

  onMount(() => {
    if(options && options.defaultFileMaxSizeMb){
      defaultFileMaxSizeMb = options.defaultFileMaxSizeMb;
    }
    if(options && options.acceptedExtensions){
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
    if (options && options.placeholder) {
      labels.forEach(element => {
        internalState[element].fileName = options.placeholder;
      });
    }
    if (typeof options.required === "undefined" || options.required === null || !options.required) {
        valid = true;
    }

    if ( typeof value !== "undefined" && value !== null){
      const check = labels.every( 
        (label) => {
          return typeof value[label] !== "undefined" && value[label] !== null && Object.keys(value[label]).length > 0;
      });
      labels.forEach(element => {
        if( value[element] && value[element].fileName ){
          internalState[element].fileName = value[element].fileName;
        }
      });
        uploadComplete = check;
    }
    allSelected = checkAllSelected();
    setTimeout(()=>{
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

  function handleChange(event) {
    const id = event.target.id;
    if(internalState[id].files.length === 0) {
      internalState[id].fileSelected = false;
      return;
    }
    if( uploadComplete ){
      uploadComplete = false;
      if( allSelected ){
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

    if(allSelected){
      handleUpload()
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
      value[element].fileNameOnServer = value[element].fileName;
      value[element].fileName = internalState[element].fileName;
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
      if( !checkFileSize(arg) ) {
        openFPModal({ title:DEFAULT_MODAL_TITLE_SIZE_ERROR, failureText: getSizeErrorMessage(), errorIntent:true});
        uploadFail();
      } else if ( !checkFileExtensions(arg) ) {
        openFPModal({ title:DEFAULT_MODAL_TITLE_EXT_ERROR, failureText: getExtErrorMessage(), errorIntent:true});
        uploadFail();
      } else {
        uploadRunning = true;
        openFPModal({ title: DEFAULT_MODAL_TITLE_SUCCESS, loadingText: DEFAULT_MODAL_LOADING_TEXT, successText: DEFAULT_MODAL_SUCCESS_TEXT,failureText: DEFAULT_MODAL_FAILURE_TEXT});
        eventDispatcher("upload-start", {});
        options.handler(arg, options.context, uploadSuccess, uploadFail);
      }
    }
  }

  function checkFileSize(arg){
    return labels.every( (elem) => { return arg[elem].file.size <= defaultFileMaxSizeMb * 1024 * 1024 })
  }

  function checkFileExtensions(arg){
    return labels.every( (elem) => { return (acceptedExtensions.indexOf(arg[elem].ext.toLowerCase()) !== -1); })
  }

  function openFPModal(arg){

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

  function getSizeErrorMessage(){
    return `Dimensione massima dei file: ${defaultFileMaxSizeMb}MB`
  }

  function getExtErrorMessage(){
    return `Estensioni valide: ${getAccepted(true)}`
  }

  function getAccepted(pretty=false) {
    let accepted = "";
    acceptedExtensions.forEach(
            (value, index) => {
              if(index === 0) {
                accepted = `${pretty ? " " : "."}${value}`;
              } else {
                accepted = accepted + `,${pretty ? " " : "."}${value}`;
              }
            })
    return accepted
  }
  //".jpg,.jpeg,.png,.tiff"
  /*label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase()*/
</script>

<!--<label class="fp-label fp-side fp-note">{DEFAULT_NOTE}</label>-->
<div class="input-group input-group-fp">
  {#each labels as labelValue}
    <div class="fp-container">
    <div class="fp-item">
      <label class="fp-label fp-side">{internalState[labelValue].fileName.length > 0 ? internalState[labelValue].fileName: labelValue}</label>
      <label class="fp-label fp-action" class:fp-retry={internalState[labelValue].error} class:fp-re-upload={retryEnabled && !internalState[labelValue].fileSelected} on:click={(internalState[labelValue].error || (retryEnabled && !internalState[labelValue].fileSelected)) ? handleUpload : undefined}>
        {internalState[labelValue].error ? RETRY_TEXT : (uploadComplete ? UPLOADED_TEXT : (internalState[labelValue].fileSelected ? SELECTED_TEXT : ( retryEnabled ? REUPLOAD_TEXT: UPLOAD_TEXT) ) )}
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
                    id={labelValue}
                    type="file"
                    accept="{fileInputAcceptField}"
                    {name}
                    on:change={handleChange}
                    bind:value={internalState[labelValue].address}
                    bind:files={internalState[labelValue].files} />
          </span>
        {/if}
      {/if}
    </div>
    <span bind:this={tooltips[labelValue]} type="button" class="fp-note-tooltip" data-toggle="tooltip" data-placement="right" title={note}/>
    </div>
    <div class="invalid-feedback"
         class:invalid-feedback-visible={!(internalState[labelValue].completed)&& options.required && (internalState[labelValue].touched || ignoreDirty) }>
      {validationMessage}
    </div>
  {/each}
</div>

<Modal bind:modalElement={uploadModal} title={modalTitle} closeButton={false}>
    {#if modalErrorIntent}
      <div class="modauth-text">{modalFailureText}</div>
      <div class="modauth-buttons">
          <Button name={'Ok'} ordinality="1" size="2"
                  on:click={()=>closeModal(uploadModal)}/>
      </div>

    {:else}
      {#if uploadRunning}
          <div class="modauth-text">{modalLoadingText}</div>
          <Loader/>
      {/if}
      {#if !uploadRunning}
          {#if !feedback}
              <div class="modauth-text">{modalSuccessText}</div>
          {:else}
              <div class="modauth-text">{modalFailureText}</div>
          {/if}
          
          <div class="modauth-buttons">
              <Button name={'Ok'} ordinality="1" size="2"
                      on:click={()=>closeModal(uploadModal)}/>
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
