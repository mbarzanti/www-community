<script>
    import FormGroup from "../../../UiKitLite/forms/FormGroup.svelte";
    import FormLabel from "../../../UiKitLite/forms/FormLabel.svelte";
    import TextInput from "../../../UiKitLite/forms/TextInput.svelte";
    import {createEventDispatcher} from 'svelte';
    import * as validators from '../../../libs/validators';

    const dispatch = createEventDispatcher();

    export let pratica;

    function mostraTabella() {
        dispatch('indietro');
    }

</script>

<div class="welcome welcome-simple spacer-xs-top-20 spacer-md-top-0
    spacer-xs-bottom-0 spacer-md-bottom-20" style="width: 1320px">
    <div class="main-pills">
        <div class="main-pills-wrap">
            <h2>
                Dettagli numero pratica {pratica.funnelId}
            </h2>
            <div style="margin-top: 60px !important;">
                <table class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th class="tableTitle" style="text-align: center!important;"><strong>Tipo Bonus</strong></th>
                        <th class="tableTitle" style="text-align: center!important;"><strong>Codice Bonus</strong></th>
                        <th class="tableTitle" style="text-align: center!important;"><strong>Anno</strong></th>
                        <th class="tableTitle" style="text-align: center!important;"><strong>Importo</strong></th>
                        <th class="tableTitle" style="text-align: center!important;"><strong>Importo ceduto</strong></th>
                    </tr>
                    </thead>

                    <tbody>
                    {#each pratica.iterator as tipobonus, index}
                        {#each pratica.dettagli[tipobonus].anniBonus as value}

                            <tr>
                                <td>{tipobonus}</td>
                                <td style="text-align: center!important;">{pratica.codeIndex[tipobonus]}</td>
                                <td style="text-align: center!important;">{value.anno}</td>
                                <td style="text-align: center!important;">{value.importo}</td>
                                <td style="text-align: center!important;">{value.importoRichiesto}</td>
                                <!--<FormGroup>
                                    <FormLabel inputDescription="Importo Anno {value.anno}"
                                               inputName="anno-{value.anno}"/>
                                    <TextInput id="anno-{value.anno}" name="anno-{value.anno}"
                                               placeholder="Importo Anno {value.anno}"
                                               value={value.importo}
                                               readOnly={true}
                                               maxlength={validators.MAX_NAME_LENGTH}/>
                                </FormGroup>-->

                            </tr>
                        {/each}
                    {/each}
                    </tbody>
                </table>
            </div>
        </div>
        <div class="row spacer-xs-top-30 text-center">
            <button class="btn btn-primary" on:click={mostraTabella}>Indietro</button>
        </div>
    </div>
</div>