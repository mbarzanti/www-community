<script>
    import BoxMessages from "./BoxMessages.svelte";
    import {FIELD_MESSAGES_TYPE, MESSAGES_TYPE} from "./utils";
    import mustace from "mustache";

    export let title = "Sono stati riscontrati alcuni errori.";
    export let text = "";
    export let messages;
    export let anchorTag = "{{component}}";

    function anchorOnClick(e) {
        const element = document.getElementById(e.target.attributes.ref.value);
        element.scrollIntoView({
            behavior: "smooth",
            block: "center",
            inline: "nearest"
        });
    }

    function renderMessagePrefix(messageItem){
        let text = messageItem.template.split(anchorTag)[0];
        let view = {
            id: messageItem.id,
            label: messageItem.label,
            ...(messageItem.props)
        }
        return mustace.render(text, view);
    }

    function renderMessageSuffix(messageItem){
        let text = messageItem.template.split(anchorTag)[1];
        let view = {
            id: messageItem.id,
            label: messageItem.label,
            ...(messageItem.props)
        }
        return mustace.render(text, view);
    }

    function renderMessage(messageItem){
        let view = {
            id: messageItem.id,
            label: messageItem.label,
            ...(messageItem.props)
        }
        return mustace.render(messageItem.template, view);
    }

</script>
<BoxMessages {text} {title} type={MESSAGES_TYPE.ERROR}>
    {#if messages && messages.length > 0}
        <ul>
            {#each messages as messageItem (messageItem.id)}
                {#if messageItem.onlyText}
                    <li>{renderMessage(messageItem)}</li>
                {:else}
                    <li>
                        {renderMessagePrefix(messageItem)}<a
                            ref="{messageItem.id}" style="cursor: pointer" on:click={anchorOnClick}>{messageItem.label}</a>{renderMessageSuffix(messageItem)}
                    </li>
                {/if}
            {/each}
        </ul>
    {/if}
</BoxMessages>