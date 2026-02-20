<script>
    import {documentsExampleImageURL} from "../commons/constants/documents";
    import * as PhotoSwipeHelper from "../ui/photoSwipeHelper";

    export let message = "Per sapere cosa intendiamo per Fronte e Retro del documento clicca";
    export let clickableMessage = "qui";
    export let document;

    function getDocumentsExampleImageURL(){
        const key = `${document.type.code}_${document.subType}`;
        return documentsExampleImageURL[key];
    }

    function openImage(event) {
        let url = getDocumentsExampleImageURL();
        let items = [{
            src: url,
            title: "",
            w: 0,
            h: 0
        }];
        PhotoSwipeHelper.show(items);
    }
</script>

<div style="margin-top: 1rem; margin-left:8px; font-style: italic; font-weight: 700;">
    {message}
    <div on:click={openImage} style="cursor:pointer">
        <p style="color:#0047bb;">{clickableMessage}</p>
    </div>
</div>