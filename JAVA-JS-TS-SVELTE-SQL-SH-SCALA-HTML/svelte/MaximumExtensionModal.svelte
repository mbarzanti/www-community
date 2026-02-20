<script>
  import { afterUpdate, tick, onMount } from "svelte";
  import { jQuery } from "../../../UiKitLite/libs/jquery";
  import { pop, push } from "svelte-spa-router";
  import { plafondError, closeButton, companyData, showLoadingSpinner, hideDashboard } from "../../../stores";
  import { createEventDispatcher } from "svelte";


  const dispatch = createEventDispatcher();

  export let id;
  export let title;
  export let showModal;
  export let isModifica = false;
  export let sogliaReale;
  export let importoFuoriSoglia;
  export let sogliaEstesa;
  export let sogliaEstendibile = false;

  let email = "";
  let emailVerify = "";
  let btnCheck = false;
  let annullaButton = "";

  let disableBack = true;

  afterUpdate(async () => {
    if (showModal) {
      jQuery("#" + id).modal("show");
      disableBack = false;
    } else {
      jQuery("#" + id).modal("hide");
      await tick();
      dispatch("close");
      disableBack = true;
    }
  });

  async function closeModal() {
    showModal = false;
    await tick();
  }

  async function popModal() {
    closeModal();
    dispatch("pop");
  }

  function radioCheck() {
        if(checkMail(email) && email === emailVerify) {
            btnCheck = true;
        } else {
            btnCheck = false;
        }
    }

    function checkMail(email) {
        const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    }

    function sendEmail() {
      closeModal();
      dispatch("sendMail", {email: email});
    }

    onMount(() => {
      annullaButton = isModifica? "MODIFICA IMPORTO" : "ANNULLA";
    })

</script>

<div data-backdrop="static" class="modal fade" {id} tabindex="-1"  role="dialog" aria-labelledby={id}>
  <div class="modal-dialog modal-md modal-dialog-custom" style="width: 50%;">
    <div class="modal-content" style="padding-top: 20px;">
      <div class="modal-body">
        <div class="row">
          <div class="col-lg-12" style="text-align:center">
              <img class="logo-image-pi-medium"  src="assets/ico-reclami.png" srcset="assets/ico-reclami.png"
              alt="Poste Italiane" style="width:45px; margin:15px 0px 0px 0px"/>
                <h3 style="font-weight: bold;">Richiedi cessione</h3>
                <div class="row" style="padding: 0px 20px 20px">
                  {#if sogliaReale}
                    <p style="text-align: center;margin-left:20px">Siamo spiacenti, è stato raggiunto l'importo massimo da te cedibile per l'anno in corso che è di {sogliaReale} €.</p>
                  {/if}
                  {#if importoFuoriSoglia}
                    <p style="text-align: center;margin-left:20px">L’importo complessivamente ceduto eccede l'importo massimo per {importoFuoriSoglia} €.</p>
                    <p style="text-align: center;margin-left:20px">Scegli modifica importo per rimodulare gli importi già inseriti e completare la pratica in corso.</p>
                  {/if}
                  {#if sogliaEstendibile}
                    <p style="text-align: center;margin-left:20px">Se invece vuoi richiedere una valutazione per poter cedere nell'anno in corso fino a {sogliaEstesa} € indica l'indirizzo e-mail a cui ti verranno inviate le istruzioni e la documentazione da presentare.</p>
                    <p style="text-align: center;margin-left:20px">Si precisa che il mancato invio della documentazione richiesta entro i termini previsti o l’invio di documentazione errata o non conforme comporterà il rifiuto della richiesta di aumento dell’importo cedibile e l’impossibilità di effettuare successive cessioni a Poste Italiane.</p>
                    <br>
                  <div id="accordionGroup"  class="accordion-group" style="padding: 0px 0px 0px 20px;text-align: left;" >
                    <div style="display:flex; flex-direction: row;">
                      <div style="width:50%">
                      <p style="color:gray;font-weight: bold;margin:0px" >
                        <i class="glyphicon glyphicon-check" style="margin-right: 8px;font-size: 17px;" />EMAIL
                      </p>
                      <input  placeholder="Inserisci" type="email"  class="form-control"  style="width:90%;padding:0px!important;font-size: 17px;"  bind:value={email} on:change={() => radioCheck()} onpaste="return false;" oncopy="return false;" />
                      </div>
                      <div style="width:50%">
                        <p style="color:gray;font-weight: bold;margin:0px" >
                        <i class="glyphicon glyphicon-check" style="margin-right: 8px;font-size: 17px;" />CONFERMA EMAIL
                        </p>
                      <input placeholder="Inserisci" type="email" class="form-control" style="padding:0px!important;font-size: 17px;" bind:value={emailVerify} on:change={() => radioCheck()} onpaste="return false;" oncopy="return false;"/>
                    </div></div>
                    
                  </div>
                  {/if}
                  {#if !sogliaEstendibile}
                    <p>Per l'anno in corso hai raggiunto il massimo cedibile. Potrai procedere a nuove cessioni a partire dall'anno prossimo.</p>
                  {/if}
                  <!--<p style="margin-top: 20px;">Riceverai un codice di conferma all'indirizzo email indicato</p>-->
                  <br />
                </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-sm btn-secondary" on:click={closeModal}>{annullaButton}</button>
        {#if sogliaEstendibile}
        <button class="btn btn-primary" on:click={() => sendEmail()} disabled={btnCheck===false}>Invio</button>
        {/if}
      </div>
      <div class="modal-footer" style="text-align:left!important">
        <p>*Salvo la possibilità di Poste Italiane di modificare o aggiornare tale importo sulla base delle proprie valutazioni</p>
      </div>
    </div>
  </div>
</div>
