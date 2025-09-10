import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import { useState } from "react";

const slides = [
    {
        img: "https://contents.mediadecathlon.com/s1270272/k$0f297e34340edd057514730dba0da205/MAB-Active-wear-mobi-VI.webp",
        link: "https://www.decathlon.vn",
        newTap: false,
    },
    {
        img:"https://static.standard.co.uk/2025/07/13/22/27/SEI258947883.jpeg?trim=0,0,250,0&quality=75&auto=webp&width=1000",
        link: "https://www.decathlon.vn",
        newTap: false,
    },
    {
        img: "https://contents.mediadecathlon.com/s1299301/k$d0223377e8be49a66a9b64b7099cd9c8/1500x840/DKVN%20-%20B2P-02.webp",
        link: "https://www.decathlon.vn",
        newTap: true,
    },
];

export default function Banner() {
    const [currentSlide, setCurrentSlide] = useState(0);

    const settings = {
        dots: true,
        infinite: true,
        speed: 800,
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 4000,
        arrows: true,
        pauseOnHover: true,
        nextArrow: <SampleNextArrow/>,
        prevArrow: <SamplePrevArrow />,
        beforeChange: (current, next) => setCurrentSlide(next),
        appendDots: dots => (
            <div
                style={{
                    bottom: "20px",
                }}
            >
                <ul style={{ margin: "0px" }}> {dots} </ul>
            </div>
        ),
        customPaging: (i) => (
            <div
                className={`rounded-full w-3 h-3 transition-transform duration-300 transform ${
                    i === currentSlide ? "scale-150 bg-white" : "bg-white/50 border border-gray-300"
                }`}
            ></div>
        ),

    };

    const handleBannerClick = (link, newTap) => {
        if(newTap) {
            window.open(link, '_blank');
        }else{
            window.open(link, '_self');
        }
    };

    return (
        <div className="w-full overflow-hidden relative">
            <Slider {...settings}>
                {slides.map((slide, i) => (
                    <div
                        key={i}
                        className="relative w-full cursor-pointer"
                        onMouseDown={(e) => (slide.startX = e.clientX)}
                        onMouseUp={(e) => {
                            const diff = Math.abs(e.clientX - slide.startX);
                            if (diff < 10) handleBannerClick(slide.link, slide.newTap);
                        }}
                        onTouchStart={(e) => (slide.startX = e.touches[0].clientX)}
                        onTouchEnd={(e) => {
                            const diff = Math.abs(e.changedTouches[0].clientX - slide.startX);
                            if (diff < 10) handleBannerClick(slide.link, slide.newTap);
                        }}
                    >
                        <div className="w-full aspect-[3/1] overflow-hidden relative">
                            <img
                                src={slide.img}
                                className="w-full h-full object-cover transition-transform duration-700 hover:scale-105"
                                alt={`Slide ${i + 1}`}
                            />
                        </div>
                    </div>
                ))}
            </Slider>
        </div>
    );
}

function SampleNextArrow(props) {
    const { onClick } = props;
    return (
        <div
            className="absolute right-4 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer hover:bg-black/50 transition-all"
            onClick={onClick}
        >
            <FaChevronRight size={16} />
        </div>
    );
}

function SamplePrevArrow(props) {
    const { onClick } = props;
    return (
        <div
            className="absolute left-4 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer hover:bg-black/50 transition-all"
            onClick={onClick}
        >
            <FaChevronLeft size={16} />
        </div>
    );
}