import { FaFacebookF, FaInstagram, FaYoutube, FaTiktok } from "react-icons/fa";

export default function Footer() {
    return (
        <footer className="bg-gray-100 text-gray-800 mt-8">
            {/* Brand & Description */}
            <div className="bg-white py-6">
                <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row sm:justify-between sm:justify-between gap-6">
                    <div className="sm:w-1/2 space-y-2 text-start">
                        <h2 className="text-2xl font-bold italic text-blue-700">PBLUES - SPORTS SHOP</h2>
                        <p className="text-gray-700 text-sm leading-relaxed">
                            Thương hiệu đồ thể thao chất lượng. Hơn 5.000 sản phẩm cho mọi môn thể thao,
                            từ người mới bắt đầu đến chuyên nghiệp. Khám phá ngay giày thể thao, quần áo,
                            dụng cụ thể thao cho cả người lớn và trẻ em.
                        </p>
                    </div>

                    {/* Contact & Social */}
                    <div className="sm:w-1/2 flex flex-col sm:items-end gap-3">
                        <div className="text-gray-700 text-sm space-y-1 text-center sm:text-right">
                            <p><strong>LIÊN HỆ:</strong></p>
                            <p>Địa chỉ: 26 Ung Văn Khiêm, P. Thạnh Mỹ Tây, TP. HCM</p>
                            <p>Điện thoại: 1800 9044 | Email: cskh@decathlon.com</p>
                            <p>ĐKDN: 0305728805 | Ngày cấp: 22/05/2008</p>
                        </div>
                        <div className="flex justify-center sm:justify-end gap-4 text-xl text-gray-600">
                            <FaFacebookF className="hover:text-blue-600 cursor-pointer transition" />
                            <FaInstagram className="hover:text-pink-500 cursor-pointer transition" />
                            <FaYoutube className="hover:text-red-600 cursor-pointer transition" />
                            <FaTiktok className="hover:text-black cursor-pointer transition" />
                        </div>
                    </div>
                </div>
            </div>

            {/* Footer Menu */}
            <div className="max-w-7xl mx-auto px-4 py-8 grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-6 text-gray-700 text-sm">
                {[
                    {
                        title: "Hỗ Trợ Khách Hàng",
                        items: [
                            "Liên hệ CSKH",
                            "Hướng dẫn đặt hàng",
                            "Đổi trả & Bảo hành",
                            "Quy trình trả hàng trực tuyến",
                            "Phương thức giao hàng",
                            "Phương thức thanh toán",
                        ],
                    },
                    {
                        title: "Về Pblues",
                        items: ["Pblues là ai?", "Phát triển bền vững", "Tuyển dụng", "Blog Thể Thao"],
                    },
                    {
                        title: "Mua Sắm",
                        items: [
                            "Danh sách cửa hàng",
                            "Click & Collect",
                            "Ứng dụng Pblues",
                            "Đơn hàng doanh nghiệp",
                            "Thẻ quà tặng",
                        ],
                    },
                    { title: "Ưu Đãi", items: ["Chương trình tích điểm", "Đổi điểm sang quà tặng"] },
                    {
                        title: "Pháp Lý",
                        items: [
                            "Chính sách mua hàng",
                            "Chính sách bảo mật",
                            "Bản công bố sản phẩm",
                            "Thu hồi sản phẩm",
                        ],
                    },
                    {
                        title: "Sản Phẩm Nổi Bật",
                        items: ["Kính Bơi", "Giày Chạy Bộ", "Đồ Bơi", "Lều Cắm Trại", "Giày Trượt Patin"],
                    },
                ].map((section, idx) => (
                    <div key={idx}>
                        <h3 className="font-semibold mb-2">{section.title}</h3>
                        <ul className="space-y-1">
                            {section.items.map((item, i) => (
                                <li key={i} className="hover:text-blue-700 cursor-pointer transition">
                                    {item}
                                </li>
                            ))}
                        </ul>
                    </div>
                ))}
            </div>

            {/* Footer Bottom */}
            <div className="border-t border-gray-300 text-center py-2 text-xs text-gray-500">
                &copy; Bản quyền thuộc về Pblues - Sports Shop
            </div>
        </footer>
    );
}
