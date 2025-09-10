import { useRef } from "react";
import { Link } from "react-router-dom";
import {FaChevronLeft, FaChevronRight} from "react-icons/fa";

export default function HomeCategoryList({ categories, loading }) {
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

    if (loading) return <p>Đang tải danh mục...</p>;

    return (
        <div className="relative w-full">
            {/* Nút trái */}
            <div
                className="absolute -left-5 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer transition-all  shadow-lg shadow-gray-500"
                onClick={() => scroll("left")}
            >
                <FaChevronLeft size={16} />
            </div>
            {/* List container */}
            <div
                ref={listRef}
                className="flex space-x-4 overflow-x-hidden scroll-smooth py-2"
            >
                {categories.map((category) => (
                    <Link
                        key={category.id}
                        to={`c${category.path}`}
                        className="flex-shrink-0 w-40 flex flex-col items-center"
                    >
                        {category.imageUrl ? (
                            <img
                                src={category.imageUrl}
                                alt={category.name}
                                className="w-40 h-40 object-cover rounded mb-2"
                            />
                        ) : (
                            <div className="w-40 h-40 bg-gray-200 rounded mb-2 flex items-center justify-center">
                                <span className="text-gray-500 text-sm">No Img</span>
                            </div>
                        )}
                        <span className="text-sm font-medium">{category.name}</span>
                    </Link>
                ))}
            </div>

            {/* Nút phải */}
            <div
                className="absolute -right-5 top-1/2 -translate-y-1/2 z-30 flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer transition-all shadow-lg shadow-gray-500"
                onClick={() => scroll("right")}
            >
                <FaChevronRight size={16} />
            </div>
        </div>
    );
}
