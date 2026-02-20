<script>
    import Loader from "../../../suit/uikit/up/elements/Loader.svelte";
    import typosCaller from "../../../suit/cdm/typos"
    import sharedConfig from '../../api/sharedConfig';

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;

    import * as cdm from '../../../suit/cdm'

    let tCC = new typosCaller("/after-sales-ms/proxy/list", sharedConfig);

    tCC.setCache(typosCaller.TYPOS.ATECO, false);
    tCC._callLists("TIP_ATECO", {});
    tCC._callLists("TIP_ATECO", {});
    tCC.setCache(typosCaller.TYPOS.ATECO, true);
    tCC._callLists("TIP_ATECO", {});
    tCC._callLists("TIP_ATECO", {});
    tCC._callLists("TIP_ATECO", {});


    // EXAMPLE CDM USE
    /** @type {cdm.Customer} */
    let customer = {};
    customer.legalNature = cdm.legalNatureEnumCustomer.PF;
</script>

<div class='container'>
        <div class="center-loader">
            <div>
                <Loader/>
            </div>
        </div>
    <div style="margin-top: 80px;"></div>
</div>

<style>

    .container {
        min-height: 200px;
        height: calc(100vh - 15em);
    }

    h2{
        margin-top: 40px;
        text-align: center;
    }

</style>
