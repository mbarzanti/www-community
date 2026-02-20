<script>
    import AddRoleHeader from "../components/AddRoleHeader.svelte";
    import AddRolesAccordion from "../components/AddRolesAccordion.svelte";
    import AddRoleRow from "./AddRoleRow.svelte";
    import AddButton from "../components/AddButton.svelte";
    import {Contraente, legalForm, mainCustomer, Richiedente} from "../stores";

    import {BUTTON_TYPE, CLASSIFICATION, LEGAL_FORMS, ROLES} from "../../../commons/typos/definitions";

    import {STRINGS} from "../resources";
    import {onDestroy, onMount} from "svelte";
    import {get} from "svelte/store";
    import {prepopulationRowData} from "../../../commons/mainCustomer";

    const headerConfig = [
        {
            text: "Ruolo",
            col: "col-sm-3 col-sm-3-reduced"
        },
        {
            text: "Codice Fiscale / P.iva",
            col: "col-sm-3 col-sm-3-reduced"
        },
        {
            text: "Nome / cognome",
            col: "col-sm-2"
        },
        {
            text: "Data di nascita",
            col: "col-sm-2"
        }
    ];

    export let valueMap = [];
    export let validMap = [];
    export let valid;
    export let ignoreDirty;
    let rows = [];
    let addEnabled = false;
    export let debug = false;
    let amountRoles = 0;

    const MAX_TITOLARI = 10;

    $:{
        valid = validMap.every(
            (row)=>{
                let items = Object.keys(row);
                return items.every(key=>row[key]);
            }
        );
    }

    let legalFormUnsubscribe;

    onMount(async ()=>{
        let mainCustomerData = get(mainCustomer);
        //configureRows(get(legalForm), mainCustomerData);
        legalFormUnsubscribe = legalForm.subscribe(
            value => configureRows(value, mainCustomerData)
        )
    })

    onDestroy(
        ()=>{
            legalFormUnsubscribe();
        }
    )



    function configureRows(legalForm, mainCustomerData){
        let rowContraente = {};
        let rowRichiedente = {};
        amountRoles = 0;
        valueMap = [];
        validMap = [];
        rows = [];
        const byTaxCode = mainCustomerData.taxCode.length===16;
/*        let customerRichiedenteData = {};
        let customerContraenteData = {};

        customerRichiedenteData.customer = get(Richiedente);
        customerRichiedenteData.
        let customerRichiedenteData = get(Richiedente);
        const taxCode = customerRichiedenteData && customerRichiedenteData.taxData.taxCode;
        const firstName = customerRichiedenteData && customerRichiedenteData.personInfo.firstName;
        const lastName = customerRichiedenteData && customerRichiedenteData.personInfo.lastName;
        const birthDate = customerRichiedenteData && customerRichiedenteData.personInfo.birthDate && convertDateToLocalDate(customerRichiedenteData.personInfo.birthDate);*/

        let richiedenteRowData = prepopulationRowData(false,legalForm,Richiedente,mainCustomerData,byTaxCode);
        let contraenteRowData = prepopulationRowData(true,legalForm,Contraente,mainCustomerData,byTaxCode);
        switch (legalForm.code) {

            case LEGAL_FORMS.LP.code:
                contraenteRowData = prepopulationRowData(false,legalForm,Contraente,mainCustomerData,byTaxCode);
                valueMap.push({role: ROLES.CONTRAENTE,
                    firstName: richiedenteRowData.firstName,
                    lastName: richiedenteRowData.lastName,
                    birthDate: richiedenteRowData.birthDate,
                    taxCode: richiedenteRowData.taxCode,
                    customer: contraenteRowData.customer || richiedenteRowData.customer,
                    prospect: contraenteRowData.prospect && richiedenteRowData.prospect});
                rowContraente.configuration = { type: CLASSIFICATION.RETAIL, disabled: richiedenteRowData.disabled};
                rowContraente.removable = false;
                rows.push(rowContraente);
                amountRoles++;
                break;

            case LEGAL_FORMS.DI.code:
                valueMap.push({role: ROLES.CONTRAENTE,
                    companyName: contraenteRowData.companyName,
                    taxCode: contraenteRowData.vatNumber,
                    customer: contraenteRowData.customer,
                    prospect: contraenteRowData.prospect});
                rowContraente.configuration = { type: CLASSIFICATION.BUSINESS, disabled: contraenteRowData.disabled };

                /*if( customerContraenteData) {
                    valueMap.push({role: ROLES.CONTRAENTE,
                        companyName:customerContraenteData.legalInfo.companyName,
                        taxCode: customerContraenteData.taxData.vatNumber,
                        customer: customerContraenteData,
                        prospect: false});
                    rowContraente.configuration = { type: CLASSIFICATION.BUSINESS, disabled: true };
                } else {
                    valueMap.push({role: ROLES.CONTRAENTE,
                        taxCode: !byTaxCode ? mainCustomerData.taxCode: undefined,
                        prospect: true});
                    rowContraente.configuration = { type: CLASSIFICATION.BUSINESS, disabled: !byTaxCode };
                }*/
                rowContraente.removable = false;
                rows.push(rowContraente);
                amountRoles++;

                valueMap.push({role: ROLES.RICHIEDENTE,
                    firstName: richiedenteRowData.firstName,
                    lastName: richiedenteRowData.lastName,
                    birthDate: richiedenteRowData.birthDate,
                    taxCode: richiedenteRowData.taxCode,
                    customer: richiedenteRowData.customer,
                    prospect: richiedenteRowData.prospect});

                /*if(byTaxCode){
                    valueMap.push({role: ROLES.RICHIEDENTE,
                        firstName: mainCustomerData.name || firstName,
                        lastName: mainCustomerData.surname || lastName,
                        birthDate: mainCustomerData.dateOfBirth || birthDate,
                        taxCode: mainCustomerData.taxCode,
                        customer: customerRichiedenteData,
                        prospect: !customerRichiedenteData});
                } else {
                    valueMap.push({role: ROLES.RICHIEDENTE,
                        firstName: firstName,
                        lastName: lastName,
                        birthDate: birthDate,
                        taxCode: taxCode,
                        customer: customerRichiedenteData,
                        prospect: !customerRichiedenteData});
                }*/

                rowRichiedente.configuration = { type: CLASSIFICATION.RETAIL, disabled: richiedenteRowData.disabled };
                rowRichiedente.removable = false;
                rows.push(rowRichiedente);
                amountRoles++;
                break;

            default:
                rowContraente.configuration = { type: CLASSIFICATION.BUSINESS, disabled: contraenteRowData.disabled  };
                rowContraente.removable = false;
                rows.push(rowContraente);
                amountRoles++;
                valueMap.push({role: ROLES.CONTRAENTE,
                    companyName: contraenteRowData.companyName,
                    taxCode: contraenteRowData.vatNumber,
                    customer: contraenteRowData.customer,
                    prospect: contraenteRowData.prospect});

                valueMap.push({role: ROLES.RICHIEDENTE,
                    firstName: richiedenteRowData.firstName,
                    lastName: richiedenteRowData.lastName,
                    birthDate: richiedenteRowData.birthDate,
                    taxCode: richiedenteRowData.taxCode,
                    customer: richiedenteRowData.customer,
                    prospect: richiedenteRowData.prospect});
              /*
                if(byTaxCode){
                    valueMap.push({role: ROLES.CONTRAENTE});
                    valueMap.push({role: ROLES.RICHIEDENTE,
                        firstName:mainCustomerData.name,
                        lastName:mainCustomerData.surname,
                        birthDate:mainCustomerData.dateOfBirth,
                        taxCode: mainCustomerData.taxCode,
                        customer:get(Richiedente), prospect: !get(Richiedente)});
                } else {
                    valueMap.push({role: ROLES.CONTRAENTE,
                        taxCode: mainCustomerData.taxCode,
                        customer:get(Richiedente),
                        prospect: !get(Richiedente)});
                    valueMap.push({role: ROLES.RICHIEDENTE});
                }*/

                rowRichiedente.configuration = { type: CLASSIFICATION.RETAIL, disabled: richiedenteRowData.disabled };
                rowRichiedente.removable = false;
                rows.push(rowRichiedente);
                amountRoles++;


                let rowTitolare = {};
                valueMap.push({role: ROLES.TITOLARE_DITTA});
                rowTitolare.configuration = { type: CLASSIFICATION.RETAIL };
                rowTitolare.removable = true;
                rows.push(rowTitolare);
                amountRoles++;
                addEnabled = true;
                break;
        }
        rows = rows;
    }





    async function removeHandler(event){
        const index = event.detail.id;
        rows.splice(index,1);
        valueMap.splice(index,1);
        validMap.splice(index,1);
        amountRoles--;
        rows = rows;
        valueMap = valueMap;
        validMap = validMap;
    }

    function addHandler(){
        valueMap.push({role: ROLES.TITOLARE_DITTA})
        let row = {};
        row.configuration = { type: CLASSIFICATION.RETAIL };
        row.removable = true;
        ignoreDirty = false;
        rows.push(row);
        amountRoles++;
        rows = rows;
    }



</script>
<AddRolesAccordion
        productDisplayName="Codice Postepay"
        productIndex="1"
        productsAmount="1"
        css="padding-bottom:160px;">

    <div class="product-configurator">

        <AddRoleHeader {headerConfig}/>
        {#each rows as row, index}
            <AddRoleRow {index} bind:completeValueMap={valueMap} bind:valueMap={valueMap[index]} bind:validMap={validMap[index]} {ignoreDirty} {...row} {debug} on:remove={removeHandler}/>
        {/each}
        {#if addEnabled && amountRoles < 2 + MAX_TITOLARI}
            <div class="add-row">
                <AddButton objectSelectId="test" type={BUTTON_TYPE.ADD} label={STRINGS.PAGES.ADD_ROLES.BUTTONS.ADD} onClick={addHandler}/>
            </div>
        {/if}
    </div>

</AddRolesAccordion>