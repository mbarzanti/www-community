<script>
    import {getUniqueId} from "../../../../suit/uikit/utils";

    export let productDisplayName;
    export let productIndex;
    export let productsAmount;
    export let id = getUniqueId("accordionid-");
    export let css;


</script>


<div class="container width960" style="{css}">
    <div class="content-padd pt10">
        <div class="accordion accordion-gestione-ruoli accordion-gestione-ruoli-rev" id="{id}">
            <div class="card">
                <div class="card-header">
                    <button class="button-acc-rel" type="button" data-toggle="collapse" data-target="#collapse-{id}">
                        <div class="relative-prod-conf" style="padding-bottom: 0; border-bottom: 0 none;">
                           <span class="cta-label">
                               <strong>Prodotto {productIndex} di {productsAmount}</strong>
                               <strong>{productDisplayName}</strong>
                           </span>
                            <div class="prod-status incomplete-status"></div>
                        </div>
                    </button>
                </div>
                <div id="collapse-{id}" class="collapse show" data-parent="#{id}">
                    <div class="card-body">
                        <slot/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>