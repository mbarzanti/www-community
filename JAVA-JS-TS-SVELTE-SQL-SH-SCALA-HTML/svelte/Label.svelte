<script>

    import {createEventDispatcher, afterUpdate, onMount, onDestroy} from 'svelte';
    import {regexTest} from '../sveltekit';

    const eventDispatcher = createEventDispatcher();

    export let name;
    // export let label = '';
    export let value = undefined;
    export let options = {};
    export const valid = true;
    export let default_value = "";
    export let style = options.style;
    export let styleClass = options.styleClass;
    let contextMasterUnsubscribe = undefined;


    onMount(() => {
        if(typeof value === 'undefined'){
            value = default_value;
        }
        if(options && options.handler && options.contextMaster){
            contextMasterUnsubscribe = options.contextMaster.subscribe(eventValue => {
                if(eventValue && Object.keys(eventValue).length > 0){
                    value = options.handler(eventValue);
                }
            });
        }
    });

    onDestroy( ()=> {
        if(contextMasterUnsubscribe){
            contextMasterUnsubscribe();
        }
    });

</script>

<style>
.form-control[readonly] {
    background-color: transparent;
    opacity: 1;
}
</style>

<input class={"input-text form-control custom-label " + (styleClass ? styleClass : "")} type="text" style="{style}" autocomplete="off"
    {name}
    readonly={true}
    bind:value={value}
>
