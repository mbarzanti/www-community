<script>

export let valuemin = 0;
export let valuemax = 100;
export let valuenow = 0;

let perc = ""

$ : {
    perc = ((valuenow*100) / valuemax) + "%"
}

</script>
<style>

    .green {
        color: green;
        font-weight: bold;
    }

</style>
<div class="progress">
    <div class="progress-bar" role="progressbar" style="width: {perc}" aria-valuenow={valuenow} aria-valuemin={valuemin} aria-valuemax={valuemax}></div>
</div>
