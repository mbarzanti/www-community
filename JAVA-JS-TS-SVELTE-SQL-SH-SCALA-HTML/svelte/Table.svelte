<script>
/*
// Colonne della tabella, la proprieta "cell" viene usata per recuperare il valore da data
const columns = [
  {title: 'Name', cell: 'name'},
  {title: 'Surname', cell: 'surname'},
  {title: '', cell: 'download'}
];

// Dati da inserire nella tabella, ogni oggetto e' una righa
// Da notare la proprieta "download" che non e' semplicemente un valore ma crea un componente svelte (ButtonDownload) 
// a cui verranno passate le proprieta èresenti in "props"
const data = [
  {name:"Elvis Aaron", surname: "Presley", download:{component: ButtonDownload, props:{url:"/download/001"}}},
  {name:"Enzo", surname: "Ghinazzi", download:{component: ButtonDownload, props:{url:"/download/001"}}}
];

<Table 
  title="Cantanti" 
  info="Una lista di cantanti fenomenali" 
  {columns} 
  {data} 
  emptyMessage="Nessun cantante fenomenale trovato"></Table>
*/

/** Titolo da impostare per la tabella */
export let title = null;

/** {string} testo sotto il titolo che da informazioni aggiuntive riguardanti la tabella */
export let info = null;

/** Lista di oggetti che definiscono le colonne presenti nella tabella */
export let columns = [];

/** Lista di oggetti che definiranno il contenuto della tabella */
export let data = [];

/** Valore booleano che indica se mostrare l'header della tabella */
export let showHeader = true;

/** Messaggio da mostrare se non vengono trovati elementi*/
export let emptyMessage = 'No elements'
</script>

{#if title || info}
<div class="row">
    <div class="col-12 col-sm-8 col-md-8">
        {#if title}
        <div class="title-card">{title}</div>
        {/if}
        {#if info}
        <div class="info-card">{info}</div>
        {/if}
    </div>
</div>
{/if}
<div class="row">
    <table class="table">
      { #if showHeader }
      <thead>
        <tr>
          { #each columns as headerItem }
          <th scope="col">
            {headerItem.title}
          </th>
          { /each }
        </tr>
      </thead>
      { /if }
      <tbody>
        { #if data.length === 0 && emptyMessage} 
          <tr>
            <td colspan="{ columns.length }">
              {emptyMessage}
            </td>
          </tr>
        {:else}
          { #each data as row }
          <tr>
            { #each columns as item }
            <td>
              { #if typeof item.cell === 'function' }
              { @html item.cell(row) }
              { :else if row[item.cell].component }
                <svelte:component this={row[item.cell].component} {...row[item.cell].props} />
              { :else }
              { @html row[item.cell] }
              { /if }
            </td>
            { /each }
          </tr>
          { /each }
        { /if }
      </tbody>
    </table>
</div>