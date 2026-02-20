<script>

    import {createEventDispatcher} from "svelte";

    import {hideDashboard, disableCessionButton} from "../../stores";

    const dispatch = createEventDispatcher();
</script>
<div class="row">
    <div class="col-xs-12 col-sm-6 col-sm-push-6 col-md-push-0 col-md-12">
        <div class="bracket-pills border-md-bottom">

            {#if $hideDashboard == false}
                <button on:click={() => {dispatch('init')}} class="btn btn-primary">RICHIEDI CESSIONE</button>
            {/if  }
            <h5 class="h6">Domande collegate</h5>
            <ul class="list-unstyled">
                <li><a target="_blank" href="https://www.poste.it/faq-cessione-del-credito-di-imposta.html?wt.ac=1476526738621#quali_crediti_posso_cedere-1476526734089">Quali crediti posso cedere?</a></li>
                <li><a target="_blank" href="https://www.poste.it/faq-cessione-del-credito-di-imposta.html?wt.ac=1476526738621#quale_importo_devo_inserire_ne-1476526735056">Quale importo devo inserire nella richiesta di cessione?</a></li>
                <li><a target="_blank" href="https://www.poste.it/faq-cessione-del-credito-di-imposta.html?wt.ac=1476526738621#di_quale_documentazione_ho_bis-1476526735312">Di quale documentazione ho bisogno per presentare la richiesta?</a></li>
            </ul>
        </div>
    </div>
    <div class="col-xs-12 col-sm-6 col-sm-pull-6 col-md-pull-0 col-md-12 border-sm-right border-md-right-not">
        <div class="bracket-pills">
            <h5 class="h6">Hai bisogno di aiuto</h5>
            <div class="btn-container">
                <a href="https://www.poste.it/chiamaci.html?wt.ac=1476515410100" class="btn btn-sm btn-secondary btn-expand" target="_blank">
                    <img class="spacer-xs-right-10" src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-chiamaci.png" srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-chiamaci@2x.png 2x">Chiamaci
                </a>
                <br class="hidden-xs">
                <a href="https://www.poste.it/scrivici.html?wt.ac=1476515410100" class="btn btn-sm btn-secondary btn-expand" target="_blank">
                    <img class="spacer-xs-right-10" src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-scrivici.png" srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-scrivici@2x.png 2x">Scrivici
                </a>
                <br class="hidden-xs">
                <a href="https://www.poste.it/cerca/index.html#/vieni-in-poste?wt.ac=1476515410100" class="btn btn-sm btn-secondary btn-expand" target="_blank">
                    <img class="spacer-xs-right-10" src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-vieni-in-poste-cerca-up.png" srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-vieni-in-poste-cerca-up@2x.png 2x">Vieni in poste
                </a>
            </div>
        </div>
    </div>
</div>
