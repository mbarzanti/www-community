<script>
    import {onMount} from 'svelte';

    import CreateUser from "../panels/CreateUserForm.svelte";
    import UserListForm from "../panels/UserListForm.svelte";
    import WelcomePane from "../panels/WelcomePane.svelte";
    import ViewUserPane from "../panels/ViewUserPane.svelte";
    import ChangePassword from "../panels/ChangePasswordForm.svelte";


    async function handleSubmit(event) {
        console.log('SubmitJobForm::handleSubmit entry values: ' + JSON.stringify({}));
    }

    export let status;

    var statusComponentMap = {
        "welcome": WelcomePane,
        "createUser": CreateUser,
        "changePassword": ChangePassword,
        "userList": UserListForm,
        "viewUser": ViewUserPane,
    };

    onMount(() => {
        status = {
            page: 'welcome',
            context : {}
        }
    })

    function component() {
        if (typeof status !== 'undefined') {
            return statusComponentMap[status.page];
        }

        return undefined;
    }


</script>

<style>


</style>


<svelte:component this={component()} bind:status={status}>
</svelte:component>

<!--
<WelcomePane></WelcomePane>

<UpdateUserForm userid="e677d433-dd84-4a45-a44f-d3eaf64cf6a3"></UpdateUserForm>

<UserListForm></UserListForm>

<CreateUser></CreateUser>
-->

