<script>
    import {MESSAGES_TYPE} from "./utils";

    export let type;
    export let text;
    export let style = "";

    let cssClass;

    $:{
        switch (type) {
            case MESSAGES_TYPE.ERROR:
                cssClass = "main-messages-error";
                break;

            case MESSAGES_TYPE.WARNING:
                cssClass = "main-messages-warning";
                break;

            case MESSAGES_TYPE.SUCCESS:
                cssClass = "main-messages-success";
                break;

            case MESSAGES_TYPE.INFO:
                cssClass = "main-messages-info";
                break;

            default:
                cssClass = "main-messages-info";
                break;
        }
    }

</script>
<div class="col-xs-12">
    <div class="main-messages {cssClass}">
        <div class="main-messages-wrap">
            <div class="messages-heading" style="{style}">
                <p>{text}</p>
            </div>
        </div>
    </div>
</div>
