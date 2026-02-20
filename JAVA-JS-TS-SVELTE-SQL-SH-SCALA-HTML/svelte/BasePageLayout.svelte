<script>
    export let enableSidebar = true;
    export let enableMainPills = true;
</script>

<slot name="top-content"/>
<div class="content content-applicative">
    <div class="container">
        <div class="row">
            <div class="col-md-12" id="accessibility-anchor">
                <div class="row">
                    {#if enableSidebar}
                    <div class="col-md-9 col-structure">
                        <div id="main">
                            {#if enableMainPills}
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="main-pills">
                                        <div class="main-pills-wrap">
                                            <div class="row">
                                                <div class="col-sm-12">
                                                    <slot/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {:else}
                            <slot/>
                            {/if}
                            <slot name="bottom-content"/>
                        </div>
                    </div>
                    <div class="col-md-3 col-structure">
                        <div id="bracket">
                            <div class="row">
                                <div class="col-xs-12">
                                    <slot name="sidebar"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    {:else}
                        <div class="col-md-12 col-structure">
                            <div id="main">
                                {#if enableMainPills}
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="main-pills">
                                            <div class="main-pills-wrap">
                                                <div class="row">
                                                    <div class="col-sm-12">
                                                        <slot/>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                {:else}
                                <slot/>
                                {/if}
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</div>
