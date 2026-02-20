<script>
    import LinkedQuestion from "./LinkedQuestion.svelte";

    export let title = "Domande collegate";
</script>
<div class="col-xs-12 col-sm-6 col-sm-push-6 col-md-push-0 col-md-12">
    <div class="bracket-pills border-md-bottom">
        <h5 class="h6">{title}</h5>
        <ul class="list-unstyled">
            <slot>
                <!--EXAMPLES-->
                <LinkedQuestion href="#" text="Quanto costa spedire all'estero?"/>
                <LinkedQuestion href="#" text="Quali sono i vantaggi di una Raccomandata ?"/>
                <LinkedQuestion href="#" text="Conviene la Consegna personalizzata?"/>
            </slot>
        </ul>
    </div>
</div>