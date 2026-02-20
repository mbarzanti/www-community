<script>
    import { bankAccounts} from '../../../stores';
    let pageNumber = 0;
    let token = "";

    

</script>


<div class="row">
    <div class="col-xs-12 col-md-6 col-xs-offset-6">
    {#if !$bankAccounts.lastPage}
        <button class="btn btn-primary">Next</button>
    {/if}
    {#if pageNumber > 0}
        <button class="btn btn-primary">Prev</button>
    {/if}
    </div>
</div>

