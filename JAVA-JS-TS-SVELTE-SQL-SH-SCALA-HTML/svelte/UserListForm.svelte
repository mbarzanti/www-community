<script>

    import {onMount} from 'svelte';
    import {Overlay, Card, CardTitle, CardSubtitle, Divider, TextField, Row, Col, Container, Button, ProgressCircular} from 'svelte-materialify';
    import DataTable from '../widgets/table/TabulatorDataTable.svelte';


    var userListTable;
    var snackbar = false;
    var snackbarMessage = '';

    var namePrefix;

    var formVisible = true;
    var tableVisible = false;
    var processing = false;

    export let status;


    var columns = [
        {title:"id", field:"id", hozAlign:"left" },
        {title:"nome utente", field:"displayName", hozAlign:"left" },
        {title:"user principal", field:"userPrincipalName", hozAlign:"left" },
        /*{title:"nome", field:"givenName", hozAlign:"left"},
        {title:"cognome", field:"surname", hozAlign:"left"},
        {title:"mail", field:"mail", hozAlign:"left"},*/
        {field:"view", headerSort:false, width: 100},
        {field:"chpwd", headerSort:false, width: 100},
    ];

    var data = [];

    function doSearch(e) {
        processing = true;
        tableVisible = true;

        fetch(`/aadgui/api/v1/users/find/` + namePrefix,
            {
                method: "GET",
            })
            .then(r => r.json())
            .then(r => {
                snackbar = true;
                snackbarMessage = "the user has been created";
                console.log('result: ' + JSON.stringify(r));

                data = [];
                r.value.forEach((item) => {
                    data = [...data,
                        {
                            displayName: item.displayName,
                            userPrincipalName: item.userPrincipalName,
                            givenName: item.givenName,
                            surname: item.surname,
                            mail: item.mail,
                            id: item.id
                        },
                    ];

                })


                userListTable.replaceData(data);

                formVisible = false;
                processing = false;
            })
            .catch(err => {
                console.log(err);
                snackbarMessage = "the user has not been created with error: " + err;
            });

    }

    function doBack(e) {
        processing = false;
        tableVisible = false;
        formVisible = true;
        namePrefix = '';
        userListTable.replaceData([]);
    }

    function handleUserView(e) {
        var rowData = e.detail.row.getData()
        var userId = rowData['id'];
        console.log('userId: ' + userId);
        status = {
            page: 'viewUser',
            context: {
                userId: userId
            }
        }
    }

    function handleChangePwd(e) {
        var rowData = e.detail.row.getData()
        var userId = rowData['id'];
        console.log('userId: ' + userId);
        status = {
            page: 'changePassword',
            context: {
                userId: userId
            }
        }
    }

    onMount(() => {})


</script>

<Card style="width: 100%;">
    <CardTitle>
        Ricerca Utenti
    </CardTitle>
    <CardSubtitle>

    </CardSubtitle>
    <Divider/>
    <Container>
        {#if formVisible}
        <Row>
            <Col>
                <TextField bind:value={namePrefix}>digita le prime lettere del nome utente</TextField>
            </Col>
            <Col>
                <Button on:click={doSearch}>Cerca</Button>
            </Col>
        </Row>
        {:else}
        <Row>
            <Col>
                <Button on:click={doBack}>Nuova Ricerca</Button>
            </Col>
        </Row>
        {/if}
        <Row>
            <Col>
                <DataTable bind:this={userListTable} title={"users list"} {data} {columns} on:view={handleUserView} on:chpwd={handleChangePwd}/>
            </Col>
        </Row>
        {#if processing}
        <Overlay active>
            <ProgressCircular color="white" indeterminate size={128} />
        </Overlay>
        {/if}
    </Container>
</Card>