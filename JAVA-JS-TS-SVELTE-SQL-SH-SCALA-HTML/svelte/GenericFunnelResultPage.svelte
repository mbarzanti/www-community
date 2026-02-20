<script>
    import {onMount} from 'svelte';
    import SidebarContent from "./StaticSidebar.svelte";

    export let resultTitle;
    export let resultText;
    export let resultType;
    export let innerContentComponent;

    let resultTypeCssClass;

    onMount(() => {
        resultTypeCssClass = setCssClass(resultType)
    });

    function setCssClass(resultType) {
        switch (resultType) {
            case "info":
                return "main-result-error";
            case "warning":
                return "main-result-warning";
            default:
                return "main-result-error";
        }
    }
</script>

<div class="content content-main border-xs-bottom">
    <div class="container">
        <div class="row">
            <div class="col-md-12" id="accessibility-anchor">
                <div class="row">
                    <div class="col-md-9 col-structure">
                        <div id="main">
                            <div class="main-pills">
                                <div class="main-pills-wrap">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <div class="main-result {resultTypeCssClass}">
                                                <div class="main-result-wrap">
                                                    <div class="result-heading">
                                                        {#if resultTitle}<h1>{resultTitle}</h1>{/if}
                                                    </div>
                                                    <div class="result-body">
                                                        <div class="box-editable-area box-editable-spacing">
                                                            {#if resultText}<p>{resultText}</p>{/if}
                                                            {#if innerContentComponent}<svelte:component this={innerContentComponent}/>{/if}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <SidebarContent />
                </div>
            </div>
        </div>
    </div>
</div>