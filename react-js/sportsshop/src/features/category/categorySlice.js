import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import {getCategories, getCategoryChildren} from "./categoryApi.js";
import {STATUS} from "../../utils/constant.js";

export const fetchCategories = createAsyncThunk(
    "category/fetchCategories",
    async () => {
        return await getCategories();
    }
);

export const fetchCategoryChildren = createAsyncThunk(
    "category/fetchCategoryChildren",
    async (id) => {
        return await getCategoryChildren(id);
    }
);

const categorySlice = createSlice({
    name: "category",
    initialState: {
        rootCategories: [], // Level 1 categories
        allCategories: {}, // Tất cả categories đã fetch, key = categoryId, value = category data
        displayLevels: [], // Array of levels để hiển thị: [level1Items, level2Items, level3Items, ...]
        activePath: [], // Path của các category đang active: [categoryId1, categoryId2, ...]
        status: STATUS.IDLE,
        error: null,
        openDropdown: null,
    },
    reducers: {
        setOpenDropdown: (state, action) => {
            state.openDropdown = action.payload;
            if (!action.payload) {
                // Reset khi đóng dropdown
                state.displayLevels = [];
                state.activePath = [];
            } else {
                const rootCategory = state.allCategories[action.payload];
                if (rootCategory?.children) {
                    state.displayLevels[0] = rootCategory.children.map(id => state.allCategories[id]);
                }
                state.activePath = [action.payload];
            }
        },
        setActivePath: (state, action) => {
            const { categoryId, level } = action.payload;

            // Cập nhật active path
            state.activePath = state.activePath.slice(0, level - 1);
            if (categoryId) {
                state.activePath[level - 1] = categoryId;
            }

            // Cập nhật display levels
            state.displayLevels = [];

            // Level 1: Root categories của dropdown đang mở
            if (state.openDropdown) {
                const rootCategory = state.allCategories[state.openDropdown];
                if (rootCategory?.children) {
                    state.displayLevels[0] = rootCategory.children.map(id => state.allCategories[id]);
                }
            }

            // Các level tiếp theo dựa trên active path
            state.activePath.forEach((activeId, index) => {
                const category = state.allCategories[activeId];
                if (category?.children) {
                    state.displayLevels[index + 1] = category.children.map(id => state.allCategories[id]);
                }
            });
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchCategories.pending, (state) => {
                state.status = STATUS.LOADING;
            })
            .addCase(fetchCategories.fulfilled, (state, action) => {
                state.status = STATUS.SUCCEEDED;
                state.rootCategories = action.payload;

                // Lưu vào allCategories
                action.payload.forEach(category => {
                    state.allCategories[category.id] = category;
                });
            })
            .addCase(fetchCategories.rejected, (state, action) => {
                state.status = STATUS.FAILED;
                state.error = action.error.message;
            })
            .addCase(fetchCategoryChildren.fulfilled, (state, action) => {
                const parentId = action.meta.arg;
                const children = action.payload;

                // Lưu children vào allCategories
                children.forEach(child => {
                    state.allCategories[child.id] = child;
                });

                // Cập nhật parent category với danh sách children IDs
                if (state.allCategories[parentId]) {
                    state.allCategories[parentId].children = children.map(child => child.id);
                }

                // Refresh display levels nếu cần
                if (state.openDropdown && state.activePath.length >= 0) {
                    const currentLevel = state.activePath.length;
                    if (state.activePath[currentLevel - 1] === parentId || state.openDropdown === parentId) {
                        // Trigger refresh display levels
                        const lastActivePath = [...state.activePath];
                        // const lastLevel = lastActivePath.length;

                        // Reset và rebuild display levels
                        state.displayLevels = [];

                        // Level 1
                        const rootCategory = state.allCategories[state.openDropdown];
                        if (rootCategory?.children) {
                            state.displayLevels[0] = rootCategory.children.map(id => state.allCategories[id]);
                        }

                        // Rebuild các level tiếp theo
                        lastActivePath.forEach((activeId, index) => {
                            const category = state.allCategories[activeId];
                            if (category?.children) {
                                state.displayLevels[index + 1] = category.children.map(id => state.allCategories[id]);
                            }
                        });
                    }
                }
            });
    },
});

export const { setOpenDropdown, setActivePath } = categorySlice.actions;
export default categorySlice.reducer;