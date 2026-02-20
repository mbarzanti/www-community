<script>
    import {onMount} from 'svelte';
    import {setupI18n, isLocaleLoaded} from './locale/i18n';
    import Navbar from "./components/ui/navbar.svelte";
    import Footer from "./components/ui/footer.svelte";
    import Loader from './components/ui/loader.svelte';
    import Router from './route/Router.svelte';
    import api from './api/api';
    import {redirectTo} from "./commons/utils";
    import * as constants from "./commons/constants";

    let ready = false;
    export let channel;

    onMount(() => {
            setupI18n();
            const init = (success)=>{
                if(success){
                    ready=true;
                } else {
                    redirectTo(constants.ERROR_PAGE_REF_UP, constants.ERROR_PAGE_ID_GENERIC_ERROR);
                }
            }
            api.ms.configurations.getNewTraceId(init);
        });

</script>

<Navbar title={"Post Vendita"}/>

{#if !$isLocaleLoaded || !ready}
    <Loader/>
{:else}
    <Router {channel}/>
{/if}
<Footer/>
