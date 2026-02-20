<script>

    export let groupId;
    export let stepIndex = null;
    export let stepTitle;
    export let stepName;
    export let isDisabled = false;
    export let isCollapsed = false;
    export let noOpenButton = false;
    export let markCompleted = false;

</script>

<div class="panel panel-pills" class:panel-pills-disabled={isDisabled}>
    <div id="{stepName}" class="panel-heading" role="tab">
        <div class="row">
            <div class="col-xs-12">
                <div class="pills-wrap pills-wrap-accordion">
                    <div class="row">
                        <div class="col-xs-12">
                            <h3 class="h4 panel-title">
                                {#if !noOpenButton}
                                <a role="button" data-toggle="collapse" data-parent="#{groupId}" href="#{stepName}-content" aria-expanded="true" aria-controls="{stepName}-content">
                                    <span class="panel-raise bold spacer-xs-right-15">
                                        {#if stepIndex && !markCompleted}{stepIndex}{:else}&nbsp;{/if}
                                        {#if markCompleted}
                                            <img class="vam" src="/risorse_dt/condivise/immagini/icone/ico-applicative-success.png" srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-success@2x.png 2x"
                                              alt="Step completato" />{/if}
                                    </span>{stepTitle}
                                </a>
                                {:else}
                                    <a href="javascript:void(0);">
                                        <span class="panel-raise bold spacer-xs-right-15">
                                            {#if stepIndex && !markCompleted}{stepIndex}{:else}&nbsp;{/if}
                                            {#if markCompleted}
                                                <img class="vam" src="/risorse_dt/condivise/immagini/icone/ico-applicative-success.png" srcset="/risorse_dt/condivise/immagini/icone/ico-applicative-success@2x.png 2x"
                                                     alt="Step completato" />{/if}
                                        </span>{stepTitle}
                                    </a>
                                {/if}
                            </h3>
                        </div>
                    </div>
                    <div class="pills-abs pills-abs-right" class:hide={noOpenButton}> <a class="open collapsed" role="button" data-toggle="collapse" data-parent="#{groupId}" href="#{stepName}-content" aria-expanded="true" aria-controls="{stepName}-content">
                        <span class="collapsed-arrow collapsed-arrow-closed"></span></a> </div>
                </div>
            </div>
        </div>
    </div>
    <div id="{stepName}-content" class="panel-collapse collapse" class:in={!isCollapsed} role="tabpanel" aria-labelledby="{stepName}">
        <div class="panel-body">
            <slot/>
        </div>
    </div>
</div>