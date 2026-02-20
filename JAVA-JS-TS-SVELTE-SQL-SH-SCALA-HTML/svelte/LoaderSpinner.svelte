<script>
    import {afterUpdate} from 'svelte';
    import {getUniqueId} from "../../suit/uikit/utils";

    export let showModal = false;
    export let text = "Caricamento in corso";
    export let id=getUniqueId();

    afterUpdate(() => {
        if (showModal) {
            jQuery("#" + id).modal('show');
        } else {
            jQuery("#" + id).modal('hide');
        }
    });

</script>

<div id="{id}" style="display: none">
    <div class="fullscreen">
        <div class="loader" style="width: 80px; height: 80px;"></div>
        <span class="loaderTxt">{text}</span></div>
</div>

<style>

    .loader {
        border: 7px solid #fff;
        border-radius: 50%;
        border-top: 7px solid #eedc00;
        -webkit-animation: spin 2s linear infinite;
        animation: spin 2s linear infinite;
        opacity: 1;
        position: relative;
    }

    .fullscreen {
        position: fixed;
        top: 0;
        left: 0;
        z-index: 4050;
        display: flex;
        flex-direction: column;
        width: 100%;
        height: 100%;
        overflow: hidden;
        outline: 0;
        justify-content: center;
        align-items: center;
        background-color: rgba(0, 0, 0, 0.7);
    }

    @-webkit-keyframes spin {
        0% {
            -webkit-transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
        }
    }

    @keyframes spin {
        0% {
            transform: rotate(0deg);
        }
        100% {
            transform: rotate(360deg);
        }
    }

    .loaderTxt {
        color: white;
        margin-top: 10px;
    }
</style>
