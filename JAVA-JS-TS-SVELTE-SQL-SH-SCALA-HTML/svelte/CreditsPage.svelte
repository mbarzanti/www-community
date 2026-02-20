<script>
    import {onMount, tick, onDestroy} from 'svelte'
    import AppBasePageLayout from '../AppBasePageLayout.svelte'
    import Credit from './components/Credit.svelte'
    import CalculatedCredits from './components/CalculatedCredits.svelte'
    import {pop, push } from "svelte-spa-router";
    import {
        credits,
        bonus,
        showLoadingSpinner,
        pickedBonusDataSet,
        pickedYearsPerBonusSet,
        flags,
        sismaBonusId,
        sismaBonusMessage,
        numberYears,
        plafondError, plafondValue,
        convention, listino, aliasSelected, conventionalCodeSelected, maxTreshold, tresholdRetrived, rischiFinanziariAmountError, hideDashboard,companyData,
    } from '../../stores'
    import * as yup from 'yup'
    import {scrollTo} from '../../UiKitLite/libs/jquery'
    import GenericFunnelResult from '../../UiKitLite/layouts/GenericFunnelResult.svelte'
    import AppStepper from '../../components/common/AppStepper.svelte'
    import AlertModal from '../../UiKitLite/common/AlertModal.svelte'
    import BoxAdvice from '../../UiKitLite/common/BoxAdvice.svelte'
    import PanelGroupItemWrapper from '../../components/common/PanelGroupItemWrapper.svelte'
    import {mapCreditsArray} from './utils/MappingUtils.js'
    import {
        isBonusDataValid,
    } from './utils/ValidationUtils.js'
    import {formErrors, formLabels} from '../../labels.js'

    import {calculateCredits, completeFunnelStep, notifyCorrectFieldsChange, popUpSendMail, scrivaniaCreazionePraticaCcr} from '../../api'
    import numeral from 'numeral'
    import CreditsErrorModal from './components/CreditsErrorModal.svelte'

    import Manager from '../../libs/adobetm'
    import MaximumExtensionModal from "./components/MaximumExtensionModal.svelte";
    import ResultExtensionModal from "./components/ResultExtensionModal.svelte";

    const trackingManager = Manager()

    let flagAssicuraArray = [{id: 0,posteAssicuraCheck:false}]

    let creditsFormData = [{id: 0, data: null}]

    // Parameter to define the number of selected credits
    let selectedCredits = 1

    // Array to provide titles to credit component
    let creditNames = []
    // Variable to check if credits' values were retrieved
    let creditsRetrieved = false

    // Parameters to handle errors
    let showErrorBox = false

    // Data to be shown in result panel
    let resultData = {
        computedBonus: 0,
        bonusDetail: [],
    }
    //sismabonus message
    let sBm = ''

    // Start loading spinner
    // showLoadingSpinner.set(true);

    // Message to be shown to user if errors happen
    let errorMessage

    let creditsErrors = []

    // Bonus data to be passed to Credit component
    let bonusData = null

    let estensioneStato = "";
    let popUpRichiediEstensioneVisible = false;
    let sogliaReale = "";
    let popupEsitoVisible = false;
    let sendMailError = false;
    let extensionMail = "";
    let importoFuoriSoglia = "";
    let sogliaEstesa = "";
    let sogliaEstendibile = false;

    $: removeEnabled = creditsFormData.length > 1
    let erogabile = false;

    // Given the selected credits, remove duplicates(same id). Used to check consistency/incosistency in 'first transfer credit' flags
    $: mixedTransfer = $credits.reduce((unique, o) => {
        if (!unique.some((obj) => obj.id === o.id)) {
            unique.push(o)
        }
        return unique
    }, [])

    $: noInvalidYears = numberValidYears === $numberYears

    $: atLeastOneFlag = creditsFormData.filter(function (item) {
        if (item && item.primaCessione) {
            return item.primaCessione;
        } else return false;
    }).length > 0;

    bonus.subscribe(value => {
        bonusData = value;
        if(value !== null) {
            bonusData.bonus = bonusData.bonus.filter((item) => item.available);
        }
    });

    let numberValidYears = 0;
    let creditsId = [];
    let idCount = 0;


    let errorMessages = []

    let titolo = 'Errore'

    function modifyFlagStore() {
        if (atLeastOneFlag) flags.set(1);
        else flags.set(0);
    }


    function countValideYears() {
        numberValidYears = 0
        creditsFormData.forEach(function (element) {
            if (element && element.primaCessione) {
                if (element && element.data) {
                    numberValidYears += 1
                }
            } else {
                if (element && element.data) {
                    numberValidYears += element.data.length
                }
            }
        })
    }

    function correctModify() {
        tresholdRetrived.set(false);
        let firstTransferFlag = $flags > 0 ? true : false
        // Update data to be sent to Back-end
        let creditsToCalculate = $credits.map(function (item) {
            item.flagPrimaCessione = firstTransferFlag
            return item
        })
        // Perform api call
        notifyCorrectFieldsChange(creditsToCalculate).then(response => {
            maxTreshold.set(null);
            if (!response.erogabile) {
                let message="";
                if(response.segnalazioni && response.segnalazioni[0] && (response.segnalazioni[0].codice === "MP01" ||
                     response.segnalazioni[0].codice === "MP02" )) {
                    maxTreshold.set(response.massimoImportoRichiedibile);     
                    response.segnalazioni[0].codice === "MP01"? 
                        rischiFinanziariAmountError.set("Il valore di credito non può superare € " + $maxTreshold + ' per pratica') :
                        rischiFinanziariAmountError.set("Il valore di credito non può superare € " + $maxTreshold + ' per anno'); 
                    message = response.segnalazioni[0].descrizione;
                    errorMessage = message;
                    showErrorBox = true;
                    titolo = 'Attenzione'; 
                }
                else {
                    estensioneStato = response.stato;
                    if(!estensioneStato || estensioneStato.toString().toUpperCase() === "NOT_EXIST" && response.sogliaEstesa) {
                        popUpRichiediEstensioneVisible = true;
                    }
                    else {
                        estensioneStato = estensioneStato+"_RISCHI_FINANZIARI";
                        popupEsitoVisible = true;
                    }
                    // rischiFinanziariAmountError.set("Il valore di credito non può superare € " + $maxTreshold + ' per anno');
                    /*  response.tipoSoglia.toLowerCase() === "min"?
                        message="L'importo massimo cedibile per anno è pari a " + getSeparator(response.sogliaReale) + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di "+ getSeparator(response.importoFuoriSoglia)+" €." :
                        message="L'importo massimo cedibile per anno è pari a " + getSeparator(response.sogliaReale) + " €. L’importo finora ceduto, incluso quello della presente richiesta, eccede il valore massimo di "+ getSeparator(response.importoFuoriSoglia)+" €." */
                    sogliaReale = formatMoney(response.sogliaReale);
                    importoFuoriSoglia = formatMoney(response.importoFuoriSoglia);
                    sogliaEstesa = response.sogliaEstesa? formatMoney(response.sogliaEstesa) : null;
                    sogliaEstendibile = response.sogliaEstendibile;
                    maxTreshold.set(null);
                }
               /*
                errorMessage = message;
                showErrorBox = true;
                titolo = 'Attenzione'; */
                erogabile = false;
                // maxTreshold.set(response.massimoImportoRichiedibile);
                tresholdRetrived.set(true);
            } else {
                erogabile = true;
            }
        }).catch(error => {
            errorMessage = "Errore durante la notifica del cambiamento"
            showErrorBox = true;
        })
    }

    function goToDashboard(){
        hideDashboard.set(false);
        push("/");
    }

    
    function sendEmail(event) {
        showLoadingSpinner.set(true);
        scrivaniaCreazionePraticaCcr(event.detail.email, $companyData.codiceNdg).then(resp => {
            showLoadingSpinner.set(false);
            extensionMail = event.detail.email;
            popupEsitoVisible = true;
        })
        .catch((err) => { 
            showLoadingSpinner.set(false);
            sendMailError = true;
            popupEsitoVisible = true;
        })
        }

        function formatMoney(number) {
            return number.toLocaleString('it', {minimumFractionDigits: 2});
        }

    function valueChanged(event) {
        let data = event.detail.value
        creditsFormData[event.detail.id].data = event.detail.value
        if (event.detail.primaCessione)
            creditsFormData[event.detail.id].primaCessione = true
        else creditsFormData[event.detail.id].primaCessione = false
        modifyFlagStore()
        countValideYears()
        storeSavedData()
        //If some year has been modified correctly

    }

    function manageBlur2(event) {
        if(numberValidYears===$numberYears){
            correctModify();
        }
    }


    function removeYear(event) {
        creditsFormData[event.detail.id].data = creditsFormData[
            event.detail.id
            ].data.filter((item) => item.fiscalYear !== event.detail.year)
        storeSavedData()
        countValideYears()
        modifyFlagStore()
        if (numberValidYears === $numberYears) {
            correctModify();
        }
    }

    function creditChanged(event) {
        if (event.detail.id && creditsFormData[event.detail.id].data)
            creditsFormData[event.detail.id].data = null
        storeSavedData()
        countValideYears()
        modifyFlagStore()
    }

    function storeSavedData() {
        //Save in store
        let dataToStore = mapCreditsArray(creditsFormData)
        if (!isBonusDataValid(dataToStore)) {
            displayErrorMessage(formErrors.BONUS_INTEGRITY_CHECK_FAILED)
        } else {
            credits.set(dataToStore)
        }
    }

    function displayErrorMessage(message) {
        showErrorBox = true
        errorMessage = message
    }

    function handleSubmit() {
        if ($plafondError) {
            pop()
        } else {
            showLoadingSpinner.set(true)
            completeFunnelStep()
                .then((data) => {
                    window.location.href = data.nextStepUrl
                })
                .catch((error) => {
                    errorMessage = 'Errore durante il completamento della procedura'
                    trackingManager.trackErrorMessage(errorMessage)
                    trackingManager.sendErrorEvent()
                    showErrorBox = true
                })
                .finally(() => {
                    showLoadingSpinner.set(false)
                })
        }
    }

    const creditsUnsubscribe = credits.subscribe((data) => {
        creditsRetrieved = false
    })

    function performCalculation() {
        if (maxAmountExceeded($credits)) {
            showErrorBox = true
            errorMessage = formErrors.MAX_CREDIT_EXCEEDED
            return
        }
        let numberFlag = creditsFormData.filter(function (item) {
            if (item && item.primaCessione) {
                return item.primaCessione;
            } else return false;
        }).length;
        if (numberFlag > 0 && numberFlag < creditsFormData.length) {
            showErrorBox = true
            errorMessage =
                'Non è possibile inserire nella stessa richiesta crediti relativi a una prima cessione e crediti relativi a cessioni successive. Ti invitiamo dunque ad eliminare il nuovo credito inserito ed effettuare due richieste distinte'
        } else {
            showLoadingSpinner.set(true)
            //Check if all fields have been filled
            if (creditsFormData.length > 0) {
                for (var i = 0; i < creditsFormData.length; i++) {
                    if (creditsFormData[i] == null) {
                        scrollTo('#credit-box-' + i)
                        return
                    }
                }

                /** TODO: this will be removed after implementing the modal on the Credit component
                 */
                if (!isBonusDataValid($credits)) {
                    displayErrorMessage(formErrors.BONUS_INTEGRITY_CHECK_FAILED)
                    return
                }

                let firstTransferFlag = $flags > 0 ? true : false
                let pAFlag = flagAssicuraArray
                // Update data to be sent to Back-end
                var creditsToCalculate = $credits.map(function (item) {
                    item.flagPrimaCessione = firstTransferFlag
                    return item
                })

                // Perform api call
                calculateCredits(creditsToCalculate)
                    .then((result) => {
                        trackingManager.trackStep('alias_EcoBonusRet_2_2_Valore_Crediti')
                        trackingManager.sendApplicationDirectCall()

                        showLoadingSpinner.set(false)
                        //Clear old data
                        resultData = {
                            computedBonus: result.creditAmount,
                            bonusDetail: [],
                        }
                        errorMessages = [];
                        if(result.errore && ( result.errore.codice === "MP01" || result.errore.codice === "MP02" )) {
                            showErrorBox = true;
                            errorMessage = result.errore.descrizione;
                            titolo="Attenzione";
                        }
                        else if ($plafondError === true) {
                            showErrorBox = true;
                            errorMessage = "Per la convenzione l’Importo massimo cedibile è di " + $plafondValue +
                                "\n €. Seleziona il pulsante MODIFICA per rimodulare l’importo o il pulsante AGGIORNA per continuare senza convenzione";
                            titolo = "Attenzione"
                        } else {

                            //Handle errors in calculation
                            const creditsWithErrors = result.details.filter(
                                (credit) => !credit.available,
                            )

                            creditsErrors = creditsWithErrors.map((credit) => {
                                return {
                                    creditName: bonusData.bonus.filter(
                                        (bonus) => bonus.id === credit.id,
                                    )[0].ecobonus,
                                    creditId: credit.id,
                                    fiscalYear: credit.fiscalYear,
                                    errors: credit.errors,
                                }
                            })

                            //handle positive results
                            const positiveResults = result.details.filter(
                                (result) => result.available,
                            )
                            resultData.bonusDetail = positiveResults.map((result) => {
                                const creditData = $credits.filter(
                                    (item) =>
                                        item.id === result.id &&
                                        item.fiscalYear === result.fiscalYear,
                                )[0]
                                return {
                                    creditRequested: result.creditInserted,
                                    creditAmount: result.creditAmount,
                                    name: creditData.type,
                                    year: creditData.fiscalYear,
                                    amount: creditData.creditAmount,
                                }
                            })

                            if (resultData.bonusDetail.length > 0) {
                                creditsRetrieved = true
                                //check if data changes to remove result block
                            }
                            scrollTo('#credits-value')
                        }
                    })
                    .catch((error) => {
                        showLoadingSpinner.set(false)
                        if (error.status === "409" || error.status === 409) {
                            errorMessage = 'Non è possibile inserire nella stessa richiesta crediti relativi a Superbonus (con prima cessione e flag Polizza) e altri crediti. Ti invitiamo dunque ad eliminare il nuovo credito inserito ed effettuare due richieste distinte'
                        } else {
                            errorMessage = 'Errore durante il calcolo dei crediti'
                        }
                        trackingManager.trackErrorMessage(errorMessage)
                        trackingManager.sendErrorEvent()
                        showErrorBox = true
                    })
            }
        }
    }

    function addCredit(event) {
        idCount += 1;
        creditsId.push(idCount);
        creditsId = creditsId;
        titolo = ''
        displayErrorMessage(
            'Attenzione! Nella medesima richiesta non possono essere ceduti crediti relativi a una prima cessione e crediti relativi a cessioni successive',
        )
        event.preventDefault()
        resultData = {
            computedBonus: 0,
            bonusDetail: [],
        }
        creditsRetrieved = false
        let nextId =
            creditsFormData.length > 0
                ? creditsFormData[creditsFormData.length - 1].id + 1
                : 0
        creditsFormData.push({id: nextId, data: null})
        creditsFormData = creditsFormData
        pickedBonusDataSet.update((theArray) => {
            theArray.push({id: nextId, data: null})
            return theArray
        })

        flagAssicuraArray.push({id: nextId,posteAssicuraCheck:false});
        numberYears.update((n) => n + 1)
    }

    function removeCredit(event) {
        let indexToRemove = event.detail
        creditsFormData = creditsFormData
            .slice(0, indexToRemove)
            .concat(creditsFormData.slice(indexToRemove + 1, creditsFormData.length))
        creditsId = creditsId.slice(0, indexToRemove)
            .concat(creditsId.slice(indexToRemove + 1, creditsId.length));
        storeSavedData()
        pickedBonusDataSet.update((theArray) => {
            theArray = theArray
                .slice(0, indexToRemove)
                .concat(theArray.slice(indexToRemove + 1, theArray.length))
            let idx = 0
            for (let item of theArray) {
                item.id = idx
                idx++
            }
            return theArray
        })
        countValideYears()
        modifyFlagStore()
        if (numberValidYears === $numberYears) {
            correctModify();
        }

    }

    onMount(async () => {
        creditsId.push(0);
        creditsId = creditsId;
        sismaBonusMessage.subscribe((value) => {
            sBm = value
        })
        trackingManager.trackStep('alias_EcoBonusRet_2_1_Inserimento_Crediti')
        trackingManager.sendApplicationDirectCall()
        scrollTo('#title-div')

        numeral.register('locale', 'fr', {
            delimiters: {
                thousands: '.',
                decimal: ',',
            },
            abbreviations: {
                thousand: 'k',
                million: 'm',
                billion: 'b',
                trillion: 't',
            },
            ordinal: function (number) {
                return number === 1 ? 'er' : 'ème'
            },
            currency: {
                symbol: '€',
            },
        })
        // switch between locales
        numeral.locale('fr')
    })

    async function onEditCreditsClick() {
        creditsRetrieved = false
        await tick()
        scrollTo('#credits-panel')
    }

    let groupItems = [
        {
            stepName: 'crediti',
            stepIndex: '2.1',
            title: 'Compila crediti',
            isCompleted: false,
            isCurrentStep: true,
        },
    ]

    function displaySismaAlert() {
        titolo = ''
        displayErrorMessage(sBm)
    }

    function implicitResub() {
        convention.set('');
        conventionalCodeSelected.set('');
        listino.set('standard');
        aliasSelected.set('');
        performCalculation()
    }

    function updateFlagAssicura(flagValue) {

        let editDone = false;
        if (flagValue.detail.posteAssicuraCheck === true) {
            for (let i = 0; i < flagAssicuraArray.length; i++) {
                if (flagAssicuraArray[i].id === flagValue.detail.id) {
                    flagAssicuraArray[i].posteAssicuraCheck = flagValue.detail.posteAssicuraCheck
                    editDone = true
                }
            }
            if (editDone === false) {
                flagAssicuraArray.push(flagValue.detail)
            }
        } else {
            for (let i = 0; i < flagAssicuraArray.length; i++) {
                if (flagAssicuraArray[i].id === flagValue.detail.id)
                    flagAssicuraArray[i].posteAssicuraCheck = flagValue.detail.posteAssicuraCheck
            }
        }
    }

    function maxAmountExceeded(data) {
        let totalAmount = 0;
        for (const bonus of data) {
            totalAmount += bonus.creditAmount;
        }
        if ($maxTreshold && totalAmount > $maxTreshold) {
            return true;
        }
        return false;
    }

    onDestroy(creditsUnsubscribe)
</script>

<CreditsErrorModal bind:creditsErrors/>
<AppBasePageLayout enableMainPills={false}>
    <div slot="top-content">
        <AppStepper currentStepName="credits"/>
    </div>
    <div
            class="welcome welcome-simple spacer-xs-top-20 spacer-md-top-0
    spacer-xs-bottom-0 spacer-md-bottom-20">
        <div class="row" id="title-div">
            <div class="col-sm-12 col-md-9">
                <div class="abstract">
                    <div class="abstract-heading spacer-xs-bottom-30">
                        <h1>Cessione e bonus fiscali</h1>
                    </div>
                </div>
            </div>
        </div>

        <PanelGroupItemWrapper groupId="accordion" itemDescriptor={groupItems[0]}>
            <div
                    id="panel"
                    class="panel-collapse in"
                    role="tabpanel"
                    aria-labelledby="heading-04">
                <div class="row" id="credits-panel">
                    <div class="col-xs-12">
                        <p>
                            Inserisci i dati utili alla richiesta di cessione del credito
                            d’imposta.
                        </p>
                    </div>
                    <div class="col-xs-12 spacer-xs-top-10">
                        <p>
                            Seleziona "Aggiungi altro credito" per cedere una nuova tipologia
                            di credito.
                        </p>
                    </div>
                </div>
                {#each creditsFormData as credit, i (credit.id)}
                    <Credit
                            id={i}
                            title={'Credito ' + (i + 1)}
                            number={i}
                            {bonusData}

                            bind:removeEnabled
                            on:remove={removeCredit}
                            on:valueChanged={valueChanged}
                            on:blurEvent1={manageBlur2}
                            on:sismaAlert={displaySismaAlert}
                            on:removeYear={removeYear}
                            on:creditChanged={creditChanged}
                            on:updateFlagAssicura={updateFlagAssicura}
                            realId={creditsId[i]}/>
                {/each}
                <div class="row spacer-xs-top-10">
                    <div class="col-xs-12">
                        <button
                                type="button"
                                class="btn btn-xs btn-secondary"
                                href
                                on:click={addCredit}
                                disabled={!(noInvalidYears && erogabile)}>
                            Aggiungi altro credito
                        </button>
                        <button
                                disabled={!($credits.length > 0 && !creditsRetrieved && noInvalidYears && erogabile)}
                                on:click={performCalculation}
                                class="btn btn-xs btn-primary">
                            Calcola importo
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-xs-12">
                        <hr/>
                    </div>
                </div>
                {#if creditsRetrieved}
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="box-editable-area">
                                <h4><b>Importo netto che ti verrà accreditato</b></h4>
                            </div>
                        </div>
                    </div>
                    <CalculatedCredits bind:result={resultData}/>
                {/if}
                <div class="row spacer-xs-top-20">
                    <div class="col-xs-12 text-right">
                        <button
                                disabled={!creditsRetrieved}
                                type="button"
                                on:click={onEditCreditsClick}
                                class="btn btn-secondary">
                            MODIFICA
                        </button>
                        <button
                                disabled={!creditsRetrieved}
                                type="button"
                                class="btn btn-primary"
                                on:click={handleSubmit}>
                            PROSEGUI
                        </button>
                    </div>
                </div>
            </div>
        </PanelGroupItemWrapper>
    </div>

    {#if showErrorBox}
        <AlertModal
                id="practice-submit-error"
                title={titolo}
                showModal="true"
                on:close={() => {
                                    showErrorBox = false
                                    titolo = 'Errore'
                }}
                on:pop={()=> implicitResub()}>
            <p>{@html errorMessage}</p>
        </AlertModal>
    {/if}
</AppBasePageLayout>
{#if popUpRichiediEstensioneVisible}
    <MaximumExtensionModal
            id="maximum-extension"
            title="Errore"
            showModal="true"
            isModifica={true}
            sogliaReale={sogliaReale}
            importoFuoriSoglia={importoFuoriSoglia}
            sogliaEstesa={sogliaEstesa}
            sogliaEstendibile={sogliaEstendibile}
            on:close={() => (popUpRichiediEstensioneVisible = false)}
            on:sendMail={sendEmail}>
    </MaximumExtensionModal>
{/if}
{#if popupEsitoVisible}
    <ResultExtensionModal
            id="result-extension"
            title="Errore"
            showModal="true"
            stato={estensioneStato}
            error={sendMailError}
            email={extensionMail}
            sogliaReale={sogliaReale}
            importoFuoriSoglia={importoFuoriSoglia}
            on:close={() => {popupEsitoVisible = false; sendMailError = false}}
            on:back={() => goToDashboard()}>
    </ResultExtensionModal>
{/if}