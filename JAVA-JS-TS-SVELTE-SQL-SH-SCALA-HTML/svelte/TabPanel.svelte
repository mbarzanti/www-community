<style>
  div.hide{
    display:none;
  }
</style>
<script>
    import { getContext } from 'svelte';
    import { TABS } from './Tabs.svelte';

    const panel = {};
    const { registerPanel, selectedPanel } = getContext(TABS);

    registerPanel(panel);
</script>

<!--
{#if $selectedPanel === panel}
    <slot></slot>
{/if}
-->

<div class:hide="{$selectedPanel !== panel}">
  <slot></slot>
</div>


