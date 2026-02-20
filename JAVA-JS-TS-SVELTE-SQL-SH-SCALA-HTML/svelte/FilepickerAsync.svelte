<style>
  .btn-file {
    position: relative;
    overflow: hidden;
  }

  .btn-upload {
    display: inline-block;
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

  .fp-up-btn {
    display: flex;
    align-items: flex-end;
  }

  .filepicker-status.success {
    background: url(../images/ico-status-complete.png) center center no-repeat;
  }
  .filepicker-status.fail {
    background: url(../images/ico-status-missing.png) center center no-repeat;
  }

  .filepicker-status.run {
    margin: 5px;
    border: 2px solid #f3f3f3; /* Light grey */
    border-top: 2px solid #0047bb; /* Blue Poste*/
    border-radius: 50%;
    width: 24px;
    height: 24px;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
  }

  .filepicker-status {
      width: 30px;
      height: 100%;
      text-align: center;
      position: relative;
      line-height: 20px;
      border-radius: 0;
      color: #FFF;
      font-size: 10px;
      font-weight: 600;
      display: inline-block;
  }
</style>

<script>
  import { createEventDispatcher } from "svelte";
  import Button from "./Button.svelte";
  
  const eventDispatcher = createEventDispatcher();

  export let name; // attributo name del input di tipo file
  let value;
  
  export let options = {
    handler: null,
    url: null,
    method: "post"
  };

  let fileName = null; // Nome del file da caricare
  let files = null; // Array contente  i file da caricare, sara sempre solo un file

  let status = null; // null, "run", "success", "fail"
  
  let errorMsg = null; // Eventuale messaggio di errore 

  function handleChange(event) {
    if (status!=='run'){
      status = null;
      if(event.target && event.target.files[0] && event.target.files[0].name){
        fileName = event.target.files[0].name;
      }

      eventDispatcher("change", {});
    }
  }

  function uploadSuccess(fileRemoteLinks) {
    status = 'success';
    value = fileRemoteLinks;

    if(options && options.onSuccess){
      options.onSuccess();
    }

    eventDispatcher("upload-done", {});
  }

  function uploadFail(msg) {
    status = 'fail';
    if(msg){
      errorMsg = msg;
    }else{
      errorMsg = null;
    }

    if(options && options.onError){
      options.onError(msg);
    }

    eventDispatcher("upload-failed", {});
  }

  function handleUpload(event) {
    status = "run";
    eventDispatcher("upload-start", {});
    if(files && files[0]){
      if (options){
        if(options.maxFileSize && options.maxFileSize<files[0].size){
          uploadFail("Il file supera le dimensioni massime consentite: "+(files[0].size/1024/1024)+" MB");
        }else if (options.handler) {
          let arg = {};
          arg.file = files[0];
          arg.ext = fileName.split(".").pop();
          options.handler(arg, uploadSuccess, uploadFail);
        }else if (options.url){
          let arg = {};
          arg.file = files[0];
          arg.ext = fileName.split(".").pop();
          arg.url = options.url;
          arg.method = options.method?options.method:"post";

          defaultUploadHandler(arg, uploadSuccess, uploadFail);
        }
      }
    }else{
      uploadFail("Errore in fase di upload");
    }
  }

  /**
   * Funzione che gestisce l'upload dei file
   * @param {object} arg oggetto che contiene le informazioni sul file da caricare
   * @param {function} uploadSuccess funzione di callback invocata in caso di successo
   * @param {function} uploadFail funzione di callback invocata in caso di errore
   */
  function defaultUploadHandler(arg, uploadSuccess, uploadFail) {
    let url = arg.url;

    if (typeof url === 'function'){
      url = url();
    }

    if(!arg.file){
      uploadFail("Nessun file da caricare");
    }else{
      const formData = new FormData();
      formData.append('file', arg.file);
      fetch(url, {
        method: arg.method,
        body: formData,
      }).then(response => {
        if(response && response.status>=200 && response.status < 300){
          uploadSuccess();
        }else{
          uploadFail("Errore in fase di processamento del file");
        }
      }).catch(error => {
        uploadFail("Errore in fase di caricamento del file");
      });
    }
  }
</script>

<div class="input-group input-group-fp">
    <input
      type="text"
      class="form-control"
      readonly
      value={fileName}
      class:is-invalid={status==='fail'} />

    <span class="input-group-btn ml-1">
      <span class="btn btn-primary btn-file {status==='run'?'disabled':''}">
        Browse&hellip;
        <input
          type="file"
          {name}
          on:change={handleChange}
          disabled={status==='run'}
          bind:value={value}
          bind:files={files} />
      </span>
    </span>

    <span class="input-group-btn ml-1 fp-up-btn">
      <span class="btn-upload">
          <Button
          name="Upload"
          {options}
          disabled={status==='run'}
          on:click={handleUpload} />
      </span>
      {#if status==='success'}
      <span class="filepicker-status success" title="Caricamento completato"></span>
      {:else if status==='fail'}
      <span class="filepicker-status fail" title="Caricamento fallito - {errorMsg}"></span>
      {:else if status==='run'}
      <span class="filepicker-status run" title="Caricamento in corso"></span>
      {/if}
      
    </span>

</div>