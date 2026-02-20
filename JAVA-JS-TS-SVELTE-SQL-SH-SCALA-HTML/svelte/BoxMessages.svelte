<script>
    import {getBoxMessagesSrc} from "./utils";

    export let type;
    export let title;
    export let text = "";
    export let little = false;

    let setting;

    $:{
        setting = getBoxMessagesSrc(type);
    }

</script>
<div class="col-xs-12">
    <div class="box-advice box-messages {setting.cssClass}">
        <div class="box-heading">
            {#if !little}
                <h3 class="area-heading">
                    <img alt="{setting.alt}"
                         src="{setting.src}"
                         srcset="{setting.srcset}"
                         class="spacer-xs-right-10"
                         style="vertical-align: inherit;">
                    {title}
                </h3>
            {:else}
                <img alt="{setting.alt}"
                     src="{setting.src}"
                     srcset="{setting.srcset}"
                     class="spacer-xs-right-10"
                     style="vertical-align: inherit;">
                {title}
            {/if}
        </div>
        <div class="box-body">
            {#if text && text !== ""}
                <p>{text}</p>
            {/if}
            <slot/>
        </div>
    </div>
</div>
