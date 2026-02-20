<script>
    import AutoSubForm from "../widgets/forms/AutoSubForm.svelte";
    import {TextField, Button, Chip, Icon, Overlay, ProgressCircular, Snackbar, Row, Col, Container, Divider, Card, CardTitle} from 'svelte-materialify';
    import {onMount} from 'svelte';
    import {mdiAccount, mdiAccountCheck} from '@mdi/js';
    import { retrieveExtensionsById, retrieveUserById } from '../js/apiCalls';

    export let status;

    var invalid = true;
    var processing = false;
    var values_map = {}
    var validation_map = {}
    let contentjson;
    var resetCounter = 0;

    var createUserForm;

    var snackbar = false;
    var snackbarMessage;

    var userId;
    var retrieved;
    var userRoleArray = [];
    var userMnemonicArray = [];

    function now() {
        return '31-12-2020'; // moment().format('YYYY-MM-DD HH:mm:ss');
    }

    var formDescriptor = {
        "values": [
            {
                "type": "group",
                "name": "user data",
                "title": "user data",
                "values": [
                    /*{
                        "type": "text",
                        "name": "userid",
                        "label": "userId",
                        "default_value": "",
                        "size": 12,
                        "readonly": true,
                        "options": {
                            "debug": false
                        }
                    },*/
                    {
                        "type": "text",
                        "name": "displayName",
                        "label": "display name",
                        "default_value": "",
                        "size": 12,
                        "readonly": true,
                        "options": {
                            "debug": false
                        }
                    },
                  /*  [
                        {
                            "type": "text",
                            "name": "givenName",
                            "label": "given name",
                            "default_value": "",
                            "size": 12,
                            "readonly": true,
                            "options": {
                                "floatingLabel": true,
                                "placeholder": "",
                                "regex": "^[\\w'\\-,.][^0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}|~<>;:[\\]]{2,}$",
                                "validationMessage": "Invalid name",
                                "required": true,
                                "default_invalid": false,
                                "debug": false
                            }
                        },
                        {
                            "type": "text",
                            "name": "surname",
                            "label": "last name",
                            "default_value": "",
                            "readonly": true,
                            "size": 12,
                            "options": {
                                "floatingLabel": true,
                                "placeholder": "",
                                "regex": "^[\\w'\\-,.][^0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}|~<>;:[\\]]{2,}$",
                                "validationMessage": "Invalid name",
                                "required": true,
                                "default_invalid": false,
                                "debug": false
                            }
                        },
                    ],*/
                    {
                        "type": "text",
                        "name": "userPrincipalName",
                        "label": "user principal name",
                        "default_value": "",
                        "size": 12,
                        "readonly": true,
                        "options": {
                            "debug": false
                        }
                    },
                   /* [
                        {
                            "type": "text",
                            "name": "mail",
                            "label": "email",
                            "default_value": "",
                            "size": 12,
                            "readonly": true,
                            "options": {
                                "placeholder": "",
                                "regex": "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$",
                                "validationMessage": "Invalid name",
                                "required": true,
                                "default_invalid": false,
                                "debug": false
                            }
                        },
                        {
                            "type": "text",
                            "name": "mailNickname",
                            "label": "email nick name",
                            "default_value": "",
                            "size": 12,
                            "readonly": true,
                            "options": {
                                "placeholder": "",
                                "regex": "^[\\w'\\-,.][^0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}|~<>;:[\\]]{2,}$",
                                "validationMessage": "Invalid name",
                                "required": true,
                                "default_invalid": false,
                                "debug": false
                            }
                        },
                        {
                            "type": "text",
                            "name": "mobilePhone",
                            "label": "mobile phone",
                            "default_value": "",
                            "size": 12,
                            "readonly": true,
                            "options": {
                                "floatingLabel": true,
                                "placeholder": "",
                                "regex": "^(\\((00|\\+)39\\)|(00|\\+)39)?(38[890]|34[4-90]|36[680]|33[13-90]|32[89]|35[01]|37[019])(\\s?\\d{3}\\s?\\d{3,4}|\\d{6,7})$",
                                "validationMessage": "Valid Phone Number must be in format (+39) 349 1234567",
                                "required": true,
                                "default_invalid": false,
                                "debug": false
                            }
                        },
                    ],*/
                ]
            },
        ],
        "hidden_values": []
    };



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

    function doBack(e) {
        processing = false;
        userId = '';
        retrieved = false;
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

    function doSwitchToChangePwd(e) {
        processing = false;
        retrieved = false;
        status = {
            page: 'changePassword',
            context: {
                userId: values_map['userid']
            }
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
        Mostra Dati Utente
    </CardTitle>
    <Divider/>
    <Container>
        {#if !retrieved }
            <Row>
                <Col>
                    <TextField bind:value={userId}>digita l'id dell'utente che si vuole visualizzare</TextField>
                </Col>
                <Col>
                    <Button on:click={handleRetrieve}>Cerca</Button>
                </Col>
            </Row>
        {:else}
            <Row>
                <Col style="display: flex">
                    <Button on:click={doBack} class="mr-4">Visualizza Nuovo Utente</Button>
                    <Button on:click={doSwitchToSearch} class="mr-4">Cerca Utenti</Button>
                    <Button on:click={doSwitchToChangePwd} class="mr-4">Cambio Password</Button>
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
                        bind:this={createUserForm}
                        title="Mostra Dati Utente"
                        {formDescriptor}
                        {contentjson}
                        bind:values_map={values_map}
                        bind:validation_map={validation_map}
                        bind:invalid={invalid}
                        bind:resetCounter={resetCounter}>
                    <div slot="actions">
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
        <Button text class="error-text" on:click={() => snackbar = false}>Dismiss</Button>
    </div>
</Snackbar>
