import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    openAddToCartSidebar: false,
    selectedProduct: null,
};

const productSlice = createSlice({
    name: "product",
    initialState,
    reducers: {
        openAddToCartSidebar: (state, action) => {
            state.openAddToCartSidebar = true;
            state.selectedProduct = action.payload;
        },
        closeAddToCartSidebar: (state) => {
            state.openAddToCartSidebar = false;
            state.selectedProduct = null;
        },
    },
});

export const { openAddToCartSidebar, closeAddToCartSidebar } =
    productSlice.actions;
export default productSlice.reducer;
