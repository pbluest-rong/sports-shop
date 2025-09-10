import { useState } from "react";
import { FaFilter, FaTimes } from "react-icons/fa";

export default function ProductFilter() {
    const [openMobile, setOpenMobile] = useState(false);

    return (
        <>
            {/* Desktop filter */}
            <aside className="hidden lg:block w-64 shrink-0 pr-4">
                <div className="bg-white rounded-lg shadow p-4 sticky top-20">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="font-semibold">BỘ LỌC</h2>
                        <button className="text-sm text-blue-600 hover:underline">Xóa tất cả</button>
                    </div>

                    {/* Thương hiệu */}
                    <div className="mb-6">
                        <h3 className="font-medium mb-2">CHỌN THƯƠNG HIỆU</h3>
                        <div className="space-y-1">
                            {["KIPSTA", "DECATHLON", "ADIDAS", "NABAIJI", "IMVISO"].map((brand) => (
                                <label key={brand} className="flex items-center gap-2 text-sm">
                                    <input type="checkbox" className="accent-blue-600" />
                                    {brand}
                                </label>
                            ))}
                        </div>
                    </div>

                    {/* Màu sắc */}
                    <div className="mb-6">
                        <h3 className="font-medium mb-2">CHỌN MÀU</h3>
                        <div className="grid grid-cols-5 gap-2">
                            {["black", "blue", "orange", "white", "gray", "red", "green", "yellow"].map((c) => (
                                <button
                                    key={c}
                                    className={`w-6 h-6 rounded-full border ${c === "white" ? "bg-white" : ""}`}
                                    style={{ backgroundColor: c }}
                                />
                            ))}
                        </div>
                    </div>

                    {/* Giá */}
                    <div className="mb-6">
                        <h3 className="font-medium mb-2">CHỌN GIÁ</h3>
                        <input type="range" min="39000" max="4790000" className="w-full accent-blue-600" />
                        <div className="flex justify-between text-sm text-gray-600 mt-1">
                            <span>39.000₫</span>
                            <span>4.790.000₫</span>
                        </div>
                    </div>

                    {/* Kích cỡ */}
                    <div>
                        <h3 className="font-medium mb-2">CHỌN KÍCH CỠ</h3>
                        <div className="grid grid-cols-4 gap-2">
                            {[0, 1, 2, 3, 4].map((s) => (
                                <label key={s} className="flex items-center gap-1 text-sm">
                                    <input type="checkbox" className="accent-blue-600" />
                                    {s}
                                </label>
                            ))}
                        </div>
                    </div>
                </div>
            </aside>

            {/* Mobile filter button */}
            <div className="lg:hidden mb-4">
                <button
                    onClick={() => setOpenMobile(true)}
                    className="px-4 py-2 flex items-center gap-2 border rounded-md w-full justify-center"
                >
                    <FaFilter /> Bộ lọc
                </button>
            </div>

            {/* Mobile filter drawer */}
            {openMobile && (
                <div className="fixed inset-0 bg-black bg-opacity-40 z-50 flex">
                    <div className="w-72 bg-white p-4 overflow-y-auto">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="font-semibold">BỘ LỌC</h2>
                            <button onClick={() => setOpenMobile(false)}>
                                <FaTimes />
                            </button>
                        </div>
                        {/* Copy lại filter content giống desktop */}
                        <div className="space-y-6">
                            {/* Brand */}
                            <div>
                                <h3 className="font-medium mb-2">CHỌN THƯƠNG HIỆU</h3>
                                <div className="space-y-1">
                                    {["KIPSTA", "DECATHLON", "ADIDAS", "NABAIJI", "IMVISO"].map((brand) => (
                                        <label key={brand} className="flex items-center gap-2 text-sm">
                                            <input type="checkbox" className="accent-blue-600" />
                                            {brand}
                                        </label>
                                    ))}
                                </div>
                            </div>
                            {/* ... Màu, Giá, Kích cỡ tương tự desktop ... */}
                        </div>
                    </div>
                    {/* click outside to close */}
                    <div className="flex-1" onClick={() => setOpenMobile(false)} />
                </div>
            )}
        </>
    );
}
