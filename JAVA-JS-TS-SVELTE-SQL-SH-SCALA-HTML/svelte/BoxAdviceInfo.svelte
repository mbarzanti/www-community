<script>
    export let id = null
    export let message
  </script>
  
  <div {id} class="box-advice box-messages" style="border-color: #0047bb;">
    <div class="box-heading" style="display: flex;align-items:center;">
        <img
          alt="messaggio di errore"
          src="/risorse_dt/condivise/immagini/icone/ico-messages-info.png"
          srcset="/risorse_dt/condivise/immagini/icone/ico-messages-info@2x.png
          2x"
          class="spacer-xs-right-10" />
      <p class="area-heading">
        {message}
      </p>
    </div>
  </div>