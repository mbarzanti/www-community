<script>

    import SearchBox from "../components/SearchBox.svelte";
    import TextField from "./TextField.svelte";
    import Datepicker from "../../../../suit/uikit/up/input/Datepicker.svelte";
    import SelectField from "../components/SelectField.svelte";
    import AddButton from "../components/AddButton.svelte";
    import * as componentsConfigurations from "./rowComponentsConfiguration";
    import {getCustomer} from "../../../api/customer";
    import {BUTTON_TYPE, CLASSIFICATION, ROLES} from "../../../commons/typos/definitions";
    import {STRINGS} from "../resources";
    import {createEventDispatcher, onMount} from "svelte";
    import bluebird from "bluebird";
    import {writable} from "svelte/store";
    import {legalForm} from "../stores";
    import {LEGAL_FORMS} from "../../../commons/typos/definitions";
    import {
        checkChannel,
        closeSpinner,
        convertDateToLocalDate,
        openAlertModal,
        openSpinner
    } from "../../../commons/utils";
    import {
        checkCervedAlreadyFailed,
        checkLegalRepresentative,
        checkLegalRepresentativeFromContractor
    } from "../checks/cerved";
    import Checkbox from "../../../../suit/uikit/up/input/Checkbox.svelte";

    export let index;
    export let valueMap = {};
    export let validMap = {};
    export let ignoreDirty;
    export let configuration = {type: CLASSIFICATION.RETAIL};
    export let removable = false;
    export let completeValueMap = {};
    const eventDispatcher = createEventDispatcher();

    let components = {};
    let ready = false;
    export let debug = true;
    let searched = false;

    let firstName = writable(undefined);
    let lastName = writable(undefined);
    let birthDate = writable(undefined);

    const customerPromise = bluebird.promisify(getCustomer);
    onMount(() => {
        switch (configuration.type) {
            case CLASSIFICATION.RETAIL:
                components.role = componentsConfigurations.roleSelectOption(true);
                components.search = componentsConfigurations.searchBoxRetailOption(configuration.disabled, {
                    firstName: firstName,
                    lastName: lastName,
                    birthDate: birthDate
                });
                components.firstName = componentsConfigurations.firstNameOption(!valueMap.prospect && configuration.disabled, firstName);
                components.lastName = componentsConfigurations.lastNameOption(!valueMap.prospect && configuration.disabled, lastName);
                components.birthDate = componentsConfigurations.birthDateOption(!valueMap.prospect && configuration.disabled, birthDate);
                components.owner = componentsConfigurations.checkBoxOwner(!valueMap.prospect && configuration.disabled);
                break;
            case CLASSIFICATION.BUSINESS:
                components.role = componentsConfigurations.roleSelectOption(true);
                components.search = componentsConfigurations.searchBoxBusinessOption(configuration.disabled);
                components.companyName = componentsConfigurations.companyNameOption(false);
                break;
        }
        ready = true;
    })

    function setProspect(prospect) {
        valueMap.prospect = prospect;
        switch (configuration.type) {
            case CLASSIFICATION.RETAIL:
                components.role = componentsConfigurations.roleSelectOption(true);
                components.search = componentsConfigurations.searchBoxRetailOption(configuration.disabled, {
                    firstName: firstName,
                    lastName: lastName,
                    birthDate: birthDate
                });
                components.firstName = componentsConfigurations.firstNameOption(configuration.disabled || !prospect, firstName);
                components.lastName = componentsConfigurations.lastNameOption(configuration.disabled || !prospect, lastName);
                components.birthDate = componentsConfigurations.birthDateOption(configuration.disabled || !prospect, birthDate);
                components.owner = componentsConfigurations.checkBoxOwner(configuration.disabled || !prospect);
                break;
            case CLASSIFICATION.BUSINESS:
                components.role = componentsConfigurations.roleSelectOption(true);
                components.search = componentsConfigurations.searchBoxBusinessOption(configuration.disabled);
                components.companyName = componentsConfigurations.companyNameOption(false);
                break;
        }
    }


    async function search(event) {
        if (valueMap.role === ROLES.CONTRAENTE && checkChannel("CC") && !checkCervedAlreadyFailed()) {
            if (!await checkLegalRepresentativeFromContractor(customerSearch, event, completeValueMap)) {
                valueMap.taxCode = "";
            } else {
                searched = true;
            }
        } else if (valueMap.role === ROLES.RICHIEDENTE && !checkCervedAlreadyFailed()) {
            if (!await checkLegalRepresentative(customerSearch, event, valueMap)) {
                valueMap.taxCode = "";
            } else {
                searched = true;
            }
        } else if (valueMap.role === ROLES.TITOLARE_DITTA && checkDoubleCustomer(event.detail.value)) {
            openAlertModal("Attenzione", "Non è possibile inserire due ruoli con lo stesso codice fiscale");
            valueMap.taxCode = "";
        } else {
            searched = true;
            await customerSearch(event);
        }
    }

    function checkDoubleCustomer(taxCode) {
        for (let i = 0; i < completeValueMap.length; i++) {
            let mapValues = completeValueMap[i];
            if (i !== index && (mapValues.role === ROLES.TITOLARE_DITTA || mapValues.role === ROLES.RICHIEDENTE)) {
                if (mapValues.taxCode === taxCode) {
                    return true;
                }
            }
        }
        return false;
    }

    async function customerSearch(event) {
        let customer;
        openSpinner(STRINGS.SPINNER.SEARCH);
        try {
            customer = await customerPromise(event.detail.value, configuration.type, valueMap.role);
        } catch (e) {
            closeSpinner();
            if (e.code) {
                switch (e.code) {
                    case 400:
                    case 500:
                    case 403:
                        openAlertModal(e.title, e.text);
                        valueMap.taxCode = "";
                        break;
                    case 404:
                        setProspect(true);
                }
            } else {
                setProspect(true);
            }
        }
        if (!customer) {
            return;
        }
        setProspect(false);
        switch (configuration.type) {
            case CLASSIFICATION.RETAIL:
                valueMap.taxCode = customer.taxData.taxCode;
                valueMap.firstName = customer.personInfo.firstName;
                valueMap.lastName = customer.personInfo.lastName;
                valueMap.birthDate = convertDateToLocalDate(customer.personInfo.birthDate)
                components.birthDate.options.reload = true;
                break;
            case CLASSIFICATION.BUSINESS:
                valueMap.taxCode = customer.taxData.vatNumber;
                valueMap.companyName = customer.legalInfo.companyName;
                break;
        }
        valueMap.customer = customer;
        closeSpinner();
    }

    function onRemoveHandler() {
        eventDispatcher('remove', {id: index});
    }

</script>
{#if ready}
    <div class="row product-conf-row jct ">
        <div class="col-sm-3 col-sm-3-reduced config-content">
            {#if components.role.options.disabled}
                <TextField bind:valid={validMap[components.role.name]} {ignoreDirty}
                             value={STRINGS.ROLES[valueMap[components.role.name]]} {...components.role} name="{index}-{components.role.name}"/>
            {:else}
                <SelectField bind:valid={validMap[components.role.name]} {ignoreDirty}
                             bind:value={valueMap[components.role.name]} {...components.role} name="{index}-{components.role.name}"/>
            {/if}
        </div>
        <div class="col-sm-3 col-sm-3-extended  config-content">
            <SearchBox bind:valid={validMap[components.search.name]} {ignoreDirty}
                       bind:value={valueMap[components.search.name]} {...components.search} name="{index}-{components.search.name}"
                       on:search={search}/>
        </div>
        {#if searched || configuration.disabled || (typeof valueMap.prospect != "undefined" && !valueMap.prospect)}
            {#if configuration.type === CLASSIFICATION.RETAIL}
                <div class="col-sm-2 config-content">
                    <div class="name-wrapper">
                        <TextField bind:valid={validMap[components.firstName.name]} {ignoreDirty}
                                   bind:value={valueMap[components.firstName.name]} {...components.firstName} name="{index}-{components.firstName.name}"/>
                        <TextField bind:valid={validMap[components.lastName.name]} {ignoreDirty}
                                   bind:value={valueMap[components.lastName.name]} {...components.lastName} name="{index}-{components.lastName.name}"/>
                    </div>
                </div>
                <div class="col-sm-2 config-content">
                    <Datepicker bind:valid={validMap[components.birthDate.name]} {ignoreDirty}
                                bind:value={valueMap[components.birthDate.name]} {...components.birthDate} name="{index}-{components.birthDate.name}"/>
                </div>
                {#if valueMap.role === ROLES.RICHIEDENTE && $legalForm.code!==LEGAL_FORMS.DI.code && $legalForm.code!==LEGAL_FORMS.LP.code}
                    <div class="col-sm-2 col-sm-2-reduced config-content" style="white-space: nowrap;padding-top: 7px;">
                        <Checkbox bind:valid={validMap[components.owner.name]} {ignoreDirty}
                                  bind:value={valueMap[components.owner.name]} {...components.owner} name="{index}-{components.owner.name}"/>
                    </div>
                {/if}
            {:else}
                <div class="col-sm-4 config-content">
                    <div class="name-wrapper">
                        <TextField bind:valid={validMap[components.companyName.name]} {ignoreDirty} style="max-width: none !important"
                                   bind:value={valueMap[components.companyName.name]} {...components.companyName} name="{index}-{components.companyName.name}"/>
                    </div>
                </div>
            {/if}
        {/if}
        {#if debug}
            <a class="abs-action btn-add"
               href="javascript:void(0)" tabindex="0" on:click={()=>console.log(valueMap)}><span class="label">Debug Row</span></a>
        {/if}
        {#if removable}
            <AddButton objectSelectId="test" type={BUTTON_TYPE.REMOVE} label={STRINGS.PAGES.ADD_ROLES.BUTTONS.REMOVE}
                       onClick={onRemoveHandler}/>
        {/if}
    </div>
{/if}