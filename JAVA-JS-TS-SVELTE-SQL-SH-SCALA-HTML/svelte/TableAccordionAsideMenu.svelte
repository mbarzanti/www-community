<script>

    import {onMount} from "svelte";

    export let tableId;
    export let title;
    export let form;
    export let valid_map = {};

    onMount(() => {
        jQuery(`a#${tableId}-0.label-link`).removeClass("collapsed");
    })
</script>


<aside class="asidecol">
    <h3 class="buono-type">{title}</h3>
    <ul class="asidetabsmenu asidetabsmenu-ncb" id="{tableId}-aside">
        {#each form.tasks as task, t (task.name)}
            <li>
                <a class:error={!valid_map[task.name]} class:incomplete-view={!valid_map[task.name]}
                   class:complete-view={valid_map[task.name]}
                   class="label-link collapsed" href="javascript:void(0)" id="{tableId}-{t}"
                   data-toggle="collapse" data-target="{tableId}-{t}" aria-expanded="true">

                    {task.title}<br/>
                </a>
            </li>
        {/each}
    </ul>

</aside>
<style>
    .asidetabsmenu > li:first-child > a.label-link {
        /* color: black; */
        border-top: 1px solid #D0D0D0;
    }
</style>