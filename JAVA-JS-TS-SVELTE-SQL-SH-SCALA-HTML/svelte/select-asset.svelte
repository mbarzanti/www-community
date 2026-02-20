<script>
    import * as constants from './../../commons/constants';
    import { _ } from 'svelte-i18n';
    import VirtualList from '@sveltejs/svelte-virtual-list';
    import ListItem from '../../components/ui/ListItemAsset.svelte';
    import {onMount} from 'svelte';
import { tipoDiRicerca } from '../change-operations/store';
import { get } from 'svelte/store';
import ListItemAssetPartner from '../../components/ui/ListItemAssetPartner.svelte';

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;
    runAlternativeFlow;
    runAction;

    let items = [];
    onMount(() => {
        if(get(tipoDiRicerca) === 'cliente') {
            items = fill( (i, info, res) => {
                let activationDate = info[i].activationDate;
                if(activationDate){
                    let aD = [];
                    aD = activationDate.split(' ');
                    console.log(aD);
                    activationDate = aD[0];
                }
                let endDate = info[i].endDate;
                if(endDate){
                    let eD = [];
                    eD = activationDate.split(' ');
                    activationDate = eD[0];
                }
                return {
                    key: `_${i}`,
                    productName: info[i].productName,
                    productCode: info[i].productCode,
                    activationDate: activationDate,
                    status: info[i].status,
                    endDate: endDate,
                    idAss: info[i].idAsset
                };
            });
        } else {
            items.push({
                productName: 'Codice PostePay',
                productCode: 'Codice_001',
                idAss: 1
            })
        }
        if(appState.globalContext.asset && appState.globalContext.asset.productCode && typeof appState.globalContext.asset.productCode === "string"){
            nextStateEnabled = true;
        }
    });

    function fill(fn) {
        return Array(Object.keys(appState.context.mappedAssets).length).fill().map((_, i) => fn(i, appState.context.mappedAssets));
    }

    let start;
    let end;

</script>

    <div class='container' style="height: 65vh;">
        {#if items.length === 0}
            <h2 class="no-asset">{$_("default.noAsset")}</h2>
        {:else}
            {#if get(tipoDiRicerca) === 'cliente'}
                <VirtualList {items} bind:start bind:end let:item>
                    <ListItem {...item} bind:nextStateEnabled bind:appState/>
                </VirtualList>
            {:else}
                <VirtualList {items} bind:start bind:end let:item>
                    <ListItemAssetPartner {...item} bind:nextStateEnabled bind:appState/>
                </VirtualList>
            {/if}
           <!-- <p>showing items {start}-{end}</p>-->
        {/if}
    </div>

<style>

    .container {
        min-height: 200px;
        height: calc(100vh - 15em);
    }

    h2.no-asset{
        margin-top: 40px;
        text-align: center;
    }

</style>
