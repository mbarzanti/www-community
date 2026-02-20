<script>
    import HelpButton from "./HelpButton.svelte";

    export let title = "Hai bisogno di aiuto ?";
</script>

<div class="col-xs-12 col-sm-6 col-sm-pull-6 col-md-pull-0 col-md-12 border-sm-right border-md-right-not">
    <div class="bracket-pills">
        <h5 class="h6">{title}</h5>
        <div class="btn-container">
            <slot>
                <!--DEFAULT-->
                <HelpButton href="https://www.poste.it/chiamaci.html"
                            src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-chiamaci.png"
                            srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-chiamaci@2x.png 2x">
                    {"Chiamaci"}
                </HelpButton>
                <HelpButton href="https://www.poste.it/scrivici.html"
                            src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-scrivici.png"
                            srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-scrivici@2x.png 2x">
                    {"Scrivici"}
                </HelpButton>
                <HelpButton href="https://www.poste.it/cerca/index.html#/vieni-in-poste"
                            src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-vieni-in-poste-cerca-up.png"
                            srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-vieni-in-poste-cerca-up@2x.png 2x">
                    {"Vieni a trovarci"}
                </HelpButton>
            </slot>
        </div>
    </div>

</div>