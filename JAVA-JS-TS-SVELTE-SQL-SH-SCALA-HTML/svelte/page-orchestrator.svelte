<script>

    import {onMount} from 'svelte';
    import Loader from "../../SvelteKit/elements/Loader.svelte";
    import DefaultWrapper from './default-wrapper.svelte'
    import * as constants from './constants';
    import * as backwardUtils from "./backward/utils.js";

    export let flow = {};
    export let alternativeFlows = {};
    export let checkpointManager = undefined;
    export let servicePageName = "";
    export let configInput = {};

    let config = {};
    let currentIdx = {stepIdx: 0};
    let pathLength;
    let currentStep;
    let currentWrapper;
    let currentWrapperProps;
    let numberOfRetry = 1;

    // AlternativeFlow
    let currentAlternativeFlowId;
    let alternativeFlowIndex = 0;
    let alternativeFlowNextStepEnabled = false;
    let alternativeFlowRunning = false;

    const defaultActions = {
        [constants.CONTINUE_ACTION_ID] : onComplete,
        [constants.BACK_ACTION_ID] : onBack,
        [constants.SHIFT_TO_STATE_ACTION_ID] : shiftToState,
        [constants.FALLBACK_ACTION_ID] : fallback,
        [constants.GOTO_SERVICE_PAGE_ACTION_ID] : goToServicePage,
        [constants.SET_READY_ACTION_ID] : setReady
    }

    $:{
        currentStep = flow[currentIdx.stepIdx] === undefined ? [] : flow[currentIdx.stepIdx];
        currentWrapper = flow[currentIdx.stepIdx].wrapper && !flow[currentIdx.stepIdx].wrapper.disableOnAlternativeFlow ? flow[currentIdx.stepIdx].wrapper.component : DefaultWrapper;
        currentWrapperProps = flow[currentIdx.stepIdx].wrapper && !flow[currentIdx.stepIdx].wrapper.disableOnalternativeFlow ? flow[currentIdx.stepIdx].wrapper.props : {};
    }

    let appState = {
        currentPageId:"",
        context: {},
        globalContext: {},
        state: constants.STATE_LOADING,
        fail: false,
        failMessage: "",
        failCode: 0,
        nextStateEnabled: false,
        alternativeFlowContext: {},
    };


    const init = () => {
        if( checkInvalidField("loader") ){
            appState.state = constants.STATE_READY;
        } else {
            appState.state = constants.STATE_LOADING;
            flow[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback);
        }
        updateStateChartVariables();
        if(configInput[constants.OPT_DEBUG_START_STEP]){

            shiftToState(configInput[constants.OPT_DEBUG_START_STEP]);
        }
    };


    function loadCheckPoint(status, appState, fallback, exitIndex, index=0){
        if( exitIndex < index ){
            init();
        } else {
            const loadNextCheckPoint = () => {
                if(index < exitIndex) {
                    index++;
                    checkpointManager.context[status][index](appState, ()=>{}, loadNextCheckPoint, fallback);
                }else{
                    init();
                }
            };
            checkpointManager.context[status][index](appState, ()=>{}, loadNextCheckPoint, fallback);
        }
    }

    const initConfig = () => {
        Object.keys(constants.CONFIG_SET).forEach(
            (configEntry) => {
                config[configEntry] = configInput[configEntry] || constants.CONFIG_SET[configEntry]
            }
        );
    };

    onMount(() => {
        initConfig();
        pathLength = flow.length;
        appState.state = constants.STATE_LOADING;
        if(checkpointManager){
            const onMountGetStatus = (data, success) => {
                if(success && data.status && !config[constants.OPT_DEBUG_WITHOUT_STATUS]){
                    let found = false;
                    if( data.status.localeCompare(checkpointManager.start) === 0){
                        init();
                    } else {
                        for(let i=0; i<pathLength; i++){
                            if( flow[i].checkpointLoad && flow[i].checkpointLoad.localeCompare(data.status) === 0 && checkpointManager.context[data.status]){
                                currentIdx.stepIdx = i;
                                const exitIndex = checkpointManager.context[data.status].length - 1;
                                loadCheckPoint(data.status, appState, fallback, exitIndex);
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            init();
                        }
                    }
                } else {
                    init();
                }
            };
            checkpointManager.get(onMountGetStatus);
        } else {
            init();
        }
        if( constants.DEBUG_SESSION_STATE ){
            history.pushState({name: 'onMount'}, "pushState onMount", null);
        }
        //console.log(`History.state after pushState onMount: ${history.state}`);
    });

    function updateStateChartVariables() {
        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
        appState.currentPageId = flow[currentIdx.stepIdx].name;
        appState.context = (backwardUtils.getSession("appState") && backwardUtils.getSession("appState").context) ? backwardUtils.getSession("appState").context : {};
        appState.fail = false;
        appState.failMessage = "";
        appState.failCode = 0;
        appState.nextStateEnabled = false;
        alternativeFlowRunning = false;
        numberOfRetry = 1;
        backwardUtils.setSession("appState", appState);
    }

    function fallback(failCode, failMessage=undefined, data=undefined, endFail=false){

        if(data && data.status === config[constants.OPT_RETRY_STATUS_ERROR] && numberOfRetry < config[constants.OPT_RETRY_NUMBER])
        {
            appState.state = constants.STATE_LOADING;
            numberOfRetry = numberOfRetry + 1;
            flow[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback);
         // Should be null because we haven't modified the history stack yet
            if( constants.DEBUG_SESSION_STATE ){
                history.pushState({name: 'fallback'}, "pushState fallback", null);
                console.log(`History.state after pushState fallback: ${history.state}`);
            }
        }
        else {
            appState.state = constants.STATE_READY;
            jQuery([document.documentElement, document.body]).animate({
                scrollTop: jQuery("body").offset().top
            }, 500);
            appState.fail = true;
            appState.failMessage = failMessage;
            appState.failCode = failCode;
            appState.nextStateEnabled = false;
            if (endFail){
                goToServicePage(failCode, failMessage)
            }
        }

    }

    function setReady(){
        appState.state = constants.STATE_READY;

    }

    function checkInvalidField(field){
        return typeof flow[currentIdx.stepIdx][field] === 'undefined' || flow[currentIdx.stepIdx][field] === null
    }

    function onComplete() {
        appState.fail =false;
        if( checkInvalidField("handler") || config[constants.OPT_DEBUG_WITHOUT_FLOW] ) {
            shiftToState( 1 );
        } else {
            appState.state = constants.STATE_ENDING;
            appState.nextStateEnabled = false;
            flow[currentIdx.stepIdx].handler(appState, shiftToState, fallback);
            if( constants.DEBUG_SESSION_STATE ) {
                history.pushState({name: 'onComplete'}, "pushState onComplete", null);
                console.log(`History.state after pushState onComplete: ${history.state}`);
            }
        }
    }

     function onBack() {
       shiftToState( -1 );
     }

    function runAction( actionId, arg={} ){
        if( flow[currentIdx.stepIdx][actionId]){
            flow[currentIdx.stepIdx][actionId](appState, shiftToState, setReady, fallback);
        } else if( defaultActions[actionId] ) {
            runDefaultAction(actionId, arg);
        } else {
            console.error(`ActionId ${actionId} Unknown`)
        }
    }

    function runDefaultAction(actionId, arg){
        if( actionId === constants.CONTINUE_ACTION_ID ) {
            defaultActions[actionId]();
        } else if( actionId === constants.BACK_ACTION_ID ) {
            defaultActions[actionId]();
        } else if( actionId === constants.SHIFT_TO_STATE_ACTION_ID ) {
            defaultActions[actionId](arg.next);
        } else if( actionId === constants.FALLBACK_ACTION_ID ) {
            defaultActions[actionId](arg.failCode, arg.failMessage, arg.data, arg.endFail);
        } else if( actionId === constants.GOTO_SERVICE_PAGE_ACTION_ID ) {
            defaultActions[actionId](arg.failCode, arg.failMessage);
        } else if( actionId === constants.SET_READY_ACTION_ID ) {
            defaultActions[actionId]();
        } else{
            console.error(`ActionId ${actionId} Unknown`)
        }
    }



    function goToServicePage(failCode, failMessage) {
        shiftToState(servicePageName);
        appState.fail = true;
        appState.failMessage = failMessage;
        appState.failCode = failCode;
    }

    function flowTransition(newFlowIndex, nextStepEnabled){
        alternativeFlowIndex = newFlowIndex;
        alternativeFlowNextStepEnabled = nextStepEnabled;
    }

    function runAlternativeFlow( flowId, flowArg ) {

        if( alternativeFlows[flowId]) {

            currentAlternativeFlowId = flowId;
            alternativeFlowIndex = 0;
            alternativeFlowNextStepEnabled = false;
            alternativeFlows[flowId].initFlow(appState.alternativeFlowContext, flowArg, flowTransition, closeAlternativeFlow, onComplete);

            if( constants.DEBUG_SESSION_STATE ) {
                history.pushState({name: 'runAlternativeFlow'}, "pushState runAlternativeFlow", null);
                console.log(`History.state after pushState runAlternativeFlow: ${history.state}`);
            }
            alternativeFlowRunning = true;
        } else {
            console.error("AlternativeFlow Unknown");
            return;
        }
    }

    function closeAlternativeFlow(){
        alternativeFlowRunning = false;
    }

    function getPageIdByName(name){
        let pageId = null;
        for (let i = 0; i < flow.length && pageId === null; i++) {
            if (flow[i].name === name) {
                pageId = i;
            }
        }
        return pageId
    }

    function shiftToState(name) {
        if(typeof name === "number") {
            _shiftToStateByPageId(name);
        } else {
            const pageId = getPageIdByName(name);
            if (pageId !== null) {
                _shiftToStateByPageId(pageId, true);
            } else {
                console.error("Cannot find selected State : " + name);
                return;
            }
        }
    }

    function _shiftToStateByPageId( shift, absolute=false ) {
        if( typeof shift !== 'number' || (!absolute && currentIdx.stepIdx + shift > pathLength - 1)) {
            return;
        }
        const newIndex = (absolute===false) ? currentIdx.stepIdx + shift : shift;

        if(!config[constants.OPT_DEBUG_WITHOUT_STATUS] && flow[newIndex].checkpointSave){
            checkpointManager.set( flow[newIndex].checkpointSave, (data, success) => {
                if( success){
                    _shiftToState( newIndex, true );
                } else {
                    _shiftToState( newIndex, true );
                }
            })

        } else {
            _shiftToState( newIndex, true );
        }

    }


    function _shiftToState( shift, absolute=false ) {
        if( typeof shift !== 'number' || (!absolute && currentIdx.stepIdx + shift > pathLength - 1)) {
            return;
        }
        currentIdx.stepIdx = (absolute===false) ? currentIdx.stepIdx + shift : shift;
        updateStateChartVariables();
        if( checkInvalidField("loader") ){
            appState.state = constants.STATE_READY;
        } else {
            appState.state = constants.STATE_LOADING;
            appState.nextStateEnabled = false;
            flow[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback);
    // Should be null because we haven't modified the history stack yet
            if( constants.DEBUG_SESSION_STATE ){
                history.pushState({name: '_shiftToState'}, "pushState _shiftToState", null);
                //console.log(`History.state after pushState _shiftToState: ${history.state}`);
            }
        }
    }

    function checkCompletedStep(currentStep){
        for(let i=0; i<=currentIdx.stepIdx; i++){
            if(currentStep === flow[i].name ){
                return true;
            }
        }
        return false;
    }

    function getButtonAction(buttonAction){
        if( typeof buttonAction === "string" && defaultActions[buttonAction]) {
            return defaultActions[buttonAction];
        } else if (buttonAction.handler && typeof buttonAction.handler === "function"){
            buttonAction.handler(buttonAction.arg, appState, shiftToState, fallback);
        } else {
            return undefined;
        }
    }

      // jQuery
        window.addEventListener('popstate', function (e) {
            //let state = e.state;
            onBack();
        });

</script>

<div class="content h-100">
    {#if !alternativeFlowRunning && (flow[currentIdx.stepIdx].title || flow[currentIdx.stepIdx].subtitle)}
        <div class="row">
            <div class="col-12 text-center">
                {#if flow[currentIdx.stepIdx].title}
                    <div class="context-abstract">
                        <h3 class="area-heading">{alternativeFlowRunning ? "" : flow[currentIdx.stepIdx].title}</h3>
                    </div>
                {/if}
                {alternativeFlowRunning ? "" : flow[currentIdx.stepIdx].subtitle}
            </div>
        </div>
    {/if}
        <svelte:component this={currentWrapper} {appState}
                  bind:nextStateEnabled={appState.nextStateEnabled}
                  {...currentWrapperProps}>

            {#if alternativeFlowRunning}
                <svelte:component this={alternativeFlows[currentAlternativeFlowId].content}
                                  bind:appState={appState}
                                  bind:flowIndex={alternativeFlowIndex}
                                  bind:alternativeFlowNextStepEnabled={alternativeFlowNextStepEnabled}
                                  runAction={runAction}/>

            {:else if appState.state === constants.STATE_LOADING || appState.state === constants.STATE_ENDING}
                    <div class="center-loader">
                        {#if appState.state === constants.STATE_LOADING ? flow[currentIdx.stepIdx].loaderMessage : flow[currentIdx.stepIdx].handlerMessage }
                            <div class="loading-message">
                                {appState.state === constants.STATE_LOADING ? flow[currentIdx.stepIdx].loaderMessage : flow[currentIdx.stepIdx].handlerMessage }
                            </div>
                        {/if}
                        <div>
                            <Loader/>
                        </div>
                    </div>

            {:else if appState.state === constants.STATE_READY}
                <svelte:component this={currentStep.content} {appState}
                                  {...currentStep.props}
                                  bind:nextStateEnabled={appState.nextStateEnabled}
                                  runAlternativeFlow={runAlternativeFlow}
                                  runAction={runAction}/>

            {/if}

        </svelte:component>
    </div>

{#if flow[currentIdx.stepIdx].buttons && flow[currentIdx.stepIdx].buttons.length  && flow[currentIdx.stepIdx].buttons.length > 0}
    <div class="bottom-buttons fixed fixed-bottom" style="position:fixed;z-index: 999;">
        {#if !alternativeFlowRunning}
            {#each flow[currentIdx.stepIdx].buttons as button}
                <a href={undefined}
                   class="btn absolute-action fixed-bottom-button"
                   class:btn-yellow={button.primary}
                   class:btn-default={!button.primary}
                   on:click={getButtonAction(button.action)}
                   class:disabled={(!appState.nextStateEnabled && !config[constants.OPT_DEBUG]) && button.class && button.class === constants.BUTTON_CLASS_CONTINUE}>
                    {button.label}
                </a>
            {/each}
        {:else}
            <div class="buttons-container">
                {#each alternativeFlows[currentAlternativeFlowId].buttons as button}
                    {#if appState.alternativeFlowContext.functions[button.visibility] && appState.alternativeFlowContext.functions[button.visibility]() && typeof alternativeFlowIndex !== 'undefined'}
                        <a href={undefined}
                           class:btn-yellow={button.primary}
                           class:btn-default={button.secondary}
                           class="btn btn-default absolute-action fixed-bottom-button"
                           on:click={appState.alternativeFlowContext.actions[button.action] }
                           class:disabled={!appState.alternativeFlowContext.functions[button.enabled]()}
                        >
                            {button.label}
                        </a>
                    {/if}
                {/each}
            </div>
        {/if}
    </div>
{/if}
<style>
    .center-loader{
        min-height: 50vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .loading-message{
        color: #0047bb;
        font-size: 23px;
        white-space: pre;
        margin-bottom: 2rem;
    }

    .buttons-container{
        flex-direction: column;
        width: 100%;
        display: inline-flex;
    }

    .fixed-bottom-button{
        margin-top: 0.5rem;
    }

</style>





