<script>
    import AutoSubForm from "./AutoSubForm.svelte";

    import FormItem from "./FormItem.svelte";
    import FormRow from "./FormRow.svelte";
    import Button from "./Button.svelte";
    import FormSwitch from "./FormSwitch.svelte";
    import {createEventDispatcher, onMount} from 'svelte';
    import {doRefresh} from '../sveltekit';
    import CenteredContainer from "../elements/CenteredContainer.svelte";

    const formEventDispatcher = createEventDispatcher();

    onMount(() => {
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
            if (typeof item.name != "undefined") {
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

        validate();

        doRefresh();
    }

    function handleChange(e) {
        validate();
        formEventDispatcher('change', {});
    }

    function validate() {
        let keys = Object.keys(validation_map);
        for (let i = 0; i < keys.length; i++) {
            let k = keys[i];
            if (!validation_map[k]) {
                valid = false;
                invalid = true;
                return;
            }
        }
        valid = true;
        invalid = false;
    }

    export let title;
    export let values_map = {};
    export let validation_map = {};
    export let formDescriptor = {};

    let valid;
    let invalid;

</script>
{#if formDescriptor.values}
    <AutoSubForm title={title} {formDescriptor} bind:values_map={values_map} bind:validation_map={validation_map} />
    <div class="form-row" style="border-bottom: none;">
        <div class="form-group">
            <div class="">
                <Button name={'Submit'} action="submit" on:click={handleClick} bind:disabled={invalid}/>
            </div>
        </div>
        <!--            <div class="form-group">-->
        <!--                <div class="">-->
        <!--                    <Button name={'Reset'} action="reset" on:click={handleClick}/>-->
        <!--                </div>-->
        <!--            </div>-->
    </div>
{:else}
    <CenteredContainer>
        <p class="font-weight-bold" style="color: red;">Form descriptor not loaded</p>
        <pre class="d-none">{JSON.stringify(values_map,null,2)}</pre>
    </CenteredContainer>
{/if}