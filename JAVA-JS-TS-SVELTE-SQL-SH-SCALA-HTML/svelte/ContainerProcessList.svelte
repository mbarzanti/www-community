<script>
    import SimplePage from "../../SvelteKit/layout/SimplePage.svelte";

    export const progress_list = undefined;
    import Loader from './../../SvelteKit/elements/Loader.svelte';
    import {TASK_STATUS, SUBTASK_STATUS, INTERVAL_TIMEOUT, MAX_REQUESTS, BPM_DICTIONARY, PREPROCESSING_SUBTASK} from "./constants";
    import {createEventDispatcher, onMount} from "svelte";
    import ProcessList from './ProcessList.svelte';
    import axios from 'axios';
    import * as utils from './../../commons/utils';
    import * as constants from './../../commons/constants';
    import Axios from "axios";
    import {REDIRECT_STATE_KEY} from "../../commons/constants";


    const formEventDispatcher = createEventDispatcher();
    export let count = 0;
    export let processList = [];
    export let params = {};

    let submitted = false;
    var values_map = {};
    let validation_map = {};
    let disable_complete = true;
    let timeout = false;
    let isLoading = true;
    let urlRedirect = '#';
    let redirectState;

    let flagErrors = false;

    // Check if there is FUNNEL INSTANCE ID
    if (!params.fid) {
        utils.redirectTo(constants.ERROR_PAGE_REF_UP, constants.ERROR_PAGE_ID_BPM_ERROR);
    }
    let timer;

    onMount(() => {
        redirectState = utils.popRedirectState(REDIRECT_STATE_KEY);
        Axios.defaults.headers["Pragma"] = "no-cache";
        timer = setInterval(function () {
            checkStatusAxiosPolling();
        }, INTERVAL_TIMEOUT);
    });

    $: if (count >= MAX_REQUESTS) {
        timeout = true;
    }


    function checkStatusAxiosPolling() {
        if (!timeout) {
            count += 1;
            axios({
                url: `/${utils.getContextPath()}/api/v1/funnels/${params.fid}/steps/${utils.getContextPath()}`,
                method: 'get',
                headers: {"groupid": "admin"},
            }).then(
                    (res) =>handleStatus(res.data)
            ).catch(
                    function (error) {
                        clearInterval(timer);
                        console.log(error)
                        utils.redirectTo(constants.ERROR_PAGE_REF_UP, constants.ERROR_PAGE_ID_BPM_ERROR);
                    }
            );
        }
    }
    function handleStatus(res){
        processList = res.task.progress_list;
        useDictionary();
        let status = res.task.status.toUpperCase();
        if (status === TASK_STATUS.LOADING) {
            isLoading = true;
        } else if (status === TASK_STATUS.RUNNING) {
            isLoading = ((res.task.progress_list.length === 1) && res.task.progress_list[0].name === PREPROCESSING_SUBTASK)
                || ((res.task.progress_list.length === 2) && res.task.progress_list[1].name === PREPROCESSING_SUBTASK);
        } else if (status === TASK_STATUS.ERROR || status === TASK_STATUS.FAILED) {
            isLoading = false;
            timeout = true;
            clearInterval(timer);
            flagErrors = true;
            jQuery([document.documentElement, document.body]).animate({
                scrollTop: jQuery("body").offset().top
            }, 500);
        } else if (status === TASK_STATUS.COMPLETED) {
            isLoading = false;
            timeout = true;
            clearInterval(timer);
            utils.redirectToWithState(
                "",
                {
                    state: {
                        key: REDIRECT_STATE_KEY,
                        content: redirectState
                    },
                    hash: constants.SUCCESS_PAGE_REF_UP
                }
            );
        }
    }

    function useDictionary(){
       let list = [];
       for(let elem of processList){
           if(BPM_DICTIONARY[elem.name]){
            elem.name = BPM_DICTIONARY[elem.name];
          }
          list.push(elem);
       }
     processList = list;
    }

</script>

<SimplePage>
  {#if isLoading}
     <div class="container width960 container-stepper-circle">
          <div class="padd-conf-tab attivazione-page sm-activation-recap tac">
            <p class="sm-act-notice">
              Attendi qualche secondo
            </p>
            <Loader/>
          </div>
     </div>
  {:else}
     <ProcessList progress_list={processList} flag_errors={flagErrors}/>
  {/if}
</SimplePage>