import {Link} from "react-router-dom";
import {formatPrice} from "../utils/formatPrice.js";
import {TbShoppingBagPlus} from "react-icons/tb";
import {useDispatch} from "react-redux";
import {openAddToCartSidebar} from "../features/product/productSlice.js";

export default function ProductCard({product, isSlide = false}) {
    const dispatch = useDispatch();

    const image = product.mainImage || (product.firstVariant?.images?.length > 0 ? product.firstVariant?.images[0] : "https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg");
    const price = product.firstVariant?.price || 0;
    const oldPrice = product.firstVariant?.oldPrice || price * 1.2;
    const discount = oldPrice && price ? Math.round(((oldPrice - price) / oldPrice) * 100) : 0;
    if (isSlide) {
        return (<Link key={product.id} to={`/p/${product.slug}`} className="group">
            <div
                className="relative bg-white rounded-xl sm:rounded-2xl shadow-sm sm:shadow-md hover:shadow-lg transition duration-300 h-full flex flex-col w-50">
                {/* Sale Badge */}
                {discount > 0 && (<span
                    className="absolute bg-yellow-400 text-[10px] font-semibold px-1.5 py-0.5 sm:px-2 sm:py-1 sm:text-xs z-10">
                                  Sale
                                </span>)}

                {/* Ảnh sản phẩm */}
                <div className="relative pt-[100%] overflow-hidden">
                    <img
                        src={image}
                        alt={product.title}
                        className="absolute top-0 left-0 w-full h-full object-contain p-2 sm:p-4 group-hover:scale-105 transition-transform duration-300"
                    />
                </div>

                <div className="p-2 sm:p-4 flex-grow flex flex-col">
                    {/* Giá */}
                    <div className="flex flex-wrap items-center gap-1 mb-1">
                        <p className="text-xs font-bold text-gray-800 sm:text-sm">
                            {formatPrice(price)}
                        </p>
                        {discount > 0 && (<p className="text-[10px] line-through text-gray-400 sm:text-xs">
                            {formatPrice(oldPrice)}
                        </p>)}
                        {discount > 0 && (<span
                            className="text-[10px] bg-red-100 text-red-600 px-1 py-0.5 rounded sm:text-xs">
                                    -{discount}%
                    </span>)}
                    </div>

                    {/* Tên sản phẩm */}
                    <h3 className="text-xs font-medium text-gray-700 mb-1 line-clamp-2 min-h-[2.5rem] sm:text-sm sm:min-h-[3rem]">
                        {product.title}
                    </h3>

                    {/* Thương hiệu */}
                    <p className="text-[10px] text-gray-400 mb-2 line-clamp-1 sm:text-xs">
                        {product.brand || "PERFLY"}
                    </p>

                    {/* Rating + Giỏ hàng */}
                    <div
                        className="flex items-center justify-between mt-auto text-[10px] text-gray-500 sm:text-sm">
                  <span className="flex items-center">
                    ⭐ {product.rating?.avg || 4.9} ({product.rating?.count || 1234})
                  </span>
                        <button
                            className="p-1 rounded-full hover:bg-gray-100 transition sm:p-2"
                            onClick={(e) => {
                                e.preventDefault();
                                dispatch(openAddToCartSidebar(product));
                            }}
                        >
                            <TbShoppingBagPlus size={14} className="sm:w-[18px] sm:h-[18px]"/>
                        </button>
                    </div>
                </div>
            </div>
        </Link>);
    } else {
        return (<Link key={product.id} to={`/p/${product.slug}`} className="group">
            <div
                className="relative bg-white rounded-xl sm:rounded-2xl shadow-sm sm:shadow-md hover:shadow-lg transition duration-300 h-full flex flex-col">
                {/* Sale Badge */}
                {discount > 0 && (<span
                    className="absolute bg-yellow-400 text-[10px] font-semibold px-1.5 py-0.5 sm:px-2 sm:py-1 sm:text-xs z-10">
                                  Sale
                                </span>)}

                {/* Ảnh sản phẩm */}
                <div className="relative pt-[100%] overflow-hidden">
                    <img
                        src={image}
                        alt={product.title}
                        className="absolute top-0 left-0 w-full h-full object-contain p-2 sm:p-4 group-hover:scale-105 transition-transform duration-300"
                    />
                </div>

                <div className="p-2 sm:p-4 flex-grow flex flex-col">
                    {/* Giá */}
                    <div className="flex flex-wrap items-center gap-1 mb-1">
                        <p className="text-xs font-bold text-gray-800 sm:text-sm">
                            {formatPrice(price)}
                        </p>
                        {discount > 0 && (<p className="text-[10px] line-through text-gray-400 sm:text-xs">
                            {formatPrice(oldPrice)}
                        </p>)}
                        {discount > 0 && (<span
                            className="text-[10px] bg-red-100 text-red-600 px-1 py-0.5 rounded sm:text-xs">
                                    -{discount}%
                    </span>)}
                    </div>

                    {/* Tên sản phẩm */}
                    <h3 className="text-xs font-medium text-gray-700 mb-1 line-clamp-2 min-h-[2.5rem] sm:text-sm sm:min-h-[3rem]">
                        {product.title}
                    </h3>

                    {/* Thương hiệu */}
                    <p className="text-[10px] text-gray-400 mb-2 line-clamp-1 sm:text-xs">
                        {product.brand || "PERFLY"}
                    </p>

                    {/* Rating + Giỏ hàng */}
                    <div
                        className="flex items-center justify-between mt-auto text-[10px] text-gray-500 sm:text-sm">
                  <span className="flex items-center">
                    ⭐ {product.rating?.avg || 4.9} ({product.rating?.count || 1234})
                  </span>
                        <button
                            className="p-1 rounded-full hover:bg-gray-100 transition sm:p-2"
                            onClick={(e) => {
                                e.preventDefault();
                                dispatch(openAddToCartSidebar(product));
                            }}
                        >
                            <TbShoppingBagPlus size={14} className="sm:w-[18px] sm:h-[18px]"/>
                        </button>
                    </div>
                </div>
            </div>
        </Link>);
    }
}