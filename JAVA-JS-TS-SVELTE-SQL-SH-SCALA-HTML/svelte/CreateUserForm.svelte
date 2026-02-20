<script>
    import AutoSubForm from "../widgets/forms/AutoSubForm.svelte";
    import {AppBar, Button, Icon, MaterialApp, Snackbar} from 'svelte-materialify';

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
                            "regex": "^[a-zA-Z0-9](_(?!(\\.|_))|\\.(?!(_|\\.))|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]$",
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
                            "name": "mailNickname",
                            "label": "email nick name",
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
                    {
                        "type": "checkbox",
                        "name": "accountEnabled",
                        "label": "account enabled",
                        "default_value": false,
                        "size": 12,
                        "options": {
                            "debug": false
                        }
                    },
                ]
            },
            {
                "type": "group",
                "name": "passwordpolicy",
                "title": "password policy",
                "values": [
                    {
                        "type": "password",
                        "name": "password",
                        "label": "password",
                        "default_value": "",
                        "size": 12,
                        "options": {
                            "floatingLabel": true,
                            "placeholder": "",
                            "regex": "",
                            "validationMessage": "the password does not comply with complexity requirements",
                            "required": true,
                            "default_invalid": false,
                            "debug": false
                        }
                    },
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

    async function handleReset(event) {
        createUserForm.reset();
    }

    async function handleSubmit(event) {
        console.log('SubmitJobForm::handleSubmit entry values: ' + JSON.stringify(values_map));

        processing = true;

        let formData = new FormData();
        Object.keys(values_map).forEach(key => {

            if (key === "userPrincipalName") {
                // appends domain  @posteaadb2ctest.onmicrosoft.com,
                // TODO: make it server-side
                formData.append(key, values_map[key] + '@posteaadb2ctest.onmicrosoft.com');
            } else {
                formData.append(key, values_map[key]);
            }

        })
        console.log('create formData: ' + JSON.stringify(formData));

        fetch(`/aadgui/api/v1/users`,
            {
                method: "POST",
                body: formData,
            })
            .then(r => r.json())
            .then(r => {
                snackbar = true;
                snackbarMessage = "the user " + values_map.displayName + " has been created";
                createUserForm.reset();
                processing = false;
            })
            .catch(err => {
                console.log(err);
                snackbarMessage = "the user " + values_map.displayName + " has not been created with error: " + err;
                processing = false;
            });
    }

</script>

<style>


</style>


<AutoSubForm
        bind:this={createUserForm}
        title="user create"
        {formDescriptor}
        {contentjson}
        bind:values_map={values_map}
        bind:validation_map={validation_map}
        bind:invalid={invalid}
        bind:resetCounter={resetCounter}>
    <div slot="actions">
        <Button disabled={invalid || processing} on:click={handleSubmit}>create</Button>
        <Button disabled={processing} on:click={handleReset}>reset</Button>
    </div>
</AutoSubForm>


<Snackbar class="flex-column" bind:active={snackbar} bottom center timeout={3000}>
    {snackbarMessage}
    <div class="mt-1">
        <Button text class="success-text" on:click={() => snackbar = false}>Dismiss</Button>
    </div>
</Snackbar>