import Banner from "../components/Banner.jsx";
import HomeProductList from "../components/HomeProductList.jsx";
import HomeCategoryList from "../components/HomeCategoryList.jsx";
import { useEffect, useState, useRef } from "react";
import { getSportCategories } from "../features/category/categoryApi.js";
import { getProducts } from "../features/product/productApi.js";
import {
    BESTSELLERS_URL,
    FOOTBALL_URL,
    MEN_URL,
    RUNNING_SHOES_URL,
    WOMEN_URL,
} from "../utils/constant.js";

// Danh sách section
const sections = [
    { title: "Sản phẩm bán chạy nhất", url: BESTSELLERS_URL, isCategory: false },
    { title: "Các môn thể thao", url: null, isCategory: true },
    { title: "Bóng đá & Futsal", url: FOOTBALL_URL, isCategory: false },
    { title: "Giày chạy bộ", url: RUNNING_SHOES_URL, isCategory: false },
    { title: "Nam", url: MEN_URL, isCategory: false },
    { title: "Nữ", url: WOMEN_URL, isCategory: false },
];

function SkeletonList({ isCategory }) {
    return (
        <div className="flex space-x-4 py-2 w-full">
            {Array.from({ length: 6 }).map((_, i) => (
                <div
                    key={i}
                    className={`bg-gray-200 rounded-lg flex-1 ${
                        isCategory ? "h-32 min-w-[120px]" : "h-56 min-w-[160px]"
                    }`}
                />
            ))}
        </div>
    );
}

function HomePage() {
    const [visibleSections, setVisibleSections] = useState([0]); // chỉ show section đầu
    const [sectionData, setSectionData] = useState({}); // dữ liệu từng section
    const [loading, setLoading] = useState({});
    const bottomRef = useRef(null);

    const fetchSectionData = async (index) => {
        const section = sections[index];
        if (!section || sectionData[index] || loading[index]) return;

        setLoading((prev) => ({ ...prev, [index]: true }));
        try {
            const data = section.isCategory
                ? await getSportCategories()
                : await getProducts(section.url);
            setSectionData((prev) => ({ ...prev, [index]: data || [] }));
        } catch (err) {
            console.error(`Error fetching ${section.title}:`, err);
        } finally {
            setLoading((prev) => ({ ...prev, [index]: false }));
        }
    };

    // Load section đầu tiên khi mount
    useEffect(() => {
        fetchSectionData(0);
    }, []);

    // Observer lazy load khi scroll
    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting) {
                    setVisibleSections((prev) => {
                        const nextIndex = prev.length;
                        if (nextIndex < sections.length && !loading[nextIndex]) {
                            fetchSectionData(nextIndex);
                            return [...prev, nextIndex];
                        }
                        return prev;
                    });
                }
            },
            { threshold: 0.1, rootMargin: "0px 0px 300px 0px" }
        );

        if (bottomRef.current) observer.observe(bottomRef.current);
        return () => {
            if (bottomRef.current) observer.unobserve(bottomRef.current);
        };
    }, [loading]);

    return (
        <div className="w-full min-h-screen flex flex-col">
            <Banner />
            <main className="flex-1 p-6 mx-10 bg-white">
                {visibleSections.map((index) => {
                    const section = sections[index];
                    const data = sectionData[index];
                    const isLoading = loading[index];

                    return (
                        <div key={index} className="mb-8">
                            <h2 className="text-xl font-bold mb-4">{section.title}</h2>
                            {isLoading ? (
                                <SkeletonList isCategory={section.isCategory} />
                            ) : section.isCategory ? (
                                <HomeCategoryList categories={data || []}  />
                            ) : (
                                <HomeProductList products={data || []}  />
                            )}
                        </div>
                    );
                })}
                <div ref={bottomRef} className="h-1" />
            </main>
        </div>
    );
}

export default HomePage;