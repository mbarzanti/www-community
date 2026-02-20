<script>
    import * as constants from './../../commons/constants';
    import {_} from 'svelte-i18n';
    import AutoFormPageWizard from "../../SvelteKit/forms/AutoFormPageWizard.svelte";
    import {label} from "../../components/templates/general";
    import * as pageOrchestratorConstants from './../../components/orchestrator/constants';
    import * as localUtils from './utils';
    import * as localConstants from './review-constants';
    import * as general from '../../components/templates/general';
    import Loader from "../../SvelteKit/elements/Loader.svelte";
import { partnerNome, partner_tax_code, partner_vat_number, tipoDiRicerca } from '../change-operations/store';
import { get } from 'svelte/store';

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction;
    runAlternativeFlow;


    let promise = genReviewUiModel(appState.context.customers)

    setTimeout(() => {
        nextStateEnabled = true;
    }, 1000);

    function mergePhoneNumber(phone){
        return `(${phone.countryCode.value}) ${phone.areaCode || ""} ${phone.number}`;
    }
    function genId(customerData, fieldName) {
        return `${customerData.type}_${fieldName}`;
    }

    function genAddressRows (customerData, address, title, rowDecorated=false) {
        let row = [];
        if(address){
            row = [
                general.group(title, title, {subtitle:true, decorated:true}),
                [
                    general.rowMetadata(rowDecorated),
                    general.label(genId(customerData, "address"), $_("review.form.address.toponym"), {
                        size: "lg",
                        default_value: `${address.dug || ""} ${address.toponym || ""} ${address.number || ""}` +
                                ` - ${address.zipCode.code}  ${address.city.value} ${ address.province ? "("+address.province.code+")" : ""}` +
                                ` - ${address.country.value}`
                    })
                ]
            ];
        }
        return row;
    }

    function genPartnerUiModel() {
        
        let path = [];
        path.push([
            general.rowMetadata(false),
            general.label("pIva-id", "Partita IVA", {
                size: "ld",
                default_value: get(partner_vat_number)
            }),
            general.label("cd-id", "Codice Fiscale", {
                size: "ld",
                default_value: get(partner_tax_code)
            })
        ]);
        path.push([
            general.rowMetadata(false),
            general.label("denSociale-id", "Denominazione sociale", {
                size: "ld",
                default_value: get(partnerNome)
            })
        ]);
        return path;
    }

    function genCustomerReviewUiModel(customerData) {
        let path = [];
        if (localConstants.CUSTOMER_TYPE.LP === customerData.type) {
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "taxCode"), $_("review.form.taxCode"), {
                    size: "ld",
                    default_value: customerData.taxCode
                }),
                general.label(genId(customerData, "firstName"), $_("review.form.firstName"), {
                    size: "ld",
                    default_value: customerData.firstName
                }),
                general.label(genId(customerData, "lastName"), $_("review.form.lastName"), {
                    size: "ld",
                    default_value: customerData.lastName
                })
            ]);
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "legalForm"), $_("review.form.legalForm"), {
                    size: "ld",
                    default_value: customerData.legalForm.value
                }),
                general.label(genId(customerData, "companyName"), $_("review.form.companyName"), {
                    size: "md",
                    default_value: customerData.companyName
                })
            ]);
            if(customerData.ateco && customerData.ateco.code) {
                path.push([
                    general.rowMetadata(false),
                    general.label(genId(customerData, "atecoCode"), $_("review.form.ateco.code"), {
                        size: "ld",
                        default_value: customerData.ateco.code
                    }),
                    general.label(genId(customerData, "atecoDescription"), $_("review.form.ateco.description"), {
                        size: "hmd",
                        default_value: customerData.ateco.description
                    })
                ]);
            }
        } if (localConstants.CUSTOMER_TYPE.PG === customerData.type) {
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "vatNumber"), $_("review.form.vatNumber"), {
                    size: "ld",
                    default_value: customerData.vatNumber
                }),
                general.label(genId(customerData, "taxCode"), $_("review.form.taxCode"), {
                    size: "ld",
                    default_value: customerData.taxCode
                })
            ]);
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "legalForm"), $_("review.form.legalForm"), {
                    size: "ld",
                    default_value: customerData.legalForm.value
                }),
                general.label(genId(customerData, "companyName"), $_("review.form.companyName"), {
                    size: "md",
                    default_value: customerData.companyName
                })
            ]);
            if(customerData.ateco && customerData.ateco.code) {
                path.push([
                    general.rowMetadata(false),
                    general.label(genId(customerData, "atecoCode"), $_("review.form.ateco.code"), {
                        size: "ld",
                        default_value: customerData.ateco.code
                    }),
                    general.label(genId(customerData, "atecoDescription"), $_("review.form.ateco.description"), {
                        size: "hmd",
                        default_value: customerData.ateco.description
                    })
                ]);
            }
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "constitutionDate"), $_("review.form.constitution.date"), {
                    size: "ld",
                    default_value: customerData.constitution.date
                })/*,
                general.label(genId(customerData, "constitutionCity"), $_("review.form.constitution.city"), {
                    size: "ld",
                    default_value: `${customerData.constitution.city.value} (${customerData.constitution.province.code || ""}) - ${customerData.constitution.country.value}`
                })*/
            ]);
        } else if (localConstants.CUSTOMER_TYPE.PF === customerData.type) {
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "taxCode"), $_("review.form.taxCode"), {
                    size: "ld",
                    default_value: customerData.taxCode
                }),
                general.label(genId(customerData, "firstName"), $_("review.form.firstName"), {
                    size: "ld",
                    default_value: customerData.firstName
                }),
                general.label(genId(customerData, "lastName"), $_("review.form.lastName"), {
                    size: "ld",
                    default_value: customerData.lastName
                })
            ]);
        }

        if (localConstants.CUSTOMER_TYPE.PF === customerData.type || localConstants.CUSTOMER_TYPE.LP === customerData.type) {
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "birthDate"), $_("review.form.birth.date"), {
                    size: "ld",
                    default_value: customerData.birth.date
                }),
                general.label(genId(customerData, "birthCity"), $_("review.form.birth.city"), {
                    size: "ld",
                    default_value: `${customerData.birth.city.value} (${customerData.birth.province.code || ""}) - ${customerData.birth.country.value}`
                })
            ]);
        }
        path.push(general.group($_("review.form.contacts"), $_("review.form.contacts"), {subtitle:true, decorated:true}));
        if (localConstants.CUSTOMER_TYPE.LP === customerData.type || localConstants.CUSTOMER_TYPE.PG === customerData.type) {
            let contacts = [
                general.rowMetadata(false)
            ];
            if(customerData.phones.mobile){
                contacts.push(
                    general.label(genId(customerData, "mobilePhone"), $_("review.form.phone.mobile"), {
                        size: "ld",
                        default_value: mergePhoneNumber(customerData.phones.mobile)
                    })
                );
            }
            if(customerData.phones.landing){
                contacts.push(
                    general.label(genId(customerData, "landingPhone"), $_("review.form.phone.landing"), {
                        size: "ld",
                        default_value: mergePhoneNumber(customerData.phones.landing)
                    })
                );
            }
            if(customerData.phones.mobile && customerData.phones.landing && customerData.emails.email){
                path.push(contacts);
                path.push([
                    general.rowMetadata(false),
                    general.label(genId(customerData, "email"), $_("review.form.email.email"), {
                        size: "ld",
                        default_value: customerData.emails.email
                    }),
                    general.label(genId(customerData, "emailPec"), $_("review.form.email.pec"), {
                        size: "ld",
                        default_value: customerData.emails.pec
                    })
                ]);
            } else {
                contacts.push(
                    general.label(genId(customerData, "emailPec"), $_("review.form.email.pec"), {
                        size: "ld",
                        default_value: customerData.emails.pec
                    })
                );
                if(customerData.emails.email){
                    contacts.push(
                        general.label(genId(customerData, "email"), $_("review.form.email.email"), {
                            size: "ld",
                            default_value: customerData.emails.email
                        })
                    );
                }
                path.push(contacts);
            }

        } else if (localConstants.CUSTOMER_TYPE.PF === customerData.type) {
            path.push([
                general.rowMetadata(false),
                general.label(genId(customerData, "mobilePhone"), $_("review.form.phone.mobile"), {
                    size: "ld",
                    default_value: mergePhoneNumber(customerData.phones.mobile)
                }),
                general.label(genId(customerData, "email"), $_("review.form.email.email"), {
                    size: "ld",
                    default_value: customerData.emails.email
                })
            ]);
        }

        if (localConstants.CUSTOMER_TYPE.LP === customerData.type || localConstants.CUSTOMER_TYPE.PG === customerData.type) {
            path = path.concat(genAddressRows(customerData, customerData.registeredOffice, $_("review.form.registeredOffice")/*, localConstants.CUSTOMER_TYPE.LP !== customerData.type*/));
        }

        if (localConstants.CUSTOMER_TYPE.PF === customerData.type) {
            path = path.concat(genAddressRows(customerData, customerData.residency, $_("review.form.residency")));
        }
        return path;
    }

    async function genReviewUiModel(data) {
        let path = [];

        try {
                path.push(general.group("contractor", get(tipoDiRicerca) === "cliente" ? $_("review.form.contractor") : "Partner", {title:true, decorated:true}));
                if(get(tipoDiRicerca) === "cliente") {
                    path = path.concat(genCustomerReviewUiModel(data.contractor));
                    if (data.legalRepresentative) {
                        path.push(general.group("legalRepresentative", $_("review.form.legalRepresentative"), {title:true, style:"padding-top:30px;", decorated:true}));
                        path = path.concat(genCustomerReviewUiModel(data.legalRepresentative));
                    } 
                } else {
                    path = path.concat(genPartnerUiModel());
                }
        } catch (e) {
            console.error("Error encountered parsing customer data", e);
            runAction(pageOrchestratorConstants.GOTO_SERVICE_PAGE_ACTION_ID, {});
        }

        return path;
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
                externalButtons={true}
                externalStepper={true}
                path={localUtils.dummyFormDescriptor(reviewUiModel)}
                submitted={false}
                readOnly={true}
                on:submit={undefined}
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
