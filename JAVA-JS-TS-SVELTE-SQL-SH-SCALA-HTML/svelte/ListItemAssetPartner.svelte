<script>
import { get } from "svelte/store";
import { nomiPartnersTecnologici, ibanCreditoPartner, ibanDebitoPartner, intestazioneIbanCreditoPartner, intestazioneIbanDebitoPartner, pagamentoCommissioni, partnershipAttiva, partnerTecnologico } from "../../pages/change-operations/store";


    export let productName;
    export let productCode;
    export let activationDate;
    export let status;
    export let endDate;
    export let nextStateEnabled;
    export let idAss;
    export let appState = {};

    jQuery(document).ready(function(){
        jQuery(".card").parent().css("display", "flex")
    });

    function handleSelection(idAssChange, prodCode, e) {
        let querySelector = "#"+idAssChange.idAss;
        let component = jQuery(querySelector);
        if(component.hasClass("selected")){
            nextStateEnabled = false;
            component.removeClass("selected")
            appState.globalContext.asset.idAsset = {};
            appState.globalContext.asset.productCode = {};
        }else{
            jQuery(".card").removeClass("selected")
            component.addClass("selected")
            nextStateEnabled = true;
            appState.globalContext.asset.idAsset = idAssChange;
            appState.globalContext.asset.productCode = prodCode.productCode;
        }
    }
    if(get(partnerTecnologico)){
        nextStateEnabled = true;
    }
</script>

<div id="{idAss}" class="{(appState.globalContext.asset.idAsset && appState.globalContext.asset.idAsset.idAss) ? (appState.globalContext.asset.idAsset.idAss === idAss ? 'selected' : '') : ''} card card-asset" on:click|preventDefault={eventone => handleSelection({idAss}, {productCode}, eventone)}>
    <p class="card-asset-title">{productName} - {productCode}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Stato Partnership:</span> {get(partnershipAttiva) ? 'Active' : 'Inactive'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Partner Tecnologico:</span> {get(partnerTecnologico) ? 'Si' : 'No'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Partner Tecnologici:</span> {get(nomiPartnersTecnologici) ? get(nomiPartnersTecnologici) : 'Non disponibile'} </p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Commissioni:</span> {get(pagamentoCommissioni) ? get(pagamentoCommissioni) : 'Non disponibile'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">IBAN Addebito:</span> {get(ibanDebitoPartner) ? get(ibanDebitoPartner) : 'Non disponibile'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Intestazione IBAN Addebito:</span> {get(intestazioneIbanDebitoPartner) ? get(intestazioneIbanDebitoPartner) : 'Non disponibile'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">IBAN Accredito:</span> {get(ibanCreditoPartner) ? get(ibanCreditoPartner) : 'Non disponibile'}</p>
    <p class="card-asset-subtitle"><span class="card-asset-subtitle-label">Intestazione IBAN Accredito:</span> {get(intestazioneIbanCreditoPartner) ? get(intestazioneIbanCreditoPartner) : 'Non disponibile'}</p>
    <p style="display: none" class="selected"></p>
</div>

<style>
    .card {
        position: relative;
        margin: 0.5em;
        padding: 0.5em;
        border: 1px solid #eee;
        border-radius: 4px;
        box-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        min-height: 5em;
        display: grid;
        width: 100%;
    }

    .card-asset{
        padding-bottom: 15px;
    }

    .selected {
        border: 2px solid #0047bb;
    }

    .card::after {
        clear: both;
        display: block;
    }

    /*h2 {
        margin: 0 0 0.5em 0;
        font-size: 16px;
    }*/

    p {
        margin: 0;
        font-size: 14px;
        display: flex;
        align-items: center;
    }

    p.card-asset-title{
        font-weight: bold;
    }

    p.card-asset-subtitle{
        font-size: 11px;
    }

    span.card-asset-subtitle-label{
        font-weight: bold;
        padding-right: 5px;
    }
</style>
