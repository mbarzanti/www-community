<script>
    export let insegna;
    export let id_sales_point;
    export let id_SIA_sales_point;
    export let site_type;
    export let site_specialization;
    export let ateco_code;
    export let ateco_description;
    export let email;
    export let mobile_number;
    export let address;
    export let credit_bank_iban;
    export let credit_bank_iban_holder;
    export let debit_bank_iban;
    export let debit_bank_iban_holder;
    export let sale_point;
    export let nextStateEnabled;
    export let appState = {};

    jQuery(document).ready(function(){
        jQuery(".card").parent().css("display", "flex")
    });

    if(!appState.globalContext.salesPoint) {
        appState.globalContext.salesPoint = {};
    }

    function handleSelection(sale_point_tmp, tmp_id_sales_point, e) {
        if(jQuery(jQuery(e)[0].currentTarget).hasClass("selected")){
            jQuery(jQuery(e)[0].currentTarget).removeClass("selected")
            nextStateEnabled = false;
            appState.globalContext.salesPoint.sales_point = {};
        }else{
            jQuery(".card").removeClass("selected")
            jQuery(jQuery(e)[0].currentTarget).addClass("selected")
            nextStateEnabled = true;
            appState.globalContext.salesPoint.sales_point = sale_point_tmp;
            appState.globalContext.salesPoint.id_sales_point = tmp_id_sales_point.id_sales_point;
        }
    }
</script>

<div class="{appState.globalContext.salesPoint ? (appState.globalContext.salesPoint.id_sales_point === id_sales_point ? 'selected' : '') : ''} card card-sales" on:click|preventDefault={eventone => handleSelection({sale_point}, {id_sales_point}, eventone)}>
    <p class="card-asset-title">{insegna} - {id_SIA_sales_point}</p>
    <div class="sales-content">
        <p class="card-asset-subtitle">
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Tipologia sede:</span>  {site_type}</span>
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Specializzazione sede:</span> {site_specialization}</span>
        </p>
        <p class="card-asset-subtitle">
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Codice ateco:</span>  {ateco_code}</span>
            <span class = "span-no-padding col-6"><span class="card-asset-subtitle-label">Descrizione ateco:</span> {ateco_description}</span>
        </p>
        <p class="card-asset-subtitle">
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Email:</span>  {email}</span>
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Numero di telefono mobile:</span> {mobile_number}</span>
        </p>
        <p class="card-asset-subtitle address-sales"><span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Indirizzo:</span> {address}</span></p>
        <p class="card-asset-subtitle-iban">
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Codice IBAN di credito:</span>  {credit_bank_iban}</span>
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Proprietario IBAN di credito:</span> {credit_bank_iban_holder}</span>
        </p>
        <p class="card-asset-subtitle-iban">
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Codice IBAN di debito:</span>  {debit_bank_iban}</span>
            <span class = "span-no-padding col-5"><span class="card-asset-subtitle-label">Proprietario IBAN di debito:</span> {debit_bank_iban_holder}</span>
        </p>
    </div>
    <p style="display: none" class="selected"></p>
</div>

<style>
    .card {
        position: relative;
        margin: 0.5em;
        border: 1px solid #eee;
        border-radius: 4px;
        box-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        min-height: 5em;
        display: grid;
        width: 100%;
    }

    .card-sales{
        padding-bottom: 15px;
    }

    .selected {
        border: 2px solid #0047bb;
    }

    .card::after {
        clear: both;
        display: block;
    }

/*    h2 {
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
        font-size: 20px;
        font-weight: bold;
        border-bottom: 1px solid #0047bb;
    }

    p.card-asset-subtitle{
        font-size: 11px;
    }

    p.card-asset-subtitle-iban{
        font-size: 11px;
    }

    span.card-asset-subtitle-label{
        font-weight: 501;
        padding-right: 5px;
    }

    span.span-no-padding{
        padding: 0px;
    }

    .address-sales{
        border-bottom: 1px solid #EBE9E9;
        margin-bottom: 5px;
    }
    .sales-content{
        margin-top: 7px;
    }
</style>
