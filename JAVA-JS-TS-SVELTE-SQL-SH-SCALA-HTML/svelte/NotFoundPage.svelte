<script>
    import AppBasePageLayout from "./AppBasePageLayout.svelte";
    import GenericFunnelResult from "../UiKitLite/layouts/GenericFunnelResult.svelte";
</script>

<AppBasePageLayout>
    <div slot="default">
        <GenericFunnelResult
                resultTitle="Pagina 404"
                resultText="Pagina non trovata."/>
    </div>
</AppBasePageLayout>
