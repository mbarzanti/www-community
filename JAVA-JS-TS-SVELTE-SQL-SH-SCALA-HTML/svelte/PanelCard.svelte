<script>
    import PanelCardItem from "./PanelCardItem.svelte";

    export let title;
</script>
<div class="col-xs-12">
    <div class="bracket-pills border-sm-bottom-not border-md-bottom">
        <div class="panel panel-cards panel-cards-boxed">
            <div class="panel-wrap-pre">
                <div class="panel-heading">
                    <div class="panel-heading-subtitle clearfix"><span>{title}</span></div>
                </div>
            </div>
            <div class="panel-wrap border-xs-bottom border-sm-bottom border-md-bottom">
                <div class="panel-body">
                    <div class="box-advice box-summary spacer-xs-top-20">
                        <div class="box-body">
                            <div class="row">
                                <slot>
                                    <!--EXAMPLES-->
                                    <PanelCardItem text="BFP a 3 anni Plus" title="Tipologia di Buono"/>
                                    <PanelCardItem text="3 Anni" title="Durata"/>
                                    <PanelCardItem text="€ 1.000,00" title="Importo nominale"/>
                                    <PanelCardItem text="Nuovo Libretto Smart" title="Tipologia di libretto"/>
                                </slot>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>