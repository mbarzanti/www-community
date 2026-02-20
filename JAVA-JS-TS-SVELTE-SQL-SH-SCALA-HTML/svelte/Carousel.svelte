<script>
    import {onMount} from 'svelte';

    onMount(() => {
        jQuery('.caroselloprodotto .carouselcontainer').slick({
            dots: false,
            infinite: false,
            autoplay: false,
            autoplaySpeed: 3000,
            slidesToShow: 1.2,
            slidesToScroll: 1,
            responsive: [
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 1,
                        slidesToScroll: 1,
                        infinite: false,
                        dots: false
                    }
                }
            ]
        });
    })
</script>
<div class="container">
    <div class="caroselloprodotto">
        <div class="carouselcontainer">

            <!-- INIZIO SLIDE -->
            <div class="slide">
                <div class="background" style="background-image:url('../images/bitmap.jpg')"></div>
                <div class="overlay"
                     style="background: linear-gradient(0deg, #000000 0%, rgba(255,255,255,0) 60%)"></div>
                <div class="zoombox">
                    <img class="zoomImage"
                         src="../images/imagezoom.svg"
                         data-zoom-src="../images/Bitmap.jpg"
                         alt="Zoom immagine"/>
                </div>
                <div class="slidecontents">
                    <div class="contentbox">
                        <div class="abstractbox">
                            <p>Perché è importante considerare anche altri strumenti di investimento oltre che i
                                tradizionali Titoli di stato e le polizze Ramo I?</p>
                        </div>
                        <div class="ctabox">
                            <a href={undefined} class="cardcta clickable">Scarica</a>
                        </div>
                    </div>
                </div>
            </div>
            <!-- FINE SLIDE -->

            <div class="slide">
                <div class="background" style="background-image:url('../images/Bitmap.jpg')"></div>
                <div class="overlay"
                     style="background: linear-gradient(0deg, #000000 0%, rgba(255,255,255,0) 60%)"></div>
                <div class="zoombox">
                    <img class="zoomImage"
                         src="../images/imagezoom.svg"
                         data-zoom-src="../images/Bitmap.jpg"
                         alt="Zoom immagine"/>
                </div>
                <div class="slidecontents">
                    <div class="contentbox">
                        <div class="abstractbox">
                            <p> Ut enim ad minima veniam, quis nostrum exercitationem</p>
                        </div>
                        <div class="ctabox">
                            <a href={undefined} class="cardcta clickable">Scarica</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="slide">
                <div class="background" style="background-image:url('../images/bitmap.jpg')"></div>
                <div class="overlay"
                     style="background: linear-gradient(0deg, #000000 0%, rgba(255,255,255,0) 60%)"></div>
                <div class="zoombox">
                    <img class="zoomImage"
                         src="../images/imagezoom.svg"
                         data-zoom-src="../images/Bitmap.jpg"
                         alt="Zoom immagine"/>
                </div>
                <div class="slidecontents">
                    <div class="contentbox">
                        <div class="abstractbox">
                            <p>Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae
                                consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur</p>
                        </div>
                        <div class="ctabox">
                            <a href={undefined} class="cardcta clickable">Scarica</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>


</div>
