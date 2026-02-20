<script>
    import { getContext } from 'svelte';
    import { TABS } from './Tabs.svelte';

    const tab = {};
    const { registerTab, selectTab, selectedTab } = getContext(TABS);

    export let invalid = false;

    registerTab(tab);
</script>

<style>
    div.tab-list > button {
        background: none;
        border: none;
        border-bottom: 2px solid white;
        border-radius: 0;
        margin: 0;
        color: #ccc;

        /*background-image: url(../images/ico-status-complete.png);*/
        background-repeat: no-repeat;
        background-position: right center;
        padding-right: 16px;
        background-size: 16px!important;
    }

    div.tab-list > button.invalid {
        background-image: url(../images/failed.svg);
        background-size: 15%;
    }

    div.tab-list > button.selected {
        border-bottom: 2px solid blue;
        color: #333;
    }
</style>

<button class:selected="{$selectedTab === tab}" class:invalid="{invalid}" on:click="{() => selectTab(tab)}">
    <slot></slot>
</button>