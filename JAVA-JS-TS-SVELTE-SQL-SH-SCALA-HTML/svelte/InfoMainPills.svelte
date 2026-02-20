<script>
    import MainPills from "./MainPills.svelte";

    export let title;
    export let text;
    export let infoImage = "/risorse_dt/condivise/immagini/icone/icone-wizard-2x/ico-wizard-notice@2x.png";
</script>
<MainPills>
    <div class="wizard-card wizard-card-last" style="{`background-image: url(${infoImage});`}">
        <div class="wizard-card-inner">
            <p class="h4 spacer-xs-top-0"><strong>{title}</strong></p>
            <p>{text}</p>
        </div>
    </div>
</MainPills>
