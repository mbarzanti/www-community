<script>
  import AppBasePageLayout from "../AppBasePageLayout.svelte";
  import HeadBanner from "../../UiKitLite/common/HeadBanner.svelte";

  import { push } from "svelte-spa-router";

  import * as api from "../../api";

  function goToNextStep() {
    api.createFunnel().then(() => push("/company-data"));
  }
</script>

<HeadBanner>
  <div class="head-banner">
    <h2>Ecobonus e Sismabonusu</h2>
  </div>
</HeadBanner>

<AppBasePageLayout>
  <div slot="default">
    <div class="row">
      <div class="col-sm-12">
        <h2>Come sottoscrivere il servizio</h2>
      </div>
    </div>
    <div class="row row-bottom-border">
      <div class="col-sm-12">
        <div class="box-editable-area box-editable-spacing">
          Il servizio è rivolto ai titolari di un conto <strong>BancoPosta</strong>,
          Lorem ipsum dolor sit amet, consectetur adipisci elit, sed eiusmod
          tempor incidunt ut labore et dolore magna aliqua.
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-12">
        <div class="box-editable-area box-editable-spacing">
          Solo il <strong>Rappresentante Legale</strong> può sottoscrivere il servizio
          e ti verà chiesto / avrai bisogno .... e impiegherai circa 15 min:<br />
          <br /> - Documento Uno<br /> - Documento Due<br /> - Modulo Uno<br />
          <br />
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-12">
        <h2>Lorem ipsum</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-sm-12">
        <div class="box-editable-area box-editable-spacing">
          Lorem ipsum dolor sit amet, consectetur adipisci elit, sed eiusmod
          tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim
          veniam, quis nostrum exercitationem ullam corporis suscipit
          laboriosam, nisi ut aliquid ex ea commodi consequatur. Quis aute iure
          reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
          pariatur.
        </div>
      </div>
    </div>
  </div>
  <div slot="bottom-content">
    <div class="col-sm-12">
      <p class="btn-container btn-container-left spacer-xs-bottom-15 clearfix">
        <button
          on:click={goToNextStep}
          type="button"
          class="btn btn-primary">inizia</button>
      </p>
    </div>
  </div>
</AppBasePageLayout>
