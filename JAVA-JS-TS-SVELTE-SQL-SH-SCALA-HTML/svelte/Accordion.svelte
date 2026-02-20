<script>
    import {getUniqueId} from '../sveltekit';
    export let header = "Accordion Header";
    let id = getUniqueId("accordionid-");
</script>

<div class="accordion accordion-liquidita accordion-pit accordion-config-prods" id="{'accordion-'+id}">
    <!-- add class accordion-pit -->
    <div class="card">
        <div class="card-header">
            <button class="button-acc-rel" type="button" data-toggle="collapse"
                    data-target="{'#collapse-'+id}">
                <div class="relative-prod-conf">
                    <span class="cta-label">{header}</span>
                </div>
            </button>
        </div>
        <div id="{'collapse-'+id}" class="collapse show" data-parent="{'#accordion-'+id}">
            <div class="card-body">
                <slot></slot>
            </div>
        </div>
    </div>
</div>
