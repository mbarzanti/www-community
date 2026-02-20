<script>

    import ConfirmModifyMailModal from "./ConfirmModifyMailModal.svelte";

    const jQuery = window.$;

    import SetScaduteModal from "./SetScaduteModal.svelte";
    import {createEventDispatcher, onMount} from "svelte";
    import {modifyMail, rejectCession, sendUserEmail} from "../../../api";
    import {newMail, showLoadingSpinner,} from "../../../stores";
    import AlertModal from "../../../UiKitLite/common/AlertModal.svelte";
    import {modifyUserEmail} from "../../../api";
    import Details from "./Details.svelte";
    import {marcaComeScadutaStorageUrls} from "../../../api";

    const dispatch = createEventDispatcher();

    export let data = [];
    export let value = null;
    let email = "";
    let emailVerify = "";
    let showErrorBox = false;
    let errorMessage = "";
    let showConfirmModifyEmailBox = false;

    function tornaIndietro() {
        dispatch("back");
    }

    function updateMail() {
        showLoadingSpinner.set(true);
        modifyMail(value.id, value.canale, email).then(resp => {
            newMail.set(email);
            dispatch("updateData");
        }).catch((error) => {
            ;
            errorMessage = "Errore durante la modifica della mail";
            showErrorBox = true;
        })
            .finally(() => {
                showLoadingSpinner.set(false);
            });
    }
    let btnCheck=false;
    
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

    onMount(() => {
        email = value.email;
    })

    function showConfirmModifyMailModal(flag) {
        showConfirmModifyEmailBox = flag;
    }


</script>

{#if showConfirmModifyEmailBox}
    <ConfirmModifyMailModal
            id="conferma-modifica-email"
            title="Conferma modifica Email"
            showModal="true"
            on:close={()=>showConfirmModifyMailModal(false)}
            on:modify={()=> updateMail()}>
    </ConfirmModifyMailModal>
{/if}

<div class="row">
    <div class="checkout spacer-xs-bottom-30">
        <div class="panel checkout-step" style="padding: 10px 30px 0px 30px;">

            <h3 style="font-weight: bold;">Modifica indirizzo email</h3>
            <p style="margin-bottom: 40px;">Scrivi la email che vuoi usare per la cessione del credito</p>

            <div class="column">
                <div id="accordionGroup" class="accordion-group" style="display:flex;">
                    <div style="display:flex; flex-direction: column;width:50%">
                        <p style="color:gray;font-weight: bold;font-size: 15px;margin:0px"><i class="glyphicon glyphicon-check" style="margin-right: 8px;"></i>NUOVA EMAIL</p>
                    <input type="email" class="form-control" style="width:80%;padding:0px!important" bind:value={email} on:change={() => radioCheck()}   onpaste="return false;" oncopy="return false;"></div>
                    <div style="display:flex;flex-direction: column;width:50%">
                        <p style="color:gray;font-weight: bold;font-size: 15px;margin:0px">VERIFICA E-MAIL</p>
                    <input type="email" class="form-control" style="width:80%;padding:0px!important" bind:value={emailVerify} on:change={() => radioCheck()}  onpaste="return false;" oncopy="return false;"></div>
    
                </div>
               <!--<p style="margin-top: 20px;">Riceverai un codice di conferma all'indirizzo email indicato</p>-->
    
    </div>
            
            
        </div>

    </div>

    <div style="text-align: right;">
        <button on:click={() => tornaIndietro()} class="btn btn-secondary " style="margin-right: 15px;" > ANNULLA </button>
        <button on:click={() => showConfirmModifyMailModal(true)} class="btn btn-primary" disabled={btnCheck===false}> CONFERMA </button>
    </div>

</div>

{#if showErrorBox}
    <AlertModal
            id="practice-submit-error"
            title="Errore"
            showModal="true"
            on:close={() => (showErrorBox = false)}
    >
        <p>{errorMessage}</p>
    </AlertModal>
{/if}
