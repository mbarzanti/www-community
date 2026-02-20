<script>

    import PageOrchestrator from './../components/orchestrator/page-orchestrator.svelte';

    import TaskPage from './../components/ui/taskPage.svelte';

    import ErrorPage from './service/error.svelte';
    import SearchPage from './search/search.svelte';
    import ReviewPage from './select-operation/review.svelte';
    import SanityContacts from './normalization/contacts.svelte';
    import SelectAssetPage from './select-operation/select-asset.svelte';
    import SelectChangePage from './select-operation/select-change.svelte';
    import SelectSalesPointPage from './select-operation/select-sales-point.svelte';
    import ChangeOperationPage from './change-operations/change-operation.svelte';
    import DummyPage from './redirect/dummy-page.svelte';

    import { _ } from 'svelte-i18n';

    import * as SelectOperationHandlers from './select-operation/handlers';
    import * as SanityContactsHandlers from './normalization/handlers';
    import * as ChangeOperationHandlers from './change-operations/handlers';
    import * as RedirectOperationHandlers from './redirect/redirect-handlers';


    import * as constants from './../commons/constants';
    import * as pageOrchestratorConstants from './../components/orchestrator/constants';

    import * as stepsConfig from './stepperConfig';


    export let params = {};

    const CONTINUE_BUTTON = {
        label : $_("default.buttons.continue"),
        primary : true,
        action: pageOrchestratorConstants.CONTINUE_ACTION_ID,
        class: pageOrchestratorConstants.BUTTON_CLASS_CONTINUE
    };
    const BACKWARD_BUTTON = {
        label : $_("default.buttons.goBack"),
        primary : false,
        action: pageOrchestratorConstants.BACK_ACTION_ID
    };
    const HOME_BUTTON = {
        label : $_("default.buttons.comeBackHome"),
        primary : false,
        action: {
            handler: (arg, appState, shiftToState, fallback) =>{
                appState.globalContext = {};
                shiftToState(constants.SEARCH_PAGE_ID);
            }
        }
    };

    const stepperWrapper = (currentStep, currentTask, steps) => {
        let wrapper = {
            component: TaskPage,
            props: {
                currentStep: currentStep,
                currentTask: currentTask
            }
        }
        if(steps){
            wrapper.props.steps = steps;
        }
        return wrapper;
    }

    const config = {
        [pageOrchestratorConstants.OPT_DEBUG] : false,
        [pageOrchestratorConstants.OPT_DEBUG_WITHOUT_STATUS] : true,
        [pageOrchestratorConstants.OPT_DEBUG_WITHOUT_FLOW] : false,
        [pageOrchestratorConstants.OPT_RETRY_NUMBER] : 1,
        [pageOrchestratorConstants.OPT_RETRY_STATUS_ERROR] : 0,
        [pageOrchestratorConstants.OPT_DEBUG_START_STEP] : params.id
    }

    const stepNavigationPath = [
        {
            name: constants.SEARCH_PAGE_ID,
            content: SearchPage,
            buttons:[]
        },
        {
            name: constants.SANITY_CONTACTS_ID,
            content: SanityContacts,
            loader: SanityContactsHandlers.sanityCheckLoader,
            buttons:[],
            wrapper: stepperWrapper(constants.REVIEW_STEP_ID, constants.REVIEW_PAGE_ID)
        },
        {
            name: constants.REVIEW_PAGE_ID,
            content: ReviewPage,
            loader: SelectOperationHandlers.customerLoader,
            buttons:[
                HOME_BUTTON,
                CONTINUE_BUTTON
            ],
            wrapper: stepperWrapper(constants.REVIEW_STEP_ID, constants.REVIEW_PAGE_ID)
        },
        {
            name: constants.SELECT_ASSET_PAGE_ID,
            content: SelectAssetPage,
            loader: SelectOperationHandlers.assetsLoader,
            buttons:[
                BACKWARD_BUTTON,
                CONTINUE_BUTTON
            ],
            wrapper: stepperWrapper(constants.SELECT_OP_STEP_ID, constants.SELECT_ASSET_PAGE_ID)
        },
        {
            name: constants.SELECT_CHANGE_PAGE_ID,
            content: SelectChangePage,
            loader: SelectOperationHandlers.changesSPLoader,
            buttons:[
                BACKWARD_BUTTON,
                CONTINUE_BUTTON
            ],
            handler:SelectOperationHandlers.selectChangeHandler,
            wrapper: stepperWrapper(constants.SELECT_OP_STEP_ID, constants.SELECT_CHANGE_PAGE_ID)
        },
        {
            name: constants.SELECT_SALES_POINT_PAGE_ID,
            content: SelectSalesPointPage,
            buttons:[
                BACKWARD_BUTTON,
                CONTINUE_BUTTON
            ],
            wrapper: stepperWrapper(constants.SELECT_OP_STEP_ID, constants.SELECT_SALES_POINT_PAGE_ID, stepsConfig.stepsWithSalespoint)
        },
        {
            name: constants.REDIRECT_PAGE_ID,
            content: DummyPage,
            buttons:[],
            loader: RedirectOperationHandlers.createFunnel,
            loaderMessage: $_("redirect.loader")
         },
        {
            name: constants.CHANGE_OPERATION_PAGE_ID,
            content: ChangeOperationPage,
            buttons:[],
            loader:ChangeOperationHandlers.operationLoader,
            loaderMessage: $_("redirect.change.loader")
        },
        {
            name: constants.SERVICE_PAGE_ID,
            content: ErrorPage,
            props:{
                homeAddress:"/feu-after-sales"
            },
            buttons:[]
        }

    ];
</script>
<PageOrchestrator
    flow={stepNavigationPath}
    configInput={config}
    servicePageName={constants.SERVICE_PAGE_ID}
/>








