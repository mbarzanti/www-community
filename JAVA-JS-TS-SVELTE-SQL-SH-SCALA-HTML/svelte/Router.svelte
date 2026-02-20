<script>
    import Router from 'svelte-spa-router';
    import {wrap} from 'svelte-spa-router/wrap';
    import IndexPage from "../pages/index.svelte";
    import ErrorPage from "../pages/service/error.svelte";
    import ContainerProcessList from "../pages/bpm/ContainerProcessList.svelte";
    import EndUP from "../pages/service/end.svelte";

    import * as constants from "./../commons/constants";

    export let channel = constants.CHANNEL_BO;

    let routes = new Map();
    if(channel === constants.CHANNEL_BO){
        routes.set('/', IndexPage);
        routes.set('/debug/:id', IndexPage);
        routes.set(`/${constants.SUCCESS_PAGE_REF_UP}`, wrap({
            component: EndUP,
            props: {
                homeAddress: "/feu-after-sales"
            }
        }));
        routes.set(`/${constants.ERROR_PAGE_REF_UP}/:id`, wrap({
            component: ErrorPage,
            props: {
                homeAddress: "/feu-after-sales"
            }
        }));
        routes.set('*', ErrorPage);
    } else if (channel === constants.CHANNEL_BO_BPM){
        routes.set(`/${constants.SUCCESS_PAGE_REF_UP}`, wrap({
            component: EndUP,
            props: {
                homeAddress: "/feu-after-sales"
            }
        }));
        routes.set(`/${constants.ERROR_PAGE_REF_UP}/:id`, wrap({
            component: ErrorPage,
            props: {
                homeAddress: "/feu-after-sales"
            }
        }));
        routes.set('/:fid', ContainerProcessList);
        routes.set('*', ErrorPage);
    }

</script>

<Router {routes}/>
