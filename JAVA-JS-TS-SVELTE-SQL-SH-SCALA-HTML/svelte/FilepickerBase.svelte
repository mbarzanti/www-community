<style>
.btn-file {
  background-image: url("data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgaWQ9IklDT05TIiBzdHJva2U9Im5vbmUiIHN0cm9rZS13aWR0aD0iMSIgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48ZyBpZD0iQWxsZWdhdG8iPjxnIGlkPSJpY28tYWxsZWdhdG9AMngiIHN0cm9rZS13aWR0aD0iMSIgdHJhbnNmb3JtPSJtYXRyaXgoMCAtMSAtMSAwIDMxIDMwKSIgc3Ryb2tlPSIjMDA0N0JCIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiPjxwYXRoIGQ9Ik0xOS45NzggOC4zMDZMOC4yNzYgMjAuMDQ4Yy0yLjA2IDIuMDQxLTQuMDU3IDIuMDQxLTUuOTk0IDAtMS45MzctMi4wNC0xLjkzNy00LjEyMyAwLTYuMjQ4TDE0LjUyNiAxLjQ4MWMxLjA3LTEuMDkgMi4zMTgtLjg5MiAzLjc0Mi41OTQgMS40MjMgMS40ODYgMS41ODMgMi45MTcuNDc4IDQuMjkxTDguMjc2IDE2LjkxOGMtLjg0NS42OS0xLjY0Ni42OS0yLjQwNCAwLS43NTctLjY5LS43NTctMS40OTQgMC0yLjQxMmw2Ljk4LTYuOTk0IiBpZD0iUGF0aC0xMSIvPjwvZz48L2c+PC9nPjwvc3ZnPg==");
  background-repeat: no-repeat;
  background-position: right;
  cursor: pointer;
}

.is-invalid + span {
  background-image: none
}

span {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  overflow: hidden;
}

input {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
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
</style>

<script>
  export let name; // attributo name del input di tipo file
  export let value = null; // Valore del filepicker, conterra un oggetto di tipo File
  export let valid = true; // Valore booleano che indica se il filepicker e' in uno stato valido
  
  export let options = {
    disabled: false, // Se impostato a true diabilita il caricamento dei file
    required: false, // Indica se il campo e' obbligatorio
    maxFileSize: null, // Dimensione massima in byte del file da caricare
    accept: null // Array contente i tipi di file accettatu es: ['image/png', 'image/jpeg']
  };

  let fileName = null; // Nome del file da caricare
  let files = null; // Array contente  i file da caricare, sara sempre solo un file
  let accept = null; // Sringa contente l'attributo accept del input file es: accept="image/png, image/jpeg"

  let touched = false; // Valore booleano che indica se il componente e stato usato

  let errorMessage = null;

  $: if (files && files.length>0) {
    value = files[0];
    valid = validate();
  }
  
  const init = () => {
    if(options && options.accept){
      if(Array.isArray(options.accept)){
        for(let i=0; i<options.accept.length; i++){
          if(i===0){
            accept = '';
          }else{
            accept += ', ';
          }
          accept += options.accept[i];
        }
      }else if(typeof options.accept === 'string' && options.accept.length>0){
        accept = options.accept;
      }
    }

    if (options.required && options.required == true){
      valid = false;
    }
  }

  /**
   * Verifica che i dati inseriti nel file picker siano validi
   * @return valore booleano che indica se il campo e' valido
   */
  const validate = () => {
    errorMessage = null;
    if(options){
      if(options.maxFileSize && value && options.maxFileSize<value.size){
        errorMessage = "Il file supera le dimensioni massime consentite di "+(options.maxFileSize/1024/1024)+" MB";
        return false;
      }

      if (options.required && options.required == true && !value) {
        return false;
      }
    }

    return true;
  };

  const onChange = (event) => {
    if(event.target && event.target.files[0] && event.target.files[0].name){
      fileName = event.target.files[0].name;
    }
  }

  const onClick = () => {
    setTimeout(() => {
      touched = true;
    }, 600);
  };

  init();
</script>


<div class="input-group input-group-fp" on:click={onClick}>
  <input
    type="text"
    class="form-control"
    readonly
    value={fileName}
    class:is-invalid={!valid && touched} />

  <span class="input-group-btn ml-1 btn-file">
      <input
        type="file"
        {name}
        on:change={onChange}
        disabled={options && options.disabled}
        bind:value={value}
        bind:files={files} 
        {accept} />
  </span>
</div>

{#if errorMessage}
<div class="invalid-feedback" style="display:block">
    {errorMessage}
</div>
{/if}