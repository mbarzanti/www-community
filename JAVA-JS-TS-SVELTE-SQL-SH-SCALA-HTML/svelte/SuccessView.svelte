<script>
    import ResultView from "./ResultView.svelte";
    export let title;
    export let text;
    export let additionalButton;
    export let props;
</script>
<ResultView {title} {text} {additionalButton} {props} success={true}/>