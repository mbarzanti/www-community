<script>
    import { beforeUpdate, onMount } from 'svelte';

    export let value = null;
    export let options = {};

    export let default_value = null;

    $: language = options.language;
    
    let docEditor;
    
    let editor;

    beforeUpdate(() => {
        if(!value){
          if(default_value){
            value = default_value;
          }else{
            value = '';
          }
        }
    });

    onMount(() => {
      

        editor = ace.edit(docEditor);
        editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: false
        });
        editor.setTheme("ace/theme/tomorrow");
        editor.session.setMode("ace/mode/" + language);
        editor.getSession().on('change', function() {
            value = editor.getSession().getValue();
        });
        editor.setReadOnly(false);
    })

</script>

<!-- <div class="col-md-12"> -->
    <!-- <div style="text-align:left;margin-bottom:5px;">{label}</div> -->
    <div bind:this={docEditor} class="documentfieldeditor">
        {value}
    </div>
<!-- </div> -->
