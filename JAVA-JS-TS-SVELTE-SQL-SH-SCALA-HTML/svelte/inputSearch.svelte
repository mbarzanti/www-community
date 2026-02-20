<script>
  import {createEventDispatcher, afterUpdate, onMount} from "svelte";
  import {regexTest} from "../../commons/utils";
  import * as validator from "./../../commons/customValidators";
  import * as regexLib from "./../../commons/regex";
  import * as labels from "./../../commons/labels";
  import * as templates from "../../commons/optionsTemplates";
  import * as validationMessageLib from "./../../commons/validationMessages";
import { tipoDiRicerca } from "../../pages/change-operations/store";

  var submitted = false;
  var render = false;

  export let id = "searchBar";
  export let options;
  export let value;
  export let handler;

  const tempId = "IvaInput";
  let validationMessage;
  let touched = false;
  let valid = true;

  onMount(() => {
    // Init Options
    if (options) {
      const defaultOptions = templates.SEARCH_DEFAULT_OPTIONS;
      Object.keys(defaultOptions).forEach(option => {
        options[option] = options[option] || defaultOptions[option];
      });
    } else {
      options = templates.SEARCH_DEFAULT_OPTIONS;
    }
    validationMessage = options.validationMessage;
    jQuery('#' + id).bind('keypress keydown', function (e) {
      if (e.keyCode == 13) {
        e.preventDefault();
      }
    });
    jQuery('#' + id).bind('keyup', function (e) {
      if (e.keyCode == 13) {
        handleClick();
        e.preventDefault();
      }
    });
  });

  function handleClick() {
    validate();
    if (handler && valid) {
      handler(value);
    }
  }

  //$ : value, validate();
  function blurHandler() {
    touched = true;
  }

  function validate() {
    validationMessage = options.validationMessage;

    if (typeof value != "undefined" && value != null && value.length > 0) {
      touched = true;
      valid = regexTest(value, options.regex);
      executeCustomValidators();
    } else {
      valid = false;
    }
  }

  function executeCustomValidators() {
    if (
      valid &&
      options.customPostValidators &&
      options.customPostValidators.length > 0
    ) {
      let testValid = false;
      let response;
      options.customPostValidators.forEach(item => {
        response = item(value, undefined);
        validationMessage = response.result
          ? validationMessage
          : response.message
          ? response.message
          : validationMessage;
        testValid = testValid || response.result;
      });
      valid = testValid;
    }
  }

  function handleChange() {}

  function changeTipo(e) {
    tipoDiRicerca.set(e.target.value);
  }
</script>

<style>
  .id-client-form .sf-padd .iva-searchbar .autocomplete {
    position: relative;
  }

  .id-client-form .sf-padd .autocomplete {
    position: relative;
    display: inline-block;
    width: 100%;
  }

  .id-client-form .sf-padd .iva-searchbar .autocomplete input[type="search"]{
    width: 100%!important;
    max-width: 100vw;
    height: 36px;
    background-image: url(/feu-after-sales/images/heading-bar/btn-search.svg)!important;
    background-repeat: no-repeat;
    background-size: 36px;
    background-position: right center;
  }

  .id-client-form .sf-padd .iva-searchbar input[type=search]::-webkit-search-cancel-button {
    background-image: none;
  }

  .id-client-form .sf-padd .iva-searchbar input[type=search]::-ms-search-cancel-button {
    background-image: none;
  }

  .id-client-form .sf-padd .iva-searchbar input[type=search]::-ms-clear {
    background-image: none;
  }

  /*
  .id-client-form .sf-padd .iva-searchbar input[type=search]::-webkit-search-cancel-button{
      width: 100%;
      max-width: 100vw;
      height: 36px;
      background-image: url(../images/heading-bar/btn-search.svg);
      background-repeat: no-repeat;
      background-size: contain;
      background-position: center center
  }

  .id-client-form .sf-padd .iva-searchbar input[type=search]::-ms-search-cancel-button{
      width: 100%;
      max-width: 100vw;
      height: 36px;
      background-image: url(../images/heading-bar/btn-search.svg);
      background-repeat: no-repeat;
      background-size: contain;
      background-position: center center
  }

  .id-client-form .sf-padd .iva-searchbar input[type=search]::-ms-clear{
      width: 100%;
      max-width: 100vw;
      height: 36px;
      background-image: url(../images/heading-bar/btn-search.svg);
      background-repeat: no-repeat;
      background-size: contain;
      background-position: center center
  } */

  span#search-btn {
    position: absolute;
    right: 0;
    top: 0;
    height: 100%;
    width: 10%;
    cursor: pointer;
  }

.id-client-form .sf-padd .iva-searchbar {
    position: relative;
    max-width: 500px;
    margin: 0 auto;
    padding: 0;
    padding-top: 20px;
}

  .search-box{
  text-transform: uppercase;
  }
</style>

{#if options}
  <div class="id-client-form">
    <div class="container">
    <span>
      <form id="iva-form">
        <div class="sf-padd">
          <div id="iva-searchbar" class="iva-searchbar" style="display: flex">
            <div style="margin-right: 15px;">
              <fieldset id="group1">
                <div style="display: flex;">
                  <input type="radio" class="iradio_flat-blue" noclass="checkbox_custom" id="cliente" name="group1" value="cliente" checked on:change={e => changeTipo(e)} style="margin-right: 5px;"> 
                  <label for="cliente">Cliente</label>
                </div>
                <div style="display: flex;">
                  <input type="radio" class="iradio_flat-blue" noclass="checkbox_custom" id="partner" name="group1" value="partner" on:change={e => changeTipo(e)} style="margin-right: 5px;"> 
                  <label for="cliente">Partner</label>
                </div>
              </fieldset>
            </div>
            <div id="iva-searchbar-field" class="autocomplete" style="padding-top: 8px;">
                <input
                  id={id}
                  type="search"
                  autocomplete="off"
                  name={id}
                  bind:value
                  on:blur={blurHandler}
                  class="search-box input-text form-control"
                  class:is-invalid={!valid && touched}
                  placeholder={options.placeholder}
                  maxlength={options.maxlength} />
                  <div class="invalid-feedback">{validationMessage}</div>
                <span id="search-btn" on:click={handleClick} />
            </div>
          </div>
        </div>
      </form>
      </span>
    </div>
  </div>

{/if}
