<style>

    .pdf-document-view {
        width: 100%;
        border: none;
        direction: ltr;
    }

    .pdf-document-view-container {
        height: 60vh;
        overflow: auto;
        padding-top: 1rem;
        border: 1px solid black;
    }
    @media (max-width: 767px) {
        .pdf-document-view-container {
            height: 59vh;
            overflow: auto;
            padding-top: 1rem;
            border: 1px solid black;
            margin-top: 18px;
        }

        h3.area-heading {
            margin-left: 16vw;
            margin-right: 16vw;
            text-align: center;
        }

        .control-label.large-label.form-title {
            text-transform: unset;
            padding-left: 26px!important;
            padding-right: 26px!important;
            font-size: 18px!important;
            color: #222427!important;
            padding-top: 1rem!important;
            padding-bottom: 1.1rem!important;
        }
    }

    .control-label.large-label.form-title{
        text-transform: unset;
    }



</style>
<script>
    import pdfjsLib from "pdfjs-dist";
    import pdfjsWorker from "pdfjs-dist/build/pdf.worker.entry";
    import {genDocument} from "../../../api/contracts";

    export let appState;
    export let flowIndex = 0;
    export let nextStepFlowEnabled = false;


    let pdfDocument;
    let pdfNumRenderedPages;
    let pdfNumPages;

    let pdfPagesContainer;
    let pdfFirstPage;
    let pdfOtherPages = [];

    let currentFlowIndex = -1;

    const PAGE_SCALE = 1.8;
    let tentatives = appState.alternativeFlowContext.documentsIdList.length;

    pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;

    $: if (flowIndex !== currentFlowIndex) {
        currentFlowIndex = flowIndex;
        pdfOtherPages.forEach((element) => {
            pdfPagesContainer.removeChild(element);
        });

        pdfOtherPages = [];
        // TEST
        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
        //window.scrollTo({ top: 0, behavior: "smooth" });
        appState.alternativeFlowContext.nextStepFlowEnabled = false;
        nextStepFlowEnabled = false;
        loadDocument()
    }


    function addPage(page, first = false) {
        const viewport = page.getViewport({scale: PAGE_SCALE});
        let canvas;
        if (!first) {
            canvas = document.createElement("canvas");
            canvas.className = pdfFirstPage.className;
            canvas.style.display = "block";
        } else {
            canvas = pdfFirstPage;
        }
        let context = canvas.getContext('2d');
        canvas.height = viewport.height;
        canvas.width = viewport.width;
        if (first) {
            context.clearRect(0, 0, canvas.width, canvas.height);
            context.beginPath();
        }
        page.render({canvasContext: context, viewport: viewport});
        if (!first) {
            pdfPagesContainer.appendChild(canvas);
            pdfOtherPages.push(canvas);
        }
    }

    function addFirstPage(page) {
        addPage(page, true)
        jQuery([document.documentElement, document.body]).animate({
            scrollTop: jQuery("body").offset().top
        }, 500);
        //window.scrollTo({ top: 0, behavior: "smooth" });
    }

    function loadDocument() {
        let loadingTask = pdfjsLib.getDocument(appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].url);


            loadingTask.promise.then(function (pdf) {
                pdfNumRenderedPages = 0;
                pdfNumPages = pdf.numPages;
                pdfDocument = pdf;
                renderPage(pdf, true);
                if (pdfNumPages > 1) {
                    renderPage(pdf);
                }
                if (!appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].description) {
                    setTimeout(() => {
                        appState.alternativeFlowContext.nextStepFlowEnabled = true;
                        nextStepFlowEnabled = true;
                    }, 500);
                }
            }).catch(
                    ()=>{
                        tentatives--;
                        if(tentatives > 0) {
                            genDocument(appState.alternativeFlowContext.documentsIdList[flowIndex],
                                    (response, documentId, success) => {
                                        if(success){
                                            appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].url = response[documentId].url;
                                            loadDocument();
                                        } else {
                                            tentatives--;
                                            setTimeout(loadDocument, 2000);
                                        }
                                    }, true);
                        }
                    }
            );

    }

    function renderPage(pdf, first = false) {
        pdfNumRenderedPages++;
        const renderer = first ? addFirstPage : addPage;
        pdf.getPage(pdfNumRenderedPages).then(renderer);
    }


    function loadNewPageOnScroll() {
        if (pdfPagesContainer.scrollTop > pdfFirstPage.offsetHeight * (pdfNumRenderedPages <= 2 ? 1 : pdfNumRenderedPages - 1)) {
            if (pdfNumPages > pdfNumRenderedPages) {
                renderPage(pdfDocument);
            }
        }
    }
</script>


<div class="container pb20">
    {#if currentFlowIndex === 0 && appState.alternativeFlowContext.title}
        <h3 class="area-heading">
            {appState.alternativeFlowContext.title}
        </h3>
    {/if}
    <label class="control-label large-label form-title">
        {appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].title}
    </label>
    <hr class="mobile-none">
    <!--Inserire separatore solo desktop -->
    <div bind:this={pdfPagesContainer} class="pdf-document-view-container" on:scroll={loadNewPageOnScroll}>
        <canvas
                bind:this={pdfFirstPage}
                class="pdf-document-view"
                style="border:none;  width: 100%"></canvas>
    </div>

    {#if appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].description}
        <div class="accept-form">
            <input
                    type="checkbox"
                    bind:checked={appState.alternativeFlowContext.nextStepFlowEnabled}
                    class="select-item checkbox"/>
            <p>
                {appState.alternativeFlowContext.documents[appState.alternativeFlowContext.documentsIdList[flowIndex]].description}
            </p>
        </div>
    {/if}
</div>
