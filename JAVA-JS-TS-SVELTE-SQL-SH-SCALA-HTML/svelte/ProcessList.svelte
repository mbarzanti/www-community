<script>
    export let progress_list
    export let flag_errors
    import Loader from "../../SvelteKit/elements/Loader.svelte";
    import {TASK_STATUS, SUBTASK_STATUS} from "./constants";
    import * as labels from './../../commons/labels';
</script>

<style>

.error_message{
   text-align: "center";
   color: "red";
}
.title_message{
   text-align: "center";
}
.card-title.title_message {
    font-size: 2rem;
    margin-top: -3rem;
    margin-bottom: 2rem;
}
</style>

<div class="container width960">
    <div class="content-padd pt10">
        {#if progress_list}
            <div class="card-body">
                <div class="table-verifica-ruoli">
                 <div class="card-title title_message">{labels.BPM.title}</div>
                 {#if flag_errors}
                 <div class="card-title error_message">{labels.BPM.list.error_title}</div>
                 {/if}
                    <table class=" results  projalloc">
                        <thead>
                               <tr>
                                    <th>
                                         {labels.BPM.list.title}
                                    </th>
                                    <th>

                                    </th>
                                </tr>
                            </thead>
                        <tbody>
                            {#each progress_list as proc}
                                <tr
                                    class:list-group-item-success={proc.status === SUBTASK_STATUS.COMPLETED}
                                    class:list-group-item-warning={proc.status === SUBTASK_STATUS.RECOVERABLE}
                                    class:list-group-item-danger={proc.status === SUBTASK_STATUS.SYSTEM_ERROR}
                                    class:list-group-item-light={proc.status === SUBTASK_STATUS.EMPTY}>
                                    <td data-title="prodotto" class="product-td">
                                        <p class="project-label">{proc.name}</p>
                                    </td>
                                    {#if proc.status === SUBTASK_STATUS.COMPLETED || proc.status  === SUBTASK_STATUS.OK}
                                        <td class="status">
                                            <div class="prod-status ok-status"><span class="statuslabel">{labels.BPM.list.verificato}</span>
                                            </div>
                                        </td>
                                    {/if}
                                        {#if proc.status  === SUBTASK_STATUS.SYSTEM_ERROR || proc.status  === SUBTASK_STATUS.BLOCKING_ERROR }
                                            <td class="status">
                                            <div class="prod-status warning-status"><span class="statuslabel">{labels.BPM.list.error}</span></div>
                                        </td>
                                    {/if}
                                        {#if proc.status === SUBTASK_STATUS.RECOVERABLE}
                                        <td class="status">
                                            <div class="prod-status warning-status"><span class="statuslabel">{labels.BPM.list.error}</span></div>
                                        </td>
                                    {/if}
                                        {#if proc.status  === SUBTASK_STATUS.EMPTY || proc.status  === SUBTASK_STATUS.SKIPPED}
                                        <td class="status">
                                            <div class=" prod-status not-expected-status"><span class="statuslabel">{labels.BPM.list.verifica}</span></div>
                                        </td>
                                    {/if}
                                </tr>
                            {/each}
                        </tbody>
                    </table>
                </div>
            </div>
        {/if}
    </div>
</div>