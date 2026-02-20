
<style>
  ul.list-file{
    -webkit-tap-highlight-color: rgba(0,0,0,0);
    /* font-family: 'Texta', sans-serif; */
    color: #222427;
    font-weight: 400;
    letter-spacing: 0.3px;
    font-size: 1rem;
    line-height: 1.2;
    box-sizing: border-box;
    margin-top: 10px;
    margin-bottom: 40px;
    padding: 0;
  }

  li.list-file-html{
    -webkit-tap-highlight-color: rgba(0,0,0,0);
    /* font-family: 'Texta', sans-serif; */
    color: #222427;
    font-weight: 400;
    letter-spacing: 0.3px;
    font-size: 1rem;
    line-height: 1.2;
    box-sizing: border-box;
    padding: 3px 0 3px 35px;
    background-image: url(/feu-after-sales/images/cig/list-file.png);
    background-repeat: no-repeat;
    background-position: left 2px;
    list-style: none;
    margin-bottom: 5px;
    background-size: 30px;
    padding-bottom: 10px;
    padding-top: 10px;
  }


  li.list-file-html > div{
    -webkit-tap-highlight-color: rgba(0,0,0,0);
    /* font-family: 'Texta', sans-serif; */
    font-weight: 400;
    letter-spacing: 0.3px;
    font-size: 1rem;
    line-height: 1.2;
    list-style: none;
    box-sizing: border-box;
    background-color: transparent;
    text-decoration: none;
    outline: none !important;
    color: #222427;
    cursor: pointer;
  }

  li.list-file-html > div:hover{
    -webkit-tap-highlight-color: rgba(0,0,0,0);
    /* font-family: 'Texta', sans-serif; */
    font-weight: 400;
    letter-spacing: 0.3px;
    font-size: 1rem;
    line-height: 1.2;
    list-style: none;
    box-sizing: border-box;
    background-color: transparent;
    outline: none !important;
    color: #0047bb;
    text-decoration: none;
    cursor: pointer;
  }
</style>


<script>
  import { onMount } from "svelte";

  export let name;
  export let value;

  export const valid = true;

  export let options = {};

  let internalState = {
    FRONTE: {
      address: "",
      fileName: "FRONTE",
      fileSelected: false,
      files: []
    }
  };

  let labels = ["FRONTE"];


  onMount(() => {
    if (options.labels) {
      labels = options.labels;
    }
    if (typeof value === "undefined" || value === "") {
      value = {};
      labels.forEach(element => {
        value[element] = "";
      });
    }
  });

  function handleDownload(event){

    if(options && options.handler){

      const id = (event.target.id).split("#")[1];
      options.handler(value[id], options.context);
    }
  }

</script>

<ul class="list-file">
  {#each labels as label}
    <li class="list-file-html"><div id={`${name}#${label}`} on:click={handleDownload}>{label.substring(0, 1).toUpperCase() + label.substring(1).toLowerCase()}</div></li>
  {/each}
</ul>
