import logo from "../assets/logo.svg";
import {BsChatLeftDots, BsSearch, BsList, BsX} from "react-icons/bs";
import {GoPerson} from "react-icons/go";
import {RiShoppingBag4Line} from "react-icons/ri";
import {MdExpandMore} from "react-icons/md";
import {useEffect, useState} from "react";
import {IoMdClose, IoMdSearch} from "react-icons/io";
import {useDispatch, useSelector} from "react-redux";
import {
    fetchCategories,
    fetchCategoryChildren,
    setOpenDropdown,
    setActivePath
} from "../features/category/categorySlice.js";
import Categories from "../features/category/Categories.jsx";
import {STATUS} from "../utils/constant.js";
import {Link, useNavigate} from "react-router-dom";

export default function Header() {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);

    const dispatch = useDispatch();
    const {rootCategories, allCategories, openDropdown, status} = useSelector((state) => state.category);


    const [value, setValue] = useState("");
    const [focused, setFocused] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (status === STATUS.IDLE) {
            dispatch(fetchCategories());
        }
    }, [status, dispatch]);

    const handleCategoryClick = async (category) => {
        if (!category.hasChildren) {
            navigate(`c${category.path}`);
            dispatch(setOpenDropdown(null));
            return;
        }
        // Toggle dropdown nếu level 0 (root category)
        if (!category.parentId) {
            const newDropdownState = openDropdown === category.id ? null : category.id;
            dispatch(setOpenDropdown(newDropdownState));

            if (newDropdownState) {
                // Fetch children nếu cần
                if (category.hasChildren && !allCategories[category.id]?.children) {
                    await dispatch(fetchCategoryChildren(category.id));
                }
                // Set initial display levels
                dispatch(setActivePath({categoryId: null, level: 1}));
            }
        }
    };

    return (
        <header className="w-full shadow-sm sticky top-0 z-50 bg-white dark:bg-gray-900">
            <div className="flex items-center justify-between px-4 md:px-6">
                {/* Logo và menu mobile */}
                <div className="flex items-center gap-4">
                    <button className="md:hidden" onClick={() => setIsMenuOpen(!isMenuOpen)}>
                        <BsList className="text-xl"/>
                    </button>
                    <Link to={`/`}>
                        <img src={logo} alt="Logo" className="h-16 cursor-pointer"/>
                    </Link>
                </div>

                {/* Search bar - ẩn trên mobile */}
                <div className="hidden md:flex items-center gap-4 md:w-2/5 lg:w-2/5 xl:w-1/2">
                    <div
                        className="flex items-center w-full mx-auto bg-gray-100 rounded-full px-3 py-2 hover:bg-gray-200">
                        <IoMdSearch className="text-lg mr-2"/>
                        <input
                            type="text"
                            placeholder="Tìm kiếm sản phẩm, môn thể thao"
                            className="flex-1 bg-transparent outline-none text-sm"
                            value={value}
                            onChange={(e) => setValue(e.target.value)}
                            onFocus={() => setFocused(true)}
                            onBlur={() => setFocused(false)}
                        />
                        {(focused || value) && (
                            <IoMdClose
                                className="text-lg cursor-pointer"
                                onClick={() => setValue("")}
                            />
                        )}
                    </div>
                </div>

                {/* User actions */}
                <div className="flex items-center gap-4 md:gap-6 text-sm">
                    {/* Search icon mobile */}
                    <div className="md:hidden flex items-center">
                        <BsSearch
                            className="text-xl mr-3 cursor-pointer"
                            onClick={() => setIsSearchOpen(true)}
                        />
                    </div>

                    <div className="hidden sm:flex flex-col items-center cursor-pointer">
                        <GoPerson className="text-lg"/>
                        <span className="hidden md:block text-xs">Đăng nhập</span>
                    </div>

                    <div className="hidden md:flex flex-col items-center cursor-pointer">
                        <BsChatLeftDots className="text-lg"/>
                        <span className="text-xs">Hỗ Trợ</span>
                    </div>

                    {/* Cart */}
                    <div className="flex flex-col items-center cursor-pointer p-2 rounded-lg relative">
                        <RiShoppingBag4Line className="text-lg mb-1"/>
                        <span className="hidden md:block text-xs">Giỏ hàng</span>
                        <span
                            className="absolute -top-1 -right-1 bg-red-500 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs">
                            3
                        </span>
                    </div>
                </div>
            </div>

            {/* Search bar mobile */}
            {isSearchOpen && (
                <div className="md:hidden px-4 py-2 border-t">
                    <div className="flex items-center">
                        <div
                            className="flex items-center w-full mx-auto bg-gray-100 rounded-full px-3 py-2 hover:bg-gray-200">
                            <IoMdSearch className="text-lg mr-2"/>
                            <input
                                type="text"
                                placeholder="Tìm kiếm sản phẩm, môn thể thao"
                                className="flex-1 bg-transparent outline-none text-sm"
                            />
                        </div>
                        <button
                            className="py-2 rounded-r-full"
                            onClick={() => setIsSearchOpen(false)}
                        >
                            <IoMdClose className="text-lg"/>
                        </button>
                    </div>
                </div>
            )}

            {/* Bottom nav desktop */}
            <nav
                className="bg-gray-100 hidden md:flex items-center gap-4 lg:gap-6 px-4 lg:px-6 py-2 text-sm font-medium overflow-x-auto relative">
                {rootCategories.map((item) => (
                    <div key={item.id} className="relative">
                        <span
                            className={`cursor-pointer whitespace-nowrap flex items-center gap-1 px-2 py-1 rounded transition-colors duration-200 ${
                                openDropdown === item.id ? "bg-gray-100 text-blue-600" : ""
                            } ${item.name.toLowerCase().includes("sale") ? "text-red-600" : ""}`}
                            onClick={() => handleCategoryClick(item)}
                        >
                            {item.name}
                            {item.hasChildren && (
                                <MdExpandMore
                                    className={`transition-transform duration-200 ${
                                        openDropdown === item.id ? "rotate-180" : ""
                                    }`}
                                />
                            )}
                        </span>
                    </div>
                ))}
            </nav>

            {/* Mega menu popup */}
            {openDropdown && (
                <>
                    {/* Overlay mờ */}
                    <div
                        className="fixed inset-0 bg-black/70 z-40"
                        onClick={() => dispatch(setOpenDropdown(null))}
                    ></div>

                    {/* Popup Categories */}
                    <Categories/>
                </>
            )}
            {/* Mobile menu */}
            {isMenuOpen && (
                <div className="md:hidden fixed inset-0 bg-black/30 z-50">
                    <div className="absolute top-0 left-0 w-3/4 max-w-xs h-full bg-white p-4 shadow-lg">
                        <div className="flex flex-col space-y-3">
                            {rootCategories.map((item) => (
                                <span
                                    key={item.id}
                                    className="font-bold cursor-pointer py-2 flex items-center justify-between"
                                    onClick={() => {
                                        handleCategoryClick(item);
                                        setIsMenuOpen(false);
                                    }}
                                >
                                    <span className={`cursor-pointer whitespace-nowrap ${
                                        item.name.toLowerCase().includes("sale") ? "text-red-600" : ""
                                    }`}>
                                        {item.name}
                                    </span>
                                    {item.hasChildren && (
                                        <MdExpandMore
                                            className={`transition-transform duration-200 ${
                                                openDropdown === item.id ? "rotate-180" : ""
                                            }`}
                                        />
                                    )}
                                </span>
                            ))}

                            <div className="pt-2">
                                <div className="flex items-center cursor-pointer">
                                    <GoPerson className="mr-2"/>
                                    <span>Đăng nhập</span>
                                </div>
                            </div>

                            <div className="pt-2">
                                <div className="flex items-center cursor-pointer">
                                    <BsChatLeftDots className="mr-2"/>
                                    <span>Hỗ Trợ</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="w-full h-full" onClick={() => setIsMenuOpen(false)}></div>
                </div>
            )}
        </header>
    );
}