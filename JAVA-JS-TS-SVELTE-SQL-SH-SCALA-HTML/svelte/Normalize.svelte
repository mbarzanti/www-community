<script>
    import {_} from "svelte-i18n";
    import FormItem from "../../../ui/kit/mobileForms/FormItem.svelte";
    import FormRow from "../../../ui/kit/forms/FormRow.svelte";
    import TextField from "../../../ui/kit/forms/TextField.svelte";
    import Modal from "../../../ui/kit/mobileForms/Modal.svelte";
    import {closeModal} from "../../../ui/kit/sveltekit";
    import * as form from "./form";
    import CustomSelect from "./CustomSelect.svelte";
    import {SALES_POINT_ADDRESS_ID} from "../../../commons/constants/geoPost";

    export let appState;
    export let flowIndex = 0;
    export let nextStepFlowEnabled = false;

    let validationVector = {};
    let visibilityVector = {};
    let requiredVector = {};
    let topAddressList;
    let tooLongAddressTitle;
    let selectedAddressTitle;
    let confirmAddressTitle;

    let geopostData;
    let address;
    let addressId;
    let title;
    let addressTitle;
    let infoModal;

    let addresses;
    let selectedAddress = null;
    let tooLongAddress = false;
    let disabled = false;
    let notFound = false;
    let yes = false;
    let ready = false;

    let currentFlowIndex = -1;

    $: if (flowIndex !== currentFlowIndex) {
        currentFlowIndex = flowIndex;

        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
        initGeoPost();
        initStructure();
    }

    function initGeoPost() {
        addressId = appState.alternativeFlowContext.addressIdList[currentFlowIndex];
        geopostData = appState.alternativeFlowContext.geoPostData[addressId];
        address = appState.alternativeFlowContext.addresses[addressId];
        title = appState.alternativeFlowContext.title;

        addressTitle = $_("geoPost.address." + deleteSalesPointSequence(addressId));
        topAddressList = $_("geoPost.ui.topAddressList");
        tooLongAddressTitle = $_("geoPost.ui.tooLongAddress");
        selectedAddressTitle = $_("geoPost.ui.selectedAddress");
        confirmAddressTitle = $_("geoPost.address.confirmAddress");

        Object.keys(form).forEach(elem => {
            validationVector[form[elem].id] = false
        })
        Object.keys(form).forEach(elem => {
            requiredVector[form[elem].id] = form[form[elem].id].options.required
        })
        Object.keys(form).forEach(elem => {
            visibilityVector[form[elem].id] = true
        })

        appState.alternativeFlowContext.nextStepFlowEnabled = false;
        nextStepFlowEnabled = false;
        geoPostPopulate();
    }

    function deleteSalesPointSequence(addressId) {
        return addressId.includes(SALES_POINT_ADDRESS_ID)? SALES_POINT_ADDRESS_ID : addressId;
    }

    function geoPostPopulate() {
        try {
            address.isAddress = true;
            let voidObject;
            if(geopostData.notFound !== undefined){
                notFound = true;
            }
            if (address.dug.length + address.toponym.length + address.number.length >= 33) {
                tooLongAddress = true;
                disabled = true;
            }
            voidObject = geopostData.response.Esitonorm.objIndirizzoNorm;
            addresses = voidObject;
            selectedAddress = address.indirizzo;
            ready = true;
            return addresses;
        } catch (e) {
            console.error(e)
        }
    }

    function initStructure() {
        handleClickOtherAddress();
        jQuery('input:radio').prop("checked", false);
        syncSelected();
    }

    function handleClickOtherAddress() {
        let checkBox = jQuery(".geopost-sameaddress-checkbox");
        if (checkBox.prop('checked')) {
            checkBox.parent().removeClass("selected");
            checkBox.prop("checked", false);
            yes = false;
        }
    }

    function handleClickSameAddress() {
        let checkBox = jQuery(".geopost-sameaddress-checkbox");
        if (checkBox.prop('checked')) {
            jQuery('input:radio').prop("checked", false);
        }
        syncSelected();
    }

    function handleRadioClick(e){
        handleClickOtherAddress();
        let radio = jQuery(`#${e.target.id}`);
        let checked = e.target.checked;
        jQuery('input:radio').prop("checked", false);
        syncSelected();
        if(checked){
            radio.parent().addClass("selected");
            radio.prop('checked', true);
        }
    }

    function handleCheckBoxClick(e){
        let checkBox = jQuery(".geopost-sameaddress-checkbox");
        if(e.target.checked){
            checkBox.parent().addClass("selected");
            checkBox.prop('checked', true);
        } else {
            checkBox.parent().removeClass("selected");
            checkBox.prop('checked', false);
        }
    }

    function syncSelected(){
        let amount = addresses.length;
        for(let i=0; i<amount; i++){
            let radio = jQuery(`#normalizedAddress_${i}`);
            if(radio && radio.prop('checked')){
                radio.parent().addClass("selected");
            } else {
                radio.parent().removeClass("selected");
            }
        }

    }

    $:if (ready && (validationVector || visibilityVector)) {
        nextStepFlowEnabled = Object.keys(validationVector).every(
            (formId) => {
                if (!visibilityVector[formId]) {
                    return true;
                } else {
                    return validationVector[formId];
                }
            }
        ) && (yes || selectedAddress != undefined);
        appState.alternativeFlowContext.nextStepFlowEnabled = nextStepFlowEnabled;
        if(selectedAddress != undefined){
            appState.context.normalized[addressId] = selectedAddress;
        }
    }

    // UPDATE FIELDS VISIBILITY
    $:if (ready && !yes) {
        Object.keys(visibilityVector).forEach(
            formId => {
                visibilityVector[formId] = false
            }
        );
    } else if (ready){
        selectedAddress = undefined;
        Object.keys(visibilityVector).forEach(
            formId => {
                visibilityVector[formId] = true
            }
        );
    }



</script>

{#if appState.fail}
    <div class="error-message text-center">
        {appState.failMessage}
    </div>
{/if}
{#if ready}
    <div class="container pt20 pb20 geopost">
        <form class="generic-form bordered-elements">
            {#if title}
                <h3 class="area-heading">
                    {title}
                </h3>
            {/if}
            <div style="margin-left: -0.2rem!important;">
                <FormItem trasformNone={true} label={addressTitle} size={"lg"} visible={true} title={true} type={"label"}/>
            </div>
            <FormRow indent={false} decorated={false}>
                {#if addresses}
                    {#each addresses as addressLoop, index}
                        {#if index === 0}
                            <p class="text-left geopost-caption">{topAddressList}</p>
                        {/if}
                        {#if index < 8}
                            <label class="control-label text-uppercase col-xs-12 col-sm-12" style="padding-top: 0;">
                                <span class="custom-radio" on:click={handleRadioClick}>
                                     <input type="radio" name="normalizedAddress_{index}" id="normalizedAddress_{index}"  value={addressLoop.indirizziNorm} bind:group={selectedAddress} on:click={()=>{handleClickOtherAddress();}}>
                                </span>
                                {addressLoop.indirizziNorm.viaCompleta} - {addressLoop.indirizziNorm.cap} - {addressLoop.indirizziNorm.comune} ({addressLoop.indirizziNorm.siglaProvincia})
                            </label>
                            <!--<label class="text-uppercase col-xs-12 col-sm-12 custom-radio" >
                                <input type="radio" name="normalizedAddress_{index}" value={addressLoop.indirizziNorm} bind:group={selectedAddress} on:click={()=>{handleClickOtherAddress();}}>
                                {addressLoop.indirizziNorm.viaCompleta} - {addressLoop.indirizziNorm.cap} - {addressLoop.indirizziNorm.comune} ({addressLoop.indirizziNorm.siglaProvincia})
                            </label>-->
                        {/if}
                    {/each}
                {/if}
            </FormRow>
            <label class="checkbox-inline col-xs-12 col-sm-12 geopost-checkbox">
                <span class="custom-checkbox" on:click={handleCheckBoxClick}>
                    <input type="checkbox" name="inputAddress" class="geopost-sameaddress-checkbox" bind:checked={yes} on:click={()=>{handleClickSameAddress();}}>
                </span>
                {confirmAddressTitle}
            </label>
            <!--<div class="modauth-text col-xs-12 col-sm-12">
                <input type="checkbox" name="inputAddress" class="geopost-sameaddress-checkbox" bind:checked={yes} on:click={()=>{handleClickSameAddress();}}>
                <p style="display: inline-block;">{confirmAddressTitle}</p>
            </div>-->
            <div class="geopost-personal-address" class:hidden={!yes}>
                {#if tooLongAddress}
                    <p class="text-left">{tooLongAddressTitle}</p>
                {/if}
                <p class="text-left geoLabel">{selectedAddressTitle}</p>
                <FormRow indent={false} decorated={false}>
                    <FormItem trasformNone={true} size="col-xs-4 col-sm-3"  label={$_('form.address.dug')} visible={true} type={"label"}>
                        <TextField bind:value={address.dug}
                                   options={form.addresses_dug.options}
                                   bind:valid={validationVector[form.addresses_dug.id]}
                        />
                    </FormItem>
                    <FormItem trasformNone={true} size="col-xs-8 col-sm-6"  label={$_('form.address.toponym')} visible={true} type={"label"}>
                        <TextField bind:value={address.toponym}
                                   options={form.addresses_toponym.options}
                                   bind:valid={validationVector[form.addresses_toponym.id]}
                        />
                    </FormItem>
                    <FormItem trasformNone={true} size="col-xs-4 col-sm-3"  label={$_('form.address.number')} visible={true} type={"label"}>
                        <TextField bind:value={address.number}
                                   options={form.addresses_number.options}
                                   bind:valid={validationVector[form.addresses_number.id]}
                        />
                    </FormItem>

                    <FormItem trasformNone={true} size="col-xs-8 col-sm-3"  label={$_('form.address.province')} visible={true} type={"label"}>
                        <CustomSelect bind:value={address.province}
                                     options={form.addresses_province.options}
                                     values={form.addresses_province.values}
                                     bind:valid={validationVector[form.addresses_province.id]}
                        />
                    </FormItem>
                </FormRow>
                <FormRow indent={false} decorated={false}>
                    <FormItem trasformNone={true} size="col-xs-8 col-sm-6"  label={$_('form.address.city')} visible={true} type={"label"}>
                        <CustomSelect bind:value={address.city}
                                     options={form.addresses_city.options}
                                     values={form.addresses_city.values}
                                     bind:valid={validationVector[form.addresses_city.id]}
                        />
                    </FormItem>

                    <FormItem trasformNone={true} size="col-xs-4 col-sm-3"  label={$_('form.address.zipCode')} visible={true} type={"label"}>
                        <CustomSelect bind:value={address.zipCode}
                                     options={form.addresses_zipcode.options}
                                     values={form.addresses_zipcode.values}
                                     bind:valid={validationVector[form.addresses_zipcode.id]}
                        />
                    </FormItem>
                </FormRow>
            </div>
        </form>
    </div>
{/if}
<Modal bind:modalElement={infoModal}
       closeButton={true} title={$_('pages.fea.intro.modal.title')}>
    <div class="mobile-modal-text">{$_('pages.fea.intro.modal.body')}</div>
    <div class="mobile-modal-buttons">
        <a
                href="javascript:void(0)"
                class="btn btn-yellow mobile-modal-button"
                on:click={ ()=>{
                closeModal(infoModal);
                }}>
            {$_('pages.fea.intro.modal.ok')}
        </a>
    </div>
</Modal>
<style>

    .geoLabel {
        font-size: 18px;
        font-weight: 600;
        /* padding-left: 6px; */
        padding-left: 0px;
        margin-bottom: 20px;
        margin-top: 5px;
        color: #787878;
    }

    .geopost-caption{
        margin-top: 15px !important;
    }

    .geopost-checkbox{
        margin: 50px 0;
        margin-left: -10px;
    }

    .error-message {
        color: #dc3545;
        font-weight: bold;
        white-space: pre-wrap;
    }

    .intro-title {
        color: #222427;
        font-size: 24px;
        font-weight: 500;
        line-height: 36px;
        text-align: center;
        white-space: pre;
    }

    .intro-message {
        color: #222427;
        font-size: 17px;
        font-weight: 300;
        line-height: 23px;
        text-align: center;
        padding-left: .5rem;
        padding-right: .5rem;
    }

    .intro-link {
        color: #0047BB;
        font-size: 20px;
        font-weight: bold;
        line-height: 25px;
        text-align: right;
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    div.modauth-text {
        margin-left: 0px;
        padding-left: 0px;
    }

    @media (max-width: 767px) {

        .intro-message {
            color: #222427;
            font-size: 16px;
            font-weight: 300;
            line-height: 23px;
            text-align: center;
            letter-spacing: 0;
            padding-left: 10vw;
            padding-right: 5vw;
        }

        .intro-link{
            padding-left: 9vw;
            padding-right: 5vw;
        }
    }

    @media (max-width: 767px) {


        h3.area-heading {
            margin-left: 16vw;
            margin-right: 16vw;
            text-align: center;
        }

    }
    .geopost .bordered-elements.generic-form {
        min-height: 300px;
    }
</style>