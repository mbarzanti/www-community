<script>
    import {onMount} from "svelte";
    import DashboardPage from "../../pages/dashboard/DashboardPage.svelte"
    import AppBasePageLayout from "./../AppBasePageLayout.svelte";
    import CompanyData from "./components/CompanyData.svelte";
    import Introduction from "./components/Introduction.svelte";
    import PanelGroupItemWrapper from "../../components/common/PanelGroupItemWrapper.svelte";
    import {
        goToNextTask,
        goToPrevTask,
        goToLastTask,
    } from "../../UiKitLite/panelgrouppills/panelgrouputils";

    import AppStepper from "../../components/common/AppStepper.svelte";
    import * as api from "../../api";
    import {scrollToTop} from "../../UiKitLite/libs/jquery";

    import ConfirmEmail from "./components/ConfirmEmail.svelte";
    import {
        companyDataStep1,
        showLoadingSpinner,
        companyData as companyDataStore,
        conventionalCodeSelected,
        aliasSelected,
        plafondError,
        hideDashboard
    } from "../../stores";
    import {push} from "svelte-spa-router";
    import Manager from "../../libs/adobetm";
    import InsertCode from "./components/InsertCode.svelte";

    const trackingManager = Manager();

    let companyData;

    let companyPhoneNumber;
    let dataChecked = false;
    let email = "";
    let cellphone = "";

    let groupItems = [
        {
            stepName: "welcome",
            stepIndex: "1.1",
            title: "Prima di iniziare",
            isCompleted: false,
            isCurrentStep: true,
        },
        {
            stepName: "selectup",
            stepIndex: "1.2",
            title: "Dati anagrafici",
            isCompleted: false,
            isCurrentStep: false,
        },
        {
            stepName: "uprecap",
            stepIndex: "1.3",
            title: "Email di recapito",
            isCompleted: false,
            isCurrentStep: false,
        },
        {
            stepName: "codconvezione",
            stepIndex: "1.4",
            title: "Hai un codice convenzione?",
            isCompleted: false,
            isCurrentStep: false,
        },
    ];

    $: step1Enabled = !groupItems[0].isCompleted || $plafondError;
    $: step2Enabled = !groupItems[1].isCompleted || $plafondError;
    $: step3Enabled = !groupItems[2].isCompleted || $plafondError;
    $: step4Enabled = !groupItems[3].isCompleted;

    onMount(async () => {
        conventionalCodeSelected.set("");
        aliasSelected.set("");
        if ($plafondError) {
            groupItems[0].isCompleted = true;
            groupItems[0].isCurrentStep = false;
            groupItems[1].isCompleted = true;
            groupItems[1].isCurrentStep = false;
            groupItems[2].isCompleted = true;
            groupItems[2].isCurrentStep = false;
            groupItems[3].isCompleted = false;
            groupItems[3].isCurrentStep = true;
        }
    });

    function switchToNextTask(step) {
        if (step) {
            if (step.stepIndex == "1.1" && step.isCurrentStep && !step.isCompleted) {
                api.createFunnel(companyData.codiceNdg, companyData.legalRepresentativeName, companyData.legalRepresentativeSurname).then(() => nextStep(step.stepName));
            } else {
                nextStep(step.stepName);
            }
        }
    }

    function trackAndSwitchToCodConvenzione(step) {
        trackingManager.trackStep('alias_EcoBonusRet_1_4_Codice_Convenzione');
        trackingManager.sendApplicationDirectCall();
        switchToNextTask(step);
    }

    function trackAndSwitchToPasswordConfirm(step) {
        trackingManager.trackStep('alias_EcoBonusRet_1_3_Conferma_Mail');
        trackingManager.sendApplicationDirectCall();
        switchToNextTask(step);
    }

    function trackAndSwitchToCompanyData(step) {
        trackingManager.trackStep('alias_EcoBonusRet_1_2_Dati_Azienda');
        trackingManager.trackProductDescription('EcoBonus - Retail - Cessione Credito Imposta');
        trackingManager.trackOperationType('add');
        trackingManager.sendApplicationDirectCall();
        switchToNextTask(step);
    }

    function nextStep(stepName) {
        goToNextTask(groupItems, stepName);
        groupItems = [...groupItems];
    }

    function handlePhoneData(event) {
        companyPhoneNumber = companyData.phoneNumber
            ? companyData.phoneNumber
            : event.detail.phoneNumber;
        dataChecked = event.detail.dataChecked;
    }

    function handleEmailData(event) {
        if (event.detail) {
            email = event.detail.email;
        } else {
            email = null;
        }
    }

    function storedAndNextStep() {
        showLoadingSpinner.set(true);
        if ($plafondError) {
            plafondError.set(false);
        } else {
            companyDataStep1.set({
                businessAddressOffice: companyData.businessAddressOffice,
                businessCap: companyData.businessCap,
                businessCityOffice: companyData.businessCityOffice,
                businessId: companyData.businessId,
                email: email,
                legalRepresentativeName: companyData.legalRepresentativeName,
                legalRepresentativeSurname: companyData.legalRepresentativeSurname,
                legalRepresentativeProvBirth: companyData.legalRepresentativeProvBirth,
                legalRepresentativeCityBirth: companyData.legalRepresentativeCityBirth,
                legalRepresentativeDateBirth: companyData.legalRepresentativeDateBirth,
                mobileNumber: companyPhoneNumber,
            });
        }
        api
            .submitStepAnagrafica()
            .then(() => push("/credits"))
            .finally(() => {
                showLoadingSpinner.set(false);
            });
    }

    $: formIsNotValid = !email || !companyPhoneNumber || !dataChecked;

    companyData = $companyDataStore;
    companyPhoneNumber = companyData.phoneNumber;
    onMount(() => {
        scrollToTop();
    });
</script>

<AppBasePageLayout id="company-data-main" enableMainPills={false}>
    <div slot="top-content">
        <AppStepper currentStepName="companyData"/>
    </div>
    <div
            class="welcome welcome-simple spacer-xs-top-20 spacer-md-top-0
      spacer-xs-bottom-0 spacer-md-bottom-20">
        <div class="row">
            <div class="col-sm-12 col-md-9">
                <div class="abstract">
                    <div class="abstract-heading spacer-xs-bottom-25">
                        <h1>Cessione del credito d’imposta</h1>
                    </div>
                </div>
            </div>
        </div>

        {#if $hideDashboard == false}
            <DashboardPage></DashboardPage>
        {:else }
            <PanelGroupItemWrapper
                    groupId="accordion"
                    itemDescriptor={groupItems[0]}
                    bind:noOpenButton={step1Enabled}>
                <Introduction
                        bind:transitionDone={groupItems[0].isCompleted}
                        on:goBack={() => push('/')}
                        on:complete={() => trackAndSwitchToCompanyData(groupItems[0])}/>
            </PanelGroupItemWrapper>
            <PanelGroupItemWrapper
                    groupId="accordion"
                    itemDescriptor={groupItems[1]}
                    bind:noOpenButton={step2Enabled}>
                <CompanyData
                        bind:transitionDone={groupItems[1].isCompleted}
                        {...companyData}
                        on:value={handlePhoneData}
                        on:complete={() => trackAndSwitchToPasswordConfirm(groupItems[1])}/>
            </PanelGroupItemWrapper>
            <PanelGroupItemWrapper
                    groupId="accordion"
                    itemDescriptor={groupItems[2]}
                    bind:noOpenButton={step3Enabled}>
                <ConfirmEmail
                        on:value={handleEmailData}
                        on:complete={() => trackAndSwitchToCodConvenzione(groupItems[2])}/>
            </PanelGroupItemWrapper>
            <PanelGroupItemWrapper
                    groupId="accordion"
                    itemDescriptor={groupItems[3]}
                    bind:noOpenButton={step4Enabled}>
                <InsertCode
                        on:complete={() => storedAndNextStep()}/>
            </PanelGroupItemWrapper>
        {/if}
    </div>
</AppBasePageLayout>
