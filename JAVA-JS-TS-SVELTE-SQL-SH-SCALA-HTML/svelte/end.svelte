<style>
    .success-container {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        padding-bottom: 5rem;
        height: auto;
        min-height: 60vh;
        padding-top: 3rem;
    }

    .success-image {
        padding-top: 1rem;
        width: fit-content;
    }

    .success-title {
        color: #222427;
        font-size: 24px;
        font-weight: 500;
        line-height: 36px;
        text-align: center;
        white-space: pre;
    }

 /*   .success-message {
        margin-top: 5rem;
        margin-bottom: 1rem;
        color: #222427;
        font-size: 1.3rem;
        font-weight: 300;
        line-height: 30px;
        text-align: center;
        white-space: pre;
    }*/

    .container {
        margin-top: 2rem;
    }

    .row {
        width: 100%;
    }

   /* .btn {
        cursor: pointer;
    }

    .print-warning-container {
        margin-top: 0;
        margin-bottom: 0;
        font-weight: 500;
    }

    .exit-warning-container {
        margin-top: 0;
        margin-bottom: 0;

    }*/

    .back-home{
        position: absolute;
        bottom: 50px;
        right: 50px;
    }
</style>
<script>
    import * as labels from './../../commons/labels';
    import Button from "../../SvelteKit/forms/Button.svelte";
    import * as utils from "../../commons/utils";
    import {REDIRECT_STATE_KEY} from "../../commons/constants";
    import {onMount} from "svelte";

    export let homeAddress;
    let redirectState;

    function comeBackHome() {
        if(redirectState && typeof redirectState === "object"){
            redirectState = {
                key: REDIRECT_STATE_KEY,
                content: {
                    ...redirectState,
                    skipReview: true,
                    skipAsset: true
                }
            }
        }
        utils.redirectToWithState(
            homeAddress,
            {
                state: redirectState
            }
        );
    }
    onMount(()=>{
        redirectState = utils.popRedirectState(REDIRECT_STATE_KEY);
    })
</script>


<div class="container width960">
    <div class="card success-container">
        <div class="row">
            <div class="col-12 text-center success-title">
                <h4>{labels.SUCCESS_TEXT_END}</h4>
            </div>
        </div>
        <img class="success-image" src={"/feu-after-sales/images/cig/ico-result-success@2x.png"} alt/>

        {#if homeAddress}
            <div class="back-home">
                <Button name="Effettua una nuova variazione" on:click={comeBackHome}/>
            </div>
        {/if}
    </div>
</div>