<script>
    export let src;
    export let srcset;
    export let href;
</script>
<a class="btn btn-sm btn-secondary btn-expand" {href}>
    <img class="spacer-xs-right-10"
         {src}
         {srcset}>
    <slot/>
</a>
<br class="hidden-xs">