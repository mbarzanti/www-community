<script>

    import {
        AppBar,
        Avatar,
        Button,
        Divider,
        Icon,
        List,
        ListItem,
        MaterialApp,
        NavigationDrawer,
        Overlay,
    } from 'svelte-materialify';

    import {mdiHome, mdiAccountSearch, mdiAccountSettings, mdiKey, mdiAccountPlus} from '@mdi/js';

    let active = false;

    export function toggleNavigation() {
        active = !active;
    }

    export let status;

    export let user;

</script>

<NavigationDrawer absolute {active} style="padding-top: 60px;">
    <ListItem>
        {user.surname}, {user.givenName}
        <br>
        <code>
          {user.userPrincipalName}
        </code>
    </ListItem>
    <Divider/>
    <List dense nav>
        <ListItem on:click={() => status={page:'welcome',context:{}}}>
            <span slot="prepend">
              <Icon path={mdiHome}/>
            </span>
            Home
        </ListItem>

        <ListItem on:click={() => status={page:'userList',context:{}}}>
            <span slot="prepend">
              <Icon path={mdiAccountSearch}/>
            </span>
            Cerca Utenti
        </ListItem>

        <ListItem on:click={() => status={page:'viewUser',context:{}}}>
            <span slot="prepend">
              <Icon path={mdiAccountSettings}/>
            </span>
            Mostra Utente
        </ListItem>

        <ListItem on:click={() => status={page:'changePassword',context:{}}}>
        <span slot="prepend">
          <Icon path={mdiKey}/>
        </span>
            Cambia Password
        </ListItem>

        <ListItem on:click={() => status={page:'createUser',context:{}}}>
            <span slot="prepend">
              <Icon path={mdiAccountPlus}/>
            </span>
            (dp) Create User
        </ListItem>

    </List>
</NavigationDrawer>
<Overlay {active} absolute on:click={toggleNavigation} index={1}/>