<script>
  import FormGroup from "../../UiKitLite/forms/FormGroup.svelte";
  import FormLabel from "../../UiKitLite/forms/FormLabel.svelte";
  import FilePicker from "../../UiKitLite/forms/FilePicker.svelte";

  export let status;
  export let statusMessages;
  export let inputName;
  export let documentKey;
  export let inputDescription;
  export let accept;
  export let popover = null;
  export let files = null;
</script>

<FormGroup status={status[documentKey]}>
  <FormLabel
    {inputDescription}
    {inputName}
    {popover}
    status={status[documentKey]}
    statusMessage={statusMessages[documentKey]} />
  <FilePicker id={inputName} name={documentKey} {accept} bind:files on:change  />
</FormGroup>
