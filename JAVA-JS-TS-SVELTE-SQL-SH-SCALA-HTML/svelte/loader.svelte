<script>

</script>

<style>
    .loader {
        margin: auto;
        border: 8px solid #f3f3f3; /* Light grey */
        border-top: 8px solid #0047bb; /* Blue Poste*/
        border-radius: 50%;
        width: 60px;
        height: 60px;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }

    .loader-container{
        display: flex;
        height: -webkit-fill-available;
        align-items: center;
        padding-bottom: 10rem;
    }

</style>
<div class="loader-container">
    <div class="loader"></div>
</div>
