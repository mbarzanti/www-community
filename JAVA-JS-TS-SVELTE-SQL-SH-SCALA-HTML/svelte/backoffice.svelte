<script>
  import SubmitPage from "../../submit/submitPage.svelte";
  import Navbar from "./../../../components/ui/navbar.svelte";
  import * as utils from "./../../../commons/utils";
  import * as constants from "./../../../commons/constants";
  import * as mapper from "./../../../commons/mapper"; 
  import api from "./../../../api/api";
  import {onMount} from 'svelte';

  var render = false;

  export let context;
  export let id;

  function populateForm(success, dossier) {
    console.log(dossier);
    if (success) {
      if (dossier.stato === constants.DOSSIER_STATUS_CREATED){
        utils.redirectTo(constants.ERROR_PAGE_ID_NO_DOSSIER, constants.ERROR_PAGE_REF_DIGITAL);
      } else {
        mapper.populateFormFromGetDossier(context, dossier);
        render = true;
      }
    } else if (dossier.status === 404){
      utils.redirectTo(constants.ERROR_PAGE_ID_NO_DOSSIER_FOUND, constants.ERROR_PAGE_REF_DIGITAL);
    } else {
      utils.redirectTo(constants.ERROR_PAGE_ID_ERROR, constants.ERROR_PAGE_REF_DIGITAL);
    }
  }
  onMount(() => {
    context.dossierId = id;
    api.dossier.get(context, populateForm);
  });
</script>

<style>

</style>

{#if render}
  <!--<button on:click={printMap}>STAMPA</button>-->
  <svelte:component this={SubmitPage} {context} readOnly={true} />
{/if}
