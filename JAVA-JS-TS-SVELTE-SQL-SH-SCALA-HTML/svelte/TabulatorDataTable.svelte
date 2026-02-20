<script>
    //import 'tabulator-tables/dist/css/materialize/tabulator_materialize.min.css';
    import Tabulator from 'tabulator-tables';
    import 'tabulator-tables/dist/css/tabulator.min.css';
    import 'tabulator-tables/dist/css/semantic-ui/tabulator_semantic-ui.min.css';
    import UpdateBtn from './actions/UpdateBtn.svelte';
    import RemoveBtn from './actions/RemoveBtn.svelte';
    import ChpwdBtn from './actions/ChpwdBtn.svelte';
    import ViewBtn from './actions/ViewBtn.svelte';
    import { onMount, afterUpdate, createEventDispatcher } from 'svelte'

    export let title;
    export let data;
    export let columns;

    var datatable;
    var tableElement;

    const rowEventDispatcher = createEventDispatcher();

    function initTable() {
        datatable = new Tabulator(tableElement, {
            placeholder:"nessun utente trovato",
            rowFormatter:function(row){
                //row - row component

                row.getCells().forEach((cell) => {
                    console.log('cell: ' + cell.getField());

                    if (cell.getField() === 'chpwd') {
                        new ChpwdBtn({
                            target: cell.getElement(),
                        }).$on('click', (e) => {
                            rowEventDispatcher('chpwd', {
                                row: row
                            });
                        });
                    }

                    if (cell.getField() === 'update') {
                        new UpdateBtn({
                            target: cell.getElement(),
                        }).$on('click', (e) => {
                            rowEventDispatcher('update', {
                               row: row
                            });
                        });
                    }

                    if (cell.getField() === 'view') {
                        new ViewBtn({
                            target: cell.getElement(),
                        }).$on('click', (e) => {
                            rowEventDispatcher('view', {
                                row: row
                            });
                        });
                    }

                    if (cell.getField() === 'remove') {
                       new RemoveBtn({
                            target: cell.getElement(),
                        }).$on('click', (e) => {
                            rowEventDispatcher('remove', {
                                row: row
                            });
                        })
                    }

                })

            },
            langs: {
                "it-it": {
                    "pagination": {
                        "first": "prima",
                        "first_title": "prima pagina",
                        "last": "ultima",
                        "last_title": "ultima pagina",
                        "prev": "precedente",
                        "prev_title": "pagina precedente",
                        "next": "successiva",
                        "next_title": "pagina successiva",
                        "all": "tutte",
                    },
                }
            },
            data:[],           //load row data from array
            layout:"fitColumns",      //fit columns to width of table
            responsiveLayout:"hide",  //hide columns that dont fit on the table
            tooltips:true,            //show tool tips on cells
            addRowPos:"top",          //when adding a new row, add it to the top of the table
            history:true,             //allow undo and redo actions on the table
            pagination:"local",       //paginate the data
            paginationSize:5,         //allow 7 rows per page of data
            movableColumns:true,      //allow column order to be changed
            resizableRows:false,       //allow row order to be changed
            columns: columns,
        });
        datatable.setLocale("it-it");
    }

    export function replaceData(data) {
        data = data;
        datatable.replaceData(data);
    }

    onMount(() => {
        initTable();
    })

    afterUpdate(()  => {

    });

</script>

<style>

</style>

<div bind:this={tableElement} width="100%">
</div>

