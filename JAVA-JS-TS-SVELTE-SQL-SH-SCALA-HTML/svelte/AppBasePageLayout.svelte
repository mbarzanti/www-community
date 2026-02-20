
<script>
    import BasePageLayout from "../UiKitLite/layouts/BasePageLayout.svelte";
    import Sidebar from "../components/common/Sidebar.svelte";
    import {createEventDispatcher} from "svelte";

    export let enableMainPills = true;
    const dispatch = createEventDispatcher();

</script>

<BasePageLayout enableMainPills="{enableMainPills}">
    <div slot="top-content">
        <slot name="top-content" />
    </div>
    <div slot="default">
        <slot/>
    </div>
    <div slot="bottom-content">
        <slot name="bottom-content" />
    </div>
    <div slot="sidebar">
        <Sidebar on:init={() => {dispatch('init')}}/>
    </div>
</BasePageLayout>
