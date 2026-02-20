<script>
    import Stepper from "../../UiKitLite/stepper/Stepper.svelte";
    import { steps } from '../../stores.js';

    export let currentStepName = null;
</script>

<Stepper currentStepName={currentStepName} steps={$steps}/>