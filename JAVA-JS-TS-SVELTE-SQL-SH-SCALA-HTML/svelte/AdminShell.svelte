<script>
    import {AppBar, Button, Icon, MaterialApp, Container} from 'svelte-materialify';
    import app_logo from '../assets/images/app_logo.png';
    import {mdiAccountSearch, mdiMenu, mdiAccount} from '@mdi/js';
    import posteitaliane from './../assets/images/brand/posteitaliane.png';
    import SideBarMenu from "./SideBarMenu.svelte";

    let theme = 'light';

    var sidebarMenu;

    export let title;
    export let status;
    export let user;

</script>

<style>
    main {
        padding-top: 56px;
        height: 100vh;
    }
</style>

<MaterialApp {theme}>
    <main style="background-color: lightgray;">
        <AppBar fixed style="width: 100%;">
            <div slot="icon">
                <Button depressed fab on:click={() => sidebarMenu.toggleNavigation()}>
                    <Icon path={mdiMenu}/>
                </Button>
                <Button depressed fab on:click={() => status={page:'welcome',context:{}}}>
                    <img src={app_logo} width="40"/>
                </Button>
            </div>
            <span slot="title">
                {title}
            </span>
            <span slot="default">
        </span>
            <span slot="extension">
        </span>
            <div style="flex-grow:1"/>
            <!--
            <Button depressed>
                <Icon path={mdiAccountSearch}/>
                Find User
            </Button>
            &nbsp;
            <Button depressed>
                <Icon path={mdiAccount}/>
                New User
            </Button>
            &nbsp;
            -->
            <img src={posteitaliane} alt="logo caat" height="25px"/>
            &nbsp;
        </AppBar>

        <SideBarMenu bind:status={status} bind:this={sidebarMenu} {user}></SideBarMenu>

        <Container>
            <slot>
            </slot>
        </Container>

    </main>

</MaterialApp>