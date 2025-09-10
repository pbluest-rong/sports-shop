import {useDispatch, useSelector} from "react-redux";
import {fetchCategoryChildren, setActivePath, setOpenDropdown} from "./categorySlice.js";
import {MdExpandMore} from "react-icons/md";
import {Link, useNavigate} from "react-router-dom";

export default function Categories() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {displayLevels, activePath, allCategories, openDropdown} = useSelector((state) => state.category);

    const handleCategoryClick = async (category, levelIndex) => {
        if (!category.hasChildren) {
            navigate(`c${category.path}`);
            dispatch(setOpenDropdown(null));
            return;
        }

        const newLevel = levelIndex + 1;

        if (category.hasChildren && !allCategories[category.id]?.children) {
            await dispatch(fetchCategoryChildren(category.id));
        }

        dispatch(setActivePath({categoryId: category.id, level: newLevel}));
    };

    if (!displayLevels.length) return null;

    return (
        <div
            className="absolute top-full left-1/2 transform -translate-x-1/2 w-[90%] rounded-2xl bg-white shadow-lg z-50 mt-2 px-10 py-4"
        >
            <div className="flex max-h-[400px]">
                {/* Categories wrapper */}
                <div
                    className="flex overflow-x-auto scrollbar-thin scrollbar-track-gray-100 scrollbar-thumb-gray-300 hover:scrollbar-thumb-gray-400"
                >
                    {displayLevels.map((levelItems, levelIndex) => (
                        <div
                            key={levelIndex}
                            className="flex flex-col overflow-y-auto border-gray-200 border-r-2 last:border-r-0 p-2 min-w-[200px] max-w-[300px]"
                        >
                            <Link
                                to={`/c${
                                    levelIndex === 0
                                        ? allCategories[openDropdown]?.path || ""
                                        : allCategories[activePath[levelIndex - 1]]?.path || ""
                                }`}
                                onClick={() => dispatch(setOpenDropdown(null))}
                            >
                                <div className="cursor-pointer mb-2 font-bold">Xem tất cả</div>
                            </Link>

                            {levelItems?.map((item) => {
                                const isActive = activePath[levelIndex] === item.id;
                                return (
                                    <div
                                        key={item.id}
                                        className={`flex justify-between items-center p-2 cursor-pointer transition-colors rounded-md ${
                                            isActive
                                                ? "text-black bg-blue-50 font-bold"
                                                : "hover:text-blue-600 hover:bg-gray-50"
                                        }`}
                                        onClick={() => handleCategoryClick(item, levelIndex)}
                                    >
                                        <span className="text-start text-sm truncate pr-1">{item.name}</span>
                                        {item.hasChildren && (
                                            <MdExpandMore className="rotate-270 text-lg ml-1 flex-shrink-0"/>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </div>

                <div className="flex-1">
                    <img
                        src="https://contents.mediadecathlon.com/s1303386/k$e1c639dbd2cf88d079a4e4bdabe49d94/nav-banner-largesummer-sale-vi.webp"
                        alt="Banner"
                        className="w-full object-cover max-h-[400px] rounded"
                    />
                </div>
            </div>
        </div>
    );
}