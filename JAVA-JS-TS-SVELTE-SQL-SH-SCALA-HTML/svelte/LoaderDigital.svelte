<style>
    .pageLoader-svelte {
        background-color: #fff;
        position: fixed;
        width: 100%;
        height: 100%;
        z-index: 9999;
        top: 0;
        opacity: 1;
        text-align: center;
        padding-top: 20%;
        display: block;
    }

    .pageLoader-svelte .loader-spinner {
        padding-top: 40px;
        width: 40px;
        display: block;
        margin: 0 auto;
    }

    .pageLoader-svelte .loader-logo {
        height: 22px;
    }
</style>

<div class="pageLoader-svelte">
    <img class="loader-logo" alt="Poste Italiane" src="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane.png"
         srcset="/risorse_dt/condivise/immagini/loghi/logo-poste-italiane@2x.png 2x">
    <img class="loader-spinner" alt="attendi il caricamento"
         src="/risorse_dt/condivise/immagini/generiche/spinner_giallo.gif" />
</div>
