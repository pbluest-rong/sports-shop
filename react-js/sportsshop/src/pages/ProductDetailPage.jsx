import React, {useEffect, useState, useCallback} from "react";
import {useParams} from "react-router-dom";
import axios from "axios";

function ProductDetailPage() {
    const {slug} = useParams();
    const [product, setProduct] = useState(null);
    const [selectedVariant, setSelectedVariant] = useState(null);
    const [selectedAttributes, setSelectedAttributes] = useState({});
    const [quantity, setQuantity] = useState(1);
    const [attributesData, setAttributesData] = useState({});
    const [attributeOrder, setAttributeOrder] = useState([]);
    const [lastAttributeOptions, setLastAttributeOptions] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [loadingProduct, setLoadingProduct] = useState(true);
    const [imageIndex, setImageIndex] = useState(0);

    // Cache states
    const [lastAttributesCache, setLastAttributesCache] = useState({});

    // Tạo key cho cache dựa trên attributes
    const getCacheKey = (attributes) => {
        return Object.keys(attributes)
            .sort()
            .map(key => `${key}=${attributes[key]}`)
            .join('&');
    };

    // Load product data
    useEffect(() => {
        const fetchProduct = async () => {
            try {
                setLoadingProduct(true);
                const res = await axios.get(`http://localhost:8080/products/${slug}`);
                const productData = res.data?.data;

                if (res.data.success && productData) {
                    setProduct(productData);

                    // Setup variant data từ variantInfo
                    const variantInfo = productData.variantInfo;
                    if (variantInfo) {
                        const attributeNames = Object.keys(variantInfo.attributes);
                        setAttributeOrder(attributeNames);
                        setAttributesData(variantInfo.attributes);

                        // Set default attributes và variant
                        if (variantInfo.defaultVariant) {
                            setSelectedAttributes(variantInfo.defaultVariant.attributes || {});
                            setSelectedVariant(variantInfo.defaultVariant);

                            // Setup last attribute options cho attribute cuối
                            const lastAttributeName = attributeNames[attributeNames.length - 1];
                            const lastAttributeData = variantInfo.attributes[lastAttributeName] || [];
                            setLastAttributeOptions(lastAttributeData);
                        }
                    }
                } else {
                    console.warn("API trả về không hợp lệ:", res.data);
                }
            } catch (error) {
                console.error("Lỗi fetch product:", error);
            } finally {
                setLoadingProduct(false);
            }
        };

        fetchProduct();
    }, [slug]);

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
            const res = await axios.get(`http://localhost:8080/products/${slug}/variant?${queryString}`);
            const data = res.data;

            // Lưu vào cache
            setLastAttributesCache(prev => ({
                ...prev, [cacheKey]: data
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
        const isAvailable = options?.some(opt => opt.value === currentLastAttribute && opt.stock > 0);

        if (!isAvailable && options?.length > 0) {
            const availableOption = options.find(opt => opt.stock > 0);
            if (availableOption) {
                setSelectedAttributes(prev => ({
                    ...prev, [lastAttributeName]: availableOption.value
                }));
            } else {
                setSelectedAttributes(prev => ({
                    ...prev, [lastAttributeName]: ''
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

            const res = await axios.get(`http://localhost:8080/products/${slug}/variant?${queryParams.toString()}`);

            if (res.data.variant) {
                setSelectedVariant(res.data.variant);

                // Cập nhật cache nếu có dữ liệu mới
                if (res.data.lastAttributeOptions) {
                    setLastAttributesCache(prev => ({
                        ...prev, [cacheKey]: {
                            variant: res.data.variant, lastAttributeOptions: res.data.lastAttributeOptions
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
        const newAttributes = {...selectedAttributes};

        // Reset các attribute phía sau
        for (let i = attributeIndex + 1; i < attributeOrder.length; i++) {
            newAttributes[attributeOrder[i]] = '';
        }

        newAttributes[attributeName] = value;

        setSelectedAttributes(newAttributes);
        setQuantity(1); // Reset quantity khi thay đổi variant

        if (attributeIndex < attributeOrder.length - 1) {
            await fetchLastAttributeOptions(newAttributes);
        } else {
            await fetchVariantForSelectedAttributes(newAttributes);
        }
        setImageIndex(0)
    };

    // Lấy stock của option đã chọn của attribute cuối
    const getSelectedStock = () => {
        const lastAttributeName = attributeOrder[attributeOrder.length - 1];
        const selectedLastAttribute = selectedAttributes[lastAttributeName];
        return lastAttributeOptions.find(opt => opt.value === selectedLastAttribute)?.stock || 0;
    };

    // Kiểm tra xem attribute có thể được chọn hay không
    const isAttributeSelectable = (attributeName) => {
        const attributeIndex = attributeOrder.indexOf(attributeName);
        return attributeOrder
            .slice(0, attributeIndex)
            .every(attr => selectedAttributes[attr]);
    };

    // Xử lý thay đổi số lượng
    const handleQuantityChange = (change) => {
        const stock = getSelectedStock();
        const newQuantity = quantity + change;
        if (newQuantity >= 1 && newQuantity <= stock) {
            setQuantity(newQuantity);
        }
    };

    if (loadingProduct) {
        return (<div className="container mx-auto p-6 flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
            </div>);
    }

    if (!product) {
        return (<div className="container mx-auto p-6">
                <p className="text-center text-gray-500">Không tìm thấy sản phẩm</p>
            </div>);
    }

    return (<div className="container mx-auto p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* Hình ảnh */}
                <div>
                    <div className="mb-4">
                        <img
                            src={selectedVariant?.images?.[imageIndex] || product.mainImage}
                            alt={product.title}
                            className="w-full rounded-2xl shadow-md"
                            onError={(e) => {
                                e.target.src = 'https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg';
                            }}
                        />
                    </div>

                    {/* Thumbnail images */}
                    <div className="flex gap-2 flex-wrap">
                        {(selectedVariant?.images?.length ? selectedVariant.images : [product.mainImage]).map((img, idx) => (
                            <img
                                onClick={() => setImageIndex(idx)}
                                key={idx}
                                src={img}
                                alt={`preview-${idx}`}
                                className="w-20 h-20 object-cover rounded-lg border cursor-pointer hover:scale-105 transition"
                                onError={(e) => {
                                    e.target.src = 'https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg';
                                }}
                            />))}
                    </div>
                </div>

                {/* Thông tin sản phẩm */}
                <div className="text-start">
                    <h1 className="text-3xl font-bold mb-2">{product.title}</h1>
                    <p className="text-gray-600 mb-2">{product.brand}</p>
                    <p className="text-gray-700 mb-4">{product.shortDescription}</p>

                    <div className="mb-6">
                        <p className="text-2xl font-bold text-red-500">
                            {selectedVariant?.price ? selectedVariant.price.toLocaleString() : '0'}₫
                        </p>
                        {selectedVariant?.stock !== undefined && (<p className="text-sm text-gray-600 mt-1">
                                Còn lại: {getSelectedStock()} sản phẩm
                            </p>)}
                    </div>

                    {/* Render các attribute không phải là attribute cuối */}
                    {attributeOrder.slice(0, -1).map(attributeName => (<div key={attributeName} className="mb-6">
                            <h3 className="font-semibold mb-3 text-lg capitalize">
                                {attributeName === 'color' ? 'Màu sắc' : attributeName === 'size' ? 'Kích thước' : attributeName}
                            </h3>
                            <div className="flex gap-3 flex-wrap">
                                {attributesData[attributeName]?.map(option => {
                                    const isSelectable = isAttributeSelectable(attributeName);
                                    const isSelected = selectedAttributes[attributeName] === option.value;

                                    return (<div
                                            key={option.value}
                                            className={`border-2 rounded-lg cursor-pointer transition-all ${isSelected ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'} ${!isSelectable ? 'opacity-50 cursor-not-allowed' : ''}`}
                                            onClick={() => isSelectable && handleAttributeChange(attributeName, option.value)}
                                            title={!isSelectable ? 'Vui lòng chọn các phân loại trước' : ''}
                                        >
                                            {option.image ? (<div className="p-2">
                                                    <img
                                                        src={option.image}
                                                        alt={option.value}
                                                        className="w-16 h-16 object-cover rounded"
                                                        onError={(e) => {
                                                            e.target.src = 'https://wpklik.com/wp-content/uploads/2019/03/A-404-Page-Best-Practices-and-Design-Inspiration.jpg';
                                                        }}
                                                    />
                                                    <p className="text-center text-sm mt-1">{option.value}</p>
                                                </div>) : (<div className="px-4 py-2">
                                                    <span className="font-medium">{option.value}</span>
                                                </div>)}
                                        </div>);
                                })}
                            </div>
                        </div>))}

                    {/* Attribute cuối cùng (có stock) */}
                    {attributeOrder.length > 0 && (<div className="mb-6">
                            <h3 className="font-semibold mb-3 text-lg capitalize">
                                {(() => {
                                    const lastAttr = attributeOrder[attributeOrder.length - 1];
                                    return lastAttr === 'color' ? 'Màu sắc' : lastAttr === 'size' ? 'Kích thước' : lastAttr;
                                })()}
                            </h3>
                            <div className="flex gap-3 flex-wrap">
                                {lastAttributeOptions.map((option, idx) => {
                                    const isSelected = selectedAttributes[attributeOrder[attributeOrder.length - 1]] === option.value;
                                    const isOutOfStock = option.stock === 0;

                                    return (<div
                                            key={idx}
                                            className={`border-2 rounded-lg cursor-pointer transition-all ${isSelected ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'} ${isOutOfStock ? 'opacity-50 cursor-not-allowed' : ''}`}
                                            onClick={() => !isOutOfStock && handleAttributeChange(attributeOrder[attributeOrder.length - 1], option.value)}
                                        >
                                            <div className="px-4 py-2 text-center">
                                                <span className="font-medium">{option.value}</span>
                                                <div
                                                    className={`text-sm ${option.stock > 0 ? 'text-green-600' : 'text-red-600'}`}>
                                                    {option.stock > 0 ? `Còn ${option.stock}` : 'Hết hàng'}
                                                </div>
                                            </div>
                                        </div>);
                                })}
                            </div>
                        </div>)}

                    {/* Số lượng */}
                    <div className="mb-6">
                        <h3 className="font-semibold mb-3 text-lg">Số lượng</h3>
                        <div className="flex items-center border rounded-lg w-max">
                            <button
                                className="px-4 py-2 text-xl font-bold hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                                onClick={() => handleQuantityChange(-1)}
                                disabled={quantity <= 1}
                            >
                                -
                            </button>
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
                                className="w-16 text-center outline-none border-x py-2"
                                inputMode="numeric"
                            />
                            <button
                                className="px-4 py-2 text-xl font-bold hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                                onClick={() => handleQuantityChange(1)}
                                disabled={quantity >= getSelectedStock()}
                            >
                                +
                            </button>
                        </div>
                    </div>

                    {/* Loading indicator */}
                    {isLoading && (<div className="mb-4">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                        </div>)}

                    {/* Add to cart button */}
                    <button
                        className="bg-primary w-full font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transform text-white disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={!selectedAttributes[attributeOrder[attributeOrder.length - 1]] || getSelectedStock() === 0}
                    >
                        {!selectedAttributes[attributeOrder[attributeOrder.length - 1]] ? 'Vui lòng chọn phân loại' : getSelectedStock() === 0 ? 'Hết hàng' : 'Thêm vào giỏ hàng'}
                    </button>
                </div>
            </div>
        </div>);
}

export default ProductDetailPage;