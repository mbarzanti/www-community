<script>
  import { afterUpdate, createEventDispatcher, onMount } from "svelte";

  import { initPopovers } from "../libs/bootstrap";

  const dispatch = createEventDispatcher();

  export let id = null;
  export let popover = null;
  export let nullValueLabel = null;
  export let readOnly = false;
  export let name;
  export let value;
  export let values;
  export let labelFieldName;
  export let keyFieldName;
  export let classes;

  let inputElement;

  let extraProps = {};

  onMount(() => {
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

    bindReadOnly();
  });

  function bindReadOnly() {
    if (readOnly) {
      extraProps.readOnly = true;
      extraProps.disabled = true;
    } else {
      delete extraProps["readOnly"];
      delete extraProps["disabled"];
      extraProps = { ...extraProps };
      //TODO https://github.com/sveltejs/svelte/issues/3764
      setTimeout(() => {
        inputElement.removeAttribute("readonly");
        inputElement.removeAttribute("disabled");
      }, 100);
    }
  }

  afterUpdate(() => {
    bindReadOnly();
  });

  function checkSameValue(item) {
    if (value) {
      if (item) {
        return item[keyFieldName] === value[keyFieldName];
      }
    } else {
      return item === null;
    }

    return false;
  }

  function getChangedValue(event) {
    const options = event.target.options;
    const objectValue = options[options.selectedIndex].__value;
    return objectValue ? objectValue : options[options.selectedIndex].value;
  }

  function handleChange(event) {
    value = getChangedValue(event);
    dispatch("change", {
      name: name,
      value: value
    });
  }

  function handleBlur(event) {
    value = getChangedValue(event);
    dispatch("blur", {
      name: name,
      value: value
    });
  }
</script>

<select
  bind:this={inputElement}
  class="form-control {classes}"
  {id}
  {name}
  {...extraProps}
  on:change={handleChange}
  on:blur={handleBlur}
  bind:value>
  {#if nullValueLabel}
    <option value={null}>{nullValueLabel}</option>
  {/if}
  {#each values as item}
    <option value={item} selected={checkSameValue(item)}>
      {item[labelFieldName]}
    </option>
  {/each}
</select>
