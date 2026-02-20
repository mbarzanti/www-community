<script>
  import { onMount } from 'svelte';
  import { initDropdown } from '../libs/bootstrap';

  export let backAddress = null;
  export let backTitle = null;
  export let userName = null;
  export let logoutAddress = null;

  onMount(() => {
    initDropdown('.content-federation-bar .dropdown-toggle');
  });
</script>

<div
  class="content content-federation-bar content-federation-bar-minified
  content-federation-bar-simplified">
  <div class="container container-extended">
    <div class="row">
      <div class="col-md-12">
        <div class="header-minified">
          <div class="row">
            <div class="col-xs-12">
              <div class="federation-bar-content-logo pull-xs-left clearfix">
                <!-- [logo] start -->
                <div class="logo correct-logo">
                  <a
                    href="https://www.poste.it"
                    title="Torna alla home"
                    class="hidden-xs hidden-sm">
                    <span class="wrap-logo wrap-logo-medium">
                      <img
                        class="logo-image-pi-medium"
                        src="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-medium.png"
                        srcset="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-medium@2x.png
                        2x"
                        alt="Poste Italiane" />
                    </span>
                  </a>
                  <a
                    href="https://www.poste.it"
                    title="Torna alla home"
                    class="logo-mobile hidden-md hidden-lg">
                    <span class="wrap-logo wrap-logo-small">
                      <img
                        class="logo-image-pi-small"
                        src="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-small.png"
                        srcset="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane-small@2x.png
                        2x"
                        alt="Poste Italiane" />
                    </span>
                  </a>
                </div>
                <!-- [logo] end -->
                {#if backAddress && backTitle}
                  <!-- [back] start -->
                  <div class="back spacer-md-left-30">
                    <a target="_self" href={backAddress} class="back-link">
                      <span class="hidden-xs hidden-sm">{backTitle}</span>
                    </a>
                  </div>
                  <!-- [back] end -->
                {/if}
              </div>
              <div class="federation-bar-wrap federation-bar-wrap-right">
                <!-- [dropdown] start -->
                <ul class="list-inline clearfix">
                  <li
                    id="dropdown-windowbox-assistenza"
                    class="dropdown dropdown-windowbox
                    dropdown-windowbox-assistenza">
                    <a
                      class="pi-targetarea" style="color: white;"
                      href="https://www.poste.it/assistenza.html">
                      <span class="hidden-xs hidden-sm">Assistenza</span>
                    </a>
                  </li>
                  {#if userName}
                    <li
                      id="dropdown-windowbox-login"
                      class="dropdown dropdown-windowbox
                      dropdown-windowbox-login">
                      <a href="javascript:void(0);" data-toggle="dropdown"
                        class="pi-targetarea dropdown-toggle
                        dropdown-toggle-login">
                        <span class="hidden-xs hidden-sm">{userName}</span>
                        <span class="caret hidden-xs hidden-sm hide"></span>
                      </a>
                      <ul class="dropdown-menu dropdown-menu-federation">
                        <li class="dropdown-link">
                          <a href="https://www.poste.it/myposte/index.html#/cruscotto">
                            <img
                              class="spacer-xs-right-15"
                              src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-cruscotto.png"
                              srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-cruscotto@2x.png
                              2x"
                              alt="Area Personale - Myposte" />
                            Myposte
                          </a>
                        </li>
                        <li class="dropdown-link">
                          <a
                            href="https://www.poste.it/myposte/profilo.html">
                            <img
                              class="spacer-xs-right-15"
                              src="/risorse_dt/condivise/immagini/icone/icone-default-on/ico-impostazioni.png"
                              srcset="/risorse_dt/condivise/immagini/icone/icone-default-on-2x/ico-impostazioni@2x.png
                              2x"
                              alt="Area Personale - Profilo" />
                            Profilo
                          </a>
                        </li>
                        {#if logoutAddress}
                        <li class="dropdown-link dropdown-off-element">
                          <a href={logoutAddress}>
                            <img
                              alt="Area Personale - Logout"
                              src="/risorse_dt/condivise/immagini/icone/icone-default-off/ico-logout.png"
                              srcset="/risorse_dt/condivise/immagini/icone/icone-default-off-2x/ico-logout@2x.png
                              2x"
                              class="spacer-xs-right-15" />
                            Logout
                          </a>
                        </li>
                        {/if}
                      </ul>
                    </li>
                  {/if}
                </ul>
                <!-- [dropdown] end -->
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
