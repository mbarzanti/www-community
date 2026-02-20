<script>
    import * as constants from './../../commons/constants';
    import { _ } from 'svelte-i18n';
    import VirtualList from '@sveltejs/svelte-virtual-list';
    import ListItemSales from '../../components/ui/ListItemSalesPoint.svelte';
    import {onMount} from 'svelte';
    import jsonPath from "jsonpath";

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;
    runAlternativeFlow;
    runAction;

    const currentStep = constants.SELECT_OP_STEP_ID;


    let jsonTmp = "";
    let items = [];
    onMount(() => {
        items = fillSales( (i, info) => {
            return {
                key: `_${i}`,
                insegna: info[i].insegna,
                id_sales_point: info[i].id_sales_point,
                id_SIA_sales_point: info[i].id_SIA_sales_point,
                site_type: info[i].site_type,
                site_specialization: info[i].site_specialization,
                ateco_code: info[i].ateco_code,
                ateco_description: info[i].ateco_description,
                email: info[i].email,
                mobile_number: info[i].mobile_number,
                address: info[i].address,
                credit_bank_iban: info[i].credit_bank_iban,
                credit_bank_iban_holder: info[i].credit_bank_iban_holder,
                debit_bank_iban: info[i].debit_bank_iban,
                debit_bank_iban_holder: info[i].debit_bank_iban_holder,
                sale_point: info[i].sale_point
            };
        });
        if(appState.globalContext.salesPoint && appState.globalContext.salesPoint.id_sales_point){
            nextStateEnabled = items.findIndex(item=>item.id_sales_point===appState.globalContext.salesPoint.id_sales_point) > -1;
        }
    });

    function fillSales(fn) {
        let i = 0;
        let info = [];
        let infoObject;
        let legalInfo = appState.globalContext.customers.contractor.legalInfo;
        let salesPoints = [];
        const asset = appState.globalContext.asset.assets.find((item)=>appState.globalContext.asset.idAsset.idAss === item.id);
        salesPoints = asset.assetShops
        salesPoints.forEach(assetShop => {

            let salesPoint;
            try {
                salesPoint = jsonPath.query(legalInfo, "$.salesPoints[?(@.id == \"" + assetShop.salesPoint.id + "\")]");
                let atecoCode = "", atecoDesc = "";
                let flag = false;
                salesPoint[0].businessActivities.forEach(businessActivity => {
                    if (flag === false) {
                        if (businessActivity.type.toUpperCase() === "ATECO") {
                            atecoCode = businessActivity.code;
                            atecoDesc = businessActivity.description;
                            flag = true;
                        }
                    }
                })
                infoObject = {
                    "insegna": salesPoint[0].signboard,
                    "id_sales_point": salesPoint[0].id,
                    "id_SIA_sales_point": assetShop.code,
                    "site_type": salesPoint[0].type.value,
                    "site_specialization": salesPoint[0].description.value,
                    "ateco_code": atecoCode,
                    "ateco_description": atecoDesc,
                    "email": salesPoint[0].emails[0].email,
                    "mobile_number": "" + salesPoint[0].phoneNumbers[0].countryCode.value + " " + salesPoint[0].phoneNumbers[0].areaCode + " " + salesPoint[0].phoneNumbers[0].number,
                    "address": "" + salesPoint[0].address.dug + " " + salesPoint[0].address.toponym + ", " + salesPoint[0].address.number + " - " + salesPoint[0].address.city.value + " - " + salesPoint[0].address.province.code + " - " + salesPoint[0].address.zipCode.value,
                    "credit_bank_iban": assetShop.creditBankAccount.ibans[0].iban,
                    "credit_bank_iban_holder": assetShop.creditBankAccount.accountHolder,
                    "debit_bank_iban": assetShop.debitBankAccount.ibans[0].iban,
                    "debit_bank_iban_holder": assetShop.debitBankAccount.accountHolder,
                    "sale_point": assetShop
                };
                info[i] = infoObject;
                ++i;
            }catch(e){
                console.error("Invalid sales point ", salesPoint, e);
            }
        })
        return Array(Object.keys(info).length).fill().map((_, i) => fn(i, info));
    }


    let start;
    let end;

</script>

    <div class='container' style="height: 65vh;">
        {#if items.length === 0}
                <h2 class="no-sales-point">{$_("default.noSalesPoint")}</h2>
        {:else}
            <VirtualList {items} bind:start bind:end let:item>
                <ListItemSales {...item} bind:nextStateEnabled bind:appState/>
            </VirtualList>
            <!-- <p>showing items {start}-{end}</p>-->
        {/if}
    </div>

<style>

    .container {
        min-height: 200px;
        height: calc(100vh - 15em);
    }

    h2.no-sales-point{
        margin-top: 40px;
        text-align: center;
    }

</style>
