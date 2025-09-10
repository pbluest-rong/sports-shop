// HomeProductList.jsx
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import { useRef } from "react";
import ProductCard from "./ProductCard.jsx";

function HomeProductList({ products }) {
    const listRef = useRef(null);

    const scroll = (direction) => {
        if (listRef.current) {
            const width = listRef.current.clientWidth;
            listRef.current.scrollBy({
                left: direction === "left" ? -width : width,
                behavior: "smooth",
            });
        }
    };

    if (!products || products.length === 0) return <p>Không có sản phẩm</p>;

    return (
        <div className="relative w-full">
            {/* Nút trái */}
            <div
                className="absolute -left-5 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer shadow-lg"
                onClick={() => scroll("left")}
            >
                <FaChevronLeft size={16} />
            </div>
            <div
                ref={listRef}
                className="flex space-x-4 overflow-x-hidden scroll-smooth py-2"
            >
                {products.map((product) => (
                    <ProductCard key={product.id} product={product} isSlide={true} />
                ))}
            </div>
            {/* Nút phải */}
            <div
                className="absolute -right-5 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer shadow-lg"
                onClick={() => scroll("right")}
            >
                <FaChevronRight size={16} />
            </div>
        </div>
    );
}

export default HomeProductList;
