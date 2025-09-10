import {Link, useLocation, useParams, useSearchParams} from "react-router-dom";
import {useEffect, useState} from "react";
import axios from "axios";
import {BASE_URL, SORT_BY} from "../utils/constant.js";
import Footer from "../components/Footer.jsx";
import {FaChevronRight, FaFilter, FaTimes} from "react-icons/fa";
import ProductCard from "../components/ProductCard.jsx";
import {IoMdClose} from "react-icons/io";
import {formatPrice} from "../utils/formatPrice.js";
import {LiaExchangeAltSolid} from "react-icons/lia";

function ProductListPage() {
    const location = useLocation();
    const {'*': categoryPath} = useParams();
    const [searchParams, setSearchParams] = useSearchParams();

    const sortParam = searchParams.get("sort") || SORT_BY.RELEVANT;
    const colorsParam = searchParams.get("color")?.split(",") || [];
    const brandsParam = searchParams.get("brand")?.split(",") || [];
    const sizesParam = searchParams.get("size")?.split(",") || [];
    const minPrice = parseInt(searchParams.get("minPrice") || 39000);
    const maxPrice = parseInt(searchParams.get("maxPrice") || 2999000);

    const [products, setProducts] = useState([]);
    const [productsLoading, setProductsLoading] = useState(true);
    const [showFilters, setShowFilters] = useState(false);

    const [page, setPage] = useState(0);
    const [size] = useState(15);
    const [totalPages, setTotalPages] = useState(1);
    const [totalElements, setTotalElements] = useState(0);
    const [loadingMore, setLoadingMore] = useState(false);

    const [showMoreColors, setShowMoreColors] = useState(false);
    const [showMoreSizes, setShowMoreSizes] = useState(false);
    const [showMoreBrands, setShowMoreBrands] = useState(false);

    // Mock data for filters (thay thế bằng API call thực tế)
    const brands = [{id: 1, name: "KIPSTA", count: 249}, {id: 2, name: "DECATHLON", count: 26}, {
        id: 3, name: "ADIDAS", count: 13
    }, {id: 4, name: "NABAIJI", count: 2}, {id: 5, name: "IMVISO", count: 1}];

    const colors = [{id: 1, name: "black", color: "black"}, {id: 2, name: "blue", color: "blue"}, {
        id: 3, name: "orange", color: "orange"
    }, {id: 4, name: "gray", color: "gray"}, {id: 5, name: "red", color: "red"}, {
        id: 6, name: "green", color: "green"
    }, {id: 7, name: "yellow", color: "yellow"}];

    const sizes = [{id: 1, name: "0", count: 1}, {id: 2, name: "1", count: 4}, {id: 3, name: "2", count: 3}, {
        id: 4, name: "3", count: 6
    }, {id: 5, name: "4", count: 12}];

    const [open, setOpen] = useState(false);
    const [selected, setSelected] = useState("Liên quan nhất");

    const options = ["Liên quan nhất", "Giảm giá nhiều nhất", "Giá tăng dần", "Giá giảm dần",];

    // load page đầu tiên
    useEffect(() => {
        const fetchData = async () => {
            if (products.length === 0) setProductsLoading(true);
            setPage(0);
            try {
                let url = "";
                if (location.pathname.startsWith("/c")) {
                    url = `${BASE_URL}/products/categories/${categoryPath}?page=${page}&size=${size}`;
                } else if (location.pathname.startsWith("/search")) {
                    const query = searchParams.get("query") || "";
                    url = `${BASE_URL}/products/search?query=${query}`;
                }

                if (url) {
                    if (brandsParam.length > 0) url += `&brand=${brandsParam.join(",")}`;
                    if (colorsParam.length > 0) url += `&color=${colorsParam.join(",")}`;
                    if (sortParam) url += `&sort=${sortParam}`;
                }
                console.log('url', url);
                const res = await axios.get(url);
                setProducts(res.data.data.content || []);
                setTotalPages(res.data.data.totalPages || 1);
                setTotalElements(res.data.data.totalElements || 0);
            } finally {
                setProductsLoading(false);
            }
        };

        fetchData();
    }, [location, categoryPath, searchParams, size]);

    const handleLoadMore = () => {
        if (page + 1 >= totalPages) return; // hết trang
        setLoadingMore(true);
        axios.get(`${BASE_URL}/products/categories/${categoryPath}?page=${page + 1}&size=${size}`)
            .then(res => {
                setProducts(prev => [...prev, ...(res.data.data.content || [])]);
                setPage(prev => prev + 1);
            })
            .finally(() => setLoadingMore(false));
    }

    const resetProducts = () => {
        setProducts([]);
        setTotalPages(1);
        setTotalElements(0);
    };
    // Filter handlers
    const handleBrandToggle = (brandName) => {
        resetProducts();
        let updatedBrands = [...brandsParam];

        if (updatedBrands.includes(brandName)) {
            updatedBrands = updatedBrands.filter(b => b !== brandName);
        } else {
            updatedBrands.push(brandName);
        }

        const newParams = new URLSearchParams(searchParams);
        if (updatedBrands.length > 0) {
            newParams.set("brand", updatedBrands.join(","));
        } else {
            newParams.delete("brand");
        }

        setSearchParams(newParams);
    };

    const handleColorToggle = (colorName) => {
        resetProducts();
        let updatedColors = [...colorsParam];

        if (updatedColors.includes(colorName)) {
            updatedColors = updatedColors.filter(c => c !== colorName);
        } else {
            updatedColors.push(colorName);
        }

        const newParams = new URLSearchParams(searchParams);
        if (updatedColors.length > 0) {
            newParams.set("color", updatedColors.join(","));
        } else {
            newParams.delete("color");
        }
        setPage(0);

        setSearchParams(newParams);
    };

    const handleSizeToggle = (sizeName) => {
        resetProducts();
        let updatedSizes = [...sizesParam];

        if (updatedSizes.includes(sizeName)) {
            updatedSizes = updatedSizes.filter(s => s !== sizeName);
        } else {
            updatedSizes.push(sizeName);
        }

        const newParams = new URLSearchParams(searchParams);
        if (updatedSizes.length > 0) {
            newParams.set("size", updatedSizes.join(","));
        } else {
            newParams.delete("size");
        }
        setPage(0);

        setSearchParams(newParams);
    };

    const handlePriceChange = (e, type) => {
        resetProducts();
        const value = parseInt(e.target.value);
        const newParams = new URLSearchParams(searchParams);
        if (type === "min") newParams.set("minPrice", value);
        if (type === "max") newParams.set("maxPrice", value);
        setSearchParams(newParams);
    };

    const updateParams = (callback) => {
        const newParams = new URLSearchParams(searchParams);
        callback(newParams);
        setSearchParams(newParams);
    };

    const clearAllFilters = () => {
        updateParams((params) => {
            ["brand", "color", "size", "minPrice", "maxPrice"].forEach(key => params.delete(key));
        });
    };

    const removeBrandFilter = (brandName) => {
        const updated = brandsParam.filter(b => b !== brandName);
        updateParams((params) => {
            updated.length > 0 ? params.set("brand", updated.join(",")) : params.delete("brand");
        });
    };

    const removeColorFilter = (colorName) => {
        const updated = colorsParam.filter(c => c !== colorName);
        updateParams((params) => {
            updated.length > 0 ? params.set("color", updated.join(",")) : params.delete("color");
        });
    };

    const removeSizeFilter = (sizeName) => {
        const updated = sizesParam.filter(s => s !== sizeName);
        updateParams((params) => {
            updated.length > 0 ? params.set("size", updated.join(",")) : params.delete("size");
        });
    };

    const removePriceFilter = () => {
        updateParams((params) => {
            params.delete("minPrice");
            params.delete("maxPrice");
        });
    };

    const hasActiveFilters = () => {
        return (brandsParam.length > 0 || colorsParam.length > 0 || sizesParam.length > 0 || minPrice !== 39000 || maxPrice !== 2999000);
    };


    const SortSection = ({className = ""}) => (<div className={`relative flex items-center gap-4 z-50 ${className}`}>
        <span className="text-sm">{totalElements} sản phẩm</span>

        {/* Trigger */}
        <div
            className="flex items-center cursor-pointer"
            onClick={() => setOpen(!open)}
        >
            <LiaExchangeAltSolid className="rotate-90"/>
            <span className="text-sm ml-1">Sắp xếp theo</span>
        </div>

        {/* Popup */}
        {open && (<div
            className="absolute top-full right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg z-10 p-2">
            {options.map((opt, i) => (<label
                key={i}
                className="flex items-center gap-2 px-2 py-1 text-sm cursor-pointer hover:bg-gray-100 rounded"
                onClick={() => {
                    setSelected(opt);
                    setOpen(false);
                    console.log("Chọn:", opt);
                }}
            >
                <input
                    type="radio"
                    name="sort"
                    value={opt}
                    checked={selected === opt}
                    onChange={() => setSelected(opt)}
                    className="hidden md:block"
                />
                <span className={selected === opt ? "font-bold" : ""}>{opt}</span>
            </label>))}
        </div>)}
    </div>);
    // Filter Component
    const FilterSection = () => (<div className=" bg-white p-4 rounded-lg shadow-sm border border-gray-200">
        <div className="hidden md:flex justify-between items-center mb-4">
            <h2 className="font-bold text-lg">BỘ LỌC</h2>
            {hasActiveFilters() && (<div className="text-primary cursor-pointer"
                                         onClick={clearAllFilters}>
                Xóa tất cả
            </div>)}
        </div>

        {/* Hiển thị các chip lọc đã chọn */}
        {hasActiveFilters() && (<div className="mb-4 flex flex-wrap gap-2">
            {/* Chip lọc thương hiệu */}
            {brandsParam.map(brand => (<div key={`brand-${brand}`}
                                            className="flex items-center border border-gray-200 rounded-full px-3 py-1 text-sm">
                <span className="mr-1">{brand}</span>
                <IoMdClose
                    className="ms-1 text-sm cursor-pointer"
                    onClick={() => removeBrandFilter(brand)}
                />
            </div>))}

            {/* Chip lọc màu sắc */}
            {colorsParam.map(color => (<div key={`color-${color}`}
                                            className="flex items-center border border-gray-200 rounded-full px-3 py-1 text-sm">
                <div
                    className="w-6 h-6 rounded-full"
                    style={{backgroundColor: color}}
                />
                <IoMdClose
                    className="ms-1 text-sm cursor-pointer"
                    onClick={() => removeColorFilter(color)}
                />
            </div>))}

            {/* Chip lọc kích cỡ */}
            {sizesParam.map(size => (<div key={`size-${size}`}
                                          className="flex items-center border border-gray-200 rounded-full px-3 py-1 text-sm">
                <span className="mr-1">{size}</span>
                <IoMdClose
                    className="ms-1 text-sm cursor-pointer"
                    onClick={() => removeSizeFilter(size)}
                />
            </div>))}

            {/* Chip lọc giá */}
            {(minPrice !== 39000 || maxPrice !== 2999000) && (
                <div className="flex items-center border border-gray-200 rounded-full px-3 py-1 text-sm">
                            <span className="mr-1">
                                {formatPrice(minPrice)} -
                                {formatPrice(maxPrice)}
                            </span>
                    <IoMdClose
                        className="ms-1 text-sm cursor-pointer"
                        onClick={removePriceFilter}
                    />
                </div>)}
        </div>)}

        {/* Brand Filter */}
        <div className="mb-6">
            <h3 className="text-start font-semibold mb-3">CHỌN THƯƠNG HIỆU</h3>
            <div className="space-y-2">
                {brands.slice(0, showMoreBrands ? brands.length : 4).map(brand => (
                    <div key={brand.id} className="flex items-center">
                        <input
                            type="checkbox"
                            id={`brand-${brand.id}`}
                            checked={brandsParam.includes(brand.name)}
                            onChange={() => handleBrandToggle(brand.name)}
                            className="w-4 h-4 text-blue-600 rounded"
                        />
                        <label htmlFor={`brand-${brand.id}`} className="ml-2 text-sm">
                            {brand.name} <span className="text-gray-500">({brand.count})</span>
                        </label>
                    </div>))}
            </div>

            {brands.length > 4 && (<div
                onClick={() => setShowMoreBrands(!showMoreBrands)}
                className="cursor-pointer mt-2 border border-gray-200 rounded-full px-3 py-1 text-sm text-center"
            >
                {showMoreBrands ? 'ẨN BỚT' : 'XEM THÊM'}
            </div>)}
        </div>


        {/* Color Filter */}
        <div className="mb-6">
            <h3 className="text-start font-semibold mb-3">CHỌN MÀU</h3>
            <div className="grid grid-cols-4 gap-2">
                {colors.slice(0, showMoreColors ? colors.length : 8).map((c) => (
                    <div key={c.id} className="flex flex-col items-center justify-center cursor-pointer"
                         onClick={() => handleColorToggle(c.name)}
                    >
                        <div
                            className={`w-6 h-6 rounded-full  ${colorsParam.includes(c.name) ? 'ring-2 ring-blue-500' : ''}`}
                            style={{backgroundColor: c.color}}
                        />
                    </div>))}
            </div>

            {colors.length > 8 && (<div
                onClick={() => setShowMoreColors(!showMoreColors)}
                className="cursor-pointer mt-2 border border-gray-200 rounded-full px-3 py-1 text-sm text-center"
            >
                {showMoreColors ? 'ẨN BỚT' : 'XEM THÊM'}
            </div>)}
        </div>


        {/* Price Filter */}
        <div className="mb-6">
            <h3 className="text-start font-semibold mb-3">CHỌN GIÁ</h3>
            <div className="flex flex-col space-y-4">
                <div className="flex space-x-2">
                    <input
                        type="number"
                        value={minPrice}
                        onChange={(e) => handlePriceChange(e, 'min')}
                        className="w-full p-2 border border-gray-300 rounded text-sm"
                    />
                    <span className="self-center">-</span>
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => handlePriceChange(e, 'max')}
                        className="w-full p-2 border border-gray-300 rounded text-sm"
                    />
                </div>
                {/* Range slider would go here */}
            </div>
        </div>

        {/* Size Filter */}
        <div className="mb-6">
            <h3 className="text-start font-semibold mb-3">CHỌN KÍCH CỠ</h3>
            <div className="space-y-2">
                {sizes.slice(0, showMoreSizes ? sizes.length : 4).map(size => (
                    <div key={size.id} className="flex items-center">
                        <input
                            type="checkbox"
                            id={`size-${size.id}`}
                            checked={sizesParam.includes(size.name)}
                            onChange={() => handleSizeToggle(size.name)}
                            className="w-4 h-4 text-blue-600 rounded"
                        />
                        <label htmlFor={`size-${size.id}`} className="ml-2 text-sm">
                            {size.name}
                        </label>
                    </div>))}
            </div>
            {sizes.length > 4 && (<div
                onClick={() => setShowMoreSizes(!showMoreSizes)}
                className="cursor-pointer mt-2 border border-gray-200 rounded-full px-3 py-1 text-sm text-center"
            >
                {showMoreSizes ? 'ẨN BỚT' : 'XEM THÊM'}
            </div>)}
        </div>
    </div>);

    return (<div className="w-full min-h-screen flex flex-col">
        <main className="flex-1 p-4 md:p-6 md:mx-10 bg-white">
            {/* Mobile Filter Button */}
            <div className="md:hidden flex justify-between items-center mb-4 p-2 bg-gray-100 rounded-lg">
                <button
                    onClick={() => setShowFilters(true)}
                    className="flex items-center gap-2 font-medium"
                >
                    <FaFilter/> Bộ lọc
                </button>
                <SortSection/>
            </div>

            <div className="flex flex-col md:flex-row gap-6">
                {/* Filter Sidebar - Desktop */}
                <div className="hidden md:block w-full md:w-64 flex-shrink-0">
                    <FilterSection/>
                </div>

                {/* Mobile Filter Overlay */}
                {showFilters && (<div className="fixed inset-0 z-50 md:hidden bg-transparent">
                    <div className="absolute top-0 left-0 h-full w-4/5 max-w-sm bg-white overflow-y-auto">
                        <div className="sticky top-0 bg-white p-4 flex justify-between items-center">
                            <h2 className="font-bold text-lg">BỘ LỌC</h2>
                            <IoMdClose
                                className="cursor-pointer size-5"
                                onClick={() => setShowFilters(false)}
                            />
                        </div>
                        <div className="p-4">
                            <FilterSection/>
                        </div>
                        <div className="sticky bottom-0 bg-white p-4 flex gap-x-3 justify-between items-center">
                            {hasActiveFilters() && (<button
                                className="flex-1 bg-gray-200 py-2 rounded"
                                onClick={clearAllFilters}
                            >
                                Xóa tất cả
                            </button>)}
                            <button className="flex-1 bg-primary py-2 rounded text-white">
                                Áp dụng
                            </button>
                        </div>
                    </div>
                </div>)}
                {/* Product List */}
                <div className="flex-1">
                    <SortSection className="hidden md:flex justify-end gap-4 mb-5"/>
                    {productsLoading && (<p>Đang tải sản phẩm...</p>)}

                    <div
                        className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-3 sm:gap-4">
                        {products.map((product) => (<ProductCard key={product.id} product={product}/>))}
                    </div>

                    {products.length > 0 && (<div className="flex items-center justify-between mt-14">
                        {/* Progress + Load More (căn giữa) */}
                        <div className="flex flex-col items-center mx-auto">
                            {/* Progress info */}
                            <p className="text-sm text-gray-600 mb-2">
                                Bạn đã xem {products.length} trên {totalElements} sản phẩm
                            </p>

                            {/* Progress bar */}
                            <div className="w-64 h-1 bg-gray-200 rounded-full mb-2">
                                <div
                                    className="h-1 bg-blue-600 rounded-full"
                                    style={{width: `${(products.length / totalElements) * 100}%`}}
                                />
                            </div>

                            {/* Load More button */}
                            {page + 1 < totalPages && (<button
                                onClick={handleLoadMore}
                                disabled={loadingMore}
                                className="px-6 py-2 border border-gray-300 rounded-full hover:bg-gray-100 disabled:opacity-50"
                            >
                                {loadingMore ? "Đang tải..." : "XEM THÊM"}
                            </button>)}
                        </div>

                        <div
                            className="hidden md:flex flex-col items-center mt-6 relative md:absolute md:me-10 md:right-0 md:-translate-y-1/2">
                            <div
                                className="flex items-center justify-center w-10 h-10 bg-white rounded-full cursor-pointer transition-all border border-gray-300 mb-2"
                                onClick={() => window.scrollTo({top: 0, behavior: 'smooth'})}
                            >
                                <FaChevronRight className="rotate-[-90deg]" size={16}/>
                            </div>
                            <div className="text-xs text-gray-600">
                                Quay lại đầu trang
                            </div>
                        </div>
                    </div>)}

                    {!productsLoading && products.length === 0 && (
                        <div className="flex flex-col items-center justify-center py-20 text-gray-500">
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                className="h-16 w-16 mb-4 text-gray-400"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M9 17v-6h6v6m2 4H7a2 2 0 01-2-2V7a2 2 0 012-2h3l2-2h4l2 2h3a2 2 0 012 2v12a2 2 0 01-2 2z"
                                />
                            </svg>

                            <p className="text-lg font-medium">Không tìm thấy sản phẩm nào</p>

                            <p className="text-sm text-gray-400 mt-1">
                                Hãy thử chọn danh mục khác hoặc sử dụng ô tìm kiếm.
                            </p>
                        </div>)}
                </div>
            </div>
        </main>
    </div>);
}

export default ProductListPage;