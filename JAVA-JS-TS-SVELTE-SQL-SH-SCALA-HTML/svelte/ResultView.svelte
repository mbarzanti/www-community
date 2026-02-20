<script>
    import AppBasePageLayout from "../../../uikit/digital/layout/AppBasePageLayout.svelte";
    import Sidebar from "../../../uikit/digital/ui/sidebar/Sidebar.svelte";
    import Empty from "../../../commons/Empty.svelte"
    import MainPills from "../ui/mainContent/MainPills.svelte";

    const defaultProps = {
        sidebar: Sidebar,
        fullPage: true,
        pageLayout: AppBasePageLayout,
        downloadButton: undefined,
        printButton: undefined
    }

    export let title;
    export let text;
    export let additionalButton;
    export let props = {};
    export let success;

    function setProp(prop) {
        if (typeof props[prop] === "undefined") {
            props[prop] = defaultProps[prop];
        }
    }

    $:{
        setProp("sidebar");
        setProp("pageLayout");
        setProp("fullPage");
        setProp("downloadButton");
        setProp("printButton");
    }


</script>
<svelte:component this={props.fullPage ? props.pageLayout : Empty}>
    <div slot="sidebar">
        {#if props.sidebar}
            <svelte:component this={props.sidebar}/>
        {/if}
    </div>

    <MainPills>
        <div slot="content">
            <div class="row">
                {#if props.downloadButton || props.printButton}
                    <div class="col-md-12">
                        <p class="text-right">
                            {#if props.printButton}
                                <span class="applfunction applfunction-print">
                                    <a href="javascript.void(0)" title="{props.printButton.label}"
                                       on:click={props.printButton.action}>{props.printButton.label}</a>
                                </span>
                            {/if}
                            {#if props.downloadButton}
                                <span class="applfunction applfunction-download">
                                    <a href="javascript.void(0)" title="{props.downloadButton.label}"
                                       on:click={props.downloadButton.action}>{props.downloadButton.label}</a>
                                </span>
                            {/if}
                        </p>
                    </div>
                {/if}
                <div class="col-md-12" class:border-sm-bottom={additionalButton && additionalButton.label && additionalButton.onClick}>
                    <div class="main-result" class:main-result-success={success} class:main-result-error={!success}>
                        <div class="main-result-wrap">
                            {#if title && title.length > 0}
                                <div class="result-heading">
                                    <h1>{title}</h1>
                                </div>
                            {/if}
                            {#if text && text.length > 0}
                                <div class="result-body">
                                    <div class="box-editable-area box-editable-spacing">
                                        {@html text}
                                    </div>
                                </div>
                            {/if}
                        </div>
                    </div>
                </div>
            </div>
            {#if additionalButton && additionalButton.label && additionalButton.onClick}
                <div class="row">
                    <div class="col-md-12">
                        <div class="box-survey">
                            <div class="row">
                                <div class="col-sm-push-3 col-sm-6 col-md-push-0 col-md-4 additional-button-container">
                                    <div class="box-editable-area">
                                        <div class="btn-container btn-container-right spacer-xs-top-30 clearfix">
                                            <a class="btn btn-primary btn-expand" style="float: right;" href="javascript.void(0)" on:click={additionalButton.onClick} >{additionalButton.label}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            {/if}
        </div>
    </MainPills>


</svelte:component>
<style>

    @media (min-width: 768px) {
        .additional-button-container{
            float: unset;
        }
    }
    @media (min-width: 992px) {
        .additional-button-container{
            float: right;
        }
    }

</style>