<script>
    import {onMount} from 'svelte';

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
            case "success":
                return "main-result-success";
            case "info":
                return "main-result-info";
            case "warning":
                return "main-result-warning";
            default:
                return "main-result-error";
        }
    }
</script>

<div class="main-result {resultTypeCssClass}">
    <div class="main-result-wrap">
        <div class="result-heading">
            {#if resultTitle}<h1>{resultTitle}</h1>{/if}
        </div>
        <div class="result-body">
            <div class="box-editable-area box-editable-spacing">
                {#if resultText}<p>{resultText}</p>{/if}
                <slot/>
            </div>
        </div>
    </div>
</div>