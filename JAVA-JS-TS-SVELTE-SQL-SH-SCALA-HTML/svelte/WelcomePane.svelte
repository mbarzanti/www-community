<script>
    import app_intro from '../assets/images/app_intro.png';
    import find_user from '../assets/images/find-user.png';
    import show_user from '../assets/images/show-user.png';
    import change_password from '../assets/images/change-password.png';
    import {
        Card,
        CardTitle,
        CardSubtitle,
        CardActions,
        Button,
        Icon,
        Divider,
        Row,
        Col,
        Container,
        Ripple
    } from 'svelte-materialify/src';
    import SlidingPanel from "../widgets/Panes/SlidingPanel.svelte";
    import MenuItemCard from "../widgets/cards/MenuItemCard.svelte";


    export let status;

    function handleCreateUser(e) {
        status = 'createUser';
    }


</script>

<SlidingPanel>
    <Card style="width: 100%;">
        <div style="background-image: url({app_intro});background-repeat:no-repeat;background-size:cover;min-height: 300px;">
        <CardTitle style="color:white;">Console di Amministrazione delle Utenze</CardTitle>
        <CardSubtitle style="max-width: 500px;color:white;">
            la Console di Amministrazione di Azure Active Directory Admin UI (AADUI), fornisce alcuni pannelli di gestione che consentono di
            indirizzare le principali funzionalità di intervento sulla profilazione delle utenze.
        </CardSubtitle>
        </div>
        <Divider/>
        <Container>
            <div class="d-flex justify-center">
                <div class="white black-text pa-2 ma-1 flex-wrap flex-grow-1 align-content-stretch">
                    <MenuItemCard title="Cerca" subtitle="cerca un utente" image={find_user} on:click={() => status={page:'userList', context: {} }}/>
                </div>
                <div class="white black-text pa-2 ma-1 flex-wrap flex-grow-1 align-content-stretch">
                    <MenuItemCard title="Visualizza" subtitle="visualizza un utente" image={show_user} on:click={() => status = {page: 'viewUser', context: {} }}/>
                </div>
                <div class="white black-text pa-2 ma-1 flex-wrap flex-grow-1 align-content-stretch">
                    <MenuItemCard title="Cambia Password" subtitle="cambia la password di un utente" image={change_password} on:click={() => status = {page: 'changePassword', context: {} }}/>
                </div>
            </div>
        </Container>
    </Card>
</SlidingPanel>