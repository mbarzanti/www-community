<script>
    import {createEventDispatcher} from "svelte";

    export let merchantType;
    export let chooseMerchantTypeModal;
    export let title = "Selezione Tipologia";
    export let description = "Selezione Tipologia Merchant";
    export let buttonLabel = "PROSEGUI";
    export let values = [
        {
            label: "LIBERO PROFESSIONISTA",
            value: "lp"
        },
        {
            label: "SOCIETÀ E DITTE",
            value: "sd"
        },
    ]

    const eventDispatcher = createEventDispatcher();


    function clickHandler(e){
        eventDispatcher('chosen', { value: merchantType});
    }
</script>

<div class="stylemodal-pit modal-locfinder modal fade" bind:this={chooseMerchantTypeModal} data-backdrop="static"
     data-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="modalAuthLabel" style="display: none;"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">
                <div class="modhead">
                    <div class="modhead-title">{title}</div>
                </div>
                <div class="modal-padd no-padd">
                    <div class="row no-gutters">
                        <p class="modaltitlow">{description}</p>
                        <div class="resultmultiwrap">
                            <input bind:group={merchantType} value="empty" type="radio" style="display:none">
                            <table class="results-centered">
                                <tbody>
                                {#each values as entry}
                                    <tr>
                                        <td data-title="Seleziona" class="leftcell">
                                            <input bind:group={merchantType} value="{entry.value}" type="radio">
                                        </td>
                                        <td data-title="Tipologia" class="rightcell">
                                            <span class="segname">{entry.label}</span>
                                        </td>
                                    </tr>
                                {/each}
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="row mb20">
                        <div class="col-12 col-sm-12 col-md-12 ">
                            <div class="searchmwrap tac">
                                <a href="javascript:void(0);"
                                   class="btn btn-yellow"
                                   class:disabled={merchantType === 'nil'}
                                   data-dismiss="modal"
                                   aria-label="Close"
                                   on:click={clickHandler}>
                                    {buttonLabel}
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
