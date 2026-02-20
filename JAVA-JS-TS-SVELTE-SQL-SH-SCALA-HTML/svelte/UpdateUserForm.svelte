<script>
    import {onMount} from 'svelte';
    import AutoSubForm from "../widgets/forms/AutoSubForm.svelte";
    import {AppBar, Button, Icon, MaterialApp, Snackbar} from 'svelte-materialify';

    var invalid = true;
    var processing = false;
    var values_map = {}
    var validation_map = {}
    let contentjson;
    var resetCounter = 0;

    var createUserForm;

    var snackbar = false;
    var snackbarMessage;

    export let userid;

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
                    {
                        "type": "text",
                        "name": "displayName",
                        "label": "display name",
                        "default_value": "",
                        "size": 12,
                        "options": {
                            "placeholder": "",
                            "regex": "^[\\w'\\-,.][^0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}|~<>;:[\\]]{2,}$",
                            "validationMessage": "Invalid name",
                            "required": true,
                            "default_invalid": false,
                            "debug": false
                        }
                    },
                    [
                        {
                            "type": "text",
                            "name": "givenName",
                            "label": "given name",
                            "default_value": "",
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
                        {
                            "type": "text",
                            "name": "surname",
                            "label": "last name",
                            "default_value": "",
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
                    ],
                    {
                        "type": "text",
                        "name": "userPrincipalName",
                        "label": "user principal name",
                        "default_value": "",
                        "size": 12,
                        "options": {
                            "placeholder": "",
                            "regex": "",
                            "validationMessage": "Invalid user principal name",
                            "required": true,
                            "default_invalid": false,
                            "debug": false
                        }
                    },
                    [
                        {
                            "type": "text",
                            "name": "mail",
                            "label": "email",
                            "default_value": "",
                            "size": 12,
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
                            "name": "mobilePhone",
                            "label": "mobile phone",
                            "default_value": "",
                            "size": 12,
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
                    ],
                ]
            },
            {
                "type": "group",
                "name": "passwordpolicy",
                "title": "password policy",
                "values": [
                    {
                        "type": "checkbox",
                        "name": "forceChangePasswordNextSignIn",
                        "label": "password change on the next sign in",
                        "default_value": false,
                        "size": 12,
                        "options": {
                            "required": false,
                            "debug": false
                        }
                    },
                    {
                        "type": "checkbox",
                        "name": "forceChangePasswordNextSignInWithMfa",
                        "label": "password change on the next sign in with mfa",
                        "default_value": false,
                        "size": 12,
                        "options": {
                            "required": false,
                            "debug": false
                        }
                    },
                ]
            }
        ],
        "hidden_values": []
    };


    function handleRetrieve(event) {

        fetch(`/aadgui/api/v1/user/` + userid,
            {
                method: "GET",
            })
            .then(r => r.json())
            .then(r => {
                snackbar = true;
                snackbarMessage = "user " + userid + " has been retrieved properly";
                createUserForm.reset();

                console.log('r: ' + JSON.stringify(r));
                values_map['displayName'] = r.displayName;
                values_map['givenName'] = r.givenName;
                values_map['surname'] = r.surname;
                values_map['mail'] = r.mail;
                values_map['mobilePhone'] = r.mobilePhone;
                values_map['userPrincipalName'] = r.userPrincipalName;
                values_map['forceChangePasswordNextSignIn'] = r.forceChangePasswordNextSignIn;
                values_map['forceChangePasswordNextSignInWithMfa'] = r.forceChangePasswordNextSignInWithMfa;

                processing = false;
            })
            .catch(err => {
                console.log(err);
                snackbarMessage = "unable to retrieve user " + userid + " info";
                processing = false;
            });

    }

    async function handleSubmit(event) {
        console.log('SubmitJobForm::handleSubmit entry values: ' + JSON.stringify(values_map));

        processing = true;

        let formData = new FormData();
        Object.keys(values_map).forEach(key => {
            formData.append(key, values_map[key]);
        })


        fetch(`/aadgui/api/v1/user/` + userid,
            {
                method: "PUT",
                body: formData,
            })
            .then(r => {
                snackbar = true;
                console.log('update result: ' + JSON.stringify(r));
                snackbarMessage = "the user " + values_map.displayName + " has been update";
                handleRetrieve();
                processing = false;
            })
            .catch(err => {
                console.log(err);
                snackbarMessage = "the user " + values_map.displayName + " has not been updated with error: " + err;
                processing = false;
            });
    }

    onMount(() => {

        handleRetrieve();
    })

</script>

<style>


</style>


<AutoSubForm
        bind:this={createUserForm}
        title="user update"
        {formDescriptor}
        {contentjson}
        bind:values_map={values_map}
        bind:validation_map={validation_map}
        bind:invalid={invalid}
        bind:resetCounter={resetCounter}>
    <div slot="actions">
        <Button disabled={invalid || processing} on:click={handleSubmit}>update</Button>
        <Button disabled={processing} on:click={handleRetrieve}>revert</Button>
    </div>
</AutoSubForm>


<Snackbar class="flex-column" bind:active={snackbar} bottom center timeout={3000}>
    {snackbarMessage}
    <div class="mt-1">
        <Button text class="success-text" on:click={() => snackbar = false}>Dismiss</Button>
    </div>
</Snackbar>