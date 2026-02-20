<script>

    import {onMount} from 'svelte';
    import CircleStep from "./kit/elements/CircleStep.svelte";
    import {closeModal, openModal} from './kit/sveltekit';
    import Loader from "./kit/elements/Loader.svelte";
    import SimplePage from "./kit/layout/SimplePage.svelte";
    import Modal from "./kit/mobileForms/Modal.svelte";
    import {ENDPOINTS} from "../endpoints";
    import {_} from 'svelte-i18n'
    import SidebarStaticComponent from "../components/StaticSidebar.svelte";
    import {STEPS, TICKET_PAGE_ID} from "../commons/constants/pageOrchestrator";
    import {RETRY_STATUS_ERRORS} from "../commons/constants/errors";
    import {createTicket} from "./utils";

    // const DEBUG = false;

    export let debugParam = 0;

    let DEBUG = debugParam > 1;
    let DEBUG_WITHOUT_STATUS = debugParam > 2;
    let DEBUG_WITHOUT_FLOW = debugParam > 0;


    let currentIdx = {stepIdx: 0};
    let pathLength;
    let confirmModal;

    let currentStep;
    $:currentStep = path[currentIdx.stepIdx] === undefined ? [] : path[currentIdx.stepIdx];

    let currentStepName;

    const STATE_LOADING = 0;
    const STATE_READY = 1;
    const STATE_ENDING = 2;

    let appState = {
        context: {},
        globalContext: {},
        state: STATE_LOADING,
        fail: false,
        failMessage: "",
        error: undefined,
        onSuccessMessage: "",
        failCode: 0,
        nextStateEnabled: false,
        smsReSendEnabled: false,
        isLoading:false, //Retry
        stepTitle:"",
        continueMessage:"",
        alternativeFlowContext: {},
        runAlternativeFlow: runAlternativeFlow
    };


    export let path = {};
    export let stepsPath = {};
    export let alternativeFlows = {};
    export let checkpointManager = {};
    export let initContext = {};

    let numberOfRetry = 1;

    let alternativeFlowRunning = false;
    let currentAlternativeFlowId;

    let alternativeFlowIndex = 0;
    let alternativeFlowNextStepEnabled = false;
    let ticketModal;

    const init = () => {
        updateStateChartVariables();
        if( checkInvalidField("loader") ){
            appState.state = STATE_READY;
        } else {
            appState.state = STATE_LOADING;
            appState.isLoading= true; //Retry
            path[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback, openTicket);
        }
    };


    function loadCheckPoint(status, appState, fallback, openTicket, exitIndex, index=0){
        if( exitIndex < index ){
            init();
        } else {
            const loadNextCheckPoint = () => {
                if(index < exitIndex) {
                    index++;
                    checkpointManager.context[status][index](appState, ()=>{}, loadNextCheckPoint, fallback, openTicket);
                }else{
                    init();
                }
            };
            checkpointManager.context[status][index](appState, ()=>{}, loadNextCheckPoint, fallback, openTicket);
        }
    }


    onMount(() => {
        pathLength = path.length;
        Object.assign(appState.globalContext , initContext);
        appState.state = STATE_LOADING;
        const onMountGetStatus = (data, success) => {
            if(success && data.status && !DEBUG_WITHOUT_STATUS){
                let found = false;
                if( data.status.localeCompare(checkpointManager.start) === 0){
                    init();
                } else {
                   for(let i=0; i<pathLength; i++){
                        if( path[i].checkpointLoad && path[i].checkpointLoad.localeCompare(data.status) === 0 && checkpointManager.context[data.status]){
                            currentIdx.stepIdx = i;
                            const exitIndex = checkpointManager.context[data.status].length - 1;
                            loadCheckPoint(data.status, appState, fallback, openTicket, exitIndex);
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
    });

    function updateStateChartVariables() {
        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
        currentStepName = path[currentIdx.stepIdx].name;
        appState.context = {};
        if(path[currentIdx.stepIdx].props){
            Object.assign(appState.context, path[currentIdx.stepIdx].props);
        }
        appState.fail = false;
        appState.failMessage = "";
        appState.onSuccessMessage = "";
        appState.failCode = 0;
        appState.nextStateEnabled = false;
        appState.smsReSendEnabled = false;
        alternativeFlowRunning = false;
        numberOfRetry = 1;
        appState.stepTitle = undefined;
        if(path[currentIdx.stepIdx].name !== TICKET_PAGE_ID){
            stepsPath = stepsPath;
        }
    }

    function fallback(failCode, failMessage, data, endFail=false){

        if(data && RETRY_STATUS_ERRORS.includes(data.status) && appState.isLoading && numberOfRetry < ENDPOINTS.RETRY_NUMBER)
        {
            appState.state = STATE_LOADING;
            numberOfRetry = numberOfRetry + 1;
            path[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback, openTicket);
        }
        else {
            appState.state = STATE_READY;
            jQuery([document.documentElement, document.body]).animate({
                scrollTop: jQuery("body").offset().top
            }, 500);
            appState.fail = true;
            appState.failMessage = failMessage;
            appState.failCode = failCode;
            appState.nextStateEnabled = false;
            appState.isLoading = false;
            appState.continueMessage ="";
            appState.stepTitle ="";
            if (endFail){
                appState.errorCode = failCode;
                openTicket(false,true)
            }
        }

    }

    function setReady(){
        appState.state = STATE_READY;

    }

    function checkInvalidField(field){
        return typeof path[currentIdx.stepIdx][field] === 'undefined' || path[currentIdx.stepIdx][field] === null
    }

    function onComplete() {
        appState.fail =false;
        appState.isLoading = false;
        appState.stepTitle ="";
        appState.continueMessage ="";
        if( checkInvalidField("handler") || DEBUG_WITHOUT_FLOW ) {
            shiftToState( 1 );
        } else {
            appState.state = STATE_ENDING;
            appState.nextStateEnabled = false;
            path[currentIdx.stepIdx].handler(appState, shiftToState, fallback, openTicket);
        }
    }

    function onMainRepeatableAction(){
        path[currentIdx.stepIdx].mainRepeatableAction(appState, shiftToState, setReady, fallback, openTicket);
    }


    function openTicket(sendTicket=true, showAsError=false) {
        appState.fail =false;
        appState.state = STATE_LOADING;
        appState.nextStateEnabled = false;
        createTicket(appState, shiftToState, fallback, sendTicket, showAsError);
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

            alternativeFlowRunning = true;
        } else {
            console.error("AlternativeFlow Unknown");
            return;
        }
    }

    function closeAlternativeFlow(){
        alternativeFlowRunning = false;
    }

    function shiftToState(name) {
        if (typeof name === "number") {
            _shiftToStateByPageId(name);
        } else {
            const pageId = getPageIdByName(name);
            if (pageId !== null) {
                _shiftToStateByPageId(pageId, true);
            } else {
                console.error("Cannot find selected State : " + name);

            }
        }
    }

    function getPageIdByName(name) {
        let pageId = null;
        for (let i = 0; i < path.length && pageId === null; i++) {
            if (path[i].name === name) {
                pageId = i;
            }
        }
        return pageId
    }

    function _shiftToStateByPageId( shift, absolute=false ) {

        if( typeof shift !== 'number' || (!absolute && currentIdx.stepIdx + shift > pathLength - 1)) {
            return;
        }
        const newIndex = (absolute===false) ? currentIdx.stepIdx + shift : shift;

        if(!DEBUG_WITHOUT_STATUS && path[newIndex].checkpointSave){
            checkpointManager.set( path[newIndex].checkpointSave, (data, success) => {
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
        if(  path[currentIdx.stepIdx].name === TICKET_PAGE_ID){
            updateBar([ 100 ]);
        } else {
            updateBar([ (currentIdx.stepIdx + 1) / path.length * 100 ]);
        }
        updateStateChartVariables();
        if( checkInvalidField("loader") ){
            appState.state = STATE_READY;
        } else {
            appState.state = STATE_LOADING;
            appState.nextStateEnabled = false;
            path[currentIdx.stepIdx].loader(appState, shiftToState, setReady, fallback, openTicket);
        }
    }

    var bar;
    let series = [
        {
            perc: 1 / path.length * 100 ,
            color: '#0047bb'
        }
    ];


    const updateBar = values => {

          if(bar && bar.updatePerc){
              values.forEach((v, i) => bar.updatePerc(v, i));
          }
    };

    function checkCompletedStep(currentStep){
        for(let i=0; i<=currentIdx.stepIdx; i++){
            if(currentStep === path[i].name ){
                return true;
            }
        }
        return false;
    }

</script>
{#if !path[currentIdx.stepIdx].noSteps}
    <div class="content content-progressbar content-white innerspacer-xs-top-10 innerspacer-xs-bottom-10">
        <div class="container">
            <div class="row">
                <div class="col-sm-12">
                    <ol class="steps clearfix">
                        {#if stepsPath.length > 1}
                            {#each stepsPath as task, t}
                                <CircleStep clickable={false}
                                            active={checkCompletedStep(task.name)} done={checkCompletedStep(task.name)}
                                            taskName={task.name} t={t} title={task.title}>
                                </CircleStep>
                            {/each}
                        {/if}
                    </ol>
                </div>
            </div>
        </div>
    </div>
{:else}
    <div  class="content content-progressbar content-white innerspacer-xs-top-10 innerspacer-xs-bottom-10" style="padding-top: 30px !important;"></div>
{/if}
<SimplePage mobileCollapsed={true}>
    <div class="row mobile-collapsed" style="overflow-x: hidden;overflow-y: hidden;">
        <div class="col-md-9 col-structure mobile-collapsed">
            <div id="main mobile-collapsed">
                <div class="main-pills mobile-collapsed">
                    <div class="main-pills-wrap mobile-collapsed">
                        <div class="row mobile-collapsed">
                            <div class="col-md-12 mobile-collapsed">

                                <div class="mobile-height">
                                    {#if !alternativeFlowRunning && (path[currentIdx.stepIdx].title || appState.stepTitle || path[currentIdx.stepIdx].subtitle)}
                                        <div class="row mobile-title">
                                            <div class="col-sm-12 mobile-collapsed">
                                                {#if path[currentIdx.stepIdx].title || appState.stepTitle}
                                                    <div class="context-abstract">
                                                        <h3 class="area-heading">{alternativeFlowRunning ? "" : path[currentIdx.stepIdx].title ? path[currentIdx.stepIdx].title: appState.stepTitle }</h3>
                                                    </div>
                                                {/if}
                                                {alternativeFlowRunning ? "" : path[currentIdx.stepIdx].subtitle}
                                            </div>
                                        </div>
                                    {/if}

                                    <div class="row mobile-collapsed">
                                        <div class="col-sm-12 mobile-collapsed">
                                            {#if alternativeFlowRunning}
                                                <svelte:component this={alternativeFlows[currentAlternativeFlowId].content}
                                                                  bind:appState={appState}
                                                                  bind:flowIndex={alternativeFlowIndex}
                                                                  bind:alternativeFlowNextStepEnabled={alternativeFlowNextStepEnabled}
                                                                  openTicket={openTicket}
                                                                  onMainRepeatableAction={onMainRepeatableAction}></svelte:component>
                                            {:else if appState.state === STATE_LOADING}
                                                <div class="center-loader">
                                                    {#if path[currentIdx.stepIdx].loaderMessage}
                                                        <div class="loading-message">
                                                            {path[currentIdx.stepIdx].loaderMessage}
                                                        </div>
                                                    {/if}
                                                    <div>
                                                        <Loader margin={false}/>
                                                    </div>
                                                </div>
                                            {:else if appState.state === STATE_READY}
                                                <svelte:component this={currentStep.content} {appState}
                                                                  bind:nextStateEnabled={appState.nextStateEnabled}
                                                                  openTicket={openTicket}
                                                                  runAlternativeFlow={runAlternativeFlow}
                                                                  onMainRepeatableAction={onMainRepeatableAction}></svelte:component>
                                            {:else if appState.state === STATE_ENDING}
                                                <div class="center-loader">
                                                    {#if path[currentIdx.stepIdx].handlerMessage}
                                                        <div class="loading-message">
                                                            {path[currentIdx.stepIdx].handlerMessage}
                                                        </div>
                                                    {/if}
                                                    <div>
                                                        <Loader margin={false}/>
                                                    </div>
                                                </div>
                                            {/if}
                                        </div>

                                    </div>
                                </div>

                                {#if !path[currentIdx.stepIdx].noButtons}
                                    <div class="row mobile-collapsed" style="overflow-y: hidden;height: auto;">
                                        <div class="col-sm-12  mobile-collapsed" style="overflow-y: hidden;height: auto;">
                                            <div class="bottom-buttons" style="box-shadow:none;">
                                                {#if !alternativeFlowRunning}
                                                    {#if appState.smsReSendEnabled}
                                                        <a href="javascript:void(0)"
                                                           class="btn btn-default absolute-action-left"
                                                           on:click={onMainRepeatableAction}>{path[currentIdx.stepIdx].mainRepeatableActionLabel}</a>
                                                    {/if}
                                                    {#if typeof currentStep.continueMessage !== "undefined"}
                                                        <a href="javascript:void(0)"
                                                           class="btn btn-yellow absolute-action"
                                                           on:click={onComplete}
                                                           class:disabled={(currentIdx.stepIdx === path.length - 1) || (!appState.nextStateEnabled && !DEBUG)}>{currentStep.continueMessage? currentStep.continueMessage: appState.continueMessage}</a>
                                                    {/if}
                                                {:else}
                                                    <div class=" buttons-container">
                                                        {#each alternativeFlows[currentAlternativeFlowId].buttons as button}
                                                            {#if appState.alternativeFlowContext.functions[button.visibility] && appState.alternativeFlowContext.functions[button.visibility]() && typeof alternativeFlowIndex !== 'undefined'}
                                                                <a href="javascript:void(0)"
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
                                            {#if path[currentIdx.stepIdx].ticket}
                                                <div class="help-link" on:click={
                                                        ()=>{
                                                    appState.globalContext.ticket = path[currentIdx.stepIdx].ticket;
                                                    openModal(ticketModal);
                                                    //openTicket();
                                                    }}>
                                                    {$_("support.help")}
                                                </div>
                                            {/if}

                                        </div>
                                    </div>
                                {/if}

                                <Modal bind:modalElement={ticketModal} title={$_('support.modal.title')}>
                                    <div class="mobile-modal-text">{$_('support.modal.text')}</div>
                                    <div class="mobile-modal-buttons">
                                        <a href="javascript:void(0)" class="btn btn-primary mobile-modal-button"
                                           on:click={ ()=>{ openTicket();closeModal(ticketModal);}}>{$_('support.modal.confirm')}</a>
                                        <a href="javascript:void(0)" class="btn btn-secondary mobile-modal-button"
                                           on:click={ ()=>{
                                           closeModal(ticketModal);
                                           }}>{$_('support.modal.cancel')}</a>
                                    </div>
                                </Modal>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <SidebarStaticComponent/>
    </div>
</SimplePage>



<style>
    .center-loader{
        min-height: 80vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .mobile-modal-text{
        font-size: 20px;
        font-weight: 300;
        padding-left: 5vw;
        padding-right: 5vw;
        margin-bottom: 0;
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

    .mobile-step-title{
        color: #222427;
        font-size: 25px;
        font-weight: 500;
        line-height: 40px;
        text-align: center;
        padding-left: 1rem;
        padding-right: 1rem;
    }
</style>





