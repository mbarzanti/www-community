<script>
/** Url da usare per il download */
export let url = null;
/** Label da usare per il link del download*/
export let label = "download";

let download = () => {
  let downloadIframe;

  downloadIframe = document.createElement('iframe');
  downloadIframe.style.display = 'none';
  document.body.appendChild(downloadIframe);
  downloadIframe.src = url;
  
  setTimeout(() => document.body.removeChild(downloadIframe), 1000);
}
</script>

<a href="javascript:void(0)" on:click={()=>download()}>{@html label}</a>