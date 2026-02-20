<script>
  import { onMount, afterUpdate } from "svelte";

  import { initPopovers } from "../libs/bootstrap";

  export let id = null;
  export let placeholder = null;
  export let type = "text";
  export let popover = null;
  export let readOnly = false;
  export let name;
  export let value;
  export let maxlength;

  let extraProps = {};
  let inputElement;

  onMount(() => {
    inputElement.type = type;

    if (popover) {
      extraProps = {
        "data-toggle": "popover",
        "data-placement": "right",
        "data-trigger": "focus",
        title: popover.title,
        "data-content": popover.content,
        "data-container": "body",
        "data-html": popover.html ? "true" : "false"
      };

      initPopovers(id ? "#" + id : null);
    }

    if (placeholder) {
      extraProps.placeholder = placeholder;
    }
  });
</script>

<input
  class="form-control"
  {id}
  {name}
  {...extraProps}
  {maxlength}
  readonly={readOnly}
  bind:this={inputElement}
  on:change
  on:blur
  on:input
  bind:value />
