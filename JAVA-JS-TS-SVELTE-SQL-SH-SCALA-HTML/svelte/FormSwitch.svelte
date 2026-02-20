<script>
    import ComboBox from "./ComboBox.svelte";
    import RadioButton from "./RadioButton.svelte";
    import Checkbox from "./Checkbox.svelte";
    import Button from "./Button.svelte";
    import SelectField from "./SelectField.svelte";
    import Datepicker from "./Datepicker.svelte";
    import HiddenField from "./HiddenField.svelte";
    import NumberField from "./NumberField.svelte";
    import TextField from "./TextField.svelte";
    import TextAreaField from "./TextAreaField.svelte";
    import Label from "./Label.svelte";
    import Filepicker from "./Filepicker.svelte";
    import Download from "./Download.svelte";
    import TextFieldWithAction from "./TextFieldWithAction.svelte";
    import {createEventDispatcher} from 'svelte';
    import MultiSelectButton from './MultiSelectButton.svelte';
    import UploadFilePicker from "./UploadFilePicker.svelte";

    const eventDispatcher = createEventDispatcher();

    function handleChange(e) {
        eventDispatcher('change', e.detail);
    }

    function handleClick(e) {
        eventDispatcher('click', e.detail);
    }

    function handleActionRequest(e) {
        eventDispatcher('actionRequest', e.detail);
    }

    function handleForceValidation(e) {
        valid = e.detail.valid;
        eventDispatcher('change', e.detail);
    }

    export let descriptor;
    export let value = undefined;
    export let valid = undefined;
    export const needDisplay = {"status": false};
    export let ignoreDirty = false;

    let type = descriptor.type;
    let name = descriptor.name;
    let label = descriptor.label;
    let options = descriptor.options;
    let default_value = descriptor.default_value;

</script>

{#if type === 'text' }
    <TextField {name} {options} {default_value} {ignoreDirty}
               bind:value={value} bind:valid={valid}
               on:change={handleChange} on:forceValidation={handleForceValidation}
    />
{:else if type === 'textarea' }
    <TextAreaField {name}  {options} {default_value} {ignoreDirty}
                   bind:value={value} bind:valid={valid}
                   on:change={handleChange}
    />
{:else if type === 'number' }
    <NumberField {name} {options} {default_value} {ignoreDirty}
                 bind:value={value} bind:valid={valid}
                 on:change={handleChange}
    />
{:else if type === 'hidden' }
    <HiddenField {name} {options} {default_value} {ignoreDirty}
                 bind:value={value} bind:valid={valid}
    />
{:else if type === 'datepicker' }
    <Datepicker {options} {default_value} {ignoreDirty}
                bind:value={value} bind:valid={valid}
                on:change={handleChange}
    />
{:else if type === 'select' }
    <SelectField {name} {options} {default_value} values={descriptor.values} {ignoreDirty}
                 bind:value={value} bind:valid={valid}
                 on:change={handleChange} startsWithEnabled={options.startsWithEnabled}
    />
{:else if type === 'checkbox' }
    <Checkbox {name} {label} {options} {default_value} {ignoreDirty}
              bind:value={value} bind:valid={valid}
              on:change={handleChange}/>
{:else if type === 'multicheckbox' }
    <MultiSelectButton {name} {label} {options} {default_value} values={descriptor.values} {ignoreDirty}
                       bind:value={value} bind:valid={valid}
                       on:change={handleChange}/>
{:else if type === 'radio' }
    <RadioButton {name} {label} {options} {default_value} values={descriptor.values} {ignoreDirty}
                 bind:value={value} bind:valid={valid}
                 on:change={handleChange}
    />
{:else if type === 'combobox' }
    <ComboBox {options} {default_value} values={descriptor.values} {ignoreDirty}
              bind:value={value} bind:valid={valid}
              on:change={handleChange}
    />
{:else if type === 'button' }
    <Button {name} {options} {ignoreDirty}
            on:click={handleClick}
    />
{:else if type === 'filepicker' }
    <Filepicker {name} {options} bind:valid={valid} disabled={!valid} {ignoreDirty}
                on:change={handleChange} bind:value={value}
    />
{:else if type === 'gect' }
    <UploadFilePicker {name} {ignoreDirty} bind:valid={valid} {options} on:change={handleChange} bind:value={value}/>

{:else if type === 'download' }
    <Download {name} {options} bind:valid={valid} disabled={!valid} {ignoreDirty}
              on:change={handleChange} bind:value={value}
    />
{:else if type === 'textaction' }
    <TextFieldWithAction {name} {label} {options} {default_value} {ignoreDirty}
                         bind:value={value} bind:valid={valid}
                         on:change={handleChange} on:actionRequest={handleActionRequest}
                         on:forceValidation={handleForceValidation}
    />
{:else if type === 'label' }
    <Label {name} {options} {default_value}
           bind:value={value} bind:valid={valid}
    />
{/if}
