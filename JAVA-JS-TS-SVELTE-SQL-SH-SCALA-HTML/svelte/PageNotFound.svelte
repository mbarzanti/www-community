<script>
    import ErrorPage from './ErrorPage.svelte';
    import {ERROR_PAGE_ID_NOT_FOUND} from "../../../commons/constants";
    import * as constants from "../../../commons/constants";

    export let kit = constants.KIT.UP;
</script>

<ErrorPage
        params={{id:ERROR_PAGE_ID_NOT_FOUND}}
        {kit}
/>