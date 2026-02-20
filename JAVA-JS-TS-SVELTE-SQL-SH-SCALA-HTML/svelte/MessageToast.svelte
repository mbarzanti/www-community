<script>
    export let text;
    export let success;
    export let visible;
</script>

<style>
    .file-box-invalid {
        border: 1px solid #e60017;
        color: #73000c;
        background-color: #FFCDD2;
        font-weight: 700;
        width: 100%;
        margin-top: 20px;
        margin-bottom: 20px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }

    .file-box-success {
        border: 1px solid #439446;
        color: #224a23;
        background-color: #c8e6c9;
        font-weight: 700;
        width: 100%;
        margin-top: 20px;
        margin-bottom: 20px;
        text-overflow: ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
</style>

{#if visible}
    {#if !success}
    <div class="file-box-invalid">
        <svg
            version="1.1"
            id="Layer_1"
            xmlns="http://www.w3.org/2000/svg"
            x="0"
            y="0"
            width="40"
            height="40"
            xml:space="preserve"
            ><g id="ICONS"
                ><g id="Close"
                    ><g
                        id="Group-6"
                        fill="none"
                        stroke="#73000c"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        ><path
                            id="Stroke-1"
                            d="M27.998 12L12 28"
                        /><path
                            id="Stroke-3"
                            d="M27.998 28L12 12"
                        /></g
                    ></g
                ></g
            ></svg
        >
        {text}
    </div>
    {:else}
    <div class="file-box-success">
        <svg
            version="1.1"
            id="Layer_1"
            xmlns="http://www.w3.org/2000/svg"
            x="0"
            y="0"
            width="40"
            height="40"
            xml:space="preserve"
            ><g id="ICONS"
                ><g id="Tick"
                    ><g id="Group-7"
                        ><g id="tick" transform="translate(10 13)"
                            ><path
                                id="Path-4"
                                fill="none"
                                stroke="#224a23"
                                stroke-linecap="round"
                                font-weight=700
                                d="M.5 7.5l5.846 5.575L19.071.351"
                            /></g
                        ></g
                    ></g
                ></g
            ></svg
        >
        {text}
    </div>
    {/if}
{/if}