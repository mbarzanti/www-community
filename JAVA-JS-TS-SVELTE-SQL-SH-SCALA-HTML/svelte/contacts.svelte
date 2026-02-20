<script>

    import * as general from '../../components/templates/general';
    import * as contact from '../../components/templates/contact';
    import * as utils from  './../../commons/utils';
    import * as pageOrchestratorConstants from './../../components/orchestrator/constants';
    import * as localUtils from '../select-operation/utils';
    import Loader from "../../SvelteKit/elements/Loader.svelte";
    import AutoFormPageWizard from "../../SvelteKit/forms/AutoFormPageWizard.svelte";
    import {_} from "svelte-i18n";
    import {getItemByTypos, SANITY_AREA_CONTRACTOR, SANITY_AREAS} from "../../mapper/customer";
    import {TYPO_PHONE_LANDING} from "../../commons/constants";


    export let appState = {};
    export const nextStateEnabled = false;
    export const runAlternativeFlow = undefined;
    export let runAction;
    let formData = {};


    let promise = genSanityContactsUiModel(appState.context.sanityList)

    function genId(id, typos, fieldName) {
        return `${id}_${typos}_${fieldName}`;
    }

    function genContact(phoneNumber, role, rowDecorated=false) {

        const title = $_(`sanity.fields.phoneNumbers.${phoneNumber.type.code}`, { default: phoneNumber.type.value.charAt(0) + phoneNumber.type.value.substr(1).toLowerCase() });
        const mobile = phoneNumber.type.code !== TYPO_PHONE_LANDING;
        return [
            general.group(genId(role, phoneNumber.type.code, "title"), title, {subtitle: true, style:"padding-bottom:15px;", decorated: rowDecorated}),
            [
                contact.countryCode(genId(role, phoneNumber.type.code, "countryCode"), false, utils.getString("review.form.phone.countryCode"), {
                    default_value: phoneNumber.countryCode,
                    size: "xs"
                }),
                contact.areaCode(genId(role, phoneNumber.type.code, "areaCode"), mobile, false, utils.getString("review.form.phone.areaCode"), {
                    default_value: phoneNumber.areaCode ? phoneNumber.areaCode : "",
                    size: "xxs"
                }),
                contact.number(genId(role, phoneNumber.type.code, "phoneNumber"), mobile, false, utils.getString("review.form.phone.phoneNumber"), {
                    default_value: phoneNumber.number,
                    size: "md"
                })
            ]
        ];
    }


    function genSanityContactsAreaUiModel(sanityAreaCheckList, role) {
        let path = [];
        sanityAreaCheckList.forEach(
                (item) => {
                    console.log(role)
                    path = path.concat(genContact(item, role))
                }
        )
        return path;
    }

    async function genSanityContactsUiModel(sanityCheckList) {
        let path = [];
        let afterFirst= false;
        path.push([
            general.rowMetadata(false),
            general.textDescription( $_(`sanity.contacts.description`))
        ]);
        try {
            SANITY_AREAS.forEach(
                    (sanityArea) =>{
                        if(sanityCheckList[sanityArea].length > 0){
                            path.push(general.group(sanityArea, $_(`sanity.areas.${sanityArea}`), afterFirst ? {title:true, decorated:true} : {title:true, style:"padding-top:30px;", decorated:true}));
                            path = path.concat(genSanityContactsAreaUiModel(sanityCheckList[sanityArea], sanityArea));
                            afterFirst = true;
                        }
                    }
            )
        } catch (e) {
            console.error("Error encountered parsing customer data", e);
            runAction(pageOrchestratorConstants.GOTO_SERVICE_PAGE_ACTION_ID, {});
        }

        return path;
    }

    function onSubmit(e){
        const taskUiModel = utils.getValidJSON(formData).dummy.dummy;
        SANITY_AREAS.forEach(
                (sanityArea) =>{
                    appState.context.sanityList[sanityArea].forEach(
                            (item) => {
                                item.countryCode = taskUiModel[genId(sanityArea, item.type.code, "countryCode")];
                                item.areaCode = taskUiModel[genId(sanityArea, item.type.code, "areaCode")];
                                item.number = taskUiModel[genId(sanityArea, item.type.code, "phoneNumber")];
                            }
                    );
                }
        )
        if(appState.context.sanityList[SANITY_AREA_CONTRACTOR] && appState.globalContext.customers.reviewContractor){
            appState.context.sanityList[SANITY_AREA_CONTRACTOR].forEach(
                (item) => {
                    let phone = getItemByTypos(appState.globalContext.customers.reviewContractor.phoneNumbers, [item.type.code]);
                    if(phone){
                        phone.countryCode = item.countryCode;
                        phone.areaCode = item.areaCode;
                        phone.number = item.number;
                    }
                }
            );
        }
        runAction(pageOrchestratorConstants.CONTINUE_ACTION_ID, {});
    }

</script>

    {#await promise}
        <div class="center-loader">
            <div>
                <Loader/>
            </div>
        </div>

    {:then reviewUiModel}
        <AutoFormPageWizard
                title={'Richiesta Anticipazione Cassa Integrazione Guadagni'}
                flowRestricted={true}
                enableEditCtxBtn={false}
                externalButtons={false}
                externalStepper={true}
                externalConfirm={true}
                path={localUtils.dummyFormDescriptor(reviewUiModel)}
                bind:wizard_values_map={formData.wizard_values_map}
                bind:wizard_validation_map={formData.wizard_validation_map}
                bind:wizard_visibility_map={formData.wizard_visibility_map}
                submitted={false}
                readOnly={false}
                buttons={{submit:{label:"prosegui"}}}
                on:submit={onSubmit}
                on:complete={undefined} />
    {/await}
    <div style="margin-top: 80px;"></div>



<style>
    :global(input.input-text.form-control.custom-label) {
        border: 0;
        padding-left: 0;
        margin-top: -0.6rem;
        margin-bottom: 0.5rem!important;
    }

    :global(.form-group.title.form-row-decorated) {
        margin-bottom: 10px!important;
    }

    :global(.form-group.subtitle.form-row-decorated) {
        padding-bottom: 5px!important;
        margin-bottom: 5px!important;
    }

    :global(label.label.form-group-label) {
        padding-bottom: 0!important;
        /* margin-bottom: 0px!important; */
        /* border-spacing: 2rem!important; */
        /* margin-bottom: 5px!important; */
    }

</style>
