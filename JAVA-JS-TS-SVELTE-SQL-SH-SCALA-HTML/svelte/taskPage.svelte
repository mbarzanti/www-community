<script>
    import SimplePage from './../../SvelteKit/layout/SimplePage.svelte';
    import Stepper from "./stepper/stepper.svelte";
    import * as config from '../../pages/stepperConfig';
    import { _ } from 'svelte-i18n';
    import { beforeUpdate} from 'svelte';


    const DEFAULT_STEPS = config.steps;

    export let nextStateEnabled = false;
    export let currentStep;
    export let currentTask;
    export let steps = DEFAULT_STEPS;
    export let appState = {};
    appState;

    function checkLocalized(){
        return steps && steps.length>0 && steps[0].localized;
    }

    function localizeSteps(){
        steps.forEach(
                (step) => {
                    step.title = $_(step.title, { default: step.title });
                    step.subtitle = $_(step.subtitle, { default: step.subtitle });
                    step.localized = true;
                    step.tasks.forEach(
                            (task) => {
                                task.title = $_(task.title, { default: task.title });
                                task.subtitle = $_(task.subtitle, { default: task.subtitle });
                            }
                    );
                }
        );
    }

    beforeUpdate(()=>{
        if(!steps){
            steps = DEFAULT_STEPS;
        }
        if( !checkLocalized()){
            localizeSteps();
        }
    })



</script>
<SimplePage>
    <Stepper
        title={$_("stepper.title")}
        bind:currentStep={currentStep}
        bind:currentTask={currentTask}
        bind:done={nextStateEnabled}
        bind:steps={steps}
    />

    <slot/>
</SimplePage>
