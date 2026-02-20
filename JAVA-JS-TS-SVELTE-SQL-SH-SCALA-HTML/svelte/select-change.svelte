<script>
    import * as constants from './../../commons/constants';
    import { _ } from 'svelte-i18n';

    export let appState = {};
    export let nextStateEnabled = false;
    export let runAlternativeFlow = undefined;
    export let runAction = undefined;
    runAlternativeFlow;
    runAction;

    import RadioButton from "./../../SvelteKit/forms/RadioButton.svelte";

    let start;
    let end;
    let type = "radio";
    let name = "RadioAssets";
    let options = {
        "disabled": false,
        "required": true,
        "inline": false
    };
    let values = [];

    let default_value ='';

    let value = appState.globalContext.change.selectedChange ? appState.globalContext.change.selectedChange : undefined;
    let valid = false;


    import {onMount} from 'svelte';
import { get } from 'svelte/store';
import { tipoDiRicerca } from '../change-operations/store';

    onMount(() => {
        if(get(tipoDiRicerca) === 'cliente') {
            appState.globalContext.currentChanges.forEach(change => {
                let objChange = {
                    "label": $_(change.label),
                    "value": change.id
                }
                values.push(objChange)
            })
        } else {
            values.push({
                label: 'Variazione massiva merchant convenzionati',
                value: "VAR_RECESSO_MASSIVO"
            })
        }
    })

    function clickRadio(eventone){
        appState.globalContext.change.selectedChange = {};
        appState.globalContext.change.selectedChange = eventone.detail.value;
    }
</script>
    <div class='container container_radio'>
        <RadioButton {name} {options} {default_value} bind:values={values}
                     bind:value={value} bind:valid={nextStateEnabled} on:change={eventone => clickRadio(eventone)}
        />
    </div>

<style>

    .container {
        min-height: 200px;
        height: calc(100vh - 15em);
    }

    .container.container_radio {
        height: 100%;
        padding: 0 25px 0 25px;
        margin-top: 10px;
    }

</style>
