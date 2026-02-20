<script>
  export let id = null
  export let message = []
</script>

<div {id} class="box-advice box-messages box-error">
  <div class="box-heading">
    <h3 class="area-heading">
      <img
        alt="messaggio di errore"
        src="/risorse_dt/condivise/immagini/icone/ico-applicative-alert.png"
        srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-alert@2x.png
        2x"
        class="spacer-xs-right-10" />
      Sono stati riscontrati alcuni errori.
    </h3>
  </div>
  <div class="box-body">
    <p>
      Controlla i campi inseriti per correggere gli errori segnalati.
      {#each message as value}
        <br />
        {value}
      {/each}
    </p>
  </div>
</div>
