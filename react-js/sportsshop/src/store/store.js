import {configureStore} from "@reduxjs/toolkit";
import categoryReducer from "../features/category/categorySlice.js";
import productReducer from "../features/product/productSlice.js";

export const store = configureStore(
    {
        reducer: {
            category: categoryReducer,
            product: productReducer,
        },
    }
);