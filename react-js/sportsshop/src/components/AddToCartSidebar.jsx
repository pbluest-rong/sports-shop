import {useDispatch, useSelector} from "react-redux";
import {useEffect, useState, useRef, useCallback} from "react";
import {MdArrowBackIos, MdArrowDropDown} from "react-icons/md";
import {formatPrice} from "../utils/formatPrice.js";
import {closeAddToCartSidebar} from "../features/product/productSlice.js";
import axios from "axios";

export default function AddToCartSidebar({slug, brand, title, firstVariant}) {
    const {openAddToCartSidebar} = useSelector((state) => state.product);
    const dispatch = useDispatch();

    const [selectedVariant, setSelectedVariant] = useState(firstVariant || null);
    const [selectedAttributes, setSelectedAttributes] = useState({});
    const [quantity, setQuantity] = useState(1);
    const [attributesData, setAttributesData] = useState({});
    const [attributeOrder, setAttributeOrder] = useState([]);
    const [lastAttributeOptions, setLastAttributeOptions] = useState([]);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);
    const [isLoading, setIsLoading] = useState(false);

    // Thêm state để lưu cache
    const [variantsCache, setVariantsCache] = useState(null);
    const [lastAttributesCache, setLastAttributesCache] = useState({});

    // Tạo key cho cache dựa trên attributes
    const getCacheKey = (attributes) => {
        return Object.keys(attributes)
            .sort()
            .map(key => `${key}=${attributes[key]}`)
            .join('&');
    };

    // Fetch variants từ API với cache
    useEffect(() => {
        const fetchVariants = async () => {
            // Kiểm tra cache trước
            if (variantsCache) {
                const data = variantsCache;
                const attributeNames = Object.keys(data.attributes);
                setAttributeOrder(attributeNames);
                setAttributesData(data.attributes);
                setSelectedAttributes(data.defaultVariant?.attributes || {});

                const lastAttributeName = attributeNames[attributeNames.length - 1];
                const lastAttributeData = data.attributes[lastAttributeName] || [];
                setLastAttributeOptions(lastAttributeData);
                setSelectedVariant(data.defaultVariant);
                return;
            }

            try {
                setIsLoading(true);
                const res = await axios.get(`http://localhost:8080/products/${slug}/variants`);
                const data = res.data.data;

                // Lưu vào cache
                setVariantsCache(data);

                const attributeNames = Object.keys(data.attributes);
                setAttributeOrder(attributeNames);
                setAttributesData(data.attributes);
                setSelectedAttributes(data.defaultVariant?.attributes || {});

                const lastAttributeName = attributeNames[attributeNames.length - 1];
                const lastAttributeData = data.attributes[lastAttributeName] || [];
                setLastAttributeOptions(lastAttributeData);
                setSelectedVariant(data.defaultVariant);

            } catch (error) {
                console.error("Lỗi fetch variants:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchVariants();

        // Đóng dropdown khi click ra ngoài
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [slug, variantsCache]);

    // Hàm fetch last attribute options với cache
    const fetchLastAttributeOptions = useCallback(async (currentAttributes) => {
        if (attributeOrder.length <= 1) return;

        const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        const queryParams = {};

        // Thêm các attribute đã chọn (trừ attribute cuối) vào query params
        Object.entries(currentAttributes).forEach(([key, value]) => {
            if (key !== lastAttributeName && value) {
                queryParams[key] = value;
            }
        });

        // Chỉ fetch nếu có ít nhất 1 attribute đã được chọn
        if (Object.keys(queryParams).length === 0) return;

        const cacheKey = getCacheKey(queryParams);

        // Kiểm tra cache
        if (lastAttributesCache[cacheKey]) {
            const cachedData = lastAttributesCache[cacheKey];
            setLastAttributeOptions(cachedData.lastAttributeOptions || []);

            if (cachedData.variant) {
                setSelectedVariant(cachedData.variant);
            }
            updateLastAttributeAvailability(currentAttributes, cachedData.lastAttributeOptions, lastAttributeName);
            return;
        }

        try {
            setIsLoading(true);
            const queryString = new URLSearchParams(queryParams).toString();
            const res = await axios.get(
                `http://localhost:8080/products/${slug}/variant?${queryString}`
            );
            const data = res.data;

            // Lưu vào cache
            setLastAttributesCache(prev => ({
                ...prev,
                [cacheKey]: data
            }));

            setLastAttributeOptions(data.lastAttributeOptions || []);

            if (data.variant) {
                setSelectedVariant(data.variant);
            }
            updateLastAttributeAvailability(currentAttributes, data.lastAttributeOptions, lastAttributeName);
        } catch (error) {
            console.error("Lỗi fetch last attribute options:", error);
        } finally {
            setIsLoading(false);
        }
    }, [attributeOrder, slug, lastAttributesCache]);

    // Hàm helper để cập nhật attribute cuối nếu cần
    const updateLastAttributeAvailability = (currentAttributes, options, lastAttributeName) => {
        const currentLastAttribute = currentAttributes[lastAttributeName];
        const isAvailable = options?.some(opt =>
            opt.value === currentLastAttribute && opt.stock > 0
        );

        if (!isAvailable && options?.length > 0) {
            const availableOption = options.find(opt => opt.stock > 0);
            if (availableOption) {
                setSelectedAttributes(prev => ({
                    ...prev,
                    [lastAttributeName]: availableOption.value
                }));
            } else {
                setSelectedAttributes(prev => ({
                    ...prev,
                    [lastAttributeName]: ''
                }));
            }
        }
    };

    // Fetch variant với cache
    const fetchVariantForSelectedAttributes = useCallback(async (attrs) => {
        if (Object.keys(attrs).length === 0) return;

        const cacheKey = getCacheKey(attrs);

        // Kiểm tra cache
        if (lastAttributesCache[cacheKey]?.variant) {
            setSelectedVariant(lastAttributesCache[cacheKey].variant);
            return;
        }

        try {
            const queryParams = new URLSearchParams();
            Object.entries(attrs).forEach(([key, value]) => {
                if (value) queryParams.append(key, value);
            });

            const res = await axios.get(
                `http://localhost:8080/products/${slug}/variant?${queryParams.toString()}`
            );

            if (res.data.variant) {
                setSelectedVariant(res.data.variant);

                // Cập nhật cache nếu có dữ liệu mới
                if (res.data.lastAttributeOptions) {
                    setLastAttributesCache(prev => ({
                        ...prev,
                        [cacheKey]: {
                            variant: res.data.variant,
                            lastAttributeOptions: res.data.lastAttributeOptions
                        }
                    }));
                }
            }
        } catch (error) {
            console.error("Lỗi fetch variant:", error);
        }
    }, [slug, lastAttributesCache]);

    // Xử lý chọn attribute
    const handleAttributeChange = async (attributeName, value) => {
        const attributeIndex = attributeOrder.indexOf(attributeName);
        const newAttributes = { ...selectedAttributes };

        // Reset các attribute phía sau
        for (let i = attributeIndex + 1; i < attributeOrder.length; i++) {
            newAttributes[attributeOrder[i]] = '';
        }

        newAttributes[attributeName] = value;

        setSelectedAttributes(newAttributes);
        setIsDropdownOpen(false);
        setQuantity(1);

        if (attributeIndex < attributeOrder.length - 1) {
            await fetchLastAttributeOptions(newAttributes);
        } else {
            await fetchVariantForSelectedAttributes(newAttributes);
        }
    };

    // ... phần còn lại của component giữ nguyên
    const handleLastAttributeChange = (option) => {
        const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        handleAttributeChange(lastAttributeName, option.value);
    };

    const handleQuantityChange = (change) => {
        const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        const selectedLastAttribute = selectedAttributes[lastAttributeName];
        const stock = lastAttributeOptions.find(opt => opt.value === selectedLastAttribute)?.stock || 0;

        const newQuantity = quantity + change;
        if (newQuantity >= 1 && newQuantity <= stock) {
            setQuantity(newQuantity);
        }
    };


    const toggleDropdown = () => {
        // Chỉ cho phép mở dropdown nếu các attribute trước đó đã được chọn
        // const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        const allPreviousSelected = attributeOrder
            .slice(0, -1)
            .every(attr => selectedAttributes[attr]);

        if (allPreviousSelected) {
            setIsDropdownOpen(!isDropdownOpen);
        }
    };

    // Lấy stock của option đã chọn của attribute cuối
    const getSelectedStock = () => {
        const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        const selectedLastAttribute = selectedAttributes[lastAttributeName];
        return lastAttributeOptions.find(opt => opt.value === selectedLastAttribute)?.stock || 0;
    };

    // Kiểm tra xem attribute có thể được chọn hay không (các attribute phía trước đã được chọn)
    const isAttributeSelectable = (attributeName) => {
        const attributeIndex = attributeOrder.indexOf(attributeName);
        return attributeOrder
            .slice(0, attributeIndex)
            .every(attr => selectedAttributes[attr]);
    };

    useEffect(() => {
        document.body.style.overflow = openAddToCartSidebar ? "hidden" : "";
        return () => {
            document.body.style.overflow = "";
        };
    }, [openAddToCartSidebar]);

    if (isLoading) {
        return (
            <div className="fixed top-0 right-0 h-full bg-white shadow-xl z-50 w-full md:w-[400px]">
                <div className="flex items-center justify-center h-full">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
                </div>
            </div>
        );
    }

    return (
        <div className={`
            fixed top-0 right-0 h-full bg-white shadow-xl z-50
            w-full md:w-[400px]
            transition-transform duration-300
        `}>
            <div className="flex flex-col h-full">
                {/* Header */}
                <div className="flex items-center p-4 text-primary">
                    <div className="cursor-pointer px-2" onClick={() => dispatch(closeAddToCartSidebar())}>
                        <MdArrowBackIos/>
                    </div>
                    <h2 className="text-lg font-bold">Thay đổi lựa chọn</h2>
                </div>

                {/* Content */}
                <div className="flex-1 overflow-y-auto p-4">
                    <div className="flex items-stretch gap-4">
                        <div className="w-20 rounded cursor-pointer overflow-hidden">
                            {selectedVariant?.images?.[0] && (
                                <img
                                    src={selectedVariant.images[0]}
                                    alt={title}
                                    className="w-full h-full object-cover"
                                    onError={(e) => {
                                        e.target.src = 'https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg';
                                    }}
                                />
                            )}
                        </div>
                        <div className="flex flex-col text-start">
                            <div className="text-xl font-bold mb-1">
                                {formatPrice(selectedVariant?.price || firstVariant?.price)}
                            </div>
                            <div className="font-bold text-gray-700 mb-1">{brand}</div>
                            <div className="text-sm text-gray-600">{title}</div>
                        </div>
                    </div>

                    {/* Render các attribute không phải là attribute cuối */}
                    {attributeOrder.slice(0, -1).map(attributeName => (
                        <div key={attributeName} className="mb-4">
                            <p className="text-start font-medium mb-2 capitalize">
                                Phân loại
                            </p>
                            <div className="flex gap-2 flex-wrap">
                                {attributesData[attributeName]?.map(option => {
                                    const isSelectable = isAttributeSelectable(attributeName);
                                    const isSelected = selectedAttributes[attributeName] === option.value;

                                    return (
                                        <div
                                            key={option.value}
                                            className={`p-2 border rounded cursor-pointer ${
                                                isSelected
                                                    ? 'border-2 border-primary'
                                                    : 'border-gray-300'
                                            } ${!isSelectable ? 'opacity-50 cursor-not-allowed' : ''}`}
                                            onClick={() => isSelectable && handleAttributeChange(attributeName, option.value)}
                                            title={!isSelectable ? 'Vui lòng chọn các phân loại trước' : ''}
                                        >
                                            {option.image ? (
                                                <img
                                                    src={option.image}
                                                    alt={option.value}
                                                    className="w-12 h-12 object-cover"
                                                    onError={(e) => {
                                                        e.target.src = 'https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg';
                                                    }}
                                                />
                                            ) : (
                                                <span>{option.value}</span>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    ))}

                    {/* Attribute cuối cùng (có stock) */}
                    {attributeOrder.length > 0 && (
                        <div className="mb-4 relative" ref={dropdownRef}>
                            <p className="text-start font-medium mb-2 capitalize">
                                {attributeOrder[attributeOrder.length - 1]}
                            </p>
                            <div
                                className="w-full p-3 border rounded flex justify-between items-center cursor-pointer"
                                onClick={toggleDropdown}
                            >
                                <span>
                                    {selectedAttributes[attributeOrder[attributeOrder.length - 1]]
                                        ? selectedAttributes[attributeOrder[attributeOrder.length - 1]]
                                        : 'Chọn phân loại'
                                    }
                                </span>
                                <MdArrowDropDown
                                    className={`transform transition-transform ${isDropdownOpen ? 'rotate-180' : ''}`}
                                />
                            </div>

                            {isDropdownOpen && (
                                <div className="absolute top-full left-0 right-0 bg-white border rounded shadow-lg z-10 mt-1 max-h-60 overflow-y-auto">
                                    {lastAttributeOptions.map((option, idx) => (
                                        <div
                                            key={idx}
                                            className={`p-3 cursor-pointer flex justify-between ${
                                                option.stock === 0 ? 'opacity-50' : 'hover:bg-gray-100'
                                            } ${
                                                selectedAttributes[attributeOrder[attributeOrder.length - 1]] === option.value
                                                    ? 'bg-blue-50'
                                                    : ''
                                            }`}
                                            onClick={() => option.stock > 0 && handleLastAttributeChange(option)}
                                        >
                                            <span>{option.value}</span>
                                            <span className={option.stock > 0 ? "text-green-600" : "text-red-600"}>
                                                {option.stock > 0 ? `Còn ${option.stock}` : "Hết hàng"}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}

                    {/* Quantity */}
                    <div className="mb-4">
                        <p className="text-start font-medium mb-2">Số lượng</p>
                        <div className="flex items-center border rounded w-max">
                            <span
                                className="text-2xl px-3 py-1 cursor-pointer select-none disabled:opacity-50 disabled:cursor-not-allowed"
                                onClick={() => quantity > 1 && handleQuantityChange(-1)}
                                style={{userSelect: "none"}}
                            >
                                -
                            </span>

                            <input
                                type="text"
                                value={quantity}
                                onChange={(e) => {
                                    let val = e.target.value.replace(/\D/g, "");
                                    const stock = getSelectedStock();
                                    if (!stock) return;
                                    val = Number(val);
                                    if (val < 1) val = 1;
                                    if (val > stock) val = stock;
                                    setQuantity(val);
                                }}
                                className="w-12 text-center outline-none border-x"
                                inputMode="numeric"
                            />

                            <span
                                className="text-2xl px-3 py-1 cursor-pointer select-none disabled:opacity-50 disabled:cursor-not-allowed"
                                onClick={() => {
                                    const stock = getSelectedStock();
                                    quantity < stock && handleQuantityChange(1);
                                }}
                                style={{userSelect: "none"}}
                            >
                                +
                            </span>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <div className="p-4">
                    <button
                        className="bg-primary w-full font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transform text-white disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={!selectedAttributes[attributeOrder[attributeOrder.length - 1]] || getSelectedStock() === 0}
                    >
                        {!selectedAttributes[attributeOrder[attributeOrder.length - 1]]
                            ? 'Vui lòng chọn phân loại'
                            : getSelectedStock() === 0
                                ? 'Hết hàng'
                                : 'Thêm vào giỏ'
                        }
                    </button>
                </div>
            </div>
        </div>
    );
}