<script>
    import {createEventDispatcher, onMount, tick } from 'svelte';

    const eventDispatcher = createEventDispatcher();

    export let value;
    export let values=[];
    // export let label = "";
    export let valid = true;
    export let name;
    export let options;
    export let default_value;
    let radioGroupValue;


    let inputMap = {};

    onMount(async () => {
        if(typeof value === "undefined"  && typeof default_value != "undefined" && default_value != null){
            value = default_value;
        }
        if(typeof value !== "undefined"){
            radioGroupValue = value;
        }
        if (options && options.context && options.handlerView) {
            options.handlerView(options.context, (data) => {
                    values = data;
                    validate();
                })
        }
        await tick();
        validate();
    });

    function validate() {
        valid = (values.findIndex((item)=>item.value===value)) >= 0;
    }

    async function handleClick(e) {
        value = e.target.value;
        validate();
        eventDispatcher('change', {value: e.target.value, id: name});
    }

    function isChecked(v, dv) {
        if(v.localeCompare) {
            return v.localeCompare(dv) === 0 ? "checked" : undefined;
        } else {
            return v === dv ? "checked" : undefined;
        }

    }


</script>

<style>
    .radio-block {
        display: block !important;
    }
</style>

{#each values as v, i}
    <span>
        <label class="label-check label " class:radio-block={options && !options.inline}>
             <input bind:this={inputMap[i]} type="radio" class="iradio_flat-blue" noclass="checkbox_custom"
                    value="{v.value}"
                    {name} bind:group={radioGroupValue}
                    on:click="{handleClick}" >
            {v.label}
        </label>
    </span>
{/each}

