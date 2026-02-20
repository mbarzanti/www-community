<script>
    import {onMount} from 'svelte';
    import AutoSubForm from "../widgets/forms/AutoSubForm.svelte";
    import {Overlay, ProgressCircular, Chip, Icon, TextField, Button, Snackbar, Card, CardSubtitle, CardTitle, Container, Row, Col, Divider} from 'svelte-materialify';
    import {resetUserPassword, retrieveExtensionsById, retrieveUserById} from "../js/apiCalls";
    import {mdiAccount, mdiAccountCheck} from '@mdi/js';

    var invalid = true;
    var processing = false;
    var values_map = {}
    var validation_map = {}
    let contentjson;
    var resetCounter = 0;

    var passwordUserForm;

    var snackbar = false;
    var snackbarMessage;

    var userId;
    var retrieved;
    var userRoleArray = [];
    var userMnemonicArray = [];


    export let status;

    var formDescriptor = {
        "values": [
            {
                "type": "text",
                "name": "changePwdCode",
                "label": "codice mnemonico",
                "default_value": "",
                "size": 12,
                "options": {
                    "placeholder": "",
                    "regex": ".+",
                    "validationFormula": (v) => {

                        var valid = true;
                        v.split(',').forEach(item => {
                            if (valid && !userMnemonicArray.includes(item)) {
                                valid = false;
                            }
                        })

                        return valid;
                    },
                    "validationMessage": "confermare tramite inserimento tutti i mnemonic dell'utente",
                    "required": true,
                    "default_invalid": false,
                    "debug": false
                }
            },
            {
                "type": "password",
                "name": "newpassword",
                "label": "nuova password",
                "default_value": "",
                "size": 12,
                "options": {
                    "floatingLabel": true,
                    "placeholder": "",
                    "regex": "",
                    "validationFormula": (v) => true,
                    "validationMessage": "la password non risponde ai requisiti di complessità",
                    "required": true,
                    "default_invalid": false,
                    "debug": false
                }
            },
            {
                "type": "password",
                "name": "newpasswordconfirm",
                "label": "conferma nuova password",
                "default_value": "",
                "size": 12,
                "options": {
                    "floatingLabel": true,
                    "placeholder": "",
                    "regex": "",
                    "validationFormula": (v) => values_map['newpassword'] === v,
                    "validationMessage": "le password digitate nei due campi non corrispondono",
                    "required": true,
                    "default_invalid": false,
                    "debug": false
                }
            },
        ],
        "hidden_values": []
    };


    function handleReset(event) {

        passwordUserForm.reset();
    }

    async function handleSubmit(event) {
        console.log('SubmitJobForm::handleSubmit entry values: ' + JSON.stringify(values_map));

        processing = true;
        resetUserPassword(
            values_map['userid'],
            values_map['newpassword'],
            true,
            (r) => {
                console.log('fine');
                processing = false;
                doSwitchToWelcome();
            },
            (err) => {
                console.log(err);
                processing = false;
            }
        )
    }

    function retrieve(userId) {
        processing = true;

        retrieveUserById(
            userId,
            r => {

                if (typeof r.StatusCode !== 'undefined' && r.StatusCode !== 200) {

                    snackbarMessage = "errore durante la visualizzazione dell'utente con displayname " + userId + ": " + r.error.message;
                    snackbar = true;
                    processing = false;
                    retrieved = false;

                } else {

                    values_map['displayName'] = r.displayName;
                    values_map['userid'] = r.id;
                    values_map['userPrincipalName'] = r.userPrincipalName;

                    processing = false;
                    retrieved = true;
                }
            },
            err => {

                console.log(err);
                snackbarMessage = "unable to retrieve user " + userId + " info";
                snackbar = true;
                processing = false;
                retrieved = false;
            })

        retrieveExtensionsById(
            userId,
            ext => {
                if (ext.Name === 'Role') {
                    userRoleArray = ext.Value.split(',');
                }

                if (ext.Name === 'Mnemonic') {
                    userMnemonicArray = ext.Value.split(',');
                }
            },
            err => {

                console.log(err);
            })


    }

    function handleRetrieve(event) {
        console.log('status: ' + JSON.stringify(status));
        if (typeof status.context['userId'] !== 'undefined' && status.context['userId'].length > 0) {
            retrieve(status.context['userId']);
        }

        if (typeof userId !== 'undefined' && userId.length > 0) {
            retrieve(userId);
        }
    }

    function doSwitchToSearch(e) {
        processing = false;
        userId = '';
        retrieved = false;
        status = {
            page: 'userList',
            context: {}
        }
    }

    function doSwitchToWelcome(e) {
        processing = false;
        userId = '';
        retrieved = false;
        status = {
            page: 'welcome',
            context: {}
        }
    }

    onMount(() => {

        handleRetrieve();
    })

</script>

<style>


</style>

<Card style="width: 100%;">
    <CardTitle>
        Cambio Password
    </CardTitle>
    <Divider/>
    <Container>
    {#if !retrieved }
        <Row>
            <Col>
                <TextField bind:value={userId}>digita l'id dell'utente su cui effettuare il cambio password</TextField>
            </Col>
            <Col>
                <Button on:click={handleRetrieve}>Cerca</Button>
            </Col>
        </Row>
    {:else}
        <Row>
            <Col style="display: flex">
                <Button on:click={doSwitchToSearch} class="mr-4">Cerca Utenti</Button>
            </Col>
        </Row>
    {/if}
    {#if retrieved }
        <Row>
            <Col>
                <TextField readonly value={values_map['userid']}>id dell'utente</TextField>
            </Col>
        </Row>
        <Row>
            <Col>
                <span>Ruoli:</span>
                {#each userRoleArray as userRole}
                    <Chip class="ma-2 primary-color">
                        <span>{userRole}</span>
                        <Icon path={mdiAccount} />
                    </Chip>
                {/each}
            </Col>
        </Row>
        <Row>
            <Col>
                <span>Mnemonics:</span>
                {#each userMnemonicArray as userMnemonic}
                    <Chip class="ma-2 primary-color">
                        <span>{userMnemonic}</span>
                        <Icon path={mdiAccountCheck} />
                    </Chip>
                {/each}
            </Col>
        </Row>
        <Row>
            <Col>
                <AutoSubForm
                        bind:this={passwordUserForm}
                        title="cambio password"
                        {formDescriptor}
                        {contentjson}
                        bind:values_map={values_map}
                        bind:validation_map={validation_map}
                        bind:invalid={invalid}
                        bind:resetCounter={resetCounter}>
                    <div slot="actions">
                        <Button disabled={invalid || processing} on:click={handleSubmit}>Modifica</Button>
                        <Button disabled={processing} on:click={handleReset}>Annulla</Button>
                    </div>
                </AutoSubForm>
            </Col>
        </Row>
        {/if}
        {#if processing}
            <Overlay active>
                <ProgressCircular color="white" indeterminate size={128} />
            </Overlay>
        {/if}
    </Container>
</Card>



<Snackbar class="flex-column" bind:active={snackbar} bottom center timeout={3000}>
    {snackbarMessage}
    <div class="mt-1">
        <Button text class="success-text" on:click={() => snackbar = false}>Dismiss</Button>
    </div>
</Snackbar>