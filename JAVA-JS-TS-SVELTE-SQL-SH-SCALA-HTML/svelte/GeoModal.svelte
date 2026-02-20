<script>
    import {afterUpdate, onMount} from 'svelte';
    import {postGeoPost} from "../api/geoPost";

    import {getUniqueId} from "../../suit/uikit/utils";
    import bluebird from "bluebird";
    import {closeSpinner, openSpinner} from "../commons/utils";

    export let showModal = false;
    export let id = getUniqueId();
    export let closeButton = true;
    export let buttonConfirmLabel = "CONFERMA";
    export let buttonCancelLabel = "ANNULLA";
    export let title;
    export let text;
    export let address;
    export let values_map;
    export let chosen = undefined;
    export let fallback;
    let show = false;
    let addresses;
    const postGeoPostPromise = bluebird.promisify(postGeoPost);
    let selectedAddress = null;
    let tooLongAddress = false;
    let disabled = false;
    let notFound = false;

    onMount(async () => {

    });

    async function testPromise() {
        try {
            addresses = null;
            selectedAddress = null;
            address.indirizzo.isAddress = true;
            tooLongAddress = false;
            disabled= false;
            notFound = false;
            let voidObject;
            let notFoundOrError = false, sameAddress = false;
            if(address.indirizzo.dug.length + address.indirizzo.toponimo.length + address.indirizzo.civico.length >= 33){
                tooLongAddress = true;
                disabled = true;
            }
            let response = await postGeoPostPromise(address);
            if(response.Esitonorm.Codice !== 1 && response.Esitonorm.Codice !== 2){
                if(response.Esitonorm.Codice === 3) {
                    response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.isOk = false;
                    notFound = true;
                    notFoundOrError = true;
                }else {
                    response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.isOk = false;
                    notFoundOrError = true;
                }
            }else if(response.Esitonorm.objIndirizzoNorm.length === 1){
                if(response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.codice !== ""){
                    response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.isOk = false;
                    notFoundOrError = true;
                }
            }
            /*if(response.Esitonorm.objIndirizzoNorm.length === 1) {
                if (response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.viaCompleta === "" && response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.comune === "" && response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.siglaProvincia === "") {
                    let description = response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.descrizione.toLowerCase();
                    response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.isOk = false;
                    response.Esitonorm.objIndirizzoNorm[0].indirizziNorm.descrizione = description.charAt(0).toUpperCase() + description.slice(1);
                    notFoundOrError = true;
                }
            }*/
            if(notFoundOrError === false){
                sameAddress = compareAddress(address.indirizzo, response.Esitonorm.objIndirizzoNorm);
            }
            if(sameAddress){
                chosen(address.indirizzo);
                closeSpinner();
                close();
            }else {
                voidObject = response.Esitonorm.objIndirizzoNorm;
                addresses = voidObject;
                selectedAddress = address.indirizzo;
                closeSpinner();
                jQuery("#" + id).modal('show');
                return addresses;
            }
        } catch (e) {
            console.error(e)
            chosen(address.indirizzo);
            closeSpinner();
            close();
            //throw e;
        }
    }

    function mapAddress(address){
        let newAddress = {};
        newAddress.dug = address.dug.replace( /\s\s+/g, ' ' );
        newAddress.number = address.civico.replace( /\s\s+/g, ' ' );
        newAddress.toponym = address.toponimo.replace( /\s\s+/g, ' ' );
        newAddress.zipCode =  {
            code: address.cap,
            value: address.cap
        };
        newAddress.city = {
            code: address.codiceBelfiore,
            value: address.comune
        };
        newAddress.province = {
            code: address.siglaProvincia,
            value: address.provincia
        };
        return newAddress;
    }

    function compareAddress(address, geoPostAddresses){
        let mappedAddress = mapAddress(address);
        return geoPostAddresses.some(addressRes => {
            let check = true;
            // CAP
            check = check && mappedAddress.zipCode.code.toLowerCase() === addressRes.indirizziNorm.cap.toLowerCase();
            // CITY
            check = check && mappedAddress.city.code.toLowerCase() === addressRes.indirizziNorm.codiceBelfiore.toLowerCase();
            // PROVINCE
            check = check && mappedAddress.province.code.toLowerCase() === addressRes.indirizziNorm.siglaProvincia.toLowerCase();
            // NUMBER
            check = check && mappedAddress.number.trim().toLowerCase() === addressRes.indirizziNorm.civico.toLowerCase();
            // TOPONYM
            const complDug = addressRes.indirizziNorm.complDug ? addressRes.indirizziNorm.complDug.trim().toLowerCase() + " " : "";
            const complDugAbbrev = addressRes.indirizziNorm.complDugAbbrev ? addressRes.indirizziNorm.complDugAbbrev.trim().toLowerCase() + " " : "";

            check = check && ( mappedAddress.toponym.trim().toLowerCase() === ( complDug + addressRes.indirizziNorm.nomeVia.toLowerCase())
                || mappedAddress.toponym.trim().toLowerCase() === ( complDugAbbrev + addressRes.indirizziNorm.nomeViaAbbrev.toLowerCase()) );
            // DUG
            check = check && ( mappedAddress.dug.trim().toLowerCase() === addressRes.indirizziNorm.dug.toLowerCase()
                || mappedAddress.dug.trim().toLowerCase() === addressRes.indirizziNorm.dugAbbrev.toLowerCase() );

            return check;
        });
    }

    afterUpdate(() => {
        if (showModal) {
            show = true;
        } else {
            jQuery("#" + id).modal('hide');
            show = false;
        }
    });

    function close() {
        showModal = false;
    }

    function cancel(){
        close();
        fallback();
    }

    $:
        if(show === true){
            openSpinner("Caricamento");
            testPromise();
        }

    $:{
        if (selectedAddress && !selectedAddress.isAddress) {
            disabled = false;
        }
    }

</script>

<div class="stylemodal-pit modal-locfinder modal fade"
     id="{id}" tabindex="-1" role="dialog" aria-labelledby="modalAuthLabel" aria-hidden="true"
     data-backdrop="static" data-keyboard={false}>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <div class="modhead">
                    {#if closeButton}
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" on:click={cancel}></button>
                    {/if}
                    <div class="modhead-title">{title}</div>
                </div>
                <div class="modal-padd">
                    <div class="d-flex flex-column justify-content-center align-items-center">
                        <div class="d-flex flex-column align-items-start fullLabelSize">
                            {#if address}
                                <p>Hai specificato il seguente indirizzo:</p>
                                <div>
                                    <label class="text-uppercase">
                                        <div class="modauth-text">
                                            {#if tooLongAddress}
                                                {address.indirizzo.dug} {address.indirizzo.toponimo}
                                                {#if address.indirizzo.civico}
                                                    {`, ${address.indirizzo.civico}`}
                                                {/if}
                                                {#if address.indirizzo.comune}
                                                    {` - ${address.indirizzo.cap} - ${address.indirizzo.comune} (${address.indirizzo.siglaProvincia})`}
                                                {/if}
                                            {:else}
                                                <input type="radio" name="inputAddress" value={address.indirizzo} bind:group={selectedAddress}>
                                                    {address.indirizzo.dug} {address.indirizzo.toponimo}
                                                    {#if address.indirizzo.civico}
                                                        {`, ${address.indirizzo.civico}`}
                                                    {/if}
                                                    {#if address.indirizzo.comune}
                                                        {` - ${address.indirizzo.cap} - ${address.indirizzo.comune} (${address.indirizzo.siglaProvincia})`}
                                                    {/if}
                                            {/if}
                                        </div>
                                    </label>
                                </div>
                            {/if}
                            {#if addresses}
                                {#each addresses as addressLoop, index}
                                    {#if addressLoop.indirizziNorm.isOk === false}
                                        {#if notFound}
                                            <p class="text-left">Non sono presenti indirizzi normalizzati.</p>
                                        {:else}
                                            <p class="text-left fullLabelSize">La normalizzazione non è andata a buon fine per un errore del servizio di verifica.</p>
                                        {/if}
                                    {:else}
                                        {#if index === 0}
                                            {#if tooLongAddress}
                                                <p class="text-left">L'indirizzo supera i 35 caratteri.</p>
                                                <p class="text-left fullLabelSize">Selezionare l'indirizzo corretto nella lista sottostante e premere "Salva".</p>
                                            {:else}
                                                <p class="text-left fullLabelSize">Selezionare l'indirizzo corretto nella lista sottostante altrimenti premere "Salva" per confermare il proprio inserimento.</p>
                                            {/if}
                                        {/if}
                                        {#if index < 8}
                                            <label class="text-uppercase">
                                                <input type="radio" name="normalizedAddress_{index}" value={addressLoop.indirizziNorm} bind:group={selectedAddress}>
                                                    {addressLoop.indirizziNorm.viaCompleta} - {addressLoop.indirizziNorm.cap} - {addressLoop.indirizziNorm.comune} ({addressLoop.indirizziNorm.siglaProvincia})
                                            </label>
                                        {/if}
                                    {/if}
                                {/each}
                            {/if}
                        </div>
                    </div>
                    <div class="d-flex flex-wrap justify-content-center mt-4">
                        <div class="modauth-buttons">
                            <a href="javascript:void(0)" class="btn btn-yellow" data-dismiss="modal" aria-label="Close" on:click={cancel}>Annulla</a>
                        </div>
                        <div class="modauth-buttons">
                            <a href="javascript:void(0)" class="btn btn-yellow" data-dismiss="modal" class:disabled={disabled} aria-label="Close" on:click={ ()=>{chosen(selectedAddress); close();}}>Salva</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .fullLabelSize{
        width: 100%;
    }
</style>