<script>
    import FormItem from "./FormItem.svelte";
    import FormGroup from "./FormGroup.svelte";
    import FormRow from "./FormRow.svelte";
    import FormSwitch from "./FormSwitch.svelte";
    import {createEventDispatcher, onMount, afterUpdate} from 'svelte';
    import {doRefresh} from '../sveltekit';
    import CenteredContainer from "../elements/CenteredContainer.svelte";
    import TextDescription from "./TextDescription.svelte";

    const formEventDispatcher = createEventDispatcher();

    onMount(() => {
        evalVisibility();
        validate();
    });

    afterUpdate(() => {
        evalVisibility();
        validate();
    });

    function handleClick(e) {
        if (e.detail.action.localeCompare('submit') == 0) {
            formEventDispatcher('submit', e.detail);
        } else if (e.detail.action.localeCompare('reset') == 0) {
            handleResetClick();
            formEventDispatcher('reset', e.detail);
        } else {
            formEventDispatcher(e.detail.action, e.detail);
        }
    }

    function handleResetClick() {
        formDescriptor.values.forEach(item => {
            if (typeof item.name !== "undefined") {
                values_map[item.name] = item.default_value;
            } else {
                item.forEach(subitem => {
                    values_map[subitem.name] = subitem.default_value;
                })
            }

        });

        formDescriptor.hidden_values.forEach(item => {
            if (typeof item.default_value != "undefined") {
                values_map[item.name] = item.default_value;
            }
        });

        evalVisibility();
        validate();
        doRefresh();
    }

    function handleActionRequest(e) {
        formEventDispatcher('actionRequest', e.detail);
    }

    function handleChange(e) {

        evalVisibility();
        validate();
        formEventDispatcher('change', {});

        //invalidate sibling component
        let _invalidateNameArray = (e.detail.invalidate !== undefined ? e.detail.invalidate : []);
        _invalidateNameArray.forEach(_name => {
            needDisplay_map[_name] = {"status": true, "origin": e.detail.name, "value": values_map};
        });

    }

    function validate() {
        let keys = Object.keys(validation_map);
        for (let i = 0; i < keys.length; i++) {
            let k = keys[i];
            if (typeof validation_map[k] !== "undefined" && visibility_map[k] && !validation_map[k]) {
                valid = false;
                invalid = true;
                formEventDispatcher('validation', valid);
                return;
            }
        }
        valid = true;
        invalid = false;
        formEventDispatcher('validation', valid);
    }

    function getInternalValue(v) {
        let selected = null;

        if (typeof v === "undefined") {
            return "";
        } else if (typeof v === 'boolean') {
            selected = v.toString();
        } else if (typeof v === 'string') {
            selected = v;
        } else {
            selected = Object.keys(v).reduce(
                    (total, currentValue) => {
                    return total + '$' + getInternalValue(v[currentValue]);
                }, '');
        }
        return selected;
    }

    function compare(value, predicateValue) {

        if (predicateValue[0].localeCompare('!') === 0) {
            return (('!' + getInternalValue(value)).localeCompare(predicateValue) !== 0);
        } else if (predicateValue[0].localeCompare('?') === 0) {
            return predicateValue.slice(1) in value && value[predicateValue.slice(1)];
        } else {
            return ((getInternalValue(value)).localeCompare(predicateValue) === 0);
        }
    }

    function evalVisibility() {
        let temp_visibility_map = Object.assign({}, visibility_map);
        let keys = Object.keys(temp_visibility_map);
        for (let key of keys) { // Itero i name di tutti gli elementi della form
            let predicate = predicate_map[key]; // Recupero il predicato associato all'elmento corrente della form

            if (predicate === false) { // Se il predicate e' impostato al booleano false nascondo l'elemento
                temp_visibility_map[key] = false;
            } else if (predicate === true) { // Se il predicate e' impostato al booleano true mostro l'elemento
                temp_visibility_map[key] = true;
            } else if (predicate && Object.keys(predicate).length > 0) { // Il predicato e' stato definito sull'elemento corrente procedo con ulteriori verifiche
                temp_visibility_map[key] = evalVisibilityByPredicate(key, predicate)
            } else {
                // Il predicate non e' definito sull'elemento corrente, quindi l'elemento corrente sara visibile
                temp_visibility_map[key] = true;
            }

        }
        hideElementsForHiddenGroups(temp_visibility_map);
        visibility_map = temp_visibility_map;
    }

    function evalVisibilityByPredicate(key, predicate) {
        for (let predicateKey of Object.keys(predicate)) {
            let predicateValue = predicate[predicateKey];

            let predicateRelatedValue = getPredicateRelatedValue(predicateKey);

            if (Array.isArray(predicateValue)) {
                let result = false;
                for (let predicateValueItem of predicateValue) {
                    result = result || matchPredicateValue(predicateValueItem, predicateRelatedValue)
                    if (result) {
                        break;
                    }
                }

                if (!result) {
                    return false;
                }

            } else {
                if (!matchPredicateValue(predicateValue, predicateRelatedValue)) {
                    return false;
                }
            }
        }

        return true;
    }

    function getPredicateRelatedValue(predicateKey) {
        let refs = predicateKey.split('.');
        switch (refs.length) {
            case 1:
                return values_complete_map[currentStepName][currentTaskName][refs[0]];
            case 2:
                return values_complete_map[currentStepName][refs[0]][refs[1]];
            case 3:
                return values_complete_map[refs[0]][refs[1]][refs[2]];
            default:
                return null;
        }
    }

    function matchPredicateValue(predicateValue_orig, predicateRelatedValue_orig) {
        let negation = false;
        let objectQuery = false;
        let response = false;

        // Evita side effects dato che il dato può essere alterato
        let predicateValue = predicateValue_orig;
        let predicateRelatedValue = predicateRelatedValue_orig;

        if (typeof predicateValue === 'string') {
            negation = predicateValue.startsWith("!"); // Verifico se la condizione e' negata
            objectQuery = predicateValue.startsWith("?"); // Verifico se la condizione è una query in un object
            predicateValue = (negation||objectQuery) ? predicateValue.substring(1) : predicateValue;
        }

        if (typeof predicateRelatedValue === 'object' && !objectQuery) {
            // Il valore che con cui configurare il predicate e' un oggetto
            predicateRelatedValue = JSON.stringify(predicateRelatedValue); // Converto l'oggetto in stringa cosi da poterlo confrontare facilmente
        }

        if (negation && predicateRelatedValue !== predicateValue) {
            // Il predicate e' false, ma essendoci la negazione restituisco true
            response = true;
        } else if (objectQuery && (predicateValue in predicateRelatedValue && predicateRelatedValue[predicateValue])) {
            response = true;
        } else {
            response = !negation && predicateRelatedValue === predicateValue;
        }

        return response

    }

    function hideElementsForHiddenGroups(visibility_map) {
        // Nascondo gli elementi che appartengono a un gruppo nascosto
        if (formDescriptor && formDescriptor.values && Array.isArray(formDescriptor.values)) {
            for (let row of formDescriptor.values) {
                if (row && row.type === 'group' && row.values && Array.isArray(row.values) && !visibility_map[row.name]) {
                    // Si tratta di un gruppo con visibilata false,
                    // imposto la visibilta a false a tutti i suoi elementi
                    for (let groupRow of row.values) {
                        if (groupRow && Array.isArray(groupRow)) {
                            for (let groupRowCell of groupRow) {
                                if (groupRowCell.name) {
                                    visibility_map[groupRowCell.name] = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    export let title;
    export let values_map;
    export let values_complete_map;
    export let validation_map;
    export let formDescriptor;
    export let readOnly = false;
    export let ignoreDirty = false;

    export let currentStepName;
    export let currentTaskName;

    export let visibility_map = {};
    let predicate_map = {};
    let needDisplay_map = {};
    //let row_visibility_map = {};

    export let valid = false;
    let invalid;

    function checkRowVisibility(row, visibility_map) {
        let check = true;

        for (let item of row) {
            if (!item) {
                check = false;
            } else if (typeof visibility_map[item.name] !== "undefined") {
                check = check && visibility_map[item.name];
            }
        }
        return check;
    }

    function checkRowMetadataDecorated(row) {
        return row.length > 0 && row[0].type === 'row' && row[0].decorated;
    }

</script>

{#if formDescriptor.values}
    <div class="container pt20 pb20 mb40">
        {#if title}
        <div class="list-card-title">{title}</div>
            {/if}
        <form class="generic-form bordered-elements">
            {#each formDescriptor.values as row}
                {#if typeof row.length !== "undefined"}
                    <FormRow row={checkRowVisibility(row, visibility_map)} decorated={checkRowVisibility(row, visibility_map) && checkRowMetadataDecorated(row)}>
                        {#each row as cell}
                            {#if cell.type === 'textdescription'}
                                <TextDescription description={cell.text} />
                            {:else if cell.type !== 'row'}
                                <FormItem descriptor={cell} label={cell.label} size="{cell.size}" type="{cell.type}" bind:visible={visibility_map[cell.name]} bind:predicate={predicate_map[cell.name]}>
                                    <FormSwitch descriptor={cell} bind:ignoreDirty={ignoreDirty}
                                                bind:value={values_map[cell.name]}
                                                bind:valid={validation_map[cell.name]}
                                                bind:needDisplay={needDisplay_map[cell.name]}
                                                on:click={handleClick}
                                                on:change={handleChange}
                                                on:actionRequest={handleActionRequest}
                                    />
                                </FormItem>
                            {/if}
                        {/each}
                    </FormRow>
                {:else if row.type === 'group' && visibility_map[row.name] }
                    <FormGroup descriptor={row} bind:visible={visibility_map[row.name]} bind:predicate={predicate_map[row.name]}>
                        {#each row.values as groupRow, g}
                            {#if typeof groupRow.length !== "undefined" }
                                <FormRow indent={true} row={checkRowVisibility(row, visibility_map)} decorated={checkRowVisibility(groupRow, visibility_map)}>
                                    {#each groupRow as groupRowCell, grc}
                                        <FormItem descriptor={groupRowCell} label={groupRowCell.label} size="{groupRowCell.size}"  type="{groupRowCell.type}" bind:visible={visibility_map[groupRowCell.name]} bind:predicate={predicate_map[groupRowCell.name]}>
                                            <FormSwitch descriptor={groupRowCell} bind:ignoreDirty={ignoreDirty}
                                                        bind:value={values_map[groupRowCell.name]}
                                                        bind:valid={validation_map[groupRowCell.name]}
                                                        bind:needDisplay={needDisplay_map[groupRowCell.name]}
                                                        on:click={handleClick}
                                                        on:change={handleChange}
                                                        on:actionRequest={handleActionRequest}
                                            />
                                        </FormItem>
                                    {/each}
                                </FormRow>
                            {:else}
                                <FormRow row={visibility_map[groupRow.name]} decorated={visibility_map[groupRow.name]}>
                                    <FormItem descriptor={groupRow} label={groupRow.label} size="{groupRow.size}"  type="{groupRow.type}" bind:visible={visibility_map[groupRow.name]} bind:predicate={predicate_map[groupRow.name]}>
                                        <FormSwitch descriptor={groupRow} bind:ignoreDirty={ignoreDirty}
                                                    bind:value={values_map[groupRow.name]}
                                                    bind:valid={validation_map[groupRow.name]}
                                                    bind:needDisplay={needDisplay_map[groupRow.name]}
                                                    on:click={handleClick}
                                                    on:change={handleChange}
                                                    on:actionRequest={handleActionRequest}
                                        />
                                    </FormItem>
                                </FormRow>
                            {/if}
                        {/each}
                    </FormGroup>
                {:else}
                    <FormRow row={visibility_map[row.name]} decorated={visibility_map[row.name]}>
                        <FormItem descriptor={row} label={row.label} size="{row.size}"  type="{row.type}" bind:visible={visibility_map[row.name]} bind:predicate={predicate_map[row.name]}>
                            <FormSwitch descriptor={row} bind:ignoreDirty={ignoreDirty}
                                        bind:value={values_map[row.name]}
                                        bind:valid={validation_map[row.name]}
                                        bind:needDisplay={needDisplay_map[row.name]}
                                        on:click={handleClick}
                                        on:change={handleChange}
                                        on:actionRequest={handleActionRequest}
                            />
                        </FormItem>
                    </FormRow>
                {/if}
            {/each}
            {#each formDescriptor.hidden_values as row}
                <FormSwitch descriptor={row} bind:ignoreDirty={ignoreDirty}
                            bind:value={values_map[row.name]}
                            bind:valid={validation_map[row.name]}
                            on:click={handleClick}
                            on:actionRequest={handleActionRequest}
                />
            {/each}
        </form>
        {#if formDescriptor && !readOnly}
            <div class="required-disclaimer">
                <p>Tutti i campi contraddistinti dall'asterisco* sono obbligatori.</p>
            </div>
        {/if}

    </div>
{:else}
    <CenteredContainer>
        <p class="font-weight-bold" style="color: red;">Form descriptor not loaded</p>
        <pre class="d-none">{JSON.stringify(values_map,null,2)}</pre>
    </CenteredContainer>
{/if}
